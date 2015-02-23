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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp" %>
<%--
  ~ Copyright 2006-2012 The Kuali Foundation
  ~
  ~ Licensed under the Educational Community License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.opensource.org/licenses/ecl2.php
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<html>
<head>
     <link href="css/kuali.css" rel="stylesheet" type="text/css">
     <link href="css/screen.css" rel="stylesheet" type="text/css">
     <script language="JavaScript" src="scripts/en-common.js"></script>
     <title>Document Content Operation</title>
</head>
<body bgcolor="#ffffff" marginheight="0" marginwidth="0" topmargin="0" leftmargin="0">
    <table width="100%" border=0 cellpadding="0" cellspacing="0" class="headercell1">
    <tr>
        <td width="100%"><img src="images/wf-logo.gif" alt="Workflow" width="150" height="21" hspace="5" vspace="5"></td>
    </tr>
    </table>
    <html:form action="/DocumentContentOperation.do" method="post">
    <table border="0" cellpadding="0" cellspacing="8">
    <tr>
        <td style="text-align: center; font-size: large; font-weight: bold; padding: 5px 0;">Document Content Encryption</td>
    </tr>
    <tr>
    	<td>
    		Encyrption key: <html-el:text property="key" value=""/>
    	</td>
    </tr>
    <tr>
    	<td>
    		Document Id: <html-el:text property="documentId" value=""/>
    	</td>
    </tr>
    <tr>
    	<td>
    		<html-el:radio property="methodToCall" value="encryptContent"/> Encyrpt Content<br />
    		<html-el:radio property="methodToCall" value="decryptContent"/> Decrypt Content
    	</td>
    </tr>
    <tr>
    	<td colspace="2">
    		<input type="image" src="images/buttonsmall_submit.gif" align="absmiddle" />
    	</td>
    </tr>
    </table>
    </html:form>
</body>
</html>