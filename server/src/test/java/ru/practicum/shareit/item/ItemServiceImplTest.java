package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.comments.repository.CommentRepository;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private CreateItemRequestDto createItemDto;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).name("Test Owner").email("ksljnfd@gmail.com").build();
        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .isAvailable(true)
                .owner(owner)
                .build();

        createItemDto = CreateItemRequestDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
    }

    @Test
    void findItemById_Success() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        ItemResponseDto result = itemService.findItemById(1L);

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
    }

    @Test
    void findItemById_NotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findItemById(1L));
    }

    @Test
    void findItemsByOwnerId_Success() {
        when(userService.findUserById(anyLong())).thenReturn(UserMapper.toCreateUserDto(owner));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        List<ItemResponseDto> results = itemService.findItemsByOwnerId(1L);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(item.getId(), results.get(0).getId());
    }

    @Test
    void addItem_Success() {
        when(userService.findUserById(anyLong())).thenReturn(UserMapper.toCreateUserDto(owner));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponseDto result = itemService.addItem(createItemDto, 1L);

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void addItem_WithRequest_Success() {
        ItemRequest itemRequest = ItemRequest.builder().id(1L).build();
        createItemDto.setRequestId(1L);

        when(userService.findUserById(anyLong())).thenReturn(UserMapper.toCreateUserDto(owner));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponseDto result = itemService.addItem(createItemDto, 1L);

        assertEquals(item.getId(), result.getId());
        verify(itemRepository).save(any(Item.class));
    }
    @Test
    void updateItem_Success() {
        UpdateItemRequestDto updateDto = UpdateItemRequestDto.builder()
                .name("Updated Item")
                .build();
        when(userService.findUserById(anyLong())).thenReturn(UserMapper.toCreateUserDto(owner));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        var result = itemService.updateItem(updateDto, 1L, 1L);

        assertEquals(1L, result.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void search_Success() {
        when(itemRepository.searchItemsByText(anyString())).thenReturn(List.of(item));

        List<ItemResponseDto> results = itemService.search("test");

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void createComment_Success() {
        User booker = User.builder().id(2L).name("Booker").build();

        CommentDto commentDto = CommentDto.builder()
                .text("Test Comment")
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndDateBeforeAndStatus(
                anyLong(), anyLong(), any(), any())).thenReturn(true);

        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(1L);
            comment.setAuthor(booker);
            return comment;
        });

        CommentDto result = itemService.createComment(1L, 2L, commentDto);

        assertNotNull(result);
        assertEquals("Test Comment", result.getText());
    }

    @Test
    void createComment_NoBooking_ThrowsValidationException() {
        CommentDto commentDto = CommentDto.builder()
                .text("Test Comment")
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndDateBeforeAndStatus(
                anyLong(), anyLong(), any(), any())).thenReturn(false);

        assertThrows(ValidationException.class, () ->
                itemService.createComment(1L, 2L, commentDto)
        );
    }
}