--create index idx_user1 on main.t_conversations(user1);
--create index idx_user2 on main.t_conversations(user2);
--create index idx_user12 on main.t_conversations(user1, user2);
--create index idx_sender on main.t_messages(sender_id);
--create index idx_conversation on main.t_messages(conversation_id);
create index idx_conv_id_id on main.t_messages(conversation_id, id);