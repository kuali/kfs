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
							href="${ConfigProperties.application.url}/kr/inquiry.do?businessObjectClassName=org.kuali.kfs.module.cg.businessobject.Bill&proposalNumber=${KualiForm.document.proposalNumber}&billIdentifier=${KualiForm.document.award.bills[ctr].billIdentifier}&billNumber=${KualiForm.document.award.bills[ctr].billNumber}&methodToCall=start"
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
		<SCRIPT type="text/javascript">
			var kualiForm = document.forms['KualiForm'];
			var kualiElements = kualiForm.elements;
		</SCRIPT>
	</kul:tab>
</c:if>