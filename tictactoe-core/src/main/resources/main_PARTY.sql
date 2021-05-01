create table PARTY
(
    id            INTEGER not null
        constraint PARTY
            primary key autoincrement,
    board_size    INTEGER not null,
    time_of_party text    not null,
    winner_id     integer
        references PLAYER
);

