<%--
 Copyright 2007 The Kuali Foundation
 
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

<%@ attribute name="node" required="true"
              description="Node instance in the tree to be rendered"
              type="org.kuali.rice.core.api.util.tree.Node"%>

<%-- this tag is part of the workaround for KULRICE-5161 --%>

<c:set var="_node" value="${node}" scope="request" />
<c:import url="/WEB-INF/jsp/recurseTreeNode.jsp" />
