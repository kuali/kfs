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

<c:set var="documentAttributes" value="${DataDictionary.TravelEntertainmentDocument.attributes}" />

<kul:tab tabTitle="Expense Totals" defaultOpen="true" tabErrorKey="${TemKeyConstants.TRVL_AUTH_TRVL_EXPENSES_TOTAL_ERRORS}">
	<div id="Expense Totals" class="tab-container" align=center>
		<h3></h3>
		<table class="datatable" summary="Expense Totals" cellpadding="0">
			<tbody>
				<tr>
					<th class="bord-l-b" width="80%"><div align="right">Total Expenses:</div></th>
					<td width="20%"><bean:write name="KualiForm" property="document.documentGrandTotal" /></td>
				</tr>
				<tr>
					<th class="bord-l-b"><div align="right">Less Non-Reimbursable:</div></th>
					<td><bean:write name="KualiForm" property="document.nonReimbursableTotal" /></td>
				</tr>
				<tr>
					<th class="bord-l-b"><div align="right">Approved Amount:</div></th>
					<td><bean:write name="KualiForm" property="document.approvedAmount" /></td>
				</tr>
				<tr>
					<th class="bord-l-b"><div align="right">Less CTS Charges:</div></th>
					<td><bean:write name="KualiForm" property="document.fullCTSTotal" /></td>
				</tr>
				<tr>
					<th class="bord-l-b"><div align="right">Amount due Corporate Credit Card:</div></th>
					<td><bean:write name="KualiForm" property="document.corporateCardTotal" /></td>
				</tr>
				<tr>
					<th class="bord-l-b"><div align="right">Reimbursement for this Entertainment:</div></th>
					<td><bean:write name="KualiForm" property="document.reimbursableTotal" /></td>
				</tr>	
				<tr>
					<th class="bord-l-b"><div align="right">Apply Expense Limit:</div></th>
					<c:choose>
						<c:when test="${(KualiForm.document.expenseLimit) == null}">
							<td align="left" valign="middle">N/A</td>
						</c:when>
						<c:otherwise>
							<td align="left" valign="middle"><bean:write name="KualiForm" property="document.expenseLimit" /></td>
						</c:otherwise>
					</c:choose>
				</tr>
				<tr title="Total calculated from Disbursement Vouchers">
					<th class="bord-l-b"><div align="right">Total Amount paid to Vendor(DV):</div></th>
					<td><bean:write name="KualiForm" property="document.totalPaidAmountToVendor" /></td>
				</tr>
				<tr title="Total calculated from Payment Requests">
					<th class="bord-l-b"><div align="right">Total Amount paid from Payment Requests(PREQ):</div></th>
					<td><bean:write name="KualiForm" property="document.totalPaidAmountToRequests" /></td>
				</tr>
				<tr title="Total approved amount, DV and Invoice">
					<th class="bord-l-b"><div align="right"><b>Grand Total:</b></div></th>
					<td><bean:write name="KualiForm" property="document.reimbursableGrandTotal" /></td>
				</tr>		
				<c:if test="${fullEntryMode}">
					<tr>
						<td colspan="3">
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
