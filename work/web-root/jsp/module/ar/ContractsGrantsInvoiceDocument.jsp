<%--
 Copyright 2006-2008 The Kuali Foundation
 
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

	<c:set var="readOnlyForCorrectionDocument" value="${KualiForm.document.correctionDocument}" />
	<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] || readOnlyForCorrectionDocument}" />
	
	
		
<kul:documentPage showDocumentInfo="true"
	documentTypeName="CINV"
	htmlFormAction="arContractsGrantsInvoiceDocument"
	renderMultipart="true" showTabButtons="true">

	<sys:hiddenDocumentFields />

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />

	<ar:invoiceGeneral readOnly="${readOnly}" />

	<ar:contractsGrantsInvoiceOrganization
		documentAttributes="${DataDictionary.ContractsGrantsInvoiceDocument.attributes}"
		readOnly="${readOnly}" />

	<ar:contractsGrantsInvoiceGeneral
		documentAttributes="${DataDictionary.ContractsGrantsInvoiceDocument.attributes}"
		readOnly="${readOnly}" />
		
	<c:choose>
		<c:when test="${KualiForm.document.invoiceGeneralDetail.billingFrequency == ArConstants.MILESTONE_BILLING_SCHEDULE_CODE}">
			<ar:invoiceMilestones />
		</c:when>
		<c:when	test="${KualiForm.document.invoiceGeneralDetail.billingFrequency == ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE}">
			<ar:invoiceBills />
		</c:when>
		<c:otherwise>
			<ar:invoiceDetails readOnly="${readOnly}"/>
			<ar:invoiceAccountDetails />
		</c:otherwise>
	</c:choose>

	<ar:invoiceSuspensionCategories />
	
	<ar:invoiceTransmissionDetails readOnly="${readOnly}"/>

	<gl:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />
	
	<kul:superUserActions />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="true"
		extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
