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
<%@ taglib tagdir="/WEB-INF/tags/kr" prefix="kul"%>

<html-el:html>
<head>
<title>Service Bus</title>
<style type="text/css">
   .highlightrow {}
   tr.highlightrow:hover, tr.over td { background-color: #66FFFF; }
</style>
<link href="css/screen.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="scripts/en-common.js"></script>
<script language="JavaScript" src="scripts/messagequeue-common.js"></script>
</head>

<body bgcolor="#ffffff" marginheight="0" marginwidth="0" topmargin="0" leftmargin="0">

<table width="100%" border=0 cellpadding=0 cellspacing=0 class="headercell1">
  <tr>
    <td width="15%"><img src="images/wf-logo.gif" alt="Workflow" width=150 height=21 hspace=5 vspace=5></td>
    <td width="85%"><a href="ServiceBus.do?methodToCall=start">Refresh Page</a></td>
    <td>&nbsp;&nbsp;</td>
  </tr>
</table>

<html-el:form action="/ServiceBus.do">
<html-el:hidden property="methodToCall" />

  <table width="100%" border=0 cellspacing=0 cellpadding=0>
  <tr>
        <td width="20" height="20">&nbsp;</td>
  	<td>

      <br>
  	  <jsp:include page="../Messages.jsp"/>
      <br>

  	  <table border="0" cellpadding="0" cellspacing="0" class="bord-r-t">
		<tr>
        		<td class="thnormal">
  					Current Node Info&nbsp;
  				</td>
   			</tr>
		  <tr>
        <td class="datacell">Application ID: <c:out value="${ServiceBusForm.myApplicationId}"/><br>
        	IP Address: <c:out value="${ServiceBusForm.myIpAddress}"/><br>
        	Instance ID: <c:out value="${ServiceBusForm.myInstanceId}"/>
        </td>
        </tr>
        <tr>
        <td class="datacell">dev.mode: <c:out value="${ServiceBusForm.devMode}"/>
        </td>
	  </tr>
	</table><br>
  	</td>
  	<td width="20" height="20">&nbsp;</td>
  </tr>
  <tr>
  	<td colspan="3">&nbsp;</td>
  </tr>
  <tr>
    <td width="20" height="20">&nbsp;</td>
  	<td><input type="button" value="Refresh Service Bus" onclick="refreshServiceBus()"/></td>
  	<td width="20" height="20">&nbsp;</td>
  <tr>
  	<td colspan="3">&nbsp;</td>
  </tr>
</table>
</html-el:form>
<table width="100%" border=0 cellspacing=0 cellpadding=0>
  <tr>
    <td width="20" height="20">&nbsp;</td>
    <td>
		  <b>Published Services:</b>
		  <%-- Table layout of the search results --%>
		  <display:table excludedParams="*" class="bord-r-t" style="width:100%" cellspacing="0" cellpadding="0" name="${ServiceBusForm.publishedServices}" id="result" requestURI="ServiceBus.do?methodToCall=start" defaultsort="1" defaultorder="ascending"
				decorator="org.kuali.rice.ksb.messaging.web.KSBTableDecorator">
		    <display:setProperty name="paging.banner.placement" value="both" />
		    <display:setProperty name="paging.banner.all_items_found" value=""/>
		    <display:setProperty name="export.banner" value="" />
		    <display:setProperty name="basic.msg.empty_list">No Published Services</display:setProperty>
		    <display:column class="datacell" sortable="true" title="<div>Service Name</div>" >
		    	<c:out value="${result.serviceName}"/>&nbsp;
		    </display:column>
		    <display:column class="datacell" sortable="true" title="<div>Endpoint URL</div>" >
				<c:choose>
				<c:when test='${result["class"].name == "org.kuali.rice.ksb.api.bus.support.SoapServiceConfiguration"}'>
		    	<a href="${result.endpointUrl}?wsdl"><c:out value="${result.endpointUrl}"/></a>&nbsp;
				</c:when>
                <c:when test='${result["class"].name == "org.kuali.rice.ksb.api.bus.support.RestServiceConfiguration"}'>
                <a href="${result.endpointUrl}?_wadl&_type=xml"><c:out value="${result.endpointUrl}"/></a>&nbsp;
                </c:when>
				<c:otherwise>
		    	<c:out value="${result.endpointUrl}"/>&nbsp;
				</c:otherwise>
				</c:choose>
		    </display:column>
		    <display:column class="datacell" sortable="true" title="<div>Instance ID</div>" >
		    	<c:out value="${result.instanceId}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;"  class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Application ID</div>" >
		    	<c:out value="${result.applicationId}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Service Version</div>" >
		    	<c:out value="${result.serviceVersion}"/>&nbsp;
		    </display:column>
			<display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Type</div>" >
				<c:out value="${result.type}"/>&nbsp;
		    </display:column>
		  </display:table>

    </td>
    <td width="20" height="20">&nbsp;</td>
   </tr>

   <tr><td colspan="3">&nbsp;</td></tr>
   
    <tr>
    <td width="20" height="20">&nbsp;</td>
    <td>
		  <b>All Bus Services:</b>
		  <%-- Table layout of the search results --%>
		  <display:table excludedParams="*" class="bord-r-t" style="width:100%" cellspacing="0" cellpadding="0" name="${ServiceBusForm.globalServices}" id="result" requestURI="ServiceBus.do?methodToCall=start" defaultsort="1" defaultorder="ascending"
				decorator="org.kuali.rice.ksb.messaging.web.KSBTableDecorator">
		    <display:setProperty name="paging.banner.placement" value="both" />
		    <display:setProperty name="paging.banner.all_items_found" value=""/>
		    <display:setProperty name="export.banner" value="" />
		    <display:setProperty name="basic.msg.empty_list">No Bus Services</display:setProperty>
		    <display:column class="datacell" sortable="true" title="<div>Service Name</div>" >
		    	<c:out value="${result.serviceName}"/>&nbsp;
		    </display:column>
		    <display:column class="datacell" sortable="true" title="<div>Endpoint URL</div>" >
				<c:choose>
				<c:when test='${result["class"].name == "org.kuali.rice.ksb.api.bus.support.SoapServiceConfiguration"}'>
		    	<a href="${result.endpointUrl}?wsdl"><c:out value="${result.endpointUrl}"/></a>&nbsp;
				</c:when>
                <c:when test='${result["class"].name == "org.kuali.rice.ksb.api.bus.support.RestServiceConfiguration"}'>
                <a href="${result.endpointUrl}?_wadl&_type=xml"><c:out value="${result.endpointUrl}"/></a>&nbsp;
                </c:when>
				<c:otherwise>
		    	<c:out value="${result.endpointUrl}"/>&nbsp;
				</c:otherwise>
				</c:choose>
		    </display:column>
		    <display:column class="datacell" sortable="true" title="<div>Instance ID</div>" >
		    	<c:out value="${result.instanceId}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;"  class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Application ID</div>" >
		    	<c:out value="${result.applicationId}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Service Version</div>" >
		    	<c:out value="${result.serviceVersion}"/>&nbsp;
		    </display:column>
			<display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Type</div>" >
				<c:out value="${result.type}"/>&nbsp;
		    </display:column>
		  </display:table>

    </td>
    <td width="20" height="20">&nbsp;</td>
  </tr>


</table>

    <jsp:include page="../Footer.jsp"/>

</body>
</html-el:html>
