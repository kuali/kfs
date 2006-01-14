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

import org.kuali.core.bo.BusinessObjectBase;
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
public class Entry extends BusinessObjectBase implements Transaction {
  static final long serialVersionUID = 2265582930639775607L;

	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String objectCode;
	private String subObjectCode;
	private String balanceTypeCode;
	private String objectTypeCode;
	private String universityFiscalAccountingPeriod;
	private String documentTypeCode;
	private String originCode;
	private String documentNumber;
	private Integer transactionEntrySequenceId;
	private String transactionLedgerEntryDescription;
	private KualiDecimal transactionLedgerEntryAmount;
	private String debitOrCreditCode;
	private Date transactionDate;
	private String organizationDocumentNumber;
	private String projectCode;
	private String organizationReferenceId;
	private String referenceDocumentTypeCode;
	private String referenceOriginCode;
	private String referenceDocumentNumber;
	private Date documentReversalDate;
	private String encumbranceUpdateCode;
	private Date postDate;
	private Date timestamp;
  private String budgetYear;

  // bo references
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

  protected LinkedHashMap toStringMapper() {
		LinkedHashMap map = new LinkedHashMap();
		map.put("universityFiscalYear", getUniversityFiscalYear());
		map.put("chartOfAccountsCode", getChartOfAccountsCode());
		map.put("accountNumber", getAccountNumber());
		map.put("subAccountNumber", getSubAccountNumber());
		map.put("objectCode", getObjectCode());
		map.put("subObjectCode", getSubObjectCode());
		map.put("balanceTypeCode", getBalanceTypeCode());
		map.put("objectTypeCode", getObjectTypeCode());
		map.put("universityFiscalAccountingPeriod", getUniversityFiscalAccountingPeriod());
		map.put("documentTypeCode", getDocumentTypeCode());
		map.put("originCode", getOriginCode());
		map.put("documentNumber", getDocumentNumber());
		map.put("transactionEntrySequenceId", getTransactionEntrySequenceId());
		return map;
	}

  public Entry() {
  }

  public Entry(Transaction t,java.util.Date postDate) {
    setUniversityFiscalYear(t.getUniversityFiscalYear());
    setChartOfAccountsCode(t.getChartOfAccountsCode());
    setAccountNumber(t.getAccountNumber());
    setSubAccountNumber(t.getSubAccountNumber());
    setObjectCode(t.getObjectCode());
    setSubObjectCode(t.getSubObjectCode());
    setBalanceTypeCode(t.getBalanceTypeCode());
    setObjectTypeCode(t.getObjectTypeCode());
    setUniversityFiscalAccountingPeriod(t.getUniversityFiscalAccountingPeriod());
    setDocumentTypeCode(t.getDocumentTypeCode());
    setOriginCode(t.getOriginCode());
    setDocumentNumber(t.getDocumentNumber());
    setTransactionEntrySequenceId(t.getTransactionEntrySequenceId());
    setTransactionLedgerEntryDescription(t.getTransactionLedgerEntryDescription());
    setTransactionLedgerEntryAmount(t.getTransactionLedgerEntryAmount());
    setDebitOrCreditCode(t.getDebitOrCreditCode());
    setTransactionDate(t.getTransactionDate());
    setOrganizationDocumentNumber(t.getOrganizationDocumentNumber());
    setProjectCode(t.getProjectCode());
    setOrganizationReferenceId(t.getOrganizationReferenceId());
    setReferenceDocumentTypeCode(t.getReferenceDocumentTypeCode());
    setReferenceOriginCode(t.getReferenceOriginCode());
    setReferenceDocumentNumber(t.getReferenceDocumentNumber());
    setDocumentReversalDate(t.getDocumentReversalDate());
    setEncumbranceUpdateCode(t.getEncumbranceUpdateCode());
    if ( postDate != null ) {
      setPostDate(new Date(postDate.getTime()));
    }

    java.util.Date now = new java.util.Date();
    setTimestamp(new Date(now.getTime()));
  }

  /**
   * @return Returns the accountNumber.
   */
  public String getAccountNumber() {
    return accountNumber;
  }

  /**
   * @param accountNumber
   *            The accountNumber to set.
   */
  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  /**
   * @return Returns the balanceTypeCode.
   */
  public String getBalanceTypeCode() {
    return balanceTypeCode;
  }

  /**
   * @param balanceTypeCode
   *            The balanceTypeCode to set.
   */
  public void setBalanceTypeCode(String balanceTypeCode) {
    this.balanceTypeCode = balanceTypeCode;
  }

  /**
   * @return Returns the chartOfAccountsCode.
   */
  public String getChartOfAccountsCode() {
    return chartOfAccountsCode;
  }

