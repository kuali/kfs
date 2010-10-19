-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 7/13/10 10:58 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-07-13-5477c-1-END_HLDG_TAX_LOT_T.xml::5477c-1-1::Bonnie::(MD5Sum: e9485b1ae2cea4ca4f258e61c40ea8d)
-- missing foreign key relationship that we should define in the existing table. Plus The new sampel data is useful for testing tax lot rebalance function.
ALTER TABLE `END_HLDG_TAX_LOT_T` ADD CONSTRAINT `END_HLDG_TAX_LOT_TR4` FOREIGN KEY (`HLDG_IP_IND`) REFERENCES `END_IP_IND_T`(`IP_IND_CD`);


-- Changeset updates/2010-07-13-5477c-1-END_HLDG_TAX_LOT_T.xml::5477c-1-2::Bonnie::(MD5Sum: ba2d9feda3e061d344b8f980eafa222f)
-- missing foreign key relationship that we should define in the existing table. Plus The new sampel data is useful for testing tax lot rebalance function.
INSERT INTO `END_HLDG_TAX_LOT_T` (`HLDG_LOT_NBR`, `LAST_TRAN_DT`, `HLDG_IP_IND`, `HLDG_FRGN_TAX_WITH`, `OBJ_ID`, `HLDG_ACQD_DT`, `HLDG_ACRD_INC_DUE`, `HLDG_PRIOR_ACRD_INC`, `KEMID`, `HLDG_COST`, `SEC_ID`, `REGIS_CD`, `HLDG_UNITS`) VALUES ('1', '2009-06-26', 'P', '0', uuid(), '2009-06-26', '0', '0', '046G007720', '20000', '99BTIP011', '0CP', '1000');


-- Changeset updates/2010-07-13-5477c-1-END_HLDG_TAX_LOT_T.xml::5477c-1-3::Bonnie::(MD5Sum: e6f8c7a75f9c44542b39c86af9e8d377)
-- missing foreign key relationship that we should define in the existing table. Plus The new sampel data is useful for testing tax lot rebalance function.
INSERT INTO `END_HLDG_TAX_LOT_T` (`HLDG_LOT_NBR`, `LAST_TRAN_DT`, `HLDG_IP_IND`, `HLDG_FRGN_TAX_WITH`, `OBJ_ID`, `HLDG_ACQD_DT`, `HLDG_ACRD_INC_DUE`, `HLDG_PRIOR_ACRD_INC`, `KEMID`, `HLDG_COST`, `SEC_ID`, `REGIS_CD`, `HLDG_UNITS`) VALUES ('2', '2009-08-27', 'P', '0', uuid(), '2009-08-27', '0', '0', '046G007720', '3500', '99BTIP011', '0CP', '200');


-- Changeset updates/2010-07-13-5477c-1-END_HLDG_TAX_LOT_T.xml::5477c-1-4::Bonnie::(MD5Sum: f4d28f2d12eb42aaaa5994d7a119877)
-- missing foreign key relationship that we should define in the existing table. Plus The new sampel data is useful for testing tax lot rebalance function.
INSERT INTO `END_HLDG_TAX_LOT_T` (`HLDG_LOT_NBR`, `LAST_TRAN_DT`, `HLDG_IP_IND`, `HLDG_FRGN_TAX_WITH`, `OBJ_ID`, `HLDG_ACQD_DT`, `HLDG_ACRD_INC_DUE`, `HLDG_PRIOR_ACRD_INC`, `KEMID`, `HLDG_COST`, `SEC_ID`, `REGIS_CD`, `HLDG_UNITS`) VALUES ('3', '2008-09-30', 'P', '0', uuid(), '2008-09-30', '0', '0', '046G007720', '6400', '99BTIP011', '0CP', '300');


-- Release Database Lock

-- Release Database Lock

