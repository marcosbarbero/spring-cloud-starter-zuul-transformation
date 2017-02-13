package com.marcosbarbero.cloud.zuul.autoconfig.filters;

import com.marcosbarbero.cloud.zuul.autoconfig.TransformationProperties;
import com.netflix.zuul.ZuulFilter;

import lombok.RequiredArgsConstructor;

/**
 * @author Marcos Barbero
 */
@RequiredArgsConstructor
public class TransformationPostFilter extends ZuulFilter {

    private final TransformationProperties transformationProperties;

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return false;
    }

    @Override
    public Object run() {
        return null;
    }
}
