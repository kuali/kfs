<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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
<kul:tab tabTitle="Status History" defaultOpen="false" tabErrorKey="${PurapConstants.ADDITIONAL_TAB_ERRORS}">
    <div class="tab-container" align=center>
        <p align=left><jsp:doBody/>
        <div class="h2-container">
            <h2>Status History</h2>
        </div>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="Status History Section">
			<tr>
				<kul:htmlAttributeHeaderCell
                	attributeEntry="${documentAttributes.statusHistoryDate}"
                	scope="col" />
               	<kul:htmlAttributeHeaderCell
                	attributeEntry="${documentAttributes.statusHistoryTime}"
                	scope="col" />
				<kul:htmlAttributeHeaderCell
                	attributeEntry="${documentAttributes.oldStatus}"
                	scope="col" />
    			<kul:htmlAttributeHeaderCell
                	attributeEntry="${documentAttributes.newStatus}"
                	scope="col" />
               	<kul:htmlAttributeHeaderCell
                	attributeEntry="${documentAttributes.statusHistoryUserIdentifier}"
                	scope="col" />
        	</tr>
        	<logic:notEmpty name="KualiForm" property="document.statusHistories">
	 			<logic:iterate id="changes" name="KualiForm" property="document.statusHistories" indexId="ctr">
	        		<tr>
	        			<td align=left valign=middle class="datacell">
		        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.statusHistoryDate}" 
		                		property="document.statusHistories[${ctr}].statusHistoryDate" readOnly="true"/>
		        		</td>
		        		<td align=left valign=middle class="datacell">
		        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.statusHistoryTime}" 
		                		property="document.statusHistories[${ctr}].statusHistoryTime" readOnly="true"/>
		        		</td>
		        		<td align=left valign=middle class="datacell">
		        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.oldStatus}" 
		                		property="document.statusHistories[${ctr}].oldStatus.statusDescription" readOnly="true"/>
		        		</td>
		        		<td align=left valign=middle class="datacell">
		        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.newStatus}" 
		                    	property="document.statusHistories[${ctr}].newStatus.statusDescription" readOnly="true"/>
		        		</td>
		        		<td align=left valign=middle class="datacell">
		        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.statusHistoryUserIdentifier}" 
		                    	property="document.statusHistories[${ctr}].statusHistoryUserIdentifier" readOnly="true"/>
		        		</td>
		        	</tr>
	        	</logic:iterate>
	        </logic:notEmpty>
    	</table>
    </div>
</kul:tab>