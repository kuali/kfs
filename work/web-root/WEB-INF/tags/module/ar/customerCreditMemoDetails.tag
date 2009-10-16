<%--
 Copyright 2006-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="readOnly" required="true" description="used to hide/show recalculate/refresh buttons" %>

<c:set var="documentAttributes" value="${DataDictionary.CustomerCreditMemoDocument.attributes}" />              
<c:set var="customerInvoiceDetailAttributes" value="${DataDictionary.CustomerInvoiceDetail.attributes}" />
<c:set var="customerCreditMemoDetailAttributes" value="${DataDictionary.CustomerCreditMemoDetail.attributes}" />   
<c:set var="salesTaxEnabled" value="${(not empty KualiForm.editingMode['salesTaxEnabled'])}" />
    

<kul:tab tabTitle="Items" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_CREDIT_MEMO_DETAILS_ERRORS}">
    <div class="tab-container" align=center>		
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Items">
            <tr>
            <!--  If readOnly mode -> hide the column 'Actions' -->
            <c:if test="${readOnly}" >
            	<td colspan="11" class="subhead">Invoice Items</td>
            </c:if>
            <!--  If not readOnly mode -> show the column 'Actions' -->
            <c:if test="${not readOnly}" >
                <td colspan="12" class="subhead">Invoice Items</td>
            </c:if>
            </tr>
			<tr>
			    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemQuantity}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemCode}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemUnitOfMeasureCode}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemDescription}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemUnitPrice}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerCreditMemoDetailAttributes.creditMemoItemTotalAmount}" hideRequiredAsterisk="true" />
			    <c:if test="${salesTaxEnabled}">
				    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemTaxAmount}" hideRequiredAsterisk="true" />
			    </c:if>
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerCreditMemoDetailAttributes.invoiceLineTotalAmount}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerCreditMemoDetailAttributes.invoiceOpenItemQuantity}" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${customerCreditMemoDetailAttributes.invoiceOpenItemAmount}" hideRequiredAsterisk="true" />				
			    <!--  If not readOnly mode -> show the column 'Actions' -->
            	<c:if test="${not readOnly}" >
                	<kul:htmlAttributeHeaderCell literalLabel="Actions" />
            	</c:if>
			</tr>
			<logic:iterate
				id="customerCreditMemoDetail"
				name="KualiForm"
				property="document.creditMemoDetails"
				indexId="ctr">
		        <c:set var="displayOrangeFlower" value="${KualiForm.document.creditMemoDetails[ctr].creditMemoLineTotalAmount != 0}" />
		        <c:set var="isInvoiceOpenItemQuantityZero" value="${KualiForm.document.creditMemoDetails[ctr].invoiceOpenItemQuantityZero}" />
				<ar:customerCreditMemoDetail
					rowHeader="${ctr+1}"
					invPropertyName="document.invoice.customerInvoiceDetailsWithoutDiscounts[${ctr}]"
					crmPropertyName="document.creditMemoDetails[${ctr}]" 
	        		refreshMethod="refreshCustomerCreditMemoDetail.line${ctr}"
	        		recalculateMethod="recalculateCustomerCreditMemoDetail.line${ctr}"
	        		displayOrangeFlower="${displayOrangeFlower}"
                    isInvoiceOpenItemQuantityZero="${isInvoiceOpenItemQuantityZero}"
	        		cssClass="datacell"
	        		readOnly="${readOnly}" />
			</logic:iterate>
			<tr>
				<td class="total-line" colspan="6">
					<strong>Credit Memo Total:</strong>
				</td>
				<!--  Customer Credit Memo Total Item Amount -->
				<td class="total-line">
					<strong>${KualiForm.document.currencyFormattedCrmTotalItemAmount}</strong>
				</td>
				<!-- Customer Credit Memo Total Tax Amount -->
				<td class="total-line">
					<strong>${KualiForm.document.currencyFormattedCrmTotalTaxAmount}</strong>
				</td>
				<!--  Customer Credit Memo Total Amount -->
				<td class="total-line">
					<strong>${KualiForm.document.currencyFormattedCrmTotalAmount}</strong>
				</td>
				<td />
				<td />
				<!--  If not readOnly mode -> show Recalculate/Refresh buttons for the total line -->
				<c:if test="${not readOnly}" >
					<td><div align="center" valign="middle" >
						<html:image property="methodToCall.recalculateCustomerCreditMemoDocument"
   							src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif"
   							title="Recalculate Credit Memo Line Amounts"
   							alt="Recalculate Credit Memo Line Amounts"
                           	styleClass="tinybutton" />
	                	&nbsp;
						<html:image property="methodToCall.refreshCustomerCreditMemoDocument"
   							src="${ConfigProperties.externalizable.images.url}tinybutton-refresh.gif"
   							title="Refresh Credit Memo Lines"
   							alt="Refresh Credit Memo Lines"
                           	styleClass="tinybutton" />
	            	</div>     
					</td>
				</c:if>
			</tr> 
    	</table>
    </div>
</kul:tab>
