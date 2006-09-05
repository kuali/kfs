<%@ include file="/jsp/core/tldHeader.jsp" %>
<kul:page showDocumentInfo="true" docTitle="General Ledger Correction Process"
	htmlFormAction="generalLedgerCorrection"transactionalDocument="false"
	renderMultipart="true" showTabButtons="true">


    <kul:hiddenDocumentFields isFinancialDocument="false" />
    <kul:documentOverview editingMode="${KualiForm.editingMode}"/>
    <input type="hidden" name="document.correctionChangeGroupNextLineNumber" value="<c:out value="${KualiForm.document.correctionChangeGroupNextLineNumber}" />" />
	
	
 	
 	

	<!-- result -->
<kul:tab tabTitle="Correction Result" defaultOpen="true" tabErrorKey="Correction Result">
   <c:if test="${KualiForm.document.correctionRowCount > 0}" >  
   
   
   <div class="tab-container" align="center"> 
	         <table cellpadding=0 class="datatable" summary=""> 
    	         <tr>
        	        <td align="left" valign="middle" class="subhead"><span class="subhead-left">Correction Result</span></td>
            	 </tr>
			</table>
			<table>
             <tr> 
             
             <td width="20%" align="left" valign="middle" > Total Debits/Blanks: </td> 
             <td align="left" valign="middle" > <fmt:formatNumber value="${KualiForm.document.correctionDebitTotalAmount}" groupingUsed="true" minFractionDigits="2"/></td>
			 </tr>
			 <tr>
			 
			 <td width="20%" align="left" valign="middle" > Total Credits: </td> 
             <td align="left" valign="middle" > <fmt:formatNumber value="${KualiForm.document.correctionCreditTotalAmount}" groupingUsed="true" minFractionDigits="2"/></td>
			 
			 </tr>
			 <tr>
			 <td width="20%" align="left" valign="middle" > Rows output: </td> 
             <td align="left" valign="middle" > <fmt:formatNumber value="${KualiForm.document.correctionRowCount}" groupingUsed="true"/></td>
			 </tr>
   		</table>
   	</div>

   </c:if>
   	</kul:tab>   
   


 
 	
 	<%-- for display --%>
 <c:if test="${KualiForm.command !='displayDocSearchView'}">
 	
 	<kul:tab tabTitle="Correction Process" defaultOpen="true" tabErrorKey="Correction Process">
 	
 	<div class="tab-container" align="center" >
      
    	<table cellpadding=0 class="datatable" summary=""> 
        	  <tr>
                    <td align="left" valign="middle" class="subhead"><span class="subhead-left"></span>Select System and Edit Method</td>
         	  </tr>
	    		<tr>
     	 			<td>
		     			<center>
							<html:select property="chooseSystem">
							<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|core|lookup|keyvalues|CorrectionChooseSystemValuesFinder" label="label" value="key"/>
							</html:select>
               
      					    <html:select property="editMethod">
							<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|core|lookup|keyvalues|CorrectionEditMethodValuesFinder" label="label" value="key"/>
							</html:select>
							
							<html:image property="methodToCall.chooseMainDropdown.anchor${currentTabIndex}" 
										src="images/tinybutton-select.gif" styleClass="tinybutton" alt="chooseMainDropdown" />
		   				  </center>
    				  </td>
	  			</tr>
		</table>
 	</div>
 	
 </kul:tab>	
 
 
<kul:tab tabTitle="Documents in System" defaultOpen="true" tabErrorKey="Documents in System">      
<c:if test="${KualiForm.chooseSystem == 'system'}" >

	


          <div class="tab-container" align="center" > 
            <table cellpadding=0 class="datatable" summary="">
            
            <tr>
	             <td align="left" valign="middle" class="subhead"><span class="subhead-left"></span>Documents in System</td>
            </tr>
            <tr>
                <td colspan="2" class="bord-l-b" style="padding: 4px; vertical-align: top;"> 
                 <center>
                        <label for="pending-origin-entry-group-id"><strong>Origin Entry Group</strong></label><br/><br/>
                        
                        <html:select property="groupIdList" size="10" >
							<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|core|lookup|keyvalues|CorrectionGroupEntriesFinder" label="label" value="key" />
						</html:select>
                        
                        
                        
                        <br/><br/>  


	   	    	         	 <html:image property="methodToCall.readDocument.anchor${currentTabIndex}"
	    	                 		src="images/tinybutton-loaddoc.gif" styleClass="tinybutton" alt="ShowAllEntriesForManualEdit" />
			               	
							Copy To Desktop 
							<html:image property="methodToCall.saveToDesktop.anchor${currentTabIndex}"
	    	                 		src="images/tinybutton-copy2.gif" styleClass="tinybutton" alt="saveToDeskTop" onclick="excludeSubmitRestriction=true" />
							Delete Document
							<html:image property="methodToCall.confirmDeleteDocument.anchor${currentTabIndex}"
	    	                 		src="images/tinybutton-delete1.gif" styleClass="tinybutton" alt="deleteDocument" />
						       
			         
	           		</center> 
                </td>
            </tr>
        </table> 
    
 	</div>



</c:if>

</kul:tab>




