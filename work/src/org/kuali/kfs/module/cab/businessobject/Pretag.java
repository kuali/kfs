/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cab.businessobject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Pretag extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String purchaseOrderNumber;
    private Integer itemLineNumber;
    private KualiDecimal quantityInvoiced;
    private String capitalAssetTypeCode;
    private String manufacturerName;
    private String manufacturerModelNumber;
    private String vendorName;
    private String assetTopsDescription;
    private String organizationText;
    private String organizationInventoryName;
    private String representativeUniversalIdentifier;
    private String chartOfAccountsCode;
    private Date pretagCreateDate;
    private String organizationCode;
    private boolean active;

    private Chart chartOfAccounts;
    private Organization organization;
    private AssetType capitalAssetType;
    private Person personUniversal;

    private List<String> campusTagNumbers;
    private List<String> serialNumbers;

    private List<PretagDetail> pretagDetails;


    /**
     * Default constructor.
     */
    public Pretag() {
        pretagDetails = new ArrayList<PretagDetail>();
    }

    /**
     * Gets the purchaseOrderNumber attribute.
     * 
     * @return Returns the purchaseOrderNumber
     */
    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    /**
     * Sets the purchaseOrderNumber attribute.
     * 
     * @param purchaseOrderNumber The purchaseOrderNumber to set.
     */
    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }


    /**
     * Gets the itemLineNumber attribute.
     * 
     * @return Returns the itemLineNumber.
     */
    public Integer getItemLineNumber() {
        return itemLineNumber;
    }

    /**
     * Sets the itemLineNumber attribute value.
     * 
     * @param itemLineNumber The itemLineNumber to set.
     */
    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }

    /**
     * Gets the quantityInvoiced attribute.
     * 
     * @return Returns the quantityInvoiced
     */
    public KualiDecimal getQuantityInvoiced() {
        return quantityInvoiced;
    }

    /**
     * Sets the quantityInvoiced attribute.
     * 
     * @param quantityInvoiced The quantityInvoiced to set.
     */
    public void setQuantityInvoiced(KualiDecimal quantityInvoiced) {
        this.quantityInvoiced = quantityInvoiced;
    }


    /**
     * Gets the capitalAssetTypeCode attribute.
     * 
     * @return Returns the capitalAssetTypeCode
     */
    public String getCapitalAssetTypeCode() {
        return capitalAssetTypeCode;
    }

    /**
     * Sets the capitalAssetTypeCode attribute.
     * 
     * @param capitalAssetTypeCode The capitalAssetTypeCode to set.
     */
    public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
        this.capitalAssetTypeCode = capitalAssetTypeCode;
    }


    /**
     * Gets the manufacturerName attribute.
     * 
     * @return Returns the manufacturerName
     */
    public String getManufacturerName() {
        return manufacturerName;
    }

    /**
     * Sets the manufacturerName attribute.
     * 
     * @param manufacturerName The manufacturerName to set.
     */
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }


    /**
     * Gets the manufacturerModelNumber attribute.
     * 
     * @return Returns the manufacturerModelNumber
     */
    public String getManufacturerModelNumber() {
        return manufacturerModelNumber;
    }

    /**
     * Sets the manufacturerModelNumber attribute.
     * 
     * @param manufacturerModelNumber The manufacturerModelNumber to set.
     */
    public void setManufacturerModelNumber(String manufacturerModelNumber) {
        this.manufacturerModelNumber = manufacturerModelNumber;
    }


    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the vendorName attribute.
     * 
     * @param vendorName The vendorName to set.
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }


    /**
     * Gets the assetTopsDescription attribute.
     * 
     * @return Returns the assetTopsDescription
     */
    public String getAssetTopsDescription() {
        return assetTopsDescription;
    }

    /**
     * Sets the assetTopsDescription attribute.
     * 
     * @param assetTopsDescription The assetTopsDescription to set.
     */
    public void setAssetTopsDescription(String assetTopsDescription) {
        this.assetTopsDescription = assetTopsDescription;
    }

    /**
     * Gets the organizationText attribute.
     * 
     * @return Returns the organizationText
     */
    public String getOrganizationText() {
        return organizationText;
    }

    /**
     * Sets the organizationText attribute.
     * 
     * @param organizationText The organizationText to set.
     */
    public void setOrganizationText(String organizationText) {
        this.organizationText = organizationText;
    }


    /**
     * Gets the organizationInventoryName attribute.
     * 
     * @return Returns the organizationInventoryName
     */
    public String getOrganizationInventoryName() {
        return organizationInventoryName;
    }

    /**
     * Sets the organizationInventoryName attribute.
     * 
     * @param organizationInventoryName The organizationInventoryName to set.
     */
    public void setOrganizationInventoryName(String organizationInventoryName) {
        this.organizationInventoryName = organizationInventoryName;
    }

    public Person getPersonUniversal() {
        personUniversal = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(representativeUniversalIdentifier, personUniversal);
        return personUniversal;
    }

    public void setPersonUniversal(Person personUniversal) {
        this.personUniversal = personUniversal;
    }

    /**
     * Gets the representativeUniversalIdentifier attribute.
     * 
     * @return Returns the representativeUniversalIdentifier
     */
    public String getRepresentativeUniversalIdentifier() {
        return representativeUniversalIdentifier;
    }

    /**
     * Sets the representativeUniversalIdentifier attribute.
     * 
     * @param representativeUniversalIdentifier The representativeUniversalIdentifier to set.
     */
    public void setRepresentativeUniversalIdentifier(String representativeUniversalIdentifier) {
        this.representativeUniversalIdentifier = representativeUniversalIdentifier;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the pretagCreateDate attribute.
     * 
     * @return Returns the pretagCreateDate
     */
    public Date getPretagCreateDate() {
        return pretagCreateDate;
    }

    /**
     * Sets the pretagCreateDate attribute.
     * 
     * @param pretagCreateDate The pretagCreateDate to set.
     */
    public void setPretagCreateDate(Date pretagCreateDate) {
        this.pretagCreateDate = pretagCreateDate;
    }


    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the pretagDetails attribute.
     * 
     * @return Returns the pretagDetails.
     */
    public List<PretagDetail> getPretagDetails() {
        return pretagDetails;
    }

    /**
     * Sets the pretagDetails attribute value.
     * 
     * @param pretagDetails The pretagDetails to set.
     */
    public void setPretagDetails(List<PretagDetail> pretagDetails) {
        this.pretagDetails = pretagDetails;
    }

    /**
     * Gets the capitalAssetType attribute.
     * 
     * @return Returns the capitalAssetType.
     */
    public AssetType getCapitalAssetType() {
        return capitalAssetType;
    }

    /**
     * Sets the capitalAssetType attribute value.
     * 
     * @param capitalAssetType The capitalAssetType to set.
     * @deprecated
     */
    public void setCapitalAssetType(AssetType capitalAssetType) {
        this.capitalAssetType = capitalAssetType;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("purchaseOrderNumber", this.purchaseOrderNumber);
        if (this.itemLineNumber != null) {
            m.put("itemLineNumber", this.itemLineNumber.toString());
        }
        return m;
    }

    /**
     * Gets the campusTagNumbers attribute.
     * 
     * @return Returns the campusTagNumbers.
     */
    public List<String> getCampusTagNumbers() {
        List<String> campusTagNumbers = new ArrayList<String>();
        if (pretagDetails != null) {
            for (PretagDetail pretagDetail : pretagDetails) {
                campusTagNumbers.add(pretagDetail.getCampusTagNumber());
            }
        }
        return campusTagNumbers;
    }

    /**
     * Sets the campusTagNumbers attribute value.
     * 
     * @param campusTagNumbers The campusTagNumbers to set.
     * @deprecated
     */
    public void setCampusTagNumbers(List<String> campusTagNumbers) {
        this.campusTagNumbers = campusTagNumbers;
    }

    /**
     * Gets the serialNumbers attribute.
     * 
     * @return Returns the serialNumbers.
     */
    public List<String> getSerialNumbers() {
        List<String> serialNumbers = new ArrayList<String>();
        if (pretagDetails != null) {
            for (PretagDetail pretagDetail : pretagDetails) {
                serialNumbers.add(pretagDetail.getSerialNumber());
            }
        }
        return serialNumbers;
    }

    /**
     * Sets the serialNumbers attribute value.
     * 
     * @param serialNumbers The serialNumbers to set.
     * @deprecated
     */
    public void setSerialNumbers(List<String> serialNumbers) {
        this.serialNumbers = serialNumbers;
    }

}
