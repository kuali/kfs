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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<c:set var="budgetIndirectCostAttributes" value="${DataDictionary.BudgetIndirectCost.attributes}" />
<c:set var="budgetTaskPeriodIndirectCostAttributes" value="${DataDictionary.BudgetTaskPeriodIndirectCost.attributes}" />
<c:set var="budgetTaskAttributes" value="${DataDictionary.BudgetTask.attributes}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>

<kul:htmlControlAttribute property="document.budget.indirectCost.documentNumber" attributeEntry="${budgetIndirectCostAttributes.documentNumber}"/>
<kul:htmlControlAttribute property="document.budget.indirectCost.objectId" attributeEntry="${budgetIndirectCostAttributes.objectId}"/>
<kul:htmlControlAttribute property="document.budget.indirectCost.versionNumber" attributeEntry="${budgetIndirectCostAttributes.versionNumber}"/>
<html:hidden property="document.budget.institutionCostShareIndicator"/>

<div align="right">
	<kul:help documentTypeName="${DataDictionary.KualiBudgetDocument.documentTypeName}" pageName="${KraConstants.INDIRECT_COST_HEADER_TAB}" altText="page help"/>
</div>

<!--  Spacer and styles.  -->
      	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="t3" summary="">
        <tbody>
          <tr>
            <td ><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tl3"/></td>
            <td align="right"><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tr3"/></td>
          </tr>
        </tbody>
      </table>
<div id="workarea">
		
	<div class="tab-container-error"><div class="left-errmsg-tab"><kul:errors keyMatch="document.budget.indirectCost*"/></div></div>

	<div class="tab-container" align="center">
	
	<div class="h2-container"> <span class="subhead-left">
    <h2>Indirect Costs - Parameters</h2>
    </span>
  </div>
	
	
<!--  Top part of IDC form.  This contains metadata for all indirect costs attached to the current budget. -->
          <table cellpadding="0" cellspacing="0" class="datatable" summary="">
            <tr>
              <th width="20%" align="right"  ><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetIndirectCost.attributes.budgetPurposeCode}" skipHelpUrl="true" /></th>

              <td width="30%"   >
              	<kul:htmlControlAttribute property="document.budget.indirectCost.budgetPurposeCode" attributeEntry="${budgetIndirectCostAttributes.budgetPurposeCode}" readOnly="${viewOnly}"/>
              	<%--	
              		<c:set var="finderClass" value="${fn:replace(budgetIndirectCostAttributes.budgetPurposeCode.control.valuesFinder,'.','|')}"/>
              		<%-- <c:out value="validOptionsMap.${finderClass[document.budget.indirectCost.budgetPurposeCode]}.label"/> 
              		${not empty KualiForm.validOptionsMap} ${finderClass}
              		
              	</kul:htmlControlAttribute>
              	--%>
              </td>
              <th width="20%" rowspan="2" align="right"  >Indirect Cost on Institution Cost Share:</th>

              <td width="30%" rowspan="2"   >
                <kul:htmlControlAttribute property="document.budget.indirectCost.budgetIndirectCostCostShareIndicator" attributeEntry="${budgetIndirectCostAttributes.budgetIndirectCostCostShareIndicator}" disabled="${!KualiForm.document.budget.institutionCostShareIndicator}" readOnly="${viewOnly}"/>
                Include <kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetIndirectCost.attributes.budgetIndirectCostCostShareIndicator}" skipHelpUrl="true" noColon="true" /><br/>
                <kul:htmlControlAttribute property="document.budget.indirectCost.budgetUnrecoveredIndirectCostIndicator" attributeEntry="${budgetIndirectCostAttributes.budgetUnrecoveredIndirectCostIndicator}" disabled="${!KualiForm.document.budget.institutionCostShareIndicator}" readOnly="${viewOnly}"/>
                Include <kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetIndirectCost.attributes.budgetUnrecoveredIndirectCostIndicator}" skipHelpUrl="true" noColon="true" /></td>
            </tr>
            <tr>
              <th width="20%" align="right"  ><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetIndirectCost.attributes.budgetBaseCode}" skipHelpUrl="true" /></th>

              <td width="30%" >
              	<kul:htmlControlAttribute property="document.budget.indirectCost.budgetBaseCode" attributeEntry="${budgetIndirectCostAttributes.budgetBaseCode}" readOnly="${viewOnly}"/>
              </td>
            </tr>

            <tr>
              <th width="20%" align="right"  ><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetIndirectCost.attributes.budgetManualRateIndicator}" skipHelpUrl="true" /></th>
              <td width="30%" >
                <kul:htmlControlAttribute property="document.budget.indirectCost.budgetManualRateIndicator" attributeEntry="${budgetIndirectCostAttributes.budgetManualRateIndicator}" readOnly="${viewOnly}"/>
              </td>

              <th width="20%" align="right"  ><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetIndirectCost.attributes.budgetIndirectCostJustificationText}" skipHelpUrl="true" /></th>
              <td width="30%" >
              <kul:htmlControlAttribute property="document.budget.indirectCost.budgetIndirectCostJustificationText" attributeEntry="${budgetIndirectCostAttributes.budgetIndirectCostJustificationText}" readOnly="${viewOnly}"/>
              </td>
            </tr>
          </table>
          
          <c:if test="${not viewOnly}">
          <br/>
          <html:image src="images/tinybutton-recalculate.gif" styleClass="tinybutton" property="methodToCall.recalculate" alt="recalculate"/>
          </c:if>
          <br/>
          <br/>
          
          <div class="h2-container"><h2>View Indirect Costs</h2></div>
          
