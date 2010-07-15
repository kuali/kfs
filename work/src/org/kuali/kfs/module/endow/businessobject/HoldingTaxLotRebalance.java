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
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * Business Object for Holding Tax Lot table
 */
public class HoldingTaxLotRebalance extends PersistableBusinessObjectBase {

    public final static String INCOME_PRINCIPAL_INDICATOR = "incomePrincipalIndicator";
    public final static String REGISTRATION_CODE = "registrationCode";
    public final static String SECURITY_ID = "securityId";
    public final static String KEMID = "kemid";
    
    // Composite keys:
    private String incomePrincipalIndicator;     
    private String registrationCode;
    private String securityId;
    private String kemid;
    
    // Other fields:
    private BigDecimal totalLotNumber;
    private BigDecimal totalUnits;
    private BigDecimal totalCost;

    // Reference objects:
    private IncomePrincipalIndicator incomePrincipal;
    private RegistrationCode         registration;
    private Security                 security;
    private KEMID                    kemidObj;
    
    // Collections:
    private List<HoldingTaxLot> holdingTaxLots;
    
    /**
     * Constructor.
     */
    public HoldingTaxLotRebalance()
    {
        holdingTaxLots = new TypedArrayList(HoldingTaxLot.class);
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(HoldingTaxLotRebalance.KEMID, this.kemid);
        m.put(HoldingTaxLotRebalance.SECURITY_ID, this.securityId);
        m.put(HoldingTaxLotRebalance.REGISTRATION_CODE, this.registrationCode);
        m.put(HoldingTaxLotRebalance.INCOME_PRINCIPAL_INDICATOR, this.incomePrincipalIndicator);
        
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
    public BigDecimal getTotalLotNumber() {
        return totalLotNumber;
    }

    /**
     * Sets the totalLotNumber attribute value.
     * @param totalLotNumber The totalLotNumber to set.
     */
    public void setTotalLotNumber(BigDecimal totalLotNumber) {
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
        
//        BigDecimal totalUnits = new BigDecimal(0);
//        BigDecimal totalCost  = new BigDecimal(0);
//        
//        if (holdingTaxLots != null) {
//            
//            // Set the total number of tax lots.
//            setTotalLotNumber(new BigDecimal(holdingTaxLots.size()));
//            
//            // Calculate the total units and costs.
//            for (HoldingTaxLot taxLot : holdingTaxLots) {
//                totalUnits.add(taxLot.getUnits());
//                totalCost.add(taxLot.getCost());
//            }
//        }
//        
//        setTotalUnits(totalUnits);
//        setTotalCost(totalCost);
    }
}
