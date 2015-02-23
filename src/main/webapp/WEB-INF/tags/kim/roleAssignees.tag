<%--
 Copyright 2009 The Kuali Foundation

 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.opensource.org/licenses/ecl2.php

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="formAction" required="false" description="form action" %>

<c:set var="roleMemberAttributes" value="${DataDictionary.KimDocumentRoleMember.attributes}" />
<c:set var="roleQualifierAttributes" value="${DataDictionary.KimDocumentRoleQualifier.attributes}" />
<c:set var="kimAttributes" value="${DataDictionary.KimAttributeImpl.attributes}" />

<c:if test="${(!(empty KualiForm.document.members && empty KualiForm.document.modifiedMembers)) || canModifyAssignees}">
<kul:tab tabTitle="Assignees" defaultOpen="true" tabErrorKey="document.member*,member.*">
    <div class="tab-container" align="center">
    <c:if test="${(empty memberSearchValue) && (empty KualiForm.document.searchResultMembers)}">

      <kul:tableRenderPagingBanner pageNumber="${KualiForm.memberTableMetadata.viewedPageNumber}"
                                   totalPages="${KualiForm.memberTableMetadata.totalNumberOfPages}"
                                   firstDisplayedRow="${KualiForm.memberTableMetadata.firstRowIndex}"
                                   lastDisplayedRow="${KualiForm.memberTableMetadata.lastRowIndex}"
                                   resultsActualSize="${KualiForm.memberTableMetadata.resultsActualSize}"
                                   resultsLimitedSize="${KualiForm.memberTableMetadata.resultsLimitedSize}"
                                   buttonExtraParams=".anchor${currentTabIndex}"/>
    </c:if>

    <input type="hidden" name="memberTableMetadata.${Constants.TableRenderConstants.PREVIOUSLY_SORTED_COLUMN_INDEX_PARAM}" value="${KualiForm.memberTableMetadata.columnToSortIndex}"/>
    <input type="hidden" name="memberTableMetadata.sortDescending" value="${KualiForm.memberTableMetadata.sortDescending}"/>
    <input type="hidden" name="memberTableMetadata.viewedPageNumber" value="${KualiForm.memberTableMetadata.viewedPageNumber}"/>
    <table cellpadding="0" cellspacing="0" summary="">
        <tr>
          <td align="center">
            <div align="center">
              <br/>
              <b>Search for Members by Name:</b>
                  <kul:htmlControlAttribute property="memberSearchValue" attributeEntry="${roleMemberAttributes.memberName}" readOnly="false" />
                  <kul:lookup boClassName="org.kuali.rice.kim.impl.identity.PersonImpl"
                       fieldConversions="principalName:memberSearchValue" anchor="${currentTabIndex}" />
              <br/>
              <br/>
            </div>
          </td>
        </tr>
        <tr>
          <td class="infoline">
            <div align="center">
              <html:image property="methodToCall.search.anchor${currentTabIndex}"
                          src="${ConfigProperties.kr.externalizable.images.url}tinybutton-search.gif" styleClass="tinybutton" onchange="methodToCall.search.anchor${currentTabIndex}"/>
              <html:image property="methodToCall.clear.anchor${currentTabIndex}"
                          src="${ConfigProperties.kr.externalizable.images.url}tinybutton-clear1.gif" styleClass="tinybutton"/>
            </div>
          </td>
        </tr>
    </table>

    <c:if test="${canModifyAssignees}"> 
      <table cellpadding="0" cellspacing="0" summary="">
    		<tr>
          		<td colspan="100%" class="tab-subhead">Add Member:</td>
        </tr>
            <tr>
              <th>&nbsp;</th>
              <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberTypeCode}" horizontal="false" />
              <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberId}" horizontal="false" />
              <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberNamespaceCode}" horizontal="false" />
              <c:if test='${KualiForm.member.memberTypeCode != "R" }'>
                <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberName}" horizontal="false" /> 
              </c:if>   
              <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberFullName}" horizontal="false" />
              <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.activeFromDate}" horizontal="false" />
              <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.activeToDate}" horizontal="false" />
              <c:forEach var="attrDefn" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="status">
                <c:set var="fieldName" value="${attrDefn.kimAttribute.attributeName}" />
                <c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
                <kul:htmlAttributeHeaderCell attributeEntry="${attrEntry}" useShortLabel="false" />
              </c:forEach>
              <c:if test="${canModifyAssignees}"> 
                <kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
              </c:if> 
            </tr>     
                
            <tr>
              <th class="infoline">Add:</th>
              <td align="left" valign="middle" class="infoline">
              <div align="center">
                  <kul:htmlControlAttribute property="member.memberTypeCode" 
                  attributeEntry="${roleMemberAttributes.memberTypeCode}" 
                  onchange="changeMemberTypeCode(this.form)" disabled="${!canModifyAssignees}" />
                  <NOSCRIPT>
                      <input type="image" tabindex="32768" name="methodToCall.changeMemberTypeCode" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-refresh.gif" class="tinybutton" title="Click to refresh the page after changing the member type." alt="Click to refresh the page after changing the member type." />
                  </NOSCRIPT>              
              </div>
              <c:set var="bo" value="${KualiForm.memberBusinessObjectName}"/>
              <c:set var="fc" value="${KualiForm.memberFieldConversions}"/>
              </td>
              <td class="infoline">   
                <div align="center">                
                  <kul:htmlControlAttribute property="member.memberId" attributeEntry="${roleMemberAttributes.memberId}" readOnly="${!canModifyAssignees}"/>
                  <c:if test="${canModifyAssignees}">
                      <kul:lookup boClassName="${bo}" fieldConversions="${fc}" anchor="${tabKey}" />
                  </c:if>
                </div>
              </td>
              <td class="infoline">   
                <div align="center">  
                  <c:if test='${KualiForm.member.memberTypeCode != "G"}'>              
                    <kul:htmlControlAttribute property="member.memberNamespaceCode" attributeEntry="${roleMemberAttributes.memberNamespaceCode}" readOnly="true" />
                  </c:if>
                  <c:if test='${KualiForm.member.memberTypeCode == "G"}'>
                    <kul:htmlControlAttribute property="member.memberNamespaceCode" attributeEntry="${roleMemberAttributes.memberNamespaceCode}" readOnly="${!canModifyAssignees}" />
                  </c:if>  
                </div>
              </td>
              <c:if test='${KualiForm.member.memberTypeCode == "G" || KualiForm.member.memberTypeCode == "P"}'>
                <td class="infoline">   
                  <div align="center">                
                    <kul:htmlControlAttribute property="member.memberName" attributeEntry="${roleMemberAttributes.memberName}" readOnly="${!canModifyAssignees}" />
                    <c:if test="${canModifyAssignees}">
                      <kul:lookup boClassName="${bo}" fieldConversions="${fc}" anchor="${tabKey}" />
                    </c:if>
                  </div>    
                </td>
              </c:if>  
              <td class="infoline">   
                <div align="center">                
                    <kul:htmlControlAttribute property="member.memberFullName" attributeEntry="${roleMemberAttributes.memberFullName}" readOnly="true" />
                </div>
              </td>
              <td align="left" valign="middle" class="infoline">
                <div align="center">
                    <kul:htmlControlAttribute property="member.activeFromDate" attributeEntry="${roleMemberAttributes.activeFromDate}" datePicker="true" readOnly="${!canModifyAssignees}" />
                </div>
              </td>
              <td align="left" valign="middle" class="infoline">
                <div align="center">
                    <kul:htmlControlAttribute property="member.activeToDate" attributeEntry="${roleMemberAttributes.activeToDate}" datePicker="true" readOnly="${!canModifyAssignees}" />
                </div>
              </td>

              <c:forEach var="qualifier" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="statusQualifier">
                  <c:set var="fieldName" value="${qualifier.kimAttribute.attributeName}" />
                  <c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
                  <c:set var="attrDefinition" value="${KualiForm.document.definitionsKeyedByAttributeName[fieldName]}"/>
                  <td align="left" valign="middle">

                    <div align="center">

                      <kul:htmlControlAttribute property="member.qualifier(${qualifier.kimAttribute.id}).attrVal"  attributeEntry="${attrEntry}" readOnly="${!canModifyAssignees}" />

                      <c:if test="${canModifyAssignees}">
                        <c:forEach var="widget" items="${attrDefinition.attributeField.widgets}" >
                          <c:if test="${widget['class'].name == 'org.kuali.rice.core.api.uif.RemotableQuickFinder'}">
                            <c:if test="${!empty widget.dataObjectClass and not readOnlyAssignees}">
                              <kim:attributeLookup attributeDefinitions="${KualiForm.document.definitions}" pathPrefix="member" attr="${widget}" />
                            </c:if>
                          </c:if>
                        </c:forEach>
                      </c:if>

                      </div>

                  </td>
              </c:forEach>

              <td class="infoline">
                  <div align="center">
                      <c:choose>
                        <c:when test="${canModifyAssignees}">
                          <html:image property="methodToCall.addMember.anchor${tabKey}"
                            src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
                        </c:when>
                        <c:otherwise>
                            <html:image property="methodToCall.addMember.anchor${tabKey}"
                            src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton" disabled="true"/>
                        </c:otherwise>
                      </c:choose>
                  </div>
              </td>
           </tr> 
         </table> 
         <br />        
       </c:if>


    <c:if test="${(!empty KualiForm.document.modifiedMembers)}">
    <table cellpadding="0" cellspacing="0" summary="">
        <tr>
          <td colspan="100%" class="tab-subhead">Modified Members:</td>
        </tr>
        <tr>
          <th>&nbsp;<input type="hidden" id="sortMethodToCallPlaceholder" name="sortMethodToCallPlaceholder" value="placeholder"/></th>
          <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberTypeCode}" horizontal="false" />
          <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberId}" horizontal="false" />
          <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberNamespaceCode}" horizontal="false" />
          <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberName}" horizontal="false" />
          <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberFullName}" horizontal="false" />
          <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.activeFromDate}" horizontal="false" />
          <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.activeToDate}" horizontal="false" />
          <c:forEach var="attrDefn" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="status">
            <c:set var="fieldName" value="${attrDefn.kimAttribute.attributeName}" />
            <c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
            <kul:htmlAttributeHeaderCell attributeEntry="${attrEntry}" useShortLabel="false" />
          </c:forEach>
          <c:if test="${canModifyAssignees}">
            <kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
          </c:if>
        </tr>
          <c:forEach var="member" items="${KualiForm.document.modifiedMembers}" varStatus="statusMember">
            <c:set var="rows" value="2"/>
            <c:if test="${fn:length(member.roleRspActions) == 0}">
              <c:set var="rows" value="1"/>
            </c:if>
            <c:set var="inquiryClass" value="org.kuali.rice.kim.api.identity.Person" />
            <c:set var="keyValue" value="principalId" />
            <c:if test='${member.memberTypeCode == "G"}'>
              <c:set var="inquiryClass" value="org.kuali.rice.kim.impl.group.GroupBo" />
              <c:set var="keyValue" value="id" />
            </c:if>
            <c:if test='${member.memberTypeCode == "R"}'>
              <c:set var="inquiryClass" value="org.kuali.rice.kim.impl.role.RoleBo" />
              <c:set var="keyValue" value="id" />
            </c:if>

            <tr>
              <th rowspan="${rows}" class="infoline" valign="top">
                <c:out value="${statusMember.index+1}" />
              </th>
              <td align="left" valign="middle">
                <div align="center">
                  <html:link linkName="${KualiForm.document.modifiedMembers[statusMember.index].roleMemberId}" />
                  <kul:htmlControlAttribute property="document.modifiedMembers[${statusMember.index}].memberTypeCode"  attributeEntry="${roleMemberAttributes.memberTypeCode}" disabled="true" readOnly="false" />
                </div>
              </td>
              <td align="left" valign="middle">
                <div align="center"> <kul:htmlControlAttribute property="document.modifiedMembers[${statusMember.index}].memberId"  attributeEntry="${roleMemberAttributes.memberId}" readOnly="true" />
                </div>
              </td>
              <td align="left" valign="middle">
                <div align="center">
                  <kul:htmlControlAttribute property="document.modifiedMembers[${statusMember.index}].memberNamespaceCode"  attributeEntry="${roleMemberAttributes.memberNamespaceCode}" readOnly="true"  />
                </div>
              </td>
              <td align="left" valign="middle">
                <div align="center">
                  <kul:inquiry boClassName="${inquiryClass}" keyValues="${keyValue}=${member.memberId}" render="true">
                    <kul:htmlControlAttribute property="document.modifiedMembers[${statusMember.index}].memberName"  attributeEntry="${roleMemberAttributes.memberName}" readOnly="true"  />
                  </kul:inquiry>
                </div>
              </td>
              <td align="left" valign="middle">
                <div align="center">
                  <kul:inquiry boClassName="${inquiryClass}" keyValues="${keyValue}=${member.memberId}" render="true">
                    <kul:htmlControlAttribute property="document.modifiedMembers[${statusMember.index}].memberFullName"  attributeEntry="${roleMemberAttributes.memberFullName}" readOnly="true"  />
                  </kul:inquiry>
                </div>
              </td>
              <td align="left" valign="middle">
                <div align="center"> <kul:htmlControlAttribute property="document.modifiedMembers[${statusMember.index}].activeFromDate"  attributeEntry="${roleMemberAttributes.activeFromDate}" readOnly="${!canModifyAssignees}" datePicker="true" />
                </div>
              </td>
              <td align="left" valign="middle">
                <div align="center"> <kul:htmlControlAttribute property="document.modifiedMembers[${statusMember.index}].activeToDate"  attributeEntry="${roleMemberAttributes.activeToDate}" readOnly="${!canModifyAssignees}" datePicker="true" />
                </div>
              </td>
              <c:set var="numberOfColumns" value="${KualiForm.member.numberOfQualifiers+6}"/>
              <c:forEach var="qualifier" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="statusQualifier">
                <c:set var="fieldName" value="${qualifier.kimAttribute.attributeName}" />
                <c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
                <c:set var="attrDefinition" value="${KualiForm.document.definitionsKeyedByAttributeName[fieldName]}"/>
                <c:set var="attrReadOnly" value="${(!canModifyAssignees || member.edit)}"/>
                <c:set var="index" value="${statusMember.index}"/>
                <c:set var="kimAttrId" value="${qualifier.kimAttribute.id}"  />
                <td align="left" valign="middle">
                  <div align="center">
                    <c:if test="${kfunc:matchingQualifierExists(KualiForm.document.modifiedMembers,index,kimAttrId) }">

                      <kul:htmlControlAttribute property="document.modifiedMembers[${statusMember.index}].qualifier(${qualifier.kimAttribute.id}).attrVal"  attributeEntry="${attrEntry}" readOnly="${attrReadOnly}" />

                      <c:if test="${!attrReadOnly}">
                        <c:forEach var="widget" items="${attrDefinition.attributeField.widgets}" >
                          <c:if test="${widget['class'].name == 'org.kuali.rice.core.api.uif.RemotableQuickFinder'}">
                            <c:if test="${!empty widget.dataObjectClass and not readOnlyAssignees}">
                              <kim:attributeLookup attributeDefinitions="${KualiForm.document.definitions}" pathPrefix="document.modifiedMembers[${statusMember.index}]" attr="${widget}" />
                            </c:if>
                          </c:if>
                        </c:forEach>
                      </c:if>
                    </c:if>
                  </div>
                </td>
              </c:forEach>
              <c:if test="${canModifyAssignees}">
                <td>
                  <div align=center>&nbsp;
                    <html:image property='methodToCall.deleteMember.line${statusMember.index}.anchor${currentTabIndex}'
                                src='${ConfigProperties.kr.externalizable.images.url}tinybutton-inactivate.gif' styleClass='tinybutton'/>
                  </div>
                </td>
              </c:if>
            </tr>
            <c:if test="${fn:length(member.roleRspActions) != 0}">
              <tr>
                <td colspan="${numberOfColumns}" style="padding:0px;">                
                  <kim:respActionsForModRoleMbrs mbrIdx="${statusMember.index}" />
                </td>
              </tr>
            </c:if>
          </c:forEach>
      </table>
      <br/>
    </c:if>

    <c:if test="${(!empty memberSearchValue) && (empty KualiForm.document.searchResultMembers)}">
      <table cellpadding="0" cellspacing="0" summary="">
        <tr>
          <br />
          <b>No unmodified members have a member name starting with the given search criteria.  Criteria: ${memberSearchValue}</b>
          <br />
        </tr>
      </table>
      <br />
    </c:if>

    <c:if test="${(!empty memberSearchValue) && (!empty KualiForm.document.searchResultMembers)}">
    <table cellpadding="0" cellspacing="0" summary="">
      <tr>
        <td colspan="100%" class="tab-subhead">Members who have a name starting with ${memberSearchValue}:</td>
      </tr>
      <tr>
        <th>&nbsp;<input type="hidden" id="sortMethodToCallPlaceholder" name="sortMethodToCallPlaceholder" value="placeholder"/></th>
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberTypeCode}" horizontal="false" />
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberId}" horizontal="false" />
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberNamespaceCode}" horizontal="false"  />
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberName}" horizontal="false"  />
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberFullName}" horizontal="false" />
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.activeFromDate}" horizontal="false" />
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.activeToDate}" horizontal="false" />
        <c:forEach var="attrDefn" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="status">
          <c:set var="fieldName" value="${attrDefn.kimAttribute.attributeName}" />
          <c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
          <kul:htmlAttributeHeaderCell attributeEntry="${attrEntry}" useShortLabel="false" />
        </c:forEach>
        <c:if test="${canModifyAssignees}">
          <kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
        </c:if>
      </tr>
        <c:forEach var="member" items="${KualiForm.document.searchResultMembers}" varStatus="statusMember"
                   begin="0"
                   end="199">
          <c:set var="rows" value="2"/>
          <c:if test="${fn:length(member.roleRspActions) == 0}">
            <c:set var="rows" value="1"/>
          </c:if>
          <c:set var="inquiryClass" value="org.kuali.rice.kim.api.identity.Person" />
          <c:set var="keyValue" value="principalId" />
          <c:if test='${member.memberTypeCode == "G"}'>
            <c:set var="inquiryClass" value="org.kuali.rice.kim.impl.group.GroupBo" />
            <c:set var="keyValue" value="id" />
          </c:if>
          <c:if test='${member.memberTypeCode == "R"}'>
            <c:set var="inquiryClass" value="org.kuali.rice.kim.impl.role.RoleBo" />
            <c:set var="keyValue" value="id" />
          </c:if>

          <tr>
            <th rowspan="${rows}" class="infoline" valign="top">
              <c:out value="${statusMember.index+1}" />
            </th>
            <td align="left" valign="middle">
              <div align="center">
                <html:link linkName="${KualiForm.document.searchResultMembers[statusMember.index].roleMemberId}" />
                <kul:htmlControlAttribute property="document.searchResultMembers[${statusMember.index}].memberTypeCode"  attributeEntry="${roleMemberAttributes.memberTypeCode}" disabled="true" readOnly="false" />
              </div>
            </td>
            <td align="left" valign="middle">
              <div align="center"> <kul:htmlControlAttribute property="document.searchResultMembers[${statusMember.index}].memberId"  attributeEntry="${roleMemberAttributes.memberId}" readOnly="true" />
              </div>
            </td>
            <td align="left" valign="middle">
              <div align="center">
                <kul:htmlControlAttribute property="document.searchResultMembers[${statusMember.index}].memberNamespaceCode"  attributeEntry="${roleMemberAttributes.memberNamespaceCode}" readOnly="true"  />
              </div>
            </td>
            <td align="left" valign="middle">
              <div align="center">
                <kul:inquiry boClassName="${inquiryClass}" keyValues="${keyValue}=${member.memberId}" render="true">
                  <kul:htmlControlAttribute property="document.searchResultMembers[${statusMember.index}].memberName"  attributeEntry="${roleMemberAttributes.memberName}" readOnly="true"  />
                </kul:inquiry>
              </div>
            </td>
            <td align="left" valign="middle">
              <div align="center">
                <kul:inquiry boClassName="${inquiryClass}" keyValues="${keyValue}=${member.memberId}" render="true">
                  <kul:htmlControlAttribute property="document.searchResultMembers[${statusMember.index}].memberFullName"  attributeEntry="${roleMemberAttributes.memberFullName}" readOnly="true"  />
                </kul:inquiry>
              </div>
            </td>
            <td align="left" valign="middle">
              <div align="center"> <kul:htmlControlAttribute property="document.searchResultMembers[${statusMember.index}].activeFromDate"  attributeEntry="${roleMemberAttributes.activeFromDate}" readOnly="true" datePicker="true" />
              </div>
            </td>
            <td align="left" valign="middle">
              <div align="center"> <kul:htmlControlAttribute property="document.searchResultMembers[${statusMember.index}].activeToDate"  attributeEntry="${roleMemberAttributes.activeToDate}" readOnly="true" datePicker="true" />
              </div>
            </td>
            <c:set var="numberOfColumns" value="${KualiForm.member.numberOfQualifiers+6}"/>
            <c:forEach var="qualifier" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="statusQualifier">
              <c:set var="fieldName" value="${qualifier.kimAttribute.attributeName}" />
              <c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
              <c:set var="attrDefinition" value="${KualiForm.document.definitionsKeyedByAttributeName[fieldName]}"/>
              <c:set var="attrReadOnly" value="true"/>
              <c:set var="index" value="${statusMember.index}"/>
              <c:set var="kimAttrId" value="${qualifier.kimAttribute.id}"  />
              <td align="left" valign="middle">
                <div align="center">
                  <c:if test="${kfunc:matchingQualifierExists(KualiForm.document.searchResultMembers,index,kimAttrId) }">

                    <kul:htmlControlAttribute property="document.searchResultMembers[${statusMember.index}].qualifier(${qualifier.kimAttribute.id}).attrVal"  attributeEntry="${attrEntry}" readOnly="${attrReadOnly}" />

                    <c:if test="${!attrReadOnly}">
                      <c:forEach var="widget" items="${attrDefinition.attributeField.widgets}" >
                        <c:if test="${widget['class'].name == 'org.kuali.rice.core.api.uif.RemotableQuickFinder'}">
                          <c:if test="${!empty widget.dataObjectClass and not readOnlyAssignees}">
                            <kim:attributeLookup attributeDefinitions="${KualiForm.document.definitions}" pathPrefix="document.searchResultMembers[${statusMember.index}]" attr="${widget}" />
                          </c:if>
                        </c:if>
                      </c:forEach>
                    </c:if>
                  </c:if>
                </div>
              </td>
            </c:forEach>
            <c:if test="${canModifyAssignees}">
              <td>
                <div align=center>&nbsp;
                  <html:image property='methodToCall.editSearchResultsMember.line${statusMember.index}.anchor${currentTabIndex}'
                              src='${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif' styleClass='tinybutton'/>
                </div>
              </td>
            </c:if>
          </tr>
          <c:if test="${fn:length(member.roleRspActions) != 0}">
            <tr>
              <td colspan="${numberOfColumns}" style="padding:0px;">
                <kim:respActionsForSearchResultMbrs mbrIdx="${statusMember.index}" />
              </td>
            </tr>
          </c:if>
        </c:forEach>
    </table>
    </c:if>

    <c:if test="${fn:length(KualiForm.document.searchResultMembers) > 200}">
      <table cellpadding="0" cellspacing="0" summary="">
        <tr>
          <br />
          <b>More than 200 members matched the given search criteria. The first 200 members are shown above.  Criteria: ${memberSearchValue}</b>
          <br />
        </tr>
      </table>
    </c:if>

    <c:if test="${empty memberSearchValue && !empty KualiForm.document.members}">
    <table cellpadding="0" cellspacing="0" summary="">
      <tr>
        <td colspan="100%" class="tab-subhead">Members:</td>
      </tr>
      <tr>
        <th>&nbsp;<input type="hidden" id="sortMethodToCallPlaceholder" name="sortMethodToCallPlaceholder" value="placeholder"/></th>
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberTypeCode}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.memberTypeCode';submitForm();" />
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberId}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.memberId';submitForm();" />
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberNamespaceCode}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.memberNamespaceCode';submitForm();" />
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberName}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.memberName';submitForm();" />
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.memberFullName}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.memberFullName';submitForm();"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.activeFromDate}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.activeFromDate';submitForm();"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${roleMemberAttributes.activeToDate}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.activeToDate';submitForm();"/>
        <c:forEach var="attrDefn" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="status">
          <c:set var="fieldName" value="${attrDefn.kimAttribute.attributeName}" />
          <c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
          <kul:htmlAttributeHeaderCell attributeEntry="${attrEntry}" useShortLabel="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.${fieldName}';submitForm();"/>
        </c:forEach>
        <c:if test="${canModifyAssignees}"> 
          <kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
        </c:if> 
      </tr>
      <c:if test="${KualiForm.memberTableMetadata.firstRowIndex >= 0}">
        <c:forEach var="member" items="${KualiForm.document.members}" varStatus="statusMember"
                   begin="${KualiForm.memberTableMetadata.firstRowIndex}"
                   end="${KualiForm.memberTableMetadata.lastRowIndex}">
            <c:set var="rows" value="2"/>
            <c:if test="${fn:length(member.roleRspActions) == 0}">  
                   <c:set var="rows" value="1"/>
            </c:if> 
            <c:set var="inquiryClass" value="org.kuali.rice.kim.api.identity.Person" />
            <c:set var="keyValue" value="principalId" />
            <c:if test='${member.memberTypeCode == "G"}'>
            	<c:set var="inquiryClass" value="org.kuali.rice.kim.impl.group.GroupBo" />
            	<c:set var="keyValue" value="id" />
            </c:if>
            <c:if test='${member.memberTypeCode == "R"}'>
            	<c:set var="inquiryClass" value="org.kuali.rice.kim.impl.role.RoleBo" />
            	<c:set var="keyValue" value="id" />
            </c:if>

              <tr>
                <th rowspan="${rows}" class="infoline" valign="top">
                    <c:out value="${statusMember.index+1}" />
                </th>
                <td align="left" valign="middle">
                    <div align="center"> 
                        <html:link linkName="${KualiForm.document.members[statusMember.index].roleMemberId}" />
                        <kul:htmlControlAttribute property="document.members[${statusMember.index}].memberTypeCode"  attributeEntry="${roleMemberAttributes.memberTypeCode}" disabled="true" readOnly="true" />
                    </div>
                </td>
                <td align="left" valign="middle">
                    <div align="center"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].memberId"  attributeEntry="${roleMemberAttributes.memberId}" readOnly="true" />
                    </div>
                </td>
                <td align="left" valign="middle">
                    <div align="center"> 
                    	<kul:htmlControlAttribute property="document.members[${statusMember.index}].memberNamespaceCode"  attributeEntry="${roleMemberAttributes.memberNamespaceCode}" readOnly="true"  /> 	
                    </div>
                </td>
                <td align="left" valign="middle">				
                	<div align="center">
                    	<kul:inquiry boClassName="${inquiryClass}" keyValues="${keyValue}=${member.memberId}" render="true">
                        	<kul:htmlControlAttribute property="document.members[${statusMember.index}].memberName"  attributeEntry="${roleMemberAttributes.memberName}" readOnly="true"  />
	                    </kul:inquiry>  
                    </div>
                </td>
                <td align="left" valign="middle">
                	<div align="center">               
                    	<kul:inquiry boClassName="${inquiryClass}" keyValues="${keyValue}=${member.memberId}" render="true">
