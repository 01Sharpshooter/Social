select m.* from main.t_messages m
join main.t_conversations c on m.conversation_id = c.id
where m.id = c.last_message
and (c.user1 = 1 or c.user2=1);