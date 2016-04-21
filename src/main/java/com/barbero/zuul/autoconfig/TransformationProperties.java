package com.barbero.zuul.autoconfig;

import com.barbero.zuul.autoconfig.props.Policy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcos Barbero
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("zuul.transformer")
public class TransformationProperties {
    private boolean enabled;
    private Map<String, Policy> policies = new HashMap<>();
}