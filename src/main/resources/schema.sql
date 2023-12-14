DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS users
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(40),
    email VARCHAR(40) UNIQUE
    );

create table IF NOT EXISTS requests
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  text not null,
    requestor_id INT REFERENCES users (id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL
    );

CREATE TABLE IF NOT EXISTS items (
 id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(40),
    description VARCHAR(255),
    is_available BOOLEAN,
    REQUEST         BIGINT,
    owner_id INT REFERENCES users(id) ON DELETE CASCADE,
    request_item_id INT REFERENCES requests(id) ON DELETE CASCADE
    );


create table IF NOT EXISTS  bookings
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    status VARCHAR(30),
    item_id INT REFERENCES items (id) ON DELETE CASCADE,
    booker_id INT REFERENCES users (id) ON DELETE CASCADE
    );


create table IF NOT EXISTS comments
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text TEXT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE,
    item_id INT REFERENCES items (id) ON DELETE CASCADE,
    author_id  INT REFERENCES users (id) ON DELETE CASCADE
    );