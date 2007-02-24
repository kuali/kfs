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
<%@ taglib uri="/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<c:set var="budgetAttributes" value="${DataDictionary.Budget.attributes}" />
<c:set var="budgetGraduateFringeRateAttributes" value="${DataDictionary.BudgetGraduateAssistantRate.attributes}" />
<c:set var="businessObjectClass" value="${DataDictionary.GraduateAssistantRate.businessObjectClass}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>

<html:hidden property="numberOfAcademicYearSubdivisions" />
<html:hidden property="academicYearSubdivisionNamesString"/>

<div class="h2-container"> <span class="subhead-left">
  <a name="GradAsst"></a><h2>Research &amp; Grad Assistant Fringe Benefit Rates</h2>
  </span><span class="subhead-right"> <span class="subhead"><kul:help businessObjectClassName="${businessObjectClass}" altText="help"/></span> </span> </div>

<table cellpadding="0" cellspacing="0" class="datatable" summary=""> 
  <c:forEach var="i" begin="1" end="${KualiForm.numberOfAcademicYearSubdivisions}">
  <tr>
    <td colspan="3" class="tab-subhead">${KualiForm.academicYearSubdivisionNames[i - 1]} Health Insurance</td>
  </tr>

  <tr>
    <th><div align="left">Position (<kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetGraduateAssistantRate.attributes.campusCode}" useShortLabel="true" skipHelpUrl="true" noColon="true"/>)</div></th>
    <th>System Rate</th>
    <th>* Current Rate</th>
  </tr>

  <!-- begin inner loop -->
  <logic:iterate id="graduateAssistantRatesLine" name="KualiForm" property="document.budget.graduateAssistantRates" indexId="ctr"> 
  <c:if test="${i eq 1}"> <html:hidden property="document.budget.graduateAssistantRate[${ctr}].documentNumber" /> 
  <html:hidden property="document.budget.graduateAssistantRate[${ctr}].campusCode" /> 
  <html:hidden property="document.budget.graduateAssistantRate[${ctr}].graduateAssistantRate.campusCode" />
  <html:hidden property="document.budget.graduateAssistantRate[${ctr}].graduateAssistantRate.campus.campusName" /> 
  <html:hidden property="document.budget.graduateAssistantRate[${ctr}].objectId"/> 
  <html:hidden property="document.budget.graduateAssistantRate[${ctr}].versionNumber" /> 
  <html:hidden property="document.budget.graduateAssistantRate[${ctr}].lastUpdateTimestamp" /> 
  </c:if>  

  <html:hidden property="document.budget.graduateAssistantRate[${ctr}].graduateAssistantRate.campusMaximumPeriodRate[${i}]"/>
  <tr>
    <td>${graduateAssistantRatesLine.graduateAssistantRate.campus.campusName} ( ${graduateAssistantRatesLine.graduateAssistantRate.campusCode} )</td>
    <td><div align="center"> $<bean:write name="graduateAssistantRatesLine" property="graduateAssistantRate.campusMaximumPeriodRate[${i}]" /> </div></td>
    <td><div align="center"> $ 
    <c:choose> 
    <c:when test="${i eq 1}"> 
    <kul:htmlControlAttribute property="document.budget.graduateAssistantRate[${ctr}].campusMaximumPeriod${i}Rate" attributeEntry="${budgetGraduateFringeRateAttributes.campusMaximumPeriod1Rate}" readOnly="${viewOnly}" styleClass="amount"/> 
    </c:when> 
    <c:when test="${i eq 2}"> 
    <kul:htmlControlAttribute property="document.budget.graduateAssistantRate[${ctr}].campusMaximumPeriod${i}Rate" attributeEntry="${budgetGraduateFringeRateAttributes.campusMaximumPeriod2Rate}" readOnly="${viewOnly}" styleClass="amount"/> 
    </c:when> 
    <c:when test="${i eq 3}"> 
    <kul:htmlControlAttribute property="document.budget.graduateAssistantRate[${ctr}].campusMaximumPeriod${i}Rate" attributeEntry="${budgetGraduateFringeRateAttributes.campusMaximumPeriod3Rate}" readOnly="${viewOnly}" styleClass="amount"/> 
    </c:when> 
    <c:when test="${i eq 4}"> 
    <kul:htmlControlAttribute property="document.budget.graduateAssistantRate[${ctr}].campusMaximumPeriod${i}Rate" attributeEntry="${budgetGraduateFringeRateAttributes.campusMaximumPeriod4Rate}" readOnly="${viewOnly}" styleClass="amount"/> 
    </c:when> 
    <c:when test="${i eq 5}"> 
    <kul:htmlControlAttribute property="document.budget.graduateAssistantRate[${ctr}].campusMaximumPeriod${i}Rate" attributeEntry="${budgetGraduateFringeRateAttributes.campusMaximumPeriod5Rate}" readOnly="${viewOnly}" styleClass="amount"/> 
    </c:when> 
    <c:when test="${i eq 6}"> 
    <kul:htmlControlAttribute property="document.budget.graduateAssistantRate[${ctr}].campusMaximumPeriod${i}Rate" attributeEntry="${budgetGraduateFringeRateAttributes.campusMaximumPeriod6Rate}" readOnly="${viewOnly}" styleClass="amount"/> 
    </c:when> 
    </c:choose> 
    </div></td>
  </tr>
  <!-- end Inner loop -->
  </logic:iterate> 
  </c:forEach>
  <c:if test="${not viewOnly}">
  <tr align="left">
    <th height="22">&nbsp;</th>
    <th height="22" colspan="2"><div align="center"><html:image property="methodToCall.copySystemGraduateAssistantLines.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-copsysrates.gif" alt="copy system rate"/></div></th>
  </tr>
  </c:if>
</table>
<div align="right">*required&nbsp;&nbsp;&nbsp;<br></div>


