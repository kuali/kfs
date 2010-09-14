-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/13/10 1:40 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-1::Bonnie::(MD5Sum: 79974745da66adf1f92e9112bc67c)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
DELETE FROM `KRNS_PARM_T`  WHERE NMSPC_CD='KFS-ENDOW';


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-2::Bonnie::(MD5Sum: 54c844c827981380639840372c34d3c)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The number of times per year that the institution will distribute income to the account holders', 'DISTRIBUTIONS_PER_YEAR', 'PooledFundValue', 'A', '2', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-3::Bonnie::(MD5Sum: e8bb371074bab3e7e72d889a3157bc3e)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The value of this parameter is used to map the Asset Endowment Transaction Type to the Assets code defined in the Basic Accounting Category table', 'ASSET_TYPE', 'EndowmentTransactionCode', 'A', 'AS', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-4::Bonnie::(MD5Sum: 82905a1fcae44891b0ddd44e36fe78d)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The value of this parameter is used to map the Expense Endowment Transaction Type to the Expenses code defined in the Basic Accounting Category table', 'EXPENSE_TYPE', 'EndowmentTransactionCode', 'A', 'EX', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-5::Bonnie::(MD5Sum: 6cee5557c32c8a3b8f1a8dc8d1585d7)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The value of this parameter is used to map the Income Endowment Transaction Type to the Income code defined in the Basic Accounting Category table', 'INCOME_TYPE', 'EndowmentTransactionCode', 'A', 'IN', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-6::Bonnie::(MD5Sum: eef382aa085a34cbac44a3a94f6bd5)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The value of this parameter is used to map the Liabilities Endowment Transaction Type to the Liabilities code defined in the Basic Accounting Category table', 'LIABILITY_TYPE', 'EndowmentTransactionCode', 'A', 'LI', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-7::Bonnie::(MD5Sum: c97539772b9df67e229f1462fbe6a4e7)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The creation of a new KEMID value will be dependent upon the institutional parameter chosen for the KEMID_VALUE: 1 If the parameter is Automatic,each KEMID initiated will be sequentially numbered by the system based on a database sequence and beginning with the value the sequence has been configured to start with 2 If the parameter is Automatic,each KEMID initiated will be sequentially numbered by the system based on a database sequence and beginning with the value the sequence has been configured to start with', 'KEMID_VALUE', 'KEMID', 'A', 'Manual', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-8::Bonnie::(MD5Sum: d77b39f4394ee3e246e759ecad69c7)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('This parameter is used to identify the institutional selection for tracking security Tax Lots and transactions involving cash The possible values for this parameter are: Average Balance,FIFO First In - First Out or LIFO Last In - First Out', 'TAX_LOTS_ACCOUNTING_METHOD', 'All', 'A', 'Average Balance', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-9::Bonnie::(MD5Sum: 6cdbd8d2f4db15a29cbf92843277e2e)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Accrual transaction eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'BLANKET_APPROVAL_IND', 'CreateAccrualTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-10::Bonnie::(MD5Sum: a1ecc637625611d6aa80818970371d98)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Accrual Transactions batch process', 'DESCRIPTION', 'CreateAccrualTransactionsStep', 'A', 'Accrued Income Distribution', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-11::Bonnie::(MD5Sum: b6c1699fb4793f504333b668c03af5c5)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The values of this parameter represent a complete list of document type names supported by the endowment recurring cash transfer maintenance documents The application maps this list to the document types that have been registered with the Workflow Engine', 'DOCUMENT_TYPES', 'EndowmentRecurringCashTransfer', 'A', 'ECT;EGLT', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-12::Bonnie::(MD5Sum: 1915eb5045277dc7866f30f7ab6e8)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('If Y,then the Target transaction amount can exceed the calculated cash equivalents balance', 'ALLOW_NEGATIVE_BALANCE_IND', 'EndowmentRecurringCashTransfer', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-13::Bonnie::(MD5Sum: f48690ea6c615030c57c1f575643ca7f)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The KFS object subtypes that are allowed for the KEM to GL Cash Transfer transaction types', 'GL_OBJECT_SUB_TYPES', 'EndowmentToGLTransferOfFunds', 'A', 'MT; TN', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-14::Bonnie::(MD5Sum: de6d1455f8f6a8af17ff4c2c2efccf)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The KFS object subtypes that are allowed for the GL to KEM Cash Transfer transaction types', 'GL_OBJECT_SUB_TYPES', 'GLToEndowmentTransferOfFunds', 'A', 'MT; TN', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-15::Bonnie::(MD5Sum: bf2a3c327b5a96ac6116613f534a16c)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The KFS object subtypes that are allowed for the KEM to KEM Cash Transfer transaction type', 'CASH_OBJECT_SUB_TYPES', 'CashTransfer', 'A', 'MT; TN', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-16::Bonnie::(MD5Sum: 50724e9d5b436f7423efa6b2e215596)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The KFS object subtypes that are allowed for the KEM to KEM Holding Transfer transaction type', 'HOLDING_OBJECT_SUB_TYPES', 'SecurityTransfer', 'A', 'MT; TN', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-17::Bonnie::(MD5Sum: c2949fe5fd91fdddeb881f5de9f38cc7)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The month and day marking the last date of the fiscal year 0630 means June 30th', 'FISCAL_YEAR_END_MONTH_AND_DAY', 'Batch', 'A', '0630', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-18::Bonnie::(MD5Sum: 45cb929171e5918b7394fcdd0293a)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The maximum number of transaction lines that the institution prefers for the eDocs that are generated automatically as a result of a nightly batch process', 'MAXIMUM_TRANSACTION_LINES', 'Batch', 'A', '1000', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-19::Bonnie::(MD5Sum: 351d1a88df2f39d833f989d3620e217)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('If USE_PROCESS_DATE_IND=Y,get the value from CURRENT_PROCESS_DATE If USE_PROCESS_DATE_IND=N,get the current date from the local system using standard Java API', 'USE_PROCESS_DATE_IND', 'All', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-20::Bonnie::(MD5Sum: f8d0247466d2a4da475161f1ef729e)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The Current Process Date', 'CURRENT_PROCESS_DATE', 'All', 'A', 'now()', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-21::Bonnie::(MD5Sum: 7bf9c03c4c41659d344b265e50101a74)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The values of this parameter represent a complete list of document type names for the endowment transactional documents that can be posted to the endowment transaction archive table The application maps this list to the document types that have been registered with the Workflow Engine', 'DOCUMENT_TYPES', 'TransactionArchive', 'A', 'EAD;EAI;ECDD;ECI;ECT;ECA;EUSA;ELD;ELI;EST;EHA;EGLT;GLET', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-22::Bonnie::(MD5Sum: 5c99731c2d42a374d8112fa719e6c49)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Control Purchase transaction eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'PURCHASE_BLANKET_APPROVAL_IND', 'PooledFundControlTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-23::Bonnie::(MD5Sum: cb59d1db4666ca93e342d0b33955dfa1)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Control Sale transaction eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'SALE_BLANKET_APPROVAL_IND', 'PooledFundControlTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-24::Bonnie::(MD5Sum: c2d8b22896bb3599364e8f47a6f9215d)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Control Sale Gain Loss transaction eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'GAIN_LOSS_BLANKET_APPROVAL_IND', 'PooledFundControlTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-25::Bonnie::(MD5Sum: d0fdad77acfd1448acd5b19cd5e71)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Control Income Distribution transaction eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'INCOM_BLANKET_APPROVAL_IND', 'PooledFundControlTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-26::Bonnie::(MD5Sum: 182ff0be72cfbfed4f319ebb99b6bc91)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Pooled Fund Control Purchase batch process', 'PURCHASE_DESCRIPTION', 'PooledFundControlTransactionsStep', 'A', 'Net Pooled Fund Purchases', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-27::Bonnie::(MD5Sum: 44c5e0621f6bdd397eaff997dea45b0)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Pooled fund Control Sale batch process', 'SALE_DESCRIPTION', 'PooledFundControlTransactionsStep', 'A', 'Net Pooled Fund Sales at Book', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-28::Bonnie::(MD5Sum: 7b7a5ebc745e322c5348bfcea77a4b8e)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Pooled fund Control Sale Gain Loss batch process', 'GAIN_LOSS_DESCRIPTION', 'PooledFundControlTransactionsStep', 'A', 'Net Pooled Fund Sale Gains and Losses', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-29::Bonnie::(MD5Sum: e76fd799852af1cc35edcf256489b64)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Pooled fund Control Income Distribution batch process', 'INCOME_DESCRIPTION', 'PooledFundControlTransactionsStep', 'A', 'Net Pooled Fund Income Distributions', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-30::Bonnie::(MD5Sum: 143ccbc207e6a510c75ddac2865b16)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Cash Sweep Purchase transaction eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'PURCHASE_BLANKET_APPROVAL_IND', 'CreateCashSweepTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-31::Bonnie::(MD5Sum: d69a13c72862cf6deaad173b964f117)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Cash Sweep Sale eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'SALE_BLANKET_APPROVAL_IND', 'CreateCashSweepTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-32::Bonnie::(MD5Sum: 2efeca74d4662f2ff2b130dae39afeb7)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the asset purchase eDocs produced as a result of the Cash Sweep batch process', 'PURCHASE_DESCRIPTION', 'CreateCashSweepTransactionsStep', 'A', 'Increase Cash Sweep Investments', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-33::Bonnie::(MD5Sum: 4a3de28c4eac4d84d4bdae0fd9df4)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the asset sale eDocs produced as a result of the Cash Sweep batch process', 'SALE_DESCRIPTION', 'CreateCashSweepTransactionsStep', 'A', 'Decrease Cash Sweep Investments', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-34::Bonnie::(MD5Sum: bf4a858f6d5fc364f230eee156de157)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated ACI Purchase eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'PURCHASE_BLANKET_APPROVAL_IND', 'CreateAutomatedCashInvestmentTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-35::Bonnie::(MD5Sum: f41d7417cbecdab25fa498d8f8c518)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated ACI Sale eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'SALE_BLANKET_APPROVAL_IND', 'CreateAutomatedCashInvestmentTransactionsStep', 'A', 'Y', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-36::Bonnie::(MD5Sum: f1b7d9b67e66f72ff85b3f775a9d4)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the asset purchase eDocs produced as a result of the ACI batch process', 'PURCHASE_DESCRIPTION', 'CreateAutomatedCashInvestmentTransactionsStep', 'A', 'Purchase Pooled Funds', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-37::Bonnie::(MD5Sum: 7c6b7ff694f6fec5a3c6e607836547b)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the asset sale eDocs produced as a result of the ACI batch process', 'SALE_DESCRIPTION', 'CreateAutomatedCashInvestmentTransactionsStep', 'A', 'Sell Pooled Funds', 'KFS', 'CONFG', 'KFS-ENDOW');


