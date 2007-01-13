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
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<%@ attribute name="includeSummary" required="true" %>

<div class="annotate">
          <table class="annotate-top" cellpadding="0" cellspacing="0" width="100%">
            <tbody><tr>
              <td class="annotate-t"><img src="images/annotate-tl1.gif" alt="" class="annotate-t" align="middle" height="24" width="12">Select View:</td>
              <td class="annotate-t"><div align="right"><img src="images/annotate-tr1.gif" alt="" align="middle" height="24" width="12"></div></td>
            </tr>
          </tbody></table>
          <div class="annotate-container"> <kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetTask.attributes.budgetTaskName}" skipHelpUrl="true" readOnly="true" />
            <html:hidden property="previousTaskNumber" value="${KualiForm.currentTaskNumber}" />
            <html:select property="currentTaskNumber">
              <c:set var="budgetTasks" value="${KualiForm.budgetDocument.budget.tasks}"/>
              <html:options collection="budgetTasks" property="budgetTaskSequenceNumber" labelProperty="budgetTaskName"/>
              <c:if test="${includeSummary && KualiForm.document.taskListSize > 1}"><html:option value="0">Summary</html:option></c:if>
            </html:select>

&nbsp;&nbsp; Period:
            <html:hidden property="previousPeriodNumber" value="${KualiForm.currentPeriodNumber}" />
            <html:select property="currentPeriodNumber" >
              <c:set var="budgetPeriods" value="${KualiForm.budgetDocument.budget.periods}"/>
              <html:options collection="budgetPeriods" property="budgetPeriodSequenceNumber" labelProperty="budgetPeriodLabel"/>
              <c:if test="${includeSummary && KualiForm.document.periodListSize > 1}"><html:option value="0">Summary</html:option></c:if>
            </html:select>
&nbsp; &nbsp;<html:image property="methodToCall.update" src="images/tinybutton-updateview.gif" align="middle" styleClass="tinybutton" alt="update"/></div>
          <table width="100%" cellpadding="0"  cellspacing="0" class="annotate-top">
            <tr>
              <td class="annotate-b"><img src="images/annotate-bl1.gif" alt="" width=12 height=24></td>
              <td class="annotate-b"><div align="right"><img src="images/annotate-br1.gif" alt="" width=12 height=24></div></td>
            </tr>
          </table>
        </div>
