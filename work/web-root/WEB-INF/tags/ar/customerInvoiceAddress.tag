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

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${empty editingMode['fullEntry']}" />

<%-- hidden attribute for document number since it isn't displayed--%>
<html:hidden property="document.accountsReceivableDocumentHeader.documentNumber" />

<kul:tab tabTitle="Billing/Shipping" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_ADDRESS}">
    <div class="tab-container" align=center>	
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Section">
            <tr>
                <td colspan="4" class="subhead">Bill to Address</td>
            </tr>

            <tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.customerBillToAddressIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.customerBillToAddressIdentifier}" property="document.customerBillToAddressIdentifier" readOnly="${readOnly}"/>
                    <c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.module.ar.bo.CustomerAddress" />
	                </c:if>
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                	Country
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                	&nbsp;
                </td>
                
            </tr>      
            
            <tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    Address Type
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                	&nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                	URL
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                	&nbsp;
                </td>
            </tr>             
            
            <tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    Address 1
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                	&nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                	Fax Number
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                	&nbsp;
                </td>
            </tr>      
            
			<tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    Address 2
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                	&nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                	Email Address
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                	&nbsp;
                </td>
            </tr>  
            
			<tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    City
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                	&nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                	Set as Default Address
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                &nbsp;
                </td>
            </tr> 
            
            
			<tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    State
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                &nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                	Active Indicator
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                &nbsp;
                </td>
            </tr>     
            
			<tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    Postal Code
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                &nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
					Attention
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                &nbsp;
                </td>
            </tr> 
            
			<tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    Province
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                &nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                &nbsp;
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                &nbsp;
                </td>
            </tr>                          
                             
            <tr>
                <td colspan="4" class="subhead">Ship To Address</td>
            </tr>            
			<tr>		
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.customerShipToAddressIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.customerShipToAddressIdentifier}" property="document.customerShipToAddressIdentifier" readOnly="${readOnly}"/>
                    <c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.module.ar.bo.CustomerAddress" />
	                </c:if>
                </td> 
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                	Country
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                &nbsp;
                </td>
                
            </tr>      
            
            <tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    Address Type
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                &nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                	URL
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                &nbsp;
                </td>
            </tr>             
            
            <tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    Address 1
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                &nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                	Fax Number
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                &nbsp;
                </td>
            </tr>      
            
			<tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    Address 2
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                &nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                	Email Address
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                &nbsp;
                </td>
            </tr>  
            
			<tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    City
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                &nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                	Set as Default Address
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                &nbsp;
                </td>
            </tr> 
            
            
			<tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    State
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                &nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                	Active Indicator
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                &nbsp;
                </td>
            </tr>     
            
			<tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    Postal Code
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">
                &nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
					Attention
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >
                </td>
            </tr> 
            
			<tr>
				<th align=right valign=middle class="bord-l-b"  style="width: 25%;">
                    Province
                </th>
                <td align=left valign=middle class="datacell"  style="width: 25%;">&nbsp;
                </td>     
                <th align=right valign=middle class="bord-l-b" style="width: 25%;">
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;" >&nbsp;
                </td>
            </tr>
        </table>
    </div>
</kul:tab>