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

<c:set var="objectCodeAttribute"
	value="${DataDictionary.BudgetConstructionObjectPick.attributes}" />

<table align="center" cellpadding="0" cellspacing="0" class="datatable-100">
	<tr>
		<th class="grid" colspan="2" align="left">
			<br>
			<bean:message key="${BCConstants.Report.SELECTED_OBJECT_CODES_MESSAGE_KEY}" />
			<br><br>
		</th>
	</tr>

	<tr>
		<th class="grid">
			<kul:htmlAttributeLabel
				attributeEntry="${objectCodeAttribute.financialObjectCode}"
				useShortLabel="false" />
		</th>
		<th class="grid">
			<kul:htmlAttributeLabel
				attributeEntry="${objectCodeAttribute.objectCodeDescription}"
				useShortLabel="false" />
		</th>
	</tr>

	<logic:iterate name="KualiForm" id="objectCodePick"	property="objectCodePickList" indexId="ctr">
		<html-el:hidden name="KualiForm" property="objectCodePickList[${ctr}].principalId" />
		<html-el:hidden name="KualiForm" property="objectCodePickList[${ctr}].versionNumber" />
		<html-el:hidden name="KualiForm" property="objectCodePickList[${ctr}].selectFlag" />

		<c:if test="${objectCodePick.selectFlag == 1}">
			<tr align="center">
				<td class="grid" valign="center">
					<kul:htmlControlAttribute
						property="objectCodePickList[${ctr}].financialObjectCode"
						attributeEntry="${objectCodeAttribute.financialObjectCode}"
						readOnly="true" />
				</td>
				<td class="grid" valign="center">
					<kul:htmlControlAttribute
						property="objectCodePickList[${ctr}].objectCodeDescription"
						attributeEntry="${objectCodeAttribute.objectCodeDescription}"
						readOnly="true" />
				</td>
			</tr>
		</c:if>
	</logic:iterate>

</table>
<br />
<table align="center" cellpadding="0" cellspacing="0"class="datatable-100">
	<tr>
		<th class="grid" colspan="6" align="left">
			<br>
			${KualiForm.operatingModeTitle}
			<br><br>
		</th>
	</tr>

	<c:if test="${empty KualiForm.reasonCodePickList}">
		<tr>
			<th class="grid" colspan="6" align="left">
				<bean:message key="${BCConstants.Report.REASON_CODE_LIST_EMPTY_MESSAGE_KEY}" />
			</th>
		</tr>
</table>

<div id="globalbuttons" class="globalbuttons">
	<html:image
		src="${ConfigProperties.externalizable.images.url}buttonsmall_back.gif"
		styleClass="globalbuttons" property="methodToCall.start" title="back"
		alt="back" />
	<html:image
		src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif"
		styleClass="globalbuttons" property="methodToCall.returnToCaller"
		title="close" alt="close" />
</div>
</c:if>

<c:if test="${!empty KualiForm.reasonCodePickList}">
	<c:set var="reasonCodeAttribute" value="${DataDictionary.BudgetConstructionReasonCodePick.attributes}" />

	<tr>
		<th class="grid">
			Select
		</th>
		<th class="grid">
			<kul:htmlAttributeLabel
				attributeEntry="${reasonCodeAttribute.appointmentFundingReasonCode}"
				useShortLabel="false" />
		</th>
		<th class="grid">
			<kul:htmlAttributeLabel
				attributeEntry="${reasonCodeAttribute['appointmentFundingReason.appointmentFundingReasonDescription']}"
				useShortLabel="false" />
		</th>
	</tr>

	<logic:iterate name="KualiForm" id="reasonCodePick"	property="reasonCodePickList" indexId="ctr">
		<html-el:hidden name="KualiForm" property="reasonCodePickList[${ctr}].principalId" />
		<html-el:hidden name="KualiForm" property="reasonCodePickList[${ctr}].versionNumber" />

		<tr align="center">
			<td class="grid" valign="center">
				<html:checkbox property="reasonCodePickList[${ctr}].selectFlag"
					value="1" />
			</td>
			<td class="grid" valign="center">
				<kul:htmlControlAttribute
					property="reasonCodePickList[${ctr}].appointmentFundingReasonCode"
					attributeEntry="${reasonCodeAttribute.appointmentFundingReasonCode}"
					readOnly="true" />
			</td>
			<td class="grid" valign="center">
				<kul:htmlControlAttribute
					property="reasonCodePickList[${ctr}].appointmentFundingReason.appointmentFundingReasonDescription"
					attributeEntry="${reasonCodeAttribute['appointmentFundingReason.appointmentFundingReasonDescription']}"
					readOnly="true" />
			</td>
		</tr>
	</logic:iterate>

	</table>

	<div id="globalbuttons" class="globalbuttons">
		<html:image
			src="${ConfigProperties.externalizable.images.url}buttonsmall_back.gif"
			styleClass="globalbuttons" property="methodToCall.start" title="back"
			alt="back" />
		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_selectall.gif"
			property="methodToCall.selectAllReasonCodes" title="Select"
			alt="Select All Codes" styleClass="smallbutton" />
		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_unselall.gif"
			property="methodToCall.unselectAllReasonCodes" title="Unselect"
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

