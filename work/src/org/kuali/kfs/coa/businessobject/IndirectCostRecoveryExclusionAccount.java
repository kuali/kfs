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
public class IndirectCostRecoveryExclusionAccount extends BusinessObjectBase {
    static final long serialVersionUID = -7894071504928822853L;
    
    private String chartOfAccountsCode;
    private String accountNumber;
    private String objectChartOfAccountsCode;
    private String objectCode;
    
    private Chart chartOfAccounts;
    private Account account;
    private Chart objectChartOfAccounts;
    private ObjectCode object;
    
    /**
     * 
     */
    public IndirectCostRecoveryExclusionAccount() {
        super();
    }
    
    /* (non-Javadoc)
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("chartOfAccountsCode", getChartOfAccountsCode());
        map.put("accountNumber", getAccountNumber());
        map.put("objectChartOfAccountsCode", getObjectChartOfAccountsCode());
        map.put("objectCode", getObjectCode());
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
    public String getObjectChartOfAccountsCode() {
        return objectChartOfAccountsCode;
    }
    public void setObjectChartOfAccountsCode(String objectChartOfAccountsCode) {
        this.objectChartOfAccountsCode = objectChartOfAccountsCode;
    }
    public String getObjectCode() {
        return objectCode;
    }
    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }
    
    
    /**
     * Gets the account attribute. 
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }
    /**
     * Sets the account attribute value.
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }
    /**
     * Gets the chartOfAccounts attribute. 
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }
    /**
     * Sets the chartOfAccounts attribute value.
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }
    /**
     * Gets the object attribute. 
     * @return Returns the object.
     */
    public ObjectCode getObject() {
        return object;
    }
    /**
     * Sets the object attribute value.
     * @param object The object to set.
     */
    public void setObject(ObjectCode object) {
        this.object = object;
    }
    /**
     * Gets the objectChartOfAccounts attribute. 
     * @return Returns the objectChartOfAccounts.
     */
    public Chart getObjectChartOfAccounts() {
        return objectChartOfAccounts;
    }
    /**
     * Sets the objectChartOfAccounts attribute value.
     * @param objectChartOfAccounts The objectChartOfAccounts to set.
     */
    public void setObjectChartOfAccounts(Chart objectChartOfAccounts) {
        this.objectChartOfAccounts = objectChartOfAccounts;
    }
}
