package com.library.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibraryEvent {

	private Integer libraryEventId;
	private Book book;
	private LibraryEventType libraryEventType;
}
