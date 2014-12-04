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


<!-- If there are no bills, this section should not be displayed -->
<kul:tab tabTitle="Award Accounts" defaultOpen="true" tabErrorKey="document.contractsGrantsLetterOfCreditReviewDetails*">
	<c:set var="contractsGrantsLetterOfCreditReviewDetailAttributes" value="${DataDictionary.ContractsGrantsLetterOfCreditReviewDetail.attributes}" />
	<%@ attribute name="invPropertyName" required="true" description="Name of form property containing the customer invoice source accounting line."%>
	<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
	<c:set var="detailPropertyName" value="document.contractsGrantsLetterOfCreditReviewDetails[${ctr}]" />

	<div class="tab-container" align="center">
		<h3>Award Accounts</h3>

		<table style="width: 100%; border: none" cellpadding="0" cellspacing="0" class="datatable" border="0">
			<logic:iterate indexId="counter" name="KualiForm" property="proposalNumbers" id="proposalNumber">
				<tr>
					<th width="5%" align="center">Award (${KualiForm.proposalNumbers[counter] }):</th>
					<td><ar:contractsGrantsLetterOfCreditReviewDetail proposalNumberValue="${KualiForm.proposalNumbers[counter] }" /></td>
				</tr>
			</logic:iterate>
		</table>
	</div>
</kul:tab>
