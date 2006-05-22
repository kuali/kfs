<%@ include file="/jsp/core/tldHeader.jsp" %>
<kul:documentPage showDocumentInfo="true" documentTypeName="KualiGeneralLedgerErrorCorrectionDocument" htmlFormAction="generalLedgerCorrection" renderMultipart="true" > 
 
    <kul:hiddenDocumentFields isFinancialDocument="false" />
    <kul:documentOverview editingMode="${KualiForm.editingMode}"/>
    <input type="hidden" name="document.correctionChangeGroupNextLineNumber" value="<c:out value="${KualiForm.document.correctionChangeGroupNextLineNumber}" />" />
 
 	<div class="tab-container" align="center">
 	<input type="radio" checked="checked" name="glcpRadio" value="CRITERIA" /> Criteria
	<input type="radio" name="glcpRadio" value="MANUAL" /> Manual Edit
	<input type="radio" name="glcpRadio" value="FILE" /> File Upload
 	</div>
 
 
 
    <!--  File upload control -->
    <div class="tab-container" align="center"> 
    
        <div class="h2-container">
        <h2>Corrections File Upload</h2>
        </div>
        <table cellpadding=0 class="datatable" summary=""> 
 
            
            <tr>
                <td class="bord-l-b" style="padding: 4px;">
                    <label for="fileUpload">File</label>
                    <input type="file" name="FileUpload" id="fileUpload">
                
                <button type="submit" name="methodToCall" value="uploadFile">Upload</button>
	    	    
            </tr>
        </table>
    </div>
 
 
<!--    <kul:errors keyMatch="fieldError${group.groupNumber}"/>  -->
            
    
    <!-- Search criteria control -->
    <div class="tab-container" align="center"> 
        <kul:errors keyMatch="searchFieldError" />
        <table cellpadding=0 class="datatable" summary="">
            <tr>
                <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Criteria</span></td> 
                <td align="left" valign="middle" class="subhead"><span class="subhead-left">Modification Criteria</span></td>
            </tr>
            <tr>
            <td colspan="2" align="left" class="bord-l-b" style="padding: 4px; vertical-align: top;"> 
            <STRONG>Add Groups </STRONG> 
            <input type="image" 
                   name="methodToCall.addCorrectionGroup" 
                   src="images/tinybutton-add1.gif" alt="add correction group" class="tinybutton" /> 
            </td>
            
            </tr>
 







          
            
            
            
            <tr>
                <td colspan="2" class="bord-l-b" style="padding: 4px; vertical-align: top;"> 
                    <center>
                        <label for="pending-origin-entry-group-id"><strong>Origin Entry Group</strong></label><br/><br/>
                        <select name="pending-origin-entry-group-id" MULTIPLE> 
                            <c:forEach var="pendingGroup" items="${KualiForm.originEntryGroupsPendingProcessing}">
                                <option value="<c:out value="${ pendingGroup.id}" />" <c:if test="${KualiForm.document.originEntryGroup.id eq pendingGroup.id}"> selected="selected"</c:if> > 
                                    <c:out value="${pendingGroup.id}" /> - <c:out value="${pendingGroup.source.name}" /> - <fmt:formatDate value="${ pendingGroup.date}" dateStyle="full" />
                                </option>
                            </c:forEach>
                        </select><br/><br/> 
                        <strong>Show Matching Entries</strong><br /><br />
                        <button type="submit" name="methodToCall" value="searchAndReplaceWithCriteria"> 
                            Search and Replace using Criteria
                        </button>
                        <button type="submit" name="methodToCall" value="showAllEntries"> 
                            Show all Entries for manual editing
                        </button>
                    </center>
                </td>
            </tr>
        </table> 
    </div>
 
 
    <!--  Search Results Control -->
