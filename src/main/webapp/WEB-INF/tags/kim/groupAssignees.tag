<%--
 Copyright 2009 The Kuali Foundation
 
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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="groupMemberAttributes" value="${DataDictionary.GroupDocumentMember.attributes}" />
<c:set var="groupQualifierAttributes" value="${DataDictionary.GroupDocumentQualifier.attributes}" />

<kul:tab tabTitle="Assignees" defaultOpen="true" tabErrorKey="document.member*">
    <c:if test="${empty KualiForm.document.groupNamespace}">
    <div class="tab-container">Select a Group Namespace to determine if you have the appropriate permission to add members to this group</div>
    </c:if>
	<div class="tab-container" align="center">
	    <kul:tableRenderPagingBanner pageNumber="${KualiForm.memberTableMetadata.viewedPageNumber}"
	                                totalPages="${KualiForm.memberTableMetadata.totalNumberOfPages}"
	                                firstDisplayedRow="${KualiForm.memberTableMetadata.firstRowIndex}" 
	                                lastDisplayedRow="${KualiForm.memberTableMetadata.lastRowIndex}"
	                                resultsActualSize="${KualiForm.memberTableMetadata.resultsActualSize}" 
	                                resultsLimitedSize="${KualiForm.memberTableMetadata.resultsLimitedSize}"
	                                buttonExtraParams=".anchor${currentTabIndex}"/>
	    <input type="hidden" name="memberTableMetadata.${Constants.TableRenderConstants.PREVIOUSLY_SORTED_COLUMN_INDEX_PARAM}" value="${KualiForm.memberTableMetadata.columnToSortIndex}"/>
	    <input type="hidden" name="memberTableMetadata.sortDescending" value="${KualiForm.memberTableMetadata.sortDescending}"/>
	    <input type="hidden" name="memberTableMetadata.viewedPageNumber" value="${KualiForm.memberTableMetadata.viewedPageNumber}"/>
	    <c:if test="${canAssignGroup}">
	      <table cellpadding="0" cellspacing="0" summary="">
	    	  <tr>
                <td colspan=8 class="tab-subhead">Add Member: 
             </td> 
              </tr>
	          <tr>
	        		<th>&nbsp;</th> 
	        		<kul:htmlAttributeHeaderCell attributeEntry="${groupMemberAttributes.memberTypeCode}" horizontal="false" />
	        		<kul:htmlAttributeHeaderCell attributeEntry="${groupMemberAttributes.memberId}" horizontal="false" />
	        		<c:if test='${KualiForm.member.memberTypeCode == "G"}'>
	        		  <kul:htmlAttributeHeaderCell attributeEntry="${groupMemberAttributes.memberNamespaceCode}" horizontal="false" />
	        		</c:if>  
	        		<kul:htmlAttributeHeaderCell attributeEntry="${groupMemberAttributes.memberName}" horizontal="false" />
	        		<kul:htmlAttributeHeaderCell attributeEntry="${groupMemberAttributes.activeFromDate}" horizontal="false" />
	        		<kul:htmlAttributeHeaderCell attributeEntry="${groupMemberAttributes.activeToDate}" horizontal="false" />
					<c:if test="${canAssignGroup}">	
	            		<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col" horizontal="false" />
					</c:if>	
	          </tr> 
	    	  	
	          <tr>
					<th class="infoline">Add:</th>
	                <td align="left" valign="middle" class="infoline">
		                <div align="center">
		                	<kul:htmlControlAttribute property="member.memberTypeCode" 
		                	attributeEntry="${groupMemberAttributes.memberTypeCode}" 
		                	onchange="changeMemberTypeCode( this.form );" disabled="${readOnly}" />
							<NOSCRIPT>
                                <input type="image" tabindex="32768" name="methodToCall.changeMemberTypeCode" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-refresh.gif" class="tinybutton" title="Click to refresh the page after changing the member type." alt="Click to refresh the page after changing the member type." />
							</NOSCRIPT>                
			            </div>
		            	<c:set var="bo" value="${KualiForm.memberBusinessObjectName}"/>
		            	<c:set var="fc" value="${KualiForm.memberFieldConversions}"/>
					</td>
	                <td class="infoline">   
	                <div align="center">             	
						<kul:htmlControlAttribute property="member.memberId" attributeEntry="${groupMemberAttributes.memberId}" readOnly="${readOnly}"/>
						<c:if test="${!readOnly}">
			               	<kul:lookup boClassName="${bo}" fieldConversions="${fc}" anchor="${tabKey}" />
		               	</c:if>
					</div>
					</td>
					<c:if test='${KualiForm.member.memberTypeCode == "G"}'>
					  <td class="infoline">   
	                    <div align="center">             	
						  <kul:htmlControlAttribute property="member.memberNamespaceCode" attributeEntry="${groupMemberAttributes.memberNamespaceCode}" readOnly="${readOnly}" />
					    </div>
					  </td>
					</c:if>  
	                <td class="infoline">    
	                  <div align="center">             	
					    <kul:htmlControlAttribute property="member.memberName" attributeEntry="${groupMemberAttributes.memberName}" readOnly="${readOnly}" />
					    <c:if test="${!readOnly}">
			              <kul:lookup boClassName="${bo}" fieldConversions="${fc}" anchor="${tabKey}" />
		                </c:if>
					  </div>
					</td>
					  
	                <td align="left" valign="middle" class="infoline">
	                <div align="center">
	                	<kul:htmlControlAttribute property="member.activeFromDate" attributeEntry="${groupMemberAttributes.activeFromDate}" datePicker="true" readOnly="${readOnly}" />
	                </div>
	                </td>
	                <td align="left" valign="middle" class="infoline">
	                <div align="center">
	                	<kul:htmlControlAttribute property="member.activeToDate" attributeEntry="${groupMemberAttributes.activeToDate}" datePicker="true" readOnly="${readOnly}" />
	                </div>
	                </td>
	                <td class="infoline">
       					<div align="center">
							<html:image property="methodToCall.addMember.anchor${tabKey}"
							src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
                        </div>
                    </td>
	    	 </tr>       
		  </table>	
		</c:if>  
	    <br /><br />
	    <table cellpadding="0" cellspacing="0" summary="">
	    	  <tr>
                <td colspan=9 class="tab-subhead">Members:</td>
              </tr>
	        	<tr>
	        		<th>&nbsp;<input type="hidden" id="sortMethodToCallPlaceholder" name="sortMethodToCallPlaceholder" value="placeholder"/></th>
	        		<kul:htmlAttributeHeaderCell attributeEntry="${groupMemberAttributes.memberTypeCode}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.memberTypeCode';submitForm();" />
                    <kul:htmlAttributeHeaderCell attributeEntry="${groupMemberAttributes.memberId}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.memberId';submitForm();" />
	        		<kul:htmlAttributeHeaderCell attributeEntry="${groupMemberAttributes.memberNamespaceCode}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.memberNamespaceCode';submitForm();" />
	        		<kul:htmlAttributeHeaderCell attributeEntry="${groupMemberAttributes.memberName}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.memberName';submitForm();" />
	        		<kul:htmlAttributeHeaderCell attributeEntry="${groupMemberAttributes.memberFullName}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.memberFullName';submitForm();" />
	        		<kul:htmlAttributeHeaderCell attributeEntry="${groupMemberAttributes.activeFromDate}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.activeFromDate';submitForm();" />
	        		<kul:htmlAttributeHeaderCell attributeEntry="${groupMemberAttributes.activeToDate}" horizontal="false" headerLink="javascript:document.forms[0].sortMethodToCallPlaceholder.name='methodToCall.sort.activeToDate';submitForm();" />
					<c:if test="${canAssignGroup}">	
	            		<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col" horizontal="false" />
					</c:if>	
	        	</tr> 
			<c:if test="${KualiForm.memberTableMetadata.firstRowIndex >= 0}">
	      	<c:forEach var="member" items="${KualiForm.document.members}" varStatus="statusMember"
                 begin="${KualiForm.memberTableMetadata.firstRowIndex}" 
                 end="${KualiForm.memberTableMetadata.lastRowIndex}">
                 <c:set var="inquiryClass" value="org.kuali.rice.kim.api.identity.Person" />
                 <c:set var="keyValue" value="principalId" />
                 <c:if test='${member.memberTypeCode == "G"}'>
                   <c:set var="inquiryClass" value="org.kuali.rice.kim.impl.group.GroupBo" />
                   <c:set var="keyValue" value="id" />
                 </c:if>
	             <tr>
					<th class="infoline" valign="top">
						<c:out value="${statusMember.index+1}" />
					</th>
		            <td align="left" valign="middle">
		               	<div align="center"><kul:htmlControlAttribute property="document.members[${statusMember.index}].memberTypeCode"  attributeEntry="${groupMemberAttributes.memberTypeCode}" disabled="true" readOnly="false" />
						</div>
					</td>
		            <td align="left" valign="middle">
		               	<div align="center"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].memberId"  attributeEntry="${groupMemberAttributes.memberId}" readOnly="true" />
						</div>
					</td>
					<td align="left" valign="middle">						            
                     	<div align="center"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].memberNamespaceCode"  attributeEntry="${groupMemberAttributes.memberNamespaceCode}" readOnly="true"  />
						</div>
                    </td>
					<td align="left" valign="middle">	
						<div align="center">						            
										            
                    		<kul:inquiry boClassName="${inquiryClass}" keyValues="${keyValue}=${member.memberId}" render="true">
                        		<kul:htmlControlAttribute property="document.members[${statusMember.index}].memberName"  attributeEntry="${groupMemberAttributes.memberName}" readOnly="true"  />
							</div>
                    	</kul:inquiry>
                    </td>	
		            <td align="left" valign="middle">	
		            	<div align="center">						            			            
	                    	<kul:inquiry boClassName="${inquiryClass}" keyValues="${keyValue}=${member.memberId}" render="true">
    	                    	<kul:htmlControlAttribute property="document.members[${statusMember.index}].memberFullName"  attributeEntry="${groupMemberAttributes.memberFullName}" readOnly="true"  />
                    		</kul:inquiry>
                    	</div>
                    </td>
					</td>
		            <td align="left" valign="middle">
		               	<div align="center"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].activeFromDate"  attributeEntry="${groupMemberAttributes.activeFromDate}" readOnly="${!canAssignGroup}" datePicker="true" />
						</div>
					</td>
		            <td align="left" valign="middle">
		               	<div align="center"> <kul:htmlControlAttribute property="document.members[${statusMember.index}].activeToDate"  attributeEntry="${groupMemberAttributes.activeToDate}" readOnly="${!canAssignGroup}" datePicker="true" />
						</div>
					</td>
					<c:if test="${canAssignGroup}">
                        <td><div align="center">                             
                            <html:image property='methodToCall.deleteMember.line${statusMember.index}.anchor${currentTabIndex}'
                            src='${ConfigProperties.kr.externalizable.images.url}tinybutton-inactivate.gif' styleClass='tinybutton'/>
                        </div></td>
                    </c:if>
				</tr>
			</c:forEach>        
			</c:if>
		</table>
		
		<br />
	</div>
</kul:tab>
