# Calendar schema

# --- !Ups

CREATE SEQUENCE s_fatal_error_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE fatal_error
(
  id bigint NOT NULL DEFAULT nextval('s_fatal_error_id'::regclass),
  at timestamp without time zone,
  message text,
  CONSTRAINT fatal_error_pkey PRIMARY KEY (id)
)

# --- !Downs

drop table fatal_error;

drop sequence s_fatal_error_id;