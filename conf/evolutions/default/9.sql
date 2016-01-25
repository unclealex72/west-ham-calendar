# Calendar schema

# --- !Ups

alter table "prioritypointsconfiguration" ADD "signature" TEXT;

# --- !Downs

alter table "prioritypointsconfiguration" drop "signature";