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
<%@ attribute name="customerInvoiceDocumentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="propertyName" required="true"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<c:set var="customerInvoiceWriteoffLookupResultAttributes" value="${DataDictionary.CustomerInvoiceWriteoffLookupResult.attributes}" />

<div class="tab-container" align="center">
	<h3>Invoices to Writeoff</h3>
	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
		<thead>
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDocumentAttributes.documentNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDocumentAttributes.age}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDocumentAttributes.billingDate}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDocumentAttributes.sourceTotal}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDocumentAttributes.openAmount}" />
			</tr>
		</thead>
		<logic:iterate id="customerInvoiceDocument" name="KualiForm"
				property="${propertyName}.customerInvoiceDocuments" indexId="ctr">
			<ar:customerInvoiceWriteoffSummarySubResult
				customerInvoiceDocumentAttributes="${customerInvoiceDocumentAttributes}"
				propertyName="${propertyName}.customerInvoiceDocument[${ctr}]" />
		</logic:iterate>
	</table>
	<h3>Customer Note</h3>
	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
		<tr>
			<th align=right valign=middle class="bord-l-b">
				<div align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceWriteoffLookupResultAttributes.customerNote}" /></div>
			</th>
			<td>
				<c:if test="${KualiForm.sentToBatch}">
					<kul:htmlControlAttribute attributeEntry="${customerInvoiceWriteoffLookupResultAttributes.customerNote}" property="${propertyName}.customerNote" readOnly="true" />
				</c:if>
				<c:if test="${!KualiForm.sentToBatch}">
					<kul:htmlControlAttribute attributeEntry="${customerInvoiceWriteoffLookupResultAttributes.customerNote}" property="${propertyName}.customerNote" />
				</c:if>
			</td>
		</tr>	
	</table>
</div>	
