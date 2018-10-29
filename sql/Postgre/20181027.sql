--
-- PostgreSQL database dump
--

-- Dumped from database version 10.5
-- Dumped by pg_dump version 10.5

-- Started on 2018-10-27 21:54:04

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE "SocialDB";
--
-- TOC entry 2261 (class 1262 OID 16385)
-- Name: SocialDB; Type: DATABASE; Schema: -; Owner: social
--

CREATE DATABASE "SocialDB" WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'Hungarian_Hungary.1250' LC_CTYPE = 'Hungarian_Hungary.1250';


ALTER DATABASE "SocialDB" OWNER TO social;

\connect "SocialDB"

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2262 (class 0 OID 0)
-- Dependencies: 2261
-- Name: DATABASE "SocialDB"; Type: COMMENT; Schema: -; Owner: social
--

COMMENT ON DATABASE "SocialDB" IS 'Database of Social application.';


--
-- TOC entry 3 (class 2615 OID 16661)
-- Name: main; Type: SCHEMA; Schema: -; Owner: social
--

CREATE SCHEMA main;


ALTER SCHEMA main OWNER TO social;

--
-- TOC entry 2263 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA main; Type: COMMENT; Schema: -; Owner: social
--

COMMENT ON SCHEMA main IS 'Main schema of Social';


