<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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

<c:set var="readOnly"
	value="${!empty KualiForm.editingMode['viewOnly']}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="CustomerInvoiceDocument"
	htmlFormAction="arCustomerInvoiceDocument" renderMultipart="true"
	showTabButtons="true">

    <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

	<kul:hiddenDocumentFields />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	
    <ar:customerInvoiceOrganization documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}"  readOnly="${readOnly}"/>	
	
    <ar:customerInvoiceGeneral
        documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" readOnly="${readOnly}" />
        
    <ar:customerInvoiceAddress
        documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" readOnly="${readOnly}" />        
     
	<c:if test="${!empty KualiForm.editingMode['showReceivableFAU']}">
     <ar:customerInvoiceReceivableAccountingLine
      	documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" readOnly="${readOnly}"
      	receivableValuesMap="${KualiForm.document.valuesMap}"  />
    </c:if>
     
	<c:set var="actionInfixVar" value="" scope="request" />
	<c:set var="accountingLineIndexVar" value="" scope="request" />     
	<fin:accountingLines
	    editingMode="${KualiForm.editingMode}"
	    editableAccounts="${KualiForm.editableAccounts}"
	    optionalFields="invoiceItemCode,invoiceItemQuantity,invoiceItemDescription,invoiceItemServiceDate,invoiceItemUnitOfMeasureCode,invoiceItemUnitPrice,taxableIndicator,invoiceItemTaxAmount"
	    extraHiddenFields=",accountsReceivableObjectCode,invoiceItemDiscountLineNumber"
	    isOptionalFieldsInNewRow="true"
	    sourceAccountingLinesOnly="true"
	    forcedReadOnlyFields="${KualiForm.forcedReadOnlyFields}"
	    >
	    
	    <jsp:attribute name="newLineCustomActions">
			<c:set var="refreshMethod"
				value="refreshNewSourceLine"
				scope="request" />
			<html:image
				property="methodToCall.${refreshMethod}"
				src="${ConfigProperties.externalizable.images.url}tinybutton-load.gif" title="Refresh New Source Line"
				alt="Refresh New Source Line" styleClass="tinybutton" />
		</jsp:attribute>		    
		
		<jsp:attribute name="customActions">
			<c:set var="isDiscountLineParent" value="${KualiForm.document.sourceAccountingLines[accountingLineIndexVar].discountLineParent}" />
			<c:set var="isDiscountLine" value="${KualiForm.document.sourceAccountingLines[accountingLineIndexVar].discountLine}" />
		
			<c:set var="recalculateMethod"
				value="recalculateSourceLine.line${accountingLineIndexVar}"
				scope="request" />
			<html:image
				property="methodToCall.${recalculateMethod}.anchoraccounting${actionInfixVar}Anchor"
				src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif" title="Recalculate a Source Accounting Line"
				alt="Recalculate Source Accounting Line" styleClass="tinybutton" /><br/>
				
			<c:if test="${!isDiscountLine && !isDiscountLineParent}">
				<c:set var="discountMethod"
					value="discountSourceLine.line${accountingLineIndexVar}"
					scope="request" />
				<html:image
					property="methodToCall.${discountMethod}.anchoraccounting${actionInfixVar}Anchor"
					src="${ConfigProperties.externalizable.images.url}tinybutton-initiatequote.gif" title="Discount a Source Accounting Line"
					alt="Discount a Source Accounting Line" styleClass="tinybutton" /><br/>
			</c:if>				
		</jsp:attribute>

	</fin:accountingLines>
	
	<gl:generalLedgerPendingEntries />
		            
	<kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"  allowsNoteFYI="true"/> 

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
