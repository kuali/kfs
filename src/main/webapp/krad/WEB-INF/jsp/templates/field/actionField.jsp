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

<tiles:useAttribute name="field"
                    classname="org.kuali.rice.krad.uif.field.ActionField"/>

<%--
    Standard HTML Input Submit - will create an input of type submit or type image if the action
    image field is configured
    
 --%>
<c:if test="${field.skipInTabOrder}">
  <c:set var="tabindex" value="tabindex=-1"/>
</c:if>

<c:if test="${field.actionImage != null}">
  <c:if test="${not empty field.actionImage.height}">
    <c:set var="height" value="height='${field.actionImage.height}'"/>
  </c:if>
  <c:if test="${not empty field.actionImage.width}">
    <c:set var="width" value="width='${field.actionImage.width}'"/>
  </c:if>
</c:if>

<c:if test="${field.disabled}">
  <c:set var="disabled" value="disabled=\"true\""/>
</c:if>

<c:choose>

  <c:when test="${(field.actionImage != null) && field.actionImage.render && (empty field.actionImageLocation || field.actionImageLocation eq 'IMAGE_ONLY')}">
    <krad:attributeBuilder component="${field.actionImage}"/>

    <span id="${field.id}_span">
      <krad:fieldLabel field="${field}">

        <input type="image" id="${field.id}" ${disabled}
               src="${field.actionImage.source}"
               alt="${field.actionImage.altText}" ${height} ${width} ${style} ${styleClass} ${title} ${tabindex} />

      </krad:fieldLabel>
    </span>
  </c:when>
  <c:otherwise>
    <krad:attributeBuilder component="${field}"/>

    <c:choose>
      <c:when test="${not empty field.actionImageLocation && (field.actionImage != null) && field.actionImage.render}">

        <c:choose>
          <c:when test="${(field.actionImageLocation eq 'TOP')}">
            <button id="${field.id}" ${style} ${styleClass} ${title} ${disabled}><span
                    class="topBottomSpan"><img ${height} ${width}
                    class="actionImage topActionImage ${field.actionImage.styleClassesAsString}"
                    style="${field.actionImage.style}"
                    src="${field.actionImage.source}"
                    alt="${field.actionImage.altText}"/></span>${field.actionLabel}
            </button>
          </c:when>
          <c:when test="${(field.actionImageLocation eq 'BOTTOM')}">
            <button id="${field.id}" ${style} ${styleClass} ${title} ${disabled}>${field.actionLabel}<span
                    class="topBottomSpan"><img ${height} ${width}
                    style="${field.actionImage.style}"
                    class="actionImage bottomActionImage ${field.actionImage.styleClassesAsString}"
                    src="${field.actionImage.source}"
                    alt="${field.actionImage.altText}"/></span></button>
          </c:when>
          <c:when test="${(field.actionImageLocation eq 'RIGHT')}">
            <button id="${field.id}" ${style} ${styleClass} ${title} ${disabled}>${field.actionLabel}<img ${height} ${width}
                    style="${field.actionImage.style}"
                    class="actionImage rightActionImage ${field.actionImage.styleClassesAsString}"
                    src="${field.actionImage.source}" alt="${field.actionImage.altText}"/></button>
          </c:when>
          <c:when test="${(field.actionImageLocation eq 'LEFT')}">
            <button id="${field.id}" ${style} ${styleClass} ${title} ${disabled}><img ${height} ${width}
                    style="${field.actionImage.style}"
                    class="actionImage leftActionImage ${field.actionImage.styleClassesAsString}"
                    src="${field.actionImage.source}"
                    alt="${field.actionImage.altText}"/>${field.actionLabel}
            </button>
          </c:when>
          <c:otherwise>
            <button id="${field.id}" ${style} ${styleClass} ${title} ${disabled}>${field.actionLabel}</button>
          </c:otherwise>
        </c:choose>
      </c:when>
      <c:otherwise>
        <button id="${field.id}" ${style} ${styleClass} ${title} ${disabled}>${field.actionLabel}</button>
      </c:otherwise>
    </c:choose>

  </c:otherwise>
</c:choose>

<c:if test="${(field.lightBoxLookup != null)}">
  <krad:template component="${field.lightBoxLookup}" componentId="${field.id}"/>
</c:if>
