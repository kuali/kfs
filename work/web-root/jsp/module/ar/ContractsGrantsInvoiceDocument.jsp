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

	<ar:cgCustomerInvoiceOrganization
		documentAttributes="${DataDictionary.ContractsGrantsInvoiceDocument.attributes}"
		readOnly="${readOnly}" />

	<ar:cgCustomerInvoiceGeneral
		documentAttributes="${DataDictionary.ContractsGrantsInvoiceDocument.attributes}"
		readOnly="${readOnly}" />
	<c:if
		test="${KualiForm.document.invoiceGeneralDetail.billingFrequency == ArConstants.MILESTONE_BILLING_SCHEDULE_CODE}">

		<ar:invoiceMilestones />
	</c:if>

	<c:if
		test="${KualiForm.document.invoiceGeneralDetail.billingFrequency == ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE}">

		<ar:invoiceBills />
	</c:if>

	<c:if
		test="${KualiForm.document.invoiceGeneralDetail.billingFrequency != ArConstants.MILESTONE_BILLING_SCHEDULE_CODE}">
		<c:if
			test="${KualiForm.document.invoiceGeneralDetail.billingFrequency != ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE}">

			<ar:invoiceDetails readOnly="${readOnly}"/>
			<ar:invoiceAccountDetails />
		</c:if>
	</c:if>

	<ar:invoiceSuspensionCategories />
	
	<ar:invoiceTypeAssignments readOnly="${readOnly}"/>

	<gl:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />
	
	<kul:superUserActions />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="true"
		extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
