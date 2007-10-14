<?xml version="1.0"?>
<!--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
<xsl:stylesheet version ="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	 xmlns:fo="http://www.w3.org/1999/XSL/Format">
	 
 <!--  The following keys and variables are used throughout the stylesheet.  Some should be given more descriptive names.  
Variables with the double X, such as S1XX_Hospitalization, are used on Page 5.  Their single X counterparts, which are used on page 4, produced garbage when re-used on page 5.  This was observed on personal computers and on the Server.  The results were not consistent among personal computers.  
-->

  <!--                           VERSION HISTORY
    - 05/2006: pcberg@indiana.edu, refactoring for KRA.
               - @PERIOD = @PERIOD_NUMBER
               - Added variables baseUrl and imageArrow.
               - CREATE_TIMESTAMP = SEQUENCE_NUMBER
  -->

<xsl:variable name="baseUrl" select="/PROPOSAL/BUDGET/@BASE_URL"/>
<xsl:variable name="imageArrow" select="'/images-xslt/arrow.gif'"/>

  <!-- 
 Alterations and Renovations is a subcategory of Other.  Page 4 of output does has a separate space for Alterations and Renovations.  Adding "not(SUB_CATEGORY = 'Alterations and Renovations')" was a convenient hack, but applying the test for categories other than "Other" is unnecessary.
 -->
<xsl:key name="CATEGORY_Other" match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1' ]/NON_PERSONNEL/NON_PERSONNEL_ITEM[not(SUB_CATEGORY = 'Alterations and Renovations')] " use="CATEGORY" />


<!-- The "Other" space on page 4 does not itemize Subject Payments, Fee Remissions, or Remissions -->

<xsl:key name="CATEGORY_Filtered" 
	match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1' ]
	/NON_PERSONNEL/NON_PERSONNEL_ITEM[not(SUB_CATEGORY = 'Alterations and Renovations' or 
	SUB_CATEGORY = 'Subject Payments' or SUB_CATEGORY = 'Fee Remissions' 
	or CATEGORY = 'Fellowships'  or AGENCY_REQUEST_AMOUNT = 0)] " 
	use="CATEGORY" />

<!--  If AGENCY_REQUEST_AMOUNT = 0, there should be no output.    -->

<xsl:key name="CATEGORY_Participant_Filtered" 
	match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1' ]
	/NON_PERSONNEL/NON_PERSONNEL_ITEM[not(AGENCY_REQUEST_AMOUNT = 0)] " 
	use="CATEGORY" />

<xsl:variable name="PERSON_VAR" select="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON" /> 

<xsl:key name="CATEGORY" match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/NON_PERSONNEL/NON_PERSONNEL_ITEM" use="CATEGORY" />

<xsl:key name="SUB_CATEGORY" match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/NON_PERSONNEL/NON_PERSONNEL_ITEM" use="SUB_CATEGORY" />

<xsl:key name="PERSON_KEY"
         match="PERSON"
         use="concat(../../@PERIOD_NUMBER, ' ',
                     NAME, ' ',
                     APPOINTMENT/@APPOINTMENT_TYPE, ' ',
                     @PROJECT_DIRECTOR, ' ',
                     @SEQUENCE_NUMBER)" />

<xsl:variable name="PERSON_PI" select="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[generate-id(.) = generate-id(key('PERSON_KEY',
                                    concat(../../@PERIOD_NUMBER, ' ',
                                           NAME, ' ',
                                           APPOINTMENT/@APPOINTMENT_TYPE, ' ',
                                           'TRUE', ' ',
                                           @SEQUENCE_NUMBER)))]"/>

<xsl:variable name="PERSON_OTHER" select="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[generate-id(.) = generate-id(key('PERSON_KEY',
                                    concat(../../@PERIOD_NUMBER, ' ',
                                           NAME, ' ',
                                           APPOINTMENT/@APPOINTMENT_TYPE, ' ',
                                           'FALSE', ' ',
                                           @SEQUENCE_NUMBER)))]"/>

 <xsl:variable name="PI_Count_Preliminary" select="count($PERSON_PI)" />
 
 <xsl:variable name="PI_Count" >
 	<xsl:choose>
 		<xsl:when test="$PI_Count_Preliminary = 0" >
 			1
 		</xsl:when>
 		<xsl:otherwise>
 			<xsl:value-of select="$PI_Count_Preliminary" />
 		</xsl:otherwise>
 	</xsl:choose>
 
 </xsl:variable>
 
<xsl:key name="PERIOD" match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD" use="@PERIOD_NUMBER" />

<xsl:variable name="FilteredPersonCount" select="count($PERSON_OTHER) " />

<xsl:variable name="Other_Count" select="count(key('CATEGORY_Filtered', 'Other Expenses')) " />

<xsl:variable name="Participant_Count" 
	select="count(key('CATEGORY_Participant_Filtered', 'Participant Expenses')) " />

<xsl:variable name="Participant_Sum" select="sum(key('CATEGORY', 'Participant Expenses')/AGENCY_REQUEST_AMOUNT) " />

<!--  Variables beginning with Sn_  , where n is 1, 2, 3, 4, or 5, designating budget periods, are numbers which can be added to other numbers.  Variables beginning with S1n_ are formatted for final output  -->

<xsl:variable name="S1X_Fee" >
	      <xsl:choose>
			<xsl:when test="key('SUB_CATEGORY', 'Fee Remissions')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('SUB_CATEGORY', 'Fee Remissions')/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S1X_Subject" >
	      <xsl:choose>
			<xsl:when test="key('SUB_CATEGORY', 'Subject Payments')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('SUB_CATEGORY', 'Subject Payments')/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
<xsl:variable name="S1_Fee" >
	      <xsl:choose>
			<xsl:when test="key('SUB_CATEGORY', 'Fee Remissions')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('SUB_CATEGORY', 'Fee Remissions')/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      

      <xsl:variable name="S1_Subject" >
	      <xsl:choose>
			<xsl:when test="key('SUB_CATEGORY', 'Subject Payments')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum(key('SUB_CATEGORY', 'Subject Payments')/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      

	<xsl:variable name="Equipment_P1">
		<xsl:value-of select="count(key('CATEGORY', 'Equipment' ))"/>
	</xsl:variable>
	
	<xsl:variable name="OtherCount_P1">
		<xsl:value-of select="count(key('CATEGORY', 'Other Expenses'))"/>
	</xsl:variable>

	<xsl:variable name="TravelCount_P1">
		<xsl:value-of select="count(key('CATEGORY', 'Travel'))"/>
	</xsl:variable>

	<xsl:variable name="ConsultantCount_P1">
		<xsl:value-of select="count(key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = 'Consultants'])"/>
	</xsl:variable>

	<xsl:variable name="SuppliesCount_P1">
		<xsl:value-of select="count(key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = 'Supplies'])"/>
	</xsl:variable>


	<xsl:variable name="EquipmentCount_P1">
		<xsl:value-of select="count(key('CATEGORY', 'Equipment' ))"/>
	</xsl:variable>

	<xsl:variable name="OtherExpenseCount_P1">
		<xsl:value-of select="count(key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = 'Other Expenses'])"/>
	</xsl:variable>

	
	<xsl:variable name="AlterationsCount_P1" select="count(key('SUB_CATEGORY', 'Alterations and Renovations'))" />
		
      <xsl:variable name="FeeCount" select="count(key('SUB_CATEGORY', 'Fee Remissions'))" />


      <xsl:variable name="SubjectCount" select="count(key('SUB_CATEGORY', 'Subject Payments'))" />
	     

      <xsl:variable name="AlterCount" select="count(key('SUB_CATEGORY', 'Alterations and Renovations'))" />
	      

<xsl:variable name="OE_Count" >
	<xsl:choose>
		<xsl:when test="$FeeCount &gt; 0 and $SubjectCount &gt; 0" >
			<xsl:value-of select="$OtherCount_P1 - $FeeCount - $SubjectCount - $AlterCount + 2" />
		</xsl:when>
		
		<xsl:when test="$FeeCount = 0 and $SubjectCount &gt; 0" >
			<xsl:value-of select="$OtherCount_P1 - $SubjectCount - $AlterCount + 1" />		
		</xsl:when>
		
		<xsl:when test="$SubjectCount = 0 and $FeeCount &gt; 0" >
			<xsl:value-of select="$OtherCount_P1 - $FeeCount - $AlterCount + 1" />		
		</xsl:when>
		
		<xsl:when test="$SubjectCount = 0 and $FeeCount = 0" >
			<xsl:value-of select="$OtherCount_P1 - $AlterCount" />		
		</xsl:when>
	
	</xsl:choose>
</xsl:variable>

<xsl:variable name="OE_Count_Base" >
			<xsl:value-of select="$OtherCount_P1 - $FeeCount - $SubjectCount" />
</xsl:variable>			
	
<xsl:variable name="Fee_Total" select="sum(key('SUB_CATEGORY', 'Fee Remissions')/AGENCY_REQUEST_AMOUNT)" />

<xsl:variable name="Subject_Total" select="sum(key('SUB_CATEGORY', 'Subject Payments')/AGENCY_REQUEST_AMOUNT)" />
			
			<!-- 5/12/2005 dterret: Modular adjustment added in separately because it is independent of
					 task. -->
      <xsl:variable name="S1X_TotalDirect" >
	      <xsl:choose>
					<xsl:when test="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']
						/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S1_ModularAdjustment &gt; 0">
						<xsl:value-of select="format-number(sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
							[@PERIOD_NUMBER = '1']/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S1_ModularAdjustment,'###,###')" />
					</xsl:when>
	      	<xsl:when test="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']
	      		/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S1_ModularAdjustment &lt; 0">
	      		(<xsl:value-of select="format-number(substring(sum(/PROPOSAL/BUDGET/TASK_PERIODS
	      			/TASK_PERIOD[@PERIOD_NUMBER = '1']/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + 
	      			$S1_ModularAdjustment,2),'###,###')" />)
	      	</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
		
			<!-- 5/12/2005 dterret: Modular adjustment added in separately because it is independent of
					 task. -->
      <xsl:variable name="S2X_TotalDirect" >
	    	<xsl:choose>
					<xsl:when test="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '2']
						/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S2_ModularAdjustment &gt; 0">
						<xsl:value-of select="format-number(sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
							[@PERIOD_NUMBER = '2']/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S2_ModularAdjustment, '###,###')" />
					</xsl:when>
	    		<xsl:when test="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '2']
	    			/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S2_ModularAdjustment &lt; 0">
	    			(<xsl:value-of select="format-number(substring(sum(/PROPOSAL/BUDGET/TASK_PERIODS
	    				/TASK_PERIOD[@PERIOD_NUMBER = '2']/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + 
	    				$S2_ModularAdjustment,2), '###,###')"/>)
	    		</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
      
			<!-- 5/12/2005 dterret: Modular adjustment added in separately because it is independent of
					 task. -->
      <xsl:variable name="S3X_TotalDirect" >
	    	<xsl:choose>
					<xsl:when test="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '3']
						/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S3_ModularAdjustment &gt; 0">
						<xsl:value-of select="format-number(sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
							[@PERIOD_NUMBER = '3']/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S3_ModularAdjustment, '###,###')" />
					</xsl:when>
	    		<xsl:when test="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '3']
	    			/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S3_ModularAdjustment &lt; 0">
	    			(<xsl:value-of select="format-number(substring(sum(/PROPOSAL/BUDGET/TASK_PERIODS
	    				/TASK_PERIOD[@PERIOD_NUMBER = '3']/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + 
	    				$S3_ModularAdjustment,2), '###,###')"/>)
	    		</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
   
			<!-- 5/12/2005 dterret: Modular adjustment added in separately because it is independent of
					 task. -->
      <xsl:variable name="S4X_TotalDirect" >
	      <xsl:choose>
					<xsl:when test="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '4']
						/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S4_ModularAdjustment &gt; 0">
						<xsl:value-of select="format-number(sum(/PROPOSAL/BUDGET/TASK_PERIODS
							/TASK_PERIOD[@PERIOD_NUMBER = '4']/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + 
							$S4_ModularAdjustment, '###,###')" />
					</xsl:when>
	      	<xsl:when test="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '4']
	      		/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S4_ModularAdjustment &lt; 0">
	      		(<xsl:value-of select="format-number(substring(sum(/PROPOSAL/BUDGET/TASK_PERIODS
	      			/TASK_PERIOD[@PERIOD_NUMBER = '4']/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + 
	      			$S4_ModularAdjustment,2), '###,###')"/>)
	      	</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
      
			<!-- 5/12/2005 dterret: Modular adjustment added in separately because it is independent of
					 task. -->
      <xsl:variable name="S5X_TotalDirect" >
	    	<xsl:choose>
					<xsl:when test="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '5']
						/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S5_ModularAdjustment &gt; 0">
						<xsl:value-of select="format-number(sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
							[@PERIOD_NUMBER = '5']/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S5_ModularAdjustment, '###,###')" />
					</xsl:when>
	    		<xsl:when test="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '5']
	    			/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S5_ModularAdjustment &lt; 0">
	    			(<xsl:value-of select="format-number(substring(sum(/PROPOSAL/BUDGET/TASK_PERIODS
	    				/TASK_PERIOD[@PERIOD_NUMBER = '5']/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + 
	    				$S5_ModularAdjustment,2), '###,###')"/>)
	    		</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>

			<!-- 5/12/2005 dterret: Modular adjustment added in separately because it is independent of
					 task. -->
      <xsl:variable name="S1_TotalDirect" >
	      <xsl:choose>
					<xsl:when test="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']
						/@TOTAL_AGENCY_REQUEST_DIRECT_COST">
						<xsl:value-of select=" sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']
							/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S1_ModularAdjustment" />
					</xsl:when>
					<xsl:otherwise>
						0
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>

			<!-- 5/12/2005 dterret: Modular adjustment added in separately because it is independent of
					 task. -->
      <xsl:variable name="S2_TotalDirect" >
	      <xsl:choose>
					<xsl:when test="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '2']
						/@TOTAL_AGENCY_REQUEST_DIRECT_COST">
						<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '2']
							/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S2_ModularAdjustment" />
					</xsl:when>
					<xsl:otherwise>
						0
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
      
			<!-- 5/12/2005 dterret: Modular adjustment added in separately because it is independent of
				task. -->
      <xsl:variable name="S3_TotalDirect" >
	      <xsl:choose>
					<xsl:when test="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '3']
						/@TOTAL_AGENCY_REQUEST_DIRECT_COST">
						<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '3']
							/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S3_ModularAdjustment" />
					</xsl:when>
					<xsl:otherwise>
						0
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
      
			<!-- 5/12/2005 dterret: Modular adjustment added in separately because it is independent of
					 task. -->
      <xsl:variable name="S4_TotalDirect" >
	      <xsl:choose>
					<xsl:when test="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '4']
						/@TOTAL_AGENCY_REQUEST_DIRECT_COST">
						<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '4']
							/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S4_ModularAdjustment" />
					</xsl:when>
					<xsl:otherwise>
						0
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
      
			<!-- 5/12/2005 dterret: Modular adjustment added in separately because it is independent of
					 task. -->
      <xsl:variable name="S5_TotalDirect" >
	      <xsl:choose>
					<xsl:when test="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '5']
						/@TOTAL_AGENCY_REQUEST_DIRECT_COST">
						<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '5']
							/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S5_ModularAdjustment" />
					</xsl:when>
					<xsl:otherwise>
						0
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>      
      
      <xsl:variable name="S1X_Subcontractors" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>

			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      
      <xsl:variable name="S2X_Subcontractors" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
			
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3X_Subcontractors" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
			
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4X_Subcontractors" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
		
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5X_Subcontractors" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
			
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>   
      
      <xsl:variable name="S1_Subcontractors" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2_Subcontractors" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S3_Subcontractors" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4_Subcontractors" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5_Subcontractors" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
              
      <xsl:variable name="S1_Fringe" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '1')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT">

<xsl:value-of select="sum(key('PERIOD', '1')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2_Fringe" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '2')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
     
      <xsl:variable name="S3_Fringe" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '3')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4_Fringe" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '4')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S5_Fringe" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '5')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S1_Salary" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '1')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY">

<xsl:value-of select="sum( key('PERIOD', '1')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S2_Salary" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY">

<xsl:value-of select="sum( key('PERIOD', '2')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3_Salary" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY">

<xsl:value-of select="sum( key('PERIOD', '3')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S4_Salary" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY">

<xsl:value-of select="sum( key('PERIOD', '4')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5_Salary" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY">

<xsl:value-of select="sum( key('PERIOD', '5')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S1_Hospitalization" >
	      <xsl:choose>

 			<xsl:when test="
      key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT or  key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT) + sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT) " />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S2_Hospitalization" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT or  key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT) + sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT)  " />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S3_Hospitalization" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT or  key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT) + sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT) " />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4_Hospitalization" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT or  key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT)  + sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT) " />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
  
      <xsl:variable name="S1X_Hospitalization" >
	         <xsl:choose>
			<xsl:when test="
      key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT &gt; 0 or  key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="format-number($S1_Hospitalization , '###,###')" />  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>     
      
      <!-- Variables with the double X are used on Page 5.  Their single X counterparts, which are used on page 4, produced garbage when re-used on page 5.  This was observed on personal computers and on the Server.  The results were not consistent among personal computers.  -->
      
      <xsl:variable name="S1XX_Hospitalization" >
	         <xsl:choose>
			<xsl:when test="
      key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT &gt; 0 or  key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="format-number($S1_Hospitalization , '###,###')" />  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>     
      
      <xsl:variable name="S5_Hospitalization" >
	    <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT or  key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT) + sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT) " />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
   
      <xsl:variable name="S2X_Hospitalization" >
	         <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT &gt; 0 or  key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="format-number($S2_Hospitalization , '###,###')" />  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>     
  
      <xsl:variable name="S3X_Hospitalization" >
	         <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT &gt; 0 or  key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="format-number($S3_Hospitalization , '###,###')" />  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>     
  
      <xsl:variable name="S4X_Hospitalization" >
	         <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT &gt; 0 or  key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="format-number($S4_Hospitalization , '###,###')" />  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>     
  
      <xsl:variable name="S5X_Hospitalization" >
	         <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT &gt; 0 or  key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="format-number($S5_Hospitalization , '###,###')" />  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>     
  
      <xsl:variable name="S1_IDC" >
	      <xsl:choose>
	      
			<xsl:when test=" key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT) " />
			</xsl:when>
			
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S2_IDC" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT) " />
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S3_IDC" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT) " />
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4_IDC" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT) " />
			</xsl:when>
  
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S5_IDC" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT) " />
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
     
      <xsl:variable name="S1X_IDC" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S1_IDC, '###,###')" />
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
    
      <xsl:variable name="S1XX_IDC" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S1_IDC, '###,###')" />
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S2X_IDC" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S2_IDC, '###,###')" />
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S3X_IDC" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S3_IDC, '###,###')" />
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4X_IDC" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S4_IDC, '###,###')" />
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S5X_IDC" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S5_IDC, '###,###')" />
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>     
      
      <xsl:variable name="S1_Outpatient" >
	      <xsl:choose>
	      
			<xsl:when test=" key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT) " />
			</xsl:when>
			
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S2_Outpatient" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT) " />
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S3_Outpatient" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT) " />
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4_Outpatient" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT) " />
			</xsl:when>
  
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S5_Outpatient" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT) " />
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
     
      <xsl:variable name="S1X_Outpatient" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S1_Outpatient, '###,###')" />
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
   
      <xsl:variable name="S1XX_Outpatient" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S1_Outpatient, '###,###')" />
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S2X_Outpatient" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S2_Outpatient, '###,###')" />
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S3X_Outpatient" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S3_Outpatient, '###,###')" />
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4X_Outpatient" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S4_Outpatient, '###,###')" />
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S5X_Outpatient" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S5_Outpatient, '###,###')" />
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>     
      
      <xsl:variable name="S1_Alterations" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2_Alterations" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S3_Alterations" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4_Alterations" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5_Alterations" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
     
      <xsl:variable name="S1X_Alterations" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
       
      <xsl:variable name="S1XX_Alterations" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2X_Alterations" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3X_Alterations" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4X_Alterations" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5X_Alterations" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>     
      
      <xsl:variable name="S1_Consultants" >
	      <xsl:choose>
			<xsl:when test="
      key('CATEGORY', 'Consultants')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('CATEGORY', 'Consultants')/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2_Consultants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3_Consultants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4_Consultants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5_Consultants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
           
      <xsl:variable name="S1_Participants" >
	      <xsl:choose>
			<xsl:when test="
      key('CATEGORY', 'Participant Expenses')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('CATEGORY', 'Participant Expenses')/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
          
      <xsl:variable name="S1_DUP_Participants" >
	      <xsl:choose>
			<xsl:when test="
      key('CATEGORY', 'Participant Expenses')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('CATEGORY', 'Participant Expenses')/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2_Participants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3_Participants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4_Participants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5_Participants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S1_Equipment" >
	      <xsl:choose>
			<xsl:when test="
   key('CATEGORY', 'Equipment')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2_Equipment" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3_Equipment" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4_Equipment" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5_Equipment" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
          
      <xsl:variable name="S1_Fellowships" >
	      <xsl:choose>
			<xsl:when test="
   key('CATEGORY', 'Fellowships')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
            
      <xsl:variable name="S1_DUP_Fellowships" >
	      <xsl:choose>
			<xsl:when test="
   key('CATEGORY', 'Fellowships')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2_Fellowships" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3_Fellowships" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4_Fellowships" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5_Fellowships" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S1_Travel" >
	      <xsl:choose>
			<xsl:when test="
      key('CATEGORY', 'Travel')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('CATEGORY', 'Travel')/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2_Travel" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3_Travel" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4_Travel" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5_Travel" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
			<!-- 5/10/2005 dterret: Modular adjustment amount added in. -->
      <xsl:variable name="S1_OtherExpenses" >
	      <xsl:choose>
					<xsl:when test="key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM
						[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]
						/AGENCY_REQUEST_AMOUNT">
						<xsl:value-of select="sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM
							[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]
							/AGENCY_REQUEST_AMOUNT) + $S1_ModularAdjustment"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$S1_ModularAdjustment"/>
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
      
			<!-- 5/10/2005 dterret: Modular adjustment amount added in. -->
      <xsl:variable name="S1_DUP_OtherExpenses" >
	      <xsl:choose>
					<xsl:when test="key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM
						[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]
						/AGENCY_REQUEST_AMOUNT">
						<xsl:value-of select="sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM
							[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]
							/AGENCY_REQUEST_AMOUNT) + $S1_ModularAdjustment" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$S1_ModularAdjustment"/>
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
      
			<!-- 5/10/2005 dterret: Modular adjustment amount added in. -->
      <xsl:variable name="S2_OtherExpenses" >
	      <xsl:choose>
					<xsl:when test="key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM
						[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]
						/AGENCY_REQUEST_AMOUNT">
						<xsl:value-of select="sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM
							[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]
							/AGENCY_REQUEST_AMOUNT) + $S2_ModularAdjustment" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$S2_ModularAdjustment"/>
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
      
			<!-- 5/10/2005 dterret: Modular adjustment amount added in. -->
      <xsl:variable name="S3_OtherExpenses" >
	      <xsl:choose>
					<xsl:when test="key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM
						[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]
						/AGENCY_REQUEST_AMOUNT">
						<xsl:value-of select="sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM
							[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]
							/AGENCY_REQUEST_AMOUNT) + $S3_ModularAdjustment"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$S3_ModularAdjustment"/>
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
      
			<!-- 5/10/2005 dterret: Modular adjustment amount added in. -->
      <xsl:variable name="S4_OtherExpenses" >
	      <xsl:choose>
					<xsl:when test="key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM
						[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]
						/AGENCY_REQUEST_AMOUNT">
						<xsl:value-of select="sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM
							[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]
							/AGENCY_REQUEST_AMOUNT) + $S4_ModularAdjustment" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$S4_ModularAdjustment"/>
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
      
			<!-- 5/10/2005 dterret: Modular adjustment amount added in. -->
      <xsl:variable name="S5_OtherExpenses" >
	      <xsl:choose>
					<xsl:when test="key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM
						[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]
						/AGENCY_REQUEST_AMOUNT">
						<xsl:value-of select="sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM
							[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]
							/AGENCY_REQUEST_AMOUNT) + $S5_ModularAdjustment"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$S5_ModularAdjustment"/>
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1_Supplies" >
	      <xsl:choose>
			<xsl:when test="
key('CATEGORY', 'Supplies')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2_Supplies" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>      

      <xsl:variable name="S3_Supplies" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4_Supplies" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5_Supplies" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT)" />
  
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
  
  <!--   (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((())))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))   -->
      
      <xsl:variable name="S1X_Fringe" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '1')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT">

<xsl:value-of select="format-number(sum(key('PERIOD', '1')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2X_Fringe" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '2')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3X_Fringe" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '3')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4X_Fringe" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '4')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5X_Fringe" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '5')/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S1X_Salary" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '1')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY">

<xsl:value-of select="format-number(sum( key('PERIOD', '1')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2X_Salary" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY">

<xsl:value-of select="format-number(sum( key('PERIOD', '2')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3X_Salary" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY">

<xsl:value-of select="format-number(sum( key('PERIOD', '3')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4X_Salary" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY">

<xsl:value-of select="format-number(sum( key('PERIOD', '4')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5X_Salary" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY">

