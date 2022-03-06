package com.library.producer.intg.controller;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.producer.domain.Book;
import com.library.producer.domain.LibraryEvent;
import com.library.producer.domain.LibraryEventType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}", 
								  "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}" })
public class LibraryEventsControllerIntegrationTest {

	@Autowired
	TestRestTemplate restTemplate;
	
	@Autowired
	EmbeddedKafkaBroker embeddedKafkaBroker;
	
	@Autowired
	ObjectMapper objectMapper;
	
	private Consumer<Integer, String> consumer;
	
	@BeforeEach
	void setUp() {
		Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
		consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
		embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
	}
	
	@AfterEach
	void tearDown() {
		consumer.close();
	}
	
	@Test	
	void postLibraryEvent() throws JsonProcessingException {
		Book book = Book.builder()
				.bookId(123)
				.bookName("Kafka using spring boot")
				.bookAuthor("Dilip")
				.build();
				
		LibraryEvent libraryEvent = LibraryEvent.builder()
				.libraryEventId(null)
				.book(book)
				.build();
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("content-type", MediaType.APPLICATION_JSON.toString());
		HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);
		
		ResponseEntity<LibraryEvent> responseEntity = restTemplate.exchange("/v1/libraryevent", HttpMethod.POST, request, LibraryEvent.class);

		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		
		ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
		
		libraryEvent.setLibraryEventType(LibraryEventType.NEW);
		
		String expected = objectMapper.writeValueAsString(libraryEvent);
		
		String value = consumerRecord.value();
		
		assertEquals(expected, value);
	}
}
