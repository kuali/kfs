<%--
 Copyright 2006-2009 The Kuali Foundation
 
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
              
<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="vendorReadOnly" value="${(not empty KualiForm.editingMode['lockVendorEntry'])}" />
<c:set var="tabindexOverrideBase" value="10" />

<kul:tab tabTitle="Vendor" defaultOpen="${true}" tabErrorKey="${PurapConstants.BULK_RECEIVING_VENDOR_TAB_ERRORS}">
    <div class="tab-container" align=center>
        
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Vendor Section">
            <tr>
                <td colspan="4" class="subhead">Vendor</td>
            </tr>
		
			<%-- If PO available, display the available vendor and alternate vendor details --%>
	        <c:if test="${isPOAvailable}" > 
        		<tr>
	        		<th align=right valign=middle  class="bord-l-b">
	                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell"> 
	                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorName}" property="document.vendorName" readOnly="true" /><br>
	                   	<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine1Address}" property="document.vendorLine1Address" readOnly="true" /><br>
	                   	<c:if test="${! empty KualiForm.document.vendorLine2Address}">                   	
	                   		<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine2Address}" property="document.vendorLine2Address" readOnly="true" /><br>
	                   	</c:if>
	                   	<c:if test="${! empty KualiForm.document.vendorCityName}">                   	
	    	           		<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCityName}" property="document.vendorCityName" readOnly="true" />,&nbsp;
	                   	</c:if>
	                   	<c:if test="${! empty KualiForm.document.vendorStateCode}">                   	
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorStateCode}" property="document.vendorStateCode" readOnly="true" />&nbsp;
	                  	</c:if>
	                   	<c:if test="${! empty KualiForm.document.vendorPostalCode}">                   	
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPostalCode}" property="document.vendorPostalCode" readOnly="true" />
	                   	</c:if>
	                   	<c:if test="${! empty KualiForm.document.vendorAddressInternationalProvinceName}">                   	
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorAddressInternationalProvinceName}" property="document.vendorAddressInternationalProvinceName" readOnly="true" />
	                   	</c:if>
	                   	<c:if test="${! empty KualiForm.document.vendorCountryCode}">                   	
		            		<br><kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCountryCode}" property="document.vendorCountryCode" readOnly="true" />
	                   	</c:if>
	            	</td>
	            	
	            	<th align=right valign=middle  class="bord-l-b">
	                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternateVendorName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell"> 
	                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.alternateVendorName}" property="document.alternateVendorName" readOnly="true" /><br>
	                	<c:if test="${! empty KualiForm.document.alternateVendorNumber}">
		                   	<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine1Address}" property="document.alternateVendorDetail.defaultAddressLine1" readOnly="true" /><br>
		                   	<c:if test="${! empty KualiForm.document.alternateVendorDetail.defaultAddressLine2}">                   	
		                   		<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine2Address}" property="document.alternateVendorDetail.defaultAddressLine2" readOnly="true" /><br>
		                   	</c:if>
		                   	<c:if test="${! empty KualiForm.document.alternateVendorDetail.defaultAddressCity}">                   	
		    	           		<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCityName}" property="document.alternateVendorDetail.defaultAddressCity" readOnly="true" />,&nbsp;
		                   	</c:if>
		                   	<c:if test="${! empty KualiForm.document.alternateVendorDetail.defaultAddressStateCode}">                   	
			                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorStateCode}" property="document.alternateVendorDetail.defaultAddressStateCode" readOnly="true" />&nbsp;
		                  	</c:if>
		                   	<c:if test="${! empty KualiForm.document.alternateVendorDetail.defaultAddressPostalCode}">                   	
			                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPostalCode}" property="document.alternateVendorDetail.defaultAddressPostalCode" readOnly="true" />
		                   	</c:if>
		                   	<c:if test="${! empty KualiForm.document.alternateVendorDetail.defaultAddressInternationalProvince}">                   	
			                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorAddressInternationalProvinceName}" property="document.alternateVendorDetail.defaultAddressInternationalProvince" readOnly="true" />
		                   	</c:if>
		                   	<c:if test="${! empty KualiForm.document.alternateVendorDetail.defaultAddressCountryCode}">                   	
			            		<br><kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCountryCode}" property="document.alternateVendorDetail.defaultAddressCountryCode" readOnly="true" />
		                   	</c:if>
		                 </c:if>  	
	            	</td>
            	</tr>
            	<tr>
            		<th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorContact}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorContact}" 
                        						  property="document.vendorContact" 
                        						  readOnly="true"/>   
                        <c:if test="${(not empty KualiForm.document.vendorNumber) and fullEntryMode}" > 						                   
	                        <kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.VendorContact" 
	                        		    readOnlyFields="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier" 
	                        		    autoSearch="yes" 
	 			                        lookupParameters="document.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,document.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier" 
	                        	        hideReturnLink="true" 
	                        	        extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_return.gif" />     
	                    </c:if>    	                        
                    </td>
                    <th align=right valign=middle class="bord-l-b">
   	                    <div align="right"><bean:message key="${KualiForm.goodsDeliveredByLabel}" /></div>
       	            </th>
       	            <td align=left valign=middle class="datacell" width="25%">
        	          <c:if test="${(not empty KualiForm.document.alternateVendorNumber) and fullEntryMode}" >
            	 			<html:radio property="document.goodsDeliveredVendorNumber" 
		            		    		value="${KualiForm.document.vendorNumber}"> 
		            		<c:out value="${KualiForm.document.vendorName}"/></html:radio>
		         			<html:radio property="document.goodsDeliveredVendorNumber" 
		            		    		value="${KualiForm.document.alternateVendorNumber}"> 
		            		<c:out value="${KualiForm.document.alternateVendorName}"/>
		            		</html:radio>
					   </c:if>
			           <c:if test="${((empty KualiForm.document.alternateVendorNumber) and fullEntryMode) or not fullEntryMode}" > 
			              	  <kul:htmlControlAttribute attributeEntry="${documentAttributes.goodsDeliveredVendorName}" property="document.goodsDeliveredVendorName" readOnly="true" />
		    	       </c:if>
		    	    </td>    
                 </tr>
			</c:if>

			<%-- If PO not available, display all the vendor editable fields to allow the user to key --%>

            <c:if test="${!isPOAvailable}" > 
	            <tr>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorName}" 
	                    						  property="document.vendorName" 
	                    						  readOnly="${not (fullEntryMode) or vendorReadOnly}" 
	                    						  tabindexOverride="${tabindexOverrideBase + 0}"/>
	                    <c:if test="${fullEntryMode}" >
	                        <kul:lookup  boClassName="org.kuali.kfs.vnd.businessobject.VendorDetail" 
	                        			 lookupParameters="'Y':activeIndicator, 'PO':vendorHeader.vendorTypeCode"
	                        			 fieldConversions="vendorNumber:document.vendorNumber,vendorHeaderGeneratedIdentifier:document.vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier:document.vendorDetailAssignedIdentifier,defaultAddressLine1:document.vendorLine1Address,defaultAddressLine2:document.vendorLine2Address,defaultAddressCity:document.vendorCityName,defaultAddressPostalCode:document.vendorPostalCode,defaultAddressStateCode:document.vendorStateCode,defaultAddressInternationalProvince:document.vendorAddressInternationalProvinceName,defaultAddressCountryCode:document.vendorCountryCode"/>
	                    </c:if>
                	</td>
	                <th align=right valign=middle class="bord-l-b" width="25%">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCityName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell" width="25%">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCityName}" property="document.vendorCityName" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                </td>
	            </tr>
	
	            <tr>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorNumber}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorNumber}" property="document.vendorNumber" readOnly="true" />
	                </td>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorStateCode}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorStateCode}" property="document.vendorStateCode" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                </td>
	            </tr>
	
	            <tr>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorLine1Address}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine1Address}" 
	                    						  property="document.vendorLine1Address" 
	                    						  readOnly="${not fullEntryMode}" 
	                    						  tabindexOverride="${tabindexOverrideBase + 0}"/>
	                    <c:if test="${fullEntryMode}">
	                        <kul:lookup  boClassName="org.kuali.kfs.vnd.businessobject.VendorAddress" 
	                        			 readOnlyFields="active, vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier" 
	                        			 lookupParameters="'Y':active,document.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,document.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier" 
	                        			 fieldConversions="vendorAddressGeneratedIdentifier:document.vendorAddressGeneratedIdentifier"/>
	                    </c:if>
	                </td>
	                <th align=right valign=middle class="bord-l-b">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorAddressInternationalProvinceName}" />
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorAddressInternationalProvinceName}" property="document.vendorAddressInternationalProvinceName" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
	                </td>
	            </tr>
	
	            <tr>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorLine2Address}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine2Address}" property="document.vendorLine2Address" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 0}"/>
	                </td>
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorPostalCode}" /></div>
	                </th>
					<td align=left valign=middle class="datacell">
						<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPostalCode}" property="document.vendorPostalCode" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
					</td>
	            </tr>
	            
	            <tr>
	            	<th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorContact}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorContact}" 
                        						  property="document.vendorContact" 
                        						  readOnly="true"/>   
                        <c:if test="${fullEntryMode}" > 						                   
	                        <kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.VendorContact" 
	                        		    readOnlyFields="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier" 
	                        		    autoSearch="yes" 
	 			                        lookupParameters="document.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,document.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier" 
	                        	        hideReturnLink="true" 
	                        	        extraButtonSource="${ConfigProperties.externalizable.images.url}buttonsmall_return.gif" />     
	                    </c:if>    	                        
                    </td>
	            	<th align=right valign=middle class="bord-l-b">
	            		<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCountryCode}" /></div>
	            	</th>
	            	<td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCountryCode}" property="document.vendorCountryCode"
	                    extraReadOnlyProperty="document.vendorCountry.name" 
	                    readOnly="${not fullEntryMode}" 
	                    tabindexOverride="${tabindexOverrideBase + 5}"/>
	            	</td>
	            </tr>
            
			</c:if>

			<%-- This is common if PO is available or not --%>
			
			<tr>
                <td colspan="4" class="subhead">Shipment Information</td>
            </tr> 
	           
			<tr>
				 <th align=right valign=middle class="bord-l-b">
                   	 <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.trackingNumber}" /></div>
                 </th>
                 <td align=left valign=middle class="datacell">
                   	 <kul:htmlControlAttribute attributeEntry="${documentAttributes.trackingNumber}" property="document.trackingNumber" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                 </td>
				 <th align=right valign=middle class="bord-l-b">
	                 <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentReceivedDate}" /></div>
	             </th>
	             <td align=left valign=middle class="datacell">
	                 <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentReceivedDate}" property="document.shipmentReceivedDate" datePicker="true" readOnly="${not fullEntryMode}"/>
	             </td>
            </tr>
			<tr>
            	<th align=right valign=middle class="bord-l-b">
                  		<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentPackingSlipNumber}" /></div>
                </th>
   	            <td align=left valign=middle class="datacell">
       	           <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentPackingSlipNumber}" property="document.shipmentPackingSlipNumber" readOnly="${not fullEntryMode}"/>
           	    </td>
           	    <th align=right valign=middle class="bord-l-b">
                  		<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentReferenceNumber}" /></div>
                </th>
   	            <td align=left valign=middle class="datacell">
       	           <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentReferenceNumber}" property="document.shipmentReferenceNumber" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
           	    </td>
            </tr>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentBillOfLadingNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentBillOfLadingNumber}" property="document.shipmentBillOfLadingNumber" readOnly="${not fullEntryMode}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                  		<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.carrierCode}" /></div>
               	</th>
               	<td align=left valign=middle class="datacell">
                  		<kul:htmlControlAttribute attributeEntry="${documentAttributes.carrierCode}" property="document.carrier.carrierDescription" readOnly="true"/>
               	</td>
            </tr>     
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.shipmentWeight}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.shipmentWeight}" property="document.shipmentWeight" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                  		<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.noOfCartons}" /></div>
               	</th>
               	<td align=left valign=middle class="datacell">
                  		<kul:htmlControlAttribute attributeEntry="${documentAttributes.noOfCartons}" property="document.noOfCartons" readOnly="${not fullEntryMode}" tabindexOverride="${tabindexOverrideBase + 5}"/>
               	</td>
            </tr>    
      </table> <%-- If PO not available --%>
    </div>
</kul:tab>
