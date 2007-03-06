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

<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:tab tabTitle="Default Permissions" defaultOpen="false">
	<div class="tab-container" align=center>
		<div class="h2-container">
			<h2>Default Permissions</h2>
		</div>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="view default permissions">
          	<tr>
            	<th width="40%">Name</th>
            	<th width="12%">Chart</th>
            	<th width="12%">Org</th>
            	<th>Role</th>
            	<th width="12%">Type</th>
            </tr>
            <c:forEach items="${KualiForm.document.routingFormPersonnel}" var="person" varStatus="status">
				<c:if test="${!person.personToBeNamedIndicator && person.personRoleCode == 'M' || person.personRoleCode == 'N'}">
            		<tr>
            			<td>${person.user.personName}&nbsp;</td>
            			<td>${person.chartOfAccountsCode}&nbsp;</td>
            			<td>${person.organizationCode}&nbsp;</td>
            			<td>${person.personRole.personRoleDescription}&nbsp;</td>
            			<td>
            				Read
            				<html:hidden property="document.routingFormPersonnel[${status.index}].personRoleCode" />
            				<html:hidden property="document.routingFormPersonnel[${status.index}].user.personName" />
            				<html:hidden property="document.routingFormPersonnel[${status.index}].chartOfAccountsCode" />
            				<html:hidden property="document.routingFormPersonnel[${status.index}].organizationCode" />
            				<html:hidden property="document.routingFormPersonnel[${status.index}].personRole.personRoleDescription" />
            			</td>
            		</tr>
            	</c:if>
            </c:forEach>
        </table>
	</div>
</kul:tab>