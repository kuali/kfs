<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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
<%@ include file="/rice-portal/jsp/sys/riceTldHeader.jsp"%>

<%@ attribute name="channelTitle" required="true" %>
<%@ attribute name="channelUrl" required="true" %>
<%@ attribute name="selectedTab" required="true" %>

<portal:immutableBar />

<table border="0" width="100%"  cellspacing="0" cellpadding="0" id="iframe_portlet_container_table">
	<c:if test="${empty channelTitle && empty channelUrl}">
		<c:set var="motd" value="" scope="page"/>
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
          <%-- first try to check if they are focusing in --%>
          <c:when test='${!empty channelTitle && !empty channelUrl}'>
            <td class="content" valign="top" colspan="2">
              <c:if test="${!empty param.backdoorId}">
                  <portal:iframePortletContainer channelTitle="${channelTitle}" channelUrl="${channelUrl}?backdoorId=${param.backdoorId}&methodToCall.login.x=1" />
              </c:if>
              <c:if test="${empty param.backdoorId}">
                  <portal:iframePortletContainer channelTitle="${channelTitle}" channelUrl="${channelUrl}" />
              </c:if>
            </td>
          </c:when>
          <%-- then default to tab based actions if they are not focusing in --%>
          <c:when test='${selectedTab == "main"}'>
              <portal:mainTab />
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

 <div class="footerbevel">&nbsp;</div>
  <div id="footer-copyright"> <bean:message key="app.copyright" /></div>
