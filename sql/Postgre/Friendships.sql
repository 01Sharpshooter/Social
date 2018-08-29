create table main.t_friendships(
  id serial primary key,
  userId int,
  friendId int,
  constraint fk_fuserid foreign key (userId) references main.t_user(id),
  constraint fk_friendid foreign key (friendId) references main.t_user(id)
);
