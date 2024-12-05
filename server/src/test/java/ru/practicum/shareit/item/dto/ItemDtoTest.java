package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoTest {
    private final JacksonTester<CreateItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        User owner = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        CreateItemRequestDto createItemRequestDto = CreateItemRequestDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .requestId(2L)
                .available(true)
                .owner(owner)
                .build();

        JsonContent<CreateItemRequestDto> result = json.write(createItemRequestDto);

        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.name")
                .hasJsonPath("$.description")
                .hasJsonPath("$.requestId")
                .hasJsonPath("$.available")
                .hasJsonPath("$.owner");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(createItemRequestDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.name")
                .satisfies(name -> assertThat(name).isEqualTo(createItemRequestDto.getName()));
        assertThat(result).extractingJsonPathStringValue("$.description")
                .satisfies(description -> assertThat(description).isEqualTo(createItemRequestDto.getDescription()));
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .satisfies(requestId -> assertThat(requestId.longValue()).isEqualTo(createItemRequestDto.getRequestId()));
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .satisfies(available -> assertThat(available).isEqualTo(createItemRequestDto.getAvailable()));

        assertThat(result).hasJsonPath("$.owner.id")
                .hasJsonPath("$.owner.name")
                .hasJsonPath("$.owner.email");
    }
}
