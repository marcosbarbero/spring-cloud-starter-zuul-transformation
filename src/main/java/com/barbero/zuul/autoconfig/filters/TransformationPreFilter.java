package com.barbero.zuul.autoconfig.filters;

import com.barbero.zuul.autoconfig.TransformationProperties;
import com.barbero.zuul.autoconfig.filters.TransformationRequestHelper.Body;
import com.barbero.zuul.autoconfig.filters.TransformationRequestHelper.Headers;
import com.barbero.zuul.autoconfig.filters.TransformationRequestHelper.QueryString;
import com.barbero.zuul.autoconfig.props.Policy;
import com.barbero.zuul.autoconfig.props.TransformationRequest;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Marcos Barbero
 */
public class TransformationPreFilter extends ZuulFilter {

    private final static Log logger = LogFactory.getLog(TransformationPreFilter.class);
    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();
    private TransformationProperties transformationProperties;
    private RouteLocator routeLocator;

    public TransformationPreFilter(TransformationProperties transformationProperties,
                                   RouteLocator routeLocator) {
        this.transformationProperties = transformationProperties;
        this.routeLocator = routeLocator;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return this.transformationProperties.isEnabled() &&
                this.transformationProperties.getPolicies().get(this.serviceId()) != null;
    }

    @Override
    public Object run() {
        final RequestContext ctx = RequestContext.getCurrentContext();
        final Policy policy = this.transformationProperties.getPolicies().get(this.serviceId());
        add(policy.getRequest().getAdd(), ctx);
        replace(policy.getRequest().getReplace(), ctx.getRequest());
        remove(policy.getRequest().getRemove(), ctx.getRequest());
        return null;
    }

    private void add(TransformationRequest.Request transformationRequest, RequestContext ctx) {
        if (TransformationRequestHelper.shouldTransform(transformationRequest, ctx.getRequest())) {
            Headers.add(transformationRequest.getHeaders(), ctx);
            QueryString.add(transformationRequest.getQueryString(), ctx);
            Body.add(transformationRequest.getBody(), ctx);
        }
    }

    private void replace(TransformationRequest.Request transformationRequest,
                         HttpServletRequest request) {
        if (TransformationRequestHelper.shouldTransform(transformationRequest, request)) {

        }
    }

    private void remove(TransformationRequest.Request transformationRequest,
                        HttpServletRequest request) {
        if (TransformationRequestHelper.shouldTransform(transformationRequest, request)) {

        }
    }

    /**
     * Get the pathWithinApplication from request.
     *
     * @return The request URI
     */
    protected static String pathWithinApplication() {
        return URL_PATH_HELPER.getPathWithinApplication(RequestContext.getCurrentContext().getRequest());
    }

    /**
     * Return the serviceId from request.
     *
     * @return The serviceId
     */
    private String serviceId() {
        final Route route = this.routeLocator.getMatchingRoute(pathWithinApplication());
        return (route != null) ? route.getId().toLowerCase() : null;
    }

}
