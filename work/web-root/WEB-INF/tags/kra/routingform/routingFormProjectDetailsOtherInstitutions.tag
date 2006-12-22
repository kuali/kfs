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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/tlds/fn.tld" prefix="fn" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${empty editingMode['fullEntry']}" />
<c:set var="routingFormOrganizationAttributes" value="${DataDictionary.RoutingFormOrganization.attributes}" />


<kul:tab tabTitle="Other Organizations" defaultOpen="false" tabErrorKey="document.routingFormOrganization*" >
  <div class="tab-container" align="center">
            <div class="h2-container">
              <h2>Other Organizations</h2>
            </div>

            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <td colspan=4 class="tab-subhead"><span class="left">Default Institutions</span> </td>
              </tr>
              <tr>
                <th width="50">&nbsp;</th>
                <th> <div align="center">Chart</div></th>

                <th> <div align="center">Org</div></th>
                <th >Action</th>
              </tr>

              <tr>
                <td colspan=4 class="tab-subhead"><span class="left">Ad Hoc Institutions </span> </td>
              </tr>

              <tr>
                <th scope="row">add:</th>
                <td class="infoline"><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormOrganization.chartOfAccountsCode" attributeEntry="${routingFormOrganizationAttributes.chartOfAccountsCode}" />
                </div></td>
                <td class="infoline"><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormOrganization.organizationCode" attributeEntry="${routingFormOrganizationAttributes.organizationCode}" />
                  <kul:lookup boClassName="org.kuali.module.chart.bo.Org" lookupParameters="newRoutingFormOrganization.organizationCode:organizationCode,newRoutingFormOrganization.chartOfAccountsCode:chartOfAccountsCode" fieldConversions="organizationCode:newRoutingFormOrganization.organizationCode,chartOfAccountsCode:newRoutingFormOrganization.chartOfAccountsCode" tabindexOverride="5100" anchor="${currentTabIndex}" />
                </div></td>
                <td class="infoline"><div align=center><html:image property="methodToCall.insertRoutingFormOrganization.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add other organization" /></div></td>
              </tr>
              
              <c:forEach items="${KualiForm.document.routingFormOrganizations}" var="routingFormOrganization" varStatus="status">
                <tr>
                  <th class="neutral">
                    <div align="center">${status.index+1}</div>
                  </th> 
                  <td class="neutral">
                    <kul:htmlControlAttribute property="document.routingFormOrganization[${status.index}].documentNumber" attributeEntry="${institutionCostShareAttributes.documentNumber}" />
                    <kul:htmlControlAttribute property="document.routingFormOrganization[${status.index}].objectId" attributeEntry="${institutionCostShareAttributes.objectId}" />
                    <kul:htmlControlAttribute property="document.routingFormOrganization[${status.index}].versionNumber" attributeEntry="${institutionCostShareAttributes.versionNumber}" />
                    <div align="center"><span class="infoline">
                      <kul:htmlControlAttribute property="document.routingFormOrganization[${status.index}].chartOfAccountsCode" attributeEntry="${routingFormOrganizationAttributes.chartOfAccountsCode}" />
                    </span></div>
                  </td>
                  <td>
                    <div align="center">
                      <span class="infoline">
                        <kul:htmlControlAttribute property="document.routingFormOrganization[${status.index}].organizationCode" attributeEntry="${routingFormOrganizationAttributes.organizationCode}" /></span>
                        <kul:lookup boClassName="org.kuali.module.chart.bo.Org" lookupParameters="document.routingFormOrganization[${status.index}].organizationCode:organizationCode,document.routingFormOrganization[${status.index}].chartOfAccountsCode:chartOfAccountsCode" fieldConversions="organizationCode:document.routingFormOrganization[${status.index}].organizationCode,chartOfAccountsCode:document.routingFormOrganization[${status.index}].chartOfAccountsCode" tabindexOverride="5100" anchor="${currentTabIndex}" />
                    </div>
                  </td>
                  <td class="neutral">
                    <div align="center"><html:image property="methodToCall.deleteRoutingFormOrganization.line${status.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete institution cost share" /></div>
                  </td>
                </tr>
              </c:forEach>
            </table>
          </div>
        </kul:tab>