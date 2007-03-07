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
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>

<%-- needed? --%>
<%--
<c:set var="budgetConstructionAttributes"
	value="${DataDictionary['KualiBudgetConstructionDocument'].attributes}" />
--%>
<c:set var="pbglExpenditureAttributes" value="${DataDictionary.PendingBudgetConstructionGeneralLedger.attributes}" />


<kul:tab tabTitle="Expenditure" defaultOpen="false" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_EXPENDITURE_TAB_ERRORS}">
<div class="tab-container" align=center>

		<table>
			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.financialObjectCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.financialSubObjectCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.financialBeginningBalanceLineAmount}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.accountLineAnnualBalanceAmount}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglExpenditureAttributes.percentChange}" />
				<th>
					Month?
				</th>
				<th>
					Action
				</th>
			</tr>

			<c:forEach items="${KualiForm.document.pendingBudgetConstructionGeneralLedgerExpenditureLines}" var="item" varStatus="status" >


            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].documentNumber"/>
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].universityFiscalYear"/>
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].chartOfAccountsCode"/>
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].accountNumber"/>
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].subAccountNumber"/>
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialBalanceTypeCode"/>
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialObjectTypeCode"/>
            <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].versionNumber"/>
            <tr>
              <td valign=top nowrap><div align="left"><span>
              	  <a name="expenditureexistingLineLineAnchor${status.index}"></a>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.financialObjectCode}" property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialObjectCode" readOnly="true"/>
              </span></div></td>
              <td valign=top nowrap><div align="left"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.financialSubObjectCode}" property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialSubObjectCode" readOnly="true"/>
              </span></div></td>
              <td valign=top nowrap><div align="right"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.financialBeginningBalanceLineAmount}" property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialBeginningBalanceLineAmount" readOnly="true"/>
              </span></div></td>
              <td valign=top nowrap><div align="right"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.accountLineAnnualBalanceAmount}" property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].accountLineAnnualBalanceAmount" styleClass="amount" readOnly="false"/>
              </span></div></td>
              <td valign=top nowrap><div align="right"><span>
				  <fmt:formatNumber value="${item.percentChange}" type="number" groupingUsed="true" minFractionDigits="2" />&nbsp;
              </span></div></td>
			  <td><div align=center>
				<c:choose>
					<c:when test="${empty item.budgetConstructionMonthly}" > 
						<html:image src="images/tinybutton-createnew.gif" styleClass="tinybutton" property="methodToCall.performMonthlyBudget.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" title="Create Month" alt="Create Month"/>
					</c:when> 
					<c:otherwise> 
						<html:image src="images/tinybutton-edit1.gif" styleClass="tinybutton" property="methodToCall.performMonthlyBudget.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" title="Edit Month" alt="Edit Month"/>
					</c:otherwise> 
				</c:choose> 

<%--
	                   methodToCall.headerTab.headerDispatch.savePersonnel.navigateTo.parameters.x
--%>			
			  </div></td>
	          <td></td>
            </tr>
			</c:forEach>
			<tr>
				<th align=right colspan=2>
					Expenditure Totals
				</th>
				<td>0</td>
				<td>0</td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</table>
</div>
</kul:tab>
