<%--
 Copyright 2005-2009 The Kuali Foundation
 
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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="transactionalDocument" required="true" %>
<%@ attribute name="saveButtonOverride" required="false" %>
<%@ attribute name="suppressRoutingControls" required="false" %>
<%@ attribute name="extraButtonSource" required="false" %>
<%@ attribute name="extraButtonProperty" required="false" %>
<%@ attribute name="extraButtonAlt" required="false" %>
<%@ attribute name="extraButtons" required="false" type="java.util.List" %>
<%@ attribute name="viewOnly" required="false" %>
<c:set var="documentTypeName" value="${KualiForm.docTypeName}" />
<c:set var="documentEntry" value="${DataDictionary[documentTypeName]}" />

<kul:documentControls
transactionalDocument="${transactionalDocument}"
saveButtonOverride="${saveButtonOverride}"
suppressRoutingControls="${suppressRoutingControls}"
extraButtonSource="${extraButtonSource}"
extraButtonProperty="${extraButtonProperty}"
extraButtonAlt="${extraButtonAlt}"
extraButtons="${extraButtons}"
viewOnly="${viewOnly}"
/>
