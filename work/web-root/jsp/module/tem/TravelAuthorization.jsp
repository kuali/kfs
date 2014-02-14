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
<c:set var="advancePolicyMode" value="${KualiForm.editingMode['advancePolicyEntry']}" scope="request"/>
<c:set var="expenseTaxableMode" value="${KualiForm.editingMode['expenseTaxableEntry']}" scope="request"/>
<c:set var="clearAdvanceMode" value="${KualiForm.editingMode['clearAdvanceMode']}" scope="request"/>
<c:set var="blanketTravelEntryMode" value="${KualiForm.editingMode['blanketTravelEntry']}" scope="request"/>
<c:set var="blanketTravelViewMode" value="${KualiForm.editingMode['blanketTravelView']}" scope="request"/>
<c:set var="conversionRateEntryMode" value="${KualiForm.editingMode['conversionRateEntry']}" scope="request"/>
<c:set var="frnEntryMode" value="${canEdit && KualiForm.editingMode['frnEntry']}" scope="request" />
<c:set var="wireEntryMode" value="${canEdit && KualiForm.editingMode['wireEntry']}" scope="request" />

<kul:documentPage showDocumentInfo="true"
    documentTypeName="${KualiForm.docTypeName}"
    htmlFormAction="temTravelAuthorization" renderMultipart="true"
    showTabButtons="true">
    
<script language="javascript" src="dwr/interface/TravelDocumentService.js"></script> 
<script language="javascript" src="dwr/interface/TravelAuthorizationService.js"></script>     
<script language="javascript" src="dwr/interface/TravelExpenseService.js"></script> 
<script language="javascript" src="scripts/module/tem/common.js"></script>
<script language="javascript" src="scripts/module/tem/objectInfo.js"></script>

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
		<kul:tab tabTitle="Travel Advance" defaultOpen="${KualiForm.defaultTravelAdvanceTab}" tabErrorKey="${TemKeyConstants.TRVL_AUTH_TRVL_ADVANCE_ERRORS}">
			<div class="tab-container" align="left">
				<h3>Travel Advance</h3>
				<tem-ta:travelAdvance travelAdvanceProperty="document.travelAdvance" />
				<kul:tab tabTitle="Travel Advance Accounting Lines" defaultOpen="true" tabErrorKey="${TemKeyConstants.TRVL_AUTH_ADV_ACCT_LINES_ERRORS}">
					<sys-java:accountingLines>
						<sys-java:accountingLineGroup newLinePropertyName="newAdvanceAccountingLine" collectionPropertyName="document.advanceAccountingLines" collectionItemPropertyName="document.advanceAccountingLine" attributeGroupName="advance" />
					</sys-java:accountingLines>
				</kul:tab>
				<tem:travelPayment isForAdvance="true"/>
			</div>
		</kul:tab>
	</c:if>
	<c:if test="${KualiForm.showTravelAdvancesForTrip}">
		<tem:travelAdvances tabTitle="All Advances"/>
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
    <c:if test="${KualiForm.displayAccountingLines}">
    	<tem:accountingLines />
    </c:if>
	<c:if test="${KualiForm.advancePdpStatusTabShown}">
		<tem:travelPaymentPDPStatus travelPaymentProperty="advanceTravelPayment" pdpPaymentDocumentType="${KualiForm.document.travelAdvancePaymentDocumentType}"/>
	</c:if>
    <gl:generalLedgerPendingEntries />
	<tem:relatedDocuments />
    <kul:notes attachmentTypesValuesFinderClass="${DataDictionary.TravelEntertainmentDocument.attachmentTypesValuesFinderClass}" />
    <kul:adHocRecipients />

    <kul:routeLog />
	<kul:superUserActions />
    <kul:panelFooter />
    
    <sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}" extraButtons="${KualiForm.extraButtons}"/>

<script>
	$(document).ready(function() {
		getAllStates();
	});
</script>
      
</kul:documentPage>
