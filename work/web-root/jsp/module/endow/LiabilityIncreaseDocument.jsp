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
	documentTypeName="LiabilityIncreaseDocument"
	htmlFormAction="endowLiabilityIncreaseDocument" renderMultipart="true" 
	showTabButtons="true">

    <c:if test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
	
	<endow:endowmentTransactionalDocumentDetails
         documentAttributes="${DataDictionary.LiabilityIncreaseDocument.attributes}" 
         readOnly="${readOnly}" 
         subTypeReadOnly="false"
         tabTitle="Liability Increase Details"
         headingTitle="Liability Increase Details"
         summaryTitle="Liability Increase Details"
         />

    <endow:endowmentSecurityDetailsSection showTarget="true" showSource="false" showRegistrationCode="true" openTabByDefault="true" showLabels="false"/>  
                  
	<endow:endowmentTransactionLinesSection hasSource="false" hasTarget="true" hasUnits="true" isTransAmntReadOnly="false"/> 
                   
    <endow:endowmentTaxLotLine 
    	documentAttributes="${DataDictionary.EndowmentTransactionTaxLotLine.attributes}" 
    	isSource="false"
    	isTarget="true"
    	displayHoldingCost="true"
    	displayGainLoss="false"
    	showSourceDeleteButton="false"
    	showTargetDeleteButton="false"
    	readOnly="${readOnly}"/>

  
	<kul:notes /> 

	<kul:routeLog />

	<kul:superUserActions />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}" extraButtons="${KualiForm.extraButtons}" />
</kul:documentPage>
