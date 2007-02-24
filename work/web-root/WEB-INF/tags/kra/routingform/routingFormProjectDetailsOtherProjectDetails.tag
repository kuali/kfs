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

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${empty editingMode['fullEntry']}" />
<c:set var="routingFormQuestionAttributes" value="${DataDictionary.RoutingFormQuestion.attributes}" />

<kul:tab tabTitle="Other Project Details" defaultOpen="false" transparentBackground="false" tabAuditKey="document.budget.audit.modular.consortium" auditCluster="projectDetailsAuditErrors" >
	<div class="tab-container" align="center" id="G4" style="">
		<div class="h2-container"><h2>Other Project Details</h2></div>
		<table cellpadding=0 cellspacing="0" summary="">
			<tr><td colspan="2" class="nobord">&nbsp;</td></tr>
			<c:forEach items="${KualiForm.routingFormDocument.routingFormQuestions}" var="routingFormQuestion" varStatus="questionStatus">
        		<tr>
              		<td width="20%" class="nobord">
              			<div align="center">
              				<label><html:radio property="document.routingFormQuestion[${questionStatus.index}].yesNoIndicator" value="Y"/> yes</label>
              				<label><html:radio property="document.routingFormQuestion[${questionStatus.index}].yesNoIndicator" value="N"/> no</label>
              			</div>
              		</td>
              		<td class="nobord">
              			${routingFormQuestion.question.questionTypeDescription}
              			<html:hidden property="document.routingFormQuestion[${questionStatus.index}].documentNumber" />
              			<html:hidden property="document.routingFormQuestion[${questionStatus.index}].questionTypeCode" />
              			<html:hidden property="document.routingFormQuestion[${questionStatus.index}].versionNumber" />
              			<html:hidden property="document.routingFormQuestion[${questionStatus.index}].objectId" />
              			<html:hidden property="document.routingFormQuestion[${questionStatus.index}].question.questionTypeCode" />
              			<html:hidden property="document.routingFormQuestion[${questionStatus.index}].question.questionTypeDescription" />
              		</td>
              	</tr>
			</c:forEach>
			<tr><td colspan="2" class="nobord">&nbsp;</td></tr>
		</table>
	</div>
</kul:tab>
