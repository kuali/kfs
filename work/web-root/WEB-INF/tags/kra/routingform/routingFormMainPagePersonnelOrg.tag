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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<c:set var="routingFormAttributes" value="${DataDictionary.KualiRoutingFormDocument.attributes}" />
<c:set var="routingFormPersonnel" value="${DataDictionary.RoutingFormPersonnel.attributes}" />
<c:set var="routingFormOrganizationCreditPercent" value="${DataDictionary.RoutingFormOrganizationCreditPercent.attributes}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>
<c:set var="budgetLinked" value="${KualiForm.editingMode['budgetLinked']}"/>

<kul:tab tabTitle="Personnel and Units/Orgs" defaultOpen="true" tabErrorKey="newRoutingFormProjectDirector*,newRoutingFormOtherPerson*,document.routingFormPersonnel*,newRoutingFormOrganizationCreditPercent*,document.routingFormOrganizationCreditPercent*,document.routingFormFellowFullName" auditCluster="mainPageAuditErrors" tabAuditKey="document.routingFormPersonnel*,document.routingFormOrganizationCreditPercent*">
	<div class="tab-container-error"><div class="left-errmsg-tab"><kra:auditErrors cluster="mainPageAuditErrors" keyMatch="document.routingFormPersonnel*,document.routingFormOrganizationCreditPercent*" isLink="false" includesTitle="true"/></div></div>

  <html:hidden property="document.personnelNextSequenceNumber" />

          <div class="tab-container" align="center">
            <div class="h2-container"> <span class="subhead-left">
              <h2>Personnel and Units/Orgs</h2>

              </span> </div>
            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <td colspan=9 class="tab-subhead"><span class="left">Add Proposal PD/Co-PD </span> </td>
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
              
              <!-- Add PD/Co-PD -->  
              <c:if test="${!viewOnly}">
              <tr>
                <th scope="row">add:
                  <!-- Following fields are to keep track of personnel page data -->
                  <html:hidden property="newRoutingFormProjectDirector.personLine1Address" />
                  <html:hidden property="newRoutingFormProjectDirector.personPhoneNumber" />
                  <html:hidden property="newRoutingFormProjectDirector.personEmailAddress" />
                </th>
                <td class="infoline">
                  <html:hidden property="newRoutingFormProjectDirector.personToBeNamedIndicator" />
                  <html:hidden property="newRoutingFormProjectDirector.personUniversalIdentifier" />
                  <html:hidden property="newRoutingFormProjectDirector.user.personName"/>
                  <html:text property="newRoutingFormProjectDirector.user.personUserIdentifier" onblur="personIDLookup('newRoutingFormProjectDirector.user.personUserIdentifier')"/>                  
                  <!-- <c:if test="${empty KualiForm.newRoutingFormProjectDirector.personUniversalIdentifier && !KualiForm.newRoutingFormProjectDirector.personToBeNamedIndicator}">&nbsp;</c:if> -->
		    	  <c:if test="${KualiForm.newRoutingFormProjectDirector.personToBeNamedIndicator}">TO BE NAMED</c:if>
                  <c:if test="${!viewOnly}">
                    <kul:lookup boClassName="org.kuali.module.cg.bo.ProjectDirector" fieldConversions="universalUser.personUniversalIdentifier:newRoutingFormProjectDirector.personUniversalIdentifier,universalUser.personName:newRoutingFormProjectDirector.user.personName" extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_namelater.gif" extraButtonParams="&newRoutingFormProjectDirector.personToBeNamedIndicator=true" anchor="${currentTabIndex}" />
                  </c:if>
		          <div id="newRoutingFormProjectDirector.user.personName.div" >
		             <c:if test="${!empty KualiForm.newRoutingFormProjectDirector.user.personUserIdentifier}">
		                 <c:choose>
							<c:when test="${empty KualiForm.newRoutingFormProjectDirector.user.personName}">
								<span style='color: red;'><c:out value="person not found" /> </span>
							</c:when>
							<c:otherwise>
								<c:out value="${KualiForm.newRoutingFormProjectDirector.user.personName}" />
							</c:otherwise>
						 </c:choose>                        
		              </c:if>
		           </div>
                  
                </td>
                <td class="infoline">
                  <c:forEach items="${KualiForm.document.routingFormPersonRoles}" var="routingFormPersonRole" varStatus="status"> 
                    <html:hidden property="document.routingFormPersonRoles[${status.index}].documentNumber" /> 
                    <html:hidden property="document.routingFormPersonRoles[${status.index}].personRoleCode" /> 
                    <html:hidden property="document.routingFormPersonRoles[${status.index}].versionNumber" /> 
                    <html:hidden property="document.routingFormPersonRoles[${status.index}].personRole.personRoleDescription" /> 
                  </c:forEach>
                  <kul:checkErrors keyMatch="newRoutingFormProjectDirector.personRoleCode" auditMatch="newRoutingFormProjectDirector.personRoleCode"/>
				  <c:if test="${hasErrors==true}">
				    <c:set var="newPersonRoleCodeTextStyle" value="background-color: red"/>
				  </c:if>
                  <html:select property="newRoutingFormProjectDirector.personRoleCode" style="${newPersonRoleCodeTextStyle}" disabled="${viewOnly}"> 
                      <c:set var="routingFormPersonRoles" value="${KualiForm.document.routingFormProjectDirectorRoles}"/> 
                      <html:options collection="routingFormPersonRoles" property="personRoleCode" labelProperty="personRole.personRoleDescription"/> 
                  </html:select>
                </td>
                <td class="infoline"><kul:htmlControlAttribute property="newRoutingFormProjectDirector.personRoleText" attributeEntry="${routingFormPersonnel.personRoleText}" readOnly="${viewOnly}"/></td>
                <td class="infoline">
                  <html:hidden property="newRoutingFormProjectDirector.chartOfAccountsCode"/>
                  <html:hidden property="newRoutingFormProjectDirector.organizationCode"/>

                <div id="newRoutingFormProjectDirector.user.primaryDepartmentCode.div" style="float: left; text-align: left;">
             
                    <c:if test="${KualiForm.newRoutingFormProjectDirector.chartOfAccountsCode ne null and KualiForm.newRoutingFormProjectDirector.organizationCode ne null}">
                      ${KualiForm.newRoutingFormProjectDirector.chartOfAccountsCode} / ${KualiForm.newRoutingFormProjectDirector.organizationCode}
                    </c:if>
               </div>
                  <c:if test="${!viewOnly}">
                    &nbsp;<kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:newRoutingFormProjectDirector.chartOfAccountsCode,organizationCode:newRoutingFormProjectDirector.organizationCode" anchor="${currentTabIndex}" />
                  </c:if>
                </td>
                <td class="infoline"><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormProjectDirector.personFinancialAidPercent" attributeEntry="${routingFormPersonnel.personFinancialAidPercent}" readOnly="${viewOnly}"/>
                </div></td>
                <td class="infoline"><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormProjectDirector.personCreditPercent" attributeEntry="${routingFormPersonnel.personCreditPercent}" readOnly="${viewOnly}"/>
                </div></td>
                <td class="infoline">&nbsp;</td>
                <td class="infoline"><div align=center><html:image property="methodToCall.addProjectDirectorPersonLine.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" alt="add person line"/></div></td>
              </tr>
              </c:if>
              
              <!-- Individual PI Person -->
              <c:set var="personIndex" value="0"/>
              <c:forEach items = "${KualiForm.document.routingFormPersonnel}" var="person" varStatus="status"  >
                <c:set var="isProjectDirector" value="${person.projectDirector}" />
                <c:if test="${isProjectDirector or person.personRoleCode eq KualiForm.systemParametersMap[KraConstants.CO_PROJECT_DIRECTOR_PARAM]}">
                <c:set var="personIndex" value="${personIndex + 1}"/>
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
                  <th scope="row"><div align="center">${personIndex}</div></th>
                  <td>
                    <html:hidden property="document.routingFormPersonnel[${status.index}].user.personName" />
                    <c:if test="${viewOnly}">
                    <html:hidden property="document.routingFormPersonnel[${status.index}].user.personUserIdentifier" />                  
                    </c:if>
                    <!-- <c:if test="${empty person.personUniversalIdentifier && !person.personToBeNamedIndicator}">&nbsp;</c:if> -->
		    	    <c:if test="${person.personToBeNamedIndicator}">TO BE NAMED</c:if>
                    <c:if test="${!viewOnly}">
                      <c:choose>
                        <c:when test="${budgetLinked and isProjectDirector }" />
                        <c:otherwise>
                    		<html:text property="document.routingFormPersonnel[${status.index}].user.personUserIdentifier" onblur="personIDLookup('document.routingFormPersonnel[${status.index}].user.personUserIdentifier')"/>                  
                          <kul:lookup boClassName="org.kuali.module.cg.bo.ProjectDirector" fieldConversions="universalUser.personUniversalIdentifier:document.routingFormPersonnel[${status.index}].personUniversalIdentifier,universalUser.personName:document.routingFormPersonnel[${status.index}].user.personName" extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_namelater.gif" extraButtonParams="&document.routingFormPersonnel[${status.index}].personToBeNamedIndicator=true" anchor="${currentTabIndex}" />
                        </c:otherwise>
                      </c:choose>
                    </c:if>
		          <div id="document.routingFormPersonnel[${status.index}].user.personName.div" >
		             <c:if test="${!empty person.personUniversalIdentifier}">
		                 <c:choose>
							<c:when test="${empty person.user.personName}">
								<span style='color: red;'><c:out value="person not found" /> </span>
							</c:when>
							<c:otherwise>
								<c:out value="${person.user.personName}" />
							</c:otherwise>
						 </c:choose>                        
		              </c:if>
		           </div>
                    
                  </td>
                  <td>
                    <!-- Hidden variables for document.routingFormPersonRoles above under newRoutingFormProjectDirector item -->
	                <kul:checkErrors keyMatch="document.routingFormPersonnel[${status.index}].personRoleCode" auditMatch="document.routingFormPersonnel[${status.index}].personRoleCode"/>
					<c:if test="${hasErrors==true}">
					  <c:set var="personRoleCodeTextStyle" value="background-color: red"/>
					</c:if>
                    <c:choose>
                      <c:when test="${!viewOnly}">
    	                <html:select property="document.routingFormPersonnel[${status.index}].personRoleCode" style="${personRoleCodeTextStyle}" disabled="${viewOnly}"> 
                        <c:set var="routingFormPersonRoles" value="${KualiForm.document.routingFormProjectDirectorRoles}"/> 
                        <html:options collection="routingFormPersonRoles" property="personRoleCode" labelProperty="personRole.personRoleDescription"/> 
     	                </html:select>
	                  </c:when>
  		              <c:otherwise>
                        <html:hidden property="document.routingFormPersonnel[${status.index}].personRoleCode" />
                        <html:hidden property="document.routingFormPersonnel[${status.index}].personRole.personRoleDescription" />
                        ${person.personRole.personRoleDescription}
  		              </c:otherwise>
  		            </c:choose>
                  </td>
                  <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personRoleText" attributeEntry="${routingFormPersonnel.personRoleText}" readOnly="${viewOnly or (budgetLinked and isProjectDirector)}"/></td>
                  <td>
                   <div id="document.routingFormPersonnel[${status.index}].user.primaryDepartmentCode.div" style="float: left; text-align: left;">
                  
                      <c:if test="${person.chartOfAccountsCode ne null and person.organizationCode ne null}">
                        ${person.chartOfAccountsCode} / ${person.organizationCode}
                      </c:if>
                    </div>
                    <c:if test="${!viewOnly}">
                      <c:choose>
                        <c:when test="${budgetLinked and isProjectDirector }" />
                        <c:otherwise>
                          &nbsp;<kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:document.routingFormPersonnel[${status.index}].chartOfAccountsCode,organizationCode:document.routingFormPersonnel[${status.index}].organizationCode" anchor="${currentTabIndex}" />
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
                  <td><div align="center"><html:image property="methodToCall.headerTab.headerDispatch.save.navigateTo.personnel.anchor${status.index}" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" styleClass="tinybutton" alt="view person line"/></div></td>
                  <td><div align=center>&nbsp;<c:if test="${!viewOnly}"><html:image property="methodToCall.deletePersonLine.line${status.index}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="delete person line"/></c:if></div></td>
                </tr>
                </c:if>
              </c:forEach>
              
              <!-- Add Other Person -->
              <tr>
                <td colspan=9 class="tab-subhead"><span class="left">Add Other Proposal Personnel </span> </td>
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
                  <html:hidden property="newRoutingFormOtherPerson.personLine1Address" />
                  <html:hidden property="newRoutingFormOtherPerson.personPhoneNumber" />
                  <html:hidden property="newRoutingFormOtherPerson.personEmailAddress" />
                </th>
                <td class="infoline">
                  <html:hidden property="newRoutingFormOtherPerson.personToBeNamedIndicator" />
                  <html:hidden property="newRoutingFormOtherPerson.personUniversalIdentifier" />
                  <html:hidden  property="newRoutingFormOtherPerson.user.personName"/>
                  <html:text property="newRoutingFormOtherPerson.user.personUserIdentifier" onblur="personIDLookup('newRoutingFormOtherPerson.user.personUserIdentifier')"/>                  
                  <!--  <c:if test="${empty KualiForm.newRoutingFormOtherPerson.personUniversalIdentifier && !KualiForm.newRoutingFormOtherPerson.personToBeNamedIndicator}">&nbsp;</c:if> -->
            <c:if test="${KualiForm.newRoutingFormOtherPerson.personToBeNamedIndicator}">TO BE NAMED</c:if>
                  <c:if test="${!viewOnly}">
                    <kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" fieldConversions="personUniversalIdentifier:newRoutingFormOtherPerson.personUniversalIdentifier,personName:newRoutingFormOtherPerson.user.personName" extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_namelater.gif" extraButtonParams="&newRoutingFormOtherPerson.personToBeNamedIndicator=true" anchor="${currentTabIndex}" />
                  </c:if>
		          <div id="newRoutingFormOtherPerson.user.personName.div" >
		             <c:if test="${!empty KualiForm.newRoutingFormOtherPerson.user.personUserIdentifier}">
		                 <c:choose>
							<c:when test="${empty KualiForm.newRoutingFormOtherPerson.user.personName}">
								<span style='color: red;'><c:out value="person not found" /> </span>
							</c:when>
							<c:otherwise>
								<c:out value="${KualiForm.newRoutingFormOtherPerson.user.personName}" />
							</c:otherwise>
						 </c:choose>                        
		              </c:if>
		           </div>
                  
                </td>
                <td class="infoline">
                  <c:forEach items="${KualiForm.document.routingFormPersonRoles}" var="routingFormPersonRole" varStatus="status"> 
                    <html:hidden property="document.routingFormPersonRoles[${status.index}].documentNumber" /> 
                    <html:hidden property="document.routingFormPersonRoles[${status.index}].personRoleCode" /> 
                    <html:hidden property="document.routingFormPersonRoles[${status.index}].versionNumber" /> 
                    <html:hidden property="document.routingFormPersonRoles[${status.index}].personRole.personRoleDescription" /> 
                  </c:forEach>
                  <kul:checkErrors keyMatch="newRoutingFormOtherPerson.personRoleCode" auditMatch="newRoutingFormOtherPerson.personRoleCode"/>
          <c:if test="${hasErrors==true}">
            <c:set var="newPersonRoleCodeTextStyle" value="background-color: red"/>
          </c:if>
                  <html:select property="newRoutingFormOtherPerson.personRoleCode" style="${newPersonRoleCodeTextStyle}" disabled="${viewOnly}"> 
                    <c:set var="routingFormPersonRoles" value="${KualiForm.document.routingFormOtherPersonRoles}"/> 
                    <html:options collection="routingFormPersonRoles" property="personRoleCode" labelProperty="personRole.personRoleDescription"/> 
                  </html:select>
                </td>
                <td class="infoline"><kul:htmlControlAttribute property="newRoutingFormOtherPerson.personRoleText" attributeEntry="${routingFormPersonnel.personRoleText}" readOnly="${viewOnly}"/></td>
                <td class="infoline">
                  <html:hidden property="newRoutingFormOtherPerson.chartOfAccountsCode"/>
                  <html:hidden property="newRoutingFormOtherPerson.organizationCode"/>
                 <div id="newRoutingFormOtherPerson.user.primaryDepartmentCode.div" style="float: left; text-align: left;">
                    <c:if test="${KualiForm.newRoutingFormOtherPerson.chartOfAccountsCode ne null and KualiForm.newRoutingFormOtherPerson.organizationCode ne null}">
                      ${KualiForm.newRoutingFormOtherPerson.chartOfAccountsCode} / ${KualiForm.newRoutingFormOtherPerson.organizationCode}
                    </c:if>
                 </div>
                  <c:if test="${!viewOnly}">
                    &nbsp;<kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:newRoutingFormOtherPerson.chartOfAccountsCode,organizationCode:newRoutingFormOtherPerson.organizationCode" anchor="${currentTabIndex}" />
                  </c:if>
                </td>
                <td class="infoline"><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormOtherPerson.personFinancialAidPercent" attributeEntry="${routingFormPersonnel.personFinancialAidPercent}" readOnly="${viewOnly}"/>
                </div></td>
                <td class="infoline"><div align="center">
                  <kul:htmlControlAttribute property="newRoutingFormOtherPerson.personCreditPercent" attributeEntry="${routingFormPersonnel.personCreditPercent}" readOnly="${viewOnly}"/>
                </div></td>
                <td class="infoline">&nbsp;</td>
                <td class="infoline"><div align=center><html:image property="methodToCall.addOtherPersonLine.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" alt="add person line"/></div></td>
              </tr>
              </c:if>
              
              <!-- Individual Other Person -->
              <c:set var="otherPersonIndex" value="0"/>
              <c:forEach items = "${KualiForm.document.routingFormPersonnel}" var="person" varStatus="status"  >
                <c:set var="isProjectDirector" value="${person.projectDirector}" />
                <c:if test="${person.personRoleCode eq KualiForm.systemParametersMap[KraConstants.OTHER_PERSON_PARAM] or person.personRoleCode eq KualiForm.systemParametersMap[KraConstants.CONTACT_PERSON_PARAM]}">
                <c:set var="otherPersonIndex" value="${otherPersonIndex + 1}"/>
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
                  <th scope="row"><div align="center">${otherPersonIndex}</div></th>
                  <td>
                    <html:hidden property="document.routingFormPersonnel[${status.index}].user.personName" />
                    <c:if test="${viewOnly}">
                        <html:hidden property="document.routingFormPersonnel[${status.index}].user.personUserIdentifier"/>                  
                    </c:if>
                    <!-- <c:if test="${empty person.personUniversalIdentifier && !person.personToBeNamedIndicator}">&nbsp;</c:if> -->
              <c:if test="${person.personToBeNamedIndicator}">TO BE NAMED</c:if>
                    <c:if test="${!viewOnly}">
                      <c:choose>
                        <c:when test="${budgetLinked and isProjectDirector }" />
                        <c:otherwise>
                          <html:text property="document.routingFormPersonnel[${status.index}].user.personUserIdentifier" onblur="personIDLookup('document.routingFormPersonnel[${status.index}].user.personUserIdentifier')"/>                  
                          <kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" fieldConversions="personUniversalIdentifier:document.routingFormPersonnel[${status.index}].personUniversalIdentifier,personName:document.routingFormPersonnel[${status.index}].user.personName" extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_namelater.gif" extraButtonParams="&document.routingFormPersonnel[${status.index}].personToBeNamedIndicator=true" anchor="${currentTabIndex}" />
                        </c:otherwise>
                      </c:choose>
                    </c:if>
		          <div id="document.routingFormPersonnel[${status.index}].user.personName.div" >
		             <c:if test="${!empty person.personUniversalIdentifier}">
		                 <c:choose>
							<c:when test="${empty person.user.personName}">
								<span style='color: red;'><c:out value="person not found" /> </span>
							</c:when>
							<c:otherwise>
								<c:out value="${person.user.personName}" />
							</c:otherwise>
						 </c:choose>                        
		              </c:if>
		           </div>
                    
                  </td>
                  <td>
                    <!-- Hidden variables for document.routingFormPersonRoles above under newRoutingFormOtherPerson item -->
                  <kul:checkErrors keyMatch="document.routingFormPersonnel[${status.index}].personRoleCode" auditMatch="document.routingFormPersonnel[${status.index}].personRoleCode"/>
          <c:if test="${hasErrors==true}">
            <c:set var="personRoleCodeTextStyle" value="background-color: red"/>
          </c:if>
                    <c:choose>
                      <c:when test="${!viewOnly}">
                      <html:select property="document.routingFormPersonnel[${status.index}].personRoleCode" style="${personRoleCodeTextStyle}" disabled="${viewOnly}"> 
                          <c:set var="routingFormPersonRoles" value="${KualiForm.document.routingFormOtherPersonRoles}"/> 
                          <html:options collection="routingFormPersonRoles" property="personRoleCode" labelProperty="personRole.personRoleDescription"/> 
                        </html:select>
                    </c:when>
                    <c:otherwise>
                        <html:hidden property="document.routingFormPersonnel[${status.index}].personRoleCode" />
                        <html:hidden property="document.routingFormPersonnel[${status.index}].personRole.personRoleDescription" />
                        ${person.personRole.personRoleDescription}
                    </c:otherwise>
                  </c:choose>
                  </td>
                  <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personRoleText" attributeEntry="${routingFormPersonnel.personRoleText}" readOnly="${viewOnly or (budgetLinked and isProjectDirector)}"/></td>
                  <td>
                   <div id="document.routingFormPersonnel[${status.index}].user.primaryDepartmentCode.div" style="float: left; text-align: left;">
                      <c:if test="${person.chartOfAccountsCode ne null and person.organizationCode ne null}">
                        ${person.chartOfAccountsCode} / ${person.organizationCode}
                      </c:if>
                   </div>
                    <c:if test="${!viewOnly}">
                      <c:choose>
                        <c:when test="${budgetLinked and isProjectDirector }" />
                        <c:otherwise>
                          &nbsp;<kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="chartOfAccounts.chartOfAccountsCode:document.routingFormPersonnel[${status.index}].chartOfAccountsCode,organizationCode:document.routingFormPersonnel[${status.index}].organizationCode" anchor="${currentTabIndex}" />
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
                  <td><div align="center"><html:image property="methodToCall.headerTab.headerDispatch.save.navigateTo.personnel.anchor${status.index}" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" styleClass="tinybutton" alt="view person line"/></div></td>
                  <td><div align=center>&nbsp;<c:if test="${!viewOnly}"><html:image property="methodToCall.deletePersonLine.line${status.index}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="delete person line"/></c:if></div></td>
                </tr>
                </c:if>
              </c:forEach>

              <!-- Add Orgs -->
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
                    <c:otherwise>&nbsp;</c:otherwise>
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
                <td class="infoline"><div align=center><html:image property="methodToCall.addOrganizationCreditPercentLine.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" alt="add org line"/></div></td>
              </tr>
              </c:if>
              
              <!--  Individual Orgs -->
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
                      <c:otherwise>&nbsp;</c:otherwise>
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
                  <td><div align=center>&nbsp;<c:if test="${!viewOnly}"><html:image property="methodToCall.deleteOrganizationCreditPercentLine.line${status.index}.anchor${currentTabIndex}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" alt="delete person line"/></c:if></div></td>
                </tr>
              </c:forEach>
              
              <!-- Fellow -->
              <tr>
                <td colspan=9 class="tab-subhead"><span class="left"><kul:htmlAttributeLabel attributeEntry="${routingFormAttributes.routingFormFellowFullName}" skipHelpUrl="true" noColon="true" /></span></td>
              </tr>
              <tr>
                <th>&nbsp;</th>
                <td class="infoline" colspan=8><kul:htmlControlAttribute property="document.routingFormFellowFullName" attributeEntry="${routingFormAttributes.routingFormFellowFullName}" readOnly="${viewOnly}"/></td>
              </tr>
              
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
