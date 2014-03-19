<%--
 Copyright 2007-2014 The Kuali Foundation
 
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

<c:if test="${KualiForm.shouldDisplayAgencyLinks}">
	<kul:tab tabTitle="Agency Links" defaultOpen="false">
		<div class="tab-container" align="center">
			<h3><bean:message key="${TemKeyConstants.TAB_NAME_AGENCY_LINKS}"/></h3>
			<table cellpadding="0" cellspacing="0" class="datatable" summary="Agency Links Section">
				<logic:iterate id="agencyLink" name="KualiForm" property="agencyLinks">
					<tr>
						<td width="100%">
							<a href="<c:out value="${agencyLink.hrefText}"/>" target="<c:out value="${agencyLink.target}"/>"><c:out value="${agencyLink.linkLabel}" /></a>
						</td>
					<tr>
				</logic:iterate>
			</table>
		</div>
	</kul:tab>
</c:if>