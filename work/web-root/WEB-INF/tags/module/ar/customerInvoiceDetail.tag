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
              
<%@ attribute name="customerInvoiceDetailAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for customer invoice detail fields." %>              
              
<%@ attribute name="readOnly" required="true" description="determines whether the check  will be displayed readonly" %>
<%@ attribute name="rowHeading" required="true" %>
<%@ attribute name="propertyName" required="true" description="name of form property containing the customer invoice document" %>
<%@ attribute name="actionMethod" required="true" description="methodToCall value for actionImage" %>
<%@ attribute name="actionImage" required="true" description="path to image to be displayed in Action column" %>
<%@ attribute name="actionAlt" required="true" description="alt value for actionImage" %>
<%@ attribute name="cssClass" required="true" %>

<tr>
    <kul:htmlAttributeHeaderCell literalLabel="${rowHeading}:" scope="row" rowspan="2">
    </kul:htmlAttributeHeaderCell>
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.invoiceItemQuantity }" property="${propertyName}.invoiceItemQuantity" readOnly="${readOnly}"/>
	</td>
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.invoiceItemCode }" property="${propertyName}.invoiceItemCode" readOnly="${readOnly}"/>
	</td>
	<td align=left class="${cssClass}">
    	<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.invoiceItemUnitOfMeasureCode }" property="${propertyName}.invoiceItemUnitOfMeasureCode" readOnly="${readOnly}"/>
	</td>                
	<td align=left class="${cssClass}">
    	<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.invoiceItemDescription }" property="${propertyName}.invoiceItemDescription" readOnly="${readOnly}"/>
	</td>
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.invoiceItemUnitPrice }" property="${propertyName}.invoiceItemUnitPrice" readOnly="${readOnly}"/>
	</td>
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.amount }" property="${propertyName}.amount" readOnly="${readOnly}"/>
	</td>
	<td align=left class="${cssClass}">
       	<c:choose>
            <c:when test="${readOnly}">
                <kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.invoiceItemServiceDate}" property="${propertyName}.invoiceItemServiceDate" readOnly="${readOnly}" />
            </c:when>
            <c:otherwise>
                <kul:dateInput attributeEntry="${customerInvoiceDetailAttributes.invoiceItemServiceDate}" property="${propertyName}.invoiceItemServiceDate"/>
            </c:otherwise>
        </c:choose>
	</td>
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.accountsReceivableObjectCode }" property="${propertyName}.accountsReceivableObjectCode" readOnly="${readOnly}"/>
		<c:if test="${not readOnly}">
			&nbsp;
			<kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ObjectCode" fieldConversions="financialObjectCode:${propertyName}.accountsReceivableObjectCode"/>
		</c:if>
	</td>
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.accountsReceivableSubObjectCode }" property="${propertyName}.accountsReceivableSubObjectCode" readOnly="${readOnly}"/>
		<c:if test="${not readOnly}">
			&nbsp;
			<kul:lookup boClassName="org.kuali.kfs.coa.businessobject.SubObjCd" fieldConversions="financialSubObjectCode:${propertyName}.accountsReceivableSubObjectCode" lookupParameters="${propertyName}.accountsReceivableObjectCode:financialObjectCode"/>
		</c:if>		
	</td>
	<c:if test="${not readOnly}">
        <td class="${cssClass}" rowspan="2">
        	<div align="center">
        		<html:image property="methodToCall.${actionMethod}" src="${actionImage}" alt="${actionAlt}" title="${actionAlt}" styleClass="tinybutton"/>
        	</div>
        </td>
    </c:if>
</tr>
<ar:customerInvoiceDetailAccountingInfo  propertyName="${propertyName}" cssClass="datacell" readOnly="${readOnly}" />
