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
<%@ include file="/jsp/core/tldHeader.jsp"%>

<c:set var="routingFormPersonnel" value="${DataDictionary.RoutingFormPersonnel.attributes}" />
<c:set var="routingFormOrganizationCreditPercent" value="${DataDictionary.RoutingFormOrganizationCreditPercent.attributes}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>
<c:set var="budgetLinked" value="${KualiForm.editingMode['budgetLinked']}"/>

<kul:tab tabTitle="Personnel and Units/Orgs" defaultOpen="true" tabErrorKey="newRoutingFormPerson*,document.routingFormPersonnel*,newRoutingFormOrganizationCreditPercent*,document.routingFormOrganizationCreditPercent*" auditCluster="mainPageAuditErrors" tabAuditKey="document.routingFormPersonnel*,document.routingFormOrganizationCreditPercent*">

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
                <th><div align=left>* Name</div></th>
                <th><div align=left><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personRoleCode}" skipHelpUrl="true" noColon="true" /></div></th>
                <th><div align="left"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personRoleText}" skipHelpUrl="true" noColon="true" /></div></th>
                <th><div align=center><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.chartOfAccountsCode}" skipHelpUrl="true" noColon="true" useShortLabel="true"/>/<kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.organizationCode}" skipHelpUrl="true" noColon="true" useShortLabel="true"/></div></th>
                <th><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personFinancialAidPercent}" skipHelpUrl="true" noColon="true" /></th>
                <th><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personCreditPercent}" skipHelpUrl="true" noColon="true" /></th>
                <th>Profile</th>
                <th>Action</th>
              </tr>
              <c:if test="${!viewOnly}">
              <tr>
                <th scope="row">add:
                  <!-- Following fields are to keep track of personnel page data -->
                  <html:hidden property="newRoutingFormPerson.personLine1Address" />
                  <html:hidden property="newRoutingFormPerson.personPhoneNumber" />
                  <html:hidden property="newRoutingFormPerson.personEmailAddress" />
                </th>
                <td class="infoline">
                  <html:hidden property="newRoutingFormPerson.personToBeNamedIndicator" />
                  <html:hidden property="newRoutingFormPerson.personUniversalIdentifier" />
                  <html:hidden write="true" property="newRoutingFormPerson.user.personName"/>
                  <c:if test="${empty KualiForm.newRoutingFormPerson.personUniversalIdentifier && !KualiForm.newRoutingFormPerson.personToBeNamedIndicator}">(select)</c:if>
		    	  <c:if test="${KualiForm.newRoutingFormPerson.personToBeNamedIndicator}">TO BE NAMED</c:if>
                  <c:if test="${!viewOnly}">
                    <kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" fieldConversions="personUniversalIdentifier:newRoutingFormPerson.personUniversalIdentifier,personName:newRoutingFormPerson.user.personName" extraButtonSource="images/buttonsmall_namelater.gif" extraButtonParams="&newRoutingFormPerson.personToBeNamedIndicator=true" anchor="${currentTabIndex}" />
                  </c:if>
                </td>
                <td class="infoline">
                  <c:forEach items="${KualiForm.document.routingFormPersonRoles}" var="routingFormPersonRole" varStatus="status"> 
                    <html:hidden property="document.routingFormPersonRoles[${status.index}].documentNumber" /> 
                    <html:hidden property="document.routingFormPersonRoles[${status.index}].personRoleCode" /> 
                    <html:hidden property="document.routingFormPersonRoles[${status.index}].versionNumber" /> 
                    <html:hidden property="document.routingFormPersonRoles[${status.index}].personRole.personRoleDescription" /> 
                  </c:forEach>
                  <kul:checkErrors keyMatch="newRoutingFormPerson.personRoleCode" auditMatch="newRoutingFormPerson.personRoleCode"/>
				  <c:if test="${hasErrors==true}">
				    <c:set var="newPersonRoleCodeTextStyle" value="background-color: red"/>
				  </c:if>
                  <html:select property="newRoutingFormPerson.personRoleCode" style="${newPersonRoleCodeTextStyle}" disabled="${viewOnly}"> 
                    <html:option value="">select:</html:option> 
                    <c:set var="routingFormPersonRoles" value="${KualiForm.document.routingFormPersonRoles}"/> 
                    <html:options collection="routingFormPersonRoles" property="personRoleCode" labelProperty="personRole.personRoleDescription"/> 
                  </html:select>
                </td>
                <td class="infoline"><kul:htmlControlAttribute property="newRoutingFormPerson.personRoleText" attributeEntry="${routingFormPersonnel.personRoleText}" readOnly="${viewOnly}"/></td>
                <td class="infoline">
                  <html:hidden property="newRoutingFormPerson.chartOfAccountsCode"/>
                  <html:hidden property="newRoutingFormPerson.organizationCode"/>
                  <c:choose>
                    <c:when test="${KualiForm.newRoutingFormPerson.chartOfAccountsCode ne null and KualiForm.newRoutingFormPerson.organizationCode ne null}">
                      ${KualiForm.newRoutingFormPerson.chartOfAccountsCode} / ${KualiForm.newRoutingFormPerson.organizationCode}
                    </c:when>
                    <c:otherwise>(select)</c:otherwise>
                  </c:choose>
                  <c:if test="${!viewOnly}">
                    <kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:newRoutingFormPerson.chartOfAccountsCode,organizationCode:newRoutingFormPerson.organizationCode" anchor="${currentTabIndex}" />
                  </c:if>
                </td>
                <td class="infoline"><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormPerson.personFinancialAidPercent" attributeEntry="${routingFormPersonnel.personFinancialAidPercent}" readOnly="${viewOnly}"/>
                </div></td>
                <td class="infoline"><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormPerson.personCreditPercent" attributeEntry="${routingFormPersonnel.personCreditPercent}" readOnly="${viewOnly}"/>
                </div></td>
                <td class="infoline">&nbsp;</td>
                <td class="infoline"><div align=center><html:image property="methodToCall.addPersonLine.anchor${currentTabIndex}" src="images/tinybutton-add1.gif" styleClass="tinybutton" alt="add person line"/></div></td>
              </tr>
              </c:if>
              
              <c:forEach items = "${KualiForm.document.routingFormPersonnel}" var="person" varStatus="status"  >
                <c:set var="isProjectDirector" value="${person.projectDirector}" />
                <tr>
   	              <html:hidden property="document.routingFormPersonnel[${status.index}].personUniversalIdentifier" />
   	              <html:hidden property="document.routingFormPersonnel[${status.index}].routingFormPersonSequenceNumber" />
   	              <html:hidden property="document.routingFormPersonnel[${status.index}].chartOfAccountsCode" />
   	              <html:hidden property="document.routingFormPersonnel[${status.index}].organizationCode" />
   	              <html:hidden property="document.routingFormPersonnel[${status.index}].personToBeNamedIndicator" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].versionNumber" />
                  <!-- Following fields are to keep track of personnel page data -->
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personLine1Address" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personLine2Address" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personCityName" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personCountyName" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personPrefixText" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personStateCode" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personSuffixText" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personCountryCode" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personPositionTitle" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personZipCode" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personPhoneNumber" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personFaxNumber" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personDivisionText" />
                  <html:hidden property="document.routingFormPersonnel[${status.index}].personEmailAddress" />
                  <th scope="row"><div align="center">${status.index+1}</div></th>
                  <td>
                    <html:hidden write="true" property="document.routingFormPersonnel[${status.index}].user.personName" />
                    <c:if test="${empty person.personUniversalIdentifier && !person.personToBeNamedIndicator}">(select)</c:if>
		    	    <c:if test="${person.personToBeNamedIndicator}">TO BE NAMED</c:if>
                    <c:if test="${!viewOnly}">
                      <c:choose>
                        <c:when test="${budgetLinked and isProjectDirector }" />
                        <c:otherwise>
                          <kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" fieldConversions="personUniversalIdentifier:document.routingFormPersonnel[${status.index}].personUniversalIdentifier,personName:document.routingFormPersonnel[${status.index}].user.personName" extraButtonSource="images/buttonsmall_namelater.gif" extraButtonParams="&document.routingFormPersonnel[${status.index}].personToBeNamedIndicator=true" anchor="${currentTabIndex}" />
                        </c:otherwise>
                      </c:choose>
                    </c:if>
                  </td>
                  <td>
                    <!-- Hidden variables for document.routingFormPersonRoles above under newRoutingFormPerson item -->
	                <kul:checkErrors keyMatch="document.routingFormPersonnel[${status.index}].personRoleCode" auditMatch="document.routingFormPersonnel[${status.index}].personRoleCode"/>
					<c:if test="${hasErrors==true}">
					  <c:set var="personRoleCodeTextStyle" value="background-color: red"/>
					</c:if>
	                <html:select property="document.routingFormPersonnel[${status.index}].personRoleCode" style="${personRoleCodeTextStyle}" disabled="${viewOnly}"> 
	                  <html:option value="">select:</html:option> 
	                  <c:set var="routingFormPersonRoles" value="${KualiForm.document.routingFormPersonRoles}"/> 
	                  <html:options collection="routingFormPersonRoles" property="personRoleCode" labelProperty="personRole.personRoleDescription"/> 
	                </html:select>
                  </td>
                  <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personRoleText" attributeEntry="${routingFormPersonnel.personRoleText}" readOnly="${viewOnly or (budgetLinked and isProjectDirector)}"/></td>
                  <td>
                    <c:choose>
                      <c:when test="${person.chartOfAccountsCode ne null and person.organizationCode ne null}">
                        ${person.chartOfAccountsCode} / ${person.organizationCode}
                      </c:when>
                      <c:otherwise>(select)</c:otherwise>
                    </c:choose>
                    <c:if test="${!viewOnly}">
                      <c:choose>
                        <c:when test="${budgetLinked and isProjectDirector }" />
                        <c:otherwise>
                          <kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:document.routingFormPersonnel[${status.index}].chartOfAccountsCode,organizationCode:document.routingFormPersonnel[${status.index}].organizationCode" anchor="${currentTabIndex}" />
                        </c:otherwise>
                      </c:choose>
                    </c:if>
                  </td>
                  <td><div align="center"><span class="infoline">
                    <kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personFinancialAidPercent" attributeEntry="${routingFormPersonnel.personFinancialAidPercent}" readOnly="${viewOnly}"/>
                  </span></div></td>
                  <td><div align="center"><span class="infoline">
                    <kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personCreditPercent" attributeEntry="${routingFormPersonnel.personCreditPercent}" readOnly="${viewOnly}"/>
                  </span></div></td>
                  <td><div align="center"><html:image property="methodToCall.headerTab.headerDispatch.save.navigateTo.personnel.anchor${status.index}" src="images/tinybutton-view.gif" styleClass="tinybutton" alt="view person line"/></div></td>
                  <td><div align=center>&nbsp;<c:if test="${!viewOnly}"><html:image property="methodToCall.deletePersonLine.line${status.index}.anchor${currentTabIndex}" src="images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete person line"/></c:if></div></td>
                </tr>
              </c:forEach>

              <tr>
                <td colspan=9 class="tab-subhead"><span class="left">Add Proposal Units/Orgs </span> </td>
              </tr>
              <tr>
                <th>&nbsp;</th>
                <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${routingFormOrganizationCreditPercent.chartOfAccountsCode}" skipHelpUrl="true" noColon="true" />/<kul:htmlAttributeLabel attributeEntry="${routingFormOrganizationCreditPercent.organizationCode}" skipHelpUrl="true" noColon="true" /> </div></th>
                <th colspan=3><div align="center"><kul:htmlAttributeLabel attributeEntry="${routingFormOrganizationCreditPercent.organizationCreditRoleText}" skipHelpUrl="true" noColon="true" /></div></th>
                <th><div align="center"><kul:htmlAttributeLabel attributeEntry="${routingFormOrganizationCreditPercent.organizationFinancialAidPercent}" skipHelpUrl="true" noColon="true" /></th>
                <th><div align="center"><kul:htmlAttributeLabel attributeEntry="${routingFormOrganizationCreditPercent.organizationCreditPercent}" skipHelpUrl="true" noColon="true" /></th>
                <th>&nbsp;</th>
                <th>Action</th>
              </tr>
              <c:if test="${!viewOnly}">
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
                  <c:if test="${!viewOnly}">
                    <kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:newRoutingFormOrganizationCreditPercent.chartOfAccountsCode,organizationCode:newRoutingFormOrganizationCreditPercent.organizationCode" anchor="${currentTabIndex}" />
                  </c:if>
                </td>
                <td class="infoline" colspan=3><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormOrganizationCreditPercent.organizationCreditRoleText" attributeEntry="${routingFormOrganizationCreditPercent.organizationCreditRoleText}" readOnly="${viewOnly}"/>
                </div></td>
                <td class="infoline"><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormOrganizationCreditPercent.organizationFinancialAidPercent" attributeEntry="${routingFormOrganizationCreditPercent.organizationFinancialAidPercent}" readOnly="${viewOnly}"/>
                </div></td>
                <td class="infoline"><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormOrganizationCreditPercent.organizationCreditPercent" attributeEntry="${routingFormOrganizationCreditPercent.organizationCreditPercent}" readOnly="${viewOnly}"/>
                </div></td>
                <td class="infoline">&nbsp;</td>
                <td class="infoline"><div align=center><html:image property="methodToCall.addOrganizationCreditPercentLine.anchor${currentTabIndex}" src="images/tinybutton-add1.gif" styleClass="tinybutton" alt="add org line"/></div></td>
              </tr>
              </c:if>
              <c:forEach items = "${KualiForm.document.routingFormOrganizationCreditPercents}" var="org" varStatus="status"  >
                <tr>
   	              <html:hidden property="document.routingFormOrganizationCreditPercents[${status.index}].chartOfAccountsCode" />
   	              <html:hidden property="document.routingFormOrganizationCreditPercents[${status.index}].organizationCode" />
                  <html:hidden property="document.routingFormOrganizationCreditPercents[${status.index}].versionNumber" />
                  <th scope="row"><div align="center">${status.index+1}</div></th>
                  <td>
                    <c:choose>
                      <c:when test="${org.chartOfAccountsCode ne null and org.organizationCode ne null}">
                        ${org.chartOfAccountsCode} / ${org.organizationCode}
                      </c:when>
                      <c:otherwise>(select)</c:otherwise>
                    </c:choose>
                    <c:if test="${!viewOnly}">
                      <kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:document.routingFormOrganizationCreditPercents[${status.index}].chartOfAccountsCode,organizationCode:document.routingFormOrganizationCreditPercents[${status.index}].organizationCode" anchor="${currentTabIndex}" />
                    </c:if>
                  </td>
                  <td colspan=3><div align="center"><span class="infoline">
                    <kul:htmlControlAttribute property="document.routingFormOrganizationCreditPercents[${status.index}].organizationCreditRoleText" attributeEntry="${routingFormOrganizationCreditPercent.organizationCreditRoleText}" readOnly="${viewOnly}"/>
                  </span></div></td>
                  <td><div align="center"><span class="infoline">
                    <kul:htmlControlAttribute property="document.routingFormOrganizationCreditPercents[${status.index}].organizationFinancialAidPercent" attributeEntry="${routingFormOrganizationCreditPercent.organizationFinancialAidPercent}" readOnly="${viewOnly}"/>
                  </span></div></td>
                  <td><div align="center"><span class="infoline">
                    <kul:htmlControlAttribute property="document.routingFormOrganizationCreditPercents[${status.index}].organizationCreditPercent" attributeEntry="${routingFormOrganizationCreditPercent.organizationCreditPercent}" readOnly="${viewOnly}"/>
                  </span></div></td>
                  <td>&nbsp;</td>
                  <td><div align=center>&nbsp;<c:if test="${!viewOnly}"><html:image property="methodToCall.deleteOrganizationCreditPercentLine.line${status.index}.anchor${currentTabIndex}" src="images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete person line"/></c:if></div></td>
                </tr>
              </c:forEach>
              
              <tr>
                <td colspan=9 class="tab-subhead"><span class="left">Summary</span></td>
              </tr>
              <tr>
                <th>&nbsp;</th>
                <th colspan=4>&nbsp;</th>
                <th><div align="center"><kul:htmlAttributeLabel attributeEntry="${routingFormOrganizationCreditPercent.organizationFinancialAidPercent}" skipHelpUrl="true" noColon="true" /></th>
                <th><div align="center"><kul:htmlAttributeLabel attributeEntry="${routingFormOrganizationCreditPercent.organizationCreditPercent}" skipHelpUrl="true" noColon="true" /></th>
                <th colspan=2>&nbsp;</th>
              </tr>
              <tr>
                <th scope="row">&nbsp;</th>
                <td colspan=4 class="infoline"><div align="right"><b>Totals</b></div></td>
                <td class="infoline"><div align="center">
                  ${KualiForm.document.totalFinancialAidPercent}%
                </div></td>
                <td class="infoline"><div align="center">
                  ${KualiForm.document.totalCreditPercent}%
                </div></td>
                <td colspan=2 class="infoline">&nbsp;</td>
              </tr>

            </table>
          </div>
</kul:tab>
