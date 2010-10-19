-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 10/4/10 8:58 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-10-04-6045-1-KRNS_PARM_T.xml::6045-1-1::Winston::(MD5Sum: 26513e1d43ba70ff35a9c17049a93e)
-- New Parameter for PDF Image Location URL
INSERT INTO `KRNS_PARM_T` (`appl_nmspc_cd`, `cons_cd`, `nmspc_cd`, `parm_dtl_typ_cd`, `parm_nm`, `obj_id`, `parm_desc_txt`, `ver_nbr`, `txt`, `parm_typ_cd`) VALUES ('KFS', 'A', 'KFS-PURAP', 'Document', 'PDF_IMAGE_LOCATION_URL', uuid(), 'The URL used by the pdf generation process to find the images managed by Purchasing', 1, '', 'CONFG');


-- Release Database Lock

-- Release Database Lock

