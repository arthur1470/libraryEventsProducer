package com.library.producer.unit.controller;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.producer.controller.LibraryEventsController;
import com.library.producer.domain.Book;
import com.library.producer.domain.LibraryEvent;
import com.library.producer.producer.LibraryEventProducer;

@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventControllerUnitTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	LibraryEventProducer libraryEventProducer;

	@Test
	void postLibraryEvent() throws Exception {
		Book book = Book.builder().bookId(123).bookName("Kafka using spring boot").bookAuthor("Dilip").build();

		LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();

		String json = objectMapper.writeValueAsString(libraryEvent);
		doNothing().when(libraryEventProducer).sendLibraryEventSynchronous(isA(LibraryEvent.class));
		
		
		mockMvc.perform(post("/v1/libraryevent")
					.content(json)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}
	
	@Test
	void postLibraryEvent_4xx() throws Exception {
		Book book = Book.builder().bookId(null).bookName("Kafka using spring boot").bookAuthor("").build();
		
		LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();

		String json = objectMapper.writeValueAsString(libraryEvent);
		doNothing().when(libraryEventProducer).sendLibraryEventSynchronous(isA(LibraryEvent.class));
		
		String expectedErrorMessage = "book.bookAuthor - must not be blank, book.bookId - must not be null";
		
		mockMvc.perform(post("/v1/libraryevent")
					.content(json)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError())
				.andExpect(content().string(expectedErrorMessage));
		
		
	}
}
