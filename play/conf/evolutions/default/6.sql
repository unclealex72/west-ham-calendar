# Calendar schema

# --- !Ups

create table prioritypointsconfiguration (
  id bigint not null primary key,
  emailAddress varchar(128) not null,
  daytimeTelephoneNumber varchar(128) not null,
  mobilePhoneNumber varchar(128) not null,
  address varchar(128) not null,
  creditCardNumber varchar(128) not null,
  creditCardSecurityCode int not null,
  creditCardExpiryMonth int not null,
  creditCardExpiryYear int not null,
  nameOnCreditCard varchar(128) not null
);
create table client (
  id bigint not null primary key,
  name varchar(128) not null,
  referenceNumber int not null,
  priorityPointsConfigurationId bigint not null,
  clientType varchar(128)
);
create index idxPpcIdIdx on client (priorityPointsConfigurationId);
alter table client add constraint clientFK1 foreign key (priorityPointsConfigurationId) references prioritypointsconfiguration(id);

# --- !Downs

drop table client;
drop table prioritypointsconfiguration;