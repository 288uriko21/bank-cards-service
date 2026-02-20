-- changeset julia:001-init-schema

CREATE TABLE IF NOT EXISTS public.users (
    id        BIGSERIAL     NOT NULL,
    "password" VARCHAR(255) NOT NULL,
    "role"     VARCHAR(20)  NOT NULL,
    username   VARCHAR(50)  NOT NULL,
    CONSTRAINT uk_r43af9ap4edm43mmtq01oddj6 UNIQUE (username),
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.cards (
    id          BIGSERIAL      NOT NULL,
    balance     NUMERIC(38, 2) NOT NULL,
    card_number VARCHAR(32)    NOT NULL,
    expiry_date DATE           NOT NULL,
    status      VARCHAR(255)   NOT NULL,
    user_id     INT8           NOT NULL,
    CONSTRAINT cards_pkey PRIMARY KEY (id),
    CONSTRAINT cards_status_check CHECK (
        (status)::text = ANY (
            (ARRAY['ACTIVE'::varchar, 'BLOCKED'::varchar, 'EXPIRED'::varchar])::text[]
        )
    ),
    CONSTRAINT uk_qualp9iflk959u561wanavuj1 UNIQUE (card_number)
);

ALTER TABLE public.cards
    ADD CONSTRAINT fkcmanafgwbibfijy2o5isfk3d5
        FOREIGN KEY (user_id) REFERENCES public.users(id);

CREATE TABLE IF NOT EXISTS public.transactions (
    id           BIGSERIAL      NOT NULL,
    amount       NUMERIC(38, 2) NOT NULL,
    created_at   TIMESTAMP(6)   NOT NULL,
    message      VARCHAR(255),
    status       VARCHAR(20)    NOT NULL,
    from_card_id INT8           NOT NULL,
    to_card_id   INT8           NOT NULL,
    "type"       VARCHAR(20)    DEFAULT 'INTERNAL'::varchar NOT NULL,
    CONSTRAINT transactions_pkey PRIMARY KEY (id)
);

ALTER TABLE public.transactions
    ADD CONSTRAINT fk2rjf7q3aokek0bc3817l80p77
        FOREIGN KEY (from_card_id) REFERENCES public.cards(id);

ALTER TABLE public.transactions
    ADD CONSTRAINT fkrjipfg1tkqdhun7vvoe5m6rca
        FOREIGN KEY (to_card_id) REFERENCES public.cards(id);
