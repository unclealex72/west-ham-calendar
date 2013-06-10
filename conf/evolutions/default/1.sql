# Calendar schema

# --- !Ups

-- table declarations :
create table "game" (
    "opponents" varchar(128) not null,
    "attended" boolean,
    "result" varchar(128),
    "dateTimePriorityPointPostAvailable" timestamp,
    "dateTimeSeasonTicketsAvailable" timestamp,
    "competition" varchar(128) not null,
    "season" integer not null,
    "attendence" integer,
    "dateTimeAcademyMembersAvailable" timestamp,
    "id" bigint primary key not null,
    "location" varchar(128) not null,
    "dateTimePlayed" timestamp,
    "televisionChannel" varchar(128),
    "matchReport" varchar(128),
    "dateTimeBondholdersAvailable" timestamp,
    "dateTimeGeneralSaleAvailable" timestamp
  );
create sequence "s_game_id";
-- column group indexes :
create unique index "idxGameKeyComposite" on "game" ("competition","location","opponents","season");

# --- !Downs

DROP INDEX "idxGameKeyComposite";
DROP TABLE "game";
DROP SEQUENCE "s_game_id";
