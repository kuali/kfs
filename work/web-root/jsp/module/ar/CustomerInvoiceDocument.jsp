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

<%@ page import="org.kuali.kfs.sys.context.SpringContext" %>
<%@ page import="org.kuali.kfs.coa.service.AccountService" %>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:if test="${!accountingLineScriptsLoaded}">
       <script type='text/javascript' src="dwr/interface/ChartService.js"></script>
       <script type='text/javascript' src="dwr/interface/AccountService.js"></script>
       <script type='text/javascript' src="dwr/interface/SubAccountService.js"></script>
       <script type='text/javascript' src="dwr/interface/ObjectCodeService.js"></script>
       <script type='text/javascript' src="dwr/interface/ObjectTypeService.js"></script>
       <script type='text/javascript' src="dwr/interface/SubObjectCodeService.js"></script>
       <script type='text/javascript' src="dwr/interface/ProjectCodeService.js"></script>
       <script type='text/javascript' src="dwr/interface/OriginationCodeService.js"></script>
       <script language="JavaScript" type="text/javascript" src="scripts/sys/objectInfo.js"></script>
       <c:set var="accountingLineScriptsLoaded" value="true" scope="request" />
</c:if>     
       
<c:set var="accountsCanCrossCharts" value="<%=SpringContext.getBean(AccountService.class).accountsCanCrossCharts()%>" />       
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="CustomerInvoiceDocument"
	htmlFormAction="arCustomerInvoiceDocument" renderMultipart="true"
	showTabButtons="true">

	<sys:hiddenDocumentFields />

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
	
    <ar:customerInvoiceOrganization documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}"  readOnly="${readOnly}"/>	
	
    <ar:customerInvoiceRecurrenceDetails
        documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" readOnly="${readOnly}" />
        
    <ar:customerInvoiceGeneral
        documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" readOnly="${readOnly}" />
        
    <ar:customerInvoiceAddresses
        documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" readOnly="${readOnly}" />        
     
	<c:if test="${!empty KualiForm.editingMode['showReceivableFAU']}">
     <ar:customerInvoiceReceivableAccountingLine
      	documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" readOnly="${readOnly}"
      	receivableValuesMap="${KualiForm.document.valuesMap}"
        accountsCanCrossCharts="${accountsCanCrossCharts}"/>
    </c:if>
     
	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
		<sys-java:accountingLines>
			<sys-java:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
		</sys-java:accountingLines>
	</kul:tab>
	    
	<gl:generalLedgerPendingEntries />
		            
	<kul:notes /> 

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:superUserActions />

	<kul:panelFooter />

	<c:set var="extraButtons" value="${KualiForm.extraButtons}" scope="request"/>
	
	<sys:documentControls transactionalDocument="true" extraButtons="${extraButtons}"/>

</kul:documentPage>
