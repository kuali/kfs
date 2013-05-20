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

<c:set var="referralToCollectionsDetailAttributes" value="${DataDictionary.ReferralToCollectionsDetail.attributes}" />
<c:set var="referralToCollectionsDocumentAttributes" value="${DataDictionary.ReferralToCollectionsDocument.attributes}" />
<c:set var="cgInvoiceAttributes" value="${DataDictionary['ContractsGrantsInvoiceDocument'].attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<!-- If there are no bills, this section should not be displayed -->
<kul:tab tabTitle="Refer to Collections" defaultOpen="true" tabErrorKey="document.referralToCollectionsDocument*">
	<c:set var="referralToCollectionsLookupResultAttributes" value="${DataDictionary.ReferralToCollectionsLookupResult.attributes}" />
	<%@ attribute name="invPropertyName" required="true" description="Name of form property containing the customer invoice source accounting line."%>
	<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
	<%-- <c:set var="detailPropertyName" value="document.contractsGrantsLOCReviewDetails[${ctr}]" /> --%>

	<div class="tab-container" align="center">
		<h3>Customer Information</h3>

<table style="width: 100%; border: none" cellpadding="0" cellspacing="0" class="datatable">

	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDocumentAttributes.agencyNumber}" useShortLabel="false"
			hideRequiredAsterisk="true" width="50%" align="right" />
		<td class="datacell" width="50%"><kul:htmlControlAttribute attributeEntry="${referralToCollectionsDocumentAttributes.agencyNumber}"
			property="document.agencyNumber" readOnly="true" /></td>
	</tr>
	
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDocumentAttributes.agencyFullName}" useShortLabel="false"
			hideRequiredAsterisk="true" align="right" />
		<td class="datacell"><kul:htmlControlAttribute attributeEntry="${referralToCollectionsDocumentAttributes.agencyFullName}"
			property="document.agencyFullName" readOnly="true" /></td>	
	</tr>
		
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDocumentAttributes.customerNumber}" useShortLabel="false"
			hideRequiredAsterisk="true" align="right" />
		<td class="datacell"><kul:htmlControlAttribute attributeEntry="${referralToCollectionsDocumentAttributes.customerNumber}"
			property="document.customerNumber" readOnly="true" /></td>
	</tr>
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDocumentAttributes.customerName}" useShortLabel="false"
			hideRequiredAsterisk="true" align="right" />
		<td class="datacell"><kul:htmlControlAttribute attributeEntry="${referralToCollectionsDocumentAttributes.customerName}"
			property="document.customerName" readOnly="true" /></td>
	</tr>
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDocumentAttributes.customerTypeCode}" useShortLabel="false"
			hideRequiredAsterisk="true" align="right" />
		<td class="datacell"><kul:htmlControlAttribute attributeEntry="${referralToCollectionsDocumentAttributes.customerTypeCode}"
			property="document.customerTypeCode" readOnly="true" /></td>
	</tr>
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDocumentAttributes.referralTypeCode}" useShortLabel="false"
			hideRequiredAsterisk="false" align="right" />
		<td class="datacell"><kul:htmlControlAttribute attributeEntry="${referralToCollectionsDocumentAttributes.referralTypeCode}"
			property="document.referralTypeCode" readOnly="${readOnly}" /></td>
	</tr>
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDocumentAttributes.collectionStatusCode}" useShortLabel="false"
			hideRequiredAsterisk="true" align="right" />
		<td class="datacell"><kul:htmlControlAttribute attributeEntry="${referralToCollectionsDocumentAttributes.collectionStatusCode}"
			property="document.collectionStatusCode" readOnly="${readOnly}" /></td>
	</tr>
		
</table>
</div>

	<div class="tab-container" align="center">
		<h3>Invoices Referred To Collections</h3>
	
		<table style="width: 100%; border: none" cellpadding="0" cellspacing="0" class="datatable" border="0">
		
			<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDetailAttributes.proposalNumber}" useShortLabel="false"
				hideRequiredAsterisk="true" align="center" />
			<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDetailAttributes.accountNumber}" useShortLabel="false"
				hideRequiredAsterisk="true" align="center" />
			<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDetailAttributes.invoiceNumber}" useShortLabel="false"
				hideRequiredAsterisk="true" align="center" />
			<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDetailAttributes.billingDate}" useShortLabel="false"
				hideRequiredAsterisk="true" align="center" />
			<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDetailAttributes.invoiceTotal}" useShortLabel="false"
				hideRequiredAsterisk="true" align="center" />
			<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDetailAttributes.invoiceBalance}" useShortLabel="false"
				hideRequiredAsterisk="true" align="center" />
			<kul:htmlAttributeHeaderCell attributeEntry="${referralToCollectionsDetailAttributes.age}" useShortLabel="false"
				hideRequiredAsterisk="true" align="center" />
			<kul:htmlAttributeHeaderCell attributeEntry="${cgInvoiceAttributes.finalDispositionCode}" useShortLabel="false"
				hideRequiredAsterisk="true" align="center" />
			<kul:htmlAttributeHeaderCell literalLabel="Actions" horizontal="true" align="center" />
		
				<logic:iterate indexId="ctr" name="KualiForm" property="document.referralToCollectionsDetails" id="rocDocumentDetails">
						<tr>
								<td class="datacell"><kul:htmlControlAttribute attributeEntry="${referralToCollectionsDetailAttributes.proposalNumber}"
										property="document.referralToCollectionsDetails[${ctr}].proposalNumber" readOnly="true" /></td>
								<td class="datacell"><kul:htmlControlAttribute attributeEntry="${referralToCollectionsDetailAttributes.accountNumber}"
										property="document.referralToCollectionsDetails[${ctr}].accountNumber" readOnly="true" /></td>
								<td class="datacell">
								<a href="${ConfigProperties.workflow.url}/DocHandler.do?docId=${KualiForm.document.referralToCollectionsDetails[ctr].invoiceNumber}&command=displayDocSearchView"
									target="blank"> <kul:htmlControlAttribute attributeEntry="${referralToCollectionsDetailAttributes.invoiceNumber}"
										property="document.referralToCollectionsDetails[${ctr}].invoiceNumber" readOnly="true" /></a></td>
								<td class="datacell"><kul:htmlControlAttribute attributeEntry="${referralToCollectionsDetailAttributes.billingDate}"
										property="document.referralToCollectionsDetails[${ctr}].billingDate" readOnly="true" /></td>
								<td class="datacell"><kul:htmlControlAttribute attributeEntry="${referralToCollectionsDetailAttributes.invoiceTotal}"
										property="document.referralToCollectionsDetails[${ctr}].invoiceTotal" readOnly="true" /></td>
								<td class="datacell"><kul:htmlControlAttribute attributeEntry="${referralToCollectionsDetailAttributes.invoiceBalance}"
										property="document.referralToCollectionsDetails[${ctr}].invoiceBalance" readOnly="true" /></td>
								<td class="datacell"><kul:htmlControlAttribute attributeEntry="${referralToCollectionsDetailAttributes.age}"
										property="document.referralToCollectionsDetails[${ctr}].age" readOnly="true" /></td>
								<td class="datacell"><kul:htmlControlAttribute attributeEntry="${cgInvoiceAttributes.finalDispositionCode}"
										property="document.referralToCollectionsDetails[${ctr}].finalDispositionCode" readOnly="${readOnly}" /></td>
								<td class="datacell">
									<html:image property="methodToCall.deleteInvoice.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="Delete Invoice" title="Delete Invoice" styleClass="tinybutton" />
								</td>
						</tr>
		
			</logic:iterate>
		</table>
	</div>
</kul:tab>