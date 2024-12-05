package ru.practicum.shareit.item.comments.mapper;

import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.model.Comment;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CommentsMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentDto> toCommentDtoCollection(Collection<Comment> comments) {
        System.out.println();
        if (comments == null || comments.isEmpty()) {
            return null;
        }
        return comments.stream().map(CommentsMapper::toCommentDto).collect(Collectors.toList());
    }
}
