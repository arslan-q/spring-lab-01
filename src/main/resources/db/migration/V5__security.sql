create table app_user (
    id            bigserial primary key,
    username      varchar(60)  not null unique,
    password_hash varchar(100) not null,
    enabled       boolean      not null default true
);

create table user_role (
    user_id bigint      not null references app_user (id) on delete cascade,
    role    varchar(20) not null,
    primary key (user_id, role),
    constraint user_role_allowed check (role in ('USER', 'ADMIN'))
);

-- Local teaching accounts only. Both values are BCrypt hashes (work factor 10).
insert into app_user (username, password_hash) values
    ('user',  '$2a$10$JBGOs4KsMtEvxcV8Og1saueeOlXIaLywI6LGG6SkpbnA73NtThZC6'),
    ('admin', '$2a$10$wHm2hJuc7Ah..9YS9lSUsehUyknOMS43F79vsgYQmhaMOWBnAQJjG');

insert into user_role (user_id, role)
    select id, 'USER' from app_user;
insert into user_role (user_id, role)
    select id, 'ADMIN' from app_user where username = 'admin';
