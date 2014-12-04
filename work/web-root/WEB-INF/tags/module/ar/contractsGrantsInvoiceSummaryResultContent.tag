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

<c:set var="contractsGrantsInvoiceLookupResultAttributes" value="${DataDictionary.ContractsGrantsInvoiceLookupResult.attributes}" />
<c:set var="awardAttributes" value="${DataDictionary.Award.attributes}"/>

<div class="tab-container" align="center">
	<h3>Awards to be Invoiced</h3>
	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
		<thead>
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.proposalNumber}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.awardBeginningDate}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.awardEndingDate}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.billingFrequencyCode}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.instrumentTypeCode}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.invoicingOptionCode}" useShortLabel="false" hideRequiredAsterisk="true" />
				<kul:htmlAttributeHeaderCell attributeEntry="${awardAttributes.awardTotalAmount}" useShortLabel="false" hideRequiredAsterisk="true" />
			</tr>
		</thead>
		<logic:iterate id="awards" name="KualiForm" property="${requestScope.propertyName}.awards" indexId="ctr">
			<ar:contractsGrantsInvoiceSummarySubResult awardAttributes="${awardAttributes}" propertyName="${requestScope.propertyName}.awards[${ctr}]" />
		</logic:iterate>
	</table>
</div>
