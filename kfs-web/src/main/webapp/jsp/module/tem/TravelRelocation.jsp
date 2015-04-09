<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:set var="canEdit" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" scope="request" />
<c:set var="fullEntryMode" value="${KualiForm.editingMode['fullEntry']}" scope="request" />
<c:set var="advancePaymentMode" value="${KualiForm.editingMode['advancePaymentEntry']}" scope="request"/>
<c:set var="expenseTaxableMode" value="${KualiForm.editingMode['expenseTaxableEntry']}" scope="request"/>
<c:set var="conversionRateEntryMode" value="${KualiForm.editingMode['conversionRateEntry']}" scope="request"/>
<c:set var="frnEntryMode" value="${canEdit && KualiForm.editingMode['frnEntry']}" scope="request" />
<c:set var="wireEntryMode" value="${canEdit && KualiForm.editingMode['wireEntry']}" scope="request" />
<c:set var="lookupRequesterMode" value="${canEdit && KualiForm.editingMode['requesterLooupMode']}" scope="request" />

<kul:documentPage showDocumentInfo="true"
    documentTypeName="RELO"
    htmlFormAction="temTravelRelocation" renderMultipart="true"
    showTabButtons="true">
	
	<script language="javascript" src="dwr/interface/TravelExpenseService.js"></script>
	<script language="javascript" src="scripts/module/tem/common.js"></script>
	<script language="javascript" src="scripts/module/tem/objectInfo.js"></script>
       
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
     <c:if test="${KualiForm.displayAccountingLines}">
    	<tem:accountingLines />
    </c:if>
	<tem:travelPayment/>
	<tem:travelPaymentPDPStatus travelPaymentProperty="travelPayment" pdpPaymentDocumentType="${KualiForm.document.achCheckDocumentType}" displayCorporateCardExtraction="${KualiForm.document.corporateCardPayable}"/>
    <gl:generalLedgerPendingEntries />
    <tem:relatedDocuments />
	<tem:agencyLinks/>
    
    <kul:notes attachmentTypesValuesFinderClass="${DataDictionary.TravelRelocationDocument.attachmentTypesValuesFinderClass}" />

	<kul:adHocRecipients />

    <kul:routeLog />
	<kul:superUserActions />
	<kul:panelFooter />
	
	<sys:documentControls
    transactionalDocument="${documentEntry.transactionalDocument}"
    extraButtons="${KualiForm.extraButtons}" />
 
</kul:documentPage>
