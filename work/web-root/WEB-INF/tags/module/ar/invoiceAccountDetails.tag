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
