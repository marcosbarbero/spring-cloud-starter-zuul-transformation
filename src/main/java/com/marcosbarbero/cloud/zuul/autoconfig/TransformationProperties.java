package com.marcosbarbero.cloud.zuul.autoconfig;

import com.marcosbarbero.cloud.zuul.autoconfig.props.Policy;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.marcosbarbero.cloud.zuul.autoconfig.TransformationProperties.PREFIX;

/**
 * @author Marcos Barbero
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(PREFIX)
public class TransformationProperties {

    public static final String PREFIX = "zuul.transformer";

    private boolean enabled;
    private Map<String, Policy> policies = new HashMap<>();
}