<div class="tab-container" align="left">
        <div style="overflow: scroll; max-width: 100%;">
            <table cellpadding=0 class="datatable" summary=""> 
                <tr>
                    <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Results</span></td>
                </tr>
                <tr>
                    <td align="center" valign="top" class="bord-1-b">
                        <style type="text/css"> 
                            #entries td, #entries th {
                                border: 1px solid #333;
                                text-align: center;
                            }
                        </style> 
                        <table id="entries">
                            <tr>
                                <td>Account Number</td>
                                <td>Financial Document Number</td> 
                                <td>Reference Financial Document Number</td>
                                <td>Reference Financial Document TypeCode</td>
                                <td>Financial Document Reversal Date</td> 
                                <td>Financial Document TypeCode</td>
                                <td>Financial Balance TypeCode</td>
                                <td>Chart Of Accounts Code</td> 
                                <td>Financial Object TypeCode</td>
                                <td>Financial Object Code</td>
                                <td>Financial Sub-Object Code</td> 
                                <td>Financial System Origination Code</td>
                                <td>Reference Financial System Origination Code</td>
                                <td>Organization Document Number</td> 
                                <td>Organization ReferenceId</td>
                                <td>Project Code</td>
                                <td>Sub-Account Number</td> 
                                <td>Transaction Date</td>
                                <td>Transaction DebitCreditCode</td>
                                <td>Transaction Encumbrance Update Code</td> 
                                <td>Transaction LedgerEntry Sequence Number</td>
                                <td>Transaction LedgerEntry Amount</td>
                                <td>Transaction Ledger Entry Description</td> 
                                <td>University FiscalPeriod Code</td>
                                <td>University FiscalYear</td>
                                <td>BudgetYear</td> 
                                
                                
                            </tr>
                            <c:forEach var="entry" items="${KualiForm.entriesThatMatchSearchCriteria }">
                                <tr>
                                    <td><c:out value="${entry.accountNumber}" /></td>
                                    <td><c:out value="${entry.financialDocumentNumber}" /></td>
                                    <td><c:out value="${entry.referenceFinancialDocumentNumber}" /></td>
                                    <td><c:out value="${entry.referenceFinancialDocumentTypeCode}" /></td>
                                    <td><c:out value="${entry.financialDocumentReversalDate}" /></td>
                                    <td><c:out value="${entry.financialDocumentReversalDate}" /></td>
                                    <td><c:out value="${entry.financialDocumentTypeCode}" /></td>
                                    <td><c:out value="${entry.financialBalanceTypeCode}" /></td>
                                    <td><c:out value="${entry.chartOfAccountsCode}" /></td>
                                    <td><c:out value="${entry.financialObjectTypeCode}" /></td>
                                    <td><c:out value="${entry.financialObjectCode}" /></td>
                                    <td><c:out value="${entry.financialSubObjectCode}" /></td>
                                    <td><c:out value="${entry.financialSystemOriginationCode}" /></td>
                                    <td><c:out value="${entry.referenceFinancialSystemOriginationCode}" /></td>
                                    <td><c:out value="${entry.organizationReferenceId}" /></td>
                                    <td><c:out value="${entry.projectCode}" /></td>
                                    <td><c:out value="${entry.subAccountNumber}" /></td>
                                    <td><c:out value="${entry.transactionDate}" /></td>
                                    <td><c:out value="${entry.transactionDebitCreditCode}" /></td>
                                    <td><c:out value="${entry.transactionEncumbranceUpdateCode}" /></td>
                                    <td><c:out value="${entry.transactionLedgerEntrySequenceNumber}" /></td>
                                    <td><c:out value="${entry.transactionLedgerEntryAmount}" /></td>
                                    <td><c:out value="${entry.transactionLedgerEntryDescription}" /></td>
                                    <td><c:out value="${entry.universityFiscalPeriodCode}" /></td>
                                    <td><c:out value="${entry.universityFiscalYear}" /></td>
                                    <td><c:out value="${entry.budgetYear}" /></td>
                             
                                    <!-- 
                                    <td><input size="7" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][accountNumber]" 
                                        value="<c:out value="${entry.accountNumber}" />"></td>
                                     
                                    <td><input size="10" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][financialDocumentNumber]" 
                                        value="<c:out value="${entry.financialDocumentNumber}" />"></td>
 
                                    <td><input size="10" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][referenceFinancialDocumentNumber]" 
                                        value="<c:out value="${entry.referenceFinancialDocumentNumber }" />"></td>    
                                        
                                    <td><input size="10" type="text" name="entries[<c:out
                                        value="${ entry.entryId}" />][referenceFinancialDocumentTypeCode]" 
                                        value="<c:out value="${entry.referenceFinancialDocumentTypeCode}" />"></td> 
                                        
                                    <td><input size="10" type="text" name="entries[<c:out 
                                        value="${ entry.entryId}" />][financialDocumentReversalDate]" 
                                        value="<c:out value="${entry.financialDocumentReversalDate}" />"></td>
                                     
                                    <td><input size="2" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][financialDocumentTypeCode]" 
                                        value="<c:out value="${entry.financialDocumentTypeCode}" />"></td>
                                    
                                    <td><input size="2" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][financialBalanceTypeCode]" 
                                        value="<c:out value="${entry.financialBalanceTypeCode }" />"></td>
                                        
                                    <td><input size="2" type="text" name="entries[<c:out 
                                        value="${ entry.entryId}" />][chartOfAccountsCode]" 
                                        value="<c:out value="${entry.chartOfAccountsCode}" />"></td>
                                     
                                    <td><input size="2" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][financialObjectTypeCode]" 
                                        value="<c:out value="${entry.financialObjectTypeCode}" />"></td>
                                        
                                    <td><input size="4" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][financialObjectCode]" 
                                        value="<c:out value="${entry.financialObjectCode}" />"></td> 
                                        
                                    <td><input size="3" type="text" name="entries[<c:out 
                                        value="${ entry.entryId}" />][financialSubObjectCode]" 
                                        value="<c:out value="${entry.financialSubObjectCode}" />"></td>
                                         
                                    <td><input size="10" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][financialSystemOriginationCode]" 
                                        value="<c:out value="${entry.financialSystemOriginationCode}" />"></td>
                                    
                                    <td><input size="10" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][referenceFinancialSystemOriginationCode]" 
                                        value="<c:out value="${entry.referenceFinancialSystemOriginationCode }" />"></td>
                                        
                                    <td><input size="5" type="text" name="entries[<c:out 
                                        value="${ entry.entryId}" />][subAccountNumber]" 
                                        value="<c:out value="${entry.subAccountNumber}" />"></td>        
                                     
                                    <td><input size="10" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][organizationReferenceId]" 
                                        value="<c:out value="${entry.organizationReferenceId}" />"></td>        
                                            
                                    <td><input size="3" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][projectCode]" 
                                        value="<c:out value="${entry.projectCode}" />"></td> 
                                    
                                    <td><input size="5" type="text" name="entries[<c:out 
                                        value="${ entry.entryId}" />][subAccountNumber]" 
                                        value="<c:out value="${entry.subAccountNumber}" />"></td>
                                     
                                    <td><input size="10" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][transactionDate]" 
                                        value="<c:out value="${entry.transactionDate}" />"></td>
                                        
                                    <td><input size="1" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][transactionDebitCreditCode]" 
                                        value="<c:out value="${entry.transactionDebitCreditCode }" />"></td>
                                        
                                    <td><input size="10" type="text" name="entries[<c:out 
                                        value="${ entry.entryId}" />][transactionEncumbranceUpdateCode]" 
                                        value="<c:out value="${entry.transactionEncumbranceUpdateCode}" />"></td>
                                        
                                    <td><input size="4" type="text" name="entries[<c:out 
                                        value="${ entry.entryId}" />][transactionLedgerEntrySequenceNumber]" 
                                        value="<c:out value="${entry.transactionLedgerEntrySequenceNumber}" />"></td>     
                                            
                                    <td><input size="7" type="text" name="entries[<c:out 
                                        value="${ entry.entryId}" />][transactionLedgerEntryAmount]" 
                                        value="<c:out value="${entry.transactionLedgerEntryAmount}" />"></td>
                                         
                                    <td><input size="10" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][transactionLedgerEntryDescription]" 
                                        value="<c:out value="${entry.transactionLedgerEntryDescription}" />"></td>
 
                                    <td><input size="1" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][universityFiscalPeriodCode]" 
                                        value="<c:out value="${entry.universityFiscalPeriodCode }" />"></td>    
                                        
                                    <td><input size="4" type="text" name="entries[<c:out 
                                        value="${ entry.entryId}" />][universityFiscalYear]" 
                                        value="<c:out value="${entry.universityFiscalYear}" />"></td>
                                     
                                    <td><input size="4" type="text" name="entries[<c:out 
                                        value="${entry.entryId}" />][budgetYear]" 
                                        value="<c:out value="${entry.budgetYear}" />"></td>
                                    
                                    -->
                                    </tr>
                            </c:forEach> 
                        </table>
                    </td>
                </tr>
        
  
     <tr>
     <td>
      	
     	<c:forEach var="group" items="${ KualiForm.document.correctionChangeGroup}"> 
     		<c:forEach var="specification" items="${group.correctionChange }">
 	
					
					
					
					
					
					 	<input type="hidden" name="correction-groups[<c:out 
                            value="${group.correctionChangeGroupLineNumber}" />][next-search-criterion-number]" value="<c:out 
                            value="${group.correctionCriteriaNextLineNumber}" />" /> 
                        <input type="hidden" name="correction-groups[<c:out 
                            value="${group.correctionChangeGroupLineNumber}" />][next-replacement-specification-number]" value="<c:out 
                            value="${group.correctionChangeNextLineNumber}" />" />
                    
                    
                    
                    
                    
                    
                    
                            <!-- Existing Replacement Specifications -->
                            <div>
                                <label for="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][<c:out 
                                    value="${specification.correctionChangeLineNumber}" />][field-name]">Field</label>
                                <select id="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][<c:out 
                                        value="${specification.correctionChangeLineNumber}" />][field-name]" 
                                    name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][<c:out 
                                        value="${specification.correctionChangeLineNumber}" />][field-name]">
                                    <c:forEach var="fieldName" items="${ KualiForm.fieldNames}">
                                        <option value="<c:out value="${fieldName}" />"<c:if 
                                            test="${fieldName eq specification.correctionFieldName}"> selected="true"</c:if>>
                                            <c:out value="${fieldName}" />
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
                                <input type="image" 
                                    name="methodToCall.removeReplacementSpecification.specification<c:out value="${specification.correctionChangeGroupLineNumber}" />-<c:out 
                                    value="${ specification.correctionChangeLineNumber}" />" 
                                    src="images/tinybutton-delete1.gif" alt="delete search specification" class="tinybutton" />
                                
                            </div>
                        </c:forEach>
                        
                        <!-- New Replacement Specification --> 
                        <div>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][field-name]">Field</label> 
                            <select id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][field-name]"
                                name="correction-groups[<c:out value="${ group.correctionChangeGroupLineNumber}" />][replacement-specifications][field-name]">
                                <option value="" selected="selected">Specify Modification</option>
                                <c:forEach var="fieldName" items="${ KualiForm.fieldNames}"><option value="<c:out 
                                    value="${fieldName}" />"><c:out value="${fieldName}" /></option></c:forEach> 
                            </select>
                            <label for="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][replacement-value]">Replacement 
                                Value</label>
                            <input type="text" name="correction-groups[<c:out 
                                value="${group.correctionChangeGroupLineNumber }" />][replacement-specifications][replacement-value]" 
                                id="correction-groups[<c:out value="${group.correctionChangeGroupLineNumber}" />][replacement-specifications][replacement-value]"> 
                            <html:image property="methodToCall.addReplacementSpecification" src="images/tinybutton-add1.gif" 
                                alt="add replacement specification" styleClass="tinybutton" /> 
                        </div>
                  	</c:forEach>
     
    			 </td>
    		 	</tr>
     		 </table>
    	 </div>
    	 </div>
     
     
     
    
    <!-- shawn --> 
 
           
              <c:out value="${resultMessage}" /> 
          
 