  /**
   * @param chartOfAccountsCode
   *            The chartOfAccountsCode to set.
   */
  public void setChartOfAccountsCode(String chartOfAccountsCode) {
    this.chartOfAccountsCode = chartOfAccountsCode;
  }

  /**
   * @return Returns the debitOrCreditCode.
   */
  public String getDebitOrCreditCode() {
    return debitOrCreditCode;
  }

  /**
   * @param debitOrCreditCode
   *            The debitOrCreditCode to set.
   */
  public void setDebitOrCreditCode(String debitOrCreditCode) {
    this.debitOrCreditCode = debitOrCreditCode;
  }

  /**
   * @return Returns the documentNumber.
   */
  public String getDocumentNumber() {
    return documentNumber;
  }

  /**
   * @param documentNumber
   *            The documentNumber to set.
   */
  public void setDocumentNumber(String documentNumber) {
    this.documentNumber = documentNumber;
  }

  /**
   * @return Returns the documentReversalDate.
   */
  public Date getDocumentReversalDate() {
    return documentReversalDate;
  }

  /**
   * @param documentReversalDate
   *            The documentReversalDate to set.
   */
  public void setDocumentReversalDate(Date documentReversalDate) {
    this.documentReversalDate = documentReversalDate;
  }

  /**
   * @return Returns the documentTypeCode.
   */
  public String getDocumentTypeCode() {
    return documentTypeCode;
  }

  /**
   * @param documentTypeCode
   *            The documentTypeCode to set.
   */
  public void setDocumentTypeCode(String documentTypeCode) {
    this.documentTypeCode = documentTypeCode;
  }

  /**
   * @return Returns the encumbranceUpdateCode.
   */
  public String getEncumbranceUpdateCode() {
    return encumbranceUpdateCode;
  }

  /**
   * @param encumbranceUpdateCode
   *            The encumbranceUpdateCode to set.
   */
  public void setEncumbranceUpdateCode(String encumbranceUpdateCode) {
    this.encumbranceUpdateCode = encumbranceUpdateCode;
  }

  /**
   * @return Returns the objectCode.
   */
  public String getObjectCode() {
    return objectCode;
  }

  /**
   * @param objectCode
   *            The objectCode to set.
   */
  public void setObjectCode(String objectCode) {
    this.objectCode = objectCode;
  }

  /**
   * @return Returns the objectTypeCode.
   */
  public String getObjectTypeCode() {
    return objectTypeCode;
  }

  /**
   * @param objectTypeCode
   *            The objectTypeCode to set.
   */
  public void setObjectTypeCode(String objectTypeCode) {
    this.objectTypeCode = objectTypeCode;
  }

  /**
   * @return Returns the organizationDocumentNumber.
   */
  public String getOrganizationDocumentNumber() {
    return organizationDocumentNumber;
  }

