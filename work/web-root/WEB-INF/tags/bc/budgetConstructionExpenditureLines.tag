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
<c:set var="readOnly" value="${KualiForm.editingMode['systemViewOnly'] || !KualiForm.editingMode['fullEntry']}" />

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
			
            <c:if test="${!readOnly}">
              <c:set var="valuesMap" value="${KualiForm.newExpenditureLine.valuesMap}"/>
                
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

              <bc:pbglLineDataCell dataCellCssClass="infoline"
                  accountingLine="newExpenditureLine"
                  field="financialObjectCode" detailFunction="loadObjectInfo"
                  detailFunctionExtraParam="'${KualiForm.document.universityFiscalYear}', 'newExpenditureLine.objectType.name', 'newExpenditureLine.financialObjectTypeCode', "
                  detailField="financialObject.financialObjectCodeName"
                  attributes="${pbglExpenditureAttributes}" lookup="true" inquiry="true"
                  boClassSimpleName="ObjectCode"
                  readOnly="false"
                  displayHidden="false"
                  lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode"
                  lookupUnkeyedFieldConversions="financialObjectTypeCode:newExpenditureLine.financialObjectTypeCode,"
                  accountingLineValuesMap="${newExpenditureLine.valuesMap}"
                  inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                  anchor="expenditurenewLineLineAnchor" />

              <bc:pbglLineDataCell dataCellCssClass="infoline"
                  accountingLine="newExpenditureLine"
                  field="financialSubObjectCode" detailFunction="loadSubObjectInfo"
                  detailFunctionExtraParam="'${KualiForm.document.universityFiscalYear}', "
                  detailField="financialSubObject.financialSubObjectCodeName"
                  attributes="${pbglExpenditureAttributes}" lookup="true" inquiry="true"
                  boClassSimpleName="SubObjCd"
                  readOnly="false"
                  displayHidden="false"
                  lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,financialObjectCode,accountNumber"
                  accountingLineValuesMap="${newExpenditureLine.valuesMap}"
                  inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                  lookupAnchor="expenditurenewLineLineAnchor" />

              <td class="infoline" nowrap><div align="right"><span>
                  &nbsp;
              </span></div></td>

              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="newExpenditureLine"
                  cellProperty="newExpenditureLine.accountLineAnnualBalanceAmount"
                  attributes="${pbglExpenditureAttributes}"
                  field="accountLineAnnualBalanceAmount"
                  fieldAlign="right"
                  readOnly="false"
                  rowSpan="1" dataFieldCssClass="amount" />

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
            </c:if>
			

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

              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                  field="financialObjectCode" detailFunction="loadObjectInfo"
                  detailFunctionExtraParam="'${KualiForm.document.universityFiscalYear}', 'document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].objectType.name', 'document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialObjectTypeCode', "
                  detailField="financialObject.financialObjectCodeShortName"
                  attributes="${pbglExpenditureAttributes}" lookup="true" inquiry="true"
                  boClassSimpleName="ObjectCode"
                  readOnly="true"
                  displayHidden="false"
                  lookupOrInquiryKeys="chartOfAccountsCode"
                  lookupUnkeyedFieldConversions="financialObjectTypeCode:document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].objectTypeCode,"
                  accountingLineValuesMap="${item.valuesMap}"
                  inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                  anchor="expenditureexistingLineLineAnchor${status.index}" />

              <c:set var="doLookupOrInquiry" value="false"/>
              <c:if test="${item.financialSubObjectCode ne Constants.DASHES_SUB_OBJECT_CODE}">
                  <c:set var="doLookupOrInquiry" value="true"/>
              </c:if>

              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                  field="financialSubObjectCode" detailFunction="loadSubObjectInfo"
                  detailFunctionExtraParam="'${KualiForm.document.universityFiscalYear}', "
                  detailField="financialSubObject.financialSubObjectCdshortNm"
                  attributes="${pbglExpenditureAttributes}" lookup="${doLookupOrInquiry}" inquiry="${doLookupOrInquiry}"
                  boClassSimpleName="SubObjCd"
                  readOnly="true"
                  displayHidden="false"
                  lookupOrInquiryKeys="chartOfAccountsCode,financialObjectCode,accountNumber"
                  accountingLineValuesMap="${item.valuesMap}"
                  inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}" />

              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                  cellProperty="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].financialBeginningBalanceLineAmount"
                  attributes="${pbglExpenditureAttributes}"
                  field="financialBeginningBalanceLineAmount"
                  fieldAlign="right"
                  readOnly="true"
                  rowSpan="1" dataFieldCssClass="amount" />

              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}]"
                  cellProperty="document.pendingBudgetConstructionGeneralLedgerExpenditureLines[${status.index}].accountLineAnnualBalanceAmount"
                  attributes="${pbglExpenditureAttributes}"
                  field="accountLineAnnualBalanceAmount"
                  fieldAlign="right"
                  readOnly="false"
                  rowSpan="1" dataFieldCssClass="amount" />

              <td class="datacell" valign="top" nowrap><div align="right"><span>
				  <fmt:formatNumber value="${item.percentChange}" type="number" groupingUsed="true" minFractionDigits="2" />&nbsp;
                  <bc:pbglLineDataCellDetail/>
              </span></div></td>

			  <td class="datacell" nowrap><div align=center>
				  <c:choose>
					<c:when test="${!readOnly && empty item.budgetConstructionMonthly[0]}" > 
                      <c:if test="${empty item.laborObject || (!empty item.laborObject && item.laborObject.financialObjectFringeOrSalaryCode != 'F')}">
						<html:image src="images/tinybutton-createnew.gif" styleClass="tinybutton" property="methodToCall.performMonthlyExpenditureBudget.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" title="Create Month" alt="Create Month"/>
                      </c:if>
					</c:when> 
					<c:otherwise> 
                      <c:choose>
                        <c:when test="${!readOnly}">
                          <html:image src="images/tinybutton-edit1.gif" styleClass="tinybutton" property="methodToCall.performMonthlyExpenditureBudget.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" title="Edit Month" alt="Edit Month"/>
                        </c:when> 
                        <c:otherwise> 
                          <html:image src="images/tinybutton-view.gif" styleClass="tinybutton" property="methodToCall.performMonthlyExpenditureBudget.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" title="View Month" alt="View Month"/>
                        </c:otherwise> 
                      </c:choose>
						
					</c:otherwise> 
				  </c:choose>&nbsp; 
			  </div></td>

             <td class="datacell" nowrap>
                 <div align="center">
                   <html:image property="methodToCall.performBalanceInquiryForExpenditureLine.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" src="images/tinybutton-balinquiry.gif" title="Balance Inquiry For Expenditure Line ${status.index}" alt="Balance Inquiry For Expenditure Line ${status.index}" styleClass="tinybutton" />
                   <c:if test="${empty item.financialBeginningBalanceLineAmount || item.financialBeginningBalanceLineAmount == 0}">
                     <br>
                     <html:image property="methodToCall.deleteExpenditureLine.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" src="images/tinybutton-delete1.gif" title="Delete Expenditure Line ${status.index}" alt="Delete Expenditure Line ${status.index}" styleClass="tinybutton"/>
                   </c:if>
                   <c:if test="${!empty item.financialBeginningBalanceLineAmount && item.financialBeginningBalanceLineAmount != 0}">
                     <br>
                     <html:image property="methodToCall.performPercentAdjustmentExpenditureLine.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" src="images/tinybutton-percentincdec.gif" title="Percent Adjustment Expenditure Line ${status.index}" alt="Percent Adjustment Expenditure Line ${status.index}" styleClass="tinybutton"/>
                   </c:if>
                   <c:if test="${!empty item.laborObject && item.laborObject.detailPositionRequiredIndicator}">
                     <br>
                     <html:image property="methodToCall.performSalarySetting.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" src="images/tinybutton-salarysetting.gif" title="Perform Salary Setting For ${status.index}" alt="Perform Salary Setting For Line ${status.index}" styleClass="tinybutton"/>
                   </c:if>
                   <c:if test="${!empty item.positionObjectBenefit[0]}">
                     <br>
                     <html:image property="methodToCall.performShowBenefits.line${status.index}.anchorexpenditureexistingLineLineAnchor${status.index}" src="images/tinybutton-showbenefits.gif" title="Show Benefits For ${status.index}" alt="Show Benefits For Line ${status.index}" styleClass="tinybutton"/>
                   </c:if>
                 </div>
             </td>
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

            <c:if test="${!readOnly}">
            <tr>
              <td colspan="8" class="datacell" nowrap>
                <div align="center">
                  <html:image property="methodToCall.performPercentChange.anchorexpenditureControlsAnchor" src="images/buttonsmall_percentincdec.gif" title="Apply Percent Change" alt="Apply Percent Change" styleClass="tinybutton"/>&nbsp;&nbsp;&nbsp;
                  <html:image property="methodToCall.performMonthSpread.anchorexpenditureControlsAnchor" src="images/buttonsmall_monthspread.gif" title="Monthly Spread" alt="Monthly Spread" styleClass="tinybutton" />&nbsp;&nbsp;&nbsp;
                  <html:image property="methodToCall.performMonthDelete.anchorexpenditureControlsAnchor" src="images/buttonsmall_monthdel.gif" title="Monthly Delete" alt="Monthly Delete" styleClass="tinybutton"/>&nbsp;&nbsp;&nbsp;
                  <html:image property="methodToCall.performCalculateBenfits.anchorexpenditureControlsAnchor" src="images/buttonsmall_calcbenefits.gif" title="Calculate Benefits" alt="Calculate Benefits" styleClass="tinybutton"/>
                </div>
              </td>
	        </tr>
	        </c:if>
			
		</table>
</div>
</kul:tab>
