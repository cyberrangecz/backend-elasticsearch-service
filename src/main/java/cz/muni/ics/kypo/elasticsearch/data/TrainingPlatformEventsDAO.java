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
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The type Training events dao.
 */
@Repository
public class TrainingPlatformEventsDAO extends AbstractElasticClientDAO {

    private static final int INDEX_DOCUMENTS_MAX_RETURN_NUMBER = 10_000;

    /**
     * Instantiates a new Training events dao.
     *
     * @param restHighLevelClient the rest high level client
     * @param objectMapper        the object mapper
     */
    @Autowired
    public TrainingPlatformEventsDAO(@Qualifier("kypoRestHighLevelClient") RestHighLevelClient restHighLevelClient,
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

        SearchRequest searchRequest = new SearchRequest(AbstractKypoIndexPath.KYPO_EVENTS_INDEX + "*" + ".definition=" + trainingDefinitionId + ".instance=" + trainingInstanceId + ".*");
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

        SearchRequest searchRequest = new SearchRequest(AbstractKypoIndexPath.KYPO_EVENTS_INDEX + "*" + ".definition=" + trainingDefinitionId + ".instance=" + trainingInstanceId + ".*");
        searchRequest.source(searchSourceBuilder);

        return handleElasticsearchResponse(getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT));
    }

    /**
     * Find all events by training definition and training instance id list.
     *
     * <pre>{@code
     *  GET kypo.cz.muni.csirt.kypo.events.trainings*.definition={definitionId}.instance={instanceId}
     *  {
     *   "query": {
     *     "match_all": {}
     *   },
     *   "collapse": {
     *     "field": "user_ref_id",
     *     "inner_hits": {
     *       "name": "by_user_ref_id",
     *       "sort": [{"timestamp": "asc" }, {"syslog.@timestamp": "asc" }],
     *       "size": 10000
     *     }
     *   }
     * }
     * }*
     * </pre>
     *
     * @param trainingInstanceId   the training instance id
     * @return the list
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     * @throws IOException                             the io exception
     */
    public Map<Integer, Map<Integer, List<Map<String, Object>>>> getEventsOfTrainingInstanceAggregatedByUsersAndLevels(Long trainingInstanceId) throws ElasticsearchTrainingDataLayerException, IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.matchAllQuery())
                .sort(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_USER_REF_ID, SortOrder.ASC)
                .size(INDEX_DOCUMENTS_MAX_RETURN_NUMBER)
                .timeout(new TimeValue(5, TimeUnit.MINUTES));

        InnerHitBuilder innerHitBuilder = new InnerHitBuilder().setName("by_" + AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_USER_REF_ID)
                .setSize(INDEX_DOCUMENTS_MAX_RETURN_NUMBER)
                .addSort(SortBuilders.fieldSort("timestamp"));
        CollapseBuilder collapseBuilder = new CollapseBuilder(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_USER_REF_ID).setInnerHits(innerHitBuilder);
        searchSourceBuilder.collapse(collapseBuilder);

        SearchRequest searchRequest = new SearchRequest(AbstractKypoIndexPath.KYPO_EVENTS_INDEX + "*.instance=" + trainingInstanceId + "*");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);

        return handleAggregationOfCollapsedEventsByAnotherId(searchResponse, AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_USER_REF_ID, AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_LEVEL_ID);
    }

    /**
     * Find all events by training definition and training instance id list.
     *
     * <pre>{@code
     *  GET kypo.cz.muni.csirt.kypo.events.trainings*.definition={definitionId}.instance={instanceId}
     *  {
     *   "query": {
     *     "match_all": {}
     *   },
     *   "collapse": {
     *     "field": "level",
     *     "inner_hits": {
     *       "name": "by_level",
     *       "sort": [{"timestamp": "asc" }, {"syslog.@timestamp": "asc" }],
     *       "size": 10000
     *     }
     *   }
     * }
     * }*
     * </pre>
     *
     * @param trainingInstanceId   the training instance id
     * @return the list
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     * @throws IOException                             the io exception
     */
    public Map<Integer, Map<Integer, List<Map<String, Object>>>> getEventsOfTrainingInstanceAggregatedByLevelsAndUsers(Long trainingInstanceId) throws ElasticsearchTrainingDataLayerException, IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.matchAllQuery())
                .sort(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_USER_REF_ID, SortOrder.ASC)
                .size(INDEX_DOCUMENTS_MAX_RETURN_NUMBER)
                .timeout(new TimeValue(5, TimeUnit.MINUTES));

        InnerHitBuilder innerHitBuilder = new InnerHitBuilder().setName("by_" + AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_LEVEL_ID)
                .setSize(INDEX_DOCUMENTS_MAX_RETURN_NUMBER)
                .addSort(SortBuilders.fieldSort("timestamp"));
        CollapseBuilder collapseBuilder = new CollapseBuilder(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_LEVEL_ID).setInnerHits(innerHitBuilder);
        searchSourceBuilder.collapse(collapseBuilder);

        SearchRequest searchRequest = new SearchRequest(AbstractKypoIndexPath.KYPO_EVENTS_INDEX + "*.instance=" + trainingInstanceId + "*");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);

        return handleAggregationOfCollapsedEventsByAnotherId(searchResponse, AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_LEVEL_ID, AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_USER_REF_ID);
    }

    /**
     * Process and aggregate Elasticsearch response that contains events of the users.
     * @param searchResponse Elasticsearch response.
     * @return events aggregated by user.
     */
    private Map<Integer, Map<Integer, List<Map<String, Object>>>> handleAggregationOfCollapsedEventsByAnotherId(SearchResponse searchResponse, String collapseIdField, String anotherIdField) throws ElasticsearchTrainingDataLayerException, IOException {
        Map<Integer, Map<Integer, List<Map<String, Object>>>> aggregatedEventsByFirstFieldVar = new HashMap<>();

        if (searchResponse != null) {
            SearchHits responseHits = searchResponse.getHits();
            if (responseHits != null) {
                SearchHit[] collapsedResults = responseHits.getHits();
                for (SearchHit hit : collapsedResults) {
                    Integer firstFieldValue = hit.getFields().get(collapseIdField).getValue();
                    SearchHits innerHitsByFirstField = hit.getInnerHits().get("by_" + collapseIdField);
                    aggregatedEventsByFirstFieldVar.put(firstFieldValue, aggregateEventsByField(innerHitsByFirstField, anotherIdField));
                }
            }
        } else {
            throw new ElasticsearchTrainingDataLayerException("Client could not connect to Elastic. Please, restart Elasticsearch service.");
        }
        return aggregatedEventsByFirstFieldVar;
    }

    /**
     * Process and aggregate sorted list of events by level.
     * @param unprocessedEvents list of SearchHits that represents events of the particular user sorted by timestamp.
     * @return events aggregated by level.
     */
    private Map<Integer, List<Map<String, Object>>> aggregateEventsByField(SearchHits unprocessedEvents, String aggregationField) {
        Map<Integer, List<Map<String, Object>>> aggregatedEvents = new LinkedHashMap<>();
        List<Map<String, Object>> eventsOfField = new ArrayList<>();
        Integer fieldValue = (Integer) unprocessedEvents.getAt(0).getSourceAsMap().get(aggregationField);

        for (SearchHit unprocessedEvent: unprocessedEvents) {
            if(!fieldValue.equals(unprocessedEvent.getSourceAsMap().get(aggregationField))) {
                aggregatedEvents.put(fieldValue, eventsOfField);
                fieldValue = (Integer) unprocessedEvent.getSourceAsMap().get(aggregationField);
                eventsOfField = new ArrayList<>();
            }
            eventsOfField.add(unprocessedEvent.getSourceAsMap());
        }
        aggregatedEvents.put(fieldValue, eventsOfField);
        return aggregatedEvents;
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
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(AbstractKypoIndexPath.KYPO_EVENTS_INDEX + "*" + ".instance=" + trainingInstanceId + ".*");
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
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(AbstractKypoIndexPath.KYPO_EVENTS_INDEX + "*" + ".instance=" + trainingInstanceId + ".run=" + trainingRunId);
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
     * Find all events by training definition and training instance id list.
     *
     * <pre>{@code
     *  GET kypo.cz.muni.csirt.kypo.events.trainings*.definition={definitionId}.instance={instanceId}
     *  {
     *   "query": {
     *     "match_all": {}
     *   },
     *   "collapse": {
     *     "field": "user_ref_id",
     *     "inner_hits": {
     *       "name": "by_user",
     *       "sort": "timestamp",
     *       "size": 10000
     *     }
     *   }
     * }
     * }*
     * </pre>
     *
     * @param trainingDefinitionId the training definition id
     * @param trainingInstanceId   the training instance id
     * @return the list
     * @throws ElasticsearchTrainingDataLayerException the elasticsearch training data layer exception
     * @throws IOException                             the io exception
     */
    public Map<Integer, Map<Integer, List<Map<String, Object>>>> findAllEventsByTrainingDefinitionAndTrainingInstanceAggregated(Long trainingDefinitionId, Long trainingInstanceId) throws ElasticsearchTrainingDataLayerException, IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.sort(AbstractKypoElasticTermQueryFields.KYPO_ELASTICSEARCH_USER_REF_ID, SortOrder.ASC);
        searchSourceBuilder.size(INDEX_DOCUMENTS_MAX_RETURN_NUMBER);
        searchSourceBuilder.timeout(new TimeValue(5, TimeUnit.MINUTES));

        //Collapse query
        InnerHitBuilder innerHitBuilder = new InnerHitBuilder().setName("by_user")
                .setSize(INDEX_DOCUMENTS_MAX_RETURN_NUMBER)
                .addSort(SortBuilders.fieldSort("timestamp"))
                .addSort(SortBuilders.fieldSort("syslog.@timestamp"));
        CollapseBuilder collapseBuilder = new CollapseBuilder("user_ref_id").setInnerHits(innerHitBuilder);
        searchSourceBuilder.collapse(collapseBuilder);

        SearchRequest searchRequest = new SearchRequest(AbstractKypoIndexPath.KYPO_EVENTS_INDEX + "*" + ".definition=" + trainingDefinitionId + ".instance=" + trainingInstanceId + "*");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);
        return handleAggregationOfEventsByUser(searchResponse);
    }

    /**
     * Process and aggregate Elasticsearch response that contains events of the users.
     * @param searchResponse Elasticsearch response.
     * @return events aggregated by user.
     */
    private Map<Integer, Map<Integer, List<Map<String, Object>>>> handleAggregationOfEventsByUser(SearchResponse searchResponse) throws ElasticsearchTrainingDataLayerException, IOException {
        Map<Integer, Map<Integer, List<Map<String, Object>>>> aggregatedEventsByUserRefId = new HashMap<>();

        if (searchResponse != null) {
            SearchHits responseHits = searchResponse.getHits();
            if (responseHits != null) {
                SearchHit[] collapsedResults = responseHits.getHits();
                for (SearchHit hit : collapsedResults) {
                    Integer userRefId = hit.getFields().get("user_ref_id").getValue();
                    SearchHits innerHitsByUser = hit.getInnerHits().get("by_user");
                    aggregatedEventsByUserRefId.put(userRefId, aggregateEventsOfUserByLevels(innerHitsByUser));
                }
            }
        } else {
            throw new ElasticsearchTrainingDataLayerException("Client could not connect to Elastic. Please, restart Elasticsearch service.");
        }
        return aggregatedEventsByUserRefId;
    }

    /**
     * Process and aggregate sorted list of events by level.
     * @param unprocessedEvents list of SearchHits that represents events of the particular user sorted by timestamp.
     * @return events aggregated by level.
     */
    private Map<Integer, List<Map<String, Object>>> aggregateEventsOfUserByLevels(SearchHits unprocessedEvents) {
        Map<Integer, List<Map<String, Object>>> aggregatedEvents = new HashMap<>();
        List<Map<String, Object>> eventsOfLevel = new ArrayList<>();
        Integer levelId = (Integer) unprocessedEvents.getAt(0).getSourceAsMap().get("level");

        for (SearchHit unprocessedEvent: unprocessedEvents) {
            if(!levelId.equals(unprocessedEvent.getSourceAsMap().get("level"))) {
                aggregatedEvents.put(levelId, eventsOfLevel);
                levelId = (Integer) unprocessedEvent.getSourceAsMap().get("level");
                eventsOfLevel = new ArrayList<>();
            }
            eventsOfLevel.add(unprocessedEvent.getSourceAsMap());
        }
        return aggregatedEvents;
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
            throw new ElasticsearchTrainingDataLayerException("Client could not connect to Elastic. Please, restart Elasticsearch service.");
        }
        return events;
    }
}
