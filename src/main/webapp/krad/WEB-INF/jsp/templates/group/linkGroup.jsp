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

<tiles:useAttribute name="group" classname="org.kuali.rice.krad.uif.container.LinkGroup"/>

<krad:group group="${group}">

  <c:choose>
    <c:when test="${empty group.items}">
        ${group.emptyLinkGroupString}
    </c:when>
    <c:otherwise>
      <c:if test="${!empty group.groupBeginDelimiter}">
        ${group.groupBeginDelimiter}
      </c:if>

      <c:forEach items="${group.items}" var="item" varStatus="itemVarStatus">
        <krad:template component="${item}"/>

        <c:if test="${!empty group.linkSeparator and !itemVarStatus.last}">
          ${group.linkSeparator}
        </c:if>
      </c:forEach>

      <c:if test="${!empty group.groupEndDelimiter}">
        ${group.groupEndDelimiter}
      </c:if>
    </c:otherwise>
  </c:choose>

</krad:group>