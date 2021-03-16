package cz.muni.ics.kypo.elasticsearch.rest.controllers;

import cz.muni.ics.kypo.elasticsearch.api.exceptions.ResourceNotFoundException;
import cz.muni.ics.kypo.elasticsearch.api.exceptions.ResourceNotModifiedException;
import cz.muni.ics.kypo.elasticsearch.rest.ApiError;
import cz.muni.ics.kypo.elasticsearch.service.AdaptiveTrainingStatisticsService;
import cz.muni.ics.kypo.elasticsearch.service.exceptions.ElasticsearchTrainingServiceLayerException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * The rest controller for Training platform events.
 */
@Api(value = "/training-statistics",
        tags = "Training statistics",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        authorizations = @Authorization(value = "bearerAuth"))
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Full authentication is required to access this resource.", response = ApiError.class),
        @ApiResponse(code = 403, message = "The necessary permissions are required for a resource.", response = ApiError.class)
})
@RestController
@RequestMapping(path = "/training-statistics", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdaptiveTrainingStatisticsRestController {

    private AdaptiveTrainingStatisticsService adaptiveTrainingStatisticsService;

    /**
     * Instantiates a new Training events rest controller.
     *
     * @param adaptiveTrainingStatisticsService the training statistics service
     */
    @Autowired
    public AdaptiveTrainingStatisticsRestController(AdaptiveTrainingStatisticsService adaptiveTrainingStatisticsService) {
        this.adaptiveTrainingStatisticsService = adaptiveTrainingStatisticsService;
    }

    /**
     * Get statistic about entered commands in the particular phases.
     *
     * @param trainingRunId        id of wanted run
     * @param phaseIds ids of the phases whose statistic are requested
     * @param keywordsMapping  optional mapping of the keywords to phases.
     * @return number of entered commands in the specified phases. If keywords mapping is set, contains information about the presence of keywords in individual levels.
     */
    @ApiOperation(httpMethod = "POST",
            value = "Get commands statistics for particular training run and phases.",
            nickname = "getCommandsStatistics",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All events in particular training run by id was found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @PostMapping(path = "/training-runs/{runId}/phases/commands", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getCommandsStatistics(
            @ApiParam(value = "Training run ID", required = true)
            @PathVariable("runId") Long trainingRunId,
            @ApiParam(value = "Phase Ids", required = true)
            @RequestParam("phaseIds") List<Long> phaseIds,
            @ApiParam(value = "Training run ID", required = false)
            @RequestBody(required = false) Map<Long, List<String>> keywordsMapping) {
        try {
            return ResponseEntity.ok(adaptiveTrainingStatisticsService.findCommandsStatistic(trainingRunId, phaseIds, keywordsMapping));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Get time taken in the specified phases.
     *
     * @param trainingRunId      id of wanted run
     * @param phaseIds ids of the phases whose statistic are requested
     * @return time taken in each specified phase.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get time statistics about phases for particular training run and phases.",
            nickname = "getPhaseTimeStatistics"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Time statistics was found."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/training-runs/{runId}/phases/events/time")
    public ResponseEntity<Object> getPhaseTimeStatistics(
            @ApiParam(value = "Training run ID", required = true)
            @PathVariable("runId") Long trainingRunId,
            @ApiParam(value = "Phase Ids", required = true)
            @RequestParam("phaseIds") List<Long> phaseIds) {
        try {
            return ResponseEntity.ok(adaptiveTrainingStatisticsService.findPhaseTimeStatistics(trainingRunId, phaseIds));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }

    /**
     * Get information if the solution has been taken in the specified phases.
     *
     * @param trainingRunId      id of wanted run
     * @param phaseIds ids of the phases whose statistic are requested
     * @return true value for each specified phase in which the solution was taken, false otherwise.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get solution statistics for particular training run and phases.",
            nickname = "getPhaseSolutionsStatistics"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All events in particular training instance by id was were deleted."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/training-runs/{runId}/phases/events/solutions")
    public ResponseEntity<Object> getPhaseSolutionsStatistics(
            @ApiParam(value = "Training run ID", required = true)
            @PathVariable("runId") Long trainingRunId,
            @ApiParam(value = "Phase Ids", required = true)
            @RequestParam("phaseIds") List<Long> phaseIds) {
        try {
            return ResponseEntity.ok(adaptiveTrainingStatisticsService.findPhaseSolutionsStatistics(trainingRunId, phaseIds));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }
    /**
     * Get wrong answers statistics of the specified phases.
     *
     * @param trainingRunId      id of wanted run
     * @param phaseIds ids of the phases whose statistic are requested
     * @return number of wrong answers submitted for each specified phase.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Obtaing all wrong answers in phases.",
            nickname = "getPhaseWrongAnswersStatistics"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All wrong answers of the particular phases have been found."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/training-runs/{runId}/phases/events/wrong-answers")
    public ResponseEntity<Object> getPhaseWrongAnswersStatistics(
            @ApiParam(value = "Training run ID", required = true)
            @PathVariable("runId") Long trainingRunId,
            @ApiParam(value = "Phase Ids", required = true)
            @RequestParam("phaseIds") List<Long> phaseIds) {
        try {
            return ResponseEntity.ok(adaptiveTrainingStatisticsService.findPhaseWrongAnswersStatistics(trainingRunId, phaseIds));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }

    /**
     * Get overall statistic in the particular phases.
     *
     * @param trainingRunId      id of wanted run
     * @param phaseIds ids of the phases whose statistic are requested
     * @param keywordsMapping  optional mapping of the keywords to phases.
     * @return overall statistics of the specified phases.
     */
    @ApiOperation(httpMethod = "POST",
            value = "Get overall statistics.",
            nickname = "getOverallPhasesStatistics"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Overall statistics was found."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @PostMapping(path = "/training-runs/{runId}/phases/overall")
    public ResponseEntity<Object> getOverallPhasesStatistics(
            @ApiParam(value = "Training run ID", required = true)
            @PathVariable("runId") Long trainingRunId,
            @ApiParam(value = "Phase Ids", required = true)
            @RequestParam("phaseIds") List<Long> phaseIds,
            @ApiParam(value = "Training run ID", required = false)
            @RequestBody(required = false) Map<Long, List<String>> keywordsMapping) {
        try {
            return ResponseEntity.ok(adaptiveTrainingStatisticsService.findOverallStatistics(trainingRunId, phaseIds, keywordsMapping));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }


}
