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

<c:set var="bciiAttributes" value="${DataDictionary['BudgetConstructionIntendedIncumbent'].attributes}" />
<c:set var="bccsftAttributes" value="${DataDictionary['BudgetConstructionCalculatedSalaryFoundationTracker'].attributes}" />
<c:set var="pbcafAttributes" value="${DataDictionary['PendingBudgetConstructionAppointmentFunding'].attributes}" />
<c:set var="bcpAttributes" value="${DataDictionary['BudgetConstructionPosition'].attributes}" />

<c:set var="readOnly" value="${KualiForm.editingMode['systemViewOnly'] || !KualiForm.editingMode['fullEntry']}" />

<div class="h2-container"><h2>Salary Line Detail</h2></div>
						
<table cellpadding="0" cellspacing="0" class="datatable" summary="Expenditure Salary Line Detail">
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentFundingDeleteIndicator}" />
				
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
	
	<c:set var="appointmentFundingPropertyName" value="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding"/>
	<c:forEach items="${KualiForm.pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding}" var="item" varStatus="status">
	<tr>		
		<%-- Appointment Funding Delete Indicator --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" accountingLine="${appointmentFundingPropertyName}[${status.index}]"
			cellProperty="${appointmentFundingPropertyName}[${status.index}].appointmentFundingDeleteIndicator" 
			attributes="${pbcafAttributes}" field="appointmentFundingDeleteIndicator" dataFieldCssClass="nobord"
			fieldAlign="left" readOnly="false" rowSpan="2">
			
			<html:hidden property="${appointmentFundingPropertyName}[${status.index}].universityFiscalYear" />
			<html:hidden property="${appointmentFundingPropertyName}[${status.index}].chartOfAccountsCode" />
			<html:hidden property="${appointmentFundingPropertyName}[${status.index}].accountNumber" />
			<html:hidden property="${appointmentFundingPropertyName}[${status.index}].subAccountNumber" />
			<html:hidden property="${appointmentFundingPropertyName}[${status.index}].financialObjectCode" />
			<html:hidden property="${appointmentFundingPropertyName}[${status.index}].financialSubObjectCode" />
			<html:hidden property="${appointmentFundingPropertyName}[${status.index}].versionNumber" />
			<html:hidden property="${appointmentFundingPropertyName}[${status.index}].emplid" />
		</bc:pbglLineDataCell>
	
		<%-- Position Number --%>	
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${appointmentFundingPropertyName}[${status.index}]"
			cellProperty="${appointmentFundingPropertyName}[${status.index}].positionNumber" 
			attributes="${pbcafAttributes}" field="positionNumber" inquiry="true"
			lookupOrInquiryKeys="positionNumber,universityFiscalYear" 
			boClassSimpleName="BudgetConstructionPosition" boPackageName="org.kuali.module.budget.bo" 
			accountingLineValuesMap="${item.valuesMap}" fieldAlign="left" readOnly="true" rowSpan="2"
			anchor="salaryexistingLineLineAnchor${status.index}" />
	
		<c:choose>
			<c:when test="${item.emplid != 'VACANT'}">
				<%-- Person Name --%>
				<bc:pbglLineDataCell dataCellCssClass="datacell" 
					accountingLine="${appointmentFundingPropertyName}[${status.index}].budgetConstructionIntendedIncumbent"
					cellProperty="${appointmentFundingPropertyName}[${status.index}].budgetConstructionIntendedIncumbent.personName" 
					attributes="${bciiAttributes}" inquiry="true" lookupOrInquiryKeys="emplid"
					boClassSimpleName="BudgetConstructionIntendedIncumbent" 
					boPackageName="org.kuali.module.budget.bo" accountingLineValuesMap="${item.valuesMap}" 
					field="personName" fieldAlign="left" readOnly="true" rowSpan="2" />

				<%-- Classification Level --%>
				<bc:pbglLineDataCell dataCellCssClass="datacell" 
					accountingLine="${appointmentFundingPropertyName}[${status.index}].budgetConstructionIntendedIncumbent"
					cellProperty="${appointmentFundingPropertyName}[${status.index}].budgetConstructionIntendedIncumbent.iuClassificationLevel" 
					attributes="${bciiAttributes}" field="iuClassificationLevel"
					fieldAlign="left" readOnly="true" rowSpan="2" />
			</c:when>
			<c:otherwise>
				<bc:pbglLineDataCell dataCellCssClass="datacell" 
					cellProperty="${appointmentFundingPropertyName}[${status.index}].budgetConstructionIntendedIncumbent.personName" 
					field="personName"
					attributes="${bciiAttributes}" readOnly="true" formattedNumberValue="${KFSConstants.BudgetConstructionConstants.VACANT_EMPLID}" 
					displayHidden="false" rowSpan="2" />
				
				<td rowspan="2">&nbsp;</td>
			</c:otherwise>
		</c:choose>
		
		<%-- Salary Plan Default --%>
		<c:choose>
			<c:when test="${!empty item.budgetConstructionPosition.positionSalaryPlanDefault}">
				<bc:pbglLineDataCell dataCellCssClass="datacell" 
					accountingLine="${appointmentFundingPropertyName}[${status.index}].budgetConstructionPosition"
					cellProperty="${appointmentFundingPropertyName}[${status.index}].budgetConstructionPosition.positionSalaryPlanDefault" 
					attributes="${bcpAttributes}" field="positionSalaryPlanDefault"
					fieldAlign="left" readOnly="true" rowSpan="2" />
			</c:when>
			<c:otherwise>
				<td  rowSpan="2">
					&nbsp;
				</td>
			</c:otherwise>
		</c:choose>

		<%-- Position Grade Default --%>
		<c:choose>
			<c:when test="${!empty item.budgetConstructionPosition.positionGradeDefault}">

				<bc:pbglLineDataCell dataCellCssClass="infocell" 
					accountingLine="${appointmentFundingPropertyName}[${status.index}].budgetConstructionPosition"
					cellProperty="${appointmentFundingPropertyName}[${status.index}].budgetConstructionPosition.positionGradeDefault" 
					attributes="${bcpAttributes}" field="positionGradeDefault" fieldAlign="left"
					readOnly="true" rowSpan="2" />
			</c:when>
			<c:otherwise>
				<td rowSpan="2">
					&nbsp;
				</td>
			</c:otherwise>
		</c:choose>

		<%-- IU Normal Work Months --%>
		<bc:pbglLineDataCell dataCellCssClass="infocell" 
			accountingLine="${appointmentFundingPropertyName}[${status.index}].budgetConstructionPosition"
			cellProperty="${appointmentFundingPropertyName}[${status.index}].budgetConstructionPosition.iuNormalWorkMonths" 
			attributes="${bcpAttributes}" field="iuNormalWorkMonths" fieldAlign="left"
			readOnly="true" rowSpan="2" />

		<%-- IU Pay Months --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${appointmentFundingPropertyName}[${status.index}].budgetConstructionPosition"
			cellProperty="${appointmentFundingPropertyName}[${status.index}].budgetConstructionPosition.iuPayMonths" 
			attributes="${bcpAttributes}" field="iuPayMonths" fieldAlign="left" readOnly="true" rowSpan="2" />
				
		<%-- csf Amount --%>
		<c:choose>
			<c:when test="${!empty item.bcnCalculatedSalaryFoundationTracker}">
				<bc:pbglLineDataCell dataCellCssClass="datacell" 
					accountingLine="${appointmentFundingPropertyName}[${status.index}]bcnCalculatedSalaryFoundationTracker[0]"
					cellProperty="${appointmentFundingPropertyName}[${status.index}].bcnCalculatedSalaryFoundationTracker[0].csfAmount" 
					attributes="${pbcafAttributes}" field="csfAmount" inquiry="true"
					lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,financialSubObjectCode,positionNumber,emplid" 
					boClassSimpleName="BudgetConstructionCalculatedSalaryFoundationTracker"
					boPackageName="org.kuali.module.budget.bo" 
					accountingLineValuesMap="${item.valuesMap}" fieldAlign="right" readOnly="true" rowSpan="1" />
			</c:when>
			<c:otherwise>
				<td>
					&nbsp;
				</td>
			</c:otherwise>
		</c:choose>

		<!-- csf Full Time Employment Quantity -->
		<c:choose>
			<c:when test="${!empty item.bcnCalculatedSalaryFoundationTracker}">
				<fmt:formatNumber value="${item.bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity}" 
					var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />
								
				<bc:pbglLineDataCell dataCellCssClass="datacell" 
					accountingLine="${appointmentFundingPropertyName}[${status.index}].bcnCalculatedSalaryFoundationTracker[0]"
					cellProperty="${appointmentFundingPropertyName}[${status.index}].bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity" 
					attributes="${bccsftAttributes}" field="csfFullTimeEmploymentQuantity" fieldAlign="right" 
					formattedNumberValue="${formattedNumber}" readOnly="true" rowSpan="1" dataFieldCssClass="amount" />
			</c:when>
			<c:otherwise>
				<td>
					&nbsp;
				</td>
			</c:otherwise>
		</c:choose>

		<%-- Appointment Funding Month --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${appointmentFundingPropertyName}[${status.index}]"
			cellProperty="${appointmentFundingPropertyName}[${status.index}].appointmentFundingMonth" 
			attributes="${pbcafAttributes}" field="appointmentFundingMonth" fieldAlign="left" readOnly="true" rowSpan="1" />
			
		<!-- appointment requested hourly rate -->
        <fmt:formatNumber value="${item.appointmentRequestedPayRate}" 
        	var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />	

		<bc:pbglLineDataCell dataCellCssClass="datacell" accountingLine="${appointmentFundingPropertyName}[${status.index}]"
			cellProperty="${appointmentFundingPropertyName}[${status.index}].appointmentRequestedPayRate" 
			attributes="${pbcafAttributes}" field="appointmentRequestedPayRate" fieldAlign="right"
			formattedNumberValue="${formattedNumber}" readOnly="true" rowSpan="1" dataFieldCssClass="amount" />			

		<%-- Appointment Requested Amount --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${appointmentFundingPropertyName}[${status.index}]"
			cellProperty="${appointmentFundingPropertyName}[${status.index}].appointmentRequestedAmount" 
			attributes="${pbcafAttributes}" field="appointmentRequestedAmount" fieldAlign="right" readOnly="${readOnly}"
			rowSpan="1" dataFieldCssClass="amount">
		</bc:pbglLineDataCell>
		        				
		<!-- appointment Requested Fte Quantity -->
        <fmt:formatNumber value="${item.appointmentRequestedFteQuantity}" 
        	var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />	

		<bc:pbglLineDataCell dataCellCssClass="datacell" accountingLine="${appointmentFundingPropertyName}[${status.index}]"
			cellProperty="${appointmentFundingPropertyName}[${status.index}].appointmentRequestedFteQuantity" 
			attributes="${pbcafAttributes}" field="appointmentRequestedFteQuantity" fieldAlign="right"
			formattedNumberValue="${formattedNumber}" readOnly="true" rowSpan="1" dataFieldCssClass="amount" />

		<%-- Percent Change --%>
		<bc:pbglLineDataCell dataCellCssClass="datacell" 
			accountingLine="${appointmentFundingPropertyName}[${status.index}]"
			cellProperty="${appointmentFundingPropertyName}[${status.index}].percentChange" 
			attributes="${pbcafAttributes}" field="percentChange" fieldAlign="right" readOnly="true" rowSpan="1" />

		<td class="datacell" rowspan="2" nowrap>
			<html:image 
				property="methodToCall.performPositionSalarySetting.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
				src="${ConfigProperties.externalizable.images.url}tinybutton-posnsalset.gif" 
				title="Position Salary Setting For Line ${status.index}"
				alt="Position Salary Setting For Line ${status.index}" 
				styleClass="tinybutton" />
			
			<c:if test="${item.emplid != 'VACANT'}">
				<html:image property="methodToCall.performIncumbentSalarySetting.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
					src="${ConfigProperties.externalizable.images.url}tinybutton-incmbntsalset.gif" 
					title="Incumbent Salary Setting For Line ${status.index}"
					alt="Incumbent Salary Setting For Line ${status.index}" styleClass="tinybutton" />
			</c:if>
		</td>
	</tr>
	
	<tr>
		<td colspan ="7">
				<kul:htmlAttributeLabel attributeEntry="${pbcafAttributes.adjustmentMeasurement}"
					forceRequired="true" useShortLabel="false" />
				<kul:htmlControlAttribute attributeEntry="${pbcafAttributes.adjustmentMeasurement}"
					property="${appointmentFundingPropertyName}[${status.index}].adjustmentMeasurement"/>
				
				<kul:htmlAttributeLabel attributeEntry="${pbcafAttributes.adjustmentAmount}"
					forceRequired="true" useShortLabel="false" />
				<kul:htmlControlAttribute attributeEntry="${pbcafAttributes.adjustmentAmount}"
					property="${appointmentFundingPropertyName}[${status.index}].adjustmentAmount"/>	
				
				<html:image property="methodToCall.performPercentAdjustmentSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" 
					src="${ConfigProperties.externalizable.images.url}tinybutton-apply.gif" 
					title="Percent Adjustment For Line ${status.index}"
					alt="Percent Adjustment For Line ${status.index}" styleClass="tinybutton" />
		</td>
	</tr>
	</c:forEach>
		
	<tr>
		<kul:htmlAttributeHeaderCell scope="row" rowspan="1" colspan="8" literalLabel="Total:" horizontal="true" />
	
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
</table>