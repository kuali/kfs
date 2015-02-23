<%--

    Copyright 2005-2014 The Kuali Foundation

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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<tiles:useAttribute name="field" classname="org.kuali.rice.krad.uif.field.DataField"/>

<c:set var="readOnly" value="${field.readOnly || !field.inputAllowed}"/>

<krad:span component="${field}">

  <krad:fieldLabel field="${field}">

    <%-- render field value (if read-only) or control (if edit) --%>
    <c:choose>
      <c:when test="${readOnly}">
        <c:set var="readOnlyDisplay">
          <%-- display alternate display value if set --%>
          <c:if test="${not empty field.alternateDisplayValue}">
            ${field.alternateDisplayValue}
          </c:if>

          <c:if test="${empty field.alternateDisplayValue}">
            <%-- display actual field value --%>
            <s:bind path="${field.bindingInfo.bindingPath}"
                    htmlEscape="${field.escapeHtmlInPropertyValue}">${status.value}</s:bind>

            <%-- add alternate display value if set --%>
            <c:if test="${not empty field.additionalDisplayValue}">
              *-* ${field.additionalDisplayValue}
            </c:if>
          </c:if>
        </c:set>

        <span id="${field.id}">
          <%-- render inquiry if enabled --%>
          <c:if test="${field.fieldInquiry.render}">
            <krad:template component="${field.fieldInquiry}" componentId="${field.id}" body="${readOnlyDisplay}"/>
          </c:if>

          <c:if test="${!field.fieldInquiry.render}">
            ${readOnlyDisplay}
          </c:if>
        </span>
      </c:when>

      <c:otherwise>
        <%-- render field instructional text --%>
        <krad:template component="${field.instructionalMessageField}"/>

        <%-- render control for input --%>
        <krad:template component="${field.control}" field="${field}"/>
      </c:otherwise>
    </c:choose>

    <%-- render field quickfinder --%>
    <c:if test="${field.inputAllowed}">
      <krad:template component="${field.fieldLookup}" componentId="${field.id}"/>
    </c:if>

    <%-- render field direct inquiry if field is editable --%>
    <c:if test="${!readOnly && field.fieldDirectInquiry.render}">
      <krad:template component="${field.fieldDirectInquiry}" componentId="${field.id}"/>
    </c:if>

  </krad:fieldLabel>

  <!-- placeholder for dynamic field markers -->
  <span id="${field.id}_markers"></span>

  <%-- render field constraint if field is editable --%>
  <c:if test="${!readOnly}">
    <krad:template component="${field.constraintMessageField}"/>
  </c:if>

  <%-- render span and values for informational properties --%>
  <span id="${field.id}_info_message"></span>
  <c:forEach items="${field.informationalDisplayPropertyNames}" var="infoPropertyPath" varStatus="status">
    <%-- TODO: clean this up somehow! --%>
    <c:set var="infoPropertyId" value="${fn:replace(infoPropertyPath,'.','_')}"/>
    <c:set var="infoPropertyId" value="${fn:replace(infoPropertyId,'[','-lbrak-')}"/>
    <c:set var="infoPropertyId" value="${fn:replace(infoPropertyId,']','-rbrak-')}"/>
    <c:set var="infoPropertyId" value="${fn:replace(infoPropertyId,'\\\'','-quot-')}"/>
     <span id="${field.id}_info_${infoPropertyId}" class="uif-informationalMessage">
        <s:bind path="${infoPropertyPath}">${status.value}</s:bind>
     </span>
  </c:forEach>

  <%-- render field help --%>

  <%-- render field suggest if field is editable --%>
  <c:if test="${!readOnly}">
    <krad:template component="${field.fieldSuggest}" parent="${field}"/>
  </c:if>

  <%-- render hidden fields --%>
  <%-- TODO: always render hiddens if configured? --%>
  <c:forEach items="${field.hiddenPropertyNames}" var="hiddenPropertyName" varStatus="status">
    <form:hidden id="${field.id}_h${status.count}" path="${hiddenPropertyName}"/>
  </c:forEach>
</krad:span>

<%-- transform all text on attribute field to uppercase --%>
<c:if test="${!readOnly && field.performUppercase}">
  <krad:script value="jq('#${field.control.id}').css('text-transform', 'uppercase');"/>
</c:if>

<%-- render error container for field --%>
<c:if test="${!readOnly && ((empty field.errorsField.alternateContainer) || (!field.errorsField.alternateContainer))}">
  <krad:template component="${field.errorsField}"/>
</c:if>
