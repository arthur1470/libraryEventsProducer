package com.library.producer.producer;

import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.producer.domain.LibraryEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LibraryEventProducer {

	private KafkaTemplate<Integer, String> kafkaTemplate;
	private ObjectMapper objectMapper;
	private final String TOPIC = "library-events";
	
	public LibraryEventProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}
	
	public void sendLibraryEventSynchronous (LibraryEvent libraryEvent) throws Exception {
		Integer key = libraryEvent.getLibraryEventId();
		String value = objectMapper.writeValueAsString(libraryEvent);
		
		ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, TOPIC);
		
		ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send(producerRecord);
		
		listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				log.info("Success: ", result.getProducerRecord());
			}
			
			@Override
			public void onFailure(Throwable ex) {
				log.error("Failure: ", ex.getMessage());
			}
		});
		
		
	}
	
	private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
		List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));	
		return new ProducerRecord<>(topic, null, key, value, recordHeaders);
	}
}
