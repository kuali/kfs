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
       
