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

<c:set var="contractsGrantsLetterOfCreditReviewDetailAttributes" value="${DataDictionary.ContractsGrantsLetterOfCreditReviewDetail.attributes}" />

<%@ attribute name="proposalNumberValue" required="true" description="Name of form property containing the customer invoice source accounting line."%>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<c:set var="hideRecalculateButton"
	value="${KualiForm.editingMode['hideRecalculateButton']}" scope="request" />
	
<c:set var="disableAmountToDraw"
	value="${KualiForm.editingMode['disableAmountToDraw']}" scope="request" />

<table style="width: 100%; border: none" cellpadding="0" cellspacing="0" class="datatable">

	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardDocumentNumber}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.agencyNumber}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.customerNumber}" useShortLabel="false"
		hideRequiredAsterisk="true" width="5%" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardBeginningDate}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardEndingDate}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardBudgetAmount}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.letterOfCreditAmount}" useShortLabel="false" hideRequiredAsterisk="true"
		align="center" />

	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.claimOnCashBalance}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountToDraw}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountAvailableToDraw}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />

	<logic:iterate indexId="ctr" name="KualiForm" property="document.headerReviewDetails" id="headerReviewDetail">

		<c:if test="${KualiForm.document.headerReviewDetails[ctr].proposalNumber == proposalNumberValue }">

			<tr>

				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardDocumentNumber}"
						property="document.headerReviewDetails[${ctr}].awardDocumentNumber" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.agencyNumber}"
						property="document.headerReviewDetails[${ctr}].agencyNumber" readOnly="true" /></td>
				<td class="datacell" width="5%"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.customerNumber}"
						property="document.headerReviewDetails[${ctr}].customerNumber" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardBeginningDate}"
						property="document.headerReviewDetails[${ctr}].awardBeginningDate" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardEndingDate}"
						property="document.headerReviewDetails[${ctr}].awardEndingDate" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardBudgetAmount}"
						property="document.headerReviewDetails[${ctr}].awardBudgetAmount" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.letterOfCreditAmount}"
						property="document.headerReviewDetails[${ctr}].letterOfCreditAmount" readOnly="true" /></td>

				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.claimOnCashBalance}"
						property="document.headerReviewDetails[${ctr}].claimOnCashBalance" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountToDraw}"
						property="document.headerReviewDetails[${ctr}].amountToDraw" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountAvailableToDraw}"
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
			</c:if><c:if test="${hideRecalculateButton != 'true' && hideRecalculateButton != 'TRUE'}"> <%-- display the recalculate button--%> <html:image property="methodToCall.recalculateAmountToDraw.line${lineNumber}"
				src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif" title="Recalculate Amount to Draw" alt="Recalculate Amount to Draw"
				styleClass="tinybutton" /></c:if>
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

	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.accountDescription}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.chartOfAccountsCode}" useShortLabel="false"
		hideRequiredAsterisk="true" width="5%" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.accountNumber}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.accountExpirationDate}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardBudgetAmount}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.claimOnCashBalance}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />
		
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountToDraw}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />		
	
	<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.fundsNotDrawn}" useShortLabel="false"
		hideRequiredAsterisk="true" align="center" />


	<logic:iterate indexId="ctr" name="KualiForm" property="document.accountReviewDetails" id="accountReviewDetail">
		<c:if test="${KualiForm.document.accountReviewDetails[ctr].proposalNumber == proposalNumberValue }">

			<tr>

				</td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.accountDescription}"
						property="document.accountReviewDetails[${ctr}].accountDescription" readOnly="true" /></td>
				<td class="datacell" width="5%"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.chartOfAccountsCode}"
						property="document.accountReviewDetails[${ctr}].chartOfAccountsCode" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.accountNumber}"
						property="document.accountReviewDetails[${ctr}].accountNumber" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.accountExpirationDate}"
						property="document.accountReviewDetails[${ctr}].accountExpirationDate" readOnly="true" />
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.awardBudgetAmount}"
						property="document.accountReviewDetails[${ctr}].awardBudgetAmount" readOnly="true" /></td>
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.claimOnCashBalance}"
						property="document.accountReviewDetails[${ctr}].claimOnCashBalance" readOnly="true" /></td>
						
				<c:choose>
				<c:when test="${disableAmountToDraw != 'true' && disableAmountToDraw != 'TRUE'}">
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountToDraw}"
						property="document.accountReviewDetails[${ctr}].amountToDraw" readOnly="false" /></td>
						
				</c:when>
				
				<c:when test="${disableAmountToDraw}">
				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.amountToDraw}"
						property="document.accountReviewDetails[${ctr}].amountToDraw" readOnly="true" /></td>
						
				</c:when>
						</c:choose>
		

				<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsLetterOfCreditReviewDetailAttributes.fundsNotDrawn}"
						property="document.accountReviewDetails[${ctr}].fundsNotDrawn" readOnly="true" /></td>
			</tr>
		</c:if>
	</logic:iterate>
</table>


