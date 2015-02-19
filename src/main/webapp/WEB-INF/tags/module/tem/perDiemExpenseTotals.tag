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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<table cellpadding="0" class="datatable" summary="Per Diem Totals">
	<tr>
		<td colspan="10" class="subhead"><span
			class="subhead-left">Grand Totals</span>
		</td>
	</tr>
	<tr>
		<td colspan="5" class="total-line">
			<div class="right">
				<strong>Grand Totals:</strong>
			</div>
		</td>
		<td class="total-line">
			<div align="right">
				Meals & Incidentals:
				<bean:write name="KualiForm" property="document.mealsAndIncidentalsGrandTotal" />
			</div>
		</td>
		<td class="total-line">
			<div align="right">
				Lodging:
				<bean:write name="KualiForm" property="document.lodgingGrandTotal" />
			</div>
		</td>
		<td class="total-line">
			<div align="right">
				Miles:
				<bean:write name="KualiForm" property="document.milesGrandTotal" />
			</div>
		</td>
		<td class="total-line">
			<div align="right">
				Mileage Total:
				<bean:write name="KualiForm" property="document.mileageTotalGrandTotal" />
			</div>
		</td>
		<td class="total-line">
			<div align="right">
				Daily Total:
				<bean:write name="KualiForm" property="document.dailyTotalGrandTotal" />
			</div>
		</td>
	</tr>
</table>
