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