<kul:tab tabTitle="Correction File Upload" defaultOpen="true" tabErrorKey="Correction File Upload">  

 	 <c:if test="${KualiForm.chooseSystem == 'file'}" >
   		 <!--  File upload control -->
   			 <div class="tab-container" align="center"> 
    
     		   <div class="h2-container">
    		   	 <h2>Corrections File Upload</h2>
    	       </div>
      		  <table cellpadding=0 class="datatable" summary=""> 
 	           	<tr>
    	            <td class="bord-l-b" style="padding: 4px;">
        	        
        	        
        	        <html:file size="30" property="sourceFile" />
        	        <html:image property="methodToCall.uploadFile.anchor${currentTabIndex}" src="images/tinybutton-loaddoc.gif"
                                    styleClass="tinybutton" alt="uploadFile" />
        	     	</td>	    	    
               	</tr>
	        	</table>
   			 </div>

    </c:if>
</kul:tab> 
	

<!--  Search Results Control -->



<kul:tab tabTitle="Search Results" defaultOpen="true" tabErrorKey="Search Results">    
 	<c:if test="${KualiForm.chooseSystem != null && KualiForm.editMethod !=null}" >   
	<%--	<div class="tab-container" align="left" style="overflow: scroll; max-width: 100%;"> --%>
	<div class="tab-container" align="left" STYLE="overflow-x:scroll; overflow-y:hidden"> 
	<kul:errors keyMatch="numberFormatError" />
            <table cellpadding=0 class="datatable" summary=""> 
                <tr>
                    <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Results</span></td>
                </tr>
                <tr>
                    
                      <td>
                	    <input type="hidden" name="d-2339813-p" value="${displayTablePageNumber}"/> 
						<input type="hidden" name="d-2339813-s" value="${displayTableColumnNumber}"/> 
						<input type="hidden" name="d-2339813-d" value="ASC"/>            
					<display:table class="datatable-100" cellspacing="0" cellpadding="0" name="${KualiForm.allEntries}" id="allEntries" pagesize="10"
                      requestURI="generalLedgerCorrection.do?methodToCall=viewResults&document.financialDocumentNumber=${KualiForm.document.documentHeader.financialDocumentNumber}" >
         	         		   <c:choose>
        			     			<c:when test="${KualiForm.editableFlag == 'Y'}">
	    		       		    		<display:column title="Manual Edit" >
    	        			    			<html:image property="methodToCall.showOneEntry.anchor${currentTabIndex}.${allEntries.entryId}" 
             		    						src="images/tinybutton-edit1.gif" styleClass="tinybutton" alt="edit" />
		   									<html:image property="methodToCall.deleteEntry.anchor${currentTabIndex}.${allEntries.entryId}" 
             		    						src="images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete" />
             		       		    	</display:column>
			           				</c:when>
						
									<c:otherwise>
										<display:column title="" > </display:column>
									</c:otherwise>
								</c:choose>
						
						<display:column class="infocell" sortable="true" title="Fiscal Year" >
						<c:out value="${allEntries.universityFiscalYear}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Budget Year" >
						<c:out value="${allEntries.budgetYear}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Chart Code" >
						<c:out value="${allEntries.chartOfAccountsCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Account Number" >
						<c:out value="${allEntries.accountNumber}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Sub Account Number" >
						<c:out value="${allEntries.subAccountNumber}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Object Code" >
						<c:out value="${allEntries.financialObjectCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Sub Object Code" >
						<c:out value="${allEntries.financialSubObjectCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Balance Type" >
						<c:out value="${allEntries.financialBalanceTypeCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Object Type" >
						<c:out value="${allEntries.financialObjectTypeCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Fiscal Period" >
						<c:out value="${allEntries.universityFiscalPeriodCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Document Type" >
						<c:out value="${allEntries.financialDocumentTypeCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Origin Code" >
						<c:out value="${allEntries.financialSystemOriginationCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Document Number" >
						<c:out value="${allEntries.financialDocumentNumber}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Sequence Number" >
						<c:out value="${allEntries.transactionLedgerEntrySequenceNumber}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Description" >
						<c:out value="${allEntries.transactionLedgerEntryDescription}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Amount" >
						<c:out value="${allEntries.transactionLedgerEntryAmount}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Debit Credit Indicator" >
						<c:out value="${allEntries.transactionDebitCreditCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Transaction Date" >
						<c:out value="${allEntries.transactionDate}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Org Doc Number" >
						<c:out value="${allEntries.organizationDocumentNumber}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Project Code" >
						<c:out value="${allEntries.projectCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Org Ref ID" >
						<c:out value="${allEntries.organizationReferenceId}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Ref Doc Type" >
						<c:out value="${allEntries.referenceFinancialDocumentTypeCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Ref Origin Code" >
						<c:out value="${allEntries.referenceFinancialSystemOriginationCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Ref Doc Number" >
						<c:out value="${allEntries.referenceFinancialDocumentNumber}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Reversal Date" >
						<c:out value="${allEntries.financialDocumentReversalDate}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Enc Update Code" >
						<c:out value="${allEntries.transactionEncumbranceUpdateCode}" />&nbsp;</display:column>
					
						</display:table>

                    
                      
                      </td>
                      
                 	
              	  </tr>
              	 
              	  
          	<c:if test="${KualiForm.editMethod == 'manual'}" >        	
		    <c:if test="${KualiForm.editableFlag == 'Y'}" >    
           
                <tr>
                    <td align="left" valign="middle" class="subhead"><span class="subhead-left">Manual Editing</span></td>
                </tr>
			<tr>                
        	<td>
        	

   			<display:table id="eachEntryForManualEdit" name="${KualiForm.eachEntryForManualEdit}" requestURIcontext="false"  >
   			 
	   			<display:column title="Manual Edit" >

	   			<html:image property="methodToCall.editEntry.anchor${currentTabIndex}.${eachEntryForManualEdit.entryId}" value="${eachEntryForManualEdit.entryId}"
  						src="images/tinybutton-saveedits.gif" styleClass="tinybutton" alt="EditAnEntry" />
             	</display:column>
	   			<display:column title="Fiscal Year" >
		   			<html:text property="eachEntryForManualEdit.universityFiscalYear" size="5"  />
	   			</display:column>
				<display:column title="Budget Year" >
		   			<html:text property="eachEntryForManualEdit.budgetYear" size="7"  />
				</display:column>
	   			<display:column title="Chart Code" >
		   			<html:text property="eachEntryForManualEdit.chartOfAccountsCode" size="5"  />
				</display:column>
				<display:column title="Account Number" >
   		   			<html:text property="eachEntryForManualEdit.accountNumber" size="7"  />
        	    </display:column>
				<display:column title="Sub Account Number" >
		   			<html:text property="eachEntryForManualEdit.subAccountNumber" size="7"  />
				</display:column>
				<display:column title="Object Code" >
		   			<html:text property="eachEntryForManualEdit.financialObjectCode" size="5"  />
				</display:column>
				<display:column title="Sub Object Code" >
		   			<html:text property="eachEntryForManualEdit.financialSubObjectCode" size="6"  />
				</display:column>
				<display:column title="Balance Type" >
		   			<html:text property="eachEntryForManualEdit.financialBalanceTypeCode" size="8"  />
				</display:column>
				<display:column title="Object Type" >
		   			<html:text property="eachEntryForManualEdit.financialObjectTypeCode" size="6"  />
				</display:column>
				<display:column title="Fiscal Period" >
		   			<html:text property="eachEntryForManualEdit.universityFiscalPeriodCode" size="6"  />
				</display:column>
				<display:column title="Document Type" >
		   			<html:text property="eachEntryForManualEdit.financialDocumentTypeCode" size="10"  />
				</display:column>
				<display:column title="Origin Code" >
		   			<html:text property="eachEntryForManualEdit.financialSystemOriginationCode" size="6"  />
				</display:column>
				<display:column title="Document Number" >
		   			<html:text property="eachEntryForManualEdit.financialDocumentNumber" size="9"  />
        	    </display:column>	
        	    <display:column title="Sequence Number" >
		   			<html:text property="eachEntryForManualEdit.transactionLedgerEntrySequenceNumber" size="9"  />
				</display:column>
				<display:column title="Description" >
		   			<html:text property="eachEntryForManualEdit.transactionLedgerEntryDescription" size="11"  />
				</display:column>
				<display:column title="Amount" >
		   			<html:text property="eachEntryForManualEdit.transactionLedgerEntryAmount" size="7"  />
				</display:column>
				<display:column title="Debit Credit Indicator" >
		   			<html:text property="eachEntryForManualEdit.transactionDebitCreditCode" size="9"  />
				</display:column>
				<display:column title="Transaction Date" >
		   			<html:text property="eachEntryForManualEdit.transactionDate" size="12"  />
				</display:column>
				<display:column title="Organization Document Number" >
		   			<html:text property="eachEntryForManualEdit.organizationDocumentNumber" size="12"  />
				</display:column>
				<display:column title="Project Code" >
		   			<html:text property="eachEntryForManualEdit.projectCode" size="7"  />
				</display:column>
				<display:column title="Organization Reference Number" >
		   			<html:text property="eachEntryForManualEdit.organizationReferenceId" size="13"  />
				</display:column>
				<display:column title="Reference Document Type" >
		   			<html:text property="eachEntryForManualEdit.referenceFinancialDocumentTypeCode" size="10"  />
				</display:column>
				<display:column title="Reference Origin Code" >
		   			<html:text property="eachEntryForManualEdit.referenceFinancialSystemOriginationCode" size="10"  />
				</display:column>
				<display:column title="Reference Document Number" >
		   			<html:text property="eachEntryForManualEdit.referenceFinancialDocumentNumber" size="9"  />
				</display:column>
				<display:column title="Reversal Date" >
		   			<html:text property="eachEntryForManualEdit.financialDocumentReversalDate" size="8"  />
				</display:column>				
				<display:column title="Transaction Encumbrance Update Code" >
		   			<html:text property="eachEntryForManualEdit.transactionEncumbranceUpdateCode" size="13"  />
				</display:column>
			</display:table>
                        
   			</td>
   			</tr>
   			
   			</c:if>
              	  
              	  
              	  <td>
              	  	<c:if test="${KualiForm.manualEditFlag == 'Y'}" >           
                      	<STRONG> Do you want to edit this document? </STRONG>
                    	  	<html:image property="methodToCall.showEditMethod.anchor${currentTabIndex}" src="images/tinybutton-edit1.gif" styleClass="tinybutton" alt="show edit" />
		   			</c:if>
           			<c:if test="${KualiForm.deleteFileFlag == 'Y'}" >
           				<STRONG> Do you want to delete this document? </STRONG>
           				<html:image property="methodToCall.deleteDocument.anchor${currentTabIndex}"
             		    						src="images/tinybutton-delete1.gif" styleClass="tinybutton" alt="deleteDocument" />
           			</c:if>
           			
           		  </td>
       		
       

	</c:if>
            
            
            
            
            
            
	<c:if test="${KualiForm.editMethod == 'manual'}" >        	
		<c:if test="${KualiForm.editableFlag == 'Y'}" >   
			<tr>
				<td>
   			<div align="center" >	
		   		<html:checkbox property="processInBatch" title="processInBatch" /> <STRONG> Process In Batch </STRONG>
   			</div>
		    </br>
			   	</td>	
		   	</tr>
   		</c:if>  
   </c:if>
      
    </table> 
	</div>
            
        
     
    </c:if>      
