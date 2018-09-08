create table main.t_messages(
  id serial, 
  senderId int, 
  receiverId int, 
  message text,
  seen boolean default false,
  time timestamp,
  primary key(id),
  constraint fk_sender foreign key (senderId) references main.t_user(id),
  constraint fk_receiver foreign key (receiverId) references main.t_user(id)
  );
  