<!--  Task/Period section of IDC lists tasks and their costs for all periods covered by the budget. -->
          <table class="datatable" summary="" cellpadding="0" cellspacing="0">
            
<!--  Headers for bottom portion of IDC form. -->
            <tr>
              <th rowspan="2" align="center" >&nbsp;</th>
              <td colspan="4" align="center" class="tab-subhead"><div align="center"><b>Agency Amount Requested</b></div></td>
              <td width="10" align="center" >&nbsp;</td>
              <td colspan="4" align="center" class="tab-subhead"><div align="center"><b>Indirect Cost on Institution Cost Share</b> </div></td>
            </tr>
            <tr>
              <th width="7%" align="center" >TDC </th>
              <th width="10%"align="center" >Base </th>
              <th width="7%" align="center" >ICR</th>
              <th width="7%" align="center" >Calculated Indirect Cost </th>
              <td width="10" align="center" >&nbsp;</td>
              <th width="7%" align="center" >Base </th>
              <th width="7%" align="center" >ICR</th>
              <th width="7%" align="center" >Calculated Indirect Cost </th>
              <th width="7%" align="center" ><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetIndirectCost.attributes.budgetUnrecoveredIndirectCostIndicator}" skipHelpUrl="true" noColon="true" /></th>
            </tr>
         
<!--  Begin iterating over tasks.  For each task, iterate over associated periods.  -->
<c:set var="totalsIter" value="0"/>
<logic:iterate id="taskPeriodLine" name="KualiForm" property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItems" indexId="ctr">

