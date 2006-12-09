<%--
 Copyright 2006 The Kuali Foundation.
 
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
<%@ include file="/jsp/core/tldHeader.jsp"%>
<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiProcurementCardDocument"
	htmlFormAction="financialProcurementCard" renderMultipart="true"
	showTabButtons="true">

	<html:hidden
		property="document.procurementCardHolder.documentNumber" />
	<html:hidden property="document.procurementCardHolder.cardHolderName" />
	<html:hidden
		property="document.procurementCardHolder.cardHolderAlternateName" />
	<html:hidden
		property="document.procurementCardHolder.cardHolderLine1Address" />
	<html:hidden
		property="document.procurementCardHolder.cardHolderLine2Address" />
	<html:hidden
		property="document.procurementCardHolder.cardHolderCityName" />
	<html:hidden
		property="document.procurementCardHolder.cardHolderStateCode" />
	<html:hidden
		property="document.procurementCardHolder.cardHolderZipCode" />
	<html:hidden
		property="document.procurementCardHolder.cardHolderWorkPhoneNumber" />
	<html:hidden property="document.procurementCardHolder.cardLimit" />
	<html:hidden
		property="document.procurementCardHolder.cardCycleAmountLimit" />
	<html:hidden
		property="document.procurementCardHolder.cardCycleVolumeLimit" />
	<html:hidden property="document.procurementCardHolder.cardStatusCode" />
	<html:hidden property="document.procurementCardHolder.cardNoteText" />
	<html:hidden
		property="document.procurementCardHolder.chartOfAccountsCode" />
	<html:hidden property="document.procurementCardHolder.accountNumber" />
	<html:hidden property="document.procurementCardHolder.subAccountNumber" />
	<html:hidden property="document.procurementCardHolder.versionNumber" />

	<kul:hiddenDocumentFields />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />

	<fin:procurementCardAccountingLines
		editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}" />

	<kul:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
