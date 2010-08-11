/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * Business Object for Holding Tax Lot table
 */
public class HoldingTaxLot extends PersistableBusinessObjectBase {

    private String kemid;
    private String securityId;
    private String registrationCode;
    private KualiInteger lotNumber;
    private String incomePrincipalIndicator;
    private Date acquiredDate;
    private BigDecimal units;
    private BigDecimal cost;
    private BigDecimal currentAccrual;
    private BigDecimal priorAccrual;
    private BigDecimal foreignTaxWithheld;
    private Date lastTransactionDate;

    private KEMID kemidObj;
    private Security security;
    private IncomePrincipalIndicator incomePrincipal;
    private RegistrationCode registration;

    /**
     * Constructs a HoldingTaxLot.java.
     */
    public HoldingTaxLot() {
        super();
    }

    /**
     * Gets the incomePrincipal.
     * 
     * @return incomePrincipal
     */
    public IncomePrincipalIndicator getIncomePrincipal() {
        return incomePrincipal;
    }

    /**
     * Sets the incomePrincipal.
     * 
     * @param incomePrincipal
     */
    public void setIncomePrincipal(IncomePrincipalIndicator incomePrincipal) {
        this.incomePrincipal = incomePrincipal;
    }

    /**
     * Gets the security
     * 
     * @return security
     */
    public Security getSecurity() {
        return security;
    }

    /**
     * Sets the security.
     * 
     * @param security
     */
    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, this.kemid);
        m.put(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, this.securityId);
        m.put(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE, this.registrationCode);
        m.put(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR, this.incomePrincipalIndicator);
        return m;
    }

    /**
     * Gets the acquiredDate.
     * 
     * @return acquiredDate
     */
    public Date getAcquiredDate() {
        return acquiredDate;
    }

    /**
     * Sets the acquiredDate.
     * 
     * @param acquiredDate
     */
    public void setAcquiredDate(Date acquiredDate) {
        this.acquiredDate = acquiredDate;
    }

    /**
     * Gets the cost.
     * 
     * @return cost
     */
    public BigDecimal getCost() {
        return cost;
    }

    /**
     * Sets the cost.
     * 
     * @param cost
     */
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    /**
     * Gets the currentAccrual.
     * 
     * @return currentAccrual
     */
    public BigDecimal getCurrentAccrual() {
        return currentAccrual;
    }

    /**
     * Sets the currentAccrual.
     * 
     * @param currentAccrual
     */
    public void setCurrentAccrual(BigDecimal currentAccrual) {
        this.currentAccrual = currentAccrual;
    }

    /**
     * Gets the foreignTaxWithheld.
     * 
     * @return foreignTaxWithheld
     */
    public BigDecimal getForeignTaxWithheld() {
        return foreignTaxWithheld;
    }

    /**
     * Sets the foreignTaxWithheld.
     * 
     * @param foreignTaxWithheld
     */
    public void setForeignTaxWithheld(BigDecimal foreignTaxWithheld) {
        this.foreignTaxWithheld = foreignTaxWithheld;
    }

    /**
     * Gets the incomePrincipalIndicator.
     * 
     * @return incomePrincipalIndicator
     */
    public String getIncomePrincipalIndicator() {
        return incomePrincipalIndicator;
    }

    /**
     * Sets the incomePrincipalIndicator.
     * 
     * @param incomePrincipalIndicator
     */
    public void setIncomePrincipalIndicator(String incomePrincipalIndicator) {
        this.incomePrincipalIndicator = incomePrincipalIndicator;
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
     * Gets the lastTransactionDate.
     * 
     * @return lastTransactionDate
     */
    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    /**
     * Sets the lastTransactionDate.
     * 
     * @param lastTransactionDate
     */
    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    /**
     * Gets the lotNumber.
     * 
     * @return lotNumber
     */
    public KualiInteger getLotNumber() {
        return lotNumber;
    }

    /**
     * Sets the lotNumber.
     * 
     * @param lotNumber
     */
    public void setLotNumber(KualiInteger lotNumber) {
        this.lotNumber = lotNumber;
    }

    /**
     * Gets the priorAccrual.
     * 
     * @return priorAccrual
     */
    public BigDecimal getPriorAccrual() {
        return priorAccrual;
    }

    /**
     * Sets the priorAccrual.
     * 
     * @param priorAccrual
     */
    public void setPriorAccrual(BigDecimal priorAccrual) {
        this.priorAccrual = priorAccrual;
    }

    /**
     * Gets the registrationCode.
     * 
     * @return registrationCode
     */
    public String getRegistrationCode() {
        return registrationCode;
    }

    /**
     * Sets the registrationCode.
     * 
     * @param registrationCode
     */
    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    /**
     * Gets the securityId.
     * 
     * @return securityId
     */
    public String getSecurityId() {
        return securityId;
    }

    /**
     * Sets the securityId.
     * 
     * @param securityId
     */
    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    /**
     * Gets the units.
     * 
     * @return units
     */
    public BigDecimal getUnits() {
        return units;
    }

    /**
     * Sets the units.
     * 
     * @param units
     */
    public void setUnits(BigDecimal units) {
        this.units = units;
    }

    /**
     * Gets the market value.
     * 
     * @return the market value
     */
    public BigDecimal getMarketValue() {

        KEMService kemService = SpringContext.getBean(KEMService.class);
        BigDecimal marketValue = kemService.getMarketValue(this.getSecurityId());

        return marketValue;
    }

    /**
     * Gets the Balance Date which is the Current System/Process date
     * 
     * @return the Balance Date
     */
    public Date getBalanceDate() {

        return SpringContext.getBean(KEMService.class).getCurrentDate();
    }

    /**
     * Gets the registration.
     * 
     * @return registration
     */
    public RegistrationCode getRegistration() {
        return registration;
    }

    /**
     * Sets the registration
     * 
     * @param registration
     */
    public void setRegistration(RegistrationCode registration) {
        this.registration = registration;
    }

    /**
     * Gets the kemidObj.
     * 
     * @return kemidObj
     */
    public KEMID getKemidObj() {
        return kemidObj;
    }

    /**
     * Sets the kemidObj.
     * 
     * @param kemidObj
     */
    @Deprecated
    public void setKemidObj(KEMID kemidObj) {
        this.kemidObj = kemidObj;
    }
}
