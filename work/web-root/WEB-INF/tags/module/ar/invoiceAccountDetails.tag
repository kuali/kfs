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
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAccountDetailsAttributes.totalBudget}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAccountDetailsAttributes.invoiceAmount}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAccountDetailsAttributes.cumulativeExpenditures}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAccountDetailsAttributes.budgetRemaining}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceAccountDetailsAttributes.totalAmountBilledToDate}" useShortLabel="false" />
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
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAccountDetailsAttributes.totalBudget}"
								property="document.accountDetails[${ctr}].totalBudget" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAccountDetailsAttributes.invoiceAmount}"
								property="document.accountDetails[${ctr}].invoiceAmount" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAccountDetailsAttributes.cumulativeExpenditures}"
								property="document.accountDetails[${ctr}].cumulativeExpenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAccountDetailsAttributes.budgetRemaining}"
								property="document.accountDetails[${ctr}].budgetRemaining" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceAccountDetailsAttributes.totalAmountBilledToDate}"
								property="document.accountDetails[${ctr}].adjustedCumExpenditures" readOnly="true" /></td>
					</tr>
				</logic:iterate>
				<c:if test="${fn:length(KualiForm.document.accountDetails) gt 1}">
					<tr>
						<td colspan="2"><b>Totals</b></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDocumentAttributes.totalBudgetAmount}" property="document.totalBudgetAmount" readOnly="true"/> </td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDocumentAttributes.totalInvoiceAmount}" property="document.totalInvoiceAmount" readOnly="true"/> </td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDocumentAttributes.totalCumulativeExpenditures}" property="document.totalCumulativeExpenditures" readOnly="true"/> </td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDocumentAttributes.totalBudgetRemaining}" property="document.totalBudgetRemaining" readOnly="true"/> </td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDocumentAttributes.totalAmountBilledToDate}" property="document.totalAmountBilledToDate" readOnly="true"/> </td>
					</tr>
				</c:if>
			</table>
		</div>
	</kul:tab>
</c:if>
