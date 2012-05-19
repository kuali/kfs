-- Update MOTD for TEM
UPDATE FP_MOTD_T 
SET FS_MOTD_TXT = 'Welcome to TEM Contribution Team Test Site (5.0 Pre-Rice 2.0).   Please be aware that some of the non-TEM modules might have issues since the code base is a snapshot of a point of time.   Please visit https://wiki.kuali.org/display/KULKFS/TEM+Contribution+Team   for monitoring progress.'
WHERE FS_ORIGIN_CD = '01'
/

-- Insert Special Circumstances Questions for TEM
INSERT INTO TEM_SPCL_CRCMSNCS_Q_T (ID,DOCUMENT_TYPE,TEXT,TXT_IND,ACTV_IND,OBJ_ID) 
values (1,'TA','Question for TA - is anyone traveling with you?','Y','Y','KFS-TEM-SPCM-001')
/
INSERT INTO TEM_SPCL_CRCMSNCS_Q_T (ID,DOCUMENT_TYPE,TEXT,TXT_IND,ACTV_IND,OBJ_ID) 
values (2,'TR','Question for TR documents - did you stay overnight with family or friends?:','Y','Y','KFS-TEM-SPCM-002')
/
INSERT INTO TEM_SPCL_CRCMSNCS_Q_T (ID,DOCUMENT_TYPE,TEXT,TXT_IND,ACTV_IND,OBJ_ID) 
VALUES (3,'ENT','Question for Entertainment documents - was alcohol served?:','Y','Y','KFS-TEM-SPCM-003')
/
INSERT INTO TEM_SPCL_CRCMSNCS_Q_T (ID,DOCUMENT_TYPE,TEXT,TXT_IND,ACTV_IND,OBJ_ID) 
values (4,'RELO','Question for Relocation documents - do you have pets moving with you?:','Y','Y','KFS-TEM-SPCM-004')
/
INSERT INTO TEM_SPCL_CRCMSNCS_Q_T (ID,DOCUMENT_TYPE,TEXT,TXT_IND,ACTV_IND,OBJ_ID) 
values (5,'TA','Question for TA documents - not free form: Carrying Fruit?','N','Y','KFS-TEM-SPCM-005')
/
INSERT INTO TEM_SPCL_CRCMSNCS_Q_T (ID,DOCUMENT_TYPE,TEXT,TXT_IND,ACTV_IND,OBJ_ID) 
VALUES (6,'TA','Inactive Question for search testing','N','N','KFS-TEM-SPCM-006')
/
INSERT INTO TEM_SPCL_CRCMSNCS_Q_T (ID,DOCUMENT_TYPE,TEXT,TXT_IND,ACTV_IND,OBJ_ID) 
VALUES (7,'TA','Do you have large pets traveling with you?:','Y','Y','KFS-TEM-SPCM-007')
/

-- Update max file upload size to 200M 
UPDATE KRNS_PARM_T
SET TXT = '200M'
WHERE NMSPC_CD = 'KR-NS'
AND PARM_NM = 'MAX_FILE_SIZE_DEFAULT_UPLOAD'
/

-- Set foreign travel company for Delta 
UPDATE FP_DV_TRVL_CO_NM_T
SET FRGN_CMPNY = 'Y'
WHERE DV_EXP_CD in ('A', 'PA')
AND DV_EXP_CO_NM = 'DELTA'
/

-- Create a PayeePCH Account for DHEAGLE
INSERT into PDP_PAYEE_ACH_ACCT_T 
(ACH_ACCT_GNRTD_ID,OBJ_ID,VER_NBR,BNK_RTNG_NBR,BNK_ACCT_NBR,PAYEE_NM,PAYEE_EMAIL_ADDR,PAYEE_ID_TYP_CD,ACH_TRANS_TYP,ROW_ACTV_IND,BNK_ACCT_TYP_CD,PAYEE_ID_NBR) 
VALUES (20000,'KFS-TEM-ACH-ACCT-001',1,'876543210','XBy8HbA0p2mZzQpI+9a6nw==','EAGLE, DEAN H','kdfks@nfksdf.com','E','PRAP','Y','22','0000175456');
/