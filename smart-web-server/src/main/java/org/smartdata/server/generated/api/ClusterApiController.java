package org.smartdata.server.generated.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@RestController
@RequestMapping("${openapi.sSMAPIDocumentation.base-path:}")
public class ClusterApiController implements ClusterApi {

    private final ClusterApiDelegate delegate;

    public ClusterApiController(@Autowired(required = false) ClusterApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ClusterApiDelegate() {});
    }

    @Override
    public ClusterApiDelegate getDelegate() {
        return delegate;
    }

}
