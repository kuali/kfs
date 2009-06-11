<%--
 Copyright 2007 The Kuali Foundation.
 
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

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<%@ attribute name="documentAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="groupList" required="true" %>
<%@ attribute name="limitByPoId" required="true" %>

<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentType}" />
<c:set var="isATypeOfPODoc" value="${KualiForm.document.isATypeOfPODoc}" />

<c:if test="${KualiForm.document.needWarningRelatedPOs}">
	<font color="black"><bean:message key="${PurapConstants.WARNING_PURCHASEORDER_NUMBER_DONT_DISCLOSE}" /></font>
    <br>
</c:if>
	
<logic:notEmpty name="KualiForm" property="${groupList}">	   		
	<logic:iterate id="group" name="KualiForm" property="${groupList}" indexId="groupCtr">
		<c:forEach items="${group.views}" var="view" varStatus="viewCtr">					
			<c:if test="${(empty limitByPoId) or (limitByPoId eq view.purapDocumentIdentifier)}">
			    <c:choose>
			        <c:when test="${view.purchaseOrderCurrentIndicator}">
						<c:set var="documentTitle" value="${view.documentLabel}${view.purapDocumentIdentifier}"/>
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

						<h3> ${view.documentLabel} - 
        	            	<a href="<c:out value="${view.url}" />" style="color: #FFF" target="_BLANK"><c:out value="${view.purapDocumentIdentifier}" /></a>
        	            	<c:if test="${view.needWarning}" >
        	            		&nbsp;<font color="white">UNAPPROVED</font>
        	            	</c:if>
        	            	<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
							<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
										onclick="javascript: return toggleTab(document, '${tabKey}'); " />
							</c:if>
							<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
							<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
										onclick="javascript: return toggleTab(document, '${tabKey}'); " />
							</c:if>
						</h3>
				    	<c:if test="${not empty view.notes}">
				    		<c:set var="notes" value="${view.notes}"/>
				    	</c:if>				
			        </c:when>
			        <c:otherwise>
						<c:set var="documentTitle" value="${view.documentLabel}${view.purapDocumentIdentifier}"/>
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
			            <c:if test="${viewCtr.count eq 1}">
                            <h3><c:out value="${view.documentLabel}"/>
							<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
							<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
										onclick="javascript: return toggleTab(document, '${tabKey}'); " />
							</c:if>
							<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
							<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
										onclick="javascript: return toggleTab(document, '${tabKey}'); " />
							</c:if>
							</h3>
			            </c:if>
						<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
							<div style="display: block;" id="tab-${tabKey}-div">
						</c:if>
						<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
							<div style="display: none;" id="tab-${tabKey}-div">
						</c:if>
                        <h3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${view.documentLabel} - <c:out value="Doc #"/> <a href="<c:out value="${view.url}" />"  target="_BLANK"><c:out value="${view.documentNumber}" /></a></h3>
			        	</div>
			        </c:otherwise>
			    </c:choose>
	        </c:if>
            <c:if test="${(not empty limitByPoId) and (limitByPoId eq view.purapDocumentIdentifier)}">
		    	<c:set var="viewShown" value="true"/>
		    </c:if>
		</c:forEach>

		<!--  Only display the notes if the document type is not Purchase Order -->
		<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
			<div style="display: block;" id="tab-${tabKey}-div">
		</c:if>
		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
			<div style="display: none;" id="tab-${tabKey}-div">
		</c:if>

		<c:if test="${((empty limitByPoId) or viewShown) and (not isATypeOfPODoc)}">                    	                    	
            <table cellpadding="0" cellspacing="0" class="datatable" summary="Notes">
    	        <c:if test="${not empty notes}">
			        <tr>
						<kul:htmlAttributeHeaderCell scope="col" width="15%">Date</kul:htmlAttributeHeaderCell>
	        			<kul:htmlAttributeHeaderCell scope="col" width="15%">User</kul:htmlAttributeHeaderCell>
			        	<kul:htmlAttributeHeaderCell scope="col" width="70%">Note</kul:htmlAttributeHeaderCell>
		        	</tr>
       				<c:forEach items="${notes}" var="note" >
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
			        <c:set var="notes" value=""/>
				</c:if>	
			</table>		    		
		</c:if>
		</div>
	</logic:iterate>
	
    <c:if test="${(empty limitByPoId) or viewShown}">
		<c:if test="${isATypeOfPODoc}">
			<br/>
			<h3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="Please refer to the Notes and Attachments Tab for the Purchase Order Notes"/></h3>
		</c:if>
		<br />
	   	<br />
	</c:if>
</logic:notEmpty>

