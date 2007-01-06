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
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra"%>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt"%>

<c:set var="KraConstants" value="${KraConstants}" />
<c:set var="showCostShareIndirect" value="${KualiForm.document.budget.institutionCostShareIndicator && KualiForm.document.budget.indirectCost.budgetIndirectCostCostShareIndicator}"/>

<c:set var="tabDescription">
	<c:choose>
		<c:when test="${showCostShareIndirect}">
			&nbsp;
		</c:when>
		<c:otherwise>
			None
		</c:otherwise>
	</c:choose>
</c:set>

<kul:tab tabTitle="Cost Share - Institution Indirect" tabDescription="${tabDescription}" defaultOpen="false" transparentBackground="false">
	<c:if test="${!showCostShareIndirect}">
		<div class="tab-container" align="center">
			<div class="h2-container">
				<span class="subhead-left">
					<h2>
						Cost Share - Institution Indirect
					</h2> </span>
					<span class="subhead-right"> <span class="subhead"><kul:help securityGroupName="${KraConstants.KRA_ADMIN_GROUP_NAME}" parameterName="${KraConstants.BUDGET_COSTSHARE_INDIRECT_HELP_PARAMETER_NAME}" altText="help"/></span> </span>
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

	<c:if test="${showCostShareIndirect}">
		<div class="tab-container" align=center>
			<table class="datatable" cellpadding="0" cellspacing="0" >
				<tr>
					<td class="subhead" colspan="${KualiForm.document.periodListSize+3}">
						<span class="subhead-left">Cost Share - Institution Indirect</span>
						<span class="subhead-right"> <span class="subhead"><kul:help securityGroupName="${KraConstants.KRA_ADMIN_GROUP_NAME}" parameterName="${KraConstants.BUDGET_COSTSHARE_INDIRECT_HELP_PARAMETER_NAME}" altText="help"/></span> </span>
					</td>
				</tr>
				<tr>
					<th class="bord-l-b">
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
				</tr>
				<tr>
					<th class="bord-l-b">
						<div align="right">
							Calculated Indirect Cost:
						</div>
					</th>
					<!--- Iterate over the periods to create the columns -->
					<logic:iterate id="periodTotal" name="KualiForm" property="budgetIndirectCostFormHelper.periodTotals" indexId="ctr">
	
						<c:choose>
							<c:when test="${KualiForm.document.budget.indirectCost.budgetIndirectCostCostShareIndicator}">
								<td class="datacell">
									<div align="right">
										<fmt:formatNumber value="${periodTotal.costShareCalculatedIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
									</div>
								</td>
							</c:when>
							<c:otherwise>
								<td>
									&nbsp;
								</td>
							</c:otherwise>
						</c:choose>
					</logic:iterate>
	
	
					<td class="datacell">
						<div align="right">
							<strong> <fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.periodSubTotal.costShareCalculatedIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" /> </strong>
						</div>
					</td>
				</tr>
				<tr>
					<th class="bord-l-b">
						<div align="right">
							Unrecovered Indirect Cost:
						</div>
					</th>
					<!--- Iterate over the periods to create the columns -->
					<logic:iterate id="periodTotal" name="KualiForm" property="budgetIndirectCostFormHelper.periodTotals" indexId="ctr">
						<td class="datacell">
							<c:choose>
								<c:when test="${KualiForm.document.budget.indirectCost.budgetIndirectCostCostShareIndicator}">
									<div align="right">
										<fmt:formatNumber value="${periodTotal.costShareUnrecoveredIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
									</div>
								</c:when>
								<c:otherwise>
								&nbsp;
							</c:otherwise>
							</c:choose>
						</td>
					</logic:iterate>
					<td class="datacell">
						<div align="right">
							<strong> <fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.periodSubTotal.costShareUnrecoveredIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" /> </strong>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</c:if>
</kul:tab>

<!-- Hidden variables for field level validation. -->
<html:hidden property="document.budget.indirectCost.budgetIndirectCostCostShareIndicator" />

<html:hidden property="budgetIndirectCostFormHelper.periodSubTotal.costShareCalculatedIndirectCost" />
<html:hidden property="budgetIndirectCostFormHelper.periodSubTotal.costShareUnrecoveredIndirectCost" />

<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="ctr">
	<html:hidden property="budgetIndirectCostFormHelper.periodTotal[${ctr}].costShareCalculatedIndirectCost" />
	<html:hidden property="budgetIndirectCostFormHelper.periodTotal[${ctr}].costShareUnrecoveredIndirectCost" />
</logic:iterate>
