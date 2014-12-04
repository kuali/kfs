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

	<!-- If there are no bills, this section should not be displayed -->
	<kul:tab tabTitle="Bills" defaultOpen="true" tabErrorKey="document.invoiceBills*">
		<c:set var="invoiceBillAttributes" value="${DataDictionary.Bill.attributes}" />
		<c:set var="billAttributes" value="${DataDictionary.Bill.attributes}" />

		<div class="tab-container" align="center">
			<h3>Bills</h3>

			<table cellpadding=0 class="datatable" summary="Bills section">
				<tr>
					<kul:htmlAttributeHeaderCell attributeEntry="${billAttributes.billNumber}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${billAttributes.billDescription}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${billAttributes.billDate}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${billAttributes.estimatedAmount}" useShortLabel="false" />
				</tr>

				<logic:iterate indexId="ctr" name="KualiForm" property="document.invoiceBills" id="bill">
					<tr>
						<td class="datacell"><a
							href="${ConfigProperties.application.url}/kr/inquiry.do?businessObjectClassName=org.kuali.kfs.module.ar.businessobject.Bill&proposalNumber=${KualiForm.document.invoiceGeneralDetail.proposalNumber}&billIdentifier=${KualiForm.document.invoiceBills[ctr].billIdentifier}&billNumber=${KualiForm.document.invoiceBills[ctr].billNumber}&methodToCall=start"
							target="_blank"> <kul:htmlControlAttribute attributeEntry="${invoiceBillAttributes.billNumber}"
									property="document.invoiceBills[${ctr}].billNumber" readOnly="true" />
						</a></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceBillAttributes.billDescription}"
								property="document.invoiceBills[${ctr}].billDescription" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceBillAttributes.billDate}"
								property="document.invoiceBills[${ctr}].billDate" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceBillAttributes.estimatedAmount}"
								property="document.invoiceBills[${ctr}].estimatedAmount" readOnly="true" /></td>
					</tr>

				</logic:iterate>

				<tr>

					<td colspan="3" style="text-align: right" class="datacell"><b>Total</b></td>
					<td class="datacell"><b>${KualiForm.currentTotal }</b></td>
				</tr>
			</table>
		</div>
	</kul:tab>
</c:if>
