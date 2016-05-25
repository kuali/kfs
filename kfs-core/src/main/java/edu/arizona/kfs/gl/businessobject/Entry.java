package edu.arizona.kfs.gl.businessobject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.sys.KFSPropertyConstants;

/**
 * This class is the UA Modification to the Entry BO class.
 *
 * @author Adam Kost <kosta@email.arizona.edu> with some code adapted from UCI
 */

@SuppressWarnings("deprecation")
public class Entry extends org.kuali.kfs.gl.businessobject.Entry {

    private static final long serialVersionUID = 7915615641174612210L;

    private transient String gecDocumentNumber;
    private transient String entryId;

    private static transient volatile BusinessObjectService businessObjectService;

    private static BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
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
