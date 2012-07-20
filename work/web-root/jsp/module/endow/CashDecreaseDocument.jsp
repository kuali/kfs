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
	documentTypeName="CashDecreaseDocument"
	htmlFormAction="endowCashDecreaseDocument" renderMultipart="true"
	showTabButtons="true">

    <c:if test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
	
	<sys:hiddenDocumentFields isFinancialDocument="false" />
     

    <endow:endowmentTransactionalDocumentDetails
         documentAttributes="${DataDictionary.CashDecreaseDocument.attributes}"
         readOnly="${readOnly}"
         subTypeReadOnly="true"
         tabTitle="Cash Decrease Details"
         headingTitle="Cash Decrease Details"
         summaryTitle="Cash Decrease Details" />
 
    <endow:endowmentSecurityDetailsSection showSource="true" showTarget="false" showRegistrationCode="false" openTabByDefault="false" showLabels="false" securityRequired="false"/>          
         
    <endow:endowmentTransactionLinesSection hasSource="true" hasTarget="false" hasUnits="false" isTransAmntReadOnly="false"/>                 
   
	<kul:notes /> 
	
	<kul:routeLog />

	<kul:superUserActions />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="true" extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
	