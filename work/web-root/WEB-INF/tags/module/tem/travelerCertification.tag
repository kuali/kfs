<%--
 Copyright 2007-2009 The Kuali Foundation
 
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

<kul:tab tabTitle="Traveler Certification" defaultOpen="true" tabErrorKey="${TemKeyConstants.TEM_TRAVELER_CERT_TAB_ERRORS}">
	<c:set var="trmbTravCertAttributes" value="${DataDictionary.TravelReimbursementDocument.attributes}" />
    <div class="tab-container" align=center>
    <h3>Statement</h3>

	<table cellpadding=0 class="datatable" summary="Traveler Certification">
              <tr>
                <td colspan=4 align=left valign=middle class="datacell">${certificationStatement}</td>
              </tr>
              <tr>                
                <th scope=row class="bord-l-b"><div align="right">
<c:choose>
<c:when test="${isEmployee}">
<kul:htmlAttributeLabel
              attributeEntry="${trmbTravCertAttributes.employeeCertification}"/>
</c:when><c:otherwise>
<kul:htmlAttributeLabel
              attributeEntry="${trmbTravCertAttributes.nonEmployeeCertification}"/>
</c:otherwise>
</c:choose>
              </div>
</th>
                <td class="datacell">
                  <kul:htmlControlAttribute attributeEntry="${trmbTravCertAttributes.employeeCertification}" property="document.employeeCertification" readOnly="${!KualiForm.canCertify}"/>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
</kul:tab>
