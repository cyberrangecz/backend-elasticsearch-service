package cz.cyberrange.platform.elasticsearch.rest.controllers;

import cz.cyberrange.platform.elasticsearch.api.exceptions.ResourceNotFoundException;
import cz.cyberrange.platform.elasticsearch.api.exceptions.ResourceNotModifiedException;
import cz.cyberrange.platform.elasticsearch.data.enums.TrainingType;
import cz.cyberrange.platform.elasticsearch.rest.ApiError;
import cz.cyberrange.platform.elasticsearch.service.TrainingPlatformEventsService;
import cz.cyberrange.platform.elasticsearch.service.exceptions.ElasticsearchTrainingServiceLayerException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The rest controller for Training platform events.
 */
@Api(value = "/training-platform-events",
        tags = "Training events",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        authorizations = @Authorization(value = "bearerAuth"))
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Full authentication is required to access this resource.", response = ApiError.class),
        @ApiResponse(code = 403, message = "The necessary permissions are required for a resource.", response = ApiError.class)
})
@RestController
@RequestMapping(path = "/training-platform-events", produces = MediaType.APPLICATION_JSON_VALUE)
public class TrainingPlatformEventsRestController {

    private final TrainingPlatformEventsService trainingEventsService;

    /**
     * Instantiates a new Training events rest controller.
     *
     * @param trainingPlatformEventsService the training events service
     */
    @Autowired
    public TrainingPlatformEventsRestController(TrainingPlatformEventsService trainingPlatformEventsService) {
        this.trainingEventsService = trainingPlatformEventsService;
    }

