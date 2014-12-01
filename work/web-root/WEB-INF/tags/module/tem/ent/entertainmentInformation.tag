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

<c:set var="personAttributes" value="${DataDictionary.PersonImpl.attributes}" />
<c:set var="documentAttributes" value="${DataDictionary.TravelEntertainmentDocument.attributes}" />
<c:set var="travelerAttributes" value="${DataDictionary.TravelerDetail.attributes}" />
<c:set var="tabindexOverrideBase" value="8" />

<h3>Entertainment Information</h3>
<table cellpadding="0" cellspacing="0" class="datatable" >
	<tr>
            <th class="bord-l-b">
	            <div align="right" >
	            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.purposeCode}" />
	           	</div>
            </th>
            <td class="datacell">
            	<kul:htmlControlAttribute attributeEntry="${documentAttributes.purposeCode}" property="document.purposeCode" readOnly="${!fullEntryMode}"/>                 
            </td>
            <th class="bord-l-b" />
            <td class="datacell" />
      </tr>
      <tr>
            <th class="bord-l-b">
	            <div align="right" >
	            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.tripBegin}" />
	           	</div>
            </th>
            <td class="datacell">
            	<kul:htmlControlAttribute attributeEntry="${documentAttributes.tripBegin}" property="document.tripBegin" readOnly="${!fullEntryMode}"/>                 
            </td>
            <th>
            	<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.tripEnd}" /></div>
            </th>
            <td class="datacell">
            	<kul:htmlControlAttribute attributeEntry="${documentAttributes.tripEnd}" property="document.tripEnd" readOnly="${!fullEntryMode}"/>
			</td>
      </tr>
      <tr>
            <th class="bord-l-b" >
	            <div align="right" >
	            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.spouseIncluded}" />
	           	</div>
            </th>
            <td class="datacell" >
            	<kul:htmlControlAttribute attributeEntry="${documentAttributes.spouseIncluded}" property="document.spouseIncluded" readOnly="${!fullEntryMode}"/>                 
            </td>
      </tr>     
      <tr>
           <th >
            	<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.description}" /></div>
            </th>
            <td class="datacell" colspan="6">
            	<kul:htmlControlAttribute attributeEntry="${documentAttributes.description}" property="document.description" readOnly="${!fullEntryMode}"/>
			</td>
      </tr>
</table>
