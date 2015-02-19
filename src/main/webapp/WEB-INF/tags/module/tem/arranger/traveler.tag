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
				<c:if test="${fullEntryMode}">
					<kul:lookup boClassName="org.kuali.kfs.module.tem.businessobject.TemProfile"
									lookupParameters="document.profileId:profileId" />
				</c:if>
            </td>
        </tr>
        
    </table> 
    
   
    </div>
</kul:tab>
       
