create table t_friendrequests(
id int not null primary key,
requestorid int,
requestedid int,
constraint fk_requestor foreign key (requestorid) references t_user(id),
constraint fk_requested foreign key (requestedid) references t_user(id)
);

create sequence s_friendrequests;

--drop table t_friendrequests;