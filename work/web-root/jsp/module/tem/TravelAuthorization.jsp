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
<c:set var="canEdit" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" scope="request" />
<c:set var="fullEntryMode" value="${KualiForm.editingMode['fullEntry']}" scope="request" />
<c:set var="advancePaymentMode" value="${KualiForm.editingMode['advancePaymentEntry']}" scope="request"/>
<c:set var="actualExpenseTaxableMode" value="${KualiForm.editingMode['actualExpenseTaxableEntry']}" scope="request"/>
<c:set var="clearAdvanceMode" value="${KualiForm.editingMode['clearAdvanceMode']}" scope="request"/>

<kul:documentPage showDocumentInfo="true"
    documentTypeName="${KualiForm.docTypeName}"
    htmlFormAction="temTravelAuthorization" renderMultipart="true"
    showTabButtons="true">
    
<script language="javascript" src="dwr/interface/TravelDocumentService.js"></script> 
<script language="javascript" src="dwr/interface/TravelAuthorizationService.js"></script>     
<script language="javascript" src="scripts/module/tem/common.js"></script>

	<tem-ta:dvNotFinalizedMessage/>
    <sys:documentOverview editingMode="${KualiForm.editingMode}" includeBankCode="true"
	  bankProperty="document.financialDocumentBankCode" 
	  bankObjectProperty="document.bank"
	  disbursementOnly="true" />
	  
	<script type="text/javascript">
		function clearSpecialHandlingTab() {
		var prefix = "document.travelPayment.";
		var ctrl;
		
		ctrl = kualiElements[prefix + "specialHandlingCityName"]
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "specialHandlingLine1Addr"];
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "specialHandlingStateCode"];
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "specialHandlingLine2Addr"];
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "specialHandlingZipCode"];
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "specialHandlingCountryCode"];
		ctrl.value = "";
	   }
	</script>
	<sys:paymentMessages />
 
    <tem-ta:tripOverview/>
	<c:if test="${KualiForm.docTypeName!=TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT}"> 
		<kul:tab tabTitle="Travel Advance" defaultOpen="false" tabErrorKey="${TemKeyConstants.TRVL_AUTH_TRVL_ADVANCE_ERRORS}">
			<div class="tab-container" align="left">
				<h3>Travel Advance</h3>
				<tem-ta:travelAdvance travelAdvanceProperty="document.travelAdvance" />
				<kul:tab tabTitle="Travel Advance Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
					<sys-java:accountingLines>
						<sys-java:accountingLineGroup newLinePropertyName="newAdvanceAccountingLine" collectionPropertyName="document.advanceAccountingLines" collectionItemPropertyName="document.advanceAccountingLine" attributeGroupName="advance" />
					</sys-java:accountingLines>
				</kul:tab>
				<tem:travelPayment isForAdvance="true"/>
			</div>
		</kul:tab>
	</c:if>
	<c:if test="${KualiForm.showTravelAdvancesForTrip}">
		<tem:travelAdvances />
	</c:if>
    <c:if test="${KualiForm.editingMode['displayEmergencyContactTab']}">
	    <tem-ta:emergencyContact />
    </c:if>
    <tem:specialCircumstances />
    <tem:groupTravel />
    <c:if test="${not empty KualiForm.document.primaryDestinationName}"> 
    	<tem:perDiemExpenses />
    </c:if>
    <tem-ta:expenses />
    <tem-ta:estimateTotal /> 
	<c:if test="${KualiForm.displayImportedExpenseRelatedTab}">
	    <tem:summaryByObjectCode />
	    <tem:assignAccounts />
    </c:if>
    <c:if test="${KualiForm.displayAccountingLines}">
    	<tem:accountingLines />
    </c:if>
    <gl:generalLedgerPendingEntries />
	<tem:relatedDocuments />
    <kul:notes attachmentTypesValuesFinderClass="${DataDictionary.TravelEntertainmentDocument.attachmentTypesValuesFinderClass}" />
    <kul:adHocRecipients />

    <kul:routeLog />
	<kul:superUserActions />
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
