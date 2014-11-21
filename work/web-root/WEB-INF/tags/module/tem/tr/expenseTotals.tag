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
