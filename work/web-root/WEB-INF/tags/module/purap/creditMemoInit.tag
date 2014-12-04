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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<c:set var="tabindexOverrideBase" value="20" />
              
<kul:tabTop tabTitle="Credit Memo Initiation" defaultOpen="true" tabErrorKey="*">

    <div class="tab-container" align=center>
            <h3>Credit Memo Initiation</h3>
        
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Credit Memo Init Section" >
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.creditMemoNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.creditMemoNumber}" property="document.creditMemoNumber" 
                   		tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right">**<kul:htmlAttributeLabel attributeEntry="${documentAttributes.paymentRequestIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.paymentRequestIdentifier}" property="document.paymentRequestIdentifier" 
                   		tabindexOverride="${tabindexOverrideBase + 5}"/>
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.creditMemoDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.creditMemoDate}" property="document.creditMemoDate" datePicker="true" 
                   		tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right">**<kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.purchaseOrderIdentifier}" property="document.purchaseOrderIdentifier"  
                   		tabindexOverride="${tabindexOverrideBase + 5}"/>
                </td>
            </tr>    
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.creditMemoAmount}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.creditMemoAmount}" property="document.creditMemoAmount" 
                   		tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right">**<kul:htmlAttributeLabel  attributeEntry="${documentAttributes.vendorNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.vendorNumber}" property="document.vendorNumber"
                   		tabindexOverride="${tabindexOverrideBase + 5}"/>
                </td>
            </tr>
		</table> 
    </div>

</kul:tabTop>
