<%--
 Copyright 2007-2013 The Kuali Foundation
 
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
<%@ attribute name="travelAdvanceProperty" required="true" type="java.lang.String" description="Name of the property which holds the path to the travel advance to render."%>
<c:set var="documentAttributes" value="${DataDictionary.TravelAuthorizationDocument.attributes}" />
<c:set var="travelAdvanceAttributes" value="${DataDictionary.TravelAdvance.attributes}" />
<c:set var="advanceAttributes" value="${DataDictionary.AdvancePaymentReason.attributes}" />
<c:set var="docType" value="${KualiForm.document.dataDictionaryEntry.documentTypeName }" />
<c:set var="policyDisabled" value="${!KualiForm.waitingOnTraveler && !fullEntryMode}" />
<c:set var="advancePolicyMode" value="${KualiForm.editingMode['advancePolicyEntry']}" scope="request"/>



<table cellpadding="0" cellspacing="0" class="datatable"
	summary="Travel Advance Section">
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.travelAdvanceRequested}" />
			</div>
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.travelAdvanceRequested}"
				property="${travelAdvanceProperty}.travelAdvanceRequested"
				readOnly="${!fullEntryMode && !conversionRateEntryMode}" />
		</td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.arCustomerId}" />
			</div>
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.arCustomerId}"
				property="${travelAdvanceProperty}.arCustomerId" readOnly="true" />
		</td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.arInvoiceDocNumber}" />
			</div>
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.arInvoiceDocNumber}"
				property="${travelAdvanceProperty}.arInvoiceDocNumber"
				readOnly="true" />
		</td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.dueDate}" />
			</div>
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.dueDate}"
				property="${travelAdvanceProperty}.dueDate" datePicker="true"
				readOnly="${!fullEntryMode}" />
		</td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.advancePaymentReasonCode}" />
			</div>
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.advancePaymentReasonCode}"
				property="${travelAdvanceProperty}.advancePaymentReasonCode"
				readOnly="${!fullEntryMode}" />
		</td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${travelAdvanceAttributes.travelAdvancePolicy}" />
			</div>
		</th>
		<td class="datacell"><kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.travelAdvancePolicy}"
				property="${travelAdvanceProperty}.travelAdvancePolicy"
				readOnly="${!advancePolicyMode}" />${KualiForm.policyURL}</td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.additionalJustification}" />
			</div>
		</th>
		<td class="datacell">
			<kul:htmlControlAttribute
				attributeEntry="${travelAdvanceAttributes.additionalJustification}"
				property="${travelAdvanceProperty}.additionalJustification"
				readOnly="${!fullEntryMode}" />
		</td>
	</tr>
	<c:if test="${clearAdvanceMode}">
		<tr>
			<td class="datacell" colspan="2">
				<div align="center"><html:image property="methodToCall.clearAdvance" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-clear1.gif" title="Clear Advance" alt="Clear Advance" styleClass="tinybutton" /></div>
			</td>
		</tr>
	</c:if>
</table>