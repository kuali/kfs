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
	
    <ar:accountsReceivableDocumentHeader/>	
	
    <ar:customerInvoice
        documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" readOnly="${readOnly}" />
        
        
    <%--
	<ar:customerInvoiceDetails
        documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}"
        customerInvoiceDetailAttributes="${DataDictionary.CustomerInvoiceDetail.attributes}"
        readOnly="${readOnly}" />
     --%>
     
	<fin:accountingLines
	    editingMode="${KualiForm.editingMode}"
	    editableAccounts="${KualiForm.editableAccounts}"
	    optionalFields="invoiceItemNumber,invoiceItemQuantity,invoiceItemUnitOfMeasureCode,invoiceItemUnitPrice,invoiceItemServiceDate,invoiceItemCode,invoiceItemDescription"
	    isOptionalFieldsInNewRow="true"
	    sourceAccountingLinesOnly="true" />
		            
	<kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"  allowsNoteFYI="true"/> 

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
