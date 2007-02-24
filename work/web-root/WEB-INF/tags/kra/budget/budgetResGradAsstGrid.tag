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
<%@ taglib uri="/tlds/fn.tld" prefix="fn" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<%@ attribute name="person" required="true" type="org.kuali.module.kra.budget.bo.BudgetUser"%>
<%@ attribute name="personListIndex" required="true" %>
<%@ attribute name="matchAppointmentType" required="true" %>

<c:set var="budgetUserAttributes" value="${DataDictionary.BudgetUser.attributes}" />
<c:set var="userAppointmentTaskAttributes" value="${DataDictionary.UserAppointmentTask.attributes}" />
<c:set var="userAppointmentTaskPeriodAttributes" value="${DataDictionary.UserAppointmentTaskPeriod.attributes}" />

<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}" />

              <tr>

                <th rowspan="2" class="bord-l-b"><b>Period</b></th>
                <td colspan="3" class="tab-subhead"><div align="center"><b>Agency Amount Requested</b> </div></td>
                <td colspan="3" class="tab-subhead"><div align="center"><b>Institution CS</b></div></td>
                <th rowspan="2" class="bord-l-b">Total Effort</th>
                <th rowspan="2" class="bord-l-b"><b>Total Salary </b></th>
                <th rowspan="2" class="bord-l-b">Total Health Insurance</th>

              </tr>
              <tr>
                <th class="bord-l-b"> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.agencyFullTimeEquivalentPercent}" useShortLabel="true" skipHelpUrl="true" noColon="true" /> </th>
                <th class="bord-l-b"> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.agencySalaryAmount}" useShortLabel="true" skipHelpUrl="true" noColon="true" readOnly="true" /> </th>
                <th class="bord-l-b"> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.agencyHealthInsuranceAmount}" useShortLabel="true" skipHelpUrl="true" noColon="true" readOnly="true" /></th>
                <th class="bord-l-b"> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.institutionFullTimeEquivalentPercent}" useShortLabel="true" skipHelpUrl="true" noColon="true" /> </th>

                <th class="bord-l-b"> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.institutionSalaryAmount}" useShortLabel="true" skipHelpUrl="true" noColon="true" readOnly="true" /></th>
                <th class="bord-l-b"> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.institutionHealthInsuranceAmount}" useShortLabel="true" skipHelpUrl="true" noColon="true" readOnly="true" /></th>
              </tr>
              
              <logic:iterate id="userAppointmentTask" name="KualiForm" property="document.budget.personFromList[${personListIndex}].userAppointmentTasks" indexId="userAppointmentTaskIndex">
                <c:if test="${userAppointmentTask.budgetTaskSequenceNumber eq person.currentTaskNumber and matchAppointmentType eq userAppointmentTask.institutionAppointmentTypeCode}">
                  <logic:iterate id="userAppointmentTaskPeriod" name="KualiForm" property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriods" indexId="userAppointmentTaskPeriodIndex">
  		              <tr>
  		                <td class="datacell"><div class="nowrap" align="center"><strong>${userAppointmentTaskPeriodIndex + 1}</strong><span class="fineprint"><br />
  		                    (<fmt:formatDate value="${userAppointmentTaskPeriod.period.budgetPeriodBeginDate}" dateStyle="short"/> - 
  		                     <fmt:formatDate value="${userAppointmentTaskPeriod.period.budgetPeriodEndDate}" dateStyle="short"/>)</span></div>
  		                </td>
                      
                      <td class="datacell">
                        <div align="center">
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyFullTimeEquivalentPercent" attributeEntry="${userAppointmentTaskPeriodAttributes.agencyFullTimeEquivalentPercent}" readOnly="${viewOnly}" styleClass="amount" />%
                        </div>
                      </td>
                          
                      <td class="datacell">
                        <div align="center">
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencySalaryAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.agencySalaryAmount}" readOnly="${viewOnly}" styleClass="amount" />
                        </div>
                      </td>
                      
                      <td class="datacell">
                        <div align="center">
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyHealthInsuranceAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.agencyHealthInsuranceAmount}" readOnly="${viewOnly}" styleClass="amount" />
                        </div>
                      </td>
                      
                      <td class="datacell" nowrap="nowrap">
                        <div align="center">
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionFullTimeEquivalentPercent" disabled="${! KualiForm.document.budget.institutionCostShareIndicator}" attributeEntry="${userAppointmentTaskPeriodAttributes.institutionFullTimeEquivalentPercent}" readOnly="${viewOnly}" styleClass="amount" />%
                        </div>
                      </td>
      
                      <td class="datacell">
                        <div align="center">
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionSalaryAmount" disabled="${! KualiForm.document.budget.institutionCostShareIndicator}" attributeEntry="${userAppointmentTaskPeriodAttributes.institutionSalaryAmount}" readOnly="${viewOnly}" styleClass="amount" />
                        </div>
                      </td>
                      
                      <td class="datacell">
                        <div align="center">
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionHealthInsuranceAmount" disabled="${! KualiForm.document.budget.institutionCostShareIndicator}" attributeEntry="${userAppointmentTaskPeriodAttributes.institutionHealthInsuranceAmount}" readOnly="${viewOnly}" styleClass="amount" />
                        </div>
                      </td>
                      
                      <td class="datacell">
                        <div align="right">${userAppointmentTaskPeriod.totalFteAmount}% </div>
                      </td>
                      
                      <td class="datacell">
                        <div align="right"><fmt:formatNumber value="${userAppointmentTaskPeriod.totalGradAsstSalaryAmount}" type="currency" currencySymbol="$" maxFractionDigits="0" /></div>
                      </td>
      
                      <td class="datacell">
                        <div align="right"><fmt:formatNumber value="${userAppointmentTaskPeriod.totalHealthInsuranceAmount}" type="currency" currencySymbol="$" maxFractionDigits="0" /></div>
                      </td>
  		              </tr>
                  </logic:iterate>
	              <tr>
	                <th class="bord-l-b"><b>TOTAL</b> </th>
	                <td class="infoline">
                      &nbsp;
                  </td>
	
	                <td class="infoline">
                    <div align="right">
                      <b><fmt:formatNumber value="${userAppointmentTask.gradAsstAgencySalaryTotal}" type="currency" currencySymbol="$" maxFractionDigits="0" /></b>
                    </div>
                  </td>
	                
                  <td class="infoline">
                    <div align="right">
                      <b><fmt:formatNumber value="${userAppointmentTask.gradAsstAgencyHealthInsuranceTotal}" type="currency" currencySymbol="$" maxFractionDigits="0" /></b>
                    </div>
                  </td>
	                
                  <td class="infoline">&nbsp;</td>
	                
                  <td class="infoline">
                    <div align="right">
                      <b><fmt:formatNumber value="${userAppointmentTask.gradAsstInstSalaryTotal}" type="currency" currencySymbol="$" maxFractionDigits="0" /></b>
                    </div>
                  </td>
	                
                  <td class="infoline">
                    <div align="right">
                      <b><fmt:formatNumber value="${userAppointmentTask.gradAsstInstHealthInsuranceTotal}" type="currency" currencySymbol="$" maxFractionDigits="0" /></b>
                    </div>
                  </td>
	                
                  <td class="infoline">
                    &nbsp;
                  </td>
	
	                <td colspan="3" class="infoline">&nbsp;</td>
	              </tr>
                
                <tr bgcolor="#ffffff">
                  <td colspan="13" class="tab-subhead" height="30"><span class="left"><strong>Fee Remissions </strong></span></td>
                </tr>

                <tr>
                  <th class="bord-l-b"> Period </th>
                  <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.userCreditHoursNumber}" skipHelpUrl="true" noColon="true" /></th>
                  <th colspan="2" class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.userCreditHourAmount}" skipHelpUrl="true" noColon="true" /></th>
                  <th colspan="2" class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.userMiscellaneousFeeAmount}" skipHelpUrl="true" noColon="true" /></th>
  
                  <th colspan="2" class="bord-l-b">Total Fee Remissions</th>
                  <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.agencyRequestedFeesAmount}" skipHelpUrl="true" noColon="true" /></th>
                  <th class="bord-l-b"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.institutionRequestedFeesAmount}" skipHelpUrl="true" noColon="true" /></th>
                </tr>

                <logic:iterate id="userAppointmentTaskPeriod" name="KualiForm" property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriods" indexId="userAppointmentTaskPeriodIndex">
                  <tr>
                    <td class="datacell"><div class="nowrap" align="center"><strong>${userAppointmentTaskPeriodIndex + 1}</strong><span class="fineprint"><br />
                        (<fmt:formatDate value="${userAppointmentTaskPeriod.period.budgetPeriodBeginDate}" dateStyle="short"/> - 
                         <fmt:formatDate value="${userAppointmentTaskPeriod.period.budgetPeriodEndDate}" dateStyle="short"/>)</span></div></td>

                    <td class="datacell">
                      <div align="center">
                        <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userCreditHoursNumber" attributeEntry="${userAppointmentTaskPeriodAttributes.userCreditHoursNumber}" readOnly="${viewOnly}" styleClass="amount" />
                      </div>
                    </td>
                    
                    <td colspan="2" class="datacell">
                      <div align="center">
                        <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userCreditHourAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.userCreditHourAmount}" readOnly="${viewOnly}" styleClass="amount" />
                      </div>
                    </td>
                    
                    <td colspan="2" class="datacell">
                      <div align="center">
                        <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userMiscellaneousFeeAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.userMiscellaneousFeeAmount}" readOnly="${viewOnly}" styleClass="amount" />
                      </div>
                    </td>
                    
                    <td colspan="2" class="datacell">
                      <div align="right"><fmt:formatNumber value="${userAppointmentTaskPeriod.totalFeeRemissionsAmount}" type="currency" currencySymbol="$" maxFractionDigits="0" /></div>
                    </td>
                    
                    <td class="datacell">
                      <div align="center">
                        <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyRequestedFeesAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.agencyRequestedFeesAmount}" readOnly="${viewOnly}" styleClass="amount" />
                      </div>
                    </td>
                    
                    <td class="datacell">
                      <div align="center">
                        <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionRequestedFeesAmount" disabled="${! KualiForm.document.budget.institutionCostShareIndicator}" attributeEntry="${userAppointmentTaskPeriodAttributes.institutionRequestedFeesAmount}" readOnly="${viewOnly}" styleClass="amount" />
                      </div>
                    </td>
                  </tr>
                </logic:iterate>
              </c:if>
            </logic:iterate>