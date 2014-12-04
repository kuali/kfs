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
