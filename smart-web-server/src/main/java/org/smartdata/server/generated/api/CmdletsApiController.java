package org.smartdata.server.generated.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@RestController
@RequestMapping("${openapi.sSMAPIDocumentation.base-path:}")
public class CmdletsApiController implements CmdletsApi {

    private final CmdletsApiDelegate delegate;

    public CmdletsApiController(@Autowired(required = false) CmdletsApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new CmdletsApiDelegate() {});
    }

    @Override
    public CmdletsApiDelegate getDelegate() {
        return delegate;
    }

}
