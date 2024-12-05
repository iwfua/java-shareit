package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.booking.model.Status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        bookingRequestDto = new BookingRequestDto(start, end, 1L);

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Item")
                .build();

        CreateUserDto userDto = CreateUserDto.builder()
                .id(1L)
                .name("User")
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(itemResponseDto)
                .booker(userDto)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(any(), anyLong())).thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));

        verify(bookingService, times(1)).createBooking(any(), anyLong());
    }

    @Test
    void updateBooking() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));

        verify(bookingService, times(1)).updateBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void findBookingById() throws Exception {
        when(bookingService.findBookingById(anyLong(), anyLong())).thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));

        verify(bookingService, times(1)).findBookingById(anyLong(), anyLong());
    }

    @Test
    void findBookingsByUser() throws Exception {
        when(bookingService.findBookingByUserId(anyLong(), anyString())).thenReturn(Collections.singletonList(bookingResponseDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.getStatus().toString())));

        verify(bookingService, times(1)).findBookingByUserId(anyLong(), anyString());
    }
}