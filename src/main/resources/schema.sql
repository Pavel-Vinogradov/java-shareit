DROP ALL OBJECTS;

create table IF NOT EXISTS users
(
    id   BIGINT auto_increment,
    name CHARACTER VARYING(255) not null,
    email     CHARACTER VARYING(512) not null
    constraint "USERS_pk2"
    unique,
    constraint "USERS_pk"
    primary key (id)
    );

create table IF NOT EXISTS items
(
    id      BIGINT auto_increment,
    name    VARCHAR(255)  not null,
    description   VARCHAR(1012) not null,
    is_available BOOLEAN,
    owner_id     BIGINT        not null,
    constraint "ITEMS_pk"
    primary key (id),
    constraint "ITEMS_USERS_ID_fk"
    foreign key (owner_id) references users (id)
    on delete cascade
    );

create table IF NOT EXISTS  bookings
(
    id BIGINT auto_increment,
    start_date TIMESTAMP,
    end_date   TIMESTAMP,
    item_id    BIGINT     not null,
    booker_id  BIGINT     not null,
    status     VARCHAR(8) not null,
    constraint "BOOKINGS_pk"
    primary key (id),
    constraint "BOOKINGS_ITEMS_ID_fk"
    foreign key (item_id) references items (id),
    constraint "BOOKINGS_USERS_ID_fk"
    foreign key (booker_id) references users (id)
    );

create table IF NOT EXISTS requests
(
    id   BIGINT auto_increment,
    description  VARCHAR(1000) not null,
    requestor_id BIGINT        not null,
    constraint "REQUESTS_pk"
    primary key (id),
    constraint "REQUESTS_USERS_ID_fk"
    foreign key (requestor_id) references users (id)
    );

create table IF NOT EXISTS comments
(
    id BIGINT auto_increment,
    text       CHARACTER VARYING(1000) not null,
    item_id    BIGINT                  not null,
    user_id  BIGINT                  not null,
    CREATED    TIMESTAMP,
    constraint "COMMENTS_pk"
    primary key (id),
    constraint "COMMENTS_ITEMS_ID_fk"
    foreign key (item_id) references items (id),
    constraint "COMMENTS_USERS_ID_fk"
    foreign key (user_id) references users (id)
    );