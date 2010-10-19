INSERT INTO `KRNS_PARM_T` (`appl_nmspc_cd`, `cons_cd`, `nmspc_cd`, `parm_dtl_typ_cd`, `parm_nm`, `obj_id`, `parm_desc_txt`, `ver_nbr`, `txt`, `parm_typ_cd`) VALUES ('KFS', 'D', 'KFS-FP', 'CreditCardReceipt', 'OBJECT_TYPES', sys_guid(), 'Object Types restricted from use on the Credit Card Receipt document', 1, 'IC;ES', 'VALID');


-- Changeset updates/2010-09-24-6011-1-KRNS_PARM_T.xml::6011-1-2::Dan::(MD5Sum: c9743fe568d6b17bd6aeb9a4088d41)
-- Credit Card Type Documented Business Rules are not parameters
INSERT INTO `KRNS_PARM_T` (`appl_nmspc_cd`, `cons_cd`, `nmspc_cd`, `parm_dtl_typ_cd`, `parm_nm`, `obj_id`, `parm_desc_txt`, `ver_nbr`, `txt`, `parm_typ_cd`) VALUES ('KFS', 'D', 'KFS-FP', 'CreditCardReceipt', 'OBJECT_CONSOLIDATIONS',  sys_guid(), 'Object Consolidations restricted from use on the Credit Card Receipt document', 1, 'FDBL', 'VALID');


-- Changeset updates/2010-09-24-6012-1-KRNS_PARM_T.xml::6012-1-1::Dan::(MD5Sum: bb4e446b94d89484f6cb5a2962e10e2)
-- Advance Deposit - documented business rules are not parameters and not in code
INSERT INTO `KRNS_PARM_T` (`appl_nmspc_cd`, `cons_cd`, `nmspc_cd`, `parm_dtl_typ_cd`, `parm_nm`, `obj_id`, `parm_desc_txt`, `ver_nbr`, `txt`, `parm_typ_cd`) VALUES ('KFS', 'D', 'KFS-FP', 'AdvanceDeposit', 'OBJECT_SUB_TYPES',  sys_guid(), 'Object Sub-Types restricted from use on the Advance Deposit document', 1, 'CA;BU;FB;PL;MT;CE;FR;HW;RE;SA;VA', 'VALID');


-- Changeset updates/2010-09-24-6012-1-KRNS_PARM_T.xml::6012-1-2::Dan::(MD5Sum: c090e468f2e1be3b75af2db1924189f)
-- Advance Deposit - documented business rules are not parameters and not in code
INSERT INTO `KRNS_PARM_T` (`appl_nmspc_cd`, `cons_cd`, `nmspc_cd`, `parm_dtl_typ_cd`, `parm_nm`, `obj_id`, `parm_desc_txt`, `ver_nbr`, `txt`, `parm_typ_cd`) VALUES ('KFS', 'D', 'KFS-FP', 'AdvanceDeposit', 'OBJECT_TYPES',  sys_guid(), 'Object Types restricted from use on the Advance Deposit document', 1, 'ES;IC', 'VALID');


-- Changeset updates/2010-09-24-6012-1-KRNS_PARM_T.xml::6012-1-3::Dan::(MD5Sum: 3b41ad37dc9dc1c634dbf65aba7b8c7)
-- Advance Deposit - documented business rules are not parameters and not in code
INSERT INTO `KRNS_PARM_T` (`appl_nmspc_cd`, `cons_cd`, `nmspc_cd`, `parm_dtl_typ_cd`, `parm_nm`, `obj_id`, `parm_desc_txt`, `ver_nbr`, `txt`, `parm_typ_cd`) VALUES ('KFS', 'D', 'KFS-FP', 'AdvanceDeposit', 'OBJECT_CONSOLIDATIONS',  sys_guid(), 'Object Consolidations restricted from use on the Advance Deposit document', 1, 'FDBL', 'VALID');


-- Changeset updates/2010-09-24-6025-1-KRNS_PARM_T.xml::6025-1-1::Dan::(MD5Sum: c8595ab69b4d9188ee29e26842825df)
-- Add Parameter for tax zip code parsing
INSERT INTO `KRNS_PARM_T` (`appl_nmspc_cd`, `cons_cd`, `nmspc_cd`, `parm_dtl_typ_cd`, `parm_nm`, `obj_id`, `parm_desc_txt`, `ver_nbr`, `txt`, `parm_typ_cd`) VALUES ('KFS', 'A', 'KFS-FP', 'SalesTax', 'POSTAL_CODE_DIGITS_PASSED_TO_SALES_TAX_REGION_SERVICE',  sys_guid(), 'This determines the number of digits from the start that the Tax Service will use from the Postal Code to determine what Tax Regions to use For example,this is commonly set to 5 in the US,in order to ignore the dash and +4 digits in the ZIP+4 formatted postal codes This value can be set to blank to indicate to the system not to truncate the postal codes', 1, '5', 'CONFG');
