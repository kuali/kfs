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
            
<c:set var="customerInvoiceDetailAttributes" value="${DataDictionary.CustomerInvoiceDetail.attributes}" />
<c:set var="documentAttributes" value="${DataDictionary.CustomerInvoiceWriteoffDocument.attributes}" />            
              
<kul:tab tabTitle="Invoice Items" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_WRITEOFF_DETAILS_ERRORS}">
    <div class="tab-container" align=center>		
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Items">
            <tr>
            	<td colspan="5" class="subhead">Invoice Items</td>
            </tr>
			<tr>
			    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemQuantity}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemDescription}" hideRequiredAsterisk="true" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.amountOpen}" hideRequiredAsterisk="true" />				
				<kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.writeoffAmount}" hideRequiredAsterisk="true" />
			</tr>
			<logic:iterate
				id="customerInvoiceDetail"
				name="KualiForm"
				property="document.customerInvoiceDetailsForWriteoff"
				indexId="ctr">
				<ar:customerInvoiceWriteoffDetail
					rowHeader="${ctr+1}"
					invPropertyName="document.customerInvoiceDetailsForWriteoff[${ctr}]"
	        		cssClass="datacell" />
			</logic:iterate>
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.invoiceWriteoffAmount}" hideRequiredAsterisk="true" align="right" colspan="4"/>
				<!--  Customer Credit Memo Total Item Amount -->
				<td class="total-line" style="border-left: 0px;">
					<strong>${KualiForm.document.invoiceWriteoffAmount}</strong>
				</td>
			</tr> 
    	</table>
    </div>
</kul:tab>
