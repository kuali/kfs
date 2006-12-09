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
<%@ taglib uri="/tlds/fn.tld" prefix="fn" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<%@ attribute name="person" required="true" type="org.kuali.module.kra.budget.bo.BudgetUser"%>
<%@ attribute name="personListIndex" required="true"%>

                 <c:choose>
	                 <c:when test="${fn:contains(KualiForm.appointmentTypeGridMappings['fullYear'], person.appointmentTypeCode)}">
	                   <kra-b:budgetPersonnelFullYearGrid person="${person}" personListIndex="${personListIndex}" matchAppointmentType="${person.appointmentTypeCode}" />
	                 </c:when>
                   <c:when test="${fn:contains(KualiForm.appointmentTypeGridMappings['academicYear'], person.appointmentTypeCode)}">
	                   <kra-b:budgetPersonnelFullYearGrid person="${person}" personListIndex="${personListIndex}"  matchAppointmentType="${person.appointmentTypeCode}"/>
	                 </c:when>
                   <c:when test="${fn:contains(KualiForm.appointmentTypeGridMappings['academicSummer'], person.appointmentTypeCode)}">
                     <kra-b:budgetPersonnelSummerGrid person="${person}" personListIndex="${personListIndex}"  matchAppointmentType="${person.appointmentTypeCode}"/>
                   </c:when>
                   <c:when test="${fn:contains(KualiForm.appointmentTypeGridMappings['hourly'], person.appointmentTypeCode)}">
                     <kra-b:budgetPersonnelHourlyGrid person="${person}" personListIndex="${personListIndex}" matchAppointmentType="${person.appointmentTypeCode}" />
                   </c:when>
                   <c:when test="${fn:contains(KualiForm.appointmentTypeGridMappings['gradResAssistant'], person.appointmentTypeCode)}">
                     <kra-b:budgetResGradAsstGrid person="${person}" personListIndex="${personListIndex}"  matchAppointmentType="${person.appointmentTypeCode}" />
                   </c:when>
                 </c:choose>

                 <c:if test="${not empty person.secondaryAppointmentTypeCode }">
                  <c:choose>
	                  <c:when test="${fn:contains(KualiForm.appointmentTypeGridMappings['fullYear'], person.secondaryAppointmentTypeCode)}">
	                    <kra-b:budgetPersonnelFullYearGrid person="${person}" personListIndex="${personListIndex}" matchAppointmentType="${person.secondaryAppointmentTypeCode}" />
	                  </c:when>
                    <c:when test="${fn:contains(KualiForm.appointmentTypeGridMappings['academicYear'], person.secondaryAppointmentTypeCode)}">
	                    <kra-b:budgetPersonnelFullYearGrid person="${person}" personListIndex="${personListIndex}" matchAppointmentType="${person.secondaryAppointmentTypeCode}"  />
                    </c:when>
                    <c:when test="${fn:contains(KualiForm.appointmentTypeGridMappings['academicSummer'], person.secondaryAppointmentTypeCode)}">
                      <kra-b:budgetPersonnelSummerGrid person="${person}" personListIndex="${personListIndex}" matchAppointmentType="${person.secondaryAppointmentTypeCode}"  />
                    </c:when>
                    <c:when test="${fn:contains(KualiForm.appointmentTypeGridMappings['hourly'], person.secondaryAppointmentTypeCode)}">
                      <kra-b:budgetPersonnelHourlyGrid person="${person}" personListIndex="${personListIndex}" matchAppointmentType="${person.secondaryAppointmentTypeCode}"  />
                    </c:when>
                    <c:when test="${fn:contains(KualiForm.appointmentTypeGridMappings['gradResAssistant'], person.secondaryAppointmentTypeCode)}">
                      <kra-b:budgetResGradAsstGrid person="${person}" personListIndex="${personListIndex}" matchAppointmentType="${person.secondaryAppointmentTypeCode}"  />
                    </c:when>
                  </c:choose>
                 </c:if>
                 
                 <logic:iterate id="userAppointmentTask" name="KualiForm" property="document.budget.personFromList[${personListIndex}].userAppointmentTasks" indexId="userAppointmentTaskIndex">
                   <c:set var="userAppointmentType" value="${KualiForm.document.budget.personnel[personListIndex].userAppointmentTasks[userAppointmentTaskIndex].institutionAppointmentTypeCode}" />
                   
                    <!-- User Appointment Task Fields -->
	                 <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].documentNumber" />
	                 <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].budgetUserSequenceNumber" />
	                 <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].budgetTaskSequenceNumber" />
	                 <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].institutionAppointmentTypeCode" />
	                 <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].versionNumber" />
	                 <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].secondaryAppointment" />
	                 <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].objectId" />
	                 
	                 <logic:iterate id="userAppointmentTaskPeriod" name="KualiForm" property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriods" indexId="userAppointmentTaskPeriodIndex">
                     <!-- User Appointment Task Period Fields -->
                     <!-- All Appointment Types -->
                     <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].documentNumber" />
                     <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].budgetUserSequenceNumber" />
                     <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].budgetTaskSequenceNumber" />
                     <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].budgetPeriodSequenceNumber" />
                     <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionAppointmentTypeCode" />
                     <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].versionNumber" />
                     <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].objectId" />

	                     <c:if test="${ (!(fn:contains(KualiForm.appointmentTypeGridMappings['fullYear'], userAppointmentType) or fn:contains(KualiForm.appointmentTypeGridMappings['academicYearSummer'], userAppointmentType))) or not (userAppointmentTask.budgetTaskSequenceNumber eq person.currentTaskNumber) or (userAppointmentTask.secondaryAppointment and empty person.secondaryAppointmentTypeCode) }">
                         <!-- Salary Appointment Types -->
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userBudgetPeriodSalaryAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyPercentEffortAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyRequestTotalAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyFringeBenefitTotalAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionCostSharePercentEffortAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionCostShareRequestTotalAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionCostShareFringeBenefitTotalAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionCostSharePercentEffortAmount" />
	                     </c:if>
	                  
                       <c:if test="${ !fn:contains(KualiForm.appointmentTypeGridMappings['academicSummer'], userAppointmentType) or not (userAppointmentTask.budgetTaskSequenceNumber eq person.currentTaskNumber) or (userAppointmentTask.secondaryAppointment and empty person.secondaryAppointmentTypeCode)  }">
                         <!-- Summer Appointment Type -->
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].personWeeksAmount" />
                       </c:if>
                    
                       <c:if test="${!fn:contains(KualiForm.appointmentTypeGridMappings['hourly'], userAppointmentType) or not (userAppointmentTask.budgetTaskSequenceNumber eq person.currentTaskNumber) or (userAppointmentTask.secondaryAppointment and empty person.secondaryAppointmentTypeCode) }">
                         <!-- Hourly Appointment Types -->
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userHourlyRate" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userAgencyHours" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userInstitutionHours" />
                       </c:if>
                    
                       <c:if test="${!fn:contains(KualiForm.appointmentTypeGridMappings['gradResAssistant'], userAppointmentType) or not (userAppointmentTask.budgetTaskSequenceNumber eq person.currentTaskNumber) or (userAppointmentTask.secondaryAppointment and empty person.secondaryAppointmentTypeCode) }">
                         <!-- Grad Asst Appointment -->
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyFullTimeEquivalentPercent" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyHealthInsuranceAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyRequestedFeesAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencySalaryAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionFullTimeEquivalentPercent" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionHealthInsuranceAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionRequestedFeesAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionSalaryAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userCreditHoursNumber" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userCreditHourAmount" />
                         <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userMiscellaneousFeeAmount" />
                       </c:if>

	                 </logic:iterate>
                 </logic:iterate>
                 