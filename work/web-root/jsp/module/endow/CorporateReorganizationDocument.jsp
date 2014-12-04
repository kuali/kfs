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
