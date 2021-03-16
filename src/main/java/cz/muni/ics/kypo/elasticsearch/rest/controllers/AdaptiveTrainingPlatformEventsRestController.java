package cz.muni.ics.kypo.elasticsearch.rest.controllers;

import cz.muni.ics.kypo.elasticsearch.api.exceptions.ResourceNotFoundException;
import cz.muni.ics.kypo.elasticsearch.api.exceptions.ResourceNotModifiedException;
import cz.muni.ics.kypo.elasticsearch.data.enums.TrainingType;
import cz.muni.ics.kypo.elasticsearch.rest.ApiError;
import cz.muni.ics.kypo.elasticsearch.service.TrainingPlatformEventsService;
import cz.muni.ics.kypo.elasticsearch.service.exceptions.ElasticsearchTrainingServiceLayerException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The rest controller for Training platform events.
 */
@Api(value = "/adaptive-training-platform-events",
     tags = "Adaptive Training events",
     consumes = MediaType.APPLICATION_JSON_VALUE,
     authorizations = @Authorization(value = "bearerAuth"))
@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Full authentication is required to access this resource.", response = ApiError.class),
        @ApiResponse(code = 403, message = "The necessary permissions are required for a resource.", response = ApiError.class)
})
@RestController
@RequestMapping(path = "/adaptive-training-platform-events", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdaptiveTrainingPlatformEventsRestController {

    private TrainingPlatformEventsService trainingEventsService;

    /**
     * Instantiates a new Adaptive Training events rest controller.
     *
     * @param trainingPlatformEventsService the training events service
     */
    @Autowired
    public AdaptiveTrainingPlatformEventsRestController(TrainingPlatformEventsService trainingPlatformEventsService) {
        this.trainingEventsService = trainingPlatformEventsService;
    }

    /**
     * Get all events in particular Adaptive Training Instance.
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
            return ResponseEntity.ok(trainingEventsService.findAllEventsByTrainingDefinitionAndTrainingInstanceId(trainingDefinitionId, trainingInstanceId, TrainingType.ADAPTIVE));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Get all events in particular Adaptive Training Run.
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
            return ResponseEntity.ok(trainingEventsService.findAllEventsFromTrainingRun(trainingDefinitionId, trainingInstanceId, trainingRunId, TrainingType.ADAPTIVE));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Delete all events in particular Adaptive Training Run.
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
            trainingEventsService.deleteEventsFromTrainingRun(trainingInstanceId, trainingRunId, TrainingType.ADAPTIVE);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }

    /**
     * Delete all events in particular Adaptive Training Instance.
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
            trainingEventsService.deleteEventsByTrainingInstanceId(trainingInstanceId, TrainingType.ADAPTIVE);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }

}
