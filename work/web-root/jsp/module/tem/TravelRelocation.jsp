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

<script language="javascript" src="scripts/module/tem/common.js"></script>

<c:set var="fullEntryMode" value="${KualiForm.editingMode['fullEntry']}" scope="request" />
<c:set var="documentTitle" value="${'TravelRelocationDocument'}" />

<c:if test="${KualiForm.openPaymentInformationWindow}">
	<script type="text/javascript">
		window.open("${ConfigProperties.application.url}/${KualiForm.travelPaymentFormAction}.do?methodToCall=paymentInformationStart","_blank");
	</script>
</c:if>

<kul:documentPage showDocumentInfo="true"
    documentTypeName="TravelRelocationDocument"
    htmlFormAction="temTravelRelocation" renderMultipart="true"
    showTabButtons="true">
       
    <sys:documentOverview editingMode="${KualiForm.editingMode}" />
    <c:if test="${showReports}">
    	<tem-relo:reports/>
   	</c:if>
    <tem-relo:movAndReloOverview/>
    <tem:specialCircumstances />
    <tem:expenses />
    <!-- TODO: Need to add importexpenses tab-->
    <tem-relo:expenseTotals/>
    <tem:summaryByObjectCode />
    <tem:assignAccounts />
    <tem:accountingLines />
    <gl:generalLedgerPendingEntries />
    <tem:relatedDocuments />
    
    <kul:notes attachmentTypesValuesFinderClass="${DataDictionary.TravelRelocationDocument.attachmentTypesValuesFinderClass}" />

	<kul:adHocRecipients />

    <kul:routeLog />
	
	<kul:panelFooter />
	
	<sys:documentControls
    transactionalDocument="${documentEntry.transactionalDocument}"
    extraButtons="${KualiForm.extraButtons}" />
 
</kul:documentPage>
