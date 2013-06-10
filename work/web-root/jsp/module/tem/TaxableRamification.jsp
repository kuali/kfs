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

<c:set var="documentAttributes"	value="${DataDictionary.TaxableRamificationDocument.attributes}" />

<c:set var="documentTypeName" value="TaxableRamificationDocument"/>
<c:set var="htmlFormAction" value="temTaxableRamification"/>

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:documentPage showDocumentInfo="true" documentTypeName="${documentTypeName}"
	htmlFormAction="${htmlFormAction}" renderMultipart="true"
    showTabButtons="true">
    
    <sys:hiddenDocumentFields isFinancialDocument="false" />   
    <sys:documentOverview editingMode="${KualiForm.editingMode}" />
    
	<c:set var="tabTitle" value="Taxable Ramification Notice"/>
	<kul:tab tabTitle="${tabTitle}" defaultOpen="true">
		<div class="tab-container" align=center>
		<h3></h3>
		<table cellpadding=0 class="datatable" summary="${tabTitle}">
			<c:if test="${!readOnly}">
			<tr>	
				<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.travelDocumentIdentifier}"
					horizontal="true" width="20%"  labelFor="document.travelDocumentIdentifier"/>

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.travelDocumentIdentifier}"	property="document.travelDocumentIdentifier" readOnly="${readOnly}" /> 
				</td>
			</tr>
			<tr>	
				<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.travelAdvanceDocumentNumber}"
					horizontal="true" width="20%"  labelFor="document.travelAdvanceDocumentNumber"/>

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.travelAdvanceDocumentNumber}" property="document.travelAdvanceDocumentNumber" readOnly="${readOnly}" /> 
				</td>
			</tr>
			<tr>	
				<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.travelerDetailId}"
					horizontal="true" width="20%"  labelFor="document.travelerDetailId"/>

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.travelerDetailId}" property="document.travelerDetailId" readOnly="${readOnly}" /> 
				</td>
			</tr>			
			</c:if>	
					
			<tr>	
				<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.arInvoiceDocNumber}"
					horizontal="true" width="20%"  labelFor="document.arInvoiceDocNumber"/>

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.arInvoiceDocNumber}"	property="document.arInvoiceDocNumber" readOnly="${readOnly}" /> 
				</td>
			</tr>
			<tr>	
				<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.dueDate}"
					horizontal="true" width="20%"  labelFor="document.dueDate"/>

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.dueDate}" property="document.dueDate" readOnly="${readOnly}" /> 
				</td>
			</tr>
			
			<tr>	
				<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.invoiceAmount}"
					horizontal="true" width="20%"  labelFor="document.invoiceAmount"/>

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceAmount}" property="document.invoiceAmount" readOnly="${readOnly}" /> 
				</td>
			</tr>						
			<tr>	
				<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.openAmount}"
					horizontal="true" width="20%"  labelFor="document.openAmount"/>

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.openAmount}" property="document.openAmount" readOnly="${readOnly}" /> 
				</td>
			</tr>
			
			<tr>
				<td class="datacell" colspan="2">
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.taxableRamificationNotice}" property="document.taxableRamificationNotice" readOnly="${readOnly}" /> 
				</td>
			</tr>			
		</table>
		</div>				
	</kul:tab>
	
	<tem:relatedDocuments noNewRelatedDocument="true"/>
	
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:panelFooter />
    <sys:documentControls transactionalDocument="false" />
</kul:documentPage>
