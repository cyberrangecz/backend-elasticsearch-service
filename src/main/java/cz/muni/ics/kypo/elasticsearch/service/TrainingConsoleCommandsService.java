package cz.muni.ics.kypo.elasticsearch.service;

import cz.muni.ics.kypo.elasticsearch.data.TrainingConsoleCommandsDao;
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
public class TrainingConsoleCommandsService {

    private TrainingConsoleCommandsDao trainingConsoleCommandsDao;

    @Autowired
    public TrainingConsoleCommandsService(TrainingConsoleCommandsDao trainingConsoleCommandsDao) {
        this.trainingConsoleCommandsDao = trainingConsoleCommandsDao;
    }

    public List<Map<String, Object>> findAllConsoleCommandsByPoolId(Long poolId) {
        try {
            return trainingConsoleCommandsDao.findAllConsoleCommandsByPoolId(poolId);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public List<Map<String, Object>> findAllConsoleCommandsBySandboxId(Long sandboxId) {
        try {
            return trainingConsoleCommandsDao.findAllConsoleCommandsBySandboxId(sandboxId);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public void deleteConsoleCommandsByPoolId(Long poolId) {
        try {
            trainingConsoleCommandsDao.deleteConsoleCommandsByPoolId(poolId);
        } catch (ElasticsearchTrainingDataLayerException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public void deleteConsoleCommandsBySandboxId(Long sandboxId) {
        try {
            trainingConsoleCommandsDao.deleteConsoleCommandsBySandboxId(sandboxId);
        } catch (ElasticsearchTrainingDataLayerException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

}
