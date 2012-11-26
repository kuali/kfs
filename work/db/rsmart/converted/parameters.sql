-- Create namespace
INSERT INTO KRNS_NMSPC_T (NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND)
VALUES ('KFS-TEM',SYS_GUID(),1,'Kuali Travel/Entertainment Module','Y');

-- Parameter Component
INSERT INTO KRNS_PARM_DTL_TYP_T(NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND)
VALUES('KFS-TEM','Document',SYS_GUID(),1,'Document','Y');

INSERT INTO KRNS_PARM_DTL_TYP_T(NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND)
VALUES('KFS-TEM','TravelEntertainment',SYS_GUID(),1,'TravelEntertainment','Y');

INSERT INTO KRNS_PARM_DTL_TYP_T(NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND)
VALUES('KFS-TEM','TravelRelocation',SYS_GUID(),1,'TravelRelocation','Y');

INSERT INTO KRNS_PARM_DTL_TYP_T(NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND)
VALUES('KFS-TEM','TemProfile',SYS_GUID(),1,'TemProfile','Y');

INSERT INTO KRNS_PARM_DTL_TYP_T(NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND)
VALUES('KFS-TEM','AgencyMatchProcess',SYS_GUID(),1,'AgencyMatchProcess','Y');

INSERT INTO KRNS_PARM_DTL_TYP_T(NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND)
VALUES('KFS-TEM','ProfileExport',SYS_GUID(),1,'ProfileExport','Y');

INSERT INTO KRNS_PARM_DTL_TYP_T(NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND)
VALUES('KFS-TEM','AgencyDataValidation',SYS_GUID(),1,'AgencyDataValidation','Y');

