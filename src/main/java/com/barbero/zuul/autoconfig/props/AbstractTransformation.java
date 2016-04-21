package com.barbero.zuul.autoconfig.props;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Marcos Barbero
 */
@Data
public class AbstractTransformation {
    private Set<String> headers = new LinkedHashSet<>();
    private Set<String> body = new LinkedHashSet<>();
}