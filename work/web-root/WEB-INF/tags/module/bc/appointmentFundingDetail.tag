<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="fundingLine" required="true" type="java.lang.Object"
			  description="The funding line object containing the data being displayed"%>
<%@ attribute name="fundingLineName" required="true" description="The name  of the funding line"%>
<%@ attribute name="lineIndex" required="false" description="The index of the funding line"%>
<%@ attribute name="isSetteingByIncumbent" required="false" description="The index of the funding line"%>
<%@ attribute name="readOnly" required="false" description="determine whether the contents can be read only or not"%>
<%@ attribute name="hasBeenAdded" required="false" description="determine if the current funding line has been added"%>

<c:set var="reasonAttributes" value="${DataDictionary['BudgetConstructionAppointmentFundingReason'].attributes}" />
<c:set var="bcsfAttributes"	value="${DataDictionary['BudgetConstructionCalculatedSalaryFoundationTracker'].attributes}" />
<c:set var="pbcafAttributes" value="${DataDictionary['PendingBudgetConstructionAppointmentFunding'].attributes}" />

<c:set var="hourlyPaid" value="${fundingLine.hourlyPaid}" />
<c:set var="newLine" value="${fundingLine.newLineIndicator}" />
<c:set var="vacantLine" value="${fundingLine.emplid eq BCConstants.VACANT_EMPLID}" />
<c:set var="excludedFromTotal" value="${fundingLine.excludedFromTotal}" />

<c:if test="${isSetteingByIncumbent}" >
	<c:set var="postionFieldPrefix" value="${fundingLineName}." />
</c:if>