</kul:tab>          
            
            
            
            
            



<kul:tab tabTitle="Edit Options and Action" defaultOpen="true" tabErrorKey="Edit Options and Action">    
<c:if test="${KualiForm.editMethod == 'criteria'}" >	


 <div class="tab-container" align="center"> 
 
            <table cellpadding=0 class="datatable" summary="">
       			 <tr>
                    <td align="left" valign="middle" class="subhead"><span class="subhead-left">Edit Options and Action</span></td>
                </tr>
          	  
            <tr>
            	<td>
					<center>
						<html:checkbox property="processInBatch" title="processInBatch" /> <STRONG> Process In Batch </STRONG> &nbsp; &nbsp; &nbsp; &nbsp;  
						<html:checkbox property="matchCriteriaOnly" alt="matchCriteriaOnly"/> <STRONG> Output only records which match criteria? </STRONG>
					</center>
				</td>
			</tr>
			<tr>
				<td>
					<center>
						<STRONG>Show Output Group<STRONG>
						
						<html:image property="methodToCall.showOutputFile.anchor${currentTabIndex}"
	    	                 		src="images/tinybutton-show.gif" styleClass="tinybutton" alt="showOutputFile" />
						
					</center>
				</td>
			</tr>
		</table>				  
	</div>	
	
	</c:if>
