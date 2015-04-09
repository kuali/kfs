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
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>
<%@ attribute name="assetValueObj" type="java.lang.String" required="false" description="Asset object name" %>
<c:if test="${assetValueObj==null}">
	<c:set var="assetValueObj" value="document.asset" />
</c:if>
<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
<kul:tab tabTitle="View Asset Depreciation" defaultOpen="${!defaultTabHide}"> 
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">								
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.primaryDepreciationMethodCode}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.primaryDepreciationMethodCode" attributeEntry="${assetAttributes.primaryDepreciationMethodCode}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.baseAmount}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.baseAmount" attributeEntry="${assetAttributes.baseAmount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right">Depreciable Lifelimit:</th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.capitalAssetType.depreciableLifeLimit" attributeEntry="${assetAttributes['capitalAssetType.depreciableLifeLimit']}" readOnly="true"/></td>								
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.salvageAmount}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.salvageAmount" attributeEntry="${assetAttributes.salvageAmount}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.depreciationDate}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.depreciationDate" attributeEntry="${assetAttributes.depreciationDate}" readOnly="true"/></td>								
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.accumulatedDepreciation}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.accumulatedDepreciation" attributeEntry="${assetAttributes.accumulatedDepreciation}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.yearToDateDepreciation}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.yearToDateDepreciation" attributeEntry="${assetAttributes.yearToDateDepreciation}" readOnly="true"/></td>								
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.bookValue}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.bookValue" attributeEntry="${assetAttributes.bookValue}" readOnly="true"/></td>								
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.currentMonthDepreciation}" readOnly="true" /></th>
				<td class="grid" width="25%" ><kul:htmlControlAttribute property="${assetValueObj}.currentMonthDepreciation" attributeEntry="${assetAttributes.currentMonthDepreciation}" readOnly="true"/></td>								
				<th class="grid" width="25%" align="right">&nbsp;</th>
				<td class="grid" width="25%">&nbsp;</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.prevYearDepreciation}" readOnly="true" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetValueObj}.prevYearDepreciation" attributeEntry="${assetAttributes.prevYearDepreciation}" readOnly="true"/></td>
				<th class="grid" width="25%" align="right">&nbsp;</th>
				<td class="grid" width="25%">&nbsp;</td>								
			</tr>
		</table>
		</div>
</kul:tab>
