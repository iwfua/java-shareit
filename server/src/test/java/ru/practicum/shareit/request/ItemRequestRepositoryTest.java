package ru.practicum.shareit.request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User requester;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setName("Тестовый пользователь");
        requester.setEmail("test@example.com");
        entityManager.persist(requester);

        itemRequest = new ItemRequest();
        itemRequest.setRequester(requester);
        itemRequest.setDescription("Тестовый запрос");
        itemRequest.setCreated(LocalDateTime.now());
        entityManager.persist(itemRequest);

        entityManager.flush();
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc() {
        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(requester.getId());

        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
        assertEquals(itemRequest.getId(), requests.iterator().next().getId());
    }

    @Test
    void findAllByRequesterId() {
        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequesterId(requester.getId());

        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
        assertEquals(itemRequest.getId(), requests.iterator().next().getId());
    }

    @Test
    void findByIdOrderByCreatedAsc() {
        Optional<ItemRequest> foundRequest = itemRequestRepository.findByIdOrderByCreatedAsc(itemRequest.getId());

        assertTrue(foundRequest.isPresent());
        assertEquals(itemRequest.getId(), foundRequest.get().getId());
    }
}