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