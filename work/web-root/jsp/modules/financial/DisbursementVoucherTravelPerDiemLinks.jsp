<%--
 Copyright 2006 The Kuali Foundation.
 
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
<%@ include file="/jsp/core/tldHeader.jsp"%>
<%@ taglib tagdir="/WEB-INF/tags/dv" prefix="dv"%>

<kul:page showDocumentInfo="false" headerTitle="Travel Per Diem Links"
	docTitle="Travel Per Diem Links" transactionalDocument="false"
	htmlFormAction="help">

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
