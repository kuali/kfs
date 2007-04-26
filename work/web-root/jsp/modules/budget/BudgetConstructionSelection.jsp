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
<%@ include file="/jsp/core/tldHeader.jsp"%>

<c:if test="${!accountingLineScriptsLoaded}">
	<script type='text/javascript' src="dwr/interface/ChartService.js"></script>
	<script type='text/javascript' src="dwr/interface/AccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/SubAccountService.js"></script>
	<script language="JavaScript" type="text/javascript" src="scripts/kfs/objectInfo.js"></script>
	<c:set var="accountingLineScriptsLoaded" value="true" scope="page" />
</c:if>

<c:set var="bcHeaderAttributes"
	value="${DataDictionary['BudgetConstructionHeader'].attributes}" />

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetBudgetConstructionSelection" renderMultipart="true"
	docTitle="Budget Construction Selection"
    transactionalDocument="false"
	>

<%--	<kul:hiddenDocumentFields /> --%>

	<kul:errors errorTitle="Errors found in Search Criteria:" />
	<kul:messages/>

    <table align="center" cellpadding="0" cellspacing="0" class="datatable-100">
    	<tr>
            <th class="grid" colspan="6" align="left">
                Budget Construction Document Open
                <br><br>
		    </th>
		</tr>
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
            <th class="grid" colspan="4">
			    &nbsp;
            </th>
	    </tr>
	    <tr>
            <th class="grid" colspan="2" rowspan="2">&nbsp;</th>
		    <th class="grid" align="center" colspan="1">
                <html:hidden property="budgetConstructionHeader.universityFiscalYear"/>
                <html:hidden property="budgetConstructionHeader.documentNumber"/>
			    <kul:htmlAttributeLabel
			        attributeEntry="${bcHeaderAttributes.chartOfAccountsCode}"
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
		    <th class="grid" align="center" colspan="1">
			    Action
		    </th>
	    </tr>
	    <tr>
            <bc:pbglLineDataCell dataCellCssClass="grid"
                accountingLine="budgetConstructionHeader"
                field="chartOfAccountsCode" detailFunction="loadChartInfo"
                detailField="chartOfAccounts.finChartOfAccountDescription"
                attributes="${bcHeaderAttributes}" inquiry="true"
                boClassSimpleName="Chart"
                readOnly="false"
                displayHidden="false"
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
                lookupOrInquiryKeys="chartOfAccountsCode,accountNumber"
                accountingLineValuesMap="${KualiForm.budgetConstructionHeader.valuesMap}"
                anchor="budgetConstructionHeaderSubAccountAnchor" />
            <td class="grid" nowrap>
            <div align="center">
                <html:image property="methodToCall.performBCDocumentOpen.anchorbudgetConstructionHeaderAnchor" src="images/tinybutton-loaddoc.gif" title="Load Budget Construction Document" alt="Load Budget Construction Document" styleClass="tinybutton" />
            </div>
            </td>
	    </tr>
	</table>

    <div id="globalbuttons" class="globalbuttons">
        <html:image src="images/buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.returnToCaller" title="close" alt="close"/>
    </div>

<%-- Need these here to override and initialize vars used by objectinfo.js to BC specific --%>
<SCRIPT type="text/javascript">
  var kualiForm = document.forms['KualiForm'];
  var kualiElements = kualiForm.elements;
</SCRIPT>
</kul:page>
