package org.smartdata.server.generated.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.smartdata.server.generated.model.CachedFilesDto;
import org.smartdata.server.generated.model.FileAccessCountsDto;
import org.smartdata.server.generated.model.PageRequestDto;
import org.smartdata.server.generated.model.TimeIntervalDto;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A delegate to be called by the {@link FilesApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface FilesApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/v2/files/access-counts : List access counts of files
     *
     * @param pageRequest  (optional)
     * @param pathLike The file path filter (optional)
     * @param lastAccessedTime Time interval in which the file was accessed (optional)
     * @return OK (status code 200)
     *         or Data is filled incorrectly (status code 400)
     * @see FilesApi#getAccessCounts
     */
    default FileAccessCountsDto getAccessCounts(PageRequestDto pageRequest,
        String pathLike,
        TimeIntervalDto lastAccessedTime) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

    /**
     * GET /api/v2/files/cached : List cached files
     *
     * @param pageRequest  (optional)
     * @param pathLike The file path filter (optional)
     * @param lastAccessedTime Time interval in which the file was accessed (optional)
     * @param cachedTime Time interval in which the file was cached (optional)
     * @return OK (status code 200)
     *         or Data is filled incorrectly (status code 400)
     * @see FilesApi#getCachedFiles
     */
    default CachedFilesDto getCachedFiles(PageRequestDto pageRequest,
        String pathLike,
        TimeIntervalDto lastAccessedTime,
        TimeIntervalDto cachedTime) throws Exception {
        throw new IllegalArgumentException("Not implemented");

    }

}