<kul:htmlControlAttribute property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].documentNumber" attributeEntry="${budgetTaskPeriodIndirectCostAttributes.documentNumber}"/>
<kul:htmlControlAttribute property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].objectId" attributeEntry="${budgetTaskPeriodIndirectCostAttributes.objectId}"/>
<kul:htmlControlAttribute property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].versionNumber" attributeEntry="${budgetTaskPeriodIndirectCostAttributes.versionNumber}"/>
<kul:htmlControlAttribute property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].budgetTaskSequenceNumber" attributeEntry="${budgetTaskPeriodIndirectCostAttributes.budgetTaskSequenceNumber}"/>
<kul:htmlControlAttribute property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].budgetPeriodSequenceNumber" attributeEntry="${budgetTaskPeriodIndirectCostAttributes.budgetPeriodSequenceNumber}"/>
<kul:htmlControlAttribute property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].budgetTaskSequenceNumber" attributeEntry="${budgetTaskPeriodIndirectCostAttributes.budgetTaskSequenceNumber}"/>
<kul:htmlControlAttribute property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].budgetPeriodSequenceNumber" attributeEntry="${budgetTaskPeriodIndirectCostAttributes.budgetPeriodSequenceNumber}"/>
<html:hidden property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].indirectCostRate" />
<html:hidden property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].totalDirectCost" />
<html:hidden property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].calculatedIndirectCost" />
<html:hidden property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].baseCost" />
<html:hidden property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].costShareBaseCost" />
<html:hidden property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].costShareIndirectCostRate" />
<html:hidden property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].costShareCalculatedIndirectCost" />
<html:hidden property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].costShareUnrecoveredIndirectCost" />
<html:hidden property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].task.budgetTaskName" />
<html:hidden property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].period.budgetPeriodBeginDate" />
<html:hidden property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].period.budgetPeriodEndDate" />

 
 <c:if test="${empty taskName or taskName ne taskPeriodLine.task.budgetTaskName}">
 
    <c:set var="taskName" value="${taskPeriodLine.task.budgetTaskName}"/>
	<tr>
	  <td colspan="5" class="tab-subhead"><b>${taskName}</b> </td>
      <td width="10" align="center" >&nbsp;</td>
	  <td colspan="4" class="tab-subhead">&nbsp;</td>
	</tr>
	<c:set var="periodNum" value="1"/>
	
 </c:if>
            
<tr>
  <th ><div align="center" class="nowrap"><strong>Period ${periodNum}</strong> <span class="fineprint"><br/><c:set var="periodNum" value="${periodNum + 1}"/>
    (<fmt:formatDate value="${taskPeriodLine.period.budgetPeriodBeginDate}" dateStyle="short"/> - <fmt:formatDate value="${taskPeriodLine.period.budgetPeriodEndDate}" dateStyle="short"/>)</span></div></th>
  <td align="right" >
  	<div align="right">
  		<fmt:formatNumber value="${taskPeriodLine.totalDirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
  	</div>
  </td>
  <td align="right">
  	<div align="right">
			<c:choose>
				<c:when test="${KualiForm.document.budget.indirectCost.budgetBaseCode eq 'MN'}">
				  <kul:htmlControlAttribute property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].budgetManualMtdcAmount" attributeEntry="${budgetTaskPeriodIndirectCostAttributes.budgetManualMtdcAmount}" readOnly="${viewOnly}" styleClass="amount"/>
				</c:when>
				<c:otherwise>
					<fmt:formatNumber value="${taskPeriodLine.baseCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
					<html:hidden property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].budgetManualMtdcAmount" />
				</c:otherwise>
			</c:choose>
  	</div>
  </td>
  <td align="center" >
  	<div align="right">
			<c:choose>
				<c:when test="${KualiForm.document.budget.indirectCost.budgetManualRateIndicator eq 'Y'}">
				  <kul:htmlControlAttribute property="document.budget.indirectCost.budgetTaskPeriodIndirectCostItem[${ctr}].budgetManualIndirectCostRate" attributeEntry="${budgetTaskPeriodIndirectCostAttributes.budgetManualIndirectCostRate}" readOnly="${viewOnly}" styleClass="amount"/>%
				</c:when>
				<c:otherwise>
          ${taskPeriodLine.indirectCostRate}%
				</c:otherwise>
			</c:choose>
  	</div>
  </td>
  <td align="right" >
  	<div align="right">
  		<fmt:formatNumber value="${taskPeriodLine.calculatedIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
  	</div>
  </td>  
  <td width="10" align="center" >&nbsp;</td>
  
    <c:choose>
  		<c:when test="${KualiForm.document.budget.indirectCost.budgetIndirectCostCostShareIndicator}">
  		
  <td align="right" ><div align="right">
  		<fmt:formatNumber value="${taskPeriodLine.costShareBaseCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
  </div></td>
  <td align="right" ><div align="right">
		${taskPeriodLine.costShareIndirectCostRate}%
  </div></td>
  <td align="right" ><div align="right">
    <c:if test="${KualiForm.document.budget.indirectCost.budgetIndirectCostCostShareIndicator}">
	  <fmt:formatNumber value="${taskPeriodLine.costShareCalculatedIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
	</c:if>
  </div></td>
  <td align="right" ><div align="right">
  	<c:choose>
  		<c:when test="${KualiForm.document.budget.indirectCost.budgetUnrecoveredIndirectCostIndicator}">
  			<fmt:formatNumber value="${taskPeriodLine.costShareUnrecoveredIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
		</c:when>
		<c:otherwise>&nbsp;</c:otherwise>
	</c:choose>
  </div></td>
  
          		</c:when>
		<c:otherwise>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</c:otherwise>
	</c:choose>  
	
