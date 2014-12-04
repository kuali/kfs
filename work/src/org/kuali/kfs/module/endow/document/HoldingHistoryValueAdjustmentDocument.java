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
package org.kuali.kfs.module.endow.document;

import java.math.BigDecimal;

import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityValuationMethod;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.document.Copyable;

/**
 * This is the transactional document that is used to record a modification to the 
 * market value of a record or group of records in the holding history table
 */
public class HoldingHistoryValueAdjustmentDocument extends FinancialSystemTransactionalDocumentBase implements Copyable {

    protected String securityId;
    protected KualiInteger holdingMonthEndDate;
    protected BigDecimal securityUnitValue;
    protected BigDecimal securityMarketValue;
    protected boolean transactionPosted;
    
    protected Security security;
    protected SecurityValuationMethod securityValuation;
    protected MonthEndDate monthEndDate;
    
    public HoldingHistoryValueAdjustmentDocument() {
        super();
    }
    
    /**
     * This method will get securityId
     * @return securityId
     */
    public String getSecurityId() {
        return securityId;
    }

    /**
     * This method will set securityId
     * @param securityId
     */
    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }
    
    /**
     * This method will get holdingMonthEndDate
     * @return holdingMonthEndDate
     */    
    public KualiInteger getHoldingMonthEndDate() {
        return holdingMonthEndDate;
    }

    /**
     * This method will set holdingMonthEndDate
     * @param holdingMonthEndDate
     */
    public void setHoldingMonthEndDate(KualiInteger holdingMonthEndDate) {
        this.holdingMonthEndDate = holdingMonthEndDate;
    }

    /**
     * This method will get securityUnitValue
     * @return securityUnitValue
     */    
    public BigDecimal getSecurityUnitValue() {
        return securityUnitValue;
    }

    /**
     * This method will set securityUnitValue
     * @param securityUnitValue
     */
    public void setSecurityUnitValue(BigDecimal securityUnitValue) {
        this.securityUnitValue = securityUnitValue;
    }

    /**
     * This method will get securityMarketValue
     * @return securityMarketValue
     */    
    public BigDecimal getSecurityMarketValue() {
        return securityMarketValue;
    }

    /**
     * This method will set securityMarketValue
     * @param securityMarketValue
     */
    public void setSecurityMarketValue(BigDecimal securityMarketValue) {
        this.securityMarketValue = securityMarketValue;
    }

    /**
     * Get TransactionPosted
     * @return transactionPosted
     */
    public boolean isTransactionPosted() {
        return transactionPosted;
    }

    /**
     * Set TransactionPosted
     * @param transactionPosted
     */
    public void setTransactionPosted(boolean transactionPosted) {
        this.transactionPosted = transactionPosted;
    }
    
    /**
     * This method will get security
     * @return security
     */    
    public Security getSecurity() {
        return security;
    }

    /**
     * This method will set security
     * @param security
     */    
    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * This method will get securityValuation
     * @return securityValuation
     */    
    public SecurityValuationMethod getSecurityValuation() {
        return securityValuation;
    }

    /**
     * This method will set securityValuation
     * @param securityValuation
     */    
    public void setSecurityValuation(SecurityValuationMethod securityValuation) {
        this.securityValuation = securityValuation;
    }

    /**
     * This method will get monthEndDate
     * @return monthEndDate
     */    
    public MonthEndDate getMonthEndDate() {
        return monthEndDate;
    }

    /**
     * This method will set monthEndDate
     * @param monthEndDate
     */
    public void setMonthEndDate(MonthEndDate monthEndDate) {
        this.monthEndDate = monthEndDate;
    }
    
    /**
     * When document is processed or in the final status, do nothing specific.
     *
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent){
        //override the action defined in EndowmentTransactionalDocumentBase.
        //since we don't need to post any transaction from this document type to
        //the endowment transaction archive tables
    }
}
