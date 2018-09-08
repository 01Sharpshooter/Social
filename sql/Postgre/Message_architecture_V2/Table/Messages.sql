-- Table: main.t_messages

-- DROP TABLE main.t_messages;

CREATE TABLE main.t_messages
(
    id integer NOT NULL DEFAULT nextval('main.t_messages_id_seq'::regclass),
    sender_id integer,
    conversation_id integer,
    message text COLLATE pg_catalog."default",
    "time" timestamp without time zone,
    CONSTRAINT t_messages_pkey PRIMARY KEY (id),
    CONSTRAINT fk_conversation FOREIGN KEY (conversation_id)
        REFERENCES main.t_conversations (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_sender FOREIGN KEY (sender_id)
        REFERENCES main.t_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE main.t_messages
    OWNER to social;

-- Index: t_messages_conversation_id_idx

-- DROP INDEX main.t_messages_conversation_id_idx;

CREATE INDEX t_messages_conversation_id_idx
    ON main.t_messages USING btree
    (conversation_id)
    TABLESPACE pg_default;

-- Index: t_messages_sender_id_idx

-- DROP INDEX main.t_messages_sender_id_idx;

CREATE INDEX t_messages_sender_id_idx
    ON main.t_messages USING btree
    (sender_id)
    TABLESPACE pg_default;