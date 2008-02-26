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

<c:set var="documentAttributes"	value="${DataDictionary.EffortCertificationDocument.attributes}" />
<c:set var="detailAttributes" value="${DataDictionary.EffortCertificationDetail.attributes}" />

<c:set var="detailLines" value="${KualiForm.detailLines}"/>
<c:set var="documentTypeName" value="EffortCertificationDocument"/>
<c:set var="htmlFormAction" value="effortCertificationRecreate"/>

<kul:documentPage showDocumentInfo="true" documentTypeName="${documentTypeName}"
	htmlFormAction="${htmlFormAction}" renderMultipart="true"
    showTabButtons="true">
    
    <kul:hiddenDocumentFields isTransactionalDocument="false" />
    <kul:documentOverview editingMode="${KualiForm.editingMode}" />
 
	<kul:tab tabTitle="Effort Detail Importing" defaultOpen="true"
		tabErrorKey="${EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS}">
		<div class="tab-container" align=center>
			<div class="h2-container"><h2>Retrieve Data</h2></div>
			
			<er:detailLineImport attributes="${documentAttributes}"/>
		</div>
		
		<div class="tab-container" align=center>
			<div class="h2-container"><h2>Effort Detail Lines</h2></div>
			
			<er:detailLines detailLines="${detailLines}" attributes="${detailAttributes}"
				detailFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,sourceChartOfAccountsCode,sourceAccountNumber,positionNumber,effortCertificationPayrollAmount,effortCertificationCalculatedOverallPercent"
				hiddenFieldNames="financialDocumentPostingYear"
				inquirableUrl="${KualiForm.detailLineFieldInquiryUrl}"
				fieldInfo="${KualiForm.fieldInfo}"/>
		</div>		
	</kul:tab>
	
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:panelFooter />
    <kul:documentControls transactionalDocument="false" />
</kul:documentPage>
