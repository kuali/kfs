<%--
 Copyright 2007 The Kuali Foundation.
 
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

<%@ attribute name="fundingLine" required="true" type="java.lang.Object"
			  description="The funding line object containing the data being displayed"%>
<%@ attribute name="fundingLineName" required="true" description="The name  of the funding line"%>
<%@ attribute name="lineIndex" required="false" description="The index of the funding line"%>

<c:set var="reasonAttributes" value="${DataDictionary['BudgetConstructionAppointmentFundingReason'].attributes}" />
<c:set var="bcafAttributes"	value="${DataDictionary['PendingBudgetConstructionAppointmentFunding'].attributes}" />
<c:set var="bcsfAttributes"	value="${DataDictionary['BudgetConstructionCalculatedSalaryFoundationTracker'].attributes}" />
<c:set var="pbcafAttributes" value="${DataDictionary['PendingBudgetConstructionAppointmentFunding'].attributes}" />

<c:set var="readOnly" value="false" />

<table style="border-top: 1px solid rgb(153, 153, 153); width: inherit;" cellpadding="0" cellspacing="0" class="datatable">
	<tr>
		<th>&nbsp;</th>
		<th>Row Operation</th>
		<th>Amount</th>
		<th>Hourly</th>
		<th>Months</th>
		<th>Percent Time</th>
		<th>FTE</th>
		<th>Reason Select</th>
		<th>Reason Amount</th>
	</tr>
	
	<c:if test="${!empty fundingLine.bcnCalculatedSalaryFoundationTracker}">
	<tr>
		<th align="right">CSF:</th>	
		<td>
			<bc:salaryAdjustment attributes="${bcafAttributes}" 
				adjustmentMeasurementFieldName="${fundingLineName}.adjustmentMeasurement" 
				adjustmentAmountFieldName="${fundingLineName}.adjustmentAmount"
				lineIndex="${lineIndex}"/>
		</td>		
		
		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.bcnCalculatedSalaryFoundationTracker[0].csfAmount"
                attributes="${pbcafAttributes}"
                field="csfAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		
		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.bcnCalculatedSalaryFoundationTracker[0].csfTimePercent"
                attributes="${pbcafAttributes}"
                field="csfTimePercent"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity"
                attributes="${pbcafAttributes}"
                field="csfFullTimeEmploymentQuantity"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
		
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	</c:if>
      
	<tr>
		<th align="right">Request:</th>					
		<td>&nbsp;</td>
		
		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedAmount"
                attributes="${bcafAttributes}"
                field="appointmentRequestedAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedPayRate"
                attributes="${bcafAttributes}"
                field="appointmentRequestedPayRate"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
		
		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentFundingMonth"
                attributes="${bcafAttributes}"
                field="appointmentFundingMonth"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
		
		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedTimePercent"
                attributes="${bcafAttributes}"
                field="appointmentRequestedTimePercent"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedFteQuantity"
                attributes="${bcafAttributes}"
                field="appointmentRequestedFteQuantity"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
		
		<%-- TODO: fix NPE
		<bc:pbglLineDataCell dataCellCssClass="infoline"
              accountingLine="${fundingLine}"
              cellProperty="${fundingLineName}.budgetConstructionAppointmentFundingReason[0].appointmentFundingReasonCode"
              attributes="${reasonAttributes}"
              field="appointmentFundingReasonCode"
              fieldAlign="right"
              readOnly="${readOnly}"
              rowSpan="1"/>
	
		<bc:pbglLineDataCell dataCellCssClass="infoline"
              accountingLine="${fundingLine}"
              cellProperty="${fundingLineName}.budgetConstructionAppointmentFundingReason[0].appointmentFundingReasonAmount"
              attributes="${reasonAttributes}"
              field="appointmentFundingReasonAmount"
              fieldAlign="right"
              readOnly="${readOnly}"
              rowSpan="1" dataFieldCssClass="amount" />
         --%>
         
        <td>&nbsp;</td>		
		<td>&nbsp;</td>
	</tr>
	
	<tr>
		<th align="right">Leave Request CSF:</th>
		
		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentFundingDurationCode"
                attributes="${bcafAttributes}"
                field="appointmentFundingDurationCode"
                fieldAlign="left"
                readOnly="${readOnly}"
                rowSpan="1" />	

		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedCsfAmount"
                attributes="${bcafAttributes}"
                field="appointmentRequestedCsfAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<td>&nbsp;</td>		
		<td>&nbsp;</td>
		
		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedCsfTimePercent"
                attributes="${bcafAttributes}"
                field="appointmentRequestedCsfTimePercent"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedCsfFteQuantity"
                attributes="${bcafAttributes}"
                field="appointmentRequestedCsfFteQuantity"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
		
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	
	<tr>
		<th align="right">Total Intended:</th>		
		<td>&nbsp;</td>	

		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentTotalIntendedAmount"
                attributes="${bcafAttributes}"
                field="appointmentTotalIntendedAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<td>&nbsp;</td>		
		<td>&nbsp;</td>
		<td>&nbsp;</td>
                
		<bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentTotalIntendedFteQuantity"
                attributes="${bcafAttributes}"
                field="appointmentTotalIntendedFteQuantity"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
		
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
</table>