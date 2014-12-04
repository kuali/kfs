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
              description="The DataDictionary entry containing attributes for this row's fields."%>

    <h3>Requisition Detail</h3>

<table cellpadding="0" cellspacing="0" class="datatable" summary="Requisition Detail Section">
    <tr>
        <th align=right valign=middle class="bord-l-b">
            <div align="right">Related Documents:</div>
        </th>
        <td align=left valign=middle class="datacell">
        <!-- "View" links only work if you comment out lines 130 and 131 of KualiDocumentActionBase (but don't commit the lines commented out!!) -->
            <!--  html:image property="methodToCall.viewRelatedDocuments" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="View Related Documents" alt="View Related Documents" styleClass="tinybutton"/ -->
            <a href="purapRequisition.do?methodToCall=viewRelatedDocuments" tabindex="1000000" target="purapWindow"  title="View Related Documents">View</a>
        </td>
        <th align=right valign=middle class="bord-l-b">
            <div align="right">Payment History:</div>
        </th>
        <td align=left valign=middle class="datacell">
            <!-- html:image property="methodToCall.viewPaymentHistory" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="View Related Documents" alt="View Related Documents" styleClass="tinybutton"/ -->
            <a href="purapRequisition.do?methodToCall=viewPaymentHistory&docTypeName=RequisitionDocument" tabindex="1000000" target="purapWindow"  title="View Payment History">View</a>
        </td>
    </tr>
    <tr>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.contractManagerCode}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <kul:htmlControlAttribute 
                property="document.contractManagerCode" 
                attributeEntry="${documentAttributes.contractManagerCode}" 
                readOnly="true" />
        </td>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.documentFundingSourceCode}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <kul:htmlControlAttribute
                property="document.documentFundingSourceCode"
                attributeEntry="${documentAttributes.documentFundingSourceCode}"/>
        </td>
    </tr>
</table> 

