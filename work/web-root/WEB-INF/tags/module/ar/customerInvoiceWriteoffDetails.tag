<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
