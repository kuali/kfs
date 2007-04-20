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
<c:set var="intincAttributes"
	value="${DataDictionary['BudgetConstructionIntendedIncumbent'].attributes}" />
<c:set var="bcsfAttributes"
	value="${DataDictionary['BudgetConstructionCalculatedSalaryFoundationTracker'].attributes}" />

<c:set var="readOnly" value="${KualiForm.editingMode['systemViewOnly'] || !KualiForm.editingMode['fullEntry']}" />

<kul:tabTop tabTitle="Salary Setting by Position" defaultOpen="true" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_POSITION_SALARY_SETTING_TAB_ERRORS}">
<div class="tab-container" align=center>
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
        <bc:subheadingWithDetailToggleRow
          columnCount="12"
          subheading="Position" />
        <tr>
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.universityFiscalYear"
                literalLabel="<span class=\"nowrap\">Pos#/Fy:</span>"
                horizontal="true" colspan="3" >
              <html:hidden property="returnAnchor" />
              <html:hidden property="returnFormKey" />
              <html:hidden property="universityFiscalYear" />
              <html:hidden property="chartOfAccountsCode" />
              <html:hidden property="accountNumber" />
              <html:hidden property="subAccountNumber" />
              <html:hidden property="financialObjectCode" />
              <html:hidden property="financialSubObjectCode" />
              <html:hidden property="positionNumber" />
              <html:hidden property="returnFormKey" />
            </kul:htmlAttributeHeaderCell>
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition"
                field="positionNumber"
                attributes="${posnAttributes}" inquiry="true"
                boClassSimpleName="BudgetConstructionPosition"
                boPackageName="org.kuali.module.budget.bo"
                readOnly="true"
                displayHidden="false"
                lookupOrInquiryKeys="universityFiscalYear"
                accountingLineValuesMap="${KualiForm.budgetConstructionPosition.valuesMap}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionPosition.universityFiscalYear"
                field="universityFiscalYear"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.positionDescription"
                literalLabel="<span class=\"nowrap\">Pos.Desc:</span>"
                horizontal="true" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionPosition.positionDescription"
                field="positionDescription"
                attributes="${positionAttributes}"
                colSpan="6"
                readOnly="true"
                displayHidden="false" />
       </tr>
        <tr>
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.iuDefaultObjectCode"
                literalLabel="<span class=\"nowrap\">Dflt.Obj:</span>"
                horizontal="true" colspan="3" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionPosition.iuDefaultObjectCode"
                field="iuDefaultObjectCode"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" colSpan="2" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.positionDepartmentIdentifier"
                literalLabel="<span class=\"nowrap\">Coa-Org:</span>"
                horizontal="true" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionPosition.positionDepartmentIdentifier"
                field="positionDepartmentIdentifier"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" colSpan="2" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.jobCode"
                literalLabel="<span class=\"nowrap\">SetId:</span>"
                horizontal="true" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionPosition.setidJobCode"
                field="setidJobCode"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.jobCode"
                literalLabel="<span class=\"nowrap\">JCode:</span>"
                horizontal="true" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionPosition.jobCode"
                field="jobCode"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />
        </tr>
        <tr>
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.positionSalaryPlanDefault"
                literalLabel="<span class=\"nowrap\">SalPln/Grd:</span>"
                horizontal="true" colspan="3" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionPosition.positionSalaryPlanDefault"
                field="positionSalaryPlanDefault"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionPosition.positionGradeDefault"
                field="positionGradeDefault"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.iuNormalWorkMonths"
                literalLabel="<span class=\"nowrap\">WM/PM:</span>"
                horizontal="true" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionPosition.iuNormalWorkMonths"
                field="iuNormalWorkMonths"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionPosition.iuPayMonths"
                field="iuPayMonths"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.positionStandardHoursDefault"
                literalLabel="<span class=\"nowrap\">SHr:</span>"
                horizontal="true" />
            <fmt:formatNumber value="${KualiForm.budgetConstructionPosition.positionStandardHoursDefault}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionPosition.positionStandardHoursDefault"
                field="positionStandardHoursDefault"
                attributes="${positionAttributes}"
                readOnly="true"
                formattedNumberValue="${formattedNumber}"
                displayHidden="false" dataFieldCssClass="amount" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.positionStandardHoursDefault"
                literalLabel="<span class=\"nowrap\">FTE:</span>"
                horizontal="true" />
            <fmt:formatNumber value="${KualiForm.budgetConstructionPosition.positionFullTimeEquivalency}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionPosition.positionFullTimeEquivalency"
                field="positionFullTimeEquivalency"
                attributes="${positionAttributes}"
                readOnly="true"
                formattedNumberValue="${formattedNumber}"
                displayHidden="false" />
        </tr>
