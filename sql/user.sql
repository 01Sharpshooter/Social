create table t_user(
id int not null,
username varchar(50), 
passwd varchar(100),
constraint pk_t_user primary key(id)
);

create sequence s_user;

alter table t_user
add CONSTRAINT u_username UNIQUE(username);

alter table t_user
add enabled int default(1);

select * from t_user;

alter table t_user
add image varchar(50);