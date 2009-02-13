<%--
 Copyright 2005-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	htmlFormAction="financialDisbursementVoucher"
	documentTypeName="DisbursementVoucherDocument"
	renderMultipart="true" showTabButtons="true">
	<dv:dvPrintCoverSheet />
	<dv:dvMessages />
	
	<c:if test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
		<c:set var="fullEntryMode" value="true" scope="request" />
	</c:if>
	
	<c:if test="${fullEntryMode && KualiForm.editingMode['frnEntry']}">
		<c:set var="frnEntryMode" value="true" scope="request" />
	</c:if>	
	<c:if test="${fullEntryMode && KualiForm.editingMode['travelEntry']}">
		<c:set var="travelEntryMode" value="true" scope="request" />
	</c:if>
	
	<c:if test="${fullEntryMode && KualiForm.editingMode['wireEntry']}">
		<c:set var="wireEntryMode" value="true" scope="request" />
	</c:if>
	<c:if test="${fullEntryMode && KualiForm.editingMode['taxEntry']}">
		<c:set var="taxEntryMode" value="true" scope="request" />
	</c:if>
	
	<c:if test="${fullEntryMode && KualiForm.editingMode['adminEntry']}">
		<c:set var="adminEntryMode" value="true" scope="request" />
	</c:if>
	<c:if test="${fullEntryMode && KualiForm.editingMode['payeeEntry']}">
		<c:set var="payeeEntryMode" value="true" scope="request" />
	</c:if>
	
	<kfs:documentOverview editingMode="${KualiForm.editingMode}" includeBankCode="true"
	  bankProperty="document.disbVchrBankCode" 
	  bankObjectProperty="document.bank"
	  disbursementOnly="true" />
	  
	<dv:dvPayment />

	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
		<sys:accountingLines>
			<sys:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
		</sys:accountingLines>
	</kul:tab>
	
	<dv:dvContact />
    <dv:dvSpecialHandling />
	<dv:dvNRATax />
	<dv:dvWireTransfer />
	<dv:dvForeignDraft />
	<dv:dvNonEmployeeTravel />
	<dv:dvPrePaidTravel />
    <dv:dvPDPStatus />
	<gl:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />
	
	<kfs:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
