-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 3/30/10 12:25 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-1::Winston::(MD5Sum: ed9f2bf0977e65bbcf82f7cc81680dc)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (1, 'US', 4109, uuid(), '19446', 'Y', 'PA', 0, 'Lansdale', 4108, 'Y', '85 North South St', 'PO');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-2::Winston::(MD5Sum: 103f2c44f8c276eaa926debd9f54737)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (1, 'US', 4110, uuid(), '19446', 'Y', 'PA', 0, 'Lansdale', 4108, 'Y', '87 North South St', 'RM');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-3::Winston::(MD5Sum: df58a9f1bc7cd19eaa6ed71f91324f8)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (1, 'US', 4119, uuid(), '14886', 'Y', 'NY', 0, 'Trumansburg', 4116, 'Y', '123 E Queen St', 'RM');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-4::Winston::(MD5Sum: fe33e62e79aab84d13afaedc791ad52f)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_DTL_ASND_ID`, `VNDR_LN2_ADDR`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_ATTN_NM`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (1, 'CH', 4120, uuid(), '200433', 'Y', 0, 'Institute for Advanced Res - 777 Guoding Rd', 'Shanghai', 4117, 'Y', 'Shanghai University of Finance & Economics', '707 School of Economics Building', 'PO');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-5::Winston::(MD5Sum: c3ee4accb67afc6afbe2afe72b2db2)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (2, 'US', 4113, uuid(), '14850', 'Y', 'NY', 0, 'Ithaca', 4111, 'Y', '12 Snowly Lane', 'PO');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-6::Winston::(MD5Sum: ef7e75bc40747d4298ec0fc2835e0c)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (2, 'US', 4114, uuid(), '14850', 'Y', 'NY', 0, 'Ithaca', 4111, 'Y', '12 Snowy Lane', 'RM');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-7::Winston::(MD5Sum: 6ae540a3573aca1027197a9b32a46abf)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (1, 'US', 4111, uuid(), '19446', 'Y', 'PA', 0, 'Lansdale', 4109, 'Y', '987 West East St', 'RM');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-8::Winston::(MD5Sum: 6cfa7ddf6b9b934415c89542af6da4b4)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (2, 'US', 4115, uuid(), '94303', 'Y', 'CA', 0, 'E Palo Alto', 4112, 'Y', '2050 University Ave', 'PO');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-9::Winston::(MD5Sum: 9ead5662b3e2c75d6f6e414993234ea)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (3, 'US', 4116, uuid(), '14850', 'Y', 'NY', 0, 'Ithaca', 4113, 'Y', '787 Airport Rd', 'PO');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-10::Winston::(MD5Sum: e3b98fc3164065eb8078c858e672429)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (2, 'US', 4117, uuid(), '14850', 'Y', 'NY', 0, 'Ithaca', 4114, 'Y', '73526 Elm Tree Road', 'PO');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-11::Winston::(MD5Sum: 7e79d7ec447e2f1775949dec60b5bf)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (1, 'US', 4118, uuid(), '19440', 'Y', 'PA', 0, 'North Wales', 4115, 'Y', '654 Accordian St', 'RM');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-12::Winston::(MD5Sum: 70a2a970e7bec53929fec7294cbc9514)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `VNDR_ZIP_CD`, `OBJ_ID`, `VNDR_ADDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_DFLT_ADDR_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`, `VNDR_DTL_ASND_ID`) VALUES (1, 'FR', 'Paris', 4105, '75044', uuid(), 4105, 'Y', 'Y', '12 Rue de Rivoli', 'RM', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-13::Winston::(MD5Sum: c03eb7292e83257bfebd54c11292a4c)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (1, 'US', 4106, uuid(), '14850', 'N', 'NY', 0, 'Ithaca', 4105, 'Y', '123 E Main St', 'RM');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-14::Winston::(MD5Sum: c5ea42145fdb5d9f46262cb78dd9286d)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (1, 'US', 4107, uuid(), '14850', 'Y', 'NY', 0, 'Ithaca', 4106, 'Y', '341 Pine Tree Rd', 'RM');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-15::Winston::(MD5Sum: 4b38323dd58b2b236f28adc17338)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (2, 'US', 4108, uuid(), '14850', 'Y', 'NY', 0, 'Ithaca', 4107, 'Y', '123 E Queen St', 'RM');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-16::Winston::(MD5Sum: 5c871cfb3c17a4e2c0bcea4bc06284da)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (2, 'US', 4112, uuid(), '14850', 'Y', 'NY', 0, 'Ithaca', 4110, 'Y', '4415 East State St', 'PO');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-17::Winston::(MD5Sum: 5811e3f8c133bdd8ff8986353f7adfc0)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (1, 'US', 4121, uuid(), '14850', 'Y', 'NY', 0, 'Ithaca', 4118, 'Y', '2922 River Road', 'PO');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_ADDR_T.xml::5473-addr-1-18::Winston::(MD5Sum: bdb3f332214be6caf56546d7c0e280)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_ADDR_T` (`VER_NBR`, `VNDR_CNTRY_CD`, `VNDR_ADDR_GNRTD_ID`, `OBJ_ID`, `VNDR_ZIP_CD`, `VNDR_DFLT_ADDR_IND`, `VNDR_ST_CD`, `VNDR_DTL_ASND_ID`, `VNDR_CTY_NM`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_LN1_ADDR`, `VNDR_ADDR_TYP_CD`) VALUES (1, 'US', 4122, uuid(), '14850', 'Y', 'NY', 0, 'Ithaca', 4119, 'Y', '191 Lake St', 'PO');


-- Release Database Lock

-- Release Database Lock

