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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Business Object for Holding Tax Lot table
 */
public class HoldingTaxLotRebalance extends PersistableBusinessObjectBase {

    // Composite keys:
    private String incomePrincipalIndicator;     
    private String registrationCode;
    private String securityId;
    private String kemid;
    
    // Other fields:
    private KualiInteger totalLotNumber;
    private BigDecimal   totalUnits;
    private BigDecimal   totalCost;

    // Reference objects:
    protected IncomePrincipalIndicator incomePrincipal;
    protected RegistrationCode         registration;
    protected Security                 security;
    protected KEMID                    kemidObj;
    
    // Collections:
    protected List<HoldingTaxLot> holdingTaxLots;
    
    /**
     * Constructor.
     */
    public HoldingTaxLotRebalance()
    {
        holdingTaxLots = new ArrayList<HoldingTaxLot>();
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        m.put(EndowPropertyConstants.HOLDING_TAX_LOT_REBAL_SECURITY_ID, this.securityId);
        m.put(EndowPropertyConstants.HOLDING_TAX_LOT_REBAL_REGISTRATION_CODE, this.registrationCode);
        m.put(EndowPropertyConstants.HOLDING_TAX_LOT_REBAL_INCOME_PRINCIPAL_INDICATOR, this.incomePrincipalIndicator);
        
        return m;
    }
    
    /**
     * Gets the registrationCode attribute. 
     * @return Returns the registrationCode.
     */
    public String getRegistrationCode() {
        return registrationCode;
    }

    /**
     * Sets the registrationCode attribute value.
     * @param registrationCode The registrationCode to set.
     */
    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    /**
     * Gets the securityId attribute. 
     * @return Returns the securityId.
     */
    public String getSecurityId() {
        return securityId;
    }

    /**
     * Sets the securityId attribute value.
     * @param securityId The securityId to set.
     */
    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    /**
     * Gets the kemid attribute. 
     * @return Returns the kemid.
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * Sets the kemid attribute value.
     * @param kemid The kemid to set.
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * Gets the incomePrincipalIndicator attribute. 
     * @return Returns the incomePrincipalIndicator.
     */
    public String getIncomePrincipalIndicator() {
        return incomePrincipalIndicator;
    }

    /**
     * Sets the incomePrincipalIndicator attribute value.
     * @param incomePrincipalIndicator The incomePrincipalIndicator to set.
     */
    public void setIncomePrincipalIndicator(String incomePrincipalIndicator) {
        this.incomePrincipalIndicator = incomePrincipalIndicator;
    }

    /**
     * Gets the totalLotNumber attribute. 
     * @return Returns the totalLotNumber.
     */
    public KualiInteger getTotalLotNumber() {
        return totalLotNumber;
    }

    /**
     * Sets the totalLotNumber attribute value.
     * @param totalLotNumber The totalLotNumber to set.
     */
    public void setTotalLotNumber(KualiInteger totalLotNumber) {
        this.totalLotNumber = totalLotNumber;
    }

    /**
     * Gets the totalUnits attribute. 
     * @return Returns the totalUnits.
     */
    public BigDecimal getTotalUnits() {
        return totalUnits;
    }

    /**
     * Sets the totalUnits attribute value.
     * @param totalUnits The totalUnits to set.
     */
    public void setTotalUnits(BigDecimal totalUnits) {
        this.totalUnits = totalUnits;
    }

    /**
     * Gets the totalCost attribute. 
     * @return Returns the totalCost.
     */
    public BigDecimal getTotalCost() {
        return totalCost;
    }

    /**
     * Sets the totalCost attribute value.
     * @param totalCost The totalCost to set.
     */
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Gets the incomePrincipal attribute. 
     * @return Returns the incomePrincipal.
     */
    public IncomePrincipalIndicator getIncomePrincipal() {
        return incomePrincipal;
    }

    /**
     * Sets the incomePrincipal attribute value.
     * @param incomePrincipal The incomePrincipal to set.
     */
    public void setIncomePrincipal(IncomePrincipalIndicator incomePrincipal) {
        this.incomePrincipal = incomePrincipal;
    }

    /**
     * Gets the registration attribute. 
     * @return Returns the registration.
     */
    public RegistrationCode getRegistration() {
        return registration;
    }

    /**
     * Sets the registration attribute value.
     * @param registration The registration to set.
     */
    public void setRegistration(RegistrationCode registration) {
        this.registration = registration;
    }

    /**
     * Gets the security attribute. 
     * @return Returns the security.
     */
    public Security getSecurity() {
        return security;
    }

    /**
     * Sets the security attribute value.
     * @param security The security to set.
     */
    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * Gets the kemidObj attribute. 
     * @return Returns the kemidObj.
     */
    public KEMID getKemidObj() {
        return kemidObj;
    }

    /**
     * Sets the kemidObj attribute value.
     * @param kemidObj The kemidObj to set.
     */
    public void setKemidObj(KEMID kemidObj) {
        this.kemidObj = kemidObj;
    }

    /**
     * Gets the holdingTaxLots attribute. 
     * @return Returns the holdingTaxLots.
     */
    public List<HoldingTaxLot> getHoldingTaxLots() {
        return holdingTaxLots;
    }

    /**
     * Sets the holdingTaxLots attribute value.
     * @param holdingTaxLots The holdingTaxLots to set.
     */
    public void setHoldingTaxLots(List<HoldingTaxLot> holdingTaxLots) {
        this.holdingTaxLots = holdingTaxLots;
    }
}
