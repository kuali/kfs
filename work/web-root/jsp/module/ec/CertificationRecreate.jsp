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

<c:set var="documentAttributes"	value="${DataDictionary.EffortCertificationDocument.attributes}" />
<c:set var="detailAttributes" value="${DataDictionary.EffortCertificationDetail.attributes}" />

<c:set var="detailLines" value="${KualiForm.detailLines}"/>

<c:set var="documentTypeName" value="EffortCertificationDocument"/>
<c:set var="htmlFormAction" value="effortCertificationRecreate"/>

<c:set var="readOnly" value="${empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:documentPage showDocumentInfo="true" documentTypeName="${documentTypeName}"
	htmlFormAction="${htmlFormAction}" renderMultipart="true"
    showTabButtons="true">
    
    <sys:hiddenDocumentFields isFinancialDocument="false" />
    
    <sys:documentOverview editingMode="${KualiForm.editingMode}" />
    
    <c:set var="hiddenFieldNames" value="emplid,universityFiscalYear,effortCertificationReportNumber,effortCertificationDocumentCode,totalOriginalPayrollAmount"/>
	<c:forTokens var="fieldName" items="${hiddenFieldNames}" delims=",">	
		<input type="hidden" name="document.${fieldName}" id="document.${fieldName}" value="${KualiForm.document[fieldName]}"/>		  
	</c:forTokens>
    
	<kul:tab tabTitle="Effort Detail" defaultOpen="true"
		tabErrorKey="${EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS}">
		
		<div class="tab-container" align=center>
			<h3>Retrieve Data</h3>
			<ec:detailLineImport readOnly="${readOnly}" attributes="${documentAttributes}" />				
		</div>
		
		<div class="tab-container-error"><div class="left-errmsg-tab"><kul:errors keyMatch="${EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS}"/></div></div>
					
		<div class="tab-container" align=center>			
			<h3>Effort Detail Lines</h3>
			
			<ec:detailLines detailLines="${detailLines}" attributes="${detailAttributes}"
				detailFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,sourceChartOfAccountsCode,sourceAccountNumber,positionNumber,effortCertificationCalculatedOverallPercent,effortCertificationOriginalPayrollAmount"
				detailFieldNamesWithHiddenFormWhenReadonly="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,sourceChartOfAccountsCode,sourceAccountNumber,positionNumber,effortCertificationOriginalPayrollAmount,effortCertificationCalculatedOverallPercent"				
				hiddenFieldNames="universityFiscalYear,effortCertificationUpdatedOverallPercent,effortCertificationPayrollAmount,costShareSourceSubAccountNumber,versionNumber"
				inquirableUrl="${KualiForm.detailLineFieldInquiryUrl}"
				fieldInfo="${KualiForm.fieldInfo}"/>
		</div>		
	</kul:tab>
	
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:superUserActions />
    <kul:panelFooter />
    <sys:documentControls transactionalDocument="false" />
</kul:documentPage>
