package com.marcosbarbero.cloud.zuul.autoconfig.filters;

import com.marcosbarbero.cloud.zuul.autoconfig.props.TransformationRequest;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.util.Collections.list;

/**
 * @author Marcos Barbero
 */
@Slf4j
public class TransformationRequestHelper {

    protected static final String DELIMITER = ":";
    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();
    private static ProxyRequestHelper requestHelper;

    public TransformationRequestHelper(ProxyRequestHelper requestHelper) {
        TransformationRequestHelper.requestHelper = requestHelper;
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
     * Handle Headers transformation.
     */
    protected static class Headers {
        /**
         * Add headers to request.
         *
         * @param headers The headers to be added
         * @param ctx     The request context
         */
        protected static void add(Set<String> headers, RequestContext ctx) {
            Map<String, String> headersValues = tokenize(headers, DELIMITER);
            list(ctx.getRequest().getHeaderNames()).stream()
                    .filter(headersValues::containsKey).forEach(headersValues::remove);
            for (Map.Entry<String, String> entry : headersValues.entrySet()) {
                ctx.addZuulRequestHeader(entry.getKey(), entry.getValue());
            }
        }

        /**
         * Replace headers from request.
         *
         * @param headers The headers to be replaced
         * @param ctx     The requestContext
         */
        protected static void replace(Set<String> headers, RequestContext ctx) {
//            Map<String, String> replaceHeaders = tokenize(headers, DELIMITER);
//            for (Map.Entry<String, String> entry : replaceHeaders.entrySet()) {
//                ctx.replace(entry.getKey(), entry.getValue());
//            }
        }
    }

    /**
     * Handle the queryString transformation.
     */
    protected static class QueryString {
        /**
         * Add params to queryString.
         *
         * @param params The params to be added on queryString
         * @param ctx    The request context
         */
        public static void add(Set<String> params, RequestContext ctx) {
            if (params == null || params.isEmpty()) {
                return;
            }
            MultiValueMap<String, String> queryParams = requestHelper.buildZuulRequestQueryParams(ctx.getRequest());
            Map<String, String> paramsAsMap = tokenize(params, DELIMITER);
            queryParams.keySet().stream().filter(paramsAsMap::containsKey).forEach(paramsAsMap::remove);
            paramsAsMap.entrySet().stream().forEach(entry -> queryParams.put(entry.getKey(), Arrays.asList(entry.getValue())));
            ctx.setRequestQueryParams(queryParams);
        }

        /**
         * Replace params from queryString.
         *
         * @param params The params to be replaced from queryString
         * @param ctx    The request context
         */
        public static void replace(Set<String> params, RequestContext ctx) {
            if (params == null || params.isEmpty()) {
                return;
            }
            MultiValueMap<String, String> queryParams = requestHelper.buildZuulRequestQueryParams(ctx.getRequest());
            Map<String, String> paramsAsMap = tokenize(params, DELIMITER);
            MultiValueMap<String, String> replacedParams = new LinkedMultiValueMap<>();
            for (String key : queryParams.keySet()) {
                for (String value : queryParams.get(key)) {
                    if (paramsAsMap.containsKey(key)) {
                        replacedParams.add(paramsAsMap.get(key), value);
                    } else {
                        replacedParams.add(key, value);
                    }
                }
            }
            ctx.setRequestQueryParams(replacedParams);
        }

    }

    /**
     * Handle body attributes transformation.
     */
    protected static class Body {
        private HttpServletRequest request;

        protected static void add(Set<String> body, RequestContext ctx) {
            list(ctx.getRequest().getAttributeNames()).forEach(log::info);
            final HttpServletRequest request = ctx.getRequest();
            final InputStream requestBody = getRequestBody(request);
            log.info("RequestBody: ");
            final MediaType mediaType = MediaType.valueOf(request.getContentType());
            if (mediaType.equals(MediaType.APPLICATION_JSON)) {
                // TODO: no ideia
            }

        }

        private static InputStream getRequestBody(HttpServletRequest request) {
            InputStream requestEntity = null;
            try {
                requestEntity = request.getInputStream();
            } catch (IOException ex) {
                // no requestBody is ok.
            }
            return requestEntity;
        }


        private synchronized void buildContentData() {
            try {
                MultiValueMap<String, Object> builder = new LinkedMultiValueMap<String, Object>();
                Set<String> queryParams = findQueryParams();
                for (Map.Entry<String, String[]> entry : this.request.getParameterMap()
                        .entrySet()) {
                    if (!queryParams.contains(entry.getKey())) {
                        for (String value : entry.getValue()) {
                            builder.add(entry.getKey(), value);
                        }
                    }
                }
                if (this.request instanceof MultipartRequest) {
                    MultipartRequest multi = (MultipartRequest) this.request;
                    for (Map.Entry<String, List<MultipartFile>> parts : multi
                            .getMultiFileMap().entrySet()) {
                        for (Part file : this.request.getParts()) {
                            HttpHeaders headers = new HttpHeaders();
                            headers.setContentDispositionFormData(file.getName(),
                                    file.getSubmittedFileName());
                            if (file.getContentType() != null) {
                                headers.setContentType(
                                        MediaType.valueOf(file.getContentType()));
                            }
                            HttpEntity<Resource> entity = new HttpEntity<Resource>(
                                    new InputStreamResource(file.getInputStream()),
                                    headers);
                            builder.add(parts.getKey(), entity);
                        }
                    }
                }
                FormHttpOutputMessage data = new FormHttpOutputMessage();
                this.contentType = MediaType.valueOf(this.request.getContentType());
                data.getHeaders().setContentType(this.contentType);
                this.converter.write(builder, this.contentType, data);
                // copy new content type including multipart boundary
                this.contentType = data.getHeaders().getContentType();
                this.contentData = data.getInput();
                this.contentLength = this.contentData.length;
            }
            catch (Exception e) {
                throw new IllegalStateException("Cannot convert form data", e);
            }
        }

        private Set<String> findQueryParams() {
            Set<String> result = new HashSet<>();
            String query = this.request.getQueryString();
            if (query != null) {
                for (String value : StringUtils.split(query, "&")) {
                    if (value.contains("=")) {
                        value = value.substring(0, value.indexOf("="));
                    }
                    result.add(value);
                }
            }
            return result;
        }
    }

}
