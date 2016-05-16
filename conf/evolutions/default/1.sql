# Calendar schema

# --- !Ups

-- table declarations :
create table "game" (
    "opponents" varchar(128) not null,
    "academymembers" timestamp,
    "bondholders" timestamp,
    "attended" boolean,
    "result" varchar(128),
    "competition" varchar(128) not null,
    "season" integer not null,
    "attendence" integer,
    "prioritypoint" timestamp,
    "at" timestamp,
    "id" bigint primary key not null,
    "location" varchar(128) not null,
    "tvchannel" varchar(128),
    "report" varchar(128),
    "seasontickets" timestamp,
    "generalsale" timestamp
  );
create sequence "s_game_id";
-- column group indexes :
create unique index "idxgameKeyComposite" on "game" ("competition","location","opponents","season");

# --- !Downs

DROP INDEX "idxgameKeyComposite";
DROP TABLE "game";
DROP SEQUENCE "s_game_id";
