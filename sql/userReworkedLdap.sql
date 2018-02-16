create table t_user(
id int not null,
username varchar(50), 
image varchar(50),
constraint pk_t_user primary key(id)
);

drop table t_userimages;
drop table t_news;
drop sequence s_news;
drop table t_user;
