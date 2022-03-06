package com.library.producer.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.library.producer.domain.LibraryEvent;
import com.library.producer.domain.LibraryEventType;
import com.library.producer.producer.LibraryEventProducer;

@RestController
public class LibraryEventsController {

	private LibraryEventProducer libraryEventProducer;
	
	public LibraryEventsController(LibraryEventProducer libraryEventProducer) {
		this.libraryEventProducer = libraryEventProducer;
	}

	@PostMapping("/v1/libraryevent")
	public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws Exception {
		libraryEvent.setLibraryEventType(LibraryEventType.NEW);
		libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}
}
