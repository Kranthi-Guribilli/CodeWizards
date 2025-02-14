INSERT INTO `roles` (`role_name`,`created_at`, `created_by`)
  VALUES ('ADMIN',CURDATE(),'DBA');

INSERT INTO `roles` (`role_name`,`created_at`, `created_by`)
  VALUES ('STUDENT',CURDATE(),'DBA');
INSERT INTO `person` (`name`,`email`,`mobile_number`,`pwd`,`role_id`,`created_at`, `created_by`)
  VALUES ('Admin','admin@eazyschool.com','3443434343','$2a$10$XhU4UcSxDPb5G0I0fT/CZ.Lfj2VW2fkLkUP5cOEM.xM8EzyUQXaD2', 1 ,CURDATE(),'DBA');