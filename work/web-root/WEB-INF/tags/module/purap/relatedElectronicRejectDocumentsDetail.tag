<%--
 Copyright 2010 The Kuali Foundation
 
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


