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
<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiRoutingFormDocument"
	htmlFormAction="researchRoutingFormMainPage" headerDispatch="save"
	feedbackKey="app.krafeedback.link" headerTabActive="mainpage"
	showTabButtons="true">

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS_LESS_DOCUMENT}" />

    <kra-rf:routingFormHiddenDocumentFields includeRoutingFormMainPage="true" />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />

	<kra-rf:routingFormMainPageAgencyDeliveryInfo/>

	<kra-rf:routingFormMainPagePersonnelOrg/>

	<kra-rf:routingFormMainPageSubmissionDetails/>

    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
      <tr>
        <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
        <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
      </tr>
    </table>

    <kul:documentControls transactionalDocument="false" suppressRoutingControls="false" viewOnly="${KualiForm.editingMode['viewOnly']}" />

</kul:documentPage>
