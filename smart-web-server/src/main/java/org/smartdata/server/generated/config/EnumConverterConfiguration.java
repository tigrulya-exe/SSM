/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.server.generated.config;

import org.smartdata.server.generated.model.ActionSourceDto;
import org.smartdata.server.generated.model.ActionStateDto;
import org.smartdata.server.generated.model.AuditEventResultDto;
import org.smartdata.server.generated.model.AuditObjectTypeDto;
import org.smartdata.server.generated.model.AuditOperationDto;
import org.smartdata.server.generated.model.CmdletStateDto;
import org.smartdata.server.generated.model.ExecutorTypeDto;
import org.smartdata.server.generated.model.RuleStateDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class EnumConverterConfiguration {

    @Bean(name = "org.smartdata.server.generated.config.EnumConverterConfiguration.actionSourceConverter")
    Converter<String, ActionSourceDto> actionSourceConverter() {
        return new Converter<String, ActionSourceDto>() {
            @Override
            public ActionSourceDto convert(String source) {
                return ActionSourceDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.smartdata.server.generated.config.EnumConverterConfiguration.actionStateConverter")
    Converter<String, ActionStateDto> actionStateConverter() {
        return new Converter<String, ActionStateDto>() {
            @Override
            public ActionStateDto convert(String source) {
                return ActionStateDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.smartdata.server.generated.config.EnumConverterConfiguration.auditEventResultConverter")
    Converter<String, AuditEventResultDto> auditEventResultConverter() {
        return new Converter<String, AuditEventResultDto>() {
            @Override
            public AuditEventResultDto convert(String source) {
                return AuditEventResultDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.smartdata.server.generated.config.EnumConverterConfiguration.auditObjectTypeConverter")
    Converter<String, AuditObjectTypeDto> auditObjectTypeConverter() {
        return new Converter<String, AuditObjectTypeDto>() {
            @Override
            public AuditObjectTypeDto convert(String source) {
                return AuditObjectTypeDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.smartdata.server.generated.config.EnumConverterConfiguration.auditOperationConverter")
    Converter<String, AuditOperationDto> auditOperationConverter() {
        return new Converter<String, AuditOperationDto>() {
            @Override
            public AuditOperationDto convert(String source) {
                return AuditOperationDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.smartdata.server.generated.config.EnumConverterConfiguration.cmdletStateConverter")
    Converter<String, CmdletStateDto> cmdletStateConverter() {
        return new Converter<String, CmdletStateDto>() {
            @Override
            public CmdletStateDto convert(String source) {
                return CmdletStateDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.smartdata.server.generated.config.EnumConverterConfiguration.executorTypeConverter")
    Converter<String, ExecutorTypeDto> executorTypeConverter() {
        return new Converter<String, ExecutorTypeDto>() {
            @Override
            public ExecutorTypeDto convert(String source) {
                return ExecutorTypeDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.smartdata.server.generated.config.EnumConverterConfiguration.ruleStateConverter")
    Converter<String, RuleStateDto> ruleStateConverter() {
        return new Converter<String, RuleStateDto>() {
            @Override
            public RuleStateDto convert(String source) {
                return RuleStateDto.fromValue(source);
            }
        };
    }

}
