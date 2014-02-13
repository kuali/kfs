/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.ar.report;

import java.math.BigDecimal;

/**
 * Defines a data holder class for Suspense Activity Reports. *
 */
public class ContractsGrantsInvoiceSuspenseActivityReportDetailDataHolder {

    private String suspenseCategory;
    private String categoryDescription;
    private Long totalInvoicesSuspended;
    private String sortedFieldValue;
    private BigDecimal subTotal;
    public boolean displaySubtotalInd;


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
     * Gets the displaySubtotalInd attribute.
     *
     * @return Returns the displaySubtotalInd
     */
    public boolean isDisplaySubtotalInd() {
        return displaySubtotalInd;
    }

    /**
     * Sets the displaySubtotalInd attribute value.
     *
     * @param displaySubtotalInd The displaySubtotalInd to set.
     */
    public void setDisplaySubtotalInd(boolean displaySubtotalInd) {
        this.displaySubtotalInd = displaySubtotalInd;
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