</kul:tab>          
           
             
             
             
<kul:tab tabTitle="Edit Criteria" defaultOpen="true" tabErrorKey="Edit Criteria">          

	<c:if test="${KualiForm.editMethod == 'criteria'}" >
 	
    <!-- Search criteria control -->
    <div class="tab-container" align="center"> 
    
        <kul:errors keyMatch="searchFieldError" />
    	
    	 
        <table cellpadding=0 class="datatable" summary="">
        
            <tr>
                <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Criteria</span></td> 
                <td align="left" valign="middle" class="subhead"><span class="subhead-left">Modification Criteria</span></td>
            </tr>
            
            
               <c:forEach var="group" items="${KualiForm.document.correctionChangeGroup}"> 
                   <tr>
                       <td colspan="2" align="left" class="bord-l-b" style="padding: 4px; vertical-align: top;"> 
                           <strong>Group:</strong> 
                          		
                           <html:image property="methodToCall.removeCorrectionGroup.group${group.correctionChangeGroupLineNumber}.anchor${currentTabIndex}"
	    	                 		src="images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete correction group" />
	    	              
                       </td>
                   </tr>
                <tr style="border-bottom: 1px solid #333;"> 
                    <td class="bord-l-b" style="padding: 4px; vertical-align: top;">
                        <input type="hidden" name="correction-groups[<c:out 
                            value="${group.correctionChangeGroupLineNumber}" />][group-number]" value="" /> 
                        <input type="hidden" name="correction-groups[<c:out 
                            value="${group.correctionChangeGroupLineNumber}" />][next-search-criterion-number]" value="<c:out 
                            value="${group.correctionCriteriaNextLineNumber}" />" /> 
                        <input type="hidden" name="correction-groups[<c:out 
                            value="${group.correctionChangeGroupLineNumber}" />][next-replacement-specification-number]" value="<c:out 
                            value="${group.correctionChangeNextLineNumber}" />" />
                        <c:forEach items="${group.correctionCriteria}" var="criterion"> 
                            <div>
                                <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber }" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][field-name]">Field</label>
                                <select id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][field-name]"
                                      name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                        value="${criterion.correctionCriteriaLineNumber}" />][field-name]">
                                    <option value="" selected>Select Search Criteria</option> 
            	                        <c:forEach var="fieldName" items="${KualiForm.fieldNames}">
                	                        <option value="<c:out value="${fieldName.value}" />"<c:if 
                    	                        test="${fieldName.value eq criterion.correctionFieldName}"> selected="true"</c:if>>
                        	                    <c:out value="${fieldName.value}" /> 
                            	            </option>
                                	    </c:forEach>
                                </select>
                                <label for="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][operator]">Operator</label>
                                <select id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][operator]"
                                  name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][operator]">
                                    <c:forEach var="operator" items="${KualiForm.searchOperators}">
                                        <option value="<c:out value="${operator.key }" />"<c:if 
                                            test="${operator.key eq criterion.correctionOperatorCode}">selected="true"</c:if>><c:out 
                                            value="${ operator.value}" /></option>
                                    </c:forEach>
                                </select>
                                <label for="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][field-value]">Value</label>
                                <input id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][field-value]"
                                    value="${ criterion.correctionFieldValue}"
                                    name="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${ criterion.correctionCriteriaLineNumber}" />][field-value]" type="text">
                                
                                
                                <html:image property="methodToCall.removeSearchCriterion.criterion${criterion.correctionChangeGroupLineNumber}-${criterion.correctionCriteriaLineNumber}.anchor${currentTabIndex}"
	    	                 		src="images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete search criterion" />
							
                                
                            </div>
                        </c:forEach>

                        
                        <div>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-name]">Field</label> 
                            <select id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-name]"
                                  name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][field-name]">
                                <option value="" selected="selected">Select Search Criteria</option>
                                <c:forEach var="fieldName" items="${ KualiForm.fieldNames}"><option value="<c:out 
                                    value="${fieldName.value}" />"><c:out value="${fieldName.value}" /></option></c:forEach> 
                            </select>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][operator]">Operator</label> 
                            <select id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][operator]"
                                  name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][operator]">
                                <c:forEach var="operator" items="${ KualiForm.searchOperators}">
                                <option value="<c:out value="${operator.key}" />" <c:if test="${operator.key =='eq'}">selected="true"</c:if>><c:out value="${operator.value}" />  </option> 

                                </c:forEach>
                            </select>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-value]">Value</label> 
                            <input id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-value]"
                                  name="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-value]" 
                                  type="text">
                            <html:image property="methodToCall.addSearchCriterion.anchor${currentTabIndex}" src="images/tinybutton-add1.gif" 
                                alt="add search criteria" styleClass="tinybutton" />
                        </div>
                        
                    </td> 
                    <td class="bord-l-b" style="padding: 4px; vertical-align: top;">
                        <c:forEach var="specification" items="${group.correctionChange }">
                            <div>
                                <label for="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][<c:out 
                                    value="${specification.correctionChangeLineNumber}" />][field-name]">Field</label>
                                <select id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][<c:out 
                                        value="${specification.correctionChangeLineNumber}" />][field-name]" 
                                    name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][<c:out 
                                        value="${specification.correctionChangeLineNumber}" />][field-name]">
                                    <c:forEach var="fieldName" items="${ KualiForm.fieldNames}">
                                        <option value="<c:out value="${fieldName.value}" />"<c:if 
                                            test="${fieldName.value eq specification.correctionFieldName}"> selected="true"</c:if>>
                                            <c:out value="${fieldName.value}" />
                                        </option> 
                                    </c:forEach>
                                </select>
                                <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber }" />][replacement-specifications][<c:out 
                                    value="${specification.correctionChangeLineNumber}" />][replacement-value]">Replacement Value</label>
                                <input type="text" 
                                    name="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][<c:out 
                                        value="${ specification.correctionChangeLineNumber}" />][replacement-value]" 
                                    value="<c:out value="${specification.correctionFieldValue}" />"
                                    id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][<c:out 
                                        value="${specification.correctionChangeLineNumber}" />][replacement-value]">



                                <html:image property="methodToCall.removeReplacementSpecification.specification${specification.correctionChangeGroupLineNumber}-${specification.correctionChangeLineNumber}.anchor${currentTabIndex}" 
	    	                 		src="images/tinybutton-delete1.gif" alt="delete search specification" styleClass="tinybutton" />
                            </div>
                        </c:forEach>

                       
                        <div>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][field-name]">Field</label> 
                            <select id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][field-name]"
                                name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][field-name]">
                                <option value="" selected="selected">Specify Modification</option>
                                <c:forEach var="fieldName" items="${ KualiForm.fieldNames}"><option value="<c:out 
                                    value="${fieldName.value}" />"><c:out value="${fieldName.value}" /></option></c:forEach> 
                            </select>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][replacement-value]">Replacement 
                                Value</label>
                            <input type="text" name="correction-groups[<c:out 
                                value="${group.correctionChangeGroupLineNumber }" />][replacement-specifications][replacement-value]" 
                                id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][replacement-value]"> 
                            <html:image property="methodToCall.addReplacementSpecification.anchor${currentTabIndex}" src="images/tinybutton-add1.gif" 
                                alt="add replacement specification" styleClass="tinybutton" /> 
                        </div>

                        
			                   
			                   
			                   
                  
                </tr>
            </c:forEach>
			
			<tr>
            	<td colspan="2" align="left" class="bord-l-b" style="padding: 4px; vertical-align: top;"> 
					<center>
			            <STRONG>Add Groups </STRONG>
			             <html:image property="methodToCall.addCorrectionGroup.anchor${currentTabIndex}"
	    	                 		src="images/tinybutton-add1.gif" alt="add correction group" styleClass="tinybutton" />
			            
            		 <center>
        	    </td>
            
            </tr>
		 		                   
	</table>
	
