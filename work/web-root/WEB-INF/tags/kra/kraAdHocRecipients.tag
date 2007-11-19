<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="adhocType" required="true" %>
<%@ attribute name="adhocLabel" required="true" %>
<%@ attribute name="excludeActionRequested" required="false" %>
<%@ attribute name="disableActionRequested" required="false" %>
<%@ attribute name="actionRequestedDefault" required="false" %>
<%@ attribute name="actionRequestedMessage" required="false" %>
<%@ attribute name="editingMode" required="false" type="java.util.Map"%>

<%-- derive displayReadOnly value --%>
<c:set var="displayReadOnly" value="false" />
<c:set var="adhocPersonAttributes" value="${DataDictionary.AdhocPerson.attributes}" />
<c:set var="adhocOrgAttributes" value="${DataDictionary.AdhocOrg.attributes}" />
<c:set var="adhocWorkgroupAttributes" value="${DataDictionary.AdhocWorkgroup.attributes}" />

<c:choose>
	<c:when test="${excludeActionRequested}">
		<c:set var="numCols" value="5" />
	</c:when>
	<c:otherwise>
		<c:set var="numCols" value="6" />
	</c:otherwise>
</c:choose>

<c:if test="${!empty editingMode['viewOnly']}" >
    <c:set var="displayReadOnly" value="true" />
