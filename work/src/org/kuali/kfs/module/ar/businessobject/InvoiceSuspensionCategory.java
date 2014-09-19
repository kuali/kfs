/*
 * Copyright 2011 The Kuali Foundation.
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
        m.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, getDocumentNumber());
        m.put("suspensionCategoryCode", getSuspensionCategoryCode());
        return m;
    }
}
