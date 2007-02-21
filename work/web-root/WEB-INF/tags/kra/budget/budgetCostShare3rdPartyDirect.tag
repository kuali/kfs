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

<c:set var="budgetThirdPartyCostShareAttributes" value="${DataDictionary.BudgetThirdPartyCostShare.attributes}" />
<c:set var="budgetPeriodThirdPartyCostShareAttributes" value="${DataDictionary.BudgetPeriodThirdPartyCostShare.attributes}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>

<c:set var="tabDescription">
	<c:choose>
		<c:when test="${KualiForm.document.budget.budgetThirdPartyCostShareIndicator}">
			balance: <fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.thirdPartyDirect.totalBalanceToBeDistributed}" type="currency" currencySymbol="$" maxFractionDigits="0" />
		</c:when>
		<c:otherwise>
			None
		</c:otherwise>
	</c:choose>
</c:set>

<kul:tab tabTitle="Cost Share - 3rd Party Direct" tabDescription="${tabDescription}" defaultOpen="false" transparentBackground="false" tabErrorKey="document.budget.thirdPartyCostShareItem*" auditCluster="costShareAuditErrors" tabAuditKey="document.budget.audit.costShare.3rdParty*">
	<c:if test="${!KualiForm.document.budget.budgetThirdPartyCostShareIndicator}">
		<div class="tab-container" align="center">
			<div class="h2-container">
				<span class="subhead-left">
					<h2>
						Cost Share - 3rd Party Direct
					</h2> </span>
					<span class="subhead-right"> <span class="subhead"><kul:help businessObjectClassName="${DataDictionary.BudgetThirdPartyCostShare.businessObjectClass}" altText="help"/></span> </span>
			</div>
			<table cellpadding=0  cellspacing="0" summary="view/edit document overview information">
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

	<c:if test="${KualiForm.document.budget.budgetThirdPartyCostShareIndicator}">
		<div class="tab-container" align=center>
			<table class="datatable" cellpadding="0" cellspacing="0" >
				<tr>
					<td class="subhead" colspan="${KualiForm.document.periodListSize+6}">
						<span class="subhead-left">Cost Share - 3rd Party Direct</span>
						<span class="subhead-right"> <span class="subhead"><kul:help businessObjectClassName="${DataDictionary.BudgetThirdPartyCostShare.businessObjectClass}" altText="help"/></span> </span>
					</td>
				</tr>
				<tr>
					<td colspan="${KualiForm.document.periodListSize+6}" class="tab-subhead">
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
						Total
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
								<fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.thirdPartyDirect.totalBudgeted[ctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
							</div>
						</td>
					</logic:iterate>
					<td class="datacell">
						<div align="right">
							<strong><fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.thirdPartyDirect.totalTotalBudgeted}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong>
						</div>
					</td>
					<c:if test="${!viewOnly}">
					<td rowspan="3" class="infoline">
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
								<fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.thirdPartyDirect.amountDistributed[ctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
							</div>
						</td>
					</logic:iterate>
					<td class="datacell">
						<div align="right">
							<strong><fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.thirdPartyDirect.totalAmountDistributed}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong>
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
								<fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.thirdPartyDirect.balanceToBeDistributed[ctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
							</div>
						</td>
					</logic:iterate>
					<td class="datacell">
						<div align="right">
							<strong><fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.thirdPartyDirect.totalBalanceToBeDistributed}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong>
						</div>
					</td>
				</tr>

				<!---------------------- Imported from Nonpersonnel --------------------------------------------->
				<tr>
					<td colspan="${KualiForm.document.periodListSize+7}" class="tab-subhead">
						Imported from Nonpersonnel
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

				<logic:iterate id="subcontractor" name="KualiForm" property="budgetCostShareFormHelper.subcontractors" indexId="rowctr">
					<tr>
						<th rowspan="2" class="bord-l-b">
							<div align="center">
								${rowctr+1}
							</div>
						</th>
						<th class="bord-l-b">
							<div align="right">
								Subcontractor:
							</div>
						</th>
						<td class="datacell">
							<div align="left">
								${subcontractor.budgetNonpersonnelDescription}
							</div>
						</td>

						<!--- Iterate over the periods to create the columns -->
						<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="colctr">
							<td class="datacell">
								<div align="right">
                                    <fmt:formatNumber value="${subcontractor.periodAmounts[colctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
								</div>
							</td>
						</logic:iterate>

						<td class="datacell">
							<div align="right">
								<strong><fmt:formatNumber value="${subcontractor.totalPeriodAmount}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong>
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
								&nbsp;
							</div>
						</th>
						<td colspan="${KualiForm.document.periodListSize+2}" class="datacell">
							<div align="left">
								&nbsp;
							</div>
						</td>
					</tr>
				</logic:iterate>

				<!------------------- Add Source ---------------------------------------------------------------->
				<tr>
					<td colspan="10" class="tab-subhead">
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
					<th rowspan="2" class="bord-l-b">
						add:
					</th>
					<th class="bord-l-b">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetThirdPartyCostShare.attributes.budgetCostShareSourceName}" skipHelpUrl="true" />
						</div>
					</th>

					<td class="infoline">
						<div align="left">
							<html:text property="newThirdPartyCostShare.budgetCostShareSourceName" styleClass="infoline" size="8" />
						</div>
					</td>

					<!--- Iterate over the periods to create the columns -->
					<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="ctr">
						<td class="infoline">
							<div align="center">
								<html:text property="newThirdPartyCostShare.budgetPeriodCostShareItem[${ctr}].budgetCostShareAmount" size="5" styleClass="amount" />
							</div>
						</td>
					</logic:iterate>
					<td class="infoline">
						<div align="center">
							&nbsp;
						</div>
					</td>
					<td rowspan="2" class="infoline">
						<div align="center">
							<html:image property="methodToCall.insertThirdPartyCostShareDirect.anchor${currentTabIndex}" src="./images/tinybutton-add1.gif" styleClass="tinybutton" alt="add" />
						</div>
					</td>
				</tr>
				<tr>
					<th class="bord-l-b">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetThirdPartyCostShare.attributes.budgetCostShareDescription}" skipHelpUrl="true" />
						</div>
					</th>
					<td colspan="${KualiForm.document.periodListSize+2}" class="infoline">
						<div align="left">
							<html:text property="newThirdPartyCostShare.budgetCostShareDescription" styleClass="infoline" size="40" />
						</div>
					</td>
				</tr>
				</c:if>

				<!--------- Show added Sources ------------------------------------------------------------------->

				<logic:iterate id="sources" name="KualiForm" property="document.budget.thirdPartyCostShareItems" indexId="rowctr">
					<tr>
						<th rowspan="2" class="bord-l-b">
							${rowctr+1}
						</th>
						<th class="bord-l-b">
							<div align="right">
								<kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetThirdPartyCostShare.attributes.budgetCostShareSourceName}" skipHelpUrl="true" />
							</div>
						</th>

						<td class="datacell">
							<div align="left">
								<kul:htmlControlAttribute property="document.budget.thirdPartyCostShareItem[${rowctr}].budgetCostShareSourceName" attributeEntry="${budgetThirdPartyCostShareAttributes.budgetCostShareSourceName}" readOnly="${viewOnly}" />
							</div>
						</td>

						<!--- Iterate over the periods to create the columns -->
						<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="colctr">
							<td class="datacell">
								<div align="center">
									<kul:htmlControlAttribute property="document.budget.thirdPartyCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].budgetCostShareAmount"
										attributeEntry="${budgetPeriodThirdPartyCostShareAttributes.budgetCostShareAmount}" readOnly="${viewOnly}" styleClass="amount" />
								</div>
							</td>
						</logic:iterate>
						<td class="datacell">
							<div align="right">
								<fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.thirdPartyDirect.totalSource[rowctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
							</div>
						</td>
						<c:if test="${!viewOnly}">
						<td rowspan="2" class="datacell">
							<div align="center">
								<html:image property="methodToCall.deleteThirdPartyCostShare.line${rowctr}.anchor${currentTabIndex}" src="./images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete" />
							</div>
						</td>
						</c:if>
					</tr>
					<tr>
						<th class="datacell">
							<div align="right">
								<kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetThirdPartyCostShare.attributes.budgetCostShareDescription}" skipHelpUrl="true" />
							</div>
						</th>
						<td colspan="${KualiForm.document.periodListSize+2}" class="datacell">
							<div align="left">
								<kul:htmlControlAttribute property="document.budget.thirdPartyCostShareItem[${rowctr}].budgetCostShareDescription" attributeEntry="${budgetThirdPartyCostShareAttributes.budgetCostShareDescription}" readOnly="${viewOnly}" />
							</div>
						</td>
					</tr>
				</logic:iterate>
			</table>
		</div>

		<html:hidden property="document.thirdPartyCostShareNextSequenceNumber" />
		<html:hidden property="document.budget.budgetThirdPartyCostShareIndicator" />

		<!-- Hiddens for added cost share indirect items, including for it's budgetPeriodCostShareItem objects -->
		<logic:iterate id="sources" name="KualiForm" property="document.budget.thirdPartyCostShareItems" indexId="rowctr">
			<html:hidden property="document.budget.thirdPartyCostShareItem[${rowctr}].documentNumber" />
			<html:hidden property="document.budget.thirdPartyCostShareItem[${rowctr}].budgetCostShareSequenceNumber" />
			<html:hidden property="document.budget.thirdPartyCostShareItem[${rowctr}].versionNumber" />

			<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="colctr">
				<html:hidden property="document.budget.thirdPartyCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].documentNumber" />
				<html:hidden property="document.budget.thirdPartyCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].budgetCostShareSequenceNumber" />
				<html:hidden property="document.budget.thirdPartyCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].budgetPeriodSequenceNumber" />
				<html:hidden property="document.budget.thirdPartyCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].versionNumber" />
			</logic:iterate>
		</logic:iterate>
	</c:if>
</kul:tab>
