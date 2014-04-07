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
<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>

<c:if test="${!empty KualiForm.document.proposalNumber}">
	<kul:tab tabTitle="Transmission Details" defaultOpen="true" tabErrorKey="document.invoiceAddressDetails*">
		<c:set var="invoiceAddressDetailsAttributes" value="${DataDictionary.InvoiceAddressDetail.attributes}" />

		<div class="tab-container" align="center">
			<h3>Transmission Details</h3>
			<table cellpadding=0 class="datatable" summary="Transmission Details section">
				<tr>
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAddressDetailsAttributes.customerAddressTypeCode}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAddressDetailsAttributes.customerAddressName}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAddressDetailsAttributes.customerInvoiceTemplateCode}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAddressDetailsAttributes.invoiceTransmissionMethodCode}" useShortLabel="false" />
				</tr>
				<logic:iterate indexId="ctr" name="KualiForm" property="document.invoiceAddressDetails" id="invoiceAddressDetail">
					<tr>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerAddressTypeCode}"
								property="document.invoiceAddressDetails[${ctr}].customerAddressTypeCode" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerAddressName}"
								property="document.invoiceAddressDetails[${ctr}].customerAddressName" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.customerInvoiceTemplateCode}"
								property="document.invoiceAddressDetails[${ctr}].customerInvoiceTemplateCode" readOnly="${readOnly}" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAddressDetailsAttributes.invoiceTransmissionMethodCode}"
								property="document.invoiceAddressDetails[${ctr}].invoiceTransmissionMethodCode" readOnly="${readOnly}" /></td>
					</tr>
				</logic:iterate>
			</table>
		</div>
		<SCRIPT type="text/javascript">
			var kualiForm = document.forms['KualiForm'];
			var kualiElements = kualiForm.elements;
		</SCRIPT>
	</kul:tab>
</c:if>
