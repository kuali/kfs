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

<kul:documentPage showDocumentInfo="true"
	documentTypeName="CustomerInvoiceWriteoffDocument"
	htmlFormAction="arCustomerInvoiceWriteoffDocument" renderMultipart="true"
	showTabButtons="true">
	
	<c:set var="displayInitTab" value="${KualiForm.editingMode['displayInitTab']}" scope="request" />
	
	<sys:hiddenDocumentFields isFinancialDocument="false" />

	<!--  Display 1st screen -->
	<c:if test="${displayInitTab}" >
		<ar:customerInvoiceWriteoffInit />
		<kul:panelFooter />
	</c:if>

	<!--  Display 2nd screen -->
	<c:if test="${not displayInitTab}" >
		<kul:documentOverview editingMode="${KualiForm.editingMode}" />

		<ar:customerInvoiceWriteoffHiddenFields />

		<ar:customerInvoiceSummary 
			customerInvoiceDocumentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}"
			documentAttributes="${DataDictionary.CustomerInvoiceWriteoffDocument.attributes}"
			customerAttributes="${DataDictionary.Customer.attributes}"
			customerAddressAttributes="${DataDictionary.CustomerAddress.attributes}"
		 /> 
	
		<ar:customerInvoiceWriteoffDetails />

      	<gl:generalLedgerPendingEntries />
    	<kul:notes />
		<kul:adHocRecipients />
		<kul:routeLog />
		<kul:superUserActions />
		<kul:panelFooter />
	</c:if>

	<c:set var="extraButtons" value="${KualiForm.extraButtons}" scope="request"/>
  	<kul:documentControls transactionalDocument="true" extraButtons="${extraButtons}" suppressRoutingControls="${displayInitTab}" />

</kul:documentPage>
