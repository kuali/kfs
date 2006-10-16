<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/WEB-INF/tags/purap/statushistory.tag,v $
 
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
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

<kul:tab tabTitle="StatusHistory" defaultOpen="true" tabErrorKey="${Constants.ADDITIONAL_TAB_ERRORS}">
    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Status History</h2>
        </div>
	</div>
	
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Status History Section">
		<tr>
        	<th align=center valign=middle class="bord-l-b">
            	<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.statusHistory.oldStatusCode}" /></div>
            </th>
            <th align=center valign=middle class="bord-l-b">
            	<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.statusHistory.newStatusCode}" /></div>
            </th>
            <th align=center valign=middle class="bord-l-b">
            	<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.statusHistory.noteIdentifier}" /></div>
            </th>
        </tr>
        <c:forEach items="${documentAttributes.statusHistory}">
        	<td align=left valign=middle class="datacell">
        		<kul:htmlControlAttribute attributeEntry="${documentAttributes.statusHistory.oldStatusCode}" 
                	property="document.statusHistory.oldStatusCode" readOnly="${readOnly}"/>
        	</td>
        	<td align=left valign=middle class="datacell">
        		<kul:htmlControlAttribute attributeEntry="${documentAttributes.statusHistory.newStatusCode}" 
                    property="document.statusHistory.newStatusCode" readOnly="${readOnly}"/>
        	</td>
        	<td align=left valign=middle class="datacell">
        		<kul:htmlControlAttribute attributeEntry="${documentAttributes.statusHistory.noteIdentifier}" 
                    property="document.statusHistory.noteIdentifier" readOnly="${readOnly}"/>
        	</td>
        </c:forEach>
    </table>
</kul:tab>