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
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>
<%@ attribute name="supportsModular" required="true" %>

<c:set var="budgetTaskAttributes" value="${DataDictionary.BudgetTask.attributes}" /> <c:set var="KraConstants" value="${KraConstants}" />
<c:set var="businessObjectClass" value="${DataDictionary.BudgetTask.businessObjectClass}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>


<kul:tab 
	tabTitle="Tasks/Components" 
	defaultOpen="true" 
	tabErrorKey="document.budget.task*,newTask*" 
	auditCluster="parametersSoftAuditErrors" 
	tabAuditKey="document.budget.audit.parameters.tasks.negativeIdc*">
        
<div class="tab-container" id="G02" style="" align="center">
              
<div class="h2-container"> <span class="subhead-left">
  <a name="Tasks"></a><h2>Tasks/Components</h2>
  </span><span class="subhead-right"> <span class="subhead"><kul:help businessObjectClassName="${businessObjectClass}" altText="help"/></span> </span> </div>
<table cellpadding="0" cellspacing="0" class="datatable" summary=""> 
  <!-- Column headers -->
  <tr>
    <th width="2%" scope="row">&nbsp;</th>
    <th><div align="left"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetTask.attributes.budgetTaskName}" skipHelpUrl="true" noColon="true" /></div></th>
    <th width="5%"><div align="center"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetTask.attributes.budgetTaskOnCampus}" skipHelpUrl="true" noColon="true" /></div></th>
    <th width="5%"><div align="center"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetModular.attributes.budgetModularTaskNumber}" skipHelpUrl="true" noColon="true" /></div></th>
    <c:if test="${not viewOnly}"><th width="2%">Action</th></c:if>
  </tr>
  <!-- Default add line for additional tasks
         shown only if there are < 20 tasks currently attached. -->
  <c:if test="${KualiForm.document.taskListSize lt KraConstants.maximumNumberOfTasks && not viewOnly}">
  <tr>
    <th width="50" align="right" scope="row"><div align="right">add:</div></th>
    <td class="infoline"><html:hidden property="newTask.documentNumber" /> <html:hidden property="newTask.budgetTaskSequenceNumber" /> <html:hidden property="newTask.objectId"/> <html:hidden property="newTask.versionNumber" /> <span> <kul:htmlControlAttribute property="newTask.budgetTaskName" attributeEntry="${budgetTaskAttributes.budgetTaskName}"/> </span> </td>
    <td class="infoline"><div align="center"> <kul:htmlControlAttribute property="newTask.budgetTaskOnCampus" attributeEntry="${budgetTaskAttributes.budgetTaskOnCampus}"/> </div></td>
    <td class="infoline"><div align="center">
        <label><input type="radio" name="document.budget.modularBudget.budgetModularTaskNumber" disabled="true" value="-1" /></label>
      </div></td>
    <td class="infoline"><div align="center"><html:image property="methodToCall.insertTaskLine.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add task line"/></div></td>
  </tr>
  </c:if>
  <!-- Iterate over currently attached tasks. -->
  <logic:iterate id="taskLine" name="KualiForm" property="document.budget.tasks" indexId="ctr">
  <tr>
    <th width="50" scope="row"><div align="right">${ctr+1}:</div></th>
    <td><kul:htmlControlAttribute property="document.budget.task[${ctr}].budgetTaskName" attributeEntry="${budgetTaskAttributes.budgetTaskName}" readOnly="${viewOnly}"/> <html:hidden property="document.budget.task[${ctr}].documentNumber" /> <html:hidden property="document.budget.task[${ctr}].budgetTaskSequenceNumber" /> <html:hidden property="document.budget.task[${ctr}].objectId"/> <html:hidden property="document.budget.task[${ctr}].versionNumber" /> </td>
    <td><div align="center">
        <label> <kul:htmlControlAttribute property="document.budget.task[${ctr}].budgetTaskOnCampus" attributeEntry="${budgetTaskAttributes.budgetTaskOnCampus}" readOnly="${viewOnly}"/> </label>
      </div></td>
    <td><div align="center">
        <label> <html:radio property="document.budget.modularBudget.budgetModularTaskNumber" value="${taskLine.budgetTaskSequenceNumber}" disabled="${not(supportsModular and KualiForm.document.budget.agencyModularIndicator) || viewOnly}"/> </label>
      </div></td>
    <c:if test="${not viewOnly}"><td><div align="center"> <html:image property="methodToCall.deleteTaskLine.line${ctr}.anchorTasks" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete"/> </div></td></c:if>
  </tr>
  </logic:iterate>
  <!-- End of tasks table. -->
</table>
<div align="right">*required&nbsp;&nbsp;&nbsp;</div>
</div>
</kul:tab>
