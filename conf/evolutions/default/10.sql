# Calendar schema

# --- !Ups

alter table game alter column id set DEFAULT nextval('s_game_id'::regclass)

# --- !Downs

alter table game alter column id drop DEFAULT
