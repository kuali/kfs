<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib tagdir="/WEB-INF/tags/bc" prefix="bc"%>

<c:set var="salarySettingAttributes"
	value="${DataDictionary['PendingBudgetConstructionAppointmentFunding'].attributes}" />
<c:set var="positionAttributes"
	value="${DataDictionary['BudgetConstructionPosition'].attributes}" />
<c:set var="readOnly" value="${KualiForm.editingMode['systemViewOnly'] || !KualiForm.editingMode['fullEntry']}" />

<kul:tabTop tabTitle="Position Salary Setting" defaultOpen="true" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_POSITION_SALARY_SETTING_TAB_ERRORS}">
<div class="tab-container" align=center>
		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable" summary="">
			<tbody>
				<tr>
                    <kul:htmlAttributeHeaderCell align="right" attributeEntry="${positionAttributes.positionNumber}" >
                        <html:hidden property="returnAnchor" />
                        <html:hidden property="returnFormKey" />
                    </kul:htmlAttributeHeaderCell>
					<th>
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${positionAttributes.universityFiscalYear}"
						useShortLabel="false" /></div>
					</th>
				</tr>
				<tr>
					<td class="datacell">
						${KualiForm.budgetConstructionPosition.positionNumber}
					</td>
					<td class="datacell">
						${KualiForm.budgetConstructionPosition.universityFiscalYear}
					</td>
				</tr>

			</tbody>
		</table>
		
		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
                <kul:htmlAttributeHeaderCell literalLabel="Del" scope="col">
                </kul:htmlAttributeHeaderCell>
				<th>
					Cht
				</th>
				<th>
					Acct
				</th>
				<th>
					SAcct
				</th>
				<th>
					Obj
				</th>
				<th>
					SObj
				</th>
				<th>
					Emplid
				</th>
				<th>
					Name
				</th>
				<th>
					Lvl
				</th>
				<th>
					AdmPst
				</th>
				
			</tr>
			<c:forEach items="${KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding}" var="item" varStatus="status">
				<tr>
					<td>
                        <%-- these hidden fields are inside a table cell to keep the HTML valid --%>
<%-- TODO add the others --%>
                        <html:hidden property="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].universityFiscalYear" />
                        <html:hidden property="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].positionNumber" />
					    ${item.appointmentFundingDeleteIndicator}
					</td>
					<td>${item.chartOfAccountsCode}</td>
					<td>${item.accountNumber}</td>
					<td>${item.subAccountNumber}</td>
					<td>${item.financialObjectCode}</td>
					<td>${item.financialSubObjectCode}</td>
					<c:choose>
					<c:when test="${item.emplid != 'VACANT'}">
						<td>${item.budgetConstructionIntendedIncumbent.personName}</td>
						<td>${item.budgetConstructionIntendedIncumbent.iuClassificationLevel}</td>
<%-- TODO add adminstrative post --%>
						<td>&nbsp;</td>
					</c:when>
					<c:otherwise>
						<td>VACANT</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</c:otherwise>
					</c:choose>
                    <td class="datacell" nowrap>
                        <div align="center">
                          <c:if test="${item.emplid != 'VACANT'}">
                            <c:if test="${!readOnly}">
                                <html:image property="methodToCall.performVacateSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" src="images/tinybutton-clear1.gif" title="Vacate Salary Setting Line ${status.index}" alt="Vacate Salary Setting Line ${status.index}" styleClass="tinybutton" />
                            </c:if>
                          </c:if>
                          <c:if test="${!empty item.bcnCalculatedSalaryFoundationTracker && !readOnly}">
                            <br>
                            <html:image property="methodToCall.performPercentAdjustmentSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" src="images/tinybutton-percentincdec.gif" title="Percent Adjustment For Line ${status.index}" alt="Percent Adjustment For Line ${status.index}" styleClass="tinybutton" />
                          </c:if>
                        </div>
                    </td>
										
				</tr>
			</c:forEach>
		</table>
</div>
</kul:tabTop>