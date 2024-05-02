package org.smartdata.server.generated.api;

import java.util.List;
import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.Valid;
import org.smartdata.server.generated.model.AuditEventResultDto;
import org.smartdata.server.generated.model.AuditEventsDto;
import org.smartdata.server.generated.model.AuditObjectTypeDto;
import org.smartdata.server.generated.model.AuditOperationDto;
import org.smartdata.server.generated.model.EventTimeIntervalDto;
import org.smartdata.server.generated.model.PageRequestDto;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A delegate to be called by the {@link AuditApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface AuditApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/v2/audit/events : List all audit events
     *
     * @param pageRequest  (optional)
     * @param usernameLike Filter of the name of the user who performed the event (optional)
     * @param eventTime Time interval in which the event occurred (optional)
     * @param objectTypes List of audit object types (optional)
     * @param objectIds Ids of the event objects (optional)
     * @param operations List of audit operations (optional)
     * @param results List of audit event results (optional)
     * @return OK (status code 200)
     *         or Data is filled incorrectly (status code 400)
     * @see AuditApi#getAuditEvents
     */
    default AuditEventsDto getAuditEvents(PageRequestDto pageRequest,
        String usernameLike,
        EventTimeIntervalDto eventTime,
        List<@Valid AuditObjectTypeDto> objectTypes,
        List<Long> objectIds,
        List<@Valid AuditOperationDto> operations,
        List<@Valid AuditEventResultDto> results) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

}
