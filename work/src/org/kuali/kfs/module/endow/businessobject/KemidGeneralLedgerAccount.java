/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
