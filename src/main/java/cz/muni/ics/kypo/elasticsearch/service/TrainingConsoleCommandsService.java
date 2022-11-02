package cz.muni.ics.kypo.elasticsearch.service;

import cz.muni.ics.kypo.elasticsearch.data.TrainingConsoleCommandsDao;
import cz.muni.ics.kypo.elasticsearch.data.exceptions.ElasticsearchTrainingDataLayerException;
import cz.muni.ics.kypo.elasticsearch.data.indexpaths.AbstractKypoIndexPath;
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

    private final TrainingConsoleCommandsDao trainingConsoleCommandsDao;

    @Autowired
    public TrainingConsoleCommandsService(TrainingConsoleCommandsDao trainingConsoleCommandsDao) {
        this.trainingConsoleCommandsDao = trainingConsoleCommandsDao;
    }

    public List<Map<String, Object>> findAllConsoleCommandsByPoolId(Long poolId, List<String> filterCommands) {
        try {
            return trainingConsoleCommandsDao.findAllConsoleCommands(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".pool=" + poolId + ".*", filterCommands);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public List<Map<String, Object>> findAllConsoleCommandsByAccessToken(String accessToken, List<String> filterCommands) {
        try {
            return trainingConsoleCommandsDao.findAllConsoleCommands(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".access-token=" + accessToken + ".*", filterCommands);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public List<Map<String, Object>> findAllConsoleCommandsBySandboxId(String sandboxId, List<String> filterCommands) {
        try {
            return trainingConsoleCommandsDao.findAllConsoleCommands(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".sandbox=" + sandboxId, filterCommands);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public List<Map<String, Object>> findAllConsoleCommandsByAccessTokenAndUserId(String accessToken, Long userId, List<String> filterCommands) {
        try {
            return trainingConsoleCommandsDao.findAllConsoleCommands(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".access-token=" + accessToken + ".user=" + userId, filterCommands);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public List<Map<String, Object>> findAllConsoleCommandsBySandboxIdAndTimeRange(String sandboxId, Long from, Long to, List<String> filterCommands) {
        try {
            return trainingConsoleCommandsDao.findAllConsoleCommandsBySandboxAndTimeRange(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".sandbox=" + sandboxId, from, to, filterCommands);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public List<Map<String, Object>> findAllConsoleCommandsByAccessTokenAndUserIdAndTimeRange(String accessToken, Long userId, Long from, Long to, List<String> filterCommands) {
        try {
            return trainingConsoleCommandsDao.findAllConsoleCommandsBySandboxAndTimeRange(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".access-token=" + accessToken + ".user=" + userId, from, to, filterCommands);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public void deleteConsoleCommandsByPoolId(Long poolId) {
        try {
            trainingConsoleCommandsDao.deleteConsoleCommands(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".pool=" + poolId + ".*");
        } catch (ElasticsearchTrainingDataLayerException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public void deleteConsoleCommandsByAccessToken(String accessToken) {
        try {
            trainingConsoleCommandsDao.deleteConsoleCommands(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".access-token=" + accessToken + ".*");
        } catch (ElasticsearchTrainingDataLayerException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public void deleteConsoleCommandsBySandboxId(String sandboxId) {
        try {
            trainingConsoleCommandsDao.deleteConsoleCommands(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".sandbox=" + sandboxId);
        } catch (ElasticsearchTrainingDataLayerException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }
    public void deleteConsoleCommandsByAccessTokenAndUserId(String accessToken, Long userId) {
        try {
            trainingConsoleCommandsDao.deleteConsoleCommands(AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".access-token=" + accessToken + ".user=" + userId);
        } catch (ElasticsearchTrainingDataLayerException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

}
