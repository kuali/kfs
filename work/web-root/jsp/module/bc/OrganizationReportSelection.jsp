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
