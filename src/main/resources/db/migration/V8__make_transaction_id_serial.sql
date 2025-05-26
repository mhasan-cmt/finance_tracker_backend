
ALTER TABLE transaction ALTER COLUMN category_id DROP DEFAULT;


CREATE SEQUENCE IF NOT EXISTS transaction_transaction_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE transaction ALTER COLUMN transaction_id SET DEFAULT nextval('transaction_transaction_id_seq');

SELECT setval('transaction_transaction_id_seq', (SELECT MAX(transaction_id) FROM transaction));

ALTER SEQUENCE transaction_transaction_id_seq OWNED BY transaction.transaction_id;