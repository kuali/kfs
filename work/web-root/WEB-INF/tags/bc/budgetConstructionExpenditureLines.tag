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

<c:if test="${!accountingLineScriptsLoaded}">
	<script type='text/javascript' src="dwr/interface/ChartService.js"></script>
	<script type='text/javascript' src="dwr/interface/AccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/SubAccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/ObjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/ObjectTypeService.js"></script>
	<script type='text/javascript' src="dwr/interface/SubObjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/ProjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/OriginationCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/DocumentTypeService.js"></script>
	<script language="JavaScript" type="text/javascript" src="scripts/kfs/objectInfo.js"></script>
	<c:set var="accountingLineScriptsLoaded" value="true" scope="page" />
</c:if>

<c:set var="pbglExpenditureAttributes" value="${DataDictionary.PendingBudgetConstructionGeneralLedger.attributes}" />

<kul:tab tabTitle="Expenditure" defaultOpen="false" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_EXPENDITURE_TAB_ERRORS}">
<div class="tab-container" align=center>

        <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
            <bc:subheadingWithDetailToggleRow
              columnCount="8"
              subheading="Expenditure"/>
			<tr>
				<th>
				    &nbsp;	
				</th>
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
			
			<tr>
              <kul:htmlAttributeHeaderCell literalLabel="Add:" scope="row" rowspan="1">
                  <%-- these hidden fields are inside a table cell to keep the HTML valid --%>
                  <html:hidden property="newExpenditureLine.documentNumber"/>
                  <html:hidden property="newExpenditureLine.universityFiscalYear"/>
                  <html:hidden property="newExpenditureLine.chartOfAccountsCode"/>
                  <html:hidden property="newExpenditureLine.accountNumber"/>
                  <html:hidden property="newExpenditureLine.subAccountNumber"/>
                  <html:hidden property="newExpenditureLine.financialBalanceTypeCode"/>
                  <html:hidden property="newExpenditureLine.financialObjectTypeCode"/>
                  <html:hidden property="newExpenditureLine.versionNumber"/>
              </kul:htmlAttributeHeaderCell>
              <td class="infoline" nowrap><div align="left"><span>
              	  <a name="expenditurenewLineLineAnchor"></a>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.financialObjectCode}" property="newExpenditureLine.financialObjectCode" readOnly="false"/>
              </span></div></td>
              <td class="infoline" nowrap><div align="left"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.financialSubObjectCode}" property="newExpenditureLine.financialSubObjectCode" readOnly="false"/>
              </span></div></td>
              <td class="infoline" nowrap><div align="right"><span>
                  &nbsp;
              </span></div></td>
              <td class="infoline" nowrap><div align="right"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.accountLineAnnualBalanceAmount}" property="newExpenditureLine.accountLineAnnualBalanceAmount" styleClass="amount" readOnly="false"/>
              </span></div></td>
              <td class="infoline" nowrap><div align="right"><span>
                  &nbsp;
              </span></div></td>
			  <td class="infoline" nowrap><div align=center>
                  &nbsp;
			  </div></td>
              <td class="infoline" nowrap><div align="center">
                  <html:image property="methodToCall.insertExpenditureLine.anchorexpenditurenewLineLineAnchor" src="images/tinybutton-add1.gif" title="Add an Expenditure Line" alt="Add an Expenditure Line" styleClass="tinybutton"/>
              </div></td>
			</tr>
			

			<c:forEach items="${KualiForm.document.pendingBudgetConstructionGeneralLedgerExpenditureLines}" var="item" varStatus="status" >


            <tr>
              <kul:htmlAttributeHeaderCell scope="row" rowspan="1">
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].documentNumber"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].universityFiscalYear"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].chartOfAccountsCode"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].accountNumber"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].subAccountNumber"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialBalanceTypeCode"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialObjectTypeCode"/>
                  <html:hidden property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].versionNumber"/>
                  <bc:pbglLineDataCellDetail/>
              </kul:htmlAttributeHeaderCell>
              <td class="datacell" nowrap><div align="left"><span>
              	  <a name="expenditureexistingLineLineAnchor${status.index}"></a>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.financialObjectCode}" property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialObjectCode" readOnly="true"/>
                  <bc:pbglLineDataCellDetail
                     accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                     detailField="financialObject.financialObjectCodeShortName"
                     detailFields="${detailFields}"
                     />
              </span></div></td>
              <td class="datacell" nowrap><div align="left"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.financialSubObjectCode}" property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialSubObjectCode" readOnly="true"/>
                  <bc:pbglLineDataCellDetail
                     accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                     detailField="financialSubObject.financialSubObjectCdshortNm"
                     detailFields="${detailFields}"
                     />
              </span></div></td>
              <td class="datacell" nowrap><div align="right"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.financialBeginningBalanceLineAmount}" property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialBeginningBalanceLineAmount" readOnly="true"/>
                  <bc:pbglLineDataCellDetail/>
              </span></div></td>
              <td class="datacell" nowrap><div align="right"><span>
                  <kul:htmlControlAttribute attributeEntry="${pbglExpenditureAttributes.accountLineAnnualBalanceAmount}" property="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].accountLineAnnualBalanceAmount" styleClass="amount" readOnly="false"/>
                  <bc:pbglLineDataCellDetail/>
              </span></div></td>
              <td class="datacell" nowrap><div align="right"><span>
				  <fmt:formatNumber value="${item.percentChange}" type="number" groupingUsed="true" minFractionDigits="2" />&nbsp;
                  <bc:pbglLineDataCellDetail/>
              </span></div></td>
			  <td class="datacell" nowrap><div align=center>
                <bc:pbglLineDataCellDetail/>
				<c:choose>
					<c:when test="${empty item.budgetConstructionMonthly[0]}" > 
						<html:image src="images/tinybutton-createnew.gif" styleClass="tinybutton" property="methodToCall.performMonthlyExpenditureBudget.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" title="Create Month" alt="Create Month"/>
					</c:when> 
					<c:otherwise> 
						<html:image src="images/tinybutton-edit1.gif" styleClass="tinybutton" property="methodToCall.performMonthlyExpenditureBudget.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" title="Edit Month" alt="Edit Month"/>
					</c:otherwise> 
				</c:choose> 

<%--
	                   methodToCall.headerTab.headerDispatch.savePersonnel.navigateTo.parameters.x
--%>			
			  </div></td>
	          <td>&nbsp;</td>
            </tr>
			</c:forEach>
			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="Expenditure Totals" colspan="3" horizontal="true" />
                <td class="datacell" nowrap><div align="right"><span>
				    0
                </span></div></td>
                <td class="datacell" nowrap><div align="right"><span>
				    0
                </span></div></td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
		</table>
</div>
</kul:tab>
