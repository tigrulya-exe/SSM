package org.smartdata.server.generated.config;

import org.smartdata.server.generated.model.ActionSourceDto;
import org.smartdata.server.generated.model.ActionStateDto;
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
