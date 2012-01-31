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
<kul:tab tabTitle="Award Accounts" defaultOpen="true" tabErrorKey="document.contractsGrantsLOCReviewDetails*">
	<c:set var="contractsGrantsLOCReviewDetailAttributes" value="${DataDictionary.ContractsGrantsLOCReviewDetail.attributes}" />
	<%@ attribute name="invPropertyName" required="true" description="Name of form property containing the customer invoice source accounting line."%>
	<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
	<c:set var="detailPropertyName" value="document.contractsGrantsLOCReviewDetails[${ctr}]" />

	<div class="tab-container" align="center">
		<h3>Award Accounts</h3>

		<table style="width: 100%; border: none" cellpadding="0" cellspacing="0" class="datatable" border="0">
			<logic:iterate indexId="counter" name="KualiForm" property="proposalNumbers" id="proposalNumber">
				<tr>
					<th width="5%" align="center">Award (${KualiForm.proposalNumbers[counter] }):</th>
					<td><ar:contractsGrantsLOCReviewDetail proposalNumberValue="${KualiForm.proposalNumbers[counter] }" /></td>
				</tr>
			</logic:iterate>
		</table>
	</div>
</kul:tab>