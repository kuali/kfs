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
<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>

<c:if test="${!empty KualiForm.document.proposalNumber}">
	<kul:tab tabTitle="Invoice Details" defaultOpen="true" tabErrorKey="document.invoiceDetails*,document.prorateWarning">
		<c:set var="invoiceDetailAttributes" value="${DataDictionary.InvoiceDetail.attributes}" />

		<div class="tab-container" align="center">
			<h3>Invoice Details</h3>
			<table cellpadding=0 class="datatable" summary="Invoice Details section">
				<tr>
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceDetailAttributes.category}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceDetailAttributes.budget}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceDetailAttributes.expenditures}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceDetailAttributes.cumulative}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceDetailAttributes.balance}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceDetailAttributes.billed}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceDetailAttributes.adjustedCumExpenditures}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceDetailAttributes.adjustedBalance}" useShortLabel="false" />
				</tr>
				<c:if test="${!empty KualiForm.document.invoiceDetails}">
					<!-- If the categories are not retrieved, then its better not to display the total fields in this section. -->
					<logic:iterate indexId="ctr" name="KualiForm" property="document.invoiceDetailsWithIndirectCosts" id="invoiceDetail">
						<tr>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.category}"
									property="document.invoiceDetailsWithIndirectCosts[${ctr}].category" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.budget}"
									property="document.invoiceDetailsWithIndirectCosts[${ctr}].budget" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.expenditures}"
									property="document.invoiceDetailsWithIndirectCosts[${ctr}].expenditures" readOnly="${readOnly}" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.cumulative}"
									property="document.invoiceDetailsWithIndirectCosts[${ctr}].cumulative" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.balance}"
									property="document.invoiceDetailsWithIndirectCosts[${ctr}].balance" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.billed}"
									property="document.invoiceDetailsWithIndirectCosts[${ctr}].billed" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.billed}"
									property="document.invoiceDetailsWithIndirectCosts[${ctr}].adjustedCumExpenditures" readOnly="true" /></td>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.billed}"
									property="document.invoiceDetailsWithIndirectCosts[${ctr}].adjustedBalance" readOnly="true" /></td>
						</tr>
					</logic:iterate>
					<tr>
						<td class="datacell"><b> <kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.category}"
									property="document.directCostInvoiceDetails[0].category" readOnly="true" />
						</b></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.budget}"
								property="document.directCostInvoiceDetails[0].budget" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.expenditures}"
								property="document.directCostInvoiceDetails[0].expenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.cumulative}"
								property="document.directCostInvoiceDetails[0].cumulative" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.balance}"
								property="document.directCostInvoiceDetails[0].balance" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.billed}"
								property="document.directCostInvoiceDetails[0].billed" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.balance}"
								property="document.directCostInvoiceDetails[0].adjustedCumExpenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.billed}"
								property="document.directCostInvoiceDetails[0].adjustedBalance" readOnly="true" /></td>
					</tr>

					<tr>
						<td class="datacell"><b> <kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.category}"
									property="document.inDirectCostInvoiceDetails[0].category" readOnly="true" />
						</b></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.budget}"
								property="document.inDirectCostInvoiceDetails[0].budget" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.expenditures}"
								property="document.inDirectCostInvoiceDetails[0].expenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.cumulative}"
								property="document.inDirectCostInvoiceDetails[0].cumulative" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.balance}"
								property="document.inDirectCostInvoiceDetails[0].balance" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.billed}"
								property="document.inDirectCostInvoiceDetails[0].billed" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.balance}"
								property="document.inDirectCostInvoiceDetails[0].adjustedCumExpenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.billed}"
								property="document.inDirectCostInvoiceDetails[0].adjustedBalance" readOnly="true" /></td>
					</tr>

					<tr>
						<td class="datacell"><b> <kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.category}"
									property="document.totalInvoiceDetails[0].category" readOnly="true" />
						</b></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.budget}"
								property="document.totalInvoiceDetails[0].budget" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.expenditures}"
								property="document.totalInvoiceDetails[0].expenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.cumulative}"
								property="document.totalInvoiceDetails[0].cumulative" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.balance}"
								property="document.totalInvoiceDetails[0].balance" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.billed}"
								property="document.totalInvoiceDetails[0].billed" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.balance}"
								property="document.totalInvoiceDetails[0].adjustedCumExpenditures" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceDetailAttributes.billed}"
								property="document.totalInvoiceDetails[0].adjustedBalance" readOnly="true" /></td>
					</tr>


				</c:if>
			</table>
		</div>
		<SCRIPT type="text/javascript">
    var kualiForm = document.forms['KualiForm'];
    var kualiElements = kualiForm.elements;
  </SCRIPT>
	</kul:tab>
</c:if>