<xsl:value-of select="format-number(sum( key('PERIOD', '5')/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>    
    
      <xsl:variable name="S1X_Consultants" >
	      <xsl:choose>
			<xsl:when test="
      key('CATEGORY', 'Consultants')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('CATEGORY', 'Consultants')/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2X_Consultants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3X_Consultants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4X_Consultants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT), '###,###')" />

			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5X_Consultants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Consultants']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
        
      <xsl:variable name="S1X_Participants" >
	      <xsl:choose>
			<xsl:when test="
      key('CATEGORY', 'Participant Expenses')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('CATEGORY', 'Participant Expenses')/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
     
      <xsl:variable name="S2X_Participants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3X_Participants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4X_Participants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT), '###,###')" />

			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
     
      <xsl:variable name="S5X_Participants" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Participant Expenses']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S1X_Equipment" >
	      <xsl:choose>
			<xsl:when test="
   key('CATEGORY', 'Equipment')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT), '###,###')" />  
  
			</xsl:when>
			<xsl:otherwise>
			
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2X_Equipment" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3X_Equipment" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4X_Equipment" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5X_Equipment" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
     
      <xsl:variable name="S1X_Fellowships" >
	      <xsl:choose>
			<xsl:when test="
   key('CATEGORY', 'Fellowships')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '1')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT), '###,###')" />  
  
			</xsl:when>
			<xsl:otherwise>
			
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2X_Fellowships" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3X_Fellowships" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4X_Fellowships" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5X_Fellowships" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S1X_Travel" >
	      <xsl:choose>
			<xsl:when test="
      key('CATEGORY', 'Travel')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('CATEGORY', 'Travel')/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2X_Travel" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3X_Travel" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4X_Travel" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5X_Travel" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Travel']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
			<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
			<xsl:variable name="S1_ModularAdjustment">
				<xsl:choose>
					<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
						/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '1']/MODULAR_ADJUSTMENT">
						<xsl:value-of select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
							/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '1']/MODULAR_ADJUSTMENT"/>
					</xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
			<xsl:variable name="S1_ModularAdjustment_Display">
				<xsl:choose>
					<xsl:when test="$S1_ModularAdjustment &gt;= 0">
						<xsl:value-of select="format-number($S1_ModularAdjustment, '###,###')"/>
					</xsl:when>
					<xsl:otherwise>
						(<xsl:value-of select="format-number(substring($S1_ModularAdjustment,2), '###,###')"/>)
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
	
			<!-- 5/10/2005 dterret: Modular adjustment addition to Other Expenses. -->
			<xsl:variable name="S2_ModularAdjustment">
				<xsl:choose>
					<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
						/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '2']/MODULAR_ADJUSTMENT">
						<xsl:value-of select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
							/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '2']/MODULAR_ADJUSTMENT"/>
					</xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<!-- 5/10/2005 dterret: Modular adjustment addition to Other Expenses. -->
			<xsl:variable name="S3_ModularAdjustment">
				<xsl:choose>
					<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
						/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '3']/MODULAR_ADJUSTMENT">
						<xsl:value-of select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
							/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '3']/MODULAR_ADJUSTMENT"/>
					</xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
	
			<!-- 5/10/2005 dterret: Modular adjustment addition to Other Expenses. -->
			<xsl:variable name="S4_ModularAdjustment">
				<xsl:choose>
					<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
						/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '4']/MODULAR_ADJUSTMENT">
						<xsl:value-of select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
							/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '4']/MODULAR_ADJUSTMENT"/>
					</xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
	
			<!-- 5/10/2005 dterret: Modular adjustment addition to Other Expenses. -->
			<xsl:variable name="S5_ModularAdjustment">
				<xsl:choose>
					<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
						/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '5']/MODULAR_ADJUSTMENT">
						<xsl:value-of select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
							/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '5']/MODULAR_ADJUSTMENT"/>
					</xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
			<xsl:variable name="S1X_OtherExpenses" >
				<xsl:choose>
					<xsl:when test="$S1_OtherExpenses + $S1_Fellowships + $S1_Participants &gt; 0">
						<xsl:value-of select="format-number($S1_OtherExpenses + $S1_Fellowships + $S1_Participants,
							'###,###')" /></xsl:when>
					<xsl:when test="$S1_OtherExpenses + $S1_Fellowships + $S1_Participants &lt; 0">
						(<xsl:value-of select="format-number(substring($S1_OtherExpenses + $S1_Fellowships + 
							$S1_Participants,2), '###,###')" />)</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
	
			<xsl:variable name="S1_SubtotalDirect" select="$S1_Salary + $S1_Fringe + $S1_Consultants + 
				$S1_Equipment + $S1_Travel + $S1_Supplies + $S1_DUP_OtherExpenses + $S1_Hospitalization +  
				$S1_DUP_Fellowships + $S1_Outpatient + $S1_DUP_Participants  + $S1_Alterations + 
				$S1_Subcontractors"/>
	
			<xsl:variable name="S2_SubtotalDirect" select="$S2_Salary + $S2_Fringe + $S2_Consultants + 
				$S2_Equipment + $S2_Travel + $S2_Supplies + $S2_OtherExpenses + $S2_Hospitalization +  
				$S2_Fellowships + $S2_Outpatient  + $S2_Participants + $S2_Alterations + 
				$S2_Subcontractors"/>
	
			<xsl:variable name="S3_SubtotalDirect" select="$S3_Salary + $S3_Fringe + $S3_Consultants + 
				$S3_Equipment + $S3_Travel + $S3_Supplies + $S3_OtherExpenses + $S3_Hospitalization +  
				$S3_Fellowships  + $S3_Outpatient + $S3_Participants + $S3_Alterations + 
				$S3_Subcontractors"/>
	
			<xsl:variable name="S4_SubtotalDirect" select="$S4_Salary + $S4_Fringe + $S4_Consultants + 
				$S4_Equipment + $S4_Travel + $S4_Supplies + $S4_OtherExpenses + $S4_Hospitalization +  
				$S4_Fellowships + $S4_Outpatient + $S4_Participants + $S4_Alterations + 
				$S4_Subcontractors"/>
	
			<!-- 6/29/2005 dterret: Bug fix for improper addition.-->
			<xsl:variable name="S5_SubtotalDirect" select="$S5_Salary + $S5_Fringe + $S5_Consultants + 
				$S5_Equipment + $S5_Travel + $S5_Supplies + $S5_OtherExpenses + $S5_Hospitalization +  
				$S5_Fellowships  + $S5_Outpatient + $S5_Participants + $S5_Alterations + 
				$S5_Subcontractors"/>
	
			<!-- 5/9/2005 dterret: Bug fix for negative case. -->
			<xsl:variable name="S1X_Subtotal_Direct_Costs">
				<xsl:choose>
					<xsl:when test="$S1_SubtotalDirect &gt; 0">
						<xsl:value-of select="format-number($S1_SubtotalDirect, '###,###')" /></xsl:when>
					<xsl:when test="$S1_SubtotalDirect &lt; 0">
						(<xsl:value-of select="format-number(substring($S1_SubtotalDirect,2), '###,###')" />)</xsl:when>
					<xsl:otherwise>
					
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
                
      <xsl:variable name="S1X_DUP_OtherExpenses" >
	      <xsl:choose>
					<xsl:when test="$S1_DUP_OtherExpenses + $S1_DUP_Fellowships + $S1_DUP_Participants &gt; 0">
						<xsl:value-of select="format-number($S1_DUP_OtherExpenses + $S1_DUP_Fellowships 
							+ $S1_DUP_Participants, '###,###')" />
					</xsl:when>
	      	<xsl:when test="$S1_DUP_OtherExpenses + $S1_DUP_Fellowships + $S1_DUP_Participants &lt; 0">
	      		(<xsl:value-of select="format-number(substring($S1_DUP_OtherExpenses + 
	      			$S1_DUP_Fellowships + $S1_DUP_Participants,2), '###,###')" />)</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S1XX_OtherExpenses" >
	      <xsl:choose>
					<xsl:when test="$S1_OtherExpenses + $S1_Fellowships + $S1_Participants &gt; 0">
						<xsl:value-of select="format-number($S1_OtherExpenses + $S1_Fellowships + 
							$S1_Participants, '###,###')" />
					</xsl:when>
	      	<xsl:when test="$S1_OtherExpenses + $S1_Fellowships + $S1_Participants &lt; 0">
						(<xsl:value-of select="format-number(substring($S1_OtherExpenses + $S1_Fellowships + 
							$S1_Participants,2), '###,###')" />)
	      	</xsl:when>
	      	<xsl:otherwise>
	      		
	      	</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
           
      <xsl:variable name="S2X_OtherExpenses" >
	   		<xsl:choose>
					<xsl:when test="$S2_OtherExpenses + $S2_Fellowships + $S2_Participants &gt; 0">
						<xsl:value-of select="format-number($S2_OtherExpenses + $S2_Fellowships + 
							$S2_Participants, '###,###')" />
					</xsl:when>
	   			<xsl:when test="$S2_OtherExpenses + $S2_Fellowships + $S2_Participants &lt; 0">
	   				(<xsl:value-of select="format-number(substring($S2_OtherExpenses + $S2_Fellowships + 
	   					$S2_Participants,2), '###,###')"/>)
	   			</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3X_OtherExpenses" >
	    	<xsl:choose>
					<xsl:when test="$S3_OtherExpenses + $S3_Fellowships + $S3_Participants &gt; 0">
						<xsl:value-of select="format-number($S3_OtherExpenses + $S3_Fellowships + 
							$S3_Participants, '###,###')" />
					</xsl:when>
	    		<xsl:when test="$S3_OtherExpenses + $S3_Fellowships + $S3_Participants &lt; 0">
	    			(<xsl:value-of select="format-number(substring($S3_OtherExpenses + $S3_Fellowships + 
	    				$S3_Participants,2), '###,###')" />)
	    		</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4X_OtherExpenses" >
	    	<xsl:choose>
					<xsl:when test="$S4_OtherExpenses + $S4_Fellowships + $S4_Participants &gt; 0">
						<xsl:value-of select="format-number($S4_OtherExpenses + $S4_Fellowships + 
							$S4_Participants, '###,###')"/>
					</xsl:when>
	    		<xsl:when test="$S4_OtherExpenses + $S4_Fellowships + $S4_Participants &lt; 0">
	    			(<xsl:value-of select="format-number(substring($S4_OtherExpenses + $S4_Fellowships + 
	    				$S4_Participants,2), '###,###')"/>)
	    		</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S5X_OtherExpenses" >
	    	<xsl:choose>
					<xsl:when test="$S5_OtherExpenses + $S5_Fellowships + $S5_Participants &gt; 0">
						<xsl:value-of select="format-number($S5_OtherExpenses + $S5_Fellowships + 
							$S5_Participants, '###,###')" />
					</xsl:when>
	    		<xsl:when test="$S5_OtherExpenses + $S5_Fellowships + $S5_Participants &lt; 0">
	    			(<xsl:value-of select="format-number(substring($S5_OtherExpenses + $S5_Fellowships + 
	    				$S5_Participants,2), '###,###')" />)
	    		</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>
       
      <xsl:variable name="S1X_Supplies" >
	      <xsl:choose>
			<xsl:when test="key('CATEGORY', 'Supplies')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('CATEGORY', 'Supplies')/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S2X_Supplies" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '2')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S3X_Supplies" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '3')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S4X_Supplies" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '4')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S5X_Supplies" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', '5')/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
       
<!--  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Now that keys and universal variables are defined, the XSL-FO layout code begins.

 -->

<xsl:template match="/">
	
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

		<fo:layout-master-set>
    		<fo:simple-page-master master-name="first" 
                           page-height="11in" 
                           page-width="8.5in" 
                           margin-bottom="0.5in" 
                           margin-left="0.5in" 
                           margin-right="0.5in"
 				margin-top="0.5in">
 				
 	   			<fo:region-body />			
 				
    		</fo:simple-page-master>
 		</fo:layout-master-set>
	
  		<fo:page-sequence master-reference="first">
			<fo:flow flow-name="xsl-region-body">
		
    			<fo:table   table-layout="fixed">
      				<fo:table-column column-width="95mm"/>
      				<fo:table-column column-width="95mm"/>
    				<fo:table-body>
    
        				<fo:table-row >
        
          					<fo:table-cell>
		
								<fo:block font-size="8pt" 
									text-align="left" 
									margin-left="2cm"
									space-after="3.2mm"
									space-before="2mm"
									start-indent="4mm">
			
<!--  The following code puts the project director / principal investigator's name at the top of Page 4.  Similar code is used to put the name at the top of Page 5 and on the overflow personnel pages.  -->			
			
			Principal Investigator/Program Director (Last, first, middle)
		</fo:block>
		</fo:table-cell>

          <fo:table-cell padding-right="2mm" display-align="center">
     	  	       	<fo:block  >
		       	
		       <xsl:if test="not(//PROJECT_DIRECTOR/@FIRST_NAME = 'To Be Named') " >
			       
		       	<xsl:value-of select="substring(concat(//PROJECT_DIRECTOR/@LAST_NAME, ', ', //PROJECT_DIRECTOR/@FIRST_NAME ), 1, 28)" />
		       </xsl:if>	
		            		       	
		       <xsl:if test="//PROJECT_DIRECTOR/@FIRST_NAME = 'To Be Named' " >
		       	To Be Named
		       </xsl:if>	
     	  	</fo:block>

</fo:table-cell>

        </fo:table-row>
      </fo:table-body>
    </fo:table>

    <fo:table   table-layout="fixed"      >
      <fo:table-column column-width="127mm"/>
      <fo:table-column column-width="32mm"/>
      <fo:table-column column-width="31mm"/>
    <fo:table-body>
    
        <fo:table-row height="11.8mm">
        
          <fo:table-cell border-left-width="0mm" border-style="solid" text-align="center" font-size="11pt" display-align="center">
          <fo:block font-weight="bold">
	DETAILED BUDGET FOR INITIAL BUDGET PERIOD <fo:block />DIRECT COSTS ONLY
		</fo:block>
		</fo:table-cell>

          <fo:table-cell border-style="solid"  font-size="8pt" border-left-width="0mm" >
          
          <fo:block start-indent="1mm"  padding-before.length="1mm">FROM
<fo:block padding-top="1.4mm"></fo:block>
<fo:inline font-size="10pt" ><xsl:value-of select="/PROPOSAL/BUDGET/PERIODS/PERIOD[PERIOD_NUMBER='1']/START_DATE" /></fo:inline>
</fo:block >


</fo:table-cell>
          <fo:table-cell border-style="solid" font-size="8pt" border-left-width="0mm" border-right-width="0mm" start-indent="1mm">
<fo:block padding-before.length="1mm">
          THROUGH


<fo:block padding-top="1.4mm"></fo:block>
<fo:inline font-size="10pt" ><xsl:value-of select="/PROPOSAL/BUDGET/PERIODS/PERIOD[PERIOD_NUMBER='1']/STOP_DATE" /></fo:inline>
</fo:block >


</fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>

<!--   END OF FIRST TABLE

		The following table produces the heading for the personnel section.  -->
	    

	
    <fo:table   table-layout="fixed"       >
      <fo:table-column column-width="49mm"/>
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="19mm"/>
      <fo:table-column column-width="20mm"/>
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="22mm"/>
  
      <fo:table-body>
   
      <fo:table-row font-size="8pt">   

     	 	<fo:table-cell border-style="solid" text-align="left" 
      			border-top-width="0mm" border-left-width="0mm" padding-before="1mm" number-columns-spanned="2">	
      			
	         	<fo:block start-indent="2mm" >
PERSONNEL <fo:inline font-style="italic">(Applicant organization only)</fo:inline>
			</fo:block>
		</fo:table-cell>
		
	      	<fo:table-cell border-style="solid" text-align="left" 
	      		border-top-width="0mm" border-left-width="0mm" padding-before="1mm" number-columns-spanned="3">	
	      		
	      		<fo:block start-indent="1mm" >
	      			Months Devoted to Project
	      		</fo:block>
	      	</fo:table-cell>
            
		<fo:table-cell border-style="solid" text-align="center" 
				border-top-width="0mm" border-left-width="0mm" padding-before="1mm" number-rows-spanned="2">
	         	<fo:block padding-before="4mm">

INST. BASE<fo:block></fo:block>SALARY
			</fo:block>
		</fo:table-cell>         

		<fo:table-cell border-style="solid" text-align="left" 
				border-top-width="0mm" border-left-width="0mm" border-right-width="0mm"  number-columns-spanned="3" padding-before="1mm" >
	         	<fo:block start-indent="2mm" text-indent="2mm">
	         	 DOLLAR AMOUNT REQUESTED<fo:inline font-style="italic"> (omit cents)</fo:inline>
			</fo:block >
		</fo:table-cell>
   
        </fo:table-row>

    <fo:table-row font-size="8pt">   
     	 	<fo:table-cell border-style="solid" text-align="center" font-size="8pt" height="5mm" 
      			border-top-width="0mm" border-left-width="0mm" >
	         	<fo:block  padding-before="4.5mm"   >
	         	

NAME
			</fo:block>
		</fo:table-cell>
		
   		<fo:table-cell border-style="solid" text-align="center" 
   			 height="5.5mm" border-top-width="0mm" border-left-width="0mm">
	         	<fo:block padding-before="3mm">
ROLE ON <fo:block></fo:block>PROJECT
			</fo:block>
		</fo:table-cell>
            
	    	<fo:table-cell border-style="solid" text-align="center" 
	    		height="5.5mm" border-top-width="0mm" border-left-width="0mm">
	    		<fo:block padding-before="3mm">
	    			Cal.<fo:block></fo:block>Mnths
	    		</fo:block>
	    	</fo:table-cell>
	
	    	<fo:table-cell border-style="solid" text-align="center" 
	    		height="5.5mm" border-top-width="0mm" border-left-width="0mm">
	    		<fo:block padding-before="3mm">
	    			Acad.<fo:block></fo:block>Mnths
	    		</fo:block>
	    	</fo:table-cell>
	    	
	    	<fo:table-cell border-style="solid" text-align="center" 
	    		height="5.5mm" border-top-width="0mm" border-left-width="0mm">
	    		<fo:block padding-before="3mm">
	    			Sum.<fo:block></fo:block>Mnths
	    		</fo:block>
	    	</fo:table-cell>
    	
		<fo:table-cell border-style="solid" text-align="center" height="5.5mm" 
				border-top-width="0mm" border-left-width="0mm" >
	         	<fo:block padding-before="2mm">

SALARY REQUESTED
			</fo:block>
		</fo:table-cell>         

		<fo:table-cell border-style="solid" text-align="center" font-size="8pt" height="5.5mm" 
				border-top-width="0mm" border-left-width="0mm"  >
	         	<fo:block padding-before="2mm">
 FRINGE BENEFITS
			</fo:block  >
		</fo:table-cell>
   
		<fo:table-cell border-style="solid" text-align="center" font-size="8pt" height="5.5mm" 
				border-top-width="0mm" border-left-width="0mm" border-right-width="0mm" padding-before="2mm">
	         	<fo:block padding-before="3mm">
TOTAL
			</fo:block>
		</fo:table-cell>
          
        </fo:table-row>
   
      </fo:table-body>
    </fo:table>
		
    <fo:table   table-layout="fixed"       >
      <fo:table-column column-width="49mm"/>
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="19mm"/>
      <fo:table-column column-width="20mm"/>
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="22mm"/>
 
     <fo:table-body line-height="9.4pt">
     
     
<!--   The following code fills in the NAME and ROLE boxes of the first row of the Personnel section on Page 5 when PROJECT DIRECTOR is "To be named."  If there is a Person in the Personnel section flagged TRUE for Project Director, that persons data is entered in the first row, with one exception.  The exception is that regadless of what the user enters as the Project Director's Role, the role will appear as "Principal Investigator."

For the follow see the xsl:key PERSON_KEY and its two variables:

$PERSON_PI is true only for the person flagged "TRUE" for Project Director in the personnel section of the XML file.  

Note that $PERSON_OTHER is not the same as  not($PERSON_PI), since a person might not be flagged TRUE or FALSE.  

 -->     
     
     
<xsl:if test="not($PERSON_PI)" >

      <fo:table-row height="9.3mm">   
      
  <!--   	 
     	 	<fo:table-cell border-style="solid" text-align="left"   
      			border-top-width="0mm" border-left-width="0mm"   font-size="10pt" display-align="center" >
	     	    	<fo:block text-align="center" padding-top="1mm" >
	         		  Select "Add Person" and enter<fo:block></fo:block>data for Principal Investigator
				</fo:block>
			</fo:table-cell>
	-->		
			  	 
     	 	<fo:table-cell border-style="solid" text-align="left"   
      			border-top-width="0mm" border-left-width="0mm"   font-size="10pt" display-align="center" >
	<fo:block font-size="10" padding-top="0.8mm" padding-bottom="0.2mm" line-height="12.2pt" >
	
	
		       	
		       <xsl:if test="not(//PROJECT_DIRECTOR/@FIRST_NAME = 'To Be Named') " >
		       	<xsl:value-of select="substring(concat(//PROJECT_DIRECTOR/@LAST_NAME, ', ', //PROJECT_DIRECTOR/@FIRST_NAME ), 1, 28)" />
		       	</xsl:if>	
		       	
		       <xsl:if test="//PROJECT_DIRECTOR/@FIRST_NAME = 'To Be Named' " >
		       	To Be Named
		       	</xsl:if>	
		  	       	
				</fo:block>
			</fo:table-cell>

	  		<fo:table-cell border-style="solid" text-align="left" padding-left="1.5mm" 
   			font-size="10pt"  border-top-width="0mm" border-left-width="0mm" display-align="after"  >
    			
			<fo:block font-size="10" padding-top="0.8mm" padding-bottom="0.2mm" line-height="12.2pt" display-align="center">Principal  <fo:block ></fo:block>  Investigator</fo:block>
			</fo:table-cell>

		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
				border-top-width="0mm" border-left-width="0mm" display-align="after" >
	         	<fo:block >
			
			</fo:block>
		</fo:table-cell>        

	      	<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
	      		border-top-width="0mm" border-left-width="0mm" display-align="after" >
	      		<fo:block >
	      			
	      		</fo:block>
	      	</fo:table-cell>
      	
		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
				border-top-width="0mm" border-left-width="0mm" display-align="after" >
	         	<fo:block >
	         		
			</fo:block>
		</fo:table-cell>
            
		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
				border-top-width="0mm" border-left-width="0mm" display-align="after" >
	         	<fo:block >
			</fo:block>
		</fo:table-cell>         

		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
				border-top-width="0mm" border-left-width="0mm" display-align="after" >
	         	<fo:block >
	         		 
			</fo:block>
		</fo:table-cell>
   
		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  border-right-width="0mm"
				border-top-width="0mm" border-left-width="0mm" display-align="after" >
	         	<fo:block >

	         	</fo:block>
		</fo:table-cell>
            
    		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
				border-top-width="0mm" border-left-width="0.3mm" border-right-width="0mm"  >
	         	<fo:block >
			</fo:block>
		</fo:table-cell>      
        </fo:table-row>     
	</xsl:if>
	
<!--   The following code fills in the first row of the PERSONNEL section of Page 4 when someone is flagged "TRUE" for personnel dierector   -->	

<xsl:if test="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[@PROJECT_DIRECTOR = 'TRUE']" >
		<xsl:call-template name="People" >
			<xsl:with-param name="End" select="$PI_Count + 1" />
			<xsl:with-param name="Start" select="0" />
			<xsl:with-param name="PI" select="$PERSON_PI" />
		</xsl:call-template>
</xsl:if>		
	
	<xsl:if test="count($PERSON_OTHER) + $PI_Count = 7" >
		<xsl:call-template name="People" >
			<xsl:with-param name="End" select="9 - $PI_Count" />
			<xsl:with-param name="Start" select="0" />
			<xsl:with-param name="PI" select="$PERSON_OTHER" />
		</xsl:call-template>
	</xsl:if>	
	
	<xsl:if test="count($PERSON_OTHER) + $PI_Count  &lt; 7" >
		<xsl:call-template name="People" >
			<xsl:with-param name="End" select="8 - $PI_Count" />
			<xsl:with-param name="Start" select="0" />
			<xsl:with-param name="PI" select="$PERSON_OTHER" />
		</xsl:call-template>
		
  		<xsl:call-template name="ExtraRows" >
  			<xsl:with-param name="n" select="6 - count($PERSON_OTHER) - $PI_Count " />
  		</xsl:call-template>
	</xsl:if>
         
	<xsl:if test="count($PERSON_OTHER) + $PI_Count  &gt; 7" >
		<xsl:call-template name="People" >
			<xsl:with-param name="End" select="7 - $PI_Count" />
			<xsl:with-param name="Start" select="0" />
			<xsl:with-param name="PI" select="$PERSON_OTHER" />
		</xsl:call-template>
	</xsl:if>
		
      </fo:table-body>
    </fo:table>
   
   <xsl:if test="count($PERSON_OTHER) + $PI_Count  &gt; 7" >
	 
    <fo:table   table-layout="fixed"       >
      <fo:table-column column-width="49mm"/>
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="19mm"/>
      <fo:table-column column-width="20mm"/>
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="22mm"/>
 
     <fo:table-body>
      
      
      <fo:table-row height="5.5mm">   
      
     	 	<fo:table-cell border-style="solid" text-align="left" font-size="10pt" height="5mm" 
      			border-top-width="0mm" border-left-width="0mm" display-align="after" >
      			
	         	<fo:block  padding-left="2mm" display-align="after" line-height="10pt" font-style="italic">
	         		 Subtotals From the</fo:block><fo:block font-style="italic" line-height="10pt"> Following Page(s)
			</fo:block>
		</fo:table-cell>
		
   		<fo:table-cell border-style="solid" text-align="center" 
   			font-size="8pt" height="5.5mm" border-top-width="0mm" border-left-width="0mm" padding-start="2mm" padding-before="2mm">
	         	<fo:block >

			</fo:block>
		</fo:table-cell>


		<fo:table-cell border-style="solid" text-align="center" font-size="8pt" height="5.5mm" 
				border-top-width="0mm" border-left-width="0mm" >
	         	<fo:block >

			</fo:block>
		</fo:table-cell>        

	      	<fo:table-cell border-style="solid" text-align="center" font-size="8pt" height="5.5mm" 
	      		border-top-width="0mm" border-left-width="0mm" >
	      		<fo:block >
	      			
	      		</fo:block>
	      	</fo:table-cell>
      	
		<fo:table-cell border-style="solid" text-align="center" font-size="8pt" height="5.5mm" 
				border-top-width="0mm" border-left-width="0mm" >
	         	<fo:block >

			</fo:block>
		</fo:table-cell>
            
		<fo:table-cell border-style="solid" text-align="center" font-size="8pt" height="5.5mm" 
				border-top-width="0mm" border-left-width="0mm" >
	         	
		</fo:table-cell>         

	<fo:table-cell border-style="solid" text-align="right" 
   			font-size="10pt"  border-top-width="0mm" border-left-width="0mm" display-align="after" padding-right="1mm" >
	         	<fo:block>

<xsl:call-template name="SalarySubtotal" >
	<xsl:with-param name="total" select="0" />
	<xsl:with-param name="n" select="7 - $PI_Count" />
	<xsl:with-param name="p" select="$FilteredPersonCount + 1" />
</xsl:call-template>	
	

			</fo:block>
		</fo:table-cell>
   <fo:table-cell border-style="solid" text-align="right" 
   			font-size="10pt"  border-top-width="0mm" border-left-width="0mm" border-right-width="0mm" display-align="after" padding-right="1mm" >
	         	<fo:block >

<xsl:call-template name="FringeSubtotal" >
	<xsl:with-param name="total" select="0" />
	<xsl:with-param name="n" select="7 - $PI_Count" />
	<xsl:with-param name="p" select="$FilteredPersonCount + 1" />

</xsl:call-template>	
	

			</fo:block>
		</fo:table-cell>
      <fo:table-cell border-style="solid" text-align="right" 
   			font-size="10pt"  border-top-width="0mm" border-right-width="0mm" display-align="after" padding-right="1mm" >
	         	<fo:block >

<xsl:call-template name="TotalSubtotal" >
	<xsl:with-param name="total" select="0" />
	<xsl:with-param name="n" select="7 - $PI_Count" />
	<xsl:with-param name="p" select="$FilteredPersonCount + 1" />

</xsl:call-template>	
	

			</fo:block>
		</fo:table-cell>      
        </fo:table-row>
        
   
      </fo:table-body>
    </fo:table>
</xsl:if>


<!--  ################   NEW TABLE   ############### -->           

    <fo:table   table-layout="fixed"      >
      <fo:table-column column-width="78mm"/>
         <fo:table-column column-width="45mm"/>
           <fo:table-column column-width="23mm"/>
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="22mm"/>
 
    <fo:table-body>
    
        <fo:table-row height="8.4mm">
        
     <fo:table-cell  display-align="after" font-size="10pt" padding-right="3mm">

		<fo:block start-indent="1mm" text-align="right" font-weight="bold" >
				SUBTOTALS         
		</fo:block >