</div>
</c:if>
</kul:tab> 
 

      
      
<!--  Search for Manual Edit -->
<kul:tab tabTitle="Search Criteria for Manual Edit" defaultOpen="true" tabErrorKey="Search Criteria for Manual Edit">    
	  <c:if test="${KualiForm.editMethod == 'manual'}" >  
		<c:if test="${KualiForm.editableFlag == 'Y'}" > 
		
		
     	 <div class="tab-container" align="center"> 
	         <table cellpadding=0 class="datatable" summary=""> 
             <tr>
                <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Criteria for Manual Edit</span></td>
             </tr>

           	<c:forEach var="group" items="${KualiForm.document.correctionChangeGroup}"> 
           
     		<tr style="border-bottom: 1px solid #333;"> 
                    <td class="bord-l-b" style="padding: 4px; vertical-align: top;">
                        
                        <input type="hidden" name="correction-groups[<c:out 
                            value="${group.correctionChangeGroupLineNumber}" />][next-search-criterion-number]" value="<c:out 
                            value="${group.correctionCriteriaNextLineNumber}" />" /> 
                        <input type="hidden" name="correction-groups[<c:out 
                            value="${group.correctionChangeGroupLineNumber}" />][next-replacement-specification-number]" value="<c:out 
                            value="${group.correctionChangeNextLineNumber}" />" />
                        <c:forEach items="${group.correctionCriteria}" var="criterion"> 

                            <!-- Existing Search Criteria -->
                            <div>
                                <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber }" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][field-name]">Field</label>
                                <select id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][field-name]"
                                      name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                        value="${criterion.correctionCriteriaLineNumber}" />][field-name]">
                                    <option value="" selected>Select Search Criteria</option> 
                                    <c:forEach var="fieldName" items="${KualiForm.fieldNames}">
                                        <option value="<c:out value="${fieldName.value}" />"<c:if 
                                            test="${fieldName.value eq criterion.correctionFieldName}"> selected="true"</c:if>>
                                            <c:out value="${fieldName.value}" /> 
                                        </option>
                                    </c:forEach>
                                </select>
                                <label for="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][operator]">Operator</label>
                                <select id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][operator]"
                                  name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][operator]">
                                    <c:forEach var="operator" items="${KualiForm.searchOperators}">
                                        <option value="<c:out value="${operator.key }" />"<c:if 
                                            test="${operator.key eq criterion.correctionOperatorCode}">selected="true"</c:if>><c:out 
                                            value="${ operator.value}" /></option>
                                    </c:forEach>
                                </select>
                                <label for="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][field-value]">Value</label>
                                <input id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][field-value]"
                                    value="${ criterion.correctionFieldValue}"
                                    name="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${ criterion.correctionCriteriaLineNumber}" />][field-value]" type="text">
                                
                                
                                <html:image property="methodToCall.removeSearchCriterionForManualEdit.criterion${criterion.correctionChangeGroupLineNumber}-${criterion.correctionCriteriaLineNumber}.anchor${currentTabIndex}"
	    	                 		src="images/tinybutton-delete1.gif" alt="delete search criterion" styleClass="tinybutton" />
			            	</div>
                        </c:forEach>

                        <!-- New Search Criteria --> 
                        <div>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-name]">Field</label> 
                            <select id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-name]"
                                  name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][field-name]">
                                <option value="" selected="selected">Select Search Criteria</option>
                                <c:forEach var="fieldName" items="${ KualiForm.fieldNames}"><option value="<c:out 
                                    value="${fieldName.value}" />"><c:out value="${fieldName.value}" /></option></c:forEach> 
                            </select>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][operator]">Operator</label> 
                            <select id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][operator]"
                                  name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][operator]">
                                <c:forEach var="operator" items="${ KualiForm.searchOperators}">
                                    <option value="<c:out value="${operator.key}" />" <c:if test="${operator.key =='eq'}">selected="true"</c:if>><c:out value="${operator.value}" />  </option> 
                                </c:forEach>
                            </select>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-value]">Value</label> 
                            <input id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-value]"
                                  name="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-value]" 
                                  type="text">
                            <html:image property="methodToCall.addSearchCriterionForManualEdit.anchor${currentTabIndex}" src="images/tinybutton-add1.gif" 
                                alt="add search criteria" styleClass="tinybutton" />
                        </div>
                        
                    </td> 
                    </tr>
                      
                  		</c:forEach>
		   			
		   			
		   			
		   					 	
     		
    	 
     
		</table>
		
        	<div align="center" >
     	
		
		   			<html:image property="methodToCall.searchForManualEdit.anchor${currentTabIndex}"
	    	                 		src="images/buttonsmall_search.gif" alt="search" styleClass="tinybutton" />
		   			
		   			<html:image property="methodToCall.searchCancelForManualEdit.anchor${currentTabIndex}"
	    	                 		src="images/buttonsmall_cancel.gif" alt="cancel" styleClass="tinybutton" />
		   		  			
	    		  	
			</div>		
       	</div>
       	
     	
     	
     	</c:if>	
      </c:if>
