create table t_friendships(
  id int not null primary key,
  userId int,
  friendId int,
  constraint fk_fuserid foreign key (userId) references t_user(id),
  constraint fk_friendid foreign key (friendId) references t_user(id)
);

create sequence s_friendships;

--drop table t_friends;

--drop sequence s_friends;