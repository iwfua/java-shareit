package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collections;


@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getItemById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getItemByUserId(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> createItem(ItemRequestDto itemDto, Long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(UpdateItemDto updateItemRequestDto, Long ownerId, Long itemId) {
        return patch("/" + itemId, ownerId, updateItemRequestDto);
    }

    public ResponseEntity<Object> getByText(String text) {

        if (!StringUtils.hasText(text)) {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        } else {
            return get("/search?text=" + text.toLowerCase());
        }
    }

    public ResponseEntity<Object> createComment(Long ownerId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", ownerId, commentDto);
    }
}
