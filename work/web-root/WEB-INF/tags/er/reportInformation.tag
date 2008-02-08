<%--
 Copyright 2007 The Kuali Foundation.
 
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

<kul:tab tabTitle="Report Information" defaultOpen="true" tabErrorKey="${KFSConstants.DV_CONTACT_TAB_ERRORS}">
	<c:set var="erAttributes" value="${DataDictionary.EffortCertificationDocument.attributes}" />
	<c:set var="sharedDocumentAttributes" value="${DataDictionary.OutstandingReportsByOrganization.attributes}" />
	<c:set var="documentObject" value="${KualiForm.effortCertificationDocument}" />
    <c:set var="detailLines" value="${KualiForm.effortCertificationDocument.effortCertificationDetailLines}" />
  	<div class="tab-container" align=center > 
    <div class="h2-container">
<h2>Report Information</h2>
</div>
	<table class="datatable" summary="Report Information" cellpadding="0">
    	<tbody>
        	<tr>
            	<th width="35%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${sharedDocumentAttributes['employee.personName']}"/></div></th>
                <td width="65%"><a href="#"><c:out value="${documentObject.employee.personName}" /></a></td>
            </tr>
            <tr>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${erAttributes.effortCertificationReportNumber}"/></div></th>
                <td><a href="#"><c:out value="${documentObject.effortCertificationReportNumber}" /></a></td>
            </tr>
            <tr>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${erAttributes['effortCertificationReportDefinition.effortCertificationReportBeginFiscalYear']}"/></div></th>
                <td><a href="#"><c:out value="${documentObject.effortCertificationReportDefinition.reportBeginFiscalYear.universityFiscalYear}" /></a></td>
            </tr>
            <tr>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${erAttributes['effortCertificationReportDefinition.effortCertificationReportEndFiscalYear']}"/></div></th>
                <td><a href="#"><c:out value="${documentObject.effortCertificationReportDefinition.reportEndFiscalYear.universityFiscalYear}" /></a></td>
            </tr>
        </tbody>
     </table>
    </div>
</kul:tab>