-- Travel Parameters
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','ENABLE_PER_DIEM_CATEGORIES',SYS_GUID(),1,'CONFG','LODGING=Y;MILEAGE=Y;PER_DIEM=Y','When the flag is Y, the per diem category will be displayed. Format will be LODGING=Y;MILEAGE=Y;PER_DIEM=Y.  If the flag is all ?N,"N",  Trip Details Estimate Tab/Per Diem section will not be displayed.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','NON_EMPLOYEE_TRAVELER_TYPE_CODES',SYS_GUID(),1,'CONFG','NON','The traveler type code(s) that represents a non-employee.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','EMPLOYEE_TRAVELER_TYPE_CODES',SYS_GUID(),1,'CONFG','EMP','The traveler type code(s) that represents an employee.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','TRAVEL_DOCUMENTATION_LOCATION_CODE',SYS_GUID(),1,'CONFG','T','Document location code for populating DV from Travel Auth.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','TRAVEL_COVERSHEET_INSTRUCTIONS',SYS_GUID(),1,'CONFG','T','Derives instructions so institutions will be able to place the instructions of their choice.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','EMPLOYEE_CERTIFICATION_STATEMENT',SYS_GUID(),1,'CONFG','I certify the statements here in are true and just in all respects; that payment of the amounts claimed has not and will not be reimbursed to me from any other sources; that travel performed for which reimbursement is claimed was performed by me on State business and that no claims are included for expense of a personal or political nature or for any other expense not authorized by the State of Colorado Fiscal Rules; and that I actually incurred or paid the operating expenses of the motor vehicles for which reimbursement is claimed.','The statement displayed in the Employee Certification tab of the Travel Expense claim document when the Traveler Type is "Employee"','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','NON_EMPLOYEE_CERTIFICATION_STATEMENT',SYS_GUID(),1,'CONFG','(TBD)','The statement displayed in the Employee Certification tab of the Travel Expense claim document when the Traveler Type is "Non-Employee"','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','CUMULATIVE_REIMBURSABLE_AMT_WITHOUT_DIV_APPROVAL',SYS_GUID(),1,'CONFG','10000','The cumulative amount for reimbursement without division approval','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','INCIDENTALS_WITH_MEALS_ONLY_IND',SYS_GUID(),1,'CONFG','N','When this parameter is "Y", then the incidentals are added to the Per Diem only when there is at least one meal (breakfast, lunch or dinner) claimed. If "N", then the incidentals will be added regardless.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','QUARTER_DAY_TIME_TABLE',SYS_GUID(),1,'CONFG','1=6:00;2=12:00;3=18:00;4=24:00','Defines the quarter time table for a day. The format is quarter=ending time in the military format.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','ALLOW_TRAVELER_ADDRESS_CHANGE_IND',SYS_GUID(),1,'CONFG','Y','Parameter to determine whether the person filling out a form can modify the address.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','HOSTED_MEAL_EXPENSE_TYPES',SYS_GUID(),1,'CONFG','BREAKFAST=HB;LUNCH=HL;DINNER=HD;','Describes what the expense types are for various hosted meals. This allows the application to determine not only whether an other expense is a meal, but which meal and whether it was hosted or not.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','NUMBER_OF_TR_DELINQUENT_DAYS',SYS_GUID(),1,'CONFG','W=60;S=120','Parameter to determine whether a Travel Document is considered delinquent and the appropriate action for it.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','EXPENSE_TYPE_FOR_AIRFARE',SYS_GUID(),1,'CONFG','A','Specifies the expense type codes for Airline to enforce airfare related business rules.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','EXPENSE_TYPE_FOR_MILEAGE',SYS_GUID(),1,'CONFG','MM','Specifies the expense type code for Mileage to enforce mileage related business rules.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','EXPENSE_TYPE_FOR_RENTAL_CAR',SYS_GUID(),1,'CONFG','R','Specifies the expense type code for rental car to enforce rental car related business rules.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','EXPENSE_TYPE_FOR_LODGING',SYS_GUID(),1,'CONFG','L','Specifies the expense type code for lodging.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','EXPENSE_TYPE_FOR_LODGING_ALLOWANCE',SYS_GUID(),1,'CONFG','LA','Specifies the expense type code for lodging allowance.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','ALLOW_TRAVEL_OFFICE_TO_MODIFY_ALL_IND',SYS_GUID(),1,'CONFG','N','When this flag is "Y",? the travel office can modify any amount fields on the TR document.','A','KFS');
-- KUALITEM-1170 (change VENDOR_PAYMENT_DV_REASON_CODE to a generic document parameter)
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','VENDOR_PAYMENT_DV_REASON_CODE',SYS_GUID(),1,'CONFG','3','Travel Vendor Payments','A','KFS');
-- KUALITEM-833
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','EXPENSE_TYPES_REQUIRING_SPECIAL_REQUEST_APPROVAL',SYS_GUID(),1,'CONFG','SPEC','Specifies the expense type codes requiring special request approval routing (e.g. show tickets).','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','ALWAYS_REIMBURSABLE_CARD_TYPE',SYS_GUID(),1,'CONFG','MC','Specifies the card types used in the Imported Tab where the expense is always reimbursable (for example, CTS card).','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','ENABLE_AMOUNT_DUE_CORP_CARD_TOTAL_LINE_IND',SYS_GUID(),1,'CONFG','Y','When this flag is enabled, Due Corporate Card Total line is displayed in the Total tab.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','ENABLE_CORP_CARD_PAYMENT_DV_IND',SYS_GUID(),1,'CONFG','Y','When this parameter is enabled and the corporate credit card transactions are included in the imported expense, a DV is spawned to pay the corporate credit card company when the document becomes final.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','DISABLE_IMPORTED_EXPENSE_DETAIL_IND',SYS_GUID(),1,'CONFG','N','When this parameter is "Y," the Imported Expense Detail section of the Imported Expense tab is disabled.','A','KFS');

