<%--
 Copyright 2009 The Kuali Foundation
 
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
<c:set var="delegationMemberAttributes" value="${DataDictionary.RoleDocumentDelegationMember.attributes}" />
<c:set var="roleDocumentDelegationMemberQualifier" value="${DataDictionary.RoleDocumentDelegationMemberQualifier.attributes}" />

<kul:subtab lookedUpCollectionName="delegations" width="${tableWidth}" subTabTitle="Delegations" noShowHideButton="false">      
    <table cellpadding="0" cellspacing="0" summary="">
        <c:if test="${!readOnly}">	          	
          	<tr>
          		<th>&nbsp;</th> 
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${delegationMemberAttributes.roleMemberId}" noColon="true" /> 
            	<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${delegationMemberAttributes.activeFromDate}" noColon="true" /> 
            	<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${delegationMemberAttributes.activeToDate}" noColon="true" /> 
            	<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${delegationMemberAttributes.delegationTypeCode}" noColon="true" /> 
           	<c:if test="${!readOnly}">	
              	<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
          	</c:if>	
          	</tr>     
          	
            <tr>
				<th class="infoline">Add:</th>

                <td align="left" valign="middle" class="infoline">
                <div align="center">
                	${KualiForm.newDelegationMember.roleBo.namespaceCode}&nbsp;${KualiForm.newDelegationMember.roleBo.name}&nbsp;&nbsp;${KualiForm.newDelegationMember.roleMemberNamespaceCode}&nbsp;${KualiForm.newDelegationMember.roleMemberName}
                	<kul:lookup boClassName="org.kuali.rice.kim.impl.role.RoleBo" fieldConversions="id:newDelegationMemberRoleId,kimTypeId:newDelegationMember.roleBo.kimTypeId,name:newDelegationMember.roleBo.name,namespaceCode:newDelegationMember.roleBo.namespaceCode,kimRoleType.name:newDelegationMember.roleBo.kimRoleType.name,kimRoleType.serviceName:newDelegationMember.roleBo.kimRoleType.serviceName" anchor="${tabKey}" />

					<html:hidden property="newDelegationMember.roleBo.name" />
					<html:hidden property="newDelegationMember.roleBo.id" />
					<html:hidden property="newDelegationMember.roleBo.namespaceCode" />
					<html:hidden property="newDelegationMember.roleBo.kimTypeId" />
					<html:hidden property="newDelegationMember.roleBo.kimRoleType.name" />
					<html:hidden property="newDelegationMember.roleBo.kimRoleType.serviceName" />
					<html:hidden property="newDelegationMember.roleMemberId" />
	            </div>
				</td>
                <td align="left" valign="middle">
                	<div align="center"> <kul:htmlControlAttribute property="newDelegationMember.activeFromDate"  attributeEntry="${delegationMemberAttributes.activeToDate}"  datePicker="true" readOnly="${readOnly}" />
				</div>
				</td>

				</td>
                <td align="left" valign="middle">
                	<div align="center"> <kul:htmlControlAttribute property="newDelegationMember.activeToDate"  attributeEntry="${delegationMemberAttributes.activeToDate}"  datePicker="true" readOnly="${readOnly}" />
				</div>
				</td>
                <td align="left" valign="middle" class="infoline">
	                <div align="center">
	                	<kul:htmlControlAttribute property="newDelegationMember.delegationTypeCode" 
	                	attributeEntry="${delegationMemberAttributes.delegationTypeCode}" disabled="${readOnly}" />
		            </div>
				</td>
                <td class="infoline">
					<div align="center">
						<html:image property="methodToCall.addDelegationMember.anchor${tabKey}"
							src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton"/>
					</div>
                </td>
       		</tr>         
     	</c:if>       
        <c:forEach var="delegationMember" items="${KualiForm.document.delegationMembers}" varStatus="status">
        	<%-- add header label for each 'role' to see if it is less confusion for user --%>
          	<tr>
          		<th>&nbsp;</th> 
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${delegationMemberAttributes.roleMemberId}" noColon="true" /> 
            	<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${delegationMemberAttributes.activeFromDate}" noColon="true" /> 
            	<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${delegationMemberAttributes.activeToDate}" noColon="true" /> 
	           	<c:if test="${!readOnly}">	
	              	<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
	          	</c:if>	
          	</tr>             	
	        <tr>
                <%-- TODO : try 'valign' to see if it helps user--%>
				<th rowspan="1" class="infoline" valign="top">
					<c:out value="${status.index+1}" />
				</th>
                <td align="left" valign="middle">
                	<div align="center"> 
           	        <b>Role:</b> ${KualiForm.document.delegationMembers[status.index].roleBo.namespaceCode}&nbsp;${KualiForm.document.delegationMembers[status.index].roleBo.name}&nbsp;&nbsp;<b>Role Member:</b>&nbsp;${KualiForm.document.delegationMembers[status.index].roleMemberNamespaceCode}&nbsp;${KualiForm.document.delegationMembers[status.index].roleMemberName}
				</div>
				</td>
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.delegationMembers[${status.index}].activeFromDate"  attributeEntry="${delegationMemberAttributes.activeFromDate}" readOnly="${readOnly}" datePicker="true" />
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.delegationMembers[${status.index}].activeToDate"  attributeEntry="${delegationMemberAttributes.activeToDate}" readOnly="${readOnly}" datePicker="true" />
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.delegationMembers[${status.index}].delegationTypeCode"  attributeEntry="${delegationMemberAttributes.delegationTypeCode}" disabled="${readOnly}" readOnly="false" />
			
           		<c:if test="${!readOnly}">						
					<td>
						<div align=center>&nbsp;
			        	     <c:choose>
				        	       <c:when test="${role.edit or readOnly}">
				        	          <img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete2.gif' styleClass='tinybutton'/>
				        	       </c:when>
				        	       <c:otherwise>
				        	          <html:image property='methodToCall.deleteDelegationMember.line${status.index}.anchor${currentTabIndex}'
											src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif' styleClass='tinybutton'/>
				        	       </c:otherwise>
			        	     </c:choose>  
						</div>
	                </td>
	           </c:if>     
	      	</tr>
	        <c:if test="${fn:length(delegationMember.attributesHelper.definitions) != 0}">	
            	<tr>
	              <td colspan="7" style="padding:0px;">
	              	<kim:personDelegationMemberQualifier delegationMemberIdx="${status.index}" />
		          </td>
		        </tr>
			</c:if>	 
       	</c:forEach>                   
    </table>
</kul:subtab>
