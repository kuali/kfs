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

<c:set var="canEdit" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" scope="request" />
<c:set var="fullEntryMode" value="${KualiForm.editingMode['fullEntry']}" scope="request" />
<c:set var="advancePaymentMode" value="${KualiForm.editingMode['advancePaymentEntry']}" scope="request"/>
<c:set var="actualExpenseTaxableMode" value="${KualiForm.editingMode['actualExpenseTaxableEntry']}" scope="request"/>

<kul:documentPage showDocumentInfo="true"
    documentTypeName="RELO"
    htmlFormAction="temTravelRelocation" renderMultipart="true"
    showTabButtons="true">
       
    <sys:documentOverview editingMode="${KualiForm.editingMode}" includeBankCode="true"
	  bankProperty="document.financialDocumentBankCode" bankObjectProperty="document.bank" disbursementOnly="true" />
    <c:if test="${showReports}">
    	<tem-relo:reports/>
   	</c:if>
	
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
	
    <tem-relo:movAndReloOverview/>
    <tem:specialCircumstances />
    <tem:expenses />
    <!-- TODO: Need to add importexpenses tab-->
    <tem-relo:expenseTotals/>
    <tem:summaryByObjectCode />
    <tem:assignAccounts />
    <tem:accountingLines />
	<tem:travelPayment/>
	<tem:travelPaymentPDPStatus travelPaymentProperty="travelPayment" pdpPaymentDocumentType="${KualiForm.document.paymentDocumentType}"/>
    <gl:generalLedgerPendingEntries />
    <tem:relatedDocuments />
    
    <kul:notes attachmentTypesValuesFinderClass="${DataDictionary.TravelRelocationDocument.attachmentTypesValuesFinderClass}" />

	<kul:adHocRecipients />

    <kul:routeLog />
	<kul:superUserActions />
	<kul:panelFooter />
	
	<sys:documentControls
    transactionalDocument="${documentEntry.transactionalDocument}"
    extraButtons="${KualiForm.extraButtons}" />
 
</kul:documentPage>
