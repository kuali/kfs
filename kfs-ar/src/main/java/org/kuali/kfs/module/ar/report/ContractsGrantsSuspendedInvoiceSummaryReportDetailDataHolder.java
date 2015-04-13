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

package org.kuali.kfs.module.ar.report;

import java.math.BigDecimal;

/**
 * Defines a data holder class for Suspended Invoices Summary Reports.
 */
public class ContractsGrantsSuspendedInvoiceSummaryReportDetailDataHolder {

    private String suspenseCategory;
    private String categoryDescription;
    private Long totalInvoicesSuspended;
    private String sortedFieldValue;
    private BigDecimal subTotal;
    public boolean displaySubtotal;


    /**
     * Gets the suspenseCategory attribute.
     *
     * @return Returns the suspenseCategory
     */
    public String getSuspenseCategory() {
        return suspenseCategory;
    }

    /**
     * Sets the suspenseCategory attribute value.
     *
     * @param suspenseCategory The suspenseCategory to set.
     */
    public void setSuspenseCategory(String suspenseCategory) {
        this.suspenseCategory = suspenseCategory;
    }

    /**
     * Gets the sortedFieldValue attribute.
     *
     * @return Returns the sortedFieldValue
     */
    public String getSortedFieldValue() {
        return sortedFieldValue;
    }

    /**
     * Sets the sortedFieldValue attribute value.
     *
     * @param sortedFieldValue The sortedFieldValue to set.
     */
    public void setSortedFieldValue(String sortedFieldValue) {
        this.sortedFieldValue = sortedFieldValue;
    }

    /**
     * Gets the subTotal attribute.
     *
     * @return Returns the subTotal
     */
    public BigDecimal getSubTotal() {
        return subTotal;
    }

    /**
     * Sets the subTotal attribute value.
     *
     * @param subTotal The subTotal to set.
     */
    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    /**
     * Gets the displaySubtotal attribute.
     *
     * @return Returns the displaySubtotal
     */
    public boolean isDisplaySubtotal() {
        return displaySubtotal;
    }

    /**
     * Sets the displaySubtotal attribute value.
     *
     * @param displaySubtotal The displaySubtotal to set.
     */
    public void setDisplaySubtotal(boolean displaySubtotal) {
        this.displaySubtotal = displaySubtotal;
    }

    /**
     * Gets the categoryDescription attribute.
     *
     * @return Returns the categoryDescription.
     */
    public String getCategoryDescription() {
        return categoryDescription;
    }

    /**
     * Gets the totalInvoicesSuspended attribute.
     *
     * @return Returns the totalInvoicesSuspended.
     */
    public Long getTotalInvoicesSuspended() {
        return totalInvoicesSuspended;
    }

    /**
     * Sets the categoryDescription attribute value.
     *
     * @param categoryDescription The categoryDescription to set.
     */
    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    /**
     * Sets the totalInvoicesSuspended attribute value.
     *
     * @param totalInvoicesSuspended The totalInvoicesSuspended to set.
     */
    public void setTotalInvoicesSuspended(Long totalInvoicesSuspended) {
        this.totalInvoicesSuspended = totalInvoicesSuspended;
    }

}