--
-- TOC entry 1 (class 3079 OID 12278)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2266 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- TOC entry 219 (class 1255 OID 16900)
-- Name: random_between(integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.random_between(low integer, high integer) RETURNS integer
    LANGUAGE plpgsql STRICT
    AS $$
BEGIN
   RETURN floor(random()* (high-low + 1) + low);
END;
$$;


ALTER FUNCTION public.random_between(low integer, high integer) OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 217 (class 1259 OID 25413)
-- Name: t_conversation_user; Type: TABLE; Schema: main; Owner: social
--

CREATE TABLE main.t_conversation_user (
    id integer NOT NULL,
    conversation_id integer NOT NULL,
    user_id integer NOT NULL,
    seen boolean DEFAULT false NOT NULL
);


ALTER TABLE main.t_conversation_user OWNER TO social;

--
-- TOC entry 216 (class 1259 OID 25411)
-- Name: t_conversation_user_id_seq; Type: SEQUENCE; Schema: main; Owner: social
--

CREATE SEQUENCE main.t_conversation_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE main.t_conversation_user_id_seq OWNER TO social;

--
-- TOC entry 2267 (class 0 OID 0)
-- Dependencies: 216
-- Name: t_conversation_user_id_seq; Type: SEQUENCE OWNED BY; Schema: main; Owner: social
--

ALTER SEQUENCE main.t_conversation_user_id_seq OWNED BY main.t_conversation_user.id;


--
-- TOC entry 213 (class 1259 OID 25239)
-- Name: t_conversations; Type: TABLE; Schema: main; Owner: social
--

CREATE TABLE main.t_conversations (
    id integer NOT NULL,
    last_message integer
);


ALTER TABLE main.t_conversations OWNER TO social;

--
-- TOC entry 212 (class 1259 OID 25237)
-- Name: t_conversations_id_seq; Type: SEQUENCE; Schema: main; Owner: social
--

CREATE SEQUENCE main.t_conversations_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE main.t_conversations_id_seq OWNER TO social;

--
-- TOC entry 2268 (class 0 OID 0)
-- Dependencies: 212
-- Name: t_conversations_id_seq; Type: SEQUENCE OWNED BY; Schema: main; Owner: social
--

ALTER SEQUENCE main.t_conversations_id_seq OWNED BY main.t_conversations.id;


--
-- TOC entry 209 (class 1259 OID 16884)
-- Name: t_friendrequests; Type: TABLE; Schema: main; Owner: social
--

CREATE TABLE main.t_friendrequests (
    id integer NOT NULL,
    requestorid integer,
    requestedid integer
);


ALTER TABLE main.t_friendrequests OWNER TO social;

--
-- TOC entry 208 (class 1259 OID 16882)
-- Name: t_friendrequests_id_seq; Type: SEQUENCE; Schema: main; Owner: social
--

CREATE SEQUENCE main.t_friendrequests_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE main.t_friendrequests_id_seq OWNER TO social;

--
-- TOC entry 2269 (class 0 OID 0)
-- Dependencies: 208
-- Name: t_friendrequests_id_seq; Type: SEQUENCE OWNED BY; Schema: main; Owner: social
--

ALTER SEQUENCE main.t_friendrequests_id_seq OWNED BY main.t_friendrequests.id;


--
-- TOC entry 207 (class 1259 OID 16866)
-- Name: t_friendships; Type: TABLE; Schema: main; Owner: social
--

CREATE TABLE main.t_friendships (
    id integer NOT NULL,
    userid integer,
    friendid integer
);


ALTER TABLE main.t_friendships OWNER TO social;

--
-- TOC entry 206 (class 1259 OID 16864)
-- Name: t_friendships_id_seq; Type: SEQUENCE; Schema: main; Owner: social
--

CREATE SEQUENCE main.t_friendships_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE main.t_friendships_id_seq OWNER TO social;

--
-- TOC entry 2270 (class 0 OID 0)
-- Dependencies: 206
-- Name: t_friendships_id_seq; Type: SEQUENCE OWNED BY; Schema: main; Owner: social
--

ALTER SEQUENCE main.t_friendships_id_seq OWNED BY main.t_friendships.id;


--
-- TOC entry 215 (class 1259 OID 25257)
-- Name: t_messages; Type: TABLE; Schema: main; Owner: social
--

CREATE TABLE main.t_messages (
    id integer NOT NULL,
    sender_id integer,
    conversation_id integer,
    message text,
    "time" timestamp without time zone
);


ALTER TABLE main.t_messages OWNER TO social;

--
-- TOC entry 214 (class 1259 OID 25255)
-- Name: t_messages_id_seq; Type: SEQUENCE; Schema: main; Owner: social
--

CREATE SEQUENCE main.t_messages_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE main.t_messages_id_seq OWNER TO social;

--
-- TOC entry 2271 (class 0 OID 0)
-- Dependencies: 214
-- Name: t_messages_id_seq; Type: SEQUENCE OWNED BY; Schema: main; Owner: social
--

ALTER SEQUENCE main.t_messages_id_seq OWNED BY main.t_messages.id;


--
-- TOC entry 205 (class 1259 OID 16849)
-- Name: t_news; Type: TABLE; Schema: main; Owner: social
--

CREATE TABLE main.t_news (
    id integer NOT NULL,
    user_id integer,
    message text,
    "time" timestamp without time zone
);


ALTER TABLE main.t_news OWNER TO social;

--
-- TOC entry 204 (class 1259 OID 16847)
-- Name: t_news_id_seq; Type: SEQUENCE; Schema: main; Owner: social
--

CREATE SEQUENCE main.t_news_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE main.t_news_id_seq OWNER TO social;

--
-- TOC entry 2272 (class 0 OID 0)
-- Dependencies: 204
-- Name: t_news_id_seq; Type: SEQUENCE OWNED BY; Schema: main; Owner: social
--

ALTER SEQUENCE main.t_news_id_seq OWNED BY main.t_news.id;


--
-- TOC entry 203 (class 1259 OID 16780)
-- Name: t_user; Type: TABLE; Schema: main; Owner: social
--

CREATE TABLE main.t_user (
    id integer NOT NULL,
    username text NOT NULL,
    image text,
    full_name text,
    enabled boolean DEFAULT true
);


ALTER TABLE main.t_user OWNER TO social;

--
-- TOC entry 202 (class 1259 OID 16778)
-- Name: t_user_id_seq; Type: SEQUENCE; Schema: main; Owner: social
--

CREATE SEQUENCE main.t_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE main.t_user_id_seq OWNER TO social;

--
-- TOC entry 2273 (class 0 OID 0)
-- Dependencies: 202
-- Name: t_user_id_seq; Type: SEQUENCE OWNED BY; Schema: main; Owner: social
--

ALTER SEQUENCE main.t_user_id_seq OWNED BY main.t_user.id;


--
-- TOC entry 211 (class 1259 OID 16937)
-- Name: lorem; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.lorem (
    id integer NOT NULL,
    lorem text
);


ALTER TABLE public.lorem OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 16935)
-- Name: lorem_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.lorem_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.lorem_id_seq OWNER TO postgres;

--
-- TOC entry 2274 (class 0 OID 0)
-- Dependencies: 210
-- Name: lorem_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.lorem_id_seq OWNED BY public.lorem.id;


--
-- TOC entry 200 (class 1259 OID 16628)
-- Name: s_friendrequests; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.s_friendrequests
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.s_friendrequests OWNER TO postgres;

--
-- TOC entry 199 (class 1259 OID 16611)
-- Name: s_friendships; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.s_friendships
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.s_friendships OWNER TO postgres;

--
-- TOC entry 201 (class 1259 OID 16645)
-- Name: s_messages; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.s_messages
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.s_messages OWNER TO postgres;

--
-- TOC entry 198 (class 1259 OID 16609)
-- Name: s_news; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.s_news
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.s_news OWNER TO postgres;

--
-- TOC entry 197 (class 1259 OID 16589)
-- Name: s_user; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.s_user
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.s_user OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 25449)
-- Name: v_conv_count; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.v_conv_count (
    count bigint
);


ALTER TABLE public.v_conv_count OWNER TO postgres;

