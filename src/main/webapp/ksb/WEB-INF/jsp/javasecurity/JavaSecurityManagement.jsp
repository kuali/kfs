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
<%@ taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@ taglib uri="http://www.kuali.org/struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-logic-el" prefix="logic-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html-el:html>
<head>
<title>Message Queue</title>
<style type="text/css">
   .highlightrow {}
   tr.highlightrow:hover, tr.over td { background-color: #66FFFF; }
</style>
<link href="css/screen.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="scripts/en-common.js"></script>
<script language="JavaScript" src="scripts/messagequeue-common.js"></script>
</head>

<body>

<table width="100%" border=0 cellpadding=0 cellspacing=0 class="headercell1">
  <tr>
    <td width="15%"><img src="images/wf-logo.gif" alt="Workflow" width=150 height=21 hspace=5 vspace=5></td>
    <td width="85%"><a href="JavaSecurityManagement.do?methodToCall=start">Refresh Page</a></td>
    <td>&nbsp;&nbsp;</td>
  </tr>
</table>


<html-el:form action="/JavaSecurityManagement.do">

<table width="100%" border=0 cellspacing=0 cellpadding=0>
  <tr>
    <td width="20" height="20">&nbsp;</td>
  	<td>

      <br>
  	  <jsp:include page="../Messages.jsp"/>
      <br>
      <br>
      <strong>Create new Client Keystore file:</strong>
      <br><br>
	  <table border="0" cellpadding="2" cellspacing="0" class="bord-r-t">
		<tr>
		  <td class="thnormal" align="right" valign="middle" nowrap="nowrap">
		    Desired Alias (must be unique from list below):
		  </td>
		  <td class="datacell" align="right" valign="middle" nowrap="nowrap">
		    <html-el:text property="alias"/>
		  </td>
		</tr>
		<tr>
		  <td class="thnormal" align="right" valign="middle" nowrap="nowrap">
		    Password (will be used for keys as well as keystore):
		  </td>
		  <td class="datacell" align="right" valign="middle" nowrap="nowrap">
		    <html-el:password property="password"/>
		  </td>
		</tr>
		<tr>
		  <td class="thnormal" align="right" valign="middle" nowrap="nowrap">
		    Re-Enter Password:
		  </td>
		  <td class="datacell" align="right" valign="middle" nowrap="nowrap">
		    <html-el:password property="passwordVerify"/>
		  </td>
		</tr>
        <tr>
          <td height="28" colspan="2" valign="top" class="thnormal">
            <div align="center">
              <html-el:image property="methodToCall.generateClientKeyStore" src="images/buttonsmall_create.gif" align="absmiddle" />&nbsp;&nbsp;&nbsp;&nbsp;
              <html-el:image property="methodToCall.clear" src="images/buttonsmall_clearfields.gif" align="absmiddle" />
            </div>
          </td>
        </tr>
      </table>
  	</td>
  	<td width="20" height="20">&nbsp;</td>
  </tr>
  <tr>
  	<td colspan="3">&nbsp;</td>
  </tr>
  <tr>
    <td width="20" height="20">&nbsp;</td>
    <td height="30">
       <strong>Existing Keystore Entries:</strong>
  	   <c:if test="${empty keyStoreEntryList}">
         &nbsp;&nbsp;None.
  	   </c:if>
    </td>
    <td width="20" height="20">&nbsp;</td>
  </tr>
</table>
</html-el:form>
<table width="80%" border=0 cellspacing=0 cellpadding=0>
  <c:if test="${not empty keyStoreEntryList}">
  <tr>
    <td width="20" height="20">&nbsp;</td>
    <td>
		  <display:table excludedParams="*" pagesize="20" class="bord-r-t" style="width:100%" cellspacing="0" cellpadding="0" name="${keyStoreEntryList}" export="true" id="result" requestURI="JavaSecurityManagement.do?methodToCall=sort" defaultsort="1" defaultorder="ascending"
				decorator="org.kuali.rice.ksb.messaging.web.KSBTableDecorator">
		    <display:setProperty name="paging.banner.placement" value="both" />
		    <display:setProperty name="export.banner" value="" />
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Alias</div>">
		    	<c:out value="${result.alias}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;"  class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Create Date</div>" sortProperty="createDate.time">
		    	<fmt:formatDate value="${result.createDate}" pattern="${rice_constant.DEFAULT_DATE_FORMAT_PATTERN}" />&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Type</div>" >
		    	<c:out value="${result.displayType}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="false" title="<div style='text-align:center;vertical-align:top;'>Actions</div>" >
		      <c:if test="${result.allowsRemoval}">
		    	<a href='JavaSecurityManagement.do?methodToCall=removeEntry&aliasToRemove=<c:out value="${result.alias}" />' onClick="return confirm('Are you sure you want to Remove this entry?\n\nOnce an entry is removed it cannot be recovered.');">Remove</a>
		      </c:if>&nbsp;
		    </display:column>
		  </display:table>

    </td>
    <td width="20" height="20">&nbsp;</td>
  </tr>
  </c:if>
</table>

<jsp:include page="../Footer.jsp"/>

</body>
</html-el:html>
