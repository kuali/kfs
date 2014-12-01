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
package org.kuali.kfs.module.purap.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Attribute Reference Dummy Business Object
 */
public class PurApGenericAttributes extends PersistableBusinessObjectBase {

    private Integer purapDocumentIdentifier;
    private String genericTime;
    private KualiDecimal genericItemQuantity;
    private KualiDecimal genericItemAmount;
    private Integer genericItemIdentifier;
    private BigDecimal genericItemUnitPrice;
    private String contractManager;
    private String organizationCode;
    private String chartOfAccountsCode;
    private String requisitionStatus;
    private String purchaseOrderStatus;
    private String paymentRequestStatus;
    private String creditMemoStatus;
    private String extractedTimestamp;
    private String paymentPaidTimestamp;
    private String creditMemoPaidTimestamp;
    private String purchaseOrderDocumentCurrentIndicator;
    private String purchaseOrderAutomaticIndicator;
    private String requisitionIdentifier;
    private String receivingDocumentRequiredIndicator;
    private String paymentRequestPositiveApprovalIndicator;
    private String sequenceId;
    private String applicationDocumentStatus;
    
    /**
     * Default constructor
     */
    public PurApGenericAttributes() {
    }

    public String getCreditMemoPaidTimestamp() {
        return creditMemoPaidTimestamp;
    }

    public void setCreditMemoPaidTimestamp(String creditMemoPaidTimestamp) {
        this.creditMemoPaidTimestamp = creditMemoPaidTimestamp;
    }

    public String getExtractedTimestamp() {
        return extractedTimestamp;
    }

    public void setExtractedTimestamp(String extractedTimestamp) {
        this.extractedTimestamp = extractedTimestamp;
    }

    public String getPaymentPaidTimestamp() {
        return paymentPaidTimestamp;
    }

    public void setPaymentPaidTimestamp(String paymentPaidTimestamp) {
        this.paymentPaidTimestamp = paymentPaidTimestamp;
    }

    public BigDecimal getGenericItemUnitPrice() {
        return genericItemUnitPrice;
    }

    public void setGenericItemUnitPrice(BigDecimal genericItemUnitPrice) {
        this.genericItemUnitPrice = genericItemUnitPrice;
    }

    public Integer getGenericItemIdentifier() {
        return genericItemIdentifier;
    }

    public void setGenericItemIdentifier(Integer genericItemIdentifier) {
        this.genericItemIdentifier = genericItemIdentifier;
    }

    public KualiDecimal getGenericItemQuantity() {
        return genericItemQuantity;
    }

    public void setGenericItemQuantity(KualiDecimal genericItemQuantity) {
        this.genericItemQuantity = genericItemQuantity;
    }

    public KualiDecimal getGenericItemAmount() {
        return genericItemAmount;
    }

    public void setGenericItemAmount(KualiDecimal genericItemAmount) {
        this.genericItemAmount = genericItemAmount;
    }

    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    public String getGenericTime() {
        return genericTime;
    }

    public void setGenericTime(String genericTime) {
        this.genericTime = genericTime;
    }

    public String getContractManager() {
        return contractManager;
    }

    public void setContractManager(String contractManager) {
        this.contractManager = contractManager;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getCreditMemoStatus() {
        return creditMemoStatus;
    }

    public void setCreditMemoStatus(String creditMemoStatus) {
        this.creditMemoStatus = creditMemoStatus;
    }

    public String getPaymentRequestStatus() {
        return paymentRequestStatus;
    }

    public void setPaymentRequestStatus(String paymentRequestStatus) {
        this.paymentRequestStatus = paymentRequestStatus;
    }

    public String getPurchaseOrderStatus() {
        return purchaseOrderStatus;
    }

    public void setPurchaseOrderStatus(String purchaseOrderStatus) {
        this.purchaseOrderStatus = purchaseOrderStatus;
    }

    public String getRequisitionStatus() {
        return requisitionStatus;
    }

    public void setRequisitionStatus(String requisitionStatus) {
        this.requisitionStatus = requisitionStatus;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("hashCode", Integer.toHexString(hashCode()));

        return m;
    }

    public String getPurchaseOrderDocumentCurrentIndicator() {
        return purchaseOrderDocumentCurrentIndicator;
    }

    public void setPurchaseOrderDocumentCurrentIndicator(String purchaseOrderDocumentCurrentIndicator) {
        this.purchaseOrderDocumentCurrentIndicator = purchaseOrderDocumentCurrentIndicator;
    }

    public String getPurchaseOrderAutomaticIndicator() {
        return purchaseOrderAutomaticIndicator;
    }

    public void setPurchaseOrderAutomaticIndicator(String purchaseOrderAutomaticIndicator) {
        this.purchaseOrderAutomaticIndicator = purchaseOrderAutomaticIndicator;
    }

    public String getRequisitionIdentifier() {
        return requisitionIdentifier;
    }

    public void setRequisitionIdentifier(String requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }

    public String getReceivingDocumentRequiredIndicator() {
        return receivingDocumentRequiredIndicator;
    }

    public void setReceivingDocumentRequiredIndicator(String receivingDocumentRequiredIndicator) {
        this.receivingDocumentRequiredIndicator = receivingDocumentRequiredIndicator;
    }

    public String getPaymentRequestPositiveApprovalIndicator() {
        return paymentRequestPositiveApprovalIndicator;
    }

    public void setPaymentRequestPositiveApprovalIndicator(String paymentRequestPositiveApprovalIndicator) {
        this.paymentRequestPositiveApprovalIndicator = paymentRequestPositiveApprovalIndicator;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    /**
     * Gets the applicationDocumentStatus attribute.
     * 
     * @return Returns the applicationDocumentStatus
     */
    
    public String getApplicationDocumentStatus() {
        return applicationDocumentStatus;
    }

    /** 
     * Sets the applicationDocumentStatus attribute.
     * 
     * @param applicationDocumentStatus The applicationDocumentStatus to set.
     */
    public void setApplicationDocumentStatus(String applicationDocumentStatus) {
        this.applicationDocumentStatus = applicationDocumentStatus;
    }
}
