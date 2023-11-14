package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import org.apache.catalina.connector.Request;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Request request;
}