</fo:table-cell>
    
     <fo:table-cell border-right-style="solid"   display-align="after" >

		<fo:block  text-align="left" >
			<fo:external-graphic width="36mm"  height="3mm"  src="url({$baseUrl}{$imageArrow})" />
			
		
		</fo:block >
</fo:table-cell>

    <fo:table-cell border-style="solid" text-align="right"  border-right="0.6mm" border-bottom="0.6mm" border-top="0.6mm" border-left="0.6mm"
   			font-size="10pt"   display-align="after" padding-right="1mm" >

		<fo:block start-indent="1mm">
	
	<fo:block></fo:block>
		
			<xsl:value-of select="$S1X_Salary" />
		</fo:block ></fo:table-cell>
		
           <fo:table-cell border-style="solid" border-bottom-width="0.6mm" border-right-width="0mm" border-left-width="0mm"     text-align="right" border-top="0.6mm"
   			font-size="10pt"  display-align="after" padding-right="1mm" >

		<fo:block start-indent="1mm"  >
 
<xsl:value-of select="$S1X_Fringe" />
          </fo:block ></fo:table-cell>
  
            <fo:table-cell border-style="solid" text-align="right"
   			font-size="10pt"  border-top-width="0.6mm" border-bottom-width="0.6mm"  border-right-width="0.6mm"  display-align="after" border-left-width="0.6mm" padding-right="1mm" >

<fo:block>

<xsl:value-of select="format-number($S1_Salary + $S1_Fringe, '###,###') "/>

</fo:block >

</fo:table-cell>

        </fo:table-row>
      </fo:table-body>
    </fo:table>
    
<!--
    		The following table sets the boundaries for most of Page 4.  Data for CONSULTANTS, EQUIPMENT, etc is entered into this table by calling the corresponding templates.  The templates define rows and cells.

Note that variables of the form ?1_ are numbers, which can be used for numerical tests and mathmatical manipulaitons.  Variable of the form ?1X_ are formatted numbers, which are really strings, and are not suitable for addition, numberical tests, etc.  
     -->
 
    <fo:table   table-layout="fixed"      >
    	<fo:table-column column-width="44mm"/>
        <fo:table-column column-width="3mm"/>
        <fo:table-column column-width="7.5mm"/>
        <fo:table-column column-width="5.5mm"/>
		<fo:table-column column-width="18mm"/>  
        <fo:table-column column-width="60mm"/>
        <fo:table-column column-width="18mm"/>
        <fo:table-column column-width="3.5mm"/>
        <fo:table-column column-width="3.5mm"/> 
        <fo:table-column column-width="5mm"/>
        <fo:table-column column-width="22mm"/>             
               
     <fo:table-body>
     
	<xsl:call-template name="Consultants" >
		<xsl:with-param name="n" select="1" />
	</xsl:call-template>

	<xsl:call-template name="Equipment" >
		<xsl:with-param name="n" select="1" />
	</xsl:call-template>

	<xsl:call-template name="Supplies" >
		<xsl:with-param name="n" select="1" />
	</xsl:call-template>
    
	<xsl:call-template name="Travel" >
		<xsl:with-param name="n" select="1" />
	</xsl:call-template>

       <fo:table-row>

          <fo:table-cell border-left-width="0mm"  border-top-width="0.3mm"  border-right-width="0mm"    border-style="solid" 
          		  font-size="8pt"  font-family="Arial, sans-serif"  padding-before="0.4mm" number-rows-spanned="2" height="10.5mm" start-indent="2mm">
          <fo:block >PATIENT CARE COSTS
		</fo:block>
		</fo:table-cell>
		
    <fo:table-cell border-style="solid"   font-size="8pt"  font-family="Arial, sans-serif" 
		border-left-width="0.3mm" 
		border-top-width="0.3mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm" 
	       text-align="left"   
	       padding-before="0.5mm"
	       	       padding-after="1.5mm"
	       number-columns-spanned="3" >
	       
	       	<fo:block start-indent="1.5mm">
	       		INPATIENT
	       	</fo:block >
	</fo:table-cell>
	       
	 <fo:table-cell border-style="solid"   font-size="10pt"  font-family="Arial, sans-serif" border-left-width="0mm"
       	border-top-width="0.3mm" number-columns-spanned="6" display-align="center"
		border-right-width="0mm" 
		border-bottom-width="0.3mm">
		
	
	</fo:table-cell>
	
	<fo:table-cell border-style="solid" border-right-width="0mm" display-align="after" font-size="10pt">
		<fo:block text-align="right" margin-right="2mm">
			<xsl:if test="$S1_Hospitalization &gt; 0" >
				<xsl:value-of select="$S1X_Hospitalization" />
			</xsl:if>
		</fo:block>
	</fo:table-cell>
	
     </fo:table-row>

       <fo:table-row>
			
    <fo:table-cell border-style="solid"   font-size="8pt"  font-family="Arial, sans-serif" 
		border-left-width="0.3mm" 
		border-top-width="0mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm" 
	       text-align="left"   
	       padding-before="0.5mm"
	       	       padding-after="1.5mm"
	       number-columns-spanned="9" >
	       
	       	<fo:block start-indent="1.5mm">
	       		OUTPATIENT
	       	</fo:block >
	</fo:table-cell>
	
	<fo:table-cell border-style="solid" border-right-width="0mm" display-align="after" font-size="10pt">
		<fo:block text-align="right" margin-right="2mm">
			<xsl:if test="$S1_Outpatient &gt; 0" >
				<xsl:value-of select="$S1X_Outpatient" />
			</xsl:if>
		</fo:block>
	</fo:table-cell>
</fo:table-row>
	

<!--   AAAAAA  ALTERATIONS  AAAAAAAAA    -->

<xsl:call-template name="Alterations" >
	<xsl:with-param name="n" select="1" />
</xsl:call-template>

<!--     OTHER EXPENSES 	In two parts.   -->

	<xsl:call-template name="Other_A" />

	<xsl:call-template name="Other_B" >
		<xsl:with-param name="n" select="1" />
	</xsl:call-template>
       </fo:table-body>
     </fo:table>

    <fo:table   table-layout="fixed"      >
          <fo:table-column column-width="99.5mm"/>
          <fo:table-column column-width="40mm"/>
          <fo:table-column column-width="23.5mm"/>
           <fo:table-column column-width="5mm"/>
          <fo:table-column column-width="21mm"/>
     <fo:table-body>
       <fo:table-row height="6mm">

         <fo:table-cell  font-size="8pt"  font-family="Arial, sans-serif" border-style="solid" display-align="center" padding-left="2mm" border-left-width="0mm" border-bottom-width="0mm">
          <fo:block >CONSORTIUM/CONTRACTUAL COSTS</fo:block >         
		
		</fo:table-cell>

                <fo:table-cell text-align="right" font-size="8pt"  font-family="Arial, sans-serif" border-style="solid" number-columns-spanned="2" display-align="center" padding-right="2mm">
	       	<fo:block start-indent="1.5mm">
	       		DIRECT COSTS
	       	</fo:block >
	</fo:table-cell>
	       
	 <fo:table-cell border-style="solid"   font-size="8pt"  font-family="Arial, sans-serif" border-left-width="0.3mm" 
       	border-top-width="0.3mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm"
		number-columns-spanned="5"
		display-align="after">
		
			<fo:block start-indent="1mm" text-align="right" font-size="10pt" margin-right="1.3mm">	
<xsl:value-of select="$S1X_Subcontractors" />
			</fo:block>
	</fo:table-cell>
         </fo:table-row>
 
     <fo:table-row height="9mm">
   
          <fo:table-cell border-left-width="0mm"  border-top-width="0.3mm"  border-right-width="0mm"    border-style="solid" 
          		  font-size="10pt"  font-family="Arial, sans-serif"  number-columns-spanned="3"  start-indent="2mm" display-align="after" padding-bottom="1mm">
          <fo:block font-weight="bold">SUBTOTAL DIRECT COSTS FOR INITIAL BUDGET PERIOD
          <fo:inline font-size="8pt" font-weight="100" font-style="italic" >
          	 (Item 7a, Face Page)
         	</fo:inline>
		</fo:block >
		</fo:table-cell>
	
    <fo:table-cell border-style="solid" text-align="right" border-right-width="0mm" border-bottom-width="0.6mm"
   			font-size="10pt" border-top-width="0.6mm"  border-left-width="0.6mm" display-align="after" padding-right="1mm" >
	       
	       	<fo:block start-indent="1mm" font-weight="bold">
	$
	       	</fo:block >
	</fo:table-cell>

	
	   <fo:table-cell border-style="solid" text-align="right" 
   			font-size="10pt"  border-top-width="0.6mm" border-left-width="0mm" border-right-width="0.6mm" border-bottom-width="0.6mm" display-align="after" padding-right="1mm" >

	       	<fo:block >
	       		<xsl:value-of select="$S1X_Subtotal_Direct_Costs"/>	
	       	</fo:block >
	</fo:table-cell>
	       
         </fo:table-row>
         
       <fo:table-row height="6mm">

         <fo:table-cell  font-size="8pt"  font-family="Arial, sans-serif" border-style="solid" border-left-width="0mm" padding-left="2mm" border-top-width="0mm" display-align="center" >
          <fo:block >CONSORTIUM/CONTRACTUAL COSTS</fo:block>         
		
		</fo:table-cell>
                <fo:table-cell text-align="right" font-size="8pt"  font-family="Arial, sans-serif" border-style="solid" border-left-width="0mm" number-columns-spanned="2" padding-right="2mm" display-align="center" >
          <fo:block text-indent="2.5mm">FACILITIES AND ADMINISTRATIVE COSTS
		</fo:block>
		</fo:table-cell>

    <fo:table-cell border-style="solid"   font-size="10pt"  font-family="Arial, sans-serif" 
		border-left-width="0.3mm" 
		border-top-width="0mm" 
		border-right-width="0mm" 
		border-bottom-width="0.6mm" 
	       padding-before="2mm"
	       number-columns-spanned="2"
	       margin-left="2mm"  >
	       
			<fo:block  text-align="right" margin-right="1.3mm">
		<xsl:value-of select="$S1X_IDC" />
	       	</fo:block >
	</fo:table-cell>
         </fo:table-row>
       </fo:table-body>
     </fo:table>

    <fo:table   table-layout="fixed"      >
           <fo:table-column column-width="44mm"/>
           <fo:table-column column-width="3mm"/>
          <fo:table-column column-width="7.5mm"/>
          <fo:table-column column-width="5.5mm"/>
		<fo:table-column column-width="18mm"/>  
          <fo:table-column column-width="60mm"/>
          <fo:table-column column-width="18mm"/>
          <fo:table-column column-width="3.5mm"/>
          <fo:table-column column-width="3.5mm"/> 
          <fo:table-column column-width="5mm"/>
          <fo:table-column column-width="21mm"/>
     <fo:table-body>
     <fo:table-row height="8mm">
   
          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"    border-style="solid" 
          		  font-size="10pt"  font-family="Arial, sans-serif"  number-columns-spanned="6"  start-indent="2mm" display-align="after" padding-bottom="0.7mm">
          <fo:block font-weight="bold">TOTAL DIRECT COSTS FOR INITIAL BUDGET PERIOD         	
		</fo:block >
		</fo:table-cell>
			
          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"    border-style="solid" 
          		  font-size="10pt"  font-family="Arial, sans-serif"   number-columns-spanned="3" display-align="after" padding-bottom="0.7mm" padding-right="8mm">
<!-- Removed per 11/24 NIH-398 revision.
          <fo:block  text-align="left" >
          	<fo:external-graphic width="14mm"  height="5mm"  src="url({$baseUrl}{$imageArrow})" />
		</fo:block >
-->
		</fo:table-cell>

    <fo:table-cell border-style="solid" text-align="right" border-right-width="0mm" border-bottom-width="0.6mm"
   			font-size="10pt" border-top-width="0mm"  border-left-width="0.6mm" display-align="after" padding-right="1mm" >
	       
	       	<fo:block start-indent="1mm" font-weight="bold">
	$
	       	</fo:block >
		</fo:table-cell>
	
	  <fo:table-cell border-style="solid" text-align="right" border-bottom-width="0.6mm"
   			font-size="10pt"  border-top-width="0mm" border-left-width="0mm" border-right-width="0.6mm" display-align="after" padding-right="1mm" >
	  	<fo:block >
				<xsl:value-of select="format-number(sum(/PROPOSAL/BUDGET/TASK_PERIODS
					/TASK_PERIOD[@PERIOD_NUMBER = '1']/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + 
					$S1_ModularAdjustment, '###,###')"/>
	    </fo:block >
		</fo:table-cell>
	       
         </fo:table-row>
         
       <fo:table-row>
		
    <fo:table-cell font-size="10pt"  font-family="Arial, sans-serif" 
	       text-align="center"   padding-before="2mm"
	       number-columns-spanned="2">
	       
	       	<fo:block start-indent="1mm">

	       	</fo:block >
	</fo:table-cell>
	
		         </fo:table-row>
      			</fo:table-body>
		    </fo:table>
		    
    <fo:table   table-layout="fixed">
      <fo:table-column column-width="2in"/>
      <fo:table-column column-width="3.5in"/>
      <fo:table-column column-width="2in"/>
    <fo:table-body>
      
        <fo:table-row keep-with-previous="3">
          <fo:table-cell border-left-width="0mm"  border-after-width="0mm" font-size="8pt"  font-family="Arial, sans-serif"  border-right-width="0mm"  >
          <fo:block>
	PHS 398 (Rev. 04/06)
	</fo:block>
		</fo:table-cell>

          <fo:table-cell font-size="8pt"  font-family="Arial, sans-serif" border-left-width="0mm"     border-right-width="0mm"   border-after-width="0mm"  >
<fo:block  text-align="center" text-indent="2mm">

Page _______

</fo:block>
</fo:table-cell>

          <fo:table-cell font-size="8pt"  font-family="Arial, sans-serif" border-left-width="0mm" border-right-width="0mm" border-after-width="0mm" text-align="right">
<fo:block><fo:inline font-weight="bold">Form Page 4</fo:inline></fo:block ></fo:table-cell>
        </fo:table-row>  
            
      </fo:table-body>  
    </fo:table>
    
    <!-- If the number of persons who are not flagged TRUE for Principal Investigator exceeds 6, extra pages are required.  The template PersonControl creates additional pages until all Persons are included in output.  PersonControl calls the template MorePeople to add people as necssary, and MorePeople calls ExtraRows if needed when all Persons have been displayed. -->
    
    
	<xsl:if test="count($PERSON_OTHER) + $PI_Count  &gt; 7" >
		<xsl:call-template name="PersonControl" >
			<xsl:with-param name="Start" select="6 - $PI_Count" />
		</xsl:call-template>
  	</xsl:if>
 	    
<!--    $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  PAGE 2  ############################	  

  -->
		      <fo:block break-before="page"/>
    <fo:table   table-layout="fixed"        >
      	<fo:table-column column-width="95mm"/>
      	<fo:table-column column-width="95mm"/>
    		<fo:table-body>
    
        <fo:table-row >
	    	<fo:table-cell>
		
			<fo:block font-size="8pt" 
			text-align="left" 
			margin-left="2cm"
			space-after="3.2mm"
			space-before="1mm"
			start-indent="4mm">
			
			Principal Investigator/Program Director (Last, first, middle)
		</fo:block>
		</fo:table-cell>

          <fo:table-cell padding-right="2mm" display-align="center">
     	  	
		       <fo:block  >
	       	
		       <xsl:if test="not(//PROJECT_DIRECTOR/@FIRST_NAME = 'To Be Named') " >
		       	<xsl:value-of select="substring(concat(//PROJECT_DIRECTOR/@LAST_NAME, ', ', //PROJECT_DIRECTOR/@FIRST_NAME ), 1, 28)" />
		       	</xsl:if>	
		       	
		       <xsl:if test="//PROJECT_DIRECTOR/@FIRST_NAME = 'To Be Named' " >
		       	To Be Named
		       	</xsl:if>	
		       	
		  	</fo:block>

	</fo:table-cell>

        </fo:table-row>
      </fo:table-body>
    </fo:table>

			<fo:block  font-size="11pt"  font-family="Arial, sans-serif"
			padding-before="4mm"
			padding-after="3.5mm"

			font-weight="bold"
			text-align="center"
			border-style="solid"
			border-right-width="0mm"
			border-left-width="0mm"
			>
			BUDGET FOR ENTIRE PROPOSED PROJECT PERIOD<fo:block></fo:block>DIRECT COSTS ONLY
		</fo:block>

    <fo:table   table-layout="fixed">
      <fo:table-column column-width="41mm"/>
      <fo:table-column column-width="32mm"/>
      <fo:table-column column-width="30mm"/>
      <fo:table-column column-width="30mm"/>
      <fo:table-column column-width="28.5mm"/>
      <fo:table-column column-width="28.5mm"/>
 
   		<fo:table-body>
				
     		<fo:table-row  height="8mm">
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif"  border-before-width="0mm" number-rows-spanned="2"  border-left-width="0mm" display-align="after">
			<fo:block start-indent="2mm" text-align="center" space-after="1mm">BUDGET CATEGORY<fo:block space-after="1mm"></fo:block>TOTALS</fo:block>
			</fo:table-cell>
  		  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif"  text-align="center" border-before-width="0mm" number-rows-spanned="2" padding-before="1.5mm">
			<fo:block >INITIAL BUDGET<fo:block></fo:block>PERIOD<fo:block padding-before="1mm" ></fo:block><fo:inline font-style="italic">(from Form Page 4)</fo:inline></fo:block>
			</fo:table-cell>

			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="8pt"  font-family="Arial, sans-serif"  number-columns-spanned="4" display-align="center">
			<fo:block text-align="center" space-before="1mm">ADDITIONAL YEARS OF SUPPORT REQUESTED</fo:block>
			</fo:table-cell>

		</fo:table-row > 
      
 		<fo:table-row  height="4mm" >
	
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="8pt"  font-family="Arial, sans-serif" text-align="center" display-align="center">
			<fo:block>2nd</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="8pt"  font-family="Arial, sans-serif"  text-align="center" display-align="center">
			<fo:block>3rd</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="8pt"  font-family="Arial, sans-serif"   text-align="center" display-align="center">
			<fo:block>4th</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="8pt"  font-family="Arial, sans-serif"   text-align="center" display-align="center">
			<fo:block>5th</fo:block>
			</fo:table-cell>
		</fo:table-row > 
      
		</fo:table-body>
	</fo:table>

    <fo:table   table-layout="fixed">
      <fo:table-column column-width="41mm"/>
      <fo:table-column column-width="32mm"/>
      <fo:table-column column-width="30mm"/>
      <fo:table-column column-width="30mm"/>
      <fo:table-column column-width="28.5mm"/>
      <fo:table-column column-width="28.5mm"/>
 
   		<fo:table-body>
		
     		<fo:table-row text-align="right" margin-right="3mm" height="9mm">
     		
			<fo:table-cell  border-style="solid" border-bottom="0mm"    font-size="7pt"  font-family="Arial, sans-serif"  border-left-width="0mm">


			<fo:block padding-before="3.5mm" text-align="left">PERSONNEL<fo:inline font-style="italic"> Salary and<fo:block></fo:block>fringe benefits. Applicant<fo:block></fo:block>organization only.</fo:inline></fo:block>
			</fo:table-cell>
  		  		
		 <fo:table-cell border-style="solid" text-align="right"  border-bottom="0mm" border-left="0.3mm"
   			font-size="10pt"  display-align="after" >
   			
			<fo:block>
<!--  When the sum is 0, produce no output.  Otherwise, format with commas.  -->
			
			<xsl:choose>
				<xsl:when test="$S1_Fringe + $S1_Salary = 0">
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="format-number($S1_Fringe + $S1_Salary, '###,###') " />
				</xsl:otherwise>
			</xsl:choose>
	
			</fo:block>
			</fo:table-cell>
	  		
			 <fo:table-cell border-style="solid" text-align="right" border-right-width="0mm" border-bottom-width="0mm"
   			font-size="10pt"    display-align="after"  >
						
			<xsl:choose>
				<xsl:when test="$S2_Fringe + $S2_Salary = 0">
				</xsl:when>
				<xsl:otherwise>
					<fo:block>
						<xsl:value-of select="format-number($S2_Fringe + $S2_Salary, '###,###') " />
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>
			
			</fo:table-cell>
	  		
		
			 <fo:table-cell border-style="solid" text-align="right"  border-bottom-width="0mm"
   			font-size="10pt"    display-align="after"  >
							
			<xsl:choose>
				<xsl:when test="$S3_Fringe + $S3_Salary = 0">
				</xsl:when>
				<xsl:otherwise>
					<fo:block>
						<xsl:value-of select="format-number($S3_Fringe + $S3_Salary, '###,###') " />
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>
			
			</fo:table-cell>
	
			 <fo:table-cell border-style="solid" text-align="right" border-right-width="0mm" border-bottom-width="0mm"  border-left-width="0mm"
   			font-size="10pt"    display-align="after"  >
			
			<xsl:choose>
				<xsl:when test="$S4_Fringe + $S4_Salary = 0">
				</xsl:when>
				<xsl:otherwise>
					<fo:block>
						<xsl:value-of select="format-number($S4_Fringe + $S4_Salary, '###,###') " />
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>

			</fo:table-cell>
		
			 <fo:table-cell border-style="solid" text-align="right" border-right-width="0mm"   border-bottom-width="0mm"
   			font-size="10pt"    display-align="after"  >

			<xsl:choose>
				<xsl:when test="$S5_Fringe + $S5_Salary = 0">
				</xsl:when>
				<xsl:otherwise>
					<fo:block>
						<xsl:value-of select="format-number($S5_Fringe + $S5_Salary, '###,###') " />
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>
			
			</fo:table-cell>
		</fo:table-row > 
      
		</fo:table-body>
	</fo:table>
       
       <!--  The following table, along with the cells and rows brought in by the template Data, fills in most of the substance of Page 5.  This is the Personnel and Non-Personnel data for Periods 1 through 5.  -->
       
    <fo:table   table-layout="fixed"       >
      <fo:table-column column-width="19.9mm"/>
      <fo:table-column column-width="4mm"/>
      <fo:table-column column-width="17mm"/>
      <fo:table-column column-width="32mm"/>
      <fo:table-column column-width="30mm"/>     
      <fo:table-column column-width="30mm"/>
      <fo:table-column column-width="28.5mm"/>
      <fo:table-column column-width="28.5mm"/>
 
     <fo:table-body>
   
   <xsl:call-template name="Data" />  		
     	
      </fo:table-body>
    </fo:table>

    <fo:table   table-layout="fixed">
      <fo:table-column column-width="41.5mm"/>
      <fo:table-column column-width="30mm"/>
      <fo:table-column column-width="28.5mm"/>
      <fo:table-column column-width="30mm"/>
      <fo:table-column column-width="30mm"/>
      <fo:table-column column-width="10mm"/>
      <fo:table-column column-width="20mm"/>
 
   		<fo:table-body>
   		<fo:table-row height="12mm">

			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif"  number-columns-spanned="5"  border-left-width="0mm" display-align="center" padding-top="1mm">
			
				<fo:block font-weight="bold" font-size="9pt">
			
			TOTAL DIRECT COSTS FOR ENTIRE PROPOSED PROJECT PERIOD  
			
<!-- Removed per 11/24/2004 NIH-398 revisions.
		<fo:inline font-size="8pt" font-weight="100" font-style="italic" >
          	 (Item 8a, Face Page)
         	</fo:inline>
                <fo:external-graphic padding-before="4mm" width="24mm"  height="3mm"  src="url({$baseUrl}{$imageLine})" />
-->
				</fo:block>
			</fo:table-cell>
  		  
			<fo:table-cell  border-style="solid"  border-width="0.6mm" border-right-width="0mm" font-size="10pt"  font-family="Arial, sans-serif"  font-weight="bold" display-align="center" padding-left="1.2mm" padding-top="1mm">
			<fo:block>
	$		
			</fo:block>
			</fo:table-cell>

				<fo:table-cell  border-style="solid"  border-width="0.6mm" border-left-width="0mm" font-size="10pt"  font-family="Arial, sans-serif"   display-align="center" text-align="right" padding-right="1mm" padding-top="1mm">
			<fo:block>

			<xsl:value-of select="format-number($S1_TotalDirect +  $S2_TotalDirect + $S3_TotalDirect + $S4_TotalDirect + $S5_TotalDirect, '###,###')" />
			</fo:block>
			</fo:table-cell>
		
			  
			<fo:table-cell  border-style="solid"  border-width="0.6mm" border-top-width="0.4mm"  font-size="10pt"  font-family="Arial, sans-serif" font-weight="bold" display-align="after">
			<fo:block>

			</fo:block>
			</fo:table-cell>
			
		</fo:table-row > 
 			
   			<fo:table-row>
   				<fo:table-cell height="83mm" padding-before="0.5mm" number-columns-spanned="6" border-style="solid" border-after-width="0mm" border-right-width="0mm" border-left-width="0mm">
   					
   					
   					<fo:block height="12mm"  font-size="10pt"  font-family="Arial, sans-serif">JUSTIFICATION.  Follow the budget justification instructions exactly.  Use continuation pages as needed.</fo:block>
   					
   				</fo:table-cell>
   			</fo:table-row>
     
		</fo:table-body>
	</fo:table>
  
    <fo:table   table-layout="fixed"       space-before="1.5mm">
      <fo:table-column column-width="2in"/>
      <fo:table-column column-width="3.5in"/>
      <fo:table-column column-width="2in"/>
    <fo:table-body>
    
        <fo:table-row>
          <fo:table-cell border-left-width="0mm"  border-after-width="0mm" border-style="solid"   font-size="8pt"  font-family="Arial, sans-serif"  border-right-width="0mm"  >
          <fo:block space-before="2mm">
	PHS 398 (Rev. 04/06)
	</fo:block>
		</fo:table-cell>

          <fo:table-cell border-style="solid"   font-size="8pt"  font-family="Arial, sans-serif" border-left-width="0mm"     border-right-width="0mm"   border-after-width="0mm"  ><fo:block start-indent="1mm"  space-before="2mm" text-align="center">Page _______</fo:block >

</fo:table-cell>

          <fo:table-cell border-style="solid"  font-size="8pt"  font-family="Arial, sans-serif" border-left-width="0mm" border-right-width="0mm" border-after-width="0mm" text-align="right">
<fo:block  space-before="2mm"><fo:inline font-weight="bold">Form Page 5</fo:inline></fo:block ></fo:table-cell>
        </fo:table-row>  
        
      </fo:table-body>  
    </fo:table>
    
       	</fo:flow>
