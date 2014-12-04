<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="readOnly" required="false" description="determine whether the contents can be read only or not"%>

<c:set var="sseAttributes" value="${DataDictionary['SalarySettingExpansion'].attributes}" />
<c:set var="accountAttributes" value="${DataDictionary['Account'].attributes}" />

<c:set var="accountingLine" value="salarySettingExpansion" />
<c:set var="colSpan" value="7" />

<table cellpadding="0" cellspacing="0" class="datatable" summary="Expenditure Salary Line">
<tbody>
	<tr>
		<td colspan="${colSpan}" class="subhead">
			<span class="subhead-left">Expenditure Salary Line</span>
		</td>
	</tr>

	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${sseAttributes.chartOfAccountsCode}" hideRequiredAsterisk="true"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${sseAttributes.accountNumber}" hideRequiredAsterisk="true"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${sseAttributes.subAccountNumber}" hideRequiredAsterisk="true"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${sseAttributes.financialObjectCode}" hideRequiredAsterisk="true"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${sseAttributes.financialSubObjectCode}" hideRequiredAsterisk="true"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${accountAttributes.subFundGroupCode}" hideRequiredAsterisk="true"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${accountAttributes.organizationCode}" hideRequiredAsterisk="true"/>
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
			accountingLineValuesMap="${KualiForm.salarySettingExpansion.valuesMap}" />


		<%-- Account Number and Name --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${accountingLine}" 
			field="accountNumber" 
			detailFunction="loadAccountInfo" 
			detailField="account.accountName" 
			attributes="${sseAttributes}" lookup="false" inquiry="true" 
			boClassSimpleName="Account" readOnly="true" displayHidden="false" colSpan="1" 
			lookupOrInquiryKeys="chartOfAccountsCode" 
			accountingLineValuesMap="${KualiForm.salarySettingExpansion.valuesMap}" />

		<%-- Sub-Account Number and Name --%>
		<c:set var="doLookupOrInquiry" value="${KualiForm.salarySettingExpansion.subAccountNumber ne KualiForm.dashSubAccountNumber ? true : false}" />
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${accountingLine}" 
			field="subAccountNumber" 
			detailFunction="loadSubAccountInfo" detailField="subAccount.subAccountName" 
			attributes="${sseAttributes}" lookup="${doLookupOrInquiry}" inquiry="${doLookupOrInquiry}" 
			boClassSimpleName="SubAccount" readOnly="true" displayHidden="false" colSpan="1" 
			lookupOrInquiryKeys="chartOfAccountsCode,accountNumber"
			accountingLineValuesMap="${KualiForm.salarySettingExpansion.valuesMap}" />

		<%-- Object Code and Name --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${accountingLine}" field="financialObjectCode" 
			detailFunction="loadObjectInfo" detailField="financialObject.financialObjectCodeName" 
			attributes="${sseAttributes}" lookup="false" inquiry="true" 
			boClassSimpleName="ObjectCode" readOnly="true" displayHidden="false" colSpan="1" 
			lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode"
			accountingLineValuesMap="${KualiForm.salarySettingExpansion.valuesMap}" />

		<%-- Sub-Object Code and Name --%>
		<c:set var="doLookupOrInquiry" value="${KualiForm.salarySettingExpansion.financialSubObjectCode ne KualiForm.dashFinancialSubObjectCode ? true : false}" />
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${accountingLine}" field="financialSubObjectCode" 
			detailFunction="loadSubObjectInfo" detailField="financialSubObject.financialSubObjectCodeName"
			attributes="${sseAttributes}" lookup="${doLookupOrInquiry}" inquiry="${doLookupOrInquiry}" 
			boClassSimpleName="SubObjCd" readOnly="true" displayHidden="false" colSpan="1"
			lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,accountNumber,financialObjectCode" 
			accountingLineValuesMap="${KualiForm.salarySettingExpansion.valuesMap}" />

		<%-- Sub-Fund Group Code  --%>
		<td align="center" valign="middle">
			<kul:htmlControlAttribute property="salarySettingExpansion.account.subFundGroupCode" 
				attributeEntry="${accountAttributes.subFundGroupCode}" readOnly="true" readOnlyBody="true">
				
				<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.SubFundGroup" 
					keyValues="subFundGroupCode=${KualiForm.salarySettingExpansion.account.subFundGroupCode}" render="true">
				</kul:inquiry>
			</kul:htmlControlAttribute>

			<bc:pbglLineDataCellDetail accountingLine="${accountingLine}" detailFields="account.subFundGroup.subFundGroupDescription" />
		</td>

		<%-- organization Code  --%>
		<td align="center" valign="middle">
			<kul:htmlControlAttribute property="salarySettingExpansion.account.organizationCode" 
				attributeEntry="${accountAttributes.organizationCode}" readOnly="true" readOnlyBody="true">
				
				<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Organization"
					keyValues="chartOfAccountsCode=${KualiForm.salarySettingExpansion.account.chartOfAccountsCode}&amp;organizationCode=${KualiForm.salarySettingExpansion.account.organizationCode}" render="true">
				</kul:inquiry>
			</kul:htmlControlAttribute>

			<bc:pbglLineDataCellDetail accountingLine="${accountingLine}" detailFields="account.organization.organizationName" />
		</td>
	</tr>

	<%-- Row for Add Position and Add Incumbent Buttons --%>
	<c:if test="${not readOnly}">
	<tr>
		<td class="infoline" colspan="${colSpan}"><center>			
			<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-addposition.gif" 
				property="methodToCall.addPosition" title="Add Position" 
				alt="Add Position" styleClass="tinybutton" />
				
			&nbsp;&nbsp;&nbsp;	   			
	   		<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-addincumbent.gif" 
	   			property="methodToCall.addIncumbent" title="Add Incumbent" 
	   			alt="Add Incumbent" styleClass="tinybutton" />
	          
	   		</center>
		</td>
	</tr>	
	</c:if>
</tbody>
</table>
