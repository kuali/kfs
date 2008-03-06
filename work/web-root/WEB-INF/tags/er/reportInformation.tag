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
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${erAttributes['employee.personName']}" noColon="true" /></div></th>
              <td>
              	<kul:inquiry 
					boClassName="org.kuali.core.bo.user.UniversalUser" 
					keyValues="personPayrollIdentifier=${documentObject.emplid}&personUniversalIdentifier=${documentObject.employee.personUniversalIdentifier}" 
					render="true">
              		<kul:htmlControlAttribute attributeEntry="${erAttributes['employee.personName']}" property="document.employee.personName" readOnly="true"/>
              	</kul:inquiry>
              </td>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${erAttributes['effortCertificationReportDefinition.effortCertificationReportBeginFiscalYear']}" noColon="true" /></th>
              <td><c:out value="${KualiForm.formattedStartDate}" /></td>
            </tr>
            <tr>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${erAttributes.effortCertificationReportNumber}" noColon="true" /></div></th>
              <td>
              	<kul:inquiry 
					boClassName="org.kuali.module.effort.bo.EffortCertificationReportDefinition" 
					keyValues="universityFiscalYear=${documentObject.effortCertificationReportDefinition.universityFiscalYear}&effortCertificationReportNumber=${documentObject.effortCertificationReportDefinition.effortCertificationReportNumber}" 
					render="true">
              		<kul:htmlControlAttribute attributeEntry="${erAttributes['effortCertificationReportDefinition.effortCertificationReportBeginFiscalYear']}" property="document.effortCertificationReportDefinition.reportBeginFiscalYear.universityFiscalYear" readOnly="true"/>-<kul:htmlControlAttribute attributeEntry="${erAttributes.effortCertificationReportNumber}" property="document.effortCertificationReportNumber" readOnly="true"/>
              	</kul:inquiry>
              </td>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${erAttributes['effortCertificationReportDefinition.effortCertificationReportEndFiscalYear']}" noColon="true" /></div></th>
              <td><c:out value="${KualiForm.formattedEndDate}" /></td>
            </tr>
         </tbody>
      </table>
    </div>
</kul:tab>
