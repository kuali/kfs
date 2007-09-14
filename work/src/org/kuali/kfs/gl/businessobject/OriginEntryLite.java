/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.gl.bo;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.exception.LoadException;

public class OriginEntryLite extends PersistableBusinessObjectBase implements OriginEntryable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryLite.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    // 17 characters while it is 19 character in DD. Don't change, it has to be 17.
    public static final String SPACE_TRANSACTION_LEDGER_ENTRY_AMOUNT = "                 ";
    
    private Integer entryId;
    private Integer entryGroupId;
    protected String accountNumber;
    protected String documentNumber;
    protected String referenceFinancialDocumentNumber;
    protected String referenceFinancialDocumentTypeCode;
    protected Date financialDocumentReversalDate;
    protected String financialDocumentTypeCode;
    protected String financialBalanceTypeCode;
    protected String chartOfAccountsCode;
    protected String financialObjectTypeCode;
    protected String financialObjectCode;
    protected String financialSubObjectCode;
    protected String financialSystemOriginationCode;
    protected String referenceFinancialSystemOriginationCode;
    protected String organizationDocumentNumber;
    protected String organizationReferenceId;
    protected String projectCode;
    protected String subAccountNumber;
    protected Date transactionDate;
    protected String transactionDebitCreditCode;
    protected String transactionEncumbranceUpdateCode;
    protected Integer transactionLedgerEntrySequenceNumber;
    protected KualiDecimal transactionLedgerEntryAmount;
    protected String transactionLedgerEntryDescription;
    protected String universityFiscalPeriodCode;
    protected Integer universityFiscalYear;
    private boolean transactionScrubberOffsetGenerationIndicator;
    
    public OriginEntryLite(GeneralLedgerPendingEntry glpe) {
        accountNumber = glpe.getAccountNumber();
        documentNumber = glpe.getDocumentNumber();
        referenceFinancialDocumentNumber = glpe.getReferenceFinancialDocumentNumber();
        referenceFinancialDocumentTypeCode = glpe.getReferenceFinancialDocumentTypeCode();
        financialDocumentReversalDate = glpe.getFinancialDocumentReversalDate();
        financialDocumentTypeCode = glpe.getFinancialDocumentTypeCode();
        financialBalanceTypeCode = glpe.getFinancialBalanceTypeCode();
        chartOfAccountsCode = glpe.getChartOfAccountsCode();
        financialObjectTypeCode = glpe.getFinancialObjectTypeCode();
        financialObjectCode = glpe.getFinancialObjectCode();
        financialSubObjectCode = glpe.getFinancialSubObjectCode();
        financialSystemOriginationCode = glpe.getFinancialSystemOriginationCode();
        referenceFinancialSystemOriginationCode = glpe.getReferenceFinancialSystemOriginationCode();
        organizationDocumentNumber = glpe.getOrganizationDocumentNumber();
        organizationReferenceId = glpe.getOrganizationReferenceId();
        projectCode = glpe.getProjectCode();
        subAccountNumber = glpe.getSubAccountNumber();
        transactionDate = glpe.getTransactionDate();
        transactionDebitCreditCode = glpe.getTransactionDebitCreditCode();
        transactionEncumbranceUpdateCode = glpe.getTransactionEncumbranceUpdateCode();
        transactionLedgerEntrySequenceNumber = glpe.getTransactionLedgerEntrySequenceNumber();
        transactionLedgerEntryAmount = glpe.getTransactionLedgerEntryAmount();
        transactionLedgerEntryDescription = glpe.getTransactionLedgerEntryDescription();
        universityFiscalPeriodCode = glpe.getUniversityFiscalPeriodCode();
        universityFiscalYear = glpe.getUniversityFiscalYear();
    }

    /**
     * 
     */
    public OriginEntryLite(String financialDocumentTypeCode, String financialSystemOriginationCode) {
        super();

        setChartOfAccountsCode(KFSConstants.EMPTY_STRING);
        setAccountNumber(KFSConstants.EMPTY_STRING);
        setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        setProjectCode(KFSConstants.getDashProjectCode());

        setFinancialDocumentTypeCode(financialDocumentTypeCode);
        setFinancialSystemOriginationCode(financialSystemOriginationCode);

        setFinancialObjectCode(KFSConstants.EMPTY_STRING);
        setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        setFinancialBalanceTypeCode(KFSConstants.EMPTY_STRING);
        setFinancialObjectTypeCode(KFSConstants.EMPTY_STRING);
        setDocumentNumber(KFSConstants.EMPTY_STRING);
        setFinancialDocumentReversalDate(null);

        setUniversityFiscalYear(new Integer(0));
        setUniversityFiscalPeriodCode(KFSConstants.EMPTY_STRING);

        setTransactionLedgerEntrySequenceNumber(new Integer(1));
        setTransactionLedgerEntryAmount(new KualiDecimal(0));
        setTransactionLedgerEntryDescription(KFSConstants.EMPTY_STRING);
        setTransactionDate(null);
        setTransactionDebitCreditCode(KFSConstants.EMPTY_STRING);
        setTransactionEncumbranceUpdateCode(KFSConstants.EMPTY_STRING);

        setOrganizationDocumentNumber(KFSConstants.EMPTY_STRING);
        setOrganizationReferenceId(KFSConstants.EMPTY_STRING);

        setReferenceFinancialDocumentTypeCode(KFSConstants.EMPTY_STRING);
        setReferenceFinancialSystemOriginationCode(KFSConstants.EMPTY_STRING);
        setReferenceFinancialDocumentNumber(KFSConstants.EMPTY_STRING);
    }

    /**
     * 
     */
    public OriginEntryLite() {
        this(null, null);
    }

    public OriginEntryLite(Transaction t) {
        this();
        copyFieldsFromTransaction(t);
    }

    public OriginEntryLite(String line) {
        try {
            setFromTextFile(line, 0);
        }
        catch (LoadException e) {
            LOG.error("OriginEntry() Error loading line", e);
        }
    }

    public void copyFieldsFromTransaction(Transaction t) {
        setAccountNumber(t.getAccountNumber());
        setDocumentNumber(t.getDocumentNumber());
        setReferenceFinancialDocumentNumber(t.getReferenceFinancialDocumentNumber());
        setReferenceFinancialDocumentTypeCode(t.getReferenceFinancialDocumentTypeCode());
        setFinancialDocumentReversalDate(t.getFinancialDocumentReversalDate());
        setFinancialDocumentTypeCode(t.getFinancialDocumentTypeCode());
        setFinancialBalanceTypeCode(t.getFinancialBalanceTypeCode());
        setChartOfAccountsCode(t.getChartOfAccountsCode());
        setFinancialObjectTypeCode(t.getFinancialObjectTypeCode());
        setFinancialObjectCode(t.getFinancialObjectCode());
        setFinancialSubObjectCode(t.getFinancialSubObjectCode());
        setFinancialSystemOriginationCode(t.getFinancialSystemOriginationCode());
        setReferenceFinancialSystemOriginationCode(t.getReferenceFinancialSystemOriginationCode());
        setOrganizationDocumentNumber(t.getOrganizationDocumentNumber());
        setOrganizationReferenceId(t.getOrganizationReferenceId());
        setProjectCode(t.getProjectCode());
        setSubAccountNumber(t.getSubAccountNumber());
        setTransactionDate(t.getTransactionDate());
        setTransactionDebitCreditCode(t.getTransactionDebitCreditCode());
        setTransactionEncumbranceUpdateCode(t.getTransactionEncumbranceUpdateCode());
        setTransactionLedgerEntrySequenceNumber(t.getTransactionLedgerEntrySequenceNumber());
        setTransactionLedgerEntryAmount(t.getTransactionLedgerEntryAmount());
        setTransactionLedgerEntryDescription(t.getTransactionLedgerEntryDescription());
        setUniversityFiscalPeriodCode(t.getUniversityFiscalPeriodCode());
        setUniversityFiscalYear(t.getUniversityFiscalYear());
    }

    protected java.sql.Date parseDate(String sdate, boolean beLenientWithDates) throws ParseException {
        if ((sdate == null) || (sdate.trim().length() == 0)) {
            return null;
        }
        else {

            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(beLenientWithDates);

                java.util.Date d = sdf.parse(sdate);
                return new Date(d.getTime());
            }
            }

    protected String formatDate(Date date) {
        if (date == null) {
            return GLConstants.getSpaceTransactionDate();
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            return sdf.format(date);
        }
    }

    protected String getValue(String line, int s, int e) {
        //String v = line.substring(s, e);
        return StringUtils.substring(line, s, e).trim();
    }
    
    /**
     * This method loads the fields of this origin entry by parsing the passed in the string
     * It is assumed that the String does not contain the origin entry ID, but if it does, it 
     * will be ignored
     * 
     * @param line a string representing an origin entry
     * @param lineNumber used to render an error message by identifying this line
     * @throws LoadException
     */
    public void setFromTextFile(String line, int lineNumber) throws LoadException {

        // Just in case
        line = line + GLConstants.getSpaceAllOriginEntryFields();

        if (!GLConstants.getSpaceUniversityFiscalYear().equals(line.substring(0, 4))) {
            try {
                setUniversityFiscalYear(new Integer(line.substring(0, 4)));
            }
            catch (NumberFormatException e) {
                GlobalVariables.getErrorMap().putError("fileUpload", KFSKeyConstants.ERROR_NUMBER_FORMAT_ORIGIN_ENTRY_FROM_TEXT_FILE, new String[] { new Integer(lineNumber).toString(), "University Fiscal Year" });
                throw new LoadException("Invalid university fiscal year");
            }

        }
        else {
            setUniversityFiscalYear(null);
        }

        setChartOfAccountsCode(getValue(line, 4, 6));
        setAccountNumber(getValue(line, 6, 13));
        setSubAccountNumber(getValue(line, 13, 18));
        setFinancialObjectCode(getValue(line, 18, 22));
        setFinancialSubObjectCode(getValue(line, 22, 25));
        setFinancialBalanceTypeCode(getValue(line, 25, 27));
        setFinancialObjectTypeCode(getValue(line, 27, 29));
        setUniversityFiscalPeriodCode(getValue(line, 29, 31));
        setFinancialDocumentTypeCode(getValue(line, 31, 35));
        setFinancialSystemOriginationCode(getValue(line, 35, 37));
        setDocumentNumber(getValue(line, 37, 51));
        if (!GLConstants.getSpaceTransactionEntrySequenceNumber().equals(line.substring(51, 56)) && !GLConstants.getZeroTransactionEntrySequenceNumber().equals(line.substring(51, 56))) {
            try {
                setTransactionLedgerEntrySequenceNumber(new Integer(line.substring(51, 56).trim()));
        }
            catch (NumberFormatException e) {
                GlobalVariables.getErrorMap().putError("fileUpload", KFSKeyConstants.ERROR_NUMBER_FORMAT_ORIGIN_ENTRY_FROM_TEXT_FILE, new String[] { new Integer(lineNumber).toString(), "Sequence Number" });
                throw new LoadException("Invalid sequence number");
            }
        }
        else {
            setTransactionLedgerEntrySequenceNumber(null);
        }
        setTransactionLedgerEntryDescription(getValue(line, 56, 96));

        try {
            setTransactionLedgerEntryAmount(new KualiDecimal(line.substring(96, 113).trim()));
        }
        catch (NumberFormatException e) {
            GlobalVariables.getErrorMap().putError("fileUpload", KFSKeyConstants.ERROR_NUMBER_FORMAT_ORIGIN_ENTRY_FROM_TEXT_FILE, new String[] { new Integer(lineNumber).toString(), "Transaction Ledger Entry Amount" });
            throw new LoadException("Invalid Entry Amount");
        }

        setTransactionDebitCreditCode(line.substring(113, 114));

        try {
            setTransactionDate(parseDate(line.substring(114, 124), false));
        }
        catch (ParseException e) {
            GlobalVariables.getErrorMap().putError("fileUpload", KFSKeyConstants.ERROR_NUMBER_FORMAT_ORIGIN_ENTRY_FROM_TEXT_FILE, new String[] { new Integer(lineNumber).toString(), "Transaction Date" });
            throw new LoadException("Invalid Transaction Date");
        }

        setOrganizationDocumentNumber(getValue(line, 124, 134));
        setProjectCode(getValue(line, 134, 144));
        setOrganizationReferenceId(getValue(line, 144, 152));
        setReferenceFinancialDocumentTypeCode(getValue(line, 152, 156));
        setReferenceFinancialSystemOriginationCode(getValue(line, 156, 158));
        setReferenceFinancialDocumentNumber(getValue(line, 158, 172));
        try {
            setFinancialDocumentReversalDate(parseDate(line.substring(172, 182), true));
        }
        catch (ParseException e) {
            GlobalVariables.getErrorMap().putError("fileUpload", KFSKeyConstants.ERROR_NUMBER_FORMAT_ORIGIN_ENTRY_FROM_TEXT_FILE, new String[] { new Integer(lineNumber).toString(), "Financial Document Reversal Date" });
            throw new LoadException("Invalid Reversal Date");
        }

        setTransactionEncumbranceUpdateCode(line.substring(182, 183));
    }

    protected String getField(int size, String value) {
        if (value == null) {
            return GLConstants.getSpaceAllOriginEntryFields().substring(0, size);
        }
        else {
            if (value.length() < size) {
                return value + GLConstants.getSpaceAllOriginEntryFields().substring(0, size - value.length());
            }
            else {
                return value;
            }
        }
    }

    public String getLine() {
        StringBuffer sb = new StringBuffer();
        if (universityFiscalYear == null) {
            sb.append(GLConstants.getSpaceUniversityFiscalYear());
        }
        else {
            sb.append(universityFiscalYear);
        }

        sb.append(getField(2, chartOfAccountsCode));
        sb.append(getField(7, accountNumber));
        sb.append(getField(5, subAccountNumber));
        sb.append(getField(4, financialObjectCode));
        sb.append(getField(3, financialSubObjectCode));
        sb.append(getField(2, financialBalanceTypeCode));
        sb.append(getField(2, financialObjectTypeCode));
        sb.append(getField(2, universityFiscalPeriodCode));
        sb.append(getField(4, financialDocumentTypeCode));
        sb.append(getField(2, financialSystemOriginationCode));
        sb.append(getField(14, documentNumber));

        // This is the cobol code for transaction sequence numbers.
        // 3025 019280 IF TRN-ENTR-SEQ-NBR OF GLEN-RECORD NOT NUMERIC
        // 3026 019290 MOVE ZEROES TO TRN-ENTR-SEQ-NBR OF GLEN-RECORD
        // 3027 019300 END-IF
        // 3028 019310 IF TRN-ENTR-SEQ-NBR OF GLEN-RECORD = SPACES
        // 3029 019320 MOVE ZEROES
        // 3030 019330 TO TRN-ENTR-SEQ-NBR OF ALT-GLEN-RECORD
        // 3031 019340 ELSE
        // 3032 019350 MOVE TRN-ENTR-SEQ-NBR OF GLEN-RECORD
        // 3033 019360 TO TRN-ENTR-SEQ-NBR OF ALT-GLEN-RECORD
        // 3034 019370 END-IF

        if (transactionLedgerEntrySequenceNumber == null) {
            sb.append("00000");
        }
        else {
            // Format to a length of 5
            String seqNum = transactionLedgerEntrySequenceNumber.toString();
            while (5 > seqNum.length()) {
                seqNum = "0" + seqNum;
            }
            sb.append(seqNum);
        }
        sb.append(getField(40, transactionLedgerEntryDescription));
        if (transactionLedgerEntryAmount == null) {
            sb.append(SPACE_TRANSACTION_LEDGER_ENTRY_AMOUNT);
        }
        else {
            String a = transactionLedgerEntryAmount.toString();
            sb.append(SPACE_TRANSACTION_LEDGER_ENTRY_AMOUNT.substring(0, 17 - a.length()));
            sb.append(a);
        }
        sb.append(getField(1, transactionDebitCreditCode));
        sb.append(formatDate(transactionDate));
        sb.append(getField(10, organizationDocumentNumber));
        sb.append(getField(10, projectCode));
        sb.append(getField(8, organizationReferenceId));
        sb.append(getField(4, referenceFinancialDocumentTypeCode));
        sb.append(getField(2, referenceFinancialSystemOriginationCode));
        sb.append(getField(14, referenceFinancialDocumentNumber));
        sb.append(formatDate(financialDocumentReversalDate));
        sb.append(getField(1, transactionEncumbranceUpdateCode));
        // pad to full length of 173 chars.
        while (173 > sb.toString().length()) {
            sb.append(' ');
        }
        return sb.toString();
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("entryId", entryId);
        map.put("entryGroupId", entryGroupId);
        map.put("universityFiscalYear", universityFiscalYear);
        map.put("universityFiscalPeriodCode", universityFiscalPeriodCode);
        map.put("chartOfAccountsCode", chartOfAccountsCode);
        map.put("accountNumber", accountNumber);
        map.put("subAccountNumber", subAccountNumber);
        map.put("financialObjectCode", financialObjectCode);
        map.put("financialObjectTypeCode", financialObjectTypeCode);
        map.put("financialSubObjectCode", financialSubObjectCode);
        map.put("financialBalanceTypeCode", financialBalanceTypeCode);
        map.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        map.put("financialDocumentTypeCode", financialDocumentTypeCode);
        map.put("financialSystemOriginationCode", financialSystemOriginationCode);
        map.put("transactionLedgerEntrySequenceNumber", transactionLedgerEntrySequenceNumber);
        map.put("transactionLedgerEntryDescription", transactionLedgerEntryDescription);
        return map;
    }

    public boolean isTransactionScrubberOffsetGenerationIndicator() {
        return transactionScrubberOffsetGenerationIndicator;
    }

    public void setTransactionScrubberOffsetGenerationIndicator(boolean transactionScrubberOffsetGenerationIndicator) {
        this.transactionScrubberOffsetGenerationIndicator = transactionScrubberOffsetGenerationIndicator;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getTransactionDebitCreditCode() {
        return transactionDebitCreditCode;
    }

    public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
        this.transactionDebitCreditCode = transactionDebitCreditCode;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Date getFinancialDocumentReversalDate() {
        return financialDocumentReversalDate;
    }

    public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
        this.financialDocumentReversalDate = financialDocumentReversalDate;
    }

    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    public String getTransactionEncumbranceUpdateCode() {
        return transactionEncumbranceUpdateCode;
    }

    public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode) {
        this.transactionEncumbranceUpdateCode = transactionEncumbranceUpdateCode;
    }

    public Integer getEntryGroupId() {
        return entryGroupId;
    }

    public void setEntryGroupId(Integer entryGroupId) {
        this.entryGroupId = entryGroupId;
    }

    public Integer getEntryId() {
        return entryId;
    }

    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }
    
    public void resetEntryId() {
        this.entryId = null;
        this.versionNumber = null;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    public String getOrganizationDocumentNumber() {
        return organizationDocumentNumber;
    }

    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }

    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
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

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Integer getTransactionLedgerEntrySequenceNumber() {
        return transactionLedgerEntrySequenceNumber;
    }

    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
        this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
    }

    public KualiDecimal getTransactionLedgerEntryAmount() {
        return transactionLedgerEntryAmount;
    }

    public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount) {
        this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
    }

    public void setTransactionLedgerEntryAmount(String transactionLedgerEntryAmount) {
        this.transactionLedgerEntryAmount = new KualiDecimal(transactionLedgerEntryAmount);
    }
    
    public void clearTransactionLedgerEntryAmount(){
        this.transactionLedgerEntryAmount = null;
    }
    
    public String getTransactionLedgerEntryDescription() {
        return transactionLedgerEntryDescription;
    }

    public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
        this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
    }

    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }
    
    public boolean isDebit() {
        return KFSConstants.GL_DEBIT_CODE.equals(this.transactionDebitCreditCode);
    }

    public boolean isCredit() {
        return KFSConstants.GL_CREDIT_CODE.equals(this.transactionDebitCreditCode);
    }

    public void setFieldValue(String fieldName,String fieldValue) {
        if ( "universityFiscalYear".equals(fieldName) ) {
            if ( StringUtils.isNotBlank(fieldValue) ) {
                setUniversityFiscalYear(Integer.parseInt(fieldValue));
            } else {
                setUniversityFiscalYear(null);
            }
        } else if ( "chartOfAccountsCode".equals(fieldName) ) {
            setChartOfAccountsCode(fieldValue);
        } else if ( "accountNumber".equals(fieldName) ) {
            setAccountNumber(fieldValue);
        } else if ( "subAccountNumber".equals(fieldName) ) {
            setSubAccountNumber(fieldValue);
        } else if ( "financialObjectCode".equals(fieldName) ) {
            setFinancialObjectCode(fieldValue);
        } else if ( "financialSubObjectCode".equals(fieldName) ) {
            setFinancialSubObjectCode(fieldValue);
        } else if ( "financialBalanceTypeCode".equals(fieldName) ) {
            setFinancialBalanceTypeCode(fieldValue);
        } else if ( "financialObjectTypeCode".equals(fieldName) ) {
            setFinancialObjectTypeCode(fieldValue);
        } else if ( "universityFiscalPeriodCode".equals(fieldName) ) {
            setUniversityFiscalPeriodCode(fieldValue);
        } else if ( "financialDocumentTypeCode".equals(fieldName) ) {
            setFinancialDocumentTypeCode(fieldValue);
        } else if ( "financialSystemOriginationCode".equals(fieldName) ) {
            setFinancialSystemOriginationCode(fieldValue);
        } else if ( KFSPropertyConstants.DOCUMENT_NUMBER.equals(fieldName) ) {
            setDocumentNumber(fieldValue);
        } else if ( "transactionLedgerEntrySequenceNumber".equals(fieldName) ) {
            if ( StringUtils.isNotBlank(fieldValue) ) {
                setTransactionLedgerEntrySequenceNumber(Integer.parseInt(fieldValue));
            } else {
                setTransactionLedgerEntrySequenceNumber(null);
            }
        } else if ( "transactionLedgerEntryDescription".equals(fieldName) ) {
            setTransactionLedgerEntryDescription(fieldValue);
        } else if ( "transactionLedgerEntryAmount".equals(fieldName) ) {
            if ( StringUtils.isNotBlank(fieldValue) ) {
                setTransactionLedgerEntryAmount(new KualiDecimal(fieldValue));
            } else {
                clearTransactionLedgerEntryAmount();
            }
        } else if ( "transactionDebitCreditCode".equals(fieldName) ) {
            setTransactionDebitCreditCode(fieldValue);
        } else if ( "transactionDate".equals(fieldName) ) {
            if ( StringUtils.isNotBlank(fieldValue) ) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    setTransactionDate(new java.sql.Date( (df.parse(fieldValue)).getTime() ) );
                } catch (ParseException e) {
                    setTransactionDate(null);
                }
            } else {
                setTransactionDate(null);
            }
        } else if ( "organizationDocumentNumber".equals(fieldName) ) {
            setOrganizationDocumentNumber(fieldValue);
        } else if ( "projectCode".equals(fieldName) ) {
            setProjectCode(fieldValue);
        } else if ( "organizationReferenceId".equals(fieldName) ) {
            setOrganizationReferenceId(fieldValue);
        } else if ( "referenceFinancialDocumentTypeCode".equals(fieldName) ) {
            setReferenceFinancialDocumentTypeCode(fieldValue);
        } else if ( "referenceFinancialSystemOriginationCode".equals(fieldName) ) {
            setReferenceFinancialSystemOriginationCode(fieldValue);
        } else if ( "referenceFinancialDocumentNumber".equals(fieldName) ) {
            setReferenceFinancialDocumentNumber(fieldValue);
        } else if ( "financialDocumentReversalDate".equals(fieldName) ) {
            if ( StringUtils.isNotBlank(fieldValue) ) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    setFinancialDocumentReversalDate(new java.sql.Date( (df.parse(fieldValue)).getTime() ) );
                } catch (ParseException e) {
                    setFinancialDocumentReversalDate(null);
                }
            } else {
                setFinancialDocumentReversalDate(null);
            }
        } else if ( "transactionEncumbranceUpdateCode".equals(fieldName) ) {
            setTransactionEncumbranceUpdateCode(fieldValue);
        } else {
            throw new IllegalArgumentException("Invalid Field Name " + fieldName);
        }        
    }

    public Object getFieldValue(String fieldName) {
        if ( "universityFiscalYear".equals(fieldName) ) {
            return getUniversityFiscalYear();
        } else if ( "chartOfAccountsCode".equals(fieldName) ) {
            return getChartOfAccountsCode();
        } else if ( "accountNumber".equals(fieldName) ) {
            return getAccountNumber();
        } else if ( "subAccountNumber".equals(fieldName) ) {
            return getSubAccountNumber();
        } else if ( "financialObjectCode".equals(fieldName) ) {
            return getFinancialObjectCode();
        } else if ( "financialSubObjectCode".equals(fieldName) ) {
            return getFinancialSubObjectCode();
        } else if ( "financialBalanceTypeCode".equals(fieldName) ) {
            return getFinancialBalanceTypeCode();
        } else if ( "financialObjectTypeCode".equals(fieldName) ) {
            return getFinancialObjectTypeCode();
        } else if ( "universityFiscalPeriodCode".equals(fieldName) ) {
            return getUniversityFiscalPeriodCode();
        } else if ( "financialDocumentTypeCode".equals(fieldName) ) {
            return getFinancialDocumentTypeCode();
        } else if ( "financialSystemOriginationCode".equals(fieldName) ) {
            return getFinancialSystemOriginationCode();
        } else if ( KFSPropertyConstants.DOCUMENT_NUMBER.equals(fieldName) ) {
            return getDocumentNumber();
        } else if ( "transactionLedgerEntrySequenceNumber".equals(fieldName) ) {
            return getTransactionLedgerEntrySequenceNumber();
        } else if ( "transactionLedgerEntryDescription".equals(fieldName) ) {
            return getTransactionLedgerEntryDescription();
        } else if ( "transactionLedgerEntryAmount".equals(fieldName) ) {
            return getTransactionLedgerEntryAmount();
        } else if ( "transactionDebitCreditCode".equals(fieldName) ) {
            return getTransactionDebitCreditCode();
        } else if ( "transactionDate".equals(fieldName) ) {
            return getTransactionDate();
        } else if ( "organizationDocumentNumber".equals(fieldName) ) {
            return getOrganizationDocumentNumber();
        } else if ( "projectCode".equals(fieldName) ) {
            return getProjectCode();
        } else if ( "organizationReferenceId".equals(fieldName) ) {
            return getOrganizationReferenceId();
        } else if ( "referenceFinancialDocumentTypeCode".equals(fieldName) ) {
            return getReferenceFinancialDocumentTypeCode();
        } else if ( "referenceFinancialSystemOriginationCode".equals(fieldName) ) {
            return getReferenceFinancialSystemOriginationCode();
        } else if ( "referenceFinancialDocumentNumber".equals(fieldName) ) {
            return getReferenceFinancialDocumentNumber();
        } else if ( "financialDocumentReversalDate".equals(fieldName) ) {
            return getFinancialDocumentReversalDate();
        } else if ( "transactionEncumbranceUpdateCode".equals(fieldName) ) {
            return getTransactionEncumbranceUpdateCode();        
        } else {
            throw new IllegalArgumentException("Invalid Field Name " + fieldName);
        }
    }
}