-- Travel Authorization Parameters
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','FIRST_AND_LAST_DAY_PER_DIEM_PERCENTAGE',SYS_GUID(),1,'CONFG','75','Percentage of Per Diem allowed on the first and last day of travel.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','ENABLE_DV_FOR_TRAVEL_ADVANCE_IND',SYS_GUID(),1,'CONFG','Y','When this flag is "Y", the DV document is spawned to request a travel advance payment.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','TRAVEL_ADVANCE_DV_PAYMENT_REASON_CODE',SYS_GUID(),1,'CONFG','1','DV Payment Reason Code for the travel advance payment.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','TRAVEL_ADVANCE_PAYMENT_CHART_CODE',SYS_GUID(),1,'CONFG','UA','Chart of Account Code where the travel advance payment should be paid from.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','TRAVEL_ADVANCE_PAYMENT_ACCOUNT_NBR',SYS_GUID(),1,'CONFG','9323001','Account Number where the travel advance payment should be paid from.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','TRAVEL_ADVANCE_PAYMENT_OBJECT_CODE',SYS_GUID(),1,'CONFG','1960','Object Code where the travel advance payment should be paid from.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','ENABLE_AR_INV_FOR_TRAVL_ADVANCE_IND',SYS_GUID(),1,'CONFG','Y','When this flag is "Y", the TA document spawns CUS and INV documents to establish receivables.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','TRAVEL_ADVANCE_BILLING_CHART_CODE',SYS_GUID(),1,'CONFG','UA','Account Number where the travel advance payment should be paid from.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','TRAVEL_ADVANCE_BILLING_ORG_CODE',SYS_GUID(),1,'CONFG','VPIT','Processing Organization Code for Travel Advance.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','TRAVEL_ADVANCE_INVOICE_ITEM_CODE',SYS_GUID(),1,'CONFG','TRAVEL','Invoice Item Code, which should be used to establish receivables.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','NUMBER_OF_DAYS_DUE',SYS_GUID(),1,'CONFG','60','The number of days to add to current date to derive the AR receivable due date.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','ENABLE_CONTACT_INFORMATION_IND',SYS_GUID(),1,'CONFG','Y','Should emergency contact information show up?','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','TRAVELER_AR_CUSTOMER_TYPE',SYS_GUID(),1,'CONFG','11','Customer Type used by Travel and Entertainment module for cash advance tracking.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','ENABLE_PER_DIEM_LOOKUP_LINKS_IND',SYS_GUID(),1,'CONFG','Y','When this flag is turned on, the user will see government per diem sites from the DV Per Diem Table (FP_DV_DIEM_T) when entering the primary destination.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','ALLOW_FREE_FORMAT_PRIMARY_DESTINATION_IND',SYS_GUID(),1,'CONFG','Y','When this flag is "Y", the user may override the primary destination returned from the table, or, enter the value as a free form text.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','ENABLE_TA_PER_DIEM_AMOUNT_EDIT_IND',SYS_GUID(),1,'CONFG','Y','When this flag is "Y", the per diem amount field for TA opens up for editing and the initiator may reduce down the per diem amount.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','ENABLE_VENDOR_PAYMENT_BEFORE_TA_FINAL_APPROVAL_IND',SYS_GUID(),1,'CONFG','N','If this flag is "Y," the Pre-Trip Payments and Requisitions links become available in the TA custom search prior to TA approval.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','HOLD_NEW_FY_ENCUMBRANCES_IND',SYS_GUID(),1,'CONFG','Y','When this flag is "Y," the new fiscal year encumbrances will be held and will be posted by the year-end encumbrance forwards job.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','DEFAULT_CHART_CODE',SYS_GUID(),1,'CONFG','BL','Default chart code for creating accounting lines','A','KFS');

-- KUALITEM-705
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','CASH_ADVANCE_CREDIT_CARD_TYPES',SYS_GUID(),1,'CONFG','VISA,AMEX,MC','Card types that are allowed for advances.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','ENABLE_CC_CASH_ADVANCE_WARNING_IND',SYS_GUID(),1,'CONFG','Y','Determines whether to show a warning on cash advances.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','MULTIPLE_CASH_ADVANCES_ALLOWED_IND',SYS_GUID(),1,'CONFG','Y','Allows multiple cash advances for TAA documents.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','ENABLE_TRAVEL_ADVANCES_PAYMENT_METHOD_IND',SYS_GUID(),1,'CONFG','Y','Enables a payment method drop down','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelAuthorization','TRAVEL_ADVANCES_POLICY_URL',SYS_GUID(),1,'CONFG','http://www.rsmart.com','Link to the travel advance policy','A','KFS');

