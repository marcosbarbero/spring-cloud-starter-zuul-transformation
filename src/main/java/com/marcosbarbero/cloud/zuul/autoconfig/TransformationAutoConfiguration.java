package com.marcosbarbero.cloud.zuul.autoconfig;

import com.marcosbarbero.cloud.zuul.autoconfig.filters.TransformationPostFilter;
import com.marcosbarbero.cloud.zuul.autoconfig.filters.TransformationPreFilter;
import com.marcosbarbero.cloud.zuul.autoconfig.filters.TransformationRequestHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Marcos Barbero
 */
@Configuration
@EnableConfigurationProperties(TransformationProperties.class)
@ConditionalOnProperty(value = "zuul.transformer.enabled", havingValue = "true")
public class TransformationAutoConfiguration {

    @Bean
    public TransformationRequestHelper transformationRequestHelper(ProxyRequestHelper proxyRequestHelper) {
        return new TransformationRequestHelper(proxyRequestHelper);
    }

    @Bean
    @ConditionalOnBean(TransformationRequestHelper.class)
    public TransformationPreFilter transformationPreFilter(TransformationProperties transformationProperties,
                                                           RouteLocator routeLocator) {
        return new TransformationPreFilter(transformationProperties, routeLocator);
    }

    @Bean
    public TransformationPostFilter transformationPostFilter(TransformationProperties transformationProperties) {
        return new TransformationPostFilter(transformationProperties);
    }
}
