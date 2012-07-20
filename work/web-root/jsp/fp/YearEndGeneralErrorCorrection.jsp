<%--
 Copyright 2005-2006 The Kuali Foundation
 
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

<kul:documentPage showDocumentInfo="true"
	documentTypeName="YearEndGeneralErrorCorrectionDocument"
	htmlFormAction="financialYearEndGeneralErrorCorrection"
	renderMultipart="true" showTabButtons="true">
	
	<sys:documentOverview editingMode="${KualiForm.editingMode}" />

	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
		<sys-java:accountingLines>
			<sys-java:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
			<sys-java:accountingLineGroup newLinePropertyName="newTargetLine" collectionPropertyName="document.targetAccountingLines" collectionItemPropertyName="document.targetAccountingLine" attributeGroupName="target"/>
		</sys-java:accountingLines>
	</kul:tab>

	<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
  	<fp:capitalAccountingLines readOnly="${readOnly}"/>
  	
	<c:if test="${KualiForm.capitalAccountingLine.canCreateAsset}">
		<fp:capitalAssetCreateTab readOnly="${readOnly}"/>
	</c:if>
  	
	<fp:capitalAssetModifyTab readOnly="${readOnly}"/>  
	
	<gl:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:superUserActions />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}" extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
