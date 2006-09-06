<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>

<%@ attribute name="editingMode" required="false" type="java.util.Map"%>

<%-- derive displayReadOnly value --%>
<c:set var="displayReadOnly" value="false" />

<c:if test="${!empty editingMode['viewOnly']}" >
    <c:set var="displayReadOnly" value="true" />
</c:if>

    <c:if test="${KualiForm.documentActionFlags.canAdHocRoute and not KualiForm.suppressAllButtons}">
        <kul:tab tabTitle="Ad Hoc Recipients" defaultOpen="false" tabErrorKey="${Constants.AD_HOC_ROUTE_ERRORS}">
        
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
                  	  literalLabel="Type"
                  	  forceRequired="true"
                  	  />
                <kul:htmlAttributeHeaderCell
                    literalLabel="Actions"
                    scope="col"
                    />
              </tr>
                
			<c:if test="${!displayReadOnly}">
                <tr>
                    <td class="infoline" ><div align=center>
                        <html:hidden property="newAdHocRoutePerson.type"/>
                        <html:select property="newAdHocRoutePerson.actionRequested" value="F" disabled="true">
  		                    <c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
	    		            <html:options collection="actionRequestCodes" property="key" labelProperty="value" />
	  			        </html:select></div>
                    </td>
                    <td class="infoline" ><div align=center>
                        <kul:htmlControlAttribute property="newAdHocRoutePerson.id" attributeEntry="${DataDictionary.AdHocRoutePerson.attributes.id}" readOnly="${displayReadOnly}" />
                        <kul:lookup boClassName="org.kuali.core.bo.user.KualiUser"  fieldConversions="personUserIdentifier:newAdHocRoutePerson.id" /></div>
                    </td>
                    <td class="infoline"><div align=center>--</div></td>
                    <td class="infoline"><div align=center>--</div></td>
                    <td class="infoline">
						<div align="left">
							<html:select value="${KualiForm.newAdHocOrg.budgetPermissionCode}" property="newAdHocOrg.budgetPermissionCode">
								<html:option value="R">READ</html:option>
								<html:option value="M">MOD</html:option>
							</html:select>
						</div>
					</td>
                    <td class="infoline" ><div align=center>
                        <html:image property="methodToCall.insertAdHocRoutePerson" src="images/tinybutton-add1.gif" alt="Insert Additional Ad Hoc Person" styleClass="tinybutton"/></div>
                    </td>
                </tr>
			</c:if>
				<c:forEach items="${KualiForm.document.budget.adHocPermissions}" var="person" varStatus="status">
					<tr>
	                    <td class="datacell center">
	                    	<div align=center>
	                    		<html:select property="newAdHocRoutePerson.actionRequested" value="F" disabled="true">
  		                    		<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
	    		            		<html:options collection="actionRequestCodes" property="key" labelProperty="value" />
	  			        		</html:select>
	  			        	</div>
	                    </td>
	                    <td class="datacell center">
	                    	<div align=left>${person.user.personUserIdentifier}</div>
	                    	<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].user.personUserIdentifier" />
	                    </td>
	                    <td>${person.user.campusCode}<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].user.campusCode" /></td>
	                    <td>${person.orgCode}<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].user.deptid" /></td>
	                    <td>
	                    	<html:select value="${person.budgetPermissionCode}" property="document.budget.budgetAdHocPermissionItem[${status.index}].budgetPermissionCode" disabled="${viewOnly}">
								<html:option value="R">READ</html:option>
								<html:option value="M">MOD</html:option>
							</html:select>
							<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].personUniversalIdentifier" />
							<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].addedByPerson" />
							<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].personAddedTimestamp" />
							<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].objectId" />
							<html:hidden property="document.budget.budgetAdHocPermissionItem[${status.index}].versionNumber" />
						</td>
	                    <td class="datacell center"><div align=center>
                            <html:image property="methodToCall.delete.line${status.index}" src="images/tinybutton-delete1.gif" alt="delete" styleClass="tinybutton"/></div>
	                    </td>
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
                  	  literalLabel="Type"
                  	  forceRequired="true"
                  	  />
                  <kul:htmlAttributeHeaderCell
                      literalLabel="Actions"
                      scope="col"
                      />
              </tr>
			<c:if test="${!displayReadOnly}">
                <tr>
                    <td class="infoline"><div align=center>
                        <html:hidden property="newAdHocRouteWorkgroup.type"/>
                        <html:select property="newAdHocRouteWorkgroup.actionRequested">
  		                    <c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
    		                <html:options collection="actionRequestCodes" property="key" labelProperty="value"/>
  			            </html:select></div>
                    </td>
                    <td class="infoline" colspan="3"><div align=center>
                        <kul:htmlControlAttribute property="newAdHocRouteWorkgroup.id" attributeEntry="${DataDictionary.AdHocRouteWorkgroup.attributes.id}" readOnly="${displayReadOnly}" />
                        <kul:workflowWorkgroupLookup fieldConversions="workgroupId:newAdHocRouteWorkgroup.id" /></div>
                    </td>
                    <td class="infoline">
                    	<div align="left">
							<html:select value="${KualiForm.newAdHocOrg.budgetPermissionCode}" property="newAdHocOrg.budgetPermissionCode">
								<html:option value="R">READ</html:option>
								<html:option value="M">MOD</html:option>
							</html:select>
						</div>
					</td>
                    <td class="infoline"><div align=center>
                        <html:image property="methodToCall.insertAdHocRouteWorkgroup" src="images/tinybutton-add1.gif" alt="Insert Additional Ad Hoc Workgroup" styleClass="tinybutton"/></div>
                    </td>
                </tr>
			</c:if>
                <logic:iterate name="KualiForm" id="workgroup" property="adHocRouteWorkgroups" indexId="ctr">
                    <tr>
                        <td class="datacell center">
                        	<div align=center>
                            	<html:hidden property="adHocRouteWorkgroup[${ctr}].type"/>
                            	<html:select property="adHocRouteWorkgroup[${ctr}].actionRequested">
  		                        	<c:set var="actionRequestCodes" value="${KualiForm.adHocActionRequestCodes}"/>
    		                    	<html:options collection="actionRequestCodes" property="key" labelProperty="value"/>
  			                	</html:select>
  			                </div>
                        </td>
                        <td class="datacell center"><div align=center>
                            <kul:htmlControlAttribute property="adHocRouteWorkgroup[${ctr}].id" attributeEntry="${DataDictionary.AdHocRouteWorkgroup.attributes.id}" readOnly="${displayReadOnly}" />
                            <kul:workflowWorkgroupLookup fieldConversions="workgroupId:adHocRouteWorkgroup[${ctr}].id" /></div>
                        </td>
                        <td class="infoline">--</td>
                        <td class="datacell center" ><div align=center>
                            <html:image property="methodToCall.deleteAdHocRouteWorkgroup.line${ctr}" src="images/tinybutton-delete1.gif" alt="delete" styleClass="tinybutton"/></div>
                        </td>
                    </tr>
                </logic:iterate>
	        </table>
	        </div>
	    </kul:tab>
    </c:if>