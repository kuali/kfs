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

<%@ attribute name="group" required="true"
              description="The group instance that is being rendered"
              type="org.kuali.rice.krad.uif.container.Group"%>
<%@ attribute name="groupBodyIdSuffix" required="false"
              description="String to append to the div id for the group body" %>

<c:if test="${empty groupBodyIdSuffix}">
    <c:set var="groupBodyIdSuffix" value="_group"/>
</c:if>

<%-- Standard wrapper for group templates --%>
<krad:div component="${group}">

  <!----------------------------------- #GROUP '${group.id}' HEADER --------------------------------------->
  <c:if test="${!empty group.header}">
    <krad:template component="${group.header}"/>
  </c:if>

  <div id="${group.id}${groupBodyIdSuffix}">
    <krad:template component="${group.instructionalMessageField}"/>
    <krad:template component="${group.errorsField}"/>

    <jsp:doBody/>

    <!----------------------------------- #GROUP '${group.id}' FOOTER --------------------------------------->
    <c:if test="${!empty group.footer}">
      <krad:template component="${group.footer}"/>
    </c:if>
  </div>

</krad:div>

<%-- render group disclosure --%>
<krad:template component="${group.disclosure}" parent="${group}"/>