-- Travel Reimbursement Parameters
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','ALLOW_TR_WITHOUT_TA_IND',SYS_GUID(),1,'CONFG','Y','When this parameter is "Y" the TR document can be initiated without TA and the Travel Number will be generated by the TR document','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','DEFAULT_CHART_CODE',SYS_GUID(),1,'CONFG','BL','Default chart code for creating accounting lines','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','FOREIGN_CURRENCY_CONVERSION_URL',SYS_GUID(),1,'CONFG','http://www.federalreserve.gov/releases/h10/update/','Parameter containing the URL of the federal currency conversion site','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','DISPLAY_ADVANCES_IN_REIMBURSEMENT_TOTAL_IND',SYS_GUID(),1,'CONFG','Y','Flag whether to show the Less Advances for this Trip and Reimbursment for this Trip fields','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','DISPLAY_ENCUMBRANCE_IND',SYS_GUID(),1,'CONFG','Y','Flag whether to show the Encumbrance Total field','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','TRAVEL_PAYMENT_MEDIUM_TYPE_CODE',SYS_GUID(),1,'CONFG','TR','Configurable AR Payment Medium Code for the TEM to use.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','REIMBURSEMENT_DOCUMENTATION_LOCATION_CODE',SYS_GUID(),1,'CONFG','TR','Location to be used to derive the documentation location for the TR Coversheet','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','ENABLE_ACCOUNTING_DISTRIBUTION_TAB_IND',SYS_GUID(),1,'CONFG','Y','Enables accounting distribution tab (summary of expense by object code). When the parameter is "N," only the normal Accounting Lines are displayed.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','VALID_OBJECT_LEVELS',SYS_GUID(),1,'CONFG','TRVL','When the ENABLE_ ACCOUNTING_DISTRIBUTION_TAB parameter = "N" then the object codes are validated against this list of valid object levels.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','VALID_OBJECT_CODES',SYS_GUID(),1,'CONFG','6000','When ENABLE_ ACCOUNTING_DISTRIBUTION_TAB parameter = ?N" the object codes are validated against this list of valid codes.  Works with VALID_OBJECT_LEVELS.  The list of object codes in this parameter can be denied within the allowed VALID_OBJECT_LEVELS.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','AR_REFUND_CHART_CODE',SYS_GUID(),1,'CONFG','UA','Chart of Account Code where the chart code should be paid.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','AR_REFUND_ACCOUNT_NBR',SYS_GUID(),1,'CONFG','9323001','Account Number where the reimbursements should be paid.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','AR_REFUND_OBJECT_CODE',SYS_GUID(),1,'CONFG','6000','Object Code where the travel advance payment should be charged.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','LODGING_TYPE_CODES',SYS_GUID(),1,'CONFG','L','Expense Type Codes used for Lodging. This is used when generating reports to determine how to report lodging expenses. If reports do not matter to you, then neither does this.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','TRANSPORTATION_TYPE_CODES',SYS_GUID(),1,'CONFG','A,R,T','Expense Type Codes used for Transportation. This is used when generating reports to determine how to report transporation expenses. If reports do not matter to you, then neither does this','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','APPLY_REIMBURSEMENT_AGAINST_MULTIPLE_INVOICES_IND',SYS_GUID(),1,'CONFG','Y','If this flag is on, then the reimbursements will be applied to multiple invoices.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','REIMBURSEMENT_PERCENT_OVER_ENCUMBRANCE_AMT',SYS_GUID(),1,'CONFG','15','The reimbursement percentage over encumbrance amount','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','HOSTED_MEAL_EXPENSE_TYPES',SYS_GUID(),1,'CONFG','BREAKFAST=HB;LUNCH=HL;DINNER=HD;','Describes what the expense types are for various hosted meals. This allows the application to determine not only whether an other expense is a meal, but which meal and whether it was hosted or not.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','TRAVEL_REPORT_INSTITUTION_NAME',SYS_GUID(),1,'CONFG','rSmart','Institution Name to Appear in Report Titles','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','PER_DIEM_OBJECT_CODE',SYS_GUID(),1,'CONFG','EMP=IN=6100;EMP=OUT=6170;EMP=INT=6250;NON=IN=6140;NON=OUT=6150;NON=INT=6270;','The object code which is assigned to reimburse.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','LODGING_OBJECT_CODE',SYS_GUID(),1,'CONFG','EMP=IN=6100;EMP=OUT=6170;EMP=INT=6250;NON=IN=6140;NON=OUT=6150;NON=INT=6270;','The object code which is assigned to reimburse.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','ENABLE_ACCOUNT_DISTRIBUTION_TAB_IND',SYS_GUID(),1,'CONFG','N','Parameter to determine whether to show or hide the accounting distribution tab.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','ENABLE_TR_PER_DIEM_AMOUNT_EDIT_IND',SYS_GUID(),1,'CONFG','Y','When this flag is "Y", the per diem amount field for TA opens up for editing and the initiator may reduce down the per diem amount.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','ENABLE_AUTOMATIC_TR_IND',SYS_GUID(),1,'CONFG','Y','When this flag is "Y", the low risk travel reimbursements are approved with Fiscal Officer and Organization approvals.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','SHOW_TA_ESTIMATE_IN_SUMMARY_REPORT_IND',SYS_GUID(),1,'CONFG','N','When this flag is "Y", the TA estimate amount will be displayed in the TR Expense Summary Report for comparison purpose.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','TEM_BARCODE_STYLE',SYS_GUID(),1,'CONFG','39','Specifies barcode style. Choices are 39 (Code39) and 128 (Code128).','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','TEM_FAX_NUMBER',SYS_GUID(),1,'CONFG','(800) 555-1212','The fax number printed on the Fax Coversheet and all of the barcode forms.','A','KFS');

