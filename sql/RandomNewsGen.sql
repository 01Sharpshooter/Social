declare
    v_words_count number;
    v_sentence varchar(250);
    v_word varchar(30);
    v_current_user_id number;
begin
    for i in 1..10000
    loop
        select TRUNC(DBMS_RANDOM.VALUE(3, 10)) into v_words_count from dual;
        select dbms_random.string('U', 1) || dbms_random.string('L', dbms_random.value(1,20)) into v_sentence from dual;
        for j in 1..(v_words_count-1)
        loop
            select dbms_random.string('L', dbms_random.value(1,20)) into v_word from dual;
            v_sentence := v_sentence || ' ' || v_word;
        end loop;
        select id into v_current_user_id from (select u.id as id from t_user u order by dbms_random.value) where rownum = 1;
        insert into T_NEWS(id, user_id, message, time) values (s_news.nextval, v_current_user_id, v_sentence, CURRENT_TIMESTAMP);
    end loop;
    commit;
end;