--
-- TOC entry 2094 (class 2604 OID 25416)
-- Name: t_conversation_user id; Type: DEFAULT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_conversation_user ALTER COLUMN id SET DEFAULT nextval('main.t_conversation_user_id_seq'::regclass);


--
-- TOC entry 2092 (class 2604 OID 25242)
-- Name: t_conversations id; Type: DEFAULT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_conversations ALTER COLUMN id SET DEFAULT nextval('main.t_conversations_id_seq'::regclass);


--
-- TOC entry 2090 (class 2604 OID 16887)
-- Name: t_friendrequests id; Type: DEFAULT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_friendrequests ALTER COLUMN id SET DEFAULT nextval('main.t_friendrequests_id_seq'::regclass);


--
-- TOC entry 2089 (class 2604 OID 16869)
-- Name: t_friendships id; Type: DEFAULT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_friendships ALTER COLUMN id SET DEFAULT nextval('main.t_friendships_id_seq'::regclass);


--
-- TOC entry 2093 (class 2604 OID 25260)
-- Name: t_messages id; Type: DEFAULT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_messages ALTER COLUMN id SET DEFAULT nextval('main.t_messages_id_seq'::regclass);


--
-- TOC entry 2088 (class 2604 OID 16852)
-- Name: t_news id; Type: DEFAULT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_news ALTER COLUMN id SET DEFAULT nextval('main.t_news_id_seq'::regclass);


--
-- TOC entry 2086 (class 2604 OID 16783)
-- Name: t_user id; Type: DEFAULT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_user ALTER COLUMN id SET DEFAULT nextval('main.t_user_id_seq'::regclass);


--
-- TOC entry 2091 (class 2604 OID 16940)
-- Name: lorem id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lorem ALTER COLUMN id SET DEFAULT nextval('public.lorem_id_seq'::regclass);


--
-- TOC entry 2097 (class 2606 OID 16788)
-- Name: t_user pk_t_user; Type: CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_user
    ADD CONSTRAINT pk_t_user PRIMARY KEY (id);


--
-- TOC entry 2120 (class 2606 OID 25419)
-- Name: t_conversation_user t_conversation_user_pkey; Type: CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_conversation_user
    ADD CONSTRAINT t_conversation_user_pkey PRIMARY KEY (id);


--
-- TOC entry 2110 (class 2606 OID 25244)
-- Name: t_conversations t_conversations_pkey; Type: CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_conversations
    ADD CONSTRAINT t_conversations_pkey PRIMARY KEY (id);


--
-- TOC entry 2105 (class 2606 OID 16889)
-- Name: t_friendrequests t_friendrequests_pkey; Type: CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_friendrequests
    ADD CONSTRAINT t_friendrequests_pkey PRIMARY KEY (id);


--
-- TOC entry 2103 (class 2606 OID 16871)
-- Name: t_friendships t_friendships_pkey; Type: CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_friendships
    ADD CONSTRAINT t_friendships_pkey PRIMARY KEY (id);


--
-- TOC entry 2114 (class 2606 OID 25266)
-- Name: t_messages t_messages_pkey; Type: CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_messages
    ADD CONSTRAINT t_messages_pkey PRIMARY KEY (id);


--
-- TOC entry 2100 (class 2606 OID 16857)
-- Name: t_news t_news_pkey; Type: CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_news
    ADD CONSTRAINT t_news_pkey PRIMARY KEY (id);


--
-- TOC entry 2124 (class 2606 OID 25421)
-- Name: t_conversation_user u_conversation_user; Type: CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_conversation_user
    ADD CONSTRAINT u_conversation_user UNIQUE (conversation_id, user_id);


--
-- TOC entry 2107 (class 2606 OID 16945)
-- Name: lorem lorem_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lorem
    ADD CONSTRAINT lorem_pkey PRIMARY KEY (id);


--
-- TOC entry 2116 (class 1259 OID 33838)
-- Name: t_conversation_user_conversation_id_idx; Type: INDEX; Schema: main; Owner: social
--

CREATE INDEX t_conversation_user_conversation_id_idx ON main.t_conversation_user USING btree (conversation_id);


--
-- TOC entry 2117 (class 1259 OID 33840)
-- Name: t_conversation_user_conversation_id_user_id_idx; Type: INDEX; Schema: main; Owner: social
--

CREATE INDEX t_conversation_user_conversation_id_user_id_idx ON main.t_conversation_user USING btree (conversation_id, user_id);


--
-- TOC entry 2118 (class 1259 OID 33843)
-- Name: t_conversation_user_id_user_id_conversation_id_idx; Type: INDEX; Schema: main; Owner: social
--

CREATE INDEX t_conversation_user_id_user_id_conversation_id_idx ON main.t_conversation_user USING btree (id, user_id, conversation_id);


