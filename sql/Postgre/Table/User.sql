-- Table: main.t_user

-- DROP TABLE main.t_user;

CREATE TABLE main.t_user
(
    id integer NOT NULL DEFAULT nextval('main.t_user_id_seq'::regclass),
    username text COLLATE pg_catalog."default" NOT NULL,
    image text COLLATE pg_catalog."default",
    full_name text COLLATE pg_catalog."default",
    enabled boolean DEFAULT true,
    CONSTRAINT pk_t_user PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE main.t_user
    OWNER to social;

-- Index: t_user_username_idx

-- DROP INDEX main.t_user_username_idx;

CREATE UNIQUE INDEX t_user_username_idx
    ON main.t_user USING btree
    (username COLLATE pg_catalog."default")
    TABLESPACE pg_default;