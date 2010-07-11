-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 7/7/10 10:04 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-07-07-5850-1-CA_OBJECT_CODE_T.xml::5850-1-1::David::(MD5Sum: 8f846b1d64d674aa7a2b82f8f4fdf5)
-- add new columns to CA_Object_code_t and CA_object_Code_V
ALTER TABLE `CA_OBJECT_CODE_T` ADD `RSCH_BDGT_CTGRY_CD` VARCHAR(3);


-- Changeset updates/2010-07-07-5850-1-CA_OBJECT_CODE_T.xml::5850-1-2::David::(MD5Sum: ab8ac9c7338dabf6cbdc149a1dbeb4)
-- add new columns to CA_Object_code_t and CA_object_Code_V
ALTER TABLE `CA_OBJECT_CODE_T` ADD `RSCH_OBJ_CD_DESC` VARCHAR(200);


-- Changeset updates/2010-07-07-5850-1-CA_OBJECT_CODE_T.xml::5850-1-3::David::(MD5Sum: 6bd2498e52d6ace6f788c214d36df3)
-- add new columns to CA_Object_code_t and CA_object_Code_V
ALTER TABLE `CA_OBJECT_CODE_T` ADD `RSCH_ON_CMP_IND` VARCHAR(1);


-- Release Database Lock

-- Release Database Lock

CREATE OR REPLACE VIEW CA_OBJECT_CODE_V
AS
(
SELECT
        C.UNIV_FISCAL_YR,
        FIN_COA_CD,
        FIN_OBJECT_CD,
        FIN_OBJ_CD_NM,
        FIN_OBJ_CD_SHRT_NM,
        FIN_OBJ_LEVEL_CD,
        RPTS_TO_FIN_COA_CD,
        RPTS_TO_FIN_OBJ_CD ,
        FIN_OBJ_TYP_CD,
        FIN_OBJ_SUB_TYP_CD,
        HIST_FIN_OBJECT_CD,
        FIN_OBJ_ACTIVE_CD,
        FOBJ_BDGT_AGGR_CD,
        FOBJ_MNXFR_ELIM_CD,
        FIN_FED_FUNDED_CD,
        NXT_YR_FIN_OBJ_CD,
        RSCH_BDGT_CTGRY_CD,
        RSCH_OBJ_CD_DESC,
        RSCH_ON_CMP_IND
        
FROM CA_OBJECT_CODE_T AS C, SH_UNIV_DATE_T AS S
WHERE C.UNIV_FISCAL_YR = S.UNIV_FISCAL_YR AND UNIV_DT = DATE(CURRENT_DATE));

