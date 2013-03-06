<%--
 Copyright 2007 The Kuali Foundation
 
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
<%@ page import="org.kuali.kfs.sys.context.SpringContext"%>
<%@ page import="org.kuali.kfs.coa.service.AccountService"%>

<c:set var="bcHeaderAttributes"
	value="${DataDictionary.BudgetConstructionHeader.attributes}" />
<c:set var="accountAttributes"
	value="${DataDictionary.Account.attributes}" />
<c:set var="subFundGroupAttributes"
	value="${DataDictionary.SubFundGroup.attributes}" />
<c:set var="orgAttributes"
	value="${DataDictionary.Organization.attributes}" />
<c:set var="orgPropString"
	value="budgetConstructionHeader.account.organization" />
<c:set var="accountsCanCrossCharts"
	value="<%=SpringContext.getBean(AccountService.class).accountsCanCrossCharts()%>" />

<c:if test="${KualiForm.accountReportsExist}">
	<c:set var="accountRptsAttributes"
		value="${DataDictionary.BudgetConstructionAccountReports.attributes}" />
	<c:set var="accountRptsPropString"
		value="budgetConstructionHeader.budgetConstructionAccountReports" />
	<c:set var="orgRptsAttributes"
		value="${DataDictionary.BudgetConstructionOrganizationReports.attributes}" />
	<c:set var="orgRptsPropString"
		value="budgetConstructionHeader.budgetConstructionAccountReports.budgetConstructionOrganizationReports" />
</c:if>

<%-- hack to get around ojb retrieve problems when account key is bad, don't show the info fields --%>
<c:catch var="e">
	<c:set var="showTheDetail"
		value="${!empty KualiForm.budgetConstructionHeader.account.subFundGroupCode}"
		scope="page" />

</c:catch>
<c:if test="${e!=null}">
	<c:set var="showTheDetail" value="false" scope="page" />
