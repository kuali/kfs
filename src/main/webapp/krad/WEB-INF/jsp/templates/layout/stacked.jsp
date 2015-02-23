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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<tiles:useAttribute name="items" classname="java.util.List"/>
<tiles:useAttribute name="manager" classname="org.kuali.rice.krad.uif.layout.StackedLayoutManager"/>
<tiles:useAttribute name="container" classname="org.kuali.rice.krad.uif.container.ContainerBase"/>

<%--
    Stacked Layout Manager:

 --%>

<c:if test="${!empty manager.styleClassesAsString}">
  <c:set var="styleClass" value="class=\"${manager.styleClassesAsString}\""/>
</c:if>

<c:if test="${!empty manager.style}">
  <c:set var="style" value="style=\"${manager.style}\""/>
</c:if>

<c:set var="itemSpanClasses" value="class=\"fieldLine boxLayoutVerticalItem clearfix\""/>

<c:if test="${container.fieldContainer}">
  <c:set var="fieldItemsStyle" value="style=\"float:left;\""/>
  <c:set var="itemSpanClasses" value="class=\"fieldContainerVerticalItem clearfix\""/>
</c:if>

<div id="${manager.id}" ${style} ${styleClass}>
  <span ${fieldItemsStyle}>
    <c:choose>
      <c:when test="${manager.wrapperGroup != null}">
         <%-- render Group --%>
        <krad:template component="${manager.wrapperGroup}"/>
      </c:when>
      <c:otherwise>
        <%-- render items --%>
        <c:forEach items="${manager.stackedGroups}" var="item" varStatus="itemVarStatus">
          <span ${itemSpanClasses}>
            <krad:template component="${item}"/>
          </span>
       </c:forEach>
      </c:otherwise>
     </c:choose>
  </span>
</div>