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
	
	<!--  value should be replaced with variable -->
	<c:set var="displayInitTab" value="false" scope="request" />

	<kul:hiddenDocumentFields />

	<!--  Display 1st screen -->
	<c:if test="${displayInitTab}" >
		<ar:customerCreditMemoInit />
		<kul:panelFooter />
	</c:if>

	<!--  Display 2nd screen -->
	<c:if test="${not displayInitTab}" >
		<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	
		<ar:customerCreditMemoGeneral />
    	<!-- Check if receivable accounting line should be displayed -->
    	<ar:customerCreditMemoReceivableAccountingLine />
      
      	<ar:customerCreditMemoDetails />
    	<kul:notes />
		<kul:adHocRecipients />
		<kul:routeLog />
		<kul:panelFooter />
	</c:if>

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
