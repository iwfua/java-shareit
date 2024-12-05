package ru.practicum.shareit.item.comments;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.mapper.CommentsMapper;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentsMapperTest {

    @Test
    void toCommentDto_ValidComment_MapsAllFields() {
        // Arrange
        User author = new User();
        author.setName("John Doe");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setAuthor(author);
        LocalDateTime created = LocalDateTime.now();
        comment.setCreated(created);

        // Act
        CommentDto result = CommentsMapper.toCommentDto(comment);

        // Assert
        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(author.getName(), result.getAuthorName());
        assertEquals(created, result.getCreated());
    }

    @Test
    void toCommentDto_NullComment_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> CommentsMapper.toCommentDto(null));
    }

    @Test
    void toCommentDtoCollection_NullCollection_ReturnsNull() {
        // Act
        List<CommentDto> result = CommentsMapper.toCommentDtoCollection(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toCommentDtoCollection_EmptyCollection_ReturnsNull() {
        // Act
        List<CommentDto> result = CommentsMapper.toCommentDtoCollection(Collections.emptyList());

        // Assert
        assertNull(result);
    }

    @Test
    void toCommentDtoCollection_MultipleComments_CorrectMapping() {
        // Arrange
        User author1 = new User();
        author1.setName("John Doe");
        User author2 = new User();
        author2.setName("Jane Smith");

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("First comment");
        comment1.setAuthor(author1);
        comment1.setCreated(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("Second comment");
        comment2.setAuthor(author2);
        comment2.setCreated(LocalDateTime.now());

        Collection<Comment> comments = Arrays.asList(comment1, comment2);

        // Act
        List<CommentDto> result = CommentsMapper.toCommentDtoCollection(comments);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(comment1.getId(), result.get(0).getId());
        assertEquals(comment2.getId(), result.get(1).getId());
    }

    @Test
    void toCommentDtoCollection_CommentWithNullAuthor_ThrowsNullPointerException() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment with null author");
        comment.setAuthor(null);

        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> CommentsMapper.toCommentDtoCollection(Collections.singletonList(comment)));
    }

    @Test
    void toCommentDto_MinimalComment_StillMapsSuccessfully() {
        // Arrange
        User author = new User();
        author.setName("Minimal User");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        // Act
        CommentDto result = CommentsMapper.toCommentDto(comment);

        // Assert
        assertNotNull(result);
        assertEquals("", result.getText());
    }
}