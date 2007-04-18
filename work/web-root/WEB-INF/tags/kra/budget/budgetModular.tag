<%--
 Copyright 2006 The Kuali Foundation.
 
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
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<html:hidden property="document.budget.modularBudget.documentNumber" /> 
<html:hidden property="document.budget.modularBudget.budgetModularIncrementAmount" /> 
<html:hidden property="document.budget.modularBudget.budgetModularTaskNumber" /> 
<html:hidden property="document.budget.modularBudget.budgetModularDirectCostAmount" /> 
<html:hidden property="document.budget.modularBudget.totalActualDirectCostAmount" /> 
<html:hidden property="document.budget.modularBudget.totalModularDirectCostAmount" /> 
<html:hidden property="document.budget.modularBudget.totalAdjustedModularDirectCostAmount" /> 
<html:hidden property="document.budget.modularBudget.totalConsortiumAmount" /> 
<html:hidden property="document.budget.modularBudget.totalDirectCostAmount" /> 
<html:hidden property="document.budget.modularBudget.versionNumber" /> 
<html:hidden property="document.budget.modularBudget.objectId" /> 
<html:hidden property="document.budget.modularBudget.incrementsString" /> 
<html:hidden property="document.budget.modularBudget.invalidMode" />

<c:set var="budgetModularAttributes" value="${DataDictionary.BudgetModular.attributes}" /> 
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>
<c:set var="totalActualDirectCostAmount"> 
	<fmt:formatNumber value="${KualiForm.document.budget.modularBudget.totalActualDirectCostAmount}" type="currency" currencySymbol="$" maxFractionDigits="0" /> 
</c:set> 
<c:set var="budgetModularDirectCostAmount"> 
	<fmt:formatNumber value="${KualiForm.document.budget.modularBudget.budgetModularDirectCostAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /> 
</c:set> 
<c:set var="totalModularDirectCostAmount"> 
	<fmt:formatNumber value="${KualiForm.document.budget.modularBudget.totalModularDirectCostAmount}" type="currency" currencySymbol="$" maxFractionDigits="0" /> 
</c:set> 
<c:set var="totalAdjustedModularDirectCostAmount"> 
	<fmt:formatNumber value="${KualiForm.document.budget.modularBudget.totalAdjustedModularDirectCostAmount}" type="currency" currencySymbol="$" maxFractionDigits="0" /> 
</c:set> 
<c:set var="totalConsortiumAmount"> 
	<fmt:formatNumber value="${KualiForm.document.budget.modularBudget.totalConsortiumAmount}" type="currency" currencySymbol="$" maxFractionDigits="0" /> 
</c:set> 
<c:set var="totalDirectCostAmount"> 
	<fmt:formatNumber value="${KualiForm.document.budget.modularBudget.totalDirectCostAmount}" type="currency" currencySymbol="$" maxFractionDigits="0" /> 
</c:set>

<div align="right">
  	<kul:help documentTypeName="${DataDictionary.KualiBudgetDocument.documentTypeName}" pageName="${KraConstants.MODULAR_HEADER_TAB}" altText="page help"/>
</div>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="t3" summary="">
	<tbody>
		<tr>
			<td><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tl3"></td>
            <td align="right"><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tr3"></td>
		</tr>
	</tbody>
</table>
      
