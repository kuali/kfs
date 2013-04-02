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
<c:set var="arrangerAttributes" value="${DataDictionary.PersonImpl.attributes}" />
<c:set var="profileAttributes" value="${DataDictionary.TravelArrangerDocument.attributes}" />
<c:set var="tabindexOverrideBase" value="8" />
<kul:tab tabTitle="Traveler Section" defaultOpen="true" tabErrorKey="${TemKeyConstants.TRVL_ARRGR_TRAVELER_ERRORS}">
    <div class="tab-container" align="center">
    <h3>Traveler </h3>
    <table cellpadding="0" cellspacing="0" class="datatable" summary="Traveler Section">
        
        <tr>
            <th class="bord-l-b"><div align="left">Traveler Lookup:</div></th>
            <td class="datacell">
				<kul:htmlControlAttribute attributeEntry="${profileAttributes.travelerName}" property="document.profile.name" readOnly="true"/>
                <kul:lookup boClassName="org.kuali.kfs.module.tem.businessobject.TEMProfile"
                                lookupParameters="document.profileId:profileId" />
            </td>
        </tr>
        
    </table> 
    
   
    </div>
</kul:tab>
       