-- Relocation Parameters
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelRelocation','ENABLE_ACCOUNTING_DISTRIBUTION_TAB_IND',SYS_GUID(),1,'CONFG','Y','Enables accounting distribution tab (summary of expense by object code). When the parameter is "N," only the normal Accounting Lines are displayed.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelRelocation','DEFAULT_CHART_CODE',SYS_GUID(),1,'CONFG','BL','Default chart code for creating accounting lines','A','KFS');

-- Entertainment Parameters
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelEntertainment','HOST_CERTIFICATION_REQUIRED_IND',SYS_GUID(),1,'CONFG','Y','The host certification is required with the Entertainment document when the host is different from the person requesting reimbursement.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelEntertainment','DEFAULT_CHART_CODE',SYS_GUID(),1,'CONFG','BL','Default chart code for creating accounting lines','A','KFS');

-- Profile Parameters
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TemProfile','AR_CUSTOMER_TYPE_TO_TRAVELER_TYPE_CROSSWALK',SYS_GUID(),1,'CONFG','11=NON','Mapping between the AR Customer Type and the TEM Traveler Type. The format is AR Customer Type = Traveler Type','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TemProfile','KIM_AFFILIATION_TYPE_TO_TRAVELER_TYPE_CROSSWALK',SYS_GUID(),1,'CONFG','STAFF=EMP;STDNT=NON;FCLTY=EMP;AFLT=NON','Mapping between the KIM Affiliation Type and the TEM Traveler Type. The format is KIM Affiliation Type=Traveler Type','A','KFS');

-- Per Diem Load Parameters
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','PerDiemLoadStep','INSTITUTION_STATE',SYS_GUID(),1,'CONFG','CA','Determines if the per diem entry will be in-state or out-of-state.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','PerDiemLoadStep','OCONUS_MEAL_BREAKDOWN',SYS_GUID(),1,'CONFG','B=15;L=25;D=40','Breaks down the local meals into breakfast, lunch dinner, and incidentals for Outside the Continental United States','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','PerDiemLoadStep','DEFAULT_CONUS_MIE_BREAKDOWN',SYS_GUID(),1,'CONFG','B=15;L=25;D=40','Breaks down the MIE total into breakfast, lunch, and dinner in the event that the matching MEI total is not found in the CONUS MEAL_BREAKDOWN table','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','PerDiemLoadStep','IN_STATE_TRIP_TYPE_CODE',SYS_GUID(),1,'CONFG','IN','The trip type code for in-state travel','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','PerDiemLoadStep','OUT_STATE_TRIP_TYPE_CODE',SYS_GUID(),1,'CONFG','OUT','The trip type code for out-state travel','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','PerDiemLoadStep','INTERNATIONAL_TRIP_TYPE_CODE',SYS_GUID(),1,'CONFG','INT','The trip type code for international travel','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','PerDiemLoadStep','REJECT_FILE_WHEN_ERROR_IND',SYS_GUID(),1,'CONFG','Y','Determine whether the whole per diem file has to be rejected whenever an error occurs.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','PerDiemLoadStep','PREVIOUS_PER_DIEM_DEACTIVATION_IND',SYS_GUID(),1,'CONFG','Y','Determine whether the previous per diem has to be deactivated when a new one is loaded.','A','KFS');

-- AR Refund Parameters
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-AR','PaymentApplication','DV_DOCUMENT_ROUTE',SYS_GUID(),1,'CONFG','BLANKETAPPROVE','For APP to DV integration, defines (1) if the DV document should be submitted; and (2) to blanket approve or route for the normal route nodes. The three possible VALUES are SAVE, ROUTE, BLANKETAPPROVE.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-AR','PaymentApplication','DEFAULT_REFUND_PAYMENT_REASON_CODE',SYS_GUID(),1,'CONFG','E','The payment reason to use for the refund disbursement voucher if one is not defined by the System Information record for the processing organization.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-FP','DisbursementVoucher','AR_PRE_DISBURSEMENT_EXTRACT_ORGNIZATION',SYS_GUID(),1,'CONFG','KUAL','Allowed organization code for loading Disbursement Voucher payments to process refund checks for AR.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-FP','DisbursementVoucher','AR_PRE_DISBURSEMENT_EXTRACT_SUB_UNIT',SYS_GUID(),1,'CONFG','DV','Allowed sub unit code for loading Disbursement Voucher payments to process refund checks for AR.','A','KFS');
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-FP','DisbursementVoucher','CUSTOMER_PAYEE_TYPE_LABEL',SYS_GUID(),1,'CONFG','Customer','Label used for payees with type code C within the Disbursement Voucher.','A','KFS');

