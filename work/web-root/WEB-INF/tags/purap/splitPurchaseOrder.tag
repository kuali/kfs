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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for item's fields." %>
              
<c:set var="additionalChargesExist" value="true" />
              
<kul:tabTop tabTitle="Split a PO" defaultOpen="true" tabErrorKey="${PurapConstants.SPLIT_PURCHASE_ORDER_TAB_ERRORS}">

    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Split a PO</h2>
        </div>
        
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Preliminaries">

            <tr>
            	<th align=right valign=middle class="bord-l-b" width="50%">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.copyingNotesWhenSplitting}" /></div>
                </th>
                <td align=left valign=middle class="datacell" width="50%">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.copyingNotesWhenSplitting}" property="document.copyingNotesWhenSplitting" />
                </td>
            </tr>
            
            <c:if test="${additionalChargesExist}">
	            <tr>
	            	<th align=right valign=middle class="bord-l-b" width="50%">
	            		<div align="right">WARNING</div>
	            	</th>
	            	<td align=left valign=middle class="datacell" width="50%">
	            		* ADDITIONAL CHARGES EXIST *
	            	</td>
	            </tr>
	        </c:if>
        </table>
        
        <div class="h2-container">
        	<h2>Splitting Item Selection</h2>
        </div>
        
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Splitting Item Selection">
        	<tr>
        		<th>&nbsp;</th>
        		<th>&nbsp;</th>
        	</tr>
        	<!-- Iterate here.  -->
        	<tr>
        		<td>&nbsp;</td>
        		<td>&nbsp;</td>
        	</tr>
        </table>
    </div>
    
</kul:tabTop>