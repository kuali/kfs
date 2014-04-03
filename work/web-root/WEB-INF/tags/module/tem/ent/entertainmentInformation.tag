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
