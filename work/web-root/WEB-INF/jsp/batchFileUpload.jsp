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

<form action="batchFileUpload" method="POST"
      enctype="multipart/form-data">
    
    ${message}<br />
    <br />
	<select name="uploadDir">
		<c:forEach var="dir" items="${directories}">
			<option value="${dir}">${dir}</option>option>
		</c:forEach>
	</select>
	<br />
	<input type="file" name="uploadFile" /><br />
	<br />
	<input type="submit" />
</form>		
