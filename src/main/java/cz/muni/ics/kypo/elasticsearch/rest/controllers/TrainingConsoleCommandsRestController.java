package cz.muni.ics.kypo.elasticsearch.rest.controllers;

import cz.muni.ics.kypo.elasticsearch.api.exceptions.ResourceNotFoundException;
import cz.muni.ics.kypo.elasticsearch.api.exceptions.ResourceNotModifiedException;
import cz.muni.ics.kypo.elasticsearch.rest.ApiError;
import cz.muni.ics.kypo.elasticsearch.service.TrainingConsoleCommandsService;
import cz.muni.ics.kypo.elasticsearch.service.exceptions.ElasticsearchTrainingServiceLayerException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "/training-platform-commands",
     tags = "Training events",
     consumes = MediaType.APPLICATION_JSON_VALUE,
     authorizations = @Authorization(value = "bearerAuth"))

@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Full authentication is required to access this resource.", response = ApiError.class),
        @ApiResponse(code = 403, message = "The necessary permissions are required for a resource.", response = ApiError.class)
})
@RestController
@RequestMapping(path = "/training-platform-commands", produces = MediaType.APPLICATION_JSON_VALUE)
public class TrainingConsoleCommandsRestController {

    private TrainingConsoleCommandsService trainingConsoleCommandsService;

    @Autowired
    public TrainingConsoleCommandsRestController(TrainingConsoleCommandsService trainingConsoleCommandsService) {
        this.trainingConsoleCommandsService = trainingConsoleCommandsService;
    }

    /**
     * Get all commands in particular pool.
     *
     * @param poolId id of wanted sandbox
     * @return all commands in selected pool.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get all commands in particular pool.",
            nickname = "findAllConsoleCommandsByPoolId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All commands in particular pool were found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/pools/{poolId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllConsoleCommandsByPoolId(
            @ApiParam(value = "Training pool ID", required = true)
            @PathVariable("poolId") Long poolId) {
        try {
            return ResponseEntity.ok(trainingConsoleCommandsService.findAllConsoleCommandsByPoolId(poolId));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Get all commands in particular sandbox.
     *
     * @param sandboxId id of wanted sandbox
     * @return all commands in selected sandbox.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get all commands in particular sandbox.",
            nickname = "findAllConsoleCommandsBySandboxId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All commands in particular sandbox were found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/sandboxes/{sandboxId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllConsoleCommandsBySandboxId(
            @ApiParam(value = "Training sandbox ID", required = true)
            @PathVariable("sandboxId") Long sandboxId) {
        try {
            return ResponseEntity.ok(trainingConsoleCommandsService.findAllConsoleCommandsBySandboxId(sandboxId));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Get all commands in particular sandbox aggregated by timestamp ranges.
     *
     * @param sandboxId id of wanted sandbox
     * @param from the lower bound of the time range
     * @param to the upper bound of the time range
     * @return all commands in selected sandbox.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get all commands in particular sandbox.",
            nickname = "findAllConsoleCommandsBySandboxId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All commands in particular sandbox were found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/sandboxes/{sandboxId}/ranges", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllConsoleCommandsBySandboxIdAndTimestampRange(
            @ApiParam(value = "Training sandbox ID", required = true)
            @PathVariable("sandboxId") Long sandboxId,
            @ApiParam(value = "Lower bound of the time range (timestamp in epoch_millis format) of the the resulting console commands.", required = true)
            @RequestParam(value = "from") Long from,
            @ApiParam(value = "Upper bound of the time range (timestamp in epoch_millis format) of the the resulting console commands.", required = true)
            @RequestParam(value = "to") Long to) {
        try {
            return ResponseEntity.ok(trainingConsoleCommandsService.findAllConsoleCommandsBySandboxIdAndTimeRange(sandboxId, from, to));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Delete all commands in particular pool.
     *
     * @param poolId id of pool associated with wanted pool
     * @return Confirmation that the request process is ok.
     */
    @ApiOperation(httpMethod = "DELETE",
            value = "Delete all commands in particular pool.",
            nickname = "deleteConsoleCommandsByPoolId"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All events in particular training sandbox by id were deleted."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @DeleteMapping(path = "/pools/{poolId}")
    public ResponseEntity<Void> deleteConsoleCommandsByPoolId(
            @ApiParam(value = "Training pool ID", required = true)
            @PathVariable("poolId") Long poolId) {
        try {
            trainingConsoleCommandsService.deleteConsoleCommandsByPoolId(poolId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }

    /**
     * Delete all commands in particular sandbox.
     *
     * @param sandboxId id of sandbox associated
     * @return Confirmation that the request process is ok.
     */
    @ApiOperation(httpMethod = "DELETE",
            value = "Delete all commands in particular pool.",
            nickname = "deleteConsoleCommandsByPoolId"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All commands in particular sandbox by id were deleted."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @DeleteMapping(path = "/sandboxes/{sandboxId}")
    public ResponseEntity<Void> deleteConsoleCommandsBySandboxId(
            @ApiParam(value = "Training sandbox ID", required = true)
            @PathVariable("sandboxId") Long sandboxId) {
        try {
            trainingConsoleCommandsService.deleteConsoleCommandsBySandboxId(sandboxId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }

}
