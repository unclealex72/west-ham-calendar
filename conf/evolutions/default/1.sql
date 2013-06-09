# Calendar schema

# --- !Ups

-- table declarations :
create table "Game" (
    "opponents" varchar(128) not null,
    "attended" boolean,
    "result" varchar(128),
    "dateTimePriorityPointPostAvailable" timestamp,
    "dateTimeSeasonTicketsAvailable" timestamp,
    "_competition" varchar(128) not null,
    "season" integer not null,
    "attendence" integer,
    "dateTimeAcademyMembersAvailable" timestamp,
    "id" bigint primary key not null,
    "_location" varchar(128) not null,
    "dateTimePlayed" timestamp,
    "televisionChannel" varchar(128),
    "matchReport" varchar(128),
    "dateTimeBondholdersAvailable" timestamp,
    "dateTimeGeneralSaleAvailable" timestamp
  );
create sequence "s_Game_id";
-- column group indexes :
create unique index "idxgameKeyComposite" on "Game" ("_competition","_location","opponents","season");

# --- !Downs

DROP INDEX "idxgameKeyComposite";
DROP TABLE "Game";
DROP SEQUENCE "s_Game_id";
