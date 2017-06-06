create table t_userwithrole(
id int not null primary key,
userid int,
roleid int,
constraint fk_userid foreign key (userid) references t_user(id),
constraint fk_roleid foreign key (roleid) references t_role(id)
);

drop table t_userwithrole;