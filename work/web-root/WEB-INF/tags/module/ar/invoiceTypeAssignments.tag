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
	<kul:tab tabTitle="Invoice Type Assignments" defaultOpen="true" tabErrorKey="document.agencyAddressDetails*">
		<c:set var="agencyAddressDetailsAttributes" value="${DataDictionary.InvoiceAgencyAddressDetail.attributes}" />

		<div class="tab-container" align="center">
			<h3>Invoice Type Assignments</h3>
			<table cellpadding=0 class="datatable" summary="Invoice Type Assignments section">
				<tr>
					<kul:htmlAttributeHeaderCell attributeEntry="${agencyAddressDetailsAttributes.agencyAddressTypeCode}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${agencyAddressDetailsAttributes.agencyAddressName}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${agencyAddressDetailsAttributes.agencyInvoiceTemplateCode}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${agencyAddressDetailsAttributes.preferredAgencyInvoiceTemplateCode}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${agencyAddressDetailsAttributes.invoiceIndicatorCode}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${agencyAddressDetailsAttributes.preferredInvoiceIndicatorCode}" useShortLabel="false" />
				</tr>
				<logic:iterate indexId="ctr" name="KualiForm" property="document.agencyAddressDetails" id="agencyAddressDetail">
					<tr>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${agencyAddressDetailsAttributes.agencyAddressTypeCode}"
								property="document.agencyAddressDetails[${ctr}].agencyAddressTypeCode" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${agencyAddressDetailsAttributes.agencyAddressName}"
								property="document.agencyAddressDetails[${ctr}].agencyAddressName" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${agencyAddressDetailsAttributes.agencyInvoiceTemplateCode}"
								property="document.agencyAddressDetails[${ctr}].agencyInvoiceTemplateCode" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${agencyAddressDetailsAttributes.preferredAgencyInvoiceTemplateCode}"
								property="document.agencyAddressDetails[${ctr}].preferredAgencyInvoiceTemplateCode" readOnly="${readOnly}" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${agencyAddressDetailsAttributes.invoiceIndicatorCode}"
								property="document.agencyAddressDetails[${ctr}].invoiceIndicatorCode" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${agencyAddressDetailsAttributes.preferredInvoiceIndicatorCode}"
								property="document.agencyAddressDetails[${ctr}].preferredInvoiceIndicatorCode" readOnly="${readOnly}" /></td>
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
