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

<kul:page showDocumentInfo="false" headerTitle="Travel Per Diem Links"
	docTitle="Travel Per Diem Links" transactionalDocument="false"
	htmlFormAction="dvPerDiem">

	<center>

	<table border=0 cellspacing=0 cellpadding=0 width="75%">
		<tr>
			<td>${KualiForm.travelPerDiemLinkPageMessage}</td>
		<tr>
	</table>

	<br>
	<br>

	<div id="workarea">

	<table cellpadding="0">
		<tbody>
			<tr>
				<td colspan="2" class="tab-subhead">Per Diem Category Links</td>
			</tr>

			<tr>
				<th>
				<div align=left>Category Name</div>
				</th>
				<th>
				<div align=left>Category Link</div>
				</th>
			</tr>

			<logic:iterate indexId="ctr" name="KualiForm"
				property="travelPerDiemCategoryCodes" id="currentCategory">
				<tr>
					<th scope="row">
					<div align="right">${currentCategory.perDiemCountryName}</div>
					</th>
					<td valign="top"><a href="${currentCategory.perDiemCountryText}">${currentCategory.perDiemCountryText}</a></td>
				</tr>
			</logic:iterate>
		</tbody>
	</table>

	</div>

	</center>

	<br />
	<kul:inquiryControls />

</kul:page>
