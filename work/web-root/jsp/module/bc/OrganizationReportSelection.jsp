<%--
 Copyright 2007-2008 The Kuali Foundation
 
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

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetOrganizationReportSelection" renderMultipart="true"
	docTitle="${KualiForm.operatingModeTitle}"
    transactionalDocument="false">

	<html-el:hidden name="KualiForm" property="universityFiscalYear" />
	<html-el:hidden name="KualiForm" property="backLocation" />
	<html-el:hidden name="KualiForm" property="returnAnchor" />
	<html-el:hidden name="KualiForm" property="returnFormKey" />
	<html-el:hidden name="KualiForm" property="reportMode" />
	<html-el:hidden name="KualiForm" property="reportConsolidation" />
	<html-el:hidden name="KualiForm" property="operatingModeTitle" />
	<html-el:hidden name="KualiForm" property="budgetConstructionReportThresholdSettings.lockThreshold" />
	
    <kul:errors errorTitle="Errors found on Page:" keyMatch="${BCConstants.ErrorKey.ORGANIZATION_REPORTS_SELECTION_ERRORS}"/>
    <kul:messages/>
    
    <html:hidden property="operatingModeTitle" value="${KualiForm.operatingModeTitle}" />
    
    <c:if test="${KualiForm.operatingModeTitle eq BCConstants.Report.SUB_FUND_SELECTION_TITLE}" >
       <bc:subFundPick />
    </c:if>
       
    <c:if test="${KualiForm.operatingModeTitle eq BCConstants.Report.OBJECT_CODE_SELECTION_TITLE}" >
      <bc:objectCodePick />
    </c:if>
       
    <c:if test="${KualiForm.operatingModeTitle eq BCConstants.Report.REASON_CODE_SELECTION_TITLE}" >
      <bc:reasonCodePick />
    </c:if>

</kul:page>
