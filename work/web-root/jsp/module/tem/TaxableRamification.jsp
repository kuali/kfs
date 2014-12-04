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
	<kul:superUserActions />
    <kul:panelFooter />
    <sys:documentControls transactionalDocument="false" />
</kul:documentPage>