UPDATE KRNS_PARM_T SET TXT = 'BREAKFAST=HB;LUNCH=HL;DINNER=HD;' WHERE PARM_NM = 'HOSTED_MEAL_EXPENSE_TYPES';

-- GL Encumbrance Forward Step Parameters
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-GL','EncumbranceForwardStep','CLEAR_NEW_FY_ENCUMBRANCES_IND',SYS_GUID(),1,'CONFG','N','When this flag is "Y," the new fiscal year encumbrances will be cleared from the holding table after the year-end encumbrance forwards job has been run.','A','KFS');

-- Profile Export Parameters
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','ProfileExport','EXPORT_FILE_FORMAT',SYS_GUID(),1,'CONFG','csv','The format of the profile data exported from TEM. The valid VALUES are csv or xml.','A','KFS');

-- Agency Data Validation Parameters
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','AgencyDataValidation','VALIDATION_ACCOUNTING_LINE',SYS_GUID(),1,'CONFG','Account;Sub-Account','Defines the fields within the accounting line for matching between the TA encumbrance accounting line and downloaded agency data. List the accounting line elements that will be used for matching. Valid VALUES are: Account, Sub-Account, Object Code, Sub-Object Code.','A','KFS');

-- Agency Match Process Parameters
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','AgencyMatchProcess','CTS_AIR_OBJECT_CODE',SYS_GUID(),1,'CONFG','6000','Object Code to distribute airline charges when the Trip ID is now known.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','AgencyMatchProcess','CTS_RENTAL_CAR_OBJECT_CODE',SYS_GUID(),1,'CONFG','6180','Object Code to distribute rental car charges when the Trip ID is now known.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','AgencyMatchProcess','CTS_LODGING_OBJECT_CODE',SYS_GUID(),1,'CONFG','6100','Object Code to distribute lodging when the Trip ID is now known.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','AgencyMatchProcess','AP_CLEARING_CTS_PAYMENT_CHART',SYS_GUID(),1,'CONFG','UA','AP clearing chart code where the CTS payment was made.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','AgencyMatchProcess','AP_CLEARING_CTS_PAYMENT_ACCOUNT',SYS_GUID(),1,'CONFG','9323001','AP clearing account where the CTS payment was made.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','AgencyMatchProcess','AP_CLEARING_CTS_PAYMENT_SUB_ACCOUNT',SYS_GUID(),1,'CONFG','','AP clearing sub account where the CTS payment was made.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','AgencyMatchProcess','AP_CLEARING_CTS_PAYMENT_OBJECT_CODE',SYS_GUID(),1,'CONFG','6000','AP clearing object code where the CTS payment was made.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelEntertainment','ENT_REIMBURSEMENT_DV_REASON_CODE',SYS_GUID(),1,'CONFG','4','DV Reason Code for the Entertainment Reimbursements.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelEntertainment','ENTERTAINMENT_DOCUMENT_LOCATION',SYS_GUID(),1,'CONFG','T','DV Reason Code for the Entertainment Reimbursements.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelRelocation','TRAVEL_REPORT_INSTITUTION_NAME',SYS_GUID(),1,'CONFG','rSmart','Institution Name to Appear in Report Titles','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelRelocation','RELO_REIMBURSEMENT_DV_REASON_CODE',SYS_GUID(),1,'CONFG','5','Moving And Relocation Reimbursements','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelRelocation','RELOCATION_DOCUMENTATION_LOCATION_CODE',SYS_GUID(),1,'CONFG','T','Location to be used to initiate DV','A','KFS');

-- KUALITEM-761
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','ENABLE_VENDOR_PAYMENT_BEFORE_FINAL_TR_APPROVAL_IND',SYS_GUID(),1,'CONFG','Y','Parameter to determine whether to show or hide the Third Party Vendor payment button.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','DEFAULT_REFUND_PAYMENT_REASON_CODE',SYS_GUID(),1,'CONFG','2','Parameter for TR spawned DVs.','A','KFS');

-- KUALITEM-768
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','INTERNATIONAL_TRIP_TYPE_CODES',SYS_GUID(),1,'CONFG','INT','The trip type code(s) that represents international travel.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','SEPARATION_OF_DUTIES_ROUTING_OPTION',SYS_GUID(),1,'CONFG','F','F-Traveler and Fiscal Officer cannot be the same;D-Traveler and Division Approver cannot be the same','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelEntertainment','ENABLE_ACCOUNTING_DISTRIBUTION_TAB_IND',SYS_GUID(),1,'CONFG','Y','Enables accounting distribution tab (summary of expense by object code). When the parameter is "N," only the normal Accounting Lines are displayed.','A','KFS');

