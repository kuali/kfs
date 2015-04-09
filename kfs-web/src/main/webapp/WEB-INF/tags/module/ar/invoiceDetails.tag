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
<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>

<c:if test="${!empty KualiForm.document.invoiceGeneralDetail.proposalNumber}">
	<kul:tab tabTitle="Invoice Details" defaultOpen="true" tabErrorKey="document.invoiceDetails*,document.prorateWarning">
		<c:set var="contractsGrantsInvoiceDetailAttributes" value="${DataDictionary.ContractsGrantsInvoiceDetail.attributes}" />

		<div class="tab-container" align="center">
			<h3>Invoice Details</h3>
			<table cellpadding=0 class="datatable" summary="Invoice Details section">
				<tr>
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes['costCategory.categoryName']}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalBudget}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.invoiceAmount}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.cumulativeExpenditures}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.budgetRemaining}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalPreviouslyBilled}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalAmountBilledToDate}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${contractsGrantsInvoiceDetailAttributes.amountRemainingToBill}" useShortLabel="false" />
				</tr>
				<c:if test="${!empty KualiForm.document.invoiceDetails}">
					<!-- If the categories are not retrieved, then its better not to display the total fields in this section. -->
					<logic:iterate indexId="ctr" name="KualiForm" property="document.invoiceDetails" id="invoiceDetail">
						<tr>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes['costCategory.categoryName']}"
									property="document.invoiceDetails[${ctr}].costCategory.categoryName" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalBudget}"
									property="document.invoiceDetails[${ctr}].totalBudget" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.invoiceAmount}"
									property="document.invoiceDetails[${ctr}].invoiceAmount" readOnly="${readOnly}" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.cumulativeExpenditures}"
									property="document.invoiceDetails[${ctr}].cumulativeExpenditures" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.budgetRemaining}"
									property="document.invoiceDetails[${ctr}].budgetRemaining" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalPreviouslyBilled}"
									property="document.invoiceDetails[${ctr}].totalPreviouslyBilled" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalAmountBilledToDate}"
									property="document.invoiceDetails[${ctr}].totalAmountBilledToDate" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.amountRemainingToBill}"
									property="document.invoiceDetails[${ctr}].amountRemainingToBill" readOnly="true" /></td>
						</tr>
					</logic:iterate>
					<tr>
						<td class="datacell"><b> <bean:message key="${ArKeyConstants.CONTRACTS_GRANTS_INVOICE_DETAILS_DIRECT_SUBTOTAL_LABEL}"/>
						</b></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalBudget}"
								property="document.totalDirectCostInvoiceDetail.totalBudget" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.invoiceAmount}"
								property="document.totalDirectCostInvoiceDetail.invoiceAmount" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.cumulativeExpenditures}"
								property="document.totalDirectCostInvoiceDetail.cumulativeExpenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.budgetRemaining}"
								property="document.totalDirectCostInvoiceDetail.budgetRemaining" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalPreviouslyBilled}"
								property="document.totalDirectCostInvoiceDetail.totalPreviouslyBilled" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalAmountBilledToDate}"
								property="document.totalDirectCostInvoiceDetail.totalAmountBilledToDate" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.amountRemainingToBill}"
								property="document.totalDirectCostInvoiceDetail.amountRemainingToBill" readOnly="true" /></td>
					</tr>

					<tr>
						<td class="datacell"><b> <bean:message key="${ArKeyConstants.CONTRACTS_GRANTS_INVOICE_DETAILS_INDIRECT_SUBTOTAL_LABEL}"/>
						</b></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalBudget}"
								property="document.totalIndirectCostInvoiceDetail.totalBudget" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.invoiceAmount}"
								property="document.totalIndirectCostInvoiceDetail.invoiceAmount" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.cumulativeExpenditures}"
								property="document.totalIndirectCostInvoiceDetail.cumulativeExpenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.budgetRemaining}"
								property="document.totalIndirectCostInvoiceDetail.budgetRemaining" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalPreviouslyBilled}"
								property="document.totalIndirectCostInvoiceDetail.totalPreviouslyBilled" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalAmountBilledToDate}"
								property="document.totalIndirectCostInvoiceDetail.totalAmountBilledToDate" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.amountRemainingToBill}"
								property="document.totalIndirectCostInvoiceDetail.amountRemainingToBill" readOnly="true" /></td>
					</tr>

					<tr>
						<td class="datacell"><b> <bean:message key="${ArKeyConstants.CONTRACTS_GRANTS_INVOICE_DETAILS_TOTAL_LABEL}"/>
						</b></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalBudget}"
								property="document.totalCostInvoiceDetail.totalBudget" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.invoiceAmount}"
								property="document.totalCostInvoiceDetail.invoiceAmount" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.cumulativeExpenditures}"
								property="document.totalCostInvoiceDetail.cumulativeExpenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.budgetRemaining}"
								property="document.totalCostInvoiceDetail.budgetRemaining" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalPreviouslyBilled}"
								property="document.totalCostInvoiceDetail.totalPreviouslyBilled" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.totalAmountBilledToDate}"
								property="document.totalCostInvoiceDetail.totalAmountBilledToDate" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${contractsGrantsInvoiceDetailAttributes.amountRemainingToBill}"
								property="document.totalCostInvoiceDetail.amountRemainingToBill" readOnly="true" /></td>
					</tr>


				</c:if>
			</table>
		</div>
	</kul:tab>
</c:if>
