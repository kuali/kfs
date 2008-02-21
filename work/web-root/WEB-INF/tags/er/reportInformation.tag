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
	<c:set var="documentObject" value="${KualiForm.document}" />
    <c:set var="detailLines" value="${KualiForm.document.effortCertificationDetailLines}" />
  	<div class="tab-container" align=center > 
    <div class="h2-container">
<h2>Report Information</h2>
</div>
	  <table summary="" cellpadding="0" cellspacing="0">
            <tbody><tr>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${erAttributes['employee.personName']}"/></div></th>
              <td><a href="#"><kul:htmlControlAttribute attributeEntry="${erAttributes['employee.personName']}" property="document.employee.personName" readOnly="true"/></a></td>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${erAttributes['effortCertificationReportDefinition.effortCertificationReportBeginFiscalYear']}"/></th>
              <td><kul:htmlControlAttribute attributeEntry="${erAttributes['effortCertificationReportDefinition.effortCertificationReportBeginFiscalYear']}" property="document.effortCertificationReportDefinition.reportBeginFiscalYear.universityFiscalYear" readOnly="true"/></td>
            </tr>
            <tr>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${erAttributes.effortCertificationReportNumber}"/></div></th>
              <td><a href="#"><kul:htmlControlAttribute attributeEntry="${erAttributes.effortCertificationReportNumber}" property="document.effortCertificationReportNumber" readOnly="true"/></a></td>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${erAttributes['effortCertificationReportDefinition.effortCertificationReportEndFiscalYear']}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${erAttributes['effortCertificationReportDefinition.effortCertificationReportEndFiscalYear']}" property="document.effortCertificationReportDefinition.reportEndFiscalYear.universityFiscalYear" readOnly="true"/></td>
            </tr>
         </tbody>
      </table>
    </div>
</kul:tab>
