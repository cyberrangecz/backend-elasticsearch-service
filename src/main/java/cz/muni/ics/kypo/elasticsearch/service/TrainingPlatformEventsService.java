package cz.muni.ics.kypo.elasticsearch.service;

import cz.muni.ics.kypo.elasticsearch.data.TrainingPlatformEventsDAO;
import cz.muni.ics.kypo.elasticsearch.data.exceptions.ElasticsearchTrainingDataLayerException;
import cz.muni.ics.kypo.elasticsearch.service.exceptions.ElasticsearchTrainingServiceLayerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The type Training events service.
 */
@Service
public class TrainingPlatformEventsService {

    private final TrainingPlatformEventsDAO trainingPlatformEventsDAO;

    /**
     * Instantiates a new Training events service.
     *
     * @param trainingPlatformEventsDAO the training events dao
     */
    @Autowired
    public TrainingPlatformEventsService(TrainingPlatformEventsDAO trainingPlatformEventsDAO) {
        this.trainingPlatformEventsDAO = trainingPlatformEventsDAO;
    }

    /**
     * Find all events by training definition and training instance id list.
     *
     * @param trainingDefinitionId the training definition id
     * @param trainingInstanceId   the training instance id
     * @return the list
     * @throws ElasticsearchTrainingServiceLayerException the elasticsearch training service layer exception
     */
    public List<Map<String, Object>> findAllEventsByTrainingDefinitionAndTrainingInstanceId(Long trainingDefinitionId, Long trainingInstanceId) throws ElasticsearchTrainingServiceLayerException {
        try {
            List<Map<String, Object>> eventsFromElasticsearch = trainingPlatformEventsDAO.findAllEventsByTrainingDefinitionAndTrainingInstanceId(trainingDefinitionId, trainingInstanceId);
            Collections.sort(eventsFromElasticsearch, (map1, map2) -> Long.valueOf(map1.get("timestamp").toString()).compareTo(Long.valueOf(map2.get("timestamp").toString())));
            return eventsFromElasticsearch;
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    /**
     * Find all events from training run list.
     *
     * @param trainingDefinitionId the training definition id
     * @param trainingInstanceId   the training instance id
     * @param trainingRunId        the training run id
     * @return the list
     * @throws ElasticsearchTrainingServiceLayerException the elasticsearch training service layer exception
     */
    public List<Map<String, Object>> findAllEventsFromTrainingRun(Long trainingDefinitionId, Long trainingInstanceId, Long trainingRunId) throws ElasticsearchTrainingServiceLayerException {
        try {
            List<Map<String, Object>> eventsFromElasticsearch = trainingPlatformEventsDAO.findAllEventsFromTrainingRun(trainingDefinitionId, trainingInstanceId, trainingRunId);
            Collections.sort(eventsFromElasticsearch, (map1, map2) -> Long.valueOf(map1.get("timestamp").toString()).compareTo(Long.valueOf(map2.get("timestamp").toString())));
            return eventsFromElasticsearch;
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    /**
     * Find all events by training definition and training instance aggregated by user and levels.
     *
     * @param trainingDefinitionId the training definition id
     * @param trainingInstanceId   the training instance id
     * @return the aggregated events by user and levels sorted by timestamp.
     * @throws ElasticsearchTrainingServiceLayerException the elasticsearch training service layer exception
     */
    public Map<Integer, Map<Integer, List<Map<String, Object>>>> findAllEventsByTrainingDefinitionAndTrainingInstanceAggregated(Long trainingDefinitionId, Long trainingInstanceId) throws ElasticsearchTrainingServiceLayerException {
        try {
            return trainingPlatformEventsDAO.findAllEventsByTrainingDefinitionAndTrainingInstanceAggregated(trainingDefinitionId, trainingInstanceId);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    /**
     * Delete events by training instance id.
     *
     * @param instanceId the instance id
     * @throws ElasticsearchTrainingServiceLayerException the elasticsearch training service layer exception
     */
    public void deleteEventsByTrainingInstanceId(Long instanceId) throws ElasticsearchTrainingServiceLayerException {
        try {
            trainingPlatformEventsDAO.deleteEventsByTrainingInstanceId(instanceId);
        } catch (ElasticsearchTrainingDataLayerException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    /**
     * Delete events from training run.
     *
     * @param trainingInstanceId the training instance id
     * @param trainingRunId      the training run id
     * @throws ElasticsearchTrainingServiceLayerException the elasticsearch training service layer exception
     */
    public void deleteEventsFromTrainingRun(Long trainingInstanceId, Long trainingRunId) throws ElasticsearchTrainingServiceLayerException {
        try {
            trainingPlatformEventsDAO.deleteEventsFromTrainingRun(trainingInstanceId, trainingRunId);
        } catch (ElasticsearchTrainingDataLayerException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public Map<Integer, Map<Integer, List<Map<String, Object>>>> getAllEventsOfTrainingInstanceAggregatedByUsersAndLevels(Long trainingInstanceId) throws ElasticsearchTrainingServiceLayerException {
        try {
            return trainingPlatformEventsDAO.getEventsOfTrainingInstanceAggregatedByUsersAndLevels(trainingInstanceId);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public Map<Integer, Map<Integer, List<Map<String, Object>>>> getAllEventsOfTrainingInstanceAggregatedByLevelsAndUsers(Long trainingInstanceId) throws ElasticsearchTrainingServiceLayerException {
        try {
            return trainingPlatformEventsDAO.getEventsOfTrainingInstanceAggregatedByLevelsAndUsers(trainingInstanceId);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }
}