-- adding in the system parameter for a successful logout url
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KR-NS','All','LOGOFF_REDIRECT_URL',SYS_GUID(),1,'CONFG','http://temnightly.kfs.rsmart.com/cas/logout','The url to redirect to after a logout','A','KUALI');

-- Travel Notification Parameters
-- ----------------------------------------------
-- AR Aging Report Notification 
-- ----------------------------------------------
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-AR','CustomerAgingReportNotificationStep','AR_EMAIL_SENDER',SYS_GUID(),1,'CONFG','quickstart@localhost',
'The email address from which the notification is sent from','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-AR','CustomerAgingReportNotificationStep','CUSTOMER_AR_AGING_EMAIL_NOTIFICATION_DAYS',SYS_GUID(),1,'CONFG','30',
'The timing of when the aging email notification should be sent. The value should 30, 60, 90 or AGE. When the AGE is entered, it assumes the value of the CUSTOMER_INVOICE_AGE parameter.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-AR','CustomerAgingReportNotificationStep','AR_EMAIL_NOTIFICATION_SELECTION',SYS_GUID(),1,'CONFG','CHART=BL;ORG=VPIT,PSY',
'AR customer email notification selection criteria. Syntax is Selection Criteria=Value. Available selection criteria are: CHART, ORG and ACCOUNT','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-AR','CustomerAgingReportNotificationStep','AR_EMAIL_NOTIFICATION_TEXT',SYS_GUID(),1,'CONFG',
'The following advance is now past due and you have exceeded the IRS time frame allowable to submit payment/receipts after travel.  Since your advance is more than overdue, you are in the process of being considered for payroll deduction.  IF YOU HAVE ALREADY SUBMITTED YOUR VOUCHER, PLEASE NOTIFY US.  If you traveled for Study Abroad, notify them regarding the status of your voucher. For all others, please confirm with your department''s administrator that your voucher/payment was submitted to Voucher Processing.  If you feel we should have received your voucher/payment, please contact our office to confirm receipt.  PLEASE RESPOND VIA EMAIL. Your immediate attention to this matter is appreciated.', 
'The email body.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-AR','CustomerAgingReportNotificationStep','AR_EMAIL_NOTIFICATION_SUBJECT',SYS_GUID(),1,'CONFG','Customer Aging Report for ',
'The email notification subject.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-AR','CustomerAgingReportNotificationStep','AR_EMAIL_NOTIFICATION_SELECTION_TYPE',SYS_GUID(),1,'CONFG','PROCESSING_ORG',
'The notification selection option. Its valid value can be PROCESSING_ORG, BILLING_ORG or ACCOUNT','A','KFS');

-- ------------------------------------------------
-- Tax Ramification Notification 
-- ------------------------------------------------
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','TaxableRamificationNotificationStep','TEM_EMAIL_SENDER',SYS_GUID(),1,'CONFG','quickstart@localhost',
'The email address from which the notification is sent from','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','TaxableRamificationNotificationStep','TAX_RAMIFICATION_NOTIFICATION_DAYS',SYS_GUID(),1,'CONFG','120',
'The number of days after the cash advances are due to send the tax ramification notification to the travelers.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','TaxableRamificationNotificationStep','TAX_RAMIFICATION_NOTIFICATION_TEXT',SYS_GUID(),1,'CONFG',
'The following advance is now past due and you have exceeded the IRS time frame allowable to submit payment/receipts after travel.  Since your advance is more than overdue, you are in the process of being considered for payroll deduction.  IF YOU HAVE ALREADY SUBMITTED YOUR VOUCHER, PLEASE NOTIFY US.  If you traveled for Study Abroad, notify them regarding the status of your voucher. For all others, please confirm with your department''s administrator that your voucher/payment was submitted to Voucher Processing.  If you feel we should have received your voucher/payment, please contact our office to confirm receipt.  PLEASE RESPOND VIA EMAIL. Your immediate attention to this matter is appreciated.', 
'The text which should be sent to the travelers who have outstanding advances over a specified period.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','TaxableRamificationNotificationStep','TAX_RAMIFICATION_NOTIFICATION_SUBJECT',SYS_GUID(),1,'CONFG','Taxable Ramification Notice for ',
'The email notification subject.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','TaxableRamificationNotificationStep','SEND_TAX_NOTIFICATION_TO_FISCAL_OFFICER_IND',SYS_GUID(),1,'CONFG','N',
'When this indicator is Y, the FYI for the Tax Ramification Notification document is sent to the fiscal officer.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','UPLOAD_PARSER_INSTRUCTIONS_URL',SYS_GUID(),1,'CONFG','default.htm?turl=WordDocuments%2Fdataimporttemplates.htm','Travel import instructions URL.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','Document','CORP_CARD_BANK_PAYMENT_REASON_CODE',SYS_GUID(),1,'CONFG','6','Corporate Card Payment','A','KFS');

