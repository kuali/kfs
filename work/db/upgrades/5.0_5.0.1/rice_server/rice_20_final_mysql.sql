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
-- mysql-2010-04-15.sql
-- 


ALTER TABLE KRNS_SESN_DOC_T ADD (OBJ_ID VARCHAR(36))
;
ALTER TABLE KRNS_SESN_DOC_T ADD (VER_NBR DECIMAL(8) DEFAULT 0)
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


alter table KREN_CHNL_SUBSCRP_T add OBJ_ID varchar(36)
;
alter table KREN_CNTNT_TYP_T add OBJ_ID varchar(36) 
;
alter table KREN_CHNL_T add OBJ_ID varchar(36) 
;
alter table KREN_NTFCTN_MSG_DELIV_T add OBJ_ID varchar(36) 
;
alter table KREN_NTFCTN_T add OBJ_ID varchar(36) 
;
alter table KREN_PRIO_T add OBJ_ID varchar(36) 
;
alter table KREN_PRODCR_T add OBJ_ID varchar(36) 
;
alter table KREN_RECIP_LIST_T add OBJ_ID varchar(36)
;
alter table KREN_SNDR_T add OBJ_ID varchar(36)
;
alter table KREN_RECIP_T add OBJ_ID varchar(36) 
;
alter table KREN_RVWER_T add OBJ_ID varchar(36) 
;
alter table KREN_CHNL_SUBSCRP_T add ver_nbr decimal(8)
;
alter table KREN_RECIP_LIST_T add ver_nbr decimal(8)
;
alter table KREN_SNDR_T add ver_nbr decimal(8)
;
alter table KREN_RECIP_T add ver_nbr decimal(8)
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
ALTER TABLE KREW_DOC_TYP_T CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) AFTER DOC_SEARCH_HELP_URL
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
-- Table `KRMS_TYP_T`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `KRMS_TYP_T` (
  `typ_id` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `srvc_nm` VARCHAR(200) NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`typ_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_PROP_T`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `KRMS_PROP_T` (
  `prop_id` VARCHAR(40) NOT NULL ,
  `desc_txt` VARCHAR(100) NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `dscrm_typ_cd` VARCHAR(10) NOT NULL ,
  `cmpnd_op_cd` VARCHAR(40) NULL ,
  `rule_id` VARCHAR(40) NOT NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`prop_id`) ,
  INDEX `KRMS_PROP_TI1` (`rule_id` ASC) ,
  CONSTRAINT `krms_prop_fk1`
    FOREIGN KEY (`rule_id` )
    REFERENCES `KRMS_RULE_T` (`rule_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_prop_fk2`
    FOREIGN KEY (`typ_id` )
    REFERENCES `KRMS_TYP_T` (`typ_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_RULE_T`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `KRMS_RULE_T` (
  `rule_id` VARCHAR(40) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `prop_id` VARCHAR(40) NOT NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  `descr_txt` VARCHAR(4000) NULL ,
  PRIMARY KEY (`rule_id`) ,
  INDEX `KRMS_RULE_TI1` (`prop_id` ASC) ,
  CONSTRAINT `krms_rule_fk1`
    FOREIGN KEY (`prop_id` )
    REFERENCES `KRMS_PROP_T` (`prop_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_CNTXT_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_CNTXT_T` (
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_AGENDA_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_AGENDA_T` (
  `agenda_id` VARCHAR(40) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `init_agenda_itm_id` VARCHAR(40) NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`agenda_id`) ,
  INDEX `KRMS_AGENDA_TI1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_agenda_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `KRMS_CNTXT_T` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_ATTR_DEFN_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_ATTR_DEFN_T` (
  `attr_defn_id` VARCHAR(255) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `lbl` VARCHAR(40) NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  `cmpnt_nm` VARCHAR(100) NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`attr_defn_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_TYP_ATTR_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_TYP_ATTR_T` (
  `typ_attr_id` VARCHAR(40) NOT NULL ,
  `seq_no` DECIMAL(5) NOT NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `attr_defn_id` VARCHAR(255) NOT NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`typ_attr_id`) ,
  INDEX `KRMS_TYP_ATTR_TI1` (`attr_defn_id` ASC) ,
  INDEX `KRMS_TYP_ATTR_TI2` (`typ_id` ASC) ,
  UNIQUE INDEX `KRMS_TYP_ATTR_TC1` (`typ_id` ASC, `attr_defn_id` ASC) ,
  CONSTRAINT `KRMS_Typ_attr_fk1`
    FOREIGN KEY (`attr_defn_id` )
    REFERENCES `KRMS_ATTR_DEFN_T` (`attr_defn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `KRMS_Typ_attr_fk2`
    FOREIGN KEY (`typ_id` )
    REFERENCES `KRMS_TYP_T` (`typ_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_ACTN_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_ACTN_T` (
  `actn_id` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(40) NULL ,
  `desc_txt` VARCHAR(4000) NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `rule_id` VARCHAR(40) NULL ,
  `seq_no` DECIMAL(5) NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`actn_id`) ,
  index `KRMS_ACTN_TI2` (`rule_id` asc) ,
  index `KRMS_ACTN_TI1` (`typ_id` asc) ,
  unique index `KRMS_ACTN_TC2` (`actn_id` asc, `rule_id` asc, `seq_no` asc) ,
  index `KRMS_ACTN_TI3` (`rule_id` asc, `seq_no` asc) ,
  constraint `krms_actn_fk1`
    FOREIGN KEY (`rule_id` )
    REFERENCES `KRMS_RULE_T` (`rule_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_ACTN_ATTR_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_ACTN_ATTR_T` (
  `actn_attr_data_id` VARCHAR(40) NOT NULL ,
  `actn_id` VARCHAR(40) NOT NULL ,
  `attr_defn_id` VARCHAR(40) NOT NULL ,
  `attr_val` VARCHAR(400) NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`actn_attr_data_id`) ,
  INDEX `KRMS_ACTN_ATTR_TI1` (`actn_id` ASC) ,
  INDEX `KRMS_ACTN_ATTR_TI2` (`attr_defn_id` ASC) ,
  CONSTRAINT `krms_actn_attr_fk1`
    FOREIGN KEY (`actn_id` )
    REFERENCES `KRMS_ACTN_T` (`actn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_actn_attr_fk2`
    FOREIGN KEY (`attr_defn_id` )
    REFERENCES `KRMS_ATTR_DEFN_T` (`attr_defn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_AGENDA_ITM_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_AGENDA_ITM_T` (
  `agenda_itm_id` VARCHAR(40) NOT NULL ,
  `rule_id` VARCHAR(40) NULL ,
  `sub_agenda_id` VARCHAR(40) NULL ,
  `agenda_id` VARCHAR(40) NOT NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  `when_true` VARCHAR(40) NULL ,
  `when_false` VARCHAR(40) NULL ,
  `always` VARCHAR(40) NULL ,
  PRIMARY KEY (`agenda_itm_id`) ,
  INDEX `KRMS_AGENDA_ITM_TI1` (`rule_id` ASC) ,
  INDEX `KRMS_AGENDA_ITM_TI2` (`agenda_id` ASC) ,
  INDEX `KRMS_AGENDA_ITM_TI3` (`sub_agenda_id` ASC) ,
  INDEX `KRMS_AGENDA_ITM_TI4` (`when_true` ASC) ,
  INDEX `KRMS_AGENDA_ITM_TI5` (`when_false` ASC) ,
  INDEX `KRMS_AGENDA_ITM_TI6` (`always` ASC) ,
  CONSTRAINT `krms_agenda_itm_fk1`
    FOREIGN KEY (`rule_id` )
    REFERENCES `KRMS_RULE_T` (`rule_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_agenda_itm_fk2`
    FOREIGN KEY (`agenda_id` )
    REFERENCES `KRMS_AGENDA_T` (`agenda_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_agenda_itm_fk3`
    FOREIGN KEY (`sub_agenda_id` )
    REFERENCES `KRMS_AGENDA_T` (`agenda_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_agenda_itm_fk4`
    FOREIGN KEY (`when_true` )
    REFERENCES `KRMS_AGENDA_ITM_T` (`agenda_itm_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_agenda_itm_fk5`
    FOREIGN KEY (`when_false` )
    REFERENCES `KRMS_AGENDA_ITM_T` (`agenda_itm_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_agenda_itm_fk6`
    FOREIGN KEY (`always` )
    REFERENCES `KRMS_AGENDA_ITM_T` (`agenda_itm_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_RULE_ATTR_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_RULE_ATTR_T` (
  `rule_attr_id` VARCHAR(40) NOT NULL ,
  `rule_id` VARCHAR(40) NOT NULL ,
  `attr_defn_id` VARCHAR(40) NOT NULL ,
  `attr_val` VARCHAR(400) NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`rule_attr_id`) ,
  INDEX `KRMS_RULE_ATTR_TI1` (`rule_id` ASC) ,
  INDEX `KRMS_RULE_ATTR_TI2` (`attr_defn_id` ASC) ,
  CONSTRAINT `krms_rule_attr_fk1`
    FOREIGN KEY (`rule_id` )
    REFERENCES `KRMS_RULE_T` (`rule_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_rule_attr_fk2`
    FOREIGN KEY (`attr_defn_id` )
    REFERENCES `KRMS_ATTR_DEFN_T` (`attr_defn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_CNTXT_ATTR_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_CNTXT_ATTR_T` (
  `cntxt_attr_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `attr_val` VARCHAR(400) NULL ,
  `attr_defn_id` VARCHAR(40) NULL ,
  `ver_nbr` DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_attr_id`) ,
  INDEX `KRMS_CNTXT_ATTR_TI1` (`cntxt_id` ASC) ,
  INDEX `KRMS_CNTXT_ATTR_TI2` (`attr_defn_id` ASC) ,
  CONSTRAINT `krms_cntxt_attr_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `KRMS_CNTXT_T` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_cntxt_attr_fk2`
    FOREIGN KEY (`attr_defn_id` )
    REFERENCES `KRMS_ATTR_DEFN_T` (`attr_defn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_CNTXT_VLD_ACTN_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_CNTXT_VLD_ACTN_T` (
  `cntxt_vld_actn_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `actn_typ_id` VARCHAR(40) NOT NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_vld_actn_id`) ,
  INDEX `KRMS_CNTXT_VLD_ACTN_TI1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_cntxt_vld_actn_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `KRMS_CNTXT_T` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_AGENDA_ATTR_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_AGENDA_ATTR_T` (
  `agenda_attr_id` VARCHAR(40) NOT NULL ,
  `agenda_id` VARCHAR(40) NOT NULL ,
  `attr_val` VARCHAR(400) NULL ,
  `attr_defn_id` VARCHAR(40) NOT NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`agenda_attr_id`) ,
  INDEX `KRMS_AGENDA_ATTR_TI1` (`agenda_id` ASC) ,
  INDEX `KRMS_AGENDA_ATTR_T12` (`attr_defn_id` ASC) ,
  CONSTRAINT `krms_agenda_attr_fk1`
    FOREIGN KEY (`agenda_id` )
    REFERENCES `KRMS_AGENDA_T` (`agenda_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_agenda_attr_fk2`
    FOREIGN KEY (`attr_defn_id` )
    REFERENCES `KRMS_ATTR_DEFN_T` (`attr_defn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_CMPND_PROP_PROPS_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_CMPND_PROP_PROPS_T` (
  `cmpnd_prop_id` VARCHAR(40) NOT NULL ,
  `prop_id` VARCHAR(40) NOT NULL ,
  `seq_no` TINYINT NOT NULL ,
  PRIMARY KEY (`cmpnd_prop_id`, `prop_id`) ,
  INDEX `KRMS_CMPND_PROP_PROPS_TI1` (`prop_id` ASC) ,
  CONSTRAINT `krms_cmpnd_prop_props_fk1`
    FOREIGN KEY (`prop_id` )
    REFERENCES `KRMS_PROP_T` (`prop_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_cmpnd_prop_props_fk2`
    FOREIGN KEY (`cmpnd_prop_id` )
    REFERENCES `KRMS_PROP_T` (`prop_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_PROP_PARM_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_PROP_PARM_T` (
  `prop_parm_id` VARCHAR(40) NOT NULL ,
  `prop_id` VARCHAR(40) NOT NULL ,
  `parm_val` VARCHAR(255) NULL ,
  `parm_typ_cd` VARCHAR(1) NOT NULL ,
  `seq_no` DECIMAL(5) NOT NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`prop_parm_id`) ,
  INDEX `KRMS_PROP_PARM_TI1` (`prop_id` ASC) ,
  CONSTRAINT `krms_prop_parm_fk1`
    FOREIGN KEY (`prop_id` )
    REFERENCES `KRMS_PROP_T` (`prop_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_TERM_SPEC_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_TERM_SPEC_T` (
  `term_spec_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NULL ,
  `nm` VARCHAR(255) NOT NULL ,
  `typ` VARCHAR(255) NOT NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL(8) NOT NULL ,
  PRIMARY KEY (`term_spec_id`) ,
  UNIQUE INDEX `KRMS_ASSET_TC1` (`nm` ASC, `cntxt_id` ASC) ,
  INDEX `KRMS_ASSET_TI1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_asset_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `KRMS_CNTXT_T` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_TERM_RSLVR_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_TERM_RSLVR_T` (
  `term_rslvr_id` VARCHAR(40) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `output_term_spec_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`term_rslvr_id`) ,
  INDEX `KRMS_TERM_RSLVR_TI1` (`cntxt_id` ASC) ,
  INDEX `KRMS_TERM_RSLVR_TI2` (`typ_id` ASC) ,
  UNIQUE INDEX `KRMS_TERM_RSLVR_TC1` (`nmspc_cd` ASC, `nm` ASC, `cntxt_id` ASC) ,
  CONSTRAINT `KRMS_Term_rslvr_fk1`
    FOREIGN KEY (`output_term_spec_id` )
    REFERENCES `KRMS_TERM_SPEC_T` (`term_spec_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `KRMS_Term_rslvr_fk2`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `KRMS_CNTXT_T` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `KRMS_Term_rslvr_fk3`
    FOREIGN KEY (`typ_id` )
    REFERENCES `KRMS_TYP_T` (`typ_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_TERM_RSLVR_ATTR_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_TERM_RSLVR_ATTR_T` (
  `term_rslvr_attr_id` VARCHAR(40) NOT NULL ,
  `term_rslvr_id` VARCHAR(40) NOT NULL ,
  `attr_defn_id` VARCHAR(40) NOT NULL ,
  `attr_val` VARCHAR(400) NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`term_rslvr_attr_id`) ,
  INDEX `KRMS_ASSET_RSLVR_ATTR_TI1` (`term_rslvr_id` ASC) ,
  INDEX `KRMS_ASSET_RSLVR_ATTR_TI2` (`attr_defn_id` ASC) ,
  CONSTRAINT `krms_asset_rslvr_attr_fk1`
    FOREIGN KEY (`term_rslvr_id` )
    REFERENCES `KRMS_TERM_RSLVR_T` (`term_rslvr_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_asset_rslvr_attr_fk2`
    FOREIGN KEY (`attr_defn_id` )
    REFERENCES `KRMS_ATTR_DEFN_T` (`attr_defn_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_TERM_RSLVR_INPUT_SPEC_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_TERM_RSLVR_INPUT_SPEC_T` (
  `term_spec_id` VARCHAR(40) NOT NULL ,
  `term_rslvr_id` VARCHAR(40) NOT NULL ,
  INDEX `KRMS_INPUT_ASSET_TI1` (`term_spec_id` ASC) ,
  INDEX `KRMS_INPUT_ASSET_TI2` (`term_rslvr_id` ASC) ,
  PRIMARY KEY (`term_spec_id`, `term_rslvr_id`) ,
  CONSTRAINT `krms_input_asset_fk2`
    FOREIGN KEY (`term_spec_id` )
    REFERENCES `KRMS_TERM_SPEC_T` (`term_spec_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_input_asset_fk1`
    FOREIGN KEY (`term_rslvr_id` )
    REFERENCES `KRMS_TERM_RSLVR_T` (`term_rslvr_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_CNTXT_TERM_SPEC_PREREQ_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_CNTXT_TERM_SPEC_PREREQ_T` (
  `cntxt_term_spec_prereq_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `term_spec_id` VARCHAR(40) NOT NULL ,
  PRIMARY KEY (`cntxt_term_spec_prereq_id`) ,
  INDEX `KRMS_CNTXT_ASSET_PREREQ_TI1` (`cntxt_id` ASC) ,
  INDEX `KRMS_CNTXT_ASSET_PREREQ_TI2` (`term_spec_id` ASC) ,
  CONSTRAINT `krms_cntxt_asset_prereq_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `KRMS_CNTXT_T` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `krms_cntxt_asset_prereq_fk2`
    FOREIGN KEY (`term_spec_id` )
    REFERENCES `KRMS_TERM_SPEC_T` (`term_spec_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_TERM_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_TERM_T` (
  `term_id` VARCHAR(40) NOT NULL ,
  `term_spec_id` VARCHAR(40) NOT NULL ,
  ver_nbr DECIMAL(8) NOT NULL ,
  PRIMARY KEY (`term_id`) ,
  INDEX `KRMS_TERM_TI1` (`term_spec_id` ASC) ,
  CONSTRAINT `KRMS_TERM_T__fk1`
    FOREIGN KEY (`term_spec_id` )
    REFERENCES `KRMS_TERM_SPEC_T` (`term_spec_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_TERM_RSLVR_PARM_SPEC_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_TERM_RSLVR_PARM_SPEC_T` (
  `term_rslvr_parm_spec_id` VARCHAR(40) NOT NULL ,
  `term_rslvr_id` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(45) NOT NULL ,
  ver_nbr DECIMAL(8) NOT NULL ,
  PRIMARY KEY (`term_rslvr_parm_spec_id`) ,
  CONSTRAINT `KRMS_Term_reslv_parm_fk1`
    FOREIGN KEY (`term_rslvr_id` )
    REFERENCES `KRMS_TERM_RSLVR_T` (`term_rslvr_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_TERM_PARM_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_TERM_PARM_T` (
  `term_parm_id` VARCHAR(40) NOT NULL ,
  `term_id` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(255) NOT NULL ,
  `val` VARCHAR(255) NULL ,
  ver_nbr DECIMAL(8) NOT NULL ,
  PRIMARY KEY (`term_parm_id`) ,
  INDEX `KRMS_TERM_PARM_TI1` (`term_id` ASC) ,
  CONSTRAINT `KRMS_Term_parm_fk1`
    FOREIGN KEY (`term_id` )
    REFERENCES `KRMS_TERM_T` (`term_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_FUNC_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_FUNC_T` (
  `func_id` VARCHAR(40) NOT NULL ,
  `nmspc_cd` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `desc_txt` VARCHAR(255) NULL ,
  `rtrn_typ` VARCHAR(255) NOT NULL ,
  `typ_id` VARCHAR(40) NOT NULL ,
  `actv` VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`func_id`) ,
  INDEX `KRMS_FUNC_TI1` (`typ_id` ASC) ,
  CONSTRAINT `krms_func_fk1`
    FOREIGN KEY (`typ_id` )
    REFERENCES `KRMS_TYP_T` (`typ_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_FUNC_PARM_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_FUNC_PARM_T` (
  `func_parm_id` VARCHAR(40) NOT NULL ,
  `nm` VARCHAR(100) NOT NULL ,
  `desc_txt` VARCHAR(255) NULL ,
  `typ` VARCHAR(255) NOT NULL ,
  `func_id` VARCHAR(40) NOT NULL ,
  `seq_no` DECIMAL(5) NOT NULL ,
  PRIMARY KEY (`func_parm_id`) ,
  INDEX `KRMS_FUNC_PARM_TI1` (`func_id` ASC) ,
  CONSTRAINT `krms_func_parm_fk1`
    FOREIGN KEY (`func_id` )
    REFERENCES `KRMS_FUNC_T` (`func_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_CNTXT_VLD_FUNC_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_CNTXT_VLD_FUNC_T` (
  `cntxt_vld_func_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `func_id` VARCHAR(40) NOT NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_vld_func_id`) ,
  INDEX `KRMS_CNTXT_VLD_FUNC_TI1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_cntxt_vld_func_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `KRMS_CNTXT_T` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_CNTXT_VLD_RULE_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_CNTXT_VLD_RULE_T` (
  `cntxt_vld_rule_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `rule_id` VARCHAR(40) NOT NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_vld_rule_id`) ,
  INDEX `KRMS_CNTXT_VLD_RULE_TI1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_cntxt_vld_rule_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `KRMS_CNTXT_T` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `KRMS_CNTXT_VLD_EVENT_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_CNTXT_VLD_EVENT_T` (
  `cntxt_vld_event_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `event_nm` VARCHAR(255) NOT NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_vld_event_id`) ,
  INDEX `KRMS_CNTXT_VLD_EVENT_TI1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_cntxt_vld_event_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `KRMS_CNTXT_T` (`cntxt_id` )
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


create table KRMS_TYP_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_TYP_S auto_increment = 1000;


create table KRMS_PROP_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_PROP_S auto_increment = 1000;


create table KRMS_RULE_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_RULE_S auto_increment = 1000;


create table KRMS_CNTXT_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_CNTXT_S auto_increment = 1000;


create table KRMS_AGENDA_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_AGENDA_S auto_increment = 1000;


create table KRMS_ATTR_DEFN_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_ATTR_DEFN_S auto_increment = 1000;


create table KRMS_TYP_ATTR_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_TYP_ATTR_S auto_increment = 1000;


create table KRMS_ACTN_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_ACTN_S auto_increment = 1000;


create table KRMS_ACTN_ATTR_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_ACTN_ATTR_S auto_increment = 1000;


create table KRMS_AGENDA_ITM_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_AGENDA_ITM_S auto_increment = 1000;


create table KRMS_RULE_ATTR_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_RULE_ATTR_S auto_increment = 1000;


create table KRMS_CNTXT_ATTR_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_CNTXT_ATTR_S auto_increment = 1000;


create table KRMS_CNTXT_VLD_ACTN_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_CNTXT_VLD_ACTN_S auto_increment = 1000;


create table KRMS_AGENDA_ATTR_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_AGENDA_ATTR_S auto_increment = 1000;


create table KRMS_CMPND_PROP_PROPS_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_CMPND_PROP_PROPS_S auto_increment = 1000;


create table KRMS_PROP_PARM_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_PROP_PARM_S auto_increment = 1000;


create table KRMS_TERM_SPEC_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_TERM_SPEC_S auto_increment = 1000;


create table KRMS_TERM_RSLVR_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_TERM_RSLVR_S auto_increment = 1000;


create table KRMS_TERM_RSLVR_ATTR_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_TERM_RSLVR_ATTR_S auto_increment = 1000;


create table KRMS_TERM_RSLVR_INPUT_SPEC_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_TERM_RSLVR_INPUT_SPEC_S auto_increment = 1000;


create table KRMS_CNTXT_TERM_SPEC_PREREQ_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_CNTXT_TERM_SPEC_PREREQ_S auto_increment = 1000;


create table KRMS_TERM_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_TERM_S auto_increment = 1000;


create table KRMS_TERM_RSLVR_PARM_SPEC_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_TERM_RSLVR_PARM_SPEC_S auto_increment = 1000;


create table KRMS_TERM_PARM_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_TERM_PARM_S auto_increment = 1000;


create table KRMS_FUNC_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_FUNC_S auto_increment = 1000;


create table KRMS_FUNC_PARM_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_FUNC_PARM_S auto_increment = 1000;


create table KRMS_CNTXT_VLD_FUNC_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_CNTXT_VLD_FUNC_S auto_increment = 1000;


create table KRMS_CNTXT_VLD_RULE_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_CNTXT_VLD_RULE_S auto_increment = 1000;


create table KRMS_CNTXT_VLD_EVENT_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_CNTXT_VLD_EVENT_S auto_increment = 1000;



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
  VER_NBR decimal(8,0) DEFAULT '0' NOT NULL,
  PRIMARY KEY (SVC_DEF_ID),
  index KRSB_SVC_DEF_TI1 (instn_id),
  index KRSB_SVC_DEF_TI2 (svc_nm, stat_cd),
  index KRSB_SVC_DEF_TI3 (stat_cd),
  constraint KRSB_Svc_def_fk1 foreign key (svc_dscrptr_id) references KRSB_SVC_DSCRPTR_T(svc_dscrptr_id) on delete cascade
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
ALTER TABLE KRSB_SVC_DEF_T CHANGE APPL_NMSPC APPL_ID VARCHAR(255) NOT NULL
;
ALTER TABLE KRSB_MSG_QUE_T CHANGE SVC_NMSPC APPL_ID VARCHAR(255) NOT NULL
;
ALTER TABLE KRNS_NMSPC_T CHANGE APPL_NMSPC_CD APPL_ID VARCHAR(20)
;
ALTER TABLE KRNS_PARM_T CHANGE APPL_NMSPC_CD APPL_ID VARCHAR(20)
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


-- make KRMS_RULE_T.prop_id nullable
alter table KRMS_RULE_T change column prop_id prop_id varchar(40) DEFAULT NULL;

-- add KRMS_ACTN_T.nmspc_cd
alter table KRMS_ACTN_T add column nmspc_cd varchar(40) NOT NULL;

-- make KRMS_AGENDA_T default to 'Y'
alter table KRMS_AGENDA_T change column actv actv varchar(1) DEFAULT 'Y' NOT NULL;

-- make KRMS_PROP_T.typ_id nullable 
alter table KRMS_PROP_T change column typ_id typ_id varchar(40) DEFAULT NULL;

-- change KRMS_RULE_T.descr_txt to desc_t for consistency
alter table KRMS_RULE_T change column descr_txt desc_txt varchar(4000) DEFAULT NULL;




-- 
-- mysql-2011-06-13-m6.sql
-- 


update KREW_DOC_TYP_T set post_prcsr = 'org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor' where post_prcsr = 'org.kuali.rice.kns.workflow.postprocessor.KualiPostProcessor';

update KREW_RULE_ATTR_T set cls_nm = 'org.kuali.rice.krad.workflow.attribute.KualiXmlSearchableAttributeImpl' where cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXmlSearchableAttributeImpl';

update KREW_RULE_ATTR_T set cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXMLBooleanTranslatorSearchableAttributeImpl' where cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXMLBooleanTranslatorSearchableAttributeImpl';

update KREW_RULE_ATTR_T set cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXmlRuleAttributeImpl' where cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXmlRuleAttributeImpl';


-- 
-- mysql-2011-06-17-m6.sql
-- 


ALTER TABLE KREW_DOC_TYP_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_T CHANGE PARNT_ID PARNT_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_T CHANGE PREV_DOC_TYP_VER_NBR PREV_DOC_TYP_VER_NBR VARCHAR(40);
ALTER TABLE KREW_DOC_HDR_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_PLCY_RELN_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_APP_DOC_STAT_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_ATTR_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40) NOT NULL;
ALTER TABLE KREW_RTE_NODE_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_PROC_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40) NOT NULL;




-- 
-- mysql-2011-06-21.sql
-- 


alter table KRIM_PERM_TMPL_T change column NMSPC_CD NMSPC_CD varchar(40) not null;
alter table KRIM_PERM_TMPL_T change column NM NM varchar(100) not null;
alter table KRIM_PERM_TMPL_T add constraint KRIM_PERM_TMPL_TC1 unique (NM, NMSPC_CD);

alter table KRIM_RSP_TMPL_T change column NMSPC_CD NMSPC_CD varchar(40) not null;
alter table KRIM_RSP_TMPL_T change column NM NM varchar(100) not null;
alter table KRIM_RSP_TMPL_T add constraint KRIM_RSP_TMPL_TC1 unique (NM, NMSPC_CD);


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
    , UNIQUE INDEX KRMS_CTGRY_TC0 (NM, NMSPC_CD)
)ENGINE = InnoDB;

CREATE TABLE KRMS_TERM_SPEC_CTGRY_T
(
  TERM_SPEC_ID VARCHAR(40) NOT NULL
      , CTGRY_ID VARCHAR(40) NOT NULL
  , PRIMARY KEY (TERM_SPEC_ID, CTGRY_ID)
  , constraint KRMS_TERM_Spec_ctgry_fk1 foreign key (term_spec_id) references KRMS_TERM_SPEC_T (term_spec_id)
  , constraint KRMS_TERM_Spec_ctgry_fk2 foreign key (ctgry_id) references KRMS_CTGRY_T (ctgry_id)
);

CREATE TABLE KRMS_FUNC_CTGRY_T
(
  FUNC_ID VARCHAR(40) NOT NULL
  , CTGRY_ID VARCHAR(40) NOT NULL
  , PRIMARY KEY (FUNC_ID, CTGRY_ID)
  , constraint krms_func_ctgry_fk1 foreign key (func_id) references KRMS_FUNC_T (func_id)
  , constraint krms_func_ctgry_fk2 foreign key (ctgry_id) references KRMS_CTGRY_T (ctgry_id)
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
alter table KRIM_PERM_T add constraint KRIM_PERM_T_TC1 unique (NM, NMSPC_CD);

alter table KRIM_RSP_T change column NMSPC_CD NMSPC_CD varchar(40) not null;
alter table KRIM_RSP_T change column NM NM varchar(100) not null;
alter table KRIM_RSP_T add constraint KRIM_RSP_T_TC1 unique (NM, NMSPC_CD);


-- 
-- mysql-2011-07-07-m6.sql
-- 


ALTER TABLE KREW_DOC_NTE_T CHANGE DOC_NTE_ID DOC_NTE_ID VARCHAR(40);

ALTER TABLE KREW_ATT_T CHANGE ATTACHMENT_ID ATTACHMENT_ID VARCHAR(40);
ALTER TABLE KREW_ATT_T CHANGE NTE_ID NTE_ID VARCHAR(40);

ALTER TABLE KREW_ATT_T ADD INDEX KREW_ATT_TI1 ( NTE_ID );

ALTER TABLE KREW_ACTN_ITM_T CHANGE ACTN_ITM_ID ACTN_ITM_ID VARCHAR(40);
ALTER TABLE KREW_ACTN_ITM_T CHANGE ACTN_RQST_ID ACTN_RQST_ID VARCHAR(40) NOT NULL;
ALTER TABLE KREW_ACTN_ITM_T CHANGE RSP_ID RSP_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_ACTN_TKN_T CHANGE ACTN_TKN_ID ACTN_TKN_ID VARCHAR(40);

ALTER TABLE KREW_ACTN_RQST_T CHANGE ACTN_RQST_ID ACTN_RQST_ID VARCHAR(40); 
ALTER TABLE KREW_ACTN_RQST_T CHANGE PARNT_ID PARNT_ID VARCHAR(40);
ALTER TABLE KREW_ACTN_RQST_T CHANGE RSP_ID RSP_ID VARCHAR(40) NOT NULL;
ALTER TABLE KREW_ACTN_RQST_T CHANGE ACTN_TKN_ID ACTN_TKN_ID VARCHAR(40);
ALTER TABLE KREW_ACTN_RQST_T CHANGE RULE_ID RULE_ID VARCHAR(40);
ALTER TABLE KREW_ACTN_RQST_T CHANGE RTE_NODE_INSTN_ID RTE_NODE_INSTN_ID VARCHAR(40);

ALTER TABLE KREW_RULE_TMPL_T CHANGE RULE_TMPL_ID RULE_TMPL_ID VARCHAR(40);
ALTER TABLE KREW_RULE_TMPL_T CHANGE DLGN_RULE_TMPL_ID DLGN_RULE_TMPL_ID VARCHAR(40);

ALTER TABLE KREW_RULE_TMPL_ATTR_T CHANGE RULE_TMPL_ATTR_ID RULE_TMPL_ATTR_ID VARCHAR(40);
ALTER TABLE KREW_RULE_TMPL_ATTR_T CHANGE RULE_TMPL_ID RULE_TMPL_ID VARCHAR(40) NOT NULL;
ALTER TABLE KREW_RULE_TMPL_ATTR_T CHANGE RULE_ATTR_ID RULE_ATTR_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RULE_T CHANGE RULE_ID RULE_ID VARCHAR(40);
ALTER TABLE KREW_RULE_T CHANGE RULE_TMPL_ID RULE_TMPL_ID VARCHAR(40);

-- Have to deal w/ index & constraint changes here
ALTER TABLE KREW_RULE_T DROP FOREIGN KEY KREW_RULE_TR1;
ALTER TABLE KREW_RULE_T DROP KEY KREW_RULE_TR1;

ALTER TABLE KREW_RULE_T CHANGE RULE_EXPR_ID RULE_EXPR_ID VARCHAR(40);

ALTER TABLE KREW_RULE_T CHANGE PREV_RULE_VER_NBR PREV_RULE_VER_NBR VARCHAR(40);

ALTER TABLE KREW_DLGN_RSP_T CHANGE DLGN_RULE_ID DLGN_RULE_ID VARCHAR(40);
ALTER TABLE KREW_DLGN_RSP_T CHANGE RSP_ID RSP_ID VARCHAR(40) NOT NULL;
ALTER TABLE KREW_DLGN_RSP_T CHANGE DLGN_RULE_BASE_VAL_ID DLGN_RULE_BASE_VAL_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RULE_RSP_T CHANGE RULE_RSP_ID RULE_RSP_ID VARCHAR(40);
ALTER TABLE KREW_RULE_RSP_T CHANGE RSP_ID RSP_ID VARCHAR(40) NOT NULL;
ALTER TABLE KREW_RULE_RSP_T CHANGE RULE_ID RULE_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RULE_EXT_T CHANGE RULE_EXT_ID RULE_EXT_ID VARCHAR(40);
ALTER TABLE KREW_RULE_EXT_T CHANGE RULE_TMPL_ATTR_ID RULE_TMPL_ATTR_ID VARCHAR(40) NOT NULL;
ALTER TABLE KREW_RULE_EXT_T CHANGE RULE_ID RULE_ID VARCHAR(40) NOT NULL;

-- Have to deal w/ index & constraint changes before KREW_RTE_NODE_T.RTE_NODE_ID is converted
ALTER TABLE KREW_RTE_NODE_CFG_PARM_T DROP FOREIGN KEY EN_RTE_NODE_CFG_PARM_TR1;
ALTER TABLE KREW_RTE_NODE_CFG_PARM_T DROP KEY KREW_RTE_NODE_CFG_PARM_TI1;

ALTER TABLE KREW_RTE_NODE_T CHANGE RTE_NODE_ID RTE_NODE_ID VARCHAR(40);
ALTER TABLE KREW_RTE_NODE_T CHANGE BRCH_PROTO_ID BRCH_PROTO_ID VARCHAR(40);

ALTER TABLE KREW_RTE_NODE_INSTN_T CHANGE RTE_NODE_INSTN_ID RTE_NODE_INSTN_ID VARCHAR(40);
ALTER TABLE KREW_RTE_NODE_INSTN_T CHANGE RTE_NODE_ID RTE_NODE_ID VARCHAR(40) NOT NULL;
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
ALTER TABLE KREW_RTE_BRCH_ST_T CHANGE RTE_BRCH_ID RTE_BRCH_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RTE_NODE_INSTN_ST_T CHANGE RTE_NODE_INSTN_ST_ID RTE_NODE_INSTN_ST_ID VARCHAR(40);
ALTER TABLE KREW_RTE_NODE_INSTN_ST_T CHANGE RTE_NODE_INSTN_ID RTE_NODE_INSTN_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_DOC_TYP_ATTR_T CHANGE DOC_TYP_ATTRIB_ID DOC_TYP_ATTRIB_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_ATTR_T CHANGE DOC_TYP_ID DOC_TYP_ID VARCHAR(40) NOT NULL;
ALTER TABLE KREW_DOC_TYP_ATTR_T CHANGE RULE_ATTR_ID RULE_ATTR_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RULE_ATTR_T CHANGE RULE_ATTR_ID RULE_ATTR_ID VARCHAR(40);

ALTER TABLE KREW_RULE_TMPL_OPTN_T CHANGE RULE_TMPL_OPTN_ID RULE_TMPL_OPTN_ID VARCHAR(40);
ALTER TABLE KREW_RULE_TMPL_OPTN_T CHANGE RULE_TMPL_ID RULE_TMPL_ID VARCHAR(40);

ALTER TABLE KREW_OUT_BOX_ITM_T CHANGE ACTN_ITM_ID ACTN_ITM_ID VARCHAR(40);
ALTER TABLE KREW_OUT_BOX_ITM_T CHANGE ACTN_RQST_ID ACTN_RQST_ID VARCHAR(40) NOT NULL;
ALTER TABLE KREW_OUT_BOX_ITM_T CHANGE RSP_ID RSP_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RTE_NODE_CFG_PARM_T CHANGE RTE_NODE_ID RTE_NODE_ID VARCHAR(40) NOT NULL;
-- add the key back, re-add the foreign key
ALTER TABLE KREW_RTE_NODE_CFG_PARM_T ADD KEY KREW_RTE_NODE_CFG_PARM_TI1 (RTE_NODE_ID);
-- JHK: removing since not in master datasource
-- ALTER TABLE KREW_RTE_NODE_CFG_PARM_T ADD CONSTRAINT EN_RTE_NODE_CFG_PARM_TR1 FOREIGN KEY KREW_RTE_NODE_CFG_PARM_TI1 (RTE_NODE_ID) REFERENCES KREW_RTE_NODE_T (RTE_NODE_ID)

ALTER TABLE KREW_RTE_NODE_CFG_PARM_T CHANGE RTE_NODE_CFG_PARM_ID RTE_NODE_CFG_PARM_ID VARCHAR(40);


ALTER TABLE KREW_DOC_TYP_PROC_T CHANGE DOC_TYP_PROC_ID DOC_TYP_PROC_ID VARCHAR(40);
ALTER TABLE KREW_DOC_TYP_PROC_T CHANGE INIT_RTE_NODE_ID INIT_RTE_NODE_ID VARCHAR(40);

ALTER TABLE KREW_DOC_LNK_T CHANGE DOC_LNK_ID DOC_LNK_ID VARCHAR(40);

ALTER TABLE KREW_RTE_BRCH_PROTO_T CHANGE RTE_BRCH_PROTO_ID RTE_BRCH_PROTO_ID VARCHAR(40);

ALTER TABLE KREW_HLP_T CHANGE HLP_ID HLP_ID VARCHAR(40);

ALTER TABLE KREW_RULE_EXT_VAL_T CHANGE RULE_EXT_VAL_ID RULE_EXT_VAL_ID VARCHAR(40);
ALTER TABLE KREW_RULE_EXT_VAL_T CHANGE RULE_EXT_ID RULE_EXT_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RULE_EXPR_T CHANGE RULE_EXPR_ID RULE_EXPR_ID VARCHAR(40);
ALTER TABLE KREW_RULE_T ADD CONSTRAINT krew_rule_tr1 FOREIGN KEY KREW_RULE_TR1 (RULE_EXPR_ID) REFERENCES KREW_RULE_EXPR_T (RULE_EXPR_ID);



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
alter table KRMS_CNTXT_T add column desc_txt varchar(255) default null;
alter table KRMS_TERM_SPEC_T add column desc_txt varchar(255) default null;
alter table KRMS_TERM_T add column desc_txt varchar(255) default null;
alter table KRMS_ATTR_DEFN_T add column desc_txt varchar(255) default null;



-- 
-- mysql-2011-07-24-m7.sql
-- 


drop table KREW_HLP_S;
drop table KREW_HLP_T;


-- 
-- mysql-2011-07-25-m7.sql
-- 


drop table KREW_RIA_DOC_T;
drop table KREW_RIA_DOCTYPE_MAP_ID_S;
drop table KREW_RIA_DOCTYPE_MAP_T;

drop table KREW_RMV_RPLC_DOC_T;
drop table KREW_RMV_RPLC_GRP_T;
drop table KREW_RMV_RPLC_RULE_T;


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
-- break direct fk to KRMS_CNTXT_T from KRMS_TERM_RSLVR_T & KRMS_TERM_SPEC_T
--

-- remove cntxt_id from KRMS_TERM_RSLVR_T, fix unique constraint
alter table KRMS_TERM_RSLVR_T drop index KRMS_TERM_RSLVR_TC1;
alter table KRMS_TERM_RSLVR_T add constraint KRMS_TERM_RSLVR_TC1 unique (nm, nmspc_cd);
alter table KRMS_TERM_RSLVR_T drop foreign key KRMS_Term_rslvr_fk2;
alter table KRMS_TERM_RSLVR_T drop column cntxt_id;

-- remove fk from KRMS_TERM_SPEC_T to KRMS_CNTXT_T
alter table KRMS_TERM_SPEC_T add column nmspc_cd varchar(40) not null;
alter table KRMS_TERM_SPEC_T DROP FOREIGN KEY krms_asset_fk1;
alter table KRMS_TERM_SPEC_T drop key KRMS_ASSET_TI1;
alter table KRMS_TERM_SPEC_T drop index KRMS_ASSET_TC1;
alter table KRMS_TERM_SPEC_T add constraint KRMS_TERM_SPEC_TC1 unique (nm, nmspc_cd);
alter table KRMS_TERM_SPEC_T drop column cntxt_id;

--
-- refactor KRMS_CNTXT_TERM_SPEC_PREREQ_T to be a valid term specs table instead
--
-- rename KRMS_CNTXT_TERM_SPEC_PREREQ_T to KRMS_CNTXT_VLD_TERM_SPEC_T
-- and add prereq column
alter table KRMS_CNTXT_TERM_SPEC_PREREQ_T DROP FOREIGN KEY krms_cntxt_asset_prereq_fk1;
alter table KRMS_CNTXT_TERM_SPEC_PREREQ_T drop index KRMS_CNTXT_ASSET_PREREQ_TI1;
alter table KRMS_CNTXT_TERM_SPEC_PREREQ_T DROP FOREIGN KEY krms_cntxt_asset_prereq_fk2;
alter table KRMS_CNTXT_TERM_SPEC_PREREQ_T drop index KRMS_CNTXT_ASSET_PREREQ_TI2;
rename table KRMS_CNTXT_TERM_SPEC_PREREQ_T to KRMS_CNTXT_VLD_TERM_SPEC_T;
alter table KRMS_CNTXT_VLD_TERM_SPEC_T add column prereq varchar(1) default 'n';
alter table KRMS_CNTXT_VLD_TERM_SPEC_T add constraint KRMS_CNTXT_VLD_TERM_SPEC_TI1 foreign key (cntxt_id) references KRMS_CNTXT_T(cntxt_id);
alter table KRMS_CNTXT_VLD_TERM_SPEC_T add constraint KRMS_CNTXT_VLD_TERM_SPEC_TI2 foreign key (term_spec_id) references KRMS_TERM_SPEC_T(term_spec_id);

--
-- set up some missing unique constraints
--
-- wow, Oracle and MySQL support the same syntax here
alter table KRMS_CNTXT_T add constraint KRMS_CNTXT_TC1 unique (nm, nmspc_cd);
alter table KRMS_FUNC_T add constraint KRMS_FUNC_TC1 unique (nm, nmspc_cd);

-- drop namespace code from KRMS_AGENDA_T
alter table KRMS_AGENDA_T drop column nmspc_cd;
alter table KRMS_AGENDA_T add constraint KRMS_AGENDA_TC1 unique (nm, cntxt_id);

alter table KRMS_TYP_T add constraint KRMS_TYP_TC1 unique (nm, nmspc_cd);
alter table KRMS_ATTR_DEFN_T add constraint KRMS_ATTR_DEFN_TC1 unique (nm, nmspc_cd);
alter table KRMS_RULE_T add constraint KRMS_RULE_TC1 unique (nm, nmspc_cd);

--
-- clean up some crufty index and constraint names
--

alter table KRMS_TERM_RSLVR_ATTR_T drop foreign key krms_asset_rslvr_attr_fk1;
alter table KRMS_TERM_RSLVR_ATTR_T drop foreign key krms_asset_rslvr_attr_fk2;
alter table KRMS_TERM_RSLVR_ATTR_T drop index KRMS_ASSET_RSLVR_ATTR_TI1;
create index KRMS_TERM_RSLVR_ATTR_TI1 on KRMS_TERM_RSLVR_ATTR_T (term_rslvr_id);
alter table KRMS_TERM_RSLVR_ATTR_T drop index KRMS_ASSET_RSLVR_ATTR_TI2;
create index KRMS_TERM_RSLVR_ATTR_TI2 on KRMS_TERM_RSLVR_ATTR_T (attr_defn_id);
alter table KRMS_TERM_RSLVR_ATTR_T add constraint KRMS_Term_rslvr_attr_fk1 foreign key (term_rslvr_id) references KRMS_TERM_RSLVR_T (term_rslvr_id);
alter table KRMS_TERM_RSLVR_ATTR_T add constraint KRMS_Term_rslvr_attr_fk2 foreign key (attr_defn_id) references KRMS_ATTR_DEFN_T (attr_defn_id);


-- 
-- mysql-2011-08-11.sql
-- 



-- DROP TABLE IF EXISTS KREW_PPL_FLW_ATTR_T ;
-- DROP TABLE IF EXISTS KREW_PPL_FLW_MBR_T ;
-- DROP TABLE IF EXISTS KREW_TYP_ATTR_T ;
-- DROP TABLE IF EXISTS KREW_ATTR_DEFN_T ;
-- DROP TABLE IF EXISTS KREW_PPL_FLW_T ;
-- DROP TABLE IF EXISTS KREW_TYP_T ;
-- DROP TABLE IF EXISTS KREW_PPL_FLW_MBR_S ;
-- DROP TABLE IF EXISTS KREW_PPL_FLW_ATTR_S ;
-- DROP TABLE IF EXISTS KREW_PPL_FLW_S ;
-- DROP TABLE IF EXISTS KREW_ATTR_DEFN_S ;
-- DROP TABLE IF EXISTS KREW_TYP_ATTR_S ;
-- DROP TABLE IF EXISTS KREW_TYP_S ;


-- -----------------------------------------------------
-- Table KREW_TYP_T
-- -----------------------------------------------------

CREATE  TABLE KREW_TYP_T (
  typ_id VARCHAR(40) NOT NULL ,
  nm VARCHAR(100) NOT NULL ,
  nmspc_cd VARCHAR(40) NOT NULL ,
  srvc_nm VARCHAR(200) NULL ,
  actv VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (typ_id) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX KREW_TYP_TC1 ON KREW_TYP_T (nm, nmspc_cd) ;


-- -----------------------------------------------------
-- Table KREW_PPL_FLW_T
-- -----------------------------------------------------

CREATE  TABLE KREW_PPL_FLW_T (
  ppl_flw_id VARCHAR(40) NOT NULL ,
  nm VARCHAR(100) NOT NULL ,
  nmspc_cd VARCHAR(40) NOT NULL ,
  typ_id VARCHAR(40) NOT NULL ,
  actv VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  desc_txt VARCHAR(4000) NULL ,
  PRIMARY KEY (ppl_flw_id) ,
  CONSTRAINT krew_ppl_flw_fk1
    FOREIGN KEY (typ_id )
    REFERENCES KREW_TYP_T (typ_id ))
ENGINE = InnoDB;

CREATE UNIQUE INDEX KREW_PPL_FLW_TC1 ON KREW_PPL_FLW_T (nm, nmspc_cd) ;


-- -----------------------------------------------------
-- Table KREW_ATTR_DEFN_T
-- -----------------------------------------------------

CREATE  TABLE KREW_ATTR_DEFN_T (
  attr_defn_id VARCHAR(40) NOT NULL ,
  nm VARCHAR(100) NOT NULL ,
  nmspc_cd VARCHAR(40) NOT NULL ,
  lbl VARCHAR(40) NULL ,
  actv VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  cmpnt_nm VARCHAR(100) NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  desc_txt VARCHAR(40) NULL ,
  PRIMARY KEY (attr_defn_id) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX KREW_ATTR_DEFN_TC1 ON KREW_ATTR_DEFN_T (nm, nmspc_cd) ;


-- -----------------------------------------------------
-- Table KREW_TYP_ATTR_T
-- -----------------------------------------------------

CREATE  TABLE KREW_TYP_ATTR_T (
  typ_attr_id VARCHAR(40) NOT NULL ,
  seq_no DECIMAL(5,0) NOT NULL ,
  typ_id VARCHAR(40) NOT NULL ,
  attr_defn_id VARCHAR(255) NOT NULL ,
  actv VARCHAR(1) NOT NULL DEFAULT 'Y' ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (typ_attr_id) ,
  CONSTRAINT KREW_Typ_attr_fk1
    FOREIGN KEY (attr_defn_id )
    REFERENCES KREW_ATTR_DEFN_T (attr_defn_id ),
  CONSTRAINT KREW_Typ_attr_fk2
    FOREIGN KEY (typ_id )
    REFERENCES KREW_TYP_T (typ_id ))
ENGINE = InnoDB;

CREATE INDEX KREW_TYP_ATTR_TI1 ON KREW_TYP_ATTR_T (attr_defn_id) ;

CREATE INDEX KREW_TYP_ATTR_TI2 ON KREW_TYP_ATTR_T (typ_id) ;

CREATE UNIQUE INDEX KREW_TYP_ATTR_TC1 ON KREW_TYP_ATTR_T (typ_id, attr_defn_id) ;


-- -----------------------------------------------------
-- Table KREW_PPL_FLW_MBR_T
-- -----------------------------------------------------

CREATE  TABLE KREW_PPL_FLW_MBR_T (
  ppl_flw_mbr_id VARCHAR(40) NOT NULL ,
  ppl_flw_id VARCHAR(40) NOT NULL ,
  mbr_typ_cd VARCHAR(1) NOT NULL ,
  mbr_id VARCHAR(40) NOT NULL ,
  prio DECIMAL(8,0) NULL ,
  dlgt_frm_id VARCHAR(40) NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (ppl_flw_mbr_id) ,
  CONSTRAINT krew_ppl_flw_mbr_fk1
    FOREIGN KEY (ppl_flw_id )
    REFERENCES KREW_PPL_FLW_T (ppl_flw_id ),
  CONSTRAINT krew_ppl_flw_mbr_fk2
    FOREIGN KEY (dlgt_frm_id )
    REFERENCES KREW_PPL_FLW_MBR_T (ppl_flw_mbr_id ))
ENGINE = InnoDB;

CREATE INDEX KREW_PPL_FLW_MBR_TI1 ON KREW_PPL_FLW_MBR_T (ppl_flw_id) ;

CREATE INDEX KREW_PPL_FLW_MBR_TI2 ON KREW_PPL_FLW_MBR_T (ppl_flw_id, prio) ;

CREATE UNIQUE INDEX KREW_PPL_FLW_MBR_TC1 ON KREW_PPL_FLW_MBR_T (ppl_flw_id, mbr_typ_cd, mbr_id, dlgt_frm_id) ;

CREATE INDEX krew_ppl_flw_mbr_ti3 ON KREW_PPL_FLW_MBR_T (dlgt_frm_id) ;


-- -----------------------------------------------------
-- Table KREW_PPL_FLW_ATTR_T
-- -----------------------------------------------------

CREATE  TABLE KREW_PPL_FLW_ATTR_T (
  ppl_flw_attr_id VARCHAR(40) NOT NULL ,
  ppl_flw_id VARCHAR(40) NOT NULL ,
  attr_defn_id VARCHAR(40) NOT NULL ,
  attr_val VARCHAR(400) NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (ppl_flw_attr_id) ,
  CONSTRAINT krew_ppl_flw_attr_fk1
    FOREIGN KEY (ppl_flw_id )
    REFERENCES KREW_PPL_FLW_T (ppl_flw_id ),
  CONSTRAINT krew_ppl_flw_attr_fk2
    FOREIGN KEY (attr_defn_id )
    REFERENCES KREW_ATTR_DEFN_T (attr_defn_id ))
ENGINE = InnoDB;

CREATE INDEX KREW_PPL_FLW_ATTR_TI1 ON KREW_PPL_FLW_ATTR_T (ppl_flw_id) ;

CREATE INDEX KREW_PPL_FLW_ATTR_TI2 ON KREW_PPL_FLW_ATTR_T (attr_defn_id) ;


-- -----------------------------------------------------
-- Table KREW_TYP_S
-- -----------------------------------------------------

CREATE  TABLE KREW_TYP_S (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;


-- -----------------------------------------------------
-- Table KREW_TYP_ATTR_S
-- -----------------------------------------------------

CREATE  TABLE KREW_TYP_ATTR_S (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;


-- -----------------------------------------------------
-- Table KREW_ATTR_DEFN_S
-- -----------------------------------------------------

CREATE  TABLE KREW_ATTR_DEFN_S (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;


-- -----------------------------------------------------
-- Table KREW_PPL_FLW_S
-- -----------------------------------------------------

CREATE  TABLE KREW_PPL_FLW_S (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;


-- -----------------------------------------------------
-- Table KREW_PPL_FLW_ATTR_S
-- -----------------------------------------------------

CREATE  TABLE KREW_PPL_FLW_ATTR_S (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;


-- -----------------------------------------------------
-- Table KREW_PPL_FLW_MBR_S
-- -----------------------------------------------------

CREATE  TABLE KREW_PPL_FLW_MBR_S (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;




-- 
-- mysql-2011-08-17.sql
-- 


delete from KRCR_PARM_T
where nmspc_cd = 'KR-NS'
and cmpnt_cd = 'All'
and parm_nm in ('STRING_TO_DATE_FORMATS', 'STRING_TO_TIMESTAMP_FORMATS', 'TIMESTAMP_TO_STRING_FORMAT_FOR_USER_INTERFACE', 'DATE_TO_STRING_FORMAT_FOR_FILE_NAME', 'TIMESTAMP_TO_STRING_FORMAT_FOR_FILE_NAME')
;


-- 
-- mysql-2011-08-29.sql
-- 


delete from KRCR_PARM_T where PARM_NM = 'CACHING_IND'
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
alter table KREW_PPL_FLW_MBR_T DROP COLUMN dlgt_frm_id;

drop INDEX KREW_PPL_FLW_MBR_TC1 ON KREW_PPL_FLW_MBR_T;

-- -----------------------------------------------------
-- Table KREW_PPL_FLW_DLGT_T
-- -----------------------------------------------------

CREATE  TABLE KREW_PPL_FLW_DLGT_T (
  ppl_flw_dlgt_id VARCHAR(40) NOT NULL ,
  ppl_flw_mbr_id VARCHAR(40) NOT NULL ,
  mbr_id VARCHAR(40) NOT NULL ,
  mbr_typ_cd VARCHAR(1) NOT NULL ,
  dlgn_typ_cd VARCHAR(1) NOT NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (ppl_flw_dlgt_id) ,
  CONSTRAINT krew_ppl_flw_dlgt_fk1
    FOREIGN KEY (ppl_flw_mbr_id )
    REFERENCES KREW_PPL_FLW_MBR_T (ppl_flw_mbr_id ))
ENGINE = InnoDB;

CREATE INDEX KREW_PPL_FLW_DLGT_TI1 ON KREW_PPL_FLW_DLGT_T (ppl_flw_mbr_id) ;

-- -----------------------------------------------------
-- Table KREW_PPL_FLW_DLGT_S
-- -----------------------------------------------------

CREATE  TABLE KREW_PPL_FLW_DLGT_S (
  id BIGINT(19) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (id) )
ENGINE = MyISAM
AUTO_INCREMENT = 10000;




-- 
-- mysql-2011-09-26b.sql
-- 


-- KIM permissions
insert into KRCR_NMSPC_T
(nmspc_cd, nm, actv_ind, appl_id, ver_nbr, obj_id)
values ('KR-RULE','Kuali Rules','Y','RICE',1,uuid())
;

insert into KRIM_PERM_TMPL_T
(perm_tmpl_id, nm, nmspc_cd, desc_txt, kim_typ_id, actv_ind, ver_nbr, obj_id)
values ((select perm_tmpl_id from
        (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000)
        as tmptable),
        'KRMS Agenda Permission','KR-RULE','View/Maintain Agenda',
        (select kim_typ_id from KRIM_TYP_T where nm = 'Namespace' and nmspc_cd = 'KR-NS'),
        'Y',1,uuid())
;

insert into KRIM_PERM_T
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select perm_id from
        (select (max(cast(perm_id as decimal)) + 1) as perm_id from KRIM_PERM_T where perm_id is not NULL and perm_id REGEXP '^[1-9][0-9]*$' and cast(perm_id as decimal) < 10000)
        as tmptable),
        (select perm_tmpl_id from KRIM_PERM_TMPL_T where nm = 'KRMS Agenda Permission' and nmspc_cd = 'KR-RULE'),
        'KR-RULE','Maintain KRMS Agenda','Allows creation and modification of agendas via the agenda editor','Y',1,uuid())
;

insert into KRIM_PERM_ATTR_DATA_T
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select attr_data_id from
        (select (max(cast(attr_data_id as decimal)) + 1) as attr_data_id from KRIM_PERM_ATTR_DATA_T where attr_data_id is not NULL and attr_data_id REGEXP '^[1-9][0-9]*$' and cast(attr_data_id as decimal) < 10000)
        as tmptable),
        (select perm_id from KRIM_PERM_T where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KR-RULE'),
        (select kim_typ_id from KRIM_TYP_T where nm = 'Namespace' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from KRIM_ATTR_DEFN_T where nm = 'namespaceCode'),
        'KRMS_TEST',1,uuid())
;

-- KIM roles
insert into KRIM_ROLE_T
(role_id, role_nm, nmspc_cd, desc_txt, kim_typ_id, actv_ind, last_updt_dt, obj_id)
values ((select role_id from
        (select (max(cast(role_id as decimal)) + 1) as role_id from KRIM_ROLE_T where role_id is not NULL and role_id REGEXP '^[1-9][0-9]*$' and cast(role_id as decimal) < 10000)
        as tmptable),
        'Kuali Rules Management System Administrator',
        'KR-RULE',
        'This role maintains KRMS agendas and rules.',
        (select kim_typ_id from KRIM_TYP_T where nm = 'Default' and nmspc_cd = 'KUALI'),
        'Y', curdate(), uuid())
;

insert into KRIM_ROLE_MBR_T
(role_mbr_id, role_id, mbr_id, mbr_typ_cd, last_updt_dt, ver_nbr, obj_id)
values ((select role_mbr_id from
        (select (max(cast(role_mbr_id as decimal)) + 1) as role_mbr_id from KRIM_ROLE_MBR_T where role_mbr_id is not NULL and role_mbr_id REGEXP '^[1-9][0-9]*$' and cast(role_mbr_id as decimal) < 10000)
        as tmptable),
        (select role_id from KRIM_ROLE_T where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select prncpl_id from KRIM_PRNCPL_T where prncpl_nm = 'admin'),
        'P', curdate(), 1, uuid())
;

insert into KRIM_ROLE_PERM_T
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select role_perm_id from
        (select (max(cast(role_perm_id as decimal)) + 1) as role_perm_id from KRIM_ROLE_PERM_T where role_perm_id is not NULL and role_perm_id REGEXP '^[1-9][0-9]*$' and cast(role_perm_id as decimal) < 10000)
        as tmptable),
        (select role_id from KRIM_ROLE_T where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select perm_id from KRIM_PERM_T where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KR-RULE'),
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

insert into KRMS_TYP_T
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('1000', 'notificationPeopleFlowActionType', 'KRMS', 'notificationPeopleFlowActionTypeService', 'Y', 1)
;

-- Approval PeopleFlowActionType

insert into KRMS_TYP_T
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('1001', 'approvalPeopleFlowActionType', 'KRMS_TEST', 'approvalPeopleFlowActionTypeService', 'Y', 1)
;



-- 
-- mysql-2011-09-30.sql
-- 



-- -----------------------------------------------------
-- Table `KRMS_CNTXT_VLD_AGENDA_T`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `KRMS_CNTXT_VLD_AGENDA_T` (
  `cntxt_vld_agenda_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `agenda_typ_id` VARCHAR(40) NOT NULL ,
  ver_nbr DECIMAL(8) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_vld_agenda_id`) ,
  INDEX `KRMS_CNTXT_VLD_AGENDA_TI1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_cntxt_vld_agenda_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `KRMS_CNTXT_T` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

create table KRMS_CNTXT_VLD_AGENDA_S ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table KRMS_CNTXT_VLD_AGENDA_S auto_increment = 1000;


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

-- KULRICE-4589
UPDATE KRCR_PARM_T
SET PARM_NM = 'NOTIFY_GROUPS',
    PARM_DESC_TXT = 'Defines a group name (in the format "namespace:name") which contains members who should never receive "notifications" action requests from KEW.',
    EVAL_OPRTR_CD = 'D'
WHERE NMSPC_CD = 'KR-WKFLW'
  AND CMPNT_CD = 'Notification'
  AND PARM_NM = 'NOTIFY_EXCLUDED_USERS_IND';


-- 
-- mysql-2011-10-23.sql
-- 


alter table KRCR_CMPNT_T add column cmpnt_set_id varchar(40);

create table KRCR_CMPNT_SET_T (
  cmpnt_set_id varchar(40) not null,
  last_updt_ts datetime not null,
  chksm varchar(40) not null,
  ver_nbr DECIMAL(8) not null default 0,
  primary key (cmpnt_set_id))
ENGINE = InnoDB;


-- 
-- mysql-2011-10-24.sql
-- 


alter table KRCR_CMPNT_T drop column cmpnt_set_id;

create table KRCR_DRVD_CMPNT_T (
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

insert into KRIM_PERM_T
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select perm_id from
          (select (max(cast(perm_id as decimal)) + 1) as perm_id from KRIM_PERM_T where perm_id is not NULL and perm_id REGEXP '^[1-9][0-9]*$'  and cast(perm_id as decimal) < 10000)
         as tmptable),
        (select perm_tmpl_id from KRIM_PERM_TMPL_T where nm = 'Use Screen' and nmspc_cd = 'KR-NS'),
        'KR-SYS','Use Cache Adminstration Screen','Allows use of the cache administration screen','Y',1,uuid());

insert into KRIM_PERM_ATTR_DATA_T
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select attr_data_id from
          (select (max(cast(attr_data_id as decimal)) + 1) as attr_data_id from KRIM_PERM_ATTR_DATA_T where attr_data_id is not NULL and attr_data_id REGEXP '^[1-9][0-9]*$'  and cast(attr_data_id as decimal) < 10000)
         as tmptable),
        (select perm_id from KRIM_PERM_T where nm = 'Use Cache Adminstration Screen' and nmspc_cd = 'KR-SYS'),
        (select kim_typ_id from KRIM_TYP_T where nm = 'Namespace or Action' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from KRIM_ATTR_DEFN_T where nm = 'actionClass'),
        'org.kuali.rice.core.web.cache.CacheAdminController',1,uuid());

insert into KRIM_ROLE_PERM_T
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select role_perm_id from
          (select (max(cast(role_perm_id as decimal)) + 1) as role_perm_id from KRIM_ROLE_PERM_T where role_perm_id is not NULL and role_perm_id REGEXP '^[1-9][0-9]*$' and cast(role_perm_id as decimal) < 10000)
         as tmptable),
        (select role_id from KRIM_ROLE_T where role_nm = 'Technical Administrator' and nmspc_cd = 'KR-SYS'),
        (select perm_id from KRIM_PERM_T where nm = 'Use Cache Adminstration Screen' and nmspc_cd = 'KR-SYS'),
        'Y', 1, uuid());



-- 
-- mysql-2011-11-03.sql
-- 


-- Make typ_id column optional where appropriate

ALTER TABLE KRMS_RULE_T MODIFY typ_id varchar(40) default null;
ALTER TABLE KRMS_AGENDA_T MODIFY typ_id varchar(40) default null;
ALTER TABLE KRMS_CNTXT_T MODIFY typ_id varchar(40) default null;



-- 
-- mysql-2011-11-14.sql
-- 


-- Drop foreign key constraint on KRMS_PROP_T table
alter table KRMS_PROP_T drop foreign key krms_prop_fk1;




-- 
-- mysql-2011-11-23.sql
-- 


-- give PeopleFlows friendlier names

update KRMS_TYP_T set nm='Notify PeopleFlow' where typ_id = '1000';
update KRMS_TYP_T set nm='Route to PeopleFlow' where typ_id = '1001';

-- remove constraint that is preventing compound props from persisting

alter table KRMS_CMPND_PROP_PROPS_T modify seq_no decimal(5,0) default null;



-- 
-- mysql-2011-11-28.sql
-- 


update KRIM_PERM_T
   set nmspc_cd = 'KRMS_TEST'
 where nm = 'Maintain KRMS Agenda'
   and nmspc_cd = 'KR-RULE'
;

delete from KRIM_PERM_ATTR_DATA_T
 where perm_id = (select perm_id from KRIM_PERM_T where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KRMS_TEST')
;


-- 
-- mysql-2011-11-29.sql
-- 


alter table KREW_RTE_NODE_T modify ACTVN_TYP varchar(1);


-- 
-- mysql-2011-12-07.sql
-- 



-- correct fields in krms test data
update KRMS_PROP_T set cmpnd_op_cd = '&' where cmpnd_op_cd = 'a';
update KRMS_CMPND_PROP_PROPS_T set seq_no = '2' where prop_id = 'P421C';
update KRMS_CMPND_PROP_PROPS_T set seq_no = '3' where prop_id = 'P421D';
update KRMS_CMPND_PROP_PROPS_T set seq_no = '3' where prop_id = 'P502C';

-- move seq_no column from KRMS_CMPND_PROP_PROPS_T pivot table to KRMS_PROP_T table.
alter table KRMS_PROP_T add column cmpnd_seq_no decimal(5,0) default null;

update KRMS_PROP_T, KRMS_CMPND_PROP_PROPS_T set KRMS_PROP_T.cmpnd_seq_no = KRMS_CMPND_PROP_PROPS_T.seq_no
where KRMS_PROP_T.prop_id = KRMS_CMPND_PROP_PROPS_T.prop_id;

alter table KRMS_CMPND_PROP_PROPS_T drop seq_no;



-- 
-- mysql-2011-12-21.sql
-- 


update KRCR_NMSPC_T set APPL_ID = NULL where nmspc_cd = 'KUALI';


-- 
-- mysql-2012-01-03.sql
-- 


INSERT INTO KRCR_NMSPC_T VALUES ('KR-KRAD', uuid(), 1, 'Kuali Rapid Application Development', 'Y', 'RICE');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and KIM_ATTR_DEFN_ID REGEXP '^[1-9][0-9]*$'  and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'viewId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and KIM_ATTR_DEFN_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'actionEvent', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and KIM_ATTR_DEFN_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'collectionPropertyName', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and KIM_ATTR_DEFN_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'fieldId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and KIM_ATTR_DEFN_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'groupId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and KIM_ATTR_DEFN_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'widgetId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and KIM_ATTR_DEFN_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'actionId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where KIM_TYP_ID REGEXP '^[1-9][0-9]*$' AND cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View', 'viewPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where KIM_TYP_ID REGEXP '^[1-9][0-9]*$' AND cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Edit Mode', 'viewEditModePermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where KIM_TYP_ID REGEXP '^[1-9][0-9]*$' AND cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Field', 'viewFieldPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where KIM_TYP_ID REGEXP '^[1-9][0-9]*$' AND cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Group', 'viewGroupPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where KIM_TYP_ID REGEXP '^[1-9][0-9]*$' AND cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Widget', 'viewWidgetPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where KIM_TYP_ID REGEXP '^[1-9][0-9]*$' AND cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Action', 'viewActionPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where KIM_TYP_ID REGEXP '^[1-9][0-9]*$' AND cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Line Field', 'viewLineFieldPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_T VALUES ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where KIM_TYP_ID REGEXP '^[1-9][0-9]*$' AND cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'View Line Action', 'viewLineActionPermissionTypeService', 'Y', 'KR-KRAD');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' AND cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Edit Mode'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Edit Mode'), '10', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='fieldId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'c', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Field'), '6', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Group'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Group'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='groupId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'c', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Group'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='fieldId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Widget'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Widget'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='widgetId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='actionId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'c', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='actionEvent'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Line Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Line Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='groupId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'c', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Line Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='collectionPropertyName'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'd', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Line Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='fieldId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'e', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Line Field'), '6', 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'b', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='groupId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'c', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='collectionPropertyName'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'd', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='actionId'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'e', (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='actionEvent'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Open View', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Edit View', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Use View', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Edit Mode'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'View Field', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Field'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Edit Field', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Field'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'View Group', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Group'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Edit Group', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Group'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'View Widget', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Widget'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Edit Widget', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Widget'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Perform Action', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Action'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'View Line', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Group'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Edit Line', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Group'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'View Line Field', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Line Field'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Edit Line Field', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Line Field'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from KRIM_PERM_TMPL_T where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-KRAD', 'Perform Line Action', null, (select kim_typ_id from KRIM_TYP_T where NMSPC_CD='KR-KRAD' and NM='View Line Action'), 'Y');




-- 
-- mysql-2012-01-04.sql
-- 


update KRMS_TERM_SPEC_T set TYP = 'java.lang.String' where TYP = 'T2';
update KRMS_TERM_SPEC_T set TYP = 'java.lang.String' where TYP = 'T1';
update KRMS_TERM_SPEC_T set TYP = 'java.lang.Integer' where TYP = 'T6';



-- 
-- mysql-2012-01-05.sql
-- 


alter table KRMS_CNTXT_VLD_ACTN_T drop foreign key krms_cntxt_vld_actn_fk1;
drop index KRMS_CNTXT_VLD_ACTN_TI1 on KRMS_CNTXT_VLD_ACTN_T;
rename table KRMS_CNTXT_VLD_ACTN_S to KRMS_CNTXT_VLD_ACTN_TYP_S;
rename table KRMS_CNTXT_VLD_ACTN_T to KRMS_CNTXT_VLD_ACTN_TYP_T;
create index KRMS_CNTXT_VLD_ACTN_TYP_TI1 on KRMS_CNTXT_VLD_ACTN_TYP_T (cntxt_id);
alter table KRMS_CNTXT_VLD_ACTN_TYP_T add constraint KRMS_CNTXT_VLD_ACTN_Typ_fk1
  foreign key (cntxt_id) references KRMS_CNTXT_T (cntxt_id);

alter table KRMS_CNTXT_VLD_AGENDA_T drop foreign key krms_cntxt_vld_agenda_fk1;
drop index KRMS_CNTXT_VLD_AGENDA_TI1 on KRMS_CNTXT_VLD_AGENDA_T;
rename table KRMS_CNTXT_VLD_AGENDA_S to KRMS_CNTXT_VLD_AGENDA_TYP_S;
rename table KRMS_CNTXT_VLD_AGENDA_T to KRMS_CNTXT_VLD_AGENDA_TYP_T;
create index KRMS_CNTXT_VLD_AGENDA_TYP_TI1 on KRMS_CNTXT_VLD_AGENDA_TYP_T (cntxt_id);
alter table KRMS_CNTXT_VLD_AGENDA_TYP_T add constraint KRMS_CNTXT_VLD_AGENDA_Typ_fk1
  foreign key (cntxt_id) references KRMS_CNTXT_T (cntxt_id);

alter table KRMS_CNTXT_VLD_RULE_T drop foreign key krms_cntxt_vld_rule_fk1;
drop index KRMS_CNTXT_VLD_RULE_TI1 on KRMS_CNTXT_VLD_RULE_T;
rename table KRMS_CNTXT_VLD_RULE_S to KRMS_CNTXT_VLD_RULE_TYP_S;
rename table KRMS_CNTXT_VLD_RULE_T to KRMS_CNTXT_VLD_RULE_TYP_T;
create index KRMS_CNTXT_VLD_RULE_TYP_TI1 on KRMS_CNTXT_VLD_RULE_TYP_T (cntxt_id);
alter table KRMS_CNTXT_VLD_RULE_TYP_T add constraint KRMS_CNTXT_VLD_RULE_Typ_fk1
  foreign key (cntxt_id) references KRMS_CNTXT_T (cntxt_id);

alter table KRMS_CNTXT_VLD_RULE_TYP_T change rule_id rule_typ_id varchar(40) not null;

update KRMS_AGENDA_T set typ_id = null where typ_id = 'T5';
update KRMS_RULE_T set typ_id = null;
delete from KRMS_CNTXT_VLD_RULE_TYP_T;


-- 
-- mysql-2012-01-06.sql
-- 


-- KULRICE-6299: New DB index to improve action list performance
create index KREW_ACTN_ITM_TI6 on KREW_ACTN_ITM_T (DLGN_TYP, DLGN_PRNCPL_ID, DLGN_GRP_ID);


-- 
-- mysql-2012-01-11.sql
-- 


-- KULRICE-6452

drop table KRMS_CNTXT_VLD_EVENT_T;
drop table KRMS_CNTXT_VLD_EVENT_S;
rename table KRMS_CNTXT_TERM_SPEC_PREREQ_S to KRMS_CNTXT_VLD_TERM_SPEC_S;


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

create index KRIM_ROLE_PERM_TI2 on KRIM_ROLE_PERM_T (perm_id, actv_ind);
create index KRIM_PERM_TI1 on KRIM_PERM_T (perm_tmpl_id);
create index KRIM_PERM_TI2 on KRIM_PERM_T (perm_tmpl_id, actv_ind);
create index KRIM_PERM_TMPL_TI1 on KRIM_PERM_TMPL_T (nmspc_cd, nm);
create index KRIM_ROLE_MBR_TI2 on KRIM_ROLE_MBR_T (role_id, mbr_id, mbr_typ_cd);
create index KRIM_ROLE_MBR_TI3 on KRIM_ROLE_MBR_T (mbr_id, mbr_typ_cd);


--
-- KRMS Sample (and production) Data
--

-- If you should want to clean out your KRMS tables:
delete from  KRMS_CNTXT_VLD_RULE_TYP_T ;
delete from  KRMS_CNTXT_VLD_FUNC_T ;
delete from  KRMS_TERM_SPEC_CTGRY_T ;
delete from  KRMS_FUNC_CTGRY_T ;
delete from  KRMS_CTGRY_T ;
delete from  KRMS_FUNC_PARM_T ;
delete from  KRMS_FUNC_T ;
delete from  KRMS_TERM_PARM_T ;
delete from  KRMS_TERM_RSLVR_PARM_SPEC_T ;
delete from  KRMS_TERM_T ;
delete from  KRMS_CNTXT_VLD_TERM_SPEC_T ;
delete from  KRMS_TERM_RSLVR_INPUT_SPEC_T ;
delete from  KRMS_TERM_RSLVR_ATTR_T ;
delete from  KRMS_TERM_RSLVR_T ;
delete from  KRMS_TERM_SPEC_T ;
delete from  KRMS_PROP_PARM_T ;
delete from  KRMS_CMPND_PROP_PROPS_T ;
delete from  KRMS_AGENDA_ATTR_T ;
delete from  KRMS_CNTXT_VLD_ACTN_TYP_T ;
delete from  KRMS_CNTXT_VLD_AGENDA_TYP_T ;
delete from  KRMS_CNTXT_ATTR_T ;
delete from  KRMS_RULE_ATTR_T ;
update KRMS_AGENDA_ITM_T set when_true=null;
update KRMS_AGENDA_ITM_T set when_false=null;
update KRMS_AGENDA_ITM_T set always=null;
delete from  KRMS_AGENDA_ITM_T ;
delete from  KRMS_ACTN_ATTR_T ;
delete from  KRMS_ACTN_T ;
delete from  KRMS_TYP_ATTR_T ;
delete from  KRMS_ATTR_DEFN_T ;
delete from  KRMS_AGENDA_T ;
update KRMS_RULE_T set prop_id=null;
delete from  KRMS_PROP_T ;
delete from  KRMS_RULE_T ;
delete from  KRMS_TYP_T;
delete from  KRMS_CNTXT_T ;
delete from KRCR_NMSPC_T where obj_id = '5a83c912-94b9-4b4d-ac3f-88c53380a4a3';


--
-- Copyright 2005-2012 The Kuali Foundation
--
--

-- Notification PeopleFlowActionType

insert into KRMS_TYP_T
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('1000', 'Notify PeopleFlow', 'KR-RULE', 'notificationPeopleFlowActionTypeService', 'Y', 1)
;

-- Approval PeopleFlowActionType

insert into KRMS_TYP_T
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('1001', 'Route to PeopleFlow', 'KR-RULE', 'approvalPeopleFlowActionTypeService', 'Y', 1)
;


-- add a PeopleFlow attribute to the PeopleFlow types
insert into KRMS_ATTR_DEFN_T (ATTR_DEFN_ID, NM, NMSPC_CD, LBL, CMPNT_NM, DESC_TXT)
values ('1000', 'peopleFlowId', 'KR-RULE', 'PeopleFlow', null,
'An identifier for a PeopleFlow')
;
insert into KRMS_TYP_ATTR_T (TYP_ATTR_ID, SEQ_NO, TYP_ID, ATTR_DEFN_ID) values ('1000', 1, '1000', '1000');
insert into KRMS_TYP_ATTR_T (TYP_ATTR_ID, SEQ_NO, TYP_ID, ATTR_DEFN_ID) values ('1001', 1, '1001', '1000');



-- General validation rule type
insert into KRMS_TYP_T (typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr) values('1002', 'Validation Rule', 'KR-RULE', 'validationRuleTypeService', 'Y', 1);

-- General validation action type
insert into KRMS_TYP_T (typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr) values('1003', 'Validation Action', 'KR-RULE', 'validationActionTypeService', 'Y', 1);

-- Invalid rule
insert into KRMS_ATTR_DEFN_T (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1001', 'Invalid Rule', 'KR-RULE', 'Invalid Rule', 'Y', null, 1, 'If true, execute the action');

-- Valid rule
insert into KRMS_ATTR_DEFN_T (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1002', 'Valid Rule', 'KR-RULE', 'Valid Rule', 'Y', null, 1, 'If false, execute the action');

-- warning action
insert into KRMS_ATTR_DEFN_T (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1003', 'Warning Action', 'KR-RULE', 'Warning Action', 'Y', null, 1, 'Warning');

-- error action
insert into KRMS_ATTR_DEFN_T (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1004', 'Error Action', 'KR-RULE', 'Error Action', 'Y', null, 1, 'Error');

-- action message
insert into KRMS_ATTR_DEFN_T (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1005', 'Action Message', 'KR-RULE', 'Action Message', 'Y', null, 1, 'Message validation action returns');

-- The General action type attribute
insert into KRMS_TYP_ATTR_T (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1004', 1, '1003', '1003', 'Y', 1);
insert into KRMS_TYP_ATTR_T (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1005', 2, '1003', '1004', 'Y', 1);
insert into KRMS_TYP_ATTR_T (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1006', 3, '1003', '1005', 'Y', 1);



-- change permisison to use new krms test namespace
update KRIM_PERM_ATTR_DATA_T set attr_val = 'KR-RULE-TEST' where attr_val = 'KRMS_TEST';

update KRIM_PERM_T
   set nmspc_cd = 'KR-RULE-TEST'
 where nm = 'Maintain KRMS Agenda'
   and nmspc_cd = 'KR-RULE'
;

update KRIM_PERM_T
   set nmspc_cd = 'KR-RULE-TEST'
 where nm = 'Maintain KRMS Agenda'
   and nmspc_cd = 'KRMS_TEST'
;

delete from KRIM_PERM_ATTR_DATA_T
 where perm_id = (select perm_id from KRIM_PERM_T where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KRMS_TEST');


-- set sequence values up to 10000

alter table KRMS_ACTN_ATTR_S auto_increment = 10000;
alter table KRMS_ACTN_S auto_increment = 10000;
alter table KRMS_AGENDA_ATTR_S auto_increment = 10000;
alter table KRMS_AGENDA_ITM_S auto_increment = 10000;
alter table KRMS_AGENDA_S auto_increment = 10000;
alter table KRMS_ATTR_DEFN_S auto_increment = 10000;
alter table KRMS_CMPND_PROP_PROPS_S auto_increment = 10000;
alter table KRMS_CNTXT_ATTR_S auto_increment = 10000;
alter table KRMS_CNTXT_S auto_increment = 10000;
alter table KRMS_CNTXT_VLD_ACTN_TYP_S auto_increment = 10000;
alter table KRMS_CNTXT_VLD_AGENDA_TYP_S auto_increment = 10000;
alter table KRMS_CNTXT_VLD_FUNC_S auto_increment = 10000;
alter table KRMS_CNTXT_VLD_RULE_TYP_S auto_increment = 10000;
alter table KRMS_CNTXT_VLD_TERM_SPEC_S auto_increment = 10000;
alter table KRMS_CTGRY_S auto_increment = 10000;
alter table KRMS_FUNC_PARM_S auto_increment = 10000;
alter table KRMS_FUNC_S auto_increment = 10000;
alter table KRMS_PROP_PARM_S auto_increment = 10000;
alter table KRMS_PROP_S auto_increment = 10000;
alter table KRMS_RULE_ATTR_S auto_increment = 10000;
alter table KRMS_RULE_S auto_increment = 10000;
alter table KRMS_TERM_PARM_S auto_increment = 10000;
alter table KRMS_TERM_RSLVR_ATTR_S auto_increment = 10000;
alter table KRMS_TERM_RSLVR_INPUT_SPEC_S auto_increment = 10000;
alter table KRMS_TERM_RSLVR_PARM_SPEC_S auto_increment = 10000;
alter table KRMS_TERM_RSLVR_S auto_increment = 10000;
alter table KRMS_TERM_S auto_increment = 10000;
alter table KRMS_TERM_SPEC_S auto_increment = 10000;
alter table KRMS_TYP_ATTR_S auto_increment = 10000;
alter table KRMS_TYP_S auto_increment = 10000;



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

ALTER TABLE KRCR_STYLE_T DROP INDEX KREW_STYLE_TC0;

ALTER TABLE KRCR_STYLE_T ADD CONSTRAINT UNIQUE INDEX KRCR_STYLE_TC0 ( OBJ_ID );

--
-- mysql-2010-01-24.sql
--


-- unset type on My Fabulous Agenda since the required attribute isn't specified
UPDATE KRMS_AGENDA_T SET TYP_ID=null WHERE AGENDA_ID='T1000'
;

-- PeopleFlow Name
insert into KRMS_ATTR_DEFN_T (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt)
values ('1006', 'peopleFlowName', 'KR_RULE', 'PeopleFlow Name', 'Y', null, 1, 'PeopleFlow Name')
;
insert into KRMS_TYP_ATTR_T (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr)
values ('1007', 3, '1000', '1006', 'Y', 1)
;
insert into KRMS_TYP_ATTR_T (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr)
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
-- mysql-2012-02-08.sql
--

insert into KRIM_PERM_T
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select perm_id from
          (select (max(cast(perm_id as decimal)) + 1) as perm_id from KRIM_PERM_T where perm_id is not NULL and perm_id REGEXP '^[1-9][0-9]*$' and cast(perm_id as decimal) < 10000)
         as tmptable),
        (select perm_tmpl_id from KRIM_PERM_TMPL_T where nm = 'Create / Maintain Record(s)' and nmspc_cd = 'KR-NS'),
        'KR-NS','Create Term Maintenance Document','Allows user to create a new Term maintainence document','Y',1,
        '0dbce939-4f22-4e9b-a4bb-1615c0f411a2');

insert into KRIM_PERM_ATTR_DATA_T
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select attr_data_id from
          (select (max(cast(attr_data_id as decimal)) + 1) as attr_data_id from KRIM_PERM_ATTR_DATA_T where attr_data_id is not NULL and attr_data_id REGEXP '^[1-9][0-9]*$' and cast(attr_data_id as decimal) < 10000)
         as tmptable),
        (select perm_id from KRIM_PERM_T where nm = 'Create Term Maintenance Document' and nmspc_cd = 'KR-NS'),
        (select kim_typ_id from KRIM_TYP_T where nm = 'Document Type & Existing Records Only' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from KRIM_ATTR_DEFN_T where nm = 'documentTypeName'),
        'TermMaintenanceDocument',1,'aa1d1400-6155-4819-8bad-e5dd81f9871b');

insert into KRIM_ROLE_PERM_T
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select role_perm_id from
          (select (max(cast(role_perm_id as decimal)) + 1) as role_perm_id from KRIM_ROLE_PERM_T where role_perm_id is not NULL and role_perm_id REGEXP '^[1-9][0-9]*$' and cast(role_perm_id as decimal) < 10000)
         as tmptable),
        (select role_id from KRIM_ROLE_T where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select perm_id from KRIM_PERM_T where nm = 'Create Term Maintenance Document' and nmspc_cd = 'KR-NS'),
        'Y', 1, '45f8f55e-23d9-4278-ade8-ddfc870852e6');


insert into KRIM_PERM_T
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select perm_id from
          (select (max(cast(perm_id as decimal)) + 1) as perm_id from KRIM_PERM_T where perm_id is not NULL and perm_id REGEXP '^[1-9][0-9]*$' and cast(perm_id as decimal) < 10000)
         as tmptable),
        (select perm_tmpl_id from KRIM_PERM_TMPL_T where nm = 'Create / Maintain Record(s)' and nmspc_cd = 'KR-NS'),
        'KR-NS','Create Context Maintenance Document','Allows user to create a new Context maintainence document','Y',1,
        'cefeed6d-b5e2-40aa-9034-137db317b532');

insert into KRIM_PERM_ATTR_DATA_T
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select attr_data_id from
          (select (max(cast(attr_data_id as decimal)) + 1) as attr_data_id from KRIM_PERM_ATTR_DATA_T where attr_data_id is not NULL and attr_data_id REGEXP '^[1-9][0-9]*$' and cast(attr_data_id as decimal) < 10000)
         as tmptable),
        (select perm_id from KRIM_PERM_T where nm = 'Create Context Maintenance Document' and nmspc_cd = 'KR-NS'),
        (select kim_typ_id from KRIM_TYP_T where nm = 'Document Type & Existing Records Only' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from KRIM_ATTR_DEFN_T where nm = 'documentTypeName'),
        'ContextMaintenanceDocument',1,'c43bc7f5-949e-4a85-b173-6a53d81f2762');

insert into KRIM_ROLE_PERM_T
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select role_perm_id from
          (select (max(cast(role_perm_id as decimal)) + 1) as role_perm_id from KRIM_ROLE_PERM_T where role_perm_id is not NULL and role_perm_id REGEXP '^[1-9][0-9]*$' and cast(role_perm_id as decimal) < 10000)
         as tmptable),
        (select role_id from KRIM_ROLE_T where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select perm_id from KRIM_PERM_T where nm = 'Create Context Maintenance Document' and nmspc_cd = 'KR-NS'),
        'Y', 1, 'cd7cbc67-c0b2-4785-afa8-8c8d073b78df');

insert into KRIM_PERM_T
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select perm_id from
          (select (max(cast(perm_id as decimal)) + 1) as perm_id from KRIM_PERM_T where perm_id is not NULL and perm_id REGEXP '^[1-9][0-9]*$'  and cast(perm_id as decimal) < 10000)
         as tmptable),
        (select perm_tmpl_id from KRIM_PERM_TMPL_T where nm = 'Create / Maintain Record(s)' and nmspc_cd = 'KR-NS'),
        'KR-NS','Create TermSpecification Maintenance Document','Allows user to create a new TermSpecification maintainence document','Y',1,
        '02bd9acd-48d9-4fec-acbd-6a441c5ea8c2');

insert into KRIM_PERM_ATTR_DATA_T
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select attr_data_id from
          (select (max(cast(attr_data_id as decimal)) + 1) as attr_data_id from KRIM_PERM_ATTR_DATA_T where attr_data_id is not NULL and attr_data_id REGEXP '^[1-9][0-9]*$' and cast(attr_data_id as decimal) < 10000)
         as tmptable),
        (select perm_id from KRIM_PERM_T where nm = 'Create TermSpecification Maintenance Document' and nmspc_cd = 'KR-NS'),
        (select kim_typ_id from KRIM_TYP_T where nm = 'Document Type & Existing Records Only' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from KRIM_ATTR_DEFN_T where nm = 'documentTypeName'),
        'TermSpecificationMaintenanceDocument',1,'d3e373ca-296b-4834-bd66-ba159ebe733a');

insert into KRIM_ROLE_PERM_T
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select role_perm_id from
          (select (max(cast(role_perm_id as decimal)) + 1) as role_perm_id from KRIM_ROLE_PERM_T where role_perm_id is not NULL and role_perm_id REGEXP '^[1-9][0-9]*$' and cast(role_perm_id as decimal) < 10000)
         as tmptable),
        (select role_id from KRIM_ROLE_T where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select perm_id from KRIM_PERM_T where nm = 'Create TermSpecification Maintenance Document' and nmspc_cd = 'KR-NS'),
        'Y', 1, '83a270a0-1cdb-4440-ab8b-41cd8afc41d9');
--
-- mysql-2012-02-23.sql
--

--
-- KULRICE-6811: Conversion of WorkflowFunctions to EDLFunctions in eDocLite stylesheets
--

UPDATE KRCR_STYLE_T set XML=replace(XML,'org.kuali.rice.kew.edl.WorkflowFunctions','org.kuali.rice.edl.framework.util.EDLFunctions');

