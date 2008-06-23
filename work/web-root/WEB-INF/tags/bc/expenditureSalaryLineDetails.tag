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
<c:set var="fundingPropertyName" value="salarySettingExpansion.pendingBudgetConstructionAppointmentFunding"/>
<c:set var="isHourlyPaid" value="${true or KualiForm.salarySettingExpansion.hourlyPaid}" />
<c:set var="numOfColumsRemoved" value="${isHourlyPaid ? 0 : 1 }" />
	
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
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.emplid}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bciiAttributes.iuClassificationLevel}"/>
		
		<kul:htmlAttributeHeaderCell attributeEntry="${bcpAttributes.positionSalaryPlanDefault}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bcpAttributes.positionGradeDefault}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bcpAttributes.iuNormalWorkMonths}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bcpAttributes.iuPayMonths}" />
		
		<kul:htmlAttributeHeaderCell attributeEntry="${bccsftAttributes.csfAmount}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${bccsftAttributes.csfFullTimeEmploymentQuantity}" />
		
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentFundingMonth}" />
		
		<c:if test="${isHourlyPaid}">
			<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentRequestedPayRate}" />
		</c:if>
		
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentRequestedAmount}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentRequestedFteQuantity}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.percentChange}" />
		
		<kul:htmlAttributeHeaderCell literalLabel="Actions"/>		
	</tr>

	<c:forEach items="${KualiForm.salarySettingExpansion.pendingBudgetConstructionAppointmentFunding}" var="fundingLine" varStatus="status">
	<c:set var="fundingLineName" value="${fundingPropertyName}[${status.index}]"/>	
	<c:set var="isVacant" value="${fundingLine.emplid eq BCConstants.VACANT_EMPLID}" />
	<c:set var="hidePercentAdjustment" value="${fundingLine.appointmentFundingDeleteIndicator || KualiForm.hideAdjustmentMeasurement || readOnly}" />
	<c:set var="notEditable" value="${readOnly || fundingLine.persistedDeleteIndicator}"/>
	<c:set var="rowspan" value="${ hidePercentAdjustment ? 1: 2}"/>
	
	<tr>		
		<%-- Appointment Funding Delete Indicator --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell"
			accountingLine="${fundingLineName}"	attributes="${pbcafAttributes}" 
			field="appointmentFundingDeleteIndicator" rowSpan="${rowspan}" readOnly="false"
			fieldAlign="left" disabled="${notEditable}">
			
			<html:hidden property="${fundingLineName}.universityFiscalYear" />
			<html:hidden property="${fundingLineName}.chartOfAccountsCode" />
			<html:hidden property="${fundingLineName}.accountNumber" />
			<html:hidden property="${fundingLineName}.subAccountNumber" />
			<html:hidden property="${fundingLineName}.financialObjectCode" />
			<html:hidden property="${fundingLineName}.financialSubObjectCode" />
			<html:hidden property="${fundingLineName}.emplid" />
			<html:hidden property="${fundingLineName}.appointmentRequestedTimePercent" />
			<html:hidden property="${fundingLineName}.persistedDeleteIndicator" />
			<html:hidden property="${fundingLineName}.versionNumber" />
			
			<c:if test="${notEditable}">
				<html:hidden property="${fundingLineName}.appointmentFundingDeleteIndicator" />
			</c:if>
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
					
				<td class="datacell" rowspan="${rowspan}">${fundingLine.budgetConstructionIntendedIncumbent.iuClassificationLevel}</td>						
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
		<td class="datacell" rowSpan="${rowspan}">${fundingLine.budgetConstructionPosition.positionSalaryPlanDefault}</td>

		<%-- Position Grade Default --%>
		<td class="datacell" rowSpan="${rowspan}">${fundingLine.budgetConstructionPosition.positionGradeDefault}</td>

		<%-- IU Normal Work Months --%>
		<td class="datacell" rowSpan="${rowspan}">${fundingLine.budgetConstructionPosition.iuNormalWorkMonths}</td>

		<%-- IU Pay Months --%>
		<td class="datacell" rowSpan="${rowspan}">${fundingLine.budgetConstructionPosition.iuPayMonths}</td>
				
		<%-- csf Amount --%>
		<c:choose>
			<c:when test="${!empty fundingLine.bcnCalculatedSalaryFoundationTracker}">	
				<fmt:formatNumber value="${fundingLine.bcnCalculatedSalaryFoundationTracker[0].csfAmount}" 
					var="formattedCsfAmount" type="number" groupingUsed="true"/>		
				<td class="datacell" style="text-align: right;">
					<a href="${ConfigProperties.application.url}/budgetTempListLookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker&universityFiscalYear=${KualiForm.universityFiscalYear}&chartOfAccountsCode=${KualiForm.chartOfAccountsCode}&accountNumber=${KualiForm.accountNumber}&subAccountNumber=${KualiForm.subAccountNumber}&hideReturnLink=true&suppressActions=true&tempListLookupMode=6&showInitialResults=true&docFormKey=${KualiForm.returnFormKey}&backLocation=${KualiForm.backLocation}"  target="_blank">
    					${formattedCsfAmount}
    				</a>
    			</td>
				
				<fmt:formatNumber value="${fundingLine.bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity}" 
					var="formattedCsfFTE" type="number" groupingUsed="true" minFractionDigits="4" />
				<td class="datacell" style="text-align: right;">${formattedCsfFTE}</td>
			</c:when>
			<c:otherwise>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</c:otherwise>
		</c:choose>

		<%-- Appointment Funding Month --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${fundingLineName}" 
			attributes="${pbcafAttributes}" field="appointmentFundingMonth" fieldAlign="right" readOnly="true" />
			
		<!-- appointment requested hourly rate -->
		<c:if test="${isHourlyPaid}">
	        <fmt:formatNumber value="${fundingLine.appointmentRequestedPayRate}" 
	        	var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />	
	
			<bc:pbglLineDataCell dataCellCssClass="datacell" 
				accountingLine="${fundingLineName}"	attributes="${pbcafAttributes}" 
				field="appointmentRequestedPayRate" fieldAlign="right"
				formattedNumberValue="${formattedNumber}" readOnly="${notEditable}" dataFieldCssClass="amount"/>
		</c:if>		

		<%-- Appointment Requested Amount --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${fundingLineName}" attributes="${pbcafAttributes}" 
			field="appointmentRequestedAmount" fieldAlign="right" readOnly="${notEditable}"
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

		<td class="datacell" rowspan="${rowspan}" style="white-space: nowrap;">
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
				
				<c:if test="${isHourlyPaid && not notEditable}">	
					<html:image property="methodToCall.normalizePayRateAndAmount.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
						src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif" 
						title="Normalize the hourly rate and annual amount for Salary Setting Line ${status.index}"
						alt="Normalize the hourly rate and annual amount for Salary Setting Line ${status.index}" styleClass="tinybutton" />
				</c:if>
			</c:if>
		</td>
	</tr>
	
	<c:if test="${not hidePercentAdjustment}">
	<tr>
		<td colspan ="${7 - numOfColumsRemoved}" class="infoline"><center>
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
	    	cellProperty="salarySettingExpansion.csfAmountTotal" />
	
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1" 
			cellProperty="salarySettingExpansion.csfFullTimeEmploymentQuantityTotal" />
	
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="${3 - numOfColumsRemoved}" 
			cellProperty="salarySettingExpansion.appointmentRequestedAmountTotal" />
	
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1" 
			cellProperty="salarySettingExpansion.appointmentRequestedFteQuantityTotal" />
	
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1" 
			cellProperty="salarySettingExpansion.percentChangeTotal" />
		
		<kul:htmlAttributeHeaderCell />
	</tr>
	
	<tr>
		<kul:htmlAttributeHeaderCell scope="row" colspan="8" literalLabel="Expenditure Line Base:" horizontal="true" />
	
	    <bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="1"
	    	cellProperty="salarySettingExpansion.financialBeginningBalanceLineAmount" />
	
		<kul:htmlAttributeHeaderCell nowrap="true" literalLabel="Req:" horizontal="true" />
	
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="${3 - numOfColumsRemoved}"  
			cellProperty="salarySettingExpansion.accountLineAnnualBalanceAmount" />
	
		<bc:columnTotalCell dataCellCssClass="datacell" textStyle="${textStyle}" fieldAlign="right" colSpan="2" 
			cellProperty="salarySettingExpansion.percentChange" />
		
		<kul:htmlAttributeHeaderCell />
	</tr>
	
	<c:if test="${not readOnly}">
	<tr>
		<td colspan="${16 - numOfColumsRemoved}">
			<div class="h2-container" style="width: 100%;"><h2>Global Percent Adjustment</h2></div>
		</td>
	</tr>
	
	<tr>
		<kul:htmlAttributeHeaderCell scope="row" colspan="8" literalLabel="" horizontal="true" />
	
	    <td colspan ="${7 - numOfColumsRemoved}"><center>
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
