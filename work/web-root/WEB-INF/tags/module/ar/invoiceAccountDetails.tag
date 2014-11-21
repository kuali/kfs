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
<%@ attribute name="readOnly" required="false" description="If document is in read only mode"%>

<c:if test="${!empty KualiForm.document.invoiceGeneralDetail.proposalNumber}">
	<kul:tab tabTitle="Account Summary" defaultOpen="true" tabErrorKey="document.invoiceAccountDetails*">
		<c:set var="invoiceAccountDetailsAttributes" value="${DataDictionary.InvoiceAccountDetail.attributes}" />
		<c:set var="contractsGrantsInvoiceDocumentAttributes" value="${DataDictionary.ContractsGrantsInvoiceDocument.attributes}"/>

		<div class="tab-container" align="center">
			<h3>Account Summary</h3>
			<table cellpadding=0 class="datatable" summary="Invoice Account Details section">
				<tr>
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAccountDetailsAttributes.chartOfAccountsCode}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAccountDetailsAttributes.accountNumber}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAccountDetailsAttributes.budgetAmount}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAccountDetailsAttributes.expenditureAmount}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAccountDetailsAttributes.cumulativeAmount}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAccountDetailsAttributes.balanceAmount}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAccountDetailsAttributes.billedAmount}" useShortLabel="false" />
				</tr>
				<logic:iterate indexId="ctr" name="KualiForm" property="document.accountDetails" id="accountDetail">
					<tr>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAccountDetailsAttributes.chartOfAccountsCode}"
								property="document.accountDetails[${ctr}].chartOfAccountsCode" readOnly="true" /></td>
						<td class="datacell"><a
							href="${ConfigProperties.application.url}/kr/inquiry.do?chartOfAccountsCode=${KualiForm.document.accountDetails[ctr].chartOfAccountsCode}&businessObjectClassName=org.kuali.kfs.coa.businessobject.Account&methodToCall=start&accountNumber=${KualiForm.document.accountDetails[ctr].accountNumber}"
							target="_blank"> <kul:htmlControlAttribute attributeEntry="${invoiceAccountDetailsAttributes.accountNumber}"
									property="document.accountDetails[${ctr}].accountNumber" readOnly="true" />
						</a></td>						
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAccountDetailsAttributes.budgetAmount}"
								property="document.accountDetails[${ctr}].budgetAmount" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAccountDetailsAttributes.expenditureAmount}"
								property="document.accountDetails[${ctr}].expenditureAmount" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAccountDetailsAttributes.cumulativeAmount}"
								property="document.accountDetails[${ctr}].cumulativeAmount" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAccountDetailsAttributes.balanceAmount}"
								property="document.accountDetails[${ctr}].balanceAmount" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAccountDetailsAttributes.billedAmount}"
								property="document.accountDetails[${ctr}].adjustedCumExpenditures" readOnly="true" /></td>
					</tr>
				</logic:iterate>
				<c:if test="${fn:length(KualiForm.document.accountDetails) gt 1}">
					<tr>
						<td colspan="2"><b>Totals</b></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDocumentAttributes.budgetAmountTotal}" property="document.budgetAmountTotal" readOnly="true"/> </td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDocumentAttributes.expenditureAmountTotal}" property="document.expenditureAmountTotal" readOnly="true"/> </td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDocumentAttributes.cumulativeAmountTotal}" property="document.cumulativeAmountTotal" readOnly="true"/> </td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDocumentAttributes.balanceAmountTotal}" property="document.balanceAmountTotal" readOnly="true"/> </td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDocumentAttributes.billedAmountTotal}" property="document.billedAmountTotal" readOnly="true"/> </td>
					</tr>
				</c:if>
			</table>
		</div>
	</kul:tab>
</c:if>