<!-- shawn's end -->
 
 
 
<!-- 
    <div class="tab-container" align="left"> 
        <div style="overflow: scroll; max-width: 100%;">
            <table cellpadding=0 class="datatable" summary="">
                <tr>
                    <td align="left" valign="middle" class="subhead"><span class="subhead-left">Search Results</span></td> 
                </tr>
                <tr>
                    <td align="center" valign="top" class="bord-1-b">
                        <style type="text/css"> 
                            #entries td, #entries th {
                                border: 1px solid #333;
                                text-align: center;
                            }
                        </style> 
                        <table id="entries">
                            
                                <td>Ref. Origin</td>
                                <td>FDOC Ref. Number</td>
                                <td>FDOC Reversal Date</td> 
                                <td>Encumbrance Update</td>
                                <td>Origin</td>
                                <td>Origin Entry Group ID</td>
                            </tr> 
                            <c:forEach var="entry" items="${KualiForm.entriesThatMatchSearchCriteria}">
                                <tr>
                                    <td><c:out value="${ entry.entryId}" /></td>
                                    <td><input size="2" type="text" name="entries[<c:out 
                                        value="${ entry.entryId}" />][chartOfAccountsCode]" 
                                        value="<c:out value="${entry.chartOfAccountsCode}" />"></td>
                                   
                                </tr>
                            </c:forEach>
                        </table>
                    </td>
                </tr>
            </table> 
        </div>
    </div>
 
 
    -->
 
    <kul:notes/>
    <kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>        
    <kul:routeLog/>
    <kul:panelFooter/>
    <kul:documentControls transactionalDocument="false" />
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
</kul:documentPage>
 
 

