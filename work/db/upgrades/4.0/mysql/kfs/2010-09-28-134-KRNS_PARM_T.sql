-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/28/10 10:05 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-28-NONE1-1-KRNS_PARM_T.xml::NONE1-1-1::Winston::(MD5Sum: e37f4bf8c26db079e223c4dd7f12a7fb)
-- Insert Customizable help for batch
INSERT INTO `KRNS_PARM_T` (`VER_NBR`, `PARM_DESC_TXT`, `OBJ_ID`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES (1, 'A Parameter to provide customizing the help URL for the Batch Upload help page', uuid(), 'BATCH_UPLOAD_HELP_URL', 'BatchUpload', 'A', 'customerLoadInputFileType=default.htm?turl=WordDocuments%2Fbatch.htm;procurementCardInputFileType=default.htm?turl=WordDocuments%2Fbatch.htm;collectorFlatFileInputFileType=default.htm?turl=WordDocuments%2Fbatch.htm;collectorXmlInputFileType=default.htm?turl=WordDocuments%2Fbatch.htm;enterpriseFeederFileSetType=default.htm?turl=WordDocuments%2Fbatch.htm;laborEnterpriseFeederFileSetType=default.htm?turl=WordDocuments%2Fbatch.htm;assetBarcodeInventoryInputFileType=default.htm?turl=WordDocuments%2Fbatch.htm;paymentInputFileType=default.htm?turl=WordDocuments%2Fbatch.htm', 'KFS', 'HELP', 'KFS-SYS');


-- Release Database Lock

-- Release Database Lock

