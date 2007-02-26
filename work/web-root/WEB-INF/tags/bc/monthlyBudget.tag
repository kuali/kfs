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

<c:set var="monthlyBudgetAttributes" value="${DataDictionary['BudgetConstructionMonthly'].attributes}" />
<c:set var="monthlyBudget" value="${KualiForm.budgetConstructionMonthly}" />
<c:set var="pbgl" value="${KualiForm.budgetConstructionMonthly.pendingBudgetConstructionGeneralLedger}" />

<kul:tabTop tabTitle="Monthly Budget Construction" defaultOpen="true" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_MONTHLY_BUDGET_ERRORS}">
<div class="tab-container" align=center>
		<table>
    <html:hidden property="returnAnchor" />
    <html:hidden property="returnFormKey" />
			<tr>
				<td>PBGL Key</td>
				<c:if test="${pbgl != null}">
				<td>${pbgl.universityFiscalYear}</td>
				<td>${pbgl.chartOfAccountsCode}</td>
				<td>${pbgl.accountNumber}</td>
				<td>${pbgl.subAccountNumber}</td>
				<td>${pbgl.financialObjectCode}</td>
				<td>${pbgl.financialSubObjectCode}</td>
				<td>${pbgl.accountLineAnnualBalanceAmount}</td>
				<td>
                <html:image src="images/tinybutton-recalculate.gif" styleClass="tinybutton" property="methodToCall.view" title="View" alt="View"/>
                </td>
				</c:if>
				
			</tr>
		</table>
		<table>

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

		    <tr><th>Period</th><th>Amount</th></tr>
				<c:if test="${monthlyBudget != null}">
				<tr><th>Month 1</th>
					<td valign=top nowrap><div align="right"><span>
						<kul:htmlControlAttribute attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth1LineAmount}" property="budgetConstructionMonthly.financialDocumentMonth1LineAmount" readOnly="true"/>
					</span></div></td>
				</tr>
				<tr><th>Month 2</th>
					<td valign=top nowrap><div align="right"><span>
						<kul:htmlControlAttribute attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth1LineAmount}" property="budgetConstructionMonthly.financialDocumentMonth2LineAmount" readOnly="true"/>
					</span></div></td>
				</tr>
				<tr><th>Month 3</th>
					<td valign=top nowrap><div align="right"><span>
						<kul:htmlControlAttribute attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth1LineAmount}" property="budgetConstructionMonthly.financialDocumentMonth3LineAmount" readOnly="true"/>
					</span></div></td>
				</tr>
				<tr><th>Month 4</th>
					<td valign=top nowrap><div align="right"><span>
						<kul:htmlControlAttribute attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth1LineAmount}" property="budgetConstructionMonthly.financialDocumentMonth4LineAmount" readOnly="true"/>
					</span></div></td>
				</tr>
				<tr><th>Month 5</th>
					<td valign=top nowrap><div align="right"><span>
						<kul:htmlControlAttribute attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth1LineAmount}" property="budgetConstructionMonthly.financialDocumentMonth5LineAmount" readOnly="true"/>
					</span></div></td>
				</tr>
				<tr><th>Month 6</th>
					<td valign=top nowrap><div align="right"><span>
						<kul:htmlControlAttribute attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth1LineAmount}" property="budgetConstructionMonthly.financialDocumentMonth6LineAmount" readOnly="true"/>
					</span></div></td>
				</tr>
				<tr><th>Month 7</th>
					<td valign=top nowrap><div align="right"><span>
						<kul:htmlControlAttribute attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth1LineAmount}" property="budgetConstructionMonthly.financialDocumentMonth7LineAmount" readOnly="true"/>
					</span></div></td>
				</tr>
				<tr><th>Month 8</th>
					<td valign=top nowrap><div align="right"><span>
						<kul:htmlControlAttribute attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth1LineAmount}" property="budgetConstructionMonthly.financialDocumentMonth8LineAmount" readOnly="true"/>
					</span></div></td>
				</tr>
				<tr><th>Month 9</th>
					<td valign=top nowrap><div align="right"><span>
						<kul:htmlControlAttribute attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth1LineAmount}" property="budgetConstructionMonthly.financialDocumentMonth9LineAmount" readOnly="true"/>
					</span></div></td>
				</tr>
				<tr><th>Month 10</th>
					<td valign=top nowrap><div align="right"><span>
						<kul:htmlControlAttribute attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth1LineAmount}" property="budgetConstructionMonthly.financialDocumentMonth10LineAmount" readOnly="true"/>
					</span></div></td>
				</tr>
				<tr><th>Month 11</th>
					<td valign=top nowrap><div align="right"><span>
						<kul:htmlControlAttribute attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth1LineAmount}" property="budgetConstructionMonthly.financialDocumentMonth11LineAmount" readOnly="true"/>
					</span></div></td>
				</tr>
				<tr><th>Month 12</th>
					<td valign=top nowrap><div align="right"><span>
						<kul:htmlControlAttribute attributeEntry="${monthlyBudgetAttributes.financialDocumentMonth1LineAmount}" property="budgetConstructionMonthly.financialDocumentMonth12LineAmount" readOnly="true"/>
					</span></div></td>
				</tr>
				</c:if>
				<tr>
					<td colspan="2" valign=top nowrap><div align="center"><span>
		                <html:image src="images/tinybutton-saveedits.gif" styleClass="tinybutton" property="methodToCall.returnToDocument" title="View" alt="View"/>
					</span></div></td>
				</tr>
		</table>
</div>
</kul:tabTop>