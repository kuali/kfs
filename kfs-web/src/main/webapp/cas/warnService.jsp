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
<%@ include file="header.jsp" %>

<%
  String serviceId = (String) request.getAttribute("serviceId");
  String token = (String) request.getAttribute("token");
  String service = null;
  if (serviceId.indexOf('?') == -1)
    service = serviceId + "?ticket=" + token;
  else
    service = serviceId + "&ticket=" + token;
  service =
    edu.yale.its.tp.cas.util.StringUtil.substituteAll(service, "\n", "");
  service =
    edu.yale.its.tp.cas.util.StringUtil.substituteAll(service, "\r", "");
  service =
    edu.yale.its.tp.cas.util.StringUtil.substituteAll(service, "\"", "");
%>

            <table width="100%"
                   border="0"
                   cellspacing="0"
                   cellpadding="10"
                   height="100%">
              <tr>
                <td bgcolor="#FFFFFF" valign="top">
                  <font face="Arial, Helvetica, sans-serif"
                        size="4"
                        color="#003399">
                <b>Privacy notice</b></font></td>
              </tr>
              <tr>
                <td bgcolor="#FFFFFF" valign="top" height="363">
                  <p><font face="Arial, Helvetica, sans-serif" size="2">

A service claiming to have the following URL has asked the Central
Authentication Service to log you in:

		  </font></p>

<p>
 <blockquote>
  <b><tt><%= pageContext.findAttribute("serviceId") %></tt></b>
 </blockquote>
</p>

<p>Click <b>Proceed</b> below to proceed.</p>

<p align="center">
  <a href="<%= service %>"><font color="#336699">Proceed</font></a>
</p>

</td>
</tr>
</table>
                </td>
              </tr>
            </table>

<%@ include file="footer.jsp" %>
