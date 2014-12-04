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
<c:set var="documentAttributes" value="${DataDictionary.TravelRelocationDocument.attributes}" />
<c:set var="requesterAttributes" value="${DataDictionary.TravelerDetail.attributes}" />
<c:set var="tabindexOverrideBase" value="8" />

<h3>Moving and Relocation Information</h3>

<table cellpadding="0" cellspacing="0" class="datatable"
	summary="Moving And Relocation Information">
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.reasonCode}" />
			</div></th>
		<td class="datacell" ><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.reasonCode}"
				property="document.reasonCode" readOnly="${!fullEntryMode}" /></td>
		<th class="bord-l-b" />
		<td class="datacell" />
	</tr>
	<tr>
		<th width="25%">
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.tripBegin}" />
			</div></th>
		<td class="datacell" width="25%"><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.tripBegin}"
				property="document.tripBegin" readOnly="${!fullEntryMode}" /></td>
		<th width="25%">
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.tripEnd}" />
			</div></th>
		<td class="datacell" width="25%"><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.tripEnd}"
				property="document.tripEnd" readOnly="${!fullEntryMode}" /></td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.fromCity}" />
			</div></th>
		<td class="datacell"><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.fromCity}"
				property="document.fromCity" readOnly="${!fullEntryMode}" /></td>
		<th>
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.toCity}" readOnly="true" />
			</div></th>
		<td class="datacell"><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.toCity}"
				property="document.toCity" readOnly="${!fullEntryMode}" /></td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.fromStateCode}" />
			</div></th>
		<td class="datacell"><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.fromStateCode}"
				property="document.fromStateCode" readOnly="${!fullEntryMode}" /></td>
		<th>
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.toStateCode}" readOnly="true" />
			</div></th>
		<td class="datacell"><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.toStateCode}"
				property="document.toStateCode" readOnly="${!fullEntryMode}" /></td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.fromCountryCode}" />
			</div></th>
		<td class="datacell"><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.fromCountryCode}"
				property="document.fromCountryCode" readOnly="${!fullEntryMode}" />
		</td>
		<th>
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.toCountryCode}"
					readOnly="true" />
			</div></th>
		<td class="datacell"><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.toCountryCode}"
				property="document.toCountryCode" readOnly="${!fullEntryMode}" /></td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.titleCode}" />
			</div></th>
		<td class="datacell"><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.titleCode}"
				property="document.titleCode" readOnly="${!fullEntryMode}" /></td>
		<th rowspan="2">
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.comments}" readOnly="true" />
			</div></th>
		<td class="datacell" rowspan="2"><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.comments}"
				property="document.comments" readOnly="${!fullEntryMode}" /></td>
	</tr>
	<tr>
		<th class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.jobClsCode}" />
			</div></th>
		<td class="datacell"><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.jobClsCode}"
				property="document.jobClsCode" readOnly="${!fullEntryMode}" /></td>
	</tr>
</table>
