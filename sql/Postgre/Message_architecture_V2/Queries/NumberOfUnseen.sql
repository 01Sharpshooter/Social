select count(distinct c.id) from main.t_conversations c
join main.t_messages m on c.last_message = m.id
where c.seen = false
AND (c.user1 = 1 OR c.user2 = 1)
AND m.sender_id <> 1;