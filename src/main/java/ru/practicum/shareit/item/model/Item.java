package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.model.Comment;

import javax.persistence.*;
import java.util.List;


/**
 * TODO Sprint add-controllers.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
@Table(name = "items")
@Builder
public class Item {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    @Column(name = "is_available")
    private Boolean available;
    @Column(name = "request")
    private Long request;
    @OneToMany(mappedBy = "item")
    private List<Booking> bookings;
    @OneToMany(mappedBy = "item")
    private List<Comment> comments;
}