</kul:tab>        
      
      
      
    
     
   
</c:if>
















<c:if test="${KualiForm.command =='displayDocSearchView'}">



<div class="tab-container" align="center" >
      
    	<table cellpadding=0 class="datatable" summary=""> 
        	  <tr>
                    <td align="left" valign="middle" class="subhead"><span class="subhead-left"></span>System and Edit Method</td>
         	  </tr>
	    		<tr>
     	 			<td>
		     			<center>
							<Strong> <c:out value="${KualiForm.chooseSystem}" /> </Strong>
							<Strong> <c:out value="${KualiForm.editMethod}" /> </Strong>
						</center>
    				  </td>
	  			</tr>
		</table>
 	</div>


<div class="tab-container" align="center" >
      
    	<table cellpadding=0 class="datatable" summary=""> 
        	  <tr>
                    <td align="left" valign="middle" class="subhead"><span class="subhead-left"></span>Input and Output File</td>
         	  </tr>
	  	</table>
			<table>
             <tr> 
            
             <td width="20%" align="left" valign="middle" > Input File Name: </td> 
             <td align="left" valign="middle" > <c:out value="${KualiForm.document.correctionInputFileName}" /></td>

			 </tr>
			 <tr>
			 
			 <td width="20%" align="left" valign="middle" > Output File Name: </td> 
             <td align="left" valign="middle" > <c:out value="${KualiForm.document.correctionOutputFileName}" /></td>
			 
			 </tr>
		
   		</table>
		
 	</div>


