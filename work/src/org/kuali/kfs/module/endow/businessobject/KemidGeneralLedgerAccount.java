/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This KemidGeneralLedgerAccount class provides the general ledger account to which transaction information will be interfaced.
 * Also controls etran code use.
 */
public class KemidGeneralLedgerAccount extends PersistableBusinessObjectBase implements MutableInactivatable{

    private String kemid;
    private String incomePrincipalIndicatorCode;
    private String chartCode;
    private String accountNumber;

    private KEMID kemidObjRef;
    private IncomePrincipalIndicator incomePrincipalIndicator;
    private Chart chart;
    private Account account;
    private boolean active;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        m.put(EndowPropertyConstants.KEMID_GL_ACCOUNT_IP_INDICATOR_CD, this.incomePrincipalIndicatorCode);
        return m;
    }

    /**
     * Gets the accountNumber.
     * 
     * @return accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the chartCode.
     * 
     * @return chartCode
     */
    public String getChartCode() {
        return chartCode;
    }

    /**
     * Sets the chartCode.
     * 
     * @param chartCode
     */
    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
    }

    /**
     * Gets the incomePrincipalIndicator.
     * 
     * @return incomePrincipalIndicator
     */
    public IncomePrincipalIndicator getIncomePrincipalIndicator() {
        return incomePrincipalIndicator;
    }

    /**
     * Sets the incomePrincipalIndicator.
     * 
     * @param incomePrincipalIndicator
     */
    public void setIncomePrincipalIndicator(IncomePrincipalIndicator incomePrincipalIndicator) {
        this.incomePrincipalIndicator = incomePrincipalIndicator;
    }

    /**
     * Gets the incomePrincipalIndicatorCode.
     * 
     * @return incomePrincipalIndicatorCode
     */
    public String getIncomePrincipalIndicatorCode() {
        return incomePrincipalIndicatorCode;
    }

    /**
     * Sets the incomePrincipalIndicatorCode.
     * 
     * @param incomePrincipalIndicatorCode
     */
    public void setIncomePrincipalIndicatorCode(String incomePrincipalIndicatorCode) {
        this.incomePrincipalIndicatorCode = incomePrincipalIndicatorCode;
    }

    /**
     * Gets the kemid.
     * 
     * @return kemid
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * Sets the kemid.
     * 
     * @param kemid
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * Gets the kemidObjRef.
     * 
     * @return kemidObjRef
     */
    public KEMID getKemidObjRef() {
        return kemidObjRef;
    }

    /**
     * Sets the kemidObjRef.
     * 
     * @param kemidObjRef
     */
    public void setKemidObjRef(KEMID kemidObjRef) {
        this.kemidObjRef = kemidObjRef;
    }

    /**
     * Gets the account.
     * 
     * @return account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account.
     * 
     * @param account
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the chart.
     * 
     * @return chart
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart.
     * 
     * @param chart
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }
    
    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }

}
