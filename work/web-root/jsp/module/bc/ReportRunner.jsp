<%--
 Copyright 2007-2008 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetReportRunner" renderMultipart="true"
	showTabButtons="false"
	docTitle="Document Reports and Exports"
    transactionalDocument="false"
	>
              <html:hidden property="returnAnchor" />
              <html:hidden property="returnFormKey" />
              <html-el:hidden name="KualiForm" property="backLocation" />
              <html:hidden property="documentNumber" />
              <html:hidden property="universityFiscalYear" />
              <html:hidden property="chartOfAccountsCode" />
              <html:hidden property="accountNumber" />
              <html:hidden property="subAccountNumber" />
    <html:hidden property="mainWindow" />
	
    <bc:budgetConstructionReportDumpLines />

	<kul:panelFooter />

    <div id="globalbuttons" class="globalbuttons">
<%--
        <html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_genpdf.gif" styleClass="globalbuttons" property="methodToCall.performReport" title="Perform Report" alt="Perform Report" onclick="excludeSubmitRestriction=true"/>
--%>
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.close" onclick="excludeSubmitRestriction=true" title="close" alt="close"/>
    </div>

</kul:page>
