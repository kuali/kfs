-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 3/30/10 11:09 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-1::Winston::(MD5Sum: 6fe1aa40625330d032bdbcd38120fc)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_TAX_NBR`, `VNDR_W9_RCVD_IND`, `VNDR_TAX_TYP_CD`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (1, 'ID', 'XBy8HbA0p2kc3lalnjPKpA==', 'Y', 'SSN', 4106, uuid(), 'DV', 'N');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-2::Winston::(MD5Sum: 6eb4549f9cefbf46e61cad6b444a4d8)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_TAX_NBR`, `VNDR_W9_RCVD_IND`, `VNDR_TAX_TYP_CD`, `VNDR_FWT_END_DT`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_FWT_BEG_DT`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (2, 'ID', 'TJY6/PeY/DKK9pAI/L50kg==', 'N', 'SSN', '2011-07-31 12:00:00.0', 4107, uuid(), '2010-03-01 12:00:00.0', 'DV', 'N');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-3::Winston::(MD5Sum: 57298f43f43ec26ada8827b4a2049)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_TAX_NBR`, `VNDR_W9_RCVD_IND`, `VNDR_TAX_TYP_CD`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (1, 'ID', 'liKW4ZarlC01yan6wdvxhQ==', 'Y', 'SSN', 4109, uuid(), 'DV', 'N');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-4::Winston::(MD5Sum: a3226376953167ab2f536998d318242d)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (1, 'ID', 4117, uuid(), 'PO', 'Y');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-5::Winston::(MD5Sum: adc730983a2e2216b0a4bba3e5e3a974)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_TAX_NBR`, `VNDR_W9_RCVD_IND`, `VNDR_TAX_TYP_CD`, `VNDR_FWT_END_DT`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_FWT_BEG_DT`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (1, 'ID', 'DZb08jT3iP60Dedyut8K/A==', 'N', 'SSN', '2012-04-01 12:00:00.0', 4108, uuid(), '2010-03-01 12:00:00.0', 'PO', 'N');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-6::Winston::(MD5Sum: 1057c527865b37e16373e34697151c)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_TAX_NBR`, `VNDR_W9_RCVD_IND`, `VNDR_TAX_TYP_CD`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (2, 'CP', 'hlpoCk9p3vKkutkw59ibRA==', 'Y', 'FEIN', 4110, uuid(), 'PO', 'N');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-7::Winston::(MD5Sum: 45927e656e6abca6d52a34ad6e1e98)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_TAX_NBR`, `VNDR_TAX_TYP_CD`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (1, 'ID', '/YOAwYqdvMA1yan6wdvxhQ==', 'SSN', 4116, uuid(), 'RF', 'N');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-8::Winston::(MD5Sum: b0ab204e3d6488a4488cd2d2338e555)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_W8_BEN_RCVD_IND`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (1, 'ID', 4105, uuid(), 'Y', 'DV', 'Y');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-9::Winston::(MD5Sum: 1df61e2a77a512718fbbdc5efd3174c1)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_TAX_NBR`, `VNDR_W9_RCVD_IND`, `VNDR_TAX_TYP_CD`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (2, 'PT', 'qpCX26lt+snbGOdhtgfyiw==', 'Y', 'FEIN', 4112, uuid(), 'PO', 'N');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-10::Winston::(MD5Sum: 7d6f6e10a2161e24d84c891bb8bbe18c)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_TAX_NBR`, `VNDR_W9_RCVD_IND`, `VNDR_TAX_TYP_CD`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (3, 'CP', '9ayf4tscexT4uOW53wiKPQ==', 'Y', 'FEIN', 4113, uuid(), 'PO', 'N');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-11::Winston::(MD5Sum: 6cced2cc257fb9bdcb459432d7827d43)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_TAX_NBR`, `VNDR_W9_RCVD_IND`, `VNDR_TAX_TYP_CD`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (2, 'CP', 'uxpeQ3unxZu39UFEd4GMnQ==', 'Y', 'FEIN', 4114, uuid(), 'PO', 'N');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-12::Winston::(MD5Sum: e3ce85e98029f481958f0c233298ff1)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_TAX_NBR`, `VNDR_TAX_TYP_CD`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (1, 'ID', 'GEzbcnq11xv4uOW53wiKPQ==', 'SSN', 4115, uuid(), 'SP', 'N');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-13::Winston::(MD5Sum: dde5a39b963eec80b672d83b33030bb)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_TAX_NBR`, `VNDR_W9_RCVD_IND`, `VNDR_TAX_TYP_CD`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (2, 'ID', 'YOtrwHHMpX0N078NbkIQyQ==', 'Y', 'SSN', 4111, uuid(), 'PO', 'N');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-14::Winston::(MD5Sum: 7689293ba9950726a671e63e9ef0a)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_TAX_NBR`, `VNDR_W9_RCVD_IND`, `VNDR_TAX_TYP_CD`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (1, 'CP', '1bK3GD1nbZgc3lalnjPKpA==', 'Y', 'FEIN', 4118, uuid(), 'PO', 'N');


-- Changeset updates/2010-03-30-5473-2-PUR_VNDR_HDR_T.xml::5473-2-15::Winston::(MD5Sum: 1e4a2afe0795a690ef651fb9d21f51)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_HDR_T` (`VER_NBR`, `VNDR_OWNR_CD`, `VNDR_TAX_NBR`, `VNDR_W9_RCVD_IND`, `VNDR_TAX_TYP_CD`, `VNDR_HDR_GNRTD_ID`, `OBJ_ID`, `VNDR_TYP_CD`, `VNDR_FRGN_IND`) VALUES (1, 'CP', 'PL2y8YXOHf4c3lalnjPKpA==', 'Y', 'FEIN', 4119, uuid(), 'PO', 'N');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-1::Winston::(MD5Sum: 1b4ddf530d62dbe778f72b54a76eda6)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_HDR_GNRTD_ID`, `VNDR_1ST_LST_NM_IND`, `OBJ_ID`, `VNDR_PMT_TERM_CD`, `VNDR_NM`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_PARENT_IND`, `VNDR_DTL_ASND_ID`) VALUES (1, 'N', 4106, 'Y', uuid(), '00N30', 'Smith, Iggy - Employee as Vendor', 'Y', 'Y', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-2::Winston::(MD5Sum: 624dfb41ec8b97a26b9e9c86ffd2a4b)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_HDR_GNRTD_ID`, `VNDR_1ST_LST_NM_IND`, `OBJ_ID`, `VNDR_NM`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_PARENT_IND`, `VNDR_DTL_ASND_ID`) VALUES (2, 'N', 4107, 'Y', uuid(), 'Back Up Withholding, DV Vendor', 'Y', 'Y', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-3::Winston::(MD5Sum: 4f731d8d96339a17fdcf6b43858d22)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `OBJ_ID`, `VNDR_1ST_LST_NM_IND`, `VNDR_NM`, `VNDR_RSTRC_DT`, `VNDR_DTL_ASND_ID`, `VNDR_PARENT_IND`, `VNDR_HDR_GNRTD_ID`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_RSTRC_REAS_TXT`, `VNDR_RSTRC_PRSN_ID`, `VNDR_RSTRC_IND`) VALUES (1, 'N', uuid(), 'N', 'DV Vendor Restricted', '2010-03-24', 0, 'Y', 4109, 'Y', 'Avoid appearance of conflict of interest', '4161901639', 'Y');


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-4::Winston::(MD5Sum: c89c9d3b24368ae3eaea27e541cb150)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_HDR_GNRTD_ID`, `VNDR_1ST_LST_NM_IND`, `OBJ_ID`, `VNDR_PMT_TERM_CD`, `VNDR_NM`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_RMT_NM`, `VNDR_PARENT_IND`, `VNDR_DTL_ASND_ID`) VALUES (1, 'N', 4117, 'N', uuid(), '00N30', 'Foreign PO Vendor', 'Y', 'Wong Feng', 'Y', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-5::Winston::(MD5Sum: e8547b31cd65e50eda9ee3ec25e4e7c)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_HDR_GNRTD_ID`, `VNDR_1ST_LST_NM_IND`, `OBJ_ID`, `VNDR_NM`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_PARENT_IND`, `VNDR_DTL_ASND_ID`) VALUES (1, 'N', 4108, 'N', uuid(), 'Back-Up Withholding PO Vendor', 'Y', 'Y', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-6::Winston::(MD5Sum: d95cb3b860aaaf2771b6a27228e6e97)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_HDR_GNRTD_ID`, `VNDR_1ST_LST_NM_IND`, `OBJ_ID`, `VNDR_PMT_TERM_CD`, `VNDR_NM`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_RMT_NM`, `VNDR_PARENT_IND`, `VNDR_DTL_ASND_ID`) VALUES (2, 'N', 4110, 'N', uuid(), '00N30', 'PO Vendor with Multiple Names', 'Y', 'Cascade Plaza LLC', 'Y', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-7::Winston::(MD5Sum: 7f9d5edf2cc748f99bc04229b3e8a6)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_HDR_GNRTD_ID`, `VNDR_1ST_LST_NM_IND`, `OBJ_ID`, `VNDR_NM`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_PARENT_IND`, `VNDR_DTL_ASND_ID`) VALUES (1, 'N', 4116, 'N', uuid(), 'Revolving Fund Vendor', 'Y', 'Y', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-8::Winston::(MD5Sum: 3926c8f383e62c231d5cf1c71a7e5465)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_HDR_GNRTD_ID`, `VNDR_1ST_LST_NM_IND`, `OBJ_ID`, `VNDR_PMT_TERM_CD`, `VNDR_NM`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_PARENT_IND`, `VNDR_DTL_ASND_ID`) VALUES (1, 'N', 4105, 'N', uuid(), '00N05', 'Foreign DV Vendor', 'Y', 'Y', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-9::Winston::(MD5Sum: 68f8814e13974ed8db6d11585fc228d8)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_HDR_GNRTD_ID`, `VNDR_1ST_LST_NM_IND`, `OBJ_ID`, `VNDR_PMT_TERM_CD`, `VNDR_NM`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_RMT_NM`, `VNDR_PARENT_IND`, `VNDR_DTL_ASND_ID`) VALUES (2, 'N', 4112, 'N', uuid(), '00N30', 'PO Vendor Hotel Owned by Parent Company', 'Y', 'East Palo Alto Development LLC', 'Y', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-10::Winston::(MD5Sum: f5f289466f67c8577bcd2bbebb32fda)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_HDR_GNRTD_ID`, `VNDR_1ST_LST_NM_IND`, `OBJ_ID`, `VNDR_PMT_TERM_CD`, `VNDR_NM`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_RMT_NM`, `VNDR_PARENT_IND`, `VNDR_DTL_ASND_ID`) VALUES (3, 'N', 4113, 'N', uuid(), '00N30', 'PO Vendor - Sold-To Company', 'Y', 'Cayuga Press Inc', 'Y', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-11::Winston::(MD5Sum: 3cc7e5d23a507cac606d1a35512b27e6)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_SOLD_TO_NM`, `OBJ_ID`, `VNDR_1ST_LST_NM_IND`, `VNDR_NM`, `VNDR_SOLD_TO_GNRTD_ID`, `VNDR_DTL_ASND_ID`, `VNDR_PARENT_IND`, `VNDR_RMT_NM`, `VNDR_HDR_GNRTD_ID`, `VNDR_PMT_TERM_CD`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_SOLD_TO_ASND_ID`) VALUES (2, 'N', 'Sold-To Company', uuid(), 'N', 'PO Vendor - Sold to Another Company', 4113, 0, 'Y', 'Cayuga Press', 4114, '00N30', 'Y', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-12::Winston::(MD5Sum: b155e39f3cacb6c2d99dde0985bc779)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_HDR_GNRTD_ID`, `VNDR_1ST_LST_NM_IND`, `OBJ_ID`, `VNDR_NM`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_PARENT_IND`, `VNDR_DTL_ASND_ID`) VALUES (1, 'N', 4115, 'N', uuid(), 'Participant Vendor', 'Y', 'Y', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-13::Winston::(MD5Sum: 90dcdb41ddadefeba17ced6573cedc)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_HDR_GNRTD_ID`, `VNDR_1ST_LST_NM_IND`, `OBJ_ID`, `VNDR_PMT_TERM_CD`, `VNDR_NM`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_RMT_NM`, `VNDR_PARENT_IND`, `VNDR_DTL_ASND_ID`) VALUES (2, 'N', 4111, 'N', uuid(), '00N30', 'PO Vendor - Employee-Independent Contractor', 'Y', 'Donald M Bazley', 'Y', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-14::Winston::(MD5Sum: cd96f1f3a6fbe5585085a54bf7ee2762)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_HDR_GNRTD_ID`, `VNDR_1ST_LST_NM_IND`, `OBJ_ID`, `VNDR_PMT_TERM_CD`, `VNDR_NM`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_RMT_NM`, `VNDR_PARENT_IND`, `VNDR_DTL_ASND_ID`) VALUES (1, 'N', 4118, 'N', uuid(), '00N30', 'High Volume PO Vendor', 'Y', 'Dell Marketing', 'Y', 0);


-- Changeset updates/2010-03-30-5473-1-PUR_VNDR_DTL_T.xml::5473-1-15::Winston::(MD5Sum: 602617cc72d440b29d3e2cb96833425)
-- DV Vendor - add a foreign vendor to the master data base
INSERT INTO `PUR_VNDR_DTL_T` (`VER_NBR`, `COLLECT_TAX_IND`, `VNDR_HDR_GNRTD_ID`, `VNDR_1ST_LST_NM_IND`, `OBJ_ID`, `VNDR_PMT_TERM_CD`, `VNDR_NM`, `DOBJ_MAINT_CD_ACTV_IND`, `VNDR_RMT_NM`, `VNDR_PARENT_IND`, `VNDR_DTL_ASND_ID`) VALUES (1, 'N', 4119, 'N', uuid(), '00N30', 'Cap Asset PO Vendor', 'Y', 'VWR International', 'Y', 0);


-- Release Database Lock

-- Release Database Lock

