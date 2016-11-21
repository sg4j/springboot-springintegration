package com.springboot.sqs.route;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.aws.outbound.SqsMessageHandler;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.amazonaws.services.sqs.AmazonSQS;

@Configuration
public class SqsPetQueueSendRoute {

    @Autowired
    private AmazonSQS amazonSqs;

    @Bean
    public MessageChannel fileInputChannel() {
        return new DirectChannel();
    }
    
    @Bean
    public MessageChannel sqsInputChannel() {
        return new DirectChannel();
    }
    
    @Bean
    @InboundChannelAdapter(value = "fileInputChannel")
    public MessageSource<File> fileReadingMessageSource() {
         FileReadingMessageSource source = new FileReadingMessageSource();
         source.setDirectory(new File("f:\\sqsFile"));
         source.setFilter(new SimplePatternFileListFilter("*.txt"));
         return source;
    }
    
    @Bean
    @Transformer(inputChannel = "fileInputChannel", outputChannel = "sqsInputChannel")
    public FileToStringTransformer fileToStringTransformer() {
    	FileToStringTransformer fileTransformer = new FileToStringTransformer();
    	fileTransformer.setDeleteFiles(true);
    	return fileTransformer;
    }

    
    @Bean
    public QueueMessagingTemplate queueMessagingTemplate() {
        return new QueueMessagingTemplate(this.amazonSqs);
    }

    @Bean
    @ServiceActivator(inputChannel = "sqsInputChannel")
    public MessageHandler sqsMessageHandler() {
    	SqsMessageHandler messageHandler = new SqsMessageHandler(queueMessagingTemplate());
    	messageHandler.setQueue("PetQueue");
    	return messageHandler;
    }
}
