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

<kul:tab tabTitle="Cost Share - Totals" defaultOpen="false" tabDescription="&nbsp;" transparentBackground="false">
	<div class="tab-container" style="" align=center>
		<table class="datatable" cellpadding="0" cellspacing="0">
			<tbody>
				<tr>
					<td class="subhead" colspan="${KualiForm.document.periodListSize+3}">
						<span class="subhead-left">Cost Share - Totals</span>
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
							Institution Direct Cost Share:
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
					<th class="bord-l-b">
						<div align="right">
							Institution Indirect Cost Share:
						</div>
					</th>
					<!--- Iterate over the periods to create the columns -->
					<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="ctr">
						<td class="datacell">
							<div align="right">
								<fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.institutionIndirectCostShare[ctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
							</div>
						</td>
					</logic:iterate>
					<td class="datacell">
						<div align="right">
							<strong><fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.totalInstitutionIndirectCostShare}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong>
						</div>
					</td>
				</tr>
				<tr>
					<th class="bord-l-b">
						<div align="right">
							3rd Party Direct Cost Share:
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
					<th class="bord-l-b">
						<div align="right">
							Subcontractor Cost Share:
						</div>
					</th>
					<!--- Iterate over the periods to create the columns -->
					<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="ctr">
						<td class="datacell">
							<div align="right">
								<fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.subcontractorCostShare[ctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
							</div>
						</td>
					</logic:iterate>
					<td class="datacell">
						<div align="right">
							<strong><fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.totalSubcontractorCostShare}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong>
						</div>
					</td>
				</tr>
				<tr>
					<th class="bord-l-b">
						<div align="right">
							Total:
						</div>
					</th>
					<!--- Iterate over the periods to create the columns -->
					<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="ctr">
						<td class="datacell">
							<div align="right">
								<fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.total[ctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
							</div>
						</td>
					</logic:iterate>
					<td class="datacell">
						<div align="right">
							<strong><fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.totalTotal}" type="currency" currencySymbol="$" maxFractionDigits="0" /></strong>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</kul:tab>
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
	<tr>
		<td align="left" class="footer">
			<img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3">
		</td>
		<td align="right" class="footer-right">
			<img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3">
		</td>
	</tr>
</table>
