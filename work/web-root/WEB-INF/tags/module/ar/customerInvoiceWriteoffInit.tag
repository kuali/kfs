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

<c:set var="documentAttributes" value="${DataDictionary.CustomerInvoiceWriteoffDocument.attributes}" />

<kul:tabTop tabTitle="Customer Invoice Writeoff Initiation" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_WRITEOFF_INIT_ERRORS}">
    <div class="tab-container" align=center>
            <h3>Customer Invoice Writeoff Initiation</h3>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Customer Invoice Writeoff Init Section" >
            <tr>
                <th align="right" valign="middle" class="bord-l-b" >
                   <div align="right">
                   	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.financialDocumentReferenceInvoiceNumber}" />
                   </div>
                </th>
                <td align="left" valign="middle" class="datacell" style="width: 50%;" >
                   <kul:htmlControlAttribute
                       attributeEntry="${documentAttributes.financialDocumentReferenceInvoiceNumber}"
                       property="document.financialDocumentReferenceInvoiceNumber"
                       readOnly="false" />
                   <kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.CustomerInvoiceLookup"  fieldConversions="invoiceNumber:document.financialDocumentReferenceInvoiceNumber" />    
                </td>
            </tr>
		</table> 
    </div>
</kul:tabTop>
