package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ShareItServerTest {

    @Test
    public void testMainMethod() {
        String[] args = new String[]{};

        ShareItServer.main(args);
    }
}