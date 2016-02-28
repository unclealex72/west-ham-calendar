# Calendar schema

# --- !Ups

update game set result = regexp_replace(result, '(\d)-(\d)', '\2-\1') where location = 'AWAY' and result similar to '\d-\d' and season <= 2012;

# --- !Downs

