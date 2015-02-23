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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp" %>

<c:set var="groupMemberAttributes" value="${DataDictionary.GroupDocumentMember.attributes}"/>
<c:set var="groupQualifierAttributes" value="${DataDictionary.GroupDocumentQualifier.attributes}"/>
<c:set var="kimAttributes" value="${DataDictionary.KimAttributeImpl.attributes}"/>

<c:if test="${!empty KualiForm.document.kimType.attributeDefinitions}">
  <kul:tab tabTitle="Attributes" defaultOpen="true" tabErrorKey="document.qualifier*">
    <div class="tab-container" align="center">
      <table cellpadding="0" cellspacing="0" summary="">
        <tr>
          <c:forEach var="attrDefn" items="${KualiForm.document.kimType.attributeDefinitions}" varStatus="status">
            <c:set var="fieldName" value="${attrDefn.kimAttribute.attributeName}"/>
            <c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${attrEntry}" useShortLabel="false" horizontal="false"/>
          </c:forEach>
        </tr>
        <tr>
          <c:forEach var="qualifier" items="${KualiForm.document.kimType.attributeDefinitions}"
                     varStatus="statusQualifier">
            <c:set var="fieldName" value="${qualifier.kimAttribute.attributeName}"/>
            <c:set var="attrEntry" value="${KualiForm.document.attributeEntry[fieldName]}"/>
            <c:set var="attrDefinition" value="${KualiForm.document.definitionsKeyedByAttributeName[fieldName]}"/>
            <c:set var="attrReadOnly" value="${(readOnly || (attrDefinition.unique && KualiForm.document.editing))}"/>
            <c:set var="attrReadOnly" value="${(readOnly || (attrDefinition.unique && KualiForm.document.editing))}"/>
            <td align="left" valign="middle">
              <div align="center">
                <c:if test="${not empty document.qualifiers[statusQualifier.index]}">
                  <kul:htmlControlAttribute property="document.qualifiers[${statusQualifier.index}].attrVal"
                                            attributeEntry="${attrEntry}" readOnly="${attrReadOnly}"/>
                </c:if>
                <c:forEach var="widget" items="${attrDefinition.attributeField.widgets}">
                  <c:if test="${widget['class'].name == 'org.kuali.rice.core.api.uif.RemotableQuickFinder'}">
                    <c:if test="${!empty widget.dataObjectClass and not attrReadOnly}">
                      <kim:attributeLookup attributeDefinitions="${KualiForm.document.definitions}"
                                           pathPrefix="document" attr="${widget}"/>
                    </c:if>
                  </c:if>
                </c:forEach>
              </div>
            </td>
          </c:forEach>
        </tr>
      </table>
    </div>
  </kul:tab>
</c:if>
