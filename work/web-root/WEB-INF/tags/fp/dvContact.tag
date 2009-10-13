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
