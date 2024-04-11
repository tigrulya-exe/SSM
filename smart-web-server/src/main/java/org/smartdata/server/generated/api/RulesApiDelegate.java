package org.smartdata.server.generated.api;

import java.util.List;
import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.Valid;
import org.smartdata.server.generated.model.PageRequestDto;
import org.smartdata.server.generated.model.RuleDto;
import org.smartdata.server.generated.model.RuleStateDto;
import org.smartdata.server.generated.model.RulesDto;
import org.smartdata.server.generated.model.SubmitRuleRequestDto;
import org.smartdata.server.generated.model.TimeIntervalDto;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A delegate to be called by the {@link RulesApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface RulesApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/v2/rules : Submit rule
     *
     * @param submitRuleRequestDto  (required)
     * @return OK (status code 200)
     *         or Data is filled incorrectly (status code 400)
     * @see RulesApi#addRule
     */
    default RuleDto addRule(SubmitRuleRequestDto submitRuleRequestDto) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

    /**
     * DELETE /api/v2/rules/{id} : Delete rule by id
     *
     * @param id Id of the resource (required)
     * @return Rule has been removed (status code 200)
     *         or Rule with specified id not found (status code 404)
     * @see RulesApi#deleteRule
     */
    default void deleteRule(Long id) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

    /**
     * GET /api/v2/rules/{id} : Get rule by id
     *
     * @param id Id of the resource (required)
     * @return OK (status code 200)
     *         or Rule with specified id not found (status code 404)
     * @see RulesApi#getRule
     */
    default RuleDto getRule(Long id) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

    /**
     * GET /api/v2/rules : List all rules
     *
     * @param pageRequest  (optional)
     * @param textRepresentationLike The object&#39;s text representation filter (optional)
     * @param submissionTime Time interval in which the entity was submitted (optional)
     * @param ruleStates List of rule states (optional)
     * @param lastCheckTime Time interval in which the rule was activated (optional)
     * @return OK (status code 200)
     *         or Data is filled incorrectly (status code 400)
     * @see RulesApi#getRules
     */
    default RulesDto getRules(PageRequestDto pageRequest,
        String textRepresentationLike,
        TimeIntervalDto submissionTime,
        List<@Valid RuleStateDto> ruleStates,
        TimeIntervalDto lastCheckTime) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

    /**
     * POST /api/v2/rules/{id}/start : Start or continue specified rule
     *
     * @param id Id of the resource (required)
     * @return Rule has been started (status code 200)
     *         or Rule with specified id not found (status code 404)
     *         or Unsupported state transition (status code 400)
     * @see RulesApi#startRule
     */
    default void startRule(Long id) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

    /**
     * POST /api/v2/rules/{id}/stop : Stop specified rule
     *
     * @param id Id of the resource (required)
     * @return Rule has been stopped (status code 200)
     *         or Rule with specified id not found (status code 404)
     *         or Unsupported state transition (status code 400)
     * @see RulesApi#stopRule
     */
    default void stopRule(Long id) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

}
