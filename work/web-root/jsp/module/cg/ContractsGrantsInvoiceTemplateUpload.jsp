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
	headerTitle="Contracts and Grants Invoice Template Upload" docTitle=""
	renderMultipart="true" transactionalDocument="false"
	htmlFormAction="cgContractsGrantsInvoiceTemplateUpload" errorKey="foo">

	<c:set var="InvoiceTemplateAttributes"
		value="${DataDictionary.InvoiceTemplate.attributes}" />

	<strong><h2>Invoice Template Upload</h2> </strong>
	</br>

	<table width="100%" border="0">
		<tr>
			<td><kul:errors keyMatch="document.invoiceTemplateUpload"
					errorTitle="Errors Found On Page:" />
			</td>
		</tr>
	</table>
	</br>

	<kul:tabTop tabTitle="Manage Invoice Template Files" defaultOpen="true"
		tabErrorKey="">
		<div class="tab-container" align="center">
			<h3>Upload Invoice Template Document</h3>
			<table width="100%" summary="" cellpadding="0" cellspacing="0">
				<tr>
					<td class="infoline"><html:file title="Browse File"
							property="uploadedFile" /> <span class="fineprint"></span>
					</td>
					<td class="infoline"><kul:htmlAttributeLabel
							attributeEntry="${InvoiceTemplateAttributes.invoiceTemplateCode}"
							useShortLabel="false" /> <html:select
							property="invoiceTemplateCode">
							<c:forEach var="invoiceTemplate"
								items="${KualiForm.invoiceTemplateList}">
								<c:choose>
									<c:when
										test="${param.invoiceTemplateCode == invoiceTemplate.key}">
										<option value="${invoiceTemplate.key}" selected="selected">${invoiceTemplate.label}</option>
									</c:when>
									<c:otherwise>
										<option value="${invoiceTemplate.key}">${invoiceTemplate.label}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</html:select>
					</td>

					<td class="infoline">
						<div align="center">
							<html:image property="methodToCall.save"
								src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
								alt="Add an Invoice Template" title="Add an Invoice Template"
								styleClass="tinybutton" />
						</div>
					</td>
				</tr>
			</table>
		</div>
	</kul:tabTop>

	<kul:panelFooter />

</kul:page>



