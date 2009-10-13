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
