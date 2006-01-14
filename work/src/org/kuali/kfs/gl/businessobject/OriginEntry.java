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
import java.util.LinkedHashMap;

import org.kuali.Constants;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.OriginationCode;
import org.kuali.core.document.DocumentType;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;

/**
 * @author jsissom
 * 
 */
public class OriginEntry extends BusinessObjectBase implements Transaction {
    static final long serialVersionUID = -2498312988235747448L;

    private Integer entryId;
    private Integer entryGroupId;
    private String accountNumber;
    private String documentNumber;
    private String referenceDocumentNumber;
    private String referenceDocumentTypeCode;
    private Date documentReversalDate;
    private String documentTypeCode;
    private String balanceTypeCode;
    private String chartOfAccountsCode;
    private String objectTypeCode;
    private String objectCode;
    private String subObjectCode;
    private String originCode;
    private String referenceOriginCode;
    private String organizationDocumentNumber;
    private String organizationReferenceId;
    private String projectCode;
    private String subAccountNumber;
    private Date transactionDate;
    private String debitOrCreditCode;
    private String encumbranceUpdateCode;
    private Integer transactionEntrySequenceId;
    private KualiDecimal transactionLedgerEntryAmount;
    private String transactionLedgerEntryDescription;
    private String universityFiscalAccountingPeriod;
    private Integer universityFiscalYear;
    private String budgetYear;

    // bo references
    private OriginEntryGroup group;
    private Account account;
    private SubAccount subAccount;
    private BalanceTyp balanceType;
    private Chart chart;
    private ObjectCode financialObject;
    private SubObjCd financialSubObject;
    private ObjectType objectType;
    private ProjectCode project;
    private DocumentType documentType;
    private UniversityDate universityDate;
    private Option option;
    private AccountingPeriod accountingPeriod;
    private UniversityDate reversalDate;
    private OriginationCode origination;
    private DocumentType referenceDocumentType;

    /**
     * 
     */
    public OriginEntry() {
        super();
    }

    public OriginEntry(Transaction t) {
        super();
        setTransaction(t);
    }

    public void setTransaction(Transaction t) {
        setAccountNumber(t.getAccountNumber());
        setDocumentNumber(t.getDocumentNumber());
        setReferenceDocumentNumber(t.getReferenceDocumentNumber());
        setReferenceDocumentTypeCode(t.getReferenceDocumentTypeCode());
        setDocumentReversalDate(t.getDocumentReversalDate());
        setDocumentTypeCode(t.getDocumentTypeCode());
        setBalanceTypeCode(t.getBalanceTypeCode());
        setChartOfAccountsCode(t.getChartOfAccountsCode());
        setObjectTypeCode(t.getObjectTypeCode());
        setObjectCode(t.getObjectCode());
        setSubObjectCode(t.getSubObjectCode());
        setOriginCode(t.getOriginCode());
        setReferenceOriginCode(t.getReferenceOriginCode());
        setOrganizationDocumentNumber(t.getOrganizationDocumentNumber());
        setOrganizationReferenceId(t.getOrganizationReferenceId());
        setProjectCode(t.getProjectCode());
        setSubAccountNumber(t.getSubAccountNumber());
        setTransactionDate(t.getTransactionDate());
        setDebitOrCreditCode(t.getDebitOrCreditCode());
        setEncumbranceUpdateCode(t.getEncumbranceUpdateCode());
        setTransactionEntrySequenceId(t.getTransactionEntrySequenceId());
        setTransactionLedgerEntryAmount(t.getTransactionLedgerEntryAmount());
        setTransactionLedgerEntryDescription(t.getTransactionLedgerEntryDescription());
        setUniversityFiscalAccountingPeriod(t.getUniversityFiscalAccountingPeriod());
        setUniversityFiscalYear(t.getUniversityFiscalYear());
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("entryId", entryId);
        map.put("universityFiscalYear", universityFiscalYear);
        map.put("chartOfAccountsCode", chartOfAccountsCode);
        map.put("accountNumber", accountNumber);
        map.put("subAccountNumber", subAccountNumber);
        map.put("objectCode", objectCode);
        map.put("subObjectCode", subObjectCode);
        map.put("balanceTypeCode", balanceTypeCode);
        map.put("objectTypeCode", objectTypeCode);
        map.put("universityFiscalAccountingPeriod", universityFiscalAccountingPeriod);
        map.put("documentTypeCode", documentTypeCode);
        map.put("originCode", originCode);
        map.put("documentNumber", documentNumber);
        map.put("transactionEntrySequenceId", transactionEntrySequenceId);
        return map;
    }

