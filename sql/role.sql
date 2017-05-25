create table t_role(
id int not null,
username varchar(50), 
role varchar(50),
constraint pk_t_role primary key(id)
);

create sequence s_role;

select * from t_role;

alter table t_role
add constraint c_username_fk foreign key(username) references t_user(username);

alter table t_role
modify role default 'user';