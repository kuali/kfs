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

<tiles:useAttribute name="group" classname="org.kuali.rice.krad.uif.container.NavigationGroup"/>
<tiles:useAttribute name="currentPageId"/>

<%-- renders standard unordered list and calls doNavigation function --%>

<!----------------------------------- #NAVIGATION --------------------------------------->
<krad:div component="${group}">
  <%-- render items in list --%>
  <ul id="${group.id}" role="navigation">
    <c:forEach items="${group.items}" var="item" varStatus="itemVarStatus">
      <li>
         <krad:template component="${item}"/>
      </li>
    </c:forEach>
  </ul>
</krad:div>

<krad:script value="
  var options = ${group.componentOptionsJSString};
  options.currentPage = '${currentPageId}';
  createNavigation('${group.id}', '${group.navigationType}', options);
"/>