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

	<!-- If there are no invoiceMilestones, this section should not be displayed -->
	<kul:tab tabTitle="Milestones" defaultOpen="true" tabErrorKey="document.invoiceMilestones*">
		<c:set var="invoiceMilestoneAttributes" value="${DataDictionary.Milestone.attributes}" />
		<c:set var="milestoneAttributes" value="${DataDictionary.Milestone.attributes}" />

		<div class="tab-container" align="center">
			<h3>Milestones</h3>
			<table cellpadding=0 class="datatable" summary="Milestonessection">
				<tr>
					<kul:htmlAttributeHeaderCell attributeEntry="${milestoneAttributes.milestoneNumber}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${milestoneAttributes.milestoneDescription}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${milestoneAttributes.milestoneActualCompletionDate}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${milestoneAttributes.milestoneAmount}" useShortLabel="false" />
				</tr>

				<logic:iterate indexId="ctr" name="KualiForm" property="document.invoiceMilestones" id="milestone">
					<tr>
						<td class="datacell"><a
							href="${ConfigProperties.application.url}/kr/inquiry.do?businessObjectClassName=org.kuali.kfs.module.ar.businessobject.Milestone&proposalNumber=${KualiForm.document.invoiceGeneralDetail.proposalNumber}&milestoneIdentifier=${KualiForm.document.invoiceMilestones[ctr].milestoneIdentifier}&methodToCall=start"
							target="_blank"> <kul:htmlControlAttribute attributeEntry="${invoiceMilestoneAttributes.milestoneNumber}"
									property="document.invoiceMilestones[${ctr}].milestoneNumber" readOnly="true" />
						</a></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceMilestoneAttributes.milestoneDescription}"
								property="document.invoiceMilestones[${ctr}].milestoneDescription" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${milestoneAttributes.milestoneActualCompletionDate}"
								property="document.invoiceMilestones[${ctr}].milestoneActualCompletionDate" readOnly="true" /></td>
						<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceMilestoneAttributes.milestoneAmount}"
								property="document.invoiceMilestones[${ctr}].milestoneAmount" readOnly="true" /></td>
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
