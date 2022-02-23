package com.library.producer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.library.producer.domain.LibraryEvent;
import com.library.producer.producer.LibraryEventProducer;

@RestController
public class LibraryEventsController {

	private LibraryEventProducer libraryEventProducer;
	
	public LibraryEventsController(LibraryEventProducer libraryEventProducer) {
		this.libraryEventProducer = libraryEventProducer;
	}

	@PostMapping("/v1/libraryevent")
	public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
		libraryEventProducer.sendLibraryEvent(libraryEvent);
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}
}
