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
<c:set var="bcsfAttributes"	value="${DataDictionary['BudgetConstructionCalculatedSalaryFoundationTracker'].attributes}" />
<c:set var="pbcafAttributes" value="${DataDictionary['PendingBudgetConstructionAppointmentFunding'].attributes}" />

<c:set var="readOnly" value="false" />

<table style="border-top: 1px solid rgb(153, 153, 153); width: 90%;" cellpadding="0" cellspacing="0" class="datatable">
	<tr>
		<th>&nbsp;</th>
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
		<th align="right" rowspan="2">CSF:</th>			
		
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.bcnCalculatedSalaryFoundationTracker[0].csfAmount"
                attributes="${bcsfAttributes}"
                field="csfAmount"
                fieldAlign="right"
                readOnly="true"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<td class="datacell">&nbsp;</td>
		<td class="datacell">&nbsp;</td>
		
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.bcnCalculatedSalaryFoundationTracker[0].csfTimePercent"
                attributes="${bcsfAttributes}"
                field="csfTimePercent"
                fieldAlign="right"
                readOnly="true"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity"
                attributes="${bcsfAttributes}"
                field="csfFullTimeEmploymentQuantity"
                fieldAlign="right"
                readOnly="true"
                rowSpan="1" dataFieldCssClass="amount" />
		
		<td class="datacell">&nbsp;</td>
		<td class="datacell">&nbsp;</td>
	</tr>
	
	<tr>
		<td colspan="5" class="infoline"><center>
			<bc:salaryAdjustment attributes="${pbcafAttributes}" 
				adjustmentMeasurementFieldName="${fundingLineName}.adjustmentMeasurement" 
				adjustmentAmountFieldName="${fundingLineName}.adjustmentAmount"
				methodToCall="adjustSalarySettingLinePercent"
				lineIndex="${lineIndex}"/>
			</center>
		</td>
		
		<td class="datacell">&nbsp;</td>
		<td class="datacell">&nbsp;</td>		
	</tr>
	</c:if>
      
	<tr>
		<th align="right">Request:</th>					
		
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedAmount"
                attributes="${pbcafAttributes}"
                field="appointmentRequestedAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedPayRate"
                attributes="${pbcafAttributes}"
                field="appointmentRequestedPayRate"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
		
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentFundingMonth"
                attributes="${pbcafAttributes}"
                field="appointmentFundingMonth"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
		
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedTimePercent"
                attributes="${pbcafAttributes}"
                field="appointmentRequestedTimePercent"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedFteQuantity"
                attributes="${pbcafAttributes}"
                field="appointmentRequestedFteQuantity"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
		
		<bc:pbglLineDataCell dataCellCssClass="datacell"
              accountingLine="${fundingLine}"
              cellProperty="${fundingLineName}.budgetConstructionAppointmentFundingReason[0].appointmentFundingReasonCode"
              attributes="${reasonAttributes}"
              field="appointmentFundingReasonCode"
              fieldAlign="left"
              readOnly="${readOnly}"
              rowSpan="1"/>
	
		<bc:pbglLineDataCell dataCellCssClass="datacell"
              accountingLine="${fundingLine}"
              cellProperty="${fundingLineName}.budgetConstructionAppointmentFundingReason[0].appointmentFundingReasonAmount"
              attributes="${reasonAttributes}"
              field="appointmentFundingReasonAmount"
              fieldAlign="right"
              readOnly="${readOnly}"
              rowSpan="1" dataFieldCssClass="amount" />
	</tr>
	
	<tr>
		<th align="right">Leave Request CSF:</th>	

		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedCsfAmount"
                attributes="${pbcafAttributes}"
                field="appointmentRequestedCsfAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<td>&nbsp;</td>		
		<td>&nbsp;</td>
		
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedCsfTimePercent"
                attributes="${pbcafAttributes}"
                field="appointmentRequestedCsfTimePercent"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentRequestedCsfFteQuantity"
                attributes="${pbcafAttributes}"
                field="appointmentRequestedCsfFteQuantity"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
						
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentFundingDurationCode"
                attributes="${pbcafAttributes}"
                field="appointmentFundingDurationCode"
                fieldAlign="left"
                readOnly="${readOnly}"
                rowSpan="1" />
                
		<td>&nbsp;</td>
	</tr>
	
	<tr>
		<th align="right">Total Intended:</th>		

		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentTotalIntendedAmount"
                attributes="${pbcafAttributes}"
                field="appointmentTotalIntendedAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
                
		<td>&nbsp;</td>		
		<td>&nbsp;</td>
		<td>&nbsp;</td>
                
		<bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="${fundingLine}"
                cellProperty="${fundingLineName}.appointmentTotalIntendedFteQuantity"
                attributes="${pbcafAttributes}"
                field="appointmentTotalIntendedFteQuantity"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
		
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
</table>