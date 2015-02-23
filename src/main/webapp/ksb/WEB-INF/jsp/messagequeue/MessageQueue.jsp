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

<c:set var="hasAnyRows" value="${false}" />
<c:if test="${!empty MessageQueueForm.messageQueueRows}">
	<c:if test="${MessageQueueForm.messageQueueRowsSize > 0}">
		<c:set var="hasAnyRows" value="${true}" />
	</c:if>
</c:if>

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
    <td width="85%"><a href="MessageQueue.do?methodToCall=start">Refresh Page</a></td>
    <td>&nbsp;&nbsp;</td>
  </tr>
</table>


<html-el:form action="/MessageQueue.do">
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
        <td class="datacell">IP Address: <c:out value="${MessageQueueForm.myIpAddress}"/><br>
        	Application ID: <c:out value="${MessageQueueForm.myApplicationId}"/>
        </td>
        </tr>
        <tr>
        <td class="datacell">message.persistence: <c:out value="${MessageQueueForm.messagePersistence}"/><br>
        	message.delivery: <c:out value="${MessageQueueForm.messageDelivery}"/><br>
        	message.off: <c:out value="${MessageQueueForm.messageOff}"/>
        </td>

	  </tr>
	</table><br>
      <table border="0" cellpadding="0" cellspacing="0" class="bord-r-t">
  			<tr>
  				<td class="thnormal" align="right" width="20%">
  					Message ID:&nbsp;
  				</td>
  				<td class="datacell">
						<html-el:text property="routeQueueId${ksb_constant.ROUTE_QUEUE_FILTER_SUFFIX}" size="20"/>
						&nbsp;
   				</td>
   			</tr>
   			<tr>
  				<td class="thnormal" align="right" width="20%">
   					Service Name:&nbsp;
  				</td>
  				<td class="datacell">
   					<html-el:text property="serviceName${ksb_constant.ROUTE_QUEUE_FILTER_SUFFIX}" />
   				</td>
   			</tr>
   			<tr>
  				<td class="thnormal" align="right" width="20%">
   					Application ID:&nbsp;
  				</td>
  				<td class="datacell">
   					<html-el:text property="applicationId${ksb_constant.ROUTE_QUEUE_FILTER_SUFFIX}" />
   				</td>
   			</tr>
   			<tr>
  				<td class="thnormal" align="right" width="20%">
  					IP Number:&nbsp;
  				</td>
  				<td class="datacell">
   					<html-el:text property="ipNumber${ksb_constant.ROUTE_QUEUE_FILTER_SUFFIX}" size="20" maxlength="15" />
   					&nbsp;
   				</td>
   			</tr>
  			<tr>
  				<td class="thnormal" align="right" width="20%">
  					Queue Status:&nbsp;
  				</td>
  				<td class="datacell">
						<html-el:select property="queueStatus${ksb_constant.ROUTE_QUEUE_FILTER_SUFFIX}">
							<html-el:option value=""></html-el:option>
							<html-el:option value="${ksb_constant.ROUTE_QUEUE_QUEUED}"><c:out value="${ksb_constant.ROUTE_QUEUE_QUEUED_LABEL}" /></html-el:option>
							<html-el:option value="${ksb_constant.ROUTE_QUEUE_ROUTING}"><c:out value="${ksb_constant.ROUTE_QUEUE_ROUTING_LABEL}" /></html-el:option>
							<html-el:option value="${ksb_constant.ROUTE_QUEUE_EXCEPTION}"><c:out value="${ksb_constant.ROUTE_QUEUE_EXCEPTION_LABEL}" /></html-el:option>
						</html-el:select>
						&nbsp;
   				</td>
   			</tr>
   			<tr>
  				<td class="thnormal" align="right" width="20%">
  					App Specific Value 1:&nbsp;
  				</td>
  				<td class="datacell">
						<html-el:text property="value1${ksb_constant.ROUTE_QUEUE_FILTER_SUFFIX}" size="40"/>
						&nbsp;
   				</td>
   			</tr>
   			<tr>
  				<td class="thnormal" align="right" width="20%">
  					App Specific Value 2:&nbsp;
  				</td>
  				<td class="datacell">
						<html-el:text property="value2${ksb_constant.ROUTE_QUEUE_FILTER_SUFFIX}" size="40"/>
						&nbsp;
   				</td>
   			</tr>

   			<tr>
  				<td class="thnormal" align="right" width="20%">
   					&nbsp;
  				</td>
  				<td class="datacell">
  					<html-el:submit property="filterApplied" value="Filter" />
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
  	<td><html-el:text property="maxMessageFetcherMessages" size="3"/>&nbsp;&nbsp;<input type="button" value="Execute Message Fetcher" onclick="executeMessageFetcher()"/></td>
  	<td width="20" height="20">&nbsp;</td>
  <tr>
  	<td colspan="3">&nbsp;</td>
  </tr>
  <tr>
    <td width="20" height="20">&nbsp;</td>
    <td height="30">
       <strong>Documents currently in route queue:</strong>
       <c:if test="${!hasAnyRows}">
  	   &nbsp;&nbsp;None.
  	   </c:if>
  	   <c:if test="${hasAnyRows}">
  	   <c:out value="${MessageQueueForm.messageQueueRowsSize}"/>
	  	   <c:if test="${MessageQueueForm.messageQueueRowsSize > MessageQueueForm.maxRows}">
	  	     &nbsp;&nbsp;<c:out value="There were ${MessageQueueForm.maxRows} or more rows, only displaying the first ${MessageQueueForm.maxRows}."/>
	  	   </c:if>
	   </c:if>
       <br>
    </td>
    <td width="20" height="20">&nbsp;</td>
  </tr>
