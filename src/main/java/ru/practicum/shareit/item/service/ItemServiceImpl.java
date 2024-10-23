package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.mapper.CommentsMapper;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.comments.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public ItemDto findItemById(Long id) {
        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(id)
                .stream()
                .map(CommentsMapper::toCommentDto)
                .toList();

        return ItemMapper.toDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("item not found")))
                .toBuilder()
                .comments(comments)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> findItemsByOwnerId(Long ownerId) {
        validateUserExist(ownerId);

        // Получаем все вещи пользователя
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(item -> ItemMapper.toDto(item).toBuilder()
                        .comments(commentRepository.findByItemIdOrderByCreatedDesc(item.getId()).stream()
                                .map(CommentsMapper::toCommentDto)
                                .toList())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        validateUserExist(ownerId);

        Item item = ItemMapper.fromDtoWithOwner(itemDto, ownerId);

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto newItemDto, Long ownerId, Long itemId) {
        validateUserExist(ownerId);

        if (findItemById(itemId) == null) {
            throw new NotFoundException("item not found");
        }

        Item oldItem = ItemMapper.fromDtoWithOwner(findItemById(itemId), ownerId);

        if (newItemDto.getAvailable() != null) {
            oldItem.setIsAvailable(newItemDto.getAvailable());
        }

        if (newItemDto.getName() != null) {
            oldItem.setName(newItemDto.getName());
        }

        if (newItemDto.getDescription() != null) {
            oldItem.setDescription(newItemDto.getDescription());
        }

        return ItemMapper.toDto(itemRepository.save(oldItem));
    }

    @Override
    @Transactional
    public List<ItemDto> search(String text) {
        return itemRepository.searchItemsByText(text).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean hasCompletedBooking = bookingRepository.existsByBookerIdAndItemIdAndEndDateBeforeAndStatus(
                userId,
                itemId,
                LocalDateTime.now(),
                Status.APPROVED
        );

        if (!hasCompletedBooking) {
            throw new ValidationException("User must have completed booking");
        }

        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        return CommentsMapper.toCommentDto(savedComment);
    }

    private void validateUserExist(Long ownerId) {
        if (userService.findUserById(ownerId) == null) {
            throw new NotFoundException("owner not exists");
        }
    }
}
