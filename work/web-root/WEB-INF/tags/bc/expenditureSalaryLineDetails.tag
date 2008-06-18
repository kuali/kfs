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

<%@ attribute name="readOnly" required="false" description="determine whether the contents can be read only or not"%>

<c:set var="bciiAttributes" value="${DataDictionary['BudgetConstructionIntendedIncumbent'].attributes}" />
<c:set var="bccsftAttributes" value="${DataDictionary['BudgetConstructionCalculatedSalaryFoundationTracker'].attributes}" />
<c:set var="pbcafAttributes" value="${DataDictionary['PendingBudgetConstructionAppointmentFunding'].attributes}" />
<c:set var="bcpAttributes" value="${DataDictionary['BudgetConstructionPosition'].attributes}" />
<c:set var="fundingPropertyName" value="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding"/>
	
<div class="h2-container">
	<h2><span class="subhead-left"><span class="tabtable1-left">Salary Line Detail</span></span></h2>
	
	<c:if test="${not readOnly}">
	    <span class="subhead-right"><span class="subhead">
	    	<html:hidden property="hideAdjustmentMeasurement"/>
	    	
	    	<c:set var="hideOrShow" value="${KualiForm.hideAdjustmentMeasurement ? 'show' : 'hide'}" />
	    	<html:image property="methodToCall.toggleAdjustmentMeasurement" 
				src="${ConfigProperties.externalizable.images.url}tinybutton-${hideOrShow}adjust.gif" 
				alt="${hideOrShow} percent adjustment" title="${hideOrShow} percent adjustment"
				styleClass="tinybutton" />	
	    </span></span>
    </c:if>
</div>
						
