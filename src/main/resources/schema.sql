drop TABLE IF EXISTS comments, requests, bookings, users, items;

create table if not exists users (
    id bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

--CREATE TABLE IF NOT EXISTS users
--(
--    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
--    name  VARCHAR(100) NOT NULL,
--    email VARCHAR(100) NOT NULL,
--    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
--);
create table if not exists requests
(
    id           bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  VARCHAR(512) NOT NULL,
    requestor_id BIGINT,
    created      TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY (requestor_id) REFERENCES users (id)
);

create table if not exists items (
    id bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    available BOOLEAN default false,
    owner_id int references users (id) ON delete CASCADE,
    request_id BIGINT,
    FOREIGN KEY (request_id) REFERENCES requests (id),
    CONSTRAINT pk_item PRIMARY KEY (id)
);

create table if not exists bookings (
    id bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id int references items (id) ON delete CASCADE,
    booker_id int references users (id) ON delete CASCADE,
    status VARCHAR(50) NOT NULL,

    CONSTRAINT pk_booking PRIMARY KEY (id)
);

create table if not exists comments (
    comment_id bigint GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(2048) NOT NULL,
    item_id int references items (id) ON delete CASCADE,
    author_id int references users (id) ON delete CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT pk_comment PRIMARY KEY (comment_id)
);