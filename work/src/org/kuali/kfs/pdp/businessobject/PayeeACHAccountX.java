/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.pdp.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiInteger;

public class PayeeACHAccountX extends PersistableBusinessObjectBase implements Inactivateable {

    private KualiInteger achAccountGeneratedIdentifier;
    private String bankRoutingNumber;
    private String bankAccountNumber;
    private String payeeName;
    private String payeeEmailAddress;
    private KualiInteger vendorHeaderGeneratedIdentifier;
    private KualiInteger vendorDetailAssignedIdentifier;
    private String principalId;
    private String payeeIdentifierTypeCode;
    private String achTransactionType;
    private boolean active;
    private String bankAccountTypeCode;

    private ACHBank bankRouting;
    private VendorDetail vendorDetail;
    private Person user;
    private ACHTransactionType transactionType;

    /**
     * Default constructor.
     */
    public PayeeACHAccountX() {

    }

    /**
     * Gets the achAccountGeneratedIdentifier attribute.
     * 
     * @return Returns the achAccountGeneratedIdentifier
     */
    public KualiInteger getAchAccountGeneratedIdentifier() {
        return achAccountGeneratedIdentifier;
    }

    /**
     * Sets the achAccountGeneratedIdentifier attribute.
     * 
     * @param achAccountGeneratedIdentifier The achAccountGeneratedIdentifier to set.
     */
    public void setAchAccountGeneratedIdentifier(KualiInteger achAccountGeneratedIdentifier) {
        this.achAccountGeneratedIdentifier = achAccountGeneratedIdentifier;
    }


    /**
     * Gets the bankRoutingNumber attribute.
     * 
     * @return Returns the bankRoutingNumber
     */
    public String getBankRoutingNumber() {
        return bankRoutingNumber;
    }

    /**
     * Sets the bankRoutingNumber attribute.
     * 
     * @param bankRoutingNumber The bankRoutingNumber to set.
     */
    public void setBankRoutingNumber(String bankRoutingNumber) {
        this.bankRoutingNumber = bankRoutingNumber;
    }


    /**
     * Gets the bankAccountNumber attribute.
     * 
     * @return Returns the bankAccountNumber
     */
    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    /**
     * Sets the bankAccountNumber attribute.
     * 
     * @param bankAccountNumber The bankAccountNumber to set.
     */
    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }


    /**
     * Gets the payeeName attribute.
     * 
     * @return Returns the payeeName
     */
    public String getPayeeName() {
        return payeeName;
    }

    /**
     * Sets the payeeName attribute.
     * 
     * @param payeeName The payeeName to set.
     */
    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }


    /**
     * Gets the payeeEmailAddress attribute.
     * 
     * @return Returns the payeeEmailAddress
     */
    public String getPayeeEmailAddress() {
        return payeeEmailAddress;
    }

    /**
     * Sets the payeeEmailAddress attribute.
     * 
     * @param payeeEmailAddress The payeeEmailAddress to set.
     */
    public void setPayeeEmailAddress(String payeeEmailAddress) {
        this.payeeEmailAddress = payeeEmailAddress;
    }


    /**
     * Gets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @return Returns the vendorHeaderGeneratedIdentifier
     */
    public KualiInteger getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    /**
     * Sets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
     */
    public void setVendorHeaderGeneratedIdentifier(KualiInteger vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }


    /**
     * Gets the vendorDetailAssignedIdentifier attribute.
     * 
     * @return Returns the vendorDetailAssignedIdentifier
     */
    public KualiInteger getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    /**
     * Sets the vendorDetailAssignedIdentifier attribute.
     * 
     * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
     */
    public void setVendorDetailAssignedIdentifier(KualiInteger vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * Gets the payeeIdentifierTypeCode attribute.
     * 
     * @return Returns the payeeIdentifierTypeCode
     */
    public String getPayeeIdentifierTypeCode() {
        return payeeIdentifierTypeCode;
    }

    /**
     * Sets the payeeIdentifierTypeCode attribute.
     * 
     * @param payeeIdentifierTypeCode The payeeIdentifierTypeCode to set.
     */
    public void setPayeeIdentifierTypeCode(String payeeIdentifierTypeCode) {
        this.payeeIdentifierTypeCode = payeeIdentifierTypeCode;
    }

    /**
     * Gets the achTransactionType attribute.
     * 
     * @return Returns the achTransactionType.
     */
    public String getAchTransactionType() {
        return achTransactionType;
    }

    /**
     * Sets the achTransactionType attribute value.
     * 
     * @param achTransactionType The achTransactionType to set.
     */
    public void setAchTransactionType(String achTransactionType) {
        this.achTransactionType = achTransactionType;
    }

    /**
     * Gets the transactionType attribute.
     * 
     * @return Returns the transactionType.
     */
    public ACHTransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * Sets the transactionType attribute value.
     * 
     * @param transactionType The transactionType to set.
     */
    public void setTransactionType(ACHTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the bankAccountTypeCode attribute.
     * 
     * @return Returns the bankAccountTypeCode.
     */
    public String getBankAccountTypeCode() {
        return bankAccountTypeCode;
    }

    /**
     * Sets the bankAccountTypeCode attribute value.
     * 
     * @param bankAccountTypeCode The bankAccountTypeCode to set.
     */
    public void setBankAccountTypeCode(String bankAccountTypeCode) {
        this.bankAccountTypeCode = bankAccountTypeCode;
    }

    /**
     * Gets the bankRouting attribute.
     * 
     * @return Returns the bankRouting.
     */
    public ACHBank getBankRouting() {
        return bankRouting;
    }

    /**
     * Sets the bankRouting attribute value.
     * 
     * @param bankRouting The bankRouting to set.
     * @deprecated
     */
    public void setBankRouting(ACHBank bankRouting) {
        this.bankRouting = bankRouting;
    }

    /**
     * Gets the user attribute.
     * 
     * @return Returns the user.
     */
    public Person getUser() {
        user = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(principalId, user);
        return user;
    }

    /**
     * Sets the user attribute value.
     * 
     * @param user The user to set.
     */
    public void setUser(Person user) {
        this.user = user;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.achAccountGeneratedIdentifier != null) {
            m.put(PdpPropertyConstants.ACH_ACCOUNT_GENERATED_IDENTIFIER, this.achAccountGeneratedIdentifier.toString());
        }
        return m;
    }

    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    public String getVendorNumber() {
        // using the code from the VendorDetail to generate the vendor number
        if (vendorHeaderGeneratedIdentifier != null && vendorDetailAssignedIdentifier != null) {
            VendorDetail vDUtil = new VendorDetail();
            vDUtil.setVendorHeaderGeneratedIdentifier(vendorHeaderGeneratedIdentifier.intValue());
            vDUtil.setVendorDetailAssignedIdentifier(vendorDetailAssignedIdentifier.intValue());
            return vDUtil.getVendorNumber();
        }

        return "";
    }

    public void setVendorNumber(String vendorNumber) {
        // using the code from the VendorDetail to set the 2 component fields of the vendor number
        if (StringUtils.isNotBlank(vendorNumber)) {
            VendorDetail vDUtil = new VendorDetail();
            vDUtil.setVendorNumber(vendorNumber);
            setVendorHeaderGeneratedIdentifier(new KualiInteger(vDUtil.getVendorHeaderGeneratedIdentifier()));
            setVendorDetailAssignedIdentifier(new KualiInteger(vDUtil.getVendorDetailAssignedIdentifier()));
        }
    }
}