    /**
     * Get all events in particular Training Instance.
     *
     * @param trainingDefinitionId id of definition associated with wanted instance
     * @return all events in selected Training Definition.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get all events in particular training definition.",
            nickname = "getAllEventsByTrainingDefinition",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All events in particular training run by id was found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/training-definitions/{definitionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllEventsByTrainingDefinition(
            @ApiParam(value = "Training definition ID", required = true)
            @PathVariable("definitionId") Long trainingDefinitionId) {
        try {
            return ResponseEntity.ok(trainingEventsService.findAllEventsByTrainingDefinition(trainingDefinitionId, TrainingType.LINEAR));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Get all events in particular Training Instance.
     *
     * @param trainingDefinitionId id of definition associated with wanted instance
     * @param trainingInstanceId   id of wanted instance
     * @return all events in selected Training Instance.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get all events in particular training definition and training instance.",
            nickname = "getAllEventsByTrainingDefinitionAndTrainingInstanceId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All events in particular training run by id was found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/training-definitions/{definitionId}/training-instances/{instanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllEventsByTrainingDefinitionAndTrainingInstanceId(
            @ApiParam(value = "Training definition ID", required = true)
            @PathVariable("definitionId") Long trainingDefinitionId,
            @ApiParam(value = "Training instance ID", required = true)
            @PathVariable("instanceId") Long trainingInstanceId) {
        try {
            return ResponseEntity.ok(trainingEventsService.findAllEventsByTrainingDefinitionAndTrainingInstanceId(trainingDefinitionId, trainingInstanceId, TrainingType.LINEAR));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Get all events in particular Training Run.
     *
     * @param trainingDefinitionId id of definition associated with wanted run
     * @param trainingInstanceId   id of instance associated with wanted run
     * @param trainingRunId        id of wanted run
     * @return all events in selected Training Run.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get all events in particular training run.",
            nickname = "getAllEventsFromTrainingRun",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All events in particular training run by id was found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/training-definitions/{definitionId}/training-instances/{instanceId}/training-runs/{runId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllEventsFromTrainingRun(
            @ApiParam(value = "Training definition ID", required = true)
            @PathVariable("definitionId") Long trainingDefinitionId,
            @ApiParam(value = "Training instance ID", required = true)
            @PathVariable("instanceId") Long trainingInstanceId,
            @ApiParam(value = "Training run ID", required = true)
            @PathVariable("runId") Long trainingRunId) {
        try {
            return ResponseEntity.ok(trainingEventsService.findAllEventsFromTrainingRun(trainingDefinitionId, trainingInstanceId, trainingRunId, TrainingType.LINEAR));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Get all events in particular Training Run.
     *
     * @param instanceId   id of instance associated with wanted run
     * @param levelId id of the level
     * @param aggregationField name of the field used to aggregate data
     * @return all events in selected Training Run.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get all events in particular training run.",
            nickname = "getEventsOfTrainingInstanceAndLevelAggregatedByGivenField",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All events in particular training run by id was found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/training-instances/{instanceId}/levels/{levelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getEventsOfTrainingInstanceAndLevelAggregatedByGivenField(
            @ApiParam(value = "Training instance ID", required = true) @PathVariable Long instanceId,
            @ApiParam(value = "Training level ID", required = true) @PathVariable Long levelId,
            @ApiParam(value = "Field used to aggregate data", required = true) @RequestParam("aggregationField") String aggregationField) {
        try {
            return ResponseEntity.ok(trainingEventsService.findAllEventsFromTrainingRunByLevelId(instanceId, levelId, aggregationField, TrainingType.LINEAR));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Get all events in particular Training Instance and sorted by user ref id and timestamp.
     *
     * @param trainingInstanceId id of wanted instance
     * @return all events in selected Training Instance.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get all events in particular training definition and training instance.",
            nickname = "getAllEventsByTrainingDefinitionAndTrainingInstanceId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All events in particular training run by id was found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/training-instances/{instanceId}/aggregated/training-runs/levels", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllEventsByTrainingDefinitionAndTrainingInstanceIdAggregated(
            @ApiParam(value = "Training instance ID", required = true)
            @PathVariable("instanceId") Long trainingInstanceId) {
        try {
            return ResponseEntity.ok(trainingEventsService.getAllEventsOfTrainingInstanceAggregatedByRunsAndLevels(trainingInstanceId));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Get all events of particular Training Instance aggregated by levels and training runs, sorted by timestamp.
     *
     * @param trainingInstanceId id of wanted instance
     * @return all events in selected Training Instance.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get all events in particular training definition and training instance.",
            nickname = "getAllEventsByTrainingDefinitionAndTrainingInstanceId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All events in particular training run by id was found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/training-instances/{instanceId}/aggregated/levels/training-runs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getEventsOfTrainingInstanceAggregatedByLevelsAndRuns(
            @ApiParam(value = "Training instance ID", required = true)
            @PathVariable("instanceId") Long trainingInstanceId) {
        try {
            return ResponseEntity.ok(trainingEventsService.getAllEventsOfTrainingInstanceAggregatedByLevelsAndRuns(trainingInstanceId));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Delete all events in particular Training Run.
     *
     * @param trainingInstanceId id of instance associated with wanted run
     * @param trainingRunId      id of wanted run
     * @return Confirmation that the request process is ok.
     */
    @ApiOperation(httpMethod = "DELETE",
            value = "Delete all events in particular training run.",
            nickname = "deleteEventsFromTrainingRun"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All events in particular training run by id was were deleted."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @DeleteMapping(path = "/training-instances/{instanceId}/training-runs/{runId}")
    public ResponseEntity<Void> deleteEventsFromTrainingRun(
            @ApiParam(value = "Training instance ID", required = true)
            @PathVariable("instanceId") Long trainingInstanceId,
            @ApiParam(value = "Training run ID", required = true)
            @PathVariable("runId") Long trainingRunId) {
        try {
            trainingEventsService.deleteEventsFromTrainingRun(trainingInstanceId, trainingRunId, TrainingType.LINEAR);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }

    /**
     * Delete all events in particular Training Instance.
     *
     * @param trainingInstanceId id of wanted instance
     * @return Confirmation that the request process is ok.
     */
    @ApiOperation(httpMethod = "DELETE",
            value = "Delete all events in particular training instance.",
            nickname = "deleteEventsFromTrainingInstance"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All events in particular training instance by id was were deleted."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @DeleteMapping(path = "/training-instances/{instanceId}")
    public ResponseEntity<Void> deleteEventsFromTrainingInstance(
            @ApiParam(value = "Training instance ID", required = true)
            @PathVariable("instanceId") Long trainingInstanceId) {
        try {
            trainingEventsService.deleteEventsByTrainingInstanceId(trainingInstanceId, TrainingType.LINEAR);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }

}
