# Calendar schema

# --- !Ups

ALTER TABLE "game" ALTER COLUMN "report" TYPE text;

# --- !Downs

ALTER TABLE "game" ALTER COLUMN "report" TYPE varchar(128);
