package com.marcosbarbero.cloud.zuul.autoconfig.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author Marcos Barbero
 * @since 2017-02-13
 */
public class MutableHttpServletRequest extends HttpServletRequestWrapper {

    private HttpServletRequest request;

    MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public HttpServletRequest getRequest() {
        return this.request;
    }

}
