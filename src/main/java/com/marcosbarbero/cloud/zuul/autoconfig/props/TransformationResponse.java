package com.marcosbarbero.cloud.zuul.autoconfig.props;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Marcos Barbero
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransformationResponse {

    private Response remove = new Response();
    private Response replace = new Response();
    private Response add = new Response();

    public static class Response extends AbstractTransformation {
    }
}
