create table MOVE
(
    id        INTEGER not null
        constraint MOVE
            primary key autoincrement,
    tile      TEXT    not null,
    player_id INTEGER not null
        references PLAYER,
    party_id  INTEGER not null
        references PARTY
);

