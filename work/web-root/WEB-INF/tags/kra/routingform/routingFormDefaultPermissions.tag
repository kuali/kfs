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

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>

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
            <%-- %>
            <c:forEach items="${KualiForm.document.routingFormPersonnel}" var="person" varStatus="status">
            	<c:if test="${person.personRoleCode == 'M' || person.personRoleCode == 'N'}">
            		<tr>
            			<td>${person.user.personName}</td>
            			<td>${person.chartOfAccountsCode}</td>
            			<td>${person.organizationCode}</td>
            			<td>${person.personRole.personRoleDescription}</td>
            			<td>Read</td>
            		</tr>
            	</c:if>
            </c:forEach>
            --%>
            <tr>
            			<td>Joe User</td>
            			<td>BL</td>
            			<td>VPIT</td>
            			<td>Contact Person</td>
            			<td>Read</td>
            </tr>
        </table>
	</div>
</kul:tab>