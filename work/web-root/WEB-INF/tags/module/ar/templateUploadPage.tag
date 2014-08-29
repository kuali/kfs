<%--
 Copyright 2006-2014 The Kuali Foundation
 
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