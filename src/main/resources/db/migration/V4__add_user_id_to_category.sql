ALTER TABLE category
ADD COLUMN user_id BIGINT NULL;

ALTER TABLE category
ADD CONSTRAINT FK_CATEGORY_ON_USERID FOREIGN KEY (user_id) REFERENCES users (id);

-- Set existing categories to have NULL user_id (global/admin categories)
UPDATE category SET user_id = NULL;