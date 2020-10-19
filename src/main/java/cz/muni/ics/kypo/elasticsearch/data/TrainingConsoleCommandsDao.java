package cz.muni.ics.kypo.elasticsearch.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.ics.kypo.elasticsearch.data.exceptions.ElasticsearchTrainingDataLayerException;
import cz.muni.ics.kypo.elasticsearch.data.indexpaths.AbstractKypoElasticTermQueryFields;
import cz.muni.ics.kypo.elasticsearch.data.indexpaths.AbstractKypoIndexPath;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.ParsedRange;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
public class TrainingConsoleCommandsDao extends AbstractElasticClientDAO {

    private static final int INDEX_DOCUMENTS_MAX_RETURN_NUMBER = 10_000;

    /**
     * Instantiates a new Training events dao.
     *
     * @param restHighLevelClient the rest high level client
     * @param objectMapper        the object mapper
     */
    @Autowired
    public TrainingConsoleCommandsDao(@Qualifier("kypoRestHighLevelClient") RestHighLevelClient restHighLevelClient,
                                      @Qualifier("objMapperForElasticsearch") ObjectMapper objectMapper) {
        super(restHighLevelClient, objectMapper);
    }

    /**
     * Find all bash commands from a pool by its id.
     *
     * @param poolId the pool id
     * @return the list
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     * @throws IOException                             the io exception
     */
    public List<Map<String, Object>> findAllConsoleCommandsByPoolId(Long poolId) throws ElasticsearchTrainingDataLayerException, IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_POOL_ID, poolId));
        searchSourceBuilder.sort(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_TIMESTAMP_STR, SortOrder.ASC);
        searchSourceBuilder.size(INDEX_DOCUMENTS_MAX_RETURN_NUMBER);
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.MINUTES));

        SearchRequest searchRequest = new SearchRequest(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".pool=" + poolId + "*");
        searchRequest.source(searchSourceBuilder);

        return handleElasticsearchResponse(getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT));
    }


    /**
     * Find all bash commands from sandbox.
     *
     * @param sandboxId the sandbox id
     * @return the list
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     * @throws IOException                             the io exception
     */
    public List<Map<String, Object>> findAllConsoleCommandsBySandboxId(Long sandboxId) throws ElasticsearchTrainingDataLayerException, IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_SANDBOX_ID, sandboxId));
        searchSourceBuilder.sort(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_TIMESTAMP_STR, SortOrder.ASC);
        searchSourceBuilder.size(INDEX_DOCUMENTS_MAX_RETURN_NUMBER);
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.MINUTES));

        SearchRequest searchRequest = new SearchRequest(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".sandbox=" + sandboxId + "*");
        searchRequest.source(searchSourceBuilder);

        return handleElasticsearchResponse(getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT));
    }

    /**
     * Find all bash commands from sandbox aggregated by timestamp ranges.
     *
     * @param sandboxId the sandbox id
     * @param ranges the list of range limits that divide bash commands into intervals
     * @return the list
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     * @throws IOException                             the io exception
     */
    public List<List<Map<String, Object>>> findAllConsoleCommandsBySandboxIdAggregatedByTimeRanges(Long sandboxId, List<String> ranges) throws ElasticsearchTrainingDataLayerException, IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_SANDBOX_ID, sandboxId));
        searchSourceBuilder.sort(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_TIMESTAMP_STR, SortOrder.ASC);
        searchSourceBuilder.size(0);
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.MINUTES));

        //Date Range Aggregation Query
        DateRangeAggregationBuilder dateRangeAggregationBuilder = AggregationBuilders.dateRange("timestamp_ranges").field("timestamp_str");
        dateRangeAggregationBuilder.addUnboundedTo(ranges.get(0));
        for (int i = 1; i < ranges.size(); i++) {
            dateRangeAggregationBuilder.addRange(ranges.get(i-1), ranges.get(i));
        }
        dateRangeAggregationBuilder.addUnboundedFrom(ranges.get(ranges.size()-1));
        dateRangeAggregationBuilder.subAggregation(AggregationBuilders.topHits("by_top_hits").sort("timestamp_str").size(100));
        searchSourceBuilder.aggregation(dateRangeAggregationBuilder);

        SearchRequest searchRequest = new SearchRequest(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".sandbox=" + sandboxId + "*");
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);

        return resolveTimeRangeAggregation(searchResponse);
    }

    /**
     * @param poolId the pool id
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     */
    public void deleteConsoleCommandsByPoolId(Long poolId) throws ElasticsearchTrainingDataLayerException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".pool=" + poolId + "*");
        try {
            AcknowledgedResponse deleteIndexResponse = getRestHighLevelClient().indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            if (!deleteIndexResponse.isAcknowledged()) {
                throw new ElasticsearchTrainingDataLayerException("Client could not connect to Elastic.");
            }
        } catch (IOException e) {
            throw new ElasticsearchTrainingDataLayerException("Client could not connect to Elastic.");
        }
    }

    /**
     * @param sandboxId the sandbox id
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     */
    public void deleteConsoleCommandsBySandboxId(Long sandboxId) throws ElasticsearchTrainingDataLayerException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".sandbox=" + sandboxId + "*");
        try {
            AcknowledgedResponse deleteIndexResponse = getRestHighLevelClient().indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            if (!deleteIndexResponse.isAcknowledged()) {
                throw new ElasticsearchTrainingDataLayerException("Client could not connect to Elastic.");
            }
        } catch (IOException e) {
            throw new ElasticsearchTrainingDataLayerException("Client could not connect to Elastic.");
        }
    }

    private List<Map<String, Object>> handleElasticsearchResponse(SearchResponse response) throws ElasticsearchTrainingDataLayerException {
        List<Map<String, Object>> events = new ArrayList<>();
        if (response != null) {
            SearchHits responseHits = response.getHits();
            if (responseHits != null) {
                SearchHit[] results = responseHits.getHits();
                for (SearchHit hit : results) {
                    Map<String, Object> source = hit.getSourceAsMap();
                    events.add(source);
                }
            }
        } else {
            throw new ElasticsearchTrainingDataLayerException("Client could not connect to Elasticsearch. Please, restart Elasticsearch service.");
        }
        return events;
    }

    private List<List<Map<String, Object>>> resolveTimeRangeAggregation(SearchResponse searchResponse) {
        Aggregations aggregations = searchResponse.getAggregations();
        ParsedRange commandsByTimeRanges = aggregations.get("timestamp_ranges");
        Collection<Range.Bucket> timestampRanges = (Collection<Range.Bucket>) commandsByTimeRanges.getBuckets();

        List<List<Map<String, Object>>> commandsDivision = new ArrayList<>();
        for (Range.Bucket rangeBucket : timestampRanges) {
            commandsDivision.add(resolveBucketTopHits(rangeBucket));
        }
        return commandsDivision;
    }

    private List<Map<String, Object>> resolveBucketTopHits(Range.Bucket rangeBucket) throws ElasticsearchTrainingDataLayerException {
        List<Map<String, Object>> resultBucketTopHits = new ArrayList<>();
        TopHits topHits = rangeBucket.getAggregations().get("by_top_hits");
        SearchHits responseHits =  topHits.getHits();

        if (responseHits != null) {
            SearchHit[] results = responseHits.getHits();
            for (SearchHit hit : results) {
                resultBucketTopHits.add(hit.getSourceAsMap());
            }
        } else {
            throw new ElasticsearchTrainingDataLayerException("Client could not connect to Elasticsearch. Please, restart Elasticsearch service.");
        }
        return resultBucketTopHits;
    }


}