    public OriginEntryGroup getGroup() {
        return group;
    }

    public void setGroup(OriginEntryGroup oeg) {
      if ( oeg != null ) {
        entryGroupId = oeg.getId();
        group = oeg;
      } else {
        entryGroupId = null;
        group = null;
      }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBalanceTypeCode() {
        return balanceTypeCode;
    }

    public void setBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getDebitOrCreditCode() {
        return debitOrCreditCode;
    }

    public void setDebitOrCreditCode(String debitOrCreditCode) {
        this.debitOrCreditCode = debitOrCreditCode;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Date getDocumentReversalDate() {
        return documentReversalDate;
    }

    public void setDocumentReversalDate(Date documentReversalDate) {
        this.documentReversalDate = documentReversalDate;
    }

    public String getDocumentTypeCode() {
        return documentTypeCode;
    }

    public void setDocumentTypeCode(String documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
    }

    public String getEncumbranceUpdateCode() {
        return encumbranceUpdateCode;
    }

    public void setEncumbranceUpdateCode(String encumbranceUpdateCode) {
        this.encumbranceUpdateCode = encumbranceUpdateCode;
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

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    public String getObjectTypeCode() {
        return objectTypeCode;
    }

    public void setObjectTypeCode(String objectTypeCode) {
        this.objectTypeCode = objectTypeCode;
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

    public String getOriginCode() {
        return originCode;
    }

    public void setOriginCode(String originCode) {
        this.originCode = originCode;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getReferenceDocumentNumber() {
        return referenceDocumentNumber;
    }

    public void setReferenceDocumentNumber(String referenceDocumentNumber) {
        this.referenceDocumentNumber = referenceDocumentNumber;
    }

    public String getReferenceDocumentTypeCode() {
        return referenceDocumentTypeCode;
    }

    public void setReferenceDocumentTypeCode(String referenceDocumentTypeCode) {
        this.referenceDocumentTypeCode = referenceDocumentTypeCode;
    }

    public String getReferenceOriginCode() {
        return referenceOriginCode;
    }

    public void setReferenceOriginCode(String referenceOriginCode) {
        this.referenceOriginCode = referenceOriginCode;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getSubObjectCode() {
        return subObjectCode;
    }

    public void setSubObjectCode(String subObjectCode) {
        this.subObjectCode = subObjectCode;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Integer getTransactionEntrySequenceId() {
        return transactionEntrySequenceId;
    }

    public void setTransactionEntrySequenceId(Integer transactionEntrySequenceId) {
        this.transactionEntrySequenceId = transactionEntrySequenceId;
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

    public String getUniversityFiscalAccountingPeriod() {
        return universityFiscalAccountingPeriod;
    }

    public void setUniversityFiscalAccountingPeriod(String universityFiscalAccountingPeriod) {
        this.universityFiscalAccountingPeriod = universityFiscalAccountingPeriod;
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

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
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

    /**
     * @return Returns the budgetYear.
     */
    public String getBudgetYear() {
        return budgetYear;
    }

    /**
     * @param budgetYear The budgetYear to set.
     */
    public void setBudgetYear(String budgetYear) {
        this.budgetYear = budgetYear;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		OriginEntry entry = (OriginEntry) obj;
		return getEntryId().equals(entry.getEntryId());
	}

    public boolean isDebit() {
        return Constants.GL_DEBIT_CODE.equals(this.debitOrCreditCode);
    }
    public boolean isCredit() {
        return Constants.GL_CREDIT_CODE.equals(this.debitOrCreditCode);
    }

}