<table cellpadding="0" cellspacing="0" class="datatable" summary="Expenditure Salary Line Detail">	
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentFundingDeleteIndicator}"/>
				
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.positionNumber}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bciiAttributes.personName}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bciiAttributes.iuClassificationLevel}" />
		
		<kul:htmlAttributeHeaderCell attributeEntry="${bcpAttributes.positionSalaryPlanDefault}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bcpAttributes.positionGradeDefault}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bcpAttributes.iuNormalWorkMonths}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bcpAttributes.iuPayMonths}" />
		
		<kul:htmlAttributeHeaderCell attributeEntry="${bccsftAttributes.csfAmount}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bccsftAttributes.csfFullTimeEmploymentQuantity}" />
		
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentFundingMonth}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentRequestedPayRate}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentRequestedAmount}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentRequestedFteQuantity}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.percentChange}" />
		
		<kul:htmlAttributeHeaderCell literalLabel="Actions"/>		
	</tr>
	
	<c:forEach items="${KualiForm.pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding}" var="fundingLine" varStatus="status">
	<c:set var="fundingLineName" value="${fundingPropertyName}[${status.index}]"/>	
	<c:set var="isVacant" value="${fundingLine.emplid eq BCConstants.VACANT_EMPLID}" />
	<c:set var="hidePercentAdjustment" value="${fundingLine.appointmentFundingDeleteIndicator || KualiForm.hideAdjustmentMeasurement || readOnly}" />
	<c:set var="rowspan" value="${ hidePercentAdjustment ? 1: 2}"/>
	
	<tr>		
		<%-- Appointment Funding Delete Indicator --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell"
			accountingLine="${fundingLineName}"	attributes="${pbcafAttributes}" 
			field="appointmentFundingDeleteIndicator"  rowSpan="${rowspan}" readOnly="false"
			fieldAlign="left" disabled="${readOnly}">
			
			<html:hidden property="${fundingLineName}.universityFiscalYear" />
			<html:hidden property="${fundingLineName}.chartOfAccountsCode" />
			<html:hidden property="${fundingLineName}.accountNumber" />
			<html:hidden property="${fundingLineName}.subAccountNumber" />
			<html:hidden property="${fundingLineName}.financialObjectCode" />
			<html:hidden property="${fundingLineName}.financialSubObjectCode" />
			<html:hidden property="${fundingLineName}.versionNumber" />
			<html:hidden property="${fundingLineName}.emplid" />
		</bc:pbglLineDataCell>
	
		<%-- Position Number --%>	
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${fundingLineName}" attributes="${pbcafAttributes}" 
			field="positionNumber" inquiry="true"
			lookupOrInquiryKeys="positionNumber,universityFiscalYear" 
			boClassSimpleName="BudgetConstructionPosition" boPackageName="org.kuali.module.budget.bo" 
			accountingLineValuesMap="${fundingLine.valuesMap}" fieldAlign="left" readOnly="true" rowSpan="${rowspan}"
			anchor="salaryexistingLineLineAnchor${status.index}" />
	
		<c:choose>
			<c:when test="${not isVacant}">					
				<bc:pbglLineDataCell dataCellCssClass="datacell"
					accountingLine="${fundingLineName}"
					field="emplid" detailFunction="loadEmplInfo"
					detailField="budgetConstructionIntendedIncumbent.personName"
					attributes="${pbcafAttributes}" inquiry="true"
					boClassSimpleName="BudgetConstructionIntendedIncumbent"
					boPackageName="org.kuali.module.budget.bo"
					readOnly="true"	displayHidden="false"
					lookupOrInquiryKeys="emplid" rowSpan="${rowspan}" 
					accountingLineValuesMap="${fundingLine.valuesMap}"/>					

			<%-- Classification Level --%>
				<bc:pbglLineDataCell dataCellCssClass="datacell" 
					accountingLine="${fundingLineName}.budgetConstructionIntendedIncumbent"	attributes="${bciiAttributes}" 
					field="iuClassificationLevel" fieldAlign="left" readOnly="true" rowSpan="${rowspan}" />
			</c:when>
			<c:otherwise>
				<bc:pbglLineDataCell dataCellCssClass="datacell" 
					accountingLine="${fundingLineName}.budgetConstructionIntendedIncumbent" attributes="${bciiAttributes}"
					field="personName"
					readOnly="true" formattedNumberValue="${KFSConstants.BudgetConstructionConstants.VACANT_EMPLID}" 
					displayHidden="false" rowSpan="${rowspan}" />
				
				<td rowspan="${rowspan}">&nbsp;</td>
			</c:otherwise>
		</c:choose>
		
		<%-- Salary Plan Default --%>
		<c:choose>
			<c:when test="${!empty fundingLine.budgetConstructionPosition.positionSalaryPlanDefault}">
				<bc:pbglLineDataCell dataCellCssClass="datacell" 
					accountingLine="${fundingLineName}.budgetConstructionPosition"
					attributes="${bcpAttributes}" field="positionSalaryPlanDefault"
					fieldAlign="left" readOnly="true" rowSpan="${rowspan}" />
			</c:when>
			<c:otherwise>
				<td rowSpan="${rowspan}">&nbsp;</td>
			</c:otherwise>
		</c:choose>

		<%-- Position Grade Default --%>
		<c:choose>
			<c:when test="${!empty fundingLine.budgetConstructionPosition.positionGradeDefault}">

				<bc:pbglLineDataCell dataCellCssClass="infocell" 
					accountingLine="${fundingLineName}.budgetConstructionPosition" 
					attributes="${bcpAttributes}" field="positionGradeDefault" fieldAlign="left"
					readOnly="true" rowSpan="${rowspan}" />
			</c:when>
			<c:otherwise>
				<td rowSpan="${rowspan}">&nbsp;</td>
			</c:otherwise>
		</c:choose>

		<%-- IU Normal Work Months --%>
		<bc:pbglLineDataCell dataCellCssClass="infocell" 
			accountingLine="${fundingLineName}.budgetConstructionPosition" 
			attributes="${bcpAttributes}" field="iuNormalWorkMonths" fieldAlign="right"
			readOnly="true" rowSpan="${rowspan}" />

		<%-- IU Pay Months --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${fundingLineName}.budgetConstructionPosition"
			attributes="${bcpAttributes}" field="iuPayMonths" fieldAlign="right" readOnly="true" rowSpan="${rowspan}" />
				
		<%-- csf Amount --%>
		<c:choose>
			<c:when test="${!empty fundingLine.bcnCalculatedSalaryFoundationTracker}">
				<bc:pbglLineDataCell dataCellCssClass="datacell" 
					accountingLine="${fundingLineName}.bcnCalculatedSalaryFoundationTracker[0]"
					attributes="${pbcafAttributes}" field="csfAmount" csfInquiry="true"
					lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,financialSubObjectCode,positionNumber,emplid" 
					boClassSimpleName="BudgetConstructionCalculatedSalaryFoundationTracker"
					boPackageName="org.kuali.module.budget.bo" 
					accountingLineValuesMap="${fundingLine.valuesMap}" fieldAlign="right" readOnly="true" />
			</c:when>
			<c:otherwise>
				<td>&nbsp;</td>
			</c:otherwise>
		</c:choose>

		<!-- csf Full Time Employment Quantity -->
		<c:choose>
			<c:when test="${!empty fundingLine.bcnCalculatedSalaryFoundationTracker}">
				<fmt:formatNumber value="${fundingLine.bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity}" 
					var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="4" />
								
				<bc:pbglLineDataCell dataCellCssClass="datacell" 
					accountingLine="${fundingLineName}.bcnCalculatedSalaryFoundationTracker[0]" 
					attributes="${bccsftAttributes}" field="csfFullTimeEmploymentQuantity" fieldAlign="right" 
					formattedNumberValue="${formattedNumber}" readOnly="true" dataFieldCssClass="amount" />
			</c:when>
			<c:otherwise>
				<td>&nbsp;</td>
			</c:otherwise>
		</c:choose>

		<%-- Appointment Funding Month --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${fundingLineName}" 
			attributes="${pbcafAttributes}" field="appointmentFundingMonth" fieldAlign="right" readOnly="true" />
			
		<!-- appointment requested hourly rate -->
        <fmt:formatNumber value="${fundingLine.appointmentRequestedPayRate}" 
        	var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />	

		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${fundingLineName}"	attributes="${pbcafAttributes}" 
			field="appointmentRequestedPayRate" fieldAlign="right"
			formattedNumberValue="${formattedNumber}" readOnly="${readOnly}" dataFieldCssClass="amount" />			

		<%-- Appointment Requested Amount --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${fundingLineName}" attributes="${pbcafAttributes}" 
			field="appointmentRequestedAmount" fieldAlign="right" readOnly="${readOnly}"
			rowSpan="1" dataFieldCssClass="amount">
		</bc:pbglLineDataCell>
		        				
		<!-- appointment Requested Fte Quantity -->
        <fmt:formatNumber value="${fundingLine.appointmentRequestedFteQuantity}" 
        	var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="4" />	

		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${fundingLineName}" attributes="${pbcafAttributes}" 
			field="appointmentRequestedFteQuantity" fieldAlign="right"
			formattedNumberValue="${formattedNumber}" readOnly="true" dataFieldCssClass="amount" />

		<%-- Percent Change --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${fundingLineName}"
			attributes="${pbcafAttributes}" field="percentChange" fieldAlign="right" readOnly="true" />

		<td class="datacell" rowspan="${rowspan}" nowrap>
			<c:if test="${not readOnly}">
				<html:image 
					property="methodToCall.performPositionSalarySetting.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
					src="${ConfigProperties.externalizable.images.url}tinybutton-posnsalset.gif" 
					title="Position Salary Setting For Line ${status.index}"
					alt="Position Salary Setting For Line ${status.index}" 
					styleClass="tinybutton" />
				
				<c:if test="${not isVacant}">
					<html:image property="methodToCall.performIncumbentSalarySetting.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-incmbntsalset.gif" 
						title="Incumbent Salary Setting For Line ${status.index}"
						alt="Incumbent Salary Setting For Line ${status.index}" styleClass="tinybutton" />
				</c:if>
				
				<c:if test="${fundingLine.vacatable}">
					<br/>
					<html:image property="methodToCall.vacateSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-vacate.gif" 
						title="Vacate Salary Setting Line ${status.index}"
						alt="Vacate Salary Setting Line ${status.index}" styleClass="tinybutton" />
				</c:if>
			</c:if>
		</td>
	</tr>
	
	<c:if test="${not hidePercentAdjustment}">
	<tr>
		<td colspan ="7" class="infoline"><center>
			<bc:salaryAdjustment attributes="${pbcafAttributes}" 
				adjustmentMeasurementFieldName="${fundingLineName}.adjustmentMeasurement" 
				adjustmentAmountFieldName="${fundingLineName}.adjustmentAmount"
				methodToCall = "adjustSalarySettingLinePercent"
				lineIndex = "${status.index}"/>
			</center>
		</td>
	</tr>
	</c:if>
	</c:forEach>
		
	<tr>
		<kul:htmlAttributeHeaderCell scope="row" colspan="8" literalLabel="Total:" horizontal="true" />
	
	    <bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1"
	    	cellProperty="pendingBudgetConstructionGeneralLedger.csfAmountTotal" />
	
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1" 
			cellProperty="pendingBudgetConstructionGeneralLedger.csfFullTimeEmploymentQuantityTotal" />
	
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="3" 
			cellProperty="pendingBudgetConstructionGeneralLedger.appointmentRequestedAmountTotal" />
	
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1" 
			cellProperty="pendingBudgetConstructionGeneralLedger.appointmentRequestedFteQuantityTotal" />
	
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1" 
			cellProperty="pendingBudgetConstructionGeneralLedger.percentChangeTotal" />
		
		<kul:htmlAttributeHeaderCell />
	</tr>
	
	<tr>
		<kul:htmlAttributeHeaderCell scope="row" colspan="8" literalLabel="Expenditure Line Base:" horizontal="true" />
	
	    <bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1"
	    	cellProperty="pendingBudgetConstructionGeneralLedger.financialBeginningBalanceLineAmount" />
	
		<kul:htmlAttributeHeaderCell nowrap="true" literalLabel="Req:" horizontal="true" />
	
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="3" 
			cellProperty="pendingBudgetConstructionGeneralLedger.appointmentRequestedAmountTotal" />
	
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="2" 
			cellProperty="pendingBudgetConstructionGeneralLedger.percentChangeTotal" />
		
		<kul:htmlAttributeHeaderCell />
	</tr>
	
	<c:if test="${not readOnly}">
	<tr>
		<td colspan="16">
			<div class="h2-container" style="width: 100%;"><h2>Global Percent Adjustment</h2></div>
		</td>
	</tr>
	
	<tr>
		<kul:htmlAttributeHeaderCell scope="row" colspan="8" literalLabel="" horizontal="true" />
	
	    <td colspan ="7"><center>
			<bc:salaryAdjustment attributes="${pbcafAttributes}" 
				adjustmentMeasurementFieldName="adjustmentMeasurement" 
				adjustmentAmountFieldName="adjustmentAmount"
				methodToCall="adjustAllSalarySettingLinesPercent"/>
			</center>
		</td>
		
		<kul:htmlAttributeHeaderCell />
	</tr>
	</c:if>
		
</table>
