package com.barbero.zuul.autoconfig.filters;

import com.barbero.zuul.autoconfig.TransformationProperties;
import com.netflix.zuul.ZuulFilter;

/**
 * @author Marcos Barbero
 */
public class TransformationPostFilter extends ZuulFilter {

    private TransformationProperties transformationProperties;

    public TransformationPostFilter(TransformationProperties transformationProperties) {
        this.transformationProperties = transformationProperties;
    }

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
