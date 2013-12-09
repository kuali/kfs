<%--
 Copyright 2007-2009 The Kuali Foundation
 
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

<c:set var="documentAttributes" value="${DataDictionary.TravelReimbursementDocument.attributes}" />
<kul:tab tabTitle="Travel Expense Total" defaultOpen="true" tabErrorKey="${TemKeyConstants.TRVL_AUTH_TRVL_EXPENSES_TOTAL_ERRORS}">
	<div id="TravelExpenseTotal" class="tab-container" align=center>
		<h3>Travel Expense Total</h3>
		<table class="datatable" summary="Travel Expense Total" cellpadding="0">
			<tbody>
				<tr>
					<th class="bord-l-b" colspan="3" width="80%"><div align="right">Total Expenses:</div></th>
					<td width="20%"><bean:write name="KualiForm" property="document.documentGrandTotal" /></td>
				</tr>
				<tr>
					<th class="bord-l-b" colspan="3"> <div align="right">Less Manual Per Diem Adjustment: -</div></th>
					<td><kul:htmlControlAttribute attributeEntry="${documentAttributes.perDiemAdjustment}" property="document.perDiemAdjustment" readOnly="${!fullEntryMode}" /></td>
				</tr>
				<tr>
					<th class="bord-l-b" colspan="3"><div align="right">Less Non-Reimbursable: -</div></th>
					<td><bean:write name="KualiForm" property="document.nonReimbursableTotal" /></td>
				</tr>
				<tr>
					<th class="bord-l-b" colspan="3"><div align="right">Eligible for Reimbursement:</div></th>
					<td><bean:write name="KualiForm" property="document.approvedAmount" /></td>
				</tr>
				<tr>
					<c:if test="${showEncumbrance}">
						<th class="bord-l-b"><div align="right">Encumbrance Amount:</div></th>
						<td width="10%"><bean:write name="KualiForm" property="document.encumbranceTotal" /></td>
					</c:if>
					<th class="bord-l-b" colspan="${showEncumbrance?'1':'3'}"><div align="right">Apply Expense Limit:</div></th>
					<c:choose>
						<c:when test="${(KualiForm.document.expenseLimit) == null}">
							<td align="left" valign="middle">N/A</td>
						</c:when>
						<c:otherwise>
							<td align="left" valign="middle"><bean:write name="KualiForm" property="document.expenseLimit" /></td>
						</c:otherwise>
					</c:choose>

				</tr>
				<tr>
					<th class="bord-l-b" colspan="3"><div align="right">Less CTS Charges: -</div></th>
					<td><bean:write name="KualiForm" property="document.fullCTSTotal" /></td>
				</tr>
				<tr>
					<th class="bord-l-b" colspan="3"><div align="right">Amount due Corporate Credit Card: -</div></th>
					<td><bean:write name="KualiForm" property="document.corporateCardTotal" /></td>
				</tr>
				<tr>
					<th class="bord-l-b" colspan="3"><div align="right">Total Reimbursable:</div></th>
					<td><bean:write name="KualiForm" property="document.reimbursableTotal" /></td>
				</tr>
				<c:if test="${showAdvances}">
					<tr>
						<th class="bord-l-b" colspan="3"><div align="right">Less Advances from this Trip: -</div></th>
						<td><bean:write name="KualiForm" property="document.advancesTotal" /></td>
					</tr>
					<tr>
						<th class="bord-l-b" colspan="3"><div align="right">Reimbursement from this Trip:</div></th>
						<td><bean:write name="KualiForm" property="document.reimbursableGrandTotal" /></td>
					</tr>
				</c:if>
				<c:if test="${fullEntryMode}">
					<tr>
						<td colspan="4">
							<div align="center">
								<html:image property="methodToCall.recalculate"
									src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif"
									alt="Recalculate Expense Total"
									title="Recalculate Expense Total" styleClass="tinybutton" />
							</div>
						</td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</div>
</kul:tab>
