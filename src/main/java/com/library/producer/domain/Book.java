package com.library.producer.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

	@NotNull
	private Integer bookId;
	@NotBlank
	private String bookName;
	@NotBlank
	private String bookAuthor;
}
