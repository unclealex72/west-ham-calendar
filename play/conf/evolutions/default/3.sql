# Calendar schema

# --- !Ups

alter table "game" add "datecreated" timestamp;
alter table "game" add "lastupdated" timestamp;

update "game" set "datecreated" = at, "lastupdated" = at;

alter table "game" alter column "datecreated" set not null;
alter table "game" alter column "lastupdated" set not null;

# --- !Downs

alter table "game" drop "datecreated";
alter table "game" drop "lastupdated";