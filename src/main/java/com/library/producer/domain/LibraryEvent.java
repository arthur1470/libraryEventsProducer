package com.library.producer.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
	@NotNull
	@Valid
	private Book book;
	private LibraryEventType libraryEventType;
}
