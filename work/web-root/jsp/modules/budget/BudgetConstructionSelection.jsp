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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<c:if test="${!accountingLineScriptsLoaded}">
	<script type='text/javascript' src="dwr/interface/ChartService.js"></script>
	<script type='text/javascript' src="dwr/interface/AccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/SubAccountService.js"></script>
	<script language="JavaScript" type="text/javascript" src="scripts/kfs/objectInfo.js"></script>
	<c:set var="accountingLineScriptsLoaded" value="true" scope="page" />
</c:if>

<c:set var="bcHeaderAttributes" value="${DataDictionary.BudgetConstructionHeader.attributes}" />
<c:set var="accountAttributes" value="${DataDictionary.Account.attributes}" />
<c:set var="subFundGroupAttributes" value="${DataDictionary.SubFundGroup.attributes}" />
<c:set var="orgAttributes" value="${DataDictionary.Org.attributes}" />
<c:set var="orgPropString" value="budgetConstructionHeader.account.organization" />
<c:set var="accountRptsAttributes" value="${DataDictionary.BudgetConstructionAccountReports.attributes}" />
<c:set var="accountRptsPropString" value="budgetConstructionHeader.budgetConstructionAccountReports" />
<c:set var="orgRptsAttributes" value="${DataDictionary.BudgetConstructionOrganizationReports.attributes}" />
<c:set var="orgRptsPropString" value="budgetConstructionHeader.budgetConstructionAccountReports.budgetConstructionOrganizationReports" />

<%-- hack to get around ojb retrieve problems when account key is bad, don't show the info fields --%>
<c:catch var="e">
	<c:set var="showTheDetail" value="${!empty KualiForm.budgetConstructionHeader.account.subFundGroupCode}" scope="page" />
</c:catch>
<c:if test="${e!=null}">
	<c:set var="showTheDetail" value="false" scope="page" />
</c:if>

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetBudgetConstructionSelection" renderMultipart="true"
	docTitle="Budget Construction Selection"
    transactionalDocument="false">

<%--	<kul:hiddenDocumentFields /> --%>

	<kul:errors errorTitle="Errors found in Search Criteria:" />
	<kul:messages/>

    <table align="center" cellpadding="0" cellspacing="0" class="datatable-100">
	    <tr>