</tr>



<!-- Display subtotal for each task after last period line. -->
<html:hidden name="KualiForm" property="budgetIndirectCostFormHelper.numPeriods"/>
<c:if test="${ctr % KualiForm.budgetIndirectCostFormHelper.numPeriods eq (KualiForm.budgetIndirectCostFormHelper.numPeriods - 1)}">

		<tr>
          <th ><div align="right">&nbsp;Subtotal: </div></th>
          <td align="right" class="infoline"><div align="right"><b>
          	<fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.taskTotals[totalsIter].totalDirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
          	<html:hidden property="budgetIndirectCostFormHelper.taskTotals[${totalsIter}].totalDirectCost"/>
          </b></div></td>
          <td align="right" class="infoline"><div align="right"><b>
          	<fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.taskTotals[totalsIter].baseCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
          </b></div></td>
          <td class="infoline">&nbsp;</td>
          <td align="right" class="infoline"><div align="right"><b>
          	<fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.taskTotals[totalsIter].calculatedIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
          </b></div></td>
          <td width="10" align="center" >&nbsp;</td>
          
        
  	<c:choose>
  		<c:when test="${KualiForm.document.budget.indirectCost.budgetIndirectCostCostShareIndicator}">    
  		
          <td align="right" class="infoline"><div align="right"><b>
          	<fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.taskTotals[totalsIter].costShareBaseCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
          </b></div></td>
          <td class="infoline">&nbsp;</td>
          <td align="right" class="infoline"><div align="right"><b>
          		<fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.taskTotals[totalsIter].costShareCalculatedIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
          </b></div></td>
          <td align="right" class="infoline"><div align="right"><b>
            <c:choose>
            	<c:when test="${KualiForm.document.budget.indirectCost.budgetUnrecoveredIndirectCostIndicator}">
          			<fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.taskTotals[totalsIter].costShareUnrecoveredIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
				</c:when>
				<c:otherwise>&nbsp;</c:otherwise>
			</c:choose>
          </b></div></td>
          
        </c:when>
		<c:otherwise>
			<td class="infoline">&nbsp;</td>
			<td class="infoline">&nbsp;</td>
			<td class="infoline">&nbsp;</td>
			<td class="infoline">&nbsp;</td>
		</c:otherwise>
	</c:choose>  
	  
        </tr> 
        
        <c:set var="totalsIter" value="${totalsIter + 1}"/>
</c:if>
           
</logic:iterate>
<!-- Finish task loop. -->       



<!-- Display period summaries -->
<tr>
  <td colspan="5" class="tab-subhead"><b>Summary</b> </td>
  <td width="10" align="center" >&nbsp;</td>
  <td colspan="4" class="tab-subhead">&nbsp;</td>
</tr>



<!-- Loop over period totals and display relevant information. -->
<logic:iterate id="periodTotal" name="KualiForm" property="budgetIndirectCostFormHelper.periodTotals" indexId="ctr">
		
