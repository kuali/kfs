--
-- Copyright 2005-2012 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- 
-- mysql-2010-02-15.sql
-- 


ALTER TABLE trv_acct ADD (OBJ_ID VARCHAR(36))
;
ALTER TABLE trv_acct ADD (VER_NBR DECIMAL(8) DEFAULT 0)
;

ALTER TABLE trv_acct_type ADD (OBJ_ID VARCHAR(36))
;
ALTER TABLE trv_acct_type ADD (VER_NBR DECIMAL(8) DEFAULT 0)
;

ALTER TABLE trv_acct_fo ADD (OBJ_ID VARCHAR(36))
;
ALTER TABLE trv_acct_fo ADD (VER_NBR DECIMAL(8) DEFAULT 0)
;

ALTER TABLE trv_acct_ext ADD (OBJ_ID VARCHAR(36))
;
ALTER TABLE trv_acct_ext ADD (VER_NBR DECIMAL(8) DEFAULT 0)
;


-- 
-- mysql-2010-04-15.sql
-- 


ALTER TABLE krns_sesn_doc_t ADD (OBJ_ID VARCHAR(36))
;
ALTER TABLE krns_sesn_doc_t ADD (VER_NBR DECIMAL(8) DEFAULT 0)
;




-- 
-- mysql-2010-05-03.sql
-- 


ALTER TABLE KRIM_PND_GRP_ATTR_DATA_T ADD (ACTV_IND VARCHAR(1) default 'Y'
                                        , EDIT_FLAG VARCHAR(1) default 'N')
;


-- 
-- mysql-2010-05-12.sql
-- 


alter table kren_chnl_subscrp_t add OBJ_ID varchar(36)
;
alter table kren_cntnt_typ_t add OBJ_ID varchar(36) 
;
alter table kren_chnl_t add OBJ_ID varchar(36) 
;
alter table kren_ntfctn_msg_deliv_t add OBJ_ID varchar(36) 
;
alter table kren_ntfctn_t add OBJ_ID varchar(36) 
;
alter table kren_prio_t add OBJ_ID varchar(36) 
;
alter table kren_prodcr_t add OBJ_ID varchar(36) 
;
alter table kren_recip_list_t add OBJ_ID varchar(36)
;
alter table kren_sndr_t add OBJ_ID varchar(36)
;
alter table kren_recip_t add OBJ_ID varchar(36) 
;
alter table kren_rvwer_t add OBJ_ID varchar(36) 
;
alter table kren_chnl_subscrp_t add ver_nbr decimal(8)
;
alter table kren_recip_list_t add ver_nbr decimal(8)
;
alter table kren_sndr_t add ver_nbr decimal(8)
;
alter table kren_recip_t add ver_nbr decimal(8)
;

-- 
-- mysql-2011-03-23.sql
-- 


update KREW_DOC_TYP_T set POST_PRCSR='org.kuali.rice.edl.framework.workflow.EDocLitePostProcessor'
where POST_PRCSR='org.kuali.rice.kew.edl.EDocLitePostProcessor'
;

update KREW_DOC_TYP_T set POST_PRCSR='org.kuali.rice.edl.framework.workflow.EDocLiteDatabasePostProcessor'
where POST_PRCSR='org.kuali.rice.kew.edl.EDLDatabasePostProcessor'
;

UPDATE KREW_DOC_TYP_T SET PARNT_ID='2681' WHERE DOC_TYP_NM='TravelAccountMaintenanceDocument'
;
UPDATE KREW_DOC_TYP_T SET PARNT_ID='2681' WHERE DOC_TYP_NM='FiscalOfficerMaintenanceDocument'
;
UPDATE KREW_DOC_TYP_T SET PARNT_ID='2681' WHERE DOC_TYP_NM='TravelRequest'
;


-- 
-- mysql-2011-04-28.sql
-- 


--
-- KULRICE-4794
-- The following statements will change the DOC_HDR_ID from a decimal to a VARCHAR(40) on various tables. 
--

ALTER TABLE KREW_ACTN_ITM_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_ACTN_RQST_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_ACTN_TKN_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_APP_DOC_STAT_TRAN_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40)
;
ALTER TABLE KREW_DOC_HDR_CNTNT_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_DOC_HDR_EXT_DT_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_DOC_HDR_EXT_FLT_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_DOC_HDR_EXT_LONG_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_DOC_HDR_EXT_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_DOC_HDR_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_DOC_NTE_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_DOC_TYP_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40)
;
ALTER TABLE KREW_EDL_DMP_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_EDL_FLD_DMP_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_INIT_RTE_NODE_INSTN_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_OUT_BOX_ITM_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_RMV_RPLC_DOC_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_RMV_RPLC_GRP_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_RMV_RPLC_RULE_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_RTE_NODE_INSTN_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_RULE_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40)
;

--
-- The following statements will change the ORGN_DOC_ID and DEST_DOC_ID 
-- from a decimal to a VARCHAR(40) on the KREW_DOC_LNK_T table 
--

ALTER TABLE KREW_DOC_LNK_T CHANGE ORGN_DOC_ID ORGN_DOC_ID VARCHAR(40) NOT NULL
;
ALTER TABLE KREW_DOC_LNK_T CHANGE DEST_DOC_ID DEST_DOC_ID VARCHAR(40) NOT NULL
;


-- 
-- mysql-2011-05-09.sql
-- 


--
--
-- DDL for KRMS repository
--
--

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';