<div id="workarea">
	<div class="tab-container" align="center"> 
		<div class="left-errmsg-tab"><kul:errors keyMatch="document.budget.modular.tooLarge" /></div>
		<table align="center" cellspacing="0">
			<tr>
				<td class="subhead" colspan="${KualiForm.document.periodListSize + 2}">
					<span class="subhead-left"><h2>Overview</h2></span>
				</td>
			</tr>
			<tr>
				<th width="35%" class="bord-l-b"><br/></th>
				<logic:iterate id="modularPeriod" indexId="i" name="KualiForm" property="document.budget.modularBudget.budgetModularPeriods">
					<th class="bord-l-b">
						<div align=center><strong>Period <%= i.intValue() + 1%></strong></div>
						<html:hidden property="document.budget.modularBudget.budgetModularPeriod[${i}].documentNumber" /> 
						<html:hidden property="document.budget.modularBudget.budgetModularPeriod[${i}].budgetPeriodSequenceNumber" /> 
						<html:hidden property="document.budget.modularBudget.budgetModularPeriod[${i}].actualDirectCostAmount" /> 
						<html:hidden property="document.budget.modularBudget.budgetModularPeriod[${i}].consortiumAmount" /> 
						<html:hidden property="document.budget.modularBudget.budgetModularPeriod[${i}].totalPeriodDirectCostAmount" /> 
						<html:hidden property="document.budget.modularBudget.budgetModularPeriod[${i}].versionNumber" /> 
						<html:hidden property="document.budget.modularBudget.budgetModularPeriod[${i}].objectId" /> 
					</th>
				</logic:iterate>
				<th class="bord-l-b"><div align=center><strong>Total</strong></div></th>
			</tr>
		
			<!-- Actual Direct Cost -->
			<tr>
				<th class="bord-l-b"><div align="right">Actual Direct Cost</div></th>
				<logic:iterate id="modularPeriod" name="KualiForm" property="document.budget.modularBudget.budgetModularPeriods">
					<td class="datacell">
						<div align="right"><fmt:formatNumber value="${modularPeriod.actualDirectCostAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /> <br/></div>
						<html:hidden name="modularPeriod" property="documentNumber"/>
					</td>
				</logic:iterate>
				<td class="datacell"><div align="right"><strong>${totalActualDirectCostAmount}</strong> <br/></div></td>
			</tr>
			
			<!-- Modular Direct Cost -->
			<tr>
				<th class="bord-l-b"><div align="right">Modular Direct Cost</div></th>
				<logic:iterate id="modularPeriod" name="KualiForm" property="document.budget.modularBudget.budgetModularPeriods">
					<td class="datacell"><div align="right">${budgetModularDirectCostAmount} <br/></div></td>
				</logic:iterate>
				<td class="datacell"><div align="right"><strong>${totalModularDirectCostAmount}</strong> <br/></div></td>
			</tr>
			
			<!-- Adjusted Modular Direct Cost -->
			<tr>
				<th class="bord-l-b"><div align="right">Adjusted Modular Direct Cost</div></th>
				<logic:iterate id="modularPeriod" indexId="i" name="KualiForm" property="document.budget.modularBudget.budgetModularPeriods">
					<td class="datacell">
						<div align="right"> 
							<c:choose>
								<c:when test="${KualiForm.document.budget.modularBudget.invalidMode}">N/A</c:when>
								<c:when test="${!viewOnly}">
									<html:select value="${modularPeriod.budgetAdjustedModularDirectCostAmount}" property="document.budget.modularBudget.budgetModularPeriod[${i}].budgetAdjustedModularDirectCostAmount" disabled="${viewOnly}">
										<html:options name="KualiForm" property="document.budget.modularBudget.increments"/> 
									</html:select> 
								</c:when>
								<c:otherwise>
								    <html:hidden property="document.budget.modularBudget.budgetModularPeriods[${i}].budgetAdjustedModularDirectCostAmount"/>
								    <fmt:formatNumber value="${modularPeriod.budgetAdjustedModularDirectCostAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /> <br/>
								</c:otherwise>
							</c:choose>
						</div>
					</td>
				</logic:iterate>
				<td class="datacell"><div align="right"><strong>${totalAdjustedModularDirectCostAmount}</strong> <br/></div></td>
			</tr>
			
			<!-- Consorium F&A Cost -->
			<tr>
				<th class="bord-l-b"><div align="right">Consortium F&A</div></th>
				<logic:iterate id="modularPeriod" name="KualiForm" property="document.budget.modularBudget.budgetModularPeriods">
					<td class="datacell">
						<div align="right"><fmt:formatNumber value="${modularPeriod.consortiumAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /> <br/></div>
					</td>
				</logic:iterate>
				<td class="datacell"><div align="right"><strong>${totalConsortiumAmount}</strong> <br/></div></td>
			</tr>
          
			<!-- Total Direct Costs -->
			<tr>
				<td class="infoline"><div align="right"><strong>Total Direct Costs</strong></div></td>
				<logic:iterate id="modularPeriod" name="KualiForm" property="document.budget.modularBudget.budgetModularPeriods">
					<td class="infoline">
						<div align="right">
							<strong><fmt:formatNumber value="${modularPeriod.totalPeriodDirectCostAmount}" type="currency" currencySymbol="$" maxFractionDigits="0" /> </strong><br/>
						</div>
					</td>
				</logic:iterate>
				<td class="infoline"><div align="right"><strong>${totalDirectCostAmount}</strong> <br/></div></td>
			</tr>
		</table>
		<c:if test="${not viewOnly}">
			<br><html:image property="methodToCall.recalculate" src="images/tinybutton-recalculate.gif" styleClass="tinybutton" disabled="${viewOnly}"/>
		</c:if>
	</div>
	
	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
		<tr>
			<td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
			<td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
		</tr>
	</table>
    <br><br>
      
	<kul:tabTop tabTitle="${budgetModularAttributes.budgetModularPersonnelDescription.label}" defaultOpen="false" tabErrorKey="document.budget.modularBudget.budgetModularPersonnelDescription.missing">
      	<div class="tab-container" align="center">
			<div class="h2-container"> 
				<span class="subhead-left"><h2>${budgetModularAttributes.budgetModularPersonnelDescription.label}</h2></span>
			</div>
            <table cellpadding=0  summary="view/edit document overview information">
          		<tr>
            		<td height="70" align=left valign=middle class="datacell">
            			<div align="center"> <br>
                			<kul:htmlControlAttribute property="document.budget.modularBudget.budgetModularPersonnelDescription" attributeEntry="${budgetModularAttributes.budgetModularPersonnelDescription}" readOnly="${viewOnly}"/><br><br>
              			</div>
              		</td>
          		</tr>
        	</table>
      	</div>
    </kul:tabTop>
      
	<kul:tab 
		tabTitle="${budgetModularAttributes.budgetModularConsortiumDescription.label}" 
		defaultOpen="false" 
		tabErrorKey="document.budget.modularBudget.budgetModularConsortiumDescription*" 
		tabAuditKey="document.budget.audit.modular.consortium*"
		auditCluster="modularSoftAuditErrors">
      	
      	<div class="tab-container" align="center">
			<div class="h2-container">
				<span class="subhead-left"><h2>${budgetModularAttributes.budgetModularConsortiumDescription.label}</h2></span>
			</div>
            <table cellpadding=0  summary="view/edit document overview information">
          		<tr>
            		<td height="70" align=left valign=middle class="datacell">
            			<div align="center"><br>
                			<kul:htmlControlAttribute property="document.budget.modularBudget.budgetModularConsortiumDescription" attributeEntry="${budgetModularAttributes.budgetModularConsortiumDescription}" readOnly="${viewOnly}"/><br><br>
              			</div>
              		</td>
          		</tr>
        	</table>
      	</div>
    </kul:tab>
      
	<kul:tab tabTitle="${budgetModularAttributes.budgetModularVariableAdjustmentDescription.label}" defaultOpen="false" tabErrorKey="document.budget.modularBudget.budgetModularVariableAdjustmentDescription*">
		<div class="tab-container" align="center"> 
			<div class="h2-container"> 
				<span class="subhead-left"><h2>${budgetModularAttributes.budgetModularVariableAdjustmentDescription.label} </h2></span>
			</div>
			<table cellpadding=0 summary="view/edit document overview information">
				<tr>
					<td height="70" align=left valign=middle class="datacell">
						<div align="center"> <br>
							<kul:htmlControlAttribute property="document.budget.modularBudget.budgetModularVariableAdjustmentDescription" attributeEntry="${budgetModularAttributes.budgetModularVariableAdjustmentDescription}" readOnly="${viewOnly}"/> <br><br>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</kul:tab>
	
	<!-- TAB -->
	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
		<tr>
			<td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
			<td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
		</tr>
	</table>
</div>
