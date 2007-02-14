<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>

<%-- needed? --%>
<%--
<c:set var="budgetConstructionAttributes"
	value="${DataDictionary['KualiBudgetConstructionDocument'].attributes}" />
--%>
<c:set var="pbglExpenditureAttributes" value="${DataDictionary.PendingBudgetConstructionGeneralLedgerExpenditure.attributes}" />


<kul:tab tabTitle="Expenditure" defaultOpen="false" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_EXPENDITURE_TAB_ERRORS}">
<div class="tab-container" align=center>

		<table>
			<tr>
				<th>
					Object
				</th>
				<th>
					Sub-Object
				</th>
				<th>
					Base
				</th>
				<th>
					Request
				</th>
				<th>
					Month?
				</th>
				<th>
					Action
				</th>
			</tr>

            <logic:iterate indexId="ctr" name="KualiForm" property="document.pendingBudgetConstructionGeneralLedgerExpenditure" id="currentLine">
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditure[${ctr}].documentNumber" />
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditure[${ctr}].universityFiscalYear" />
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditure[${ctr}].chartOfAccountsCode" />
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditure[${ctr}].accountNumber" />
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditure[${ctr}].subAccountNumber" />
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditure[${ctr}].financialBalanceTypeCode" />
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditure[${ctr}].financialObjectTypeCode" />
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditure[${ctr}].versionNumber" />
            <tr>
              <td valign=top nowrap><div align="left"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.financialObjectCode}" property="document.pendingBudgetConstructionGeneralLedgerExpenditure[${ctr}].financialObjectCode" readOnly="true"/>
              </span></div></td>
              <td valign=top nowrap><div align="left"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.financialSubObjectCode}" property="document.pendingBudgetConstructionGeneralLedgerExpenditure[${ctr}].financialSubObjectCode" readOnly="true"/>
              </span></div></td>
              <td valign=top nowrap><div align="right"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.financialBeginningBalanceLineAmount}" property="document.pendingBudgetConstructionGeneralLedgerExpenditure[${ctr}].financialBeginningBalanceLineAmount" readOnly="true"/>
              </span></div></td>
              <td valign=top nowrap><div align="right"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.accountLineAnnualBalanceAmount}" property="document.pendingBudgetConstructionGeneralLedgerExpenditure[${ctr}].accountLineAnnualBalanceAmount" readOnly="true"/>
              </span></div></td>
	              <td><div align=center>
	                   <html:image src="images/tinybutton-view.gif" styleClass="tinybutton" property="methodToCall.performMonthlyBudget.line${ctr}" title="View Month" alt="View Month"/>
<%--
	                   methodToCall.headerTab.headerDispatch.savePersonnel.navigateTo.parameters.x
--%>			
	              </div></td>
	              <td></td>
            </tr>
            </logic:iterate>
<%--
			<c:forEach items="${KualiForm.document.pendingBudgetConstructionGeneralLedgerExpenditure}" var="item" >
				<tr>
					<td>${item.financialObjectCode}</td>
					<td>${item.financialSubObjectCode}</td>
					<td>${item.financialBeginningBalanceLineAmount}</td>
					<td>${item.accountLineAnnualBalanceAmount}</td>
					<td></td>
					<td></td>
				</tr>
			</c:forEach>
--%>			
			<tr>
				<th align=right colspan=2>
					Expenditure Totals
				</th>
				<td>0</td>
				<td>0</td>
				<td></td>
				<td></td>
			</tr>
		</table>
</div>
</kul:tab>
