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

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiInteger;

public class PayeeACHAccount extends PersistableBusinessObjectBase implements Inactivateable {

    private KualiInteger achAccountGeneratedIdentifier;
    private String bankRoutingNumber;
    private String bankAccountNumber;
    private String payeeIdNumber;
    private String payeeName;
    private String payeeEmailAddress;
    private String payeeIdentifierTypeCode;
    private String achTransactionType;
    private String bankAccountTypeCode;
    private boolean active;

    private ACHBank bankRouting;
    private ACHTransactionType transactionType;
    private ACHPayee achPayee;

    /**
     * Default constructor.
     */
    public PayeeACHAccount() {

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
     * Gets the payeeIdNumber attribute.
     * 
     * @return Returns the payeeIdNumber.
     */
    public String getPayeeIdNumber() {
        return payeeIdNumber;
    }

    /**
     * Sets the payeeIdNumber attribute value.
     * 
     * @param payeeIdNumber The payeeIdNumber to set.
     */
    public void setPayeeIdNumber(String payeeIdNumber) {
        this.payeeIdNumber = payeeIdNumber;
    }


    /**
     * Gets the achPayee attribute.
     * 
     * @return Returns the achPayee.
     */
    public ACHPayee getAchPayee() {
        return achPayee;
    }

    /**
     * Sets the achPayee attribute value.
     * 
     * @param achPayee The achPayee to set.
     */
    public void setAchPayee(ACHPayee achPayee) {
        this.achPayee = achPayee;
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
}
