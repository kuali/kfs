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
<%@ include file="/jsp/core/tldHeader.jsp"%>

<c:set var="routingFormPersonnel" value="${DataDictionary.RoutingFormPersonnel.attributes}" />
<c:set var="routingFormOrganizationCreditPercent" value="${DataDictionary.RoutingFormOrganizationCreditPercent.attributes}" />

<kul:tab tabTitle="Personnel and Units/Orgs" defaultOpen="false" >

  <html:hidden property="document.personnelNextSequenceNumber" />

          <div class="tab-container" align="center">
            <div class="h2-container"> <span class="subhead-left">
              <h2>Personnel and Units/Orgs</h2>

              </span> </div>
            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <td colspan=9 class="tab-subhead"><span class="left">Add Proposal Personnel </span> </td>
              </tr>
              <tr>
                <th>&nbsp;</th>
                <th><div align=left>Name</div></th>
                <th><div align=left><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personRoleCode}" skipHelpUrl="true" noColon="true" /></div></th>
                <th><div align="left"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personRoleText}" skipHelpUrl="true" noColon="true" /></div></th>
                <th><div align=center><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.chartOfAccountsCode}" skipHelpUrl="true" noColon="true" />/<kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.organizationCode}" skipHelpUrl="true" noColon="true" /></div></th>
                <th><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personFinancialAidPercent}" skipHelpUrl="true" noColon="true" /></th>
                <th><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personCreditPercent}" skipHelpUrl="true" noColon="true" /></th>
                <th>Profile</th>
                <th>Action</th>
              </tr>
              <tr>
                <th scope="row">add:</th>
                <td class="infoline">
                  <html:hidden property="newRoutingFormPersonnel.personToBeNamedIndicator" />
                  <html:hidden property="newRoutingFormPersonnel.personSystemIdentifier" />
                  <html:hidden write="true" property="newRoutingFormPersonnel.user.personName"/>
                  <c:if test="${empty KualiForm.newRoutingFormPersonnel.personSystemIdentifier && !KualiForm.newRoutingFormPersonnel.personToBeNamedIndicator}">(select)</c:if>
		    	  <c:if test="${KualiForm.newRoutingFormPersonnel.personToBeNamedIndicator}">TO BE NAMED</c:if>
                  <kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" fieldConversions="personUniversalIdentifier:newRoutingFormPersonnel.personSystemIdentifier,personName:newRoutingFormPersonnel.user.personName" extraButtonSource="images/buttonsmall_namelater.gif" extraButtonParams="&newRoutingFormPersonnel.personToBeNamedIndicator=true" tabindexOverride="5100" anchor="${currentTabIndex}" />
                </td>
                <td class="infoline"><kul:htmlControlAttribute property="newRoutingFormPersonnel.personRoleCode" attributeEntry="${routingFormPersonnel.personRoleCode}"  /></td>
                <td class="infoline"><kul:htmlControlAttribute property="newRoutingFormPersonnel.personRoleText" attributeEntry="${routingFormPersonnel.personRoleText}"  /></td>
                <td class="infoline">
                  <html:hidden property="newRoutingFormPersonnel.chartOfAccountsCode"/>
                  <html:hidden property="newRoutingFormPersonnel.organizationCode"/>
                  <c:choose>
                    <c:when test="${KualiForm.newRoutingFormPersonnel.chartOfAccountsCode ne null and KualiForm.newRoutingFormPersonnel.organizationCode ne null}">
                      ${KualiForm.newRoutingFormPersonnel.chartOfAccountsCode} / ${KualiForm.newRoutingFormPersonnel.organizationCode}
                    </c:when>
                    <c:otherwise>(select)</c:otherwise>
                  </c:choose>
                  <kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:newRoutingFormPersonnel.chartOfAccountsCode,organizationCode:newRoutingFormPersonnel.organizationCode" anchor="${currentTabIndex}" />
                </td>
                <td class="infoline"><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormPersonnel.personFinancialAidPercent" attributeEntry="${routingFormPersonnel.personFinancialAidPercent}" />
                </div></td>
                <td class="infoline"><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormPersonnel.personCreditPercent" attributeEntry="${routingFormPersonnel.personCreditPercent}" />
                </div></td>
                <td class="infoline">&nbsp;</td>
                <td class="infoline"><div align=center><html:image property="methodToCall.addPersonLine.anchor${currentTabIndex}" src="images/tinybutton-add1.gif" styleClass="tinybutton" alt="add person line"/></div></td>
              </tr>
              
              <c:forEach items = "${KualiForm.document.routingFormPersonnel}" var="person" varStatus="status"  >
                <tr>
   	              <html:hidden property="document.routingFormPerson[${status.index}].personSystemIdentifier" />
   	              <html:hidden property="document.routingFormPerson[${status.index}].routingFormPersonSequenceNumber" />
   	              <html:hidden property="document.routingFormPerson[${status.index}].chartOfAccountsCode" />
   	              <html:hidden property="document.routingFormPerson[${status.index}].organizationCode" />
   	              <html:hidden property="document.routingFormPerson[${status.index}].personToBeNamedIndicator" />
                  <html:hidden property="document.routingFormPerson[${status.index}].versionNumber" />
                  <th scope="row"><div align="center">${status.index+1}</div></th>
                  <td>
                    <html:hidden write="true" property="document.routingFormPerson[${status.index}].user.personName" />
                    <c:if test="${empty person.personSystemIdentifier && !person.personToBeNamedIndicator}">(select)</c:if>
		    	    <c:if test="${person.personToBeNamedIndicator}">TO BE NAMED</c:if>
                    <kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" fieldConversions="personUniversalIdentifier:document.routingFormPerson[${status.index}].personSystemIdentifier,personName:document.routingFormPerson[${status.index}].user.personName" extraButtonSource="images/buttonsmall_namelater.gif" extraButtonParams="&document.routingFormPerson[${status.index}].personToBeNamedIndicator=true" tabindexOverride="5100" anchor="${currentTabIndex}" />
                  </td>
                  <td><kul:htmlControlAttribute property="document.routingFormPerson[${status.index}].personRoleCode" attributeEntry="${routingFormPersonnel.personRoleCode}"  /></td>
                  <td><kul:htmlControlAttribute property="document.routingFormPerson[${status.index}].personRoleText" attributeEntry="${routingFormPersonnel.personRoleText}"  /></td>
                  <td>
                    <html:hidden property="document.routingFormPerson[${status.index}].chartOfAccountsCode"/>
                    <html:hidden property="document.routingFormPerson[${status.index}].organizationCode"/>
                    <c:choose>
                      <c:when test="${person.chartOfAccountsCode ne null and person.organizationCode ne null}">
                        ${person.chartOfAccountsCode} / ${person.organizationCode}
                      </c:when>
                      <c:otherwise>(select)</c:otherwise>
                    </c:choose>
                    <kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:document.routingFormPerson[${status.index}].chartOfAccountsCode,organizationCode:document.routingFormPerson[${status.index}].organizationCode" anchor="${currentTabIndex}" />
                  </td>
                  <td><div align="center"><span class="infoline">
                    <kul:htmlControlAttribute property="document.routingFormPerson[${status.index}].personFinancialAidPercent" attributeEntry="${routingFormPersonnel.personFinancialAidPercent}" />
                  </span></div></td>
                  <td><div align="center"><span class="infoline">
                    <kul:htmlControlAttribute property="document.routingFormPerson[${status.index}].personCreditPercent" attributeEntry="${routingFormPersonnel.personCreditPercent}" />
                  </span></div></td>
                  <td><div align="center"><html:image property="methodToCall.headerTab.headerDispatch.save.navigateTo.personnel.anchor${currentTabIndex}" src="images/tinybutton-view.gif" styleClass="tinybutton" alt="view person line"/></div></td>
                  <td><div align=center><html:image property="methodToCall.deletePersonLine.line${status.index}.anchor${currentTabIndex}" src="images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete person line"/></div></td>
                </tr>
              </c:forEach>

              <tr>
                <td colspan=9 class="tab-subhead"><span class="left">Add Proposal Units/Orgs </span> </td>
              </tr>
              <tr>
                <th>&nbsp;</th>
                <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${routingFormOrganizationCreditPercent.chartOfAccountsCode}" skipHelpUrl="true" noColon="true" />/<kul:htmlAttributeLabel attributeEntry="${routingFormOrganizationCreditPercent.organizationCode}" skipHelpUrl="true" noColon="true" /> </div></th>
                <th colspan=2><div align="center"><kul:htmlAttributeLabel attributeEntry="${routingFormOrganizationCreditPercent.organizationCreditRoleText}" skipHelpUrl="true" noColon="true" /></div></th>
                <th colspan=2><div align="center"><kul:htmlAttributeLabel attributeEntry="${routingFormOrganizationCreditPercent.organizationFinancialAidPercent}" skipHelpUrl="true" noColon="true" /></th>
                <th colspan=2><div align="center"><kul:htmlAttributeLabel attributeEntry="${routingFormOrganizationCreditPercent.organizationCreditPercent}" skipHelpUrl="true" noColon="true" /></th>
                <th >Action</th>
              </tr>
              <tr>
                <th scope="row">add:</th>
                <td class="infoline">
                  <html:hidden property="newRoutingFormOrganizationCreditPercent.chartOfAccountsCode" />
                  <html:hidden property="newRoutingFormOrganizationCreditPercent.organizationCode" />
                  <c:choose>
                    <c:when test="${KualiForm.newRoutingFormOrganizationCreditPercent.chartOfAccountsCode ne null and KualiForm.newRoutingFormOrganizationCreditPercent.organizationCode ne null}">
                      ${KualiForm.newRoutingFormOrganizationCreditPercent.chartOfAccountsCode} / ${KualiForm.newRoutingFormOrganizationCreditPercent.organizationCode}
                    </c:when>
                    <c:otherwise>(select)</c:otherwise>
                  </c:choose>
                  <kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:newRoutingFormOrganizationCreditPercent.chartOfAccountsCode,organizationCode:newRoutingFormOrganizationCreditPercent.organizationCode" anchor="${currentTabIndex}" />
                </td>
                <td class="infoline" colspan=2><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormOrganizationCreditPercent.organizationCreditRoleText" attributeEntry="${routingFormOrganizationCreditPercent.organizationCreditRoleText}" />
                </div></td>
                <td class="infoline" colspan=2><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormOrganizationCreditPercent.organizationFinancialAidPercent" attributeEntry="${routingFormOrganizationCreditPercent.organizationFinancialAidPercent}" />
                </div></td>
                <td class="infoline" colspan=2><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormOrganizationCreditPercent.organizationCreditPercent" attributeEntry="${routingFormOrganizationCreditPercent.organizationCreditPercent}" />
                </div></td>
                <td class="infoline"><div align=center><html:image property="methodToCall.addOrganizationCreditPercentLine.anchor${currentTabIndex}" src="images/tinybutton-add1.gif" styleClass="tinybutton" alt="add org line"/></div></td>
              </tr>
              <c:forEach items = "${KualiForm.document.routingFormOrganizationCreditPercents}" var="org" varStatus="status"  >
                <tr>
   	              <html:hidden property="document.routingFormOrganizationCreditPercent[${status.index}].chartOfAccountsCode" />
   	              <html:hidden property="document.routingFormOrganizationCreditPercent[${status.index}].organizationCode" />
                  <html:hidden property="document.routingFormOrganizationCreditPercent[${status.index}].versionNumber" />
                  <th scope="row"><div align="center">${status.index+1}</div></th>
                  <td>
                    <c:choose>
                      <c:when test="${org.chartOfAccountsCode ne null and org.organizationCode ne null}">
                        ${org.chartOfAccountsCode} / ${org.organizationCode}
                      </c:when>
                      <c:otherwise>(select)</c:otherwise>
                    </c:choose>
                    <kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:document.routingFormOrganizationCreditPercent[${status.index}].chartOfAccountsCode,organizationCode:document.routingFormOrganizationCreditPercent[${status.index}].organizationCode" anchor="${currentTabIndex}" />
                  </td>
                  <td colspan=2><div align="center"><span class="infoline">
                    <kul:htmlControlAttribute property="document.routingFormOrganizationCreditPercent[${status.index}].organizationCreditRoleText" attributeEntry="${routingFormOrganizationCreditPercent.organizationCreditRoleText}" />
                  </span></div></td>
                  <td colspan=2><div align="center"><span class="infoline">
                    <kul:htmlControlAttribute property="document.routingFormOrganizationCreditPercent[${status.index}].organizationFinancialAidPercent" attributeEntry="${routingFormOrganizationCreditPercent.organizationFinancialAidPercent}" />
                  </span></div></td>
                  <td colspan=2><div align="center"><span class="infoline">
                    <kul:htmlControlAttribute property="document.routingFormOrganizationCreditPercent[${status.index}].organizationCreditPercent" attributeEntry="${routingFormOrganizationCreditPercent.organizationCreditPercent}" />
                  </span></div></td>
                  <td><div align=center><html:image property="methodToCall.deleteOrganizationCreditPercentLine.line${status.index}.anchor${currentTabIndex}" src="images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete person line"/></div></td>
                </tr>
              </c:forEach>
            </table>
          </div>
</kul:tab>
