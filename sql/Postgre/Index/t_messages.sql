-- Index: t_messages_receiverid_idx

-- DROP INDEX main.t_messages_receiverid_idx;

CREATE INDEX t_messages_receiverid_idx
    ON main.t_messages USING btree
    (receiverid)
    TABLESPACE pg_default;
	
-- Index: t_messages_receiverid_seen_idx

-- DROP INDEX main.t_messages_receiverid_seen_idx;

CREATE INDEX t_messages_receiverid_seen_idx
    ON main.t_messages USING btree
    (receiverid, seen)
    TABLESPACE pg_default;

-- Index: t_messages_senderid_idx

-- DROP INDEX main.t_messages_senderid_idx;

CREATE INDEX t_messages_senderid_idx
    ON main.t_messages USING btree
    (senderid)
    TABLESPACE pg_default;

-- Index: t_messages_senderid_receiverid_idx

-- DROP INDEX main.t_messages_senderid_receiverid_idx;

CREATE INDEX t_messages_senderid_receiverid_idx
    ON main.t_messages USING btree
    (senderid, receiverid)
    TABLESPACE pg_default;

