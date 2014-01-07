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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * SuspensionCategory under Contracts and Grants section. Created byFeb 21 2011 Task -666
 */

public class InvoiceSuspensionCategory extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String suspensionCategoryCode;

    private SuspensionCategory suspensionCategory;

    /**
     * Default constructor.
     */
    public InvoiceSuspensionCategory() {
    }

    /**
     * Constructor that uses document number and a suspension category code.
     * @param documentNumber
     * @param suspensionCategoryCode
     */
    public InvoiceSuspensionCategory(String documentNumber, String suspensionCategoryCode) {
        this.documentNumber = documentNumber;
        this.suspensionCategoryCode = suspensionCategoryCode;

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(ArPropertyConstants.SuspensionCategory.SUSPENSION_CATEGORY_CODE, suspensionCategoryCode);
        List suspensionCategoryList = (List) SpringContext.getBean(BusinessObjectService.class).findMatching(SuspensionCategory.class, criteria);
        if (CollectionUtils.isNotEmpty(suspensionCategoryList)) {
            this.suspensionCategory = (SuspensionCategory) suspensionCategoryList.get(0);
        }
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
     * Gets the suspensionCategory attribute.
     *
     * @return Returns the suspensionCategory
     */
    public SuspensionCategory getSuspensionCategory() {
        return suspensionCategory;
    }

    /**
     * Sets the suspensionCategory attribute value.
     *
     * @param suspensionCategory The suspensionCategory to set.
     */
    public void setSuspensionCategory(SuspensionCategory suspensionCategory) {
        this.suspensionCategory = suspensionCategory;
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
