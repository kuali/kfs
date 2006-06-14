/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.bo;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.kuali.Constants;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.OriginationCode;
import org.kuali.core.bo.user.Options;
import org.kuali.core.document.DocumentType;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.springframework.util.StringUtils;

/**
 * @author Kuali General Ledger Team (kualigltech@oncourse.iu.edu)
 * @version $Id: OriginEntry.java,v 1.29 2006-06-14 12:26:43 abyrne Exp $
 */
public class OriginEntry extends BusinessObjectBase implements Transaction {
    static final long serialVersionUID = -2498312988235747448L;

    private Integer entryId;
    private Integer entryGroupId;
    private String accountNumber;
    private String financialDocumentNumber;
    private String referenceFinancialDocumentNumber;
    private String referenceFinancialDocumentTypeCode;
    private Date financialDocumentReversalDate;
    private String financialDocumentTypeCode;
    private String financialBalanceTypeCode;
    private String chartOfAccountsCode;
    private String financialObjectTypeCode;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialSystemOriginationCode;
    private String referenceFinancialSystemOriginationCode;
    private String organizationDocumentNumber;
    private String organizationReferenceId;
    private String projectCode;
    private String subAccountNumber;
    private Date transactionDate;
    private String transactionDebitCreditCode;
    private String transactionEncumbranceUpdateCode;
    private Integer transactionLedgerEntrySequenceNumber;
    private KualiDecimal transactionLedgerEntryAmount;
    private String transactionLedgerEntryDescription;
    private String universityFiscalPeriodCode;
    private Integer universityFiscalYear;
    private boolean transactionScrubberOffsetGenerationIndicator;
    private String budgetYearFundingSourceCode;
    private Integer budgetYear;

    // bo references
    private OriginEntryGroup group;
    private Account account;
    private SubAccount subAccount;
    private A21SubAccount a21SubAccount;
    private BalanceTyp balanceType;
    private Chart chart;
    private ObjectCode financialObject;
    private SubObjCd financialSubObject;
    private ObjectType objectType;
    private ProjectCode project;
    private DocumentType documentType;
    private UniversityDate universityDate;
    private Options option;
    private AccountingPeriod accountingPeriod;
    private UniversityDate reversalDate;
    private OriginationCode origination;
    private DocumentType referenceDocumentType;

    /**
     * 
     */
    public OriginEntry(String financialDocumentTypeCode, String financialSystemOriginationCode) {
        super();

        setChartOfAccountsCode(Constants.EMPTY_STRING);
        setAccountNumber(Constants.EMPTY_STRING);
        setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        setProjectCode(Constants.DASHES_PROJECT_CODE);

        setFinancialDocumentTypeCode(financialDocumentTypeCode);
        setFinancialSystemOriginationCode(financialSystemOriginationCode);

        setFinancialObjectCode(Constants.EMPTY_STRING);
        setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        setFinancialBalanceTypeCode(Constants.EMPTY_STRING);
        setFinancialObjectTypeCode(Constants.EMPTY_STRING);
        setFinancialDocumentNumber(Constants.EMPTY_STRING);
        setFinancialDocumentReversalDate(null);

        setUniversityFiscalYear(new Integer(0));
        setUniversityFiscalPeriodCode(Constants.EMPTY_STRING);

        setTransactionLedgerEntrySequenceNumber(new Integer(1));
        setTransactionLedgerEntryAmount(new KualiDecimal(0));
        setTransactionLedgerEntryDescription(Constants.EMPTY_STRING);
        setTransactionDate(null);
        setTransactionDebitCreditCode(Constants.EMPTY_STRING);
        setTransactionEncumbranceUpdateCode(Constants.EMPTY_STRING);

        setOrganizationDocumentNumber(Constants.EMPTY_STRING);
        setOrganizationReferenceId(Constants.EMPTY_STRING);

        setReferenceFinancialDocumentTypeCode(Constants.EMPTY_STRING);
        setReferenceFinancialSystemOriginationCode(Constants.EMPTY_STRING);
        setReferenceFinancialDocumentNumber(Constants.EMPTY_STRING);
    }

    /**
     * 
     */
    public OriginEntry() {
        this(null, null);
    }

    public OriginEntry(Transaction t) {
        this();
        setTransaction(t);
    }

    public OriginEntry(String line) {
        setFromTextFile(line);
    }

    public void setTransaction(Transaction t) {
        setAccountNumber(t.getAccountNumber());
        setFinancialDocumentNumber(t.getFinancialDocumentNumber());
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

    private java.sql.Date parseDate(String sdate, boolean beLenientWithDates) {
        if ((sdate == null) || (sdate.trim().length() == 0)) {
            return null;
        }
        else {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(beLenientWithDates);

            try {
                java.util.Date d = sdf.parse(sdate);
                return new Date(d.getTime());
            }
            catch (ParseException e) {
                return null;
            }
        }
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "          ";
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        }
    }

    private String getValue(String line, int s, int e) {
        String v = line.substring(s, e);
        return StringUtils.trimTrailingWhitespace(v);
    }

