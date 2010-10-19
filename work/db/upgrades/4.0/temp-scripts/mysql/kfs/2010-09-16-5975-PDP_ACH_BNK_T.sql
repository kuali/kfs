-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/16/10 2:27 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-16-5975-1-PDP_ACH_BNK_T.xml::5975-1-1::Bob::(MD5Sum: 2f37639a83c71fc3649882e52b3c1bf)
-- These changes are required to handle the changes that will be made to PayeeACHAccount.xml, PaymentGroup.xml, ProcessSummary.xml and ACHPayee.xml.
ALTER TABLE `PDP_ACH_BNK_T` MODIFY `bnk_nm` VARCHAR(40);


-- Changeset updates/2010-09-16-5975-1-PDP_ACH_BNK_T.xml::5975-1-2::Bob::(MD5Sum: b9631a2328e458699a4d3aa095ec97ce)
-- These changes are required to handle the changes that will be made to PayeeACHAccount.xml, PaymentGroup.xml, ProcessSummary.xml and ACHPayee.xml.
ALTER TABLE `PDP_PAYEE_ACH_ACCT_T` MODIFY `PAYEE_EMAIL_ADDR` VARCHAR(200);


-- Changeset updates/2010-09-16-5975-1-PDP_ACH_BNK_T.xml::5975-1-3::Bob::(MD5Sum: fa27ddffae3b59f5c742dfe74f09466)
-- These changes are required to handle the changes that will be made to PayeeACHAccount.xml, PaymentGroup.xml, ProcessSummary.xml and ACHPayee.xml.
ALTER TABLE `PDP_PMT_GRP_T` MODIFY `ADV_EMAIL_ADDR` VARCHAR(200);


-- Release Database Lock

-- Release Database Lock

