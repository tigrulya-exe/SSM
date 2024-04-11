package org.smartdata.server.generated.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@RestController
@RequestMapping("${openapi.sSMAPIDocumentation.base-path:}")
public class FilesApiController implements FilesApi {

    private final FilesApiDelegate delegate;

    public FilesApiController(@Autowired(required = false) FilesApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new FilesApiDelegate() {});
    }

    @Override
    public FilesApiDelegate getDelegate() {
        return delegate;
    }

}
