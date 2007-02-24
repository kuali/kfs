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
                <td colspan="14" class="tab-subhead" height="30"><div align="left"><strong>Salary Disbursement - Summer</strong></div></td>
              </tr>

              <tr>
                <th rowspan="2"><b>Period</b></th>
                <th rowspan="2"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.personWeeksAmount}" skipHelpUrl="true" noColon="true" /></th>
                <th rowspan="2"> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.userBudgetPeriodSalaryAmount}" skipHelpUrl="true" noColon="true" /> </th>
                <td colspan="4" class="tab-subhead"><div align="center"><b>Agency Amount Requested</b> </div></td>
                <td colspan="4" class="tab-subhead"><div align="center"><b>Institution CS</b></div></td>
                <th rowspan="2"><b>Total Effort </b></th>
                <th rowspan="2"><b>Total Salary </b></th>
                <th rowspan="2"><b>Total Fringe Benefits</b></th>
              </tr>

              <tr>
                <th> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.agencyPercentEffortAmount}" useShortLabel="true" skipHelpUrl="true" noColon="true" /> </th>
                <th> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.agencySalaryAmount}" useShortLabel="true" skipHelpUrl="true" noColon="true" readOnly="true" /> </th>

                <th> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetFringeRate.attributes.contractsAndGrantsFringeRateAmount}" useShortLabel="true" skipHelpUrl="true" noColon="true" readOnly="true" /> </th>
                <th> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.agencyFringeBenefitTotalAmount}" useShortLabel="true" skipHelpUrl="true" noColon="true" readOnly="true" /> </th>
                <th> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.institutionCostSharePercentEffortAmount}" useShortLabel="true" skipHelpUrl="true" noColon="true" /> </th>
                <th> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.institutionSalaryAmount}" useShortLabel="true" skipHelpUrl="true" noColon="true" readOnly="true" /></th>

                <th> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetFringeRate.attributes.institutionCostShareFringeRateAmount}" useShortLabel="true" skipHelpUrl="true" noColon="true" readOnly="true" /></th>
                <th> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.UserAppointmentTaskPeriod.attributes.institutionCostShareFringeBenefitTotalAmount}" useShortLabel="true" skipHelpUrl="true" noColon="true" readOnly="true" /></th>
              </tr>

              <logic:iterate id="userAppointmentTask" name="KualiForm" property="document.budget.personFromList[${personListIndex}].userAppointmentTasks" indexId="userAppointmentTaskIndex">
                <c:if test="${userAppointmentTask.budgetTaskSequenceNumber eq person.currentTaskNumber  and userAppointmentTask.institutionAppointmentTypeCode eq matchAppointmentType}">
                  <logic:iterate id="userAppointmentTaskPeriod" name="KualiForm" property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriods" indexId="userAppointmentTaskPeriodIndex">
  		              <tr>
  		                <td class="datacell"><div class="nowrap" align="center"><strong>${userAppointmentTaskPeriodIndex + 1}</strong><span class="fineprint"><br />
  		                    (<fmt:formatDate value="${userAppointmentTaskPeriod.period.budgetPeriodBeginDate}" dateStyle="short"/> - 
  		                     <fmt:formatDate value="${userAppointmentTaskPeriod.period.budgetPeriodEndDate}" dateStyle="short"/>)</span></div>
                        </td>
  		                  <td class="datacell">
                        <div align="right">
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].personWeeksAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.personWeeksAmount}" readOnly="${viewOnly}"  styleClass="amount"/>
                        </div>
                      </td>

  		                  <td class="datacell">
                        <div align="right">
                          <fmt:formatNumber value="${userAppointmentTaskPeriod.userBudgetPeriodSalaryAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userBudgetPeriodSalaryAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.userBudgetPeriodSalaryAmount}" styleClass="amount" />
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyPercentEffortAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.agencyPercentEffortAmount}" readOnly="${viewOnly}" styleClass="amount" />%
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <fmt:formatNumber value="${userAppointmentTaskPeriod.agencyRequestTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyRequestTotalAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.agencyRequestTotalAmount}" styleClass="amount" />
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].budgetFringeRate.contractsAndGrantsFringeRateAmount" write="true" />%
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <fmt:formatNumber value="${userAppointmentTaskPeriod.agencyFringeBenefitTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyFringeBenefitTotalAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.agencyFringeBenefitTotalAmount}" styleClass="amount" />
                        </div>
                      </td>

  		                  <td class="datacell">
                        <div align="right">
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionCostSharePercentEffortAmount" disabled="${! KualiForm.document.budget.institutionCostShareIndicator}" attributeEntry="${userAppointmentTaskPeriodAttributes.institutionCostSharePercentEffortAmount}" readOnly="${viewOnly}" styleClass="amount" />%
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <fmt:formatNumber value="${userAppointmentTaskPeriod.institutionCostShareRequestTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionCostShareRequestTotalAmount" disabled="${! KualiForm.document.budget.institutionCostShareIndicator}" attributeEntry="${userAppointmentTaskPeriodAttributes.institutionCostShareRequestTotalAmount}" styleClass="amount" />
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].budgetFringeRate.institutionCostShareFringeRateAmount" write="true" />%
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <fmt:formatNumber value="${userAppointmentTaskPeriod.institutionCostShareFringeBenefitTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].institutionCostShareFringeBenefitTotalAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.institutionCostShareFringeBenefitTotalAmount}" styleClass="amount" />
                        </div>
                      </td>

  		                <td class="datacell"><div align="right">${userAppointmentTaskPeriod.totalPercentEffort}%</div></td>
  		                <td class="datacell"><div align="right"><fmt:formatNumber value="${userAppointmentTaskPeriod.totalSalaryAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /></div></td>
  		                <td class="datacell"><div align="right"><fmt:formatNumber value="${userAppointmentTaskPeriod.totalFringeAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /></div></td>
  		              </tr>
                  </logic:iterate>
  	              <tr>
  	                <th class="bord-l-b"><b>TOTAL</b> </th>
  	                <td colspan="3" class="infoline">&nbsp;</td>
  	                <td class="infoline"><div align="right"><b><fmt:formatNumber value="${userAppointmentTask.agencyRequestTotalAmountTask}" type="currency" currencySymbol="$" maxFractionDigits="0" /></b> </div></td>
  	
  	                <td class="infoline">&nbsp;</td>
  	                <td class="infoline"><div align="right"><b><fmt:formatNumber value="${userAppointmentTask.agencyFringeBenefitTotalAmountTask}" type="currency" currencySymbol="$" maxFractionDigits="0" /></b> </div></td>
  	                <td class="infoline">&nbsp;</td>
  	                <td class="infoline"><div align="right"><b><fmt:formatNumber value="${userAppointmentTask.institutionCostShareRequestTotalAmountTask}" type="currency" currencySymbol="$" maxFractionDigits="0" /></b> </div></td>
  	                <td class="infoline">&nbsp;</td>
  	                <td class="infoline"><div align="right"><b><fmt:formatNumber value="${userAppointmentTask.institutionCostShareFringeBenefitTotalAmountTask}" type="currency" currencySymbol="$" maxFractionDigits="0" /></b> </div></td>
  	
  	                <td colspan="3" class="infoline">&nbsp;</td>
  	              </tr>
  	            </c:if>
  	          </logic:iterate>
