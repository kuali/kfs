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
