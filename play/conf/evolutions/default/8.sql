# Calendar schema

# --- !Ups

alter table "client" ADD "ordering" INT;

update "client" set "ordering" = "id";

alter table "client" ALTER "ordering" SET NOT NULL;

# --- !Downs

alter table "client" drop "ordering";