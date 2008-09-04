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
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

<kul:documentPage showDocumentInfo="true"  htmlFormAction="camsAssetPayment"  documentTypeName="AssetPaymentDocument" renderMultipart="true"  showTabButtons="true">
    <kfs:hiddenDocumentFields />
  	<html:hidden property="document.capitalAssetNumber"/>
	<html:hidden property="document.nextSourceLineNumber"/>
	
    <kfs:documentOverview editingMode="${KualiForm.editingMode}" />

    <cams:assetPayments /> 

	<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		sourceAccountingLinesOnly="true"
		isOptionalFieldsInNewRow="true"		
		optionalFields="purchaseOrderNumber,requisitionNumber,expenditureFinancialSystemOriginationCode,expenditureFinancialDocumentNumber,expenditureFinancialDocumentTypeCode,expenditureFinancialDocumentPostedDate,postingYear,postingPeriodCode"		
		extraHiddenFields=",paymentApplicationDate,transferPaymentIndicator,sequenceNumber"
		forcedReadOnlyFields="${KualiForm.forcedReadOnlyFields}">
	</fin:accountingLines>
	
	<cams:viewPaymentInProcessByAsset assetPaymentAssetDetail="${KualiForm.document.assetPaymentAssetDetail}" assetPaymentDetail="${KualiForm.document.sourceAccountingLines}" />
	
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:panelFooter />
    <kfs:documentControls transactionalDocument="${documentEntry.transactionalDocument}" />
</kul:documentPage>
