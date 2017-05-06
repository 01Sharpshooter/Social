create table t_role(
id int not null,
username varchar(50), 
role varchar(50),
constraint pk_t_role primary key(id)
);

create sequence s_role;

alter table t_role
add userID int not null;

alter table t_role
add constraint c_userID_fk foreign key(userID) references t_user(id);

alter table t_role
modify role default 'user';