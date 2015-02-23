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

<tiles:useAttribute name="control" classname="org.kuali.rice.krad.uif.control.SpinnerControl "/>
<tiles:useAttribute name="field" classname="org.kuali.rice.krad.uif.field.InputField"/>

<%--
    Create Standard HTML Text Input then decorates with Spinner plugin

 --%>

<tiles:insertTemplate template="/krad/WEB-INF/jsp/templates/control/text.jsp">
  <tiles:putAttribute name="control" value="${field.control}"/>
  <tiles:putAttribute name="field" value="${field}"/>
</tiles:insertTemplate>

<krad:script value="createSpinner('${control.id}', ${control.spinner.componentOptionsJSString});" />

 