</c:if>

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetBudgetConstructionSelection"
	renderMultipart="true" docTitle="" transactionalDocument="false">

    <c:if test="${!accountingLineScriptsLoaded}">
        <script type='text/javascript' src="dwr/interface/ChartService.js"></script>
        <script type='text/javascript' src="dwr/interface/AccountService.js"></script>
        <script type='text/javascript' src="dwr/interface/SubAccountService.js"></script>
        <script language="JavaScript" type="text/javascript" src="scripts/sys/objectInfo.js"></script>
        <script language="JavaScript" type="text/javascript" src="scripts/module/bc/objectInfo.js"></script>
        <c:set var="accountingLineScriptsLoaded" value="true" scope="request" />
    </c:if>

	<strong>
	<h2>Budget Construction Selection<a
		href="${ConfigProperties.externalizable.help.url}default.htm?turl=WordDocuments%2Fbudgetconstructionselection.htm"
		tabindex="${KualiForm.nextArbitrarilyHighIndex}" target="helpWindow"
		title="[Help]Upload"> <img
		src="${ConfigProperties.kr.externalizable.images.url}my_cp_inf.gif"
		title="[Help] Upload" alt="[Help] Upload" hspace=5 border=0
		align="middle"></a></h2>
	</strong>
	</br>
	<%--	<sys:hiddenDocumentFields /> --%>

	<kul:errors
		keyMatch="${BCConstants.BUDGET_CONSTRUCTION_SELECTION_ERRORS}"
		errorTitle="Errors found in Search Criteria:" />
	<c:forEach items="${KualiForm.messages}" var="message">
	   ${message}
	</c:forEach>

	<c:if
		test="${!empty KualiForm.universityFiscalYear && !KualiForm.sessionInProgressDetected}">
		<table align="center" cellpadding="0" cellspacing="0"
			class="datatable-100">
			<tr>
				<th class="grid" align="right" width="10%" colspan="1"><span
					class="nowrap">BC Fiscal Year:</span> <html:hidden
					property="universityFiscalYear" /></th>
				<td class="grid" valign="center" rowspan="1" colspan="1"><span
					class="nowrap"> ${KualiForm.universityFiscalYear}&nbsp; </span></td>
				<th class="grid" colspan="5">&nbsp;</th>
			</tr>
			<tr>
				<th class="grid" colspan="7" align="left"><br>
				Budget Construction Document Open <br>
				<br>
				</th>
			</tr>
			<tr>
				<td class="grid" colspan="4">
				<div align="center"><html:image
					property="methodToCall.performMyAccounts.anchoraccountControlsAnchor"
					src="${ConfigProperties.externalizable.images.url}tinybutton-myaccounts.gif"
					title="Find My Budgeted Accounts" alt="Find My Budgeted Accounts"
					styleClass="tinybutton" />&nbsp;&nbsp;&nbsp; <html:image
					property="methodToCall.performMyOrganization.anchoraccountControlsAnchor"
					src="${ConfigProperties.externalizable.images.url}tinybutton-myorg.gif"
					title="Find My Organization Budgeted Accounts"
					alt="Find My Organization Budgeted Accounts"
					styleClass="tinybutton" /></div>
				</td>
				<td class="grid" colspan="3">&nbsp;</td>
			</tr>
			<tr>
				<th class="grid" colspan="2" rowspan="2">&nbsp;</th>
				<th class="grid" align="center" colspan="1">
				<html:hidden property="budgetConstructionHeader.universityFiscalYear" /> 
				<html:hidden property="budgetConstructionHeader.documentNumber" />
				<kul:htmlAttributeLabel	attributeEntry="${bcHeaderAttributes.chartOfAccountsCode}"
					labelFor="budgetConstructionHeader.chartOfAccountsCode"
					useShortLabel="true" noColon="true" /></th>
				<th class="grid" align="center" colspan="1"><kul:htmlAttributeLabel
					attributeEntry="${bcHeaderAttributes.accountNumber}"
					useShortLabel="true" noColon="true" /></th>
				<th class="grid" align="center" colspan="1"><kul:htmlAttributeLabel
					attributeEntry="${bcHeaderAttributes.subAccountNumber}"
					useShortLabel="true" noColon="true" /></th>
				<th class="grid" align="center" colspan="2">Action</th>
			</tr>
			<tr>
				<%--first cell in row above spans two rows --%>
				<c:if test="${!accountsCanCrossCharts}">
				<html:hidden property="budgetConstructionHeader.chartOfAccountsCode" />
					<bc:pbglLineDataCell dataCellCssClass="grid"
						accountingLine="budgetConstructionHeader"
						field="chartOfAccountsCode"
						detailField="chartOfAccounts.finChartOfAccountDescription"
						attributes="${bcHeaderAttributes}" inquiry="true"
						boClassSimpleName="Chart" readOnly="true" displayHidden="false"
						colSpan="1"
						accountingLineValuesMap="${KualiForm.budgetConstructionHeader.valuesMap}"
						anchor="budgetConstructionHeaderAnchor" 
						divId="budgetConstructionHeader.chartOfAccountsCode.div"/>
					<bc:pbglLineDataCell dataCellCssClass="grid"
						accountingLine="budgetConstructionHeader" field="accountNumber"
						detailFunction="budgetObjectInfoUpdator.loadChartAccountInfo" detailField="account.accountName"
						attributes="${bcHeaderAttributes}" lookup="true" inquiry="true"
						boClassSimpleName="Account" readOnly="false" displayHidden="false"
						colSpan="1" lookupOrInquiryKeys="chartOfAccountsCode"
						accountingLineValuesMap="${KualiForm.budgetConstructionHeader.valuesMap}"
						anchor="budgetConstructionHeaderAccountAnchor" />
				</c:if>
				<c:if test="${accountsCanCrossCharts}">
					<bc:pbglLineDataCell dataCellCssClass="grid"
						accountingLine="budgetConstructionHeader"
						field="chartOfAccountsCode" detailFunction="loadChartInfo"
						detailField="chartOfAccounts.finChartOfAccountDescription"
						attributes="${bcHeaderAttributes}" inquiry="true"
						boClassSimpleName="Chart" readOnly="false" displayHidden="false"
						colSpan="1"
						accountingLineValuesMap="${KualiForm.budgetConstructionHeader.valuesMap}"
						anchor="budgetConstructionHeaderAnchor" />
					<bc:pbglLineDataCell dataCellCssClass="grid"
						accountingLine="budgetConstructionHeader" field="accountNumber"
						detailFunction="loadAccountInfo" detailField="account.accountName"
						attributes="${bcHeaderAttributes}" lookup="true" inquiry="true"
						boClassSimpleName="Account" readOnly="false" displayHidden="false"
						colSpan="1" lookupOrInquiryKeys="chartOfAccountsCode"
						accountingLineValuesMap="${KualiForm.budgetConstructionHeader.valuesMap}"
						anchor="budgetConstructionHeaderAccountAnchor" />
				</c:if>
				<bc:pbglLineDataCell dataCellCssClass="grid"
					accountingLine="budgetConstructionHeader" field="subAccountNumber"
					detailFunction="loadSubAccountInfo"
					detailField="subAccount.subAccountName"
					attributes="${bcHeaderAttributes}" lookup="true" inquiry="true"
					boClassSimpleName="SubAccount" readOnly="false"
					displayHidden="false" colSpan="1"
					lookupOrInquiryKeys="chartOfAccountsCode,accountNumber"
					accountingLineValuesMap="${KualiForm.budgetConstructionHeader.valuesMap}"
					anchor="budgetConstructionHeaderSubAccountAnchor" />
                <c:set var="refreshTabIndex" value="${KualiForm.currentTabIndex}" />
                <c:set var="dummyIncrementVar" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
                <c:set var="loadTabIndex" value="${KualiForm.currentTabIndex}" />
                <c:set var="dummyIncrementVar" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
				<td class="grid" nowrap colspan="2">
				<div align="center"><html:image
					property="methodToCall.refresh.anchorbudgetConstructionHeaderAnchor"
					src="${ConfigProperties.externalizable.images.url}tinybutton-refresh.gif"
					title="Refresh" alt="Refresh" tabindex="${loadTabIndex}" styleClass="tinybutton" /> <html:image
					property="methodToCall.performBCDocumentOpen.anchorbudgetConstructionHeaderAnchor"
					src="${ConfigProperties.externalizable.images.url}tinybutton-loaddoc.gif"
					title="Load Budget Construction Document" alt="Load Budget Construction Document"
					tabindex="${refreshTabIndex}" styleClass="tinybutton" />
				</div>
				</td>
			</tr>
			<tr>
				<th class="grid" align="right" colspan="2">Sub-Fund Group:</th>
				<td class="grid" valign="center" rowspan="1" colspan="2">
				<c:if test="${showTheDetail}">
					<kul:htmlControlAttribute
						property="budgetConstructionHeader.account.subFundGroupCode"
						attributeEntry="${accountAttributes.subFundGroupCode}"
						readOnly="true" readOnlyBody="true">
						<kul:inquiry
							boClassName="org.kuali.kfs.coa.businessobject.SubFundGroup"
							keyValues="subFundGroupCode=${KualiForm.budgetConstructionHeader.account.subFundGroupCode}"
							render="${!empty KualiForm.budgetConstructionHeader.accountNumber}">
							<html:hidden write="true"
								property="budgetConstructionHeader.account.subFundGroupCode" />
						</kul:inquiry>&nbsp;
                    </kul:htmlControlAttribute>
				</c:if>&nbsp;</td>
				<td class="grid" valign="center" rowspan="1" colspan="3">
				<c:if test="${showTheDetail}">
                    <kul:htmlControlAttribute
                        property="budgetConstructionHeader.account.subFundGroup.subFundGroupDescription"
                        attributeEntry="${subFundGroupAttributes['subFundGroupDescription']}"
                        readOnly="true" />
				</c:if>&nbsp;</td>
			</tr>
			<tr>
				<th class="grid" align="right" colspan="2">Current Year Org:</th>
				<td class="grid" valign="center" rowspan="1" colspan="1">
				&nbsp;</td>
				<td class="grid" valign="center" rowspan="1" colspan="1"><c:if
					test="${showTheDetail}">
					<kul:htmlControlAttribute
						property="budgetConstructionHeader.account.organizationCode"
						attributeEntry="${accountAttributes.organizationCode}"
						readOnly="true" readOnlyBody="true">
						<kul:inquiry
							boClassName="org.kuali.kfs.coa.businessobject.Organization"
							keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.account.chartOfAccountsCode}&amp;organizationCode=${KualiForm.budgetConstructionHeader.account.organizationCode}"
							render="${!empty KualiForm.budgetConstructionHeader.account.organizationCode}">
							<html:hidden write="true"
								property="budgetConstructionHeader.account.organizationCode" />
						</kul:inquiry>&nbsp;
	      	</kul:htmlControlAttribute>
				</c:if>&nbsp;</td>
				<td class="grid" valign="center" rowspan="1" colspan="3"><c:if
					test="${showTheDetail}">
					<kul:htmlControlAttribute
						property="${orgPropString}.organizationName"
						attributeEntry="${orgAttributes.organizationName}" 
						readOnly="true" />
				</c:if>&nbsp;</td>
			</tr>
			<tr>
				<th class="grid" align="right" colspan="2">Rpts To:</th>
				<td class="grid" valign="center" rowspan="1" colspan="1"><c:if
					test="${showTheDetail}">
					<kul:htmlControlAttribute
						property="${orgPropString}.reportsToChartOfAccountsCode"
						attributeEntry="${orgAttributes.reportsToChartOfAccountsCode}"
						readOnly="true" readOnlyBody="true">
						<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart"
							keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.account.organization.reportsToChartOfAccountsCode}"
							render="${!empty KualiForm.budgetConstructionHeader.account.organization.reportsToChartOfAccountsCode}">
							<html:hidden write="true"
								property="${orgPropString}.reportsToChartOfAccountsCode" />
						</kul:inquiry>&nbsp;
	      	</kul:htmlControlAttribute>
				</c:if>&nbsp;</td>
				<td class="grid" valign="center" rowspan="1" colspan="1"><c:if
					test="${showTheDetail}">
					<kul:htmlControlAttribute
						property="${orgPropString}.reportsToOrganizationCode"
						attributeEntry="${orgAttributes.reportsToOrganizationCode}"
						readOnly="true" readOnlyBody="true">
						<kul:inquiry
							boClassName="org.kuali.kfs.coa.businessobject.Organization"
							keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.account.organization.reportsToChartOfAccountsCode}&amp;organizationCode=${KualiForm.budgetConstructionHeader.account.organization.reportsToOrganizationCode}"
							render="${!empty KualiForm.budgetConstructionHeader.account.organization.reportsToOrganizationCode}">
							<html:hidden write="true"
								property="${orgPropString}.reportsToOrganizationCode" />
						</kul:inquiry>&nbsp;
	      	</kul:htmlControlAttribute>
				</c:if>&nbsp;</td>
				<td class="grid" valign="center" rowspan="1" colspan="3"><c:if
					test="${showTheDetail}">
 					<kul:htmlControlAttribute
						property="${orgPropString}.reportsToOrganizationCode"
						attributeEntry="${orgAttributes.reportsToChartOfAccountsCode}"
						readOnly="true" />
				</c:if>&nbsp;</td>
			</tr>

			<c:if test="${!KualiForm.accountReportsExist}">
				<tr>
					<th class="grid" align="right" colspan="2">Next Year Org:</th>
					<td class="grid" valign="center" rowspan="1" colspan="5"><c:if
						test="${!empty KualiForm.budgetConstructionHeader.chartOfAccountsCode && !empty KualiForm.budgetConstructionHeader.accountNumber}">
                    No Account Reports To mapping found!
              </c:if>&nbsp;</td>
				</tr>
			</c:if>
			<c:if test="${KualiForm.accountReportsExist}">
				<tr>
					<th class="grid" align="right" colspan="2">Next Year Org:</th>
					<td class="grid" valign="center" rowspan="1" colspan="1"><c:if
						test="${showTheDetail}">
						<kul:htmlControlAttribute
							property="${accountRptsPropString}.reportsToChartOfAccountsCode"
							attributeEntry="${accountRptsAttributes.reportsToChartOfAccountsCode}"
							readOnly="true" readOnlyBody="true">
							<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart"
								keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.reportsToChartOfAccountsCode}"
								render="${!empty KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.reportsToChartOfAccountsCode}">
								<html:hidden write="true"
									property="${accountRptsPropString}.reportsToChartOfAccountsCode" />
							</kul:inquiry>&nbsp;
            </kul:htmlControlAttribute>
					</c:if>&nbsp;</td>
					<td class="grid" valign="center" rowspan="1" colspan="1"><c:if
						test="${showTheDetail}">
						<kul:htmlControlAttribute
							property="${accountRptsPropString}.reportsToOrganizationCode"
							attributeEntry="${accountRptsAttributes.reportsToOrganizationCode}"
							readOnly="true" readOnlyBody="true">
							<kul:inquiry
								boClassName="org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports"
								keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.reportsToChartOfAccountsCode}&amp;organizationCode=${KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.reportsToOrganizationCode}"
								render="${!empty KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.reportsToOrganizationCode}">
								<html:hidden write="true"
									property="${accountRptsPropString}.reportsToOrganizationCode" />
							</kul:inquiry>&nbsp;
            </kul:htmlControlAttribute>
					</c:if>&nbsp;</td>
					<td class="grid" valign="center" rowspan="1" colspan="3"><c:if
						test="${showTheDetail}">
						<kul:htmlControlAttribute
							property="${accountRptsPropString}.budgetConstructionOrganizationReports.organization.organizationName"
							attributeEntry="${orgAttributes.organizationName}"
							readOnly="true" /></td>
			</c:if>
			&nbsp;
			</tr>
			<tr>
				<th class="grid" align="right" colspan="2">Rpts To:</th>
				<td class="grid" valign="center" rowspan="1" colspan="1"><c:if
					test="${showTheDetail}">
					<kul:htmlControlAttribute
						property="${orgRptsPropString}.reportsToChartOfAccountsCode"
						attributeEntry="${orgRptsAttributes.reportsToChartOfAccountsCode}"
						readOnly="true" readOnlyBody="true">
						<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart"
							keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToChartOfAccountsCode}"
							render="${!empty KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToChartOfAccountsCode}">
							<html:hidden write="true"
								property="${orgRptsPropString}.reportsToChartOfAccountsCode" />
						</kul:inquiry>&nbsp;
            		</kul:htmlControlAttribute>
				</c:if>&nbsp;</td>
				<td class="grid" valign="center" rowspan="1" colspan="1"><c:if
					test="${showTheDetail}">
					<kul:htmlControlAttribute
						property="${orgRptsPropString}.reportsToOrganizationCode"
						attributeEntry="${orgRptsAttributes.reportsToOrganizationCode}"
						readOnly="true" readOnlyBody="true">
						<kul:inquiry
							boClassName="org.kuali.kfs.coa.businessobject.Organization"
							keyValues="chartOfAccountsCode=${KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToChartOfAccountsCode}&amp;organizationCode=${KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToOrganizationCode}"
							render="${!empty KualiForm.budgetConstructionHeader.budgetConstructionAccountReports.budgetConstructionOrganizationReports.reportsToOrganizationCode}">
							<html:hidden write="true"
								property="${orgRptsPropString}.reportsToOrganizationCode" />
						</kul:inquiry>&nbsp;
	      	</kul:htmlControlAttribute>
				</c:if>&nbsp;</td>
				<td class="grid" valign="center" rowspan="1" colspan="3"><c:if
					test="${showTheDetail}">
 					<kul:htmlControlAttribute
						property="${orgRptsPropString}.reportsToOrganization.organizationName"
						attributeEntry="${orgAttributes.organizationName}"
						readOnly="${true}" />
						</td>
				</c:if>
				&nbsp;
			</tr>
			</c:if>

			<tr>
				<th class="grid" colspan="7" align="left"><br>
				Budget Construction Organization Salary Setting/Report/Control <br>
				<br>
				</th>
			</tr>
			<tr>
				<td class="grid" colspan="4">
				<div align="center"><c:if
					test="${!KualiForm.salarySettingDisabled}">
					<html:image
						property="methodToCall.performOrgSalarySetting.anchororgControlsAnchor"
						src="${ConfigProperties.externalizable.images.url}tinybutton-orgsalsettings.gif"
						title="Organization Salary Setting"
						alt="Organization Salary Setting" styleClass="tinybutton" />&nbsp;&nbsp;&nbsp;
              </c:if> <html:image
					property="methodToCall.performReportDump.anchororgControlsAnchor"
					src="${ConfigProperties.externalizable.images.url}tinybutton-orgrepdump.gif"
					title="Organization Report Export" alt="Organization Report/Dump"
					styleClass="tinybutton" />&nbsp;&nbsp;&nbsp; <html:image
					property="methodToCall.performRequestImport.anchororgControlsAnchor"
					src="${ConfigProperties.externalizable.images.url}tinybutton-requestimport.gif"
					title="Organization Request Import"
					alt="Organization Request Import" styleClass="tinybutton" />&nbsp;&nbsp;&nbsp;
				<html:image
					property="methodToCall.performLockMonitor.anchororgControlsAnchor"
					src="${ConfigProperties.externalizable.images.url}tinybutton-lockmonitor.gif"
					title="Lock Monitor" alt="Lock Monitor" styleClass="tinybutton" />&nbsp;&nbsp;&nbsp;
				<c:if test="${KualiForm.canPerformPayrateImportExport}">
					<c:if test="${!KualiForm.salarySettingDisabled}">
						<html:image
							property="methodToCall.performPayrateImportExport.anchororgControlsAnchor"
							src="${ConfigProperties.externalizable.images.url}tinybutton-payrateimpexp.gif"
							title="Payrate Import/Export" alt="Payrate Import/Export"
							styleClass="tinybutton" />
					</c:if>
				</c:if></div>
				</td>
				<td class="grid" colspan="3">
				<div align="center"><html:image
					property="methodToCall.performOrgPullup.anchororgControlsAnchor"
					src="${ConfigProperties.externalizable.images.url}tinybutton-orgpullup.gif"
					title="Organization Pull Up" alt="Organization Pull Up"
					styleClass="tinybutton" />&nbsp;&nbsp;&nbsp; <html:image
					property="methodToCall.performOrgPushdown.anchororgControlsAnchor"
					src="${ConfigProperties.externalizable.images.url}tinybutton-orgpushdown.gif"
					title="Organization Push Down" alt="Organization Push Down"
					styleClass="tinybutton" /></div>
				</td>
			</tr>
		</table>
	</c:if>

	<div id="globalbuttons" class="globalbuttons"><html:image
		src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif"
		styleClass="globalbuttons" property="methodToCall.returnToCaller"
		title="close" alt="close" /></div>

	<%-- Need these here to override and initialize vars used by objectinfo.js to BC specific --%>
	<SCRIPT type="text/javascript">
  var kualiForm = document.forms['KualiForm'];
  var kualiElements = kualiForm.elements;
</SCRIPT>
</kul:page>
