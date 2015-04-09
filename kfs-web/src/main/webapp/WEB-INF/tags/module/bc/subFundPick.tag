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

<table align="center" cellpadding="0" cellspacing="0"
	class="datatable-100">
	<tr>
		<th class="grid" colspan="6" align="left">
			<br>
			${KualiForm.operatingModeTitle}
			<br>
			<br>
		</th>
	</tr>

	<c:if test="${empty KualiForm.subFundPickList}">
		<tr>
			<th class="grid" colspan="6" align="left">
				<bean:message
					key="${BCConstants.Report.SUB_FUND_LIST_EMPTY_MESSAGE_KEY}" />
			</th>
		</tr>
</table>

<div id="globalbuttons" class="globalbuttons">
	<html:image
		src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif"
		styleClass="globalbuttons" property="methodToCall.returnToCaller"
		title="close" alt="close" />
</div>
</c:if>

<c:if test="${!empty KualiForm.subFundPickList}">
	<c:set var="subFundAttribute"
		value="${DataDictionary.BudgetConstructionSubFundPick.attributes}" />

	<tr>
		<th class="grid">
			Select
		</th>
		<th class="grid">
			<kul:htmlAttributeLabel
				attributeEntry="${subFundAttribute.subFundGroupCode}"
				useShortLabel="false" />
		</th>
		<th class="grid">
			<kul:htmlAttributeLabel
				attributeEntry="${subFundAttribute['subFundGroup.subFundGroupDescription']}"
				useShortLabel="false" />
		</th>
	</tr>

	<logic:iterate name="KualiForm" id="subFundPick"
		property="subFundPickList" indexId="ctr">
		<html-el:hidden name="KualiForm"
			property="subFundPickList[${ctr}].principalId" />
		<html-el:hidden name="KualiForm"
			property="subFundPickList[${ctr}].versionNumber" />

		<tr align="center">
			<td class="grid" valign="center">
				<html:checkbox property="subFundPickList[${ctr}].reportFlag"
					value="1" />
			</td>
			<td class="grid" valign="center">
				<kul:htmlControlAttribute
					property="subFundPickList[${ctr}].subFundGroupCode"
					attributeEntry="${subFundAttribute.subFundGroupCode}"
					readOnly="true" />
			</td>
			<td class="grid" valign="center">
				<kul:htmlControlAttribute
					property="subFundPickList[${ctr}].subFundGroup.subFundGroupDescription"
					attributeEntry="${subFundAttribute['subFundGroup.subFundGroupDescription']}"
					readOnly="true" />
			</td>
		</tr>
	</logic:iterate>

	</table>

	<div id="globalbuttons" class="globalbuttons">
		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_selectall.gif"
			property="methodToCall.selectAllSubFunds" title="Select"
			alt="Select All Codes" styleClass="smallbutton" />
		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_unselall.gif"
			property="methodToCall.unselectAllSubFunds" title="Unselect"
			alt="Unselect All Codes" styleClass="smallbutton" />
		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_submit.gif"
			styleClass="globalbuttons" property="methodToCall.performReport"
			title="Perform Report" alt="submit"
			onclick="excludeSubmitRestriction=true" />
		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif"
			styleClass="globalbuttons" property="methodToCall.returnToCaller"
			title="close" alt="close" />
	</div>

</c:if>


