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

<c:set var="bcafAttributes"
	value="${DataDictionary['PendingBudgetConstructionAppointmentFunding'].attributes}" />
<c:set var="positionAttributes"
	value="${DataDictionary['BudgetConstructionPosition'].attributes}" />
<c:set var="readOnly" value="${KualiForm.editingMode['systemViewOnly'] || !KualiForm.editingMode['fullEntry']}" />

<kul:tabTop tabTitle="Position Salary Setting" defaultOpen="true" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_POSITION_SALARY_SETTING_TAB_ERRORS}">
<div class="tab-container" align=center>
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable" summary="">
        <bc:subheadingWithDetailToggleRow
          columnCount="8"
          subheading="Position" />
        <tr>
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.positionNumber"
                literalLabel="Fy/Pos#:"
                horizontal="true"
            />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition"
                cellProperty="budgetConstructionPosition.universityFiscalYear"
                field="universityFiscalYear"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition"
                cellProperty="budgetConstructionPosition.positionNumber"
                field="positionNumber"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition"
                cellProperty="budgetConstructionPosition.positionDescription"
                field="positionDescription"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.iuDefaultObjectCode"
                literalLabel="Dflt.Obj:"
                horizontal="true" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition"
                cellProperty="budgetConstructionPosition.iuDefaultObjectCode"
                field="iuDefaultObjectCode"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.positionDepartmentIdentifier"
                literalLabel="Dflt.Obj:"
                horizontal="true" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition"
                cellProperty="budgetConstructionPosition.positionDepartmentIdentifier"
                field="positionDepartmentIdentifier"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />
        </tr>
    </table>
        
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
        <tr>
        <bc:subheadingWithDetailToggleRow
          columnCount="11"
          subheading="Funding" />
        </tr>
        <c:forEach items="${KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding}" var="item" varStatus="status">
        <tr>
            <kul:htmlAttributeHeaderCell literalLabel="Del" scope="row">
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
            <th>
                Actions
            </th>
              
        </tr>
        <tr>
            <td>
                <%-- these hidden fields are inside a table cell to keep the HTML valid --%>
                <a name="salaryexistingLineLineAnchor${status.index}"></a>
                ${item.appointmentFundingDeleteIndicator}
<%-- TODO add the others --%>
                <html:hidden property="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].universityFiscalYear" />
                <html:hidden property="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].positionNumber" />
                <html:hidden property="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].versionNumber" />
           </td>
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="chartOfAccountsCode"
               detailField="chartOfAccounts.finChartOfAccountDescription"
               attributes="${bcafAttributes}" inquiry="true"
               boClassSimpleName="Chart"
               readOnly="true"
               displayHidden="false"
               accountingLineValuesMap="${item.valuesMap}" />
           <td>${item.accountNumber}</td>
           <td>${item.subAccountNumber}</td>
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="financialObjectCode" detailFunction="loadObjectInfo"
               detailFunctionExtraParam="'${KualiForm.budgetConstructionPosition.universityFiscalYear}', "
               detailField="financialObject.financialObjectCodeShortName"
               attributes="${bcafAttributes}" lookup="true" inquiry="true"
               boClassSimpleName="ObjectCode"
               readOnly="true"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode"
               accountingLineValuesMap="${item.valuesMap}"
               inquiryExtraKeyValues="universityFiscalYear=${KualiForm.budgetConstructionPosition.universityFiscalYear}" />
           <td>${item.financialSubObjectCode}</td>
           <td>${item.emplid}</td>
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
                          &nbsp;
                        </div>
                    </td>
                </tr>
                                        
            <tr>
                <th>
                    &nbsp;
                </th>
                <kul:htmlAttributeHeaderCell colspan="2" align="left" literalLabel="CSF" scope="col">
                </kul:htmlAttributeHeaderCell>
                <kul:htmlAttributeHeaderCell colspan="3" align="left" literalLabel="Request" scope="col">
                </kul:htmlAttributeHeaderCell>
                <kul:htmlAttributeHeaderCell colspan="2" align="left" literalLabel="Leaves Req.CSF" scope="col">
                </kul:htmlAttributeHeaderCell>
                <kul:htmlAttributeHeaderCell colspan="3" align="left" literalLabel="Tot.Int." scope="col">
                </kul:htmlAttributeHeaderCell>
            </tr>
            </c:forEach>
        </table>
</div>
</kul:tabTop>