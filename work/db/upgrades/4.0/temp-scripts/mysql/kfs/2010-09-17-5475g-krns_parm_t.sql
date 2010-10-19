-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/17/10 11:11 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-1::Bonnie::(MD5Sum: 79974745da66adf1f92e9112bc67c)
-- we have to update all endowment system parameters on kuldba again.
DELETE FROM `KRNS_PARM_T`  WHERE NMSPC_CD='KFS-ENDOW';


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-2::Bonnie::(MD5Sum: 54c844c827981380639840372c34d3c)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The number of times per year that the institution will distribute income to the account holders', 'DISTRIBUTIONS_PER_YEAR', 'PooledFundValue', 'A', '2', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-3::Bonnie::(MD5Sum: e8bb371074bab3e7e72d889a3157bc3e)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The value of this parameter is used to map the Asset Endowment Transaction Type to the Assets code defined in the Basic Accounting Category table', 'ASSET_TYPE', 'EndowmentTransactionCode', 'A', 'AS', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-4::Bonnie::(MD5Sum: 82905a1fcae44891b0ddd44e36fe78d)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The value of this parameter is used to map the Expense Endowment Transaction Type to the Expenses code defined in the Basic Accounting Category table', 'EXPENSE_TYPE', 'EndowmentTransactionCode', 'A', 'EX', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-5::Bonnie::(MD5Sum: 6cee5557c32c8a3b8f1a8dc8d1585d7)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The value of this parameter is used to map the Income Endowment Transaction Type to the Income code defined in the Basic Accounting Category table', 'INCOME_TYPE', 'EndowmentTransactionCode', 'A', 'IN', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-6::Bonnie::(MD5Sum: eef382aa085a34cbac44a3a94f6bd5)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The value of this parameter is used to map the Liabilities Endowment Transaction Type to the Liabilities code defined in the Basic Accounting Category table', 'LIABILITY_TYPE', 'EndowmentTransactionCode', 'A', 'LI', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-7::Bonnie::(MD5Sum: c97539772b9df67e229f1462fbe6a4e7)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The creation of a new KEMID value will be dependent upon the institutional parameter chosen for the KEMID_VALUE: 1 If the parameter is Automatic,each KEMID initiated will be sequentially numbered by the system based on a database sequence and beginning with the value the sequence has been configured to start with 2 If the parameter is Automatic,each KEMID initiated will be sequentially numbered by the system based on a database sequence and beginning with the value the sequence has been configured to start with', 'KEMID_VALUE', 'KEMID', 'A', 'Manual', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-8::Bonnie::(MD5Sum: d77b39f4394ee3e246e759ecad69c7)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('This parameter is used to identify the institutional selection for tracking security Tax Lots and transactions involving cash The possible values for this parameter are: Average Balance,FIFO First In - First Out or LIFO Last In - First Out', 'TAX_LOTS_ACCOUNTING_METHOD', 'All', 'A', 'Average Balance', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-9::Bonnie::(MD5Sum: 503143c73929ffcb8d552686b6b696)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Accrual transaction eDoc will be submitted without routing for approval Yes or for manual approval through workflow No', 'NO_ROUTE_IND', 'CreateAccrualTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-10::Bonnie::(MD5Sum: a1ecc637625611d6aa80818970371d98)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Accrual Transactions batch process', 'DESCRIPTION', 'CreateAccrualTransactionsStep', 'A', 'Accrued Income Distribution', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-11::Bonnie::(MD5Sum: b6c1699fb4793f504333b668c03af5c5)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The values of this parameter represent a complete list of document type names supported by the endowment recurring cash transfer maintenance documents The application maps this list to the document types that have been registered with the Workflow Engine', 'DOCUMENT_TYPES', 'EndowmentRecurringCashTransfer', 'A', 'ECT;EGLT', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-12::Bonnie::(MD5Sum: 1915eb5045277dc7866f30f7ab6e8)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('If Y,then the Target transaction amount can exceed the calculated cash equivalents balance', 'ALLOW_NEGATIVE_BALANCE_IND', 'EndowmentRecurringCashTransfer', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-13::Bonnie::(MD5Sum: f48690ea6c615030c57c1f575643ca7f)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The KFS object subtypes that are allowed for the KEM to GL Cash Transfer transaction types', 'GL_OBJECT_SUB_TYPES', 'EndowmentToGLTransferOfFunds', 'A', 'MT; TN', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-14::Bonnie::(MD5Sum: de6d1455f8f6a8af17ff4c2c2efccf)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The KFS object subtypes that are allowed for the GL to KEM Cash Transfer transaction types', 'GL_OBJECT_SUB_TYPES', 'GLToEndowmentTransferOfFunds', 'A', 'MT; TN', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-15::Bonnie::(MD5Sum: bf2a3c327b5a96ac6116613f534a16c)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The KFS object subtypes that are allowed for the KEM to KEM Cash Transfer transaction type', 'CASH_OBJECT_SUB_TYPES', 'CashTransfer', 'A', 'MT; TN', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-16::Bonnie::(MD5Sum: 50724e9d5b436f7423efa6b2e215596)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The KFS object subtypes that are allowed for the KEM to KEM Holding Transfer transaction type', 'HOLDING_OBJECT_SUB_TYPES', 'SecurityTransfer', 'A', 'MT; TN', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-17::Bonnie::(MD5Sum: c2949fe5fd91fdddeb881f5de9f38cc7)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The month and day marking the last date of the fiscal year 0630 means June 30th', 'FISCAL_YEAR_END_MONTH_AND_DAY', 'Batch', 'A', '0630', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-18::Bonnie::(MD5Sum: 45cb929171e5918b7394fcdd0293a)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The maximum number of transaction lines that the institution prefers for the eDocs that are generated automatically as a result of a nightly batch process', 'MAXIMUM_TRANSACTION_LINES', 'Batch', 'A', '1000', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-19::Bonnie::(MD5Sum: 351d1a88df2f39d833f989d3620e217)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('If USE_PROCESS_DATE_IND=Y,get the value from CURRENT_PROCESS_DATE If USE_PROCESS_DATE_IND=N,get the current date from the local system using standard Java API', 'USE_PROCESS_DATE_IND', 'All', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-20::Bonnie::(MD5Sum: f8d0247466d2a4da475161f1ef729e)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The Current Process Date', 'CURRENT_PROCESS_DATE', 'All', 'A', 'now()', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-21::Bonnie::(MD5Sum: 7bf9c03c4c41659d344b265e50101a74)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The values of this parameter represent a complete list of document type names for the endowment transactional documents that can be posted to the endowment transaction archive table The application maps this list to the document types that have been registered with the Workflow Engine', 'DOCUMENT_TYPES', 'TransactionArchive', 'A', 'EAD;EAI;ECDD;ECI;ECT;ECA;EUSA;ELD;ELI;EST;EHA;EGLT;GLET', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-22::Bonnie::(MD5Sum: 7b774f4684fd1457277d3cfac79ccae)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Control Purchase transaction eDoc will be submitted without routing for approval Yes or for manual approval through workflow No', 'PURCHASE_NO_ROUTE_IND', 'PooledFundControlTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-23::Bonnie::(MD5Sum: 4f66a8eb62366e57a3f7d25ecff22569)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Control Sale transaction eDoc will be submitted without routing for approval Yes or for manual approval through workflow No', 'SALE_NO_ROUTE_IND', 'PooledFundControlTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-24::Bonnie::(MD5Sum: 37baf93f4f9e44761514e4b68fce224e)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Control Sale Gain Loss transaction eDoc will be submitted without routing for approval Yes or for manual approval through workflow No', 'GAIN_LOSS_NO_ROUTE_IND', 'PooledFundControlTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-25::Bonnie::(MD5Sum: bc586373c4069d0fb643b77c8f8c)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Control Income Distribution transaction eDoc will be submitted without routing for approval Yes or for manual approval through workflow No', 'INCOM_NO_ROUTE_IND', 'PooledFundControlTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-26::Bonnie::(MD5Sum: 182ff0be72cfbfed4f319ebb99b6bc91)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Pooled Fund Control Purchase batch process', 'PURCHASE_DESCRIPTION', 'PooledFundControlTransactionsStep', 'A', 'Net Pooled Fund Purchases', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-27::Bonnie::(MD5Sum: 44c5e0621f6bdd397eaff997dea45b0)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Pooled fund Control Sale batch process', 'SALE_DESCRIPTION', 'PooledFundControlTransactionsStep', 'A', 'Net Pooled Fund Sales at Book', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-28::Bonnie::(MD5Sum: 7b7a5ebc745e322c5348bfcea77a4b8e)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Pooled fund Control Sale Gain Loss batch process', 'GAIN_LOSS_DESCRIPTION', 'PooledFundControlTransactionsStep', 'A', 'Net Pooled Fund Sale Gains and Losses', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-29::Bonnie::(MD5Sum: e76fd799852af1cc35edcf256489b64)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Pooled fund Control Income Distribution batch process', 'INCOME_DESCRIPTION', 'PooledFundControlTransactionsStep', 'A', 'Net Pooled Fund Income Distributions', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-30::Bonnie::(MD5Sum: 7de232448ccbd49fc5915e27a98f796d)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Cash Sweep Purchase transaction eDoc will be submitted without routing for approval Yes or for manual approval through workflow No', 'PURCHASE_NO_ROUTE_IND', 'CreateCashSweepTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-31::Bonnie::(MD5Sum: 111dc5ab4d8e2d4aae5918f4e36c80d3)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Cash Sweep Sale eDoc will be submitted without routing for approval Yes or for manual approval through workflow No', 'SALE_NO_ROUTE_IND', 'CreateCashSweepTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-32::Bonnie::(MD5Sum: 2efeca74d4662f2ff2b130dae39afeb7)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the asset purchase eDocs produced as a result of the Cash Sweep batch process', 'PURCHASE_DESCRIPTION', 'CreateCashSweepTransactionsStep', 'A', 'Increase Cash Sweep Investments', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-33::Bonnie::(MD5Sum: 4a3de28c4eac4d84d4bdae0fd9df4)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the asset sale eDocs produced as a result of the Cash Sweep batch process', 'SALE_DESCRIPTION', 'CreateCashSweepTransactionsStep', 'A', 'Decrease Cash Sweep Investments', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-34::Bonnie::(MD5Sum: c5958d457d1bfc872f499d47eeae78e)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated ACI Purchase eDoc will be submitted without routing for approval Yes or for manual approval through workflow No', 'PURCHASE_NO_ROUTE_IND', 'CreateAutomatedCashInvestmentTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-35::Bonnie::(MD5Sum: fee9db315041895d6dfc8ba5936e85a8)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated ACI Sale eDoc will be submitted without routing for approval Yes or for manual approval through workflow No', 'SALE_NO_ROUTE_IND', 'CreateAutomatedCashInvestmentTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-36::Bonnie::(MD5Sum: f1b7d9b67e66f72ff85b3f775a9d4)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the asset purchase eDocs produced as a result of the ACI batch process', 'PURCHASE_DESCRIPTION', 'CreateAutomatedCashInvestmentTransactionsStep', 'A', 'Purchase Pooled Funds', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-37::Bonnie::(MD5Sum: 7c6b7ff694f6fec5a3c6e607836547b)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the asset sale eDocs produced as a result of the ACI batch process', 'SALE_DESCRIPTION', 'CreateAutomatedCashInvestmentTransactionsStep', 'A', 'Sell Pooled Funds', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-38::Bonnie::(MD5Sum: 1e28d0c861bfccdc67a73bb62e87ca2)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled fund Gain loss Distribution eDoc will be submitted without routing for approval Yes or for manual approval through workflow No', 'GAIN_LOSS_NO_ROUTE_IND', 'CreateGainLossDistributionTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-39::Bonnie::(MD5Sum: 8bea11b3e338af1161450b2c4445527)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Income transaction eDoc will be submitted without routing for approval Yes or for manual approval through workflow No', 'INCOME_NO_ROUTE_IND', 'IncomeDistributionForPooledFundStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-40::Bonnie::(MD5Sum: e4eac42b9734565a2ed548ed11102b17)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the holding adjustment eDocs produced as a result of the Pooled Fund Distribution batch process', 'LONG_TERM_GAIN_LOSS_DESCRIPTION', 'CreateGainLossDistributionTransactionsStep', 'A', 'Distribute Long Term Gains and Losses', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-41::Bonnie::(MD5Sum: 8e698a808359ea3f7e7e4ad257ef2d64)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the holding adjustment eDocs produced as a result of the Pooled Fund Distribution batch process', 'SHORT_TERM_GAIN_LOSS_DESCRIPTION', 'CreateGainLossDistributionTransactionsStep', 'A', 'Distribute Short Term Gains and Losses', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-42::Bonnie::(MD5Sum: 632a9beb73291224196b9581e73e773)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the holding adjustment eDocs produced as a result of the Pooled Fund Distribution batch process', 'INCOME_DESCRIPTION', 'IncomeDistributionForPooledFundStep', 'A', 'Distribute Pooled Fund Income', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-43::Bonnie::(MD5Sum: df6d194619ba95abbca7d5253154925c)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Income Transfer transaction eDoc will be submitted without routing for approval Yes or for manual approval through workflow No', 'INCOME_TRANSFER_NO_ROUTE_IND', 'IncomeDistributionForPooledFundStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-44::Bonnie::(MD5Sum: 74ebe3c8fa9c45288d5da4f89246619e)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the holding adjustment eDocs produced as a result of the Pooled Fund Income Transfer batch process', 'INCOME_TRANSFER_DESCRIPTION', 'IncomeDistributionForPooledFundStep', 'A', 'Transfer Pooled Fund Income', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-45::Bonnie::(MD5Sum: 54aca7ed95ba6840f5a32edd558930)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The Endowment Transaction Code that the system will use to process the transfer of distributed income according to the KEMID payout instructions', 'INCOME_TRANSFER_ENDOWMENT_TRANSACTION_CODE', 'IncomeDistributionForPooledFundStep', 'A', '59990', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-46::Bonnie::(MD5Sum: 95be1c42517113a6f6a27b93ccf4672b)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Used to calculate the percent of the market value of pooled funds considered available for spending', 'AVAILABLE_CASH_PERCENT', 'AvailableCashUpdateStep', 'A', '90', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-47::Bonnie::(MD5Sum: bbaf319725d286e69dc35265974241c)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Recurring Cash Transfer transaction eDoc will be submitted without routing for approval Yes or for manual approval through workflow No', 'NO_ROUTE_IND', 'CreateRecurringCashTransferTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-48::Bonnie::(MD5Sum: 4335451ac3d3136fbd216a596419bcf9)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Recurring Cash Transfer batch process', 'DESCRIPTION', 'CreateRecurringCashTransferTransactionsStep', 'A', 'Recurring Cash Transfer Transaction', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-17-5475g-1-KRNS_PARM_T.xml::5475g-1-49::Bonnie::(MD5Sum: 734b5e374edd8e809f9662b7f57932f6)
-- we have to update all endowment system parameters on kuldba again.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('If Y,then the Target transaction amount can exceed the calculated cash equivalents balance', 'ALLOW_NEGATIVE_BALANCE_IND', 'CreateRecurringCashTransferTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Release Database Lock

-- Release Database Lock

