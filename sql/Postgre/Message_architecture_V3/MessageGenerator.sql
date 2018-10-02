UPDATE pg_index
SET indisready=false
WHERE indrelid = (
    SELECT oid
    FROM pg_class
    WHERE relname='main.t_messages'
);

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
	for i in 1..10000
	loop
		-- Drawing message and related users
		select lorem into v_sentence FROM lorem OFFSET floor(random()*100) LIMIT 1;
		select id into v_sender_id FROM main.t_user OFFSET floor(random()*v_user_count) LIMIT 1;
		select id into v_receiver_id FROM main.t_user OFFSET floor(random()*v_user_count) LIMIT 1;

		select count(*) into v_conv_count 
		from main.t_conversation_user cu1
		join main.t_conversation_user cu2 
		on cu1.conversation_id = cu2.conversation_id
		where cu1.user_id = v_sender_id and cu2.user_id = v_receiver_id;
		IF v_conv_count=0 THEN
			INSERT INTO main.t_conversations (last_message) VALUES (null) RETURNING id into v_conv_id;
			INSERT INTO main.t_conversation_user (conversation_id, user_id, seen) VALUES (v_conv_id, v_sender_id, true);
			IF v_sender_id <> v_receiver_id THEN
				INSERT INTO main.t_conversation_user (conversation_id, user_id, seen) VALUES (v_conv_id, v_receiver_id, true);
			END IF;
		END IF;

		insert into main.t_messages(sender_id, conversation_id, message, time) 
		values (v_sender_id, v_conv_id, v_sentence, CURRENT_TIMESTAMP) returning id into v_last_message;
	end loop;
	for v_last_message, v_conv_id in (
		select max(m.id), m.conversation_id as mid from main.t_messages m
		group by m.conversation_id)
	loop
		update main.t_conversations set last_message = v_last_message
		where id = v_conv_id;
	end loop;
end $$;

UPDATE pg_index
SET indisready=true
WHERE indrelid = (
    SELECT oid
    FROM pg_class
    WHERE relname='main.t_messages'
);

REINDEX TABLE main.t_messages;
