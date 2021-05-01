create table PLAYER
(
    id   integer not null
        constraint PLAYER
            primary key autoincrement,
    name text    not null
);

INSERT INTO PLAYER (id, name)
VALUES (1, 'Computer');
