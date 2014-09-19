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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * Represents an entry in the Suspended Invoice Summary Report for Contracts and Grants Invoices.
 */
public class ContractsGrantsSuspendedInvoiceSummaryReport extends TransientBusinessObjectBase {

    private String fundManager;
    private String suspensionCategoryCode;
    private String suspensionCategoryDescription;
    private Long totalInvoicesSuspended;

    /**
     * Gets the fundManager attribute.
     *
     * @return Returns the fundManager
     */
    public String getFundManager() {
        return fundManager;
    }

    /**
     * Sets the fundManager attribute value.
     *
     * @param fundManager The fundManager to set.
     */
    public void setFundManager(String fundManager) {
        this.fundManager = fundManager;
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
        return suspensionCategoryDescription;
    }

    /**
     * Sets the suspensionCategoryDescription attribute value.
     *
     * @param suspensionCategoryDescription The suspensionCategoryDescription to set.
     */
    public void setSuspensionCategoryDescription(String suspensionCategoryDescription) {
        this.suspensionCategoryDescription = suspensionCategoryDescription;
    }

    /**
     * Gets the totalInvoicesSuspended attribute.
     *
     * @return Returns the totalInvoicesSuspended
     */
    public Long getTotalInvoicesSuspended() {
        return totalInvoicesSuspended;
    }

    /**
     * Sets the totalInvoicesSuspended attribute value.
     *
     * @param totalInvoicesSuspended The totalInvoicesSuspended to set.
     */
    public void setTotalInvoicesSuspended(Long totalInvoicesSuspended) {
        this.totalInvoicesSuspended = totalInvoicesSuspended;
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> toStringMap = new LinkedHashMap<String, Object>();
        toStringMap.put("fundManager", fundManager);
        toStringMap.put("suspensionCategoryCode", suspensionCategoryCode);
        toStringMap.put("categoryDescription", suspensionCategoryDescription);

        if (this.totalInvoicesSuspended != null) {
            toStringMap.put("totalInvoicesSuspended", this.totalInvoicesSuspended.toString());
        }

        return toStringMap;
    }

}
