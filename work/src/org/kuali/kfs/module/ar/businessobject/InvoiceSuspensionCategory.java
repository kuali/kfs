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
package org.kuali.kfs.module.ar.businessobject;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class InvoiceSuspensionCategory extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String suspensionCategoryCode;

    public InvoiceSuspensionCategory() {
        super();
    }

    /**
     * Constructor that uses document number and a suspension category code.
     * @param documentNumber
     * @param suspensionCategoryCode
     */
    public InvoiceSuspensionCategory(String documentNumber, String suspensionCategoryCode) {
        this.documentNumber = documentNumber;
        this.suspensionCategoryCode = suspensionCategoryCode;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the suspensionCategoryCode attribute.
     *
     * @return Returns the suspensionCategoryCode
     */
    public String getSuspensionCategoryCode() {
        return suspensionCategoryCode;
    }

    /**
     * Sets the suspensionCategoryCode attribute value.
     *
     * @param suspensionCategoryCode The suspensionCategoryCode to set.
     */
    public void setSuspensionCategoryCode(String suspensionCategoryCode) {
        this.suspensionCategoryCode = suspensionCategoryCode;
    }

    /**
     * Gets the suspensionCategoryDescription attribute.
     *
     * @return Returns the suspensionCategoryDescription
     */
    public String getSuspensionCategoryDescription() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(ArKeyConstants.INVOICE_DOCUMENT_SUSPENSION_CATEGORY + suspensionCategoryCode);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (ObjectUtils.isNotNull(obj)) {
            if (this.getClass().equals(obj.getClass())) {
                InvoiceSuspensionCategory other = (InvoiceSuspensionCategory) obj;
                return (StringUtils.equalsIgnoreCase(this.documentNumber, other.documentNumber) &&
                        StringUtils.equalsIgnoreCase(this.suspensionCategoryCode, other.suspensionCategoryCode));
            }
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ObjectUtil.generateHashCode(this, Arrays.asList(KFSPropertyConstants.DOCUMENT_NUMBER, ArPropertyConstants.SuspensionCategoryReportFields.SUSPENSION_CATEGORY_CODE));
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        m.put("suspensionCategoryCode", getSuspensionCategoryCode());
        return m;
    }
}
