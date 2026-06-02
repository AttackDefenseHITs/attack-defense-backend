alter table teams add column if not exists is_system boolean not null default false;

alter table users add column if not exists is_system boolean not null default false;