</fo:page-sequence>
   	
      </fo:root>
  </xsl:template>
  
  
  <!--  PersonControl is called when there is not enough room for all Persons involved in Period 1.  This recursive template increments by 7 since there is room for 7 Persons on each overflow sheet.  On each recursion, MorePeople is called with the freshly incremented $Start parameter.  If count($PERSON_OTHER) - $Start &gt; 7 returns "false," the template exits.  MorePeople finishes displaying Persons, and then displays the appropriate number of extra rows by calling ExtraRows.   -->
  
   
  <xsl:template name="PersonControl" >
  	<xsl:param name="Start" />
  	
			<xsl:call-template name="MorePeople" >
				<xsl:with-param name="Start"  select="$Start" />
				<xsl:with-param name="End" select="$Start + 8" />
			</xsl:call-template>
			
			<xsl:if test="count($PERSON_OTHER) - $Start &gt; 7" >
				<xsl:call-template name="PersonControl" >
					<xsl:with-param name="Start" select="$Start + 7" />
				</xsl:call-template>
 	
			</xsl:if>

  </xsl:template>
  
  <!--  def @@@@@@@@@@PersonControl@@@@@@@@ MORE PEOPLE @@@ more_people @@  -->
   
    <!--   @@@@@@@@@@@@@ MorePeople @@@@@@@@@@@@@   -->
    
		<xsl:template name="MorePeople" >
	
		<xsl:param name="Start" />
		<xsl:param name="End" />
		<xsl:param name="n" />
				      <fo:block break-before="page"/>
    <fo:table   table-layout="fixed">
      	<fo:table-column column-width="95mm"/>
      	<fo:table-column column-width="95mm"/>
    		<fo:table-body>
    
        <fo:table-row >
        
        
          <fo:table-cell>
		
			<fo:block font-size="8pt" 
			text-align="left" 
			margin-left="2cm"
			space-after="3.2mm"
			space-before="1mm"
			start-indent="4mm">
			
			Principal Investigator/Program Director (Last, first, middle)
		</fo:block>
		</fo:table-cell>

          <fo:table-cell padding-right="2mm" display-align="center">
          	<fo:block>
     		       	
		       <xsl:if test="not(//PROJECT_DIRECTOR/@FIRST_NAME = 'To Be Named') " >
		       	<xsl:value-of select="substring(concat(//PROJECT_DIRECTOR/@LAST_NAME, ', ', //PROJECT_DIRECTOR/@FIRST_NAME ), 1, 28)" />
		       	</xsl:if>	
		       	
		       <xsl:if test="//PROJECT_DIRECTOR/@FIRST_NAME = 'To Be Named' " >
		       	To Be Named
		       	</xsl:if>	
		  	</fo:block>
</fo:table-cell>

        </fo:table-row>
      </fo:table-body>
    </fo:table>
    
    <fo:table   table-layout="fixed"      >
      <fo:table-column column-width="127mm"/>
      <fo:table-column column-width="32mm"/>
      <fo:table-column column-width="31mm"/>
    <fo:table-body>
    
        <fo:table-row height="11mm" keep-with-previous="4">
        
          <fo:table-cell border-left-width="0mm" border-style="solid" text-align="center"  font-size="11pt"  font-family="Arial, sans-serif" padding-before.length="1.5mm">
          <fo:block font-weight="bold">
	DETAILED BUDGET FOR INITIAL BUDGET PERIOD <fo:block />DIRECT COSTS ONLY
		</fo:block>
		</fo:table-cell>

          <fo:table-cell border-style="solid"   font-size="8pt"  font-family="Arial, sans-serif" border-left-width="0mm" >

 <fo:block start-indent="1mm"  padding-before.length="1mm">FROM
<fo:block padding-top="1.4mm"></fo:block>
<fo:inline font-size="10pt" ><xsl:value-of select="/PROPOSAL/BUDGET/PERIODS/PERIOD[PERIOD_NUMBER='1']/START_DATE" /></fo:inline>
</fo:block >

</fo:table-cell>
          <fo:table-cell border-style="solid"  font-size="8pt"  font-family="Arial, sans-serif" border-left-width="0mm" border-right-width="0mm" start-indent="1mm">

<fo:block padding-before.length="1mm">
          THROUGH
<fo:block padding-top="1.4mm"></fo:block>
<fo:inline font-size="10pt" ><xsl:value-of select="/PROPOSAL/BUDGET/PERIODS/PERIOD[PERIOD_NUMBER='1']/STOP_DATE" /></fo:inline>
</fo:block >

</fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
	
    <fo:table   table-layout="fixed"       >
      <fo:table-column column-width="49mm"/>
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="19mm"/>
      <fo:table-column column-width="20mm"/>
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="22mm"/>
 
      <fo:table-body>
   
      <fo:table-row>   

     	 	<fo:table-cell border-style="solid" text-align="left"  font-size="8pt"  font-family="Arial, sans-serif"
      			border-left-width="0mm" padding-before="1mm" number-columns-spanned="2">	
      			
	         	<fo:block start-indent="2mm" >
PERSONNEL <fo:inline font-style="italic">(Applicant organization only)</fo:inline>
			</fo:block>
		</fo:table-cell>
	
      		<fo:table-cell border-style="solid" text-align="left" font-size="8pt"  font-family="Arial, sans-serif"
	      		border-top-width="0mm" border-left-width="0mm" padding-before="1mm" number-columns-spanned="3">	
	      		
	      		<fo:block start-indent="1mm" >
	      			Months Devoted to Project
	      		</fo:block>
	      	</fo:table-cell>
            
		<fo:table-cell border-style="solid" text-align="center" font-size="8pt" 
				 border-left-width="0mm" padding-before="1mm" number-rows-spanned="2">
	         	<fo:block padding-before="4mm">

	         		INST. BASE<fo:block></fo:block>SALARY
			</fo:block>
		</fo:table-cell>         

		<fo:table-cell border-style="solid" text-align="left" font-size="8pt"
				 border-left-width="0mm" border-right-width="0mm"  number-columns-spanned="3" padding-before="1mm" >
		<fo:block start-indent="2mm" text-indent="2mm">
	         	 DOLLAR AMOUNT REQUESTED<fo:inline font-style="italic"> (omit cents)</fo:inline>
			</fo:block >
		</fo:table-cell>
   
        </fo:table-row>

    <fo:table-row font-size="8pt">   
     	 	<fo:table-cell border-style="solid" text-align="center"  font-size="8pt"  font-family="Arial, sans-serif" height="4mm" 
      		border-top-width="0mm"     border-left-width="0mm" >
	         	<fo:block  padding-before="4.5mm"   >

NAME
			</fo:block>
		</fo:table-cell>
		
   		<fo:table-cell border-style="solid" text-align="center" 
   			  font-family="Arial, sans-serif"  border-top-width="0mm" border-left-width="0mm">
	         	<fo:block padding-before="3mm">
ROLE ON <fo:block></fo:block>PROJECT
			</fo:block>
		</fo:table-cell>

	    	<fo:table-cell border-style="solid" text-align="center" 
	    		height="5.5mm" border-top-width="0mm" border-left-width="0mm">
	    		<fo:block padding-before="3mm">
	    			Cal.<fo:block></fo:block>Mnths
	    		</fo:block>
	    	</fo:table-cell>
	    	
	    	<fo:table-cell border-style="solid" text-align="center" 
	    		height="5.5mm" border-top-width="0mm" border-left-width="0mm">
	    		<fo:block padding-before="3mm">
	    			Acad.<fo:block></fo:block>Mnths
	    		</fo:block>
	    	</fo:table-cell>
	    	
	    	<fo:table-cell border-style="solid" text-align="center" 
	    		height="5.5mm" border-top-width="0mm" border-left-width="0mm">
	    		<fo:block padding-before="3mm">
	    			Sum.<fo:block></fo:block>Mnths
	    		</fo:block>
	    	</fo:table-cell>
    	
		<fo:table-cell border-style="solid" text-align="center"    font-family="Arial, sans-serif"  
				border-top-width="0mm" border-left-width="0mm" >
	         	<fo:block padding-before="2mm">

SALARY REQUESTED
			</fo:block>
		</fo:table-cell>         

		<fo:table-cell border-style="solid" text-align="center"    font-family="Arial, sans-serif"  
				border-top-width="0mm" border-left-width="0mm"  >
	         	<fo:block padding-before="2mm">
 FRINGE BENEFITS
			</fo:block  >
		</fo:table-cell>
   
		<fo:table-cell border-style="solid" text-align="center"    font-family="Arial, sans-serif"  
				border-top-width="0mm" border-left-width="0mm" border-right-width="0mm" padding-before="2mm">
	         	<fo:block padding-before="3mm">
TOTAL
			</fo:block>
		</fo:table-cell>
          
        </fo:table-row>
   
      </fo:table-body>
    </fo:table>
	
    <fo:table   table-layout="fixed"       >
      <fo:table-column column-width="49mm"/>
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="12mm"/>
      <fo:table-column column-width="19mm"/>
      <fo:table-column column-width="20mm"/>
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="22mm"/>
 
     <fo:table-body>
      	<!--  ddddddd  -->
	
<!-- $Start and $End are the Person positions provided when more_people was called  -->

 <xsl:call-template name="People" >
 	<xsl:with-param name="End"  select="$End" />
 	<xsl:with-param name="Start"  select="$Start" />
 	<xsl:with-param name="PI" select="$PERSON_OTHER" />
 </xsl:call-template>
 
 <!--  After all Persons are included in output, any blank rows necessary to complete the form are added.  -->
 
 
<xsl:if test="$End - count($PERSON_OTHER) &gt; 1 " >
	<xsl:call-template name="ExtraRows" >
		<xsl:with-param name="n" select="$End - count($PERSON_OTHER) -2" />
	</xsl:call-template>
</xsl:if>

 
  </fo:table-body >
  </fo:table>
  
  <fo:table   table-layout="fixed"      >
      <fo:table-column column-width="78mm"/>
         <fo:table-column column-width="45mm"/>
           <fo:table-column column-width="23mm"/>
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="22mm"/>
 
    <fo:table-body>
  
      <fo:table-row height="8.5mm">
     	<fo:table-cell border-bottom-style="solid"  display-align="after" font-size="10pt" padding-right="3mm">

		<fo:block start-indent="1mm" text-align="right" font-weight="bold" >
				SUBTOTALS         
		</fo:block >
	</fo:table-cell>
    
     <fo:table-cell border-style="solid"   border-top-width="0mm" border-left-width="0mm" display-align="after" >

		<fo:block  text-align="left" >
			<fo:external-graphic width="36mm"  height="3mm"  src="url({$baseUrl}{$imageArrow})" />
		</fo:block >
	</fo:table-cell>


      	<fo:table-cell border-style="solid" text-align="right"  border-right="0.6mm" border-bottom="0.6mm" border-top="0.6mm" border-left="0.6mm"
   			font-size="10pt"   display-align="after" padding-right="1mm" >

		<fo:block start-indent="1mm">
	
<xsl:variable name="SalaryComma" >	
	<xsl:call-template name="SubtotalSalary" >
		<xsl:with-param name="Start" select="$Start" />
		<xsl:with-param name="total" select="0" />
	</xsl:call-template>	
</xsl:variable>	

<xsl:value-of select="format-number($SalaryComma, '###,###')" />

<!--		
		<xsl:apply-templates select="$PERSON_OTHER" >
			<xsl:with-param name="k" select="$Start + 2" />
			<xsl:with-param name="total" select="0" />
			<xsl:with-param name="j" select="$Start + 9" />
		</xsl:apply-templates>
-->

		</fo:block ></fo:table-cell>
		
      	<fo:table-cell border-style="solid" border-bottom-width="0.6mm" border-right-width="0mm" border-left-width="0mm" border-top="0.6mm"    text-align="right" 
   			font-size="10pt"  display-align="after" padding-right="1mm" >

		<fo:block start-indent="1mm"  >
		
<xsl:variable name="FringeComma" >
	<xsl:call-template name="SubtotalFringe" >
		<xsl:with-param name="Start" select="$Start" />
		<xsl:with-param name="total" select="0" />
	</xsl:call-template>	
</xsl:variable>

<xsl:value-of select="format-number($FringeComma, '###,###')" />


          </fo:block ></fo:table-cell>
  
            <fo:table-cell border-style="solid" text-align="right"
            	font-size="10pt"  border-top-width="0.6mm" border-bottom-width="0.6mm"  border-right-width="0.6mm" display-align="after" border-left-width="0.6mm" padding-right="1mm" >

<fo:block>

<xsl:variable name="TotalComma" >
	<xsl:call-template name="SubtotalTotal" >
		<xsl:with-param name="Start" select="$Start" />
		<xsl:with-param name="total" select="0" />
	</xsl:call-template>	
</xsl:variable>	

<xsl:value-of select="format-number($TotalComma, '###,###')" />

</fo:block >


</fo:table-cell>

        </fo:table-row>
      </fo:table-body>
    </fo:table>
 
   	
<!--  %%%%%%%%%%%%%   ANOTHER TABLE  %%%%%%%%%%   -->           
  
    <fo:table   table-layout="fixed"   >
          <fo:table-column column-width="44.5mm"/>
          <fo:table-column column-width="95mm"/>
          <fo:table-column column-width="23.5mm"/>
           <fo:table-column column-width="5mm"/>
          <fo:table-column column-width="22mm"/> 
             
     <fo:table-body>
 
        <fo:table-row height="11.5mm">

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"    border-style="solid" 
          		  font-size="8pt"  font-family="Arial, sans-serif"  number-columns-spanned="4" padding-before="1.8mm" start-indent="2mm" >
            <fo:block >CONSULTANT COSTS
		</fo:block>
		</fo:table-cell>
		
    <fo:table-cell border-style="solid"   font-size="10pt"  font-family="Arial, sans-serif" 
		border-left-width="0.3mm" 
		border-top-width="0mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm" 
	       text-align="center"   padding-before="2mm" >
	       
	       	<fo:block start-indent="1mm">
	       	
	       	</fo:block >
	</fo:table-cell>
	   
         </fo:table-row>
         
         <!--   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   
         
         These sections output the form with no data for non-personnel items.  These are the for the Person Overflow sheets. 
         
         -->
     
        <fo:table-row height="15mm" >

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"  border-style="solid" 
          		  font-size="8pt"  font-family="Arial, sans-serif"  padding-before="0.4mm"  number-columns-spanned="4" start-indent="2mm">
          <fo:block >EQUIPMENT<fo:inline font-style="italic"> (Itemize)</fo:inline>
		</fo:block>
		</fo:table-cell>
		
		<fo:table-cell border-style="solid" border-right-width="0mm"  border-top-width="0mm" >
			<fo:block></fo:block>
		</fo:table-cell>
		
</fo:table-row>	  
         
         <!--   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   -->
 
        <fo:table-row height="29mm" >

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"  border-style="solid" 
          		  font-size="8pt"  font-family="Arial, sans-serif"  padding-before="0.4mm"  number-columns-spanned="4" start-indent="2mm">
          <fo:block >SUPPLIES<fo:inline font-style="italic"> (Itemize by category)</fo:inline>
		</fo:block>
		</fo:table-cell>
		
		<fo:table-cell border-style="solid" border-right-width="0mm" border-top-width="0mm">
				<fo:block></fo:block>
		</fo:table-cell>
		
</fo:table-row>	  
         
         <!--   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   -->
     
       <fo:table-row height="10mm">

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"    border-style="solid" 
          		  font-size="8pt"  font-family="Arial, sans-serif"  padding-before="1.8mm"   number-columns-spanned="4" start-indent="2mm">
          <fo:block >TRAVEL
		</fo:block>
		</fo:table-cell>
	
	   <fo:table-cell border-left-width="0.3mm"  border-top-width="0mm"  border-right-width="0mm"   border-style="solid" 
          		  font-size="10pt"  font-family="Arial, sans-serif"  padding-before="1.8mm"   number-columns-spanned="3" text-align="center">
          <fo:block >
  
		</fo:block>
		</fo:table-cell>
	
         </fo:table-row>
    
             
         <!--   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   -->
 
     
       <fo:table-row>

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"    border-style="solid" 
          		  font-size="8pt"  font-family="Arial, sans-serif"  padding-before="0.4mm" number-columns-spanned="0" number-rows-spanned="2" height="10.5mm" start-indent="2mm">
          <fo:block >PATIENT CARE COSTS
		</fo:block>
		</fo:table-cell>
		
    <fo:table-cell border-style="solid"   font-size="8pt"  font-family="Arial, sans-serif" 
		border-left-width="0.3mm" 
		border-top-width="0mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm" 
	       text-align="left"   
	       padding-before="0.5mm"
	       	       padding-after="1.5mm"
	       number-columns-spanned="3" >
	       
	       	<fo:block start-indent="1mm">
	       		INPATIENT
	       	</fo:block >
	</fo:table-cell>
	       
	 <fo:table-cell border-style="solid"   font-size="10pt"  font-family="Arial, sans-serif" border-left-width="0.3mm"  padding-before="2mm"
       	border-top-width="0mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm" 
		number-columns-spanned="0">
		
			<fo:block start-indent="1mm" text-align="center" >

			</fo:block >
	</fo:table-cell>
	
         </fo:table-row>

       <fo:table-row>
	
    <fo:table-cell border-style="solid"   font-size="8pt"  font-family="Arial, sans-serif" 
		border-left-width="0.3mm" 
		border-top-width="0mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm" 
	       text-align="left"   
	       padding-before="0.5mm"

	       number-columns-spanned="3" >
  	          		 
          <fo:block start-indent="1mm">OUTPATIENT
		</fo:block>
		</fo:table-cell>
		
    <fo:table-cell border-style="solid"   font-size="10pt"  font-family="Arial, sans-serif" 
		border-left-width="0.3mm" 
		border-top-width="0mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm" 
	       text-align="center"   padding-before="2mm"
	       number-columns-spanned="2">
	       
	       	<fo:block start-indent="1mm">
	       
	       	</fo:block >
	</fo:table-cell>
	       
         </fo:table-row>
                
         <!--   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   -->
 
 
       <fo:table-row height="10mm">

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"    border-style="solid" 
          		  font-size="8pt"  font-family="Arial, sans-serif"  padding-before="0.4mm" number-columns-spanned="4" start-indent="2mm">
          <fo:block >ALTERATIONS AND RENOVATIONS<fo:inline font-style="italic"> (Itemize by category)</fo:inline>
		</fo:block>
		</fo:table-cell>
		
    <fo:table-cell border-style="solid"   font-size="10pt"  font-family="Arial, sans-serif" 
		border-left-width="0.3mm" 
		border-top-width="0mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm" 
	       text-align="center"   padding-before="2mm">	
	       
	       	<fo:block start-indent="1mm">
	       	
	       	</fo:block >
	</fo:table-cell>
	
         </fo:table-row>
         
         <!--   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   -->    

       <fo:table-row height="23mm">

          <fo:table-cell 
          		  font-size="8pt"  font-family="Arial, sans-serif"  padding-before="1.8mm"   number-columns-spanned="4" start-indent="2mm">
          <fo:block >OTHER EXPENSES<fo:inline font-style="italic"> (Itemize by category)</fo:inline>
		</fo:block>
		</fo:table-cell>
	
	   <fo:table-cell border-left-width="0.3mm"  border-top-width="0mm" border-bottom-width="0mm"  border-right-width="0mm"   border-style="solid" 
          		  font-size="10pt"  font-family="Arial, sans-serif"  padding-before="1.8mm"   number-columns-spanned="3" text-align="center">
          <fo:block >
  
		</fo:block>
		</fo:table-cell>
	
         </fo:table-row>

</fo:table-body>
</fo:table>

<!--   ################   ANOTHER TABLE  #############   -->      
              
    <fo:table   table-layout="fixed"      >
          <fo:table-column column-width="99.5mm"/>
          <fo:table-column column-width="40mm"/>
          <fo:table-column column-width="23.5mm"/>
           <fo:table-column column-width="5mm"/>
          <fo:table-column column-width="22mm"/> 
             
     <fo:table-body>
          
     <fo:table-row height="6mm">
     
						<fo:table-cell  font-size="8pt"  font-family="Arial, sans-serif" border-style="solid" display-align="center" padding-left="2mm" border-left-width="0mm" border-bottom-width="0mm">
							<fo:block>
							CONSORTIUM/CONTRACTURAL COSTS
							</fo:block>
						</fo:table-cell>
						
						<fo:table-cell text-align="right" font-size="8pt"  font-family="Arial, sans-serif" border-style="solid" number-columns-spanned="2" border-bottom-width="0mm" display-align="center" padding-right="2mm">
							<fo:block>
							DIRECT COSTS
							</fo:block>
						</fo:table-cell>
			
						<fo:table-cell border-style="solid" font-size="8pt" number-columns-spanned="2" font-family="Arial, sans-serif" border-top-width="0.3mm" border-right-width="0mm">
							<fo:block>
							
							</fo:block>
						</fo:table-cell>
					
					</fo:table-row>
  
     <fo:table-row height="9mm">
   
          <fo:table-cell border-left-width="0mm"  border-top-width="0.3mm"  border-right-width="0mm"    border-style="solid" 
          		  font-size="10pt"  font-family="Arial, sans-serif"  number-columns-spanned="3"  start-indent="2mm" display-align="after" padding-bottom="1mm">
          <fo:block font-weight="bold">SUBTOTAL DIRECT COSTS FOR INITIAL BUDGET PERIOD
          <fo:inline font-size="8pt" font-weight="100" font-style="italic" >
          	 (Item 7a, Face Page)
         	</fo:inline>
		</fo:block >
		</fo:table-cell>
	
    <fo:table-cell border-style="solid" text-align="right" border-right-width="0mm" border-bottom-width="0.6mm"
   			font-size="10pt" border-top-width="0.6mm"  border-left-width="0.6mm" display-align="after" padding-right="1mm" >
	       
	       	<fo:block start-indent="1mm" font-weight="bold">
	$
	       	</fo:block >
	</fo:table-cell>
	
	   <fo:table-cell border-style="solid" text-align="right" border-bottom-width="0.6mm"
   			font-size="10pt"  border-top-width="0.6mm" border-left-width="0mm" border-right-width="0.6mm" display-align="after" padding-right="1mm" >
	
	</fo:table-cell>
	       
         </fo:table-row>

     <fo:table-row height="6mm">
     
						<fo:table-cell  font-size="8pt"  font-family="Arial, sans-serif" border-style="solid" border-left-width="0mm" padding-left="2mm" border-top-width="0mm" display-align="center" >
							<fo:block>
							CONSORTIUM/CONTRACTURAL COSTS
							</fo:block>
						</fo:table-cell>
	
						<fo:table-cell text-align="right" font-size="8pt"  font-family="Arial, sans-serif" border-style="solid" border-left-width="0mm" number-columns-spanned="2" padding-right="2mm" display-align="center" >
							<fo:block>
							FACILITIES AND ADMINISTRATIVE COSTS
							</fo:block>
						</fo:table-cell>
					
						<fo:table-cell  font-size="8pt"  font-family="Arial, sans-serif"  border-style="solid" border-left-width="0mm" border-right-width="0mm" number-columns-spanned="2">
							<fo:block>
							
							</fo:block>
						</fo:table-cell>
						
				</fo:table-row>
     
</fo:table-body>
</fo:table>

<!--    #######    ANOTHER TABLE   ############    -->     
               
    <fo:table   table-layout="fixed"      >
          <fo:table-column column-width="44.5mm"/>
          <fo:table-column column-width="95mm"/>
          <fo:table-column column-width="23.5mm"/>
           <fo:table-column column-width="5mm"/>
          <fo:table-column column-width="22mm"/> 
             
     <fo:table-body>
      
       <fo:table-row height="8mm">

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"  border-bottom-width="0.6mm"  border-style="solid" 
          		  font-size="10pt"  font-family="Arial, sans-serif"  padding-before="3mm" number-columns-spanned="3" start-indent="2mm">
          <fo:block font-weight="bold"  font-size="10pt"  font-family="Arial, sans-serif">TOTAL DIRECT COSTS FOR INITIAL BUDGET PERIOD
		</fo:block>
		</fo:table-cell>
	
    <fo:table-cell border-style="solid" text-align="left" border-right-width="0.6mm" border-bottom-width="0.6mm"
   			font-size="10pt" border-top-width="0.6mm"  border-left-width="0.6mm" display-align="after"  number-columns-spanned="2" >
 	       
	       	<fo:block start-indent="0.8mm" font-weight="bold">
	       		$
	       	</fo:block >
	</fo:table-cell>
	
         </fo:table-row>
          
       <fo:table-row>
		
    <fo:table-cell font-size="10pt"  font-family="Arial, sans-serif" 
	       text-align="center"   padding-before="2mm"
	       number-columns-spanned="2">
	       
	       	<fo:block start-indent="1mm">

	       	</fo:block >
	</fo:table-cell>
	
		         </fo:table-row>
      			</fo:table-body>
		    </fo:table>
		    
    <fo:table   table-layout="fixed">
      <fo:table-column column-width="2in"/>
      <fo:table-column column-width="3.5in"/>
      <fo:table-column column-width="2in"/>
    <fo:table-body>
    
    <fo:table-row height="0.5mm">
					<fo:table-cell>
						<fo:block></fo:block>
					</fo:table-cell>
				</fo:table-row>
        <fo:table-row >
        
          <fo:table-cell border-left-width="0mm"  border-after-width="0mm" font-size="8pt"  font-family="Arial, sans-serif"  border-right-width="0mm"  >
          <fo:block>
	PHS 398 (Rev. 04/06)
	</fo:block>
		</fo:table-cell>

          <fo:table-cell  font-size="8pt"  font-family="Arial, sans-serif" border-left-width="0mm"     border-right-width="0mm"   border-after-width="0mm"  ><fo:block start-indent="1mm"  text-align="center">Page _______</fo:block >

</fo:table-cell>

          <fo:table-cell font-size="8pt"  font-family="Arial, sans-serif" border-left-width="0mm" border-right-width="0mm" border-after-width="0mm" text-align="right">
<fo:block><fo:inline font-weight="bold">Form Page 4</fo:inline></fo:block ></fo:table-cell>
        </fo:table-row>  
        
          </fo:table-body>  
    </fo:table>
  
</xsl:template>
   	<!--  abc ^^^^  People Template   ^^^^  -->   	
  
    <xsl:template name="People" >
		<xsl:param name="Start" />
		<xsl:param name="End" />
		<xsl:param name="PI" />
	
	<xsl:for-each select="$PI" >
		<xsl:sort select="@SEQUENCE_NUMBER" />
		 
<xsl:variable name="SUM_SALARY" select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY) " />


<xsl:variable name="SUM_FRINGE" select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT) " />


<xsl:variable name="SUM_TOTAL" select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY) + sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT) " />

	
 	<xsl:if test="position() &lt; $End and position() &gt; $Start"> 
 
      <fo:table-row height="7.9mm" >   
      
   	
     	 	<fo:table-cell border-style="solid" text-align="left"   
      			border-top-width="0mm" border-left-width="0mm"   font-size="10pt" display-align="after" >
	     	    	<fo:block padding-left="2mm">
	     	    	
	     	    	 <xsl:value-of select="NAME" /> 
				</fo:block>
			</fo:table-cell>
		
   		<fo:table-cell border-style="solid" text-align="left" padding-left="1.5mm" 
   			font-size="10pt"  border-top-width="0mm" border-left-width="0mm" display-align="after"  >
    			
	         	<fo:block >
	
	<!--  THE FOLLOWING CODE PLACES THE PRINCIPAL INVESTIGATOR'S ROLE IN THE FIRST ROW OF THE FORM 			WHEN PARAMETER PI IS SET TO $PERSON_PI -->
		
		<xsl:if test="@PROJECT_DIRECTOR = 'TRUE' and position() = '1' " >
			<fo:block font-size="10" padding-top="0.8mm" padding-bottom="0.2mm" line-height="12.2pt" display-align="center">Principal  <fo:block ></fo:block>  Investigator</fo:block>
		</xsl:if>
		
	<xsl:if test="@PROJECT_DIRECTOR = 'TRUE' and position() &gt; '1' " >
			<fo:block font-size="10"  display-align="center">Principal  <fo:block ></fo:block>  Investigator</fo:block>
		</xsl:if>
		
		<xsl:if test="@PROJECT_DIRECTOR = 'FALSE' " >	
