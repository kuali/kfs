package edu.arizona.kfs.gl.businessobject.lookup;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import edu.arizona.kfs.gl.businessobject.GecEntry;
import edu.arizona.kfs.gl.businessobject.lite.AccountingLineLiteBo;
import edu.arizona.kfs.gl.businessobject.lite.DocumentHeaderLiteBo;
import edu.arizona.kfs.gl.businessobject.lite.DocumentTypeLiteBo;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;

@SuppressWarnings("deprecation")
public class GecEntryHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GecEntryHelperServiceImpl.class);

    private static transient volatile BusinessObjectService businessObjectService;

    private static BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    public static String findGecDocumentNumber(String documentNumber) {
        LOG.debug("Searching for GEC Document Number of " + documentNumber);
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.REFERENCE_NUMBER, documentNumber);
        Collection<AccountingLineLiteBo> accountingLineList = getBusinessObjectService().findMatching(AccountingLineLiteBo.class, fieldValues);

        LOG.debug("Checking each accounting line that uses " + documentNumber + " as the reference Number.");
        for (AccountingLineLiteBo accountingLine : accountingLineList) {
            String gecDocumentNumber = accountingLine.getDocumentNumber();
            LOG.debug("Checking to see if " + gecDocumentNumber + " is a GEC.");
            DocumentHeaderLiteBo docHeader = getBusinessObjectService().findBySinglePrimaryKey(DocumentHeaderLiteBo.class, gecDocumentNumber);
            if (docHeader != null) {
                String docTypeId = docHeader.getDocumentTypeId();
                DocumentTypeLiteBo docType = getBusinessObjectService().findBySinglePrimaryKey(DocumentTypeLiteBo.class, docTypeId);
                String documentTypeName = docType.getName();
                if (documentTypeName.equals(KFSConstants.FinancialDocumentTypeCodes.GENERAL_ERROR_CORRECTION)) {
                    LOG.debug("GEC Document Number " + gecDocumentNumber + " found for Document Number " + documentNumber + ".");
                    return gecDocumentNumber;
                }
            }
        }
        LOG.debug("No GEC Document Number found for Document Number " + documentNumber + ".");
        return KFSConstants.EMPTY_STRING;
    }

    public static String generateObjectId(GecEntry entry) {
        StringBuilder retval = new StringBuilder();
        retval.append(KFSConstants.ENTRY_IDENTIFIER);
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(entry.getUniversityFiscalYear());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(entry.getChartOfAccountsCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(entry.getAccountNumber());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(entry.getSubAccountNumber());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(entry.getFinancialObjectCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(entry.getFinancialSubObjectCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(entry.getFinancialBalanceTypeCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(entry.getFinancialObjectTypeCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(entry.getUniversityFiscalPeriodCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(entry.getFinancialDocumentTypeCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(entry.getFinancialSystemOriginationCode());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(entry.getDocumentNumber());
        retval.append(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        retval.append(entry.getTransactionLedgerEntrySequenceNumber());
        return retval.toString();
    }

    /**
     * retrieves the Entry corresponding to the given objectId.
     *
     * @param objectId
     * @return
     */
    public static Entry getEntry(String objectId) {
        String[] primaryKeyFields = objectId.split(SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER);
        String universityFiscalYear = primaryKeyFields[1];
        String chartOfAccountsCode = primaryKeyFields[2];
        String accountNumber = primaryKeyFields[3];
        String subAccountNumber = primaryKeyFields[4];
        String financialObjectCode = primaryKeyFields[5];
        String financialSubObjectCode = primaryKeyFields[6];
        String financialBalanceTypeCode = primaryKeyFields[7];
        String financialObjectTypeCode = primaryKeyFields[8];
        String universityFiscalPeriodCode = primaryKeyFields[9];
        String financialDocumentTypeCode = primaryKeyFields[10];
        String financialSystemOriginationCode = primaryKeyFields[11];
        String documentNumber = primaryKeyFields[12];
        String transactionLedgerEntrySequenceNumber = primaryKeyFields[13];

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

    public static void addInquiryLinksToRecords(List<ResultRow> resultTable) {
        for (ResultRow row : resultTable) {
            setFieldInquiryURL(row, KFSPropertyConstants.GEC_DOCUMENT_NUMBER);
        }
    }

    public static boolean compareGecEntryToRow(GecEntry entry, ResultRow row) {
        List<Column> columnList = row.getColumns();
        boolean isSameEntry = true;

        isSameEntry &= isPropertyEqual(entry.getUniversityFiscalYear().toString(), KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, columnList);
        isSameEntry &= isPropertyEqual(entry.getChartOfAccountsCode(), KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, columnList);
        isSameEntry &= isPropertyEqual(entry.getAccountNumber(), KFSPropertyConstants.ACCOUNT_NUMBER, columnList);
        isSameEntry &= isPropertyEqual(entry.getSubAccountNumber(), KFSPropertyConstants.SUB_ACCOUNT_NUMBER, columnList);
        isSameEntry &= isPropertyEqual(entry.getFinancialObjectCode(), KFSPropertyConstants.FINANCIAL_OBJECT_CODE, columnList);
        isSameEntry &= isPropertyEqual(entry.getFinancialSubObjectCode(), KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, columnList);
        isSameEntry &= isPropertyEqual(entry.getFinancialBalanceTypeCode(), KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, columnList);
        isSameEntry &= isPropertyEqual(entry.getFinancialObjectTypeCode(), KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, columnList);
        isSameEntry &= isPropertyEqual(entry.getUniversityFiscalPeriodCode(), KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, columnList);
        isSameEntry &= isPropertyEqual(entry.getFinancialDocumentTypeCode(), KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, columnList);
        isSameEntry &= isPropertyEqual(entry.getFinancialSystemOriginationCode(), KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, columnList);
        isSameEntry &= isPropertyEqual(entry.getDocumentNumber(), KFSPropertyConstants.DOCUMENT_NUMBER, columnList);
        isSameEntry &= isPropertyEqual(entry.getTransactionLedgerEntrySequenceNumber().toString(), KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER, columnList);

        return isSameEntry;
    }

    public static void setFieldValue(ResultRow row, String field, String value) {
        int col = getColumnIndexByProperty(row.getColumns(), field);
        if (col != -1) {
            Column column = row.getColumns().get(col);
            if (value != null) {
                column.setPropertyValue(value);
            }
        }
    }

    private static void setFieldInquiryURL(ResultRow row, String field) {
        List<Column> columnList = row.getColumns();
        int col = getColumnIndexByProperty(columnList, field);
        if (col != -1) {
            Column column = columnList.get(col);
            if (StringUtils.isNotBlank(column.getPropertyValue())) {
                Map<String, String> keys = getPrimaryKeysForInquiryURL(row, field);
                if (keys.size() != 0) {
                    String title = generateURLTitle(row, column, keys);
                    String inquiryURL = generateInquiryURL(field, keys);

                    HtmlData columnAnchor = new HtmlData.AnchorHtmlData(inquiryURL, title);
                    column.setColumnAnchor(columnAnchor);
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static Class getClassForField(String field) {
        switch (field) {
            case KFSPropertyConstants.GEC_DOCUMENT_NUMBER:
            case KFSPropertyConstants.DOCUMENT_NUMBER:
                return null;
            case KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR:
                return org.kuali.kfs.sys.businessobject.SystemOptions.class;
            case KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE:
                return org.kuali.kfs.coa.businessobject.Chart.class;
            case KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE:
            case KFSPropertyConstants.REFERENCE_FINANCIAL_SYSTEM_ORIGINATION_CODE:
                return org.kuali.kfs.sys.businessobject.OriginationCode.class;
            case KFSPropertyConstants.ACCOUNT_NUMBER:
                return org.kuali.kfs.coa.businessobject.Account.class;
            case KFSPropertyConstants.SUB_ACCOUNT_NUMBER:
                return org.kuali.kfs.coa.businessobject.SubAccount.class;
            case KFSPropertyConstants.FINANCIAL_OBJECT_CODE:
                return org.kuali.kfs.coa.businessobject.ObjectCode.class;
            case KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE:
                return org.kuali.kfs.coa.businessobject.SubObjectCode.class;
            case KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE:
                return org.kuali.kfs.coa.businessobject.BalanceType.class;
            case KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE:
                return org.kuali.kfs.coa.businessobject.ObjectType.class;
            case KFSPropertyConstants.PROJECT_CODE:
                return org.kuali.kfs.coa.businessobject.ProjectCode.class;
            case KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE:
                return org.kuali.kfs.coa.businessobject.AccountingPeriod.class;
            case KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE:
            case KFSPropertyConstants.REFERENCE_FINANCIAL_DOCUMENT_TYPE_CODE:
                return org.kuali.rice.kew.doctype.bo.DocumentTypeEBO.class;
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private static String generateURLTitle(ResultRow row, Column column, Map<String, String> parameters) {
        if (parameters.size() == 0) {
            return KFSConstants.EMPTY_STRING;
        }
        Class clazz = getClassForField(column.getPropertyName());
        if (clazz == null) {
            return KFSConstants.EMPTY_STRING;
        }
        String className = clazz.getSimpleName();
        if (className.equals(org.kuali.rice.kew.doctype.bo.DocumentTypeEBO.class.getSimpleName())) {
            className = Document.class.getSimpleName();
        }

        StringBuilder retval = new StringBuilder();
        retval.append("show Inquiry for ");
        retval.append(className);
        retval.append(": ");
        int startingLength = retval.length();
        for (String propertyName : parameters.keySet()) {
            if (retval.length() > startingLength) {
                retval.append(", ");
            }
            int col = getColumnIndexByProperty(row.getColumns(), propertyName);
            String propertyTitle = column.getColumnTitle();

            if (col != -1) {
                propertyTitle = row.getColumns().get(col).getColumnTitle();
            } else {
                if (className.equals(Document.class.getSimpleName())) {
                    propertyTitle = propertyName;
                    LOG.debug("pause here.");
                }
            }

            String propertyValue = parameters.get(propertyName);
            retval.append(propertyTitle);
            retval.append("=");
            retval.append(propertyValue);

        }
        return retval.toString();
    }

    /**
     * This method gets the index of the column with the desired propertyName.
     * 
     * @param columnList
     * @param propertyName
     * @return The index of the column desired, -1 if there is no column with the necessary propretyName
     */
    private static int getColumnIndexByProperty(List<Column> columnList, String propertyName) {
        for (int i = 0; i < columnList.size(); i++) {
            if (StringUtils.equals(propertyName, columnList.get(i).getPropertyName())) {
                return i;
            }
        }
        return -1;// not in the list
    }

    private static boolean isPropertyEqual(String entryPropertyValue, String propertyName, List<Column> columnList) {
        int col = getColumnIndexByProperty(columnList, propertyName);
        if (col == -1) {
            return true;
        }
        String columnValue = columnList.get(col).getPropertyValue();
        boolean isEqual = StringUtils.equals(entryPropertyValue, columnValue);
        return isEqual;
    }

    private static Map<String, String> getPrimaryKeysForInquiryURL(ResultRow row, String field) {
        Map<String, String> retval = new HashMap<String, String>();
        int colField = getColumnIndexByProperty(row.getColumns(), field);
        Column column = row.getColumns().get(colField);

        switch (field) {
            case KFSPropertyConstants.GEC_DOCUMENT_NUMBER:
            case KFSPropertyConstants.DOCUMENT_NUMBER:
                // Class: DocHandler
                retval.put(KFSPropertyConstants.DOCUMENT_NUMBER, column.getPropertyValue());
                return retval;

            case KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR:
            case KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE:
            case KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE:
                retval.put(field, column.getPropertyValue());
                return retval;

            case KFSPropertyConstants.REFERENCE_FINANCIAL_SYSTEM_ORIGINATION_CODE:
                retval.put(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, column.getPropertyValue());
                return retval;

            case KFSPropertyConstants.ACCOUNT_NUMBER:
                retval.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getFieldValue(row, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
                retval.put(field, column.getPropertyValue());
                return retval;

            case KFSPropertyConstants.SUB_ACCOUNT_NUMBER:
                if (StringUtils.equals(column.getPropertyValue(), PdpPropertyConstants.CustomerProfile.CUSTOMER_DEFAULT_SUB_ACCOUNT_NUMBER)) {
                    return retval;
                }
                retval.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getFieldValue(row, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
                retval.put(KFSPropertyConstants.ACCOUNT_NUMBER, getFieldValue(row, KFSPropertyConstants.ACCOUNT_NUMBER));
                retval.put(field, column.getPropertyValue());
                return retval;

            case KFSPropertyConstants.FINANCIAL_OBJECT_CODE:
                retval.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, getFieldValue(row, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR));
                retval.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getFieldValue(row, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
                retval.put(field, column.getPropertyValue());
                return retval;

            case KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE:
                if (StringUtils.equalsIgnoreCase(PdpPropertyConstants.CustomerProfile.CUSTOMER_DEFAULT_SUB_OBJECT_CODE, column.getPropertyValue())) {
                    return retval;
                }
                retval.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, getFieldValue(row, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR));
                retval.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getFieldValue(row, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
                retval.put(KFSPropertyConstants.ACCOUNT_NUMBER, getFieldValue(row, KFSPropertyConstants.ACCOUNT_NUMBER));
                retval.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, getFieldValue(row, KFSPropertyConstants.FINANCIAL_OBJECT_CODE));
                retval.put(field, column.getPropertyValue());
                return retval;

            case KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE:
            case KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE:
            case KFSPropertyConstants.PROJECT_CODE:
                if (StringUtils.equalsIgnoreCase("----------", column.getPropertyValue())) {
                    return retval;
                }
                retval.put(KFSPropertyConstants.CODE, column.getPropertyValue());
                return retval;

            case KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE:
                retval.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, getFieldValue(row, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR));
                retval.put(field, column.getPropertyValue());
                return retval;

            case KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE:
            case KFSPropertyConstants.REFERENCE_FINANCIAL_DOCUMENT_TYPE_CODE:
                DocumentTypeService documentTypeService = SpringContext.getBean(DocumentTypeService.class);
                String docTypeId = documentTypeService.getIdByName(column.getPropertyValue());
                retval.put(KFSPropertyConstants.DOCUMENT_TYPE_ID, docTypeId);
                retval.put(KFSConstants.DOC_FORM_KEY, KFSConstants.DOC_FORM_KEY_VALUE_88888888);
                return retval;

        }
        LOG.debug("Error: Unknown field.");
        return retval;
    }

    private static String getInquiryUrl() {
        String inquiryUrl = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
        String potentialUrlAddition = "kr/";
        if (!inquiryUrl.endsWith("/")) {
            inquiryUrl = inquiryUrl + "/";
        }
        return inquiryUrl + potentialUrlAddition + KRADConstants.INQUIRY_ACTION;
    }

    private static String getFieldValue(ResultRow row, String field) {
        int col = getColumnIndexByProperty(row.getColumns(), field);
        Column column = row.getColumns().get(col);
        String retval = column.getPropertyValue();
        return retval;
    }

    @SuppressWarnings("rawtypes")
    private static String generateInquiryURL(String field, Map<String, String> keys) {
        String url = KFSConstants.EMPTY_STRING;
        Class clazz = getClassForField(field);
        switch (field) {
            case KFSPropertyConstants.GEC_DOCUMENT_NUMBER:
            case KFSPropertyConstants.DOCUMENT_NUMBER:
                url = KewApiConstants.Namespaces.MODULE_NAME + KRADConstants.DOCHANDLER_DO_URL + keys.get(KFSPropertyConstants.DOCUMENT_NUMBER) + KRADConstants.DOCHANDLER_URL_CHUNK;
        }

        if (clazz != null && url.equals(KFSConstants.EMPTY_STRING)) {
            url = UrlFactory.parameterizeUrl(getInquiryUrl(), getUrlParameters(clazz, keys));
        }

        return url;
    }

    @SuppressWarnings("rawtypes")
    private static Properties getUrlParameters(Class clazz, Map<String, String> parameters) {
        Properties urlParameters = new Properties();
        urlParameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, clazz.getCanonicalName());
        for (String paramName : parameters.keySet()) {
            String parameterValue = parameters.get(paramName);
            urlParameters.put(paramName, parameterValue);
        }
        urlParameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.START_METHOD);
        return urlParameters;
    }

}
