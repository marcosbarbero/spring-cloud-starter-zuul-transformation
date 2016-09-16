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
public class Policy {
    private TransformationRequest request = new TransformationRequest();
    private TransformationResponse response = new TransformationResponse();
}
