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


<!-- If there are no bills, this section should not be displayed -->

<c:set var="contractsGrantsLOCReviewDetailAttributes" value="${DataDictionary.ContractsGrantsLOCReviewDetail.attributes}" />

<%@ attribute name="proposalNumberValue" required="true" description="Name of form property containing the customer invoice source accounting line."%>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<table style="width: 100%; border: none" cellpadding="0" cellspacing="0" class="datatable">

	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.awardDocumentNumber}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.agencyNumber}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.customerNumber}" useShortLabel="false"
		hideRequiredAsterisk="true" width="5%" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.awardBeginningDate}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.awardEndingDate}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.awardBudgetAmount}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.locAmount}" useShortLabel="false" hideRequiredAsterisk="true"
		align="center" />

	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.claimOnCashBalance}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.amountToDraw}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.amountAvailableToDraw}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />

	<logic:iterate indexId="ctr" name="KualiForm" property="document.headerReviewDetails" id="headerReviewDetail">

		<c:if test="${KualiForm.document.headerReviewDetails[ctr].proposalNumber == proposalNumberValue }">

			<tr>

				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.awardDocumentNumber}"
						property="document.headerReviewDetails[${ctr}].awardDocumentNumber" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.agencyNumber}"
						property="document.headerReviewDetails[${ctr}].agencyNumber" readOnly="true" /></td>
				<td class="datacell" width="5%"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.customerNumber}"
						property="document.headerReviewDetails[${ctr}].customerNumber" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.awardBeginningDate}"
						property="document.headerReviewDetails[${ctr}].awardBeginningDate" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.awardEndingDate}"
						property="document.headerReviewDetails[${ctr}].awardEndingDate" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.awardBudgetAmount}"
						property="document.headerReviewDetails[${ctr}].awardBudgetAmount" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.locAmount}"
						property="document.headerReviewDetails[${ctr}].locAmount" readOnly="true" /></td>

				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.claimOnCashBalance}"
						property="document.headerReviewDetails[${ctr}].claimOnCashBalance" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.amountToDraw}"
						property="document.headerReviewDetails[${ctr}].amountToDraw" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.amountAvailableToDraw}"
						property="document.headerReviewDetails[${ctr}].amountAvailableToDraw" readOnly="true" /></td>


			</tr>

			<%-- generate unique tab key from invPropertyName --%>
			<c:set var="invPropertyName" value="document.headerReviewDetails[${ctr}]" />
			<c:set var="lineNumber" value="${ctr }" />
		</c:if>
	</logic:iterate>
</table>

<c:set var="tabKey" value="${kfunc:generateTabKey(invPropertyName)}" />
<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}" />

<%-- default to closed --%>
<c:choose>
	<c:when test="${empty currentTab}">
		<c:set var="isOpen" value="false" />
	</c:when>
	<c:when test="${!empty currentTab}">
		<c:set var="isOpen" value="${currentTab == 'OPEN'}" />
	</c:when>
</c:choose>

<table style="width: 100%; border: none" cellpadding="0" cellspacing="0" class="datatable">
	<tr>
		<td class="tab-subhead" style="border-right: none;">Accounts <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
				<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide"
					title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
			</c:if> <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
				<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show"
					title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
			</c:if> <%-- display the recalculate button--%> <html:image property="methodToCall.recalculateAmountToDraw.line${lineNumber}"
				src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif" title="Recalculate Amount to Draw" alt="Recalculate Amount to Draw"
				styleClass="tinybutton" />
		</td>
	</tr>
</table>


<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
	<div style="display: block;" id="tab-${tabKey}-div" class="accountsInfo">
</c:if>
<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
	<div style="display: none;" id="tab-${tabKey}-div" class="accountsInfo">
</c:if>

<table style="width: 100%; border: none" cellpadding="0" cellspacing="0" class="datatable" border="0">

	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.accountDescription}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.chartOfAccountsCode}" useShortLabel="false"
		hideRequiredAsterisk="true" width="5%" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.accountNumber}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.accountExpirationDate}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.awardBudgetAmount}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.claimOnCashBalance}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.amountToDraw}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLOCReviewDetailAttributes.fundsNotDrawn}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />


	<logic:iterate indexId="ctr" name="KualiForm" property="document.accountReviewDetails" id="accountReviewDetail">
		<c:if test="${KualiForm.document.accountReviewDetails[ctr].proposalNumber == proposalNumberValue }">

			<tr>

				</td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.accountDescription}"
						property="document.accountReviewDetails[${ctr}].accountDescription" readOnly="true" /></td>
				<td class="datacell" width="5%"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.chartOfAccountsCode}"
						property="document.accountReviewDetails[${ctr}].chartOfAccountsCode" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.accountNumber}"
						property="document.accountReviewDetails[${ctr}].accountNumber" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.accountExpirationDate}"
						property="document.accountReviewDetails[${ctr}].accountExpirationDate" readOnly="true" />
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.awardBudgetAmount}"
						property="document.accountReviewDetails[${ctr}].awardBudgetAmount" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.claimOnCashBalance}"
						property="document.accountReviewDetails[${ctr}].claimOnCashBalance" readOnly="true" /></td>

				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.amountToDraw}"
						property="document.accountReviewDetails[${ctr}].amountToDraw" readOnly="false" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLOCReviewDetailAttributes.fundsNotDrawn}"
						property="document.accountReviewDetails[${ctr}].fundsNotDrawn" readOnly="true" /></td>
			</tr>
		</c:if>
	</logic:iterate>
</table>


