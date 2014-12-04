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
<%@ attribute name="invPropertyName" required="true"
	description="Name of form property containing the customer invoice source accounting line."%>
<%@ attribute name="cssClass" required="true"%>
<%@ attribute name="rowHeader" required="true"
	description="The value of the header cell of this row.
              It would be 'add:' or the number of this row's accounting line within its group."%>

<c:set var="customerInvoiceDetailAttributes" value="${DataDictionary.CustomerInvoiceDetail.attributes}" />

<tr>
	<!--  Line Number -->
	<th class="${cssClass}" style="text-align:right">
		${rowHeader}:
	</th>
			
	<!--  Quantity -->	
	<td class="${cssClass}" style="text-align:right" >
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.invoiceItemQuantity}"
			property="${invPropertyName}.invoiceItemQuantity"
			readOnly="true" />
	</td>

			
	<!--  Description -->
	<td class="${cssClass}" >
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.invoiceItemDescription}"
			property="${invPropertyName}.invoiceItemDescription"
			readOnly="true" />
	</td>

	<!--  Open Amount -->
	<td class="${cssClass}" style="text-align:right" >
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.amountOpen}"
			property="${invPropertyName}.amountOpen"
			readOnly="true" />
	</td>

	<!--  Open Amount -->
	<td class="${cssClass}" style="text-align:right" >
		<kul:htmlControlAttribute
			attributeEntry="${customerInvoiceDetailAttributes.writeoffAmount}"
			property="${invPropertyName}.writeoffAmount"
			readOnly="true" />
	</td>
			
</tr>