</c:if>

	<kul:tabTop tabTitle="Ad Hoc ${adhocLabel}" defaultOpen="false" tabErrorKey="${Constants.AD_HOC_ROUTE_ERRORS}">
    	<div class="tab-container" align=center>
			<div class="h2-container">
				<h2>Ad Hoc ${adhocLabel}</h2>
			</div>
            <table cellpadding="0" cellspacing="0" class="datatable" summary="view/edit ad hoc recipients">
			  	<%-- first do the persons --%>
              	<kul:displayIfErrors keyMatch="${Constants.AD_HOC_ROUTE_PERSON_ERRORS}">
				  	<tr>
	        			<th colspan=3>
	            			<kul:errors keyMatch="${Constants.AD_HOC_ROUTE_PERSON_ERRORS}" />
	        			</th>
	    		  	</tr>    
			  	</kul:displayIfErrors>
              	<tr>
                	<td colspan="${numCols}" class="tab-subhead">Person ${adhocLabel}:</td>
              	</tr>
	          	<tr>
	          		<c:if test="${!excludeActionRequested}">
                  		<kul:htmlAttributeHeaderCell
                      	attributeEntry="${DataDictionary.AdHocRoutePerson.attributes.actionRequested}"
                      	scope="col"
                      	/>
                    </c:if>
                  	<kul:htmlAttributeHeaderCell
                      attributeEntry="${DataDictionary.AdHocRoutePerson.attributes.id}"
                      scope="col"
                  	  colspan="2"
                      />
                  	<kul:htmlAttributeHeaderCell
                  	  literalLabel="Chart/Org"
                  	  scope="col"
                  	  hideRequiredAsterisk="true"
                  	  />
                  	<kul:htmlAttributeHeaderCell
                  		attributeEntry="${adhocPersonAttributes.permissionCode}"
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
                		<c:if test="${disableActionRequested}"><html:hidden property="newAdHocRoutePerson.actionRequested" value="${actionRequestedDefault}"/></c:if>
                		<c:if test="${!excludeActionRequested}">
                    	<td class="infoline">
                    		<div align=center>
                        		<html:select property="newAdHocRoutePerson.actionRequested" value="${actionRequestedDefault}" disabled="${disableActionRequested}">
  		                    		<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
	    		            		<html:options collection="actionRequestCodes" property="key" labelProperty="value" />
	  			        		</html:select> ${actionRequestedMessage}
	  			        	</div>
                    	</td>
                    	</c:if>
                    	<td class="infoline" colspan="2">
                    	  <div align=center>
	                    	<kul:user userIdFieldName="newAdHocRoutePerson.id" 
	                    			  userId="${KualiForm.newAdHocRoutePerson.id}" 
	                    			  universalIdFieldName=""
	                    			  universalId=""
	                    			  userNameFieldName="newAdHocRoutePerson.name"
	                    			  userName="${KualiForm.newAdHocRoutePerson.name}"
	                    			  readOnly="${displayReadOnly}" 
	                    			  renderOtherFields="true"
	                    			  fieldConversions="personUserIdentifier:newAdHocRoutePerson.id,personName:newAdHocRoutePerson.name" 
	                    			  lookupParameters="newAdHocRoutePerson.id:personUserIdentifier" />
	                      </div>
                    	</td>
                    	<td class="infoline"><div align=center>--</div></td>
                    	<td class="infoline">
							<div align="center">
								<kul:htmlControlAttribute property="newAdHocPerson.permissionCode" attributeEntry="${adhocPersonAttributes.permissionCode}"/>
							</div>
						</td>
						<c:if test="${not displayReadOnly}">
                    		<td class="infoline" ><div align=center>
                        		<html:image property="methodToCall.insertAdHocRoutePerson" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Insert Additional Ad Hoc Person" styleClass="tinybutton"/></div>
                    		</td>
                    	</c:if>
                	</tr>
				</c:if>
				<c:forEach items="${KualiForm.document.adhocPersons}" var="person" varStatus="status">
				  <c:choose>
                   <c:when test="${adhocType == person.adhocTypeCode}">
					  <tr>
					  	<c:if test="${disableActionRequested}"><html:hidden property="document.adhocPersonItem[${status.index}].actionRequested" value="${actionRequestedDefault}"/></c:if>
						<c:if test="${!excludeActionRequested}">
	                    <td class="datacell center">
	                    	<div align=center>
	                    		<html:select property="document.adhocPersonItem[${status.index}].actionRequested" value="${actionRequestedDefault}" disabled="${disableActionRequested}">
  		                    		<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
	    		            		<html:options collection="actionRequestCodes" property="key" labelProperty="value" />
	  			        		</html:select>
	  			        		${actionRequestedMessage}
	  			        	</div>
	                    </td>
	                    </c:if>
	                    <td class="datacell center" colspan="2">
	                    	<div align=center>
		                    	<kul:user userIdFieldName="person[${status.index}].personUserIdentifier" 
		                    			  userId="${KualiForm.document.adhocPersons[status.index].personUserIdentifier}" 
		                    			  universalIdFieldName=""
		                    			  universalId=""
		                    			  userNameFieldName="person[${status.index}].name"
		                    			  userName="${KualiForm.document.adhocPersons[status.index].name}"
		                    			  readOnly="${displayReadOnly}" 
		                    			  renderOtherFields="true"
		                    			  fieldConversions="personUserIdentifier:person.user.personUserIdentifier,personName:person.user.personName" 
		                    			  lookupParameters="person.user.personUserIdentifier:personUserIdentifier" />
	                    	</div>
	                    	<html:hidden property="document.adhocPersonItem[${status.index}].user.personUniversalIdentifier" />
	                    	<html:hidden property="document.adhocPersonItem[${status.index}].user.personUserIdentifier" />
	                    	<html:hidden property="document.adhocPersonItem[${status.index}].user.personFirstName" />
	                    	<html:hidden property="document.adhocPersonItem[${status.index}].user.personLastName" />
	                    </td>
	                    <td>
	                    	<div align=center>
	                    		${person.user.campusCode}<html:hidden property="document.adhocPersonItem[${status.index}].user.campusCode" /> / ${person.primaryDepartmentCode}<html:hidden property="document.adhocPersonItem[${status.index}].user.primaryDepartmentCode" />
	                    	</div>
	                    </td>
	                    <td>
	                      <div align="center">
	                    	<c:if test="${displayReadOnly}"><html:hidden property="document.adhocPersonItem[${status.index}].permissionCode" /></c:if>
	                    	<kul:htmlControlAttribute property="document.adhocPersonItem[${status.index}].permissionCode" attributeEntry="${adhocPersonAttributes.permissionCode}" readOnly="${displayReadOnly}"/>
							<html:hidden property="document.adhocPersonItem[${status.index}].personUniversalIdentifier" />
							<html:hidden property="document.adhocPersonItem[${status.index}].adhocTypeCode" />
							<html:hidden property="document.adhocPersonItem[${status.index}].addedByPerson" />
							<html:hidden property="document.adhocPersonItem[${status.index}].personAddedTimestamp" />
							<html:hidden property="document.adhocPersonItem[${status.index}].objectId" />
							<html:hidden property="document.adhocPersonItem[${status.index}].versionNumber" />
						  </div>
						</td>
						<c:if test="${not displayReadOnly}">
		                    <td class="datacell center"><div align=center>
                            	<html:image property="methodToCall.delete.line${status.index}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="delete" styleClass="tinybutton"/></div>
	                    	</td>
	                    </c:if>
	                  </tr>
	                </c:when>
	                <c:otherwise>
	                	<html:hidden property="document.adhocPersonItem[${status.index}].actionRequested" />
	                	<html:hidden property="document.adhocPersonItem[${status.index}].personUniversalIdentifier" />
	                	<html:hidden property="document.adhocPersonItem[${status.index}].permissionCode" />
	                	<html:hidden property="document.adhocPersonItem[${status.index}].addedByPerson" />
	                    <html:hidden property="document.adhocPersonItem[${status.index}].adhocTypeCode" />
						<html:hidden property="document.adhocPersonItem[${status.index}].personAddedTimestamp" />
						<html:hidden property="document.adhocPersonItem[${status.index}].objectId" />
						<html:hidden property="document.adhocPersonItem[${status.index}].versionNumber" />
	                </c:otherwise>
	               </c:choose>
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
					<td colspan="${numCols}" class="tab-subhead">Ad Hoc Org ${adhocLabel}:</td>
				</tr>
				<tr>
					<c:if test="${!excludeActionRequested}">
					<kul:htmlAttributeHeaderCell
                      	attributeEntry="${adhocOrgAttributes.actionRequested}"
                      	scope="col"
                    />
                    </c:if>
					<kul:htmlAttributeHeaderCell
                    	literalLabel="* Chart/Org"
                    	scope="col"
                    	forceRequired="true"
                    	colspan="3"
                    	/>
					<kul:htmlAttributeHeaderCell
                  		attributeEntry="${adhocOrgAttributes.permissionCode}"
                  		scope="col"
                  	  />
					<c:if test="${not displayReadOnly}"><th scope=col>Actions</th></c:if>
				</tr>
				
				<c:if test="${not displayReadOnly}">
				<tr>
					<c:if test="${disableActionRequested}"><html:hidden property="newAdHocOrg.actionRequested" value="${actionRequestedDefault}"/></c:if>
					<c:if test="${!excludeActionRequested}">
					<td class="infoline">
	                    <div align=center>
	                    	<html:select property="newAdHocRouteOrg.actionRequested" value="${actionRequestedDefault}" disabled="${disableActionRequested}">
  		                    	<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
	    		            	<html:options collection="actionRequestCodes" property="key" labelProperty="value" />
	  			        	</html:select>
	  			        	${actionRequestedMessage}
	  			      	</div>
	                </td>
	                </c:if>
					<td nowrap class="infoline" colspan="3">
					  <div align=center>
						<c:choose>
							<c:when test="${empty KualiForm.newAdHocOrg.fiscalCampusCode}">&nbsp;</c:when>
							<c:otherwise>
								${KualiForm.newAdHocOrg.fiscalCampusCode}/${KualiForm.newAdHocOrg.primaryDepartmentCode}
								<html:hidden property="newAdHocOrg.fiscalCampusCode"/>
								<html:hidden property="newAdHocOrg.primaryDepartmentCode"/>
							</c:otherwise>
						</c:choose>
						&nbsp;&nbsp;
						<kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:newAdHocOrg.fiscalCampusCode,organizationCode:newAdHocOrg.primaryDepartmentCode" />
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				      </div>
					</td>
					<td class="infoline">
						<div align="center">
							<kul:htmlControlAttribute property="newAdHocOrg.permissionCode" attributeEntry="${adhocOrgAttributes.permissionCode}"/>
						</div>
					</td>
					<td class="infoline">
						<div align=center>
							<html:image property="methodToCall.addOrg" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" />
						</div>
					</td>
				</tr>
				</c:if>
				
				<c:forEach items="${KualiForm.document.adhocOrgs}" var="org" varStatus="status">
				  <c:choose>
                   <c:when test="${adhocType == org.adhocTypeCode}">
					<tr>
						<c:if test="${disableActionRequested}"><html:hidden property="document.adhocOrgItem[${status.index}].actionRequested" value="${actionRequestedDefault}"/></c:if>
						<c:if test="${!excludeActionRequested}">
						<td class="datacell center">
	                    	<div align=center>
	                    		<html:select property="document.adhocOrgItem[${status.index}].actionRequested" value="${actionRequestedDefault}" disabled="${disableActionRequested}">
  		                    		<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
	    		            		<html:options collection="actionRequestCodes" property="key" labelProperty="value" />
	  			        		</html:select>
	  			        		${actionRequestedMessage}
	  			        	</div>
	                    </td>
	                    </c:if>
						<td colspan="3">${org.fiscalCampusCode}/${org.primaryDepartmentCode}</td>
						<td>
						  <div align="center">
							<c:if test="${displayReadOnly}"><html:hidden property="document.adhocOrgItem[${status.index}].permissionCode" /></c:if>
							<kul:htmlControlAttribute property="document.adhocOrgItem[${status.index}].permissionCode" attributeEntry="${adhocOrgAttributes.permissionCode}" readOnly="${displayReadOnly}"/>
							<html:hidden property="document.adhocOrgItem[${status.index}].fiscalCampusCode" />
							<html:hidden property="document.adhocOrgItem[${status.index}].primaryDepartmentCode" />
							<html:hidden property="document.adhocOrgItem[${status.index}].adhocTypeCode" />
							<html:hidden property="document.adhocOrgItem[${status.index}].addedByPerson" />
							<html:hidden property="document.adhocOrgItem[${status.index}].personAddedTimestamp" />
							<html:hidden property="document.adhocOrgItem[${status.index}].objectId" />
							<html:hidden property="document.adhocOrgItem[${status.index}].versionNumber" />
						  </div>
						</td>
						<c:if test="${not displayReadOnly}">
						<td>
							<div align="center">
								<html:image property="methodToCall.deleteOrg.line${status.index}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="delete" disabled="${displayReadOnly}"/>
							</div>
						</td>
						</c:if>
					  </tr>
					</c:when>
					<c:otherwise>
	                	<html:hidden property="document.adhocOrgItem[${status.index}].actionRequested" />
	                	<html:hidden property="document.adhocOrgItem[${status.index}].fiscalCampusCode" />
	                	<html:hidden property="document.adhocOrgItem[${status.index}].primaryDepartmentCode" />
	                	<html:hidden property="document.adhocOrgItem[${status.index}].permissionCode" />
	                	<html:hidden property="document.adhocOrgItem[${status.index}].addedByPerson" />
	                    <html:hidden property="document.adhocOrgItem[${status.index}].adhocTypeCode" />
						<html:hidden property="document.adhocOrgItem[${status.index}].personAddedTimestamp" />
						<html:hidden property="document.adhocOrgItem[${status.index}].objectId" />
						<html:hidden property="document.adhocOrgItem[${status.index}].versionNumber" />
	                </c:otherwise>
				   </c:choose>
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
                	<td colspan="${numCols}" class="tab-subhead">Ad Hoc Workgroup ${adhocLabel}:</td>
              	</tr>
	          	<tr>
	          		<c:if test="${!excludeActionRequested}">
                  	<kul:htmlAttributeHeaderCell
                      attributeEntry="${DataDictionary.AdHocRouteWorkgroup.attributes.actionRequested}"
                      scope="col"
                    />
                    </c:if>
                  	<kul:htmlAttributeHeaderCell
                      attributeEntry="${DataDictionary.AdHocRouteWorkgroup.attributes.id}"
                      scope="col"
                      colspan="3"
                      />
                  	<kul:htmlAttributeHeaderCell
                  		attributeEntry="${adhocWorkgroupAttributes.permissionCode}"
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
                		<c:if test="${disableActionRequested}"><html:hidden property="newAdHocRouteWorkgroup.actionRequested" value="${actionRequestedDefault}"/></c:if>
                		<c:if test="${!excludeActionRequested}">
                    	<td class="infoline">
                    		<html:hidden property="newAdHocRouteWorkgroup.type"/>
                    		<div align=center>
                        		<html:select property="newAdHocRouteWorkgroup.actionRequested" value="${actionRequestedDefault}" disabled="${disableActionRequested}">
  		                    		<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
    		                		<html:options collection="actionRequestCodes" property="key" labelProperty="value"/>
  			            		</html:select> ${actionRequestedMessage}
  			            	</div>
                    	</td>
                    	</c:if>
                    	<td class="infoline" colspan="3">
                    	  <div align=center>
                        	<kul:htmlControlAttribute property="newAdHocRouteWorkgroup.id" attributeEntry="${DataDictionary.AdHocRouteWorkgroup.attributes.id}" readOnly="${displayReadOnly}" />
                        	<kul:workflowWorkgroupLookup fieldConversions="workgroupId:newAdHocRouteWorkgroup.id" />
                          </div>
                    	</td>
                    	<td class="infoline">
                    		<div align="center">
                    			<kul:htmlControlAttribute property="newAdHocWorkgroupPermissionCode" attributeEntry="${adhocWorkgroupAttributes.permissionCode}"/>
							</div>
						</td>
						<c:if test="${not displayReadOnly}">
                    		<td class="infoline"><div align=center>
                        		<html:image property="methodToCall.insertAdHocRouteWorkgroup" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Insert Additional Ad Hoc Workgroup" styleClass="tinybutton"/></div>
                    		</td>
                    	</c:if>
                	</tr>
				</c:if>
                <c:forEach items="${KualiForm.document.adhocWorkgroups}" var="workgroup" varStatus="status">
                 <c:choose>
                  <c:when test="${adhocType == workgroup.adhocTypeCode}">
					<tr>
						<c:if test="${disableActionRequested}"><html:hidden property="document.adhocWorkgroupItem[${status.index}].actionRequested" value="${actionRequestedDefault}"/></c:if>
						<c:if test="${!excludeActionRequested}">
	                    <td class="datacell center">
	                    	<div align=center>
	                    		<html:select property="document.adhocWorkgroupItem[${status.index}].actionRequested" value="${actionRequestedDefault}" disabled="${disableActionRequested}">
  		                    		<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
	    		            		<html:options collection="actionRequestCodes" property="key" labelProperty="value" />
	  			        		</html:select>
	  			        		${actionRequestedMessage}
	  			        	</div>
	                    </td>
	                    </c:if>
	                    <td colspan="3" class="datacell center">
	                    	<div align=left>${workgroup.workgroupName}</div>
	                    	<html:hidden property="document.adhocWorkgroupItem[${status.index}].workgroupName" />
	                    </td>
	                    <td>
	                    	<c:if test="${displayReadOnly}"><html:hidden property="document.adhocWorkgroupItem[${status.index}].permissionCode" /></c:if>
	                    	<kul:htmlControlAttribute property="document.adhocWorkgroupItem[${status.index}].permissionCode" attributeEntry="${adhocWorkgroupAttributes.permissionCode}" readOnly="${displayReadOnly}"/>
	                    	<html:hidden property="document.adhocWorkgroupItem[${status.index}].addedByPerson" />
	                    	<html:hidden property="document.adhocWorkgroupItem[${status.index}].adhocTypeCode" />
							<html:hidden property="document.adhocWorkgroupItem[${status.index}].personAddedTimestamp" />
							<html:hidden property="document.adhocWorkgroupItem[${status.index}].objectId" />
							<html:hidden property="document.adhocWorkgroupItem[${status.index}].versionNumber" />
						</td>
						<c:if test="${not displayReadOnly}">
		                    <td class="datacell center"><div align=center>
                            	<html:image property="methodToCall.deleteWorkgroup.line${status.index}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="delete" styleClass="tinybutton"/></div>
	                    	</td>
	                    </c:if>
	                  </tr>
	                </c:when>
	                <c:otherwise>
	                	<html:hidden property="document.adhocWorkgroupItem[${status.index}].actionRequested" />
	                	<html:hidden property="document.adhocWorkgroupItem[${status.index}].workgroupName" />
	                	<html:hidden property="document.adhocWorkgroupItem[${status.index}].permissionCode" />
	                	<html:hidden property="document.adhocWorkgroupItem[${status.index}].addedByPerson" />
	                    <html:hidden property="document.adhocWorkgroupItem[${status.index}].adhocTypeCode" />
						<html:hidden property="document.adhocWorkgroupItem[${status.index}].personAddedTimestamp" />
						<html:hidden property="document.adhocWorkgroupItem[${status.index}].objectId" />
						<html:hidden property="document.adhocWorkgroupItem[${status.index}].versionNumber" />
	                </c:otherwise>
	               </c:choose>
				</c:forEach>
	        </table>
	    </div>
	</kul:tabTop>