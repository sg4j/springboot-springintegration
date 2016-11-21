package com.springboot.sqs.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.aws.inbound.SqsMessageDrivenChannelAdapter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.PollableChannel;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.springboot.sqs.transformer.PetclinicSQSCustomTransformer;

@Configuration
public class SqsPetQueueReceiveRoute {

    @Autowired
    private AmazonSQSAsync amazonSqs;

    @Bean
    public MessageChannel sqsAdapterOutputChannel() {
        return new DirectChannel();
    }
    
    @Bean
    public MessageChannel sqsTransMsgOutputChannel() {
        return new DirectChannel();
    }
    
    @Bean
    public SqsMessageDrivenChannelAdapter sqsMessageDrivenChannelAdapter() {
        SqsMessageDrivenChannelAdapter adapter = new SqsMessageDrivenChannelAdapter(this.amazonSqs, "PetQueue");
        adapter.setOutputChannel(sqsAdapterOutputChannel());
        adapter.setMessageDeletionPolicy(SqsMessageDeletionPolicy.ON_SUCCESS);
        return adapter;
    }
    
	@Bean
	@Transformer(inputChannel="sqsAdapterOutputChannel", outputChannel="sqsTransMsgOutputChannel")
	public org.springframework.integration.transformer.Transformer converttoString() {
		return new PetclinicSQSCustomTransformer();
	}
    
    @Bean
    @ServiceActivator(inputChannel = "sqsTransMsgOutputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                System.out.println(message.getPayload());
            }
        };
    }
}
