UPDATE pg_index
SET indisready=false
WHERE indrelid = (
    SELECT oid
    FROM pg_class
    WHERE relname='main.t_news'
);

do $$
declare
    v_sentence text;
    v_current_user_id int;
	v_user_count int;
begin
	select count(u.id) into v_user_count from main.t_user u;
    for i in 1..10000
    loop
        select lorem into v_sentence FROM lorem OFFSET floor(random()*100) LIMIT 1;
		select id into v_current_user_id FROM main.t_user OFFSET floor(random()*v_user_count) LIMIT 1;
        insert into main.T_NEWS(id, user_id, message, time) values (DEFAULT, v_current_user_id, v_sentence, CURRENT_TIMESTAMP);
    end loop;
end $$;

UPDATE pg_index
SET indisready=true
WHERE indrelid = (
    SELECT oid
    FROM pg_class
    WHERE relname='main.t_news'
);

REINDEX TABLE main.t_news;