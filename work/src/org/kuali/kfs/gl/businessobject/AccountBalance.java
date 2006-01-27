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
import org.kuali.core.util.KualiDecimal;

/**
 * @author jsissom
 *
 */
public class AccountBalance extends BusinessObjectBase {
  static final long serialVersionUID = 6873573726961704771L;

  private static KualiDecimal ZERO = new KualiDecimal("0");

  private Integer universityFiscalYear;
  private String chartOfAccountsCode;
  private String accountNumber;
  private String subAccountNumber;
  private String objectCode;
  private String subObjectCode;
  private KualiDecimal currentBudgetLineBalanceAmount;
  private KualiDecimal accountLineActualsBalanceAmount;
  private KualiDecimal accountLineEncumbranceBalanceAmount;
  private Date timestamp;

  public AccountBalance() {
    super();
  }

  public AccountBalance(Transaction t) {
    super();
    universityFiscalYear = t.getUniversityFiscalYear();
    chartOfAccountsCode = t.getChartOfAccountsCode();
    accountNumber = t.getAccountNumber();
    subAccountNumber = t.getSubAccountNumber();
    objectCode = t.getFinancialObjectCode();
    subObjectCode = t.getFinancialSubObjectCode();
    currentBudgetLineBalanceAmount = ZERO;
    accountLineActualsBalanceAmount = ZERO;
    accountLineEncumbranceBalanceAmount = ZERO;
  }

  /* (non-Javadoc)
   * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
   */
  protected LinkedHashMap toStringMapper() {
    LinkedHashMap map = new LinkedHashMap();
    map.put("universityFiscalYear", getUniversityFiscalYear());
    map.put("chartOfAccountsCode", getChartOfAccountsCode());
    map.put("accountNumber", getAccountNumber());
    map.put("subAccountNumber", getSubAccountNumber());
    map.put("objectCode", getObjectCode());
    map.put("subObjectCode", getSubObjectCode());
    return map;
  }

  public KualiDecimal getAccountLineActualsBalanceAmount() {
    return accountLineActualsBalanceAmount;
  }
  public void setAccountLineActualsBalanceAmount(KualiDecimal accountLineActualsBalanceAmount) {
    this.accountLineActualsBalanceAmount = accountLineActualsBalanceAmount;
  }
  public KualiDecimal getAccountLineEncumbranceBalanceAmount() {
    return accountLineEncumbranceBalanceAmount;
  }
  public void setAccountLineEncumbranceBalanceAmount(KualiDecimal accountLineEncumbranceBalanceAmount) {
    this.accountLineEncumbranceBalanceAmount = accountLineEncumbranceBalanceAmount;
  }
  public String getAccountNumber() {
    return accountNumber;
  }
  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }
  public String getChartOfAccountsCode() {
    return chartOfAccountsCode;
  }
  public void setChartOfAccountsCode(String chartOfAccountsCode) {
    this.chartOfAccountsCode = chartOfAccountsCode;
  }
  public KualiDecimal getCurrentBudgetLineBalanceAmount() {
    return currentBudgetLineBalanceAmount;
  }
  public void setCurrentBudgetLineBalanceAmount(KualiDecimal currentBudgetLineBalanceAmount) {
    this.currentBudgetLineBalanceAmount = currentBudgetLineBalanceAmount;
  }
  public String getObjectCode() {
    return objectCode;
  }
  public void setObjectCode(String objectCode) {
    this.objectCode = objectCode;
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
  public Date getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }
  public Integer getUniversityFiscalYear() {
    return universityFiscalYear;
  }
  public void setUniversityFiscalYear(Integer universityFiscalYear) {
    this.universityFiscalYear = universityFiscalYear;
  }
}