<%--                         		<a href="javascript:document.forms[0].sortMethodToCallPlaceholder=methodToCall.sort.memberFullName${roleMemberAttributes.memberFullName[fieldName]}"></a> --%>
                        		<kul:htmlControlAttribute property="document.members[${statusMember.index}].memberFullName"  attributeEntry="${roleMemberAttributes.memberFullName}" readOnly="true"  />
                    	</kul:inquiry>
					</div>                   
                </td>
                <td align="left" valign="middle">
                    <div align="center"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].activeFromDate"  attributeEntry="${roleMemberAttributes.activeFromDate}" readOnly="true" datePicker="true" />
                    </div>
                </td>
                <td align="left" valign="middle">
                    <div align="center"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].activeToDate"  attributeEntry="${roleMemberAttributes.activeToDate}" readOnly="true" datePicker="true" />
                    </div>
                </td>
                <c:set var="numberOfColumns" value="${KualiForm.member.numberOfQualifiers+6}"/>
                <c:forEach var="qualifier" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="statusQualifier">
                    <c:set var="fieldName" value="${qualifier.kimAttribute.attributeName}" />
                    <c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}" />
                    <c:set var="attrDefinition" value="${KualiForm.document.definitionsKeyedByAttributeName[fieldName]}"/>
                    <c:set var="attrReadOnly" value="true"/>
                    <c:set var="index" value="${statusMember.index}"/>
                    <c:set var="kimAttrId" value="${qualifier.kimAttribute.id}"  />
                  <td align="left" valign="middle">
                    <div align="center">
                      <c:if test="${kfunc:matchingQualifierExists(KualiForm.document.members,index,kimAttrId) }">

                        <kul:htmlControlAttribute property="document.members[${statusMember.index}].qualifier(${qualifier.kimAttribute.id}).attrVal"  attributeEntry="${attrEntry}" readOnly="${attrReadOnly}" />

                        <c:if test="${!attrReadOnly}">
                          <c:forEach var="widget" items="${attrDefinition.attributeField.widgets}" >
                            <c:if test="${widget['class'].name == 'org.kuali.rice.core.api.uif.RemotableQuickFinder'}">
                              <c:if test="${!empty widget.dataObjectClass and not readOnlyAssignees}">
                                <kim:attributeLookup attributeDefinitions="${KualiForm.document.definitions}" pathPrefix="document.members[${statusMember.index}]" attr="${widget}" />
                              </c:if>
                            </c:if>
                          </c:forEach>
                        </c:if>
                      </c:if>
                    </div>
                  </td>
                </c:forEach>
            <c:if test="${canModifyAssignees}">
              <td>
                <div align=center>&nbsp;
                    <html:image property='methodToCall.editMember.line${statusMember.index}.anchor${currentTabIndex}'
                    src='${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif' styleClass='tinybutton'/>
                </div>
              </td>
            </c:if>
            </tr>
            <c:if test="${fn:length(member.roleRspActions) != 0}">  
                    <tr>
                  <td colspan="${numberOfColumns}" style="padding:0px;">
                    <kim:responsibilityActions mbrIdx="${statusMember.index}" />
                  </td>
                </tr>
            </c:if>  
        </c:forEach>
    </c:if>
    </table>
    </c:if>
    </div>
</kul:tab>
</c:if>
