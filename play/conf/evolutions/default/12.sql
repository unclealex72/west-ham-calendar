# Calendar schema

# --- !Ups

update "game" set opponents = 'Charlton Athletic' where opponents = 'Charlton';
update "game" set opponents = 'Manchester United' where opponents = 'Man Utd';
update "game" set opponents = 'Newcastle United' where opponents = 'Newcastle';
update "game" set opponents = 'Peterborough United' where opponents = 'Peterborough';
update "game" set opponents = 'Queens Park Rangers' where opponents = 'QPR';
update "game" set opponents = 'Southend United' where opponents = 'Southend';
update "game" set opponents = 'Tottenham Hotspur' where opponents = 'Tottenham';
update "game" set opponents = 'West Bromwich Albion' where opponents = 'WBA';
update "game" set opponents = 'Wolverhampton Wanderers' where opponents = 'Wolves';

# --- !Downs

