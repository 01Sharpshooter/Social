create table main.t_news(
	id serial, 
	user_id int, 
	message text, 
	time timestamp, 
	primary key(id));

alter table main.t_news
add constraint fk_news_user foreign key (user_id) references main.t_user(id);