<div class="tab-container" align="left" style="overflow: scroll; max-width: 100%;">
      
            <table cellpadding=0 class="datatable" summary=""> 
                <tr>
                    <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Results</span></td>
                </tr>
               
                <tr>
                    
                      <td>
                      
					                    
					<display:table class="datatable-100" cellspacing="0" requestURIcontext="false" cellpadding="0" name="${KualiForm.allEntries}" id="allEntries" pagesize="10"
                      requestURI="generalLedgerCorrection.do?methodToCall=viewResults&document.financialDocumentNumber=${KualiForm.document.documentHeader.financialDocumentNumber}" >
        
       		    		<display:column class="infocell" sortable="true" title="Origin Entry Id" >
						<c:out value="${allEntries.entryId}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Fiscal Year" >
						<c:out value="${allEntries.universityFiscalYear}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Budget Year" >
						<c:out value="${allEntries.budgetYear}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Chart Code" >
						<c:out value="${allEntries.chartOfAccountsCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Account Number" >
						<c:out value="${allEntries.accountNumber}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Sub Account Number" >
						<c:out value="${allEntries.subAccountNumber}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Object Code" >
						<c:out value="${allEntries.financialObjectCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Sub Object Code" >
						<c:out value="${allEntries.financialSubObjectCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Balance Type" >
						<c:out value="${allEntries.financialBalanceTypeCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Object Type" >
						<c:out value="${allEntries.financialObjectTypeCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Fiscal Period" >
						<c:out value="${allEntries.universityFiscalPeriodCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Document Type" >
						<c:out value="${allEntries.financialDocumentTypeCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Origin Code" >
						<c:out value="${allEntries.financialSystemOriginationCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Document Number" >
						<c:out value="${allEntries.financialDocumentNumber}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Sequence Number" >
						<c:out value="${allEntries.transactionLedgerEntrySequenceNumber}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Description" >
						<c:out value="${allEntries.transactionLedgerEntryDescription}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Amount" >
						<c:out value="${allEntries.transactionLedgerEntryAmount}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Debit Credit Indicator" >
						<c:out value="${allEntries.transactionDebitCreditCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Transaction Date" >
						<c:out value="${allEntries.transactionDate}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Org Doc Number" >
						<c:out value="${allEntries.organizationDocumentNumber}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Project Code" >
						<c:out value="${allEntries.projectCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Org Ref ID" >
						<c:out value="${allEntries.organizationReferenceId}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Ref Doc Type" >
						<c:out value="${allEntries.referenceFinancialDocumentTypeCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Ref Origin Code" >
						<c:out value="${allEntries.referenceFinancialSystemOriginationCode}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Ref Doc Number" >
						<c:out value="${allEntries.financialDocumentNumber}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Reversal Date" >
						<c:out value="${allEntries.financialDocumentNumber}" />&nbsp;</display:column>
						<display:column class="infocell" sortable="true" title="Enc Update Code" >
						<c:out value="${allEntries.transactionEncumbranceUpdateCode}" />&nbsp;</display:column>
						</display:table>
                      
                      
                      </td>
                      
                 	
              	  </tr>
              	  <tr><td>
              	  	<c:out value="${KualiForm.originEntryGroupDeletedMessage}" />
              	  </td></tr>
       		 </table>
       	</div>


	


 <div class="tab-container" align="center"> 
 
            <table cellpadding=0 class="datatable" summary="">
       			 <tr>
                    <td align="left" valign="middle" class="subhead"><span class="subhead-left">Edit Options and Action</span></td>
                </tr>
          	  
            <tr>
            	<td>
					<center>
						<html:checkbox property="processInBatch" title="processInBatch" disabled="true"/> <STRONG> Process In Batch </STRONG> &nbsp; &nbsp; &nbsp; &nbsp;  
					<c:if test="${KualiForm.editMethod == 'criteria'}" >
						<html:checkbox property="matchCriteriaOnly" alt="matchCriteriaOnly" disabled="true"/> <STRONG> Output only records which match criteria? </STRONG>
					</c:if>
					</center>
				</td>
			</tr>
	
		</table>				  
	</div>	
	

	
	


	<c:if test="${KualiForm.editMethod == 'criteria'}" >
		<div class="tab-container" align="center"> 
        <table cellpadding=0 class="datatable" summary="">
        
            <tr>
                <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Criteria</span></td> 
                <td align="left" valign="middle" class="subhead"><span class="subhead-left">Modification Criteria</span></td>
            </tr>
            
            
               <c:forEach var="group" items="${KualiForm.document.correctionChangeGroup}"> 
                   <tr>
                       <td colspan="2" align="left" class="bord-l-b" style="padding: 4px; vertical-align: top;"> 
                           <strong>Group:</strong> 
                       </td>
                   </tr>
                <tr style="border-bottom: 1px solid #333;"> 
                    <td class="bord-l-b" style="padding: 4px; vertical-align: top;">
                        <input type="hidden" name="correction-groups[<c:out 
                            value="${group.correctionChangeGroupLineNumber}" />][group-number]" value="" /> 
                        <input type="hidden" name="correction-groups[<c:out 
                            value="${group.correctionChangeGroupLineNumber}" />][next-search-criterion-number]" value="<c:out 
                            value="${group.correctionCriteriaNextLineNumber}" />" /> 
                        <input type="hidden" name="correction-groups[<c:out 
                            value="${group.correctionChangeGroupLineNumber}" />][next-replacement-specification-number]" value="<c:out 
                            value="${group.correctionChangeNextLineNumber}" />" />
                        <c:forEach items="${group.correctionCriteria}" var="criterion"> 
                            <div>
                                <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber }" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][field-name]">Field</label>
                                <select id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][field-name]"
                                      name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                        value="${criterion.correctionCriteriaLineNumber}" />][field-name]">
                                    <option value="" selected>Select Search Criteria</option> 
            	                        <c:forEach var="fieldName" items="${KualiForm.fieldNames}">
                	                        <option value="<c:out value="${fieldName.value}" />"<c:if 
                    	                        test="${fieldName.value eq criterion.correctionFieldName}"> selected="true" </c:if> disabled="true">
                        	                    <c:out value="${fieldName.value}" /> 
                            	            </option>
                                	    </c:forEach>
                                </select>
                                <label for="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][operator]">Operator</label>
                                <select id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][operator]"
                                  name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][operator]">
                                    <c:forEach var="operator" items="${KualiForm.searchOperators}">
                                        <option value="<c:out value="${operator.key }" />"<c:if 
                                            test="${operator.key eq criterion.correctionOperatorCode}">selected="true" </c:if> disabled="true"><c:out 
                                            value="${operator.value}" /></option>
                                    </c:forEach>
                                </select>
                                <label for="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][field-value]">Value</label>
                                <input id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${criterion.correctionCriteriaLineNumber}" />][field-value]"
                                    value="${ criterion.correctionFieldValue}"
                                    name="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][<c:out 
                                    value="${ criterion.correctionCriteriaLineNumber}" />][field-value]" type="text">
                                
                            </div>
                        </c:forEach>

                        
                        <div>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-name]">Field</label> 
                            <select id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-name]"
                                  name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][field-name]">
                                <option value="" selected="selected" >Select Search Criteria</option>
                                <c:forEach var="fieldName" items="${ KualiForm.fieldNames}"><option value="<c:out 
                                    value="${fieldName.value}" />" disabled="true" ><c:out value="${fieldName.value}" /></option></c:forEach> 
                            </select>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][operator]">Operator</label> 
                            <select id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][operator]"
                                  name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][search-criteria][operator]">
                                <c:forEach var="operator" items="${ KualiForm.searchOperators}">
                                <option value="<c:out value="${operator.key}" />" <c:if test="${operator.key =='eq'}">selected="true"</c:if> disabled="true" ><c:out value="${operator.value}" />  </option> 

                                </c:forEach>
                            </select>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-value]">Value</label> 
                            <input id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-value]"
                                  name="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][search-criteria][field-value]" 
                                  type="text">
                            </div>
                        
                    </td> 
                    <td class="bord-l-b" style="padding: 4px; vertical-align: top;">
                        <c:forEach var="specification" items="${group.correctionChange }">
                            <div>
                                <label for="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][<c:out 
                                    value="${specification.correctionChangeLineNumber}" />][field-name]">Field</label>
                                <select id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][<c:out 
                                        value="${specification.correctionChangeLineNumber}" />][field-name]" 
                                    name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][<c:out 
                                        value="${specification.correctionChangeLineNumber}" />][field-name]">
                                    <c:forEach var="fieldName" items="${ KualiForm.fieldNames}">
                                        <option value="<c:out value="${fieldName.value}" />"<c:if 
                                            test="${fieldName.value eq specification.correctionFieldName}"> selected="true"</c:if> disabled="true">
                                            <c:out value="${fieldName.value}" />
                                        </option> 
                                    </c:forEach>
                                </select>
                                <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber }" />][replacement-specifications][<c:out 
                                    value="${specification.correctionChangeLineNumber}" />][replacement-value]">Replacement Value</label>
                                <input type="text" 
                                    name="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][<c:out 
                                        value="${ specification.correctionChangeLineNumber}" />][replacement-value]" 
                                    value="<c:out value="${specification.correctionFieldValue}" />"
                                    id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][<c:out 
                                        value="${specification.correctionChangeLineNumber}" />][replacement-value]">
                            </div>
                        </c:forEach>

                       
                        <div>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][field-name]">Field</label> 
                            <select id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][field-name]"
                                name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][field-name]">
                                <option value="" selected="selected">Specify Modification</option>
                                <c:forEach var="fieldName" items="${ KualiForm.fieldNames}"><option value="<c:out 
                                    value="${fieldName.value}" />" disabled="true"><c:out value="${fieldName.value}" /></option></c:forEach> 
                            </select>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][replacement-value]">Replacement 
                                Value</label>
                            <input type="text" name="correction-groups[<c:out 
                                value="${group.correctionChangeGroupLineNumber }" />][replacement-specifications][replacement-value]" 
                                id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][replacement-value]"> 
                        </div>

                        
			                   
			                   
			                   
                  
                </tr>
            </c:forEach>
		</table>
	</div>
	</c:if>


</c:if>



   
	<kul:notes/>
    <kul:adHocRecipients />        
    <kul:routeLog/>
    <kul:panelFooter/>
    <kul:documentControls transactionalDocument="false" />
 </kul:page>
 
 




