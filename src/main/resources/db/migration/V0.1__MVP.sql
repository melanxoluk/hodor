-- users block

create table users(
  id bigserial primary key,
  uuid UUID,
  properties text,
  app_id bigint
);

create table username_passwords(
  id bigserial primary key,
  username text,
  password text,
  user_id bigint references users(id)
);


-- apps block

create table apps(
  id bigserial primary key,
  uuid UUID,
  name varchar(50),
  creator_id bigint
);

alter table users add foreign key (app_id) references apps(id);
alter table apps add foreign key (creator_id) references users(id);

create table app_clients(
  id bigserial primary key,
  uuid UUID,
  type text,
  app_id bigint references apps(id)
);

create table app_roles (
  id bigserial primary key,
  uuid UUID,
  name text,
  app_id bigint references apps
);

create table users_roles(
  id bigserial primary key,
  user_id bigint references users(id),
  role_id bigint references app_roles(id)
);