<%--
    </table>
        
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
--%>
        <tr>
            <td colspan="12" class="subhead">
            <span class="subhead-left">Funding</span>
            </td>
        </tr>

        <%-- Add line header for newBCAFLine--%>
        <tr>
            <kul:htmlAttributeHeaderCell colspan="2" >
            </kul:htmlAttributeHeaderCell>
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.chartOfAccountCode"
                literalLabel="<span class=\"nowrap\">Cht</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.accountNumber"
                literalLabel="<span class=\"nowrap\">Acct</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.subAccountNumber"
                literalLabel="<span class=\"nowrap\">SAcct</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.financialObjectCode"
                literalLabel="<span class=\"nowrap\">Obj</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.financialSubObjectCode"
                literalLabel="<span class=\"nowrap\">SObj</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.emplid"
                literalLabel="<span class=\"nowrap\">Emplid</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.budgetConstructionIntendedIncumbent.personName"
                literalLabel="<span class=\"nowrap\">Name</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.budgetConstructionIntendedIncumbent.iuClassificationLevel"
                literalLabel="<span class=\"nowrap\">Lvl</span>" />
<%-- TODO add administrative post table ref to BCAF --%>
            <th>
                AdmPst
            </th>
            <th>
                Actions
            </th>
        </tr>

        <%-- add line data key line --%>
        <tr>
           <kul:htmlAttributeHeaderCell
               scope="row" rowspan="1" colspan="2"
               literalLabel="Add:" >
