package cz.muni.ics.kypo.elasticsearch.service;

import cz.muni.ics.kypo.elasticsearch.data.TrainingBashCommandsDao;
import cz.muni.ics.kypo.elasticsearch.data.exceptions.ElasticsearchTrainingDataLayerException;
import cz.muni.ics.kypo.elasticsearch.service.exceptions.ElasticsearchTrainingServiceLayerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The type Training bash commands service.
 */
@Service
public class TrainingBashCommandsService {

    private TrainingBashCommandsDao trainingBashCommandsDao;

    @Autowired
    public TrainingBashCommandsService(TrainingBashCommandsDao trainingBashCommandsDao) {
        this.trainingBashCommandsDao = trainingBashCommandsDao;
    }

    public List<Map<String, Object>> findAllBashCommandsByPoolId(Long poolId) {
        try {
            return trainingBashCommandsDao.findAllBashCommandsByPoolId(poolId);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public List<Map<String, Object>> findAllBashCommandsBySandboxId(Long sandboxId) {
        try {
            return trainingBashCommandsDao.findAllBashCommandsBySandboxId(sandboxId);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public void deleteBashCommandsByPoolId(Long poolId) {
        try {
            trainingBashCommandsDao.deleteBashCommandsByPoolId(poolId);
        } catch (ElasticsearchTrainingDataLayerException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public void deleteBashCommandsBySandboxId(Long sandboxId) {
        try {
            trainingBashCommandsDao.deleteBashCommandsBySandboxId(sandboxId);
        } catch (ElasticsearchTrainingDataLayerException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

}
