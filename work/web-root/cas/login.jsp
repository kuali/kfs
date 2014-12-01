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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%-- NOTE THAT THIS PAGE NOW REQUIRES A TRANSACTION ID TO BE PART OF THE
     LOGIN FORM!  IF YOU HAVE A CUSTOM login.jsp, YOU MUST ADD THIS
     NEW POST INFORMATION. --%>

<p>
<% if (request.getAttribute("edu.yale.its.tp.cas.badUsernameOrPassword") 
       != null) { %>
<font color="red">Sorry, you entered an invalid UserID. <br />
  Please try again. 
</font>
<% } else if (request.getAttribute("edu.yale.its.tp.cas.service") == null) { %>
  You may establish authentication now in order to access protected
  services later.
<% } else if (request.getAttribute("edu.yale.its.tp.cas.badLoginTicket") != null) { %>
<%-- place a message here if you want --%>
<% } else { %>
  You have requested access to a site that requires authentication. 
<% } %>
</p>
</font>

<p>
<font face="Arial,Helvetica">Enter your UserID below; then click on the <b>Login</b>
button to continue.  A password is not required, because this is a </font><font face="Arial,Helvetica" color="red"><b>DEMO ONLY</b></font><font face="Arial,Helvetica"> authentication application.</font>
</p>

        <form method="post" name="login_form">

        <table bgcolor="#FFFFAA" align="center"><tr><td>

        <table border="0" cellpadding="0" cellspacing="5">


        <tr>
        <td><font face="Arial,Helvetica"><b><label for="username">UserID</label>:</b></td>
        <td>
        <input type="text" id="username" name="username" maxlength="8"></td>
        </tr>
		<c:if test="${requestScope.showPasswordField}">
	        <tr>
	        <td><font face="Arial,Helvetica"><b><label for="password">Password</label>:</b></td>
	        <td>
	        <input type="text" id="password" name="password" maxlength="200"></td>
	        </tr>
        </c:if>

        <tr>
        <td colspan="2" align="left">
	<input type="checkbox" id="" name="warn" value="true" />
        <small>
	    <small><label for="warn">Warn me before logging me in to other sites</label>.</small>
        </small>
	</tr>

        <tr>
        <td colspan="2" align="right">
	<input type="hidden" name="lt" value="<%= request.getAttribute("edu.yale.its.tp.cas.lt") %>" />
        <input type="submit" value="Login" title="Login">
        </td>
        </tr>

        </td></tr></table>
        </form>

        </td></tr></table>

</td></tr>

<tr><td colspan="2">
<center>
<font color="red" face="Arial,Helvetica">
<i><b>For security reasons, quit your web browser when you are done
accessing services that require authentication!</b></i>
</font>
</center>
</td></tr>


<tr><td colspan="2">

</td></tr>

</table>
</table>
</table>
