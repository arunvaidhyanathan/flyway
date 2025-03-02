-- src/main/resources/db/migration/V3__add_index_on_username.sql

CREATE INDEX idx_business_user_username ON cads.business_user (username);