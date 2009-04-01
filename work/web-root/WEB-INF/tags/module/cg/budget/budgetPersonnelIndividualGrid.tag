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

<%@ attribute name="person" required="true" type="org.kuali.kfs.module.cg.businessobject.BudgetUser"%>
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
                 
                 
