INSERT INTO cads.alert_mst (alert_name, description) VALUES ('Alert 1', 'Description 1');
INSERT INTO cads.alert_mst (alert_name, description) VALUES ('Alert 2', 'Description 2');

-- src/main/resources/db/migration/V2__add_email_to_business_user.sql

ALTER TABLE cads.business_user ADD COLUMN email VARCHAR(255);