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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="routingFormQuestionAttributes" value="${DataDictionary.RoutingFormQuestion.attributes}" />

<kul:tab tabTitle="Other Project Details" defaultOpen="true" transparentBackground="false" tabAuditKey="document.budget.audit.modular.consortium" auditCluster="projectDetailsAuditErrors">
	<div class="tab-container-error"><div class="left-errmsg-tab"><cg:auditErrors cluster="projectDetailsAuditErrors" keyMatch="document.projectDetails.otherProjectDetailsQuestions" isLink="false" includesTitle="true"/></div></div>
	<div class="tab-container" align="center" id="G4" style="">
		<h3>Other Project Details</h3>
		<table cellpadding=0 cellspacing="0" summary="">
			<tr><td colspan="2" class="nobord">&nbsp;</td></tr>
			
			<c:forEach items="${KualiForm.routingFormDocument.routingFormQuestions}" var="routingFormQuestion" varStatus="questionStatus">
        		<tr>
              		<td width="20%" class="nobord">
              			<div align="center">
              				<html:radio title="${routingFormQuestion.question.questionTypeDescription} - yes" property="document.routingFormQuestion[${questionStatus.index}].yesNoIndicator" value="Y"/> yes
              				<html:radio title="${routingFormQuestion.question.questionTypeDescription} - no" property="document.routingFormQuestion[${questionStatus.index}].yesNoIndicator" value="N"/> no
              			</div>
              		</td>
              		<td class="nobord">
              			${routingFormQuestion.question.questionTypeDescription}
              		</td>
              	</tr>
			</c:forEach>
			<tr><td colspan="2" class="nobord">&nbsp;</td></tr>
		</table>
	</div>
</kul:tab>
