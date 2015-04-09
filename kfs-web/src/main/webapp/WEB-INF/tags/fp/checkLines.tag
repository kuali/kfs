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

<%@ attribute name="checkDetailMode" required="true" %>
<%@ attribute name="editingMode" required="true" type="java.util.Map" %>
<%@ attribute name="totalAmount" required="false" %>
<%@ attribute name="displayHidden" required="true" %>

<c:set var="checkBaseAttributes" value="${DataDictionary.CheckBase.attributes}" />

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="columnCount" value="5" />
<c:if test="${!readOnly}">
    <c:set var="columnCount" value="${columnCount + 1}" />
</c:if>

<c:if test="${checkDetailMode}">
        
            <table cellpadding=0 class="datatable" summary="check detail information">
                <tr>
                    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" />
                    <kul:htmlAttributeHeaderCell attributeEntry="${checkBaseAttributes.checkNumber}" />
                    <kul:htmlAttributeHeaderCell attributeEntry="${checkBaseAttributes.checkDate}" />
                    <kul:htmlAttributeHeaderCell attributeEntry="${checkBaseAttributes.description}" />
                    <kul:htmlAttributeHeaderCell attributeEntry="${checkBaseAttributes.amount}" />
                                
                    <c:if test="${!readOnly}">
                        <kul:htmlAttributeHeaderCell literalLabel="Action" />
                    </c:if>
                </tr>
    			<c:if test="${!readOnly}">
            <fp:checkLine readOnly="${readOnly}" rowHeading="add" propertyName="document.currentTransaction.newCheck" actionImage="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" actionAlt="add" actionMethod="addCheck" cssClass="infoline" displayHidden="${displayHidden}" />
          </c:if>            
                <logic:iterate id="check" name="KualiForm" property="document.currentTransaction.moneyInChecks" indexId="ctr">
                    <fp:checkLine readOnly="${readOnly}" rowHeading="${ctr + 1}" propertyName="document.currentTransaction.moneyInCheck[${ctr}]" baselinePropertyName="document.currentTransaction.baselineCheck[${ctr}]" actionImage="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" actionAlt="delete" actionMethod="deleteCheck.line${ctr}" cssClass="datacell" displayHidden="${displayHidden}" />
                </logic:iterate>
    
                <c:if test="${!empty totalAmount}">
                    <tr>
                        <td class="total-line" colspan="4">&nbsp;</td>
                        <td class="total-line"><strong>Total: ${totalAmount}</strong></td>
                        <c:if test="${!readOnly}">
                            <td class="total-line">&nbsp;</td>
                        </c:if>
                    </tr>
                </c:if>
            </table>
</c:if>

<c:if test="${!checkDetailMode}">
    <kul:hiddenTab forceOpen="true">
        <%-- maintain state of hidden checkLines --%>
        <logic:iterate id="check" name="KualiForm" property="document.currentTransaction.moneyInChecks" indexId="ctr">
            <fp:hiddenCheckLine propertyName="document.currentTransaction.moneyInCheck[${ctr}]" baselinePropertyName="document.currentTransaction.baselineCheck[${ctr}]"  displayHidden="${displayHidden}" />
        </logic:iterate>
    </kul:hiddenTab>
</c:if>
