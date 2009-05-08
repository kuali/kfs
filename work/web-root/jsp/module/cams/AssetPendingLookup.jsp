<%--
 Copyright 2007 The Kuali Foundation.
 
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

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:set var="preqLockingList" value="${KualiForm.document.newMaintainableObject.preqLinks}" />
<c:set var="linkedDocumentNames" value="${KualiForm.document.newMaintainableObject.fpLinkedDocumentInfo}" />

<kul:tab tabTitle="View Purchasing/Financial Asset Documents" defaultOpen="false">
	<div class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="view/edit pending entries">

    	<c:if test="${!empty fpLockingList }">
			<tr>
				<logic:iterate id="fpLinkedDocumentInfo" name="KualiForm" property="document.newMaintainableObject.fpLinkedDocumentInfo" indexId="ctr">
					<c:set var="documentName" value="${fn:substringBefore(KualiForm.document.newMaintainableObject.fpLinkedDocumentInfo[ctr], '-')}" />
					<c:set var="documentNumber" value="${fn:substringAfter(KualiForm.document.newMaintainableObject.fpLinkedDocumentInfo[ctr], '-')}" />
					<td class="infoline" align="center">${documentName} - 
						<a href="${ConfigProperties.application.url}/kew/DocHandler.do?command=displayDocSearchView&docId=${documentNumber}"  target="_blank">
							${documentNumber}
						</a>&nbsp;
					</td>
				</logic:iterate>
			</tr>
		</c:if>
		
		<c:if test="${!empty preqLockingList}">
			<tr>
				<logic:iterate id="preqLinks" name="KualiForm" property="document.newMaintainableObject.preqLinks" indexId="ctr">
					<td class="infoline" align="center">Payment Request - 
						<a href="${ConfigProperties.application.url}/kew/DocHandler.do?command=displayDocSearchView&docId=${KualiForm.document.newMaintainableObject.preqLinks[ctr]}"  target="_blank">
							${KualiForm.document.newMaintainableObject.preqLinks[ctr]}
					</a>&nbsp;
					</td>
				</logic:iterate>
			</tr>
		</c:if>
		
		
		</table>
	</div>
</kul:tab>
