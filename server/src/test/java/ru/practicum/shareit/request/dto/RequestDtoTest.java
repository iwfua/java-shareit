package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestDtoTest {
    private final JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("Test")
                .email("Test@test.com")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.now())
                .description("TestItemRequestDescription")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("Test")
                .description("Test")
                .request(itemRequest)
                .isAvailable(true)
                .owner(user)
                .build();
        itemRequest.setItems(List.of(item));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.created")
                .hasJsonPath("$.description")
                .hasJsonPath("$.requester.id")
                .hasJsonPath("$.requester.name")
                .hasJsonPath("$.requester.email")
                .hasJsonPath("$.items[0].id")
                .hasJsonPath("$.items[0].name")
                .hasJsonPath("$.items[0].description")
                .hasJsonPath("$.items[0].available");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(itemRequestDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.created")
                .satisfies(created -> assertThat(created).isNotNull());
        assertThat(result).extractingJsonPathNumberValue("$.requester.id")
                .satisfies(requester_id -> assertThat(requester_id.longValue()).isEqualTo(itemRequestDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.requester.name")
                .satisfies(requester_name -> assertThat(requester_name).isEqualTo(itemRequestDto.getRequester().getName()));
        assertThat(result).extractingJsonPathStringValue("$.requester.email")
                .satisfies(requester_email -> assertThat(requester_email).isEqualTo(itemRequestDto.getRequester().getEmail()));
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id")
                .satisfies(item_id -> assertThat(item_id.longValue()).isEqualTo(itemRequestDto.getItems().iterator().next().getId()));
        assertThat(result).extractingJsonPathStringValue("$.items[0].name")
                .satisfies(item_name -> assertThat(item_name).isEqualTo(itemRequestDto.getItems().iterator().next().getName()));
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
                .satisfies(item_description -> assertThat(item_description).isEqualTo(itemRequestDto.getItems().iterator().next().getDescription()));
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available")
                .satisfies(item_available -> assertThat(item_available).isEqualTo(itemRequestDto.getItems().iterator().next().getAvailable()));
    }
}
