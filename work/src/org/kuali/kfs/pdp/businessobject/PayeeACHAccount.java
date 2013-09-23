/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.pdp.businessobject;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.kuali.kfs.pdp.PdpConstants.PayeeIdTypeCodes;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.ObjectUtils;

public class PayeeACHAccount extends PersistableBusinessObjectBase implements MutableInactivatable {

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
     * Gets the payee's name from KIM or Vendor data, if the payee type is Employee, Entity or Vendor; otherwise returns the stored
     * field value.
     *
     * @return Returns the payee name
     */
    public String getPayeeName() {
        // for Employee, retrieves from Person table by employee ID
        if (StringUtils.equalsIgnoreCase(payeeIdentifierTypeCode, PayeeIdTypeCodes.EMPLOYEE)) {
            if (ObjectUtils.isNotNull(payeeIdNumber)) {
                String name = SpringContext.getBean(FinancialSystemUserService.class).getPersonNameByEmployeeId(payeeIdNumber);

                // Person person = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(payeeIdNumber);
                if (ObjectUtils.isNotNull(name)) {
                    return name;
                }
            }
        }
        // for Entity, retrieve from Entity table by entity ID
        else if (StringUtils.equalsIgnoreCase(payeeIdentifierTypeCode, PayeeIdTypeCodes.ENTITY)) {
            if (ObjectUtils.isNotNull(payeeIdNumber)) {
                EntityDefault entity = KimApiServiceLocator.getIdentityService().getEntityDefault(payeeIdNumber);
                if (ObjectUtils.isNotNull(entity) && ObjectUtils.isNotNull(entity.getName())) {
                    return entity.getName().getCompositeName();
                }
            }
        }
        // for Vendor, retrieves from Vendor table by vendor number
        else if (StringUtils.equalsIgnoreCase(payeeIdentifierTypeCode, PayeeIdTypeCodes.VENDOR_ID)) {
            VendorDetail vendor = SpringContext.getBean(VendorService.class).getVendorDetail(payeeIdNumber);
            if (ObjectUtils.isNotNull(vendor)) {
                return vendor.getVendorName();
            }
        }

        // otherwise return field value
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
     * Gets the payee's email address from KIM data if the payee type is Employee or Entity; otherwise, returns the stored field
     * value.
     *
     * @return Returns the payeeEmailAddress
     */
    public String getPayeeEmailAddress() {
        // for Employee, retrieve from Person table by employee ID
        if (StringUtils.equalsIgnoreCase(payeeIdentifierTypeCode, PayeeIdTypeCodes.EMPLOYEE)) {
            Person person = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(payeeIdNumber);
            if (ObjectUtils.isNotNull(person)) {
                return person.getEmailAddress();
            }
        }
        // for Entity, retrieve from Entity table by entity ID then from Person table
        else if (StringUtils.equalsIgnoreCase(payeeIdentifierTypeCode, PayeeIdTypeCodes.ENTITY)) {
            if (ObjectUtils.isNotNull(payeeIdNumber)) {
                EntityDefault entity = KimApiServiceLocator.getIdentityService().getEntityDefault(payeeIdNumber);
                if (ObjectUtils.isNotNull(entity)) {
                    List<Principal> principals = entity.getPrincipals();
                    if (principals.size() > 0 && ObjectUtils.isNotNull(principals.get(0))) {
                        String principalId = principals.get(0).getPrincipalId();
                        Person person = SpringContext.getBean(PersonService.class).getPerson(principalId);
                        if (ObjectUtils.isNotNull(person)) {
                            return person.getEmailAddress();
                        }
                    }
                }
            }
        }

        // otherwise returns the field value
        return payeeEmailAddress;
    }

    /**
     * Sets the payeeEmailAddress attribute if the payee is not Employee or Entity.
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
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     *
     * @param active The active to set.
     */
    @Override
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
    @Deprecated
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.achAccountGeneratedIdentifier != null) {
            m.put(PdpPropertyConstants.ACH_ACCOUNT_GENERATED_IDENTIFIER, this.achAccountGeneratedIdentifier.toString());
        }
        return m;
    }

    /**
     * KFSCNTRB-1682: Some of the fields contain confidential information
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toString()
     */
    @Override
    public String toString() {
        class PayeeACHAccountToStringBuilder extends ReflectionToStringBuilder {
            private PayeeACHAccountToStringBuilder(Object object) {
                super(object);
            }

            @Override
            public boolean accept(Field field) {
                if (BusinessObject.class.isAssignableFrom(field.getType())) {
                    return false;
                }

                DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
                AttributeSecurity attributeSecurity = dataDictionaryService.getAttributeSecurity(PayeeACHAccount.class.getName(), field.getName());
                if ((ObjectUtils.isNotNull(attributeSecurity)
                        && (attributeSecurity.isHide() || attributeSecurity.isMask() || attributeSecurity.isPartialMask()))) {
                    return false;
                }

                return super.accept(field);
            }
        };
        ReflectionToStringBuilder toStringBuilder = new PayeeACHAccountToStringBuilder(this);
        return toStringBuilder.toString();
    }
}
