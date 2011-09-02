<%--
 Copyright 2005 The Kuali Foundation
 
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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="channelTitle" required="true" %>
<%@ attribute name="channelUrl" required="true" %>
<%@ attribute name="selectedTab" required="true" %>

<portal:immutableBar />

<%-- first try to check if they are focusing in --%>
<c:choose>
  <c:when test='${!empty channelTitle && !empty channelUrl}'>
	  <div id="iframe_portlet_container_div">
	  	<portal:iframePortletContainer channelTitle="${channelTitle}" channelUrl="${channelUrl}" />
	  </div>
  </c:when>
  <c:otherwise>
	<table border="0" width="100%"  cellspacing="0" cellpadding="0" id="iframe_portlet_container_table">
	<c:if test="${empty channelTitle && empty channelUrl}">
		<c:set var="motd" value="<%= (new org.kuali.kfs.sys.businessobject.defaultvalue.MessageOfTheDayFinder()).getValue() %>" scope="page"/>
		<c:if test="${!empty pageScope.motd}">
		  	<tr valign="top" bgcolor="#FFFFFF">
				<td width="15" class="leftback-focus">&nbsp;</td>
				<td colspan="3">
				    <channel:portalChannelTop channelTitle="Message Of The Day" />
					    <div class="body">
				    	    <strong><c:out value="${pageScope.motd}"  /></strong>
		    		    </div>
		    		<channel:portalChannelBottom />
				</td>
		   	</tr>
	   	</c:if>
	</c:if>
 	<tr valign="top" bgcolor="#FFFFFF">
       <td width="15" class="leftback-focus">&nbsp;</td>
        <c:choose>
          <%-- then default to tab based actions if they are not focusing in --%>
          <c:when test='${selectedTab == "main"}'>
              <portal:mainTab />
          </c:when>
          <c:when test='${selectedTab == "maintenance"}'>
              <portal:maintenanceTab />
          </c:when>
          <c:when test='${selectedTab == "administration"}'>
              <portal:administrationTab />
          </c:when>
          
          <%-- as backup go to the main menu index --%>
          <c:otherwise>
              <portal:mainTab />
          </c:otherwise>
        </c:choose>
    </tr>
</table>
  </c:otherwise>
</c:choose>

 <div class="footerbevel">&nbsp;</div>
  <div id="footer-copyright"> <bean:message key="app.copyright" arg0="${ConfigProperties.current.year}" /></div>
