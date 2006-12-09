<%--
 Copyright 2006 The Kuali Foundation.
 
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
              description="The DataDictionary entry containing attributes for this row's fields."%>

<div class="h2-container">
    <h2>Requisition Detail</h2>
</div>

<table cellpadding="0" cellspacing="0" class="datatable" summary="Requisition Detail Section">
    <tr>
        <th align=right valign=middle class="bord-l-b">
            <div align="right">Related Documents:</div>
        </th>
        <td align=left valign=middle class="datacell">
        <!-- "View" links only work if you comment out lines 130 and 131 of KualiDocumentActionBase (but don't commit the lines commented out!!) -->
            <!--  html:image property="methodToCall.viewRelatedDocuments" src="images/tinybutton-view.gif" title="View Related Documents" alt="View Related Documents" styleClass="tinybutton"/ -->
            <a href="purapRequisition.do?methodToCall=viewRelatedDocuments" tabindex="1000000" target="purapWindow"  title="View Related Documents">View</a>
        </td>
        <th align=right valign=middle class="bord-l-b">
            <div align="right">Payment History:</div>
        </th>
        <td align=left valign=middle class="datacell">
            <!-- html:image property="methodToCall.viewPaymentHistory" src="images/tinybutton-view.gif" title="View Related Documents" alt="View Related Documents" styleClass="tinybutton"/ -->
            <a href="purapRequisition.do?methodToCall=viewPaymentHistory&docTypeName=KualiRequisitionDocument" tabindex="1000000" target="purapWindow"  title="View Payment History">View</a>
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
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.fundingSourceCode}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <kul:htmlControlAttribute
                property="document.fundingSourceCode"
                attributeEntry="${documentAttributes.fundingSourceCode}"/>
        </td>
    </tr>
</table> 

