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
