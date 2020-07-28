package cz.muni.ics.kypo.elasticsearch.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.ics.kypo.elasticsearch.api.exceptions.ResourceNotFoundException;
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
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The type Training events dao.
 */
@Repository
public class TrainingEventsDAO extends AbstractElasticClientDAO {

    private static final int INDEX_DOCUMENTS_MAX_RETURN_NUMBER = 10_000;

    /**
     * Instantiates a new Training events dao.
     *
     * @param restHighLevelClient the rest high level client
     * @param objectMapper        the object mapper
     */
    @Autowired
    public TrainingEventsDAO(@Qualifier("kypoRestHighLevelClient") RestHighLevelClient restHighLevelClient,
                             @Qualifier("objMapperForElasticsearch") ObjectMapper objectMapper) {
        super(restHighLevelClient, objectMapper);
    }

    /**
     * Find all events by training definition and training instance id list.
     *
     * @param trainingDefinitionId the training definition id
     * @param trainingInstanceId   the training instance id
     * @return the list
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     * @throws IOException                             the io exception
     */
    public List<Map<String, Object>> findAllEventsByTrainingDefinitionAndTrainingInstanceId(Long trainingDefinitionId, Long trainingInstanceId) throws ElasticsearchTrainingDataLayerException, IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.sort(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_TIMESTAMP, SortOrder.ASC);
        searchSourceBuilder.size(INDEX_DOCUMENTS_MAX_RETURN_NUMBER);
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.MINUTES));

        SearchRequest searchRequest = new SearchRequest(AbstractKypoIndexPath.KYPO_EVENTS_INDEX + "*" + ".definition=" + trainingDefinitionId + ".instance=" + trainingInstanceId + "*");
        searchRequest.source(searchSourceBuilder);

        return handleElasticsearchResponse(getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT));
    }

    /**
     * Find all events from training run list.
     *
     * @param trainingDefinitionId the training definition id
     * @param trainingInstanceId   the training instance id
     * @param trainingRunId        the training run id
     * @return the list
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     * @throws IOException                             the io exception
     */
    public List<Map<String, Object>> findAllEventsFromTrainingRun(Long trainingDefinitionId, Long trainingInstanceId, Long trainingRunId) throws ElasticsearchTrainingDataLayerException, IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_TRAINING_RUN_ID, trainingRunId));
        searchSourceBuilder.sort(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_TIMESTAMP, SortOrder.ASC);
        searchSourceBuilder.size(INDEX_DOCUMENTS_MAX_RETURN_NUMBER);
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.MINUTES));

        SearchRequest searchRequest = new SearchRequest(AbstractKypoIndexPath.KYPO_EVENTS_INDEX + "*" + ".definition=" + trainingDefinitionId + ".instance=" + trainingInstanceId + "*");
        searchRequest.source(searchSourceBuilder);

        return handleElasticsearchResponse(getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT));
    }

    /**
     * <pre>{@code
     *  DELETE /kypo3.cz.muni.csirt.kypo.events.trainings.*.instance={instanceId}
     * }*
     * </pre>
     *
     * @param trainingInstanceId the training instance id
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     */
    public void deleteEventsByTrainingInstanceId(Long trainingInstanceId) throws ElasticsearchTrainingDataLayerException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(AbstractKypoIndexPath.KYPO_EVENTS_INDEX + "*" + ".instance=" + trainingInstanceId + "*");
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
     * @param trainingInstanceId the training instance id
     * @param trainingRunId      the training run id
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     */
    public void deleteEventsFromTrainingRun(Long trainingInstanceId, Long trainingRunId) throws ElasticsearchTrainingDataLayerException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(AbstractKypoIndexPath.KYPO_EVENTS_INDEX + "*" + ".instance=" + trainingInstanceId + ".run=" + trainingRunId + "*");
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
                if (events.isEmpty()) {
                    throw new ResourceNotFoundException("There are no events in this game.");
                }
            }
        } else {
            throw new ElasticsearchTrainingDataLayerException("Client could not connect to Elastic. Please, restart Elasticsearch service.");
        }
        return events;
    }

}
