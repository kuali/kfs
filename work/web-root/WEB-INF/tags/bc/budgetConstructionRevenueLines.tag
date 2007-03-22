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
<%@ taglib tagdir="/WEB-INF/tags/bc" prefix="bc"%>

<c:set var="pbglRevenueAttributes" value="${DataDictionary.PendingBudgetConstructionGeneralLedger.attributes}" />

<kul:tab tabTitle="Revenue" defaultOpen="false" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_REVENUE_TAB_ERRORS}">
<div class="tab-container" align=center>

        <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
            <bc:subheadingWithDetailToggleRow
              columnCount="8"
              subheading="Revenue"/>
			<tr>
				<th>
				    &nbsp;	
				</th>
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.financialObjectCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.financialSubObjectCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.financialBeginningBalanceLineAmount}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.accountLineAnnualBalanceAmount}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.percentChange}" />
				<th>
					Month?
				</th>
				<th>
					Action
				</th>
			</tr>
			<tr>
              <kul:htmlAttributeHeaderCell literalLabel="Add:" scope="row" rowspan="1">
                  <%-- these hidden fields are inside a table cell to keep the HTML valid --%>
                  <html:hidden property="newRevenueLine.documentNumber"/>
                  <html:hidden property="newRevenueLine.universityFiscalYear"/>
                  <html:hidden property="newRevenueLine.chartOfAccountsCode"/>
                  <html:hidden property="newRevenueLine.accountNumber"/>
                  <html:hidden property="newRevenueLine.subAccountNumber"/>
                  <html:hidden property="newRevenueLine.financialBalanceTypeCode"/>
                  <html:hidden property="newRevenueLine.financialObjectTypeCode"/>
                  <html:hidden property="newRevenueLine.versionNumber"/>
              </kul:htmlAttributeHeaderCell>
              <td class="infoline" nowrap><div align="left"><span>
              	  <a name="revenuenewLineLineAnchor"></a>
                  <kul:htmlControlAttribute attributeEntry="${pbglRevenueAttributes.financialObjectCode}" property="newRevenueLine.financialObjectCode" readOnly="false"/>
              </span></div></td>
              <td class="infoline" nowrap><div align="left"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglRevenueAttributes.financialSubObjectCode}" property="newRevenueLine.financialSubObjectCode" readOnly="false"/>
              </span></div></td>
              <td class="infoline" nowrap><div align="right"><span>
                  &nbsp;
              </span></div></td>
              <td class="infoline" nowrap><div align="right"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglRevenueAttributes.accountLineAnnualBalanceAmount}" property="newRevenueLine.accountLineAnnualBalanceAmount" styleClass="amount" readOnly="false"/>
              </span></div></td>
              <td class="infoline" nowrap><div align="right"><span>
                  &nbsp;
              </span></div></td>
			  <td class="infoline" nowrap><div align=center>
                  &nbsp;
			  </div></td>
              <td class="infoline" nowrap><div align="center">
                  <html:image property="methodToCall.insertRevenueLine.anchorrevenuenewLineLineAnchor" src="images/tinybutton-add1.gif" title="Add an Revenue Line" alt="Add an Revenue Line" styleClass="tinybutton"/>
              </div></td>
			</tr>

			<c:forEach items="${KualiForm.document.pendingBudgetConstructionGeneralLedgerRevenueLines}" var="item" varStatus="status" >

            <tr>
              <kul:htmlAttributeHeaderCell scope="row" rowspan="1">
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].documentNumber"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].universityFiscalYear"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].chartOfAccountsCode"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].accountNumber"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].subAccountNumber"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].financialBalanceTypeCode"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].financialObjectTypeCode"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].versionNumber"/>
              </kul:htmlAttributeHeaderCell>
              <td valign=top nowrap><div align="left"><span>
              	  <a name="revenueexistingLineLineAnchor${status.index}"></a>
                  <kul:htmlControlAttribute attributeEntry="${pbglRevenueAttributes.financialObjectCode}" property="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].financialObjectCode" readOnly="true"/>
              </span></div></td>
              <td valign=top nowrap><div align="left"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglRevenueAttributes.financialSubObjectCode}" property="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].financialSubObjectCode" readOnly="true"/>
              </span></div></td>
              <td valign=top nowrap><div align="right"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglRevenueAttributes.financialBeginningBalanceLineAmount}" property="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].financialBeginningBalanceLineAmount" readOnly="true"/>
              </span></div></td>
              <td valign=top nowrap><div align="right"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglRevenueAttributes.accountLineAnnualBalanceAmount}" property="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].accountLineAnnualBalanceAmount" styleClass="amount" readOnly="false"/>
              </span></div></td>
              <td valign=top nowrap><div align="right"><span>
				  <fmt:formatNumber value="${item.percentChange}" type="number" groupingUsed="true" minFractionDigits="2" />&nbsp;
              </span></div></td>
	              <td><div align=center>
					<c:choose>
						<c:when test="${empty item.budgetConstructionMonthly}" > 
	                   		<html:image src="images/tinybutton-createnew.gif" styleClass="tinybutton" property="methodToCall.performMonthlyBudget.line${status.index}.anchorrevenueexistingLineLineAnchor${status.index}" title="Create Month" alt="Create Month"/>
						</c:when> 
						<c:otherwise> 
	                   		<html:image src="images/tinybutton-edit1.gif" styleClass="tinybutton" property="methodToCall.performMonthlyBudget.line${status.index}.anchorrevenueexistingLineLineAnchor${status.index}" title="Edit Month" alt="Edit Month"/>
						</c:otherwise> 
					</c:choose> 
	              </div></td>
	              <td></td>
            </tr>
			</c:forEach>
			<tr>
				<th align=right colspan=3>
					Revenue Totals
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
