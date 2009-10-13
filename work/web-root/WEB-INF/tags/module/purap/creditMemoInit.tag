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
