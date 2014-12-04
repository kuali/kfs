<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
