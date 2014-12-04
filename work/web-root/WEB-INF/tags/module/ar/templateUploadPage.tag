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
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>

<kul:page showDocumentInfo="false"
	headerTitle="Accounts Receivable ${KualiForm.templateType} Template Upload" docTitle=""
	renderMultipart="true" transactionalDocument="false"
	htmlFormAction="${KualiForm.htmlFormAction}" errorKey="foo">

	<strong><h2>${KualiForm.templateType} Template Upload</h2> </strong>
	</br>

	<table width="100%" border="0">
		<tr>
			<td><kul:errors keyMatch="${KualiForm.errorPropertyName}"
					errorTitle="Errors Found On Page:" />
			</td>
		</tr>
	</table>
	</br>

	<kul:tabTop tabTitle="Manage ${KualiForm.templateType} Template Files" defaultOpen="true"
		tabErrorKey="">
		<div class="tab-container" align="center">
			<h3>Upload ${KualiForm.templateType} Template Document</h3>
			<table width="100%" summary="" cellpadding="0" cellspacing="0">
				<tr>
					<td class="infoline"><html:file title="Browse File"
							property="uploadedFile" /> <span class="fineprint"></span>
					</td>

					<jsp:doBody/>

					<td class="infoline">
						<div align="center">
							<html:image property="methodToCall.save"
								src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
								alt="Add ${KualiForm.templateType} Template" title="Add ${KualiForm.templateType} Template"
								styleClass="tinybutton" />
						</div>
					</td>
				</tr>
			</table>
		</div>
	</kul:tabTop>

	<kul:panelFooter />

</kul:page>
