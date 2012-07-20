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
	documentTypeName="AssetDecreaseDocument"
	htmlFormAction="endowAssetDecreaseDocument" renderMultipart="true"
	showTabButtons="true">

    <c:if test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

	<endow:endowmentDocumentOverview editingMode="${KualiForm.editingMode}" 
	                                 endowDocAttributes="${DataDictionary.AssetDecreaseDocument.attributes}" />
	
	<sys:hiddenDocumentFields isFinancialDocument="false" />
     
    <endow:endowmentTransactionalDocumentDetails
         documentAttributes="${DataDictionary.AssetDecreaseDocument.attributes}"
         readOnly="${readOnly}"
         subTypeReadOnly="false"
         tabTitle="Asset Decrease Details"
         headingTitle="Asset Decrease Details"
         summaryTitle="Asset Decrease Details" />

    <endow:endowmentSecurityDetailsSection showTarget="false" showSource="true" showRegistrationCode="true" openTabByDefault="true" showLabels="false"/>          
         
    <endow:endowmentTransactionLinesSection hasSource="true" hasTarget="false" hasUnits="true" isTransAmntReadOnly="false"/>     
    
    <endow:endowmentTaxLotLine 
    	documentAttributes="${DataDictionary.EndowmentTransactionTaxLotLine.attributes}" 
    	isSource="true"
    	isTarget="false"
    	displayGainLoss="true"
    	displayHoldingCost="true"
    	showSourceDeleteButton="true"
    	showTargetDeleteButton="false"
    	readOnly="${readOnly}"/>            
        
	<kul:notes /> 
	
	<kul:routeLog />

	<kul:superUserActions />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}" extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
