do $$
declare
    v_sentence text;
    v_sender_id int;
	v_receiver_id int;
	v_user_count int;
begin
	select count(u.id) into v_user_count from main.t_user u;
    for i in 1..500000
    loop
        select lorem into v_sentence FROM lorem OFFSET floor(random()*100) LIMIT 1;
		select id into v_sender_id FROM main.t_user OFFSET floor(random()*v_user_count) LIMIT 1;
		select id into v_receiver_id FROM main.t_user OFFSET floor(random()*v_user_count) LIMIT 1;
        insert into main.t_messages(senderid, receiverid, message, time, seen) 
		values (v_sender_id, v_receiver_id, v_sentence, CURRENT_TIMESTAMP, true);
    end loop;
end $$;