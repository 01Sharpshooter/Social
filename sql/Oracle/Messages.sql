create table t_messages(
  id int not null, 
  senderId int, 
  receiverId int, 
  message varchar(250),
  time timestamp,
  primary key(id),
  constraint fk_sender foreign key (senderId) references t_user(id),
  constraint fk_receiver foreign key (receiverId) references t_user(id)
  );
  
  create sequence s_messages;
  
  alter table t_messages add seen number(1) default 0;
  
  select * from t_messages;
  
  drop table t_messages;
  