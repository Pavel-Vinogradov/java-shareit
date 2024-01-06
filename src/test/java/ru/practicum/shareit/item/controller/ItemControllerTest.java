package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {

        User.builder().id(1L).name("Raisa").email("raisa@mail.ru").build();

        itemDto = ItemDto.builder().id(1L).name("Щётка для обуви").description("Стандартная щётка для обуви").available(true).ownerId(1L).lastBooking(null).nextBooking(null).requestId(1L).build();
        commentDto = CommentDto.builder()
                .id(1L)
                .text("commentText")
                .authorName("commentAuthorName")
                .build();

    }

    @AfterEach
    void tearDown() {
    }

    @SneakyThrows
    @Test
    void searchFilmsTest() {
        ItemDto secondItem = ItemDto.builder()
                .id(2L)
                .name("item2Name")
                .description("item2Description")
                .available(true)
                .build();

        when(itemService.searchItem("description", 0, 10))
                .thenReturn(List.of(itemDto, secondItem));


        mockMvc.perform(get("/items/search")
                        .param("text", "description")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Щётка для обуви")))
                .andExpect(jsonPath("$[0].description", is("Стандартная щётка для обуви")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("item2Name")))
                .andExpect(jsonPath("$[1].description", is("item2Description")))
                .andExpect(jsonPath("$[1].available", is(true)));
    }

    @Test
    @SneakyThrows
    void getItemByIdTest() {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/{id}", itemDto.getId()).header(USER_ID_HEADER, 1).content(objectMapper.writeValueAsString(itemDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.name", is(itemDto.getName()))).andExpect(jsonPath("$.description", is(itemDto.getDescription())));

    }

    @Test
    @SneakyThrows
    void getItemByUserIdTest() {

        when(itemService.getItemsByUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Щётка для обуви")))
                .andExpect(jsonPath("$[0].description", is("Стандартная щётка для обуви")))
                .andExpect(jsonPath("$[0].available", is(true)));
    }

    @SneakyThrows
    @Test
    void saveItemTest() {
        when(itemService.saveItem(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Щётка для обуви")))
                .andExpect(jsonPath("$.description", is("Стандартная щётка для обуви")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @SneakyThrows
    @Test
    void updateItemTest() {
        ItemDto updatedItem = itemDto;
        updatedItem.setName("update");
        updatedItem.setDescription("update");

        when(itemService.updateItem(any(ItemDto.class), anyLong()))
                .thenReturn(updatedItem);


        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .content(new ObjectMapper().writeValueAsString(updatedItem))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("update")))
                .andExpect(jsonPath("$.description", is("update")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/items" + "/1"))
                .andExpect(status().isOk());
        verify(itemService, times(1))
                .deleteItemById(anyLong());
    }

    @Test
    void addComment() throws Exception {
        when(itemService.postComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{id}/comment", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .content(new ObjectMapper().writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("commentText")))
                .andExpect(jsonPath("$.authorName", is("commentAuthorName")));
    }
}
