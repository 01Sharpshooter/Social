create table main.t_messages(
  id serial, 
  sender_id int, 
  conversation_id int, 
  message text,
  seen boolean default false,
  time timestamp,
  primary key(id),
  constraint fk_sender foreign key (sender_id) references main.t_user(id),
  constraint fk_conversation foreign key (conversation_id) references main.t_conversations(id)
  );
  