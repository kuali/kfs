-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/28/10 12:39 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-28-5844-1-KRNS_PARM_T.xml::5844-1-1::Dan::(MD5Sum: 5a506568f41ced9f6265a9593ac6b940)
-- The follow parameter needs to be added to support UA's contribution for KFSMI-5844:
INSERT INTO `KRNS_PARM_T` (`appl_nmspc_cd`, `cons_cd`, `nmspc_cd`, `parm_dtl_typ_cd`, `parm_nm`, `obj_id`, `parm_desc_txt`, `ver_nbr`, `txt`, `parm_typ_cd`) VALUES ('KFS', 'A', 'KFS-PURAP', 'ElectronicInvoiceReject', 'SUPPRESS_REJECT_REASON_CODES_ON_EIRT_APPROVAL', uuid(), 'Specifies which types of EIRT reject reasons should be ignored when an EIRT document is Approved', 1, 'AGUV', 'CONFG');


-- Release Database Lock

-- Release Database Lock

