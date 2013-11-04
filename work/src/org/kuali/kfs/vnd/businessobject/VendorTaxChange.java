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

package org.kuali.kfs.vnd.businessobject;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Records any changes to a Vendor's Tax Number or Type. Not shown on the screen.
 */
public class VendorTaxChange extends PersistableBusinessObjectBase {

    private Integer vendorTaxChangeGeneratedIdentifier;
    private Integer vendorHeaderGeneratedIdentifier;
    private Timestamp vendorTaxChangeTimestamp;
    private String vendorPreviousTaxNumber;
    private String vendorPreviousTaxTypeCode;
    private String vendorTaxChangePersonIdentifier;

    private Person vendorTaxChangePerson;
    private VendorHeader vendorHeader;

    /**
     * Default constructor.
     */
    public VendorTaxChange() {
    }

    /**
     * Constructs a VendorTaxChange.
     *
     * @param vndrHdrGenId The generated Id of the Vendor Header
     * @param taxChangeDate The date of this change
     * @param prevTaxNum The tax number previously
     * @param prevTaxTypeCode The tax type previously
     * @param taxChangePersonId The Id of the user who is making this change
     */
    public VendorTaxChange(Integer vndrHdrGenId, Timestamp taxChangeTimestamp, String prevTaxNum, String prevTaxTypeCode, String taxChangePersonId) {
        this.vendorHeaderGeneratedIdentifier = vndrHdrGenId;
        this.vendorTaxChangeTimestamp = taxChangeTimestamp;
        this.vendorPreviousTaxNumber = prevTaxNum;
        this.vendorPreviousTaxTypeCode = prevTaxTypeCode;
        this.vendorTaxChangePersonIdentifier = taxChangePersonId;
    }

    public Integer getVendorTaxChangeGeneratedIdentifier() {
        return vendorTaxChangeGeneratedIdentifier;
    }

    public void setVendorTaxChangeGeneratedIdentifier(Integer vendorTaxChangeGeneratedIdentifier) {
        this.vendorTaxChangeGeneratedIdentifier = vendorTaxChangeGeneratedIdentifier;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    public Timestamp getVendorTaxChangeTimestamp() {
        return vendorTaxChangeTimestamp;
    }

    public void setVendorTaxChangeTimestamp(Timestamp vendorTaxChangeTimestamp) {
        this.vendorTaxChangeTimestamp = vendorTaxChangeTimestamp;
    }

    public String getVendorPreviousTaxNumber() {
        return vendorPreviousTaxNumber;
    }

    public void setVendorPreviousTaxNumber(String vendorPreviousTaxNumber) {
        this.vendorPreviousTaxNumber = vendorPreviousTaxNumber;
    }

    public String getVendorPreviousTaxTypeCode() {
        return vendorPreviousTaxTypeCode;
    }

    public void setVendorPreviousTaxTypeCode(String vendorPreviousTaxTypeCode) {
        this.vendorPreviousTaxTypeCode = vendorPreviousTaxTypeCode;
    }

    public String getVendorTaxChangePersonIdentifier() {
        return vendorTaxChangePersonIdentifier;
    }

    public void setVendorTaxChangePersonIdentifier(String vendorTaxChangePersonIdentifier) {
        this.vendorTaxChangePersonIdentifier = vendorTaxChangePersonIdentifier;
    }

    public Person getVendorTaxChangePerson() {
        vendorTaxChangePerson = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(vendorTaxChangePersonIdentifier, vendorTaxChangePerson);
        return vendorTaxChangePerson;
    }

    /**
     * Sets the vendorTaxChangePerson attribute.
     *
     * @param vendorTaxChangePerson The vendorTaxChangePerson to set.
     * @deprecated
     */
    @Deprecated
    public void setVendorTaxChangePerson(Person vendorTaxChangePerson) {
        this.vendorTaxChangePerson = vendorTaxChangePerson;
    }

    public VendorHeader getVendorHeader() {
        return vendorHeader;
    }

    /**
     * Sets the vendorHeader attribute value.
     *
     * @param vendorHeader The vendorHeader to set.
     * @deprecated
     */
    @Deprecated
    public void setVendorHeader(VendorHeader vendorHeader) {
        this.vendorHeader = vendorHeader;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.vendorTaxChangeGeneratedIdentifier != null) {
            m.put("vendorTaxChangeGeneratedIdentifier", this.vendorTaxChangeGeneratedIdentifier.toString());
        }
        return m;
    }

    @Override
    public String toString() {
        class VendorTaxChangeToStringBuilder extends ReflectionToStringBuilder {
            private VendorTaxChangeToStringBuilder(Object object) {
                super(object);
            }

            @Override
            public boolean accept(Field field) {
                if (BusinessObject.class.isAssignableFrom(field.getType())) {
                    return false;
                }

                DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
                AttributeSecurity attributeSecurity = dataDictionaryService.getAttributeSecurity(VendorTaxChange.class.getName(), field.getName());
                if (ObjectUtils.isNotNull(attributeSecurity)
                                && (attributeSecurity.isHide() || attributeSecurity.isMask() || attributeSecurity.isPartialMask())) {
                    return false;
                }

                return super.accept(field);
            }
        };
        ReflectionToStringBuilder toStringBuilder = new VendorTaxChangeToStringBuilder(this);
        return toStringBuilder.toString();
    }

}

