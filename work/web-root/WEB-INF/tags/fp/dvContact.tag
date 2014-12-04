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

<kul:tab tabTitle="Contact Information" defaultOpen="true" tabErrorKey="${KFSConstants.DV_CONTACT_TAB_ERRORS}">
  	<c:set var="dvAttributes" value="${DataDictionary.DisbursementVoucherDocument.attributes}" />
    <div class="tab-container" align=center > 
		<h3>Contact Information</h3>
		<table class="datatable" summary="Contact Information" cellpadding="0">
             <tbody>
               <tr>
                 <th width="25%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrContactPersonName}"/></div></th>
                 <td width="25%"><kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrContactPersonName}" property="document.disbVchrContactPersonName" readOnly="${!fullEntryMode}"/></td>
               </tr>
               <tr>
                 <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrContactPhoneNumber}"/></div></th>
                 <td><kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrContactPhoneNumber}" property="document.disbVchrContactPhoneNumber" readOnly="${!fullEntryMode}"/></td>
               </tr>
               <tr>
                 <th scope="row"><div align="right"> <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrContactEmailId}"/></div></th>
                 <td><kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrContactEmailId}" property="document.disbVchrContactEmailId" readOnly="${!fullEntryMode}"/></td>
               </tr>
               <tr>
                 <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${dvAttributes.campusCode}"/></div></th>
                 <td><kul:htmlControlAttribute attributeEntry="${dvAttributes.campusCode}" property="document.campusCode" readOnly="true"/></td>
               </tr>
             </tbody>
        </table>
    </div>
</kul:tab>
