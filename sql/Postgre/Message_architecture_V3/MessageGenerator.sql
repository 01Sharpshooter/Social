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
	v_disabled_count int;
	v_user_id int;
	v_enabled boolean;
	v_conv_user_count int;
	v_conv_users integer[];
	v_user_count int;
	v_conv_id int;
	v_last_message int;
begin
	select count(u.id) into v_user_count from main.t_user u;
	select count(*) into v_disabled_count from main.t_user u where u.enabled = false;
	for i in 1..500
	loop
		-- Drawing message and related users
																												
		INSERT INTO main.t_conversations (last_message) VALUES (null) RETURNING id into v_conv_id;	
		SELECT floor(random() * 5 + 2)::int into v_conv_user_count;
		v_conv_users := null;
		for j in 1..v_conv_user_count
		loop
			select id, enabled into v_user_id, v_enabled FROM main.t_user u OFFSET floor(random()*(v_user_count-v_disabled_count-v_conv_user_count)) LIMIT 1;
			WHILE  v_enabled = false OR (v_user_id = ANY(v_conv_users) AND v_conv_users is NOT null) LOOP
				select id, enabled into v_user_id, v_enabled FROM main.t_user u OFFSET floor(random()*(v_user_count-v_disabled_count-v_conv_user_count)) LIMIT 1;
			END LOOP;																		 
			v_conv_users:= array_append(v_conv_users, v_user_id);
			insert into main.t_conversation_user(conversation_id, user_id, seen) values(v_conv_id, v_user_id, true);																															 
		end loop;
		
		for l in 1..3000
		loop
			SELECT floor(random() * v_conv_user_count + 1)::int into v_user_id;		
			select lorem into v_sentence FROM lorem OFFSET floor(random()*100) LIMIT 1;
			insert into main.t_messages(sender_id, conversation_id, message, time) 
			values (v_conv_users[v_user_id], v_conv_id, v_sentence, CURRENT_TIMESTAMP);																														 
		end loop;																																 
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
