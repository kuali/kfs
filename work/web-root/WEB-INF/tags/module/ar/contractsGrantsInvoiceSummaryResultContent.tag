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
<%@ attribute name="awardAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="propertyName" required="true" description="The DataDictionary entry containing attributes for this row's fields."%>

<c:set var="contractsGrantsInvoiceLookupResultAttributes" value="${DataDictionary.ContractsGrantsInvoiceLookupResult.attributes}" />

<div class="tab-container" align="center">
	<h3>Awards to be Invoiced</h3>
	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
		<thead>
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.proposalNumber}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.awardBeginningDate}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.awardEndingDate}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.preferredBillingFrequency}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.instrumentTypeCode}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.invoicingOptions}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.awardTotalAmount}" useShortLabel="false" hideRequiredAsterisk="true" />
			</tr>
		</thead>
		<logic:iterate id="awards" name="KualiForm" property="${propertyName}.awards" indexId="ctr">
			<ar:contractsGrantsInvoiceSummarySubResult awardAttributes="${awardAttributes}" propertyName="${propertyName}.awards[${ctr}]" />
		</logic:iterate>
	</table>
</div>
