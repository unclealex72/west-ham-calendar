# Calendar schema

# --- !Ups

DROP SEQUENCE "s_game_id";
CREATE SEQUENCE s_game_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 552
  CACHE 1;

# --- !Downs
