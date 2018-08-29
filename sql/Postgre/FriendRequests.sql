create table main.t_friendrequests(
id serial primary key,
requestorid int,
requestedid int,
constraint fk_requestor foreign key (requestorid) references main.t_user(id),
constraint fk_requested foreign key (requestedid) references main.t_user(id)
);
