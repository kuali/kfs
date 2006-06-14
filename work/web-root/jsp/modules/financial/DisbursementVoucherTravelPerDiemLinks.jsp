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
