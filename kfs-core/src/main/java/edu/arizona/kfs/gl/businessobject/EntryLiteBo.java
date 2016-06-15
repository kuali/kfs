package edu.arizona.kfs.gl.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.sys.KFSPropertyConstants;

/**
 * This class is used to retrieve the data in the table for Entry objects, without retrieving any reference objects.
 *
 * @author Adam Kost <kosta@email.arizona.edu> with some code adapted from UCI
 */

@SuppressWarnings("deprecation")
public class EntryLiteBo extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 7915615641174612210L;

    // database fields
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private String universityFiscalPeriodCode;
    private String financialDocumentTypeCode;
    private String financialSystemOriginationCode;
    private String documentNumber;
    private Integer transactionLedgerEntrySequenceNumber;
    private String transactionLedgerEntryDescription;
    private KualiDecimal transactionLedgerEntryAmount;
    private String transactionDebitCreditCode;
    private Date transactionDate;
    private String organizationDocumentNumber;
    private String projectCode;
    private String organizationReferenceId;
    private String referenceFinancialDocumentTypeCode;
    private String referenceFinancialSystemOriginationCode;
    private String referenceFinancialDocumentNumber;
    private Date financialDocumentReversalDate;
    private String transactionEncumbranceUpdateCode;
    private Date transactionPostingDate;
    private Timestamp transactionDateTimeStamp;

    // transient, dynamically generated fields
    private transient String gecDocumentNumber;
    private transient String entryId;

    private static transient volatile BusinessObjectService businessObjectService;

    private static BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getTransactionLedgerEntrySequenceNumber() {
        return transactionLedgerEntrySequenceNumber;
    }

    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
        this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
    }

    public String getTransactionLedgerEntryDescription() {
        return transactionLedgerEntryDescription;
    }

    public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
        this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
    }

    public KualiDecimal getTransactionLedgerEntryAmount() {
        return transactionLedgerEntryAmount;
    }

    public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount) {
        this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
    }

    public String getTransactionDebitCreditCode() {
        return transactionDebitCreditCode;
    }

    public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
        this.transactionDebitCreditCode = transactionDebitCreditCode;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getOrganizationDocumentNumber() {
        return organizationDocumentNumber;
    }

    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    public String getReferenceFinancialDocumentTypeCode() {
        return referenceFinancialDocumentTypeCode;
    }

    public void setReferenceFinancialDocumentTypeCode(String referenceFinancialDocumentTypeCode) {
        this.referenceFinancialDocumentTypeCode = referenceFinancialDocumentTypeCode;
    }

    public String getReferenceFinancialSystemOriginationCode() {
        return referenceFinancialSystemOriginationCode;
    }

    public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode) {
        this.referenceFinancialSystemOriginationCode = referenceFinancialSystemOriginationCode;
    }

    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }

    public Date getFinancialDocumentReversalDate() {
        return financialDocumentReversalDate;
    }

    public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
        this.financialDocumentReversalDate = financialDocumentReversalDate;
    }

    public String getTransactionEncumbranceUpdateCode() {
        return transactionEncumbranceUpdateCode;
    }

    public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode) {
        this.transactionEncumbranceUpdateCode = transactionEncumbranceUpdateCode;
    }

    public Date getTransactionPostingDate() {
        return transactionPostingDate;
    }

    public void setTransactionPostingDate(Date transactionPostingDate) {
        this.transactionPostingDate = transactionPostingDate;
    }

    public Timestamp getTransactionDateTimeStamp() {
        return transactionDateTimeStamp;
    }

    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
        this.transactionDateTimeStamp = transactionDateTimeStamp;
    }

    public String getGecDocumentNumber() {
        if (gecDocumentNumber == null) {
            gecDocumentNumber = generateGecDocumentNumber();
        }
        return gecDocumentNumber;
    }

    public void setGecDocumentNumber(String gecDocumentNumber) {
        this.gecDocumentNumber = gecDocumentNumber;
    }

    public String getEntryId() {
        if (entryId == null) {
            entryId = generateEntryId();
        }
        return entryId;
    }

    private String generateGecDocumentNumber() {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.REFERENCE_NUMBER, getDocumentNumber());
        Collection<SourceAccountingLine> accountingLineList = getBusinessObjectService().findMatching(SourceAccountingLine.class, fieldValues);

        for (AccountingLine accountingLine : accountingLineList) {
            String documentNumber = accountingLine.getDocumentNumber();
            DocumentRouteHeaderValue docHeader = getBusinessObjectService().findBySinglePrimaryKey(DocumentRouteHeaderValue.class, documentNumber);
            if (docHeader != null) {
                String docTypeName = docHeader.getDocumentType().getName();
                if (docTypeName.equals(KFSConstants.FinancialDocumentTypeCodes.GENERAL_ERROR_CORRECTION)) {
                    return documentNumber;
                }
            }
        }
        return KFSConstants.EMPTY_STRING;
    }

    private String generateEntryId() {
        StringBuilder retval = new StringBuilder();
        retval.append(getUniversityFiscalYear());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(getChartOfAccountsCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(getAccountNumber());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(getSubAccountNumber());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(getFinancialObjectCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(getFinancialSubObjectCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(getFinancialBalanceTypeCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(getFinancialObjectTypeCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(getUniversityFiscalPeriodCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(getFinancialDocumentTypeCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(getFinancialSystemOriginationCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(getDocumentNumber());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(getTransactionLedgerEntrySequenceNumber());
        return retval.toString();
    }

    /**
     * Generates a Map of fieldValues for a Primary Key lookup based on the given entryId.
     *
     * @param entryId
     * @return
     */
    public static Entry getEntry(String entryId) {
        String[] primaryKeyFields = entryId.split(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        String universityFiscalYear = primaryKeyFields[0];
        String chartOfAccountsCode = primaryKeyFields[1];
        String accountNumber = primaryKeyFields[2];
        String subAccountNumber = primaryKeyFields[3];
        String financialObjectCode = primaryKeyFields[4];
        String financialSubObjectCode = primaryKeyFields[5];
        String financialBalanceTypeCode = primaryKeyFields[6];
        String financialObjectTypeCode = primaryKeyFields[7];
        String universityFiscalPeriodCode = primaryKeyFields[8];
        String financialDocumentTypeCode = primaryKeyFields[9];
        String financialSystemOriginationCode = primaryKeyFields[10];
        String documentNumber = primaryKeyFields[11];
        String transactionLedgerEntrySequenceNumber = primaryKeyFields[12];

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, financialObjectCode);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, financialSubObjectCode);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, financialBalanceTypeCode);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, financialObjectTypeCode);
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, universityFiscalPeriodCode);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, financialDocumentTypeCode);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, financialSystemOriginationCode);
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        fieldValues.put(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER, transactionLedgerEntrySequenceNumber);

        Entry entry = getBusinessObjectService().findByPrimaryKey(Entry.class, fieldValues);
        return entry;
    }
}
