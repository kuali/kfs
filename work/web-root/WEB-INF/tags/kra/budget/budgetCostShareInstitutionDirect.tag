<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra"%>

<c:set var="universityCostSharePersonnelAttributes" value="${DataDictionary.UniversityCostSharePersonnel.attributes}" />
<c:set var="budgetUniversityCostShareAttributes" value="${DataDictionary.BudgetUniversityCostShare.attributes}" />
<c:set var="budgetPeriodUniversityCostShareAttributes" value="${DataDictionary.BudgetPeriodUniversityCostShare.attributes}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>

<c:set var="tabDescription">
	<c:choose>
		<c:when test="${KualiForm.document.budget.universityCostShareIndicator}">
			balance: <fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.institutionDirect.totalBalanceToBeDistributed}" type="currency" currencySymbol="$" maxFractionDigits="0" />
		</c:when>
		<c:otherwise>
			None
		</c:otherwise>
	</c:choose>
</c:set>

<kul:tab tabTitle="Cost Share - Institution Direct" tabDescription="${tabDescription}" defaultOpen="true" transparentBackground="true" tabErrorKey="document.budget.universityCostShareItem*" tabAuditKey="document.budget.audit.costShare.institution*">
	
	<c:if test="${!KualiForm.document.budget.universityCostShareIndicator}">
		<div class="tab-container" align="center">
			<table class="datatable" cellpadding="0">
				<tr>
					<td class="subhead" colspan="${KualiForm.document.periodListSize+7}">
						<span class="subhead-left">Institution Cost Share</span>
						<span class="subhead-right"> <span class="subhead"><kul:help businessObjectClassName="${DataDictionary.BudgetUniversityCostShare.businessObjectClass}" altText="help"/></span> </span>
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

	<c:if test="${KualiForm.document.budget.universityCostShareIndicator}">
		<div class="tab-container-error">
			<div class="left-errmsg-tab">
				<kra-b:auditErrors cluster="costShareAuditErrors" keyMatch="document.budget.audit.costShare.institution*" isLink="false" includesTitle="true" />
			</div>
		</div>
		<div class="tab-container" align=center>
			<table class="datatable" cellpadding="0">
				<tr>
					<td class="subhead" colspan="${KualiForm.document.periodListSize+7}">
						<span class="subhead-left">Cost Share - Institution Direct</span>
						<span class="subhead-right"> <span class="subhead"><kul:help businessObjectClassName="${DataDictionary.BudgetUniversityCostShare.businessObjectClass}" altText="help"/></span> </span>
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
					<td rowspan="3" class="datacell">
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

				<logic:iterate id="universityCostSharePersonnel" name="KualiForm" property="document.budget.universityCostSharePersonnelItems" indexId="rowctr">
					<tr>
						<th rowspan="2" class="bord-l-b">
							<div align="center">
								${rowctr+1}
							</div>
						</th>
						<th class="bord-l-b">
							<div align="right">
								Source:
							</div>
						</th>
						<td class="datacell">
							<div align="left">
								${universityCostSharePersonnel.chartOfAccountsCode}/${universityCostSharePersonnel.organizationCode}
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
								Description:
							</div>
						</th>
						<td colspan="${KualiForm.document.periodListSize+2}" class="datacell">
							<div align="left">
								<kul:htmlControlAttribute property="document.budget.universityCostSharePersonnelItem[${rowctr}].budgetUniversityCostSharePersonnelDescription" attributeEntry="${universityCostSharePersonnelAttributes.budgetUniversityCostSharePersonnelDescription}" readOnly="${viewOnly}" />
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
					<th rowspan="2" class="bord-l-b">
						add:
					</th>
					<th class="datacell">
						<div align="right">
							Source:
						</div>
					</th>

					<td class="infoline">
						<div align="left">
							<c:if test="${empty KualiForm.newUniversityCostShare.chartOfAccountsCode}">none selected</c:if>
							<c:if test="${!empty KualiForm.newUniversityCostShare.chartOfAccountsCode}">${KualiForm.newUniversityCostShare.chartOfAccountsCode}/${KualiForm.newUniversityCostShare.organizationCode}</c:if>
							<kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="organizationCode:newUniversityCostShare.organizationCode,chartOfAccountsCode:newUniversityCostShare.chartOfAccountsCode" anchor="${currentTabIndex}"/>
						</div>
					</td>

					<!--- Iterate over the periods to create the columns -->
					<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="ctr">
						<td class="infoline">
							<div align="center">
								<html:text property="newUniversityCostShare.budgetPeriodCostShareItem[${ctr}].budgetCostShareAmount" size="5" />
							</div>
						</td>
					</logic:iterate>
					<td class="infoline">
						<div align="right">
							&nbsp;
						</div>
					</td>
					<td rowspan="2" class="infoline">
						<div align="center">
							<html:image property="methodToCall.insertUniversityCostShareDirect.anchor${currentTabIndex}" src="./images/tinybutton-add1.gif" styleClass="tinybutton" alt="add" />
						</div>
					</td>
				</tr>
				<tr>
					<th class="infoline">
						<div align="right">
							Description:
						</div>
					</th>
					<td colspan="${KualiForm.document.periodListSize+2}" class="infoline">
						<div align="left">
							<html:text property="newUniversityCostShare.budgetCostShareDescription" styleClass="datacell" size="40" />
						</div>
					</td>
				</tr>
				</c:if>

				<!--------- Show added Sources ------------------------------------------------------------------->
				<logic:iterate id="sources" name="KualiForm" property="document.budget.universityCostShareItems" indexId="rowctr">
					<tr>
						<th rowspan="2" class="bord-l-b">
							${rowctr+1}
						</th>
						<th class="bord-l-b">
							<div align="right">
								Source:
							</div>
						</th>

						<td class="datacell">
							<div align="left">
								<c:if test="${empty KualiForm.document.budget.universityCostShareItems[rowctr].chartOfAccountsCode}">none selected</c:if>
								<c:if test="${!empty KualiForm.document.budget.universityCostShareItems[rowctr].chartOfAccountsCode}">${KualiForm.document.budget.universityCostShareItems[rowctr].chartOfAccountsCode}/${KualiForm.document.budget.universityCostShareItems[rowctr].organizationCode}</c:if>
								<c:if test="${!viewOnly}">
									<kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="organizationCode:document.budget.universityCostShareItems[${rowctr}].organizationCode,chartOfAccountsCode:document.budget.universityCostShareItems[${rowctr}].chartOfAccountsCode" anchor="${currentTabIndex}"/>
								</c:if>
							</div>
						</td>

						<!--- Iterate over the periods to create the columns -->
						<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="colctr">
							<td class="datacell">
								<div align="center">
									<kul:htmlControlAttribute property="document.budget.universityCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].budgetCostShareAmount"
										attributeEntry="${budgetPeriodUniversityCostShareAttributes.budgetCostShareAmount}" readOnly="${viewOnly}" />
								</div>
							</td>
						</logic:iterate>
						<td class="datacell">
							<div align="right">
								<fmt:formatNumber value="${KualiForm.budgetCostShareFormHelper.institutionDirect.totalSource[rowctr]}" type="currency" currencySymbol="$" maxFractionDigits="0" />
							</div>
						</td>
						<c:if test="${!viewOnly}">
						<td rowspan="2" class="datacell">
							<div align="center">
								<html:image property="methodToCall.deleteUniversityCostShareDirect.line${rowctr}.anchor${currentTabIndex}" src="./images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete" />
							</div>
						</td>
						</c:if>
					</tr>
					<tr>
						<th class="bord-l-b">
							<div align="right">
								Description:
							</div>
						</th>
						<td colspan="${KualiForm.document.periodListSize+2}" class="datacell">
							<div align="left">
								<kul:htmlControlAttribute property="document.budget.universityCostShareItem[${rowctr}].budgetCostShareDescription" attributeEntry="${budgetUniversityCostShareAttributes.budgetCostShareDescription}" readOnly="${viewOnly}" />
							</div>
						</td>
					</tr>
				</logic:iterate>
			</table>
		</div>

        <html:hidden property="document.universityCostShareNextSequenceNumber" />
		<html:hidden property="document.budget.universityCostShareIndicator" />

		<!-- Hiddens for existing personnel items -->
		<logic:iterate id="universityCostSharePerson" name="KualiForm" property="document.budget.universityCostSharePersonnelItems" indexId="ctr">
			<html:hidden property="document.budget.universityCostSharePersonnelItem[${ctr}].documentHeaderId" />
			<html:hidden property="document.budget.universityCostSharePersonnelItem[${ctr}].organizationCode" />
			<html:hidden property="document.budget.universityCostSharePersonnelItem[${ctr}].chartOfAccountsCode" />
			<html:hidden property="document.budget.universityCostSharePersonnelItem[${ctr}].versionNumber" />
		</logic:iterate>

		<!-- Hidden for new item -->
		<html:hidden property="newUniversityCostShare.chartOfAccountsCode" />
		<html:hidden property="newUniversityCostShare.organizationCode" />

		<!-- Hiddens for added cost share direct items, including for it's budgetPeriodCostShareItem objects -->
		<logic:iterate id="sources" name="KualiForm" property="document.budget.universityCostShareItems" indexId="rowctr">
			<html:hidden property="document.budget.universityCostShareItem[${rowctr}].documentHeaderId" />
			<html:hidden property="document.budget.universityCostShareItem[${rowctr}].budgetCostShareSequenceNumber" />
			<html:hidden property="document.budget.universityCostShareItem[${rowctr}].organizationCode" />
			<html:hidden property="document.budget.universityCostShareItem[${rowctr}].chartOfAccountsCode" />
			<html:hidden property="document.budget.universityCostShareItem[${rowctr}].versionNumber" />

			<logic:iterate id="period" name="KualiForm" property="document.budget.periods" indexId="colctr">
				<html:hidden property="document.budget.universityCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].documentHeaderId" />
				<html:hidden property="document.budget.universityCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].budgetCostShareSequenceNumber" />
				<html:hidden property="document.budget.universityCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].budgetPeriodSequenceNumber" />
				<html:hidden property="document.budget.universityCostShareItem[${rowctr}].budgetPeriodCostShareItem[${colctr}].versionNumber" />
			</logic:iterate>
		</logic:iterate>

	</c:if>
</kul:tab>
