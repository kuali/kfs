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
<c:set var="documentAttributes" value="${DataDictionary.TravelAuthorizationDocument.attributes}" />
	<kul:tab tabTitle="Trip Detail Estimate Total" defaultOpen="true" tabErrorKey="${TemKeyConstants.TRVL_AUTH_TRVL_EXPENSES_TOTAL_ERRORS}">
	    <div id="TravelExpenseTotal" class="tab-container" align=center > 
	        <h3>Travel Expense Total</h3>
			<table border="0">
				<tbody>
					<tr>
						<th width="80%"><div align="right"><strong>Total Estimated</strong>:</div></th>
						<td width="20%"><bean:write name="KualiForm" property="document.documentGrandTotal" /></td>
					</tr>
					<tr>
						<th><div align="right">Less Manual Per Diem Adjustment: -</div></th>
						<td align="left" valign="middle"><kul:htmlControlAttribute
							attributeEntry="${documentAttributes.perDiemAdjustment}"
							property="document.perDiemAdjustment" readOnly="${!fullEntryMode}" /></td>							
					</tr>
					<tr>
						<th class="bord-l-b"><div align="right">Less CTS Charges: -</div></th>
					    <td><bean:write name="KualiForm" property="document.CTSTotal" /></td>
					</tr>
					<c:if test="${KualiForm.showCorporateCardTotal}">
						<tr>
						    <th class="bord-l-b"><div align="right">Amount due Corporate Credit Card: -</div></th>
						    <td><bean:write name="KualiForm" property="document.corporateCardTotal" /></td>
						</tr>
					</c:if>
					<tr>
			             <th class="bord-l-b"><div align="right">Less Non-Reimbursable: -</div></th>
			             <td><bean:write name="KualiForm" property="document.nonReimbursableTotal" /></td>
		           </tr>
<!-- 					<tr> -->
<!-- 						<th> -->
<!-- 							<div align="right">Calculated Encumbrance:</div> -->
<!-- 						</th> -->
<%-- 						<td align="left" valign="middle"><bean:write --%>
<!-- 							name="KualiForm" property="document.encumbranceTotal" /></td> -->
<!-- 					</tr> -->
					<tr>
						<th>
							<div align="right">Travel Expense Limit:</div>
						</th>
						<c:choose>
							<c:when test="${(KualiForm.document.expenseLimit) == null}">
								<td align="left" valign="middle">N/A</td>
							</c:when>
							<c:otherwise>
								<td align="left" valign="middle"><bean:write
									name="KualiForm" property="document.expenseLimit" /></td>
							</c:otherwise>
						</c:choose>
					</tr>
					<tr>
						<th>
							<div align="right">Actual Encumbrance:</div>
						</th>
						<td width="20%" align="left" valign="middle"><bean:write name="KualiForm" property="document.encumbranceTotal" /></td>
					</tr>
					<c:if test="${fullEntryMode}">
						<tr>
							<td colspan="2">
								<div align="center">
									<html:image  property="methodToCall.recalculate" src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif" styleClass="tinybutton" alt="recalculate total" title="recalculate total" />
	                            </div>							
							</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</div>
	</kul:tab>