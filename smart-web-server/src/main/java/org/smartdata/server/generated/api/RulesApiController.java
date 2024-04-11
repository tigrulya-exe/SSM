package org.smartdata.server.generated.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@RestController
@RequestMapping("${openapi.sSMAPIDocumentation.base-path:}")
public class RulesApiController implements RulesApi {

    private final RulesApiDelegate delegate;

    public RulesApiController(@Autowired(required = false) RulesApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new RulesApiDelegate() {});
    }

    @Override
    public RulesApiDelegate getDelegate() {
        return delegate;
    }

}
