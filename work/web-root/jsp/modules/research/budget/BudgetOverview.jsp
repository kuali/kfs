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
<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBudgetDocument"
	headerTitle="Research Administration - Overview"
	htmlFormAction="researchBudgetOverview" showTabButtons="true"
	headerDispatch="overview" headerTabActive="overview"
	feedbackKey="app.krafeedback.link">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />

	<kra-b:budgetHiddenDocumentFields includeDocumenHeaderIdFields="true"
		includeTaskPeriodLists="true" />

	<kra-b:budgetOverview />

        <div id="globalbuttons" class="globalbuttons">
          <c:if test="${KualiForm.documentActionFlags.canRoute}">
	          <html:image src="images/buttonsmall_complete.gif" styleClass="globalbuttons" property="methodToCall.route" alt="Complete Budget" />
	        </c:if>
        </div>

</kul:documentPage>
