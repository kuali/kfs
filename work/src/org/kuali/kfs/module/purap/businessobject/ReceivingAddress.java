/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;

/**
 * Receiving Address Business Object. 
 * Used when an institution has products shipped by vendors to their "central receiving" organziation, 
 * which will then deliever the products to the final delivery addresses.
 * ReceivingAddress defines all the required address fields as well as an indicator to decide whether the 
 * receiving address or the final delivery address will be used as the shipping address provided to a vendor.
 */
public class ReceivingAddress extends PersistableBusinessObjectBase {

    private Integer receivingAddressIdentifier;
    private String chartOfAccountsCode;    
    private String organizationCode;
    private String receivingName;
    private String receivingLine1Address;
    private String receivingLine2Address;
    private String receivingCityName;
    private String receivingStateCode;
    private String receivingPostalCode;
    private String receivingCountryCode;
    private boolean useReceivingIndicator;
    private boolean defaultIndicator;
    private boolean active;

    private Chart chartOfAccounts;
    private Org organization;

    /**
     * Default constructor.
     */
    public ReceivingAddress() {
    }
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public boolean isDefaultIndicator() {
        return defaultIndicator;
    }

    public void setDefaultIndicator(boolean defaultIndicator) {
        this.defaultIndicator = defaultIndicator;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public Integer getReceivingAddressIdentifier() {
        return receivingAddressIdentifier;
    }

    public void setReceivingAddressIdentifier(Integer receivingAddressIdentifier) {
        this.receivingAddressIdentifier = receivingAddressIdentifier;
    }

    public String getReceivingCityName() {
        return receivingCityName;
    }

    public void setReceivingCityName(String receivingCityName) {
        this.receivingCityName = receivingCityName;
    }

    public String getReceivingCountryCode() {
        return receivingCountryCode;
    }

    public void setReceivingCountryCode(String receivingCountryCode) {
        this.receivingCountryCode = receivingCountryCode;
    }

    public String getReceivingLine1Address() {
        return receivingLine1Address;
    }

    public void setReceivingLine1Address(String receivingLine1Address) {
        this.receivingLine1Address = receivingLine1Address;
    }

    public String getReceivingLine2Address() {
        return receivingLine2Address;
    }

    public void setReceivingLine2Address(String receivingLine2Address) {
        this.receivingLine2Address = receivingLine2Address;
    }

    public String getReceivingName() {
        return receivingName;
    }

    public void setReceivingName(String receivingName) {
        this.receivingName = receivingName;
    }

    public String getReceivingPostalCode() {
        return receivingPostalCode;
    }

    public void setReceivingPostalCode(String receivingPostalCode) {
        this.receivingPostalCode = receivingPostalCode;
    }

    public String getReceivingStateCode() {
        return receivingStateCode;
    }

    public void setReceivingStateCode(String receivingStateCode) {
        this.receivingStateCode = receivingStateCode;
    }

    public boolean isUseReceivingIndicator() {
        return useReceivingIndicator;
    }

    public void setUseReceivingIndicator(boolean useReceivingIndicator) {
        this.useReceivingIndicator = useReceivingIndicator;
    }
    
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    public Org getOrganization() {
        return organization;
    }

    /**
     * @deprecated
     */
    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        return m;
    }
}
