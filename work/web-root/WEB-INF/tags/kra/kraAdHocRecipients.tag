<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>

<%@ attribute name="editingMode" required="false" type="java.util.Map"%>

<%-- derive displayReadOnly value --%>
<c:set var="displayReadOnly" value="false" />
<c:set var="budgetAdHocPermissionAttributes" value="${DataDictionary.BudgetAdHocPermission.attributes}" />

<c:if test="${!empty editingMode['viewOnly']}" >
    <c:set var="displayReadOnly" value="true" />
</c:if>

<%-- <c:if test="${KualiForm.documentActionFlags.canAdHocRoute and not KualiForm.suppressAllButtons}"> --%>
	<kul:tabTop tabTitle="Ad Hoc Recipients" defaultOpen="false" tabErrorKey="${Constants.AD_HOC_ROUTE_ERRORS}">
    	<div class="tab-container" align=center>     
			<div class="h2-container">
				<h2>Ad Hoc Recipients</h2>
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
                	<td colspan=6 class="tab-subhead">Person Requests:</td>
              	</tr>
	          	<tr>
                  	<kul:htmlAttributeHeaderCell
                      attributeEntry="${DataDictionary.AdHocRoutePerson.attributes.actionRequested}"
                      scope="col"
                      />
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
                  		attributeEntry="${DataDictionary.BudgetAdHocPermission.attributes.budgetPermissionCode}"
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
                    		<div align=center>
                        		<html:hidden property="newAdHocRoutePerson.type"/>
                        		<html:select property="newAdHocRoutePerson.actionRequested" value="${Constants.WORKFLOW_FYI_REQUEST}" disabled="true">
  		                    		<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
	    		            		<html:options collection="actionRequestCodes" property="key" labelProperty="value" />
	  			        		</html:select> (upon completion)
	  			        	</div>
                    	</td>
                    	<td class="infoline" ><div align=center>
                        	<kul:htmlControlAttribute property="newAdHocRoutePerson.id" attributeEntry="${DataDictionary.AdHocRoutePerson.attributes.id}" readOnly="${displayReadOnly}" />
                        	<kul:lookup boClassName="org.kuali.core.bo.user.KualiUser"  fieldConversions="personUserIdentifier:newAdHocRoutePerson.id" /></div>
                    	</td>
                    	<td class="infoline"><div align=center>--</div></td>
                    	<td class="infoline"><div align=center>--</div></td>
                    	<td class="infoline">
							<div align="left">
								<kul:htmlControlAttribute property="newAdHocPermission.budgetPermissionCode" attributeEntry="${budgetAdHocPermissionAttributes.budgetPermissionCode}" readOnly="${viewOnly}"/>
							</div>
						</td>
						<c:if test="${not displayReadOnly}">
                    		<td class="infoline" ><div align=center>
                        		<html:image property="methodToCall.insertAdHocRoutePerson" src="images/tinybutton-add1.gif" alt="Insert Additional Ad Hoc Person" styleClass="tinybutton"/></div>
                    		</td>
                    	</c:if>
                	</tr>
				</c:if>
				<c:forEach items="${KualiForm.document.budget.adHocPermissions}" var="person" varStatus="status">
					<tr>
	                    <td class="datacell center">
	                    	<div align=center>
	                    		<html:select property="document.budget.budgetAdHocPermissionItem[${status.index}].actionRequested" value="${Constants.WORKFLOW_FYI_REQUEST}" disabled="true">
  		                    		<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
	    		            		<html:options collection="actionRequestCodes" property="key" labelProperty="value" />
	  			        		</html:select>
	  			        		(upon completion)
	  			        	</div>
	                    </td>
	                    <td class="datacell center">
	                    	<div align=left>${person.user.personUserIdentifier}</div>
	                    	<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].user.personUserIdentifier" />
	                    </td>
	                    <td>${person.user.campusCode}<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].user.campusCode" /></td>
	                    <td>${person.orgCode}<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].user.deptid" /></td>
	                    <td>
	                    	<c:if test="${displayReadOnly}"><html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].budgetPermissionCode" /></c:if>
	                    	<kul:htmlControlAttribute property="document.budget.budgetAdHocPermissionItem[${status.index}].budgetPermissionCode" attributeEntry="${budgetAdHocPermissionAttributes.budgetPermissionCode}" readOnly="${viewOnly}"/>
							<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].personUniversalIdentifier" />
							<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].addedByPerson" />
							<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].personAddedTimestamp" />
							<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].objectId" />
							<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].versionNumber" />
						</td>
						<c:if test="${not displayReadOnly}">
		                    <td class="datacell center"><div align=center>
                            	<html:image property="methodToCall.delete.line${status.index}" src="images/tinybutton-delete1.gif" alt="delete" styleClass="tinybutton"/></div>
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
					<td colspan=6 class="tab-subhead">Ad Hoc Org Requests:</td>
				</tr>
				<tr>
					<kul:htmlAttributeHeaderCell
                      	attributeEntry="${DataDictionary.AdHocRoutePerson.attributes.actionRequested}"
                      	scope="col"
                    />
					<kul:htmlAttributeHeaderCell
                    	literalLabel="* Chart/Org"
                    	scope="col"
                    	forceRequired="true"
                    	colspan="3"
                    	/>
					<kul:htmlAttributeHeaderCell
                  		attributeEntry="${DataDictionary.BudgetAdHocPermission.attributes.budgetPermissionCode}"
                  		scope="col"
                  	  />
					<c:if test="${not displayReadOnly}"><th scope=col>Actions</th></c:if>
				</tr>
				
				<c:if test="${not displayReadOnly}">
				<tr>
					<td class="infoline">
	                    <div align=center>
	                    	<html:select property="newAdHocRouteOrg.actionRequested" value="${Constants.WORKFLOW_FYI_REQUEST}" disabled="true">
  		                    	<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
	    		            	<html:options collection="actionRequestCodes" property="key" labelProperty="value" />
	  			        	</html:select>
	  			        	(upon completion)
	  			      	</div>
	                </td>
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
							<kul:htmlControlAttribute property="newAdHocOrg.budgetPermissionCode" attributeEntry="${budgetAdHocPermissionAttributes.budgetPermissionCode}" readOnly="${viewOnly}"/>
						</div>
					</td>
					<td class="infoline">
						<div align=center>
							<html:image property="methodToCall.addOrg" src="images/tinybutton-add1.gif" styleClass="tinybutton" />
						</div>
					</td>
				</tr>
				</c:if>
				
				<c:forEach items="${KualiForm.document.budget.adHocOrgs}" var="org" varStatus="status">
					<tr>
						<td class="datacell center">
	                    	<div align=center>
	                    		<html:select property="newAdHocRouteOrg.actionRequested" value="${Constants.WORKFLOW_FYI_REQUEST}" disabled="true">
  		                    		<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
	    		            		<html:options collection="actionRequestCodes" property="key" labelProperty="value" />
	  			        		</html:select>
	  			        		(upon completion)
	  			        	</div>
	  			        	<html:hidden property="document.budget.budgetAdHocOrgItem[${status.index}].fiscalCampusCode" />
							<html:hidden property="document.budget.budgetAdHocOrgItem[${status.index}].primaryDepartmentCode" />
							<html:hidden property="document.budget.budgetAdHocOrgItem[${status.index}].addedByPerson" />
							<html:hidden property="document.budget.budgetAdHocOrgItem[${status.index}].personAddedTimestamp" />
							<html:hidden property="document.budget.budgetAdHocOrgItem[${status.index}].objectId" />
							<html:hidden property="document.budget.budgetAdHocOrgItem[${status.index}].versionNumber" />
	                    </td>
						<td colspan="3">${org.fiscalCampusCode}/${org.primaryDepartmentCode}</td>
						<td>
							<c:if test="${displayReadOnly}"><html:hidden property="document.budget.budgetAdHocOrgItem[${status.index}].budgetPermissionCode" /></c:if>
							<kul:htmlControlAttribute property="document.budget.budgetAdHocOrgItem[${status.index}].budgetPermissionCode" attributeEntry="${budgetAdHocPermissionAttributes.budgetPermissionCode}" readOnly="${viewOnly}"/>
						</td>
						<c:if test="${not displayReadOnly}">
						<td>
							<div align="center">
								<html:image property="methodToCall.deleteOrg.line${status.index}" src="images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete" disabled="${displayReadOnly}"/>
							</div>
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
                	<td colspan=6 class="tab-subhead">Ad Hoc Workgroup Requests:</td>
              	</tr>
	          	<tr>
                  	<kul:htmlAttributeHeaderCell
                      attributeEntry="${DataDictionary.AdHocRouteWorkgroup.attributes.actionRequested}"
                      scope="col"
                      />
                  	<kul:htmlAttributeHeaderCell
                      attributeEntry="${DataDictionary.AdHocRouteWorkgroup.attributes.id}"
                      scope="col"
                      colspan="3"
                      />
                  	<kul:htmlAttributeHeaderCell
                  		attributeEntry="${DataDictionary.BudgetAdHocPermission.attributes.budgetPermissionCode}"
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
                    		<div align=center>
                        		<html:hidden property="newAdHocRouteWorkgroup.type"/>
                        		<html:select property="newAdHocRouteWorkgroup.actionRequested" value="${Constants.WORKFLOW_FYI_REQUEST}" disabled="true">
  		                    		<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
    		                		<html:options collection="actionRequestCodes" property="key" labelProperty="value"/>
  			            		</html:select> (upon completion)
  			            	</div>
                    	</td>
                    	<td class="infoline" colspan="3"><div align=center>
                        	<kul:htmlControlAttribute property="newAdHocRouteWorkgroup.id" attributeEntry="${DataDictionary.AdHocRouteWorkgroup.attributes.id}" readOnly="${displayReadOnly}" />
                        	<kul:workflowWorkgroupLookup fieldConversions="workgroupId:newAdHocRouteWorkgroup.id" /></div>
                    	</td>
                    	<td class="infoline">
                    		<div align="left">
                    			<kul:htmlControlAttribute property="newAdHocWorkgroupPermissionCode" attributeEntry="${budgetAdHocPermissionAttributes.budgetPermissionCode}" readOnly="${viewOnly}"/>
							</div>
						</td>
						<c:if test="${not displayReadOnly}">
                    		<td class="infoline"><div align=center>
                        		<html:image property="methodToCall.insertAdHocRouteWorkgroup" src="images/tinybutton-add1.gif" alt="Insert Additional Ad Hoc Workgroup" styleClass="tinybutton"/></div>
                    		</td>
                    	</c:if>
                	</tr>
				</c:if>
                <c:forEach items="${KualiForm.document.budget.adHocWorkgroups}" var="workgroup" varStatus="status">
					<tr>
	                    <td class="datacell center">
	                    	<div align=center>
	                    		<html:select property="document.budget.budgetAdHocWorkgroupItem[${status.index}].actionRequested" value="${Constants.WORKFLOW_FYI_REQUEST}" disabled="true">
  		                    		<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
	    		            		<html:options collection="actionRequestCodes" property="key" labelProperty="value" />
	  			        		</html:select>
	  			        		(upon completion)
	  			        	</div>
	                    </td>
	                    <td colspan="3" class="datacell center">
	                    	<div align=left>${workgroup.workgroupName}</div>
	                    	<html:hidden property="document.budget.budgetAdHocWorkgroupItem[${status.index}].workgroupName" />
	                    </td>
	                    <td>
	                    	<c:if test="${displayReadOnly}"><html:hidden property="document.budget.budgetAdHocWorkgroupItem[${status.index}].budgetPermissionCode" /></c:if>
	                    	<kul:htmlControlAttribute property="document.budget.budgetAdHocWorkgroupItem[${status.index}].budgetPermissionCode" attributeEntry="${budgetAdHocPermissionAttributes.budgetPermissionCode}" readOnly="${viewOnly}"/>
	                    	<html:hidden property="document.budget.budgetAdHocWorkgroupItem[${status.index}].addedByPerson" />
							<html:hidden property="document.budget.budgetAdHocWorkgroupItem[${status.index}].personAddedTimestamp" />
							<html:hidden property="document.budget.budgetAdHocWorkgroupItem[${status.index}].objectId" />
							<html:hidden property="document.budget.budgetAdHocWorkgroupItem[${status.index}].versionNumber" />
						</td>
						<c:if test="${not displayReadOnly}">
		                    <td class="datacell center"><div align=center>
                            	<html:image property="methodToCall.delete.line${status.index}" src="images/tinybutton-delete1.gif" alt="delete" styleClass="tinybutton"/></div>
	                    	</td>
	                    </c:if>
	                </tr>
				</c:forEach>
	        </table>
	    </div>
	</kul:tabTop>
<%-- </c:if> --%>