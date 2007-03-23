<%--
 Copyright 2006 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld" %>

<%@ attribute name="accountingLine" required="true"
              description="The name in the form of the accounting line." %>
<%@ attribute name="hiddenField" required="true"
              description="the name of an accounting line field
              to be put in a hidden form field by this tag." %>
<%@ attribute name="displayHidden" required="false"
              description="display hidden values (for debugging).
              This information is also available from the Firefox Web Developer extension,
              but that includes more detail and requires even more horizontal space." %>
<%@ attribute name="isBaseline" required="false"
              description="if displayed, distinguish baseline values
              from normal values by background color." %>
<%@ attribute name="value" required="false"
              description="sets the hidden field to this value" %>

<c:if test="${displayHidden}">
    <span style="background: ${isBaseline ? 'blue' : 'green'}">
        <c:out value="${hiddenField}"/> =</c:if
><c:choose
    ><c:when test="${empty value}"
        ><html:hidden write="${displayHidden}" property="${accountingLine}.${hiddenField}"
    /></c:when
    ><c:otherwise
        ><html:hidden write="${displayHidden}" property="${accountingLine}.${hiddenField}" value="${value}"
    /></c:otherwise
></c:choose><c:if test="${displayHidden}">;<br/>
    </span>
</c:if>