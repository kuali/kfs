-- Changeset updates/2010-07-22-5742-1-CA_OBJECT_CODE_T.xml::5742-1-1::Winston::(MD5Sum: 278a71aac71daff2ff4796dc49c9b52b)
-- KFSMI-5742  Change Object Code 7070 sub type in MDS from NA to CM
UPDATE `CA_OBJECT_CODE_T` SET `fin_obj_sub_typ_cd` = 'CM' WHERE fin_coa_cd = 'BL' AND fin_object_cd = '7070' and univ_fiscal_yr='2011';
