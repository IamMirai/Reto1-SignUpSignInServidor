DELIMITER $$

DROP PROCEDURE IF EXISTS `signupsignindb`.`lastSignIn` $$
CREATE PROCEDURE `signupsignindb`.`lastSignIn` (id integer)
BEGIN
  if ((SELECT COUNT(*) FROM signin WHERE user_id = id) = 10) THEN
    DELETE FROM signin WHERE user_id = id ORDER BY lastSignIn ASC LIMIT 1;
    INSERT INTO signin VALUES (id, CURRENT_TIMESTAMP());
  ELSE
    INSERT INTO signin VALUES (id, CURRENT_TIMESTAMP());
  END IF;
END $$

DELIMITER ;