  /**
   * @param organizationDocumentNumber
   *            The organizationDocumentNumber to set.
   */
  public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
    this.organizationDocumentNumber = organizationDocumentNumber;
  }

  /**
   * @return Returns the organizationReferenceId.
   */
  public String getOrganizationReferenceId() {
    return organizationReferenceId;
  }

  /**
   * @param organizationReferenceId
   *            The organizationReferenceId to set.
   */
  public void setOrganizationReferenceId(String organizationReferenceId) {
    this.organizationReferenceId = organizationReferenceId;
  }

  /**
   * @return Returns the originCode.
   */
  public String getOriginCode() {
    return originCode;
  }

  /**
   * @param originCode
   *            The originCode to set.
   */
  public void setOriginCode(String originCode) {
    this.originCode = originCode;
  }

  /**
   * @return Returns the postDate.
   */
  public Date getPostDate() {
    return postDate;
  }

  /**
   * @param postDate
   *            The postDate to set.
   */
  public void setPostDate(Date postDate) {
    this.postDate = postDate;
  }

  /**
   * @return Returns the projectCode.
   */
  public String getProjectCode() {
    return projectCode;
  }

  /**
   * @param projectCode
   *            The projectCode to set.
   */
  public void setProjectCode(String projectCode) {
    this.projectCode = projectCode;
  }

  /**
   * @return Returns the referenceDocumentNumber.
   */
  public String getReferenceDocumentNumber() {
    return referenceDocumentNumber;
  }

  /**
   * @param referenceDocumentNumber
   *            The referenceDocumentNumber to set.
   */
  public void setReferenceDocumentNumber(String referenceDocumentNumber) {
    this.referenceDocumentNumber = referenceDocumentNumber;
  }

  /**
   * @return Returns the referenceDocumentTypeCode.
   */
  public String getReferenceDocumentTypeCode() {
    return referenceDocumentTypeCode;
  }

  /**
   * @param referenceDocumentTypeCode
   *            The referenceDocumentTypeCode to set.
   */
  public void setReferenceDocumentTypeCode(String referenceDocumentTypeCode) {
    this.referenceDocumentTypeCode = referenceDocumentTypeCode;
  }

  /**
   * @return Returns the referenceOriginCode.
   */
  public String getReferenceOriginCode() {
    return referenceOriginCode;
  }

  /**
   * @param referenceOriginCode
   *            The referenceOriginCode to set.
   */
  public void setReferenceOriginCode(String referenceOriginCode) {
    this.referenceOriginCode = referenceOriginCode;
  }

  /**
   * @return Returns the subAccountNumber.
   */
  public String getSubAccountNumber() {
    return subAccountNumber;
  }

  /**
   * @param subAccountNumber
   *            The subAccountNumber to set.
   */
  public void setSubAccountNumber(String subAccountNumber) {
    this.subAccountNumber = subAccountNumber;
  }

  /**
   * @return Returns the subObjectCode.
   */
  public String getSubObjectCode() {
    return subObjectCode;
  }

  /**
   * @param subObjectCode
   *            The subObjectCode to set.
   */
  public void setSubObjectCode(String subObjectCode) {
    this.subObjectCode = subObjectCode;
  }

  /**
   * @return Returns the timestamp.
   */
  public Date getTimestamp() {
    return timestamp;
  }

  /**
   * @param timestamp
   *            The timestamp to set.
   */
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * @return Returns the transactionDate.
   */
  public Date getTransactionDate() {
    return transactionDate;
  }

  /**
   * @param transactionDate
   *            The transactionDate to set.
   */
  public void setTransactionDate(Date transactionDate) {
    this.transactionDate = transactionDate;
  }

  /**
   * @return Returns the transactionEntrySequenceId.
   */
  public Integer getTransactionEntrySequenceId() {
    return transactionEntrySequenceId;
  }

  /**
   * @param transactionEntrySequenceId
   *            The transactionEntrySequenceId to set.
   */
  public void setTransactionEntrySequenceId(Integer transactionEntrySequenceId) {
    this.transactionEntrySequenceId = transactionEntrySequenceId;
  }

  /**
   * @return Returns the transactionLedgerEntryAmount.
   */
  public KualiDecimal getTransactionLedgerEntryAmount() {
    return transactionLedgerEntryAmount;
  }

  /**
   * @param transactionLedgerEntryAmount
   *            The transactionLedgerEntryAmount to set.
   */
  public void setTransactionLedgerEntryAmount(
      KualiDecimal transactionLedgerEntryAmount) {
    this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
  }

  /**
   * @return Returns the transactionLedgerEntryDescription.
   */
  public String getTransactionLedgerEntryDescription() {
    return transactionLedgerEntryDescription;
  }

  /**
   * @param transactionLedgerEntryDescription
   *            The transactionLedgerEntryDescription to set.
   */
  public void setTransactionLedgerEntryDescription(
      String transactionLedgerEntryDescription) {
    this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
  }

  /**
   * @return Returns the universityFiscalAccountingPeriod.
   */
  public String getUniversityFiscalAccountingPeriod() {
    return universityFiscalAccountingPeriod;
  }

  /**
   * @param universityFiscalAccountingPeriod
   *            The universityFiscalAccountingPeriod to set.
   */
  public void setUniversityFiscalAccountingPeriod(
      String universityFiscalAccountingPeriod) {
    this.universityFiscalAccountingPeriod = universityFiscalAccountingPeriod;
  }

  /**
   * @return Returns the universityFiscalYear.
   */
  public Integer getUniversityFiscalYear() {
    return universityFiscalYear;
  }

  /**
   * @param universityFiscalYear
   *            The universityFiscalYear to set.
   */
  public void setUniversityFiscalYear(Integer universityFiscalYear) {
    this.universityFiscalYear = universityFiscalYear;
  }

  public Account getAccount() {
    return account;
  }
  public void setAccount(Account account) {
    this.account = account;
  }
  public AccountingPeriod getAccountingPeriod() {
    return accountingPeriod;
  }
  public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
    this.accountingPeriod = accountingPeriod;
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
  public UniversityDate getReversalDate() {
    return reversalDate;
  }
  public void setReversalDate(UniversityDate reversalDate) {
    this.reversalDate = reversalDate;
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
  public String getBudgetYear() {
    return budgetYear;
  }
  public void setBudgetYear(String budgetYear) {
    this.budgetYear = budgetYear;
  }
}
