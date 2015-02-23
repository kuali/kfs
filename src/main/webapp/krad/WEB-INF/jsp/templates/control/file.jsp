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

<tiles:useAttribute name="control" classname="org.kuali.rice.krad.uif.control.FileControl"/>
<tiles:useAttribute name="field" classname="org.kuali.rice.krad.uif.field.InputField"/>

<form:input id="${field.id}" path="${field.bindingInfo.bindingPath}" type="file" disabled="${control.disabled}"
            size="${control.size}" class="${control.styleClassesAsString}" tabIndex="${control.tabIndex}"/>
 
