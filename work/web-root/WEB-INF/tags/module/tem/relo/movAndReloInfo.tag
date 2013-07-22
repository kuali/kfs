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
					attributeEntry="${documentAttributes.tripBegin}" readOnly="true" />
			</div></th>
		<td class="datacell" width="25%"><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.tripBegin}"
				property="document.tripBegin" readOnly="${!fullEntryMode}" /></td>
		<th width="25%">
			<div align="right">
				<kul:htmlAttributeLabel
					attributeEntry="${documentAttributes.tripEnd}" readOnly="true" />
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
