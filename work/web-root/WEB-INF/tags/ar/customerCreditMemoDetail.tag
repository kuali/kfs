<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
<%@ attribute name="invPropertyName" required="true"
	description="Name of form property containing the customer invoice source accounting line."%>
<%@ attribute name="crmPropertyName" required="true"
	description="Name of form property containing the customer credit memo detail."%>
<%@ attribute name="cssClass" required="true"%>

<c:set var="customerInvoiceDetailAttributes" value="${DataDictionary.CustomerInvoiceDetail.attributes}" />
<c:set var="customerCreditMemoDetailAttributes" value="${DataDictionary.CustomerCreditMemoDetail.attributes}" /> 

<tr>
	<!--  Line Number -->
	<td align=right class="${cssClass}" rowspan="3">
		<kul:htmlControlAttribute
			attributeEntry="${customerCreditMemoDetailAttributes.referenceInvoiceItemNumber}"
			property="${crmPropertyName}.referenceInvoiceItemNumber"
			readOnly="true" />
	<!--  Quantity -->	
	<td align=right class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.invoiceItemQuantity}"
			property="${invPropertyName}.invoiceItemQuantity"
			readOnly="true" />
	
	<!--  Item Code -->		
	<td align=center class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.invoiceItemCode}"
			property="${invPropertyName}.invoiceItemCode"
			readOnly="true" />
			
	<!--  UOM -->
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.invoiceItemUnitOfMeasureCode}"
			property="${invPropertyName}.invoiceItemUnitOfMeasureCode"
			readOnly="true" />
			
	<!--  Description -->
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.invoiceItemDescription}"
			property="${invPropertyName}.invoiceItemDescription"
			readOnly="true" />

	<!--  Unit Price -->
	<td align=right class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.invoiceItemUnitPrice}"
			property="${invPropertyName}.invoiceItemUnitPrice"
			readOnly="true" />
			
	<!--  Item Amount -->
	<td align=right class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.amount}"
			property="${invPropertyName}.amount"
			readOnly="true" />

	<!--  Tax Amount -->
	<td align=right class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.invoiceItemTaxAmount}"
			property="${invPropertyName}.invoiceItemTaxAmount"
			readOnly="true" />
			
	<!--  Total Amount -->
	<td align=right class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${customerCreditMemoDetailAttributes.invoiceLineTotalAmount}"
			property="${crmPropertyName}.invoiceLineTotalAmount"
			readOnly="true" />
	
	<!--  Open Invoice Amount -->
	<td align=right class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${customerCreditMemoDetailAttributes.invoiceOpenItemAmount}"
			property="${crmPropertyName}.invoiceOpenItemAmount"
			readOnly="true" />

	<!--  Actions -->
	<td rowspan="3"><div align="center" class="middle" >
		<html:image property=""
	    	src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif"
	    	title="Recalculate Credit Memo Line Amounts"
	    	alt="Recalculate Credit Memo Line Amounts"
	        styleClass="tinybutton" />
	    &nbsp;
		<html:image property=""
	    	src="${ConfigProperties.externalizable.images.url}tinybutton-load.gif"
	    	title="Refresh Credit Memo Line"
	    	alt="Refresh Credit Memo Line"
	        styleClass="tinybutton" />
	</div>     
	</td>
</tr>
<tr>			
	<!--  CRM Quantity -->
	<td align=right class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${customerCreditMemoDetailAttributes.creditMemoItemQuantity}"
			property="${crmPropertyName}.creditMemoItemQuantity"
			readOnly="false" />
	</td>
	<td class="${cssClass}" />
	
	<!--  CRM 4 empty columns -->
	<td class="${cssClass}" colspan="4" />
	
	<!--  CRM Item Amount -->
	<td align=right class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${customerCreditMemoDetailAttributes.creditMemoItemTotalAmount}"
			property="${crmPropertyName}.creditMemoItemTotalAmount"
			readOnly="false" />
	</td>
	
	<!--  CRM Tax Amount -->
	<td align=right class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${customerCreditMemoDetailAttributes.creditMemoItemTaxAmount}"
			property="${crmPropertyName}.creditMemoItemTaxAmount"
			readOnly="true" />
	</td>
	<td class="${cssClass}" />
	
	<!--  CRM Total Amount -->
	<td align=right class="${cssClass}">
		<kul:htmlControlAttribute 
			attributeEntry="${customerCreditMemoDetailAttributes.creditMemoLineTotalAmount}"
			property="${crmPropertyName}.creditMemoLineTotalAmount"
			readOnly="true" />
	</td>
	
	<!--  CRM 1 empty column -->
	<td class="${cssClass}" />
<tr/>

<ar:customerCreditMemoDetailAccountingInfo
	invPropertyName="${invPropertyName}"
	cssClass="${cssClass}" />
