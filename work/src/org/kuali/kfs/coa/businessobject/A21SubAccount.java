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
package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author jsissom
 *
 */
public class A21SubAccount extends BusinessObjectBase {
  static final long serialVersionUID = -4101590175655031672L;

  private String chartOfAccountsCode;
  private String accountNumber;
  private String subAccountNumber;
  private String subAccountTypeCode;
  private String indirectCostRecoveryTypeCode;
  private String seriesId;
  private String indirectCostRecoveryChartOfAccountsCode;
  private String indirectCostRecoveryAccountNumber;
  private String offsetCampusCode;
  private String costSharingChartOfAccountsCode;
  private String costSharingAccountNumber;
  private String costSharingSubAccountNumber;

  /**
   * 
   */
  public A21SubAccount() {
    super();
  }

  /* (non-Javadoc)
   * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
   */
  protected LinkedHashMap toStringMapper() {
    LinkedHashMap map = new LinkedHashMap();
    map.put("chartOfAccountsCode", getChartOfAccountsCode());
    map.put("accountNumber", getAccountNumber());
    map.put("subAccountNumber", getSubAccountNumber());
    return map;
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
  public String getCostSharingAccountNumber() {
    return costSharingAccountNumber;
  }
  public void setCostSharingAccountNumber(String costSharingAccountNumber) {
    this.costSharingAccountNumber = costSharingAccountNumber;
  }
  public String getCostSharingChartOfAccountsCode() {
    return costSharingChartOfAccountsCode;
  }
  public void setCostSharingChartOfAccountsCode(String costSharingChartOfAccountsCode) {
    this.costSharingChartOfAccountsCode = costSharingChartOfAccountsCode;
  }
  public String getCostSharingSubAccountNumber() {
    return costSharingSubAccountNumber;
  }
  public void setCostSharingSubAccountNumber(String costSharingSubAccountNumber) {
    this.costSharingSubAccountNumber = costSharingSubAccountNumber;
  }
  public String getIndirectCostRecoveryAccountNumber() {
    return indirectCostRecoveryAccountNumber;
  }
  public void setIndirectCostRecoveryAccountNumber(String indirectCostRecoveryAccountNumber) {
    this.indirectCostRecoveryAccountNumber = indirectCostRecoveryAccountNumber;
  }
  public String getIndirectCostRecoveryChartOfAccountsCode() {
    return indirectCostRecoveryChartOfAccountsCode;
  }
  public void setIndirectCostRecoveryChartOfAccountsCode(String indirectCostRecoveryChartOfAccountsCode) {
    this.indirectCostRecoveryChartOfAccountsCode = indirectCostRecoveryChartOfAccountsCode;
  }
  public String getIndirectCostRecoveryTypeCode() {
    return indirectCostRecoveryTypeCode;
  }
  public void setIndirectCostRecoveryTypeCode(String indirectCostRecoveryTypeCode) {
    this.indirectCostRecoveryTypeCode = indirectCostRecoveryTypeCode;
  }
  public String getOffsetCampusCode() {
    return offsetCampusCode;
  }
  public void setOffsetCampusCode(String offsetCampusCode) {
    this.offsetCampusCode = offsetCampusCode;
  }
  public String getSeriesId() {
    return seriesId;
  }
  public void setSeriesId(String seriesId) {
    this.seriesId = seriesId;
  }
  public String getSubAccountNumber() {
    return subAccountNumber;
  }
  public void setSubAccountNumber(String subAccountNumber) {
    this.subAccountNumber = subAccountNumber;
  }
  public String getSubAccountTypeCode() {
    return subAccountTypeCode;
  }
  public void setSubAccountTypeCode(String subAccountTypeCode) {
    this.subAccountTypeCode = subAccountTypeCode;
  }
}