-- ------------------------------------------------
-- Notification on document status change 
-- ------------------------------------------------
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','Document','CAMPUS_TRAVEL_EMAIL_ADDRESS',SYS_GUID(),1,'CONFG','travel@localhost',
'The campus travel email address from which the notification is sent','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','Document','ON_CHANGE_NOTIFICATION_ENABLED_IND',SYS_GUID(),1,'CONFG','Y',
'Indicates the notification is enabled on travel document status changes','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','Document','TRAVEL_AUTHORIZATION_DOC_TYPES_FOR_ON_CHANGE_NOTIFICATION',SYS_GUID(),1,'CONFG','TA',
'the travel authorization document type codes qualified for on change notification','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','Document','TRAVEL_EXPENSE_DOC_TYPES_FOR_ON_CHANGE_NOTIFICATION',SYS_GUID(),1,'CONFG','TR;ENT;RELO',
'the travel expense document type codes qualified for on change notification','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','Document','TRAVEL_DOCUMENT_NOTIFICATION_SUBJECT',SYS_GUID(),1,'CONFG','Travel Document Has Been Changed',
'The email notification subject.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','Document','TRAVEL_DOCUMENT_NOTIFICATION_SUBJECT_TA_ON_FINAL',SYS_GUID(),1,'CONFG','Travel Authorization Final',
'The email notification subject on TA document status final','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','Document','TRAVEL_DOCUMENT_NOTIFICATION_SUBJECT_TA_ON_CHANGE',SYS_GUID(),1,'CONFG','Travel Authorization Status Change',
'The email notification subject on TA document status changed','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','Document','TRAVEL_DOCUMENT_NOTIFICATION_SUBJECT_TER_ON_FINAL',SYS_GUID(),1,'CONFG','Expense Report Final',
'The email notification subject on TER document status final','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','Document','TRAVEL_DOCUMENT_NOTIFICATION_SUBJECT_TER_ON_CHANGE',SYS_GUID(),1,'CONFG','Expense Report Status Change',
'The email notification subject on TER document status changed','A','KFS');

-- -----------------------------------
-- Notification of imported expenses
-- -----------------------------------

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','TravelImportedExpenseNotificationStep','IMPORTED_EXPENSE_NOTIFICATION_SUBJECT',SYS_GUID(),1,'CONFG','Newly Imported Expenses',
'The email notification subject.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','TravelImportedExpenseNotificationStep','IMPORTED_EXPENSE_NOTIFICATION_TEXT',SYS_GUID(),1,'CONFG',
'You have newly imported expenses from corporate card, CTS, or pre-trip payments that need to be reconciled.', 
'The text which should be sent to the travelers who have newly imported expenses.','A','KFS');

-- -----------------------------------
-- All notification
-- -----------------------------------
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) 
VALUES ('KFS-TEM','All','TEM_EMAIL_SENDER',SYS_GUID(),1,'CONFG','quickstart@localhost',
'The email address from which the notification is sent from','A','KFS');

INSERT INTO KRNS_PARM_DTL_TYP_T(NMSPC_CD,PARM_DTL_TYP_CD,OBJ_ID,VER_NBR,NM,ACTV_IND) 
VALUES ('KFS-TEM','All',SYS_GUID(),1,'All','Y');

-- KFSCNTRB-1148 START
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','CorpCardApplication','GENERATE_CC_NUMBER_IND',SYS_GUID(),1,'CONFG','Y','When this parameter is "Y", a psuedo card number is generated.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','CorpCardApplication','CORP_CARD_CODE',SYS_GUID(),1,'CONFG','USBC','Credit card code for corp cards created from a CCAP document.','A','KFS');

INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','CTSCardApplication','CTS_CARD_CODE',SYS_GUID(),1,'CONFG','CTSC','Code for CTS cards created from a CTAP document.','A','KFS');
-- KFSCNTRB-1148 END

-- KFSCNTRB-1345 START
INSERT INTO KRNS_PARM_T (NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD)
VALUES ('KFS-TEM','TravelReimbursement','ALLOW_PRETRIP_REIMBURSEMENT_IND',SYS_GUID(),1,'CONFG','Y','Disables trip date validation','A','KFS');
-- KFSCNTRB-1345 END