    public void setFromTextFile(String line) {

        // Just in case
        line = line + SPACES;

        if (!"    ".equals(line.substring(0, 4))) {
            setUniversityFiscalYear(new Integer(line.substring(0, 4)));
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
        setFinancialDocumentNumber(getValue(line, 37, 46));
        if (!"     ".equals(line.substring(46, 51)) && !"00000".equals(line.substring(46, 51))) {
            setTransactionLedgerEntrySequenceNumber(new Integer(line.substring(46, 51).trim()));
        }
        else {
            setTransactionLedgerEntrySequenceNumber(null);
        }
        setTransactionLedgerEntryDescription(getValue(line, 51, 91));
        setTransactionLedgerEntryAmount(new KualiDecimal(line.substring(91, 108).trim()));
        setTransactionDebitCreditCode(line.substring(108, 109));
        setTransactionDate(parseDate(line.substring(109, 119), false));
        setOrganizationDocumentNumber(getValue(line, 119, 129));
        setProjectCode(getValue(line, 129, 139));
        setOrganizationReferenceId(getValue(line, 139, 147));
        setReferenceFinancialDocumentTypeCode(getValue(line, 147, 151));
        setReferenceFinancialSystemOriginationCode(getValue(line, 151, 153));
        setReferenceFinancialDocumentNumber(getValue(line, 153, 162));
        setFinancialDocumentReversalDate(parseDate(line.substring(162, 172), true));
        setTransactionEncumbranceUpdateCode(line.substring(172, 173));
    }

    private static String SPACES = "                                                                                                              ";

    private String getField(int size, String value) {
        if (value == null) {
            return SPACES.substring(0, size);
        }
        else {
            if (value.length() < size) {
                return value + SPACES.substring(0, size - value.length());
            }
            else {
                return value;
            }
        }
    }

    public String getLine() {
        StringBuffer sb = new StringBuffer();
        if (universityFiscalYear == null) {
            sb.append("    ");
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
        sb.append(getField(9, financialDocumentNumber));

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
            sb.append("                 ");
        }
        else {
            String a = transactionLedgerEntryAmount.toString();
            sb.append("                 ".substring(0, 17 - a.length()));
            sb.append(a);
        }
        sb.append(getField(1, transactionDebitCreditCode));
        sb.append(formatDate(transactionDate));
        sb.append(getField(10, organizationDocumentNumber));
        sb.append(getField(10, projectCode));
        sb.append(getField(8, organizationReferenceId));
        sb.append(getField(4, referenceFinancialDocumentTypeCode));
        sb.append(getField(2, referenceFinancialSystemOriginationCode));
        sb.append(getField(9, referenceFinancialDocumentNumber));
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
        map.put("financialDocumentNumber", financialDocumentNumber);
        map.put("financialDocumentTypeCode", financialDocumentTypeCode);
        map.put("financialSystemOriginationCode", financialSystemOriginationCode);
        map.put("transactionLedgerEntrySequenceNumber", transactionLedgerEntrySequenceNumber);
        map.put("transactionLedgerEntryDescription", transactionLedgerEntryDescription);
        return map;
    }

    public OriginEntryGroup getGroup() {
        return group;
    }

    public void setGroup(OriginEntryGroup oeg) {
        if (oeg != null) {
            entryGroupId = oeg.getId();
            group = oeg;
        }
        else {
            entryGroupId = null;
            group = null;
        }
    }

    public boolean isTransactionScrubberOffsetGenerationIndicator() {
        return transactionScrubberOffsetGenerationIndicator;
    }

    public void setTransactionScrubberOffsetGenerationIndicator(boolean transactionScrubberOffsetGenerationIndicator) {
        this.transactionScrubberOffsetGenerationIndicator = transactionScrubberOffsetGenerationIndicator;
    }

    public A21SubAccount getA21SubAccount() {
        return a21SubAccount;
    }

    public void setA21SubAccount(A21SubAccount subAccount) {
        a21SubAccount = subAccount;
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

    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }

    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BalanceTyp getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(BalanceTyp balanceType) {
        this.balanceType = balanceType;
    }

    public Chart getChart() {
        return chart;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public Options getOption() {
        return option;
    }

    public void setOption(Options option) {
        this.option = option;
    }

    public ProjectCode getProject() {
        return project;
    }

    public void setProject(ProjectCode project) {
        this.project = project;
    }

    public SubAccount getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    public UniversityDate getUniversityDate() {
        return universityDate;
    }

    public void setUniversityDate(UniversityDate universityDate) {
        this.universityDate = universityDate;
    }

    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }

    public UniversityDate getReversalDate() {
        return reversalDate;
    }

    public void setReversalDate(UniversityDate reversalDate) {
        this.reversalDate = reversalDate;
    }

    public OriginationCode getOrigination() {
        return origination;
    }

    public void setOrigination(OriginationCode origination) {
        this.origination = origination;
    }


    public DocumentType getReferenceDocumentType() {
        return referenceDocumentType;
    }

    public void setReferenceDocumentType(DocumentType referenceDocumentType) {
        this.referenceDocumentType = referenceDocumentType;
    }

    public boolean isDebit() {
        return Constants.GL_DEBIT_CODE.equals(this.transactionDebitCreditCode);
    }

    public boolean isCredit() {
        return Constants.GL_CREDIT_CODE.equals(this.transactionDebitCreditCode);
    }

    /**
     * Gets the budgetYear attribute.
     * 
     * @return Returns the budgetYear.
     */
    public Integer getBudgetYear() {
        return budgetYear;
    }

    /**
     * Sets the budgetYear attribute value.
     * 
     * @param budgetYear The budgetYear to set.
     */
    public void setBudgetYear(Integer budgetYear) {
        this.budgetYear = budgetYear;
    }

    /**
     * Gets the budgetYearFundingSourceCode attribute.
     * 
     * @return Returns the budgetYearFundingSourceCode.
     */
    public String getBudgetYearFundingSourceCode() {
        return budgetYearFundingSourceCode;
    }

    /**
     * Sets the budgetYearFundingSourceCode attribute value.
     * 
     * @param budgetYearFundingSourceCode The budgetYearFundingSourceCode to set.
     */
    public void setBudgetYearFundingSourceCode(String budgetYearFundingSourceCode) {
        this.budgetYearFundingSourceCode = budgetYearFundingSourceCode;
    }


}
