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

<kul:tab tabTitle="Default Permissions" defaultOpen="false">
	<div class="tab-container" align=center>
			<h3>Default Permissions</h3>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="view default permissions">
          	<tr>
            	<th width="40%">Name</th>
            	<th width="12%">Chart</th>
            	<th width="12%">Org</th>
            	<th>Role</th>
            	<th width="12%">Type</th>
            </tr>
            <c:forEach items="${KualiForm.document.routingFormPersonnel}" var="person" varStatus="status">
				<c:if test="${!person.personToBeNamedIndicator 
								&& (person.personRoleCode == CGConstants.CONTACT_PERSON_ADMINISTRATIVE_CODE 
								|| person.personRoleCode == CGConstants.CONTACT_PERSON_PROPOSAL_CODE
								|| person.personRoleCode == CGConstants.PROJECT_DIRECTOR_CODE)}">
            		<tr>
            			<td>${person.user.name}&nbsp;</td>
            			<td>${person.chartOfAccountsCode}&nbsp;</td>
            			<td>${person.organizationCode}&nbsp;</td>
            			<td>${person.personRole.personRoleDescription}&nbsp;</td>
            			<td>
            				Read
            			</td>
            		</tr>
            	</c:if>
            </c:forEach>
        </table>
	</div>
</kul:tab>

