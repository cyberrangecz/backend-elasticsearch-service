package cz.muni.ics.kypo.elasticsearch.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.ics.kypo.elasticsearch.data.exceptions.ElasticsearchTrainingDataLayerException;
import cz.muni.ics.kypo.elasticsearch.data.indexpaths.AbstractKypoElasticTermQueryFields;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.range.ParsedRange;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class TrainingConsoleCommandsDao extends AbstractElasticClientDAO {

    private static final int INDEX_DOCUMENTS_MAX_RETURN_NUMBER = 30_000;

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
     * @param index index used to search for commands
     * @return the list
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     * @throws IOException                             the io exception
     */
    public List<Map<String, Object>> findAllConsoleCommands(String index) throws ElasticsearchTrainingDataLayerException, IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.sort(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_TIMESTAMP_STR, SortOrder.ASC);
        searchSourceBuilder.size(INDEX_DOCUMENTS_MAX_RETURN_NUMBER);
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.MINUTES));

        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(searchSourceBuilder);

        return handleElasticsearchResponse(getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT));
    }

    /**
     * Find all bash commands from sandbox aggregated by timestamp ranges.
     * <p>
     * Elasticserach query:
     * GET kypo.logs.console*.sandbox={sandboxId}/_search
     * {
     * "query": {
     * "range": {
     * "timestamp_str": {
     * "gte": {from}
     * "lte": {to}
     * }
     * }
     * }
     * }
     *
     * @param index     index under which commands from local or cloud sandboxes are stored
     * @param from      the lower bound of the time range (epoch_millis timestamp format)
     * @param to        the upper bound of the time range (epoch_millis timestamp format)
     * @return the list of commands in given time range
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     * @throws IOException                             the io exception
     */
    public List<Map<String, Object>> findAllConsoleCommandsBySandboxAndTimeRange(String index, Long from, Long to) throws ElasticsearchTrainingDataLayerException, IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.sort(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_TIMESTAMP_STR, SortOrder.ASC);
        searchSourceBuilder.size(INDEX_DOCUMENTS_MAX_RETURN_NUMBER);
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.MINUTES));

        //Date Range Aggregation Query
        RangeQueryBuilder dateRangeBuilder = QueryBuilders.rangeQuery(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_TIMESTAMP_STR)
                .gte(from)
                .lte(to);
        searchSourceBuilder.query(dateRangeBuilder);

        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);

        return handleElasticsearchResponse(searchResponse);
    }

    /**
     * @param index the pool id
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     */
    public void deleteConsoleCommands(String index) throws ElasticsearchTrainingDataLayerException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
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
        SearchHits responseHits = topHits.getHits();

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
