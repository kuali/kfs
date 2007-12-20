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

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiRoutingFormDocument"
	htmlFormAction="researchRoutingFormProjectDetails"
	headerDispatch="save" feedbackKey="app.feedback.link"
	headerTabActive="projectdetails" showTabButtons="true">
	
<kra-rf:routingFormHiddenDocumentFields />

  <html:hidden property="document.institutionCostShareNextSequenceNumber" />
  <html:hidden property="document.otherCostShareNextSequenceNumber" />
  <html:hidden property="document.subcontractorNextSequenceNumber" />

	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />
	
	<div align="right">
		<kul:help documentTypeName="${DataDictionary.KualiRoutingFormDocument.documentTypeName}" pageName="Project Details" altText="page help"/>
	</div>

	<kra-rf:routingFormProjectDetailsCostShare editingMode="${KualiForm.editingMode}" />

	<kra-rf:routingFormProjectDetailsSubcontracts editingMode="${KualiForm.editingMode}" />

	<kra-rf:routingFormProjectDetailsOtherInstitutions editingMode="${KualiForm.editingMode}" />

	<kra-rf:routingFormProjectDetailsOtherProjectDetails editingMode="${KualiForm.editingMode}" />
  <table class="b3" summary="" border="0" cellpadding="0" cellspacing="0" width="100%">
          <tbody><tr>
            <td class="footer" align="left"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" class="bl3" height="14" width="12"></td>
            <td class="footer-right" align="right"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" class="br3" height="14" width="12"></td>

          </tr>
        </tbody></table>
        
          <kul:documentControls transactionalDocument="false" suppressRoutingControls="true" viewOnly="${KualiForm.editingMode['viewOnly']}" />
<SCRIPT type="text/javascript">
var kualiForm = document.forms['KualiForm'];
var kualiElements = kualiForm.elements;
</SCRIPT>
<script language="javascript" src="scripts/research/researchDocument.js"></script>
<script language="javascript" src="dwr/interface/SubcontractorService.js"></script>
<script language="javascript" src="dwr/interface/AccountService.js"></script>
        
</kul:documentPage>