<tr>
  <th><div align="center" class="nowrap"><strong>Period ${ctr + 1}</strong> <span class="fineprint"><br/>
    (<fmt:formatDate value="${periodTotal.period.budgetPeriodBeginDate}" dateStyle="short"/> - <fmt:formatDate value="${periodTotal.period.budgetPeriodEndDate}" dateStyle="short"/>)</span></div></th>
  <td align="right" >
  	<div align="right">
  		<fmt:formatNumber value="${periodTotal.totalDirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
  	</div>
  </td>
  <td align="right">
  	<div align="right">
		<fmt:formatNumber value="${periodTotal.baseCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
  	</div>
  </td>
  <td align="center" ><div align="right">&nbsp;</div></td>
  <td align="right" >
  	<div align="right">
  		<fmt:formatNumber value="${periodTotal.calculatedIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
  	</div>
  </td>  
  <td width="10" align="center" ><div align="right">&nbsp;</div></td>
  
  	<c:choose>
  		<c:when test="${KualiForm.document.budget.indirectCost.budgetIndirectCostCostShareIndicator}">
			<td align="right" ><div align="right">
					<fmt:formatNumber value="${periodTotal.costShareBaseCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
			</div></td>
			<td align="right"><div align="right">&nbsp;</div></td>
			<td align="right" ><div align="right">
			<fmt:formatNumber value="${periodTotal.costShareCalculatedIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
			</div></td>
			<td align="right" ><div align="right">
				<c:choose>
					<c:when test="${KualiForm.document.budget.indirectCost.budgetUnrecoveredIndirectCostIndicator}">
						<fmt:formatNumber value="${periodTotal.costShareUnrecoveredIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
					</c:when>
					<c:otherwise>&nbsp;</c:otherwise>
				</c:choose>
			</div></td>
		</c:when>
		<c:otherwise>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</c:otherwise>
	</c:choose>  
</tr>

    </logic:iterate>
    


<!-- Display period subtotal line. -->
    	<tr>
          <th ><div align="right">&nbsp;Subtotal: </div></th>
          <td align="right" class="infoline"><div align="right"><b>
          	<fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.periodSubTotal.totalDirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
          </b></div></td>
          <td align="right" class="infoline"><div align="right"><b>
          	<fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.periodSubTotal.baseCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
          </b></div></td>
          <td class="infoline">&nbsp;</td>
          <td align="right" class="infoline"><div align="right"><b>
          	<fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.periodSubTotal.calculatedIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
          </b></div></td>
          <td width="10" align="center" >&nbsp;</td>
          	
          	<c:choose>
	          	<c:when test="${KualiForm.document.budget.indirectCost.budgetIndirectCostCostShareIndicator}">
		          <td align="right" class="infoline"><div align="right"><b>
		          	<fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.periodSubTotal.costShareBaseCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
		          </b></div></td>
		          <td class="infoline">&nbsp;</td>
		          <td align="right" class="infoline"><div align="right"><b>
					
						<fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.periodSubTotal.costShareCalculatedIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
					
		          </b></div></td>
		          <td align="right" class="infoline"><div align="right"><b>
                <c:choose>
                  <c:when test="${KualiForm.document.budget.indirectCost.budgetUnrecoveredIndirectCostIndicator}">
                    <fmt:formatNumber value="${KualiForm.budgetIndirectCostFormHelper.periodSubTotal.costShareUnrecoveredIndirectCost}" type="currency" currencySymbol="$" maxFractionDigits="0" />
                  </c:when>
                  <c:otherwise>&nbsp;</c:otherwise>
                </c:choose>
              </b></div></td>
				</c:when>
				<c:otherwise>
				<td class="infoline">&nbsp;</td>
				<td class="infoline">&nbsp;</td>
				<td class="infoline">&nbsp;</td>
				<td class="infoline">&nbsp;</td>
				</c:otherwise>
			</c:choose>        
          
        </tr> 
    
    
    
  </table>
</div>
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
  <tr>
    <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"/></td>
    <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"/></td>
  </tr>
</table>



      
  
</div> 