package com.marcosbarbero.cloud.zuul.autoconfig.props;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @author Marcos Barbero
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransformationRequest {

    private Request remove = new Request();
    private Request replace = new Request();
    private Request add = new Request();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public class Request extends AbstractTransformation {
        private Set<RequestMethod> methods = new LinkedHashSet<>();
        private Set<String> ignoredPaths = new LinkedHashSet<>();
        private Set<String> queryString = new LinkedHashSet<>();
    }

    public enum RequestMethod {
        GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE, ALL;

        static Map<String, RequestMethod> lookup = new HashMap<>();

        static {
            for (RequestMethod requestMethod : RequestMethod.values()) {
                lookup.put(requestMethod.name(), requestMethod);
            }

        }

        public static RequestMethod get(String method) {
            return lookup.get(method);
        }
    }
}