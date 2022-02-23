package com.library.producer.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.producer.domain.LibraryEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LibraryEventProducer {

	private KafkaTemplate<Integer, String> kafkaTemplate;
	private ObjectMapper objectMapper;
	
	public LibraryEventProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}
	
	public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
		
		Integer key = libraryEvent.getLibraryEventId();
		String value = objectMapper.writeValueAsString(libraryEvent);
		ListenableFuture<SendResult<Integer,String>> listenableFuture = kafkaTemplate.sendDefault(key, value);
		listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>> () {

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				handleSuccess(key, value, result);
			}
			
			@Override
			public void onFailure(Throwable ex) {
				handleFailure(key, value, ex);
			}

		});
	}

	private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
		log.info("Message sent successfully for the key: {} and the value is {}, partition is {}", key, value, result.getRecordMetadata().partition());
		
	}
	
	private void handleFailure(Integer key, String value, Throwable ex) {
		log.error("Error Sending the Message and the Exception is {}", ex.getMessage());
		log.error("Error in OnFailure: {}", ex.getMessage());
	}
}
