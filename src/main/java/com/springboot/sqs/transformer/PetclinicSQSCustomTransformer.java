package com.springboot.sqs.transformer;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.transformer.AbstractPayloadTransformer;

@MessageEndpoint
public class PetclinicSQSCustomTransformer extends AbstractPayloadTransformer<Object, String> {

	private final String custom = "Customized ";

	@Override
	public String getComponentType() {
		return "object-to-string-transformer";
	}

	@Override
	protected String transformPayload(Object payload) throws Exception {
		
		if (payload instanceof String) {
			StringBuffer temp = new StringBuffer(custom);
			return temp.append(payload).toString();
		}
		else {
			return payload.toString();
		}
	}

}

