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

<c:if test="${!empty KualiForm.document.proposalNumber}">

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
							href="${ConfigProperties.application.url}/kr/inquiry.do?businessObjectClassName=org.kuali.kfs.module.ar.businessobject.Milestone&proposalNumber=${KualiForm.document.proposalNumber}&milestoneIdentifier=${KualiForm.document.invoiceMilestones[ctr].milestoneIdentifier}&methodToCall=start"
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
		<SCRIPT type="text/javascript">
			var kualiForm = document.forms['KualiForm'];
			var kualiElements = kualiForm.elements;
		</SCRIPT>
	</kul:tab>

</c:if>