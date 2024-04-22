package org.smartdata.server.generated.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@RestController
@RequestMapping("${openapi.sSMAPIDocumentation.base-path:}")
public class ActionsApiController implements ActionsApi {

    private final ActionsApiDelegate delegate;

    public ActionsApiController(@Autowired(required = false) ActionsApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ActionsApiDelegate() {});
    }

    @Override
    public ActionsApiDelegate getDelegate() {
        return delegate;
    }

}
