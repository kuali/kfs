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

<c:set var="returnFromEmployeeLookup" value="${KualiForm.refreshCaller == 'travelerLookupable'}" scope="request" />
<c:set var="fullEntryMode" value="${KualiForm.editingMode['fullEntry']}" scope="request" />
<c:set var="documentTitle" value="${KualiForm.docTypeName=='TA'?'TravelAuthorizationDocument':(KualiForm.docTypeName=='TAC'?'TravelAuthorizationCloseDocument':'TravelAuthorizationAmendmentDocument')}" />
<kul:documentPage showDocumentInfo="true"
    documentTypeName="${documentTitle}"
    htmlFormAction="temTravelAuthorization" renderMultipart="true"
    showTabButtons="true">
    
<script language="javascript" src="dwr/interface/TravelDocumentService.js"></script> 
<script language="javascript" src="dwr/interface/TravelAuthorizationService.js"></script>     
<script language="javascript" src="scripts/module/tem/common.js"></script>

	<tem-ta:dvNotFinalizedMessage/>
    <sys:documentOverview editingMode="${KualiForm.editingMode}" />
    <tem-ta:tripOverview/>
    <tem-ta:travelAdvances/>
    <c:if test="${KualiForm.editingMode['displayEmergencyContactTab']}">
	    <tem-ta:emergencyContact />
    </c:if>
    <tem:specialCircumstances />
    <tem:groupTravel />
    <c:if test="${not empty KualiForm.document.primaryDestinationName}"> 
    	<tem:perDiemExpenses />
    </c:if>
    <tem:expenses />
    <tem-ta:estimateTotal /> 
    <tem:summaryByObjectCode />
    <tem:assignAccounts />
    <tem:accountingLines />
    <gl:generalLedgerPendingEntries />
	<tem:relatedDocuments />
    <kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"
	attachmentTypesValuesFinderClass="${DataDictionary.TravelEntertainmentDocument.attachmentTypesValuesFinderClass}" />
    <kul:adHocRecipients />

    <kul:routeLog />

    <kul:panelFooter />
    
    <c:if test="${KualiForm.docTypeName!=TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT}"> 
    <sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}" extraButtons="${KualiForm.extraButtons}"/>
    </c:if>

<script>
	$(document).ready(function() {
		getAllStates();
	});
</script>
      
</kul:documentPage>
