package org.smartdata.server.generated.api;

import java.util.List;
import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.Valid;
import org.smartdata.server.generated.model.CmdletDto;
import org.smartdata.server.generated.model.CmdletStateDto;
import org.smartdata.server.generated.model.CmdletsDto;
import org.smartdata.server.generated.model.PageRequestDto;
import org.smartdata.server.generated.model.SubmitCmdletRequestDto;
import org.smartdata.server.generated.model.TimeIntervalDto;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A delegate to be called by the {@link CmdletsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface CmdletsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/v2/cmdlets : Submit cmdlet
     *
     * @param submitCmdletRequestDto  (required)
     * @return OK (status code 200)
     *         or Data is filled incorrectly (status code 400)
     * @see CmdletsApi#addCmdlet
     */
    default CmdletDto addCmdlet(SubmitCmdletRequestDto submitCmdletRequestDto) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

    /**
     * DELETE /api/v2/cmdlets/{id} : Delete cmdlet by id
     *
     * @param id Id of the resource (required)
     * @return Cmdlet has been removed (status code 200)
     *         or Cmdlet with specified id not found (status code 404)
     * @see CmdletsApi#deleteCmdlet
     */
    default void deleteCmdlet(Long id) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

    /**
     * GET /api/v2/cmdlets : List all cmdlets
     *
     * @param pageRequest  (optional)
     * @param textRepresentationLike The object&#39;s text representation filter (optional)
     * @param submissionTime Time interval in which the entity was submitted (optional)
     * @param ruleIds Ids of the rules that cmdlets belong to (optional)
     * @param states List of cmdlet states (optional)
     * @param stateChangedTime Time interval in which the state of the cmdlet was changed (optional)
     * @return OK (status code 200)
     *         or Data is filled incorrectly (status code 400)
     * @see CmdletsApi#getCmdlets
     */
    default CmdletsDto getCmdlets(PageRequestDto pageRequest,
        String textRepresentationLike,
        TimeIntervalDto submissionTime,
        List<Long> ruleIds,
        List<@Valid CmdletStateDto> states,
        TimeIntervalDto stateChangedTime) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

    /**
     * POST /api/v2/cmdlets/{id}/stop : Stop specified cmdlet
     *
     * @param id Id of the resource (required)
     * @return Cmdlet has been stopped (status code 200)
     *         or Cmdlet with specified id not found (status code 404)
     * @see CmdletsApi#stopCmdlet
     */
    default void stopCmdlet(Long id) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

    /**
     * GET /api/v2/cmdlets/{id} : Get cmdlet by id
     *
     * @param id Id of the resource (required)
     * @return OK (status code 200)
     *         or Cmdlet with specified id not found (status code 404)
     * @see CmdletsApi#submitCmdlet
     */
    default CmdletDto submitCmdlet(Long id) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

}
