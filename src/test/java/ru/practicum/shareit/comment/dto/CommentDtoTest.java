package ru.practicum.shareit.comment.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CommentDtoTest {

    @Test
    void testHashCode() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("test")
                .created(LocalDateTime.now())
                .authorName("test")
                .build();
        CommentDto commentDto1 = CommentDto.builder()
                .id(2L)
                .text("test")
                .created(LocalDateTime.now())
                .authorName("test")
                .build();
        CommentDto commentDto2 = CommentDto.builder()
                .id(2L)
                .text("test")
                .created(LocalDateTime.now())
                .authorName("test")
                .build();
        assertNotEquals(commentDto.hashCode(), commentDto1.hashCode());
        assertNotEquals(commentDto.hashCode(), commentDto2.hashCode());
    }
}