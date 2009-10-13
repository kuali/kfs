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