<xsl:if test="ROLE = 'Co-Investigator' " >
Co-<fo:block></fo:block>Investigator
</xsl:if>

<xsl:if test="not(ROLE = 'Co-Investigator')and $PI = $PERSON_OTHER" >

		<xsl:value-of select="substring(substring-before(ROLE, ' '), 1, 11)" />
		<fo:block></fo:block>
		<xsl:value-of select="substring(substring-after(ROLE, ' '), 1, 11)" />


	<xsl:if test="not(substring-before(ROLE, ' ')) " >
		<xsl:value-of select="substring(ROLE, 1, 11)" /> <fo:block></fo:block><xsl:value-of select="substring(ROLE, 12, 11)" />
	</xsl:if>


	</xsl:if>
		
	</xsl:if>

			</fo:block>
		</fo:table-cell>

      	<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
      		border-top-width="0mm" border-left-width="0mm" display-align="after" padding-right="1mm">
      		<fo:block >
      			<xsl:choose>
      				<xsl:when test="not(APPOINTMENT/@APPOINTMENT_CODE  = 'A2') and not(APPOINTMENT/@APPOINTMENT_CODE  = 'AS')">
      					<xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@CALENDAR_MONTHS)" />
      				</xsl:when>
      				<xsl:otherwise></xsl:otherwise>
      			</xsl:choose>
      		</fo:block>
      	</fo:table-cell>     

      	<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
      		border-top-width="0mm" border-left-width="0mm" display-align="after" padding-right="1mm">
      		<fo:block >
      			<xsl:choose>
      				<xsl:when test="APPOINTMENT/@APPOINTMENT_CODE  = 'A2'">
      					<xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@ACADEMIC_MONTHS)" />
      				</xsl:when>
      				<xsl:otherwise></xsl:otherwise>
      			</xsl:choose>
      		</fo:block>
      	</fo:table-cell>
         
      	<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
      		border-top-width="0mm" border-left-width="0mm" display-align="after" padding-right="1mm">
      		<fo:block >
      			<xsl:choose>
      				<xsl:when test="APPOINTMENT/@APPOINTMENT_CODE  = 'AS'">
      					<xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@SUMMER_MONTHS)" />
      				</xsl:when>
      				<xsl:otherwise></xsl:otherwise>
      			</xsl:choose>
      		</fo:block>
      	</fo:table-cell>
      	
<fo:table-cell border-style="solid" text-align="right" padding-right="1mm"
		  
				border-top-width="0mm" border-left-width="0mm"  display-align="after" font-size="10pt">
	         	<fo:block >
<!--
<xsl:if test="substring-before(@AGENCY_PERCENT_SALARY, '%') and not(APPOINTMENT/@APPOINTMENT_CODE = 'GR')">
<xsl:value-of select='format-number(round((number(@AGENCY_AMOUNT_SALARY) * 100)  div  number(substring-before(@AGENCY_PERCENT_SALARY, "%"))),"###,###")' />  
</xsl:if>

<xsl:if test="not(substring-before(@AGENCY_PERCENT_SALARY, '%')) and not(APPOINTMENT/@APPOINTMENT_CODE = 'GR')">

<xsl:variable name="hours" select="substring-before(@AGENCY_PERCENT_SALARY, ' ')" />

   <fo:block text-align="right"><xsl:value-of select="concat(format-number(@AGENCY_AMOUNT_SALARY div $hours, '$#,##0.00'), '/hr')" /> </fo:block>
</xsl:if>
-->

<xsl:choose>
	<xsl:when test=" not(APPOINTMENT/@APPOINTMENT_CODE = 'GR') and (APPOINTMENT/@APPOINTMENT_CODE = 'H1' or APPOINTMENT/@APPOINTMENT_CODE = 'H2' or APPOINTMENT/@APPOINTMENT_CODE = 'SH' or APPOINTMENT/@APPOINTMENT_CODE = 'WS') ">
		$<xsl:value-of select="concat(round(@PERIOD_SALARY), '.00/hr' )" />
	</xsl:when>
	<xsl:when test="  not(APPOINTMENT/@APPOINTMENT_CODE = 'GR') and (APPOINTMENT/@APPOINTMENT_CODE = 'A1' or APPOINTMENT/@APPOINTMENT_CODE = 'A2' or APPOINTMENT/@APPOINTMENT_CODE = 'AS' or APPOINTMENT/@APPOINTMENT_CODE = 'PS' or APPOINTMENT/@APPOINTMENT_CODE = 'BI' )  " >
		<xsl:value-of select="format-number(round(@PERIOD_SALARY), '###,###')" />

</xsl:when>
	<xsl:otherwise>

	</xsl:otherwise>
</xsl:choose>

			</fo:block>
		</fo:table-cell>         

		<fo:table-cell border-style="solid" text-align="right" font-size="10pt" padding-right="1mm"
				border-top-width="0mm" border-left-width="0mm"  display-align="after" >
	         	<fo:block >
	         	
      	<fo:block >    	

<!--
      	
		<xsl:choose>
			<xsl:when test="NAME = 'To Be Named' ">
				<xsl:value-of select="format-number(@AGENCY_AMOUNT_SALARY, '###,###')" />
			</xsl:when>
			<xsl:otherwise>
-->			
				  <xsl:value-of select="format-number($SUM_SALARY, '###,###')"/> 
<!--				  
			</xsl:otherwise>
		</xsl:choose>
-->
		
	 	</fo:block>  	

	         		  
			</fo:block>
		</fo:table-cell>
   
		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  border-right-width="0mm"
				border-top-width="0mm" border-left-width="0mm"   display-align="after"  padding-right="1mm"> 
	         
	<fo:block >	   
<!--	      	
	     <xsl:choose>
			<xsl:when test="NAME = 'To Be Named' ">
				<xsl:value-of select="format-number(@AGENCY_FRINGE_BENEFIT_AMOUNT,'###,###')" />
			</xsl:when>
			<xsl:otherwise>
-->			
				  <xsl:value-of select="format-number($SUM_FRINGE, '###,###')"/> 
<!--				  
			</xsl:otherwise>
		</xsl:choose>
-->		
		
	</fo:block>		         	
	         		  
			
		</fo:table-cell>
            
    		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
				border-top-width="0mm" border-left-width="0.3mm" border-right-width="0mm"    display-align="after" padding-right="1mm">
                <fo:block >

<!--                
                	     <xsl:choose>
			<xsl:when test="NAME = 'To Be Named' ">
				<xsl:value-of select="format-number(@AGENCY_FRINGE_BENEFIT_AMOUNT + @AGENCY_AMOUNT_SALARY,'###,###')" />
			</xsl:when>
			<xsl:otherwise>
-->			
				  <xsl:value-of select="format-number($SUM_TOTAL, '###,###')"/> 
<!--				  
			</xsl:otherwise>
		</xsl:choose>
-->


			</fo:block>
             </fo:table-cell>      
         </fo:table-row>
      </xsl:if>

   </xsl:for-each>
 </xsl:template>
     
     <!--   &&&&&&&&&&&&  ExtraRows  EXTRA &&&&&&&&&&&&&&&&&&& -->
     
     
    <xsl:template name="ExtraRows">
    	<xsl:param name="n" />

      <fo:table-row height="7.9mm">   
      
     	 	<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
      			border-top-width="0mm" border-left-width="0mm" display-align="after" >
      			
	         	<fo:block >
	         		  
			</fo:block>
		</fo:table-cell>
		
   		<fo:table-cell border-style="solid" text-align="right" 
   			font-size="10pt"  border-top-width="0mm" border-left-width="0mm" display-align="after" >
	         	<fo:block >
	         		
			</fo:block>
		</fo:table-cell>


		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
				border-top-width="0mm" border-left-width="0mm" display-align="after" >
	         	<fo:block >
			
			</fo:block>
		</fo:table-cell>        

		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
				border-top-width="0mm" border-left-width="0mm" display-align="after" >
	         	<fo:block >
	         		
			</fo:block>
		</fo:table-cell>
            
	      	<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
	      		border-top-width="0mm" border-left-width="0mm" display-align="after" >
	      		<fo:block >
	      			
	      		</fo:block>
	      	</fo:table-cell>
      	
		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
				border-top-width="0mm" border-left-width="0mm" display-align="after" >
	         	<fo:block >
			</fo:block>
		</fo:table-cell>         

		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
				border-top-width="0mm" border-left-width="0mm" display-align="after" >
	         	<fo:block >
	         		 
			</fo:block>
		</fo:table-cell>
   
		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  border-right-width="0mm"
				border-top-width="0mm" border-left-width="0mm" display-align="after" >
	         	<fo:block >

	         	</fo:block>
		</fo:table-cell>
            
    		<fo:table-cell border-style="solid" text-align="right" font-size="10pt"  
				border-top-width="0mm" border-left-width="0.3mm" border-right-width="0mm"  >
	         	<fo:block >
			</fo:block>
		</fo:table-cell>      
        </fo:table-row>     
     
 <xsl:if test="$n &gt; 0">  
  <xsl:call-template name="ExtraRows">
  <xsl:with-param name="n" select="$n - 1" />
  </xsl:call-template>
 </xsl:if> 
         
     </xsl:template>
       
<!--     CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC  -->
               
<xsl:template name="Consultants" >
	<xsl:param name="n" />
	<xsl:param name="Count" select="count(key('CATEGORY', 'Consultants')[AGENCY_REQUEST_AMOUNT &gt; 0])" />

<xsl:variable name="Zero_Count" >
	
	<xsl:choose>
		<xsl:when test="$Count &gt; 3 ">
		
		<xsl:call-template name="Filter_Zeros_Consultants_Num" >
			<xsl:with-param name="n" select="3" />
			<xsl:with-param name="zero_total" select="0" />
			<xsl:with-param name="non_zero_total" select="0" />			
			<xsl:with-param name="k" select="1" />
		</xsl:call-template>

		</xsl:when>
	
		<xsl:otherwise>
			0
		</xsl:otherwise>
	
	</xsl:choose>
	
</xsl:variable>


<!--  ROW 1  -->
	<xsl:if test="$n = 1" >
	<fo:table-row height="5.0mm" >
          <fo:table-cell   font-size="8pt"  font-family="Arial, sans-serif"   number-columns-spanned="5" start-indent="2mm"   display-align="center" border-top-style="solid">
          <fo:block >CONSULTANT COSTS
		</fo:block>
		</fo:table-cell>
    
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 	   
			text-align="left" padding-start="7mm" padding-top="1mm" border-top-style="solid">  	
	     	  	
		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					
					<xsl:call-template name="Consultants_Description" >
						<xsl:with-param name="n" select="1" />
					</xsl:call-template>

		       	</xsl:variable>

		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	
	     	  	</fo:block>
		
		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
	       	display-align="after" 
	       	text-align="right"
	       	number-columns-spanned="4"
	       	padding-right="2mm" 
			border-top-style="solid"
	       	>
	       	
 	    		<fo:block>  
		
				<xsl:call-template name="Consultants_Amount" >
					<xsl:with-param name="n" select="1" />
				</xsl:call-template>

       		</fo:block >
     
		</fo:table-cell>

    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif"  border-left-style="solid" border-top-style="solid"
			display-align="after" 
			number-columns-spanned="2"
			padding-start="4mm"
			font-style="italic">
				<fo:block></fo:block>
		
		</fo:table-cell>
	
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 
			border-style="solid"
			border-right-width="0mm" 
			border-left-width="0.3mm"
			border-bottom-width="0mm"
			text-align="right"
			padding-right="2mm">
  		         			
		</fo:table-cell>
	</fo:table-row>
	
<fo:table-row height="5.0mm">

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			 display-align="after" padding-start="2mm" number-columns-spanned="3">
	
		       <fo:block  >

		       	<xsl:variable name="truncate" >
			
					<xsl:call-template name="Consultants_Description" >
						<xsl:with-param name="n" select="$n + 1" />
					</xsl:call-template>

		       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
		       	
	     	 </fo:block>
				
		</fo:table-cell>
	
    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0mm" 
		border-top-width="0mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm" 
	     number-columns-spanned="2"  display-align="after" >
	
	       	<fo:block text-align="right" >
	 	
				<xsl:call-template name="Consultants_Amount" >
					<xsl:with-param name="n" select="$n + 1" />
				</xsl:call-template>

	       	</fo:block >
		</fo:table-cell>

	       
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0.3mm" 
			border-top-width="0mm" 
			border-right-width="0mm" 
			border-bottom-width="0.3mm"
			display-align="after" 	   
			text-align="left" padding-start="7mm"
			  >  	
			
			
<xsl:if test="$Count &lt; 4" >
	
		       	<fo:block  >
		       	<xsl:variable name="truncate" >
			
					<xsl:call-template name="Consultants_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>

		       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
	
</xsl:if>

<xsl:if test="$Count &gt; 3">
	<fo:block>
		See Justification
	</fo:block>
</xsl:if> 	

		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0.3mm" 
			border-top-width="0mm" 
			border-right-width="0mm" 
			border-bottom-width="0.3mm" 
	       	display-align="after" 
	       	text-align="right"
	       	number-columns-spanned="4"
	       	padding-right="2mm"
	       	>
<xsl:choose>
	<xsl:when test="$Count &gt; 3">
	
	<fo:block>  
	
		<xsl:call-template name="ConsultantTotal_P1" >
			<xsl:with-param name="k" select="3 + $Zero_Count" />
			<xsl:with-param name="total" select="0" />
		</xsl:call-template>

	</fo:block >
	</xsl:when>
	
	<xsl:otherwise>
	 
	       	<fo:block text-align="right" >
	    	
	<xsl:call-template name="Consultants_Amount" >
		<xsl:with-param name="n" select="$n + 2" />
	</xsl:call-template>


	       	</fo:block >

	</xsl:otherwise>	       	 		
   
   </xsl:choose>
 
		</fo:table-cell>
	
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 
			border-style="solid"
			border-right-width="0mm" 
			border-left-width="0.3mm"
			border-top-width="0mm"
			border-bottom-width="0mm"
			text-align="right"
			padding-right="2mm">
			

	<fo:block>  
<xsl:value-of select="$S1X_Consultants" />

	</fo:block >
  		         			
		</fo:table-cell>

       </fo:table-row>
  
<xsl:if test="$n &lt; 2" >
		<xsl:call-template name="Consultants" >
  		<xsl:with-param name="n" select="$n + 2" />
  	</xsl:call-template>
  </xsl:if>
    </xsl:if>
    
 
    
</xsl:template>         

<!--       ttttttttttTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTttttttttttttttttttt    -->
             
<xsl:template name="Travel" >
	<xsl:param name="n" />
	<xsl:param name="Count" select="count(key('CATEGORY', 'Travel')[AGENCY_REQUEST_AMOUNT &gt; 0])" />

<xsl:variable name="Zero_Count" >
	
	<xsl:choose>
		<xsl:when test="$Count &gt; 3 ">
		
		<xsl:call-template name="Filter_Zeros_Travel_Num" >
			<xsl:with-param name="n" select="3" />
			<xsl:with-param name="zero_total" select="0" />
			<xsl:with-param name="non_zero_total" select="0" />			
			<xsl:with-param name="k" select="1" />
		</xsl:call-template>

		</xsl:when>
	
		<xsl:otherwise>
			0
		</xsl:otherwise>
	
	</xsl:choose>
	
</xsl:variable>

	
	
<!--  ROW 1  -->
<xsl:if test="$n = 1" >
<fo:table-row height="4.8mm" >

          <fo:table-cell   font-size="8pt"  font-family="Arial, sans-serif"   number-columns-spanned="5" start-indent="2mm"   display-align="center" border-top-style="solid">
          <fo:block >TRAVEL
		</fo:block>
		</fo:table-cell>
	    
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 	   
			text-align="left" padding-start="7mm" padding-top="1mm" border-top-style="solid">  	
			
	     	  	key('CATEGORY', 'Travel')
		       	<fo:block  >
		       	<xsl:variable name="truncate" >
		       	
<!-- COMMENT:  The Travel_Description template, like similar templates for other non-personnel categories, exists to make sure there is no output when the agency request amount is 0 -->		       	
		       	
					<xsl:call-template name="Travel_Description" >
						<xsl:with-param name="n" select="1" />
					</xsl:call-template>
		       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
		
		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
	       	display-align="after" 
	       	text-align="right"
	       	number-columns-spanned="4"
	       	padding-right="2mm" 
			border-top-style="solid"
	       	>
	       	
 	    	<fo:block>  
	  				<xsl:call-template name="Travel_Amount" >
						<xsl:with-param name="n" select="1" />
					</xsl:call-template>
       			</fo:block >
       
		</fo:table-cell>
   
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif"  border-left-style="solid" border-top-style="solid"
			display-align="after" 
			number-columns-spanned="2"
			padding-start="4mm"
			font-style="italic">
				<fo:block></fo:block>

		</fo:table-cell>
	
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 
			border-style="solid"
			border-right-width="0mm" 
			border-left-width="0.3mm"
			border-bottom-width="0mm"
			text-align="right"
			padding-right="2mm">
  		         			
		</fo:table-cell>
	</fo:table-row>
	
<fo:table-row height="4.8mm">

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			 display-align="after" padding-start="2mm" number-columns-spanned="3">
			 
		       	<fo:block  >

		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Travel_Description" >
						<xsl:with-param name="n" select="$n + 1" />
					</xsl:call-template>


		       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
				
		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0mm" 
		border-top-width="0mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm" 
	     number-columns-spanned="2"  display-align="after" >
	       
	       	<fo:block text-align="right" >
					<xsl:call-template name="Travel_Amount" >
						<xsl:with-param name="n" select="$n +1" />
					</xsl:call-template>
	       	</fo:block >
		</fo:table-cell>
	       
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0.3mm" 
			border-top-width="0mm" 
			border-right-width="0mm" 
			border-bottom-width="0.3mm"
			display-align="after" 	   
			text-align="left" padding-start="7mm" >  	
			
<xsl:if test="$Count &lt; 4" >
	
		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Travel_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>

 
		       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
	</xsl:if>

	<xsl:if test="$Count &gt; 3">
		<fo:block>
			See Justification
		</fo:block>
	</xsl:if> 	

		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0.3mm" 
			border-top-width="0mm" 
			border-right-width="0mm" 
			border-bottom-width="0.3mm" 
	       	display-align="after" 
	       	text-align="right"
	       	number-columns-spanned="4"
	       	padding-right="2mm" >
	 
	<xsl:choose>
	<xsl:when test="$Count &gt; 3">
	
	<fo:block>  

		<xsl:call-template name="TravelTotal_P1" >
			<xsl:with-param name="k" select="3 + $Zero_Count" />
			<xsl:with-param name="total" select="0" />
		</xsl:call-template>

	</fo:block >
	</xsl:when>
	
	<xsl:otherwise>
	 
	       	<fo:block text-align="right" >
	    	
	<xsl:call-template name="Travel_Amount" >
		<xsl:with-param name="n" select="$n + 2" />
	</xsl:call-template>


	       	</fo:block >

	</xsl:otherwise>	       	 		
   
   </xsl:choose>
 
	</fo:table-cell>

    	<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 
			border-style="solid"
			border-right-width="0mm" 
			border-left-width="0.3mm"
			border-top-width="0mm"
			border-bottom-width="0mm"
			text-align="right"
			padding-right="2mm">
			

		<fo:block>  
			<xsl:value-of select="$S1X_Travel" />

		</fo:block >
         			
		</fo:table-cell>

       </fo:table-row>
  
<xsl:if test="$n &lt; 2" >
		<xsl:call-template name="Travel" >
  		<xsl:with-param name="n" select="$n + 2" />
  	</xsl:call-template>
  </xsl:if>
    </xsl:if>
</xsl:template>         

<!--     EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE -->
             
<xsl:template name="Equipment" >
	<xsl:param name="n" />
	
	<xsl:param name="Count" select="count(key('CATEGORY', 'Equipment')[AGENCY_REQUEST_AMOUNT &gt; 0])" />	

<xsl:variable name="Zero_Count" >
	
	<xsl:choose>
		<xsl:when test="$Count &gt; 5 ">
		
		<xsl:call-template name="Filter_Zeros_Equipment_Num" >
			<xsl:with-param name="n" select="5" />
			<xsl:with-param name="zero_total" select="0" />
			<xsl:with-param name="non_zero_total" select="0" />			
			<xsl:with-param name="k" select="1" />
		</xsl:call-template>

		</xsl:when>
	
		<xsl:otherwise>
			0
		</xsl:otherwise>
	
	</xsl:choose>
	
</xsl:variable>

<!--  ROW 1  -->
<xsl:if test="$n = 1" >
<fo:table-row height="5.4mm" >

          <fo:table-cell   font-size="8pt"  font-family="Arial, sans-serif"   number-columns-spanned="5" start-indent="2mm"   display-align="center" border-top-style="solid">
          <fo:block >EQUIPMENT<fo:inline font-style="italic"> (Itemize)</fo:inline>
		</fo:block>
		</fo:table-cell>
			    
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 	   
			text-align="left" padding-start="7mm" padding-top="1mm" border-top-style="solid">  	
			   	  	
		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Equipment_Description" >
						<xsl:with-param name="n" select="1" />
					</xsl:call-template>
	       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
		
		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
	       	display-align="after" 
	       	text-align="right"
	       	number-columns-spanned="4"
	       	padding-right="2mm" 
			border-top-style="solid"
	       	>
	       	
 	    	<fo:block>  
					<xsl:call-template name="Equipment_Amount" >
						<xsl:with-param name="n" select="1" />
					</xsl:call-template>
       			</fo:block >
     		
		</fo:table-cell>

    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif"  border-left-style="solid" border-top-style="solid"
			display-align="after" 
			number-columns-spanned="2"
			padding-start="4mm"
			font-style="italic">
				<fo:block></fo:block>
		
		</fo:table-cell>
	
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 
			border-style="solid"
			border-right-width="0mm" 
			border-left-width="0.3mm"
			border-bottom-width="0mm"
			text-align="right"
			padding-right="2mm">
  		         			
		</fo:table-cell>
	</fo:table-row>
   </xsl:if>
	
	<fo:table-row height="5.4mm">

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			 display-align="after" padding-start="2mm" number-columns-spanned="3">
	
		       	<fo:block  >

		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Equipment_Description" >
						<xsl:with-param name="n" select="$n + 1" />
					</xsl:call-template>
	       	
		       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
				
		</fo:table-cell>

	
    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0mm" 
		border-top-width="0mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm" 
	     number-columns-spanned="2"  display-align="after" >
							
	       
	       	<fo:block text-align="right" >
					<xsl:call-template name="Equipment_Amount" >
						<xsl:with-param name="n" select="$n + 1" />
					</xsl:call-template>
	       	</fo:block >
		</fo:table-cell>

	       
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0.3mm" 
			border-top-width="0mm" 
			border-right-width="0mm" 
			border-bottom-width="0.3mm"
			display-align="after" 	   
			text-align="left" padding-start="7mm"
			  >  	
			

	<xsl:if test="$n = 1 "> 
	
		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Equipment_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>
		       	 </xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
	
  	</xsl:if>


	<xsl:if test="$Count &lt; 6 and $n = 3" >   
	
		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Equipment_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>
		       	 </xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
  	</xsl:if>


	<xsl:if test="$Count &gt; 5 and $n = 3">
		<fo:block>
			See Justification
		</fo:block>
	</xsl:if> 	
		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0.3mm" 
			border-top-width="0mm" 
			border-right-width="0mm" 
			border-bottom-width="0.3mm" 
	       	display-align="after" 
	       	text-align="right"
	       	number-columns-spanned="4"
	       	padding-right="2mm"
	       	>

		<xsl:choose>
				<xsl:when test="$Count &gt; 5 and $n &gt; 2">
	
	<fo:block>  

		<xsl:call-template name="EquipmentTotal_P1" >
			<xsl:with-param name="k" select="5 + $Zero_Count" />
			<xsl:with-param name="total" select="0" />
		</xsl:call-template>

				</fo:block >
			</xsl:when>
	
			<xsl:otherwise>
	 
	       	<fo:block text-align="right" >
					<xsl:call-template name="Equipment_Amount" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>
	       	</fo:block >
	
			</xsl:otherwise>	       	 		
		   </xsl:choose>
   
		</fo:table-cell>

    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 
			border-style="solid"
			border-right-width="0mm" 
			border-left-width="0.3mm"
			border-top-width="0mm"
			border-bottom-width="0mm"
			text-align="right"
			padding-right="2mm">
			
<xsl:if test="$n = 3" >
	<fo:block>  
<xsl:value-of select="$S1X_Equipment" />

	</fo:block >
	</xsl:if>
		         			
		</fo:table-cell>

       </fo:table-row>
  
<xsl:if test="$n = 1" >
		<xsl:call-template name="Equipment" >
  		<xsl:with-param name="n" select="$n + 2" />
  	</xsl:call-template>
  </xsl:if>

</xsl:template>         

<!--   SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSs     -->
             
<xsl:template name="Supplies" >
	<xsl:param name="n" />
	<xsl:param name="Count" select="count(key('CATEGORY', 'Supplies')[AGENCY_REQUEST_AMOUNT &gt; 0])" />	
	
	
<xsl:variable name="Zero_Count" >
	
	<xsl:choose>
		<xsl:when test="$Count &gt; 11 ">
		
		<xsl:call-template name="Filter_Zeros_Supplies_Num" >
			<xsl:with-param name="n" select="11" />
			<xsl:with-param name="zero_total" select="0" />
			<xsl:with-param name="non_zero_total" select="0" />			
			<xsl:with-param name="k" select="1" />
		</xsl:call-template>

		</xsl:when>
	
		<xsl:otherwise>
			0
		</xsl:otherwise>
	
	</xsl:choose>
	
</xsl:variable>

	
<!--  ROW 1  -->
<xsl:if test="$n = 1" >
<fo:table-row height="4.9mm" >

          <fo:table-cell   font-size="8pt"  font-family="Arial, sans-serif"   number-columns-spanned="5" start-indent="2mm"   display-align="center" border-top-style="solid">
          <fo:block >SUPPLIES<fo:inline font-style="italic"> (Itemize by category)</fo:inline>
		</fo:block>
		</fo:table-cell>
	    
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 	   
			text-align="left" padding-start="7mm" padding-top="1mm" border-top-style="solid">  	
	     	  	
		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Supplies_Description" >
						<xsl:with-param name="n" select="1" />
					</xsl:call-template>

		       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
		
		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
	       	display-align="after" 
	       	text-align="right"
	       	number-columns-spanned="4"
	       	padding-right="2mm" 
			border-top-style="solid"
	       	>
	       	
 	    	<fo:block>  
					<xsl:call-template name="Supplies_Amount" >
						<xsl:with-param name="n" select="1" />
					</xsl:call-template>
       			</fo:block >
    
		</fo:table-cell>
   
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif"  border-left-style="solid" border-top-style="solid"
			display-align="after" 
			number-columns-spanned="2"
			padding-start="4mm"
			font-style="italic">
				<fo:block></fo:block>
		
		</fo:table-cell>
	
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 
			border-style="solid"
			border-right-width="0mm" 
			border-left-width="0.3mm"
			border-bottom-width="0mm"
			text-align="right"
			padding-right="2mm">
  		         			
		</fo:table-cell>
	</fo:table-row>
   </xsl:if>
	
		<!--   ROWS 2, 3, 4, 5, and 6  -->
	
