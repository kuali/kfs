<%--
 Copyright 2007 The Kuali Foundation
 
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

<kul:page showDocumentInfo="false"
	headerTitle="Accounts Receivable Letter Template Upload" docTitle=""
	renderMultipart="true" transactionalDocument="false"
	htmlFormAction="arAccountsReceivableLetterTemplateUpload" errorKey="foo">

	<c:set var="LetterTemplateAttributes"
		value="${DataDictionary.DunningLetterTemplate.attributes}" />

	<strong><h2>Dunning Letter Template Upload</h2> </strong>
	</br>

	<table width="100%" border="0">
		<tr>
			<td><kul:errors keyMatch="document.letterTemplateUpload"
					errorTitle="Errors Found On Page:" />
			</td>
		</tr>
	</table>
	</br>

	<kul:tabTop tabTitle="Manage Dunning Letter Template Files" defaultOpen="true"
		tabErrorKey="">
		<div class="tab-container" align="center">
			<h3>Upload Dunning Letter Template Document</h3>
			<table width="100%" summary="" cellpadding="0" cellspacing="0">
				<tr>
					<td class="infoline"><html:file title="Browse File"
							property="uploadedFile" /> <span class="fineprint"></span>
					</td>
					<td class="infoline"><kul:htmlAttributeLabel
							attributeEntry="${LetterTemplateAttributes.letterTemplateCode}"
							useShortLabel="false" /> <html:select
							property="letterTemplateCode">
							<c:forEach var="letterTemplate"
								items="${KualiForm.letterTemplateList}">
								<c:choose>
									<c:when
										test="${param.letterTemplateCode == letterTemplate.key}">
										<option value="${letterTemplate.key}" selected="selected">${letterTemplate.value}</option>
									</c:when>
									<c:otherwise>
										<option value="${letterTemplate.key}">${letterTemplate.value}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</html:select>
					</td>

					<td class="infoline">
						<div align="center">
							<html:image property="methodToCall.save"
								src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
								alt="Add a Letter Template" title="Add a Letter Template"
								styleClass="tinybutton" />
						</div>
					</td>
				</tr>
			</table>
		</div>
	</kul:tabTop>

	<kul:panelFooter />

</kul:page>