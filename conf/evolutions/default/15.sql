# Calendar schema

# --- !Ups

update "game" set "datecreated" = at, "lastupdated" = at;

update "game" set attended = false where attended is null;

alter table "game" alter column "attended" set not null;

# --- !Downs

alter table "game" alter column "attended" drop not null;
