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
