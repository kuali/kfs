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

<%@ attribute name="fragment" required="true" 
              description="The JSP fragment code that should be evaluated and returned" 
              fragment="true"%>
              
<%@ variable name-given="bufferOut" scope="AT_END" %>
                           
<%-- Evaluates the given fragments and places the result into a variable that can
then be read by the calling template. This gives a mechanism for templates to buffer JSP
code --%>  

<jsp:invoke var="bufferOut" fragment="fragment" />
