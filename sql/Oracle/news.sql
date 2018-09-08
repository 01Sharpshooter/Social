create table t_news(id int not null, user_id int, message varchar(250), time timestamp, primary key(id));

alter table t_news
add constraint fk_news_user foreign key (user_id) references t_user(id);

create sequence s_news;

drop table t_news;
drop sequence s_news;
