package org.smartdata.server.generated.api;

import java.util.Optional;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@RestController
@RequestMapping("${openapi.sSMAPIDocumentation.base-path:}")
public class AuditApiController implements AuditApi {

    private final AuditApiDelegate delegate;

    public AuditApiController(@Autowired(required = false) AuditApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new AuditApiDelegate() {});
    }

    @Override
    public AuditApiDelegate getDelegate() {
        return delegate;
    }

}
