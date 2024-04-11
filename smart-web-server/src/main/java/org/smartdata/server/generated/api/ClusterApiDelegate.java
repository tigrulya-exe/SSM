package org.smartdata.server.generated.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.smartdata.server.generated.model.ClusterNodesDto;
import org.smartdata.server.generated.model.PageRequestDto;
import org.smartdata.server.generated.model.TimeIntervalDto;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A delegate to be called by the {@link ClusterApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface ClusterApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/v2/cluster/nodes : List all cluster nodes
     *
     * @param pageRequest  (optional)
     * @param registrationTime Time interval in which node was registered in master (optional)
     * @return OK (status code 200)
     *         or Data is filled incorrectly (status code 400)
     * @see ClusterApi#getClusterNodes
     */
    default ClusterNodesDto getClusterNodes(PageRequestDto pageRequest,
        TimeIntervalDto registrationTime) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

}