<%--TODO probably should assign width in css --%>
            <th class="grid" align="right" width="10%" colspan="1">
			    <span class="nowrap">BC Fiscal Year:</span>
                <html:hidden property="universityFiscalYear"/>
            </th>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <span class="nowrap">
                ${KualiForm.universityFiscalYear}&nbsp;
            </span>
            </td>
            <th class="grid" colspan="5">
			    &nbsp;
            </th>
	    </tr>
    	<tr>
            <th class="grid" colspan="7" align="left">
                <br>
                Budget Construction Document Open
                <br><br>
		    </th>
		</tr>
    	<tr>
            <td class="grid" colspan="4">
            <div align="center">
              <html:image property="methodToCall.performMyAccounts.anchoraccountControlsAnchor" src="${ConfigProperties.externalizable.images.url}buttonsmall_myaccounts.gif" title="Find My Budgeted Accounts" alt="Find My Budgeted Accounts" styleClass="tinybutton"/>&nbsp;&nbsp;&nbsp;
              <html:image property="methodToCall.performMyOrganization.anchoraccountControlsAnchor" src="${ConfigProperties.externalizable.images.url}buttonsmall_myorganization.gif" title="Find My Organization Budgeted Accounts" alt="Find My Organization Budgeted Accounts" styleClass="tinybutton"/>
            </div>
		    </td>
            <td class="grid" colspan="3">
			    &nbsp;
		    </td>
		</tr>
	    <tr>
            <th class="grid" colspan="2" rowspan="2">&nbsp;</th>
		    <th class="grid" align="center" colspan="1">
                <html:hidden property="budgetConstructionHeader.universityFiscalYear"/>
                <html:hidden property="budgetConstructionHeader.documentNumber"/>
			    <kul:htmlAttributeLabel
			        attributeEntry="${bcHeaderAttributes.chartOfAccountsCode}"
			        labelFor="budgetConstructionHeader.chartOfAccountsCode"
			        useShortLabel="true" noColon="true" />
		    </th>
		    <th class="grid" align="center" colspan="1">
			    <kul:htmlAttributeLabel
			        attributeEntry="${bcHeaderAttributes.accountNumber}"
			        useShortLabel="true" noColon="true" />
		    </th>
		    <th class="grid" align="center" colspan="1">
			    <kul:htmlAttributeLabel
			        attributeEntry="${bcHeaderAttributes.subAccountNumber}"
			        useShortLabel="true" noColon="true" />
		    </th>
		    <th class="grid" align="center" colspan="2">
			    Action
		    </th>
	    </tr>
	    <tr>
            <%--first cell in row above spans two rows --%>
            <bc:pbglLineDataCell dataCellCssClass="grid"
                accountingLine="budgetConstructionHeader"
                field="chartOfAccountsCode" detailFunction="loadChartInfo"
                detailField="chartOfAccounts.finChartOfAccountDescription"
                attributes="${bcHeaderAttributes}" inquiry="true"
                boClassSimpleName="Chart"
                readOnly="false"
                displayHidden="false"
                colSpan="1"
                accountingLineValuesMap="${KualiForm.budgetConstructionHeader.valuesMap}"
                anchor="budgetConstructionHeaderAnchor" />
            <bc:pbglLineDataCell dataCellCssClass="grid"
                accountingLine="budgetConstructionHeader"
                field="accountNumber" detailFunction="loadAccountInfo"
                detailField="account.accountName"
                attributes="${bcHeaderAttributes}" lookup="true" inquiry="true"
                boClassSimpleName="Account"
                readOnly="false"
                displayHidden="false"
                colSpan="1"
                lookupOrInquiryKeys="chartOfAccountsCode"
                accountingLineValuesMap="${KualiForm.budgetConstructionHeader.valuesMap}"
                anchor="budgetConstructionHeaderAccountAnchor" />
            <bc:pbglLineDataCell dataCellCssClass="grid"
                accountingLine="budgetConstructionHeader"
                field="subAccountNumber" detailFunction="loadSubAccountInfo"
                detailField="subAccount.subAccountName"
                attributes="${bcHeaderAttributes}" lookup="true" inquiry="true"
                boClassSimpleName="SubAccount"
                readOnly="false"
                displayHidden="false"
                colSpan="1"
                lookupOrInquiryKeys="chartOfAccountsCode,accountNumber"
                accountingLineValuesMap="${KualiForm.budgetConstructionHeader.valuesMap}"
                anchor="budgetConstructionHeaderSubAccountAnchor" />
            <td class="grid" nowrap colspan="2">
            <div align="center">
                <html:image property="methodToCall.performBCDocumentOpen.anchorbudgetConstructionHeaderAnchor" src="${ConfigProperties.externalizable.images.url}tinybutton-loaddoc.gif" title="Load Budget Construction Document" alt="Load Budget Construction Document" styleClass="tinybutton" />
            </div>
            </td>
	    </tr>
	    <tr>
		    <th class="grid" align="right" colspan="2">
		        Sub-Fund Group:
		    </th>
            <td class="grid" valign="center" rowspan="1" colspan="2">
                <c:if test="${showTheDetail}" >
                <kul:htmlControlAttribute
                    property="budgetConstructionHeader.account.subFundGroupCode"
                    attributeEntry="${accountAttributes.subFundGroupCode}"
                    readOnly="true"
                    readOnlyBody="true">
                    <kul:inquiry
                        boClassName="org.kuali.module.chart.bo.SubFundGroup"
                        keyValues="subFundGroupCode=${KualiForm.budgetConstructionHeader.account.subFundGroupCode}"
                        render="${!empty KualiForm.budgetConstructionHeader.accountNumber}">
                        <html:hidden write="true" property="budgetConstructionHeader.account.subFundGroupCode" />
                    </kul:inquiry>&nbsp;
                </kul:htmlControlAttribute>
                </c:if>&nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="3">
                <c:if test="${showTheDetail}" >
                <kul:htmlControlAttribute
                    property="budgetConstructionHeader.account.subFundGroup.subFundGroupDescription"
                    attributeEntry="${subFundGroupAttributes.subfundGroupDescription}"
                    readOnly="true"/>
                </c:if>&nbsp;
            </td>
	    </tr>
	    <tr>
		    <th class="grid" align="right" colspan="2">
		        Current Year Org:
		    </th>
            <td class="grid" valign="center" rowspan="1" colspan="1">
			    &nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <c:if test="${showTheDetail}" >
            <kul:htmlControlAttribute
                property="budgetConstructionHeader.account.organizationCode"
                attributeEntry="${accountAttributes.organizationCode}"
                readOnly="true"
                readOnlyBody="true">
                <kul:inquiry
                    boClassName="org.kuali.module.chart.bo.Org"
                    keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.account.chartOfAccountsCode}&amp;organizationCode=${KualiForm.budgetConstructionHeader.account.organizationCode}"
                    render="${!empty KualiForm.budgetConstructionHeader.account.organizationCode}">
                	<html:hidden write="true" property="budgetConstructionHeader.account.organizationCode" />
                </kul:inquiry>&nbsp;
	      	</kul:htmlControlAttribute>
            </c:if>&nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="3">
            <c:if test="${showTheDetail}" >
            <kul:htmlControlAttribute
                property="${orgPropString}.organizationName"
                attributeEntry="${orgAttributes.organizationName}"
                readOnly="true"/>
            </c:if>&nbsp;
            </td>
	    </tr>
	    <tr>
		    <th class="grid" align="right" colspan="2">
		        Rpts To:
		    </th>
            <td class="grid" valign="center" rowspan="1" colspan="1">
                <c:if test="${showTheDetail}" >
                <kul:htmlControlAttribute
                    property="${orgPropString}.reportsToChartOfAccountsCode"
                    attributeEntry="${orgAttributes.reportsToChartOfAccountsCode}"
                    readOnly="true"
                    readOnlyBody="true">
                    <kul:inquiry
                        boClassName="org.kuali.module.chart.bo.Chart"
                        keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.account.organization.reportsToChartOfAccountsCode}"
                        render="${!empty KualiForm.budgetConstructionHeader.account.organization.reportsToChartOfAccountsCode}">
                    <html:hidden write="true" property="${orgPropString}.reportsToChartOfAccountsCode" />
                </kul:inquiry>&nbsp;
	      	</kul:htmlControlAttribute>
            </c:if>&nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <c:if test="${showTheDetail}" >
	      	<kul:htmlControlAttribute
	      		property="${orgPropString}.reportsToOrganizationCode"
	      		attributeEntry="${orgAttributes.reportsToOrganizationCode}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.chart.bo.Org"
				    keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.account.organization.reportsToChartOfAccountsCode}&amp;organizationCode=${KualiForm.budgetConstructionHeader.account.organization.reportsToOrganizationCode}"
				    render="${!empty KualiForm.budgetConstructionHeader.account.organization.reportsToOrganizationCode}">
			    	<html:hidden write="true" property="${orgPropString}.reportsToOrganizationCode" />
				</kul:inquiry>&nbsp;
	      	</kul:htmlControlAttribute>
            </c:if>&nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="3">
            <c:if test="${showTheDetail}" >
            <kul:htmlControlAttribute
                property="${orgPropString}.reportsToOrganization.organizationName"
                attributeEntry="${organizationAttributes.organizationName}"
                readOnly="true"/>
            </c:if>&nbsp;
            </td>
	    </tr>
	    <tr>
		    <th class="grid" align="right" colspan="2">
		        Next Year Org:
		    </th>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <c:if test="${showTheDetail}" >
            <kul:htmlControlAttribute
                property="${accountRptsPropString}.reportsToChartOfAccountsCode"
                attributeEntry="${accountRptsAttributes.reportsToChartOfAccountsCode}"
                readOnly="true"
                readOnlyBody="true">
                <kul:inquiry
                    boClassName="org.kuali.module.chart.bo.Chart"
                    keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.reportsToChartOfAccountsCode}"
                    render="${!empty KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.reportsToChartOfAccountsCode}">
                	<html:hidden write="true" property="${accountRptsPropString}.reportsToChartOfAccountsCode" />
                </kul:inquiry>&nbsp;
            </kul:htmlControlAttribute>
            </c:if>&nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <c:if test="${showTheDetail}" >
            <kul:htmlControlAttribute
                property="${accountRptsPropString}.reportsToOrganizationCode"
                attributeEntry="${accountRptsAttributes.reportsToOrganizationCode}"
                readOnly="true"
                readOnlyBody="true">
                <kul:inquiry
                    boClassName="org.kuali.module.budget.bo.BudgetConstructionOrganizationReports"
                    keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.reportsToChartOfAccountsCode}&amp;organizationCode=${KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.reportsToOrganizationCode}"
                    render="${!empty KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.reportsToOrganizationCode}">
                	<html:hidden write="true" property="${accountRptsPropString}.reportsToOrganizationCode" />
                </kul:inquiry>&nbsp;
            </kul:htmlControlAttribute>
            </c:if>&nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="3">
            <c:if test="${showTheDetail}" >
            <kul:htmlControlAttribute
                property="${accountRptsPropString}.reportsToOrganization.organizationName"
                attributeEntry="${orgAttributes.organizationName}"
                readOnly="true"/>
            </td>
            </c:if>&nbsp;
	    </tr>
	    <tr>
		    <th class="grid" align="right" colspan="2">
		        Rpts To:
		    </th>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <c:if test="${showTheDetail}" >
            <kul:htmlControlAttribute
                property="${orgRptsPropString}.reportsToChartOfAccountsCode"
                attributeEntry="${orgRptsAttributes.reportsToChartOfAccountsCode}"
                readOnly="true"
                readOnlyBody="true">
                <kul:inquiry
                    boClassName="org.kuali.module.chart.bo.Chart"
                    keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToChartOfAccountsCode}"
                    render="${!empty KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToChartOfAccountsCode}">
                    <html:hidden write="true" property="${orgRptsPropString}.reportsToChartOfAccountsCode" />
                </kul:inquiry>&nbsp;
            </kul:htmlControlAttribute>
            </c:if>&nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <c:if test="${showTheDetail}" >
	      	<kul:htmlControlAttribute
	      		property="${orgRptsPropString}.reportsToOrganizationCode"
	      		attributeEntry="${orgRptsAttributes.reportsToOrganizationCode}"
	      		readOnly="true"
	      		readOnlyBody="true">
	      		<kul:inquiry
				    boClassName="org.kuali.module.chart.bo.Org"
				    keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToChartOfAccountsCode}&amp;organizationCode=${KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToOrganizationCode}"
				    render="${!empty KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToOrganizationCode}">
			    	<html:hidden write="true" property="${orgRptsPropString}.reportsToOrganizationCode" />
				</kul:inquiry>&nbsp;
	      	</kul:htmlControlAttribute>
            </c:if>&nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="3">
            <c:if test="${showTheDetail}" >
            <kul:htmlControlAttribute
                property="${orgRptsPropString}.reportsToOrganization.organizationName"
                attributeEntry="${organizationAttributes.organizationName}"
                readOnly="${true}"/>
            </td>
            </c:if>&nbsp;
		</tr>
    	<tr>
            <th class="grid" colspan="7" align="left">
                <br>
                Budget Construction Organization Salary Setting/Report/Control
                <br><br>
		    </th>
		</tr>
    	<tr>
            <td class="grid" colspan="4">
            <div align="center">
              <html:image property="methodToCall.performOrgSalarySetting.anchororgControlsAnchor" src="${ConfigProperties.externalizable.images.url}buttonsmall_orgsalsetting.gif" title="Organization Salary Setting" alt="Organization Salary Setting" styleClass="tinybutton"/>&nbsp;&nbsp;&nbsp;
              <html:image property="methodToCall.performReportDump.anchororgControlsAnchor" src="${ConfigProperties.externalizable.images.url}buttonsmall_orgreportdump.gif" title="Organization Report/Dump" alt="Organization Report/Dump" styleClass="tinybutton"/>&nbsp;&nbsp;&nbsp;
              <html:image property="methodToCall.performRequestImport.anchororgControlsAnchor" src="${ConfigProperties.externalizable.images.url}buttonsmall_reqimport.gif" title="Organization Request Import" alt="Organization Request Import" styleClass="tinybutton" />
            </div>
		    </td>
            <td class="grid" colspan="3">
            <div align="center">
              <html:image property="methodToCall.performOrgPullup.anchororgControlsAnchor" src="${ConfigProperties.externalizable.images.url}buttonsmall_orgpullup.gif" title="Organization Pull Up" alt="Organization Pull Up" styleClass="tinybutton"/>&nbsp;&nbsp;&nbsp;
              <html:image property="methodToCall.performOrgPushdown.anchororgControlsAnchor" src="${ConfigProperties.externalizable.images.url}buttonsmall_orgpushdown.gif" title="Organization Push Down" alt="Organization Push Down" styleClass="tinybutton" />
            </div>
		    </td>
		</tr>
	</table>

    <div id="globalbuttons" class="globalbuttons">
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.returnToCaller" title="close" alt="close"/>
    </div>

<%-- Need these here to override and initialize vars used by objectinfo.js to BC specific --%>
<SCRIPT type="text/javascript">
  var kualiForm = document.forms['KualiForm'];
  var kualiElements = kualiForm.elements;
</SCRIPT>
</kul:page>
