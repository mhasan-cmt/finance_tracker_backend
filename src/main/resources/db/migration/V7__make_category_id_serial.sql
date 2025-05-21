
ALTER TABLE category ALTER COLUMN category_id DROP DEFAULT;


CREATE SEQUENCE IF NOT EXISTS category_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE category ALTER COLUMN category_id SET DEFAULT nextval('category_category_id_seq');

SELECT setval('category_category_id_seq', (SELECT MAX(category_id) FROM category));

ALTER SEQUENCE category_category_id_seq OWNED BY category.category_id;
