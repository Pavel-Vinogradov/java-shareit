package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long ownerId, Pageable pageable);

    @Query("SELECT it FROM Item it " +
            "WHERE (LOWER(it.name) LIKE LOWER(CONCAT('%', :text, '%') ) OR " +
            "LOWER(it.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND it.available = true")
    List<Item> searchItem(String text,Pageable pageable);

}