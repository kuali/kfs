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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<c:set var="sseAttributes" value="${DataDictionary['SalarySettingExpansion'].attributes}" />
<c:set var="accountAttributes" value="${DataDictionary['Account'].attributes}" />

<c:set var="accountingLine" value="pendingBudgetConstructionGeneralLedger" />

<html:hidden property="returnAnchor" />
<html:hidden property="returnFormKey" />

<html:hidden property="universityFiscalYear" />
<html:hidden property="documentNumber" />
<html:hidden property="chartOfAccountsCode" />
<html:hidden property="accountNumber" />
<html:hidden property="subAccountNumber" />
<html:hidden property="financialObjectCode" />
<html:hidden property="financialSubObjectCode" />
<html:hidden property="financialBalanceTypeCode" />
<html:hidden property="financialObjectTypeCode" />

<html:hidden property="pendingBudgetConstructionGeneralLedger.documentNumber" />
<html:hidden property="pendingBudgetConstructionGeneralLedger.universityFiscalYear" />
<html:hidden property="pendingBudgetConstructionGeneralLedger.financialBalanceTypeCode" />
<html:hidden property="pendingBudgetConstructionGeneralLedger.financialObjectTypeCode" />

<div class="h2-container"><h2>Expenditure Salary Line</h2></div>
						
<table cellpadding="0" cellspacing="0" class="datatable" summary="Expenditure Salary Line">
<tbody>
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${sseAttributes.chartOfAccountsCode}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${sseAttributes.accountNumber}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${sseAttributes.subAccountNumber}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${sseAttributes.financialObjectCode}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${sseAttributes.financialSubObjectCode}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${accountAttributes.subFundGroupCode}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${accountAttributes.organizationCode}" />
	</tr>
	
	<tr>
		<%-- Chart of Accounts Code and Name --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${accountingLine}" 
			field="chartOfAccountsCode" 
			detailFunction="loadChartInfo" 
			detailField="chartOfAccounts.finChartOfAccountDescription"
			attributes="${sseAttributes}" lookup="false" inquiry="true" 
			boClassSimpleName="Chart" readOnly="true" displayHidden="false" colSpan="1" 
			lookupOrInquiryKeys="chartOfAccountsCode"
			accountingLineValuesMap="${KualiForm.pendingBudgetConstructionGeneralLedger.valuesMap}" />


		<%-- Account Number and Name --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${accountingLine}" 
			field="accountNumber" 
			detailFunction="loadAccountInfo" 
			detailField="account.accountName" 
			attributes="${sseAttributes}" lookup="false" inquiry="true" 
			boClassSimpleName="Account" readOnly="true" displayHidden="false" colSpan="1" 
			lookupOrInquiryKeys="chartOfAccountsCode" 
			accountingLineValuesMap="${KualiForm.pendingBudgetConstructionGeneralLedger.valuesMap}" />

		<%-- Sub-Account Number and Name --%>
		<c:set var="doLookupOrInquiry" value="${KualiForm.pendingBudgetConstructionGeneralLedger.subAccountNumber ne '-----' ? true : false}" />
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${accountingLine}" 
			field="subAccountNumber" 
			detailFunction="loadSubAccountInfo" detailField="subAccount.subAccountName" 
			attributes="${sseAttributes}" lookup="${doLookupOrInquiry}" inquiry="${doLookupOrInquiry}" 
			boClassSimpleName="SubAccount" readOnly="true" displayHidden="false" colSpan="1" 
			lookupOrInquiryKeys="chartOfAccountsCode,accountNumber"
			accountingLineValuesMap="${KualiForm.pendingBudgetConstructionGeneralLedger.valuesMap}" />

		<%-- Object Code and Name --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${accountingLine}" field="financialObjectCode" 
			detailFunction="loadObjectInfo" detailField="financialObject.financialObjectCodeName" 
			attributes="${sseAttributes}" lookup="false" inquiry="true" 
			boClassSimpleName="ObjectCode" readOnly="true" displayHidden="false" colSpan="1" 
			lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode"
			accountingLineValuesMap="${KualiForm.pendingBudgetConstructionGeneralLedger.valuesMap}" />

		<%-- Sub-Object Code and Name --%>
		<c:set var="doLookupOrInquiry" value="${KualiForm.pendingBudgetConstructionGeneralLedger.financialSubObjectCode ne '---' ? true : false}" />
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${accountingLine}" field="financialSubObjectCode" 
			detailFunction="loadSubObjectInfo" detailField="financialSubObject.financialSubObjectCodeName"
			attributes="${sseAttributes}" lookup="${doLookupOrInquiry}" inquiry="${doLookupOrInquiry}" 
			boClassSimpleName="SubObjCd" readOnly="true" displayHidden="false" colSpan="1"
			lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,accountNumber,financialObjectCode" 
			accountingLineValuesMap="${KualiForm.pendingBudgetConstructionGeneralLedger.valuesMap}" />

		<%-- Sub-Fund Group Code  --%>
		<td align="center" valign="middle">
			<kul:htmlControlAttribute property="pendingBudgetConstructionGeneralLedger.account.subFundGroupCode" 
				attributeEntry="${accountAttributes.subFundGroupCode}" readOnly="true" readOnlyBody="true">
				
				<kul:inquiry boClassName="org.kuali.module.chart.bo.SubFundGroup" 
					keyValues="subFundGroupCode=${KualiForm.pendingBudgetConstructionGeneralLedger.account.subFundGroupCode}" render="true">
					<html:hidden write="true" property="pendingBudgetConstructionGeneralLedger.account.subFundGroupCode" />
				</kul:inquiry>
			</kul:htmlControlAttribute>

			<bc:pbglLineDataCellDetail accountingLine="${accountingLine}" detailFields="account.subFundGroup.subFundGroupDescription" />
		</td>

		<%-- organization Code  --%>
		<td align="center" valign="middle">
			<kul:htmlControlAttribute property="pendingBudgetConstructionGeneralLedger.account.organizationCode" 
				attributeEntry="${accountAttributes.organizationCode}" readOnly="true" readOnlyBody="true">
				
				<kul:inquiry boClassName="org.kuali.module.chart.bo.Org"
					keyValues="chartOfAccountsCode=${KualiForm.pendingBudgetConstructionGeneralLedger.account.chartOfAccountsCode}&amp;organizationCode=${KualiForm.pendingBudgetConstructionGeneralLedger.account.organizationCode}" render="true">
					<html:hidden write="true" property="pendingBudgetConstructionGeneralLedger.account.organizationCode" />
				</kul:inquiry>
			</kul:htmlControlAttribute>

			<bc:pbglLineDataCellDetail accountingLine="${accountingLine}" detailFields="account.organization.organizationName" />
		</td>
	</tr>

	<%-- Row for Add Position and Add Incumbent Buttons --%>
	<tr>
		<td class="grid" colspan="7">
			<c:if test="${!KualiForm.editingMode['systemViewOnly'] && KualiForm.editingMode['fullEntry']}">
				<html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_addposn.gif" 
					property="methodToCall.performAddPosition" title="Add Position" 
					alt="Add Position" styleClass="tinybutton" />&nbsp;&nbsp;&nbsp;
     			
     			<html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_addincmbnt.gif" 
     				property="methodToCall.performAddIncumbent" title="Add Incumbent" 
     				alt="Add Incumbent" styleClass="tinybutton" />
			</c:if>
		</td>
	</tr>
</tbody>
</table>