<table style="border-top: 1px solid rgb(153, 153, 153); width: 90%;" cellpadding="0" cellspacing="0" class="datatable">
	<tr>
		<th style="width: 15%;">&nbsp;</th>
		<th style="width: 25%;">Row Operation</th>
		<th style="width: 10%;">Amount</th>
		<th style="width: 5%;">Hourly Rate</th>
		<th style="width: 5%;">Months</th>
		<th style="width: 10%;">Percent Time</th>
		<th style="width: 10%;">FTE</th>
		<th style="width: 10%;">Reason Select</th>
		<th style="width: 10%;">Reason Amount</th>
	</tr>
	
	<c:if test="${hasBeenAdded && !empty fundingLine.bcnCalculatedSalaryFoundationTracker}">
	<tr>
		<th style="text-align: right;">CSF:</th>	
		
		<td class="datacell">
			<c:if test="${!readOnly && !excludedFromTotal}">
				<bc:salaryAdjustment attributes="${pbcafAttributes}" 
					adjustmentMeasurementFieldName="${fundingLineName}.adjustmentMeasurement" 
					adjustmentAmountFieldName="${fundingLineName}.adjustmentAmount"
					methodToCall="adjustSalarySettingLinePercent"
					lineIndex="${lineIndex}"/>
			</c:if>&nbsp;
		</td>		
		
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLineName}"
                cellProperty="${fundingLineName}.bcnCalculatedSalaryFoundationTracker[0].csfAmount"
                attributes="${bcsfAttributes}"
                field="csfAmount"
                fieldAlign="right"
                readOnly="true"
                excludedFromTotal="${excludedFromTotal}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<td class="datacell">&nbsp;</td>
		<td class="datacell">&nbsp;</td>
		
		<fmt:formatNumber var="formattedCsfTimePercent" value="${fundingLine.bcnCalculatedSalaryFoundationTracker[0].csfTimePercent}" 
        		type="number" groupingUsed="true" minFractionDigits="2" maxFractionDigits="2"/>		
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLineName}"
                cellProperty="${fundingLineName}.bcnCalculatedSalaryFoundationTracker[0].csfTimePercent"
                attributes="${bcsfAttributes}"
                field="csfTimePercent"
                fieldAlign="right"
                readOnly="true"
                rowSpan="1" dataFieldCssClass="amount" formattedNumberValue="${formattedCsfTimePercent}">&nbsp;</bc:pbglLineDataCell>
        
        <fmt:formatNumber var="formattedCsfFteQuantity" value="${fundingLine.bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity}" 
        		type="number" groupingUsed="true" minFractionDigits="5" maxFractionDigits="5"/>         
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLineName}"
                cellProperty="${fundingLineName}.bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity"
                attributes="${bcsfAttributes}"
                field="csfFullTimeEmploymentQuantity"
                fieldAlign="right" readOnly="true"
                rowSpan="1" dataFieldCssClass="amount" formattedNumberValue="${formattedCsfFteQuantity}">&nbsp;</bc:pbglLineDataCell>
		
		<td class="datacell">&nbsp;</td>
		<td class="datacell">&nbsp;</td>
	</tr>
	</c:if>
      
	<tr>
		<th style="text-align: right;">Request:</th>	
		<td class="datacell">&nbsp;</td>				

		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLineName}"
                attributes="${pbcafAttributes}"
                field="appointmentRequestedAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                excludedFromTotal="${excludedFromTotal}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLineName}"
                attributes="${pbcafAttributes}"
                field="appointmentRequestedPayRate"
                fieldAlign="right"
                readOnly="${readOnly || (!hourlyPaid && hasBeenAdded)}"
                excludedFromTotal="${excludedFromTotal}"
                rowSpan="1" dataFieldCssClass="amount" />
		
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLineName}"
                attributes="${pbcafAttributes}"
                field="appointmentFundingMonth" detailFunction="budgetObjectInfoUpdator.recalculateFTE"
				detailFunctionExtraParam="'${postionFieldPrefix}budgetConstructionPosition.iuPayMonths','${fundingLineName}.appointmentFundingMonth', '${fundingLineName}.appointmentRequestedFteQuantity','${fundingLineName}.appointmentRequestedTimePercent',"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
		
		<fmt:formatNumber var="formattedRequestedTimePercent" value="${fundingLine.appointmentRequestedTimePercent}" 
        		type="number" groupingUsed="true" minFractionDigits="2" maxFractionDigits="2"/>	
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLineName}"
                attributes="${pbcafAttributes}"
                field="appointmentRequestedTimePercent" detailFunction="budgetObjectInfoUpdator.recalculateFTE"
				detailFunctionExtraParam="'${postionFieldPrefix}budgetConstructionPosition.iuPayMonths','${fundingLineName}.appointmentFundingMonth', '${fundingLineName}.appointmentRequestedFteQuantity',"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" formattedNumberValue="${formattedRequestedTimePercent}"/>
                
        <td class="datacell" style="text-align: right;" rowSpan="1">
        	<fmt:formatNumber var="formattedFteQuantity" value="${fundingLine.appointmentRequestedFteQuantity}" 
        		type="number" groupingUsed="true" minFractionDigits="5" maxFractionDigits="5"/>	
        		
		    <bc:pbglLineDataCellDetail detailField="appointmentRequestedFteQuantity" accountingLine="${fundingLineName}" 
		    	dataFieldCssClass="nowrap" formattedNumberValue="${formattedFteQuantity}"/>
		</td>        
		
		<bc:pbglLineDataCell dataCellCssClass="datacell"
              accountingLine="${fundingLineName}"
              cellProperty="${fundingLineName}.budgetConstructionAppointmentFundingReason[0].appointmentFundingReasonCode"
              attributes="${reasonAttributes}"
              detailField="budgetConstructionAppointmentFundingReason[0].appointmentFundingReason.appointmentFundingReasonDescription"
              detailFunctionExtraParam = "'${fundingLineName}.budgetConstructionAppointmentFundingReason[0].appointmentFundingReasonAmount'," 
              detailFunction="budgetObjectInfoUpdator.loadReasonCodeInfo"
              field="appointmentFundingReasonCode"
              fieldAlign="left"
              readOnly="${readOnly}"
              rowSpan="1"/>
	
		<c:set var="disabled" value="${empty fundingLine.budgetConstructionAppointmentFundingReason[0].appointmentFundingReasonCode}" />
		<bc:pbglLineDataCell dataCellCssClass="datacell"
              accountingLine="${fundingLineName}"
              cellProperty="${fundingLineName}.budgetConstructionAppointmentFundingReason[0].appointmentFundingReasonAmount"
              attributes="${reasonAttributes}"
              field="appointmentFundingReasonAmount"
              fieldAlign="right"
              readOnly="${readOnly}" disabled="${disabled}"
              rowSpan="1" dataFieldCssClass="amount" />
	</tr>
	
	<tr>
		<th style="text-align: right;">Leave Request CSF:</th>
			
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLineName}"
                attributes="${pbcafAttributes}"
                detailField="budgetConstructionDuration.appointmentDurationDescription" 
                detailFunction="budgetObjectInfoUpdator.loadDurationInfo"
                field="appointmentFundingDurationCode"
                fieldAlign="left"
                readOnly="${readOnly || vacantLine}"
                rowSpan="1" />
                	                	
		<c:set var="disabled" value="${fundingLine.appointmentFundingDurationCode eq BCConstants.AppointmentFundingDurationCodes.NONE.durationCode}" />
		
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLineName}"
                attributes="${pbcafAttributes}"
                field="appointmentRequestedCsfAmount"
                fieldAlign="right"
                excludedFromTotal="${excludedFromTotal}"
                readOnly="${readOnly || vacantLine}" disabled="${disabled}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<td>&nbsp;</td>		
		<td>&nbsp;</td>
		
		<fmt:formatNumber var="formattedReqCsfTimePercent" value="${fundingLine.appointmentRequestedCsfTimePercent}" 
        		type="number" groupingUsed="true" minFractionDigits="2" maxFractionDigits="2"/> 
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLineName}"
                attributes="${pbcafAttributes}"
                field="appointmentRequestedCsfTimePercent" detailFunction="budgetObjectInfoUpdator.recalculateFTE"
				detailFunctionExtraParam="'${postionFieldPrefix}budgetConstructionPosition.iuPayMonths', '${postionFieldPrefix}budgetConstructionPosition.iuNormalWorkMonths','${fundingLineName}.appointmentRequestedCsfFteQuantity',"
                fieldAlign="right"
                readOnly="${readOnly || vacantLine}" disabled="${disabled}"
                rowSpan="1" dataFieldCssClass="amount" formattedNumberValue="${formattedReqCsfTimePercent}"/>
                
        <td class="datacell" style="text-align: right;" rowSpan="1">
        	<fmt:formatNumber var="formattedReqCsfFteQuantity" value="${fundingLine.appointmentRequestedCsfFteQuantity}" 
        		type="number" groupingUsed="true" minFractionDigits="5" maxFractionDigits="5"/>	
        		
		    <bc:pbglLineDataCellDetail detailField="appointmentRequestedCsfFteQuantity" accountingLine="${fundingLineName}" 
		    	dataFieldCssClass="nowrap" formattedNumberValue="${formattedReqCsfFteQuantity}"/>
		</td>         
						
        <td class="datacell">&nbsp;</td>
        <td class="datacell">&nbsp;</td>        
	</tr>
	
	<tr>
		<th style="text-align: right;">Total Intended:</th>		
		<td class="datacell">&nbsp;</td> 

		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLineName}"
                attributes="${pbcafAttributes}"
                field="appointmentTotalIntendedAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                excludedFromTotal="${excludedFromTotal}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<td>&nbsp;</td>		
		<td>&nbsp;</td>
		<td>&nbsp;</td>
        
        <fmt:formatNumber var="formattedIndendedFteQuantity" value="${fundingLine.appointmentTotalIntendedFteQuantity}" 
        		type="number" groupingUsed="true" minFractionDigits="5" maxFractionDigits="5"/>          
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLineName}"
                attributes="${pbcafAttributes}"
                field="appointmentTotalIntendedFteQuantity"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" formattedNumberValue="${formattedIndendedFteQuantity}"/>
		
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
</table>
