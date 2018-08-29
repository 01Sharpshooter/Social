create index on main.t_messages(senderid);
create index on main.t_messages(receiverId);
create index on main.t_news(user_id);
create unique index on main.t_user(username);