<fo:table-row height="5mm">

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			 display-align="after" padding-start="2mm" number-columns-spanned="3">
			 
		       	<fo:block  >

		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Supplies_Description" >
						<xsl:with-param name="n" select="$n + 1" />
					</xsl:call-template>

		       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
			
		</fo:table-cell>
	
    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0mm" 
		border-top-width="0mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm" 
	     number-columns-spanned="2"  display-align="after" >
							
	       
	       	<fo:block text-align="right" >
					<xsl:call-template name="Supplies_Amount" >
						<xsl:with-param name="n" select="$n + 1" />
					</xsl:call-template>

	       	</fo:block >
		</fo:table-cell>
	       
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0.3mm" 
			border-top-width="0mm" 
			border-right-width="0mm" 
			border-bottom-width="0.3mm"
			display-align="after" 	   
			text-align="left" padding-start="7mm"
			  >  	
			
		<xsl:if test="$Count &lt; 12 and $n&lt; 10 "> 

		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Supplies_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>

		       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
		</xsl:if>

		
 		<xsl:if test="$Count &gt; 11 and $n&lt; 8 "> 

		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Supplies_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>

		       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
	
		</xsl:if>

		<xsl:if test="$Count &gt; 11 and $n &gt; 7">
			<fo:block>
				See Justification
		</fo:block>
		</xsl:if> 	
		
		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0.3mm" 
			border-top-width="0mm" 
			border-right-width="0mm" 
			border-bottom-width="0.3mm" 
	       	display-align="after" 
	       	text-align="right"
	       	number-columns-spanned="4"
	       	padding-right="2mm"
	       	>

<xsl:choose>
	<xsl:when test="$Count &gt; 11 and $n &gt; 7">
	
	<fo:block>  

		<xsl:call-template name="SuppliesTotal_P1" >
			<xsl:with-param name="k" select="11 + $Zero_Count" />
			<xsl:with-param name="total" select="0" />
		</xsl:call-template>

	</fo:block >
	</xsl:when>
	
	<xsl:otherwise>
	
	       	<fo:block text-align="right" >
						<xsl:call-template name="Supplies_Amount" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>

	       	</fo:block >
	
	</xsl:otherwise>	       	 		
  
   </xsl:choose>
   
		</fo:table-cell>
	
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 
			border-style="solid"
			border-right-width="0mm" 
			border-left-width="0.3mm"
			border-top-width="0mm"
			border-bottom-width="0mm"
			text-align="right"
			padding-right="2mm">
			
	<xsl:if test="$n = 9" >
	<fo:block>  
		<xsl:value-of select="$S1X_Supplies" />

	</fo:block >
	</xsl:if>
 		         			
		</fo:table-cell>

       </fo:table-row>
  
	<xsl:if test="$n &lt; 8" >
		<xsl:call-template name="Supplies" >
  		<xsl:with-param name="n" select="$n + 2" />
  	</xsl:call-template>
  </xsl:if>

	</xsl:template>         

<!--  OOOOOOOO  OTHER EXPENSES  In two parts.-->

<xsl:template name="Other_A" >
     
<fo:table-row height="4.7mm"  line-height="12pt">

          <fo:table-cell   font-size="8pt"  font-family="Arial, sans-serif"   number-columns-spanned="5" start-indent="2mm"   display-align="center" >
          <fo:block >OTHER EXPENSES<fo:inline font-style="italic"> (Itemize by category)</fo:inline>
		</fo:block>
		
		</fo:table-cell>
	    
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 	   
			text-align="left" padding-start="7mm" padding-top="1mm" >  	
			
		       	<fo:block  >
		       	<xsl:variable name="truncate" >
		       	
<xsl:call-template name="OtherTextControl" >
	<xsl:with-param name="n" select="1" />
</xsl:call-template>

		       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
		
		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
	       	display-align="after" 
	       	text-align="right"
	       	number-columns-spanned="4"
	       	padding-right="2mm"    	>
	       	
 	    	<fo:block>  
	       	
   	<xsl:call-template name="OtherAmountControl" >
		<xsl:with-param name="n" select="1" />
	</xsl:call-template>
		

       			</fo:block >
     
		</fo:table-cell>
   
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif"  border-left-style="solid" 
			display-align="after" 
			number-columns-spanned="2"
			padding-start="4mm"
			font-style="italic" >
				<fo:block></fo:block>
	
		</fo:table-cell>
	
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 
			border-style="solid"
			border-right-width="0mm" 
			border-left-width="0.3mm"
			border-bottom-width="0mm"
			text-align="right"
			padding-right="2mm">
  		         			
		</fo:table-cell>
	</fo:table-row>
</xsl:template>

<!--  Rows 2, 3, 4, and 5  RRRRR  -->

<xsl:template name="Other_B" >

	<xsl:param name="n" />

	<fo:table-row height="4.7mm" line-height="12pt">

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			 display-align="after" padding-start="2mm" number-columns-spanned="3">
	

		       	<fo:block  >
		       	<xsl:variable name="truncate" >
		       	
<xsl:call-template name="OtherTextControl" >
	<xsl:with-param name="n" select="$n + 1" />
</xsl:call-template>
<!--  <xsl:value-of select="$n" />  -->
		       	</xsl:variable>
		       	<xsl:value-of select="substring($truncate, 1, 25)" />
	     	  	</fo:block>
		
			
		</fo:table-cell>
	
    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0mm" 
		border-top-width="0mm" 
		border-right-width="0mm" 
		border-bottom-width="0.3mm" 
	     number-columns-spanned="2"  display-align="after" >
							
	       	<fo:block text-align="right" >
	       	
<xsl:call-template name="OtherAmountControl" >
	<xsl:with-param name="n" select="$n + 1" />
</xsl:call-template>

	       	</fo:block >
		</fo:table-cell>
	       
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0.3mm" 
			border-top-width="0mm" 
			border-right-width="0mm" 
			border-bottom-width="0.3mm"
			display-align="after" 	   
			text-align="left" padding-start="7mm"
			  >  	
			  

		       	<fo:block  >
		       	<xsl:variable name="truncate" >
		       	
<xsl:call-template name="OtherTextControl" >
	<xsl:with-param name="n" select="$n + 2" />
</xsl:call-template>

		       	 </xsl:variable>
		       	<xsl:value-of select="substring($truncate, 1, 25)" />
	     	  	</fo:block>

	
		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 
			border-left-width="0.3mm" 
			border-top-width="0mm" 
			border-right-width="0mm" 
			border-bottom-width="0.3mm" 
	       	display-align="after" 
	       	text-align="right"
	       	number-columns-spanned="4"
	       	padding-right="2mm"
	       	>
	       	<fo:block>
<xsl:call-template name="OtherAmountControl" >
	<xsl:with-param name="n" select="$n + 2" />
</xsl:call-template>
      		</fo:block>
		</fo:table-cell>

    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 
			border-style="solid"
			border-right-width="0mm" 
			border-left-width="0.3mm"
			border-top-width="0mm"
			border-bottom-width="0mm"
			text-align="right"
			padding-right="2mm">
			
	<xsl:if test="$n = 7" >
		<fo:block>  
			<xsl:value-of select="$S1X_OtherExpenses"/>
		</fo:block >
	</xsl:if>
  		         			
		</fo:table-cell>

       </fo:table-row>
  
	<xsl:if test="$n &lt; 7" >

	<xsl:call-template name="Other_B" >
		<xsl:with-param name="n" select="$n + 2" />
	</xsl:call-template>

  </xsl:if>
  
</xsl:template>

<xsl:template name="Data" >

      <xsl:variable name="S1XX_Equipment" >
	      <xsl:choose>
			<xsl:when test="
    key('CATEGORY', 'Equipment')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum(key('CATEGORY', 'Equipment')/AGENCY_REQUEST_AMOUNT), '###,###')" />  
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
   
      <xsl:variable name="S1XX_Supplies" >
	      <xsl:choose>
			<xsl:when test="
     key('CATEGORY', 'Supplies')/AGENCY_REQUEST_AMOUNT">

<!-- <xsl:value-of select="format-number(sum(key('CATEGORY', 'Supplies')/AGENCY_REQUEST_AMOUNT, '###,###'))" />  -->

<xsl:value-of select="format-number(sum(key('CATEGORY', 'Supplies')/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
    
      <xsl:variable name="S1XX_Consultants" >
	      <xsl:choose>
			<xsl:when test="
      key('CATEGORY', 'Consultants')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('CATEGORY', 'Consultants')/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
      
      <xsl:variable name="S1XX_Travel" >
	      <xsl:choose>
			<xsl:when test="
      key('CATEGORY', 'Travel')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('CATEGORY', 'Travel')/AGENCY_REQUEST_AMOUNT), '###,###')" />
  
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>
   

     		<fo:table-row text-align="right" margin-right="3mm" >
			<fo:table-cell text-align="left" border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif" number-columns-spanned="3" border-left-width="0mm" display-align="center" padding-top="1mm">
			<fo:block>CONSULTANT COSTS</fo:block>
			</fo:table-cell>
  			  		
				<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			
			<fo:block> 				  	
<xsl:value-of select="$S1XX_Consultants" />
</fo:block> 
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
	
			<fo:block> 	
<xsl:value-of select="$S2X_Consultants" />
</fo:block> 
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
	
					<fo:block> 		  	
	    	   <xsl:value-of select="$S3X_Consultants" />
</fo:block> 
			
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
		
			<fo:block> 				  	
	    	  <xsl:value-of select="$S4X_Consultants" />
</fo:block> 
			
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">

					<fo:block> 		  	
<xsl:value-of select="$S5X_Consultants" />
			</fo:block> 
			
			</fo:table-cell>
		</fo:table-row > 
		
     		<fo:table-row text-align="right" margin-right="3mm" >
			<fo:table-cell text-align="left" border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif" number-columns-spanned="3" border-left-width="0mm" display-align="center" padding-top="1mm">
			<fo:block>
			EQUIPMENT
			</fo:block>
			</fo:table-cell>
  			  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block>
			<xsl:value-of select="$S1XX_Equipment" />
			</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block>
					<xsl:value-of select="$S2X_Equipment" />
			</fo:block>
			</fo:table-cell>

			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block>
				<xsl:value-of select="$S3X_Equipment" />
			</fo:block>
			</fo:table-cell>
			
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block>
				<xsl:value-of select="$S4X_Equipment" />
			</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block>
				<xsl:value-of select="$S5X_Equipment" />
			</fo:block>
			</fo:table-cell>
		</fo:table-row > 
	
     		<fo:table-row text-align="right" margin-right="3mm" >
			<fo:table-cell text-align="left" border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif" number-columns-spanned="3" border-left-width="0mm" display-align="center" padding-top="1mm">
			<fo:block>SUPPLIES</fo:block>
			</fo:table-cell>
  		  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<fo:block><xsl:value-of select="$S1XX_Supplies" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<fo:block><xsl:value-of select="$S2X_Supplies" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<fo:block><xsl:value-of select="$S3X_Supplies" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<fo:block><xsl:value-of select="$S4X_Supplies" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<fo:block><xsl:value-of select="$S5X_Supplies" /></fo:block>
			</fo:table-cell>
		</fo:table-row > 

     		<fo:table-row text-align="right" margin-right="3mm" >
			<fo:table-cell text-align="left" border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif" number-columns-spanned="3" border-left-width="0mm" display-align="center" padding-top="1mm">
			<fo:block>TRAVEL</fo:block>
			</fo:table-cell>
  			  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm" >
			<fo:block text-align="right"><xsl:value-of select="$S1XX_Travel" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block><xsl:value-of select="$S2X_Travel" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block><xsl:value-of select="$S3X_Travel" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block><xsl:value-of select="$S4X_Travel" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block><xsl:value-of select="$S5X_Travel" /></fo:block>
			</fo:table-cell>
		</fo:table-row > 
      
     		<fo:table-row height="9mm">
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif"  number-rows-spanned="2" padding-before="4mm" border-left-width="0mm" >
			<fo:block>PATIENT<fo:block></fo:block>CARE<fo:block></fo:block>COSTS</fo:block>
			</fo:table-cell>
  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif" number-columns-spanned="2" padding-left="1mm" padding-before="3mm">
			<fo:block>INPATIENT</fo:block>
			</fo:table-cell>

			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block ><xsl:value-of select="$S1XX_Hospitalization" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block><xsl:value-of select="$S2X_Hospitalization" /></fo:block>
			</fo:table-cell>
	  		
	<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block><xsl:value-of select="$S3X_Hospitalization" /></fo:block>
			</fo:table-cell>
	  		
	<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block><xsl:value-of select="$S4X_Hospitalization" /></fo:block>
			</fo:table-cell>
	  		
	<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block><xsl:value-of select="$S5X_Hospitalization" /></fo:block>
			</fo:table-cell>
		</fo:table-row > 
	
     		<fo:table-row height="8.5mm" >
		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"   number-columns-spanned="2"  font-size="7pt"  font-family="Arial, sans-serif" padding-left="1mm" padding-before="3mm">
			<fo:block>OUTPATIENT</fo:block>
			</fo:table-cell>
	  		

			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">

			<fo:block><xsl:value-of select="$S1XX_Outpatient" /></fo:block>
			</fo:table-cell>
	  		
<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">

			<fo:block><xsl:value-of select="$S2X_Outpatient" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">

			<fo:block><xsl:value-of select="$S3X_Outpatient" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block><xsl:value-of select="$S4X_Outpatient" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block><xsl:value-of select="$S5X_Outpatient" /></fo:block>
			</fo:table-cell>
			
		</fo:table-row > 
   		
     		<fo:table-row height="8mm" >
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif" number-columns-spanned="3" border-left-width="0mm" display-align="center" padding-top="1mm">
			<fo:block>ALTERATIONS AND<fo:block></fo:block>RENOVATIONS</fo:block>
			</fo:table-cell>
				  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block text-align="right"><xsl:value-of select="$S1XX_Alterations" /></fo:block>
			</fo:table-cell>
			
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block text-align="right"><xsl:value-of select="$S2X_Alterations" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block text-align="right"><xsl:value-of select="$S3X_Alterations" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block text-align="right"><xsl:value-of select="$S4X_Alterations" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block text-align="right"><xsl:value-of select="$S5X_Alterations" /></fo:block>
			</fo:table-cell>
		</fo:table-row > 
      	
     		<fo:table-row height="8mm" >
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif" number-columns-spanned="3" border-left-width="0mm" display-align="center" padding-top="1mm">
			<fo:block text-align="left">OTHER EXPENSES</fo:block>
			</fo:table-cell>
  	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
				<fo:block><xsl:value-of select="$S1X_DUP_OtherExpenses" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
				<fo:block>
					<xsl:value-of select="$S2X_OtherExpenses" />
				</fo:block>
			</fo:table-cell>
			
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block><xsl:value-of select="$S3X_OtherExpenses" />
			</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block><xsl:value-of select="$S4X_OtherExpenses" />
			</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" text-align="right" padding-right="3mm"  font-size="10pt"  font-family="Arial, sans-serif" display-align="center" padding-top="1.5mm">
			<fo:block><xsl:value-of select="$S5X_OtherExpenses" />
			</fo:block>
			</fo:table-cell>
		</fo:table-row > 

     		<fo:table-row height="8mm" text-align="right" margin-right="3mm" >
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif" number-columns-spanned="2" border-left-width="0mm" text-align="left">
			<fo:block padding-before="1mm">CONSORTIUM/<fo:block></fo:block>CONTRACTUAL<fo:block></fo:block>COSTS</fo:block>
			</fo:table-cell>
  	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif"  padding-before="3mm" text-align="left" padding-left="1.5mm">
			<fo:block>DIRECT</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm">
			<fo:block padding-before="3mm">
					
					<xsl:value-of select="$S1X_Subcontractors" />

			</fo:block>
			</fo:table-cell>
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm">
			<fo:block padding-before="3mm">
					<xsl:value-of select="$S2X_Subcontractors" />
			</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif">
			<fo:block padding-before="3mm">
					<xsl:value-of select="$S3X_Subcontractors" />
			</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" >
			<fo:block padding-before="3mm">
					<xsl:value-of select="$S4X_Subcontractors" />
			</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" >
			<fo:block padding-before="3mm">
					<xsl:value-of select="$S5X_Subcontractors" />
			</fo:block>
			</fo:table-cell>

		</fo:table-row >

     		<fo:table-row text-align="right" margin-right="3mm" >
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm" font-family="Arial, sans-serif" height="8mm" number-columns-spanned="3" border-left-width="0mm">

			<fo:block font-size="8pt" padding-before="0.5mm" font-weight="bold" text-align="left" 
				margin-right="0mm">SUBTOTAL DIRECT COSTS</fo:block>
			<fo:block font-size="7pt" padding-before="0.5mm" text-align="left" margin-right="0mm" 
				font-style="italic">(Sum = Item 8a, Face Page)</fo:block>
			</fo:table-cell>

			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  
				font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<xsl:choose>
					<xsl:when test="$S1_SubtotalDirect &gt; 0">
						<fo:block>
							<xsl:value-of select="format-number($S1_SubtotalDirect, '###,###')" />
						</fo:block>
					</xsl:when>
					<xsl:when test="$S1_SubtotalDirect &lt; 0">
						<fo:block>
							(<xsl:value-of select="format-number(substring($S1_SubtotalDirect,2), '###,###')" />)
						</fo:block>
					</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  
				font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<xsl:choose>
					<xsl:when test="$S2_SubtotalDirect &gt; 0">
						<fo:block>
							<xsl:value-of select="format-number($S2_SubtotalDirect, '###,###')"/>
						</fo:block>
					</xsl:when>
					<xsl:when test="$S2_SubtotalDirect &lt; 0">
						<fo:block>
							(<xsl:value-of select="format-number(substring($S2_SubtotalDirect,2), '###,###')" />)
						</fo:block>
					</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  
				font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<xsl:choose>
					<xsl:when test="$S3_SubtotalDirect &gt; 0">
						<fo:block>
							<xsl:value-of select="format-number($S3_SubtotalDirect, '###,###')"/>
						</fo:block>
					</xsl:when>
					<xsl:when test="$S3_SubtotalDirect &lt; 0">
						<fo:block>
							(<xsl:value-of select="format-number(substring($S3_SubtotalDirect,2), '###,###')" />)
						</fo:block>
					</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  
				font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<xsl:choose>
					<xsl:when test="$S4_SubtotalDirect &gt; 0">
						<fo:block>
							<xsl:value-of select="format-number($S4_SubtotalDirect, '###,###')"/>
						</fo:block>
					</xsl:when>
					<xsl:when test="$S4_SubtotalDirect &lt; 0">
						<fo:block>
							(<xsl:value-of select="format-number(substring($S4_SubtotalDirect,2), '###,###')" />)
						</fo:block>
					</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
			</fo:table-cell>
	  		
			<fo:table-cell border-style="solid" border-after-width="0mm" border-right-width="0mm"  
				font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<xsl:choose>
					<xsl:when test="$S5_SubtotalDirect &gt; 0">
						<fo:block>
							<xsl:value-of select="format-number($S5_SubtotalDirect, '###,###')"/>
						</fo:block>
					</xsl:when>
					<xsl:when test="$S5_SubtotalDirect &lt; 0">
						<fo:block>
							(<xsl:value-of select="format-number(substring($S5_SubtotalDirect,2), '###,###')" />)
						</fo:block>
					</xsl:when>
					<xsl:otherwise>
						
					</xsl:otherwise>
				</xsl:choose>
			</fo:table-cell>
     			
		</fo:table-row > 
      
     		<fo:table-row height="8mm" text-align="right" margin-right="3mm" >
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif" number-columns-spanned="2" border-left-width="0mm" text-align="left">
			<fo:block padding-before="1mm">CONSORTIUM/<fo:block></fo:block>CONTRACTUAL<fo:block></fo:block>COSTS</fo:block>
			</fo:table-cell>

			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="7pt"  font-family="Arial, sans-serif" padding-left="1.5mm" padding-before="3mm" text-align="left">
			<fo:block>F&amp;A</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block>
				<xsl:value-of select="$S1XX_IDC" />
			</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block>
				<xsl:value-of select="$S2X_IDC" />
			</fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block>
				<xsl:value-of select="$S3X_IDC" />
			</fo:block>

			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block>
				<xsl:value-of select="$S4X_IDC" />
			</fo:block>

			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
			<fo:block>
				<xsl:value-of select="$S5X_IDC" />
			</fo:block>

			</fo:table-cell>
		</fo:table-row > 
      
    <fo:table-row text-align="right" margin-right="3mm" >
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="8mm" number-columns-spanned="3" border-left-width="0mm">
				<fo:block text-align="left" padding-before="3.5mm" font-size="9pt" font-weight="bold">TOTAL DIRECT COSTS</fo:block>
			</fo:table-cell>
  		  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<fo:block><xsl:value-of select="$S1X_TotalDirect" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<fo:block><xsl:value-of select="$S2X_TotalDirect" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<fo:block><xsl:value-of select="$S3X_TotalDirect" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<fo:block><xsl:value-of select="$S4X_TotalDirect" /></fo:block>
			</fo:table-cell>
	  		
			<fo:table-cell  border-style="solid" border-after-width="0mm" border-right-width="0mm"  font-size="10pt"  font-family="Arial, sans-serif" height="5mm" padding-before="3mm">
				<fo:block><xsl:value-of select="$S5X_TotalDirect" /></fo:block>
			</fo:table-cell>
		</fo:table-row > 
      
</xsl:template>

<!--   AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA -->
            
<xsl:template name="Alterations" >
	<xsl:param name="n" />
	<xsl:param name="Count" select="count(key('SUB_CATEGORY', 'Alterations and Renovations')[AGENCY_REQUEST_AMOUNT &gt; 0])" />

<xsl:variable name="Zero_Count" >
	
	<xsl:choose>
		<xsl:when test="$Count &gt; 3 ">
		
		<xsl:call-template name="Filter_Zeros_Alterations_Num" >
			<xsl:with-param name="n" select="3" />
			<xsl:with-param name="zero_total" select="0" />
			<xsl:with-param name="non_zero_total" select="0" />			
			<xsl:with-param name="k" select="1" />
		</xsl:call-template>

		</xsl:when>
	
		<xsl:otherwise>
			0
		</xsl:otherwise>
	
	</xsl:choose>
	
</xsl:variable>

<!--  ROW 1  -->
<xsl:if test="$n = 1" >
<fo:table-row height="5.0mm" >

          <fo:table-cell   font-size="8pt"  font-family="Arial, sans-serif"   number-columns-spanned="5" start-indent="2mm"   display-align="center" >
          <fo:block >ALTERATIONS AND RENOVATIONS<fo:inline font-style="italic"> (Itemize by category)</fo:inline>
		</fo:block>
		</fo:table-cell>
	    
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 	   
			text-align="left" padding-start="7mm" padding-top="1mm" >  	
	     	  	
		 <fo:block  >
		       <xsl:variable name="truncate" >
					<xsl:call-template name="Alterations_Description" >
						<xsl:with-param name="n" select="1" />
					</xsl:call-template>
		       	 </xsl:variable>
		       <xsl:value-of select="substring($truncate, 1, 30)" />
	     </fo:block>
		
		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" 		
	       	display-align="after" 
	       	text-align="right"
	       	number-columns-spanned="4"
	       	padding-right="2mm" 
	       	>
	       	
 	    		<fo:block>  
				<xsl:call-template name="Alterations_Amount" >
						<xsl:with-param name="n" select="1" />
				</xsl:call-template>
       		</fo:block >
		</fo:table-cell>

    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif"   border-left-style="solid"
			display-align="after" 
			number-columns-spanned="2"
			padding-start="4mm"
			font-style="italic">
				<fo:block></fo:block>

		
		</fo:table-cell>
	
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" 
			display-align="after" 

			text-align="right"
			padding-right="2mm">
  		         			
		</fo:table-cell>
	</fo:table-row>
	

<fo:table-row height="5.0mm">

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" border-bottom-style="solid"
			 display-align="after" padding-start="2mm" number-columns-spanned="3">
			 
		       	<fo:block  >

		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Alterations_Description" >
						<xsl:with-param name="n" select="$n + 1" />
					</xsl:call-template>
		       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
				
		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" border-bottom-style="solid"
	     number-columns-spanned="2"  display-align="after" >

	       	<fo:block text-align="right" >
					<xsl:call-template name="Alterations_Amount" >
						<xsl:with-param name="n" select="$n + 1" />
					</xsl:call-template>
	       	</fo:block >
		</fo:table-cell>

    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" border-bottom-style="solid" 
		
			display-align="after" 	   
			text-align="left" padding-start="7mm"
			  >  	
			
<xsl:if test="$Count &lt; 4" >
	
		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Alterations_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>
		       	</xsl:variable>
		       	
		       	<xsl:value-of select="substring($truncate, 1, 30)" />
	     	  	</fo:block>
</xsl:if>

<xsl:if test="$Count &gt; 3">
	<fo:block>
		See Justification
	</fo:block>
</xsl:if> 	

		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif" border-bottom-style="solid"

	       	display-align="after" 
	       	text-align="right"
	       	number-columns-spanned="4"
	       	padding-right="2mm"
	       	>

<xsl:choose>
	<xsl:when test="$Count &gt; 3">
	
	<fo:block>  

		<xsl:call-template name="AlterationsTotal_P1" >
			<xsl:with-param name="k" select="3 + $Zero_Count" />
			<xsl:with-param name="total" select="0" />
		</xsl:call-template>

	</fo:block >
	</xsl:when>
	
	<xsl:otherwise>

	       	<fo:block text-align="right" >
					<xsl:call-template name="Alterations_Amount" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>
	       	</fo:block >

	</xsl:otherwise>	       	 		
 
   </xsl:choose>
  
		</fo:table-cell>
 	
    		<fo:table-cell    font-size="10pt"  font-family="Arial, sans-serif" border-bottom-style="solid" border-left-style="solid"
			display-align="after" 
		
			text-align="right"
			padding-right="2mm">

	<fo:block>  

<xsl:value-of select="$S1X_Alterations" /> 


	</fo:block >
  		         			
		</fo:table-cell>

       </fo:table-row>
    
<xsl:if test="$n &lt; 2" >
		<xsl:call-template name="Alterations" >
  		<xsl:with-param name="n" select="$n + 2" />
  	</xsl:call-template>
  </xsl:if>
    </xsl:if>
