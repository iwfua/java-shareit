package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private CreateItemRequestDto createItemRequestDto;
    private ItemResponseDto itemResponseDto;
    private UpdateItemRequestDto updateItemRequestDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();

        createItemRequestDto = CreateItemRequestDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build();

        updateItemRequestDto = UpdateItemRequestDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        commentDto = CommentDto.builder()
                .text("Test Comment")
                .build();
    }

    @Test
    void findItemById() throws Exception {
        when(itemService.findItemById(anyLong())).thenReturn(itemResponseDto);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName())));

        verify(itemService).findItemById(1L);
    }

    @Test
    void getItems() throws Exception {
        List<ItemResponseDto> items = List.of(itemResponseDto);
        when(itemService.findItemsByOwnerId(anyLong())).thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(items)));

        verify(itemService).findItemsByOwnerId(1L);
    }

    @Test
    void searchItem() throws Exception {
        List<ItemResponseDto> searchResult = List.of(itemResponseDto);
        when(itemService.search(anyString())).thenReturn(searchResult);

        mvc.perform(get("/items/search")
                        .param("text", "test")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(searchResult)));

        verify(itemService).search("test");
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(any(), anyLong())).thenReturn(itemResponseDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(createItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName())));

        verify(itemService).addItem(any(), anyLong());
    }

    @Test
    void updateItem() throws Exception {
        UpdateItemResponseDto updateResponseDto = UpdateItemResponseDto.builder()
                .id(1L)
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(updateResponseDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updateResponseDto.getName())));

        verify(itemService).updateItem(any(), anyLong(), anyLong());
    }

    @Test
    void createComment() throws Exception {
        CommentDto savedComment = CommentDto.builder()
                .id(1L)
                .text("Test Comment")
                .build();

        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(savedComment);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(savedComment.getText())));

        verify(itemService).createComment(anyLong(), anyLong(), any());
    }
}
