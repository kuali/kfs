<%--
 Copyright 2007-2009 The Kuali Foundation
 
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