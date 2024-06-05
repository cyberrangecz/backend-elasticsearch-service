package cz.muni.ics.kypo.elasticsearch.service;

import cz.muni.ics.kypo.elasticsearch.api.dto.CommandsStatistics;
import cz.muni.ics.kypo.elasticsearch.api.dto.OverallPhaseStatistics;
import cz.muni.ics.kypo.elasticsearch.data.AdaptiveTrainingStatisticsDAO;
import cz.muni.ics.kypo.elasticsearch.data.exceptions.ElasticsearchTrainingDataLayerException;
import cz.muni.ics.kypo.elasticsearch.data.indexpaths.AbstractKypoIndexPath;
import cz.muni.ics.kypo.elasticsearch.service.exceptions.ElasticsearchTrainingServiceLayerException;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Training bash commands service.
 */
@Service
public class AdaptiveTrainingStatisticsService {

    private final AdaptiveTrainingStatisticsDAO adaptiveTrainingStatisticsDAO;

    @Autowired
    public AdaptiveTrainingStatisticsService(AdaptiveTrainingStatisticsDAO adaptiveTrainingStatisticsDAO) {
        this.adaptiveTrainingStatisticsDAO = adaptiveTrainingStatisticsDAO;
    }

    public List<CommandsStatistics> findCommandsStatistic(Long trainingRunId, List<Long> phaseIds,
                                                          String accessToken, Long userId,
                                                          Map<Long, List<String>> keywordsMapping) {
        try {
            List<CommandsStatistics> result = new ArrayList<>();
            Map<Long, Pair<Long, Long>> phasesBoundaries = adaptiveTrainingStatisticsDAO.findTimeBoundariesOfPhases(trainingRunId, phaseIds);
            Map<Long, Long> taskIdsOfPhases = adaptiveTrainingStatisticsDAO.getTaskIdsOfPhases(trainingRunId, phaseIds);

            String index = getIndexToFindCommandStatistics(trainingRunId, accessToken, userId);
            for (Map.Entry<Long, Pair<Long, Long>> phaseBoundaries : phasesBoundaries.entrySet()) {
                Pair<Long, Map<String, Long>> statistics = adaptiveTrainingStatisticsDAO.findCommandsStatisticsInTimeRange(
                        index,
                        phaseBoundaries.getValue().getValue0(),
                        phaseBoundaries.getValue().getValue1(),
                        keywordsMapping == null ? null : keywordsMapping.get(phaseBoundaries.getKey()));
                result.add(new CommandsStatistics(phaseBoundaries.getKey(), taskIdsOfPhases.get(phaseBoundaries.getKey()), statistics.getValue0(), statistics.getValue1()));
            }
            return result;
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public String getIndexToFindCommandStatistics(Long trainingRunId, String accessToken, Long userId) {
        try {
            String sandboxId = (String) adaptiveTrainingStatisticsDAO.getUniqueFieldValueFromTrainingEvent(trainingRunId, "sandbox_id");
            if (sandboxId != null) {
                return AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".sandbox=" + sandboxId;
            }
            if (accessToken == null || userId == null) {
                throw new ElasticsearchTrainingServiceLayerException("The sandbox ID in the training events is null, thus access token and user ID must be defined.");
            }
            return AbstractKypoIndexPath.KYPO_CONSOLE_COMMANDS_INDEX + "*" + ".access-token=" + accessToken + ".user=" + userId;
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public Map<Long, Long> findPhaseTimeStatistics(Long trainingRunId, List<Long> phaseIds) {
        try {
            return adaptiveTrainingStatisticsDAO.findTimeBoundariesOfPhases(trainingRunId, phaseIds).entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getValue1() - entry.getValue().getValue0()));
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public Map<Long, Boolean> findPhaseSolutionsStatistics(Long trainingRunId, List<Long> phaseIds) {
        try {
            Map<Long, Long> solutionDisplayedMapping = adaptiveTrainingStatisticsDAO.countEventsInPhases(trainingRunId, phaseIds, List.of("SolutionDisplayed")).entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, longMapEntry -> longMapEntry.getValue().get("SolutionDisplayed")));
            return solutionDisplayedMapping.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() == 1));
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public Map<Long, List<String>> findPhaseWrongAnswersStatistics(Long trainingRunId, List<Long> phaseIds) {
        try {
            return adaptiveTrainingStatisticsDAO.getWrongAnswersInPhases(trainingRunId, phaseIds);
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

    public List<OverallPhaseStatistics> findOverallStatistics(Long trainingRunId, List<Long> phaseIds,
                                                              String accessToken, Long userId,
                                                              Map<Long, List<String>> keywordsMapping) {
        try {
            List<OverallPhaseStatistics> result = new ArrayList<>();
            String index = getIndexToFindCommandStatistics(trainingRunId, accessToken, userId);
            Map<Long, Pair<Long, Long>> phasesBoundaries = adaptiveTrainingStatisticsDAO.findTimeBoundariesOfPhases(trainingRunId, phaseIds);
            Map<Long, Map<String, Long>> solutionDisplayedInPhases = adaptiveTrainingStatisticsDAO.countEventsInPhases(trainingRunId, phaseIds, List.of("SolutionDisplayed"));
            Map<Long, List<String>> wrongAnswersOfPhases = adaptiveTrainingStatisticsDAO.getWrongAnswersInPhases(trainingRunId, phaseIds);
            Map<Long, Long> taskIdsOfPhases = adaptiveTrainingStatisticsDAO.getTaskIdsOfPhases(trainingRunId, phaseIds);
            for (Map.Entry<Long, Pair<Long, Long>> phaseBoundary : phasesBoundaries.entrySet()) {
                Long phaseId = phaseBoundary.getKey();
                OverallPhaseStatistics overallPhaseStatistics = new OverallPhaseStatistics();
                overallPhaseStatistics.setPhaseId(phaseId);
                overallPhaseStatistics.setPhaseTime(phaseBoundary.getValue().getValue1() - phaseBoundary.getValue().getValue0());
                Map<String, Long> phaseMap = solutionDisplayedInPhases.get(phaseId);
                if (phaseMap != null && phaseMap.get("SolutionDisplayed") != null) {
                    overallPhaseStatistics.setSolutionDisplayed(phaseMap.get("SolutionDisplayed") == 1);
                } else {
                    overallPhaseStatistics.setSolutionDisplayed(false);
                }

                overallPhaseStatistics.setWrongAnswers(wrongAnswersOfPhases.get(phaseId));
                overallPhaseStatistics.setTaskId(taskIdsOfPhases.get(phaseId));
                Pair<Long, Map<String, Long>> phaseCommandsStatistics = adaptiveTrainingStatisticsDAO.findCommandsStatisticsInTimeRange(
                        index,
                        phaseBoundary.getValue().getValue0(),
                        phaseBoundary.getValue().getValue1(),
                        keywordsMapping == null ? null : keywordsMapping.get(phaseId));
                overallPhaseStatistics.setNumberOfCommands(phaseCommandsStatistics.getValue0());
                overallPhaseStatistics.setKeywordsInCommands(phaseCommandsStatistics.getValue1());
                result.add(overallPhaseStatistics);
            }
            return result;
        } catch (ElasticsearchTrainingDataLayerException | IOException ex) {
            throw new ElasticsearchTrainingServiceLayerException(ex);
        }
    }

}
