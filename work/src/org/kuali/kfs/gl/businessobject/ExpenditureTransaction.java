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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.user.Options;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;

/**
 * @author jsissom
 *
 */
public class ExpenditureTransaction extends BusinessObjectBase {
  static final long serialVersionUID = 5296540728313789670L;

  private Integer universityFiscalYear;
  private String chartOfAccountsCode;
  private String accountNumber;
  private String subAccountNumber;
  private String objectCode;
  private String subObjectCode;
  private String balanceTypeCode;
  private String objectTypeCode;
  private String universityFiscalAccountingPeriod;
  private String projectCode;
  private String organizationReferenceId;
  private KualiDecimal accountObjectDirectCostAmount;

  private Account account;
  private Options option;

  /**
   * 
   */
  public ExpenditureTransaction() {
    super();
  }

  public ExpenditureTransaction(Transaction t) {
    universityFiscalYear = t.getUniversityFiscalYear();
    chartOfAccountsCode = t.getChartOfAccountsCode();
    accountNumber = t.getAccountNumber();
    subAccountNumber = t.getSubAccountNumber();
    objectCode = t.getFinancialObjectCode();
    subObjectCode = t.getFinancialSubObjectCode();
    balanceTypeCode = t.getFinancialBalanceTypeCode();
    objectTypeCode = t.getFinancialObjectTypeCode();
    universityFiscalAccountingPeriod = t.getUniversityFiscalPeriodCode();
    projectCode = t.getProjectCode();
    organizationReferenceId = t.getOrganizationReferenceId();
    accountObjectDirectCostAmount = new KualiDecimal("0");
  }

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
    map.put("projectCode",getProjectCode());
    map.put("organizationReferenceId",getOrganizationReferenceId());
    return map;
  }

  public Options getOption() {
    return option;
  }
  public void setOption(Options option) {
    this.option = option;
  }
  public Account getAccount() {
    return account;
  }
  public void setAccount(Account a) {
    account = a;
  }

  public String getAccountNumber() {
    return accountNumber;
  }
  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }
  public KualiDecimal getAccountObjectDirectCostAmount() {
    return accountObjectDirectCostAmount;
  }
  public void setAccountObjectDirectCostAmount(KualiDecimal accountObjectDirectCostAmount) {
    this.accountObjectDirectCostAmount = accountObjectDirectCostAmount;
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
  public String getOrganizationReferenceId() {
    return organizationReferenceId;
  }
  public void setOrganizationReferenceId(String organizationReferenceId) {
    this.organizationReferenceId = organizationReferenceId;
  }
  public String getProjectCode() {
    return projectCode;
  }
  public void setProjectCode(String projectCode) {
    this.projectCode = projectCode;
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
}
