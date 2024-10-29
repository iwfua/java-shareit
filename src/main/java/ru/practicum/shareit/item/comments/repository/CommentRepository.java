package ru.practicum.shareit.item.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.comments.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemIdOrderByCreatedDesc(Long itemId);
}
