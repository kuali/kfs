<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<c:if test="${empty KualiForm.reasonCodePickList}">
	<tr>
		<th class="grid" colspan="6" align="left">
			<bean:message key="${BCConstants.Report.REASON_CODE_LIST_EMPTY_MESSAGE_KEY}" />
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

	<logic:iterate name="KualiForm" id="reasonCodePick" property="reasonCodePickList" indexId="ctr">
		<html-el:hidden name="KualiForm" property="reasonCodePickList[${ctr}].personUniversalIdentifier" />
        <html-el:hidden name="KualiForm" property="reasonCodePickList[${ctr}].versionNumber" />
        
		<tr align="center">
			<td class="grid" valign="center">
					<html:checkbox property="reasonCodePickList[${ctr}].selectFlag"	value="1" />
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
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_selectall.gif"
			property="methodToCall.selectAllReasonCodes" title="Select"
			alt="Select All Codes" styleClass="smallbutton" />
		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_unselall.gif"
			property="methodToCall.unselectAllReasontCodes" title="Unselect"
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
