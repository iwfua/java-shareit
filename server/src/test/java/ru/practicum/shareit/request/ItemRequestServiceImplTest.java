package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private ItemRequestService itemRequestService;
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                userRepository);

        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.now())
                .description("Test Item Request")
                .build();

        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        pageable = PageRequest.of(0, 10, Sort.by("created").descending());
    }

    @Test
    void addItemRequest_Success() {
        ItemRequestDto result = itemRequestService.addItemRequest(user.getId(), itemRequestDto);

        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        verify(itemRequestRepository, times(1)).save(any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void addItemRequest_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.addItemRequest(999L, itemRequestDto)
        );
    }

    @Test
    void getAllItemRequestByOwner_Success() {
        when(itemRequestRepository.findAllByRequesterId(user.getId()))
                .thenReturn(Collections.singletonList(itemRequest));

        Collection<ItemRequestDto> result = itemRequestService.getAllItemRequestByOwner(user.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(itemRequestRepository, times(1)).findAllByRequesterId(user.getId());
    }

    @Test
    void getAllItemRequestByOwner_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getAllItemRequestByOwner(999L)
        );
    }

    @Test
    void getAllItemRequestToOtherUser_Success() {
        List<ItemRequest> requestList = Collections.singletonList(itemRequest);
        Page<ItemRequest> requestPage = new PageImpl<>(requestList, pageable, requestList.size());
        when(itemRequestRepository.findAll(pageable)).thenReturn(requestPage);

        Collection<ItemRequestDto> result = itemRequestService.getAllItemRequestToOtherUser(pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(itemRequestRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllItemRequestToOtherUser_EmptyPage() {
        Page<ItemRequest> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(itemRequestRepository.findAll(pageable)).thenReturn(emptyPage);

        Collection<ItemRequestDto> result = itemRequestService.getAllItemRequestToOtherUser(pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void getItemRequestById_Success() {
        when(itemRequestRepository.findByIdOrderByCreatedAsc(itemRequest.getId()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestDto result = itemRequestService.getItemRequestById(itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        verify(itemRequestRepository, times(1)).findByIdOrderByCreatedAsc(itemRequest.getId());
    }

    @Test
    void getItemRequestById_NotFound() {
        when(itemRequestRepository.findByIdOrderByCreatedAsc(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getItemRequestById(999L)
        );
    }
}