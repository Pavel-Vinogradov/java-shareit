package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MockitoSettings(strictness = Strictness.LENIENT)
@Transactional
@DataJpaTest
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private TestEntityManager entityManager;

    private Booking booking1;
    private Booking booking2;
    private User booker1;
    private User booker2;

    @BeforeEach
    void setUp() {
        booker1 = userRepository.save(User.builder()
                .id(1L)
                .name("Raisa")
                .email("Raisa@mail.ru")
                .build());

        booker2 = userRepository.save(User.builder()
                .id(2L)
                .name("Name")
                .email("Name@mail.ru")
                .build());

        Item item1 = itemRepository.save(Item.builder()
                .id(1L)
                .name("Робот-пылесос")
                .description("Лучший работник на планете")
                .ownerId(booker1.getId())
                .available(true)
                .request(1L)
                .build());

        Item item2 = itemRepository.save(Item.builder()
                .id(2L)
                .name("Пауэрбанк")
                .description("Зарядное устройство для телефона")
                .ownerId(booker2.getId())
                .available(true)
                .request(1L)
                .build());

        booking1 = bookingRepository.save(Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusMonths(1))
                .end(LocalDateTime.now().plusYears(1))
                .booker(booker1)
                .item(item1)
                .status(Status.REJECTED)
                .build());

        booking2 = bookingRepository.save(Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusYears(1))
                .end(LocalDateTime.now().plusYears(2))
                .booker(booker2)
                .item(item2)
                .status(Status.REJECTED)
                .build());

    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    public void contextLoads() {
        assertNotNull(entityManager);
    }

    @Test
    void findAllByBookerIdOrderByStartDescTest() {
        User owner = userRepository.findById(booker2.getId()).get();
        long ownerId = owner.getId();
        List<Booking> bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(ownerId, null);

        assertEquals(bookingList.get(0), booking2);
    }

    @Test
    void findAllByBookerIdAndEndIsBeforeTest() {

        User owner = userRepository.findById(booker1.getId()).get();
        long ownerId = owner.getId();
        LocalDateTime localDateTime = LocalDateTime.now().plusYears(1);
        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndEndIsBefore(ownerId, localDateTime, null);
        assertEquals(bookingList.get(0), booking1);

    }

    @Test
    void findAllByBookerIdAndStartIsAfterTest() {

        User owner = userRepository.findById(booker2.getId()).get();
        long ownerId = owner.getId();
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStartIsAfter(ownerId, localDateTime, null);
        assertEquals(bookingList.get(0), booking2);

    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfterTest() {

        User owner = userRepository.findById(booker1.getId()).get();
        long ownerId = owner.getId();
        LocalDateTime time = LocalDateTime.now().minusDays(1);
        LocalDateTime time1 = LocalDateTime.now().plusDays(1);
        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(ownerId, time, time1, null);
        assertEquals(bookingList.get(0), booking1);

    }

    @Test
    void findAllByBookerIdAndStatusTest() {

        User owner = userRepository.findById(booker1.getId()).get();
        long ownerId = owner.getId();
        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStatus(ownerId, Status.REJECTED);
        assertEquals(bookingList.get(0), booking1);

    }

    @Test
    void findAllByItem_Owner_IdOrderByStartDescTest() {

        User owner = userRepository.findById(booker1.getId()).get();
        long ownerId = owner.getId();
        List<Booking> bookingList = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(ownerId, null);
        assertEquals(bookingList.get(0), booking1);
    }

    @Test
    void findAllByItem_Owner_IdAndEndIsBeforeTest() {

        User owner = userRepository.findById(booker1.getId()).get();
        long ownerId = owner.getId();
        LocalDateTime localDateTime = LocalDateTime.now().plusYears(1);
        List<Booking> bookingList = bookingRepository.findAllByItem_OwnerIdAndEndIsBefore(ownerId, localDateTime, null);
        assertEquals(bookingList.get(0), booking1);
    }

    @Test
    void findAllByItem_Owner_IdAndStartIsAfterTest() {

        User owner = userRepository.findById(booker2.getId()).get();
        long ownerId = owner.getId();
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        List<Booking> bookingList = bookingRepository.findAllByItem_OwnerIdAndStartIsAfter(ownerId, localDateTime, null);
        assertEquals(bookingList.get(0), booking2);


    }

    @Test
    void findAllByItem_Owner_IdAndStatusTest() {

        User owner = userRepository.findById(booker1.getId()).get();
        long ownerId = owner.getId();
        LocalDateTime localDateTime = LocalDateTime.now().plusYears(1);
        List<Booking> bookingList = bookingRepository.findAllByItem_OwnerIdAndEndIsBefore(ownerId, localDateTime, null);
        assertEquals(bookingList.get(0), booking1);


    }
}
