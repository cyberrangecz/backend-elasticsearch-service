package cz.muni.ics.kypo.elasticsearch.rest.controllers;

import cz.muni.ics.kypo.elasticsearch.api.exceptions.ResourceNotFoundException;
import cz.muni.ics.kypo.elasticsearch.api.exceptions.ResourceNotModifiedException;
import cz.muni.ics.kypo.elasticsearch.data.enums.CommandType;
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
        tags = "Training commands",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        authorizations = @Authorization(value = "bearerAuth"))

@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Full authentication is required to access this resource.", response = ApiError.class),
        @ApiResponse(code = 403, message = "The necessary permissions are required for a resource.", response = ApiError.class)
})
@RestController
@RequestMapping(path = "/training-platform-commands", produces = MediaType.APPLICATION_JSON_VALUE)
public class TrainingConsoleCommandsRestController {

    private final TrainingConsoleCommandsService trainingConsoleCommandsService;

    @Autowired
    public TrainingConsoleCommandsRestController(TrainingConsoleCommandsService trainingConsoleCommandsService) {
        this.trainingConsoleCommandsService = trainingConsoleCommandsService;
    }

    /**
     * Get all training commands specified by an pool ID.
     *
     * @param poolId id of the pool
     * @return all commands executed in the training.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get all training commands specified by an pool.",
            nickname = "findAllConsoleCommandsByPoolId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All training commands specified by pool ID were found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/pools/{poolId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllConsoleCommandsByPoolId(
            @ApiParam(value = "Training pool ID", required = true) @PathVariable("poolId") Long poolId,
            @ApiParam(value = "List of command to filter")
            @RequestParam(value = "commands", required = false) List<String> commands,
            @ApiParam(value = "Command type")
            @RequestParam(value = "commandType", required = false) CommandType commandType) {
        try {
            return ResponseEntity.ok(trainingConsoleCommandsService.findAllConsoleCommandsByPoolId(poolId, commands, commandType));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }


    /**
     * Get all training commands specified by an access token.
     *
     * @param accessToken access token of the training instance
     * @return all commands executed in the training.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get all training commands specified by an access token.",
            nickname = "findAllConsoleCommandsByAccessToken",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All commands in particular training instance were found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/access-tokens/{accessToken}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllConsoleCommandsByAccessToken(
            @ApiParam(value = "Training access token.", required = true) @PathVariable("accessToken") String accessToken,
            @ApiParam(value = "List of command to filter")
            @RequestParam(value = "commands", required = false) List<String> commands,
            @ApiParam(value = "Command type")
            @RequestParam(value = "commandType", required = false) CommandType commandType) {
        try {
            return ResponseEntity.ok(trainingConsoleCommandsService.findAllConsoleCommandsByAccessToken(accessToken, commands, commandType));
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
            value = "Get all training commands specified by sandbox ID.",
            nickname = "findAllConsoleCommandsBySandboxId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All training commands specified by sandbox ID were found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/sandboxes/{sandboxId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllConsoleCommandsBySandboxId(
            @ApiParam(value = "Training sandbox ID", required = true) @PathVariable("sandboxId") String sandboxId,
            @ApiParam(value = "List of command to filter", required = false)
            @RequestParam(value = "commands", required = false) List<String> commands,
            @ApiParam(value = "Command type")
            @RequestParam(value = "commandType", required = false) CommandType commandType) {
        try {
            return ResponseEntity.ok(trainingConsoleCommandsService.findAllConsoleCommandsBySandboxId(sandboxId, commands, commandType));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Get all training commands specified by an access token and user identifier.
     *
     * @param accessToken access token of the training instance
     * @param userId      identifier of the user
     * @return all commands executed during training.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get all training commands specified by access token and user identifier.",
            nickname = "findAllConsoleCommandsByAccessTokenAndUserId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All training commands specified by access token and user identifier were found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/access-tokens/{accessToken}/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllConsoleCommandsByAccessTokenAndUserId(
            @ApiParam(value = "Training instance access token", required = true) @PathVariable("accessToken") String accessToken,
            @ApiParam(value = "User identifier", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "List of command to filter")
            @RequestParam(value = "commands", required = false) List<String> commands,
            @ApiParam(value = "Command type")
            @RequestParam(value = "commandType", required = false) CommandType commandType) {
        try {
            return ResponseEntity.ok(trainingConsoleCommandsService
                    .findAllConsoleCommandsByAccessTokenAndUserId(accessToken, userId, commands, commandType));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Get all commands aggregated by timestamp ranges of training specified by sandbox ID.
     *
     * @param sandboxId id of wanted sandbox
     * @param from      the lower bound of the time range
     * @param to        the upper bound of the time range
     * @return all commands in selected sandbox.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get aggregated commands of training specified by sandbox ID.",
            nickname = "findAllConsoleCommandsBySandboxIdAndTimestampRange",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All training commands specified by sandbox ID were found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/sandboxes/{sandboxId}/ranges", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllConsoleCommandsBySandboxIdAndTimestampRange(
            @ApiParam(value = "Training sandbox ID", required = true) @PathVariable("sandboxId") String sandboxId,
            @ApiParam(value = "Lower bound of the time range (timestamp in epoch_millis format) of the the resulting console commands.", required = true)
            @RequestParam(value = "from") Long from,
            @ApiParam(value = "Upper bound of the time range (timestamp in epoch_millis format) of the the resulting console commands.", required = true)
            @RequestParam(value = "to") Long to,
            @ApiParam(value = "List of command to filter")
            @RequestParam(value = "commands", required = false) List<String> commands,
            @ApiParam(value = "Command type")
            @RequestParam(value = "commandType", required = false) CommandType commandType) {
        try {
            return ResponseEntity.ok(trainingConsoleCommandsService
                    .findAllConsoleCommandsBySandboxIdAndTimeRange(sandboxId, from, to, commands, commandType));
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }

    /**
     * Get all commands aggregated by timestamp ranges of training specified by access token and user identifier.
     *
     * @param accessToken access token of the training instance
     * @param userId      identifier of the user
     * @param from        the lower bound of the time range
     * @param to          the upper bound of the time range
     * @return all commands in selected sandbox.
     */
    @ApiOperation(httpMethod = "GET",
            value = "Get aggregated commands of training specified by access token and user identifier.",
            nickname = "findAllConsoleCommandsByAccessTokenAndUserIdAndTimestampRange",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All training commands specified by access token and user identifier were found.", responseContainer = "List"),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping(path = "/access-token/{accessToken}/users/{userId}/ranges", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findAllConsoleCommandsByAccessTokenAndUserIdAndTimestampRange(
            @ApiParam(value = "Training instance access token", required = true) @PathVariable("accessToken") String accessToken,
            @ApiParam(value = "User identifier", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "Lower bound of the time range (timestamp in epoch_millis format) of the the resulting console commands.", required = true)
            @RequestParam(value = "from") Long from,
            @ApiParam(value = "Upper bound of the time range (timestamp in epoch_millis format) of the the resulting console commands.", required = true)
            @RequestParam(value = "to") Long to,
            @ApiParam(value = "List of command to filter")
            @RequestParam(value = "commands", required = false) List<String> commands,
            @ApiParam(value = "Command type")
            @RequestParam(value = "commandType", required = false) CommandType commandType) {
        try {
            return ResponseEntity.ok(trainingConsoleCommandsService
                    .findAllConsoleCommandsByAccessTokenAndUserIdAndTimeRange(accessToken, userId, from, to, commands, commandType));
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
            value = "Delete all commands by pool.",
            nickname = "deleteConsoleCommandsByPoolId"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All events in particular training sandbox by id were deleted."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @DeleteMapping(path = "/pools/{poolId}")
    public ResponseEntity<Void> deleteConsoleCommandsByPoolId(
            @ApiParam(value = "Training pool ID", required = true) @PathVariable("poolId") Long poolId) {
        try {
            trainingConsoleCommandsService.deleteConsoleCommandsByPoolId(poolId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }

    /**
     * Delete all commands executed in trainings specified by an access token.
     *
     * @param accessToken access token of the training instance
     * @return Confirmation that the request process is ok.
     */
    @ApiOperation(httpMethod = "DELETE",
            value = "Delete all commands by access token.",
            nickname = "deleteConsoleCommandsByAccessToken"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All events in particular training specified by access token were deleted."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @DeleteMapping(path = "/access-tokens/{accessToken}")
    public ResponseEntity<Void> deleteConsoleCommandsByAccessToken(
            @ApiParam(value = "Training instance access token", required = true) @PathVariable("accessToken") String accessToken) {
        try {
            trainingConsoleCommandsService.deleteConsoleCommandsByAccessToken(accessToken);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }

    /**
     * Delete all commands executed in the training specified by sandbox ID.
     *
     * @param sandboxId id of sandbox associated
     * @return Confirmation that the request process is ok.
     */
    @ApiOperation(httpMethod = "DELETE",
            value = "Delete all commands by sandbox.",
            nickname = "deleteConsoleCommandsByPoolId"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All commands in particular training specified by sandbox ID were deleted."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @DeleteMapping(path = "/sandboxes/{sandboxId}")
    public ResponseEntity<Void> deleteConsoleCommandsBySandboxId(
            @ApiParam(value = "Training sandbox ID", required = true)
            @PathVariable("sandboxId") String sandboxId) {
        try {
            trainingConsoleCommandsService.deleteConsoleCommandsBySandboxId(sandboxId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }

    /**
     * Delete all commands executed in the training specified by access token and user identifier.
     *
     * @param accessToken access token of the training instance
     * @param userId      identifier of the user
     * @return Confirmation that the request process is ok.
     */
    @ApiOperation(httpMethod = "DELETE",
            value = "Delete all commands by access token and user identifier.",
            nickname = "deleteConsoleCommandsByAccessTokenAndUserId"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All commands in particular training specified by access token and user identifier were deleted."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @DeleteMapping(path = "/access-tokens/{accessToken}/users/{userId}")
    public ResponseEntity<Void> deleteConsoleCommandsByAccessTokenAndUserId(
            @ApiParam(value = "Training instance access token", required = true) @PathVariable("accessToken") String accessToken,
            @ApiParam(value = "User identifier", required = true) @PathVariable("userId") Long userId) {
        try {
            trainingConsoleCommandsService.deleteConsoleCommandsByAccessTokenAndUserId(accessToken, userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ElasticsearchTrainingServiceLayerException ex) {
            throw new ResourceNotModifiedException(ex);
        }
    }

}