-- -----------------------------------------------------
-- Table `krms_typ_t`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `krms_typ_t` (
  `typ_id` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `srvc_nm` VARCHAR(200) NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`typ_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_prop_t`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `krms_prop_t` (
  `prop_id` VARCHAR(40) NOT NULL ,
  `desc_txt` VARCHAR(100) NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `dscrm_typ_cd` VARCHAR(10) NOT NULL ,
  `cmpnd_op_cd` VARCHAR(40) NULL ,
  `rule_id` VARCHAR(40) NOT NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`prop_id`) ,
  INDEX `krms_prop_ti1` (`rule_id` ASC) ,
  INDEX `krms_prop_fk2` (`typ_id` ASC) ,
  CONSTRAINT `krms_prop_fk1`
    FOREIGN KEY (`rule_id` )
    REFERENCES `krms_rule_t` (`rule_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_prop_fk2`
    FOREIGN KEY (`typ_id` )
    REFERENCES `krms_typ_t` (`typ_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_rule_t`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `krms_rule_t` (
  `rule_id` VARCHAR(40) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `prop_id` VARCHAR(40) NOT NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  `descr_txt` VARCHAR(4000) NULL ,
  PRIMARY KEY (`rule_id`) ,
  INDEX `krms_rule_ti1` (`prop_id` ASC) ,
  CONSTRAINT `krms_rule_fk1`
    FOREIGN KEY (`prop_id` )
    REFERENCES `krms_prop_t` (`prop_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_cntxt_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_cntxt_t` (
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_agenda_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_agenda_t` (
  `agenda_id` VARCHAR(40) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `init_agenda_itm_id` VARCHAR(40) NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`agenda_id`) ,
  INDEX `krms_agenda_ti1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_agenda_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `krms_cntxt_t` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_attr_defn_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_attr_defn_t` (
  `attr_defn_id` VARCHAR(255) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `lbl` VARCHAR(40) NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  `cmpnt_nm` VARCHAR(100) NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`attr_defn_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_typ_attr_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_typ_attr_t` (
  `typ_attr_id` VARCHAR(40) NOT NULL ,
  `seq_no` TINYINT NOT NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `attr_defn_id` VARCHAR(255) NOT NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`typ_attr_id`) ,
  INDEX `krms_typ_attr_ti1` (`attr_defn_id` ASC) ,
  INDEX `krms_typ_attr_ti2` (`typ_id` ASC) ,
  UNIQUE INDEX `krms_typ_attr_tc1` (`typ_id` ASC, `attr_defn_id` ASC) ,
  CONSTRAINT `krms_typ_attr_fk1`
    FOREIGN KEY (`attr_defn_id` )
    REFERENCES `krms_attr_defn_t` (`attr_defn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_typ_attr_fk2`
    FOREIGN KEY (`typ_id` )
    REFERENCES `krms_typ_t` (`typ_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_actn_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_actn_t` (
  `actn_id` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(40) NULL ,
  `desc_txt` VARCHAR(4000) NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `rule_id` VARCHAR(40) NULL ,
  `seq_no` TINYINT NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`actn_id`) ,
  index `krms_actn_ti2` (`rule_id` asc) ,
  index `krms_actn_ti1` (`typ_id` asc) ,
  unique index `krms_actn_tc2` (`actn_id` asc, `rule_id` asc, `seq_no` asc) ,
  index `krms_actn_ti3` (`rule_id` asc, `seq_no` asc) ,
  constraint `krms_actn_fk1`
    FOREIGN KEY (`rule_id` )
    REFERENCES `krms_rule_t` (`rule_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_actn_attr_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_actn_attr_t` (
  `actn_attr_data_id` VARCHAR(40) NOT NULL ,
  `actn_id` VARCHAR(40) NOT NULL ,
  `attr_defn_id` VARCHAR(40) NOT NULL ,
  `attr_val` VARCHAR(400) NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`actn_attr_data_id`) ,
  INDEX `krms_actn_attr_ti1` (`actn_id` ASC) ,
  INDEX `krms_actn_attr_ti2` (`attr_defn_id` ASC) ,
  CONSTRAINT `krms_actn_attr_fk1`
    FOREIGN KEY (`actn_id` )
    REFERENCES `krms_actn_t` (`actn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_actn_attr_fk2`
    FOREIGN KEY (`attr_defn_id` )
    REFERENCES `krms_attr_defn_t` (`attr_defn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_agenda_itm_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_agenda_itm_t` (
  `agenda_itm_id` VARCHAR(40) NOT NULL ,
  `rule_id` VARCHAR(40) NULL ,
  `sub_agenda_id` VARCHAR(40) NULL ,
  `agenda_id` VARCHAR(40) NOT NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  `when_true` VARCHAR(40) NULL ,
  `when_false` VARCHAR(40) NULL ,
  `always` VARCHAR(40) NULL ,
  PRIMARY KEY (`agenda_itm_id`) ,
  INDEX `krms_agenda_itm_ti1` (`rule_id` ASC) ,
  INDEX `krms_agenda_itm_ti2` (`agenda_id` ASC) ,
  INDEX `krms_agenda_itm_ti3` (`sub_agenda_id` ASC) ,
  INDEX `krms_agenda_itm_ti4` (`when_true` ASC) ,
  INDEX `krms_agenda_itm_ti5` (`when_false` ASC) ,
  INDEX `krms_agenda_itm_ti6` (`always` ASC) ,
  CONSTRAINT `krms_agenda_itm_fk1`
    FOREIGN KEY (`rule_id` )
    REFERENCES `krms_rule_t` (`rule_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_agenda_itm_fk2`
    FOREIGN KEY (`agenda_id` )
    REFERENCES `krms_agenda_t` (`agenda_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_agenda_itm_fk3`
    FOREIGN KEY (`sub_agenda_id` )
    REFERENCES `krms_agenda_t` (`agenda_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_agenda_itm_fk4`
    FOREIGN KEY (`when_true` )
    REFERENCES `krms_agenda_itm_t` (`agenda_itm_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_agenda_itm_fk5`
    FOREIGN KEY (`when_false` )
    REFERENCES `krms_agenda_itm_t` (`agenda_itm_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_agenda_itm_fk6`
    FOREIGN KEY (`always` )
    REFERENCES `krms_agenda_itm_t` (`agenda_itm_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_rule_attr_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_rule_attr_t` (
  `rule_attr_id` VARCHAR(40) NOT NULL ,
  `rule_id` VARCHAR(40) NOT NULL ,
  `attr_defn_id` VARCHAR(40) NOT NULL ,
  `attr_val` VARCHAR(400) NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`rule_attr_id`) ,
  INDEX `krms_rule_attr_ti1` (`rule_id` ASC) ,
  INDEX `krms_rule_attr_ti2` (`attr_defn_id` ASC) ,
  CONSTRAINT `krms_rule_attr_fk1`
    FOREIGN KEY (`rule_id` )
    REFERENCES `krms_rule_t` (`rule_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_rule_attr_fk2`
    FOREIGN KEY (`attr_defn_id` )
    REFERENCES `krms_attr_defn_t` (`attr_defn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_cntxt_attr_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_cntxt_attr_t` (
  `cntxt_attr_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `attr_val` VARCHAR(400) NULL ,
  `attr_defn_id` VARCHAR(40) NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_attr_id`) ,
  INDEX `krms_cntxt_attr_ti1` (`cntxt_id` ASC) ,
  INDEX `krms_cntxt_attr_ti2` (`attr_defn_id` ASC) ,
  CONSTRAINT `krms_cntxt_attr_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `krms_cntxt_t` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_cntxt_attr_fk2`
    FOREIGN KEY (`attr_defn_id` )
    REFERENCES `krms_attr_defn_t` (`attr_defn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_cntxt_vld_actn_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_cntxt_vld_actn_t` (
  `cntxt_vld_actn_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `actn_typ_id` VARCHAR(40) NOT NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_vld_actn_id`) ,
  INDEX `krms_cntxt_vld_actn_ti1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_cntxt_vld_actn_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `krms_cntxt_t` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_agenda_attr_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_agenda_attr_t` (
  `agenda_attr_id` VARCHAR(40) NOT NULL ,
  `agenda_id` VARCHAR(40) NOT NULL ,
  `attr_val` VARCHAR(400) NULL ,
  `attr_defn_id` VARCHAR(40) NOT NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`agenda_attr_id`) ,
  INDEX `krms_agenda_attr_ti1` (`agenda_id` ASC) ,
  INDEX `krms_agenda_attr_t12` (`attr_defn_id` ASC) ,
  CONSTRAINT `krms_agenda_attr_fk1`
    FOREIGN KEY (`agenda_id` )
    REFERENCES `krms_agenda_t` (`agenda_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_agenda_attr_fk2`
    FOREIGN KEY (`attr_defn_id` )
    REFERENCES `krms_attr_defn_t` (`attr_defn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_cmpnd_prop_props_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_cmpnd_prop_props_t` (
  `cmpnd_prop_id` VARCHAR(40) NOT NULL ,
  `prop_id` VARCHAR(40) NOT NULL ,
  `seq_no` TINYINT NOT NULL ,
  PRIMARY KEY (`cmpnd_prop_id`, `prop_id`) ,
  INDEX `krms_cmpnd_prop_props_ti1` (`prop_id` ASC) ,
  INDEX `krms_cmpnd_prop_props_fk2` (`cmpnd_prop_id` ASC) ,
  CONSTRAINT `krms_cmpnd_prop_props_fk1`
    FOREIGN KEY (`prop_id` )
    REFERENCES `krms_prop_t` (`prop_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_cmpnd_prop_props_fk2`
    FOREIGN KEY (`cmpnd_prop_id` )
    REFERENCES `krms_prop_t` (`prop_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_prop_parm_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_prop_parm_t` (
  `prop_parm_id` VARCHAR(40) NOT NULL ,
  `prop_id` VARCHAR(40) NOT NULL ,
  `parm_val` VARCHAR(255) NULL ,
  `parm_typ_cd` VARCHAR(1) NOT NULL ,
  `seq_no` TINYINT NOT NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`prop_parm_id`) ,
  INDEX `krms_prop_parm_ti1` (`prop_id` ASC) ,
  CONSTRAINT `krms_prop_parm_fk1`
    FOREIGN KEY (`prop_id` )
    REFERENCES `krms_prop_t` (`prop_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_term_spec_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_term_spec_t` (
  `term_spec_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NULL ,
  `nm` VARCHAR(255) NOT NULL ,
  `typ` VARCHAR(255) NOT NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  `ver_nbr` DECIMAL NOT NULL ,
  PRIMARY KEY (`term_spec_id`) ,
  UNIQUE INDEX `krms_asset_tc1` (`nm` ASC, `cntxt_id` ASC) ,
  INDEX `krms_asset_ti1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_asset_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `krms_cntxt_t` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_term_rslvr_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_term_rslvr_t` (
  `term_rslvr_id` VARCHAR(40) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `output_term_spec_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`term_rslvr_id`) ,
  INDEX `krms_term_rslvr_ti1` (`cntxt_id` ASC) ,
  INDEX `krms_term_rslvr_ti2` (`typ_id` ASC) ,
  UNIQUE INDEX `krms_term_rslvr_tc1` (`nmspc_cd` ASC, `nm` ASC, `cntxt_id` ASC) ,
  CONSTRAINT `krms_term_rslvr_fk1`
    FOREIGN KEY (`output_term_spec_id` )
    REFERENCES `krms_term_spec_t` (`term_spec_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_term_rslvr_fk2`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `krms_cntxt_t` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_term_rslvr_fk3`
    FOREIGN KEY (`typ_id` )
    REFERENCES `krms_typ_t` (`typ_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_term_rslvr_attr_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_term_rslvr_attr_t` (
  `term_rslvr_attr_id` VARCHAR(40) NOT NULL ,
  `term_rslvr_id` VARCHAR(40) NOT NULL ,
  `attr_defn_id` VARCHAR(40) NOT NULL ,
  `attr_val` VARCHAR(400) NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`term_rslvr_attr_id`) ,
  INDEX `krms_asset_rslvr_attr_ti1` (`term_rslvr_id` ASC) ,
  INDEX `krms_asset_rslvr_attr_ti2` (`attr_defn_id` ASC) ,
  CONSTRAINT `krms_asset_rslvr_attr_fk1`
    FOREIGN KEY (`term_rslvr_id` )
    REFERENCES `krms_term_rslvr_t` (`term_rslvr_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_asset_rslvr_attr_fk2`
    FOREIGN KEY (`attr_defn_id` )
    REFERENCES `krms_attr_defn_t` (`attr_defn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_term_rslvr_input_spec_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_term_rslvr_input_spec_t` (
  `term_spec_id` VARCHAR(40) NOT NULL ,
  `term_rslvr_id` VARCHAR(40) NOT NULL ,
  INDEX `krms_input_asset_ti1` (`term_spec_id` ASC) ,
  INDEX `krms_input_asset_ti2` (`term_rslvr_id` ASC) ,
  PRIMARY KEY (`term_spec_id`, `term_rslvr_id`) ,
  CONSTRAINT `krms_input_asset_fk2`
    FOREIGN KEY (`term_spec_id` )
    REFERENCES `krms_term_spec_t` (`term_spec_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_input_asset_fk1`
    FOREIGN KEY (`term_rslvr_id` )
    REFERENCES `krms_term_rslvr_t` (`term_rslvr_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_cntxt_term_spec_prereq_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_cntxt_term_spec_prereq_t` (
  `cntxt_term_spec_prereq_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `term_spec_id` VARCHAR(40) NOT NULL ,
  PRIMARY KEY (`cntxt_term_spec_prereq_id`) ,
  INDEX `krms_cntxt_asset_prereq_ti1` (`cntxt_id` ASC) ,
  INDEX `krms_cntxt_asset_prereq_ti2` (`term_spec_id` ASC) ,
  CONSTRAINT `krms_cntxt_asset_prereq_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `krms_cntxt_t` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_cntxt_asset_prereq_fk2`
    FOREIGN KEY (`term_spec_id` )
    REFERENCES `krms_term_spec_t` (`term_spec_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_term_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_term_t` (
  `term_id` VARCHAR(40) NOT NULL ,
  `term_spec_id` VARCHAR(40) NOT NULL ,
  `ver_nbr` DECIMAL NOT NULL ,
  PRIMARY KEY (`term_id`) ,
  INDEX `krms_term_ti1` (`term_spec_id` ASC) ,
  CONSTRAINT `krms_term_t__fk1`
    FOREIGN KEY (`term_spec_id` )
    REFERENCES `krms_term_spec_t` (`term_spec_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_term_rslvr_parm_spec_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_term_rslvr_parm_spec_t` (
  `term_rslvr_parm_spec_id` VARCHAR(40) NOT NULL ,
  `term_rslvr_id` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(45) NOT NULL ,
  `ver_nbr` DECIMAL NOT NULL ,
  PRIMARY KEY (`term_rslvr_parm_spec_id`) ,
  INDEX `krms_term_reslv_parm_fk1` (`term_rslvr_id` ASC) ,
  CONSTRAINT `krms_term_reslv_parm_fk1`
    FOREIGN KEY (`term_rslvr_id` )
    REFERENCES `krms_term_rslvr_t` (`term_rslvr_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_term_parm_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_term_parm_t` (
  `term_parm_id` VARCHAR(40) NOT NULL ,
  `term_id` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(255) NOT NULL ,
  `val` VARCHAR(255) NULL ,
  `ver_nbr` DECIMAL NOT NULL ,
  PRIMARY KEY (`term_parm_id`) ,
  INDEX `krms_term_parm_ti1` (`term_id` ASC) ,
  CONSTRAINT `krms_term_parm_fk1`
    FOREIGN KEY (`term_id` )
    REFERENCES `krms_term_t` (`term_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_func_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_func_t` (
  `func_id` VARCHAR(40) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `desc_txt` VARCHAR(255) NULL ,
  `rtrn_typ` VARCHAR(255) NOT NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`func_id`) ,
  INDEX `krms_func_ti1` (`typ_id` ASC) ,
  CONSTRAINT `krms_func_fk1`
    FOREIGN KEY (`typ_id` )
    REFERENCES `krms_typ_t` (`typ_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_func_parm_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_func_parm_t` (
  `func_parm_id` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `desc_txt` VARCHAR(255) NULL ,
  `typ` VARCHAR(255) NOT NULL ,
  `func_id` VARCHAR(40) NOT NULL ,
  `seq_no` TINYINT NOT NULL ,
  PRIMARY KEY (`func_parm_id`) ,
  INDEX `krms_func_parm_ti1` (`func_id` ASC) ,
  CONSTRAINT `krms_func_parm_fk1`
    FOREIGN KEY (`func_id` )
    REFERENCES `krms_func_t` (`func_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_cntxt_vld_func_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_cntxt_vld_func_t` (
  `cntxt_vld_func_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `func_id` VARCHAR(40) NOT NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_vld_func_id`) ,
  INDEX `krms_cntxt_vld_func_ti1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_cntxt_vld_func_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `krms_cntxt_t` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_cntxt_vld_rule_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_cntxt_vld_rule_t` (
  `cntxt_vld_rule_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `rule_id` VARCHAR(40) NOT NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_vld_rule_id`) ,
  INDEX `krms_cntxt_vld_rule_ti1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_cntxt_vld_rule_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `krms_cntxt_t` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `krms_cntxt_vld_event_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_cntxt_vld_event_t` (
  `cntxt_vld_event_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `event_nm` VARCHAR(255) NOT NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_vld_event_id`) ,
  INDEX `krms_cntxt_vld_event_ti1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_cntxt_vld_event_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `krms_cntxt_t` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


--
--
-- Sequence tables
--
--


create table krms_typ_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_typ_s auto_increment = 1000;


create table krms_prop_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_prop_s auto_increment = 1000;


create table krms_rule_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_rule_s auto_increment = 1000;


create table krms_cntxt_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_cntxt_s auto_increment = 1000;


create table krms_agenda_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_agenda_s auto_increment = 1000;


create table krms_attr_defn_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_attr_defn_s auto_increment = 1000;


create table krms_typ_attr_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_typ_attr_s auto_increment = 1000;


create table krms_actn_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_actn_s auto_increment = 1000;


create table krms_actn_attr_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_actn_attr_s auto_increment = 1000;


create table krms_agenda_itm_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_agenda_itm_s auto_increment = 1000;


create table krms_rule_attr_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_rule_attr_s auto_increment = 1000;


create table krms_cntxt_attr_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_cntxt_attr_s auto_increment = 1000;


create table krms_cntxt_vld_actn_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_cntxt_vld_actn_s auto_increment = 1000;


create table krms_agenda_attr_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_agenda_attr_s auto_increment = 1000;


create table krms_cmpnd_prop_props_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_cmpnd_prop_props_s auto_increment = 1000;


create table krms_prop_parm_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_prop_parm_s auto_increment = 1000;


create table krms_term_spec_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_term_spec_s auto_increment = 1000;


create table krms_term_rslvr_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_term_rslvr_s auto_increment = 1000;


create table krms_term_rslvr_attr_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_term_rslvr_attr_s auto_increment = 1000;


create table krms_term_rslvr_input_spec_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_term_rslvr_input_spec_s auto_increment = 1000;


create table krms_cntxt_term_spec_prereq_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_cntxt_term_spec_prereq_s auto_increment = 1000;


create table krms_term_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_term_s auto_increment = 1000;


create table krms_term_rslvr_parm_spec_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_term_rslvr_parm_spec_s auto_increment = 1000;


create table krms_term_parm_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_term_parm_s auto_increment = 1000;


create table krms_func_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_func_s auto_increment = 1000;


create table krms_func_parm_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_func_parm_s auto_increment = 1000;


create table krms_cntxt_vld_func_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_cntxt_vld_func_s auto_increment = 1000;


create table krms_cntxt_vld_rule_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_cntxt_vld_rule_s auto_increment = 1000;


create table krms_cntxt_vld_event_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_cntxt_vld_event_s auto_increment = 1000;



-- 
-- mysql-2011-05-13.sql
-- 


drop table KRSB_SVC_DEF_T
;
drop table KRSB_FLT_SVC_DEF_T
;
drop table KRSB_SVC_DEF_S
;
drop table KRSB_FLT_SVC_DEF_S
;
CREATE TABLE KRSB_SVC_DSCRPTR_T (
  SVC_DSCRPTR_ID varchar(40) NOT NULL,
  DSCRPTR longtext NOT NULL,
  PRIMARY KEY (SVC_DSCRPTR_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
;
CREATE TABLE KRSB_SVC_DSCRPTR_S (
  ID bigint(19) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (ID)
) ENGINE=MyISAM AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COLLATE=utf8_bin
;
CREATE TABLE KRSB_SVC_DEF_T (
  SVC_DEF_ID varchar(40) NOT NULL,
  SVC_NM varchar(255) NOT NULL,
  SVC_URL varchar(500) NOT NULL,
  INSTN_ID varchar(255) NOT NULL,
  APPL_NMSPC varchar(255) NOT NULL,
  SRVR_IP varchar(40) NOT NULL,
  TYP_CD varchar(40) NOT NULL,
  SVC_VER varchar(40) NOT NULL,
  STAT_CD varchar(1) NOT NULL,
  SVC_DSCRPTR_ID varchar(40) NOT NULL,
  CHKSM varchar(30) NOT NULL,
  VER_NBR decimal(8,0) DEFAULT '0',
  PRIMARY KEY (SVC_DEF_ID),
  index krsb_svc_def_ti1 (instn_id),
  index krsb_svc_def_ti2 (svc_nm, stat_cd),
  index krsb_svc_def_ti3 (stat_cd),
  foreign key krsb_svc_def_fk1 (svc_dscrptr_id) references krsb_svc_dscrptr_t(svc_dscrptr_id) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
;
CREATE TABLE KRSB_SVC_DEF_S (
  ID bigint(19) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (ID)
) ENGINE=MyISAM AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COLLATE=utf8_bin
;


-- 
-- mysql-2011-06-06.sql
-- 


ALTER TABLE KREW_DOC_TYP_T CHANGE SVC_NMSPC APPL_ID VARCHAR(255)
;
ALTER TABLE KREW_RULE_ATTR_T CHANGE SVC_NMSPC APPL_ID VARCHAR(255)
;
ALTER TABLE KRSB_SVC_DEF_T CHANGE APPL_NMSPC APPL_ID VARCHAR(255)
;
ALTER TABLE KRSB_MSG_QUE_T CHANGE SVC_NMSPC APPL_ID VARCHAR(255)
;
ALTER TABLE KRNS_NMSPC_T CHANGE APPL_NMSPC_CD APPL_ID VARCHAR(255)
;
ALTER TABLE KRNS_PARM_T CHANGE APPL_NMSPC_CD APPL_ID VARCHAR(255)
;

RENAME TABLE KRNS_NMSPC_T TO KRCR_NMSPC_T
;
RENAME TABLE KRNS_PARM_TYP_T TO KRCR_PARM_TYP_T
;
RENAME TABLE KRNS_PARM_DTL_TYP_T TO KRCR_PARM_DTL_TYP_T
;
RENAME TABLE KRNS_PARM_T TO KRCR_PARM_T
;

RENAME TABLE KRNS_CAMPUS_T TO KRLC_CMP_T
;
RENAME TABLE KRNS_CMP_TYP_T TO KRLC_CMP_TYP_T
;
RENAME TABLE KR_COUNTRY_T TO KRLC_CNTRY_T
;
RENAME TABLE KR_STATE_T TO KRLC_ST_T
;
RENAME TABLE KR_POSTAL_CODE_T TO KRLC_PSTL_CD_T
;
RENAME TABLE KR_COUNTY_T TO KRLC_CNTY_T
;



-- 
-- mysql-2011-06-08.sql
-- 


-- make krms_rule_t.prop_id nullable
alter table krms_rule_t change column prop_id prop_id varchar(40) DEFAULT NULL AFTER typ_id;

-- add krms_actn_t.nmspc_cd
alter table krms_actn_t add column nmspc_cd varchar(40) NOT NULL after nm;

-- make krms_agenda_t default to 'Y'
alter table krms_agenda_t change column actv actv varchar(1) DEFAULT 'Y' AFTER typ_id;

-- make krms_prop_t.typ_id nullable 
alter table krms_prop_t change column typ_id typ_id varchar(40) DEFAULT NULL AFTER desc_txt;

-- change krms_rule_t.descr_txt to desc_t for consistency
alter table krms_rule_t change column descr_txt desc_txt varchar(4000) DEFAULT NULL AFTER nm;




-- 
-- mysql-2011-06-13-m6.sql
-- 


update krew_doc_typ_t set post_prcsr = 'org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor' where post_prcsr = 'org.kuali.rice.kns.workflow.postprocessor.KualiPostProcessor';

update krew_rule_attr_t set cls_nm = 'org.kuali.rice.krad.workflow.attribute.KualiXmlSearchableAttributeImpl' where cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXmlSearchableAttributeImpl';

update krew_rule_attr_t set cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXMLBooleanTranslatorSearchableAttributeImpl' where cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXMLBooleanTranslatorSearchableAttributeImpl';

update krew_rule_attr_t set cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXmlRuleAttributeImpl' where cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXmlRuleAttributeImpl';


-- 
-- mysql-2011-06-17-m6.sql
-- 


ALTER TABLE KREW_DOC_TYP_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_T CHANGE PARNT_ID PARNT_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_T CHANGE PREV_DOC_TYP_VER_NBR PREV_DOC_TYP_VER_NBR VARCHAR(40);
ALTER TABLE KREW_DOC_HDR_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_PLCY_RELN_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_APP_DOC_STAT_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_ATTR_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40);
ALTER TABLE KREW_RTE_NODE_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_PROC_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40);




-- 
-- mysql-2011-06-21.sql
-- 


alter table KRIM_PERM_TMPL_T change column NMSPC_CD NMSPC_CD varchar(40) not null;
alter table KRIM_PERM_TMPL_T change column NM NM varchar(100) not null;
alter table KRIM_PERM_TMPL_T add constraint krim_perm_tmpl_tc1 unique (NM, NMSPC_CD);

alter table KRIM_RSP_TMPL_T change column NMSPC_CD NMSPC_CD varchar(40) not null;
alter table KRIM_RSP_TMPL_T change column NM NM varchar(100) not null;
alter table KRIM_RSP_TMPL_T add constraint krim_rsp_tmpl_tc1 unique (NM, NMSPC_CD);


-- 
-- mysql-2011-06-23.sql
-- 


-- Sequence table
CREATE TABLE KRMS_CTGRY_S
(
    ID BIGINT(19) NOT NULL AUTO_INCREMENT
    , PRIMARY KEY (ID)
) ENGINE MyISAM;
ALTER TABLE KRMS_CTGRY_S AUTO_INCREMENT = 1;

CREATE TABLE KRMS_CTGRY_T
(
    CTGRY_ID VARCHAR(40) NOT NULL
      , NM VARCHAR(255) NOT NULL
      , NMSPC_CD VARCHAR(40) NOT NULL
      , VER_NBR DECIMAL(8) DEFAULT 0
    , PRIMARY KEY (CTGRY_ID)
    , UNIQUE INDEX krms_ctgry_tc0 (NM, NMSPC_CD)
)ENGINE = InnoDB;

CREATE TABLE KRMS_TERM_SPEC_CTGRY_T
(
  TERM_SPEC_ID VARCHAR(40) NOT NULL
      , CTGRY_ID VARCHAR(40) NOT NULL
  , PRIMARY KEY (TERM_SPEC_ID, CTGRY_ID)
  , constraint krms_term_spec_ctgry_fk1 foreign key (term_spec_id) references krms_term_spec_t (term_spec_id)
  , constraint krms_term_spec_ctgry_fk2 foreign key (ctgry_id) references krms_ctgry_t (ctgry_id)
);

CREATE TABLE KRMS_FUNC_CTGRY_T
(
  FUNC_ID VARCHAR(40) NOT NULL
  , CTGRY_ID VARCHAR(40) NOT NULL
  , PRIMARY KEY (FUNC_ID, CTGRY_ID)
  , constraint krms_func_ctgry_fk1 foreign key (func_id) references krms_func_t (func_id)
  , constraint krms_func_ctgry_fk2 foreign key (ctgry_id) references krms_ctgry_t (ctgry_id)
);


-- 
-- mysql-2011-06-29.sql
-- 


update KRIM_PERM_T t set NM='Use Document Operation Screen' where PERM_ID = '140';
update KRIM_PERM_T t set NM='Use Java Security Management Screen' where PERM_ID = '141';
update KRIM_PERM_T t set NM='Use Message Queue Screen' where PERM_ID = '142';
update KRIM_PERM_T t set NM='Use Service Registry Screen' where PERM_ID = '143';
update KRIM_PERM_T t set NM='Use Thread Pool Screen' where PERM_ID = '144';
update KRIM_PERM_T t set NM='Use Quartz Queue Screen' where PERM_ID = '145';
update KRIM_PERM_T t set NM='Ad Hoc Review RICE Document' where PERM_ID = '146';
update KRIM_PERM_T t set NM='Administer Routing RICE Document' where PERM_ID = '147';
update KRIM_PERM_T t set NM='Blanket Approve RICE Document'	where PERM_ID = '148';
update KRIM_PERM_T t set NM='Initiate RICE Document' where PERM_ID = '149';
update KRIM_PERM_T t set NM='Assign Role' where PERM_ID = '150';
update KRIM_PERM_T t set NM='Grant Permission' where PERM_ID = '151';
update KRIM_PERM_T t set NM='Grant Responsibility' where PERM_ID = '152';
update KRIM_PERM_T t set NM='Populate Group' where PERM_ID = '155';
update KRIM_PERM_T t set NM='Copy RICE Document' where PERM_ID = '156';
update KRIM_PERM_T t set NM='Inquire Into RICE Records' where PERM_ID = '161';
update KRIM_PERM_T t set NM='Look Up RICE Records' where PERM_ID = '162';
update KRIM_PERM_T t set NM='Maintain System Parameter' where PERM_ID = '163';
update KRIM_PERM_T t set NM='Modify Batch Job' where PERM_ID = '164';
update KRIM_PERM_T t set NM='Open RICE Document' where PERM_ID = '165';
update KRIM_PERM_T t set NM='Use all RICE Screen' where PERM_ID = '166';
update KRIM_PERM_T t set NM='Cancel Document' where PERM_ID = '167';
update KRIM_PERM_T t set NM='Route Document' where PERM_ID = '168';
update KRIM_PERM_T t set NM='Take Requested Apprive Action' where PERM_ID = '170';
update KRIM_PERM_T t set NM='Take Requested FYI Action' where PERM_ID = '172';
update KRIM_PERM_T t set NM='Take Requested Acknowledge Action' where PERM_ID = '173';
update KRIM_PERM_T t set NM='Log In Kuali Portal' where PERM_ID = '174';
update KRIM_PERM_T t set NM='Edit Kuali ENROUTE Document Node Name PreRoute' where PERM_ID = '180';
update KRIM_PERM_T t set NM='Edit Kuali ENROUTE Document Route Status Code R' where PERM_ID = '181';
update KRIM_PERM_T t set NM='Full Unmask Tax Identification Number Payee ACH Document'	where PERM_ID = '183';
update KRIM_PERM_T t set NM='Add Note / Attachment Kuali Document' where PERM_ID = '259';
update KRIM_PERM_T t set NM='View Note / Attachment Kuali Document' where PERM_ID = '261';
update KRIM_PERM_T t set NM='Delete Note / Attachment Kuali Document' where PERM_ID = '264';
update KRIM_PERM_T t set NM='Use Screen XML Ingester Screen' where PERM_ID = '265';
update KRIM_PERM_T t set NM='Administer Pessimistic Locking' where PERM_ID = '289';
update KRIM_PERM_T t set NM='Save RICE Document' where PERM_ID = '290';
update KRIM_PERM_T t set NM='View Other Action List' where PERM_ID = '298';
update KRIM_PERM_T t set NM='Unrestricted Document Search' where PERM_ID = '299';
update KRIM_PERM_T t set NM='Full Unmask Tax Identification Number Person Document'	where PERM_ID = '306';
update KRIM_PERM_T t set NM='Modify Entity' where PERM_ID = '307';
update KRIM_PERM_T t set NM='Send FYI Request Kuali Document' where PERM_ID = '332';
update KRIM_PERM_T t set NM='Send Acknowledge Request Kuali Document' where PERM_ID = '333';
update KRIM_PERM_T t set NM='Send Approve Request Kuali Document' where PERM_ID = '334';
update KRIM_PERM_T t set NM='Override Entity Privacy Preferences' where PERM_ID = '378';
update KRIM_PERM_T t set NM='Look Up Rule Template'	where PERM_ID = '701';
update KRIM_PERM_T t set NM='Look Up Stylesheet' where PERM_ID = '702';
update KRIM_PERM_T t set NM='Look Up eDocLite' where PERM_ID = '703';
update KRIM_PERM_T t set NM='Look Up Rule Attribute' where PERM_ID = '707';
update KRIM_PERM_T t set NM='Look Up Parameter Component' where PERM_ID = '719';
update KRIM_PERM_T t set NM='Look Up Namespace'	where PERM_ID = '720';
update KRIM_PERM_T t set NM='Look Up Parameter Type' where PERM_ID = '721';
update KRIM_PERM_T t set NM='Inquire Into Rule Template' where PERM_ID = '801';
update KRIM_PERM_T t set NM='Inquire Into Stylesheet' where PERM_ID = '802';
update KRIM_PERM_T t set NM='Inquire Into eDocLite' where PERM_ID = '803';
update KRIM_PERM_T t set NM='Inquire Into Rule Attribute' where PERM_ID = '807';
update KRIM_PERM_T t set NM='Inquire Into Pessimistic' where PERM_ID = '814';
update KRIM_PERM_T t set NM='Inquire Into Parameter Component' where PERM_ID = '819';
update KRIM_PERM_T t set NM='Inquire Into Namespace' where PERM_ID = '820';
update KRIM_PERM_T t set NM='Inquire Into Parameter Type' where PERM_ID = '821';
update KRIM_PERM_T t set NM='Populate Group KUALI Namespace' where PERM_ID = '833';
update KRIM_PERM_T t set NM='Assign Role KUALI Namespace' where PERM_ID = '834';
update KRIM_PERM_T t set NM='Grant Permission KUALI Namespace' where PERM_ID = '835';
update KRIM_PERM_T t set NM='Grant Responsibility KUALI Namespace' where PERM_ID = '836';
update KRIM_PERM_T t set NM='Use Configuration Viewer Screen' where PERM_ID = '840';
update KRIM_PERM_T t set NM='Add Message to Route Log'	where PERM_ID = '841';

alter table KRIM_PERM_T change column NMSPC_CD NMSPC_CD varchar(40) not null;
alter table KRIM_PERM_T change column NM NM varchar(100) not null;
alter table KRIM_PERM_T add constraint krim_perm_t_tc1 unique (NM, NMSPC_CD);

alter table KRIM_RSP_T change column NMSPC_CD NMSPC_CD varchar(40) not null;
alter table KRIM_RSP_T change column NM NM varchar(100) not null;
alter table KRIM_RSP_T add constraint krim_rsp_t_tc1 unique (NM, NMSPC_CD);


-- 
-- mysql-2011-07-07-m6.sql
-- 


ALTER TABLE KREW_DOC_NTE_T CHANGE DOC_NTE_ID DOC_NTE_ID VARCHAR(40);

ALTER TABLE KREW_ATT_T CHANGE ATTACHMENT_ID ATTACHMENT_ID VARCHAR(40);
ALTER TABLE KREW_ATT_T CHANGE NTE_ID NTE_ID VARCHAR(40);

ALTER TABLE KREW_ACTN_ITM_T CHANGE ACTN_ITM_ID ACTN_ITM_ID VARCHAR(40);
ALTER TABLE KREW_ACTN_ITM_T CHANGE ACTN_RQST_ID ACTN_RQST_ID VARCHAR(40);
ALTER TABLE KREW_ACTN_ITM_T CHANGE RSP_ID RSP_ID VARCHAR(40);

ALTER TABLE KREW_ACTN_TKN_T CHANGE ACTN_TKN_ID ACTN_TKN_ID VARCHAR(40);

ALTER TABLE KREW_ACTN_RQST_T CHANGE ACTN_RQST_ID ACTN_RQST_ID VARCHAR(40); 
ALTER TABLE KREW_ACTN_RQST_T CHANGE PARNT_ID PARNT_ID VARCHAR(40);
ALTER TABLE KREW_ACTN_RQST_T CHANGE RSP_ID RSP_ID VARCHAR(40);
ALTER TABLE KREW_ACTN_RQST_T CHANGE ACTN_TKN_ID ACTN_TKN_ID VARCHAR(40);
ALTER TABLE KREW_ACTN_RQST_T CHANGE RULE_ID RULE_ID VARCHAR(40);
ALTER TABLE KREW_ACTN_RQST_T CHANGE RTE_NODE_INSTN_ID RTE_NODE_INSTN_ID VARCHAR(40);

ALTER TABLE KREW_RULE_TMPL_T CHANGE RULE_TMPL_ID RULE_TMPL_ID VARCHAR(40);
ALTER TABLE KREW_RULE_TMPL_T CHANGE DLGN_RULE_TMPL_ID DLGN_RULE_TMPL_ID VARCHAR(40);

ALTER TABLE KREW_RULE_TMPL_ATTR_T CHANGE RULE_TMPL_ATTR_ID RULE_TMPL_ATTR_ID VARCHAR(40);
ALTER TABLE KREW_RULE_TMPL_ATTR_T CHANGE RULE_TMPL_ID RULE_TMPL_ID VARCHAR(40);
ALTER TABLE KREW_RULE_TMPL_ATTR_T CHANGE RULE_ATTR_ID RULE_ATTR_ID VARCHAR(40);

ALTER TABLE KREW_RULE_T CHANGE RULE_ID RULE_ID VARCHAR(40);
ALTER TABLE KREW_RULE_T CHANGE RULE_TMPL_ID RULE_TMPL_ID VARCHAR(40);

-- Have to deal w/ index & constraint changes here
ALTER TABLE KREW_RULE_T DROP FOREIGN KEY KREW_RULE_TR1;
ALTER TABLE KREW_RULE_T DROP KEY KREW_RULE_TR1;

ALTER TABLE KREW_RULE_T CHANGE RULE_EXPR_ID RULE_EXPR_ID VARCHAR(40);
-- add the key back, we'll re-add the foreign key after the target id column is converted
ALTER TABLE KREW_RULE_T ADD KEY krew_rule_ti1 (RULE_EXPR_ID);

ALTER TABLE KREW_RULE_T CHANGE PREV_RULE_VER_NBR PREV_RULE_VER_NBR VARCHAR(40);

ALTER TABLE KREW_DLGN_RSP_T CHANGE DLGN_RULE_ID DLGN_RULE_ID VARCHAR(40);
ALTER TABLE KREW_DLGN_RSP_T CHANGE RSP_ID RSP_ID VARCHAR(40);
ALTER TABLE KREW_DLGN_RSP_T CHANGE DLGN_RULE_BASE_VAL_ID DLGN_RULE_BASE_VAL_ID VARCHAR(40);

ALTER TABLE KREW_RULE_RSP_T CHANGE RULE_RSP_ID RULE_RSP_ID VARCHAR(40);
ALTER TABLE KREW_RULE_RSP_T CHANGE RSP_ID RSP_ID VARCHAR(40);
ALTER TABLE KREW_RULE_RSP_T CHANGE RULE_ID RULE_ID VARCHAR(40);

ALTER TABLE KREW_RULE_EXT_T CHANGE RULE_EXT_ID RULE_EXT_ID VARCHAR(40);
ALTER TABLE KREW_RULE_EXT_T CHANGE RULE_TMPL_ATTR_ID RULE_TMPL_ATTR_ID VARCHAR(40);
ALTER TABLE KREW_RULE_EXT_T CHANGE RULE_ID RULE_ID VARCHAR(40);

-- Have to deal w/ index & constraint changes before KREW_RTE_NODE_T.RTE_NODE_ID is converted
ALTER TABLE KREW_RTE_NODE_CFG_PARM_T DROP FOREIGN KEY EN_RTE_NODE_CFG_PARM_TR1;
ALTER TABLE KREW_RTE_NODE_CFG_PARM_T DROP KEY KREW_RTE_NODE_CFG_PARM_TI1;

ALTER TABLE KREW_RTE_NODE_T CHANGE RTE_NODE_ID RTE_NODE_ID VARCHAR(40);
ALTER TABLE KREW_RTE_NODE_T CHANGE BRCH_PROTO_ID BRCH_PROTO_ID VARCHAR(40);

ALTER TABLE KREW_RTE_NODE_INSTN_T CHANGE RTE_NODE_INSTN_ID RTE_NODE_INSTN_ID VARCHAR(40);
ALTER TABLE KREW_RTE_NODE_INSTN_T CHANGE RTE_NODE_ID RTE_NODE_ID VARCHAR(40);
ALTER TABLE KREW_RTE_NODE_INSTN_T CHANGE BRCH_ID BRCH_ID VARCHAR(40);
ALTER TABLE KREW_RTE_NODE_INSTN_T CHANGE PROC_RTE_NODE_INSTN_ID PROC_RTE_NODE_INSTN_ID VARCHAR(40);

ALTER TABLE KREW_RTE_NODE_INSTN_LNK_T CHANGE TO_RTE_NODE_INSTN_ID TO_RTE_NODE_INSTN_ID VARCHAR(40);
ALTER TABLE KREW_RTE_NODE_INSTN_LNK_T CHANGE FROM_RTE_NODE_INSTN_ID FROM_RTE_NODE_INSTN_ID VARCHAR(40);

ALTER TABLE KREW_RTE_BRCH_T CHANGE RTE_BRCH_ID RTE_BRCH_ID VARCHAR(40);
ALTER TABLE KREW_RTE_BRCH_T CHANGE PARNT_ID PARNT_ID VARCHAR(40);
ALTER TABLE KREW_RTE_BRCH_T CHANGE INIT_RTE_NODE_INSTN_ID INIT_RTE_NODE_INSTN_ID VARCHAR(40);
ALTER TABLE KREW_RTE_BRCH_T CHANGE JOIN_RTE_NODE_INSTN_ID JOIN_RTE_NODE_INSTN_ID VARCHAR(40);
ALTER TABLE KREW_RTE_BRCH_T CHANGE SPLT_RTE_NODE_INSTN_ID SPLT_RTE_NODE_INSTN_ID VARCHAR(40);

ALTER TABLE KREW_RTE_BRCH_ST_T CHANGE RTE_BRCH_ST_ID RTE_BRCH_ST_ID VARCHAR(40);
ALTER TABLE KREW_RTE_BRCH_ST_T CHANGE RTE_BRCH_ID RTE_BRCH_ID VARCHAR(40);

ALTER TABLE KREW_RTE_NODE_INSTN_ST_T CHANGE RTE_NODE_INSTN_ST_ID RTE_NODE_INSTN_ST_ID VARCHAR(40);
ALTER TABLE KREW_RTE_NODE_INSTN_ST_T CHANGE RTE_NODE_INSTN_ID RTE_NODE_INSTN_ID VARCHAR(40);

ALTER TABLE KREW_DOC_TYP_ATTR_T CHANGE DOC_TYP_ATTRIB_ID DOC_TYP_ATTRIB_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_ATTR_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_ATTR_T CHANGE RULE_ATTR_ID RULE_ATTR_ID VARCHAR(40);

ALTER TABLE KREW_RULE_ATTR_T CHANGE RULE_ATTR_ID RULE_ATTR_ID VARCHAR(40);

ALTER TABLE KREW_RULE_TMPL_OPTN_T CHANGE RULE_TMPL_OPTN_ID RULE_TMPL_OPTN_ID VARCHAR(40);
ALTER TABLE KREW_RULE_TMPL_OPTN_T CHANGE RULE_TMPL_ID RULE_TMPL_ID VARCHAR(40);

ALTER TABLE KREW_OUT_BOX_ITM_T CHANGE ACTN_ITM_ID ACTN_ITM_ID VARCHAR(40);
ALTER TABLE KREW_OUT_BOX_ITM_T CHANGE ACTN_RQST_ID ACTN_RQST_ID VARCHAR(40);
ALTER TABLE KREW_OUT_BOX_ITM_T CHANGE RSP_ID RSP_ID VARCHAR(40);

ALTER TABLE KREW_RTE_NODE_CFG_PARM_T CHANGE RTE_NODE_ID RTE_NODE_ID VARCHAR(40);
-- add the key back, re-add the foreign key
ALTER TABLE KREW_RTE_NODE_CFG_PARM_T ADD KEY krew_rte_node_cfg_parm_ti1 (RTE_NODE_ID);
ALTER TABLE KREW_RTE_NODE_CFG_PARM_T ADD CONSTRAINT krew_rte_node_cfg_parm_tr1 FOREIGN KEY krew_rte_node_cfg_parm_ti1 (RTE_NODE_ID) REFERENCES KREW_RTE_NODE_T (RTE_NODE_ID);

ALTER TABLE KREW_RTE_NODE_CFG_PARM_T CHANGE RTE_NODE_CFG_PARM_ID RTE_NODE_CFG_PARM_ID VARCHAR(40);


ALTER TABLE KREW_DOC_TYP_PROC_T CHANGE DOC_TYP_PROC_ID DOC_TYP_PROC_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_PROC_T CHANGE INIT_RTE_NODE_ID INIT_RTE_NODE_ID VARCHAR(40);

ALTER TABLE KREW_DOC_LNK_T CHANGE DOC_LNK_ID DOC_LNK_ID VARCHAR(40);

ALTER TABLE KREW_RTE_BRCH_PROTO_T CHANGE RTE_BRCH_PROTO_ID RTE_BRCH_PROTO_ID VARCHAR(40);

ALTER TABLE KREW_HLP_T CHANGE HLP_ID HLP_ID VARCHAR(40);

ALTER TABLE KREW_RULE_EXT_VAL_T CHANGE RULE_EXT_VAL_ID RULE_EXT_VAL_ID VARCHAR(40);
ALTER TABLE KREW_RULE_EXT_VAL_T CHANGE RULE_EXT_ID RULE_EXT_ID VARCHAR(40);

ALTER TABLE KREW_RULE_EXPR_T CHANGE RULE_EXPR_ID RULE_EXPR_ID VARCHAR(40);
ALTER TABLE KREW_RULE_T ADD CONSTRAINT krew_rule_fk1 FOREIGN KEY krew_rule_ti1 (RULE_EXPR_ID) REFERENCES KREW_RULE_EXPR_T (RULE_EXPR_ID);



ALTER TABLE KREW_APP_DOC_STAT_TRAN_T CHANGE APP_DOC_STAT_TRAN_ID APP_DOC_STAT_TRAN_ID VARCHAR(40);

ALTER TABLE KREW_DOC_HDR_EXT_DT_T CHANGE DOC_HDR_EXT_DT_ID DOC_HDR_EXT_DT_ID VARCHAR(40);

ALTER TABLE KREW_DOC_HDR_EXT_LONG_T CHANGE DOC_HDR_EXT_LONG_ID DOC_HDR_EXT_LONG_ID VARCHAR(40);

ALTER TABLE KREW_DOC_HDR_EXT_FLT_T CHANGE DOC_HDR_EXT_FLT_ID DOC_HDR_EXT_FLT_ID VARCHAR(40);

ALTER TABLE KREW_DOC_HDR_EXT_T CHANGE DOC_HDR_EXT_ID DOC_HDR_EXT_ID VARCHAR(40);




-- 
-- mysql-2011-07-11-m6.sql
-- 


ALTER TABLE KREW_RTE_NODE_LNK_T CHANGE FROM_RTE_NODE_ID FROM_RTE_NODE_ID VARCHAR(40);
ALTER TABLE KREW_RTE_NODE_LNK_T CHANGE TO_RTE_NODE_ID TO_RTE_NODE_ID VARCHAR(40);



-- 
-- mysql-2011-07-13.sql
-- 


-- KRCR_PARM_T
alter table KRCR_PARM_T CHANGE PARM_DTL_TYP_CD CMPNT_CD varchar(100);
alter table KRCR_PARM_T CHANGE TXT VAL varchar(4000);
alter table KRCR_PARM_T CHANGE CONS_CD EVAL_OPRTR_CD varchar(1);

-- KRCR_PARM_DTL_TYP_T to KRCR_CMPNT_T
RENAME TABLE KRCR_PARM_DTL_TYP_T TO KRCR_CMPNT_T;
ALTER TABLE KRCR_CMPNT_T CHANGE PARM_DTL_TYP_CD CMPNT_CD VARCHAR(100);

-- KRLC_CMP_TYP_T
alter table KRLC_CMP_TYP_T drop column DOBJ_MAINT_CD_ACTV_IND;


-- 
-- mysql-2011-07-22.sql
-- 


-- MySQL sql for KULRICE-5419:
alter table krms_cntxt_t add column desc_txt varchar(255) default null;
alter table krms_term_spec_t add column desc_txt varchar(255) default null;
alter table krms_term_t add column desc_txt varchar(255) default null;
alter table krms_attr_defn_t add column desc_txt varchar(255) default null;



-- 
-- mysql-2011-07-24-m7.sql
-- 


drop table krew_hlp_s;
drop table krew_hlp_t;


-- 
-- mysql-2011-07-25-m7.sql
-- 


drop table krew_ria_doc_t;
drop table krew_ria_doctype_map_id_s;
drop table krew_ria_doctype_map_t;

drop table krew_rmv_rplc_doc_t;
drop table krew_rmv_rplc_grp_t;
drop table krew_rmv_rplc_rule_t;


-- 
-- mysql-2011-07-28-m7.sql
-- 


ALTER TABLE KREW_INIT_RTE_NODE_INSTN_T CHANGE RTE_NODE_INSTN_ID RTE_NODE_INSTN_ID VARCHAR(40);


-- 
-- mysql-2011-07-29-m7.sql
-- 


update KRIM_PERM_T t set NM='Take Requested Approve Action' where PERM_ID = '170';


-- 
-- mysql-2011-07-29.sql
-- 


--
-- make combo of nm & nmspc_cd unique in all applicable KRMS tables
-- and drop nmspc_cd where it doesn't make sense
--

--
-- break direct fk to krms_cntxt_t from krms_term_rslvr_t & krms_term_spec_t
--

-- remove cntxt_id from krms_term_rslvr_t, fix unique constraint
alter table krms_term_rslvr_t drop index krms_term_rslvr_tc1;
alter table krms_term_rslvr_t add constraint krms_term_rslvr_tc1 unique (nm, nmspc_cd);
alter table krms_term_rslvr_t drop foreign key krms_term_rslvr_fk2;
alter table krms_term_rslvr_t drop column cntxt_id;

-- remove fk from krms_term_spec_t to krms_cntxt_t
alter table krms_term_spec_t add column nmspc_cd varchar(40) not null;
alter table krms_term_spec_t DROP FOREIGN KEY krms_asset_fk1;
alter table krms_term_spec_t drop key krms_asset_ti1;
alter table krms_term_spec_t drop index krms_asset_tc1;
alter table krms_term_spec_t add constraint krms_term_spec_tc1 unique (nm, nmspc_cd);
alter table krms_term_spec_t drop column cntxt_id;

--
-- refactor krms_cntxt_term_spec_prereq_t to be a valid term specs table instead
--
-- rename krms_cntxt_term_spec_prereq_t to krms_cntxt_vld_term_spec_t
-- and add prereq column
alter table krms_cntxt_term_spec_prereq_t DROP FOREIGN KEY krms_cntxt_asset_prereq_fk1;
alter table krms_cntxt_term_spec_prereq_t drop index krms_cntxt_asset_prereq_ti1;
alter table krms_cntxt_term_spec_prereq_t DROP FOREIGN KEY krms_cntxt_asset_prereq_fk2;
alter table krms_cntxt_term_spec_prereq_t drop index krms_cntxt_asset_prereq_ti2;
rename table krms_cntxt_term_spec_prereq_t to krms_cntxt_vld_term_spec_t;
alter table krms_cntxt_vld_term_spec_t add column prereq varchar(1) default 'n';
alter table krms_cntxt_vld_term_spec_t add constraint krms_cntxt_vld_term_spec_ti1 foreign key (cntxt_id) references krms_cntxt_t(cntxt_id);
alter table krms_cntxt_vld_term_spec_t add constraint krms_cntxt_vld_term_spec_ti2 foreign key (term_spec_id) references krms_term_spec_t(term_spec_id);

--
-- set up some missing unique constraints
--
-- wow, Oracle and MySQL support the same syntax here
alter table krms_cntxt_t add constraint krms_cntxt_tc1 unique (nm, nmspc_cd);
alter table krms_func_t add constraint krms_func_tc1 unique (nm, nmspc_cd);

-- drop namespace code from krms_agenda_t
alter table krms_agenda_t drop column nmspc_cd;
alter table krms_agenda_t add constraint krms_agenda_tc1 unique (nm, cntxt_id);

alter table krms_typ_t add constraint krms_typ_tc1 unique (nm, nmspc_cd);
alter table krms_attr_defn_t add constraint krms_attr_defn_tc1 unique (nm, nmspc_cd);
alter table krms_rule_t add constraint krms_rule_tc1 unique (nm, nmspc_cd);

--
-- clean up some crufty index and constraint names
--

alter table krms_term_rslvr_attr_t drop foreign key krms_asset_rslvr_attr_fk1;
alter table krms_term_rslvr_attr_t drop foreign key krms_asset_rslvr_attr_fk2;
alter table krms_term_rslvr_attr_t drop index krms_asset_rslvr_attr_ti1;
create index krms_asset_rslvr_attr_ti1 on krms_term_rslvr_attr_t (term_rslvr_id);
alter table krms_term_rslvr_attr_t drop index krms_asset_rslvr_attr_ti2;
create index krms_term_rslvr_attr_ti2 on krms_term_rslvr_attr_t (attr_defn_id);
alter table krms_term_rslvr_attr_t add constraint krms_term_rslvr_attr_fk1 foreign key (term_rslvr_id) references krms_term_rslvr_t (term_rslvr_id);
alter table krms_term_rslvr_attr_t add constraint krms_term_rslvr_attr_fk2 foreign key (attr_defn_id) references krms_attr_defn_t (attr_defn_id);


-- 
-- mysql-2011-08-11.sql
-- 



-- DROP TABLE IF EXISTS krew_ppl_flw_attr_t ;
-- DROP TABLE IF EXISTS krew_ppl_flw_mbr_t ;
-- DROP TABLE IF EXISTS krew_typ_attr_t ;
-- DROP TABLE IF EXISTS krew_attr_defn_t ;
-- DROP TABLE IF EXISTS krew_ppl_flw_t ;
-- DROP TABLE IF EXISTS krew_typ_t ;
-- DROP TABLE IF EXISTS krew_ppl_flw_mbr_s ;
-- DROP TABLE IF EXISTS krew_ppl_flw_attr_s ;
-- DROP TABLE IF EXISTS krew_ppl_flw_s ;
-- DROP TABLE IF EXISTS krew_attr_defn_s ;
-- DROP TABLE IF EXISTS krew_typ_attr_s ;
-- DROP TABLE IF EXISTS krew_typ_s ;


-- -----------------------------------------------------
-- Table krew_typ_t
-- -----------------------------------------------------

CREATE  TABLE krew_typ_t (
  typ_id VARCHAR(40) NOT NULL ,
  nm VARCHAR(100) NOT NULL ,
  nmspc_cd VARCHAR(40) NOT NULL ,
  srvc_nm VARCHAR(200) NULL ,
  actv VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (typ_id) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX krew_typ_tc1 ON krew_typ_t (nm, nmspc_cd) ;


-- -----------------------------------------------------
-- Table krew_ppl_flw_t
-- -----------------------------------------------------

CREATE  TABLE krew_ppl_flw_t (
  ppl_flw_id VARCHAR(40) NOT NULL ,
  nm VARCHAR(100) NOT NULL ,
  nmspc_cd VARCHAR(40) NOT NULL ,
  typ_id VARCHAR(40) NOT NULL ,
  actv VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL NOT NULL DEFAULT 0 ,
  desc_txt VARCHAR(4000) NULL ,
  PRIMARY KEY (ppl_flw_id) ,
  CONSTRAINT krew_ppl_flw_fk1
    FOREIGN KEY (typ_id )
    REFERENCES krew_typ_t (typ_id ))
ENGINE = InnoDB;

CREATE UNIQUE INDEX krew_ppl_flw_tc1 ON krew_ppl_flw_t (nm, nmspc_cd) ;

CREATE INDEX krew_ppl_flw_fk1 ON krew_ppl_flw_t (typ_id) ;


-- -----------------------------------------------------
-- Table krew_attr_defn_t
-- -----------------------------------------------------

CREATE  TABLE krew_attr_defn_t (
  attr_defn_id VARCHAR(40) NOT NULL ,
  nm VARCHAR(100) NOT NULL ,
  nmspc_cd VARCHAR(40) NOT NULL ,
  lbl VARCHAR(40) NULL ,
  actv VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  cmpnt_nm VARCHAR(100) NULL ,
  ver_nbr DECIMAL NOT NULL DEFAULT 0 ,
  desc_txt VARCHAR(40) NULL ,
  PRIMARY KEY (attr_defn_id) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX krew_attr_defn_tc1 ON krew_attr_defn_t (nm, nmspc_cd) ;


-- -----------------------------------------------------
-- Table krew_typ_attr_t
-- -----------------------------------------------------

CREATE  TABLE krew_typ_attr_t (
  typ_attr_id VARCHAR(40) NOT NULL ,
  seq_no DECIMAL(5,0) NOT NULL ,
  typ_id VARCHAR(40) NOT NULL ,
  attr_defn_id VARCHAR(255) NOT NULL ,
  actv VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (typ_attr_id) ,
  CONSTRAINT krew_typ_attr_fk1
    FOREIGN KEY (attr_defn_id )
    REFERENCES krew_attr_defn_t (attr_defn_id ),
  CONSTRAINT krew_typ_attr_fk2
    FOREIGN KEY (typ_id )
    REFERENCES krew_typ_t (typ_id ))
ENGINE = InnoDB;

CREATE INDEX krew_typ_attr_ti1 ON krew_typ_attr_t (attr_defn_id) ;

CREATE INDEX krew_typ_attr_ti2 ON krew_typ_attr_t (typ_id) ;

CREATE UNIQUE INDEX krew_typ_attr_tc1 ON krew_typ_attr_t (typ_id, attr_defn_id) ;


-- -----------------------------------------------------
-- Table krew_ppl_flw_mbr_t
-- -----------------------------------------------------

CREATE  TABLE krew_ppl_flw_mbr_t (
  ppl_flw_mbr_id VARCHAR(40) NOT NULL ,
  ppl_flw_id VARCHAR(40) NOT NULL ,
  mbr_typ_cd VARCHAR(1) NOT NULL ,
  mbr_id VARCHAR(40) NOT NULL ,
  prio DECIMAL(8,0) NULL ,
  dlgt_frm_id VARCHAR(40) NULL ,
  ver_nbr DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (ppl_flw_mbr_id) ,
  CONSTRAINT krew_ppl_flw_mbr_fk1
    FOREIGN KEY (ppl_flw_id )
    REFERENCES krew_ppl_flw_t (ppl_flw_id ),
  CONSTRAINT krew_ppl_flw_mbr_fk2
    FOREIGN KEY (dlgt_frm_id )
    REFERENCES krew_ppl_flw_mbr_t (ppl_flw_mbr_id ))
ENGINE = InnoDB;

CREATE INDEX krew_ppl_flw_mbr_ti1 ON krew_ppl_flw_mbr_t (ppl_flw_id) ;

CREATE INDEX krew_ppl_flw_mbr_ti2 ON krew_ppl_flw_mbr_t (ppl_flw_id, prio) ;

CREATE UNIQUE INDEX krew_ppl_flw_mbr_tc1 ON krew_ppl_flw_mbr_t (ppl_flw_id, mbr_typ_cd, mbr_id, dlgt_frm_id) ;

CREATE INDEX krew_ppl_flw_mbr_fk2 ON krew_ppl_flw_mbr_t (dlgt_frm_id) ;


-- -----------------------------------------------------
-- Table krew_ppl_flw_attr_t
-- -----------------------------------------------------

CREATE  TABLE krew_ppl_flw_attr_t (
  ppl_flw_attr_id VARCHAR(40) NOT NULL ,
  ppl_flw_id VARCHAR(40) NOT NULL ,
  attr_defn_id VARCHAR(40) NOT NULL ,
  attr_val VARCHAR(400) NULL ,
  ver_nbr DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (ppl_flw_attr_id) ,
  CONSTRAINT krew_ppl_flw_attr_fk1
    FOREIGN KEY (ppl_flw_id )
    REFERENCES krew_ppl_flw_t (ppl_flw_id ),
  CONSTRAINT krew_ppl_flw_attr_fk2
    FOREIGN KEY (attr_defn_id )
    REFERENCES krew_attr_defn_t (attr_defn_id ))
ENGINE = InnoDB;

CREATE INDEX krew_ppl_flw_attr_ti1 ON krew_ppl_flw_attr_t (ppl_flw_id) ;

CREATE INDEX krew_ppl_flw_attr_ti2 ON krew_ppl_flw_attr_t (attr_defn_id) ;


-- -----------------------------------------------------
-- Table krew_typ_s
-- -----------------------------------------------------

CREATE  TABLE krew_typ_s (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;


-- -----------------------------------------------------
-- Table krew_typ_attr_s
-- -----------------------------------------------------

CREATE  TABLE krew_typ_attr_s (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;


-- -----------------------------------------------------
-- Table krew_attr_defn_s
-- -----------------------------------------------------

CREATE  TABLE krew_attr_defn_s (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;


-- -----------------------------------------------------
-- Table krew_ppl_flw_s
-- -----------------------------------------------------

CREATE  TABLE krew_ppl_flw_s (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;


-- -----------------------------------------------------
-- Table krew_ppl_flw_attr_s
-- -----------------------------------------------------

CREATE  TABLE krew_ppl_flw_attr_s (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;


-- -----------------------------------------------------
-- Table krew_ppl_flw_mbr_s
-- -----------------------------------------------------

CREATE  TABLE krew_ppl_flw_mbr_s (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;




-- 
-- mysql-2011-08-17.sql
-- 


delete from krcr_parm_t
where nmspc_cd = 'KR-NS'
and cmpnt_cd = 'All'
and parm_nm in ('STRING_TO_DATE_FORMATS', 'STRING_TO_TIMESTAMP_FORMATS', 'TIMESTAMP_TO_STRING_FORMAT_FOR_USER_INTERFACE', 'DATE_TO_STRING_FORMAT_FOR_FILE_NAME', 'TIMESTAMP_TO_STRING_FORMAT_FOR_FILE_NAME')
;


-- 
-- mysql-2011-08-29.sql
-- 


delete from krcr_parm_t where PARM_NM = 'CACHING_IND'
;


-- 
-- mysql-2011-09-07.sql
-- 


-- KULRICE-5360 rename KIM entity fields
ALTER TABLE KRIM_ENTITY_NM_T CHANGE TITLE_NM PREFIX_NM VARCHAR(20);

ALTER TABLE KRIM_ENTITY_BIO_T  CHANGE BIRTH_STATE_CD BIRTH_STATE_PVC_CD VARCHAR(2);

ALTER TABLE KRIM_ENTITY_ADDR_T CHANGE POSTAL_STATE_CD STATE_PVC_CD VARCHAR(2);
ALTER TABLE KRIM_ENTITY_ADDR_T CHANGE CITY_NM CITY VARCHAR(30);

ALTER TABLE KRIM_PND_NM_MT CHANGE TITLE_NM PREFIX_NM VARCHAR(20);

ALTER TABLE KRIM_PND_ADDR_MT CHANGE POSTAL_STATE_CD STATE_PVC_CD VARCHAR(2);

ALTER TABLE KRIM_PND_ADDR_MT CHANGE CITY_NM CITY VARCHAR(30);




-- 
-- mysql-2011-09-15.sql
-- 


delete from KREW_USR_OPTN_T where PRSN_OPTN_ID like 'DocSearch%';




-- 
-- mysql-2011-09-16.sql
-- 


-- KULRICE-5360 add KIM entity fields
alter table KRIM_ENTITY_NM_T add column TITLE_NM VARCHAR(20);
alter table KRIM_ENTITY_NM_T add column NOTE_MSG VARCHAR(1024);
alter table KRIM_ENTITY_NM_T add column NM_CHNG_DT DATETIME;

alter table KRIM_ENTITY_ADDR_T add column ATTN_LINE VARCHAR(45);
alter table KRIM_ENTITY_ADDR_T add column ADDR_FMT VARCHAR(256);
alter table KRIM_ENTITY_ADDR_T add column MOD_DT DATETIME;
alter table KRIM_ENTITY_ADDR_T add column VALID_DT DATETIME;
alter table KRIM_ENTITY_ADDR_T add column VALID_IND VARCHAR(1);
alter table KRIM_ENTITY_ADDR_T add column NOTE_MSG VARCHAR(1024);

alter table KRIM_ENTITY_BIO_T add column NOTE_MSG VARCHAR(1024);
alter table KRIM_ENTITY_BIO_T add column GNDR_CHG_CD VARCHAR(20);

alter table KRIM_PND_NM_MT add column TITLE_NM VARCHAR(20);
alter table KRIM_PND_NM_MT add column NOTE_MSG VARCHAR(1024);
alter table KRIM_PND_NM_MT add column NM_CHNG_DT DATETIME;

alter table KRIM_PND_ADDR_MT add column ATTN_LINE VARCHAR(45);
alter table KRIM_PND_ADDR_MT add column ADDR_FMT VARCHAR(256);
alter table KRIM_PND_ADDR_MT add column MOD_DT DATETIME;
alter table KRIM_PND_ADDR_MT add column VALID_DT DATETIME;
alter table KRIM_PND_ADDR_MT add column VALID_IND VARCHAR(1);
alter table KRIM_PND_ADDR_MT add column NOTE_MSG VARCHAR(1024);




-- 
-- mysql-2011-09-18.sql
-- 


alter table KREW_PPL_FLW_T CHANGE TYP_ID TYP_ID varchar(40) NULL;

alter table KREW_PPL_FLW_MBR_T DROP FOREIGN KEY krew_ppl_flw_mbr_fk2;
alter table KREW_PPL_FLW_MBR_T DROP KEY krew_ppl_flw_mbr_fk2;
alter table KREW_PPL_FLW_MBR_T DROP COLUMN dlgt_frm_id;

drop INDEX krew_ppl_flw_mbr_tc1 ON krew_ppl_flw_mbr_t;

-- drop INDEX krew_ppl_flw_mbr_fk2 ON krew_ppl_flw_mbr_t;

-- alter table krew_ppl_flw_mbr_t drop foreign key krew_ppl_flw_mbr_fk2;

-- -----------------------------------------------------
-- Table krew_ppl_flw_dlgt_t
-- -----------------------------------------------------

CREATE  TABLE krew_ppl_flw_dlgt_t (
  ppl_flw_dlgt_id VARCHAR(40) NOT NULL ,
  ppl_flw_mbr_id VARCHAR(40) NOT NULL ,
  mbr_id VARCHAR(40) NOT NULL ,
  mbr_typ_cd VARCHAR(1) NOT NULL ,
  dlgn_typ_cd VARCHAR(1) NOT NULL ,
  ver_nbr DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (ppl_flw_dlgt_id) ,
  CONSTRAINT krew_ppl_flw_dlgt_fk1
    FOREIGN KEY (ppl_flw_mbr_id )
    REFERENCES krew_ppl_flw_mbr_t (ppl_flw_mbr_id ))
ENGINE = InnoDB;

CREATE INDEX krew_ppl_flw_dlgt_ti1 ON krew_ppl_flw_dlgt_t (ppl_flw_mbr_id) ;

-- -----------------------------------------------------
-- Table krew_ppl_flw_dlgt_s
-- -----------------------------------------------------

CREATE  TABLE krew_ppl_flw_dlgt_s (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;




-- 
-- mysql-2011-09-26b.sql
-- 


-- KIM permissions
insert into krcr_nmspc_t
(nmspc_cd, nm, actv_ind, appl_id, ver_nbr, obj_id)
values ('KR-RULE','Kuali Rules','Y','RICE',1,uuid())
;

insert into krim_perm_tmpl_t
(perm_tmpl_id, nm, nmspc_cd, desc_txt, kim_typ_id, actv_ind, ver_nbr, obj_id)
values ((select perm_tmpl_id from
        (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000)
        as tmptable),
        'KRMS Agenda Permission','KR-RULE','View/Maintain Agenda',
        (select kim_typ_id from krim_typ_t where nm = 'Namespace' and nmspc_cd = 'KR-NS'),
        'Y',1,uuid())
;

insert into krim_perm_t
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select perm_id from
        (select (max(cast(perm_id as decimal)) + 1) as perm_id from krim_perm_t where perm_id is not NULL and cast(perm_id as decimal) < 10000)
        as tmptable),
        (select perm_tmpl_id from krim_perm_tmpl_t where nm = 'KRMS Agenda Permission' and nmspc_cd = 'KR-RULE'),
        'KR-RULE','Maintain KRMS Agenda','Allows creation and modification of agendas via the agenda editor','Y',1,uuid())
;

insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select attr_data_id from
        (select (max(cast(attr_data_id as decimal)) + 1) as attr_data_id from krim_perm_attr_data_t where attr_data_id is not NULL and cast(attr_data_id as decimal) < 10000)
        as tmptable),
        (select perm_id from krim_perm_t where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KR-RULE'),
        (select kim_typ_id from krim_typ_t where nm = 'Namespace' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'namespaceCode'),
        'KRMS_TEST',1,uuid())
;

-- KIM roles
insert into krim_role_t
(role_id, role_nm, nmspc_cd, desc_txt, kim_typ_id, actv_ind, last_updt_dt, obj_id)
values ((select role_id from
        (select (max(cast(role_id as decimal)) + 1) as role_id from krim_role_t where role_id is not NULL and cast(role_id as decimal) < 10000)
        as tmptable),
        'Kuali Rules Management System Administrator',
        'KR-RULE',
        'This role maintains KRMS agendas and rules.',
        (select kim_typ_id from krim_typ_t where nm = 'Default' and nmspc_cd = 'KUALI'),
        'Y', curdate(), uuid())
;

insert into krim_role_mbr_t
(role_mbr_id, role_id, mbr_id, mbr_typ_cd, last_updt_dt, ver_nbr, obj_id)
values ((select role_mbr_id from
        (select (max(cast(role_mbr_id as decimal)) + 1) as role_mbr_id from krim_role_mbr_t where role_mbr_id is not NULL and cast(role_mbr_id as decimal) < 10000)
        as tmptable),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select prncpl_id from krim_prncpl_t where prncpl_nm = 'admin'),
        'P', curdate(), 1, uuid())
;

insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select role_perm_id from
        (select (max(cast(role_perm_id as decimal)) + 1) as role_perm_id from krim_role_perm_t where role_perm_id is not NULL and cast(role_perm_id as decimal) < 10000)
        as tmptable),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select perm_id from krim_perm_t where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KR-RULE'),
        'Y', 1, uuid())
;


-- 
-- mysql-2011-09-26.sql
-- 


ALTER TABLE KREW_DOC_TYP_T DROP COLUMN CSTM_ACTN_LIST_ATTRIB_CLS_NM;
ALTER TABLE KREW_DOC_TYP_T DROP COLUMN CSTM_ACTN_EMAIL_ATTRIB_CLS_NM;
ALTER TABLE KREW_DOC_TYP_T DROP COLUMN CSTM_DOC_NTE_ATTRIB_CLS_NM;


-- 
-- mysql-2011-09-27.sql
-- 


-- Notification PeopleFlowActionType

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('1000', 'notificationPeopleFlowActionType', 'KRMS', 'notificationPeopleFlowActionTypeService', 'Y', 1)
;

-- Approval PeopleFlowActionType

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('1001', 'approvalPeopleFlowActionType', 'KRMS_TEST', 'approvalPeopleFlowActionTypeService', 'Y', 1)
;



-- 
-- mysql-2011-09-30.sql
-- 



-- -----------------------------------------------------
-- Table `krms_cntxt_vld_agenda_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_cntxt_vld_agenda_t` (
  `cntxt_vld_agenda_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `agenda_typ_id` VARCHAR(40) NOT NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_vld_agenda_id`) ,
  INDEX `krms_cntxt_vld_agenda_ti1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_cntxt_vld_agenda_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `krms_cntxt_t` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

create table krms_cntxt_vld_agenda_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_cntxt_vld_agenda_s auto_increment = 1000;


-- 
-- mysql-2011-10-03.sql
-- 


alter table KREW_PPL_FLW_MBR_T add column ACTN_RQST_PLCY_CD VARCHAR(1);
alter table KREW_PPL_FLW_MBR_T add column RSP_ID VARCHAR(40) NOT NULL;

alter table KREW_PPL_FLW_DLGT_T add column ACTN_RQST_PLCY_CD VARCHAR(1);
alter table KREW_PPL_FLW_DLGT_T add column RSP_ID VARCHAR(40) NOT NULL;



-- 
-- mysql-2011-10-21.sql
-- 


-- KULRICE-5348
alter table KREW_RULE_T change PREV_RULE_VER_NBR PREV_VER_RULE_ID varchar(40);

--KULRICE-4589
UPDATE KRCR_PARM_T
SET PARM_NM='NOTIFY_GROUPS',
    PARM_DESC_TXT='Defines a group name (in the format "namespace:name") which contains members who should never receive "notifications" action requests from KEW.',
    EVAL_OPRTR_CD='D'
WHERE NMSPC_CD = 'KR-WKFLW'
  AND CMPNT_CD = 'Notification'
  AND PARM_NM = 'NOTIFY_EXCLUDED_USERS_IND';


-- 
-- mysql-2011-10-23.sql
-- 


alter table krcr_cmpnt_t add column cmpnt_set_id varchar(40);

create table krcr_cmpnt_set_t (
  cmpnt_set_id varchar(40) not null,
  last_updt_ts datetime not null,
  chksm varchar(40) not null,
  ver_nbr decimal not null default 0,
  primary key (cmpnt_set_id))
ENGINE = InnoDB;


-- 
-- mysql-2011-10-24.sql
-- 


alter table krcr_cmpnt_t drop column cmpnt_set_id;

create table krcr_drvd_cmpnt_t (
  nmspc_cd varchar(20) not null,
  cmpnt_cd varchar(100) not null,
  nm varchar(255),
  cmpnt_set_id varchar(40) not null,
  primary key (nmspc_cd, cmpnt_cd))
ENGINE = InnoDB;



-- 
-- mysql-2011-10-25.sql
-- 


update KREW_RULE_ATTR_T set RULE_ATTR_TYP_CD='DocumentSecurityAttribute' where RULE_ATTR_TYP_CD='DocumentSearchSecurityFilterAttribute';

update KRCR_PARM_T set CMPNT_CD='DocumentSearch' where CMPNT_CD='DocSearchCriteriaDTO';
insert into KRCR_CMPNT_T (NMSPC_CD, CMPNT_CD, NM, ACTV_IND, OBJ_ID, VER_NBR)
values ('KR-WKFLW', 'DocumentSearch', 'Document Search', 'Y', uuid(), 1);



-- 
-- mysql-2011-10-26.sql
-- 


insert into KRCR_CMPNT_T (NMSPC_CD, CMPNT_CD, NM, ACTV_IND, OBJ_ID, VER_NBR)
VALUES ('KR-WKFLW', 'Rule', 'Rule', 'Y', uuid(), 1);
update KRCR_CMPNT_T set cmpnt_cd='EDocLite' where cmpnt_cd like 'EDocLite%';


-- 
-- mysql-2011-10-27.sql
-- 


-- create a KIM permission for the Cache Administrator screen/controller

insert into krim_perm_t
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select perm_id from
          (select (max(cast(perm_id as decimal)) + 1) as perm_id from krim_perm_t where perm_id is not NULL and cast(perm_id as decimal) < 10000)
         as tmptable),
        (select perm_tmpl_id from krim_perm_tmpl_t where nm = 'Use Screen' and nmspc_cd = 'KR-NS'),
        'KR-SYS','Use Cache Adminstration Screen','Allows use of the cache administration screen','Y',1,uuid());

insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select attr_data_id from
          (select (max(cast(attr_data_id as decimal)) + 1) as attr_data_id from krim_perm_attr_data_t where attr_data_id is not NULL and cast(attr_data_id as decimal) < 10000)
         as tmptable),
        (select perm_id from krim_perm_t where nm = 'Use Cache Adminstration Screen' and nmspc_cd = 'KR-SYS'),
        (select kim_typ_id from krim_typ_t where nm = 'Namespace or Action' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'actionClass'),
        'org.kuali.rice.core.web.cache.CacheAdminController',1,uuid());

insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select role_perm_id from
          (select (max(cast(role_perm_id as decimal)) + 1) as role_perm_id from krim_role_perm_t where role_perm_id is not NULL and cast(role_perm_id as decimal) < 10000)
         as tmptable),
        (select role_id from krim_role_t where role_nm = 'Technical Administrator' and nmspc_cd = 'KR-SYS'),
        (select perm_id from krim_perm_t where nm = 'Use Cache Adminstration Screen' and nmspc_cd = 'KR-SYS'),
        'Y', 1, uuid());



-- 
-- mysql-2011-11-03.sql
-- 


-- Make typ_id column optional where appropriate

ALTER TABLE krms_rule_t MODIFY typ_id varchar(40) default null;
ALTER TABLE krms_agenda_t MODIFY typ_id varchar(40) default null;
ALTER TABLE krms_cntxt_t MODIFY typ_id varchar(40) default null;



-- 
-- mysql-2011-11-14.sql
-- 


-- Drop foreign key constraint on krms_prop_t table
alter table KRMS_PROP_T drop foreign key krms_prop_fk1;




-- 
-- mysql-2011-11-23.sql
-- 


-- give PeopleFlows friendlier names

update krms_typ_t set nm='Notify PeopleFlow' where typ_id = '1000';
update krms_typ_t set nm='Route to PeopleFlow' where typ_id = '1001';

-- remove constraint that is preventing compound props from persisting

alter table krms_cmpnd_prop_props_t modify seq_no decimal(5,0) default null;



-- 
-- mysql-2011-11-28.sql
-- 


update krim_perm_t
   set nmspc_cd = 'KRMS_TEST'
 where nm = 'Maintain KRMS Agenda'
   and nmspc_cd = 'KR-RULE'
;

delete from krim_perm_attr_data_t
 where perm_id = (select perm_id from krim_perm_t where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KRMS_TEST')
;


-- 
-- mysql-2011-11-29.sql
-- 


alter table KREW_RTE_NODE_T modify ACTVN_TYP varchar(1);


-- 
-- mysql-2011-12-07.sql
-- 



-- correct fields in krms test data
update krms_prop_t set cmpnd_op_cd = '&' where cmpnd_op_cd = 'a';
update krms_cmpnd_prop_props_t set seq_no = '2' where prop_id = 'P421C';
update krms_cmpnd_prop_props_t set seq_no = '3' where prop_id = 'P421D';
update krms_cmpnd_prop_props_t set seq_no = '3' where prop_id = 'P502C';

-- move seq_no column from krms_cmpnd_prop_props_t pivot table to krms_prop_t table.
alter table krms_prop_t add column cmpnd_seq_no decimal(5,0) default null;

update krms_prop_t, krms_cmpnd_prop_props_t set krms_prop_t.cmpnd_seq_no = krms_cmpnd_prop_props_t.seq_no
where krms_prop_t.prop_id = krms_cmpnd_prop_props_t.prop_id;

alter table krms_cmpnd_prop_props_t drop seq_no;



-- 
-- mysql-2011-12-21.sql
-- 


update KRCR_NMSPC_T set APPL_ID = 'RICE' where nmspc_cd = 'KUALI';


-- 
-- mysql-2012-01-03.sql
-- 


INSERT INTO KRCR_NMSPC_T VALUES ('KR-KRAD', uuid(), 1, 'Kuali Rapid Application Development', 'Y', 'RICE');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'viewId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'actionEvent', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'collectionPropertyName', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'fieldId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'groupId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'widgetId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'actionId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View', 'viewPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Edit Mode', 'viewEditModePermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Field', 'viewFieldPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Group', 'viewGroupPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Widget', 'viewWidgetPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Action', 'viewActionPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Line Field', 'viewLineFieldPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Line Action', 'viewLineActionPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Edit Mode'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Edit Mode'), '10', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='fieldId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'c', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Field'), '6', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='groupId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'c', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='fieldId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Widget'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Widget'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='widgetId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='actionId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'c', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='actionEvent'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='groupId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'c', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='collectionPropertyName'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'd', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='fieldId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'e', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), '6', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='groupId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'c', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='collectionPropertyName'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'd', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='actionId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'e', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='actionEvent'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Open View', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Edit View', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Use View', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Edit Mode'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'View Field', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Field'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Edit Field', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Field'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'View Group', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Edit Group', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'View Widget', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Widget'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Edit Widget', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Widget'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Perform Action', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Action'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'View Line', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Edit Line', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'View Line Field', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Edit Line Field', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Perform Line Action', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Action'), 'Y');




-- 
-- mysql-2012-01-04.sql
-- 


update KRMS_TERM_SPEC_T set TYP = 'java.lang.String' where TYP = 'T2';
update KRMS_TERM_SPEC_T set TYP = 'java.lang.String' where TYP = 'T1';
update KRMS_TERM_SPEC_T set TYP = 'java.lang.Integer' where TYP = 'T6';



-- 
-- mysql-2012-01-05.sql
-- 


alter table krms_cntxt_vld_actn_t drop foreign key krms_cntxt_vld_actn_fk1;
drop index krms_cntxt_vld_actn_ti1 on krms_cntxt_vld_actn_t;
rename table krms_cntxt_vld_actn_s to krms_cntxt_vld_actn_typ_s;
rename table krms_cntxt_vld_actn_t to krms_cntxt_vld_actn_typ_t;
create index krms_cntxt_vld_actn_typ_ti1 on krms_cntxt_vld_actn_typ_t (cntxt_id);
alter table krms_cntxt_vld_actn_typ_t add constraint krms_cntxt_vld_actn_typ_fk1
  foreign key (cntxt_id) references krms_cntxt_t (cntxt_id);

alter table krms_cntxt_vld_agenda_t drop foreign key krms_cntxt_vld_agenda_fk1;
drop index krms_cntxt_vld_agenda_ti1 on krms_cntxt_vld_agenda_t;
rename table krms_cntxt_vld_agenda_s to krms_cntxt_vld_agenda_typ_s;
rename table krms_cntxt_vld_agenda_t to krms_cntxt_vld_agenda_typ_t;
create index krms_cntxt_vld_agenda_typ_ti1 on krms_cntxt_vld_agenda_typ_t (cntxt_id);
alter table krms_cntxt_vld_agenda_typ_t add constraint krms_cntxt_vld_agenda_typ_fk1
  foreign key (cntxt_id) references krms_cntxt_t (cntxt_id);

alter table krms_cntxt_vld_rule_t drop foreign key krms_cntxt_vld_rule_fk1;
drop index krms_cntxt_vld_rule_ti1 on krms_cntxt_vld_rule_t;
rename table krms_cntxt_vld_rule_s to krms_cntxt_vld_rule_typ_s;
rename table krms_cntxt_vld_rule_t to krms_cntxt_vld_rule_typ_t;
create index krms_cntxt_vld_rule_typ_ti1 on krms_cntxt_vld_rule_typ_t (cntxt_id);
alter table krms_cntxt_vld_rule_typ_t add constraint krms_cntxt_vld_rule_typ_fk1
  foreign key (cntxt_id) references krms_cntxt_t (cntxt_id);

alter table krms_cntxt_vld_rule_typ_t change rule_id rule_typ_id varchar(40) not null;

update krms_agenda_t set typ_id = null where typ_id = 'T5';
update krms_rule_t set typ_id = null;
delete from krms_cntxt_vld_rule_typ_t;


-- 
-- mysql-2012-01-06.sql
-- 


-- KULRICE-6299: New DB index to improve action list performance
create index krew_actn_itm_ti6 on KREW_ACTN_ITM_T (DLGN_TYP, DLGN_PRNCPL_ID, DLGN_GRP_ID);


-- 
-- mysql-2012-01-11.sql
-- 


-- KULRICE-6452

drop table krms_cntxt_vld_event_t;
drop table krms_cntxt_vld_event_s;
rename table krms_cntxt_term_spec_prereq_s to krms_cntxt_vld_term_spec_s;


-- 
-- mysql-2012-01-18.sql
-- 


alter table KREW_DOC_HDR_T drop column RTE_LVL_MDFN_DT;


-- 
-- mysql-2012-01-19b.sql
-- 



--
-- KULRICE-5856: KRNS_DOC_HDR_T.FDOC_DESC column is only 40 char
--

ALTER TABLE KRNS_DOC_HDR_T MODIFY FDOC_DESC varchar(255);


--
-- KULRICE-6463: New DB Indexes for KIM Permission checks
--

create index krim_role_perm_ti2 on krim_role_perm_t (perm_id, actv_ind);
create index krim_perm_ti1 on krim_perm_t (perm_tmpl_id);
create index krim_perm_ti2 on krim_perm_t (perm_tmpl_id, actv_ind);
create index krim_perm_tmpl_ti1 on krim_perm_tmpl_t (nmspc_cd, nm);
create index krim_role_mbr_ti2 on krim_role_mbr_t (role_id, mbr_id, mbr_typ_cd);
create index krim_role_mbr_ti3 on krim_role_mbr_t (mbr_id, mbr_typ_cd);


--
-- KRMS Sample (and production) Data
--

---- If you should want to clean out your KRMS tables:
delete from  krms_cntxt_vld_rule_typ_t ;
delete from  krms_cntxt_vld_func_t ;
delete from  krms_term_spec_ctgry_t ;
delete from  krms_func_ctgry_t ;
delete from  krms_ctgry_t ;
delete from  krms_func_parm_t ;
delete from  krms_func_t ;
delete from  krms_term_parm_t ;
delete from  krms_term_rslvr_parm_spec_t ;
delete from  krms_term_t ;
delete from  krms_cntxt_vld_term_spec_t ;
delete from  krms_term_rslvr_input_spec_t ;
delete from  krms_term_rslvr_attr_t ;
delete from  krms_term_rslvr_t ;
delete from  krms_term_spec_t ;
delete from  krms_prop_parm_t ;
delete from  krms_cmpnd_prop_props_t ;
delete from  krms_agenda_attr_t ;
delete from  krms_cntxt_vld_actn_typ_t ;
delete from  krms_cntxt_vld_agenda_typ_t ;
delete from  krms_cntxt_attr_t ;
delete from  krms_rule_attr_t ;
update krms_agenda_itm_t set when_true=null;
update krms_agenda_itm_t set when_false=null;
update krms_agenda_itm_t set always=null;
delete from  krms_agenda_itm_t ;
delete from  krms_actn_attr_t ;
delete from  krms_actn_t ;
delete from  krms_typ_attr_t ;
delete from  krms_attr_defn_t ;
delete from  krms_agenda_t ;
update krms_rule_t set prop_id=null;
delete from  krms_prop_t ;
delete from  krms_rule_t ;
delete from  krms_typ_t;
delete from  krms_cntxt_t ;
delete from krcr_nmspc_t where obj_id = '5a83c912-94b9-4b4d-ac3f-88c53380a4a3';

---- KRMS test namespace

insert into krcr_nmspc_t (nmspc_cd, obj_id, nm, appl_id) 
values ('KR-RULE-TEST', '5a83c912-94b9-4b4d-ac3f-88c53380a4a3', 'Kuali Rules Test', 'RICE');


-- misc category
insert into krms_ctgry_t (ctgry_id, nm, nmspc_cd) values ('T1000', 'misc', 'KR-RULE-TEST');


--
-- TermResolver taking 1 campus code parameter
--

insert into krms_term_spec_t
(term_spec_id, nmspc_cd, nm, typ, desc_txt, actv, ver_nbr)
values ('T1000', 'KR-RULE-TEST', 'campusSize', 'java.lang.Integer', 'Size in # of students of the campus', 'Y', 1)
;

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('T1000', 'TermResolver', 'KR-RULE-TEST', null, 'Y', 1)
;

insert into krms_term_rslvr_t
(term_rslvr_id, nmspc_cd, nm, typ_id, output_term_spec_id, actv, ver_nbr)
values ('T1000', 'KR-RULE-TEST', 'campusSizeResolver', 'T1000','T1000', 'Y', 1)
;

insert into krms_term_rslvr_parm_spec_t
(term_rslvr_parm_spec_id, term_rslvr_id, nm, ver_nbr)
values ('T1000', 'T1000', 'Campus Code', 1)
;





insert into krms_term_spec_t
(term_spec_id, nmspc_cd, nm, typ, desc_txt, actv, ver_nbr)
values ('T1001', 'KR-RULE-TEST', 'orgMember', 'java.lang.Boolean', 'is the principal in the organization', 'Y', 1)
;

insert into krms_term_rslvr_t
(term_rslvr_id, nmspc_cd, nm, typ_id, output_term_spec_id, actv, ver_nbr)
values ('T1001', 'KR-RULE-TEST', 'orgMemberResolver', 'T1000','T1001', 'Y', 1)
;

insert into krms_term_rslvr_parm_spec_t
(term_rslvr_parm_spec_id, term_rslvr_id, nm, ver_nbr)
values ('T1001', 'T1001', 'Org Code', 1)
;

insert into krms_term_rslvr_parm_spec_t
(term_rslvr_parm_spec_id, term_rslvr_id, nm, ver_nbr)
values ('T1002', 'T1001', 'Principal ID', 1)
;




insert into krms_term_t
(term_id, term_spec_id, desc_txt, ver_nbr)
values ('T1000', 'T1000', 'Bloomington Campus Size', 1);

insert into krms_term_parm_t
(term_parm_id, term_id, nm, val, ver_nbr)
values ('T1000', 'T1000', 'Campus Code', 'BL', 1)
;







insert into krms_attr_defn_t
(attr_defn_id, nm, nmspc_cd, lbl, actv, ver_nbr)
values('T1000', 'Context1Qualifier', 'KR-RULE-TEST', 'Context 1 Qualifier', 'Y', 1)
;

insert into krms_attr_defn_t
(attr_defn_id, nm, nmspc_cd, lbl, actv, ver_nbr)
values('T1001', 'Event', 'KR-RULE-TEST', 'Event Name', 'Y', 1)
;

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('T1001', 'CAMPUS', 'KR-RULE-TEST', 'myCampusService', 'Y', 1)
;

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('T1002', 'KrmsActionResolverType', 'KR-RULE-TEST', 'testActionTypeService', 'Y', 1)
;

insert into krms_typ_t
(typ_id, nm, nmspc_cd, actv, ver_nbr)
values ('T1003', 'CONTEXT', 'KR-RULE-TEST',  'Y', 1)
;

insert into krms_typ_attr_t
(typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr)
values ('T1000', 1, 'T1003', 'T1000', 'Y', 1)
;

insert into krms_typ_t
(typ_id, nm, nmspc_cd, actv, ver_nbr)
values ('T1004', 'AGENDA', 'KR-RULE-TEST',  'Y', 1)
;


insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('T1005', 'Campus Agenda', 'KR-RULE-TEST', 'campusAgendaTypeService', 'Y', 1)
;


insert into krms_cntxt_t
(cntxt_id, nmspc_cd, nm, typ_id, actv, ver_nbr)
values ('CONTEXT1','KR-RULE-TEST', 'Context1', 'T1003', 'Y', 1)
;

insert into krms_cntxt_t
(cntxt_id, nmspc_cd, nm, typ_id, actv, ver_nbr)
values ('CONTEXT_NO_PERMISSION','KRMS_TEST_VOID', 'Context with no premissions', 'T1003', 'Y', 1)
;

insert into krms_cntxt_attr_t
(cntxt_attr_id, cntxt_id, attr_val, attr_defn_id, ver_nbr)
values('T1000', 'CONTEXT1', 'BLAH', 'T1000', 1)
;

insert into krms_cntxt_vld_actn_typ_t
(cntxt_vld_actn_id, cntxt_id, actn_typ_id, ver_nbr)
values ('T1000', 'CONTEXT1', 'T1002', 1)
;

insert into krms_cntxt_vld_actn_typ_t
(cntxt_vld_actn_id, cntxt_id, actn_typ_id, ver_nbr)
values ('T1001', 'CONTEXT1', '1000', 1)
;

insert into krms_cntxt_vld_actn_typ_t
(cntxt_vld_actn_id, cntxt_id, actn_typ_id, ver_nbr)
values ('T1002', 'CONTEXT1', '1001', 1)
;

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1000', 'KR-RULE-TEST', 'Rule1', null, null, 'Y', 1, 'stub rule lorem ipsum')
;

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1000', 'is campus bloomington', null, 'S','T1000',1)
;

update krms_rule_t
set prop_id = 'T1000' where rule_id = 'T1000'
;

insert into krms_term_spec_t
(term_spec_id, nm, nmspc_cd,  typ, actv, ver_nbr)
values ('T1002', 'Campus Code', 'KR-RULE-TEST', 'java.lang.String', 'Y', 1);

insert into krms_term_t
(term_id, term_spec_id, desc_txt, ver_nbr)
values ('T1002', 'T1002', null, 1);

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1000', 'T1000', 'T1002', 'T', 1, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1001', 'T1000', 'BL', 'C', 2, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1002', 'T1000', '=', 'O', 3, 1)
;

insert into krms_actn_t
(actn_id, nm, nmspc_cd, desc_txt, typ_id, rule_id, seq_no, ver_nbr)
values ( 'T1000', 'testAction', 'KR-RULE-TEST', 'Action Stub for Testing', 'T1002', 'T1000', 1, 1)
;

insert into krms_agenda_t
(agenda_id, nm, cntxt_id, init_agenda_itm_id, typ_id, actv, ver_nbr)
values ( 'T1000', 'My Fabulous Agenda', 'CONTEXT1', null, 'T1005', 'Y', 1)
;

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1000', 'T1000', 'T1000', 1)
;

update krms_agenda_t set INIT_AGENDA_ITM_ID = 'T1000' where agenda_id = 'T1000'
;


insert into krms_term_spec_t
(term_spec_id, nmspc_cd, nm, typ, actv, ver_nbr)
values ('T1008', 'KR-RULE-TEST', 'campusCode', 'java.lang.String', 'Y', 1)
;

-- next item

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1001', 'KR-RULE-TEST', 'Rule2', null, null, 'Y', 1, 'Frog specimens bogus rule foo')
;

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1001', 'is campus bloomington', null, 'S','T1001',1)
;

update krms_rule_t
set prop_id = 'T1001' where rule_id = 'T1001'
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1003', 'T1001', 'T1002', 'T', 1, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1004', 'T1001', 'BL', 'C', 2, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1005', 'T1001', '=', 'O', 3, 1)
;

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1001', 'T1001', 'T1000', 1)
;

update krms_agenda_itm_t
SET when_true = 'T1001' WHERE agenda_itm_id = 'T1000'
;

-- next item

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1002', 'KR-RULE-TEST', 'Rule3', null, null, 'Y', 1, 'Bloomington campus code rule')
;

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1002', 'is campus bloomington', null, 'S','T1002',1)
;

update krms_rule_t
set prop_id = 'T1002' where rule_id = 'T1002'
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1006', 'T1002', 'T1002', 'T', 1, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1007', 'T1002', 'BL', 'C', 2, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1008', 'T1002', '=', 'O', 3, 1)
;

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1002', 'T1002', 'T1000', 1)
;
--
update krms_agenda_itm_t
SET always = 'T1002' WHERE agenda_itm_id = 'T1001'
;

-- next item

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1003', 'KR-RULE-TEST', 'Rule4', null, null, 'Y', 1, 'check for possible BBQ ingiter hazard')
;

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1003', 'is campus bloomington', null, 'S','T1003',1)
;

update krms_rule_t
set prop_id = 'T1003' where rule_id = 'T1003'
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1009', 'T1003', 'T1002', 'T', 1, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1010', 'T1003', 'BL', 'C', 2, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1011', 'T1003', '=', 'O', 3, 1)
;

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1003', 'T1003', 'T1000', 1)
;
--
update krms_agenda_itm_t
SET always = 'T1003' WHERE agenda_itm_id = 'T1002'
;

-- next item

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1004', 'KR-RULE-TEST', 'Rule5', null, null, 'Y', 1, 'remembered to wear socks')
;

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1004', 'is campus bloomington', null, 'S','T1004',1)
;

update krms_rule_t
set prop_id = 'T1004' where rule_id = 'T1004'
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1012', 'T1004', 'T1002', 'T', 1, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1013', 'T1004', 'BL', 'C', 2, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1014', 'T1004', '=', 'O', 3, 1)
;

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1004', 'T1004', 'T1000', 1)
;

update krms_agenda_itm_t
SET when_false = 'T1004' WHERE agenda_itm_id = 'T1000'
;

-- next item

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1005', 'KR-RULE-TEST', 'Rule6', null, null, 'Y', 1, 'good behavior at carnival')
;

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1005', 'is campus bloomington', null, 'S','T1005',1)
;

update krms_rule_t
set prop_id = 'T1005' where rule_id = 'T1005'
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1015', 'T1005', 'T1002', 'T', 1, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1016', 'T1005', 'BL', 'C', 2, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1017', 'T1005', '=', 'O', 3, 1)
;

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1005', 'T1005', 'T1000', 1)
;
--
update krms_agenda_itm_t
SET always = 'T1005' WHERE agenda_itm_id = 'T1000'
;



--
-- next item
--

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1006', 'KR-RULE-TEST', 'Rule7', null, null, 'Y', 1, 'is KRMS in da haus')
;

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1006', 'is campus bloomington', null, 'S','T1006',1)
;

update krms_rule_t
set prop_id = 'T1006' where rule_id = 'T1006'
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1018', 'T1006', 'T1002', 'T', 1, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1019', 'T1006', 'BL', 'C', 2, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1020', 'T1006', '=', 'O', 3, 1)
;

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1006', 'T1006', 'T1000', 1)
;
--
update krms_agenda_itm_t
SET when_false = 'T1006' WHERE agenda_itm_id = 'T1002'
;

--
-- rule with a compound proposition
--
insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1007', 'KR-RULE-TEST', 'CmpdTestRule', null, null, 'Y', 1, 'For testing compound props')
;

insert into krms_prop_t
(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_op_cd, ver_nbr)
values ('T1007', 'a compound prop', null, 'C','T1007', '&', 1)
;

update krms_rule_t
set prop_id = 'T1007' where rule_id = 'T1007'
;

insert into krms_term_spec_t
(term_spec_id, nmspc_cd, nm, typ, actv, ver_nbr)
values ('T1003', 'KR-RULE-TEST', 'bogusFundTermSpec', 'java.lang.String', 'Y', 1);

insert into krms_term_t
(term_id, term_spec_id, desc_txt, ver_nbr)
values ('T1003', 'T1003', 'Fund Name', 1);


-- 2nd level prop
insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1008', 'a simple child to a compound prop', null, 'S','T1007', 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1021', 'T1008', 'T1002', 'T', 1, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1022', 'T1008', 'Muir', 'C', 2, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1023', 'T1008', '=', 'O', 3, 1)
;

insert into krms_cmpnd_prop_props_t
(cmpnd_prop_id, prop_id)
values ('T1007', 'T1008');

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_seq_no, ver_nbr)
values ('T1009', '2nd simple child to a compound prop ', null, 'S','T1007', 2, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1024', 'T1009', 'T1002', 'T', 1, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1025', 'T1009', 'Revelle', 'C', 2, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1026', 'T1009', '=', 'O', 3, 1)
;

insert into krms_cmpnd_prop_props_t
(cmpnd_prop_id, prop_id)
values ('T1007', 'T1009');


insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_seq_no, ver_nbr)
values ('T1010', '3nd simple child to a compound prop ', null, 'S','T1007', 3, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1027', 'T1010', 'T1002', 'T', 1, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1028', 'T1010', 'Warren', 'C', 2, 1)
;

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1029', 'T1010', '=', 'O', 3, 1)
;

insert into krms_cmpnd_prop_props_t
(cmpnd_prop_id, prop_id)
values ('T1007', 'T1010');


--
-- start of new agendas (AGENDA002, AGENDA003) and their associated items
--



--  AGENDA 002
insert into krms_agenda_t (agenda_id, nm, cntxt_id, init_agenda_itm_id, typ_id, actv, ver_nbr)
values ('T1001', 'SimpleAgendaCompoundProp', 'CONTEXT1', null, 'T1004', 'Y', 1);

insert into krms_agenda_itm_t (AGENDA_ITM_ID, RULE_ID, AGENDA_ID, VER_NBR)
values ('T1007', 'T1007', 'T1001', 1);

update krms_agenda_t set INIT_AGENDA_ITM_ID = 'T1007' where AGENDA_ID = 'T1001';

--  AGENDA 003 stuff

insert into krms_term_spec_t (TERM_SPEC_ID, NM, TYP, ACTV, VER_NBR, DESC_TXT, nmspc_cd)
values ('T1004', 'PO Value', 'java.lang.Integer', 'Y', 1, 'Purchase Order Value', 'KR-RULE-TEST');

insert into krms_term_spec_t (TERM_SPEC_ID, NM, TYP, ACTV, VER_NBR, DESC_TXT, nmspc_cd)
values ('T1005', 'PO Item Type', 'java.lang.String', 'Y', 1, 'Purchased Item Type', 'KR-RULE-TEST');

insert into krms_term_spec_t (TERM_SPEC_ID, NM, TYP, ACTV, VER_NBR, DESC_TXT, nmspc_cd)
values ('T1006', 'Account', 'java.lang.String', 'Y', 1, 'Charged To Account', 'KR-RULE-TEST');

insert into krms_term_spec_t (TERM_SPEC_ID, NM, TYP, ACTV, VER_NBR, DESC_TXT, nmspc_cd)
values ('T1007', 'Occasion', 'java.lang.String', 'Y', 1, 'Special Event', 'KR-RULE-TEST');

insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1000', 'CONTEXT1', 'T1002', 'N');

insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1001', 'CONTEXT1', 'T1003', 'N');

insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1002', 'CONTEXT1', 'T1004', 'N');

insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1003', 'CONTEXT1', 'T1005', 'N');

insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1004', 'CONTEXT1', 'T1006', 'N');

insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1005', 'CONTEXT1', 'T1007', 'N');

insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1006', 'CONTEXT1', 'T1000', 'N');

insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1007', 'CONTEXT1', 'T1001', 'N');


insert into krms_term_t(term_id,TERM_SPEC_ID, DESC_TXT, VER_NBR)values ('T1004', 'T1004', 'PO Value', 1);
insert into krms_term_t(term_id,TERM_SPEC_ID, DESC_TXT, VER_NBR)values ('T1005', 'T1005', 'PO Item Type', 1);
insert into krms_term_t(term_id,TERM_SPEC_ID, DESC_TXT, VER_NBR)values ('T1006', 'T1006', 'Account', 1);
insert into krms_term_t(term_id,TERM_SPEC_ID, DESC_TXT, VER_NBR)values ('T1007', 'T1007', 'Occasion', 1);

--
-- big fin rule
--
insert into krms_rule_t(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1008', 'KR-RULE-TEST', 'Going Away Party for Travis', null, null, 'Y', 1, 'Does PO require my approval');

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_op_cd, ver_nbr)
values ('T1011', 'is purchase special', null, 'C','T1008', '&', 1);

update krms_rule_t set prop_id = 'T1011' where rule_id = 'T1008';


-- 2nd level prop

-- is it expensive
insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1012', 'is purchase order value large', null, 'S','T1008', 1);

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1030', 'T1012', 'T1004', 'T', 1, 1);

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1031', 'T1012', '5500.00', 'C', 2, 1);

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1032', 'T1012', '>', 'O', 3, 1);

insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1011', 'T1012');

-- is it controlled
insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_op_cd, ver_nbr)
values ('T1013', 'is purchased item controlled', null, 'C','T1008', '|', 1);

insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1011', 'T1013');

-- is it special
insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_op_cd, ver_nbr)
values ('T1014', 'is it for a special event', null, 'C','T1008', '&', 1);

insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1011', 'T1014');

---- controlled 3rd level props -----

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1015', 'is item purchased animal', null, 'S','T1008', 1);

insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1013', 'T1015');

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1033', 'T1015', 'T1005', 'T', 1, 1);

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1034', 'T1015', 'ANIMAL', 'C', 2, 1);

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1035', 'T1015', '=', 'O', 3, 1);



insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1016', 'is purchased item radioactive', null, 'S','T1008', 1);

insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1013', 'T1016');

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1036', 'T1016', 'T1005', 'T', 1, 1);

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1037', 'T1016', 'RADIOACTIVE', 'C', 2, 1);

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1038', 'T1016', '=', 'O', 3, 1);


insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_seq_no, ver_nbr)
values ('T1017', 'is it medicinal', null, 'S','T1008', 3, 1);

insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1013', 'T1017');

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1039', 'T1017', 'T1005', 'T', 1, 1);

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1040', 'T1017', 'ALCOHOL BEVERAGE', 'C', 2, 1);

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1041', 'T1017', '=', 'O', 3, 1);


-- is it special 3rd level props

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1018', 'charged to Kuali', null, 'S','T1008', 1);

insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1014', 'T1018');

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1042', 'T1018', 'T1006', 'T', 1, 1);

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1043', 'T1018', 'KUALI SLUSH FUND', 'C', 2, 1);

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1044', 'T1018', '=', 'O', 3, 1);



insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1019', 'Party at Travis House', null, 'S','T1008', 1);

insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1014', 'T1019');

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1045', 'T1019', 'T1007', 'T', 1, 1);

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1046', 'T1019', 'Christmas Party', 'C', 2, 1);

insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1047', 'T1019', '=', 'O', 3, 1);


--  AGENDA 003
insert into krms_agenda_t (agenda_id, nm, cntxt_id, init_agenda_itm_id, typ_id, actv, ver_nbr)
values ('T1002', 'One Big Rule', 'CONTEXT1', null, 'T1004', 'Y', 1);

insert into krms_agenda_itm_t (AGENDA_ITM_ID, RULE_ID, AGENDA_ID, VER_NBR)
values ('T1008', 'T1008', 'T1002', 1);

update krms_agenda_t set INIT_AGENDA_ITM_ID = 'T1008' where AGENDA_ID = 'T1002';



-- SQL for test CampusAgendaType:

insert into krms_cntxt_vld_agenda_typ_t
(cntxt_vld_agenda_id, cntxt_id, agenda_typ_id, ver_nbr)
values ('T1000', 'CONTEXT1', 'T1005', 1)
;

-- add a db-only attribute to CampusAgendaType
insert into krms_attr_defn_t (ATTR_DEFN_ID, NM, NMSPC_CD, LBL, CMPNT_NM, DESC_TXT)
values ('T1002', 'Optional Test Attribute', 'KR-RULE-TEST', 'label', null,
'this is an optional attribute for testing')
;
insert into krms_typ_attr_t (TYP_ATTR_ID, SEQ_NO, TYP_ID, ATTR_DEFN_ID) values ('T1001', 2, 'T1005', 'T1002');

-- add our campus attribute to CampusAgendaType
insert into krms_attr_defn_t (ATTR_DEFN_ID, NM, NMSPC_CD, LBL, CMPNT_NM, DESC_TXT)
values ('T1003', 'Campus', 'KR-RULE-TEST', 'campus label', null, 'the campus which this agenda is valid for')
;
insert into krms_typ_attr_t (TYP_ATTR_ID, SEQ_NO, TYP_ID, ATTR_DEFN_ID) values ('T1002', 1, 'T1005', 'T1003');


--
-- Copyright 2005-2012 The Kuali Foundation
--
--

-- Notification PeopleFlowActionType

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('1000', 'Notify PeopleFlow', 'KR-RULE', 'notificationPeopleFlowActionTypeService', 'Y', 1)
;

-- Approval PeopleFlowActionType

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('1001', 'Route to PeopleFlow', 'KR-RULE', 'approvalPeopleFlowActionTypeService', 'Y', 1)
;


-- add a PeopleFlow attribute to the PeopleFlow types
insert into krms_attr_defn_t (ATTR_DEFN_ID, NM, NMSPC_CD, LBL, CMPNT_NM, DESC_TXT)
values ('1000', 'peopleFlowId', 'KR-RULE', 'PeopleFlow', null,
'An identifier for a PeopleFlow')
;
insert into krms_typ_attr_t (TYP_ATTR_ID, SEQ_NO, TYP_ID, ATTR_DEFN_ID) values ('1000', 1, '1000', '1000');
insert into krms_typ_attr_t (TYP_ATTR_ID, SEQ_NO, TYP_ID, ATTR_DEFN_ID) values ('1001', 1, '1001', '1000');



-- General validation rule type
insert into krms_typ_t (typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr) values('1002', 'Validation Rule', 'KR-RULE', 'validationRuleTypeService', 'Y', 1);

-- General validation action type
insert into krms_typ_t (typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr) values('1003', 'Validation Action', 'KR-RULE', 'validationActionTypeService', 'Y', 1);

-- Invalid rule
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1001', 'Invalid Rule', 'KR-RULE', 'Invalid Rule', 'Y', null, 1, 'If true, execute the action');

-- Valid rule
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1002', 'Valid Rule', 'KR-RULE', 'Valid Rule', 'Y', null, 1, 'If false, execute the action');

-- General context rule type mapping
insert into krms_cntxt_vld_rule_typ_t (cntxt_vld_rule_id, cntxt_id, rule_typ_id, ver_nbr) values ('T1000', 'CONTEXT1', '1002', 1);

-- The General rule subtypes attributes
-- use same sequence number to prove that falling back to natural order when sequences are the same works.
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1002', 1, '1002', '1001', 'Y', 1);
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1003', 1, '1002', '1002', 'Y', 1);

-- warning action
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1003', 'Warning Action', 'KR-RULE', 'Warning Action', 'Y', null, 1, 'Warning');

-- error action
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1004', 'Error Action', 'KR-RULE', 'Error Action', 'Y', null, 1, 'Error');

-- action message
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1005', 'Action Message', 'KR-RULE', 'Action Message', 'Y', null, 1, 'Message validation action returns');

-- Context general validation acton type mapping
insert into krms_cntxt_vld_actn_typ_t (cntxt_vld_actn_id, cntxt_id, actn_typ_id, ver_nbr) values ('T1003', 'CONTEXT1', '1003', 1);

-- The General action type attribute
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1004', 1, '1003', '1003', 'Y', 1);
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1005', 2, '1003', '1004', 'Y', 1);
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1006', 3, '1003', '1005', 'Y', 1);



-- change permisison to use new krms test namespace
update krim_perm_attr_data_t set attr_val = 'KR-RULE-TEST' where attr_val = 'KRMS_TEST';

update krim_perm_t
   set nmspc_cd = 'KR-RULE-TEST'
 where nm = 'Maintain KRMS Agenda'
   and nmspc_cd = 'KR-RULE'
;

update krim_perm_t
   set nmspc_cd = 'KR-RULE-TEST'
 where nm = 'Maintain KRMS Agenda'
   and nmspc_cd = 'KRMS_TEST'
;

delete from krim_perm_attr_data_t
 where perm_id = (select perm_id from krim_perm_t where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KRMS_TEST');


-- set sequence values up to 10000

alter table krms_actn_attr_s auto_increment = 10000;
alter table krms_actn_s auto_increment = 10000;
alter table krms_agenda_attr_s auto_increment = 10000;
alter table krms_agenda_itm_s auto_increment = 10000;
alter table krms_agenda_s auto_increment = 10000;
alter table krms_attr_defn_s auto_increment = 10000;
alter table krms_cmpnd_prop_props_s auto_increment = 10000;
alter table krms_cntxt_attr_s auto_increment = 10000;
alter table krms_cntxt_s auto_increment = 10000;
alter table krms_cntxt_vld_actn_typ_s auto_increment = 10000;
alter table krms_cntxt_vld_agenda_typ_s auto_increment = 10000;
alter table krms_cntxt_vld_func_s auto_increment = 10000;
alter table krms_cntxt_vld_rule_typ_s auto_increment = 10000;
alter table krms_cntxt_vld_term_spec_s auto_increment = 10000;
alter table krms_ctgry_s auto_increment = 10000;
alter table krms_func_parm_s auto_increment = 10000;
alter table krms_func_s auto_increment = 10000;
alter table krms_prop_parm_s auto_increment = 10000;
alter table krms_prop_s auto_increment = 10000;
alter table krms_rule_attr_s auto_increment = 10000;
alter table krms_rule_s auto_increment = 10000;
alter table krms_term_parm_s auto_increment = 10000;
alter table krms_term_rslvr_attr_s auto_increment = 10000;
alter table krms_term_rslvr_input_spec_s auto_increment = 10000;
alter table krms_term_rslvr_parm_spec_s auto_increment = 10000;
alter table krms_term_rslvr_s auto_increment = 10000;
alter table krms_term_s auto_increment = 10000;
alter table krms_term_spec_s auto_increment = 10000;
alter table krms_typ_attr_s auto_increment = 10000;
alter table krms_typ_s auto_increment = 10000;



-- 
-- mysql-2012-01-19c.sql
-- 


-- KULRICE-6537 - increase size of MBR_NM field
ALTER TABLE KRIM_PND_GRP_MBR_T MODIFY MBR_NM varchar(100);








-- 
-- mysql-2012-01-19.sql
-- 


ALTER TABLE KREW_STYLE_T CHANGE STYLE_ID STYLE_ID VARCHAR(40) NOT NULL;

RENAME TABLE KREW_STYLE_T TO KRCR_STYLE_T;


--
-- mysql-2010-01-24.sql
--


-- unset type on My Fabulous Agenda since the required attribute isn't specified
UPDATE krms_agenda_t SET TYP_ID=null WHERE AGENDA_ID='T1000'
;

-- PeopleFlow Name
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt)
values ('1006', 'peopleFlowName', 'KR_RULE', 'PeopleFlow Name', 'Y', null, 1, 'PeopleFlow Name')
;
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr)
values ('1007', 3, '1000', '1006', 'Y', 1)
;
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr)
values ('1008', 3, '1001', '1006', 'Y', 1)
;