-- Release Database Lock

-- Release Database Lock

-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/13/10 1:50 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-2::Bonnie::(MD5Sum: 5ac05c1f5d167e944e67916294c8b220)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The number of times per year that the institution will distribute income to the account holders', 'DISTRIBUTIONS_PER_YEAR', 'PooledFundValue', 'A', '2', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-3::Bonnie::(MD5Sum: bbfc48d6eaa25521c5a29a7cad5026)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The value of this parameter is used to map the Asset Endowment Transaction Type to the Assets code defined in the Basic Accounting Category table', 'ASSET_TYPE', 'EndowmentTransactionCode', 'A', 'AS', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-4::Bonnie::(MD5Sum: adfd8d9590754f812b4b5ee9bfc81a)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The value of this parameter is used to map the Expense Endowment Transaction Type to the Expenses code defined in the Basic Accounting Category table', 'EXPENSE_TYPE', 'EndowmentTransactionCode', 'A', 'EX', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-5::Bonnie::(MD5Sum: 206c4f599127c344d0e26d503bd411)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The value of this parameter is used to map the Income Endowment Transaction Type to the Income code defined in the Basic Accounting Category table', 'INCOME_TYPE', 'EndowmentTransactionCode', 'A', 'IN', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-6::Bonnie::(MD5Sum: 1b9641801ddd309b5a3cff09edf114c)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The value of this parameter is used to map the Liabilities Endowment Transaction Type to the Liabilities code defined in the Basic Accounting Category table', 'LIABILITY_TYPE', 'EndowmentTransactionCode', 'A', 'LI', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-7::Bonnie::(MD5Sum: 2b3af2d6242fcab55174ff3a7ac84e6c)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The creation of a new KEMID value will be dependent upon the institutional parameter chosen for the KEMID_VALUE: 1 If the parameter is Automatic,each KEMID initiated will be sequentially numbered by the system based on a database sequence and beginning with the value the sequence has been configured to start with 2 If the parameter is Automatic,each KEMID initiated will be sequentially numbered by the system based on a database sequence and beginning with the value the sequence has been configured to start with', 'KEMID_VALUE', 'KEMID', 'A', 'Manual', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-8::Bonnie::(MD5Sum: af5710997f70cd980a65c863ee5ffac)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('This parameter is used to identify the institutional selection for tracking security Tax Lots and transactions involving cash The possible values for this parameter are: Average Balance,FIFO First In - First Out or LIFO Last In - First Out', 'TAX_LOTS_ACCOUNTING_METHOD', 'All', 'A', 'Average Balance', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-9::Bonnie::(MD5Sum: a4969896fd7d674ab9b71c8dc571a2)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Accrual transaction eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'BLANKET_APPROVAL_IND', 'CreateAccrualTransactionsStep', 'A', 'Y', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-10::Bonnie::(MD5Sum: 7a1076407845ac44c8d89d2c0efd0d5)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Accrual Transactions batch process', 'DESCRIPTION', 'CreateAccrualTransactionsStep', 'A', 'Accrued Income Distribution', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-11::Bonnie::(MD5Sum: 328d8f62e72fd7f5459c9472b26b866)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The values of this parameter represent a complete list of document type names supported by the endowment recurring cash transfer maintenance documents The application maps this list to the document types that have been registered with the Workflow Engine', 'DOCUMENT_TYPES', 'EndowmentRecurringCashTransfer', 'A', 'ECT;EGLT', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-12::Bonnie::(MD5Sum: 933097cf96b69e4953a2eeb412d3e9d5)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('If Y,then the Target transaction amount can exceed the calculated cash equivalents balance', 'ALLOW_NEGATIVE_BALANCE_IND', 'EndowmentRecurringCashTransfer', 'A', 'Y', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-13::Bonnie::(MD5Sum: ec41d66858de7bfd856a9b98a4ac93a)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The KFS object subtypes that are allowed for the KEM to GL Cash Transfer transaction types', 'GL_OBJECT_SUB_TYPES', 'EndowmentToGLTransferOfFunds', 'A', 'MT; TN', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-14::Bonnie::(MD5Sum: 1639e3985ffa4e16113b59d696c0ff)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The KFS object subtypes that are allowed for the GL to KEM Cash Transfer transaction types', 'GL_OBJECT_SUB_TYPES', 'GLToEndowmentTransferOfFunds', 'A', 'MT; TN', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-15::Bonnie::(MD5Sum: 92532716b4fa7469873382b0e38bbf60)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The KFS object subtypes that are allowed for the KEM to KEM Cash Transfer transaction type', 'CASH_OBJECT_SUB_TYPES', 'CashTransfer', 'A', 'MT; TN', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-16::Bonnie::(MD5Sum: acc2e755ba9ff4a9350effa1f82aa74)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The KFS object subtypes that are allowed for the KEM to KEM Holding Transfer transaction type', 'HOLDING_OBJECT_SUB_TYPES', 'SecurityTransfer', 'A', 'MT; TN', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-17::Bonnie::(MD5Sum: 2da0a6de1f2c5ed3f25faf3248d961e)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The month and day marking the last date of the fiscal year 0630 means June 30th', 'FISCAL_YEAR_END_MONTH_AND_DAY', 'Batch', 'A', '0630', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-18::Bonnie::(MD5Sum: 419c1cbd43133679cb3e769aac2dda8b)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The maximum number of transaction lines that the institution prefers for the eDocs that are generated automatically as a result of a nightly batch process', 'MAXIMUM_TRANSACTION_LINES', 'Batch', 'A', '1000', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-19::Bonnie::(MD5Sum: d83cc1c0b5bee49ae870bd66ec5ceccd)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('If USE_PROCESS_DATE_IND=Y,get the value from CURRENT_PROCESS_DATE If USE_PROCESS_DATE_IND=N,get the current date from the local system using standard Java API', 'USE_PROCESS_DATE_IND', 'All', 'A', 'Y', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-20::Bonnie::(MD5Sum: 1ed6b39a8df6fb87de4958a9f525c)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The Current Process Date', 'CURRENT_PROCESS_DATE', 'All', 'A', 'now()', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-21::Bonnie::(MD5Sum: 5ab96fe4328cd7812cc15da9d283a8e)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The values of this parameter represent a complete list of document type names for the endowment transactional documents that can be posted to the endowment transaction archive table The application maps this list to the document types that have been registered with the Workflow Engine', 'DOCUMENT_TYPES', 'TransactionArchive', 'A', 'EAD;EAI;ECDD;ECI;ECT;ECA;EUSA;ELD;ELI;EST;EHA;EGLT;GLET', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-22::Bonnie::(MD5Sum: 58c22c2c554ea861e38bce1363403479)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Control Purchase transaction eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'PURCHASE_BLANKET_APPROVAL_IND', 'PooledFundControlTransactionsStep', 'A', 'Y', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-23::Bonnie::(MD5Sum: 41ab5ad24463ae4578694e5ac2d5cd)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Control Sale transaction eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'SALE_BLANKET_APPROVAL_IND', 'PooledFundControlTransactionsStep', 'A', 'Y', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-24::Bonnie::(MD5Sum: cbf87647a2b48921833dee0ef696f4c)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Control Sale Gain Loss transaction eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'GAIN_LOSS_BLANKET_APPROVAL_IND', 'PooledFundControlTransactionsStep', 'A', 'Y', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-25::Bonnie::(MD5Sum: cd3d2036744330a381ce39bae92a84)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Pooled Fund Control Income Distribution transaction eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'INCOM_BLANKET_APPROVAL_IND', 'PooledFundControlTransactionsStep', 'A', 'Y', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-26::Bonnie::(MD5Sum: 862e6cd80bc51558f3e49606eafdab3)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Pooled Fund Control Purchase batch process', 'PURCHASE_DESCRIPTION', 'PooledFundControlTransactionsStep', 'A', 'Net Pooled Fund Purchases', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-27::Bonnie::(MD5Sum: 1c5a5ec2a87a373b722da015faea15ff)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Pooled fund Control Sale batch process', 'SALE_DESCRIPTION', 'PooledFundControlTransactionsStep', 'A', 'Net Pooled Fund Sales at Book', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-28::Bonnie::(MD5Sum: d68bc5dde521bfbb3273e1f2c894111e)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Pooled fund Control Sale Gain Loss batch process', 'GAIN_LOSS_DESCRIPTION', 'PooledFundControlTransactionsStep', 'A', 'Net Pooled Fund Sale Gains and Losses', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-29::Bonnie::(MD5Sum: 29698ef0bede7f83c8999de67f3ca897)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the eDocs produced as a result of the Pooled fund Control Income Distribution batch process', 'INCOME_DESCRIPTION', 'PooledFundControlTransactionsStep', 'A', 'Net Pooled Fund Income Distributions', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-30::Bonnie::(MD5Sum: b8ead0c7b6d7df6af4c35c54d3484bf)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Cash Sweep Purchase transaction eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'PURCHASE_BLANKET_APPROVAL_IND', 'CreateCashSweepTransactionsStep', 'A', 'Y', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-31::Bonnie::(MD5Sum: bab9e2de237dfb377e28a03238842c)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated Cash Sweep Sale eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'SALE_BLANKET_APPROVAL_IND', 'CreateCashSweepTransactionsStep', 'A', 'Y', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-32::Bonnie::(MD5Sum: 14e4bd9d832ee7ba3b23bf2243895c)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the asset purchase eDocs produced as a result of the Cash Sweep batch process', 'PURCHASE_DESCRIPTION', 'CreateCashSweepTransactionsStep', 'A', 'Increase Cash Sweep Investments', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-33::Bonnie::(MD5Sum: 352065813d927d37ad6639157bd4ad6e)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the asset sale eDocs produced as a result of the Cash Sweep batch process', 'SALE_DESCRIPTION', 'CreateCashSweepTransactionsStep', 'A', 'Decrease Cash Sweep Investments', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-34::Bonnie::(MD5Sum: b947ffe9131d479723e26fb015b999b)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated ACI Purchase eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'PURCHASE_BLANKET_APPROVAL_IND', 'CreateAutomatedCashInvestmentTransactionsStep', 'A', 'Y', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-35::Bonnie::(MD5Sum: ecb64660a883b58968f0c27fd4e432)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('Flag to specify if an automatically system generated ACI Sale eDoc will be submitted with blanket approval Yes or for manual approval through workflow No', 'SALE_BLANKET_APPROVAL_IND', 'CreateAutomatedCashInvestmentTransactionsStep', 'A', 'Y', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-36::Bonnie::(MD5Sum: eb646b8fff8444201d5a40b78b32a45e)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the asset purchase eDocs produced as a result of the ACI batch process', 'PURCHASE_DESCRIPTION', 'CreateAutomatedCashInvestmentTransactionsStep', 'A', 'Purchase Pooled Funds', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Changeset updates/2010-09-13-5475f-1-KRNS_PARM_T.xml::5475f-1-37::Bonnie::(MD5Sum: db77371fc1ed22a6eb751c21501276ad)
-- drop all existing KFS_ENDOW system parameters and then create new ones.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `ver_nbr`, `NMSPC_CD`) VALUES ('The standard description to be used for the batch header of the asset sale eDocs produced as a result of the ACI batch process', 'SALE_DESCRIPTION', 'CreateAutomatedCashInvestmentTransactionsStep', 'A', 'Sell Pooled Funds', uuid(), 'KFS', 'CONFG', 1, 'KFS-ENDOW');


-- Release Database Lock

-- Release Database Lock

