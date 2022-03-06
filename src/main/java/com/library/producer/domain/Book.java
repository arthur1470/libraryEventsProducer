package com.library.producer.domain;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

	private Integer bookId;
	@NotBlank
	private String bookName;
	@NotBlank
	private String bookAuthor;
}
