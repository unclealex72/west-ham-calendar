# Calendar schema

# --- !Ups

alter table "game" add "hometeamimageurl" text;
alter table "game" add "awayteamimageurl" text;
alter table "game" add "competitionimageurl" text;
alter table "game" drop "academymemberspostal";
alter table "game" drop "generalsalepostal";

# --- !Downs

alter table "game" drop "hometeamimageurl";
alter table "game" drop "awayteamimageurl";
alter table "game" drop "competitionimageurl";
alter table "game" add "academymemberspostal" TIMESTAMP;
alter table "game" add "generalsalepostal" TIMESTAMP;
