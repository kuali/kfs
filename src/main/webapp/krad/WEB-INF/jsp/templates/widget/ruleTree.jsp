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

<tiles:useAttribute name="widget" classname="org.kuali.rice.krad.uif.widget.Tree"/>
<tiles:useAttribute name="componentId"/>
<%--
    TODO:
    TODO:
    TODO: This should be somewhere else.  In a KRMS web module?
    TODO:
    TODO:
--%>

<%--
    Invokes JS method to implement a tree plug-in
 --%>

<!-- keep track of the agenda item that is selected:
<input type="hidden" name="selected_prop" value=""/>
-->
<krad:script value="initRuleTree('${componentId}');"/>