</table>
</html-el:form>
<table width="100%" border=0 cellspacing=0 cellpadding=0>
  <c:if test="${hasAnyRows}">
  <tr>
    <td width="20" height="20">&nbsp;</td>
    <td>

		  <display:table excludedParams="*" pagesize="${MessageQueueForm.pageSize}" class="bord-r-t" style="width:100%" cellspacing="0" cellpadding="0" name="${MessageQueueForm.messageQueueRows}" export="true" id="result" requestURI="MessageQueue.do?methodToCall=start&filterApplied=${filterApplied}&queueStatusFilter=${queueStatusFilter}&ipNumberFilter=${ipNumberFilter}&serviceNameFilter=${serviceNameFilter}&applicationIdFilter=${applicationIdFilter}" defaultsort="1" defaultorder="descending"
				decorator="org.kuali.rice.ksb.messaging.web.KSBTableDecorator">
		    <display:setProperty name="paging.banner.placement" value="both" />
		    <display:setProperty name="export.banner" value="" />
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Message<br />Queue Id</div>" sortProperty="routeQueueId">
		    	<c:out value="${result.routeQueueId}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Service<br />Name</div>" >
		    	<c:out value="${result.serviceName}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;"  class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Application<br />ID</div>" >
		    	<c:out value="${result.applicationId}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>IP Number</div>" >
		    	<c:out value="${result.ipNumber}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Queue<br />Status</div>" >
		    	<c:choose>
			    	<c:when test="${result.queueStatus == ksb_constant.ROUTE_QUEUE_QUEUED}">
			    		<c:out value="${ksb_constant.ROUTE_QUEUE_QUEUED_LABEL}" />&nbsp;
			    	</c:when>
			    	<c:when test="${result.queueStatus == ksb_constant.ROUTE_QUEUE_ROUTING}">
			    		<c:out value="${ksb_constant.ROUTE_QUEUE_ROUTING_LABEL}" />&nbsp;
			    	</c:when>
			    	<c:when test="${result.queueStatus == ksb_constant.ROUTE_QUEUE_EXCEPTION}">
			    		<c:out value="${ksb_constant.ROUTE_QUEUE_EXCEPTION_LABEL}" />&nbsp;
			    	</c:when>
			    	<c:otherwise>
				    	<c:out value="${result.queueStatus}"/>&nbsp;
		    		</c:otherwise>
		    	</c:choose>
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Queue<br />Priority</div>" >
		    	<c:out value="${result.queuePriority}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Queue<br />Date</div>" sortProperty="queueDate.time">
		    	<fmt:formatDate value="${result.queueDate}" pattern="${rice_constant.DEFAULT_DATE_FORMAT_PATTERN}" />&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Expiration<br />Date</div>" sortProperty="expirationDate.time">
		    	<fmt:formatDate value="${result.queueDate}" pattern="${rice_constant.DEFAULT_DATE_FORMAT_PATTERN}" />&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>Retry<br />Count</div>" >
		    	<c:out value="${result.retryCount}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>App Specific<br />Value 1</div>" >
		    	<c:out value="${result.value1}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="true" title="<div style='text-align:center;vertical-align:top;'>App Specific<br />Value 2</div>" >
		    	<c:out value="${result.value2}"/>&nbsp;
		    </display:column>
		    <display:column style="text-align:center;vertical-align:middle;" class="datacell" sortable="false" title="<div style='text-align:center;vertical-align:top;'>Actions</div>" >
		    	<a href='MessageQueue.do?methodToCall=view&messageId=<c:out value="${result.routeQueueId}" />'>View</a>
		    	&nbsp;
		    	<a href='MessageQueue.do?methodToCall=edit&messageId=<c:out value="${result.routeQueueId}" />'>Edit</a>
		    	&nbsp;
		    	<a href='MessageQueue.do?methodToCall=quickRequeueMessage&messageId=<c:out value="${result.routeQueueId}" />' onClick="return confirm('Are you sure you want to ReQueue this message?\n\nThe QueueDate will be reset to today and the Retry set to zero.');">ReQueue</a>
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
