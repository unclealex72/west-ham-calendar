# Calendar schema

# --- !Ups

alter table "game" alter column id set default nextval('s_game_id');

# --- !Downs
