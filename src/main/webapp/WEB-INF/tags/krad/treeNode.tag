<%--
 Copyright 2005-2007 The Kuali Foundation

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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="node" required="true"
              description="Node instance in the tree to be rendered"
              type="org.kuali.rice.core.api.util.tree.Node"%>

<li id="${node.data.id}" class="${node.nodeType}">
   <a href="#" class="${node.nodeType}">
       <krad:template component="${node.nodeLabel}"/>
   </a>
   <krad:template component="${node.data}" />
   <c:if test="${(node.children != null) and (fn:length(node.children) > 0)}">
   <ul>
      <c:forEach items="${node.children}" var="childNode" varStatus="itemVarStatus">
         <%-- ran into recursive tag problem on Linux, see KULRICE-5161 --%>
         <%-- krad:treeNode node="${childNode}" / --%>
         <%@ taglib tagdir="/WEB-INF/tags/krad" prefix="kul2"%>
         <kul2:containerTreeNode node="${childNode}"/>
      </c:forEach>
   </ul>
   </c:if>

</li>

