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