--
-- mysql-2010-01-31.sql
--


-- KULRICE-6481
ALTER TABLE KRIM_ROLE_PERM_T MODIFY ROLE_ID varchar(40) NOT NULL
;

ALTER TABLE KRIM_ROLE_PERM_T MODIFY PERM_ID varchar(40) NOT NULL
;

--
-- mysql-2012-02-03.sql
--

-- KULRICE-6630: Add SQL scripts for missing data in master database

insert into krew_typ_t values ('1', 'Sample Type', 'KR-SAP', 'sampleAppPeopleFlowTypeService', 'Y', 1);

insert into krew_attr_defn_t values ('1', 'number', 'KR-SAP', 'Travel Number', 'Y', 'edu.sampleu.travel.bo.TravelAccount', 1, null);

insert into krew_attr_defn_t values ('2', 'id', 'KR-SAP', null, 'Y', 'edu.sampleu.travel.bo.FiscalOfficer', 1, null);

insert into krew_typ_attr_t values ('1', 1, '1', '1', 'Y', 1);

insert into krew_typ_attr_t values ('2', 2, '1', '2', 'Y', 1);


--
-- mysql-2012-02-08.sql
--

insert into krim_perm_t
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select perm_id from
          (select (max(cast(perm_id as decimal)) + 1) as perm_id from krim_perm_t where perm_id is not NULL and cast(perm_id as decimal) < 10000)
         as tmptable),
        (select perm_tmpl_id from krim_perm_tmpl_t where nm = 'Create / Maintain Record(s)' and nmspc_cd = 'KR-NS'),
        'KR-NS','Create Term Maintenance Document','Allows user to create a new Term maintainence document','Y',1,
        '0dbce939-4f22-4e9b-a4bb-1615c0f411a2');

insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select attr_data_id from
          (select (max(cast(attr_data_id as decimal)) + 1) as attr_data_id from krim_perm_attr_data_t where attr_data_id is not NULL and cast(attr_data_id as decimal) < 10000)
         as tmptable),
        (select perm_id from krim_perm_t where nm = 'Create Term Maintenance Document' and nmspc_cd = 'KR-NS'),
        (select kim_typ_id from krim_typ_t where nm = 'Document Type & Existing Records Only' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'documentTypeName'),
        'TermMaintenanceDocument',1,'aa1d1400-6155-4819-8bad-e5dd81f9871b');

insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select role_perm_id from
          (select (max(cast(role_perm_id as decimal)) + 1) as role_perm_id from krim_role_perm_t where role_perm_id is not NULL and cast(role_perm_id as decimal) < 10000)
         as tmptable),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select perm_id from krim_perm_t where nm = 'Create Term Maintenance Document' and nmspc_cd = 'KR-NS'),
        'Y', 1, '45f8f55e-23d9-4278-ade8-ddfc870852e6');


insert into krim_perm_t
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select perm_id from
          (select (max(cast(perm_id as decimal)) + 1) as perm_id from krim_perm_t where perm_id is not NULL and cast(perm_id as decimal) < 10000)
         as tmptable),
        (select perm_tmpl_id from krim_perm_tmpl_t where nm = 'Create / Maintain Record(s)' and nmspc_cd = 'KR-NS'),
        'KR-NS','Create Context Maintenance Document','Allows user to create a new Context maintainence document','Y',1,
        'cefeed6d-b5e2-40aa-9034-137db317b532');

insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select attr_data_id from
          (select (max(cast(attr_data_id as decimal)) + 1) as attr_data_id from krim_perm_attr_data_t where attr_data_id is not NULL and cast(attr_data_id as decimal) < 10000)
         as tmptable),
        (select perm_id from krim_perm_t where nm = 'Create Context Maintenance Document' and nmspc_cd = 'KR-NS'),
        (select kim_typ_id from krim_typ_t where nm = 'Document Type & Existing Records Only' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'documentTypeName'),
        'ContextMaintenanceDocument',1,'c43bc7f5-949e-4a85-b173-6a53d81f2762');

insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select role_perm_id from
          (select (max(cast(role_perm_id as decimal)) + 1) as role_perm_id from krim_role_perm_t where role_perm_id is not NULL and cast(role_perm_id as decimal) < 10000)
         as tmptable),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select perm_id from krim_perm_t where nm = 'Create Context Maintenance Document' and nmspc_cd = 'KR-NS'),
        'Y', 1, 'cd7cbc67-c0b2-4785-afa8-8c8d073b78df');

insert into krim_perm_t
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select perm_id from
          (select (max(cast(perm_id as decimal)) + 1) as perm_id from krim_perm_t where perm_id is not NULL and cast(perm_id as decimal) < 10000)
         as tmptable),
        (select perm_tmpl_id from krim_perm_tmpl_t where nm = 'Create / Maintain Record(s)' and nmspc_cd = 'KR-NS'),
        'KR-NS','Create TermSpecification Maintenance Document','Allows user to create a new TermSpecification maintainence document','Y',1,
        '02bd9acd-48d9-4fec-acbd-6a441c5ea8c2');

insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select attr_data_id from
          (select (max(cast(attr_data_id as decimal)) + 1) as attr_data_id from krim_perm_attr_data_t where attr_data_id is not NULL and cast(attr_data_id as decimal) < 10000)
         as tmptable),
        (select perm_id from krim_perm_t where nm = 'Create TermSpecification Maintenance Document' and nmspc_cd = 'KR-NS'),
        (select kim_typ_id from krim_typ_t where nm = 'Document Type & Existing Records Only' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'documentTypeName'),
        'TermSpecificationMaintenanceDocument',1,'d3e373ca-296b-4834-bd66-ba159ebe733a');

insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select role_perm_id from
          (select (max(cast(role_perm_id as decimal)) + 1) as role_perm_id from krim_role_perm_t where role_perm_id is not NULL and cast(role_perm_id as decimal) < 10000)
         as tmptable),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select perm_id from krim_perm_t where nm = 'Create TermSpecification Maintenance Document' and nmspc_cd = 'KR-NS'),
        'Y', 1, '83a270a0-1cdb-4440-ab8b-41cd8afc41d9');


--
-- mysql-2012-02-14.sql
--

--
-- KULRICE-6710: Drop krms_cntxt_vld_rule_t, krms_cntxt_vld_actn_t and krms_cntxt_vld_agenda_t tables
--

-- NOTE that these tables should have been renamed in the master db, but mysteriously still were present.
-- deleting here.  If you get errors that these tables and sequences don't exist, you can omit these statements
-- without concern.

drop table krms_cntxt_vld_actn_t;
drop table krms_cntxt_vld_actn_s;
drop table krms_cntxt_vld_agenda_t;
drop table krms_cntxt_vld_agenda_s;
drop table krms_cntxt_vld_rule_t;
drop table krms_cntxt_vld_rule_s;

--
-- mysql-2012-02-23.sql
--

--
-- KULRICE-6811: Conversion of WorkflowFunctions to EDLFunctions in eDocLite stylesheets
--

UPDATE KRCR_STYLE_T set XML=replace(XML,'org.kuali.rice.kew.edl.WorkflowFunctions','org.kuali.rice.edl.framework.util.EDLFunctions');