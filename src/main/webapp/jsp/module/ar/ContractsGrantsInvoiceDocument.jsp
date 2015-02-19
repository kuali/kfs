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

	<c:set var="readOnlyForCorrectionDocument" value="${KualiForm.document.correctionDocument}" />
	<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] || readOnlyForCorrectionDocument}" />
	
	
		
<kul:documentPage showDocumentInfo="true"
	documentTypeName="CINV"
	htmlFormAction="arContractsGrantsInvoiceDocument"
	renderMultipart="true" showTabButtons="true">

	<sys:hiddenDocumentFields />

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
	
	<ar:contractsGrantsInvoiceOrganization
		documentAttributes="${DataDictionary.ContractsGrantsInvoiceDocument.attributes}"
		readOnly="${readOnly}" />
	
	<ar:invoiceGeneral readOnly="${readOnly}" />
		
	<c:choose>
		<c:when test="${KualiForm.document.invoiceGeneralDetail.billingFrequencyCode == ArConstants.MILESTONE_BILLING_SCHEDULE_CODE}">
			<ar:invoiceMilestones />
		</c:when>
		<c:when	test="${KualiForm.document.invoiceGeneralDetail.billingFrequencyCode == ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE}">
			<ar:invoiceBills />
		</c:when>
		<c:otherwise>
			<ar:invoiceDetails readOnly="${readOnly}"/>
			<ar:invoiceAccountDetails />
		</c:otherwise>
	</c:choose>

	<c:if test="${!KualiForm.document.correctionDocument}">
		<ar:invoiceSuspensionCategories />
	</c:if>

	<c:if test="${!KualiForm.document.correctionDocument && KualiForm.document.invoiceGeneralDetail.billingFrequencyCode != ArConstants.LOC_BILLING_SCHEDULE_CODE}">
		<ar:invoiceTransmissionDetails readOnly="${readOnly}"/>
	</c:if>

	<gl:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />
	
	<kul:superUserActions />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="true"
		extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