<%-- TODO add the others --%>
                <html:hidden property="newBCAFLine.universityFiscalYear" />
                <html:hidden property="newBCAFLine.positionNumber" />
                <html:hidden property="newBCAFLine.appointmentFundingDeleteIndicator" />
                <html:hidden property="newBCAFLine.versionNumber" />
           </kul:htmlAttributeHeaderCell>

           <bc:pbglLineDataCell dataCellCssClass="infoline"
               accountingLine="newBCAFLine"
               field="chartOfAccountsCode" detailFunction="loadChartInfo"
               detailField="chartOfAccounts.finChartOfAccountDescription"
               attributes="${bcafAttributes}" lookup="true" inquiry="true"
               boClassSimpleName="Chart"
               readOnly="${readOnly}"
               displayHidden="false"
               accountingLineValuesMap="${KualiForm.newBCAFLine.valuesMap}"
               anchor="salarynewLineLineAnchor" />

           <bc:pbglLineDataCell dataCellCssClass="infoline"
               accountingLine="newBCAFLine"
               field="accountNumber" detailFunction="loadAccountInfo"
               detailField="account.accountName"
               attributes="${bcafAttributes}" lookup="true" inquiry="true"
               boClassSimpleName="Account"
               readOnly="${readOnly}"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode"
               accountingLineValuesMap="${KualiForm.newBCAFLine.valuesMap}"
               anchor="salarynewLineLineFYAnchor" />
           
           <bc:pbglLineDataCell dataCellCssClass="infoline"
               accountingLine="newBCAFLine"
               field="subAccountNumber" detailFunction="loadSubAccountInfo"
               detailField="subAccount.subAccountName"
               attributes="${bcafAttributes}" lookup="true" inquiry="true"
               boClassSimpleName="SubAccount"
               readOnly="${readOnly}"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode,accountNumber"
               accountingLineValuesMap="${KualiForm.newBCAFLine.valuesMap}" />

           <bc:pbglLineDataCell dataCellCssClass="infoline"
               accountingLine="newBCAFLine"
               field="financialObjectCode" detailFunction="loadObjectInfo"
               detailFunctionExtraParam="'KualiForm.newBCAFLine.universityFiscalYear', "
               detailField="financialObject.financialObjectCodeShortName"
               attributes="${bcafAttributes}" lookup="true" inquiry="true"
               boClassSimpleName="ObjectCode"
               readOnly="${readOnly}"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode"
               accountingLineValuesMap="${KualiForm.newBCAFLine.valuesMap}"
               inquiryExtraKeyValues="universityFiscalYear=${KualiForm.newBCAFLine.universityFiscalYear}" />

           <c:set var="doLookupOrInquiry" value="false"/>
           <c:if test="${KualiForm.newBCAFLine.financialSubObjectCode ne Constants.DASHES_SUB_OBJECT_CODE}">
               <c:set var="doLookupOrInquiry" value="true"/>
           </c:if>
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="newBCAFLine"
               field="financialSubObjectCode" detailFunction="loadSubObjectInfo"
               detailFunctionExtraParam="'KualiForm.newBCAFLine.universityFiscalYear', "
               detailField="financialSubObject.financialSubObjectCdshortNm"
               attributes="${bcafAttributes}" inquiry="${doLookupOrInquiry}"
               boClassSimpleName="SubObjCd"
               readOnly="${readOnly}"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode,financialObjectCode,accountNumber"
               accountingLineValuesMap="${KualiForm.newBCAFLine.valuesMap}"
               inquiryExtraKeyValues="universityFiscalYear=${KualiForm.newBCAFLine.universityFiscalYear}" />

           <c:set var="doLookupOrInquiry" value="false"/>
           <c:if test="${KualiForm.newBCAFLine.emplid ne Constants.BudgetConstructionConstants.VACANT_EMPLID}">
               <c:set var="doLookupOrInquiry" value="true"/>
           </c:if>
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="newBCAFLine"
               field="emplid"
               attributes="${bcafAttributes}" inquiry="${doLookupOrInquiry}"
               boClassSimpleName="BudgetConstructionIntendedIncumbent"
               boPackageName="org.kuali.module.budget.bo"
               readOnly="${readOnly}"
               displayHidden="false"
               accountingLineValuesMap="${KualiForm.newBCAFLine.valuesMap}" />

<%-- TODO format name to break into separate lines at comma to save horizontal screen realestate --%>
            <c:choose>
            <c:when test="${KualiForm.newBCAFLine.emplid ne Constants.BudgetConstructionConstants.VACANT_EMPLID}">
                <bc:pbglLineDataCell dataCellCssClass="datacell"
                    cellProperty="newBCAFLine.budgetConstructionIntendedIncumbent.personName"
                    field="personName"
                    attributes="${intincAttributes}"
                    readOnly="true"
                    displayHidden="false" />
                <bc:pbglLineDataCell dataCellCssClass="datacell"
                    cellProperty="newBCAFLine.budgetConstructionIntendedIncumbent.iuClassificationLevel"
                    field="iuClassificationLevel"
                    attributes="${intincAttributes}"
                    readOnly="true"
                    displayHidden="false" />
<%--
                <td>${item.budgetConstructionIntendedIncumbent.iuClassificationLevel}</td>
--%>
<%-- TODO add adminstrative post ref to BCAF and here --%>
                <td>&nbsp;</td>
            </c:when>
            <c:otherwise>
                <bc:pbglLineDataCell dataCellCssClass="datacell"
                    cellProperty="newBCAFLine.budgetConstructionIntendedIncumbent.personName"
                    field="personName"
                    attributes="${intincAttributes}"
                    readOnly="true"
                    formattedNumberValue="${Constants.BudgetConstructionConstants.VACANT_EMPLID}"
                    displayHidden="false" />
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </c:otherwise>
            </c:choose>

            <td class="infoline" nowrap>
                <div align="center"><span class=nobord">
                <html:image property="methodToCall.insertBCAFLine.anchorsalarynewLineLineAnchor" src="images/tinybutton-add1.gif" title="Add an Appointment Funding Line" alt="Add an Appointment Funding Line" styleClass="tinybutton"/>
                </span></div>
            </td>
        </tr>

        <c:forEach items="${KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding}" var="item" varStatus="status">
        <tr>
            <kul:htmlAttributeHeaderCell />
            <kul:htmlAttributeHeaderCell literalLabel="<span class=\"nowrap\">Del&nbsp;</span>" scope="row">
            </kul:htmlAttributeHeaderCell>
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding.chartOfAccountCode"
                literalLabel="<span class=\"nowrap\">Cht</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding.accountNumber"
                literalLabel="<span class=\"nowrap\">Acct</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding.subAccountNumber"
                literalLabel="<span class=\"nowrap\">SAcct</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding.financialObjectCode"
                literalLabel="<span class=\"nowrap\">Obj</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding.financialSubObjectCode"
                literalLabel="<span class=\"nowrap\">SObj</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding.emplid"
                literalLabel="<span class=\"nowrap\">Emplid</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding.budgetConstructionIntendedIncumbent.personName"
                literalLabel="<span class=\"nowrap\">Name</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding.budgetConstructionIntendedIncumbent.iuClassificationLevel"
                literalLabel="<span class=\"nowrap\">Lvl</span>" />
