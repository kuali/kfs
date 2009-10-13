<%--
 Copyright 2007-2009 The Kuali Foundation
 
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

<%@ attribute name="readOnly" required="true" %>

<c:set var="monthlyBudgetAttributes" value="${DataDictionary['BudgetConstructionMonthly'].attributes}" />
<c:set var="monthlyBudget" value="${KualiForm.budgetConstructionMonthly}" />
<c:set var="pbglAttributes" value="${DataDictionary['PendingBudgetConstructionGeneralLedger'].attributes}" />
<c:set var="pbgl" value="${KualiForm.budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger}" />

<kul:tabTop tabTitle="Monthly Budget Construction" defaultOpen="true" tabErrorKey="${BCConstants.BUDGET_CONSTRUCTION_MONTHLY_BUDGET_ERRORS}">
<div class="tab-container" align=center>
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">

      <bc:subheadingWithDetailToggleRow
        columnCount="9"
        subheading="Monthly Amounts"/>

      <tr>
          <kul:htmlAttributeHeaderCell align="left" attributeEntry="${pbglAttributes.universityFiscalYear}" >
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
          
          <bc:pbglLineDataCell dataCellCssClass="datacell"
              accountingLine="budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger"
              field="subAccountNumber"
              detailField="subAccount.subAccountName"
              attributes="${pbglAttributes}" inquiry="${pbgl.subAccountNumber ne '-----'}"
              boClassSimpleName="SubAccount"
              readOnly="true"
              displayHidden="false"
              lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,accountNumber"
              accountingLineValuesMap="${pbgl.valuesMap}" />
          
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

          <bc:pbglLineDataCell dataCellCssClass="datacell"
              accountingLine="budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger"
              field="financialSubObjectCode"
              detailField="financialSubObject.financialSubObjectCdshortNm"
              attributes="${pbglAttributes}" inquiry="${pbgl.financialSubObjectCode ne '---'}"
              boClassSimpleName="SubObjCd"
              readOnly="true"
              displayHidden="false"
              lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,financialObjectCode,accountNumber"
              accountingLineValuesMap="${pbgl.valuesMap}" />

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
                readOnly="${readOnly}"
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
                readOnly="${readOnly}"
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
                readOnly="${readOnly}"
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
                readOnly="${readOnly}"
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
                readOnly="${readOnly}"
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
                readOnly="${readOnly}"
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
                readOnly="${readOnly}"
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
                readOnly="${readOnly}"
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
                readOnly="${readOnly}"
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
                readOnly="${readOnly}"
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
                readOnly="${readOnly}"
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
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>

      </c:if>
        <tr>
            <td colspan="2" class="datacell" nowrap>
              <div align="center"><span>
              <c:if test="${!readOnly}">
                <c:if test="${KualiForm.budgetableDocument}">
                  <html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_monthspread.gif" styleClass="tinybutton" property="methodToCall.performMonthlySpread.anchormonthlyBudgetLineLineAnchor" title="Spread Evenly To Months" alt="Spread Evenly To Months"/>
                  <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_clear.gif" styleClass="tinybutton" property="methodToCall.performMonthlyZero.anchormonthlyBudgetLineLineAnchor" title="Set Months To Zero" alt="Set Months To Zero"/>
                </c:if>
                <c:if test="${KualiForm.monthlyPersisted}">
                  <html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_monthdel.gif" styleClass="tinybutton" property="methodToCall.performMonthlyDelete.anchormonthlyBudgetLineLineAnchor" title="Delete Monthly" alt="Delete Monthly"/>
                </c:if>
              </c:if>
              &nbsp;
              </div>
            </td>
        </tr>
    </table>
</div>
</kul:tabTop>
