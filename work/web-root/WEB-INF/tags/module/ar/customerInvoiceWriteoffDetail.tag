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
