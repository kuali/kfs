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

<c:set var="thresholdAttribute"
	value="${DataDictionary.BudgetConstructionReportThresholdSettings.attributes}" />

<table align="center" cellpadding="0" cellspacing="0"
	class="datatable-100">
	<tr>
		<th class="grid" colspan="2" align="left">
			<br>
			<bean:message
				key="${BCConstants.Report.THRESHOLD_SELECTION_MESSAGE_KEY}" />
		</th>
	</tr>

	<tr>
		<th class="grid" width="50%" align="right">
			<kul:htmlAttributeLabel
				attributeEntry="${thresholdAttribute.useThreshold}"
				useShortLabel="false" />
		</th>
		<td class="grid" valign="center" width="50%">
			<kul:htmlControlAttribute
				property="budgetConstructionReportThresholdSettings.useThreshold"
				attributeEntry="${thresholdAttribute.useThreshold}" readOnly="false" />
		</td>
	</tr>

	<tr>
		<th class="grid" align="right">
			<kul:htmlAttributeLabel
				attributeEntry="${thresholdAttribute.thresholdPercent}"
				useShortLabel="false" />
		</th>
		<td class="grid" valign="center">
			<kul:htmlControlAttribute
				property="budgetConstructionReportThresholdSettings.thresholdPercent"
				attributeEntry="${thresholdAttribute.thresholdPercent}"
				readOnly="false" />
		</td>
	</tr>

	<tr>
		<th class="grid" align="right">
			<kul:htmlAttributeLabel
				attributeEntry="${thresholdAttribute.useGreaterThanOperator}"
				useShortLabel="false" />
		</th>
		<td class="grid" valign="center">
			<kul:htmlControlAttribute
				property="budgetConstructionReportThresholdSettings.useGreaterThanOperator"
				attributeEntry="${thresholdAttribute.useGreaterThanOperator}"
				readOnly="false" />
		</td>
	</tr>
</table>
