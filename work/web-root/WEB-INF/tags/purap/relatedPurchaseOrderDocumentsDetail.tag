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
<%@ attribute name="documentAttributes" required="true" type="java.util.Map" 
              description="The DataDictionary entry containing attributes for this row's fields."%>

<%@ attribute name="viewList" required="true" %>
<%@ attribute name="documentTypeLabel" required="true" %>

<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentType}" />
<c:choose>
    <c:when test= "${fn:contains(documentType, 'PurchaseOrder')}">
        <c:set var="isATypeOfPODoc" value="true" />
    </c:when>
    <c:otherwise>
        <c:set var="isATypeOfPODoc" value="false" />
    </c:otherwise>
</c:choose>

	   	<logic:notEmpty name="KualiForm" property="${viewList}">
			<logic:iterate id="view" name="KualiForm" property="${viewList}" indexId="viewCtr">
			    <c:choose>
			        <c:when test= "${view.purchaseOrderCurrentIndicator}">
        	            <div class="h2-container">
        	                <h2><c:out value="${documentTypeLabel}"/> - <a href="<c:out value="${view.url}" />" style="color: #FFF" target="_BLANK"><c:out value="${view.purapDocumentIdentifier}" /></a></h2>
			            </div>
			        </c:when>
			        <c:otherwise>
			            <c:if test="${viewCtr == 0}">
                            <div class="h2-container">
                                <h2><c:out value="${documentTypeLabel}"/></h2>
                            </div>			                
			            </c:if>
                        <div class="h2">
                            <h2 style="color: #6b6b6b">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <c:out value="Doc #"/> <a href="<c:out value="${view.url}" />"  target="_BLANK"><c:out value="${view.documentNumber}" /></a></h2>
                        </div>			        
			        </c:otherwise>
			    </c:choose>
			</logic:iterate>
			
			<!--  Only display the notes if the document type is not Purchase Order -->
			<c:choose>
			    <c:when test="${isATypeOfPODoc}">
			        <br/>
                    <div class="h2">
                        <h2 style="color: #6b6b6b">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <c:out value="Please refer to the Notes and Attachments Tab for the Purchase Order Notes"/></h2>
                    </div>			    
			    </c:when>
			    <c:otherwise>

                    <logic:iterate id="view" name="KualiForm" property="${viewList}" indexId="viewCtr">
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
		        	        				<c:out value="${note.authorUniversal.personName}" />
				                		</td>
				                		<td align="left" valign="middle" class="datacell">
			        			        	<c:out value="${note.noteText}" />
        				        		</td>
		        		        	</tr>
				        		</c:forEach>
					        </c:if>	
		    	        </table>
	       	        </logic:iterate>
	       	    </c:otherwise>
            </c:choose>
		    <br />
		    <br />
		</logic:notEmpty>

