package org.smartdata.server.generated.api;

import java.util.List;
import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.Valid;
import org.smartdata.server.generated.model.ActionDto;
import org.smartdata.server.generated.model.ActionInfoDto;
import org.smartdata.server.generated.model.ActionSourceDto;
import org.smartdata.server.generated.model.ActionsDto;
import org.smartdata.server.generated.model.CmdletStateDto;
import org.smartdata.server.generated.model.PageRequestDto;
import org.smartdata.server.generated.model.SubmitActionRequestDto;
import org.smartdata.server.generated.model.TimeIntervalDto;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A delegate to be called by the {@link ActionsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface ActionsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/v2/actions/{id} : Get action by id
     *
     * @param id Id of the resource (required)
     * @return OK (status code 200)
     *         or Action with specified id not found (status code 404)
     * @see ActionsApi#getAction
     */
    default ActionDto getAction(Long id) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

    /**
     * GET /api/v2/actions : List all actions
     *
     * @param pageRequest  (optional)
     * @param textRepresentationLike The object&#39;s text representation filter (optional)
     * @param submissionTime Time interval in which the entity was submitted (optional)
     * @param hosts List of hosts on which the action is/was running (optional)
     * @param states List of cmdlet states (optional)
     * @param sources List of action sources (optional)
     * @param completionTime Time interval in which the action was finished (optional)
     * @return OK (status code 200)
     *         or Data is filled incorrectly (status code 400)
     * @see ActionsApi#getActions
     */
    default ActionsDto getActions(PageRequestDto pageRequest,
        String textRepresentationLike,
        TimeIntervalDto submissionTime,
        List<String> hosts,
        List<@Valid CmdletStateDto> states,
        List<@Valid ActionSourceDto> sources,
        TimeIntervalDto completionTime) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

    /**
     * POST /api/v2/actions : Submit action
     *
     * @param submitActionRequestDto  (required)
     * @return OK (status code 200)
     *         or Data is filled incorrectly (status code 400)
     * @see ActionsApi#submitAction
     */
    default ActionInfoDto submitAction(SubmitActionRequestDto submitActionRequestDto) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

}
