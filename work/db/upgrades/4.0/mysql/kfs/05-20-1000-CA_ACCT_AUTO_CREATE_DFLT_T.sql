-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 5/20/10 9:44 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-05-20-1000-AccountAutoCreateDefaults.xml::2010-05-20-1000-1::Winston::(MD5Sum: edfdc49ee41ad24d9f306ff1d6de1d80)
-- KC Integration: AccountAutoCreateDefaults
CREATE TABLE `CA_ACCT_AUTO_CREATE_DFLT_T` (`KC_UNIT` VARCHAR(8), `OBJ_ID` VARCHAR(36) NOT NULL, `VER_NBR` DECIMAL(8,0) DEFAULT '1' NOT NULL, `FIN_COA_CD` VARCHAR(2), `ACCOUNT_NM` VARCHAR(40), `KC_UNIT_NAME` VARCHAR(40), `ACCT_PSTL_CD` VARCHAR(20), `ACCT_CITY_NM` VARCHAR(25), `ACCT_STATE_CD` VARCHAR(2), `ACCT_STREET_ADDR` VARCHAR(30), `ACCT_OFF_CMP_IND` VARCHAR(1), `ACCT_TYP_CD` VARCHAR(2), `ACCT_PHYS_CMP_CD` VARCHAR(2), `SUB_FUND_GRP_CD` VARCHAR(5), `ACCT_FRNG_BNFT_CD` VARCHAR(1), `RPTS_TO_FIN_COA_CD` VARCHAR(2), `FRNG_BNFT_ACCT_NMBR` VARCHAR(7), `FIN_HGH_ED_FUNC_CD` VARCHAR(4), `ACCT_FSC_OFC_UID` VARCHAR(40), `ACCT_SPVSR_UNVL_ID` VARCHAR(40), `ACCT_MGR_UNVL_ID` VARCHAR(40), `ORG_CD` VARCHAR(4), `CONT_FIN_COA_CD` VARCHAR(2), `CONT_ACCOUNT_NBR` VARCHAR(7), `INCOME_FIN_COA_CD` VARCHAR(2), `INCOME_ACCOUNT_NBR` VARCHAR(7), `BDGT_REC_LVL_CD` VARCHAR(1), `ACCT_SF_CD` VARCHAR(1), `ACCT_PND_SF_CD` VARCHAR(1), `FIN_EXT_ENC_SF_CD` VARCHAR(1), `FIN_INT_ENC_SF_CD` VARCHAR(1), `FIN_PRE_ENC_SF_CD` VARCHAR(1), `FIN_OBJ_PRSCTRL_CD` VARCHAR(1), `ICR_FIN_COA_CD` VARCHAR(2), `ICR_ACCOUNT_NBR` VARCHAR(7), `CG_ACCT_RESP_ID` DECIMAL(2,0), `ACCT_ICR_TYP_CD` VARCHAR(2));


-- Changeset updates/2010-05-20-1000-AccountAutoCreateDefaults.xml::2010-05-20-1000-2::Winston::(MD5Sum: 2ebe9f2ca355f88aa056e2e6be2f61a)
-- KC Integration: index AccountAutoCreateDefaults
ALTER TABLE `CA_ACCT_AUTO_CREATE_DFLT_T` ADD PRIMARY KEY (`KC_UNIT`);


-- Release Database Lock

-- Release Database Lock

