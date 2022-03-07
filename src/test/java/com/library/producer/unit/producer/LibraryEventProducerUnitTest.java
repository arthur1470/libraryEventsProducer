package com.library.producer.unit.producer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.producer.domain.Book;
import com.library.producer.domain.LibraryEvent;
import com.library.producer.producer.LibraryEventProducer;

@ExtendWith(MockitoExtension.class)
public class LibraryEventProducerUnitTest {

	@Mock
	KafkaTemplate<Integer, String> kafkaTemplate;

	@Spy
	ObjectMapper objectMapper = new ObjectMapper();

	@InjectMocks
	LibraryEventProducer eventProducer;

	@Test
	void sendLibraryEvent_failure() throws Exception {
		Book book = Book.builder().bookId(123).bookName("Kafka using spring boot").bookAuthor("Dilip").build();
		LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();
		SettableListenableFuture future = new SettableListenableFuture();

		future.setException(new RuntimeException("Exception calling kafka"));
		when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

		assertThrows(Exception.class, () -> eventProducer.sendLibraryEventSynchronous(libraryEvent).get());
	}

	@Test
	void sendLibraryEvent_success() throws Exception {
		Book book = Book.builder().bookId(123).bookName("Kafka using spring boot").bookAuthor("Dilip").build();
		LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();
		
		SettableListenableFuture<SendResult<Integer, String>> future = new SettableListenableFuture<SendResult<Integer, String>>();

		String record = objectMapper.writeValueAsString(libraryEvent);
		
		ProducerRecord<Integer, String> producerRecord = new ProducerRecord<Integer, String>("library-events", libraryEvent.getLibraryEventId(), record);
		RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("library-events", 1), 1, 1, 342, System.currentTimeMillis(), 1, 2);
		
		SendResult<Integer, String> futureSendResult = new SendResult<Integer, String>(producerRecord, recordMetadata);
		future.set(futureSendResult);
		
		when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

		ListenableFuture<SendResult<Integer, String>> listenableFuture = eventProducer.sendLibraryEventSynchronous(libraryEvent);
		
		SendResult<Integer, String> sendResult = listenableFuture.get();
		assert sendResult.getRecordMetadata().partition() == 1;
	}
}
