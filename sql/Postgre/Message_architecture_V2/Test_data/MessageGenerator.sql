do $$
declare
	v_sentence text;
	v_sender_id int;
	v_receiver_id int;
	v_user_count int;
	v_conv_count int;
	v_conv_id int;
	v_last_message int;
begin
	select count(u.id) into v_user_count from main.t_user u;
	for i in 1..2000000
	loop
		select lorem into v_sentence FROM lorem OFFSET floor(random()*100) LIMIT 1;
		select id into v_sender_id FROM main.t_user OFFSET floor(random()*v_user_count) LIMIT 1;
		select id into v_receiver_id FROM main.t_user OFFSET floor(random()*v_user_count) LIMIT 1;

		select count(*) into v_conv_count from main.t_conversations c 
		where (c.user1 = v_sender_id and c.user2=v_receiver_id) or
		(c.user1 = v_receiver_id and c.user2=v_sender_id);
		IF v_conv_count=0 THEN
			INSERT INTO main.t_conversations (user1, user2) VALUES (v_sender_id, v_receiver_id);
		END IF;
		select c.id into v_conv_id from main.t_conversations c 
		where (c.user1 = v_sender_id and c.user2=v_receiver_id) or
		(c.user1 = v_receiver_id and c.user2=v_sender_id);

		insert into main.t_messages(sender_id, conversation_id, message, time, seen) 
		values (v_sender_id, v_conv_id, v_sentence, CURRENT_TIMESTAMP, true) returning id into v_last_message;
	end loop;
	for v_last_message, v_conv_id in (
		select max(m.id), m.conversation_id as mid from main.t_messages m
		group by m.conversation_id)
	loop
		update main.t_conversations set last_message = v_last_message
		where id = v_conv_id;
	end loop;
end $$;