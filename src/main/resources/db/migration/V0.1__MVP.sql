-- users block

create table users(
  id bigserial primary key,
  uuid UUID,
  app bigint
);

create table username_passwords(
  id bigserial primary key,
  username text,
  password text,
  "user" bigint references users(id)
);


-- apps block

create table apps(
  id bigserial primary key,
  uuid UUID,
  name varchar(50),
  creator bigint
);

alter table users add foreign key (app) references apps(id);
alter table apps add foreign key (creator) references users(id);

create table app_clients(
  id bigserial primary key,
  uuid UUID,
  app bigint references apps(id)
);

create table app_roles (
  id bigserial primary key,
  uuid UUID,
  name text,
  app bigint references apps
);
