-- Table: main.t_conversations

-- DROP TABLE main.t_conversations;

CREATE TABLE main.t_conversations
(
    id integer NOT NULL DEFAULT nextval('main.t_conversations_id_seq'::regclass),
    user1 integer,
    user2 integer,
    last_message integer,
    seen boolean DEFAULT false,
    CONSTRAINT t_conversations_pkey PRIMARY KEY (id),
    CONSTRAINT fk_last_message FOREIGN KEY (last_message)
        REFERENCES main.t_messages (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_user1 FOREIGN KEY (user1)
        REFERENCES main.t_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_user2 FOREIGN KEY (user2)
        REFERENCES main.t_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE main.t_conversations
    OWNER to social;

-- Index: t_conversations_user1_idx

-- DROP INDEX main.t_conversations_user1_idx;

CREATE INDEX t_conversations_user1_idx
    ON main.t_conversations USING btree
    (user1)
    TABLESPACE pg_default;

-- Index: t_conversations_user1_user2_idx

-- DROP INDEX main.t_conversations_user1_user2_idx;

CREATE INDEX t_conversations_user1_user2_idx
    ON main.t_conversations USING btree
    (user1, user2)
    TABLESPACE pg_default;

-- Index: t_conversations_user2_idx

-- DROP INDEX main.t_conversations_user2_idx;

CREATE INDEX t_conversations_user2_idx
    ON main.t_conversations USING btree
    (user2)
    TABLESPACE pg_default;