</xsl:template>         
	
	<xsl:template name="OtherExpenseTotal_P1">
		<xsl:param name="k"/>
		<xsl:param name="total"/>
		
		<xsl:param name="XXX">
			<xsl:choose>
				<xsl:when test="key('CATEGORY', 'Other Expenses')[not(SUB_CATEGORY = 'Alterations and Renovations') and not(SUB_CATEGORY = 'Fee Remissions') and not(SUB_CATEGORY = 'Subject Payments') ][$k]/AGENCY_REQUEST_AMOUNT">
					<xsl:value-of select="key('CATEGORY', 'Other Expenses')[$k]/AGENCY_REQUEST_AMOUNT"/>
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		
		<xsl:if test="$k &lt; $OtherCount_P1 + 1">
			<xsl:call-template name="OtherExpenseTotal_P1">
				<xsl:with-param name="total" select="$total + $XXX"/>
				<xsl:with-param name="k" select="$k + 1"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$k = $OtherCount_P1 + 1">

		<xsl:value-of select="format-number($total + $S1_Fee + $S1_Subject, '###,###')"/><fo:block></fo:block>
		
		</xsl:if>
	</xsl:template>
		
	<xsl:template name="EquipmentTotal_P1">
		<xsl:param name="k"/>
		<xsl:param name="total"/>
		<xsl:param name="XXX">
			<xsl:choose>
				<xsl:when test="key('CATEGORY', 'Equipment')[$k]/AGENCY_REQUEST_AMOUNT">
					<xsl:value-of select="key('CATEGORY', 'Equipment')[$k]/AGENCY_REQUEST_AMOUNT"/>
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<xsl:if test="$k &lt; $Equipment_P1 + 1">
			<xsl:call-template name="EquipmentTotal_P1">
				<xsl:with-param name="total" select="$total + $XXX"/>
				<xsl:with-param name="k" select="$k + 1"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$k = $Equipment_P1 + 1">
			<xsl:value-of select="format-number($total, '###,###')"/>
		</xsl:if>
	</xsl:template>
		
	<xsl:template name="SuppliesTotal_P1">
		<xsl:param name="k"/>
		<xsl:param name="total"/>
		
		
		<xsl:param name="XXX">
			<xsl:choose>
				<xsl:when test="key('CATEGORY', 'Supplies')[$k]/AGENCY_REQUEST_AMOUNT">
					<xsl:value-of select="key('CATEGORY', 'Supplies')[$k]/AGENCY_REQUEST_AMOUNT"/>
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		
		
		
		<xsl:if test="$k &lt; $SuppliesCount_P1 + 1">
			<xsl:call-template name="SuppliesTotal_P1">
				<xsl:with-param name="total" select="$total + $XXX"/>
				<xsl:with-param name="k" select="$k + 1"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$k = $SuppliesCount_P1 + 1">
			<xsl:value-of select="format-number($total, '###,###')"/>
			<fo:block/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="ConsultantTotal_P1">
		<xsl:param name="k"/>
		<xsl:param name="total"/>
		
		<xsl:param name="XXX">
			<xsl:choose>
				<xsl:when test="key('CATEGORY', 'Consultants')[$k]/AGENCY_REQUEST_AMOUNT">
					<xsl:value-of select="key('CATEGORY', 'Consultants')[$k]/AGENCY_REQUEST_AMOUNT"/>
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		
		<xsl:if test="$k &lt; $ConsultantCount_P1 + 1">
			<xsl:call-template name="ConsultantTotal_P1">
				<xsl:with-param name="total" select="$total + $XXX"/>
				<xsl:with-param name="k" select="$k + 1"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$k = $ConsultantCount_P1 + 1">
			<xsl:value-of select="format-number($total, '###,###')"/>
			<fo:block/>
		</xsl:if>
	</xsl:template>
		
	<xsl:template name="AlterationsTotal_P1">
		<xsl:param name="k"/>
		<xsl:param name="total"/>
		
		<xsl:param name="XXX">
			<xsl:choose>
				<xsl:when test="key('SUB_CATEGORY', 'Alterations and Renovations')[$k]/AGENCY_REQUEST_AMOUNT">
					<xsl:value-of select="key('SUB_CATEGORY', 'Alterations and Renovations')[$k]/AGENCY_REQUEST_AMOUNT"/>
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		
		<xsl:if test="$k &lt; $AlterationsCount_P1 + 1">
			<xsl:call-template name="AlterationsTotal_P1">
				<xsl:with-param name="total" select="$total + $XXX"/>
				<xsl:with-param name="k" select="$k + 1"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$k = $AlterationsCount_P1 + 1">
			<xsl:value-of select="format-number($total, '###,###')"/>
			<fo:block/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="TravelTotal_P1">
		<xsl:param name="k"/>
		<xsl:param name="total"/>
		<xsl:param name="XXX">
			<xsl:choose>
				<xsl:when test="key('CATEGORY', 'Travel')[$k]/AGENCY_REQUEST_AMOUNT">
					<xsl:value-of select="key('CATEGORY', 'Travel')[$k]/AGENCY_REQUEST_AMOUNT"/>
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<xsl:if test="$k &lt; $TravelCount_P1 + 1">
			<xsl:call-template name="TravelTotal_P1">
				<xsl:with-param name="total" select="$total + $XXX"/>
				<xsl:with-param name="k" select="$k + 1"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$k = $TravelCount_P1 + 1">
			<xsl:value-of select="format-number($total, '###,###')"/>
			<fo:block/>
		</xsl:if>
	</xsl:template>
	
<xsl:template name="SubtotalSalary" >
	<xsl:param name="Start" />
	<xsl:param name="total" />
	
	
	
	
	<xsl:variable name="SALARY_SUM_A" >
	<xsl:for-each select="$PERSON_OTHER" >	
	
		<xsl:if test="position() = $Start + 1 ">
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
 		</xsl:if>
 		
		
 		
 		
	</xsl:for-each>
</xsl:variable>

<xsl:variable name="SALARY_SUM_AX"	>
	<xsl:choose>
		<xsl:when test="$SALARY_SUM_A = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$SALARY_SUM_A" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
      		

	

<xsl:variable name="SALARY_SUM_B" >
	<xsl:for-each select="$PERSON_OTHER" >	
		<xsl:if test="position() = $Start + 2 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
 		</xsl:if>
 		
 		
 		
	</xsl:for-each>
</xsl:variable>

<xsl:variable name="SALARY_SUM_BX"	>
	<xsl:choose>
		<xsl:when test="$SALARY_SUM_B = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$SALARY_SUM_B" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
      		

	
<xsl:variable name="SALARY_SUM_C" >
	<xsl:for-each select="$PERSON_OTHER" >	
		<xsl:if test="position() = $Start + 3 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
 		</xsl:if>
	</xsl:for-each>
		
	
</xsl:variable>

<xsl:variable name="SALARY_SUM_CX"	>
	<xsl:choose>
		<xsl:when test="$SALARY_SUM_C = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$SALARY_SUM_C" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
      		
      		

	
<xsl:variable name="SALARY_SUM_D" >
	<xsl:for-each select="$PERSON_OTHER" >	
		<xsl:if test="position() = $Start + 4 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
 		</xsl:if>
 			
	</xsl:for-each>
	
</xsl:variable>

<xsl:variable name="SALARY_SUM_DX"	>
	<xsl:choose>
		<xsl:when test="$SALARY_SUM_D = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$SALARY_SUM_D" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
      		
      		

	
<xsl:variable name="SALARY_SUM_E" >
	<xsl:for-each select="$PERSON_OTHER" >	
		<xsl:if test="position() = $Start + 5 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
 		</xsl:if>
 		
			
	</xsl:for-each>
</xsl:variable>

<xsl:variable name="SALARY_SUM_EX"	>
	<xsl:choose>
		<xsl:when test="$SALARY_SUM_E = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$SALARY_SUM_E" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
      		
      		

	
	
<xsl:variable name="SALARY_SUM_F" >
	<xsl:for-each select="$PERSON_OTHER" >	
		<xsl:if test="position() = $Start + 6 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
 		</xsl:if>
 		
			
 		
 		
 		
	</xsl:for-each>
</xsl:variable>

<xsl:variable name="SALARY_SUM_FX"	>
	<xsl:choose>
		<xsl:when test="$SALARY_SUM_F = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$SALARY_SUM_F" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
      		
      		

	
	
<xsl:variable name="SALARY_SUM_G" >
	<xsl:for-each select="$PERSON_OTHER" >	
		<xsl:if test="position() = $Start + 7 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
 		</xsl:if>
 		
 		
	</xsl:for-each>
</xsl:variable>

<xsl:variable name="SALARY_SUM_GX"	>
	<xsl:choose>
		<xsl:when test="$SALARY_SUM_G = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$SALARY_SUM_G" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
  			
	<xsl:value-of select="  $SALARY_SUM_AX + $SALARY_SUM_BX + $SALARY_SUM_CX + $SALARY_SUM_DX + $SALARY_SUM_EX + $SALARY_SUM_FX + $SALARY_SUM_GX  " />	

</xsl:template>	

<xsl:template name="SubtotalFringe" >
	<xsl:param name="Start" />
	<xsl:param name="total" />
	
	<xsl:variable name="FRINGE_SUM_A" >
	<xsl:for-each select="$PERSON_OTHER" >	
		<xsl:if test="position() = $Start + 1 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
 		</xsl:if>
 		
	
 	</xsl:for-each>
</xsl:variable>

<xsl:variable name="FRINGE_SUM_AX"	>
	<xsl:choose>
		<xsl:when test="$FRINGE_SUM_A = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$FRINGE_SUM_A" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
      		

	

<xsl:variable name="FRINGE_SUM_B" >
	<xsl:for-each select="$PERSON_OTHER" >	
		<xsl:if test="position() = $Start + 2 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
 		</xsl:if>
 		
	
		
 		
 		
	</xsl:for-each>
</xsl:variable>

<xsl:variable name="FRINGE_SUM_BX"	>
	<xsl:choose>
		<xsl:when test="$FRINGE_SUM_B = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$FRINGE_SUM_B" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
      		

	
<xsl:variable name="FRINGE_SUM_C" >
	<xsl:for-each select="$PERSON_OTHER" >	
		<xsl:if test="position() = $Start + 3 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
 		</xsl:if>
 		
			
 		
 		
	</xsl:for-each>
</xsl:variable>

<xsl:variable name="FRINGE_SUM_CX"	>
	<xsl:choose>
		<xsl:when test="$FRINGE_SUM_C = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$FRINGE_SUM_C" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
      		
      		

	
<xsl:variable name="FRINGE_SUM_D" >
	<xsl:for-each select="$PERSON_OTHER" >	
		<xsl:if test="position() = $Start + 4 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
 		</xsl:if>
 		
		
 		
	</xsl:for-each>
</xsl:variable>

<xsl:variable name="FRINGE_SUM_DX"	>
	<xsl:choose>
		<xsl:when test="$FRINGE_SUM_D = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$FRINGE_SUM_D" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
      		
      		

	
<xsl:variable name="FRINGE_SUM_E" >
	<xsl:for-each select="$PERSON_OTHER" >	
		<xsl:if test="position() = $Start + 5 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
 		</xsl:if>
 		
 		
	</xsl:for-each>
</xsl:variable>

<xsl:variable name="FRINGE_SUM_EX"	>
	<xsl:choose>
		<xsl:when test="$FRINGE_SUM_E = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$FRINGE_SUM_E" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
      		
      		

	
	
<xsl:variable name="FRINGE_SUM_F" >
	<xsl:for-each select="$PERSON_OTHER" >	
		<xsl:if test="position() = $Start + 6 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
 		</xsl:if>
 		
	
 		
	</xsl:for-each>
</xsl:variable>

<xsl:variable name="FRINGE_SUM_FX"	>
	<xsl:choose>
		<xsl:when test="$FRINGE_SUM_F = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$FRINGE_SUM_F" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
      		
 <xsl:variable name="FRINGE_SUM_G" >
	<xsl:for-each select="$PERSON_OTHER" >	
		<xsl:if test="position() = $Start + 7 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
 		</xsl:if>
 		
		
 		
	</xsl:for-each>
</xsl:variable>

<xsl:variable name="FRINGE_SUM_GX"	>
	<xsl:choose>
		<xsl:when test="$FRINGE_SUM_G = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$FRINGE_SUM_G" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
  			
	<xsl:value-of select="  $FRINGE_SUM_AX + $FRINGE_SUM_BX + $FRINGE_SUM_CX + $FRINGE_SUM_DX + $FRINGE_SUM_EX + $FRINGE_SUM_FX + $FRINGE_SUM_GX  " />	

</xsl:template>	

<xsl:template name="SubtotalTotal" >
	<xsl:param name="Start" />
	<xsl:param name="total" />
		
	<xsl:variable name="FRINGE_SUM_A" >
		<xsl:for-each select="$PERSON_OTHER" >	
			<xsl:if test="position() = $Start + 1 " >
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
 			</xsl:if>
 		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="FRINGE_SUM_AX"	>
		<xsl:choose>
			<xsl:when test="$FRINGE_SUM_A = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$FRINGE_SUM_A" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
      		
	<xsl:variable name="FRINGE_SUM_B" >
		<xsl:for-each select="$PERSON_OTHER" >	
			<xsl:if test="position() = $Start + 2 " >
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
 			</xsl:if>	
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="FRINGE_SUM_BX"	>
		<xsl:choose>
			<xsl:when test="$FRINGE_SUM_B = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$FRINGE_SUM_B" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
      			
	<xsl:variable name="FRINGE_SUM_C" >
		<xsl:for-each select="$PERSON_OTHER" >	
			<xsl:if test="position() = $Start + 3 " >
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
	 		</xsl:if>		
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="FRINGE_SUM_CX"	>
		<xsl:choose>
			<xsl:when test="$FRINGE_SUM_C = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$FRINGE_SUM_C" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
      			
	<xsl:variable name="FRINGE_SUM_D" >
		<xsl:for-each select="$PERSON_OTHER" >	
			<xsl:if test="position() = $Start + 4 " >
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
	 		</xsl:if>		
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="FRINGE_SUM_DX"	>
		<xsl:choose>
			<xsl:when test="$FRINGE_SUM_D = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$FRINGE_SUM_D" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
      			
	<xsl:variable name="FRINGE_SUM_E" >
		<xsl:for-each select="$PERSON_OTHER" >	
			<xsl:if test="position() = $Start + 5 " >
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
	 		</xsl:if>
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="FRINGE_SUM_EX"	>
		<xsl:choose>
			<xsl:when test="$FRINGE_SUM_E = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$FRINGE_SUM_E" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
      			
	<xsl:variable name="FRINGE_SUM_F" >
		<xsl:for-each select="$PERSON_OTHER" >	
			<xsl:if test="position() = $Start + 6 " >
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
	 		</xsl:if>		
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="FRINGE_SUM_FX"	>
		<xsl:choose>
			<xsl:when test="$FRINGE_SUM_F = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$FRINGE_SUM_F" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
      		
	<xsl:variable name="FRINGE_SUM_G" >
		<xsl:for-each select="$PERSON_OTHER" >	
			<xsl:if test="position() = $Start + 7 " >
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
	 		</xsl:if>
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="FRINGE_SUM_GX"	>
		<xsl:choose>
			<xsl:when test="$FRINGE_SUM_G = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$FRINGE_SUM_G" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
  		
	<xsl:variable name="SALARY_SUM_A" >
		<xsl:for-each select="$PERSON_OTHER" >		
			<xsl:if test="position() = $Start + 1 ">
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
	 		</xsl:if>
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="SALARY_SUM_AX"	>
		<xsl:choose>
			<xsl:when test="$SALARY_SUM_A = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$SALARY_SUM_A" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
      		
	<xsl:variable name="SALARY_SUM_B" >
		<xsl:for-each select="$PERSON_OTHER" >	
			<xsl:if test="position() = $Start + 2 " >
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
	 		</xsl:if>		
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="SALARY_SUM_BX"	>
		<xsl:choose>
			<xsl:when test="$SALARY_SUM_B = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$SALARY_SUM_B" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
      		
	<xsl:variable name="SALARY_SUM_C" >
		<xsl:for-each select="$PERSON_OTHER" >	
			<xsl:if test="position() = $Start + 3 " >
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
	 		</xsl:if>
		</xsl:for-each>	
	</xsl:variable>

	<xsl:variable name="SALARY_SUM_CX"	>
		<xsl:choose>
			<xsl:when test="$SALARY_SUM_C = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$SALARY_SUM_C" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
      			
	<xsl:variable name="SALARY_SUM_D" >
		<xsl:for-each select="$PERSON_OTHER" >	
			<xsl:if test="position() = $Start + 4 " >
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
	 		</xsl:if>	
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="SALARY_SUM_DX"	>
		<xsl:choose>
			<xsl:when test="$SALARY_SUM_D = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$SALARY_SUM_D" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
      			
	<xsl:variable name="SALARY_SUM_E" >
		<xsl:for-each select="$PERSON_OTHER" >	
			<xsl:if test="position() = $Start + 5 " >
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
	 		</xsl:if>
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="SALARY_SUM_EX"	>
		<xsl:choose>
			<xsl:when test="$SALARY_SUM_E = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$SALARY_SUM_E" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
      			
	<xsl:variable name="SALARY_SUM_F" >
		<xsl:for-each select="$PERSON_OTHER" >	
			<xsl:if test="position() = $Start + 6 " >
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
	 		</xsl:if>		
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="SALARY_SUM_FX"	>
		<xsl:choose>
			<xsl:when test="$SALARY_SUM_F = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$SALARY_SUM_F" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
      			
	<xsl:variable name="SALARY_SUM_G" >
		<xsl:for-each select="$PERSON_OTHER" >	
			<xsl:if test="position() = $Start + 7 " >
				<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
	 		</xsl:if>	
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="SALARY_SUM_GX"	>
		<xsl:choose>
			<xsl:when test="$SALARY_SUM_G = '' ">
				0
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$SALARY_SUM_G" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
  			 			
	<xsl:value-of select="  $FRINGE_SUM_AX + $FRINGE_SUM_BX + $FRINGE_SUM_CX + $FRINGE_SUM_DX + $FRINGE_SUM_EX + $FRINGE_SUM_FX + $FRINGE_SUM_GX +  $SALARY_SUM_AX + $SALARY_SUM_BX + $SALARY_SUM_CX + $SALARY_SUM_DX + $SALARY_SUM_EX + $SALARY_SUM_FX + $SALARY_SUM_GX   " />	

</xsl:template>
	
<xsl:template name="InnerSalarySubtotal" >
	<xsl:param name="k" />
	<xsl:for-each select="$PERSON_OTHER" >
		<xsl:if test="position() = $k " >
			<xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY) " />
		</xsl:if>		
	</xsl:for-each>
</xsl:template>

<xsl:template name="SalarySubtotal" >
<xsl:param name="total" />
<xsl:param name="n" />
<xsl:param name="p" />

	<xsl:variable name="Var" >
		<xsl:call-template name="InnerSalarySubtotal" >
			<xsl:with-param name="k" select="$n" />
		</xsl:call-template>
	</xsl:variable>

	<xsl:if test="$n &lt; $p" >
		<xsl:call-template name="SalarySubtotal" >
			<xsl:with-param name="total" select="$total + $Var" />
			<xsl:with-param name="n" select="$n + 1" />
			<xsl:with-param name="p" select="$p" />
		</xsl:call-template>
	</xsl:if>

	<xsl:if test="$n = $p" >
		<xsl:value-of select="format-number($total, '###,###')" />
	</xsl:if>

</xsl:template>

<xsl:template name="InnerFringeSubtotal" >
	<xsl:param name="k" />
	<xsl:for-each select="$PERSON_OTHER" >
		<xsl:if test="position() = $k " >
			<xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT) " />
		</xsl:if>	
	</xsl:for-each>
</xsl:template>

<xsl:template name="FringeSubtotal" >
<xsl:param name="total" />
<xsl:param name="n" />
<xsl:param name="p" />

	<xsl:variable name="Var" >
		<xsl:call-template name="InnerFringeSubtotal" >
			<xsl:with-param name="k" select="$n" />
		</xsl:call-template>
	</xsl:variable>

	<xsl:if test="$n &lt; $p" >
		<xsl:call-template name="FringeSubtotal" >
			<xsl:with-param name="total" select="$total + $Var" />
			<xsl:with-param name="n" select="$n + 1" />
			<xsl:with-param name="p" select="$p" />
		</xsl:call-template>
	</xsl:if>

	<xsl:if test="$n = $p" >
		<xsl:value-of select="format-number($total, '###,###')" />
	</xsl:if>

</xsl:template>
	
<xsl:template name="InnerTotalSubtotal" >
	<xsl:param name="k" />
	<xsl:for-each select="$PERSON_OTHER" >
		<xsl:if test="position() = $k " >
			<xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT) + sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = '1']/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY) " />
		</xsl:if>		
	</xsl:for-each>
</xsl:template>

<xsl:template name="TotalSubtotal" >
<xsl:param name="total" />
<xsl:param name="n" />
<xsl:param name="p" />

	<xsl:variable name="Var" >
		<xsl:call-template name="InnerTotalSubtotal" >
			<xsl:with-param name="k" select="$n" />
		</xsl:call-template>
	</xsl:variable>

	<xsl:if test="$n &lt; $p" >
		<xsl:call-template name="TotalSubtotal" >
			<xsl:with-param name="total" select="$total + $Var" />
			<xsl:with-param name="n" select="$n + 1" />
			<xsl:with-param name="p" select="$p" />
		</xsl:call-template>
	</xsl:if>

	<xsl:if test="$n = $p" >
		<xsl:value-of select="format-number($total, '###,###')" />
	</xsl:if>

</xsl:template>

<xsl:template name="Other_1" >
	<xsl:choose>

	<xsl:when test=" key('SUB_CATEGORY', 'Fee Remissions')  ">
		1
	</xsl:when>

	<xsl:when test=" key('SUB_CATEGORY', 'Subject Payments')  ">
		2
	</xsl:when>

	<xsl:when test=" key('CATEGORY_Other', 'Fellowships') ">
		3
	</xsl:when>
	
	<!-- 4/19/2005 dterret: Modular adjustment addition to Other Expenses. -->
	<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
		/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '1']/MODULAR_ADJUSTMENT">
		4
	</xsl:when>
		
	<xsl:otherwise>
		5
	</xsl:otherwise>
	
	</xsl:choose>
</xsl:template>

<xsl:template name="Other_2" >
	<xsl:choose>

	<xsl:when test=" key('SUB_CATEGORY', 'Subject Payments')  ">
		2
	</xsl:when>

	<xsl:when test=" key('CATEGORY_Other', 'Fellowships') ">
		3
	</xsl:when>
		
	<!-- 4/19/2005 dterret: Modular adjustment addition to Other Expenses. -->
	<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
		/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '1']/MODULAR_ADJUSTMENT">
		4
	</xsl:when>	
	
	<xsl:otherwise>
		5
	</xsl:otherwise>
	
	</xsl:choose>
</xsl:template>

<xsl:template name="Other_3" >
	<xsl:choose>

	<xsl:when test=" key('CATEGORY_Other', 'Fellowships') ">
		3
	</xsl:when>

	<!-- 4/19/2005 dterret: Modular adjustment addition to Other Expenses. -->
	<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
		/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '1']/MODULAR_ADJUSTMENT">
		4
	</xsl:when>		
	
	<xsl:otherwise>
		5	
	</xsl:otherwise>
	
	</xsl:choose>
</xsl:template>

<!-- 4/25/2005 dterret: Modular adjustment addition to Other Expenses. -->
<xsl:template name="Other_4">
	<xsl:choose>
		<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
			/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = '1']/MODULAR_ADJUSTMENT">
			4
		</xsl:when>
		<xsl:otherwise>
			5
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="OtherTextControl" >
	<xsl:param name="n" />

	<xsl:variable name="Var_1" >
		<xsl:call-template name="Other_1" /><fo:block padding="1mm" />
	</xsl:variable>

	<xsl:variable name="Var_2" >
		<xsl:call-template name="Other_2" /><fo:block padding="1mm" />
	</xsl:variable>

	<xsl:variable name="Var_3" >
		<xsl:call-template name="Other_3" /><fo:block padding="1mm" />
	</xsl:variable>
	
	<!-- 4/25/2005 dterret: Modular adjustment addition to Other Expenses. -->
	<xsl:variable name="Var_4" >
		<xsl:call-template name="Other_4" /><fo:block padding="1mm" />
	</xsl:variable>


<xsl:if test="$n = 1" >
	<xsl:choose>
		<xsl:when test="$Var_1 = 1 ">
			Fee Remissions
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 ">
			Subject Payments
		</xsl:when>
	
		<xsl:when test="$Var_1 = 3 ">
			Fellowships
		</xsl:when>

		<!-- 4/19/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 4 ">
			Modular Adjustment
		</xsl:when>
	
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>

<!--  [2]   ******************************************************************************* BBBBBBBBBB  -->


<xsl:if test="$n = 2" >
	<xsl:choose>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2  ">
			Subject Payments
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 ">
			Fellowships
		</xsl:when>
		
		<!-- 4/25/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			Modular Adjustment
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 ">
			Fellowships
		</xsl:when>
		
		<!-- 4/25/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			Modular Adjustment
		</xsl:when>
			
		<xsl:when test="$Var_1 = 2  and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			Modular Adjustment
		</xsl:when>
		
		<!-- 4/25/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<!-- 6/29/2005 dterret: Bug fix for incorrect name in B4070. -->
		<xsl:when test="$Var_1 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
				
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>

<xsl:if test="$n = 3" >
	<xsl:choose>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3  ">
			Fellowships
		</xsl:when>
		
		<!-- 4/25/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4 ">
			Modular Adjustment
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
	
		<!-- 4/25/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_3 = 4">
			Modular Adjustment
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_3 = 3 and $Var_4 = 4">
			Modular Adjustment
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
				
		<!-- 4/25/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4">
			Modular Adjustment
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 4  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>


<xsl:if test="$n = 4" >
	<xsl:choose>
		<!-- 4/25/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4">
			Modular Adjustment
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 4  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>


<xsl:if test="$n = 5" >
	<xsl:choose>
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 4  ">			
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>		
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>


<xsl:if test="$n = 6" >
	<xsl:choose>
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 4  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>


<xsl:if test="$n = 7" >
	<xsl:choose>
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 4  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>

<xsl:if test="$n = 8" >
	<xsl:choose>
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 4  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="8" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>

<xsl:if test="$n = 9" >
	<xsl:choose>
	
<!-- 	
	$Var_1 = 1 means there are Fee Remissions.
	$Var_1 = 2 means there are no Fee Remissions, but there are Subject Payments.
	$Var_1 = 3 means there are no Fee Remissions or Subject Payments, but there are Fellowships.
	$Var_1 = 4 means there are no Fee Remissions, Subject Payments, or Fellowships.
	$Var_1 = 2 means there are Subject Payments, but says nothing about Fee Remissons.
	etc., etc.
-->

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4 and
			($Other_Count + $Participant_Count) = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4 and
			($Other_Count + $Participant_Count) &gt; 5">
			See Justification
		</xsl:when>
		
