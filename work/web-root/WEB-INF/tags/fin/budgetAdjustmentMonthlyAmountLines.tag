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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="fn" uri="/tlds/fn.tld" %>
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>

<%@ attribute name="isSource" required="true"
              description="Boolean whether this group is of source or target lines." %>
<%@ attribute name="accountingLine" required="true"
              description="The name in the form of the accounting line
              being edited or displayed by this row." %>
<%@ attribute name="baselineAccountingLine" required="false"
              description="The name in the form of the baseline accounting line
              from before the most recent edit of this row,
              to put in hidden fields for comparison or reversion." %>

<c:set var="capitalSourceOrTarget" value="${isSource ? 'Source' : 'Target'}"/>
<c:set var="baAttributes" value="${DataDictionary.BudgetAdjustmentSourceAccountingLine.attributes}" />

<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}"/>
<c:set var="currentTab" value="${KualiForm.tabStateJstl}"/>

<%-- default to closed --%>
<c:choose>
    <c:when test="${empty currentTab}">
        <c:set var="isOpen" value="false" />
    </c:when>
    <c:when test="${!empty currentTab}" >
        <c:set var="isOpen" value="${currentTab.open}" />
    </c:when>
</c:choose>


<html:hidden property="tabState[${currentTabIndex}].open" value="${isOpen}" />

<tr>
    <th>&nbsp;</th>
    <td class="total-line" colspan="10" style="padding: 0px;">
        <table class="datatable" style="width: 100%;">
            <tr>
                <td colspan="4" class="tab-subhead" style="border-right: none;">Monthly Lines 
                  <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
                    <html:image property="methodToCall.toggleTab.tab${currentTabIndex}" src="images/tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton"  styleId="tab-${currentTabIndex}-imageToggle" onclick="javascript: return toggleTab(document, ${currentTabIndex}); " />
                 </c:if>
                 <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
                   <html:image  property="methodToCall.toggleTab.tab${currentTabIndex}" src="images/tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${currentTabIndex}-imageToggle" onclick="javascript: return toggleTab(document, ${currentTabIndex}); " />
                 </c:if>
                </td>
            </tr>
        </table>    
            
        <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
            <div style="display: block;" id="tab-${currentTabIndex}-div">
        </c:if>
        <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
            <div style="display: none;" id="tab-${currentTabIndex}-div">
        </c:if>

        <c:if test="${!empty baselineAccountingLine}">
          <html:hidden property="${baselineAccountingLine}.financialDocumentMonth1LineAmount"/>
          <html:hidden property="${baselineAccountingLine}.financialDocumentMonth2LineAmount"/>
          <html:hidden property="${baselineAccountingLine}.financialDocumentMonth3LineAmount"/>
          <html:hidden property="${baselineAccountingLine}.financialDocumentMonth4LineAmount"/>
          <html:hidden property="${baselineAccountingLine}.financialDocumentMonth5LineAmount"/>
          <html:hidden property="${baselineAccountingLine}.financialDocumentMonth6LineAmount"/>
          <html:hidden property="${baselineAccountingLine}.financialDocumentMonth7LineAmount"/>
          <html:hidden property="${baselineAccountingLine}.financialDocumentMonth8LineAmount"/>
          <html:hidden property="${baselineAccountingLine}.financialDocumentMonth9LineAmount"/>
          <html:hidden property="${baselineAccountingLine}.financialDocumentMonth10LineAmount"/>
          <html:hidden property="${baselineAccountingLine}.financialDocumentMonth11LineAmount"/>
          <html:hidden property="${baselineAccountingLine}.financialDocumentMonth12LineAmount"/>
        </c:if>

        <table class="datatable" style="width: 100%;">
            <tr>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${baAttributes.financialDocumentMonth1LineAmount}" readOnly="${!KualiForm.editingMode['fullEntry']}"/></div></th>
                <td align="left" valign="middle"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth1LineAmount}" property="${accountingLine}.financialDocumentMonth1LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}" styleClass="amount"/></td>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${baAttributes.financialDocumentMonth7LineAmount}" readOnly="${!KualiForm.editingMode['fullEntry']}"/></div></th>
                <td align="left" valign="middle"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth7LineAmount}" property="${accountingLine}.financialDocumentMonth7LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}" styleClass="amount"/></td>
            </tr>
            <tr>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${baAttributes.financialDocumentMonth2LineAmount}" readOnly="${!KualiForm.editingMode['fullEntry']}"/></div></th>
                <td align="left" valign="middle"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth2LineAmount}" property="${accountingLine}.financialDocumentMonth2LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}" styleClass="amount"/></td>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${baAttributes.financialDocumentMonth8LineAmount}" readOnly="${!KualiForm.editingMode['fullEntry']}"/></div></th>
                <td align="left" valign="middle"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth8LineAmount}" property="${accountingLine}.financialDocumentMonth8LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}" styleClass="amount"/></td>
            </tr>
            <tr>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${baAttributes.financialDocumentMonth3LineAmount}" readOnly="${!KualiForm.editingMode['fullEntry']}"/></div></th>
                <td align="left" valign="middle"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth3LineAmount}" property="${accountingLine}.financialDocumentMonth3LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}" styleClass="amount"/></td>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${baAttributes.financialDocumentMonth9LineAmount}" readOnly="${!KualiForm.editingMode['fullEntry']}"/></div></th>
                <td align="left" valign="middle"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth9LineAmount}" property="${accountingLine}.financialDocumentMonth9LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}" styleClass="amount"/></td>
            </tr>
            <tr>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${baAttributes.financialDocumentMonth4LineAmount}" readOnly="${!KualiForm.editingMode['fullEntry']}"/></div></th>
                <td align="left" valign="middle"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth4LineAmount}" property="${accountingLine}.financialDocumentMonth4LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}" styleClass="amount"/></td>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${baAttributes.financialDocumentMonth10LineAmount}" readOnly="${!KualiForm.editingMode['fullEntry']}"/></div></th>
                <td align="left" valign="middle"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth10LineAmount}" property="${accountingLine}.financialDocumentMonth10LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}" styleClass="amount"/></td>
            </tr>
            <tr>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${baAttributes.financialDocumentMonth5LineAmount}" readOnly="${!KualiForm.editingMode['fullEntry']}"/></div></th>
                <td align="left" valign="middle"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth5LineAmount}" property="${accountingLine}.financialDocumentMonth5LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}" styleClass="amount"/></td>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${baAttributes.financialDocumentMonth11LineAmount}" readOnly="${!KualiForm.editingMode['fullEntry']}"/></div></th>
                <td align="left" valign="middle"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth11LineAmount}" property="${accountingLine}.financialDocumentMonth11LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}" styleClass="amount"/></td>
            </tr>
            <tr>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${baAttributes.financialDocumentMonth6LineAmount}" readOnly="${!KualiForm.editingMode['fullEntry']}"/></div></th>
                <td align="left" valign="middle"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth6LineAmount}" property="${accountingLine}.financialDocumentMonth6LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}" styleClass="amount"/></td>
                <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${baAttributes.financialDocumentMonth12LineAmount}" readOnly="${!KualiForm.editingMode['fullEntry']}"/></div></th>
                <td align="left" valign="middle"><kul:htmlControlAttribute attributeEntry="${baAttributes.financialDocumentMonth12LineAmount}" property="${accountingLine}.financialDocumentMonth12LineAmount" readOnly="${!KualiForm.editingMode['fullEntry']}" styleClass="amount"/></td>
            </tr>
            
          </table>
        </div>
    </td>
    <th>&nbsp;</th>
</tr>
