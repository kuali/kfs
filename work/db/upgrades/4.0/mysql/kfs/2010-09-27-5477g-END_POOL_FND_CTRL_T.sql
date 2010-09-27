-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/27/10 6:20 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-27-5477g-1-END_POOL_FND_CTRL_T.xml::5477g-1-1::Bonnie::(MD5Sum: 4ac39492aed03b2d571bc4faff492e0)
-- We need to set the registration code for the existing records in that table.
UPDATE `END_POOL_FND_CTRL_T` SET `POOL_REGIS_CD` = '0AI' WHERE POOL_SEC_ID='99MMKT013';


-- Changeset updates/2010-09-27-5477g-1-END_POOL_FND_CTRL_T.xml::5477g-1-2::Bonnie::(MD5Sum: 7735ba88ee3ca479ae66a75f6d85db0)
-- We need to set the registration code for the existing records in that table.
UPDATE `END_POOL_FND_CTRL_T` SET `POOL_REGIS_CD` = '0CI' WHERE POOL_SEC_ID='99PLTF013';


-- Changeset updates/2010-09-27-5477g-1-END_POOL_FND_CTRL_T.xml::5477g-1-3::Bonnie::(MD5Sum: 665f462dbf516e305ed766aad778f94)
-- We need to set the registration code for the existing records in that table.
UPDATE `END_POOL_FND_CTRL_T` SET `POOL_REGIS_CD` = '0CP' WHERE POOL_SEC_ID='99PLTF021';


-- Changeset updates/2010-09-27-5477g-1-END_POOL_FND_CTRL_T.xml::5477g-1-4::Bonnie::(MD5Sum: ec51623fb5fd545dab6b3fc6dc47d)
-- We need to set the registration code for the existing records in that table.
UPDATE `END_POOL_FND_CTRL_T` SET `POOL_REGIS_CD` = '0CP' WHERE POOL_SEC_ID='99PLTF997';


-- Changeset updates/2010-09-27-5477g-1-END_POOL_FND_CTRL_T.xml::5477g-1-5::Bonnie::(MD5Sum: 16714b3d4996de6794cc3138f36ba651)
-- We need to set the registration code for the existing records in that table.
UPDATE `END_POOL_FND_CTRL_T` SET `POOL_REGIS_CD` = '0AI' WHERE POOL_SEC_ID='99PSTF018';


-- Changeset updates/2010-09-27-5477g-1-END_POOL_FND_CTRL_T.xml::5477g-1-6::Bonnie::(MD5Sum: ae8496106f66067e962ae83ca9139)
-- We need to set the registration code for the existing records in that table.
UPDATE `END_POOL_FND_CTRL_T` SET `POOL_REGIS_CD` = '0AP' WHERE POOL_SEC_ID='99PSTF034';


-- Release Database Lock

-- Release Database Lock