<!-- When the first three spaces contain aggregates, and the sum of filtered Other Expenses 
plus Participant expenses is equal to 5, then call Participant_Include -->

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and 
			($Other_Count + $Participant_Count) = 6 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and 
			($Other_Count + $Participant_Count) &gt; 6 ">
			See Justification
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4 and 
			($Other_Count + $Participant_Count) = 6 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4 and 
			($Other_Count + $Participant_Count) &gt; 6 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5 and 
			($Other_Count + $Participant_Count) = 7 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5 and 
			($Other_Count + $Participant_Count) &gt; 7 ">
			See Justification
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4 and
			($Other_Count + $Participant_Count) = 6 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4 and 
			($Other_Count + $Participant_Count) &gt; 6 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5 and
			($Other_Count + $Participant_Count) = 7 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5 and 
			($Other_Count + $Participant_Count) &gt; 7 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 and 
			($Other_Count + $Participant_Count) = 7 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 and 
			($Other_Count + $Participant_Count) &gt; 7 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 and 
			($Other_Count + $Participant_Count) = 8 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="8" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 and 
			($Other_Count + $Participant_Count) &gt; 8 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_2 = 3 and $Var_4 = 4 and 
			$Other_Count + $Participant_Count = 6 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_2 = 3 and $Var_4 = 4 and 
			$Other_Count + $Participant_Count &gt; 6 ">
			See Justification
		</xsl:when>

		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4 and 
			($Other_Count + $Participant_Count) = 6 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 2 and  $Var_3 = 3 and $Var_4 = 4 and
			($Other_Count + $Participant_Count) &gt; 6 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and
			($Other_Count + $Participant_Count) = 7 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and  $Var_3 = 3 and
			($Other_Count + $Participant_Count) &gt; 7 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_2 = 3 and $Other_Count + $Participant_Count = 7 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 2 and $Var_2 = 4 and $Other_Count + $Participant_Count = 8 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="8" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 2 and $Var_2 = 4 and $Other_Count + $Participant_Count &gt; 8 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and  $Var_3 = 4 and 
			($Other_Count + $Participant_Count) = 7 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 and $Other_Count + $Participant_Count &gt; 7 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and  $Var_3 = 5 and 
			($Other_Count + $Participant_Count) = 8 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="8" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 and $Other_Count + $Participant_Count &gt; 8 ">
			See Justification
		</xsl:when>

		<xsl:when test="$Var_1 = 3 and $Var_4 = 4 and ($Other_Count + $Participant_Count) = 7 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5 and ($Other_Count + $Participant_Count) = 8 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="8" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 3 and ($Other_Count + $Participant_Count) &gt; 7 ">
			See Justification
		</xsl:when>

		<xsl:when test="$Var_1 = 4 and ($Other_Count + $Participant_Count) = 8 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="8" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 4  and ($Other_Count + $Participant_Count) &gt; 8 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 5  and ($Other_Count + $Participant_Count) = 9 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="9" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 5  and ($Other_Count + $Participant_Count) &gt; 9 ">
			See Justification
		</xsl:when>
		
		<xsl:otherwise>
		
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>

</xsl:template>



<xsl:template name="OtherAmountControl" >
	<xsl:param name="n" />

	<xsl:variable name="Var_1" >
		<xsl:call-template name="Other_1" /><fo:block padding="1mm" />
	</xsl:variable>

	<xsl:variable name="Var_2" >
		<xsl:call-template name="Other_2" /><fo:block padding="1mm" />
	</xsl:variable>

	<xsl:variable name="Var_3" >
		<xsl:call-template name="Other_3" /><fo:block padding="1mm" />
	</xsl:variable>
	
	<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses.-->
	<xsl:variable name="Var_4" >
		<xsl:call-template name="Other_4" /><fo:block padding="1mm" />
	</xsl:variable>


<xsl:if test="$n = 1" >
	<xsl:choose>
		<xsl:when test="$Var_1 = 1  ">
			<xsl:value-of select="format-number(sum( key('SUB_CATEGORY', 'Fee Remissions')
				/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 ">
			<xsl:value-of select="format-number(sum( key('SUB_CATEGORY', 'Subject Payments')
				/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
	
		<xsl:when test="$Var_1 = 3 ">
			<xsl:value-of select="format-number(sum( key('CATEGORY', 'Fellowships')
				/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses.-->
		<xsl:when test="$Var_1 = 4 ">
			<xsl:value-of select="$S1_ModularAdjustment_Display"/>
		</xsl:when>
		
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>

<!--  [2]   *********************************************************** BBBBBBBBBB  -->

<xsl:if test="$n = 2" >
	<xsl:choose>	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2  ">
			<xsl:value-of select="format-number(sum( key('SUB_CATEGORY', 'Subject Payments')
				/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 3  ">
			<xsl:value-of select="format-number(sum( key('CATEGORY', 'Fellowships')
				/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			<xsl:value-of select="$S1_ModularAdjustment_Display"/>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3  ">
			<xsl:value-of select="format-number(sum( key('CATEGORY', 'Fellowships')
				/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:value-of select="$S1_ModularAdjustment_Display"/>
		</xsl:when>
				
		<xsl:when test="$Var_1 = 2  and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>

		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			<xsl:value-of select="$S1_ModularAdjustment_Display"/>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 4 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:otherwise>
	
	</xsl:choose>
</xsl:if>

<xsl:if test="$n = 3" >
	<xsl:choose>	
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3  ">
			<xsl:value-of select="format-number(sum( key('CATEGORY', 'Fellowships')
				/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4 ">
			<xsl:value-of select="$S1_ModularAdjustment_Display"/>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
	
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4">
			<xsl:value-of select="$S1_ModularAdjustment_Display"/>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4 ">
			<xsl:value-of select="$S1_ModularAdjustment_Display"/>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 4  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	
</xsl:if>

<xsl:if test="$n = 4" >
	<xsl:choose>
	
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:value-of select="$S1_ModularAdjustment_Display"/>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- KULERA-794: Added missing NIH-398 Other Expenses clause. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 4  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>

<xsl:if test="$n = 5" >
	<xsl:choose>
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3  and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 2 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 4  ">			
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>

<xsl:if test="$n = 6" >
	<xsl:choose>
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 2 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
			
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 4  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>

<xsl:if test="$n = 7" >
	<xsl:choose>
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
				
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 2 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
			
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 4  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>

<xsl:if test="$n = 8" >
	<xsl:choose>
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 2 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
	
		<xsl:when test="$Var_1 = 4  ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:otherwise>
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="8" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:if>
	
<xsl:if test="$n = 9" >
	<xsl:choose>
	
<!-- 	
	$Var_1 = 1 means there are Fee Remissions.
	$Var_1 = 2 means there are no Fee Remissions, but there are Subject Payments.
	$Var_1 = 3 means there are no Fee Remissions or Subject Payments, but there are Fellowships.
	$Var_1 = 4 means there are no Fee Remissions, Subject Payments, or Fellowships.
	$Var_1 = 2 means there are Subject Payments, but says nothing about Fee Remissons.
	etc., etc.
-->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4 and 
			($Other_Count + $Participant_Count) = 5">
			<xsl:call-template name="Participant_Include_Amount">
				<xsl:with-param name="index" select="5"/>
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4 and 
			($Other_Count + $Participant_Count) &gt; 5 and $Participant_Count &lt; 5 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 4 - $Participant_Count]/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4 and 
			($Other_Count + $Participant_Count) &gt; 5 and $Participant_Count &gt;= 5">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt;= 5]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
<!-- When the first three spaces contain aggregates, and the sum of filtered Other Expenses 
		 plus Participant expenses is equal to 6, then call Participant_Include_Amount -->

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and 
			($Other_Count + $Participant_Count) = 6 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>

<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  
   		When the first three spaces contain aggregates, and the sum of filtered Other Expenses 
			plus Participant expenses is equal to or greater than 7, and no Participant Expenses remain, 
			we sum the remaining Other Expenses  Otherwise, we add the remaining Participant Expenses 
			to to whatever Other Expenses exist.
-->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Other_Count 
			+ $Participant_Count &gt; 6 and ($Participant_Count &lt; 5 or $Participant_Count = 5) ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 5 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Other_Count 
			+ $Participant_Count &gt; 6 and $Participant_Count &gt; 5 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 5]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>

		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4 and 
			($Other_Count + $Participant_Count) = 6 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4 and 
			($Other_Count + $Participant_Count) &gt; 6 and $Participant_Count &lt;= 5 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 5 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4  and 
			($Other_Count + $Participant_Count) &gt; 6 and $Participant_Count &gt; 5 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 5]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5 and 
			($Other_Count + $Participant_Count) = 7 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5 and 
			($Other_Count + $Participant_Count) &gt; 7 and $Participant_Count &lt;= 6 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 6 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  and 
			($Other_Count + $Participant_Count) &gt; 7 and $Participant_Count &gt; 6 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 6]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	


		<xsl:when test="$Var_1 = 1 and $Var_2 = 3  and $Var_4 = 4 and ($Other_Count + 
			$Participant_Count) = 6 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4 and ($Other_Count + 
			$Participant_Count) &gt; 6 and $Participant_Count &lt;= 5 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 5 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4 and ($Other_Count + 
			$Participant_Count) &gt; 6 and $Participant_Count &gt; 5 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 5]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3  and $Var_4 = 5 and ($Other_Count + 
			$Participant_Count) = 7 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5 and ($Other_Count + 
			$Participant_Count) &gt; 7 and $Participant_Count &lt;= 6 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 6 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5 and ($Other_Count + 
			$Participant_Count) &gt; 7 and $Participant_Count &gt; 6 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 6]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 1 and $Var_2 = 4  and ($Other_Count + $Participant_Count) = 7">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 and ($Other_Count + $Participant_Count) &gt; 7 
			and $Participant_Count &lt;= 6 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 6 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 and ($Other_Count + $Participant_Count) &gt; 7 
			and $Participant_Count &gt; 6 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 6]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5  and ($Other_Count + $Participant_Count) = 8">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="8" />
			</xsl:call-template>
		</xsl:when>
			
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 and ($Other_Count + $Participant_Count) &gt; 8 
			and $Participant_Count &lt;= 7 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 7 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 and ($Other_Count + $Participant_Count) &gt; 8 
			and $Participant_Count &gt; 7 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 7]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
			
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ alfred  -->	

		<xsl:when test="$Var_1 = 2 and  $Var_3 = 3  and $Other_Count + $Participant_Count = 6">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:when>
		
		
<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	
		<xsl:when test="$Var_1 = 2  and $Var_3 = 3 and $Var_4 = 4 and ($Other_Count + 
			$Participant_Count) &gt; 6 and $Participant_Count &lt;= 6 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 6 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2  and $Var_3 = 3 and $Var_4 = 5 and ($Other_Count + 
			$Participant_Count) &gt; 6 and $Participant_Count &lt;= 6 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 6 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<xsl:when test="$Var_1 = 2  and $Var_3 = 3 and ($Other_Count + $Participant_Count) &gt; 5 
			and $Participant_Count &lt;= 5 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 5 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and ($Other_Count + $Participant_Count) &gt; 6 
			and $Participant_Count &gt; 6 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 6]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 2 and  $Var_3 = 4  and $Other_Count + $Participant_Count = 8">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="8" />
			</xsl:call-template>
		</xsl:when>
		
<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 2 and  $Var_3 = 4 and $Other_Count + $Participant_Count &gt; 8 
			and ($Participant_Count &lt; 7 or $Participant_Count = 7) ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 7 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 and $Other_Count + $Participant_Count &gt; 8 
			and $Participant_Count &gt; 7 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 7]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 and ($Other_Count + $Participant_Count) = 8">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="8" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 and ($Other_Count + $Participant_Count) &gt; 8 
			and $Participant_Count &lt;= 7">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 7 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 and ($Other_Count + $Participant_Count) &gt; 8 
			and $Participant_Count &gt; 7 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 7]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
	
		<!-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 2 and $Var_2 = 3  and $Other_Count + $Participant_Count = 7">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
				
<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 2 and $Var_2 = 3 and $Other_Count + $Participant_Count &gt; 7 
			and ($Participant_Count &lt; 6 or $Participant_Count = 6) ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 6 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 and $Var_2 = 3 and $Other_Count + $Participant_Count &gt; 7 
			and $Participant_Count &gt; 6 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 6]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 2 and $Var_2 = 4  and $Other_Count + $Participant_Count = 8">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 2 and $Var_2 = 4 and $Other_Count + $Participant_Count &gt; 8 
			and ($Participant_Count &lt; 7 or $Participant_Count = 7) ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 7 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
	
		<xsl:when test="$Var_1 = 2 and $Var_2 = 4 and $Other_Count + $Participant_Count &gt; 8 
			and $Participant_Count &gt; 7 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 7]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4 and $Other_Count + $Participant_Count = 7">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="7" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4 and ($Other_Count + $Participant_Count) &gt; 7 and 
			$Participant_Count &lt;= 6">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 6 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 4 and ($Other_Count + $Participant_Count) &gt; 7 and 
			$Participant_Count &gt; 6 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 6]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 3 and $Var_4 = 5 and ($Other_Count + $Participant_Count) = 8">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="8" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 3 and $Var_4 = 5 and ($Other_Count + $Participant_Count) &gt; 8 and 
			$Participant_Count &lt;= 7">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 7 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
	
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5 and ($Other_Count + $Participant_Count) &gt; 8 and 
			$Participant_Count &gt; 7 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 7]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	

		<!-- 6/29/2005 dterret: Bug fix for ninth amount not printing in B4070 (PRD). -->
		<xsl:when test="$Var_1 = 4 and ($Other_Count + $Participant_Count) = 8">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="8" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	

		<xsl:when test="$Var_1 = 4 and ($Other_Count + $Participant_Count) &gt; 8 and 
			$Participant_Count &lt;= 7 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 7 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
	
		<xsl:when test="$Var_1 = 4 and ($Other_Count + $Participant_Count) &gt; 8 and 
			$Participant_Count &gt; 7 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 7]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	
		
		<xsl:when test="$Var_1 = 5 and $Other_Count + $Participant_Count = 9">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="9" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->	
		
		<xsl:when test="$Var_1 = 5 and ($Other_Count + $Participant_Count) &gt; 9 and 
			$Participant_Count &lt;= 8 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 8 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 5 and $Other_Count + $Participant_Count &gt; 9 and 
			$Participant_Count &gt; 8 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 8]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->	
	
		<xsl:otherwise>

		</xsl:otherwise>
	</xsl:choose>
</xsl:if>
</xsl:template>

<xsl:template name="Consultants_Description" >
	<xsl:param name="n" />
	
	<xsl:for-each select="key('CATEGORY', 'Consultants')[AGENCY_REQUEST_AMOUNT &gt; 0]" >
		<xsl:if test="position() = $n" >
			<xsl:value-of select="DESCRIPTION" />
		</xsl:if>
	</xsl:for-each>

</xsl:template>

<xsl:template name="Consultants_Amount" >
	<xsl:param name="n" />
	
	<xsl:for-each select="key('CATEGORY', 'Consultants')[AGENCY_REQUEST_AMOUNT &gt; 0]" >
		<xsl:if test="position() = $n" >
			<xsl:value-of select="format-number(AGENCY_REQUEST_AMOUNT, '###,###')" />
		</xsl:if>
	</xsl:for-each>

</xsl:template>

<xsl:template name="Equipment_Description" >
	<xsl:param name="n" />
	
	<xsl:for-each select="key('CATEGORY', 'Equipment')[AGENCY_REQUEST_AMOUNT &gt; 0]" >
		<xsl:if test="position() = $n" >
			<xsl:value-of select="DESCRIPTION" />
		</xsl:if>
	</xsl:for-each>

</xsl:template>

<xsl:template name="Equipment_Amount" >
	<xsl:param name="n" />
	
	<xsl:for-each select="key('CATEGORY', 'Equipment')[AGENCY_REQUEST_AMOUNT &gt; 0]" >
		<xsl:if test="position() = $n" >
			<xsl:value-of select="format-number(AGENCY_REQUEST_AMOUNT, '###,###')" />
		</xsl:if>
	</xsl:for-each>

</xsl:template>

<xsl:template name="Travel_Description" >
	<xsl:param name="n" />
	
	<xsl:for-each select="key('CATEGORY', 'Travel')[AGENCY_REQUEST_AMOUNT &gt; 0]" >
		<xsl:if test="position() = $n" >
			<xsl:value-of select="DESCRIPTION" />
		</xsl:if>
	</xsl:for-each>

</xsl:template>

<xsl:template name="Travel_Amount" >
	<xsl:param name="n" />
	
	<xsl:for-each select="key('CATEGORY', 'Travel')[AGENCY_REQUEST_AMOUNT &gt; 0]" >
		<xsl:if test="position() = $n" >
			<xsl:value-of select="format-number(AGENCY_REQUEST_AMOUNT, '###,###')" />
		</xsl:if>
	</xsl:for-each>

</xsl:template>

<xsl:template name="Supplies_Description" >
	<xsl:param name="n" />
	
	<xsl:for-each select="key('CATEGORY', 'Supplies')[AGENCY_REQUEST_AMOUNT &gt; 0]" >
		<xsl:if test="position() = $n" >
			<xsl:value-of select="DESCRIPTION" />
		</xsl:if>
	</xsl:for-each>

</xsl:template>

<xsl:template name="Supplies_Amount" >
	<xsl:param name="n" />
	
	<xsl:for-each select="key('CATEGORY', 'Supplies')[AGENCY_REQUEST_AMOUNT &gt; 0]" >
		<xsl:if test="position() = $n" >
			<xsl:value-of select="format-number(AGENCY_REQUEST_AMOUNT, '###,###')" />
		</xsl:if>
	</xsl:for-each>

</xsl:template>

<xsl:template name="Alterations_Description" >
	<xsl:param name="n" />
	
	<xsl:for-each select="key('SUB_CATEGORY', 'Alterations and Renovations')[AGENCY_REQUEST_AMOUNT &gt; 0]" >
		<xsl:if test="position() = $n" >
			<xsl:value-of select="DESCRIPTION" />
		</xsl:if>
	</xsl:for-each>

</xsl:template>

<xsl:template name="Alterations_Amount" >
	<xsl:param name="n" />
	
	<xsl:for-each select="key('SUB_CATEGORY', 'Alterations and Renovations')[AGENCY_REQUEST_AMOUNT &gt; 0]" >
		<xsl:if test="position() = $n" >
			<xsl:value-of select="format-number(AGENCY_REQUEST_AMOUNT, '###,###')" />
		</xsl:if>
	</xsl:for-each>

</xsl:template>

<xsl:template name="Other_Description" >
	<xsl:param name="n" />	
	<xsl:for-each select="key('CATEGORY', 'Other')[AGENCY_REQUEST_AMOUNT &gt; 0]" >
		<xsl:if test="position() = $n" >
			<xsl:value-of select="DESCRIPTION" />
		</xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template name="Other_Amount" >
	<xsl:param name="n" />	
	<xsl:for-each select="key('CATEGORY', 'Other')[AGENCY_REQUEST_AMOUNT &gt; 0]" >
		<xsl:if test="position() = $n" >
			<xsl:value-of select="format-number(AGENCY_REQUEST_AMOUNT, '###,###')" />
		</xsl:if>
	</xsl:for-each>
</xsl:template>


<xsl:template name="Participant_Include" >
	<xsl:param name="index" />
	<xsl:choose>
		<xsl:when test="$index &lt; $Participant_Count or $index = $Participant_Count ">
			<xsl:value-of select="key('CATEGORY_Filtered', 'Participant Expenses')[$index]/DESCRIPTION" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="key('CATEGORY_Filtered', 'Other Expenses')[$index - $Participant_Count]/DESCRIPTION" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="Participant_Include_Amount" >
	<xsl:param name="index" />
		<xsl:choose>		
			<xsl:when test="$index &lt; $Participant_Count or $index = $Participant_Count ">
				<xsl:value-of select="format-number(key('CATEGORY_Participant_Filtered', 
					'Participant Expenses')[$index]/AGENCY_REQUEST_AMOUNT, '###,###')" />
			</xsl:when>
			<xsl:when test="key('CATEGORY_Filtered', 'Other Expenses')[$index - $Participant_Count]
				/AGENCY_REQUEST_AMOUNT">
				<xsl:value-of select="format-number(key('CATEGORY_Filtered', 'Other Expenses')
					[$index - $Participant_Count]/AGENCY_REQUEST_AMOUNT, '###,###')" />
			</xsl:when>			
		<xsl:otherwise>
		
		</xsl:otherwise>		
	</xsl:choose>
</xsl:template>

<xsl:template name="Filter_Zeros_Consultants_Num" >
	<xsl:param name="n" />
	<xsl:param name="zero_total" />
	<xsl:param name="non_zero_total" />	
	<xsl:param name="k" />	
	
	<xsl:param name="Temp_Zero" >
		<xsl:choose>
				<xsl:when test="key('CATEGORY', 'Consultants')[$k][AGENCY_REQUEST_AMOUNT = 0]">
					1
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
		</xsl:choose>
	</xsl:param>
	
		
	<xsl:param name="Temp_Non_Zero" >
		<xsl:choose>
				<xsl:when test="key('CATEGORY', 'Consultants')[$k][AGENCY_REQUEST_AMOUNT &gt; 0]">
					1
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
		</xsl:choose>
	</xsl:param>
	
	
	<xsl:if test="$non_zero_total &lt; $n" >
		<xsl:call-template name="Filter_Zeros_Consultants_Num" >
			<xsl:with-param name="zero_total" select="$zero_total + $Temp_Zero" />
			<xsl:with-param name="non_zero_total" select="$non_zero_total + $Temp_Non_Zero" />			
			<xsl:with-param name="k" select="$k + 1" />		
			<xsl:with-param name="n" select="$n" />					
		</xsl:call-template>
	</xsl:if>
	
	<xsl:if test="$non_zero_total = $n" >
		<xsl:value-of select="$zero_total" />
	</xsl:if>
	
</xsl:template>

<xsl:template name="Filter_Zeros_Equipment_Num" >
	<xsl:param name="n" />
	<xsl:param name="zero_total" />
	<xsl:param name="non_zero_total" />	
	<xsl:param name="k" />	
	
	<xsl:param name="Temp_Zero" >
		<xsl:choose>
				<xsl:when test="key('CATEGORY', 'Equipment')[$k][AGENCY_REQUEST_AMOUNT = 0]">
					1
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
		</xsl:choose>
	</xsl:param>
	
		
	<xsl:param name="Temp_Non_Zero" >
		<xsl:choose>
				<xsl:when test="key('CATEGORY', 'Equipment')[$k][AGENCY_REQUEST_AMOUNT &gt; 0]">
					1
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
		</xsl:choose>
	</xsl:param>
	
	
	<xsl:if test="$non_zero_total &lt; $n" >
		<xsl:call-template name="Filter_Zeros_Equipment_Num" >
			<xsl:with-param name="zero_total" select="$zero_total + $Temp_Zero" />
			<xsl:with-param name="non_zero_total" select="$non_zero_total + $Temp_Non_Zero" />			
			<xsl:with-param name="k" select="$k + 1" />		
			<xsl:with-param name="n" select="$n" />					
		</xsl:call-template>
	</xsl:if>
	
	<xsl:if test="$non_zero_total = $n" >
		<xsl:value-of select="$zero_total" />
	</xsl:if>
	
</xsl:template>

<xsl:template name="Filter_Zeros_Supplies_Num" >
	<xsl:param name="n" />
	<xsl:param name="zero_total" />
	<xsl:param name="non_zero_total" />	
	<xsl:param name="k" />	
	
	<xsl:param name="Temp_Zero" >
		<xsl:choose>
				<xsl:when test="key('CATEGORY', 'Supplies')[$k][AGENCY_REQUEST_AMOUNT = 0]">
					1
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
		</xsl:choose>
	</xsl:param>
	
		
	<xsl:param name="Temp_Non_Zero" >
		<xsl:choose>
				<xsl:when test="key('CATEGORY', 'Supplies')[$k][AGENCY_REQUEST_AMOUNT &gt; 0]">
					1
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
		</xsl:choose>
	</xsl:param>
	
	<xsl:if test="$non_zero_total &lt; $n" >
		<xsl:call-template name="Filter_Zeros_Supplies_Num" >
			<xsl:with-param name="zero_total" select="$zero_total + $Temp_Zero" />
			<xsl:with-param name="non_zero_total" select="$non_zero_total + $Temp_Non_Zero" />			
			<xsl:with-param name="k" select="$k + 1" />		
			<xsl:with-param name="n" select="$n" />					
		</xsl:call-template>
	</xsl:if>
	
	<xsl:if test="$non_zero_total = $n" >
		<xsl:value-of select="$zero_total" />
	</xsl:if>
	
</xsl:template>

<xsl:template name="Filter_Zeros_Travel_Num" >
	<xsl:param name="n" />
	<xsl:param name="zero_total" />
	<xsl:param name="non_zero_total" />	
	<xsl:param name="k" />	
	
	<xsl:param name="Temp_Zero" >
		<xsl:choose>
				<xsl:when test="key('CATEGORY', 'Travel')[$k][AGENCY_REQUEST_AMOUNT = 0]">
					1
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
		</xsl:choose>
	</xsl:param>
	
		
	<xsl:param name="Temp_Non_Zero" >
		<xsl:choose>
				<xsl:when test="key('CATEGORY', 'Travel')[$k][AGENCY_REQUEST_AMOUNT &gt; 0]">
					1
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
		</xsl:choose>
	</xsl:param>
	
	
	<xsl:if test="$non_zero_total &lt; $n" >
		<xsl:call-template name="Filter_Zeros_Travel_Num" >
			<xsl:with-param name="zero_total" select="$zero_total + $Temp_Zero" />
			<xsl:with-param name="non_zero_total" select="$non_zero_total + $Temp_Non_Zero" />			
			<xsl:with-param name="k" select="$k + 1" />		
			<xsl:with-param name="n" select="$n" />					
		</xsl:call-template>
	</xsl:if>
	
	<xsl:if test="$non_zero_total = $n" >
		<xsl:value-of select="$zero_total" />
	</xsl:if>
	
</xsl:template>

<xsl:template name="Filter_Zeros_Alterations_Num" >
	<xsl:param name="n" />
	<xsl:param name="zero_total" />
	<xsl:param name="non_zero_total" />	
	<xsl:param name="k" />	
	
	<xsl:param name="Temp_Zero" >
		<xsl:choose>
				<xsl:when test="key('SUB_CATEGORY', 'Alterations and Renovations')[$k][AGENCY_REQUEST_AMOUNT = 0]">
					1
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
		</xsl:choose>
	</xsl:param>	
		
	<xsl:param name="Temp_Non_Zero" >
		<xsl:choose>
				<xsl:when test="key('SUB_CATEGORY', 'Alterations and Renovations')[$k][AGENCY_REQUEST_AMOUNT &gt; 0]">
					1
				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
		</xsl:choose>
	</xsl:param>	
	
	<xsl:if test="$non_zero_total &lt; $n" >
		<xsl:call-template name="Filter_Zeros_Alterations_Num" >
			<xsl:with-param name="zero_total" select="$zero_total + $Temp_Zero" />
			<xsl:with-param name="non_zero_total" select="$non_zero_total + $Temp_Non_Zero" />			
			<xsl:with-param name="k" select="$k + 1" />		
			<xsl:with-param name="n" select="$n" />					
		</xsl:call-template>
	</xsl:if>
	
	<xsl:if test="$non_zero_total = $n" >
		<xsl:value-of select="$zero_total" />
	</xsl:if>
	
</xsl:template>

</xsl:stylesheet> 
