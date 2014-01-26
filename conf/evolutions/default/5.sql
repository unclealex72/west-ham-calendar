# Calendar schema

# --- !Ups

alter table "game" add "academymemberspostal" timestamp;
alter table "game" add "generalsalepostal" timestamp;

# --- !Downs

alter table "game" drop "academymemberspostal";
alter table "game" drop "generalsalepostal";