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
<%@ attribute name="documentAttribute" type="java.util.Map" required="true" description="The property being rendered." %>
<%@ attribute name="text" required="true" description="The text being rendered." %>
<%@ attribute name="title" required="true" description="The title." %>
<%@ attribute name="property" required="true" description="The title." %>
<%@ attribute name="enable" required="true" description="Enable checkbox" %>
<%@ attribute name="open" required="true" description="Tab defaults to closed or open" %>

<kul:tab tabTitle="${title}" defaultOpen="${open}" tabErrorKey="${property }">
<div class="tab-container" align=center >
<table cellpadding=2 class="datatable" summary="${title}">
	<tr>
    	<td colspan=2 align=left valign=middle class="datacell">${text}</td>
    </tr>
    <tr>
    	<th scope=row class="bord-l-b">
    		<div align="right">
    			<kul:htmlAttributeLabel attributeEntry="${documentAttribute}"/>
			</div>
		</th>
		<td class="datacell" width="25%">
        	<kul:htmlControlAttribute attributeEntry="${documentAttribute}" property="document.${property}" readOnly="${!enable}" />
        </td>
    </tr>
</table>
</div>
</kul:tab>