<%-- TODO add administrative post table ref to BCAF --%>
            <th>
                AdmPst
            </th>
            <th>
                Actions
            </th>
              
        </tr>
        <tr>
           <kul:htmlAttributeHeaderCell scope="row" rowspan="1">
<%-- TODO add the others --%>
                <html:hidden property="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].universityFiscalYear" />
                <html:hidden property="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].positionNumber" />
                <html:hidden property="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].versionNumber" />
           </kul:htmlAttributeHeaderCell>
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentFundingDeleteIndicator"
               attributes="${bcafAttributes}"
               field="appointmentFundingDeleteIndicator"
               fieldAlign="center"
               readOnly="${readOnly}"
               rowSpan="1 "dataFieldCssClass="nobord"
               anchor="salaryexistingLineLineAnchor${status.index}" />
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="chartOfAccountsCode"
               detailField="chartOfAccounts.finChartOfAccountDescription"
               attributes="${bcafAttributes}" inquiry="true"
               boClassSimpleName="Chart"
               readOnly="true"
               displayHidden="false"
               accountingLineValuesMap="${item.valuesMap}" />
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="accountNumber" detailFunction="loadAccountInfo"
               detailField="account.accountName"
               attributes="${bcafAttributes}" inquiry="true"
               boClassSimpleName="Account"
               readOnly="true"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode"
               accountingLineValuesMap="${item.valuesMap}" />
           
           <c:set var="doAccountLookupOrInquiry" value="false"/>
           <c:if test="${item.subAccountNumber ne Constants.DASHES_SUB_ACCOUNT_NUMBER}">
               <c:set var="doAccountLookupOrInquiry" value="true"/>
           </c:if>
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="subAccountNumber" detailFunction="loadSubAccountInfo"
               detailField="subAccount.subAccountName"
               attributes="${bcafAttributes}" inquiry="${doAccountLookupOrInquiry}"
               boClassSimpleName="SubAccount"
               readOnly="true"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode,accountNumber"
               accountingLineValuesMap="${item.valuesMap}" />

           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="financialObjectCode" detailFunction="loadObjectInfo"
               detailFunctionExtraParam="KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].universityFiscalYear', "
               detailField="financialObject.financialObjectCodeShortName"
               attributes="${bcafAttributes}" lookup="true" inquiry="true"
               boClassSimpleName="ObjectCode"
               readOnly="true"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode"
               accountingLineValuesMap="${item.valuesMap}"
               inquiryExtraKeyValues="universityFiscalYear=${item.universityFiscalYear}" />

           <c:set var="doLookupOrInquiry" value="false"/>
           <c:if test="${item.financialSubObjectCode ne Constants.DASHES_SUB_OBJECT_CODE}">
               <c:set var="doLookupOrInquiry" value="true"/>
           </c:if>
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="financialSubObjectCode" detailFunction="loadSubObjectInfo"
               detailFunctionExtraParam="'KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].universityFiscalYear', "
               detailField="financialSubObject.financialSubObjectCdshortNm"
               attributes="${bcafAttributes}" inquiry="${doLookupOrInquiry}"
               boClassSimpleName="SubObjCd"
               readOnly="true"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode,financialObjectCode,accountNumber"
               accountingLineValuesMap="${item.valuesMap}"
               inquiryExtraKeyValues="universityFiscalYear=${item.universityFiscalYear}" />

           <c:set var="doLookupOrInquiry" value="false"/>
           <c:if test="${item.emplid ne Constants.BudgetConstructionConstants.VACANT_EMPLID}">
               <c:set var="doLookupOrInquiry" value="true"/>
           </c:if>
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="emplid"
               attributes="${bcafAttributes}" inquiry="${doLookupOrInquiry}"
               boClassSimpleName="BudgetConstructionIntendedIncumbent"
               boPackageName="org.kuali.module.budget.bo"
               readOnly="true"
               displayHidden="false"
               accountingLineValuesMap="${item.valuesMap}" />

