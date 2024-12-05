package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@example.com");
        entityManager.persist(owner);

        item = new Item();
        item.setName("Тестовый предмет");
        item.setDescription("Описание предмета");
        item.setOwner(owner);
        item.setIsAvailable(true);
        entityManager.persist(item);

        entityManager.flush();
    }

    @Test
    void findAllByOwnerId() {
        List<Item> items = itemRepository.findAllByOwnerId(owner.getId());

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    void searchItemsByText_MatchName() {
        List<Item> items = itemRepository.searchItemsByText("Тестовый предмет");

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    void searchItemsByText_MatchDescription() {
        List<Item> items = itemRepository.searchItemsByText("Описание предмета");

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    void searchItemsByText_NoMatch() {
        List<Item> items = itemRepository.searchItemsByText("Несуществующий");

        assertTrue(items.isEmpty());
    }
}