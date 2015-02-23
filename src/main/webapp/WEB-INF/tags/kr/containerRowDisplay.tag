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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="rows" required="true" type="java.util.List"
              description="The rows of fields that we'll iterate to display." %>
<%@ attribute name="numberOfColumns" required="false" 
              description="The # of fields in this row." %>
<%@ attribute name="depth" required="false" 
              description="the recursion depth number" %>
<%@ attribute name="rowsHidden" required="false"
              description="boolean that indicates whether the rows should be hidden or all fields are hidden" %>
<%@ attribute name="rowsReadOnly" required="false"
              description="boolean that indicates whether the rows should be rendered as read-only (note that rows will automatically be rendered as readonly if it is an inquiry or if it is a maintenance document in readOnly mode" %>             

<%-- Tomcat 5.5.30 and 5.5.31 had problems with the recursive tag, this is the workaround: --%>
<c:set var="_rows" value="${rows}" scope="request" />
<c:set var="_numberOfColumns" value="${numberOfColumns}" scope="request" />
<c:set var="_depth" value="${depth}" scope="request" />
<c:set var="_rowsHidden" value="${rowsHidden}" scope="request" />
<c:set var="_rowsReadOnly" value="${rowsReadOnly}" scope="request" />
<c:import url="/WEB-INF/jsp/recurseRowDisplay.jsp" />
