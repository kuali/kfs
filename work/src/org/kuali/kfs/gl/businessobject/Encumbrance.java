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

import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.codes.BalanceTyp;

/**
 * @author jsissom
 *  
 */
public class Encumbrance extends BusinessObjectBase {
    static final long serialVersionUID = -7494473472438516396L;

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String objectCode;
    private String subObjectCode;
    private String balanceTypeCode;
    private String documentTypeCode;
    private String originCode;
    private String documentNumber;
    private String transactionEncumbranceDescription;
    private Date transactionEncumbranceDate;
    private KualiDecimal accountLineEncumbranceAmount;
    private KualiDecimal accountLineEncumbranceClosedAmount;
    private String accountLineEncumbrancePurgeCode;
    private Date timestamp;

    private Chart chart;
    private Account account;
    private ObjectCode financialObject;
    private BalanceTyp balanceType;

    private DummyBusinessObject dummyBusinessObject;

    public Encumbrance() {
    }

    public Encumbrance(Transaction t) {
        universityFiscalYear = t.getUniversityFiscalYear();
        chartOfAccountsCode = t.getChartOfAccountsCode();
        accountNumber = t.getAccountNumber();
        subAccountNumber = t.getSubAccountNumber();
        objectCode = t.getFinancialObjectCode();
        subObjectCode = t.getFinancialSubObjectCode();
        balanceTypeCode = t.getFinancialBalanceTypeCode();
        documentTypeCode = t.getFinancialDocumentTypeCode();
        originCode = t.getFinancialSystemOriginationCode();
        documentNumber = t.getFinancialDocumentNumber();
        transactionEncumbranceDescription = t.getTransactionLedgerEntryDesc();
        transactionEncumbranceDate = t.getTransactionDate();
        accountLineEncumbranceAmount = new KualiDecimal("0");
        accountLineEncumbranceClosedAmount = new KualiDecimal("0");
        accountLineEncumbrancePurgeCode = " ";
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
        map.put("documentTypeCode", getDocumentTypeCode());
        map.put("originCode", getOriginCode());
        map.put("documentNumber", getDocumentNumber());
        return map;
    }

    /**
     * @return Returns the accountLineEncumbranceAmount.
     */
    public KualiDecimal getAccountLineEncumbranceAmount() {
        return accountLineEncumbranceAmount;
    }

    /**
     * @param accountLineEncumbranceAmount The accountLineEncumbranceAmount to set.
     */
    public void setAccountLineEncumbranceAmount(KualiDecimal accountLineEncumbranceAmount) {
        this.accountLineEncumbranceAmount = accountLineEncumbranceAmount;
    }

    /**
     * @return Returns the accountLineEncumbranceClearedAmount.
     */
    public KualiDecimal getAccountLineEncumbranceClosedAmount() {
        return accountLineEncumbranceClosedAmount;
    }

    /**
     * @param accountLineEncumbranceClearedAmount The accountLineEncumbranceClearedAmount
     *        to set.
     */
    public void setAccountLineEncumbranceClosedAmount(
            KualiDecimal accountLineEncumbranceClosedAmount) {
        this.accountLineEncumbranceClosedAmount = accountLineEncumbranceClosedAmount;
    }

    /**
     * @return Returns the accountLineEncumbrancePrg.
     */
    public String getAccountLineEncumbrancePurgeCode() {
        return accountLineEncumbrancePurgeCode;
    }

    /**
     * @param accountLineEncumbrancePrg The accountLineEncumbrancePrg to set.
     */
    public void setAccountLineEncumbrancePurgeCode(String accountLineEncumbrancePurgeCode) {
        this.accountLineEncumbrancePurgeCode = accountLineEncumbrancePurgeCode;
    }

    /**
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber The accountNumber to set.
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
     * @param balanceTypeCode The balanceTypeCode to set.
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
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @return Returns the documentTypeCode.
     */
    public String getDocumentTypeCode() {
        return documentTypeCode;
    }

    /**
     * @param documentTypeCode The documentTypeCode to set.
     */
    public void setDocumentTypeCode(String documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
    }

    /**
     * @return Returns the objectCode.
     */
    public String getObjectCode() {
        return objectCode;
    }

    /**
     * @param objectCode The objectCode to set.
     */
    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    /**
     * @return Returns the originCode.
     */
    public String getOriginCode() {
        return originCode;
    }

    /**
     * @param originCode The originCode to set.
     */
    public void setOriginCode(String originCode) {
        this.originCode = originCode;
    }

    /**
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * @param subAccountNumber The subAccountNumber to set.
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
     * @param subObjectCode The subObjectCode to set.
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
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return Returns the transactionEncumbranceDate.
     */
    public Date getTransactionEncumbranceDate() {
        return transactionEncumbranceDate;
    }

    /**
     * @param transactionEncumbranceDate The transactionEncumbranceDate to set.
     */
    public void setTransactionEncumbranceDate(Date transactionEncumbranceDate) {
        this.transactionEncumbranceDate = transactionEncumbranceDate;
    }

    /**
     * @return Returns the transactionEncumbranceDescription.
     */
    public String getTransactionEncumbranceDescription() {
        return transactionEncumbranceDescription;
    }

    /**
     * @param transactionEncumbranceDescription The transactionEncumbranceDescription to
     *        set.
     */
    public void setTransactionEncumbranceDescription(
            String transactionEncumbranceDescription) {
        this.transactionEncumbranceDescription = transactionEncumbranceDescription;
    }

    /**
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     * 
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the chart attribute.
     * 
     * @return Returns the chart.
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute value.
     * 
     * @param chart The chart to set.
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * Gets the financialObject attribute.
     * 
     * @return Returns the financialObject.
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject attribute value.
     * 
     * @param financialObject The financialObject to set.
     */
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the balanceType attribute.
     * 
     * @return Returns the balanceType.
     */
    public BalanceTyp getBalanceType() {
        return balanceType;
    }

    /**
     * Sets the balanceType attribute value.
     * 
     * @param balanceType The balanceType to set.
     */
    public void setBalanceType(BalanceTyp balanceType) {
        this.balanceType = balanceType;
    }

    /**
     * Gets the dummyBusinessObject attribute.
     * 
     * @return Returns the dummyBusinessObject.
     */
    public DummyBusinessObject getDummyBusinessObject() {
        return dummyBusinessObject;
    }

    /**
     * Sets the dummyBusinessObject attribute value.
     * 
     * @param dummyBusinessObject The dummyBusinessObject to set.
     */
    public void setDummyBusinessObject(DummyBusinessObject dummyBusinessObject) {
        this.dummyBusinessObject = dummyBusinessObject;
    }
}
