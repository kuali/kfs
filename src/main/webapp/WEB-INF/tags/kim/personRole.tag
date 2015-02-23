<%--
 Copyright 2008-2009 The Kuali Foundation
 
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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="docRoleAttributes" value="${DataDictionary.PersonDocumentRole.attributes}" />
<c:set var="docRolePrncplAttributes" value="${DataDictionary.KimDocumentRoleMember.attributes}" />

<kul:subtab lookedUpCollectionName="role" width="${tableWidth}" subTabTitle="Roles" noShowHideButton="false">      
    <table cellpadding="0" cellspacing="0" summary="">
        <c:if test="${!readOnly}">	          	
          	<tr>
          		<th>&nbsp;</th> 
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docRoleAttributes.roleId}" noColon="true" />
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docRoleAttributes.namespaceCode}" noColon="true" /> 
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docRoleAttributes.roleName}" noColon="true" /> 
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docRoleAttributes.kimTypeId}" noColon="true" /> 
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docRolePrncplAttributes.activeFromDate}" noColon="true" /> 
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docRolePrncplAttributes.activeToDate}" noColon="true" />  
           	<c:if test="${!readOnly}">	
              	<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
          	</c:if>	
          	</tr>     
          	
            <tr>
				<th class="infoline">Add:</th>

                <td align="left" valign="middle" class="infoline">
                <div align="center">
                	<kul:htmlControlAttribute property="newRole.roleId" attributeEntry="${docRoleAttributes.roleId}" readOnly="${readOnly}" />
                	<kul:lookup boClassName="org.kuali.rice.kim.impl.role.RoleBo" fieldConversions="id:newRole.newRolePrncpl.memberId,id:newRole.roleId,kimTypeId:newRole.kimTypeId,name:newRole.roleName,namespaceCode:newRole.namespaceCode,kimRoleType.name:newRole.kimRoleType.name,kimRoleType.serviceName:newRole.kimRoleType.serviceName" anchor="${tabKey}" />
					
					<html:hidden property="newRole.roleName" />
					<html:hidden property="newRole.roleId" />
					<html:hidden property="newRole.newRolePrncpl.memberId" />
					<html:hidden property="newRole.namespaceCode" />
					<html:hidden property="newRole.kimTypeId" />
					<html:hidden property="newRole.kimRoleType.name" />
					<html:hidden property="newRole.kimRoleType.serviceName" />
	            </div>
				</td>
				<td align="center">${KualiForm.newRole.namespaceCode}&nbsp;</td>
                <td align="left" valign="middle" class="infoline">
                <div align="center">
                	<kul:htmlControlAttribute property="newRole.roleName" attributeEntry="${docRoleAttributes.roleName}" disabled="true"/>
	            </div>
				</td>
				<td align="center">${KualiForm.newRole.kimRoleType.name}</td>
                <td align="left" valign="middle">
                	<div align="center"> <kul:htmlControlAttribute property="newRole.newRolePrncpl.activeFromDate"  attributeEntry="${docRolePrncplAttributes.activeFromDate}"  datePicker="true" readOnly="${readOnly}" />
				</div>
				</td>
                <td align="left" valign="middle">
                	<div align="center"> <kul:htmlControlAttribute property="newRole.newRolePrncpl.activeToDate"  attributeEntry="${docRolePrncplAttributes.activeToDate}"  datePicker="true" readOnly="${readOnly}" />
				</div>
				</td>

                <td class="infoline">
					<div align="center">
						<html:image property="methodToCall.addRole.anchor${tabKey}"
							src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton"/>
					</div>
                </td>
       		</tr>         
     	</c:if>       
        <c:forEach var="role" items="${KualiForm.document.roles}" varStatus="status">
			<c:set var="readOnlyRole" scope="request" value="${!role.editable || readOnly}" />
        	<%-- add header label for each 'role' to see if it is less confusion for user --%>
          	<tr>
          		<th>&nbsp;</th> 
	           	<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docRoleAttributes.roleId}" noColon="true" />
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docRoleAttributes.namespaceCode}" noColon="true" /> 
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docRoleAttributes.roleName}" noColon="true" /> 
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docRoleAttributes.kimTypeId}" noColon="true" /> 
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docRolePrncplAttributes.activeFromDate}" noColon="true" /> 
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docRolePrncplAttributes.activeToDate}" noColon="true" />
	           	<c:if test="${!readOnly}">	
	              	<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
	          	</c:if>	
          	</tr>             	
       	    <c:set var="rows" value="2"/>
       		<c:if test="${empty role.definitions and (empty role.rolePrncpls or fn:length(role.rolePrncpls) < 1 or fn:length(role.rolePrncpls[0].roleRspActions) < 1)}">	
        	       <c:set var="rows" value="1"/>       		
       		</c:if>        	
	        <tr>
                <%-- TODO : try 'valign' to see if it helps user--%>
				<th rowspan="${rows}" class="infoline" valign="top">
					<c:out value="${status.index+1}" />
				</th>
				</td>
				<kim:cell inquiry="${inquiry}" valign="middle" textAlign="center" property="document.roles[${status.index}].roleId"  attributeEntry="${docRoleAttributes.roleId}" readOnly="true" />
                <kim:cell inquiry="${inquiry}" valign="middle" textAlign="center" property="document.roles[${status.index}].namespaceCode"  attributeEntry="${docRoleAttributes.namespaceCode}" readOnly="true" />
                <kim:cell inquiry="${inquiry}" valign="middle" textAlign="center" property="document.roles[${status.index}].roleName"  attributeEntry="${docRoleAttributes.roleName}" readOnly="true" />
                <kim:cell inquiry="${inquiry}" valign="middle" textAlign="center" property="document.roles[${status.index}].kimRoleType.name"  attributeEntry="${docRoleAttributes['kimRoleType.name']}" readOnly="true" />

				<c:set var="roleMemberActiveDatesReadOnly" value="${(!empty role.definitions and fn:length(role.definitions) > 0) || readOnlyRole}" />
                <td align="left" valign="middle">
                	<c:if test="${fn:length(role.rolePrncpls) > 0}">
                		<div align="center"> <kul:htmlControlAttribute property="document.roles[${status.index}].rolePrncpls[0].activeFromDate"  attributeEntry="${docRolePrncplAttributes.activeFromDate}"  datePicker="true" readOnly="${roleMemberActiveDatesReadOnly}" />
						</div>
					</c:if>
				</td>
                <td align="left" valign="middle">
                	<c:if test="${fn:length(role.rolePrncpls) > 0}">
	               		<div align="center"> <kul:htmlControlAttribute property="document.roles[${status.index}].rolePrncpls[0].activeToDate"  attributeEntry="${docRolePrncplAttributes.activeToDate}"  datePicker="true" readOnly="${roleMemberActiveDatesReadOnly}" />
						</div>
					</c:if>
				</td>
           		<c:if test="${!readOnlyRole}">
					<td>
						<div align=center>&nbsp;

				        	          <html:image property='methodToCall.deleteRole.line${status.index}.anchor${currentTabIndex}'
											src='${ConfigProperties.kr.externalizable.images.url}tinybutton-inactivate.gif' styleClass='tinybutton'/>

						</div>
	                </td>
	           </c:if>     
	      	</tr>
		    <c:choose>
	            <c:when test="${!empty role.definitions  and fn:length(role.definitions) > 0}" >
	            	<tr>
		              <td colspan="7" style="padding:0px;">
		              	<kim:personRoleQualifier roleIdx="${status.index}" role="${role}" />
			          </td>
			        </tr>
 		        </c:when>
		        <c:otherwise>
			         <c:if test="${fn:length(role.rolePrncpls[0].roleRspActions) > 0}">	
	     			    <tr>
			              <td colspan="7" style="padding:0px;">
			              	<kim:roleResponsibilityAction roleIdx="${status.index}" mbrIdx="0" />
				          </td>
				        </tr>
					</c:if>	      			        			        
 		        </c:otherwise>
		     </c:choose>
       	</c:forEach>                   
    </table>
</kul:subtab>
