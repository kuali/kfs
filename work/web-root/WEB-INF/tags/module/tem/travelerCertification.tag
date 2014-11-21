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
