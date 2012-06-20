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

<c:set var="preqLockingList" value="${KualiForm.document.newMaintainableObject.preqLinks}" />
<c:set var="linkedDocumentNames" value="${KualiForm.document.newMaintainableObject.fpLinkedDocumentInfo}" />
<c:set var="fabricationOn" value="${KualiForm.document.newMaintainableObject.fabricationOn}" />

<c:if test="${!fabricationOn }" >
<kul:tab tabTitle="View Purchasing/Financial Asset Documents" defaultOpen="false">
	<div class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="view/edit pending entries">
    	<c:if test="${!empty linkedDocumentNames }">
			<logic:iterate id="fpLinkedDocumentInfo" name="KualiForm" property="document.newMaintainableObject.fpLinkedDocumentInfo" indexId="ctr">
				<tr>
					<c:set var="documentName" value="${fn:substringBefore(KualiForm.document.newMaintainableObject.fpLinkedDocumentInfo[ctr], '-')}" />
					<c:set var="documentNumber" value="${fn:substringAfter(KualiForm.document.newMaintainableObject.fpLinkedDocumentInfo[ctr], '-')}" />
					<td class="infoline" align="center">${documentName} - 
						<a href="${ConfigProperties.kew.url}/${KFSConstants.DOC_HANDLER_ACTION}?command=displayDocSearchView&docId=${documentNumber}"  target="_blank">
							${documentNumber}
						</a>&nbsp;
					</td>
				</tr>
			</logic:iterate>
		</c:if>
		</table>
		<br/>
		
		<table cellpadding="0" cellspacing="0" class="datatable" summary="view/edit pending entries">
		<c:if test="${!empty preqLockingList}">
			<logic:iterate id="preqLinks" name="KualiForm" property="document.newMaintainableObject.preqLinks" indexId="ctr">
				<tr>
					<td class="infoline" align="center">Payment Request - 
						<a href="${ConfigProperties.kew.url}/${KFSConstants.DOC_HANDLER_ACTION}?command=displayDocSearchView&docId=${KualiForm.document.newMaintainableObject.preqLinks[ctr]}"  target="_blank">
							${KualiForm.document.newMaintainableObject.preqLinks[ctr]}
						</a>&nbsp;
					</td>
				</tr>
			</logic:iterate>
		</c:if>
		</table>
	</div>
</kul:tab>
</c:if>
