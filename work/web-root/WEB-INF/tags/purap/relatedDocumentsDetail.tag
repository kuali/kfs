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

	   	<logic:notEmpty name="KualiForm" property="${viewList}">
			<logic:iterate id="view" name="KualiForm" property="${viewList}" indexId="viewCtr">
			    <div class="h2-container">
			        <h2><c:out value="${documentTypeLabel}"/> - <a href="<c:out value="${view.url}" />" style="color: #FFF" target="_BLANK"><c:out value="${view.documentIdentifierString}" /></a></h2>
			    </div>
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
			    	<c:if test="${empty view.notes}">
				        <tr>
				            <th align="center" valign="middle" class="bord-l-b">No Notes</th>
				        </tr>
					</c:if>	
		    	</table>
	       	</logic:iterate>
		    <br />
		    <br />
		</logic:notEmpty>

