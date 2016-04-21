package com.barbero.zuul.autoconfig.filters;

import com.barbero.zuul.autoconfig.props.TransformationRequest;
import com.netflix.zuul.context.RequestContext;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Marcos Barbero
 */
public class TransformationRequestHelper {

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();
    protected static final String DELIMITER = ":";

    private static ProxyRequestHelper PROXY_HELPER;

    public TransformationRequestHelper(ProxyRequestHelper proxyHelper) {
        PROXY_HELPER = proxyHelper;
    }

    /**
     * Get the pathWithinApplication from request.
     *
     * @return The request URI
     */
    protected static String pathWithinApplication() {
        return URL_PATH_HELPER.getPathWithinServletMapping(RequestContext.getCurrentContext().getRequest());
    }

    /**
     * Tokenize the {@link String} from {@link java.util.Set to a {@link Map} with given delimiter.
     *
     * @param values    A {@link java.util.Set} of ${@link String}
     * @param delimiter The string delimiter
     * @return A Map of tokenized {@link String}
     */
    protected static Map<String, String> tokenize(Set<String> values, String delimiter) {
        Map<String, String> valuesMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (String value : values) {
            String[] split = value.split(delimiter);
            valuesMap.put(split[0], split[1]);
        }
        return valuesMap;
    }

    /**
     * Verify is the current request should be transformed.
     *
     * @param transformationRequest The TransformationRequest policy
     * @param httpRequest           The current {@link HttpServletRequest}
     * @return True if the current request should be transformed, otherwise false
     */
    protected static boolean shouldTransform(TransformationRequest.Request transformationRequest, HttpServletRequest httpRequest) {
        return isValidPath(transformationRequest.getIgnoredPaths()) && isValidMethod(transformationRequest.getMethods(), httpRequest);
    }

    /**
     * Verify if the current request is not in ignoredPaths entry.
     *
     * @param ignoredPaths The ignored paths
     * @return True if the current request is not an ignored path, otherwise false.
     */
    private static boolean isValidPath(final Set<String> ignoredPaths) {
        final String path = pathWithinApplication();
        return !ignoredPaths.stream().anyMatch(path::matches);
    }

    /**
     * Verify if the current HTTP Method should be transformed
     *
     * @param methods The ignored paths
     * @param request The current HTTP Request
     * @return True if the current HTTP Method should be transformed, otherwise false.
     */
    private static boolean isValidMethod(Set<TransformationRequest.RequestMethod> methods, HttpServletRequest request) {
        return methods.contains(TransformationRequest.RequestMethod.get(request.getMethod()));
    }

    /**
     * Handle the queryString transformation.
     */
    protected static class QueryString {
        public static void add(Set<String> params, RequestContext ctx) {
            if (params == null || params.isEmpty()) {
                return;
            }
            MultiValueMap<String, String> queryParams = PROXY_HELPER.buildZuulRequestQueryParams(ctx.getRequest());
            if (queryParams == null) {
                queryParams = new LinkedMultiValueMap<>();
            }
            final Map<String, String> paramsAsMap = tokenize(params, DELIMITER);
            queryParams.keySet().stream().filter(paramsAsMap::containsKey).forEach(paramsAsMap::remove);
//            paramsAsMap.entrySet().stream().forEach(entry -> queryParams.put(entry.getKey(), entry.getValue()));
            ctx.setRequestQueryParams(queryParams);
        }
    }

    /**
     * Handle Headers transformation.
     */
    protected static class Headers {
        protected static void add(Set<String> headers, RequestContext ctx) {
            Map<String, String> headersValues = tokenize(headers, DELIMITER);
            Collections.list(ctx.getRequest().getHeaderNames()).stream()
                    .filter(headersValues::containsKey).forEach(headersValues::remove);
            for (Map.Entry<String, String> entry : headersValues.entrySet()) {
                ctx.addZuulRequestHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Handle body attributes transformation.
     */
    protected static class Body {
        protected static void add(Set<String> body, RequestContext ctx) {

        }
    }

}
