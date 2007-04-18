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
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib tagdir="/WEB-INF/tags/bc" prefix="bc"%>

<c:set var="monthlyBudgetAttributes" value="${DataDictionary['BudgetConstructionMonthly'].attributes}" />
<c:set var="monthlyBudget" value="${KualiForm.budgetConstructionMonthly}" />
<c:set var="pbglAttributes" value="${DataDictionary['PendingBudgetConstructionGeneralLedger'].attributes}" />
<c:set var="pbgl" value="${KualiForm.budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger}" />
<c:set var="readOnly" value="${KualiForm.editingMode['systemViewOnly'] || !KualiForm.editingMode['fullEntry']}" />

<kul:tabTop tabTitle="Monthly Budget Construction" defaultOpen="true" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_MONTHLY_BUDGET_ERRORS}">
<div class="tab-container" align=center>
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">

      <bc:subheadingWithDetailToggleRow
        columnCount="9"
        subheading="Monthly Amounts"/>

      <tr>
          <kul:htmlAttributeHeaderCell align="left" attributeEntry="${pbglAttributes.universityFiscalYear}" >
              <html:hidden property="returnAnchor" />
              <html:hidden property="returnFormKey" />
              <html:hidden property="documentNumber" />
              <html:hidden property="universityFiscalYear" />
              <html:hidden property="chartOfAccountsCode" />
              <html:hidden property="accountNumber" />
              <html:hidden property="subAccountNumber" />
              <html:hidden property="financialObjectCode" />
              <html:hidden property="financialSubObjectCode" />
              <html:hidden property="financialBalanceTypeCode" />
              <html:hidden property="financialObjectTypeCode" />
          </kul:htmlAttributeHeaderCell>
          <kul:htmlAttributeHeaderCell attributeEntry="${pbglAttributes.chartOfAccountsCode}" />
          <kul:htmlAttributeHeaderCell attributeEntry="${pbglAttributes.accountNumber}" />
          <kul:htmlAttributeHeaderCell attributeEntry="${pbglAttributes.subAccountNumber}" />
          <kul:htmlAttributeHeaderCell attributeEntry="${pbglAttributes.financialObjectCode}" />
          <kul:htmlAttributeHeaderCell attributeEntry="${pbglAttributes.financialSubObjectCode}" />
          <kul:htmlAttributeHeaderCell attributeEntry="${pbglAttributes.accountLineAnnualBalanceAmount}" />
      </tr>

      <tr>
          <bc:pbglLineDataCell dataCellCssClass="datacell"
              accountingLine="budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger"
              cellProperty="budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger.universityFiscalYear"
              field="universityFiscalYear"
              attributes="${pbglAttributes}"
              readOnly="true"
              displayHidden="false" />

          <bc:pbglLineDataCell dataCellCssClass="datacell"
              accountingLine="budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger"
              field="chartOfAccountsCode"
              detailField="chartOfAccounts.finChartOfAccountDescription"
              attributes="${pbglAttributes}" inquiry="true"
              boClassSimpleName="Chart"
              readOnly="true"
              displayHidden="$false"
              accountingLineValuesMap="${KualiForm.budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger.valuesMap}" />

          <bc:pbglLineDataCell dataCellCssClass="datacell"
              accountingLine="budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger"
              field="accountNumber"
              detailField="account.accountName"
              attributes="${pbglAttributes}" inquiry="true"
              boClassSimpleName="Account"
              readOnly="true"
              displayHidden="false"
              lookupOrInquiryKeys="chartOfAccountsCode"
              accountingLineValuesMap="${KualiForm.budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger.valuesMap}" />
          
          <c:set var="doAccountLookupOrInquiry" value="false"/>
          <c:if test="${pbgl.subAccountNumber ne Constants.DASHES_SUB_ACCOUNT_NUMBER}">
              <c:set var="doAccountLookupOrInquiry" value="true"/>
          </c:if>

          <bc:pbglLineDataCell dataCellCssClass="datacell"
              accountingLine="budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger"
              field="subAccountNumber"
              detailField="subAccount.subAccountName"
              attributes="${pbglAttributes}" inquiry="${doAccountLookupOrInquiry}"
              boClassSimpleName="SubAccount"
              readOnly="true"
              displayHidden="false"
              lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode"
              accountingLineValuesMap="${KualiForm.budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger.valuesMap}" />
          
          <bc:pbglLineDataCell dataCellCssClass="datacell"
              accountingLine="budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger"
              field="financialObjectCode"
              detailField="financialObject.financialObjectCodeShortName"
              attributes="${pbglAttributes}" inquiry="true"
              boClassSimpleName="ObjectCode"
              readOnly="true"
              displayHidden="false"
              lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode"
              accountingLineValuesMap="${KualiForm.budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger.valuesMap}" />

          <c:set var="doSubObjectLookupOrInquiry" value="false"/>
          <c:if test="${pbgl.financialSubObjectCode ne Constants.DASHES_SUB_OBJECT_CODE}">
              <c:set var="doSubObjectLookupOrInquiry" value="true"/>
          </c:if>

          <bc:pbglLineDataCell dataCellCssClass="datacell"
              accountingLine="budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger"
              field="financialSubObjectCode"
              detailField="financialSubObject.financialSubObjectCdshortNm"
              attributes="${pbglAttributes}" inquiry="${doSubObjectLookupOrInquiry}"
              boClassSimpleName="SubObjCd"
              readOnly="true"
              displayHidden="false"
              lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,financialObjectCode,accountNumber"
              accountingLineValuesMap="${KualiForm.budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger.valuesMap}" />

          <bc:pbglLineDataCell dataCellCssClass="datacell"
              accountingLine="budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger"
              cellProperty="budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger.accountLineAnnualBalanceAmount"
              attributes="${pbglAttributes}"
              field="accountLineAnnualBalanceAmount"
              fieldAlign="right"
              readOnly="true"
              rowSpan="1" dataFieldCssClass="amount" />
      </tr>
    </table>
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">

      <tr>
          <kul:htmlAttributeHeaderCell colspan="2" />
          
      </tr>
      
      <tr>
          <kul:htmlAttributeHeaderCell align="right" literalLabel="Period" scope="col">
              <%-- these hidden fields are inside a table cell to keep the HTML valid --%>
              <html:hidden property="budgetConstructionMonthly.documentNumber" />
              <html:hidden property="budgetConstructionMonthly.universityFiscalYear" />
              <html:hidden property="budgetConstructionMonthly.chartOfAccountsCode" />
              <html:hidden property="budgetConstructionMonthly.accountNumber" />
              <html:hidden property="budgetConstructionMonthly.subAccountNumber" />
              <html:hidden property="budgetConstructionMonthly.financialObjectCode" />
              <html:hidden property="budgetConstructionMonthly.financialSubObjectCode" />
              <html:hidden property="budgetConstructionMonthly.financialBalanceTypeCode" />
              <html:hidden property="budgetConstructionMonthly.financialObjectTypeCode" />
              <html:hidden property="budgetConstructionMonthly.versionNumber" />
          </kul:htmlAttributeHeaderCell>
          <kul:htmlAttributeHeaderCell align="left" literalLabel="Amount" scope="col" />
      </tr>

      <c:if test="${monthlyBudget != null}">
        <tr>
			<kul:htmlAttributeHeaderCell align="right" attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth1LineAmount}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionMonthly"
                cellProperty="budgetConstructionMonthly.financialDocumentMonth1LineAmount"
                attributes="${monthlyBudgetAttributes}"
                field="financialDocumentMonth1LineAmount"
                fieldAlign="left"
                readOnly="false"
                rowSpan="1" dataFieldCssClass="amount"
                anchor="monthlyBudgetLineLineAnchor" />
        </tr>
        <tr>
			<kul:htmlAttributeHeaderCell align="right" attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth2LineAmount}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionMonthly"
                cellProperty="budgetConstructionMonthly.financialDocumentMonth2LineAmount"
                attributes="${monthlyBudgetAttributes}"
                field="financialDocumentMonth2LineAmount"
                fieldAlign="left"
                readOnly="false"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>
        <tr>
			<kul:htmlAttributeHeaderCell align="right" attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth3LineAmount}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionMonthly"
                cellProperty="budgetConstructionMonthly.financialDocumentMonth3LineAmount"
                attributes="${monthlyBudgetAttributes}"
                field="financialDocumentMonth3LineAmount"
                fieldAlign="left"
                readOnly="false"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>
        <tr>
			<kul:htmlAttributeHeaderCell align="right" attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth4LineAmount}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionMonthly"
                cellProperty="budgetConstructionMonthly.financialDocumentMonth4LineAmount"
                attributes="${monthlyBudgetAttributes}"
                field="financialDocumentMonth4LineAmount"
                fieldAlign="left"
                readOnly="false"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>
        <tr>
			<kul:htmlAttributeHeaderCell align="right" attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth5LineAmount}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionMonthly"
                cellProperty="budgetConstructionMonthly.financialDocumentMonth5LineAmount"
                attributes="${monthlyBudgetAttributes}"
                field="financialDocumentMonth5LineAmount"
                fieldAlign="left"
                readOnly="false"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>
        <tr>
			<kul:htmlAttributeHeaderCell align="right" attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth6LineAmount}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionMonthly"
                cellProperty="budgetConstructionMonthly.financialDocumentMonth6LineAmount"
                attributes="${monthlyBudgetAttributes}"
                field="financialDocumentMonth6LineAmount"
                fieldAlign="left"
                readOnly="false"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>
        <tr>
			<kul:htmlAttributeHeaderCell align="right" attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth7LineAmount}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionMonthly"
                cellProperty="budgetConstructionMonthly.financialDocumentMonth7LineAmount"
                attributes="${monthlyBudgetAttributes}"
                field="financialDocumentMonth7LineAmount"
                fieldAlign="left"
                readOnly="false"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>
        <tr>
			<kul:htmlAttributeHeaderCell align="right" attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth8LineAmount}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionMonthly"
                cellProperty="budgetConstructionMonthly.financialDocumentMonth8LineAmount"
                attributes="${monthlyBudgetAttributes}"
                field="financialDocumentMonth8LineAmount"
                fieldAlign="left"
                readOnly="false"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>
        <tr>
			<kul:htmlAttributeHeaderCell align="right" attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth9LineAmount}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionMonthly"
                cellProperty="budgetConstructionMonthly.financialDocumentMonth9LineAmount"
                attributes="${monthlyBudgetAttributes}"
                field="financialDocumentMonth9LineAmount"
                fieldAlign="left"
                readOnly="false"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>
        <tr>
			<kul:htmlAttributeHeaderCell align="right" attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth10LineAmount}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionMonthly"
                cellProperty="budgetConstructionMonthly.financialDocumentMonth10LineAmount"
                attributes="${monthlyBudgetAttributes}"
                field="financialDocumentMonth10LineAmount"
                fieldAlign="left"
                readOnly="false"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>
        <tr>
			<kul:htmlAttributeHeaderCell align="right" attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth11LineAmount}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionMonthly"
                cellProperty="budgetConstructionMonthly.financialDocumentMonth11LineAmount"
                attributes="${monthlyBudgetAttributes}"
                field="financialDocumentMonth11LineAmount"
                fieldAlign="left"
                readOnly="false"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>
        <tr>
			<kul:htmlAttributeHeaderCell align="right" attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth12LineAmount}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionMonthly"
                cellProperty="budgetConstructionMonthly.financialDocumentMonth12LineAmount"
                attributes="${monthlyBudgetAttributes}"
                field="financialDocumentMonth12LineAmount"
                fieldAlign="left"
                readOnly="false"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>

      </c:if>
        <tr>
            <td colspan="2" class="datacell" nowrap>
              <div align="center"><span>
              <c:if test="${!readOnly}">
                <html:image src="images/buttonsmall_monthspread.gif" styleClass="tinybutton" property="methodToCall.performMonthlySpread.anchormonthlyBudgetLineLineAnchor" title="Spread Evenly To Months" alt="Spread Evenly To Months"/>
                <html:image src="images/buttonsmall_clear.gif" styleClass="tinybutton" property="methodToCall.performMonthlyZero.anchormonthlyBudgetLineLineAnchor" title="Set Months To Zero" alt="Set Months To Zero"/>
                <html:image src="images/buttonsmall_monthdel.gif" styleClass="tinybutton" property="methodToCall.performMonthlyDelete.anchormonthlyBudgetLineLineAnchor" title="Delete Monthly" alt="Delete Monthly"/>
              </c:if>
              &nbsp;
              </div>
            </td>
        </tr>
    </table>
</div>
</kul:tabTop>