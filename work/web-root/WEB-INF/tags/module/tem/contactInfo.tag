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

<kul:tab tabTitle="Contact Information" defaultOpen="true" tabErrorKey="${TemKeyConstants.TRVL_REIMB_CONTACT_INFO_ERRORS}">
  	<c:set var="trmbAttributes" value="${DataDictionary.TravelReimbursementDocument.attributes}" />
    <div class="tab-container" align=center > 
		<h3>Contact Information</h3>
		<table class="datatable" summary="Contact Information" cellpadding="0">
             <tbody>
               <tr>
                 <th width="25%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${trmbAttributes.contactName}"/></div></th>
                 <td width="25%"><kul:htmlControlAttribute attributeEntry="${trmbAttributes.contactName}" property="document.contactName" readOnly="${!fullEntryMode}"/></td>
               </tr>
               <tr>
                 <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${trmbAttributes.contactPhoneNum}"/></div></th>
                 <td><kul:htmlControlAttribute attributeEntry="${trmbAttributes.contactPhoneNum}" property="document.contactPhoneNum" readOnly="${!fullEntryMode}"/></td>
               </tr>
               <tr>
                 <th scope="row"><div align="right"> <kul:htmlAttributeLabel attributeEntry="${trmbAttributes.contactEmailAddress}"/></div></th>
                 <td><kul:htmlControlAttribute attributeEntry="${trmbAttributes.contactEmailAddress}" property="document.contactEmailAddress" readOnly="${!fullEntryMode}"/></td>
               </tr>
               <tr>
                 <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${trmbAttributes.contactCampusCode}"/></div></th>
                 <td><kul:htmlControlAttribute attributeEntry="${trmbAttributes.contactCampusCode}" property="document.contactCampusCode" readOnly="true"/></td>
               </tr>
             </tbody>
        </table>
    </div>
</kul:tab>