<%-- TODO format name to break into separate lines at comma to save horizontal screen realestate --%>
            <c:choose>
            <c:when test="${item.emplid ne Constants.BudgetConstructionConstants.VACANT_EMPLID}">
                <bc:pbglLineDataCell dataCellCssClass="datacell"
                    cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].budgetConstructionIntendedIncumbent.personName"
                    field="personName"
                    attributes="${intincAttributes}"
                    readOnly="true"
                    displayHidden="false" />
                <bc:pbglLineDataCell dataCellCssClass="datacell"
                    cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].budgetConstructionIntendedIncumbent.iuClassificationLevel"
                    field="iuClassificationLevel"
                    attributes="${intincAttributes}"
                    readOnly="true"
                    displayHidden="false" />
<%--
                <td>${item.budgetConstructionIntendedIncumbent.iuClassificationLevel}</td>
--%>
<%-- TODO add adminstrative post ref to BCAF and here --%>
                <td>&nbsp;</td>
            </c:when>
            <c:otherwise>
                <bc:pbglLineDataCell dataCellCssClass="datacell"
                    cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].budgetConstructionIntendedIncumbent.personName"
                    field="personName"
                    attributes="${intincAttributes}"
                    readOnly="true"
                    formattedNumberValue="${Constants.BudgetConstructionConstants.VACANT_EMPLID}"
                    displayHidden="false" />
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </c:otherwise>
            </c:choose>

            <td class="datacell" nowrap>
                <div align="center"><span class=nobord">&nbsp;
                  <c:if test="${item.emplid ne Constants.BudgetConstructionConstants.VACANT_EMPLID}">
                    <c:if test="${!readOnly}">
                        <html:image property="methodToCall.performVacateSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" src="images/tinybutton-clear1.gif" title="Vacate Salary Setting Line ${status.index}" alt="Vacate Salary Setting Line ${status.index}" styleClass="tinybutton" />
                        <br>
                    </c:if>
                  </c:if>
                  <c:if test="${!empty item.bcnCalculatedSalaryFoundationTracker && !readOnly}">
                    <html:image property="methodToCall.performPercentAdjustmentSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" src="images/tinybutton-percentincdec.gif" title="Percent Adjustment For Line ${status.index}" alt="Percent Adjustment For Line ${status.index}" styleClass="tinybutton" />
                  </c:if>
                </span></div>
            </td>
        </tr>
                                        
        <tr>
            <kul:htmlAttributeHeaderCell colspan="2" align="left" scope="col" />
            <kul:htmlAttributeHeaderCell colspan="3" align="left" literalLabel="CSF" scope="col" />
            <kul:htmlAttributeHeaderCell colspan="2" align="left" literalLabel="Request" scope="col" />
            <kul:htmlAttributeHeaderCell colspan="2" align="left" literalLabel="Leaves Req.CSF" scope="col" />
            <kul:htmlAttributeHeaderCell colspan="3" align="left" literalLabel="Tot.Int." scope="col" />
        </tr>
        <tr>
            <kul:htmlAttributeHeaderCell colspan="2" scope="col" />

            <c:choose>
            <c:when test="${!empty item.bcnCalculatedSalaryFoundationTracker}">
                <bc:pbglLineDataCell dataCellCssClass="datacell"
                    cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].bcnCalculatedSalaryFoundationTracker[0].csfAmount"
                    field="csfAmount"
                    fieldAlign="right"
                    attributes="${bcsfAttributes}"
                    readOnly="true"
                    colSpan="2" dataFieldCssClass="amount"
                    displayHidden="false" />
                <fmt:formatNumber value="${KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[status.index].bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />
                <bc:pbglLineDataCell dataCellCssClass="datacell"
                    cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity"
                    field="csfFullTimeEmploymentQuantity"
                    fieldAlign="right"
                    attributes="${bcsfAttributes}"
                    readOnly="true"
                    formattedNumberValue="${formattedNumber}&nbsp;"
                    dataFieldCssClass="amount"
                    displayHidden="false" />
            </c:when>
            <c:otherwise>
                <td colspan="2">&nbsp;</td>
                <td>&nbsp;</td>
            </c:otherwise>
            </c:choose>

            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedAmount"
                attributes="${bcafAttributes}"
                field="appointmentRequestedAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
            <fmt:formatNumber value="${KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[status.index].appointmentRequestedFteQuantity}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedFteQuantity"
                attributes="${bcafAttributes}"
                field="appointmentRequestedFteQuantity"
                fieldAlign="right"
                readOnly="true"
                formattedNumberValue="${formattedNumber}&nbsp;"
                rowSpan="1" dataFieldCssClass="amount" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedCsfAmount"
                attributes="${bcafAttributes}"
                field="appointmentRequestedCsfAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
            <fmt:formatNumber value="${KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[status.index].appointmentRequestedCsfFteQuantity}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedCsfFteQuantity"
                attributes="${bcafAttributes}"
                field="appointmentRequestedCsfFteQuantity"
                fieldAlign="right"
                readOnly="true"
                formattedNumberValue="${formattedNumber}&nbsp;"
                rowSpan="1" dataFieldCssClass="amount" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentTotalIntendedAmount"
                attributes="${bcafAttributes}"
                field="appointmentTotalIntendedAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                colSpan="2" rowSpan="1" dataFieldCssClass="amount" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentTotalIntendedFteQuantity"
                attributes="${bcafAttributes}"
                field="appointmentTotalIntendedFteQuantity"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>
        <tr>
            <kul:htmlAttributeHeaderCell colspan="4" scope="col" />

            <c:choose>
            <c:when test="${!empty item.bcnCalculatedSalaryFoundationTracker}">
                <fmt:formatNumber value="${KualiForm.budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[status.index].bcnCalculatedSalaryFoundationTracker[0].csfTimePercent}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />
                <bc:pbglLineDataCell dataCellCssClass="datacell"
                    cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity"
                    field="csfTimePercent"
                    fieldAlign="right"
                    attributes="${bcsfAttributes}"
                    readOnly="true"
                    fieldTrailerValue="<small>%</small>"
                    formattedNumberValue="${formattedNumber}"
                    dataFieldCssClass="amount"
                    displayHidden="false" />
            </c:when>
            <c:otherwise>
                <td>&nbsp;</td>
            </c:otherwise>
            </c:choose>

<%-- TODO figure out handling of reason annotation functionality, like BA monthly? or BC monthly? or? --%>

            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentFundingMonth"
                attributes="${bcafAttributes}"
                field="appointmentFundingMonth"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedTimePercent"
                attributes="${bcafAttributes}"
                field="appointmentRequestedTimePercent"
                fieldTrailerValue="<small>%</small>"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
<%-- TODO need ajax javascript to handle detail field changes --%>
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="appointmentFundingDurationCode"
               detailField="budgetConstructionDuration.appointmentDurationDescription"
               attributes="${bcafAttributes}" inquiry="true"
               boClassSimpleName="BudgetConstructionDuration"
               lookupUnkeyedFieldConversions="appointmentDurationCode:budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentFundingDurationCode,"
               readOnly="${readOnly}"
               displayHidden="false"
               accountingLineValuesMap="${item.valuesMap}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionPosition.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedCsfTimePercent"
                attributes="${bcafAttributes}"
                field="appointmentRequestedCsfTimePercent"
                fieldTrailerValue="<small>%</small>"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
            <td colspan="3">&nbsp;</td>
        </tr>

        </c:forEach>
    </table>
</div>
</kul:tabTop>