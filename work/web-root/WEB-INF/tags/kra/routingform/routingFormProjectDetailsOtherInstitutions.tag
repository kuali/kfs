<%--
 Copyright 2006 The Kuali Foundation.
 
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

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${empty editingMode['fullEntry']}" />
<c:set var="routingFormOrganizationAttributes" value="${DataDictionary.RoutingFormOrganization.attributes}" />


<kul:tab tabTitle="Other Organizations" defaultOpen="false" tabErrorKey="document.routingFormOrganization*" >
  <div class="tab-container" align="center">
            <div class="h2-container">
              <a name="otherOrganizations"></a><h2>Other Organizations</h2>
            </div>

            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <td colspan=4 class="tab-subhead"><span class="left">Default Institutions</span> </td>
              </tr>
              <tr>
                <th width="50">&nbsp;</th>
                <kul:htmlAttributeHeaderCell
                 literalLabel="* Chart/Org"
                 scope="col"
                 forceRequired="true"
                 colspan="2"
        		/>
                <th >Action</th>
              </tr>

              <tr>
                <td colspan=4 class="tab-subhead"><span class="left">Ad Hoc Institutions </span> </td>
              </tr>

              <tr>
                <th scope="row">add:</th>
                <td nowrap class="infoline" colspan="2">
                  <div align="center">
					<c:choose>
						<c:when test="${empty KualiForm.newRoutingFormOrganization.chartOfAccountsCode}">(select by org)</c:when>
						<c:otherwise>
							${KualiForm.newRoutingFormOrganization.chartOfAccountsCode}/${KualiForm.newRoutingFormOrganization.organizationCode}
							<html:hidden property="newRoutingFormOrganization.chartOfAccountsCode"/>
							<html:hidden property="newRoutingFormOrganization.organizationCode"/>
						</c:otherwise>
					</c:choose>
					&nbsp;&nbsp;
					<kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:newRoutingFormOrganization.chartOfAccountsCode,organizationCode:newRoutingFormOrganization.organizationCode"  anchor="otherOrganizations" />
					</div>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
                
                
                <td class="infoline"><div align=center><html:image property="methodToCall.insertRoutingFormOrganization.anchor${currentTabIndex}" styleClass="tinybutton" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add other organization" /></div></td>
              </tr>
              
              <c:forEach items="${KualiForm.document.routingFormOrganizations}" var="routingFormOrganization" varStatus="status">
                <tr>
                  <th class="neutral">
                    <div align="center">${status.index+1}</div>
                  </th> 
                <td nowrap class="neutral" colspan="2">
                    <html:hidden property="document.routingFormOrganization[${status.index}].documentNumber" />
                    <kul:htmlControlAttribute property="document.routingFormOrganization[${status.index}].objectId" attributeEntry="${routingFormOrganizationAttributes.objectId}" />
                    <kul:htmlControlAttribute property="document.routingFormOrganization[${status.index}].versionNumber" attributeEntry="${routingFormOrganizationAttributes.versionNumber}" />
                  <div align="center">
					<c:choose>
						<c:when test="${empty routingFormOrganization.chartOfAccountsCode}">(select by org)</c:when>
						<c:otherwise>
							${routingFormOrganization.chartOfAccountsCode}/${routingFormOrganization.organizationCode}
							<html:hidden property="document.routingFormOrganization[${status.index}].chartOfAccountsCode"/>
							<html:hidden property="document.routingFormOrganization[${status.index}].organizationCode"/>
						</c:otherwise>
					</c:choose>
					&nbsp;&nbsp;
					<kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:document.routingFormOrganization[${status.index}].chartOfAccountsCode,organizationCode:document.routingFormOrganization[${status.index}].organizationCode" anchor="otherOrganizations" />
				</div>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
                  
                  
                  <td class="neutral">
                    <div align="center"><html:image property="methodToCall.deleteRoutingFormOrganization.line${status.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="delete institution cost share" /></div>
                  </td>
                </tr>
              </c:forEach>
            </table>
          </div>
        </kul:tab>