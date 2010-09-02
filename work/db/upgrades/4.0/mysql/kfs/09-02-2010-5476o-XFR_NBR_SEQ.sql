-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/2/10 8:35 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-02-5476o-1-XFR_NBR_SEQ.xml::5476o-1-2::Shawn::(MD5Sum: 18bfe931e843889623baaa037603b5)
CREATE TABLE `XFR_NBR_SEQ` (`id` BIGINT(19) AUTO_INCREMENT  NOT NULL, CONSTRAINT `id` PRIMARY KEY (`id`));

ALTER TABLE XFR_NBR_SEQ auto_increment=1;


-- Release Database Lock

-- Release Database Lock

