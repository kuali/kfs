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

<c:set var="readOnly"
	value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
	
<kul:documentPage showDocumentInfo="true"
	documentTypeName="CorporateReorganizationDocument"
	htmlFormAction="endowCorporateReorganizationDocument" renderMultipart="true" 
	showTabButtons="true">

    <c:if test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

	<endow:endowmentDocumentOverview editingMode="${KualiForm.editingMode}" 
	                                 endowDocAttributes="${DataDictionary.CorporateReorganizationDocument.attributes}" />
	
	<sys:hiddenDocumentFields isFinancialDocument="false" />
     
    <endow:endowmentTransactionalDocumentDetails
         documentAttributes="${DataDictionary.CorporateReorganizationDocument.attributes}" 
         readOnly="${readOnly}" 
         subTypeReadOnly="true"
         tabTitle="Corporate Reorganization Details"
         headingTitle="Corporate Reorganization Details"
         summaryTitle="Corporate Reorganization Details"
         />

    <endow:endowmentSecurityDetailsSection showTarget="true" showSource="true" showRegistrationCode="true" openTabByDefault="true" showLabels="true" />
	<endow:endowmentTransactionLinesSection hasSource="true" hasTarget="true" hasUnits="true" isTransAmntReadOnly="true" /> 
                   
    <endow:endowmentTaxLotLine 
    	documentAttributes="${DataDictionary.EndowmentTransactionTaxLotLine.attributes}" 
    	isSource="true"
    	isTarget="true"
    	displayHoldingCost="true"
    	displayGainLoss="false"
    	readOnly="${readOnly}"
    	showSourceDeleteButton="true"
    	showTargetDeleteButton="false"/>

 	<kul:notes /> 

    <kul:adHocRecipients />

	<kul:routeLog />

	<kul:superUserActions />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}" extraButtons="${KualiForm.extraButtons}" />
</kul:documentPage>
