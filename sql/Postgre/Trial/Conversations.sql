create table main.t_conversations(
  id serial, 
  user1 int, 
  user2 int,
  last_message int,
  primary key(id),
  constraint fk_user1 foreign key (user1) references main.t_user(id),
  constraint fk_user2 foreign key (user2) references main.t_user(id)
  );
  