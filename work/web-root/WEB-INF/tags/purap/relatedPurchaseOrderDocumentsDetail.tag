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

<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
<%@ attribute name="documentAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="viewList" required="true" %>
<%@ attribute name="limitByPoId" required="true" %>

<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentType}" />
<c:set var="isATypeOfPODoc" value="${KualiForm.document.isATypeOfPODoc}" />

<logic:notEmpty name="KualiForm" property="${viewList}">	   		
	<logic:iterate id="group" name="KualiForm" property="${viewList}" indexId="groupCtr">
		<c:forEach items="${group.views}" var="view" varStatus="viewCtr">					
			<c:if test="${(empty limitByPoId) or (limitByPoId eq view.purapDocumentIdentifier)}">
			    <c:choose>
			        <c:when test= "${view.purchaseOrderCurrentIndicator}">
        	            <h3> ${view.documentLabel} - <a href="<c:out value="${view.url}" />" style="color: #FFF" target="_BLANK"><c:out value="${view.purapDocumentIdentifier}" /></a></h3>
				    	<c:if test="${not empty view.notes}">
				    		<c:set var="notes" value="${view.notes}"/>
				    	</c:if>				
			        </c:when>
			        <c:otherwise>
			            <c:if test="${viewCtr.count eq 1}">
                            <h3><c:out value="${view.documentLabel}"/></h3>
			            </c:if>
                        <h3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${view.documentLabel} - <c:out value="Doc #"/> <a href="<c:out value="${view.url}" />"  target="_BLANK"><c:out value="${view.documentNumber}" /></a></h3>
			        </c:otherwise>
			    </c:choose>
	        </c:if>
            <c:if test="${(not empty limitByPoId) and (limitByPoId eq view.purapDocumentIdentifier)}">
		    	<c:set var="viewShown" value="true"/>
		    </c:if>
		</c:forEach>

		<!--  Only display the notes if the document type is not Purchase Order -->
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
	</logic:iterate>
	
	<c:if test="${isATypeOfPODoc}">
		<br/>
		<h3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="Please refer to the Notes and Attachments Tab for the Purchase Order Notes"/></h3>
	</c:if>
    <c:if test="${(empty limitByPoId) or viewShown}">
		<br />
	   	<br />
	</c:if>
</logic:notEmpty>

