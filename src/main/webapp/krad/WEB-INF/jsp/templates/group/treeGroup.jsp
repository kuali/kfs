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

<tiles:useAttribute name="group" classname="org.kuali.rice.krad.uif.container.TreeGroup"/>

<krad:group group="${group}">
  <div id="${group.id}_tree">
	<ul>
      <c:forEach items="${group.treeGroups.rootElement.children}" var="node" varStatus="itemVarStatus">
         <krad:treeNode node="${node}" />
      </c:forEach>
	</ul>
  </div>

  <%-- invoke tree widget --%>
  <krad:template component="${group.tree}" componentId="${group.id}_tree"/>

</krad:group>