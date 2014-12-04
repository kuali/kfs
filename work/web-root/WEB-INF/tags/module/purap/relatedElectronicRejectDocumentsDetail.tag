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
<%@ attribute name="documentAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="viewList" required="true" %>
<%@ attribute name="limitByPoId" required="false" %>

<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentTypeName}" />

<logic:notEmpty name="KualiForm" property="${viewList}">
    <logic:iterate id="view" name="KualiForm" property="${viewList}" indexId="viewCtr">
        <c:if test="${(empty limitByPoId) or (limitByPoId eq view.purchaseOrderIdentifier)}">
        
            <!-- Local variables, used for each iteration cycle. -->
            <c:set var="paymentRequestId" value="${view.paymentRequestIdentifier}" />
            <c:set var="documentTitle" value="${view.documentLabel}${view.documentIdentifierString}"/>
            <c:set var="tabKey" value="${kfunc:generateTabKey(documentTitle)}" />
            <c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}" />
                  
            <%-- default to close --%>
            <c:choose>
                <c:when test="${empty currentTab}">
                    <c:set var="isOpen" value="false" />
                    <html:hidden property="tabStates(${tabKey})" value="CLOSE" />       
                </c:when>
                <c:when test="${!empty currentTab}">
                    <c:set var="isOpen" value="${currentTab == 'OPEN'}" />
                </c:when>
            </c:choose>
    
            <c:choose>
                <c:when test="${paymentRequestId == null}">
                    <h3>${view.documentLabel} - <a href="<c:out value="${view.url}" />" style="color: #FFF" target="_BLANK"><c:out value="${view.documentIdentifierString}" /></a>
                </c:when>
                <c:otherwise>
                    <h3>${view.documentLabel} - <a href="<c:out value="${view.url}" />" style="color: #FFF" target="_BLANK"><c:out value="${view.documentIdentifierString}" /></a>
                        (Payment Request - ${view.paymentRequestIdentifier})
                </c:otherwise>
            </c:choose>                   
            <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
            <html:image property="methodToCall.toggleTab.tab${tabKey}" 
                src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" 
                alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
                onclick="javascript: return toggleTab(document, '${tabKey}'); " />
            </c:if>
            <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
            <html:image property="methodToCall.toggleTab.tab${tabKey}" 
                src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" 
                alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
                onclick="javascript: return toggleTab(document, '${tabKey}'); " />
            </c:if>
            </h3>
            
            <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
                <div style="display: block;" id="tab-${tabKey}-div">
            </c:if>
            <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
                <div style="display: none;" id="tab-${tabKey}-div">
            </c:if>
            <table cellpadding="0" cellspacing="0" class="datatable" summary="Notes">
                <c:if test="${!empty view.notes}">
                    <tr>
                        <kul:htmlAttributeHeaderCell scope="col" width="15%">Date</kul:htmlAttributeHeaderCell>
                        <kul:htmlAttributeHeaderCell scope="col" width="15%">User</kul:htmlAttributeHeaderCell>
                        <kul:htmlAttributeHeaderCell scope="col" width="70%">Note</kul:htmlAttributeHeaderCell>
                    </tr>
                    <c:forEach items="${view.notes}" var="note" >
                        <tr>
                            <td align="center" valign="middle" class="datacell">
                                <c:out value="${note.notePostedTimestamp}" />
                            </td>
                            <td align="center" valign="middle" class="datacell">
                                <c:out value="${note.authorUniversal.name}" />
                            </td>
                            <td align="left" valign="middle" class="datacell">
                                <c:out value="${note.noteText}" />
                            </td>
                        </tr>
                    </c:forEach>
                </c:if> 
                <c:if test="${empty view.notes}">
                    <tr>
                        <th align="center" valign="middle" class="bord-l-b">No Notes</th>
                    </tr>
                </c:if> 
            </table>
            </div>
            
            <c:set var="viewShown" value="true"/>
        </c:if>     
    </logic:iterate>
    
    <c:if test="${viewShown}">
        <br />
        <br />
    </c:if>
</logic:notEmpty>


