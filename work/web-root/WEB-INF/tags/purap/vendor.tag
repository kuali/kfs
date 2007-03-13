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

<%@ attribute name="displayRequisitionFields" required="false"
              description="Boolean to indicate if REQ specific fields should be displayed" %>

<%@ attribute name="displayPurchaseOrderFields" required="false"
              description="Boolean to indicate if PO specific fields should be displayed" %>
              
<%@ attribute name="displayPaymentRequestFields" required="false"
              description="Boolean to indicate if PREQ specific fields should be displayed" %>              
              
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />
<c:set var="vendorReadOnly" value="${readOnly or not empty KualiForm.document.vendorNumber}" />
<c:set var="currentUserCampusCode" value="${UserSession.universalUser.campusCode}" />

<kul:tab tabTitle="Vendor" defaultOpen="true" tabErrorKey="${PurapConstants.VENDOR_ERRORS}">
    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Vendor Info</h2>
        </div>
		<html:hidden property="document.vendorHeaderGeneratedIdentifier" />
		<html:hidden property="document.vendorDetailAssignedIdentifier" />
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Vendor Section">
            <c:if test="${displayPurchaseOrderFields}">
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderVendorChoiceCode}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderVendorChoiceCode}" property="document.purchaseOrderVendorChoiceCode" readOnly="${vendorReadOnly}" />
                    </td>
                    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
                    <td align=left valign=middle class="datacell">&nbsp;</td>
                </tr>
            </c:if>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorName}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorName}" property="document.vendorName" readOnly="${vendorReadOnly}" />
                    <kul:lookup  boClassName="org.kuali.module.purap.bo.VendorDetail" 
                     fieldConversions="vendorHeaderGeneratedIdentifier:document.vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier:document.vendorDetailAssignedIdentifier,defaultAddressLine1:document.vendorLine1Address,defaultAddressLine2:document.vendorLine2Address,defaultAddressCity:document.vendorCityName,defaultAddressPostalCode:document.vendorPostalCode,defaultAddressStateCode:document.vendorStateCode,defaultAddressCountryCode:document.vendorCountryCode"/>
                </td>
                <c:if test="${not displayPaymentRequestFields}">
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.supplierDiversityLabel}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                      <c:if test="${not empty KualiForm.document.vendorDetail.vendorHeader.vendorSupplierDiversities}">
	                          <c:forEach var="item" items="${KualiForm.document.vendorDetail.vendorHeader.vendorSupplierDiversities}" varStatus="status">
	                              <c:if test="${!(status.first)}"><br></c:if>${item.vendorSupplierDiversity.vendorSupplierDiversityDescription}
	                          </c:forEach>
	                      </c:if>&nbsp;
	                </td>
	            </c:if> 
	            <c:if test="${displayPaymentRequestFields}">
	            	<th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderIdentifier}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderIdentifier}" property="document.purchaseOrderIdentifier" />
	                </td>
	            
	            
	            </c:if>   
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorLine1Address}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine1Address}" property="document.vendorLine1Address" />
                    <c:if test="${not empty KualiForm.document.vendorDetail}">
                        <kul:lookup  boClassName="org.kuali.module.purap.bo.VendorAddress" readOnlyFields="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier" autoSearch="yes"
                        lookupParameters="document.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,document.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier" fieldConversions="vendorAddressGeneratedIdentifier:document.vendorAddressGeneratedIdentifier"/>
                    </c:if>
                </td>
                <c:if test="${not displayPaymentRequestFields}">
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorPhoneNumber}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPhoneNumber}" property="document.vendorPhoneNumber" />
	                </td>
                </c:if>
            </tr>
                        
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorLine2Address}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorLine2Address}" property="document.vendorLine2Address" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorFaxNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorFaxNumber}" property="document.vendorFaxNumber" readOnly="readOnly"/>
                </td>
            </tr>
          
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCityName}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCityName}" property="document.vendorCityName" />
                </td>
                <c:if test="${not displayPaymentRequestFields}">
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorContractName}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorContractName}" property="document.vendorContractName" readOnly="true"/>
	                    <kul:lookup  boClassName="org.kuali.module.purap.bo.VendorContract" readOnlyFields="vendorCampusCode" lookupParameters="'${currentUserCampusCode}':vendorCampusCode" fieldConversions="vendorContractGeneratedIdentifier:document.vendorContractGeneratedIdentifier" />
	                </td>
	          </c:if>
            </tr>            
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorStateCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorStateCode}" property="document.vendorStateCode" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCustomerNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCustomerNumber}" property="document.vendorCustomerNumber" />
                </td>
            </tr>  
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorPostalCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPostalCode}" property="document.vendorPostalCode" />
                </td>
                <c:if test="${not displayPaymentRequestFields}">
	                <th align=right valign=middle class="bord-l-b">
	                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorContactsLabel}" /></div>
	                </th>
	                <td align=left valign=middle class="datacell">
	                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorContactsLabel}" property="document.vendorContactsLabel" readOnly="true"/>                    
	                    <kul:lookup  boClassName="org.kuali.module.purap.bo.VendorContact" readOnlyFields="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier" autoSearch="yes" lookupParameters="document.vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,document.vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier" hideReturnLink="true" extraButtonSource="images/buttonsmall_return.gif" />                    
	                </td>
                </c:if>               
            </tr>   
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorCountryCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorCountryCode}" property="document.vendorCountryCode" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorNoteText}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorNoteText}" property="document.vendorNoteText" />
                </td>
            </tr> 
            <c:if test="${displayPurchaseOrderFields}">
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorPaymentTermsCode}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorPaymentTermsCode}" property="document.vendorPaymentTermsCode" readOnly="${vendorReadOnly}" />
                    </td>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternateVendorName}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternateVendorName}" property="document.alternateVendorName" readOnly="true" />
                        <kul:lookup  boClassName="org.kuali.module.purap.bo.VendorDetail" fieldConversions="vendorHeaderGeneratedIdentifier:document.alternateVendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier:document.alternateVendorDetailAssignedIdentifier"/>                    
                    </td>                        
                </tr>
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorShippingTitleCode}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorShippingTitleCode}" property="document.vendorShippingTitleCode" readOnly="${vendorReadOnly}" />
                    </td>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternateVendorNumber}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternateVendorNumber}" property="document.alternateVendorNumber" readOnly="true" />
                    </td>                        
                </tr>
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorShippingPaymentTermsCode}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorShippingPaymentTermsCode}" property="document.vendorShippingPaymentTermsCode" readOnly="${vendorReadOnly}" />
                    </td>
                    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
                    <td align=left valign=middle class="datacell">&nbsp;</td>                        
                </tr>
            </c:if>                                                 
        </table>

        <c:if test="${displayRequisitionFields}">
            <div class="h2-container">
                <h2>Additional Suggested Vendor Names</h2>
            </div>

            <table cellpadding="0" cellspacing="0" class="datatable" summary="Additional Vendor Section">
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternate1VendorName}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternate1VendorName}" property="document.alternate1VendorName" />
                    </td>
                </tr>
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternate2VendorName}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternate2VendorName}" property="document.alternate2VendorName" />
                    </td>
                </tr>
                <tr>
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternate3VendorName}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternate3VendorName}" property="document.alternate3VendorName" />
                    </td>
                </tr>
                <tr>    
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternate4VendorName}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternate4VendorName}" property="document.alternate4VendorName" />
                    </td>
                </tr>
                <tr>    
                    <th align=right valign=middle class="bord-l-b">
                        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.alternate5VendorName}" /></div>
                    </th>
                    <td align=left valign=middle class="datacell">
                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.alternate5VendorName}" property="document.alternate5VendorName" />
                    </td>                                                
                </tr>
            </table>
        </c:if>

    </div>
</kul:tab>