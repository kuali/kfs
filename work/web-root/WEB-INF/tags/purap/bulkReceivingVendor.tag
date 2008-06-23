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

<c:set var="isPOAvailable" value="${(not empty KualiForm.isPOAvailable)}" />

<kul:tab tabTitle="Vendor" defaultOpen="${true}" tabErrorKey="${PurapConstants.VENDOR_ERRORS}">
    <div class="tab-container" align=center>
        <html:hidden property="document.vendorHeaderGeneratedIdentifier" />
        <html:hidden property="document.vendorDetailAssignedIdentifier" />

		
		
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Vendor Section">
            <tr>
                <td colspan="4" class="subhead">Vendor</td>
            </tr>

        		<tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorDetailsForDisplay}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorDetailsForDisplay}" property="document.vendorDetailsForDisplay" />
	                </td>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternateVendorDetailsForDisplay}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternateVendorDetailsForDisplay}" property="document.alternateVendorDetailsForDisplay"/>
	                </td>
            	</tr>


            

            

          

          

      

          

           

            
            
			
			
            

        </table>

        

    </div>
</kul:tab>