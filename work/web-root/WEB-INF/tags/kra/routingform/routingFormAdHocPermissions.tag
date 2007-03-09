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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>

<%@ attribute name="editingMode" required="false" type="java.util.Map"%>

<%-- derive displayReadOnly value --%>
<c:set var="displayReadOnly" value="false" />
<c:set var="routingFormAdHocPersonAttributes" value="${DataDictionary.RoutingFormAdHocPerson.attributes}" />
<c:set var="routingFormAdHocOrgAttributes" value="${DataDictionary.RoutingFormAdHocOrg.attributes}" />
<c:set var="routingFormAdHocWorkgroupAttributes" value="${DataDictionary.RoutingFormAdHocWorkgroup.attributes}" />

<c:if test="${!empty editingMode['viewOnly']}" >
    <c:set var="displayReadOnly" value="true" />
</c:if>

<kul:tabTop tabTitle="Ad Hoc Permissions" defaultOpen="false" tabErrorKey="${Constants.AD_HOC_ROUTE_ERRORS}">
   	<div class="tab-container" align=center>     
		<div class="h2-container">
			<h2>Ad Hoc Permissions</h2>
		</div>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="view/edit ad hoc permissions">
		<%-- first do the persons --%>
        	<kul:displayIfErrors keyMatch="${Constants.AD_HOC_ROUTE_PERSON_ERRORS}">
			  	<tr>
        			<th colspan=3>
            			<kul:errors keyMatch="${Constants.AD_HOC_ROUTE_PERSON_ERRORS}" />
        			</th>
    		  	</tr>    
		  	</kul:displayIfErrors>
            <tr>
               	<td colspan=6 class="tab-subhead">Ad Hoc People:</td>
            </tr>
          	<tr>
            	<kul:htmlAttributeHeaderCell
                 attributeEntry="${DataDictionary.AdHocRoutePerson.attributes.id}"
                 scope="col"
                />
               	<kul:htmlAttributeHeaderCell
                 attributeEntry="${DataDictionary.Chart.attributes.chartOfAccountsCode}"
                 scope="col"
                 hideRequiredAsterisk="true"
                />
                <kul:htmlAttributeHeaderCell
                 attributeEntry="${DataDictionary.Org.attributes.organizationCode}"
                 scope="col"
                 hideRequiredAsterisk="true"
                />
                <kul:htmlAttributeHeaderCell
                 attributeEntry="${routingFormAdHocPersonAttributes.permissionCode}"
                 scope="col"
                />
                <c:if test="${not displayReadOnly}">
               		<kul:htmlAttributeHeaderCell
                   	 literalLabel="Actions"
                   	 scope="col"
                   	/>
            	</c:if>
            </tr>
			<c:if test="${!displayReadOnly}">
               	<tr>
                   	<td class="infoline">
                       	<kul:htmlControlAttribute property="newAdHocPerson.personUniversalIdentifier" attributeEntry="${routingFormAdHocPersonAttributes.personUniversalIdentifier}" readOnly="${displayReadOnly}" />
                       	<kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser"  fieldConversions="personUserIdentifier:newAdHocRoutePerson.personUniversalIdentifier" />
                   	</td>
                   	<td class="infoline"><div align=center>--</div></td>
                   	<td class="infoline"><div align=center>--</div></td>
                   	<td class="infoline">
						<div align="left">
							<kul:htmlControlAttribute property="newAdHocPerson.permissionCode" attributeEntry="${routingFormAdHocPersonAttributes.permissionCode}"/>
						</div>
					</td>
					<c:if test="${not displayReadOnly}">
                   		<td class="infoline" ><div align=center>
                       		<html:image property="methodToCall.insertAdHocPerson" src="images/tinybutton-add1.gif" alt="Insert Additional Ad Hoc Person" styleClass="tinybutton"/></div>
                   		</td>
                   	</c:if>
               	</tr>
			</c:if>
			<c:forEach items="${KualiForm.document.routingFormAdHocPeople}" var="person" varStatus="status">
				<tr>
                    <td class="datacell center">
                    	<div align=left>${person.user.personUserIdentifier}</div>
                    	<html:hidden property="document.routingFormAdHocPerson[${status.index}].user.personUserIdentifier" />
                    </td>
                    <td>${person.user.campusCode}<html:hidden property="document.routingFormAdHocPerson[${status.index}].user.campusCode" /></td>
                    <td>${person.orgCode}<html:hidden property="document.routingFormAdHocPerson[${status.index}].user.deptid" /></td>
                    <td>
                    	<c:if test="${displayReadOnly}"><html:hidden property="document.routingFormAdHocPerson[${status.index}].permissionCode" /></c:if>
                    	<kul:htmlControlAttribute property="document.routingFormAdHocPerson[${status.index}].permissionCode" attributeEntry="${routingFormAdHocPersonAttributes.permissionCode}" readOnly="${displayReadOnly}"/>
						<html:hidden property="document.routingFormAdHocPerson[${status.index}].personUniversalIdentifier" />
						<html:hidden property="document.routingFormAdHocPerson[${status.index}].addedByPerson" />
						<html:hidden property="document.routingFormAdHocPerson[${status.index}].personAddedTimestamp" />
						<html:hidden property="document.routingFormAdHocPerson[${status.index}].objectId" />
						<html:hidden property="document.routingFormAdHocPerson[${status.index}].versionNumber" />
					</td>
					<c:if test="${not displayReadOnly}">
	                    <td class="datacell center"><div align=center>
                           	<html:image property="methodToCall.deleteAdHocPerson.line${status.index}" src="images/tinybutton-delete1.gif" alt="delete" styleClass="tinybutton"/></div>
                    	</td>
                    </c:if>
                </tr>
			</c:forEach>
			<%-- next do the orgs --%>
            <kul:displayIfErrors keyMatch="newAdHocOrg">
				<tr>
        			<th colspan=3>
            			<kul:errors keyMatch="newAdHocOrg" />
        			</th>
    		  	</tr>    
		  	</kul:displayIfErrors>
            <tr>
				<td colspan=6 class="tab-subhead">Ad Hoc Orgs:</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell
                 literalLabel="* Chart/Org"
                 scope="col"
                 forceRequired="true"
                 colspan="3"
                />
				<kul:htmlAttributeHeaderCell
                 attributeEntry="${routingFormAdHocOrgAttributes.permissionCode}"
                 scope="col"
                />
				<c:if test="${not displayReadOnly}"><th scope=col>Actions</th></c:if>
			</tr>
			
			<c:if test="${not displayReadOnly}">
			<tr>
				<td nowrap class="infoline" colspan="3">
					<c:choose>
						<c:when test="${empty KualiForm.newAdHocOrg.fiscalCampusCode}">(select by org)</c:when>
						<c:otherwise>
							${KualiForm.newAdHocOrg.fiscalCampusCode}/${KualiForm.newAdHocOrg.primaryDepartmentCode}
							<html:hidden property="newAdHocOrg.fiscalCampusCode"/>
							<html:hidden property="newAdHocOrg.primaryDepartmentCode"/>
						</c:otherwise>
					</c:choose>
					&nbsp;&nbsp;
					<kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:newAdHocOrg.fiscalCampusCode,organizationCode:newAdHocOrg.primaryDepartmentCode" />
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
				<td class="infoline">
					<div align="left">
						<kul:htmlControlAttribute property="newAdHocOrg.permissionCode" attributeEntry="${routingFormAdHocOrgAttributes.permissionCode}"/>
					</div>
				</td>
				<td class="infoline">
					<div align=center>
						<html:image property="methodToCall.insertAdHocOrg" src="images/tinybutton-add1.gif" styleClass="tinybutton" />
					</div>
				</td>
			</tr>
			</c:if>
			
			<c:forEach items="${KualiForm.document.routingFormAdHocOrgs}" var="org" varStatus="status">
				<tr>
					<td colspan="3">${org.fiscalCampusCode}/${org.primaryDepartmentCode}</td>
					<td>
						<c:if test="${displayReadOnly}"><html:hidden property="document.routingFormAdHocOrg[${status.index}].permissionCode" /></c:if>
						<kul:htmlControlAttribute property="document.routingFormAdHocOrg[${status.index}].permissionCode" attributeEntry="${routingFormAdHocOrgAttributes.permissionCode}" readOnly="${displayReadOnly}"/>
					</td>
					<c:if test="${not displayReadOnly}">
						<td>
							<div align="center">
								<html:image property="methodToCall.deleteOrg.line${status.index}" src="images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete" disabled="${displayReadOnly}"/>
							</div>
							<html:hidden property="document.routingFormAdHocOrg[${status.index}].fiscalCampusCode" />
							<html:hidden property="document.routingFormAdHocOrg[${status.index}].primaryDepartmentCode" />
							<html:hidden property="document.routingFormAdHocOrg[${status.index}].addedByPerson" />
							<html:hidden property="document.routingFormAdHocOrg[${status.index}].personAddedTimestamp" />
							<html:hidden property="document.routingFormAdHocOrg[${status.index}].objectId" />
							<html:hidden property="document.routingFormAdHocOrg[${status.index}].versionNumber" />
						</td>
					</c:if>
				</tr>
			</c:forEach>
		  	<%-- next do the workgroups --%>
		  	<kul:displayIfErrors keyMatch="${Constants.AD_HOC_ROUTE_WORKGROUP_ERRORS}">
			  	<tr>
        			<th colspan=3>
            			<kul:errors keyMatch="${Constants.AD_HOC_ROUTE_WORKGROUP_ERRORS}" />
        			</th>
    		  	</tr>    
		  	</kul:displayIfErrors>
	      	<tr>
               	<td colspan=6 class="tab-subhead">Ad Hoc Workgroups:</td>
            </tr>
          	<tr>
                <kul:htmlAttributeHeaderCell
                 attributeEntry="${routingFormAdHocWorkgroupAttributes.id}"
                 scope="col"
                 colspan="3"
                />
                <kul:htmlAttributeHeaderCell
                 attributeEntry="${routingFormAdHocWorkgroupAttributes.permissionCode}"
                 scope="col"
                />
                <c:if test="${not displayReadOnly}">
                	<kul:htmlAttributeHeaderCell
                     literalLabel="Actions"
                     scope="col"
                    />
                </c:if>
            </tr>
			<c:if test="${!displayReadOnly}">
               	<tr>
                   	<td class="infoline" colspan="3">
                       	<kul:htmlControlAttribute property="newAdHocWorkgroup.workgroupName" attributeEntry="${routingFormAdHocWorkgroupAttributes.workgroupName}" readOnly="${displayReadOnly}" />
                       	<kul:workflowWorkgroupLookup fieldConversions="workgroupName:newAdHocWorkgroup.workgroupName" />
                   	</td>
                   	<td class="infoline">
                   		<div align="left">
                   			<kul:htmlControlAttribute property="newAdHocWorkgroup.permissionCode" attributeEntry="${routingFormAdHocWorkgroupAttributes.permissionCode}"/>
						</div>
					</td>
					<c:if test="${not displayReadOnly}">
                   		<td class="infoline"><div align=center>
                       		<html:image property="methodToCall.insertAdHocWorkgroup" src="images/tinybutton-add1.gif" alt="Insert Additional Ad Hoc Workgroup" styleClass="tinybutton"/></div>
                   		</td>
                   	</c:if>
               	</tr>
			</c:if>
            <c:forEach items="${KualiForm.document.routingFormAdHocWorkgroups}" var="workgroup" varStatus="status">
				<tr>
                    <td colspan="3" class="datacell center">
                    	<div align=left>${workgroup.workgroupName}</div>
                    	<html:hidden property="document.routingFormAdHocWorkgroup[${status.index}].workgroupName" />
                    </td>
                    <td>
                    	<c:if test="${displayReadOnly}"><html:hidden property="document.routingFormAdHocWorkgroup[${status.index}].permissionCode" /></c:if>
                    	<kul:htmlControlAttribute property="document.routingFormAdHocWorkgroup[${status.index}].permissionCode" attributeEntry="${routingFormAdHocWorkgroupAttributes.permissionCode}" readOnly="${displayReadOnly}"/>
                    	<html:hidden property="document.routingFormAdHocWorkgroup[${status.index}].addedByPerson" />
						<html:hidden property="document.routingFormAdHocWorkgroup[${status.index}].personAddedTimestamp" />
						<html:hidden property="document.routingFormAdHocWorkgroup[${status.index}].objectId" />
						<html:hidden property="document.routingFormAdHocWorkgroup[${status.index}].versionNumber" />
					</td>
					<c:if test="${not displayReadOnly}">
	                    <td class="datacell center"><div align=center>
                           	<html:image property="methodToCall.deleteAdHocWorkgroup.line${status.index}" src="images/tinybutton-delete1.gif" alt="delete" styleClass="tinybutton"/></div>
                    	</td>
                    </c:if>
                </tr>
			</c:forEach>
        </table>
    </div>
</kul:tabTop>
