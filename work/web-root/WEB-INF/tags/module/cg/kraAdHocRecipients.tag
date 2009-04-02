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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="adhocType" required="true" %>
<%@ attribute name="adhocLabel" required="true" %>
<%@ attribute name="excludeActionRequested" required="false" %>
<%@ attribute name="disableActionRequested" required="false" %>
<%@ attribute name="actionRequestedDefault" required="false" %>
<%@ attribute name="actionRequestedMessage" required="false" %>
<%@ attribute name="editingMode" required="false" type="java.util.Map"%>

<%-- Define variable that will hold the Title of the html control --%>
<c:set var="accessibleTitle" value="${DataDictionary.AdHocRoutePerson.attributes.actionRequested.label}"/>
                        <c:set var="accessibleTitle2" value="${adhocOrgAttributes.actionRequested.label}"/>
                        <c:set var="accessibleTitle3" value="${DataDictionary.AdHocRouteWorkgroup.attributes.actionRequested.label}"/>
<c:if test="${DataDictionary.AdHocRoutePerson.attributes.actionRequested.required}">
<c:set var="accessibleTitle" value="${Constants.REQUIRED_FIELD_SYMBOL} ${accessibleTitle}"/>
  </c:if>
                          <c:if test="${adhocOrgAttributes.actionRequested.required}">
<c:set var="accessibleTitle2" value="${Constants.REQUIRED_FIELD_SYMBOL} ${accessibleTitle2}"/>
  </c:if>
  <c:if test="${DataDictionary.AdHocRouteWorkgroup.attributes.actionRequested.required}">
<c:set var="accessibleTitle3" value="${Constants.REQUIRED_FIELD_SYMBOL} ${accessibleTitle3}"/>
  </c:if>
  
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
				<h3>Ad Hoc ${adhocLabel}</h3>
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
                        		<html:select title="${accessibleTitle}" property="newAdHocRoutePerson.actionRequested" value="${actionRequestedDefault}" disabled="${disableActionRequested}">
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
	                    			  fieldConversions="principalName:newAdHocRoutePerson.id,name:newAdHocRoutePerson.name" 
	                    			  lookupParameters="newAdHocRoutePerson.id:principalName" />
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
                        		<html:image property="methodToCall.insertAdHocRoutePerson" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" title="Insert Additional Ad Hoc Person" alt="Insert Additional Ad Hoc Person" styleClass="tinybutton"/></div>
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
	                    		<html:select title="${accessibleTitle}" property="document.adhocPersonItem[${status.index}].actionRequested" value="${actionRequestedDefault}" disabled="${disableActionRequested}">
  		                    		<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
	    		            		<html:options collection="actionRequestCodes" property="key" labelProperty="value" />
	  			        		</html:select>
	  			        		${actionRequestedMessage}
	  			        	</div>
	                    </td>
	                    </c:if>
	                    <td class="datacell center" colspan="2">
	                    	<div align=center>
		                    	<kul:user userIdFieldName="person[${status.index}].principalName" 
		                    			  userId="${KualiForm.document.adhocPersons[status.index].principalName}" 
		                    			  universalIdFieldName=""
		                    			  universalId=""
		                    			  userNameFieldName="person[${status.index}].name"
		                    			  userName="${KualiForm.document.adhocPersons[status.index].name}"
		                    			  readOnly="${displayReadOnly}" 
		                    			  renderOtherFields="true"
		                    			  fieldConversions="principalName:person.user.principalName,name:person.user.name" 
		                    			  lookupParameters="person.user.principalName:principalName" />
	                    	</div>
	                    </td>
	                    <td>
	                    	<div align=center>
	                    		${person.user.campusCode} / ${person.primaryDepartmentCode}
	                    	</div>
	                    </td>
	                    <td>
	                      <div align="center">
	                    	<kul:htmlControlAttribute property="document.adhocPersonItem[${status.index}].permissionCode" attributeEntry="${adhocPersonAttributes.permissionCode}" readOnly="${displayReadOnly}"/>
						  </div>
						</td>
						<c:if test="${not displayReadOnly}">
		                    <td class="datacell center"><div align=center>
                            	<html:image property="methodToCall.delete.line${status.index}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="delete" title="delete" styleClass="tinybutton"/></div>
	                    	</td>
	                    </c:if>
	                  </tr>
	                </c:when>
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
	                    	<html:select title="${accessibleTitle2}" property="newAdHocRouteOrg.actionRequested" value="${actionRequestedDefault}" disabled="${disableActionRequested}">
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
								<kul:htmlControlAttribute property="newAdHocOrg.fiscalCampusCode" attributeEntry="${adhocOrgAttributes.fiscalCampusCode}" readOnly="true"/>
								/
								<kul:htmlControlAttribute property="newAdHocOrg.primaryDepartmentCode" attributeEntry="${adhocOrgAttributes.primaryDepartmentCode}" readOnly="true"/>
							</c:otherwise>
						</c:choose>
						&nbsp;&nbsp;
						<kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Organization" fieldConversions="chartOfAccounts.chartOfAccountsCode:newAdHocOrg.fiscalCampusCode,organizationCode:newAdHocOrg.primaryDepartmentCode" />
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
							<html:image alt="Insert Additional Ad Hoc Organization" title="Insert Additional Ad Hoc Organization" property="methodToCall.addOrg" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" />
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
	                    		<html:select title="${accessibleTitle2}" property="document.adhocOrgItem[${status.index}].actionRequested" value="${actionRequestedDefault}" disabled="${disableActionRequested}">
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
							<kul:htmlControlAttribute property="document.adhocOrgItem[${status.index}].permissionCode" attributeEntry="${adhocOrgAttributes.permissionCode}" readOnly="${displayReadOnly}"/>
						  </div>
						</td>
						<c:if test="${not displayReadOnly}">
						<td>
							<div align="center">
								<html:image property="methodToCall.deleteOrg.line${status.index}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="delete" title="delete" disabled="${displayReadOnly}"/>
							</div>
						</td>
						</c:if>
					  </tr>
					</c:when>
				   </c:choose>
				</c:forEach>
	        </table>
	    </div>
	</kul:tabTop>

