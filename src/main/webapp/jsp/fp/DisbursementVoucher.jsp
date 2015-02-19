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

<kul:documentPage showDocumentInfo="true"
	htmlFormAction="financialDisbursementVoucher"
	documentTypeName="DisbursementVoucherDocument"
	renderMultipart="true" showTabButtons="true">
	
	<fp:dvPrintCoverSheet />
	<script type="text/javascript">
		function clearSpecialHandlingTab() {
		var prefix = "document.dvPayeeDetail.";
		var ctrl;
		
		ctrl = kualiElements[prefix+"disbVchrSpecialHandlingPersonName"];
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "disbVchrSpecialHandlingCityName"]
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "disbVchrSpecialHandlingLine1Addr"];
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "disbVchrSpecialHandlingStateCode"];
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "disbVchrSpecialHandlingLine2Addr"];
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "disbVchrSpecialHandlingZipCode"];
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "disbVchrSpecialHandlingCountryCode"];
		ctrl.value = "";
	   }
	</script>
	<sys:paymentMessages />
	
	<c:set var="canEdit" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" scope="request" />
	<c:set var="fullEntryMode" value="${canEdit && KualiForm.editingMode['fullEntry']}" scope="request" />

	<c:set var="frnEntryMode" value="${canEdit && KualiForm.editingMode['frnEntry']}" scope="request" />
	<c:set var="travelEntryMode" value="${canEdit && KualiForm.editingMode['travelEntry']}" scope="request" />
	
	<c:set var="wireEntryMode" value="${canEdit && KualiForm.editingMode['wireEntry']}" scope="request" />
	<c:set var="taxEntryMode" value="${canEdit && KualiForm.editingMode['taxEntry']}" scope="request" />
	
	<c:set var="payeeEntryMode" value="${canEdit && KualiForm.editingMode['payeeEntry']}" scope="request" />
	
	<c:set var="paymentHandlingEntryMode" value="${canEdit && KualiForm.editingMode['paymentHandlingEntry']}" scope="request" />
	<c:set var="voucherDeadlineEntryMode" value="${canEdit && KualiForm.editingMode['voucherDeadlineEntry']}" scope="request" />
	<c:set var="specialHandlingChangingEntryMode" value="${canEdit && KualiForm.editingMode['specialHandlingChangingEntry']}" scope="request" />
	<c:set var="immediateDisbursementEntryMode" value="${canEdit && KualiForm.editingMode['immediateDisbursementEntryMode']}" scope="request"/>
	
	<sys:documentOverview editingMode="${KualiForm.editingMode}" includeBankCode="true"
	  bankProperty="document.disbVchrBankCode" 
	  bankObjectProperty="document.bank"
	  disbursementOnly="true" />
	  
	<fp:dvPayment />

	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
		<sys-java:accountingLines>
			<sys-java:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
		</sys-java:accountingLines>
	</kul:tab>
	
	<fp:dvContact />
    <fp:dvSpecialHandling />
	<fp:dvNRATax />
	<fp:wireTransfer />
	<fp:foreignDraft />
	<fp:dvNonEmployeeTravel />
	<fp:dvPrePaidTravel />
    <fp:dvPDPStatus />
	<gl:generalLedgerPendingEntries />
	<kul:notes attachmentTypesValuesFinderClass="${documentEntry.attachmentTypesValuesFinderClass}" />
	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:superUserActions />

	<kul:panelFooter />
	
	<sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}"  extraButtons="${KualiForm.extraButtons}" />
</kul:documentPage>