--
-- TOC entry 2121 (class 1259 OID 33841)
-- Name: t_conversation_user_user_id_conversation_id_idx; Type: INDEX; Schema: main; Owner: social
--

CREATE INDEX t_conversation_user_user_id_conversation_id_idx ON main.t_conversation_user USING btree (user_id, conversation_id);


--
-- TOC entry 2122 (class 1259 OID 33839)
-- Name: t_conversation_user_user_id_idx; Type: INDEX; Schema: main; Owner: social
--

CREATE INDEX t_conversation_user_user_id_idx ON main.t_conversation_user USING btree (user_id);


--
-- TOC entry 2108 (class 1259 OID 33842)
-- Name: t_conversations_last_message_idx; Type: INDEX; Schema: main; Owner: social
--

CREATE INDEX t_conversations_last_message_idx ON main.t_conversations USING btree (last_message);


--
-- TOC entry 2111 (class 1259 OID 25288)
-- Name: t_messages_conversation_id_idx; Type: INDEX; Schema: main; Owner: social
--

CREATE INDEX t_messages_conversation_id_idx ON main.t_messages USING btree (conversation_id);


--
-- TOC entry 2112 (class 1259 OID 25331)
-- Name: t_messages_id_conversation_id_idx; Type: INDEX; Schema: main; Owner: social
--

CREATE INDEX t_messages_id_conversation_id_idx ON main.t_messages USING btree (id, conversation_id);


--
-- TOC entry 2115 (class 1259 OID 25290)
-- Name: t_messages_sender_id_idx; Type: INDEX; Schema: main; Owner: social
--

CREATE INDEX t_messages_sender_id_idx ON main.t_messages USING btree (sender_id);


--
-- TOC entry 2101 (class 1259 OID 25169)
-- Name: t_news_user_id_idx; Type: INDEX; Schema: main; Owner: social
--

CREATE INDEX t_news_user_id_idx ON main.t_news USING btree (user_id);


--
-- TOC entry 2098 (class 1259 OID 16964)
-- Name: t_user_username_idx; Type: INDEX; Schema: main; Owner: social
--

CREATE UNIQUE INDEX t_user_username_idx ON main.t_user USING btree (username);


--
-- TOC entry 2132 (class 2606 OID 25272)
-- Name: t_messages fk_conversation; Type: FK CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_messages
    ADD CONSTRAINT fk_conversation FOREIGN KEY (conversation_id) REFERENCES main.t_conversations(id);


--
-- TOC entry 2133 (class 2606 OID 25422)
-- Name: t_conversation_user fk_conversation_id; Type: FK CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_conversation_user
    ADD CONSTRAINT fk_conversation_id FOREIGN KEY (conversation_id) REFERENCES main.t_conversations(id);


--
-- TOC entry 2127 (class 2606 OID 16877)
-- Name: t_friendships fk_friendid; Type: FK CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_friendships
    ADD CONSTRAINT fk_friendid FOREIGN KEY (friendid) REFERENCES main.t_user(id);


--
-- TOC entry 2126 (class 2606 OID 16872)
-- Name: t_friendships fk_fuserid; Type: FK CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_friendships
    ADD CONSTRAINT fk_fuserid FOREIGN KEY (userid) REFERENCES main.t_user(id);


--
-- TOC entry 2130 (class 2606 OID 25315)
-- Name: t_conversations fk_last_message; Type: FK CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_conversations
    ADD CONSTRAINT fk_last_message FOREIGN KEY (last_message) REFERENCES main.t_messages(id);


--
-- TOC entry 2125 (class 2606 OID 16859)
-- Name: t_news fk_news_user; Type: FK CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_news
    ADD CONSTRAINT fk_news_user FOREIGN KEY (user_id) REFERENCES main.t_user(id);


--
-- TOC entry 2129 (class 2606 OID 16895)
-- Name: t_friendrequests fk_requested; Type: FK CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_friendrequests
    ADD CONSTRAINT fk_requested FOREIGN KEY (requestedid) REFERENCES main.t_user(id);


--
-- TOC entry 2128 (class 2606 OID 16890)
-- Name: t_friendrequests fk_requestor; Type: FK CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_friendrequests
    ADD CONSTRAINT fk_requestor FOREIGN KEY (requestorid) REFERENCES main.t_user(id);


--
-- TOC entry 2131 (class 2606 OID 25267)
-- Name: t_messages fk_sender; Type: FK CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_messages
    ADD CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES main.t_user(id);


--
-- TOC entry 2134 (class 2606 OID 25427)
-- Name: t_conversation_user fk_user_id; Type: FK CONSTRAINT; Schema: main; Owner: social
--

ALTER TABLE ONLY main.t_conversation_user
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES main.t_user(id);


--
-- TOC entry 2265 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2018-10-27 21:54:04

--
-- PostgreSQL database dump complete
--

