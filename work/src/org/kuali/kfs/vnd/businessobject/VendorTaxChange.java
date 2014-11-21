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

