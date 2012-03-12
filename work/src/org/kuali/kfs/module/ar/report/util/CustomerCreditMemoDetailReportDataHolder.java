/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.report.util;

import java.math.BigDecimal;

import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class CustomerCreditMemoDetailReportDataHolder {
    
    private BigDecimal creditMemoItemQuantity;
    private String creditMemoItemUnitOfMeasureCode;
    private String creditMemoItemDescription;
    private String creditMemoItemCode;
    private KualiDecimal creditMemoItemUnitPrice;
    private KualiDecimal creditMemoItemTaxAmount;
    private KualiDecimal creditMemoItemTotalAmount;
    
    public CustomerCreditMemoDetailReportDataHolder(CustomerCreditMemoDetail cmDetail, CustomerInvoiceDetail iDetail) {
       creditMemoItemQuantity = cmDetail.getCreditMemoItemQuantity();
       creditMemoItemUnitOfMeasureCode = iDetail.getInvoiceItemUnitOfMeasureCode();
       creditMemoItemDescription = iDetail.getInvoiceItemDescription();
       creditMemoItemCode = iDetail.getInvoiceItemCode();
       creditMemoItemUnitPrice = new KualiDecimal(iDetail.getInvoiceItemUnitPrice());
       creditMemoItemTaxAmount = cmDetail.getCreditMemoItemTaxAmount();
       creditMemoItemTotalAmount = cmDetail.getCreditMemoLineTotalAmount();
    }

    /**
     * Gets the creditMemoItemQuantity attribute. 
     * @return Returns the creditMemoItemQuantity.
     */
    public BigDecimal getCreditMemoItemQuantity() {
        return creditMemoItemQuantity;
    }

    /**
     * Sets the creditMemoItemQuantity attribute value.
     * @param creditMemoItemQuantity The creditMemoItemQuantity to set.
     */
    public void setCreditMemoItemQuantity(BigDecimal creditMemoItemQuantity) {
        this.creditMemoItemQuantity = creditMemoItemQuantity;
    }

    /**
     * Gets the creditMemoItemUnitOfMeasureCode attribute. 
     * @return Returns the creditMemoItemUnitOfMeasureCode.
     */
    public String getCreditMemoItemUnitOfMeasureCode() {
        return creditMemoItemUnitOfMeasureCode;
    }

    /**
     * Sets the creditMemoItemUnitOfMeasureCode attribute value.
     * @param creditMemoItemUnitOfMeasureCode The creditMemoItemUnitOfMeasureCode to set.
     */
    public void setCreditMemoItemUnitOfMeasureCode(String creditMemoItemUnitOfMeasureCode) {
        this.creditMemoItemUnitOfMeasureCode = creditMemoItemUnitOfMeasureCode;
    }

    /**
     * Gets the creditMemoItemDescription attribute. 
     * @return Returns the creditMemoItemDescription.
     */
    public String getCreditMemoItemDescription() {
        return creditMemoItemDescription;
    }

    /**
     * Sets the creditMemoItemDescription attribute value.
     * @param creditMemoItemDescription The creditMemoItemDescription to set.
     */
    public void setCreditMemoItemDescription(String creditMemoItemDescription) {
        this.creditMemoItemDescription = creditMemoItemDescription;
    }

    /**
     * Gets the creditMemoItemCode attribute. 
     * @return Returns the creditMemoItemCode.
     */
    public String getCreditMemoItemCode() {
        return creditMemoItemCode;
    }

    /**
     * Sets the creditMemoItemCode attribute value.
     * @param creditMemoItemCode The creditMemoItemCode to set.
     */
    public void setCreditMemoItemCode(String creditMemoItemCode) {
        this.creditMemoItemCode = creditMemoItemCode;
    }

    /**
     * Gets the creditMemoItemUnitPrice attribute. 
     * @return Returns the creditMemoItemUnitPrice.
     */
    public KualiDecimal getCreditMemoItemUnitPrice() {
        return creditMemoItemUnitPrice;
    }

    /**
     * Sets the creditMemoItemUnitPrice attribute value.
     * @param creditMemoItemUnitPrice The creditMemoItemUnitPrice to set.
     */
    public void setCreditMemoItemUnitPrice(KualiDecimal creditMemoItemUnitPrice) {
        this.creditMemoItemUnitPrice = creditMemoItemUnitPrice;
    }

    /**
     * Gets the creditMemoItemTaxAmount attribute. 
     * @return Returns the creditMemoItemTaxAmount.
     */
    public KualiDecimal getCreditMemoItemTaxAmount() {
        return creditMemoItemTaxAmount;
    }

    /**
     * Sets the creditMemoItemTaxAmount attribute value.
     * @param creditMemoItemTaxAmount The creditMemoItemTaxAmount to set.
     */
    public void setCreditMemoItemTaxAmount(KualiDecimal creditMemoItemTaxAmount) {
        this.creditMemoItemTaxAmount = creditMemoItemTaxAmount;
    }

    /**
     * Gets the creditMemoItemTotalAmount attribute. 
     * @return Returns the creditMemoItemTotalAmount.
     */
    public KualiDecimal getCreditMemoItemTotalAmount() {
        return creditMemoItemTotalAmount;
    }

    /**
     * Sets the creditMemoItemTotalAmount attribute value.
     * @param creditMemoItemTotalAmount The creditMemoItemTotalAmount to set.
     */
    public void setCreditMemoItemTotalAmount(KualiDecimal creditMemoItemTotalAmount) {
        this.creditMemoItemTotalAmount = creditMemoItemTotalAmount;
    }
}
