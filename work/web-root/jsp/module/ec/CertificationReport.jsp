<%--
 Copyright 2005-2008 The Kuali Foundation
 
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
<%@ page import="org.kuali.kfs.sys.context.SpringContext" %>
<%@ page import="org.kuali.kfs.coa.service.AccountService" %>

<html:xhtml/>

<c:set var="documentAttributes"	value="${DataDictionary.EffortCertificationDocument.attributes}" />
<c:set var="detailAttributes" value="${DataDictionary.EffortCertificationDetail.attributes}" />

<c:set var="detailLines" value="${KualiForm.detailLines}"/>
<c:set var="newDetailLine" value="${KualiForm.newDetailLine}"/>

<c:set var="documentTypeName" value="EffortCertificationDocument"/>
<c:set var="htmlFormAction" value="effortCertificationRecreate"/>
<c:set var="accountsCanCrossCharts" value="<%=SpringContext.getBean(AccountService.class).accountsCanCrossCharts()%>" />    

<kul:documentPage showDocumentInfo="true"
	htmlFormAction="effortCertificationReport"
	documentTypeName="${documentTypeName}" renderMultipart="true"
	showTabButtons="true">
	
	<sys:documentOverview editingMode="${KualiForm.editingMode}" />	
	
	<ec:reportInformation />
	
    <c:set var="hiddenFieldNames" value="effortCertificationDocumentCode,totalOriginalPayrollAmount"/>
	<c:forTokens var="fieldName" items="${hiddenFieldNames}" delims=",">	
		<input type="hidden" name="document.${fieldName}" id="document.${fieldName}" value="${KualiForm.document[fieldName]}"/>		  
	</c:forTokens>

	<c:set var="canEdit" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}"/>
	<c:set var="isSummaryTabEntry" value="${KualiForm.editingMode[EffortConstants.EffortCertificationEditMode.SUMMARY_TAB_ENTRY]}"/>
	
 	< c:if test="${canEdit && isSummaryTabEntry}" >
		<ec:summaryTab accountsCanCrossCharts="${accountsCanCrossCharts}"/>	
	</c:if >
	
	<c:set var="isDetailTabEntry" value="${KualiForm.editingMode[EffortConstants.EffortCertificationEditMode.DETAIL_TAB_ENTRY]}" />
	<ec:detailTab isOpen="${!isSummaryTabEntry}" isEditable="${canEdit && isDetailTabEntry && !isSummaryTabEntry}" accountsCanCrossCharts="${accountsCanCrossCharts}"/>
	
	<kul:notes />
	
	<kul:adHocRecipients/>
	
	<kul:routeLog />
	
	<kul:superUserActions />
	
	<kul:panelFooter />
	
	<sys:documentControls transactionalDocument="${document.transactionalDocument}" />

</kul:documentPage>
