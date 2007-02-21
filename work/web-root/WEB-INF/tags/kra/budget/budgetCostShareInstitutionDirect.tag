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
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra"%>

<c:set var="institutionCostSharePersonnelAttributes" value="${DataDictionary.InstitutionCostSharePersonnel.attributes}" />
<c:set var="budgetInstitutionCostShareAttributes" value="${DataDictionary.BudgetInstitutionCostShare.attributes}" />
<c:set var="budgetPeriodInstitutionCostShareAttributes" value="${DataDictionary.BudgetPeriodInstitutionCostShare.attributes}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>
<c:set var="institutionDirectColumns" value="2"/>

<c:if test="${KualiForm.displayCostSharePermission}">
	<c:set var="institutionDirectColumns" value="3"/>
</c:if>
	
<c:set var="tabDescription">
	<c:choose>
		<c:when test="${KualiForm.document.budget.institutionCostShareIndicator}">
			balance: <fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.institutionDirect.totalBalanceToBeDistributed}" type="currency" currencySymbol="$" maxFractionDigits="0" />
		</c:when>
		<c:otherwise>
			None
		</c:otherwise>
	</c:choose>
</c:set>

<kul:tab tabTitle="Cost Share - Institution Direct" tabDescription="${tabDescription}" defaultOpen="true" transparentBackground="true" tabErrorKey="document.budget.institutionCostShareItem*" auditCluster="costShareAuditErrors" tabAuditKey="document.budget.audit.costShare.institution*">
	
	<c:if test="${!KualiForm.document.budget.institutionCostShareIndicator}">
		<div class="tab-container" align="center">
			<table class="datatable" cellpadding="0" cellspacing="0" >
				<tr>
					<td class="subhead" colspan="${KualiForm.document.periodListSize+7}">
						<span class="subhead-left">Institution Cost Share</span>
						<span class="subhead-right"> <span class="subhead"><kul:help businessObjectClassName="${DataDictionary.BudgetInstitutionCostShare.businessObjectClass}" altText="help"/></span> </span>
					</td>
				</tr>
				<tr>
					<td height="70" align=left valign=middle class="datacell">
						<div align="center">
							Not Included In This Budget
						</div>
					</td>
				</tr>
			</table>
		</div>
	</c:if>

	<c:if test="${KualiForm.document.budget.institutionCostShareIndicator}">
		<div class="tab-container" align=center>
			<table class="datatable" cellpadding="0" cellspacing="0" >
				<tr>
					<td class="subhead" colspan="${KualiForm.document.periodListSize+7}">
						<span class="subhead-left">Cost Share - Institution Direct</span>
						<span class="subhead-right"> <span class="subhead"><kul:help businessObjectClassName="${DataDictionary.BudgetInstitutionCostShare.businessObjectClass}" altText="help"/></span> </span>
					</td>
				</tr>
				<tr>
					<td colspan="${KualiForm.document.periodListSize+7}" class="tab-subhead">
						Overview
					</td>
				</tr>
				<tr>
					<th colspan="3" class="bord-l-b">
						&nbsp;
					</th>
					<!--- Iterate over the periods to create the columns -->
					<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="ctr">
						<th class="bord-l-b">
							${ctr+1}
						</th>
					</logic:iterate>
					<th class="bord-l-b">
						<strong>Total</strong>
					</th>
					<c:if test="${!viewOnly}">
					<th class="bord-l-b">
						Actions
					</th>
					</c:if>
				</tr>
				<tr>
					<th colspan="3" class="bord-l-b">
						<div align="right">
							Total Budgeted:
						</div>
					</th>
					<!--- Iterate over the periods to create the columns -->
					<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="ctr">
						<td class="datacell">
							<div align="right">
								<fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.institutionDirect.totalBudgeted[ctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
							</div>
						</td>
					</logic:iterate>
					<td class="datacell">
						<div align="right">
							<strong><fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.institutionDirect.totalTotalBudgeted}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong>
						</div>
					</td>
					<c:if test="${!viewOnly}">
					<td rowspan="${institutionDirectColumns}" class="datacell">
						<div align="center">
							<html:image property="methodToCall.recalculate.anchor${currentTabIndex}" src="./images/tinybutton-recalculate.gif" styleClass="tinybutton" alt="recalculate" />
						</div>
					</td>
					</c:if>
				</tr>
				<tr>
					<th colspan="3" class="bord-l-b">
						<div align="right">
							Amount Distributed:
						</div>
					</th>
					<!--- Iterate over the periods to create the columns -->
					<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="ctr">
						<td class="datacell">
							<div align="right">
								<fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.institutionDirect.amountDistributed[ctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
							</div>
						</td>
					</logic:iterate>
					<td class="datacell">
						<div align="right">
							<strong><fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.institutionDirect.totalAmountDistributed}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong>
						</div>
					</td>
				</tr>
				<tr>
					<th colspan="3" class="bord-l-b">
						<div align="right">
							Balance to be Distributed:
						</div>
					</th>
					<!--- Iterate over the periods to create the columns -->
					<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="ctr">
						<td class="datacell">
							<div align="right">
								<fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.institutionDirect.balanceToBeDistributed[ctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
							</div>
						</td>
					</logic:iterate>
					<td class="datacell">
						<div align="right">
							<strong><fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.institutionDirect.totalBalanceToBeDistributed}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong>
						</div>
					</td>
				</tr>

				<!---------------------- Imported from Personnel --------------------------------------------->
				<tr>
					<td colspan="${KualiForm.document.periodListSize+7}" class="tab-subhead">
						Imported from Personnel
					</td>
				</tr>
				<tr>
					<th colspan="3" class="bord-l-b">
						&nbsp;
					</th>
					<!-- Iterate over the periods to create the columns -->
					<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="ctr">
						<th class="bord-l-b">
							${ctr+1}
						</th>
					</logic:iterate>
					<th class="bord-l-b">
						Total
					</th>
					<c:if test="${!viewOnly}">
					<th class="bord-l-b">
						&nbsp;
					</th>
					</c:if>
				</tr>

				<logic:iterate id="institutionCostSharePersonnel" name="KualiForm" property="document.budget.institutionCostSharePersonnelItems" indexId="rowctr">
					<tr>
						<th rowspan="2" class="bord-l-b">
							<div align="center">
								${rowctr+1}
							</div>
						</th>
						<th class="bord-l-b">
							<div align="right">
								<kul:htmlAttributeLabel attributeEntry="${DataDictionary.InstitutionCostSharePersonnel.attributes.chartOfAccountsCode}" skipHelpUrl="true" />
							</div>
						</th>
						<td class="datacell">
							<div align="left">
								${institutionCostSharePersonnel.chartOfAccountsCode}/${institutionCostSharePersonnel.organizationCode}
							</div>
						</td>

						<!--- Iterate over the periods to create the columns -->
						<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="colctr">
							<td class="datacell">
								<div align="right">
									<fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.institutionDirect.institutionDirectPersonnel[rowctr][colctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
								</div>
							</td>
						</logic:iterate>

						<td class="datacell">
							<div align="right">
								<strong><fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.institutionDirect.totalInstitutionDirectPersonnel[rowctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong>
							</div>
						</td>
						<c:if test="${!viewOnly}">
						<td rowspan="2" class="datacell">
							<div align="center">
								&nbsp;
							</div>
						</td>
						</c:if>

					</tr>
					<tr>
						<th class="bord-l-b">
							<div align="right">
								<kul:htmlAttributeLabel attributeEntry="${DataDictionary.InstitutionCostSharePersonnel.attributes.budgetInstitutionCostSharePersonnelDescription}" skipHelpUrl="true" />
							</div>
						</th>
						<td colspan="${KualiForm.document.periodListSize+2}" class="datacell">
							<div align="left">
								<kul:htmlControlAttribute property="document.budget.institutionCostSharePersonnelItem[${rowctr}].budgetInstitutionCostSharePersonnelDescription" attributeEntry="${institutionCostSharePersonnelAttributes.budgetInstitutionCostSharePersonnelDescription}" readOnly="${viewOnly}" />
							</div>
						</td>
					</tr>
				</logic:iterate>

				<!------------------- Add Source ---------------------------------------------------------------->
				<tr>
					<td colspan="${KualiForm.document.periodListSize+7}" class="tab-subhead">
						Add Source
					</td>
				</tr>

				<tr>
					<th colspan="3" class="bord-l-b">
						&nbsp;
					</th>
					<!--- Iterate over the periods to create the columns -->
					<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="ctr">
						<th class="bord-l-b">
							${ctr+1}
						</th>
					</logic:iterate>
					<th class="bord-l-b">
						Total
					</th>
					<c:if test="${!viewOnly}">
					<th class="bord-l-b">
						Actions
					</th>
					</c:if>
				</tr>
				
				<c:if test="${!viewOnly}">
				<tr>
					<th rowspan="${institutionDirectColumns}" class="bord-l-b">
						add:
					</th>
					<th class="datacell">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${DataDictionary.InstitutionCostSharePersonnel.attributes.chartOfAccountsCode}" skipHelpUrl="true" />
						</div>
					</th>

					<td class="infoline">
						<div align="left">
							<c:if test="${empty KualiForm.newInstitutionCostShare.chartOfAccountsCode}">none selected</c:if>
							<c:if test="${!empty KualiForm.newInstitutionCostShare.chartOfAccountsCode}">${KualiForm.newInstitutionCostShare.chartOfAccountsCode}/${KualiForm.newInstitutionCostShare.organizationCode}</c:if>
							<kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="organizationCode:newInstitutionCostShare.organizationCode,chartOfAccountsCode:newInstitutionCostShare.chartOfAccountsCode" anchor="${currentTabIndex}"/>
						</div>
					</td>

					<!--- Iterate over the periods to create the columns -->
					<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="ctr">
						<td class="infoline">
							<div align="center">
								<html:text property="newInstitutionCostShare.budgetPeriodCostShareItem[${ctr}].budgetCostShareAmount" size="5" styleClass="amount" />
							</div>
						</td>
					</logic:iterate>
					<td class="infoline">
						<div align="right">
							&nbsp;
						</div>
					</td>
					<td rowspan="${institutionDirectColumns}" class="infoline">
						<div align="center">
							<html:image property="methodToCall.insertInstitutionCostShareDirect.anchor${currentTabIndex}" src="./images/tinybutton-add1.gif" styleClass="tinybutton" alt="add" />
						</div>
					</td>
				</tr>
				<tr>
					<th class="infoline">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${DataDictionary.InstitutionCostSharePersonnel.attributes.budgetInstitutionCostSharePersonnelDescription}" skipHelpUrl="true" />
						</div>
					</th>
					<td colspan="${KualiForm.document.periodListSize+2}" class="infoline">
						<div align="left">
							<html:text property="newInstitutionCostShare.budgetCostShareDescription" styleClass="datacell" size="40" />
						</div>
					</td>
				</tr>
				<c:if test="${KualiForm.displayCostSharePermission}">
				<tr>
					<th class="bord-l-b">
						<div align="right">
							Include source in permissions?
						</div>
					</th>
					<td colspan="${KualiForm.document.periodListSize + 2}" class="infoline">
						<div align="left">
							<html:checkbox property="newInstitutionCostShare.permissionIndicator" />
						</div>
					</td>
				</tr>
				</c:if>
				</c:if>

				<!--------- Show added Sources ------------------------------------------------------------------->
				<logic:iterate id="sources" name="KualiForm" property="document.budget.institutionCostShareItems" indexId="rowctr">
					<tr>
						<th rowspan="${institutionDirectColumns}" class="bord-l-b">
							${rowctr+1}
						</th>
						<th class="bord-l-b">
							<div align="right">
								<kul:htmlAttributeLabel attributeEntry="${DataDictionary.InstitutionCostSharePersonnel.attributes.chartOfAccountsCode}" skipHelpUrl="true" />
							</div>
						</th>

						<td class="datacell">
							<div align="left">
								<c:if test="${empty KualiForm.document.budget.institutionCostShareItems[rowctr].chartOfAccountsCode}">none selected</c:if>
								<c:if test="${!empty KualiForm.document.budget.institutionCostShareItems[rowctr].chartOfAccountsCode}">${KualiForm.document.budget.institutionCostShareItems[rowctr].chartOfAccountsCode}/${KualiForm.document.budget.institutionCostShareItems[rowctr].organizationCode}</c:if>
								<c:if test="${!viewOnly}">
									<kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="organizationCode:document.budget.institutionCostShareItems[${rowctr}].organizationCode,chartOfAccountsCode:document.budget.institutionCostShareItems[${rowctr}].chartOfAccountsCode" anchor="${currentTabIndex}"/>
								</c:if>
							</div>
						</td>

						<!--- Iterate over the periods to create the columns -->
						<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="colctr">
							<td class="datacell">
								<div align="center">
									<kul:htmlControlAttribute property="document.budget.institutionCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].budgetCostShareAmount"
										attributeEntry="${budgetPeriodInstitutionCostShareAttributes.budgetCostShareAmount}" readOnly="${viewOnly}" styleClass="amount" />
								</div>
							</td>
						</logic:iterate>
						<td class="datacell">
							<div align="right">
								<fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.institutionDirect.totalSource[rowctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
							</div>
						</td>
						<c:if test="${!viewOnly}">
						<td rowspan="${institutionDirectColumns}" class="datacell">
							<div align="center">
								<html:image property="methodToCall.deleteInstitutionCostShareDirect.line${rowctr}.anchor${currentTabIndex}" src="./images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete" />
							</div>
						</td>
						</c:if>
					</tr>
					<tr>
						<th class="bord-l-b">
							<div align="right">
								<kul:htmlAttributeLabel attributeEntry="${DataDictionary.InstitutionCostSharePersonnel.attributes.budgetInstitutionCostSharePersonnelDescription}" skipHelpUrl="true" />
							</div>
						</th>
						<td colspan="${KualiForm.document.periodListSize+2}" class="datacell">
							<div align="left">
								<kul:htmlControlAttribute property="document.budget.institutionCostShareItem[${rowctr}].budgetCostShareDescription" attributeEntry="${budgetInstitutionCostShareAttributes.budgetCostShareDescription}" readOnly="${viewOnly}" />
							</div>
						</td>
					</tr>
					<c:if test="${KualiForm.displayCostSharePermission}">
					<tr>
						<th class="bord-l-b">
							<div align="right">
								Include source in permissions?
							</div>
						</th>
						<td colspan="${KualiForm.document.periodListSize+2}" class="datacell">
							<div align="left">
								<kul:htmlControlAttribute property="document.budget.institutionCostShareItem[${rowctr}].permissionIndicator" attributeEntry="${budgetInstitutionCostShareAttributes.permissionIndicator}" readOnly="${viewOnly}" />
							</div>
						</td>
					</tr>
					</c:if>
				</logic:iterate>
			</table>
		</div>

        <html:hidden property="document.institutionCostShareNextSequenceNumber" />
		<html:hidden property="document.budget.institutionCostShareIndicator" />

		<!-- Hiddens for existing personnel items -->
		<logic:iterate id="institutionCostSharePerson" name="KualiForm" property="document.budget.institutionCostSharePersonnelItems" indexId="ctr">
			<html:hidden property="document.budget.institutionCostSharePersonnelItem[${ctr}].documentNumber" />
			<html:hidden property="document.budget.institutionCostSharePersonnelItem[${ctr}].organizationCode" />
			<html:hidden property="document.budget.institutionCostSharePersonnelItem[${ctr}].chartOfAccountsCode" />
			<html:hidden property="document.budget.institutionCostSharePersonnelItem[${ctr}].versionNumber" />
		</logic:iterate>

		<!-- Hidden for new item -->
		<html:hidden property="newInstitutionCostShare.chartOfAccountsCode" />
		<html:hidden property="newInstitutionCostShare.organizationCode" />

		<!-- Hiddens for added cost share direct items, including for it's budgetPeriodCostShareItem objects -->
		<logic:iterate id="sources" name="KualiForm" property="document.budget.institutionCostShareItems" indexId="rowctr">
			<html:hidden property="document.budget.institutionCostShareItem[${rowctr}].documentNumber" />
			<html:hidden property="document.budget.institutionCostShareItem[${rowctr}].budgetCostShareSequenceNumber" />
			<html:hidden property="document.budget.institutionCostShareItem[${rowctr}].organizationCode" />
			<html:hidden property="document.budget.institutionCostShareItem[${rowctr}].chartOfAccountsCode" />
			<html:hidden property="document.budget.institutionCostShareItem[${rowctr}].versionNumber" />

			<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="colctr">
				<html:hidden property="document.budget.institutionCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].documentNumber" />
				<html:hidden property="document.budget.institutionCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].budgetCostShareSequenceNumber" />
				<html:hidden property="document.budget.institutionCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].budgetPeriodSequenceNumber" />
				<html:hidden property="document.budget.institutionCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].versionNumber" />
			</logic:iterate>
		</logic:iterate>

	</c:if>
</kul:tab>