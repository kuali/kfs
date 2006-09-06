/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.purap.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Campus;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ElectronicInvoiceHeaderInformation extends BusinessObjectBase {

	private Integer invoiceHeaderInformationIdentifier;
	private Long accountsPayableElectronicInvoiceLoadSummaryIdentifier;
	private Date invoiceProcessDate;
	private String invoiceFileName;
	private String vendorDunsNumber;
	private Integer vendorHeaderGeneratedIdentifier;
	private Integer vendorDetailAssignedIdentifier;
	private String invoiceFileDate;
	private String invoiceFileNumber;
	private String invoiceFilePurposeIdentifier;
	private String invoiceFileOperationIdentifier;
	private String invoiceFileDeploymentModeValue;
	private boolean invoiceFileHeaderTypeIndicator;
	private boolean invoiceFileInformationOnlyIndicator;
	private boolean invoiceFileTaxInLineIndicator;
	private boolean invoiceFileSpecialHandlingInLineIndicator;
	private boolean invoiceFileShippingInLineIndicator;
	private boolean invoiceFileDiscountInLineIndicator;
	private String invoiceOrderReferenceOrderIdentifier;
	private String invoiceOrderReferenceDocumentReferencePayloadIdentifier;
	private String invoiceOrderReferenceDocumentReferenceText;
	private String invoiceOrderMasterAgreementReferenceIdentifier;
	private String invoiceOrderMasterAgreementReferenceDate;
	private String invoiceOrderMasterAgreementInformationIdentifier;
	private String invoiceOrderMasterAgreementInformationDate;
	private String invoiceOrderPurchaseOrderIdentifier;
	private String invoiceOrderPurchaseOrderDate;
	private String invoiceOrderSupplierOrderInformationIdentifier;
	private Integer epicPurchaseOrderIdentifier;
	private BigDecimal invoiceItemSubTotalAmount;
	private String invoiceItemSubTotalCurrencyCode;
	private BigDecimal invoiceItemSpecialHandlingAmount;
	private String invoiceItemSpecialHandlingCurrencyCode;
	private BigDecimal invoiceItemShippingAmount;
	private String invoiceItemShippingCurrencyCode;
	private String invoiceItemShippingDescription;
	private BigDecimal invoiceItemTaxAmount;
	private String invoiceItemTaxCurrencyCode;
	private String invoiceItemTaxDescription;
	private BigDecimal invoiceItemGrossAmount;
	private String invoiceItemGrossCurrencyCode;
	private BigDecimal invoiceItemDiscountAmount;
	private String invoiceItemDiscountCurrencyCode;
	private BigDecimal invoiceItemNetAmount;
	private String invoiceItemNetCurrencyCode;
	private Date invoiceRejectExtractDate;
	private String epicPurchaseOrderDeliveryCampusCode;
	private Date invoiceShipDate;
	private String invoiceAddressName;
	private String invoiceShipToLine1Address;
	private String invoiceShipToLine2Address;
	private String invoiceShipToLine3Address;
	private String invoiceCustomerNumber;
	private String invoiceShipToStateCode;
	private String invoiceShipToCountryCode;
	private String invoiceShipToCityName;
	private String invoiceShipToPostalCode;
	private String invoicePurchaseOrderNumber;

    private ElectronicInvoiceRejectReason invoiceHeaderInformation;
	private ElectronicInvoiceLoadSummary accountsPayableElectronicInvoiceLoadSummary;
	private Campus epicPurchaseOrderDeliveryCampus;

	/**
	 * Default constructor.
	 */
	public ElectronicInvoiceHeaderInformation() {

	}

	/**
	 * Gets the invoiceHeaderInformationIdentifier attribute.
	 * 
	 * @return - Returns the invoiceHeaderInformationIdentifier
	 * 
	 */
	public Integer getInvoiceHeaderInformationIdentifier() { 
		return invoiceHeaderInformationIdentifier;
	}

	/**
	 * Sets the invoiceHeaderInformationIdentifier attribute.
	 * 
	 * @param - invoiceHeaderInformationIdentifier The invoiceHeaderInformationIdentifier to set.
	 * 
	 */
	public void setInvoiceHeaderInformationIdentifier(Integer invoiceHeaderInformationIdentifier) {
		this.invoiceHeaderInformationIdentifier = invoiceHeaderInformationIdentifier;
	}


	/**
	 * Gets the accountsPayableElectronicInvoiceLoadSummaryIdentifier attribute.
	 * 
	 * @return - Returns the accountsPayableElectronicInvoiceLoadSummaryIdentifier
	 * 
	 */
	public Long getAccountsPayableElectronicInvoiceLoadSummaryIdentifier() { 
		return accountsPayableElectronicInvoiceLoadSummaryIdentifier;
	}

	/**
	 * Sets the accountsPayableElectronicInvoiceLoadSummaryIdentifier attribute.
	 * 
	 * @param - accountsPayableElectronicInvoiceLoadSummaryIdentifier The accountsPayableElectronicInvoiceLoadSummaryIdentifier to set.
	 * 
	 */
	public void setAccountsPayableElectronicInvoiceLoadSummaryIdentifier(Long accountsPayableElectronicInvoiceLoadSummaryIdentifier) {
		this.accountsPayableElectronicInvoiceLoadSummaryIdentifier = accountsPayableElectronicInvoiceLoadSummaryIdentifier;
	}


	/**
	 * Gets the invoiceProcessDate attribute.
	 * 
	 * @return - Returns the invoiceProcessDate
	 * 
	 */
	public Date getInvoiceProcessDate() { 
		return invoiceProcessDate;
	}

	/**
	 * Sets the invoiceProcessDate attribute.
	 * 
	 * @param - invoiceProcessDate The invoiceProcessDate to set.
	 * 
	 */
	public void setInvoiceProcessDate(Date invoiceProcessDate) {
		this.invoiceProcessDate = invoiceProcessDate;
	}


	/**
	 * Gets the invoiceFileName attribute.
	 * 
	 * @return - Returns the invoiceFileName
	 * 
	 */
	public String getInvoiceFileName() { 
		return invoiceFileName;
	}

	/**
	 * Sets the invoiceFileName attribute.
	 * 
	 * @param - invoiceFileName The invoiceFileName to set.
	 * 
	 */
	public void setInvoiceFileName(String invoiceFileName) {
		this.invoiceFileName = invoiceFileName;
	}


	/**
	 * Gets the vendorDunsNumber attribute.
	 * 
	 * @return - Returns the vendorDunsNumber
	 * 
	 */
	public String getVendorDunsNumber() { 
		return vendorDunsNumber;
	}

	/**
	 * Sets the vendorDunsNumber attribute.
	 * 
	 * @param - vendorDunsNumber The vendorDunsNumber to set.
	 * 
	 */
	public void setVendorDunsNumber(String vendorDunsNumber) {
		this.vendorDunsNumber = vendorDunsNumber;
	}


	/**
	 * Gets the vendorHeaderGeneratedIdentifier attribute.
	 * 
	 * @return - Returns the vendorHeaderGeneratedIdentifier
	 * 
	 */
	public Integer getVendorHeaderGeneratedIdentifier() { 
		return vendorHeaderGeneratedIdentifier;
	}

	/**
	 * Sets the vendorHeaderGeneratedIdentifier attribute.
	 * 
	 * @param - vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
	 * 
	 */
	public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
		this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
	}


	/**
	 * Gets the vendorDetailAssignedIdentifier attribute.
	 * 
	 * @return - Returns the vendorDetailAssignedIdentifier
	 * 
	 */
	public Integer getVendorDetailAssignedIdentifier() { 
		return vendorDetailAssignedIdentifier;
	}

	/**
	 * Sets the vendorDetailAssignedIdentifier attribute.
	 * 
	 * @param - vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
	 * 
	 */
	public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
		this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
	}


	/**
	 * Gets the invoiceFileDate attribute.
	 * 
	 * @return - Returns the invoiceFileDate
	 * 
	 */
	public String getInvoiceFileDate() { 
		return invoiceFileDate;
	}

	/**
	 * Sets the invoiceFileDate attribute.
	 * 
	 * @param - invoiceFileDate The invoiceFileDate to set.
	 * 
	 */
	public void setInvoiceFileDate(String invoiceFileDate) {
		this.invoiceFileDate = invoiceFileDate;
	}


	/**
	 * Gets the invoiceFileNumber attribute.
	 * 
	 * @return - Returns the invoiceFileNumber
	 * 
	 */
	public String getInvoiceFileNumber() { 
		return invoiceFileNumber;
	}

	/**
	 * Sets the invoiceFileNumber attribute.
	 * 
	 * @param - invoiceFileNumber The invoiceFileNumber to set.
	 * 
	 */
	public void setInvoiceFileNumber(String invoiceFileNumber) {
		this.invoiceFileNumber = invoiceFileNumber;
	}


	/**
	 * Gets the invoiceFilePurposeIdentifier attribute.
	 * 
	 * @return - Returns the invoiceFilePurposeIdentifier
	 * 
	 */
	public String getInvoiceFilePurposeIdentifier() { 
		return invoiceFilePurposeIdentifier;
	}

	/**
	 * Sets the invoiceFilePurposeIdentifier attribute.
	 * 
	 * @param - invoiceFilePurposeIdentifier The invoiceFilePurposeIdentifier to set.
	 * 
	 */
	public void setInvoiceFilePurposeIdentifier(String invoiceFilePurposeIdentifier) {
		this.invoiceFilePurposeIdentifier = invoiceFilePurposeIdentifier;
	}


	/**
	 * Gets the invoiceFileOperationIdentifier attribute.
	 * 
	 * @return - Returns the invoiceFileOperationIdentifier
	 * 
	 */
	public String getInvoiceFileOperationIdentifier() { 
		return invoiceFileOperationIdentifier;
	}

	/**
	 * Sets the invoiceFileOperationIdentifier attribute.
	 * 
	 * @param - invoiceFileOperationIdentifier The invoiceFileOperationIdentifier to set.
	 * 
	 */
	public void setInvoiceFileOperationIdentifier(String invoiceFileOperationIdentifier) {
		this.invoiceFileOperationIdentifier = invoiceFileOperationIdentifier;
	}


	/**
	 * Gets the invoiceFileDeploymentModeValue attribute.
	 * 
	 * @return - Returns the invoiceFileDeploymentModeValue
	 * 
	 */
	public String getInvoiceFileDeploymentModeValue() { 
		return invoiceFileDeploymentModeValue;
	}

	/**
	 * Sets the invoiceFileDeploymentModeValue attribute.
	 * 
	 * @param - invoiceFileDeploymentModeValue The invoiceFileDeploymentModeValue to set.
	 * 
	 */
	public void setInvoiceFileDeploymentModeValue(String invoiceFileDeploymentModeValue) {
		this.invoiceFileDeploymentModeValue = invoiceFileDeploymentModeValue;
	}


	/**
	 * Gets the invoiceFileHeaderTypeIndicator attribute.
	 * 
	 * @return - Returns the invoiceFileHeaderTypeIndicator
	 * 
	 */
	public boolean isInvoiceFileHeaderTypeIndicator() { 
		return invoiceFileHeaderTypeIndicator;
	}
	

	/**
	 * Sets the invoiceFileHeaderTypeIndicator attribute.
	 * 
	 * @param - invoiceFileHeaderTypeIndicator The invoiceFileHeaderTypeIndicator to set.
	 * 
	 */
	public void setInvoiceFileHeaderTypeIndicator(boolean invoiceFileHeaderTypeIndicator) {
		this.invoiceFileHeaderTypeIndicator = invoiceFileHeaderTypeIndicator;
	}


	/**
	 * Gets the invoiceFileInformationOnlyIndicator attribute.
	 * 
	 * @return - Returns the invoiceFileInformationOnlyIndicator
	 * 
	 */
	public boolean isInvoiceFileInformationOnlyIndicator() { 
		return invoiceFileInformationOnlyIndicator;
	}
	

	/**
	 * Sets the invoiceFileInformationOnlyIndicator attribute.
	 * 
	 * @param - invoiceFileInformationOnlyIndicator The invoiceFileInformationOnlyIndicator to set.
	 * 
	 */
	public void setInvoiceFileInformationOnlyIndicator(boolean invoiceFileInformationOnlyIndicator) {
		this.invoiceFileInformationOnlyIndicator = invoiceFileInformationOnlyIndicator;
	}


	/**
	 * Gets the invoiceFileTaxInLineIndicator attribute.
	 * 
	 * @return - Returns the invoiceFileTaxInLineIndicator
	 * 
	 */
	public boolean isInvoiceFileTaxInLineIndicator() { 
		return invoiceFileTaxInLineIndicator;
	}
	

	/**
	 * Sets the invoiceFileTaxInLineIndicator attribute.
	 * 
	 * @param - invoiceFileTaxInLineIndicator The invoiceFileTaxInLineIndicator to set.
	 * 
	 */
	public void setInvoiceFileTaxInLineIndicator(boolean invoiceFileTaxInLineIndicator) {
		this.invoiceFileTaxInLineIndicator = invoiceFileTaxInLineIndicator;
	}


	/**
	 * Gets the invoiceFileSpecialHandlingInLineIndicator attribute.
	 * 
	 * @return - Returns the invoiceFileSpecialHandlingInLineIndicator
	 * 
	 */
	public boolean isInvoiceFileSpecialHandlingInLineIndicator() { 
		return invoiceFileSpecialHandlingInLineIndicator;
	}
	

	/**
	 * Sets the invoiceFileSpecialHandlingInLineIndicator attribute.
	 * 
	 * @param - invoiceFileSpecialHandlingInLineIndicator The invoiceFileSpecialHandlingInLineIndicator to set.
	 * 
	 */
	public void setInvoiceFileSpecialHandlingInLineIndicator(boolean invoiceFileSpecialHandlingInLineIndicator) {
		this.invoiceFileSpecialHandlingInLineIndicator = invoiceFileSpecialHandlingInLineIndicator;
	}


	/**
	 * Gets the invoiceFileShippingInLineIndicator attribute.
	 * 
	 * @return - Returns the invoiceFileShippingInLineIndicator
	 * 
	 */
	public boolean isInvoiceFileShippingInLineIndicator() { 
		return invoiceFileShippingInLineIndicator;
	}
	

	/**
	 * Sets the invoiceFileShippingInLineIndicator attribute.
	 * 
	 * @param - invoiceFileShippingInLineIndicator The invoiceFileShippingInLineIndicator to set.
	 * 
	 */
	public void setInvoiceFileShippingInLineIndicator(boolean invoiceFileShippingInLineIndicator) {
		this.invoiceFileShippingInLineIndicator = invoiceFileShippingInLineIndicator;
	}


	/**
	 * Gets the invoiceFileDiscountInLineIndicator attribute.
	 * 
	 * @return - Returns the invoiceFileDiscountInLineIndicator
	 * 
	 */
	public boolean isInvoiceFileDiscountInLineIndicator() { 
		return invoiceFileDiscountInLineIndicator;
	}
	

	/**
	 * Sets the invoiceFileDiscountInLineIndicator attribute.
	 * 
	 * @param - invoiceFileDiscountInLineIndicator The invoiceFileDiscountInLineIndicator to set.
	 * 
	 */
	public void setInvoiceFileDiscountInLineIndicator(boolean invoiceFileDiscountInLineIndicator) {
		this.invoiceFileDiscountInLineIndicator = invoiceFileDiscountInLineIndicator;
	}


	/**
	 * Gets the invoiceOrderReferenceOrderIdentifier attribute.
	 * 
	 * @return - Returns the invoiceOrderReferenceOrderIdentifier
	 * 
	 */
	public String getInvoiceOrderReferenceOrderIdentifier() { 
		return invoiceOrderReferenceOrderIdentifier;
	}

	/**
	 * Sets the invoiceOrderReferenceOrderIdentifier attribute.
	 * 
	 * @param - invoiceOrderReferenceOrderIdentifier The invoiceOrderReferenceOrderIdentifier to set.
	 * 
	 */
	public void setInvoiceOrderReferenceOrderIdentifier(String invoiceOrderReferenceOrderIdentifier) {
		this.invoiceOrderReferenceOrderIdentifier = invoiceOrderReferenceOrderIdentifier;
	}


	/**
	 * Gets the invoiceOrderReferenceDocumentReferencePayloadIdentifier attribute.
	 * 
	 * @return - Returns the invoiceOrderReferenceDocumentReferencePayloadIdentifier
	 * 
	 */
	public String getInvoiceOrderReferenceDocumentReferencePayloadIdentifier() { 
		return invoiceOrderReferenceDocumentReferencePayloadIdentifier;
	}

	/**
	 * Sets the invoiceOrderReferenceDocumentReferencePayloadIdentifier attribute.
	 * 
	 * @param - invoiceOrderReferenceDocumentReferencePayloadIdentifier The invoiceOrderReferenceDocumentReferencePayloadIdentifier to set.
	 * 
	 */
	public void setInvoiceOrderReferenceDocumentReferencePayloadIdentifier(String invoiceOrderReferenceDocumentReferencePayloadIdentifier) {
		this.invoiceOrderReferenceDocumentReferencePayloadIdentifier = invoiceOrderReferenceDocumentReferencePayloadIdentifier;
	}


	/**
	 * Gets the invoiceOrderReferenceDocumentReferenceText attribute.
	 * 
	 * @return - Returns the invoiceOrderReferenceDocumentReferenceText
	 * 
	 */
	public String getInvoiceOrderReferenceDocumentReferenceText() { 
		return invoiceOrderReferenceDocumentReferenceText;
	}

	/**
	 * Sets the invoiceOrderReferenceDocumentReferenceText attribute.
	 * 
	 * @param - invoiceOrderReferenceDocumentReferenceText The invoiceOrderReferenceDocumentReferenceText to set.
	 * 
	 */
	public void setInvoiceOrderReferenceDocumentReferenceText(String invoiceOrderReferenceDocumentReferenceText) {
		this.invoiceOrderReferenceDocumentReferenceText = invoiceOrderReferenceDocumentReferenceText;
	}


	/**
	 * Gets the invoiceOrderMasterAgreementReferenceIdentifier attribute.
	 * 
	 * @return - Returns the invoiceOrderMasterAgreementReferenceIdentifier
	 * 
	 */
	public String getInvoiceOrderMasterAgreementReferenceIdentifier() { 
		return invoiceOrderMasterAgreementReferenceIdentifier;
	}

	/**
	 * Sets the invoiceOrderMasterAgreementReferenceIdentifier attribute.
	 * 
	 * @param - invoiceOrderMasterAgreementReferenceIdentifier The invoiceOrderMasterAgreementReferenceIdentifier to set.
	 * 
	 */
	public void setInvoiceOrderMasterAgreementReferenceIdentifier(String invoiceOrderMasterAgreementReferenceIdentifier) {
		this.invoiceOrderMasterAgreementReferenceIdentifier = invoiceOrderMasterAgreementReferenceIdentifier;
	}


	/**
	 * Gets the invoiceOrderMasterAgreementReferenceDate attribute.
	 * 
	 * @return - Returns the invoiceOrderMasterAgreementReferenceDate
	 * 
	 */
	public String getInvoiceOrderMasterAgreementReferenceDate() { 
		return invoiceOrderMasterAgreementReferenceDate;
	}

	/**
	 * Sets the invoiceOrderMasterAgreementReferenceDate attribute.
	 * 
	 * @param - invoiceOrderMasterAgreementReferenceDate The invoiceOrderMasterAgreementReferenceDate to set.
	 * 
	 */
	public void setInvoiceOrderMasterAgreementReferenceDate(String invoiceOrderMasterAgreementReferenceDate) {
		this.invoiceOrderMasterAgreementReferenceDate = invoiceOrderMasterAgreementReferenceDate;
	}


	/**
	 * Gets the invoiceOrderMasterAgreementInformationIdentifier attribute.
	 * 
	 * @return - Returns the invoiceOrderMasterAgreementInformationIdentifier
	 * 
	 */
	public String getInvoiceOrderMasterAgreementInformationIdentifier() { 
		return invoiceOrderMasterAgreementInformationIdentifier;
	}

	/**
	 * Sets the invoiceOrderMasterAgreementInformationIdentifier attribute.
	 * 
	 * @param - invoiceOrderMasterAgreementInformationIdentifier The invoiceOrderMasterAgreementInformationIdentifier to set.
	 * 
	 */
	public void setInvoiceOrderMasterAgreementInformationIdentifier(String invoiceOrderMasterAgreementInformationIdentifier) {
		this.invoiceOrderMasterAgreementInformationIdentifier = invoiceOrderMasterAgreementInformationIdentifier;
	}


	/**
	 * Gets the invoiceOrderMasterAgreementInformationDate attribute.
	 * 
	 * @return - Returns the invoiceOrderMasterAgreementInformationDate
	 * 
	 */
	public String getInvoiceOrderMasterAgreementInformationDate() { 
		return invoiceOrderMasterAgreementInformationDate;
	}

	/**
	 * Sets the invoiceOrderMasterAgreementInformationDate attribute.
	 * 
	 * @param - invoiceOrderMasterAgreementInformationDate The invoiceOrderMasterAgreementInformationDate to set.
	 * 
	 */
	public void setInvoiceOrderMasterAgreementInformationDate(String invoiceOrderMasterAgreementInformationDate) {
		this.invoiceOrderMasterAgreementInformationDate = invoiceOrderMasterAgreementInformationDate;
	}


	/**
	 * Gets the invoiceOrderPurchaseOrderIdentifier attribute.
	 * 
	 * @return - Returns the invoiceOrderPurchaseOrderIdentifier
	 * 
	 */
	public String getInvoiceOrderPurchaseOrderIdentifier() { 
		return invoiceOrderPurchaseOrderIdentifier;
	}

	/**
	 * Sets the invoiceOrderPurchaseOrderIdentifier attribute.
	 * 
	 * @param - invoiceOrderPurchaseOrderIdentifier The invoiceOrderPurchaseOrderIdentifier to set.
	 * 
	 */
	public void setInvoiceOrderPurchaseOrderIdentifier(String invoiceOrderPurchaseOrderIdentifier) {
		this.invoiceOrderPurchaseOrderIdentifier = invoiceOrderPurchaseOrderIdentifier;
	}


	/**
	 * Gets the invoiceOrderPurchaseOrderDate attribute.
	 * 
	 * @return - Returns the invoiceOrderPurchaseOrderDate
	 * 
	 */
	public String getInvoiceOrderPurchaseOrderDate() { 
		return invoiceOrderPurchaseOrderDate;
	}

	/**
	 * Sets the invoiceOrderPurchaseOrderDate attribute.
	 * 
	 * @param - invoiceOrderPurchaseOrderDate The invoiceOrderPurchaseOrderDate to set.
	 * 
	 */
	public void setInvoiceOrderPurchaseOrderDate(String invoiceOrderPurchaseOrderDate) {
		this.invoiceOrderPurchaseOrderDate = invoiceOrderPurchaseOrderDate;
	}


	/**
	 * Gets the invoiceOrderSupplierOrderInformationIdentifier attribute.
	 * 
	 * @return - Returns the invoiceOrderSupplierOrderInformationIdentifier
	 * 
	 */
	public String getInvoiceOrderSupplierOrderInformationIdentifier() { 
		return invoiceOrderSupplierOrderInformationIdentifier;
	}

	/**
	 * Sets the invoiceOrderSupplierOrderInformationIdentifier attribute.
	 * 
	 * @param - invoiceOrderSupplierOrderInformationIdentifier The invoiceOrderSupplierOrderInformationIdentifier to set.
	 * 
	 */
	public void setInvoiceOrderSupplierOrderInformationIdentifier(String invoiceOrderSupplierOrderInformationIdentifier) {
		this.invoiceOrderSupplierOrderInformationIdentifier = invoiceOrderSupplierOrderInformationIdentifier;
	}


	/**
	 * Gets the epicPurchaseOrderIdentifier attribute.
	 * 
	 * @return - Returns the epicPurchaseOrderIdentifier
	 * 
	 */
	public Integer getEpicPurchaseOrderIdentifier() { 
		return epicPurchaseOrderIdentifier;
	}

	/**
	 * Sets the epicPurchaseOrderIdentifier attribute.
	 * 
	 * @param - epicPurchaseOrderIdentifier The epicPurchaseOrderIdentifier to set.
	 * 
	 */
	public void setEpicPurchaseOrderIdentifier(Integer epicPurchaseOrderIdentifier) {
		this.epicPurchaseOrderIdentifier = epicPurchaseOrderIdentifier;
	}


	/**
	 * Gets the invoiceItemSubTotalAmount attribute.
	 * 
	 * @return - Returns the invoiceItemSubTotalAmount
	 * 
	 */
	public BigDecimal getInvoiceItemSubTotalAmount() { 
		return invoiceItemSubTotalAmount;
	}

	/**
	 * Sets the invoiceItemSubTotalAmount attribute.
	 * 
	 * @param - invoiceItemSubTotalAmount The invoiceItemSubTotalAmount to set.
	 * 
	 */
	public void setInvoiceItemSubTotalAmount(BigDecimal invoiceItemSubTotalAmount) {
		this.invoiceItemSubTotalAmount = invoiceItemSubTotalAmount;
	}


	/**
	 * Gets the invoiceItemSubTotalCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemSubTotalCurrencyCode
	 * 
	 */
	public String getInvoiceItemSubTotalCurrencyCode() { 
		return invoiceItemSubTotalCurrencyCode;
	}

	/**
	 * Sets the invoiceItemSubTotalCurrencyCode attribute.
	 * 
	 * @param - invoiceItemSubTotalCurrencyCode The invoiceItemSubTotalCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemSubTotalCurrencyCode(String invoiceItemSubTotalCurrencyCode) {
		this.invoiceItemSubTotalCurrencyCode = invoiceItemSubTotalCurrencyCode;
	}


	/**
	 * Gets the invoiceItemSpecialHandlingAmount attribute.
	 * 
	 * @return - Returns the invoiceItemSpecialHandlingAmount
	 * 
	 */
	public BigDecimal getInvoiceItemSpecialHandlingAmount() { 
		return invoiceItemSpecialHandlingAmount;
	}

	/**
	 * Sets the invoiceItemSpecialHandlingAmount attribute.
	 * 
	 * @param - invoiceItemSpecialHandlingAmount The invoiceItemSpecialHandlingAmount to set.
	 * 
	 */
	public void setInvoiceItemSpecialHandlingAmount(BigDecimal invoiceItemSpecialHandlingAmount) {
		this.invoiceItemSpecialHandlingAmount = invoiceItemSpecialHandlingAmount;
	}


	/**
	 * Gets the invoiceItemSpecialHandlingCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemSpecialHandlingCurrencyCode
	 * 
	 */
	public String getInvoiceItemSpecialHandlingCurrencyCode() { 
		return invoiceItemSpecialHandlingCurrencyCode;
	}

	/**
	 * Sets the invoiceItemSpecialHandlingCurrencyCode attribute.
	 * 
	 * @param - invoiceItemSpecialHandlingCurrencyCode The invoiceItemSpecialHandlingCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemSpecialHandlingCurrencyCode(String invoiceItemSpecialHandlingCurrencyCode) {
		this.invoiceItemSpecialHandlingCurrencyCode = invoiceItemSpecialHandlingCurrencyCode;
	}


	/**
	 * Gets the invoiceItemShippingAmount attribute.
	 * 
	 * @return - Returns the invoiceItemShippingAmount
	 * 
	 */
	public BigDecimal getInvoiceItemShippingAmount() { 
		return invoiceItemShippingAmount;
	}

	/**
	 * Sets the invoiceItemShippingAmount attribute.
	 * 
	 * @param - invoiceItemShippingAmount The invoiceItemShippingAmount to set.
	 * 
	 */
	public void setInvoiceItemShippingAmount(BigDecimal invoiceItemShippingAmount) {
		this.invoiceItemShippingAmount = invoiceItemShippingAmount;
	}


	/**
	 * Gets the invoiceItemShippingCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemShippingCurrencyCode
	 * 
	 */
	public String getInvoiceItemShippingCurrencyCode() { 
		return invoiceItemShippingCurrencyCode;
	}

	/**
	 * Sets the invoiceItemShippingCurrencyCode attribute.
	 * 
	 * @param - invoiceItemShippingCurrencyCode The invoiceItemShippingCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemShippingCurrencyCode(String invoiceItemShippingCurrencyCode) {
		this.invoiceItemShippingCurrencyCode = invoiceItemShippingCurrencyCode;
	}


	/**
	 * Gets the invoiceItemShippingDescription attribute.
	 * 
	 * @return - Returns the invoiceItemShippingDescription
	 * 
	 */
	public String getInvoiceItemShippingDescription() { 
		return invoiceItemShippingDescription;
	}

	/**
	 * Sets the invoiceItemShippingDescription attribute.
	 * 
	 * @param - invoiceItemShippingDescription The invoiceItemShippingDescription to set.
	 * 
	 */
	public void setInvoiceItemShippingDescription(String invoiceItemShippingDescription) {
		this.invoiceItemShippingDescription = invoiceItemShippingDescription;
	}


	/**
	 * Gets the invoiceItemTaxAmount attribute.
	 * 
	 * @return - Returns the invoiceItemTaxAmount
	 * 
	 */
	public BigDecimal getInvoiceItemTaxAmount() { 
		return invoiceItemTaxAmount;
	}

	/**
	 * Sets the invoiceItemTaxAmount attribute.
	 * 
	 * @param - invoiceItemTaxAmount The invoiceItemTaxAmount to set.
	 * 
	 */
	public void setInvoiceItemTaxAmount(BigDecimal invoiceItemTaxAmount) {
		this.invoiceItemTaxAmount = invoiceItemTaxAmount;
	}


	/**
	 * Gets the invoiceItemTaxCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemTaxCurrencyCode
	 * 
	 */
	public String getInvoiceItemTaxCurrencyCode() { 
		return invoiceItemTaxCurrencyCode;
	}

	/**
	 * Sets the invoiceItemTaxCurrencyCode attribute.
	 * 
	 * @param - invoiceItemTaxCurrencyCode The invoiceItemTaxCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemTaxCurrencyCode(String invoiceItemTaxCurrencyCode) {
		this.invoiceItemTaxCurrencyCode = invoiceItemTaxCurrencyCode;
	}


	/**
	 * Gets the invoiceItemTaxDescription attribute.
	 * 
	 * @return - Returns the invoiceItemTaxDescription
	 * 
	 */
	public String getInvoiceItemTaxDescription() { 
		return invoiceItemTaxDescription;
	}

	/**
	 * Sets the invoiceItemTaxDescription attribute.
	 * 
	 * @param - invoiceItemTaxDescription The invoiceItemTaxDescription to set.
	 * 
	 */
	public void setInvoiceItemTaxDescription(String invoiceItemTaxDescription) {
		this.invoiceItemTaxDescription = invoiceItemTaxDescription;
	}


	/**
	 * Gets the invoiceItemGrossAmount attribute.
	 * 
	 * @return - Returns the invoiceItemGrossAmount
	 * 
	 */
	public BigDecimal getInvoiceItemGrossAmount() { 
		return invoiceItemGrossAmount;
	}

	/**
	 * Sets the invoiceItemGrossAmount attribute.
	 * 
	 * @param - invoiceItemGrossAmount The invoiceItemGrossAmount to set.
	 * 
	 */
	public void setInvoiceItemGrossAmount(BigDecimal invoiceItemGrossAmount) {
		this.invoiceItemGrossAmount = invoiceItemGrossAmount;
	}


	/**
	 * Gets the invoiceItemGrossCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemGrossCurrencyCode
	 * 
	 */
	public String getInvoiceItemGrossCurrencyCode() { 
		return invoiceItemGrossCurrencyCode;
	}

	/**
	 * Sets the invoiceItemGrossCurrencyCode attribute.
	 * 
	 * @param - invoiceItemGrossCurrencyCode The invoiceItemGrossCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemGrossCurrencyCode(String invoiceItemGrossCurrencyCode) {
		this.invoiceItemGrossCurrencyCode = invoiceItemGrossCurrencyCode;
	}


	/**
	 * Gets the invoiceItemDiscountAmount attribute.
	 * 
	 * @return - Returns the invoiceItemDiscountAmount
	 * 
	 */
	public BigDecimal getInvoiceItemDiscountAmount() { 
		return invoiceItemDiscountAmount;
	}

	/**
	 * Sets the invoiceItemDiscountAmount attribute.
	 * 
	 * @param - invoiceItemDiscountAmount The invoiceItemDiscountAmount to set.
	 * 
	 */
	public void setInvoiceItemDiscountAmount(BigDecimal invoiceItemDiscountAmount) {
		this.invoiceItemDiscountAmount = invoiceItemDiscountAmount;
	}


	/**
	 * Gets the invoiceItemDiscountCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemDiscountCurrencyCode
	 * 
	 */
	public String getInvoiceItemDiscountCurrencyCode() { 
		return invoiceItemDiscountCurrencyCode;
	}

	/**
	 * Sets the invoiceItemDiscountCurrencyCode attribute.
	 * 
	 * @param - invoiceItemDiscountCurrencyCode The invoiceItemDiscountCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemDiscountCurrencyCode(String invoiceItemDiscountCurrencyCode) {
		this.invoiceItemDiscountCurrencyCode = invoiceItemDiscountCurrencyCode;
	}


	/**
	 * Gets the invoiceItemNetAmount attribute.
	 * 
	 * @return - Returns the invoiceItemNetAmount
	 * 
	 */
	public BigDecimal getInvoiceItemNetAmount() { 
		return invoiceItemNetAmount;
	}

	/**
	 * Sets the invoiceItemNetAmount attribute.
	 * 
	 * @param - invoiceItemNetAmount The invoiceItemNetAmount to set.
	 * 
	 */
	public void setInvoiceItemNetAmount(BigDecimal invoiceItemNetAmount) {
		this.invoiceItemNetAmount = invoiceItemNetAmount;
	}


	/**
	 * Gets the invoiceItemNetCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemNetCurrencyCode
	 * 
	 */
	public String getInvoiceItemNetCurrencyCode() { 
		return invoiceItemNetCurrencyCode;
	}

	/**
	 * Sets the invoiceItemNetCurrencyCode attribute.
	 * 
	 * @param - invoiceItemNetCurrencyCode The invoiceItemNetCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemNetCurrencyCode(String invoiceItemNetCurrencyCode) {
		this.invoiceItemNetCurrencyCode = invoiceItemNetCurrencyCode;
	}


	/**
	 * Gets the invoiceRejectExtractDate attribute.
	 * 
	 * @return - Returns the invoiceRejectExtractDate
	 * 
	 */
	public Date getInvoiceRejectExtractDate() { 
		return invoiceRejectExtractDate;
	}

	/**
	 * Sets the invoiceRejectExtractDate attribute.
	 * 
	 * @param - invoiceRejectExtractDate The invoiceRejectExtractDate to set.
	 * 
	 */
	public void setInvoiceRejectExtractDate(Date invoiceRejectExtractDate) {
		this.invoiceRejectExtractDate = invoiceRejectExtractDate;
	}


	/**
	 * Gets the epicPurchaseOrderDeliveryCampusCode attribute.
	 * 
	 * @return - Returns the epicPurchaseOrderDeliveryCampusCode
	 * 
	 */
	public String getEpicPurchaseOrderDeliveryCampusCode() { 
		return epicPurchaseOrderDeliveryCampusCode;
	}

	/**
	 * Sets the epicPurchaseOrderDeliveryCampusCode attribute.
	 * 
	 * @param - epicPurchaseOrderDeliveryCampusCode The epicPurchaseOrderDeliveryCampusCode to set.
	 * 
	 */
	public void setEpicPurchaseOrderDeliveryCampusCode(String epicPurchaseOrderDeliveryCampusCode) {
		this.epicPurchaseOrderDeliveryCampusCode = epicPurchaseOrderDeliveryCampusCode;
	}


	/**
	 * Gets the invoiceShipDate attribute.
	 * 
	 * @return - Returns the invoiceShipDate
	 * 
	 */
	public Date getInvoiceShipDate() { 
		return invoiceShipDate;
	}

	/**
	 * Sets the invoiceShipDate attribute.
	 * 
	 * @param - invoiceShipDate The invoiceShipDate to set.
	 * 
	 */
	public void setInvoiceShipDate(Date invoiceShipDate) {
		this.invoiceShipDate = invoiceShipDate;
	}


	/**
	 * Gets the invoiceAddressName attribute.
	 * 
	 * @return - Returns the invoiceAddressName
	 * 
	 */
	public String getInvoiceAddressName() { 
		return invoiceAddressName;
	}

	/**
	 * Sets the invoiceAddressName attribute.
	 * 
	 * @param - invoiceAddressName The invoiceAddressName to set.
	 * 
	 */
	public void setInvoiceAddressName(String invoiceAddressName) {
		this.invoiceAddressName = invoiceAddressName;
	}


	/**
	 * Gets the invoiceShipToLine1Address attribute.
	 * 
	 * @return - Returns the invoiceShipToLine1Address
	 * 
	 */
	public String getInvoiceShipToLine1Address() { 
		return invoiceShipToLine1Address;
	}

	/**
	 * Sets the invoiceShipToLine1Address attribute.
	 * 
	 * @param - invoiceShipToLine1Address The invoiceShipToLine1Address to set.
	 * 
	 */
	public void setInvoiceShipToLine1Address(String invoiceShipToLine1Address) {
		this.invoiceShipToLine1Address = invoiceShipToLine1Address;
	}


	/**
	 * Gets the invoiceShipToLine2Address attribute.
	 * 
	 * @return - Returns the invoiceShipToLine2Address
	 * 
	 */
	public String getInvoiceShipToLine2Address() { 
		return invoiceShipToLine2Address;
	}

	/**
	 * Sets the invoiceShipToLine2Address attribute.
	 * 
	 * @param - invoiceShipToLine2Address The invoiceShipToLine2Address to set.
	 * 
	 */
	public void setInvoiceShipToLine2Address(String invoiceShipToLine2Address) {
		this.invoiceShipToLine2Address = invoiceShipToLine2Address;
	}


	/**
	 * Gets the invoiceShipToLine3Address attribute.
	 * 
	 * @return - Returns the invoiceShipToLine3Address
	 * 
	 */
	public String getInvoiceShipToLine3Address() { 
		return invoiceShipToLine3Address;
	}

	/**
	 * Sets the invoiceShipToLine3Address attribute.
	 * 
	 * @param - invoiceShipToLine3Address The invoiceShipToLine3Address to set.
	 * 
	 */
	public void setInvoiceShipToLine3Address(String invoiceShipToLine3Address) {
		this.invoiceShipToLine3Address = invoiceShipToLine3Address;
	}


	/**
	 * Gets the invoiceCustomerNumber attribute.
	 * 
	 * @return - Returns the invoiceCustomerNumber
	 * 
	 */
	public String getInvoiceCustomerNumber() { 
		return invoiceCustomerNumber;
	}

	/**
	 * Sets the invoiceCustomerNumber attribute.
	 * 
	 * @param - invoiceCustomerNumber The invoiceCustomerNumber to set.
	 * 
	 */
	public void setInvoiceCustomerNumber(String invoiceCustomerNumber) {
		this.invoiceCustomerNumber = invoiceCustomerNumber;
	}


	/**
	 * Gets the invoiceShipToStateCode attribute.
	 * 
	 * @return - Returns the invoiceShipToStateCode
	 * 
	 */
	public String getInvoiceShipToStateCode() { 
		return invoiceShipToStateCode;
	}

	/**
	 * Sets the invoiceShipToStateCode attribute.
	 * 
	 * @param - invoiceShipToStateCode The invoiceShipToStateCode to set.
	 * 
	 */
	public void setInvoiceShipToStateCode(String invoiceShipToStateCode) {
		this.invoiceShipToStateCode = invoiceShipToStateCode;
	}


	/**
	 * Gets the invoiceShipToCountryCode attribute.
	 * 
	 * @return - Returns the invoiceShipToCountryCode
	 * 
	 */
	public String getInvoiceShipToCountryCode() { 
		return invoiceShipToCountryCode;
	}

	/**
	 * Sets the invoiceShipToCountryCode attribute.
	 * 
	 * @param - invoiceShipToCountryCode The invoiceShipToCountryCode to set.
	 * 
	 */
	public void setInvoiceShipToCountryCode(String invoiceShipToCountryCode) {
		this.invoiceShipToCountryCode = invoiceShipToCountryCode;
	}


	/**
	 * Gets the invoiceShipToCityName attribute.
	 * 
	 * @return - Returns the invoiceShipToCityName
	 * 
	 */
	public String getInvoiceShipToCityName() { 
		return invoiceShipToCityName;
	}

	/**
	 * Sets the invoiceShipToCityName attribute.
	 * 
	 * @param - invoiceShipToCityName The invoiceShipToCityName to set.
	 * 
	 */
	public void setInvoiceShipToCityName(String invoiceShipToCityName) {
		this.invoiceShipToCityName = invoiceShipToCityName;
	}


	/**
	 * Gets the invoiceShipToPostalCode attribute.
	 * 
	 * @return - Returns the invoiceShipToPostalCode
	 * 
	 */
	public String getInvoiceShipToPostalCode() { 
		return invoiceShipToPostalCode;
	}

	/**
	 * Sets the invoiceShipToPostalCode attribute.
	 * 
	 * @param - invoiceShipToPostalCode The invoiceShipToPostalCode to set.
	 * 
	 */
	public void setInvoiceShipToPostalCode(String invoiceShipToPostalCode) {
		this.invoiceShipToPostalCode = invoiceShipToPostalCode;
	}


	/**
	 * Gets the invoicePurchaseOrderNumber attribute.
	 * 
	 * @return - Returns the invoicePurchaseOrderNumber
	 * 
	 */
	public String getInvoicePurchaseOrderNumber() { 
		return invoicePurchaseOrderNumber;
	}

	/**
	 * Sets the invoicePurchaseOrderNumber attribute.
	 * 
	 * @param - invoicePurchaseOrderNumber The invoicePurchaseOrderNumber to set.
	 * 
	 */
	public void setInvoicePurchaseOrderNumber(String invoicePurchaseOrderNumber) {
		this.invoicePurchaseOrderNumber = invoicePurchaseOrderNumber;
	}


	/**
	 * Gets the invoiceHeaderInformation attribute.
	 * 
	 * @return - Returns the invoiceHeaderInformation
	 * 
	 */
	public ElectronicInvoiceRejectReason getInvoiceHeaderInformation() { 
		return invoiceHeaderInformation;
	}

	/**
	 * Sets the invoiceHeaderInformation attribute.
	 * 
	 * @param - invoiceHeaderInformation The invoiceHeaderInformation to set.
	 * @deprecated
	 */
	public void setInvoiceHeaderInformation(ElectronicInvoiceRejectReason invoiceHeaderInformation) {
		this.invoiceHeaderInformation = invoiceHeaderInformation;
	}

	/**
	 * Gets the accountsPayableElectronicInvoiceLoadSummary attribute.
	 * 
	 * @return - Returns the accountsPayableElectronicInvoiceLoadSummary
	 * 
	 */
	public ElectronicInvoiceLoadSummary getAccountsPayableElectronicInvoiceLoadSummary() { 
		return accountsPayableElectronicInvoiceLoadSummary;
	}

	/**
	 * Sets the accountsPayableElectronicInvoiceLoadSummary attribute.
	 * 
	 * @param - accountsPayableElectronicInvoiceLoadSummary The accountsPayableElectronicInvoiceLoadSummary to set.
	 * @deprecated
	 */
	public void setAccountsPayableElectronicInvoiceLoadSummary(ElectronicInvoiceLoadSummary accountsPayableElectronicInvoiceLoadSummary) {
		this.accountsPayableElectronicInvoiceLoadSummary = accountsPayableElectronicInvoiceLoadSummary;
	}

	/**
	 * Gets the epicPurchaseOrderDeliveryCampus attribute.
	 * 
	 * @return - Returns the epicPurchaseOrderDeliveryCampus
	 * 
	 */
	public Campus getEpicPurchaseOrderDeliveryCampus() { 
		return epicPurchaseOrderDeliveryCampus;
	}

	/**
	 * Sets the epicPurchaseOrderDeliveryCampus attribute.
	 * 
	 * @param - epicPurchaseOrderDeliveryCampus The epicPurchaseOrderDeliveryCampus to set.
	 * @deprecated
	 */
	public void setEpicPurchaseOrderDeliveryCampus(Campus epicPurchaseOrderDeliveryCampus) {
		this.epicPurchaseOrderDeliveryCampus = epicPurchaseOrderDeliveryCampus;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.invoiceHeaderInformationIdentifier != null) {
            m.put("invoiceHeaderInformationIdentifier", this.invoiceHeaderInformationIdentifier.toString());
        }
	    return m;
    }
}
