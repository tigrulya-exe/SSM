/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (7.3.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.smartdata.server.generated.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.smartdata.server.generated.model.ErrorResponseDto;
import org.smartdata.server.generated.model.LastActivationTimeIntervalDto;
import org.smartdata.server.generated.model.PageRequestDto;
import org.smartdata.server.generated.model.RuleDto;
import org.smartdata.server.generated.model.RuleSortDto;
import org.smartdata.server.generated.model.RuleStateDto;
import org.smartdata.server.generated.model.RulesDto;
import org.smartdata.server.generated.model.RulesInfoDto;
import org.smartdata.server.generated.model.SubmissionTimeIntervalDto;
import org.smartdata.server.generated.model.SubmitRuleRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Generated;
import javax.validation.Valid;

import java.util.List;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Tag(name = "Rules", description = "the Rules API")
public interface RulesApi {

    default RulesApiDelegate getDelegate() {
        return new RulesApiDelegate() {};
    }

    /**
     * POST /api/v2/rules : Submit rule
     *
     * @param submitRuleRequestDto  (required)
     * @return OK (status code 200)
     *         or Data is filled incorrectly (status code 400)
     *         or Unauthorized (status code 401)
     */
    @Operation(
        operationId = "addRule",
        summary = "Submit rule",
        tags = { "Rules" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = RuleDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Data is filled incorrectly", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        },
        security = {
            @SecurityRequirement(name = "basicAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/v2/rules",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    @ResponseStatus(HttpStatus.OK)
    
    default RuleDto addRule(
        @Parameter(name = "SubmitRuleRequestDto", description = "", required = true) @Valid @RequestBody SubmitRuleRequestDto submitRuleRequestDto
    ) throws Exception {
        return getDelegate().addRule(submitRuleRequestDto);
    }


    /**
     * DELETE /api/v2/rules/{id} : Delete rule by id
     *
     * @param id Id of the resource (required)
     * @return Rule has been removed (status code 200)
     *         or Rule with specified id not found (status code 404)
     *         or Unauthorized (status code 401)
     */
    @Operation(
        operationId = "deleteRule",
        summary = "Delete rule by id",
        tags = { "Rules" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Rule has been removed"),
            @ApiResponse(responseCode = "404", description = "Rule with specified id not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        },
        security = {
            @SecurityRequirement(name = "basicAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/api/v2/rules/{id}"
    )
    @ResponseStatus(HttpStatus.OK)
    
    default void deleteRule(
        @Parameter(name = "id", description = "Id of the resource", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id
    ) throws Exception {
        getDelegate().deleteRule(id);
    }


    /**
     * GET /api/v2/rules/{id} : Get rule by id
     *
     * @param id Id of the resource (required)
     * @return OK (status code 200)
     *         or Rule with specified id not found (status code 404)
     *         or Unauthorized (status code 401)
     */
    @Operation(
        operationId = "getRule",
        summary = "Get rule by id",
        tags = { "Rules" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = RuleDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Rule with specified id not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        },
        security = {
            @SecurityRequirement(name = "basicAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/v2/rules/{id}",
        produces = { "application/json" }
    )
    @ResponseStatus(HttpStatus.OK)
    
    default RuleDto getRule(
        @Parameter(name = "id", description = "Id of the resource", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id
    ) throws Exception {
        return getDelegate().getRule(id);
    }


    /**
     * GET /api/v2/rules : List all rules
     *
     * @param pageRequest  (optional)
     * @param sort Sort field names prefixed with &#39;-&#39; for descending order (optional)
     * @param textRepresentationLike The object&#39;s text representation filter.  May contain special characters like \&quot;/\&quot;, \&quot;&#39;\&quot;, so should be encoded. (optional)
     * @param submissionTime Time interval in which the entity was submitted (optional)
     * @param ruleStates List of rule states (optional)
     * @param lastActivationTime Time interval in which the rule was activated (optional)
     * @return OK (status code 200)
     *         or Data is filled incorrectly (status code 400)
     *         or Unauthorized (status code 401)
     */
    @Operation(
        operationId = "getRules",
        summary = "List all rules",
        tags = { "Rules" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = RulesDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Data is filled incorrectly", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        },
        security = {
            @SecurityRequirement(name = "basicAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/v2/rules",
        produces = { "application/json" }
    )
    @ResponseStatus(HttpStatus.OK)
    
    default RulesDto getRules(
        @Parameter(name = "pageRequest", description = "", in = ParameterIn.QUERY) @Valid PageRequestDto pageRequest,
        @Parameter(name = "sort", description = "Sort field names prefixed with '-' for descending order", in = ParameterIn.QUERY) @Valid @RequestParam(value = "sort", required = false) List<@Valid RuleSortDto> sort,
        @Parameter(name = "textRepresentationLike", description = "The object's text representation filter.  May contain special characters like \"/\", \"'\", so should be encoded.", in = ParameterIn.QUERY) @Valid @RequestParam(value = "textRepresentationLike", required = false) String textRepresentationLike,
        @Parameter(name = "submissionTime", description = "Time interval in which the entity was submitted", in = ParameterIn.QUERY) @Valid SubmissionTimeIntervalDto submissionTime,
        @Parameter(name = "ruleStates", description = "List of rule states", in = ParameterIn.QUERY) @Valid @RequestParam(value = "ruleStates", required = false) List<@Valid RuleStateDto> ruleStates,
        @Parameter(name = "lastActivationTime", description = "Time interval in which the rule was activated", in = ParameterIn.QUERY) @Valid LastActivationTimeIntervalDto lastActivationTime
    ) throws Exception {
        return getDelegate().getRules(pageRequest, sort, textRepresentationLike, submissionTime, ruleStates, lastActivationTime);
    }


    /**
     * GET /api/v2/rules/info : Get information about rules
     *
     * @return OK (status code 200)
     *         or Unauthorized (status code 401)
     */
    @Operation(
        operationId = "getRulesInfo",
        summary = "Get information about rules",
        tags = { "Rules" },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = RulesInfoDto.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        },
        security = {
            @SecurityRequirement(name = "basicAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/v2/rules/info",
        produces = { "application/json" }
    )
    @ResponseStatus(HttpStatus.OK)
    
    default RulesInfoDto getRulesInfo(
        
    ) throws Exception {
        return getDelegate().getRulesInfo();
    }


    /**
     * POST /api/v2/rules/{id}/start : Start or continue specified rule
     *
     * @param id Id of the resource (required)
     * @return Rule has been started (status code 200)
     *         or Rule with specified id not found (status code 404)
     *         or Unsupported state transition (status code 400)
     *         or Unauthorized (status code 401)
     */
    @Operation(
        operationId = "startRule",
        summary = "Start or continue specified rule",
        tags = { "Rules" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Rule has been started"),
            @ApiResponse(responseCode = "404", description = "Rule with specified id not found"),
            @ApiResponse(responseCode = "400", description = "Unsupported state transition", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        },
        security = {
            @SecurityRequirement(name = "basicAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/v2/rules/{id}/start",
        produces = { "application/json" }
    )
    @ResponseStatus(HttpStatus.OK)
    
    default void startRule(
        @Parameter(name = "id", description = "Id of the resource", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id
    ) throws Exception {
        getDelegate().startRule(id);
    }


    /**
     * POST /api/v2/rules/{id}/stop : Stop specified rule
     *
     * @param id Id of the resource (required)
     * @return Rule has been stopped (status code 200)
     *         or Rule with specified id not found (status code 404)
     *         or Unsupported state transition (status code 400)
     *         or Unauthorized (status code 401)
     */
    @Operation(
        operationId = "stopRule",
        summary = "Stop specified rule",
        tags = { "Rules" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Rule has been stopped"),
            @ApiResponse(responseCode = "404", description = "Rule with specified id not found"),
            @ApiResponse(responseCode = "400", description = "Unsupported state transition", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        },
        security = {
            @SecurityRequirement(name = "basicAuth")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/v2/rules/{id}/stop",
        produces = { "application/json" }
    )
    @ResponseStatus(HttpStatus.OK)
    
    default void stopRule(
        @Parameter(name = "id", description = "Id of the resource", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id
    ) throws Exception {
        getDelegate().stopRule(id);
    }

}
