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

<kul:documentPage showDocumentInfo="true"
	documentTypeName="CustomerCreditMemoDocument"
	htmlFormAction="arCustomerCreditMemoDocument" renderMultipart="true"
	showTabButtons="true">
	
	<c:set var="displayInitTab" value="${KualiForm.editingMode['displayInitTab']}" scope="request" />
	
	<kul:hiddenDocumentFields />
	<html:hidden property="document.statusCode" />

	<!--  Display 1st screen -->
	<c:if test="${displayInitTab}" >
		<ar:customerCreditMemoInit />
		<kul:panelFooter />
	</c:if>

	<!--  Display 2nd screen -->
	<c:if test="${not displayInitTab}" >
		<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	
		<ar:customerCreditMemoGeneral />
		
    	<c:if test="${!empty KualiForm.editingMode['showReceivableFAU']}">
    		<ar:customerCreditMemoReceivableAccountingLine />
    	</c:if>
      
      	<ar:customerCreditMemoDetails />
    	<kul:notes />
		<kul:adHocRecipients />
		<kul:routeLog />
		<kul:panelFooter />
	</c:if>

	<c:set var="extraButtons" value="${KualiForm.extraButtons}" scope="request"/>
  	<kul:documentControls transactionalDocument="true" extraButtons="${extraButtons}" suppressRoutingControls="${displayInitTab}" />

</kul:documentPage>
