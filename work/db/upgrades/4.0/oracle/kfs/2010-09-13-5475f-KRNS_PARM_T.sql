delete from KRNS_PARM_T where NMSPC_CD='KFS-ENDOW'; 

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'PooledFundValue', 'DISTRIBUTIONS_PER_YEAR', 'CONFG', '2', 'The number of times per year that the institution will distribute income to the account holders.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'EndowmentTransactionCode', 'ASSET_TYPE', 'CONFG', 'AS', 'The value of this parameter is used to map the Asset Endowment Transaction Type to the Assets code defined in the Basic Accounting Category table.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'EndowmentTransactionCode', 'EXPENSE_TYPE', 'CONFG', 'EX', 'The value of this parameter is used to map the Expense Endowment Transaction Type to the Expenses code defined in the Basic Accounting Category table.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'EndowmentTransactionCode', 'INCOME_TYPE', 'CONFG', 'IN', 'The value of this parameter is used to map the Income Endowment Transaction Type to the Income code defined in the Basic Accounting Category table.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'EndowmentTransactionCode', 'LIABILITY_TYPE', 'CONFG', 'LI', 'The value of this parameter is used to map the Liabilities Endowment Transaction Type to the Liabilities code defined in the Basic Accounting Category table.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'KEMID', 'KEMID_VALUE', 'CONFG', 'Manual', 'The creation of a new KEMID value will be dependent upon the institutional parameter chosen for the KEMID_VALUE: 1) If the parameter is Automatic, each KEMID initiated will be sequentially numbered by the system based on a database sequence and beginning with the value the sequence has been configured to start with. 2) If the parameter is Automatic, each KEMID initiated will be sequentially numbered by the system based on a database sequence and beginning with the value the sequence has been configured to start with.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'All', 'TAX_LOTS_ACCOUNTING_METHOD', 'CONFG', 'Average Balance', 'This parameter is used to identify the institutional selection for tracking security Tax Lots and transactions involving cash. The possible values for this parameter are: Average Balance, FIFO (First In - First Out) or LIFO (Last In - First Out).', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'CreateAccrualTransactionsStep', 'BLANKET_APPROVAL_IND', 'CONFG', 'Y', 'Flag to specify if an automatically (system) generated Accrual transaction eDoc will be submitted with blanket approval (Yes) or for manual approval through workflow (No).' , 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'CreateAccrualTransactionsStep', 'DESCRIPTION', 'CONFG', 'Accrued Income Distribution', 'The standard description to be used for the batch header of the eDocs produced as a result of the Accrual Transactions batch process.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'EndowmentRecurringCashTransfer',
'DOCUMENT_TYPES', 'CONFG', 'ECT;EGLT', 'The values of this parameter represent a complete list of document type names supported by the endowment recurring cash transfer maintenance documents. The application maps this list to the document types that have been registered with the Workflow Engine.' , 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'EndowmentRecurringCashTransfer',
'ALLOW_NEGATIVE_BALANCE_IND', 'CONFG', 'Y', 'If Y, then the Target transaction amount can exceed the calculated cash equivalents balance.' , 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'EndowmentToGLTransferOfFunds',
'GL_OBJECT_SUB_TYPES', 'CONFG', 'MT; TN', 'The KFS object subtypes that are allowed for the KEM to GL Cash Transfer transaction types.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'GLToEndowmentTransferOfFunds',
'GL_OBJECT_SUB_TYPES', 'CONFG', 'MT; TN', 'The KFS object subtypes that are allowed for the GL to KEM Cash Transfer transaction types.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'CashTransfer',
'CASH_OBJECT_SUB_TYPES', 'CONFG', 'MT; TN', 'The KFS object subtypes that are allowed for the KEM to KEM Cash Transfer transaction type.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'SecurityTransfer',
'HOLDING_OBJECT_SUB_TYPES', 'CONFG', 'MT; TN', 'The KFS object subtypes that are allowed for the KEM to KEM Holding Transfer transaction type.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'Batch',
'FISCAL_YEAR_END_MONTH_AND_DAY', 'CONFG', '0630', 'The month and day marking the last date of the fiscal year (0630 means June 30th).', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'Batch',
'MAXIMUM_TRANSACTION_LINES', 'CONFG', '1000', 'The maximum number of transaction lines that the institution prefers for the eDocs that are generated automatically as a result of a nightly batch process.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'All',
'USE_PROCESS_DATE_IND', 'CONFG', 'Y', 'If USE_PROCESS_DATE_IND=Y, get the value from CURRENT_PROCESS_DATE. If USE_PROCESS_DATE_IND=N, get the current date from the local system using standard Java API.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'All',
'CURRENT_PROCESS_DATE', 'CONFG', to_char(SYSDATE, 'MM/DD/YYYY'), 'The Current Process Date.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'TransactionArchive',
'DOCUMENT_TYPES', 'CONFG', 'EAD;EAI;ECDD;ECI;ECT;ECA;EUSA;ELD;ELI;EST;EHA;EGLT;GLET', 'The values of this parameter represent a complete list of document type names for the endowment transactional documents that can be posted to the endowment transaction archive table. The application maps this list to the document types that have been registered with the Workflow Engine.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'PooledFundControlTransactionsStep', 'PURCHASE_BLANKET_APPROVAL_IND', 'CONFG', 'Y', 'Flag to specify if an automatically (system) generated Pooled Fund Control Purchase transaction eDoc will be submitted with blanket approval (Yes) or for manual approval through workflow (No).' , 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'PooledFundControlTransactionsStep', 'SALE_BLANKET_APPROVAL_IND', 'CONFG', 'Y', 'Flag to specify if an automatically (system) generated Pooled Fund Control Sale transaction eDoc will be submitted with blanket approval (Yes) or for manual approval through workflow (No).', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'PooledFundControlTransactionsStep', 'GAIN_LOSS_BLANKET_APPROVAL_IND', 'CONFG', 'Y', 'Flag to specify if an automatically (system) generated Pooled Fund Control Sale Gain Loss transaction eDoc will be submitted with blanket approval (Yes) or for manual approval through workflow (No).' , 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'PooledFundControlTransactionsStep', 'INCOM_BLANKET_APPROVAL_IND', 'CONFG', 'Y', 'Flag to specify if an automatically (system) generated Pooled Fund Control Income Distribution transaction eDoc will be submitted with blanket approval (Yes) or for manual approval through workflow (No).', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'PooledFundControlTransactionsStep', 'PURCHASE_DESCRIPTION', 'CONFG', 'Net Pooled Fund Purchases', 'The standard description to be used for the batch header of the eDocs produced as a result of the Pooled Fund Control Purchase batch process.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'PooledFundControlTransactionsStep', 'SALE_DESCRIPTION', 'CONFG', 'Net Pooled Fund Sales at Book', 'The standard description to be used for the batch header of the eDocs produced as a result of the Pooled fund Control Sale batch process.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'PooledFundControlTransactionsStep', 'GAIN_LOSS_DESCRIPTION', 'CONFG', 'Net Pooled Fund Sale Gains and Losses', 'The standard description to be used for the batch header of the eDocs produced as a result of the Pooled fund Control Sale Gain Loss batch process.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'PooledFundControlTransactionsStep', 'INCOME_DESCRIPTION', 'CONFG', 'Net Pooled Fund Income Distributions', 'The standard description to be used for the batch header of the eDocs produced as a result of the Pooled fund Control Income Distribution batch process.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'CreateCashSweepTransactionsStep', 'PURCHASE_BLANKET_APPROVAL_IND', 'CONFG', 'Y', 'Flag to specify if an automatically (system) generated Cash Sweep Purchase transaction eDoc will be submitted with blanket approval (Yes) or for manual approval through workflow (No).', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'CreateCashSweepTransactionsStep', 'SALE_BLANKET_APPROVAL_IND', 'CONFG', 'Y', 'Flag to specify if an automatically (system) generated Cash Sweep Sale eDoc will be submitted with blanket approval (Yes) or for manual approval through workflow (No).', 'A');


insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'CreateCashSweepTransactionsStep', 'PURCHASE_DESCRIPTION', 'CONFG', 'Increase Cash Sweep Investments', 'The standard description to be used for the batch header of the asset purchase eDocs produced as a result of the Cash Sweep batch process.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'CreateCashSweepTransactionsStep', 'SALE_DESCRIPTION', 'CONFG', 'Decrease Cash Sweep Investments', 'The standard description to be used for the batch header of the asset sale eDocs produced as a result of the Cash Sweep batch process.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'CreateAutomatedCashInvestmentTransactionsStep', 'PURCHASE_BLANKET_APPROVAL_IND', 'CONFG', 'Y', 'Flag to specify if an automatically (system) generated ACI Purchase eDoc will be submitted with blanket approval (Yes) or for manual approval through workflow (No).', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'CreateAutomatedCashInvestmentTransactionsStep', 'SALE_BLANKET_APPROVAL_IND', 'CONFG', 'Y', 'Flag to specify if an automatically (system) generated ACI Sale eDoc will be submitted with blanket approval (Yes) or for manual approval through workflow (No).', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'CreateAutomatedCashInvestmentTransactionsStep', 'PURCHASE_DESCRIPTION', 'CONFG', 'Purchase Pooled Funds', 'The standard description to be used for the batch header of the asset purchase eDocs produced as a result of the ACI batch process.', 'A');

insert into KRNS_PARM_T columns (NMSPC_CD, APPL_NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD) values ('KFS-ENDOW', 'KFS', 'CreateAutomatedCashInvestmentTransactionsStep', 'SALE_DESCRIPTION', 'CONFG', 'Sell Pooled Funds', 'The standard description to be used for the batch header of the asset sale eDocs produced as a result of the ACI batch process.', 'A');








 





