create table conversations
(
    id         bigint auto_increment
        primary key,
    group_name varchar(30)                         null,
    created_at timestamp default CURRENT_TIMESTAMP null
);

create table users
(
    user_id      bigint auto_increment
        primary key,
    avatar_url   varchar(150) null,
    introduction text         null,
    password     varchar(16)  null,
    created_at   datetime     null,
    username     varchar(20)  null,
    email        varchar(20)  null,
    position     varchar(10)  null,
    back_url     varchar(150) null,
    email_cut    varchar(20)  null,
    constraint email
        unique (email),
    constraint email_cut
        unique (email_cut)
);

create table follows
(
    id          bigint auto_increment
        primary key,
    follower_id bigint                              null,
    followee_id bigint                              null,
    created_at  timestamp default CURRENT_TIMESTAMP null,
    status      tinyint   default 0                 null,
    constraint follows_ibfk_1
        foreign key (follower_id) references users (user_id)
            on delete set null,
    constraint follows_ibfk_2
        foreign key (followee_id) references users (user_id)
            on delete set null
);

create index followee_id
    on follows (followee_id);

create index follower_id
    on follows (follower_id);

create table messages
(
    message_id      bigint auto_increment
        primary key,
    conversation_id bigint                              null,
    sender_id       bigint                              null,
    content         varchar(255)                        null,
    type            tinyint   default 0                 null,
    sent_at         timestamp default CURRENT_TIMESTAMP null,
    constraint messages_ibfk_1
        foreign key (conversation_id) references conversations (id)
            on delete cascade,
    constraint messages_ibfk_2
        foreign key (sender_id) references users (user_id)
);

create index sender_id
    on messages (sender_id);

create table participants
(
    conversation_id bigint                              not null,
    user_id         bigint                              not null,
    joined_at       timestamp default CURRENT_TIMESTAMP null,
    primary key (conversation_id, user_id),
    constraint participants_ibfk_1
        foreign key (conversation_id) references conversations (id)
            on delete cascade,
    constraint participants_ibfk_2
        foreign key (user_id) references users (user_id)
);

create index user_id
    on participants (user_id);

create table tweets
(
    tweet_id    bigint auto_increment
        primary key,
    user_id     bigint                              not null,
    content     text                                not null,
    created_at  timestamp default CURRENT_TIMESTAMP null,
    media       json      default (json_array())    null,
    parent_id   bigint                              null,
    real_parent bigint                              null,
    constraint tweets___fk
        foreign key (user_id) references users (user_id),
    constraint tweets___fk2
        foreign key (parent_id) references tweets (tweet_id)
            on delete cascade,
    constraint tweets_tweets__fk
        foreign key (real_parent) references tweets (tweet_id)
);

create table likes
(
    id        bigint auto_increment
        primary key,
    tweet_id  bigint                              not null,
    user_id   bigint                              not null,
    liked     tinyint   default 0                 null,
    like_time timestamp default CURRENT_TIMESTAMP null,
    constraint likes_ibfk_1
        foreign key (tweet_id) references tweets (tweet_id)
            on delete cascade,
    constraint likes_ibfk_2
        foreign key (user_id) references users (user_id)
);

create index user_id
    on likes (user_id);

create table shares
(
    id         bigint auto_increment
        primary key,
    tweet_id   bigint                              not null,
    user_id    bigint                              not null,
    shared     tinyint   default 0                 null,
    share_time timestamp default CURRENT_TIMESTAMP null,
    constraint shares_ibfk_1
        foreign key (tweet_id) references tweets (tweet_id)
            on delete cascade,
    constraint shares_ibfk_2
        foreign key (user_id) references users (user_id)
);

create index user_id
    on shares (user_id);

create table tweet_interactions
(
    tweet_id      bigint           not null
        primary key,
    like_count    bigint default 0 null,
    comment_count bigint default 0 null,
    share_count   bigint default 0 null,
    constraint tweet_interactions_ibfk_1
        foreign key (tweet_id) references tweets (tweet_id)
            on delete cascade
);

create table tweet_tags
(
    tweet_id bigint not null
        primary key,
    tag      json   null,
    constraint tweet_tags___fk
        foreign key (tweet_id) references tweets (tweet_id)
            on delete cascade
);

create table user_statistics
(
    user_id         bigint             not null
        primary key,
    following_count int      default 0 not null,
    followers_count int      default 0 not null,
    tweets_count    int      default 0 not null,
    msg_count       smallint default 0 null,
    constraint user_statistics_ibfk_1
        foreign key (user_id) references users (user_id)
);

create table user_tags
(
    user_id bigint not null
        primary key,
    tag     json   null,
    constraint user_tags___fk
        foreign key (user_id) references users (user_id)
);

