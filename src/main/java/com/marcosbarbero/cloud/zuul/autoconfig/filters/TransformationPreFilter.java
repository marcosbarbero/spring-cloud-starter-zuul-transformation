package com.marcosbarbero.cloud.zuul.autoconfig.filters;

import com.marcosbarbero.cloud.zuul.autoconfig.TransformationProperties;
import com.marcosbarbero.cloud.zuul.autoconfig.props.Policy;
import com.marcosbarbero.cloud.zuul.autoconfig.props.TransformationRequest;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Marcos Barbero
 */
@Slf4j
public class TransformationPreFilter extends ZuulFilter {

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();
    private TransformationProperties transformationProperties;
    private RouteLocator routeLocator;

    public TransformationPreFilter(TransformationProperties transformationProperties,
                                   RouteLocator routeLocator) {
        this.transformationProperties = transformationProperties;
        this.routeLocator = routeLocator;
    }

    /**
     * Get the pathWithinApplication from request.
     *
     * @return The request URI
     */
    protected static String pathWithinApplication() {
        return URL_PATH_HELPER.getPathWithinApplication(RequestContext.getCurrentContext().getRequest());
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
        replace(policy.getRequest().getReplace(), ctx);
        remove(policy.getRequest().getRemove(), ctx.getRequest());
        return null;
    }

    private void add(TransformationRequest.Request transformationRequest, RequestContext ctx) {
        if (TransformationRequestHelper.shouldTransform(transformationRequest, ctx.getRequest())) {
            TransformationRequestHelper.Headers.add(transformationRequest.getHeaders(), ctx);
            TransformationRequestHelper.QueryString.add(transformationRequest.getQueryString(), ctx);
            TransformationRequestHelper.Body.add(transformationRequest.getBody(), ctx);
        }
    }

    private void replace(TransformationRequest.Request transformationRequest, RequestContext ctx) {
        if (TransformationRequestHelper.shouldTransform(transformationRequest, ctx.getRequest())) {
            TransformationRequestHelper.Headers.replace(transformationRequest.getHeaders(), ctx);
            TransformationRequestHelper.QueryString.replace(transformationRequest.getHeaders(), ctx);
        }
    }

    private void remove(TransformationRequest.Request transformationRequest,
                        HttpServletRequest request) {
        if (TransformationRequestHelper.shouldTransform(transformationRequest, request)) {

        }
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
