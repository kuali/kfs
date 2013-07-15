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
<c:set var="arrangerAttributes" value="${DataDictionary.TravelArrangerDocument.attributes}" />
<c:set var="tabindexOverrideBase" value="8" />
<kul:tab tabTitle="Edit Request" defaultOpen="true" tabErrorKey="${TemKeyConstants.TRVL_ARRGR_REQUEST_ERRORS}">
    <div class="tab-container" align="center">
    <h3>Edit Request </h3>
    <table cellpadding="0" cellspacing="0" class="datatable" summary="Edit Request">
        
        <tr>
            <th class="bord-l-b" colspan="2" width="35%">
                <div align="left">
                    <kul:htmlAttributeLabel attributeEntry="${arrangerAttributes.resign}" />
                </div>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${arrangerAttributes.resign}" property="document.resign" readOnly="${!fullEntryMode}"/>          
            </td>
        </tr>
        <tr>
            <th class="bord-l-b" rowspan="3" width="35%">
                <div align="left">
                    Request to become an arranger:
                </div>
            </th>
            
            <th  class="bord-l-b">
            <kul:htmlAttributeLabel attributeEntry="${arrangerAttributes.taInd}" noColon="true"/>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${arrangerAttributes.taInd}" property="document.taInd" readOnly="${!(fullEntryMode || travelerEntryMode)}"/>          
            </td>
            
        </tr>
        <tr>
            <th  class="bord-l-b" width="25%">
            <kul:htmlAttributeLabel attributeEntry="${arrangerAttributes.trInd}" noColon="true"/>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${arrangerAttributes.trInd}" property="document.trInd" readOnly="${!(fullEntryMode || travelerEntryMode)}"/>          
            </td>
            
        </tr>
        <tr>
            <th  class="bord-l-b">
            <kul:htmlAttributeLabel attributeEntry="${arrangerAttributes.primaryInd}" noColon="true"/>
            </th>
            <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${arrangerAttributes.primaryInd}" property="document.primaryInd" readOnly="${!(fullEntryMode || travelerEntryMode)}"/>          
            </td>
            
        </tr>
        
    </table> 
    
   
    </div>
</kul:tab>
       
