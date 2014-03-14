--
--    Copyright (C) 2014 Infinite Automation Systems Inc. All rights reserved.
--    @author Matthew Lohbihler
--
create table watchLists (
  id int not null generated by default as identity (start with 1, increment by 1),
  xid varchar(50) not null,
  userId int not null,
  name varchar(50)
);
alter table watchLists add constraint watchListsPk primary key (id);
alter table watchLists add constraint watchListsUn1 unique (xid);
alter table watchLists add constraint watchListsFk1 foreign key (userId) references users(id) on delete cascade;

create table watchListPoints (
  watchListId int not null,
  dataPointId int not null,
  sortOrder int not null
);
alter table watchListPoints add constraint watchListPointsFk1 foreign key (watchListId) references watchLists(id) on delete cascade;
alter table watchListPoints add constraint watchListPointsFk2 foreign key (dataPointId) references dataPoints(id) on delete cascade;

create table watchListUsers (
  watchListId int not null,
  userId int not null,
  accessType int not null
);
alter table watchListUsers add constraint watchListUsersPk primary key (watchListId, userId);
alter table watchListUsers add constraint watchListUsersFk1 foreign key (watchListId) references watchLists(id) on delete cascade;
alter table watchListUsers add constraint watchListUsersFk2 foreign key (userId) references users(id) on delete cascade;

create table selectedWatchList (
  userId int not null,
  watchListId int not null
);
alter table selectedWatchList add constraint selectedWatchListPk primary key (userId);
alter table selectedWatchList add constraint selectedWatchListFk1 foreign key (userId) references users(id) on delete cascade;
alter table selectedWatchList add constraint selectedWatchListFk2 foreign key (watchListId) references watchLists(id) on delete cascade;
