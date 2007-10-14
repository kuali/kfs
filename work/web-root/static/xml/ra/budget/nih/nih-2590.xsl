<?xml version="1.0"?>
<!--
 Copyright 2006 The Kuali Foundation.
 
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
    - 4/28/2005 dterret: In the XML, this field is an overloaded place to pass in parameters.  
	  The default is actually 'low', 'medium', or 'high'.  When output is generated in the application 
	  with the appropriate choices made on the form, the period number will be present in the 
	  XML generated, in this attribute.
    - 05/2006: pcberg@indiana.edu, refactoring for KRA.
               - OUTPUT_DETAIL_LEVEL = PARAMETER1
               - @PERIOD = @PERIOD_NUMBER
               - Added variables baseUrl and imageArrow.
               - CREATE_TIMESTAMP = SEQUENCE_NUMBER
  -->
	
<xsl:variable name="Period_Number" select="/PROPOSAL/BUDGET/@PARAMETER1" />
<xsl:variable name="baseUrl" select="/PROPOSAL/BUDGET/@BASE_URL"/>
<xsl:variable name="imageArrow" select="'/images-xslt/arrow.gif'"/>

  <!--
 Alterations and Renovations is a subcategory of Other Category.  Page 4 of output does has a separate space for Alterations and Renovations.  Adding "not(SUB_CATEGORY = 'Alterations and Renovations')" was a convenient hack, but applying the test for categories other than "Other" is unnecessary.
 -->
<xsl:key name="CATEGORY_Other" match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/NON_PERSONNEL/NON_PERSONNEL_ITEM[not(SUB_CATEGORY = 'Alterations and Renovations')] " use="CATEGORY" />


<!-- The "Other" space on page 4 does not itemize Subject Payments, Fee Remissions, or Remissions -->

<xsl:key name="CATEGORY_Filtered" match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number ]/NON_PERSONNEL/NON_PERSONNEL_ITEM[not(SUB_CATEGORY = 'Alterations and Renovations' or SUB_CATEGORY = 'Subject Payments' or SUB_CATEGORY = 'Fee Remissions' or CATEGORY = 'Fellowships'  or AGENCY_REQUEST_AMOUNT = 0)] " use="CATEGORY" />

<!--  If AGENCY_REQUEST_AMOUNT = 0, there should be no output.    -->

<xsl:key name="CATEGORY_Participant_Filtered" match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number ]/NON_PERSONNEL/NON_PERSONNEL_ITEM[not(AGENCY_REQUEST_AMOUNT = 0)] " use="CATEGORY" />

<xsl:variable name="PERSON_VAR" select="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON" />

<xsl:key name="CATEGORY" match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/NON_PERSONNEL/NON_PERSONNEL_ITEM" use="CATEGORY" />

<xsl:key name="SUB_CATEGORY" match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/NON_PERSONNEL/NON_PERSONNEL_ITEM" use="SUB_CATEGORY" />

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

<xsl:key name="PERIOD" match="PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD" use="@PERIOD_NUMBER" />

<xsl:variable name="FilteredPersonCount" select="count($PERSON_OTHER) " />

<xsl:variable name="Other_Count" select="count(key('CATEGORY_Filtered', 'Other Expenses')) " />

<xsl:variable name="Participant_Count" select="count(key('CATEGORY_Participant_Filtered', 'Participant Expenses')) " />

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
		<xsl:value-of select="count(key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = 'Consultants'])"/>
	</xsl:variable>

	<xsl:variable name="SuppliesCount_P1">
		<xsl:value-of select="count(key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = 'Supplies'])"/>
	</xsl:variable>

	<xsl:variable name="EquipmentCount_P1">
		<xsl:value-of select="count(key('CATEGORY', 'Equipment' ))"/>
	</xsl:variable>

	<xsl:variable name="OtherExpenseCount_P1">
		<xsl:value-of select="count(key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = 'Other Expenses'])"/>
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

<xsl:variable name="Fee_Total" select="sum(key('SUB_CATEGORY', 'Fee Remissions')/AGENCY_REQUEST_AMOUNT)" />

<xsl:variable name="Subject_Total" select="sum(key('SUB_CATEGORY', 'Subject Payments')/AGENCY_REQUEST_AMOUNT)" />

      <xsl:variable name="S1X_TotalDirect" >
	      <xsl:choose>
			<xsl:when test="
     /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/@TOTAL_AGENCY_REQUEST_DIRECT_COST &gt; 0">

<xsl:value-of select="format-number(sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/@TOTAL_AGENCY_REQUEST_DIRECT_COST), '###,###')" />

			</xsl:when>
			<xsl:otherwise>

			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1_TotalDirect" >
	      <xsl:choose>
			<xsl:when test="
     /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/@TOTAL_AGENCY_REQUEST_DIRECT_COST">

<xsl:value-of select=" sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/@TOTAL_AGENCY_REQUEST_DIRECT_COST)" />

			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1X_Subcontractors" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT), '###,###')" />

			</xsl:when>
			<xsl:otherwise>

			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1_Subcontractors" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Subcontractors' and not(SUB_CATEGORY = 'Subcontractor IDC - first $25K') and not(SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT)" />

			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1_Fringe" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', $Period_Number)/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT">

<xsl:value-of select="sum(key('PERIOD', $Period_Number)/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT)" />

			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1_Salary" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', $Period_Number)/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY">

<xsl:value-of select="sum( key('PERIOD', $Period_Number)/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY)" />

			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1_Hospitalization" >
	      <xsl:choose>

 			<xsl:when test="
      key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT or  key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT) + sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT) " />

			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1X_Hospitalization" >
	         <xsl:choose>
			<xsl:when test="
      key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Hospitalization']/AGENCY_REQUEST_AMOUNT &gt; 0 or  key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: In-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">

<xsl:value-of select="format-number($S1_Hospitalization , '###,###')" />
			</xsl:when>
			<xsl:otherwise>

			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1_IDC" >
	      <xsl:choose>

			<xsl:when test=" key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT) " />
			</xsl:when>

			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1X_IDC" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY = 'Subcontractor IDC - first $25K' or SUB_CATEGORY = 'Subcontractor IDC - over $25K']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S1_IDC, '###,###')" />
			</xsl:when>
			<xsl:otherwise>

			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1_Outpatient" >
	      <xsl:choose>

			<xsl:when test=" key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT) " />
			</xsl:when>

			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1X_Outpatient" >
	      <xsl:choose>
			<xsl:when test=" key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM [SUB_CATEGORY='Patient Care: Out-Patient']/AGENCY_REQUEST_AMOUNT &gt; 0">
<xsl:value-of select="format-number($S1_Outpatient, '###,###')" />
			</xsl:when>
			<xsl:otherwise>

			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1_Alterations" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT)" />

			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1X_Alterations" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY='Alterations and Renovations']/AGENCY_REQUEST_AMOUNT), '###,###')" />

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

      <xsl:variable name="S1_Equipment" >
	      <xsl:choose>
			<xsl:when test="
   key('CATEGORY', 'Equipment')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT)" />

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

<xsl:value-of select="sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT)" />

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

<xsl:value-of select="sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT)" />

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

      <xsl:variable name="S1_OtherExpenses" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]/AGENCY_REQUEST_AMOUNT)" />

			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1_DUP_OtherExpenses" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Other Expenses' and not(SUB_CATEGORY = 'Alterations and Renovations')]/AGENCY_REQUEST_AMOUNT)" />

			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1_Supplies" >
	      <xsl:choose>
			<xsl:when test="
key('CATEGORY', 'Supplies')/AGENCY_REQUEST_AMOUNT">



<xsl:value-of select="sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Supplies']/AGENCY_REQUEST_AMOUNT)" />

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
      key('PERIOD', $Period_Number)/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT">

<xsl:value-of select="format-number(sum(key('PERIOD', $Period_Number)/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT), '###,###')" />

			</xsl:when>
			<xsl:otherwise>

			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1X_Salary" >
	      <xsl:choose>
			<xsl:when test="
      key('PERIOD', $Period_Number)/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY">

<xsl:value-of select="format-number(sum( key('PERIOD', $Period_Number)/PERSONNEL/PERSON/@AGENCY_AMOUNT_SALARY), '###,###')" />

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

      <xsl:variable name="S1X_Equipment" >
	      <xsl:choose>
			<xsl:when test="
   key('CATEGORY', 'Equipment')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Equipment']/AGENCY_REQUEST_AMOUNT), '###,###')" />

			</xsl:when>
			<xsl:otherwise>


			</xsl:otherwise>
		</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1X_Fellowships" >
	      <xsl:choose>
			<xsl:when test="
   key('CATEGORY', 'Fellowships')/AGENCY_REQUEST_AMOUNT">

<xsl:value-of select="format-number(sum( key('PERIOD', $Period_Number)/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY='Fellowships']/AGENCY_REQUEST_AMOUNT), '###,###')" />

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
	
			<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
			<xsl:variable name="S1_ModularAdjustment">
				<xsl:choose>
					<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
						/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = $Period_Number]/MODULAR_ADJUSTMENT">
						<xsl:value-of select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
							/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = $Period_Number]/MODULAR_ADJUSTMENT"/>
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
	
			<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
      <xsl:variable name="S1X_OtherExpenses" >
	      <xsl:choose>
					<xsl:when test="$S1_OtherExpenses + $S1_Fellowships + $S1_Participants + $S1_ModularAdjustment &gt; 0">
						<xsl:value-of select="format-number($S1_OtherExpenses + $S1_Fellowships + $S1_Participants 
							+ $S1_ModularAdjustment, '###,###')" />
					</xsl:when>
	    		<xsl:when test="$S1_OtherExpenses + $S1_Fellowships + $S1_Participants + $S1_ModularAdjustment &lt; 0">
						(<xsl:value-of select="format-number(substring($S1_OtherExpenses + $S1_Fellowships + $S1_Participants 
							+ $S1_ModularAdjustment,2), '###,###')" />)
	    		</xsl:when>
	    		<xsl:otherwise>
	    	
	    		</xsl:otherwise>
				</xsl:choose>
      </xsl:variable>

      <xsl:variable name="S1X_DUP_OtherExpenses" >
	      <xsl:choose>
			<xsl:when test="$S1_DUP_OtherExpenses + $S1_DUP_Fellowships + $S1_DUP_Participants &gt; 0">

<xsl:value-of select="format-number($S1_DUP_OtherExpenses + $S1_DUP_Fellowships + $S1_DUP_Participants, '###,###')" />

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
	
			<!-- 5/10/2005 dterret: Changed to add in Modular Adjustment total. -->
			<xsl:variable name="subtotalDirect_Raw" select="$S1_Salary + $S1_Fringe + 
				$S1_Consultants + $S1_Equipment + $S1_Travel + $S1_Supplies + $S1_OtherExpenses + 
				$S1_Hospitalization + $S1_Fellowships + $S1_Outpatient + $S1_Participants + 
				$S1_Alterations + $S1_ModularAdjustment"/>
			<xsl:variable name="subtotalDirect">
				<xsl:choose>
					<xsl:when test="$subtotalDirect_Raw &gt; 0">
						<xsl:value-of select="format-number($subtotalDirect_Raw, '###,###')"/>
					</xsl:when>
					<xsl:when test="$subtotalDirect_Raw &lt; 0">
						(<xsl:value-of select="format-number(substring($subtotalDirect_Raw,2), '###,###')"/>)
					</xsl:when>
					<xsl:otherwise>
					
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
	
			<xsl:variable name="totalDirect_Raw" select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
				[@PERIOD_NUMBER = $Period_Number]/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + $S1_ModularAdjustment"/>
			<xsl:variable name="totalDirect">
				<xsl:choose>
					<xsl:when test="$totalDirect_Raw &gt; 0">
						<xsl:value-of select="format-number($totalDirect_Raw, '###,###')"/>
					</xsl:when>
					<xsl:when test="$totalDirect_Raw &lt; 0">
						(<xsl:value-of select="format-number(substring($totalDirect_Raw,2), '###,###')"/>)
					</xsl:when>
					<xsl:otherwise>
					
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

<!--  %%%%%%%%%%%%START%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

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
      	<fo:table-column column-width="115mm"/>
      	<fo:table-column column-width="75mm"/>
    		<fo:table-body>

        <fo:table-row >


          <fo:table-cell>

			<fo:block font-size="8pt"
			text-align="left"
			margin-left="4cm"

			space-before="2mm"
			start-indent="4mm">

<!--  The following code puts the project director / principal investigator's name at the top of Page 4.  Similar code is used to put the name at the top of Page 5 and on the overflow personnel pages.  -->

			Principal Investigator/Program Director (Last, first, middle)
		</fo:block>
		</fo:table-cell>

          <fo:table-cell padding-right="2mm" display-align="center" font-size="10pt">
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
      <fo:table-column column-width="80mm"/>
      <fo:table-column column-width="31mm"/>
      <fo:table-column column-width="28mm"/>
       <fo:table-column column-width="51mm"/>
    <fo:table-body>

        <fo:table-row height="11.8mm">

          <fo:table-cell border-left-width="0mm" border-style="solid" text-align="center" font-size="10pt" display-align="center">
          <fo:block font-weight="bold">
	DETAILED BUDGET FOR NEXT BUDGET<fo:block />PERIOD - DIRECT COSTS ONLY
		</fo:block>
		</fo:table-cell>

          <fo:table-cell border-style="solid"  font-size="8pt" border-left-width="0mm" >

          <fo:block start-indent="1mm"  padding-before.length="1mm"><fo:inline font-weight="bold">FROM</fo:inline>
<fo:block padding-top="1.4mm"></fo:block>
<fo:inline font-size="10pt" ><xsl:value-of select="/PROPOSAL/BUDGET/PERIODS/PERIOD[PERIOD_NUMBER=$Period_Number]/START_DATE" /></fo:inline>
</fo:block >
</fo:table-cell>


          <fo:table-cell border-style="solid" font-size="8pt" border-left-width="0mm" border-right-width="0mm" start-indent="1mm">
<fo:block padding-before.length="1mm">
         <fo:inline font-weight="bold">THROUGH</fo:inline>

<fo:block padding-top="1.4mm"></fo:block>
<fo:inline font-size="10pt" ><xsl:value-of select="/PROPOSAL/BUDGET/PERIODS/PERIOD[PERIOD_NUMBER=$Period_Number]/STOP_DATE" /></fo:inline>
</fo:block >

		</fo:table-cell>



          <fo:table-cell border-style="solid" font-size="8pt" border-right-width="0mm" start-indent="1mm">
<fo:block padding-before.length="1mm">
        <fo:inline font-weight="bold" >GRANT NUMBER</fo:inline><fo:block padding="0.5mm"></fo:block>
        <fo:inline font-size="10pt" ><xsl:value-of select="/PROPOSAL/BUDGET/@GRANT_NUMBER" /></fo:inline>
</fo:block >

		</fo:table-cell>

        </fo:table-row>
      </fo:table-body>
    </fo:table>

<!--   END OF FIRST TABLE  -->
	    <!--  SECOND TABLE BEGINS HERE -->


    <fo:table   table-layout="fixed"       >
      <fo:table-column column-width="41mm"/>
      <fo:table-column column-width="32.5mm"/>
      <fo:table-column column-width="13mm"/>
      <fo:table-column column-width="13mm"/>
      <fo:table-column column-width="13mm"/>
      <!--   <fo:table-column column-width="20mm"/>     -->
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="23mm"/>
      <fo:table-column column-width="30.5mm"/>


      <fo:table-body>

      <fo:table-row font-size="8pt">

     	 	<fo:table-cell border-style="solid" text-align="left"
      			border-top-width="0mm" border-left-width="0mm" padding-before="1mm" number-columns-spanned="2" font-size="9pt">

	         	<fo:block start-indent="2mm" >
PERSONNEL (Applicant organization only)
			</fo:block>
		</fo:table-cell>

	      	<fo:table-cell border-style="solid" text-align="left" 
	      		border-top-width="0mm" border-left-width="0mm" padding-before="1mm" number-columns-spanned="3">	
	      		
	      		<fo:block start-indent="1mm" >
	      			Months Devoted to Project
	      		</fo:block>
	      	</fo:table-cell>
      	
  <!--
		<fo:table-cell border-style="solid" text-align="center"
				border-top-width="0mm" border-left-width="0mm" padding-before="1mm" number-rows-spanned="2">
	         	<fo:block padding-before="4mm">

INST.<fo:block></fo:block>BASE<fo:block></fo:block>SALARY
			</fo:block>
		</fo:table-cell>
-->

		<fo:table-cell border-style="solid" text-align="left"
				border-top-width="0mm" border-left-width="0mm" border-right-width="0mm"  number-columns-spanned="3" padding-before="1mm" >
	         	<fo:block start-indent="2mm" text-indent="5mm">
	         	 DOLLAR AMOUNT REQUESTED<fo:inline > (omit cents)</fo:inline>
			</fo:block >
		</fo:table-cell>

        </fo:table-row>

    <fo:table-row  height="11mm" font-size="8pt">
     	 	<fo:table-cell border-style="solid"  font-size="8pt" height="5mm"
      			border-top-width="0mm" border-left-width="0mm" start-indent="2mm">
	         	<fo:block  padding-before="2mm"   >

NAME
			</fo:block>
		</fo:table-cell>

   		<fo:table-cell border-style="solid" text-align="center"
   			 height="5.5mm" border-top-width="0mm" border-left-width="0mm">
	         	<fo:block padding-before="3mm">
ROLE ON PROJECT
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

		<fo:table-cell border-style="solid" text-align="center"  height="5.5mm"
				border-top-width="0mm" border-left-width="0mm"  >
	         	<fo:block padding-before="2mm">
 FRINGE<fo:block></fo:block> BENEFITS
			</fo:block  >
		</fo:table-cell>

		<fo:table-cell border-style="solid" text-align="center"  height="3.5mm" padding-top="2mm"
				border-top-width="0mm" border-left-width="0mm" border-right-width="0mm" display-align="before" >
	         	<fo:block >
TOTALS
			</fo:block>
		</fo:table-cell>

        </fo:table-row>

<!--   The following code fills in the NAME and ROLE boxes of the first row of the Personnel section on Page 5 when PROJECT DIRECTOR is "To be named."  If there is a Person in the Personnel section flagged TRUE for Project Director, that persons data is entered in the first row, with one exception.  The exception is that regadless of what the user enters as the Project Director's Role, the role will appear as "Principal Investigator."

For the follow see the xsl:key PERSON_KEY and its two variables:

$PERSON_PI is true only for the person flagged "TRUE" for Project Director in the personnel section of the XML file.  

Note that $PERSON_OTHER is not the same as  not($PERSON_PI), since a person might not be flagged TRUE or FALSE.  

 -->


<xsl:if test="not($PERSON_PI)" >

      <fo:table-row height="7.2mm">

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
	<fo:block font-size="10" padding-top="0.8mm" padding-bottom="0.2mm"  >


		       <xsl:if test="not(//PROJECT_DIRECTOR/@FIRST_NAME = 'To Be Named') " >
		       	<xsl:value-of select="substring(concat(//PROJECT_DIRECTOR/@LAST_NAME, ', ', //PROJECT_DIRECTOR/@FIRST_NAME ), 1, 28)" />
		       	</xsl:if>

		       <xsl:if test="//PROJECT_DIRECTOR/@FIRST_NAME = 'To Be Named' " >
		       	To Be Named
		       	</xsl:if>

				</fo:block>
			</fo:table-cell>

	  		<fo:table-cell border-style="solid" text-align="center" padding-left="1.5mm"
   			font-size="7pt"  border-top-width="0mm" border-left-width="0mm" display-align="after"  >

			<fo:block padding-top="0.8mm" padding-bottom="0.2mm" display-align="center" >Principal Investigator</fo:block>
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

<xsl:if test="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[@PROJECT_DIRECTOR = 'TRUE']" >
		<xsl:call-template name="People" >
			<xsl:with-param name="End" select="$PI_Count + 1" />
			<xsl:with-param name="Start" select="0" />
			<xsl:with-param name="PI" select="$PERSON_PI" />
		</xsl:call-template>
</xsl:if>

	<xsl:if test="count($PERSON_OTHER) + $PI_Count = 8" >
		<xsl:call-template name="People" >
			<xsl:with-param name="End" select="10 - $PI_Count" />
			<xsl:with-param name="Start" select="0" />
			<xsl:with-param name="PI" select="$PERSON_OTHER" />
		</xsl:call-template>
	</xsl:if>

	<xsl:if test="count($PERSON_OTHER) + $PI_Count  &lt; 8" >
		<xsl:call-template name="People" >
			<xsl:with-param name="End" select="9 - $PI_Count" />
			<xsl:with-param name="Start" select="0" />
			<xsl:with-param name="PI" select="$PERSON_OTHER" />
		</xsl:call-template>

  		<xsl:call-template name="ExtraRows" >
  			<xsl:with-param name="n" select="7 - count($PERSON_OTHER) - $PI_Count " />
  		</xsl:call-template>
	</xsl:if>

	<xsl:if test="count($PERSON_OTHER) + $PI_Count  &gt; 8" >
		<xsl:call-template name="People" >
			<xsl:with-param name="End" select="8 - $PI_Count" />
			<xsl:with-param name="Start" select="0" />
			<xsl:with-param name="PI" select="$PERSON_OTHER" />
		</xsl:call-template>
	</xsl:if>

      </fo:table-body>
    </fo:table>

   <xsl:if test="count($PERSON_OTHER) + $PI_Count  &gt; 8" >


    <fo:table   table-layout="fixed"       >
      <fo:table-column column-width="41mm"/>
      <fo:table-column column-width="32.5mm"/>
      <fo:table-column column-width="13mm"/>
      <fo:table-column column-width="13mm"/>
      <fo:table-column column-width="13mm"/>
      <!--   <fo:table-column column-width="20mm"/>     -->
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="23mm"/>
      <fo:table-column column-width="30.5mm"/>


     <fo:table-body>


      <fo:table-row height="7.5mm">

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

	<fo:table-cell border-style="solid" text-align="right"
   			font-size="10pt"  border-top-width="0mm" border-left-width="0mm" display-align="after" padding-right="1mm" >
	         	<fo:block>

<xsl:call-template name="SalarySubtotal" >
	<xsl:with-param name="total" select="0" />
	<xsl:with-param name="n" select="8 - $PI_Count" />
	<xsl:with-param name="p" select="$FilteredPersonCount + 1" />
</xsl:call-template>

			</fo:block>
		</fo:table-cell>
   <fo:table-cell border-style="solid" text-align="right"
   			font-size="10pt"  border-top-width="0mm" border-left-width="0mm" border-right-width="0mm" display-align="after" padding-right="1mm" >
	         	<fo:block >

<xsl:call-template name="FringeSubtotal" >
	<xsl:with-param name="total" select="0" />
	<xsl:with-param name="n" select="8 - $PI_Count" />
	<xsl:with-param name="p" select="$FilteredPersonCount + 1" />

</xsl:call-template>


			</fo:block>
		</fo:table-cell>
      <fo:table-cell border-style="solid" text-align="right"
   			font-size="10pt"  border-top-width="0mm" border-right-width="0mm" display-align="after" padding-right="1mm" >
	         	<fo:block >

<xsl:call-template name="TotalSubtotal" >
	<xsl:with-param name="total" select="0" />
	<xsl:with-param name="n" select="8 - $PI_Count" />
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
      <fo:table-column column-width="79.5mm"/>
         <fo:table-column column-width="26mm"/>
           <fo:table-column column-width="25mm"/>
      <fo:table-column column-width="26mm"/>
      <fo:table-column column-width="33.5mm"/>

    <fo:table-body>

        <fo:table-row height="9.4mm">

     <fo:table-cell  display-align="center" font-size="10pt" padding-right="3mm">

		<fo:block start-indent="1mm" text-align="right" font-weight="bold" >
				SUBTOTALS
		</fo:block >
</fo:table-cell>

     <fo:table-cell border-right-style="solid"  display-align="center" padding-top="1mm" padding-right="2mm">

		<fo:block  text-align="left">
			<fo:external-graphic width="36mm"  height="3mm"  src="url({$baseUrl}{$imageArrow})" />


		</fo:block >
</fo:table-cell>

    <fo:table-cell border-style="solid" text-align="right"  border-right="0.8mm" border-bottom="0.8mm" border-top="0.8mm" border-left="0.8mm"
   			font-size="10pt"   display-align="after" padding-right="1mm" >

		<fo:block start-indent="1mm">

	<fo:block></fo:block>

			<xsl:value-of select="$S1X_Salary" />
		</fo:block ></fo:table-cell>

           <fo:table-cell border-style="solid" border-bottom-width="0.8mm" border-right-width="0mm" border-left-width="0mm"     text-align="right" border-top="0.8mm"
   			font-size="10pt"  display-align="after" padding-right="1mm" >

		<fo:block start-indent="1mm"  >

<xsl:value-of select="$S1X_Fringe" />
          </fo:block ></fo:table-cell>

            <fo:table-cell border-style="solid" text-align="right"
   			font-size="10pt"  border-top-width="0.8mm" border-bottom-width="0.8mm"  border-right-width="0.8mm"  display-align="after" border-left-width="0.8mm" padding-right="1mm" >

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

rrrr
    <fo:table   table-layout="fixed"      >
           <fo:table-column column-width="41.5mm"/>
           <fo:table-column column-width="3mm"/>
          <fo:table-column column-width="7.5mm"/>
          <fo:table-column column-width="5.5mm"/>
		<fo:table-column column-width="18mm"/>
          <fo:table-column column-width="51mm"/>
          <fo:table-column column-width="18mm"/>
          <fo:table-column column-width="3.5mm"/>
          <fo:table-column column-width="3.5mm"/>
          <fo:table-column column-width="5mm"/>
          <fo:table-column column-width="33.5mm"/>
        -->

         <fo:table   table-layout="fixed"      >
           <fo:table-column column-width="41.5mm"/>
           <fo:table-column column-width="3mm"/>
          <fo:table-column column-width="7.5mm"/>
          <fo:table-column column-width="5.5mm"/>
		<fo:table-column column-width="20mm"/>
          <fo:table-column column-width="49mm"/>
          <fo:table-column column-width="18mm"/>
          <fo:table-column column-width="3.5mm"/>
          <fo:table-column column-width="3.5mm"/>
          <fo:table-column column-width="5mm"/>
          <fo:table-column column-width="33.5mm"/>

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
 </fo:table >

    <fo:table   table-layout="fixed"       >

      <fo:table-column column-width="64.5mm"/>
      <fo:table-column column-width="92mm"/>
      <fo:table-column column-width="3.5mm"/>
      <fo:table-column column-width="30mm"/>

     <fo:table-body line-height="9.4pt">

     <fo:table-row height="7.9mm">

          <fo:table-cell border-left-width="0mm"  border-top-width="0.3mm"  border-right-width="0mm"    border-style="solid"
          		  font-size="9pt"  font-family="Arial, sans-serif"   start-indent="2mm" display-align="center"
          		   number-columns-spanned="2"
          		 >
          <fo:block font-weight="bold">SUBTOTAL DIRECT COSTS FOR NEXT BUDGET PERIOD
		</fo:block >
		</fo:table-cell>

    <fo:table-cell border-style="solid" text-align="left" border-right-width="0mm" border-bottom-width="0.8mm"
   			font-size="10pt" border-top-width="0.8mm"  border-left-width="0.8mm" display-align="after" >

	       	<fo:block start-indent="1mm" font-weight="bold">
	<!--   $  -->
	       	</fo:block >
	</fo:table-cell>

	   <fo:table-cell border-style="solid" text-align="right"
   			font-size="10pt"  border-top-width="0.8mm" border-left-width="0mm" border-right-width="0.8mm" border-bottom-width="0.8mm" display-align="after" padding-right="2mm" >

	       	<fo:block >
	       		<!-- 5/10/2005 dterret: Change to support the possibility of negative values due to the
	       		     addition of Modular Adjustment to the subtotal. -->
	       		<xsl:value-of select="$subtotalDirect"/>
	       	</fo:block >
	</fo:table-cell>

         </fo:table-row>

       <fo:table-row height="6.8mm">


          <fo:table-cell   font-size="8pt"  font-family="Arial, sans-serif"  start-indent="2mm" padding-top="1.2mm" display-align="after" line-height="0mm">
          <fo:block >CONSORTIUM/CONTRACTUAL COSTS</fo:block >

		</fo:table-cell>

    <fo:table-cell border-style="solid"   font-size="8pt"  font-family="Arial, sans-serif"
		border-left-width="0mm"
		border-top-width="0mm"

		border-bottom-width="0.3mm"
	       text-align="left"


	       padding-top="1.2mm" >

	       	<fo:block start-indent="2mm">
	       		DIRECT COSTS
	       	</fo:block >
	</fo:table-cell>

	       		<fo:table-cell border-style="solid" border-top-width="0mm" border-left-width="0mm" border-right-width="0mm" />

	     <fo:table-cell border-style="solid"   font-size="8pt"  font-family="Arial, sans-serif" border-left-width="0mm"
       	border-top-width="0mm"
		border-right-width="0mm"

		border-bottom-width="0.3mm"

		display-align="after"
		padding-right="2mm">

			<fo:block start-indent="1mm" text-align="right" font-size="10pt">
<xsl:value-of select="$S1X_Subcontractors" />
			</fo:block>
	</fo:table-cell>
         </fo:table-row>

       <fo:table-row height="5.5mm">

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"    border-style="solid"
          		  font-size="8pt"  font-family="Arial, sans-serif"  start-indent="2mm" padding-top="1mm" >
          <fo:block >

          </fo:block>

		</fo:table-cell>
          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"    border-style="solid"  font-size="8pt"  font-family="Arial, sans-serif"   padding-top="1mm" >
          <fo:block text-indent="2mm">FACILITIES AND ADMINISTRATIVE COSTS
		</fo:block>
		</fo:table-cell>

		<fo:table-cell  />

    <fo:table-cell border-style="solid"   font-size="10pt"  font-family="Arial, sans-serif"
		border-left-width="0mm"
		border-top-width="0mm"
		border-right-width="0mm"
		border-bottom-width="0.8mm"
	       padding-before="2mm"

	       padding-right="2mm"
	       margin-left="2mm"  >

			<fo:block  text-align="right" >

					<xsl:value-of select="$S1X_IDC" />

		</fo:block >
	</fo:table-cell>

         </fo:table-row>

     <fo:table-row height="6mm">

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"    border-style="solid"
          		  font-size="9pt"  font-family="Arial, sans-serif"    start-indent="2mm" display-align="center" number-columns-spanned="2" padding-top="1mm">

          <fo:block font-weight="bold"  >TOTAL DIRECT COSTS FOR NEXT BUDGET PERIOD

          <fo:inline font-size="9pt" font-weight="100" font-style="italic" >
          	 (Item 9a, Face Page)
         	</fo:inline>

		</fo:block >

		</fo:table-cell>

    <fo:table-cell border-style="solid" text-align="left" border-right-width="0mm" border-bottom-width="0.6mm"
   			font-size="10pt" border-top-width="0.6mm"  border-left-width="0.6mm" display-align="after" >

	       	<fo:block start-indent="1mm" font-weight="bold">
<!-- 	$  -->
	       	</fo:block >
		</fo:table-cell>

	  <fo:table-cell border-style="solid" text-align="right" border-bottom-width="0.6mm"
   		font-size="10pt"  border-top-width="0.6mm" border-left-width="0mm" border-right-width="0.6mm" display-align="after" padding-right="2mm" >

	    <fo:block >
				<xsl:value-of select="$totalDirect"/>
	    </fo:block >
		</fo:table-cell>

         </fo:table-row>

      			</fo:table-body>
		    </fo:table>

    <fo:table   table-layout="fixed"       space-before="1.5mm">
      <fo:table-column column-width="2in"/>
      <fo:table-column column-width="3.5in"/>
      <fo:table-column column-width="2in"/>
    <fo:table-body>

        <fo:table-row >
          <fo:table-cell  font-size="8pt"  font-family="Arial, sans-serif" text-indent="1mm" >
          <fo:block text-indent="1mm">
	PHS 2590 (Rev. 04/06)
	</fo:block>
		</fo:table-cell>

          <fo:table-cell    font-size="8pt"  font-family="Arial, sans-serif" >
<fo:block  text-align="center" text-indent="2mm" >

Page _______

</fo:block>
</fo:table-cell>

          <fo:table-cell   font-size="8pt"  font-family="Arial, sans-serif" border-left-width="0mm"  text-align="right">
<fo:block  ><fo:inline font-weight="bold">Form Page 2</fo:inline></fo:block ></fo:table-cell>
        </fo:table-row>

      </fo:table-body>
    </fo:table>

    <!-- If the number of persons who are not flagged TRUE for Principal Investigator exceeds 6, extra pages are required.  The template PersonControl creates additional pages until all Persons are included in output.  PersonControl calls the template MorePeople to add people as necssary, and MorePeople calls ExtraRows if needed when all Persons have been displayed. -->


	<xsl:if test="count($PERSON_OTHER) + $PI_Count  &gt; 8" >
		<xsl:call-template name="PersonControl" >
			<xsl:with-param name="Start" select="7 - $PI_Count" />
		</xsl:call-template>
  	</xsl:if>

       	</fo:flow>
</fo:page-sequence>

      </fo:root>
  </xsl:template>

  <!--  PersonControl is called when there is not enough room for all Persons involved in Period 1.  This recursive template increments by 7 since there is room for 7 Persons on each overflow sheet.  On each recursion, MorePeople is called with the freshly incremented $Start parameter.  If count($PERSON_OTHER) - $Start &gt; 7 returns "false," the template exits.  MorePeople finishes displaying Persons, and then displays the appropriate number of extra rows by calling ExtraRows.   -->

  <xsl:template name="PersonControl" >
  	<xsl:param name="Start" />
		
  	<xsl:call-template name="MorePeople" >
  		<xsl:with-param name="Start"  select="$Start" />
  		<xsl:with-param name="End" select="$Start + 9" />
  	</xsl:call-template>
		
  	<xsl:if test="count($PERSON_OTHER) - $Start &gt; 8" >
  		<xsl:call-template name="PersonControl" >
  			<xsl:with-param name="Start" select="$Start + 8" />
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


			<!--   3333333333333######################$$$$$$$$$$$$$$$$$$$  -->



    <fo:table   table-layout="fixed">
      	<fo:table-column column-width="115mm"/>
      	<fo:table-column column-width="75mm"/>
    		<fo:table-body>

        <fo:table-row >


          <fo:table-cell>

			<fo:block font-size="8pt"
			text-align="left"
			margin-left="4cm"

			space-before="2mm"
			start-indent="4mm">

<!--  The following code puts the project director / principal investigator's name at the top of Page 4.  Similar code is used to put the name at the top of Page 5 and on the overflow personnel pages.  -->

			Principal Investigator/Program Director (Last, first, middle)
		</fo:block>
		</fo:table-cell>

          <fo:table-cell padding-right="2mm" display-align="center" font-size="10pt">
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
      <fo:table-column column-width="80mm"/>
      <fo:table-column column-width="31mm"/>
      <fo:table-column column-width="28mm"/>
       <fo:table-column column-width="51mm"/>
    <fo:table-body>

        <fo:table-row height="11.8mm">

          <fo:table-cell border-left-width="0mm" border-style="solid" text-align="center" font-size="10pt" display-align="center">
          <fo:block font-weight="bold">
	DETAILED BUDGET FOR NEXT BUDGET<fo:block />PERIOD - DIRECT COSTS ONLY
		</fo:block>
		</fo:table-cell>

          <fo:table-cell border-style="solid"  font-size="8pt" border-left-width="0mm" >

          <fo:block start-indent="1mm"  padding-before.length="1mm"><fo:inline font-weight="bold">FROM</fo:inline>
<fo:block padding-top="1.4mm"></fo:block>
<fo:inline font-size="10pt" ><xsl:value-of select="/PROPOSAL/BUDGET/PERIODS/PERIOD[PERIOD_NUMBER=$Period_Number]/START_DATE" /></fo:inline>
</fo:block >
</fo:table-cell>


          <fo:table-cell border-style="solid" font-size="8pt" border-left-width="0mm" border-right-width="0mm" start-indent="1mm">
<fo:block padding-before.length="1mm">
          <fo:inline font-weight="bold">THROUGH</fo:inline>

<fo:block padding-top="1.4mm"></fo:block>
<fo:inline font-size="10pt" ><xsl:value-of select="/PROPOSAL/BUDGET/PERIODS/PERIOD[PERIOD_NUMBER=$Period_Number]/STOP_DATE" /></fo:inline>
</fo:block >

		</fo:table-cell>



          <fo:table-cell border-style="solid" font-size="8pt" border-right-width="0mm" start-indent="1mm">
<fo:block padding-before.length="1mm">
        <fo:inline font-weight="bold">GRANT NUMBER</fo:inline> <fo:block padding="0.5mm"></fo:block>
        <fo:inline font-size="10pt" ><xsl:value-of select="/PROPOSAL/BUDGET/@GRANT_NUMBER" /></fo:inline>
</fo:block >

		</fo:table-cell>

        </fo:table-row>
      </fo:table-body>
    </fo:table>

<!--   END OF FIRST TABLE  -->
	    <!--  SECOND TABLE BEGINS HERE -->







    <fo:table   table-layout="fixed"       >
      <fo:table-column column-width="41mm"/>
      <fo:table-column column-width="32.5mm"/>
      <fo:table-column column-width="13mm"/>
      <fo:table-column column-width="13mm"/>
      <fo:table-column column-width="13mm"/>
      <!--   <fo:table-column column-width="20mm"/>     -->
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="23mm"/>
      <fo:table-column column-width="30.5mm"/>

      <fo:table-body>

      <fo:table-row font-size="9pt">

     	 	<fo:table-cell border-style="solid" text-align="left"
      			border-top-width="0mm" border-left-width="0mm" padding-before="1mm" number-columns-spanned="2">

	         	<fo:block start-indent="2mm" >
PERSONNEL (Applicant organization only)
			</fo:block>
		</fo:table-cell>

      	               <fo:table-cell border-style="solid" text-align="left" font-size="8pt"  font-family="Arial, sans-serif"
	      		border-top-width="0mm" border-left-width="0mm" padding-before="1mm" number-columns-spanned="3">	
	      		
	      		<fo:block start-indent="1mm" >
	      			Months Devoted to Project
	      		</fo:block>
	      	</fo:table-cell>

  <!--
		<fo:table-cell border-style="solid" text-align="center"
				border-top-width="0mm" border-left-width="0mm" padding-before="1mm" number-rows-spanned="2">
	         	<fo:block padding-before="4mm">

INST.<fo:block></fo:block>BASE<fo:block></fo:block>SALARY
			</fo:block>
		</fo:table-cell>
-->

		<fo:table-cell border-style="solid" text-align="left"
				border-top-width="0mm" border-left-width="0mm" border-right-width="0mm"  number-columns-spanned="3" padding-before="1mm" >
	         	<fo:block start-indent="2mm" text-indent="5mm">
	         	 DOLLAR AMOUNT REQUESTED<fo:inline > (omit cents)</fo:inline>
			</fo:block >
		</fo:table-cell>

        </fo:table-row>

    <fo:table-row font-size="8pt">
       	 	<fo:table-cell border-style="solid"  font-size="8pt" height="5mm"
      			border-top-width="0mm" border-left-width="0mm" start-indent="2mm">
	         	<fo:block  padding-before="2mm"   >

NAME
			</fo:block>
		</fo:table-cell>
   		<fo:table-cell border-style="solid" text-align="center"
   			 height="5.5mm" border-top-width="0mm" border-left-width="0mm">
	         	<fo:block padding-before="3mm">
ROLE ON PROJECT
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
 FRINGE <fo:block></fo:block> BENEFITS
			</fo:block  >
		</fo:table-cell>

		<fo:table-cell border-style="solid" text-align="center" font-size="8pt" height="3.5mm" display-align="before"
				border-top-width="0mm" border-left-width="0mm" border-right-width="0mm" padding-top="2mm">
	         	<fo:block >
TOTALS
			</fo:block>
		</fo:table-cell>

        </fo:table-row>

      </fo:table-body>
    </fo:table>


    <fo:table   table-layout="fixed"       >
      <fo:table-column column-width="41mm"/>
      <fo:table-column column-width="32.5mm"/>
      <fo:table-column column-width="13mm"/>
      <fo:table-column column-width="13mm"/>
      <fo:table-column column-width="13mm"/>
      <!--   <fo:table-column column-width="20mm"/>     -->
      <fo:table-column column-width="22mm"/>
      <fo:table-column column-width="23mm"/>
      <fo:table-column column-width="30.5mm"/>


     <fo:table-body line-height="9.4pt">


<!--   The following code fills in the NAME and ROLE boxes of the first row of the Personnel section on Page 5 when PROJECT DIRECTOR is "To be named."  If there is a Person in the Personnel section flagged TRUE for Project Director, that persons data is entered in the first row, with one exception.  The exception is that regadless of what the user enters as the Project Director's Role, the role will appear as "Principal Investigator."

$PERSON_PI is true only for the person flagged "TRUE" for Project Director in the personnel section of the XML file.

Note that $PERSON_OTHER is not the same as  not($PERSON_PI), since a person might not be flagged TRUE or FALSE.

 -->


<!--   The following code fills in the first row of the PERSONNEL section of Page 4 when someone is flagged "TRUE" for personnel dierector   -->

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

      </fo:table-body>
    </fo:table>

    <fo:table   table-layout="fixed"      >
      <fo:table-column column-width="79.5mm"/>
         <fo:table-column column-width="26mm"/>
           <fo:table-column column-width="25mm"/>
      <fo:table-column column-width="26mm"/>
      <fo:table-column column-width="33.5mm"/>

    <fo:table-body>

      <fo:table-row height="9.4mm">
     	<fo:table-cell border-bottom-style="solid"  display-align="center" font-size="10pt" padding-right="3mm">

		<fo:block start-indent="1mm" text-align="right" font-weight="bold" >
				SUBTOTALS
		</fo:block >
	</fo:table-cell>

     <fo:table-cell border-style="solid"   border-top-width="0mm" border-left-width="0mm" display-align="center" border-right-width="0.8mm" padding-top="1mm" padding-right="2mm">

		<fo:block  text-align="left" >
			<fo:external-graphic width="36mm"  height="3mm"  src="url({$baseUrl}{$imageArrow})" />
		</fo:block >
	</fo:table-cell>


    <fo:table-cell border-style="solid" text-align="right"  border-right-width="0.8mm" border-top-width="0.8mm"  border-bottom-width="0.8mm"
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

           <fo:table-cell border-style="solid" border-bottom-width="0.8mm" border-right-width="0mm" border-left-width="0mm"  border-top-width="0.8mm"   text-align="right"
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
   			font-size="10pt"  border-top-width="0.8mm" border-bottom-width="0.8mm"  border-right-width="0.8mm"  display-align="after" border-left-width="0.8mm" padding-right="1mm"  >

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
          <fo:table-column column-width="45mm"/>
          <fo:table-column column-width="19mm"/>
          <fo:table-column column-width="92.5mm"/>
          <fo:table-column column-width="33.5mm"/>

     <fo:table-body>

        <fo:table-row height="16mm">

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"    border-style="solid"
          		  font-size="8pt"  font-family="Arial, sans-serif"  number-columns-spanned="3" padding-before="1.8mm" start-indent="2mm" >
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

        <fo:table-row height="22mm" >

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"  border-style="solid"
          		  font-size="8pt"  font-family="Arial, sans-serif"  padding-before="0.4mm"  number-columns-spanned="3" start-indent="2mm">
          <fo:block >EQUIPMENT<fo:inline font-style="italic"> (Itemize)</fo:inline>
		</fo:block>
		</fo:table-cell>

		<fo:table-cell border-style="solid" border-right-width="0mm"  border-top-width="0mm" >
			<fo:block></fo:block>
		</fo:table-cell>

</fo:table-row>

         <!--   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   -->

        <fo:table-row height="31mm" >

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"  border-style="solid"
          		  font-size="8pt"  font-family="Arial, sans-serif"  padding-before="0.4mm"  number-columns-spanned="3" start-indent="2mm">
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
          		  font-size="8pt"  font-family="Arial, sans-serif"  padding-before="1.8mm"   number-columns-spanned="3" start-indent="2mm">
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


       <fo:table-row height="5.2mm">

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"    border-style="solid"
          		  font-size="8pt"  font-family="Arial, sans-serif"  padding-before="0.4mm" number-columns-spanned="0" number-rows-spanned="2"  start-indent="2mm">
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
	       number-columns-spanned="2" >

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

       <fo:table-row height="4.9mm">

    <fo:table-cell border-style="solid"   font-size="8pt"  font-family="Arial, sans-serif"
		border-left-width="0.3mm"
		border-top-width="0mm"
		border-right-width="0mm"
		border-bottom-width="0.3mm"
	       text-align="left"
	       padding-before="0.5mm"

	       number-columns-spanned="2" >

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
          		  font-size="8pt"  font-family="Arial, sans-serif"  padding-before="0.4mm" number-columns-spanned="3" start-indent="2mm">
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

       <fo:table-row height="16.6mm">

          <fo:table-cell
          		  font-size="9pt"  font-family="Arial, sans-serif"  padding-before="1.8mm"   number-columns-spanned="3" start-indent="2mm">
          <fo:block >OTHER EXPENSES<fo:inline font-style="italic"> (Itemize by category)</fo:inline>
		</fo:block>
		</fo:table-cell>

	   <fo:table-cell border-left-width="0.3mm"  border-top-width="0mm" border-bottom-width="0mm"  border-right-width="0mm"   border-style="solid"
          		  font-size="10pt"  font-family="Arial, sans-serif"  padding-before="1.8mm"   number-columns-spanned="3" text-align="center">
          <fo:block >

		</fo:block>
		</fo:table-cell>

         </fo:table-row>

         <!--   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   -->
  </fo:table-body>
 </fo:table >

    <fo:table   table-layout="fixed"       >

      <fo:table-column column-width="64.5mm"/>
      <fo:table-column column-width="92mm"/>
      <fo:table-column column-width="3.5mm"/>
      <fo:table-column column-width="30mm"/>

     <fo:table-body line-height="11pt">

     <fo:table-row height="7mm">

          <fo:table-cell border-left-width="0mm"  border-top-width="0.3mm"  border-right-width="0mm"    border-style="solid"
          		  font-size="9pt"  font-family="Arial, sans-serif"   start-indent="2mm" display-align="after" padding-bottom="1mm"
          		   number-columns-spanned="2"
          		 >
          <fo:block font-weight="bold">SUBTOTAL DIRECT COSTS FOR NEXT BUDGET PERIOD
		</fo:block >
		</fo:table-cell>

    <fo:table-cell border-style="solid" text-align="left" border-right-width="0mm" border-bottom-width="0.8mm"
   			font-size="10pt" border-top-width="0.8mm"  border-left-width="0.8mm" display-align="after" >

	       	<fo:block start-indent="1mm" font-weight="bold">
<!-- 	$  -->
	       	</fo:block >
	</fo:table-cell>


	   <fo:table-cell border-style="solid" text-align="right"
   			font-size="10pt"  border-top-width="0.8mm" border-left-width="0mm" border-right-width="0.8mm" border-bottom-width="0.8mm" display-align="after" padding-right="2mm" >

	       	<fo:block >

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

       <fo:table-row height="7mm">


          <fo:table-cell   font-size="8pt"  font-family="Arial, sans-serif"  start-indent="2mm" padding-top="1.2mm" display-align="after" line-height="0mm">
          <fo:block >CONSORTIUM/CONTRACTUAL COSTS</fo:block >

		</fo:table-cell>

    <fo:table-cell border-style="solid"   font-size="8pt"  font-family="Arial, sans-serif"
		border-left-width="0mm"
		border-top-width="0mm"

		border-bottom-width="0.3mm"
	       text-align="left"


	       padding-top="1.2mm" >

	       	<fo:block start-indent="2mm">
	       		DIRECT COSTS
	       	</fo:block >
	</fo:table-cell>

	       		<fo:table-cell border-style="solid" border-top-width="0mm" border-left-width="0mm" border-right-width="0mm" />

	     <fo:table-cell border-style="solid"   font-size="8pt"  font-family="Arial, sans-serif" border-left-width="0mm"
       	border-top-width="0mm"
		border-right-width="0mm"

		border-bottom-width="0.3mm"

		display-align="after"
		padding-right="2mm">

			<fo:block start-indent="1mm" text-align="right" font-size="10pt">

			</fo:block>
	</fo:table-cell>
         </fo:table-row>

       <fo:table-row height="6.5mm">

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"    border-style="solid"
          		  font-size="8pt"  font-family="Arial, sans-serif"  start-indent="2mm" padding-top="1mm" >
          <fo:block >

          </fo:block>

		</fo:table-cell>
          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"    border-style="solid"  font-size="8pt"  font-family="Arial, sans-serif"   padding-top="1mm" >
          <fo:block text-indent="2mm">FACILITIES AND ADMINISTRATIVE COSTS
		</fo:block>
		</fo:table-cell>

		<fo:table-cell  />

    <fo:table-cell border-style="solid"   font-size="10pt"  font-family="Arial, sans-serif"
		border-left-width="0mm"
		border-top-width="0mm"
		border-right-width="0mm"
		border-bottom-width="0.6mm"
	       padding-before="2mm"

	       padding-right="2mm"
	       margin-left="2mm"  >

			<fo:block  text-align="right" >

	       	</fo:block >
	</fo:table-cell>

         </fo:table-row>

     <fo:table-row height="7mm">

          <fo:table-cell border-left-width="0mm"  border-top-width="0mm"  border-right-width="0mm"    border-style="solid"
          		  font-size="9pt"  font-family="Arial, sans-serif"    start-indent="2mm" display-align="after" padding-bottom="1mm" number-columns-spanned="2" >

          <fo:block font-weight="bold"  >TOTAL DIRECT COSTS FOR NEXT BUDGET PERIOD

          <fo:inline font-size="9pt" font-weight="100" font-style="italic" >
          	 (Item 9a, Face Page)
         	</fo:inline>

		</fo:block >





		</fo:table-cell>

    <fo:table-cell border-style="solid" text-align="left" border-right-width="0mm" border-bottom-width="0.6mm"
   			font-size="10pt" border-top-width="0.6mm"  border-left-width="0.6mm" display-align="after" >

	       	<fo:block start-indent="1mm" font-weight="bold">
<!-- 	$  -->
	       	</fo:block >
	</fo:table-cell>

	   <fo:table-cell border-style="solid" text-align="right" border-bottom-width="0.6mm"
   			font-size="10pt"  border-top-width="0.6mm" border-left-width="0mm" border-right-width="0.6mm" display-align="after" padding-right="2mm" >

	       	<fo:block >

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

    <fo:table   table-layout="fixed"       space-before="1.5mm">
      <fo:table-column column-width="2in"/>
      <fo:table-column column-width="3.5in"/>
      <fo:table-column column-width="2in"/>
    <fo:table-body>

        <fo:table-row >
          <fo:table-cell    font-size="8pt"  font-family="Arial, sans-serif" text-indent="1mm"  >
          <fo:block  text-indent="1mm" >
	PHS 2590 (Rev. 04/06)
	</fo:block>
		</fo:table-cell>

          <fo:table-cell    font-size="8pt"  font-family="Arial, sans-serif"  >
<fo:block  text-align="center" text-indent="2mm" >

Page _______

</fo:block>
</fo:table-cell>

          <fo:table-cell   font-size="8pt"  font-family="Arial, sans-serif"  text-align="right">
<fo:block ><fo:inline font-weight="bold">Form Page 2</fo:inline></fo:block ></fo:table-cell>
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

<xsl:variable name="SUM_SALARY" select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY) " />


<xsl:variable name="SUM_FRINGE" select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT) " />


<xsl:variable name="SUM_TOTAL" select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY) + sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT) " />


 	<xsl:if test="position() &lt; $End and position() &gt; $Start">

      <fo:table-row height="7.8mm" >


     	 	<fo:table-cell border-style="solid" text-align="left"
      			border-top-width="0mm" border-left-width="0mm"   font-size="10pt" display-align="after" >
	     	    	<fo:block padding-left="2mm">

	     	    	 <xsl:value-of select="NAME" />
				</fo:block>
			</fo:table-cell>

   		<fo:table-cell border-style="solid" text-align="left"  line-height="9.5pt"
   			font-size="10pt"  border-top-width="0mm" border-left-width="0mm" display-align="after"  >

	         	<fo:block text-indent="1.5mm">

	<!--  THE FOLLOWING CODE PLACES THE PRINCIPAL INVESTIGATOR'S ROLE IN THE FIRST ROW OF THE FORM 			WHEN PARAMETER PI IS SET TO $PERSON_PI -->

		<xsl:if test="@PROJECT_DIRECTOR = 'TRUE' and position() = '1' " >
			Principal<fo:block></fo:block>Investigator
		</xsl:if>

	<xsl:if test="@PROJECT_DIRECTOR = 'TRUE' and position() &gt; '1' " >
	 Principal<fo:block></fo:block>Investigator
		</xsl:if>

		<xsl:if test="@PROJECT_DIRECTOR = 'FALSE' " >
<xsl:if test="ROLE = 'Co-Investigator' " >
Co-<fo:block></fo:block>Investigator
</xsl:if>

<xsl:if test="not(ROLE = 'Co-Investigator')and $PI = $PERSON_OTHER " >

		<xsl:value-of select="substring(substring-before(ROLE, ' '), 1, 15)" />
		<fo:block></fo:block>
		<xsl:value-of select="substring(substring-after(ROLE, ' '), 1, 15)" />


	<xsl:if test="not(substring-before(ROLE, ' ')) " >
		<xsl:value-of select="substring(ROLE, 1, 15)" />
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
	      					<xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@CALENDAR_MONTHS)" />
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
	      					<xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@ACADEMIC_MONTHS)" />
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
	      					<xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON [ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@SUMMER_MONTHS)" />
	      				</xsl:when>
	      				<xsl:otherwise></xsl:otherwise>
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

      <fo:table-row height="7.8mm">

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
		<xsl:when test="$Count &gt; 5 ">

		<xsl:call-template name="Filter_Zeros_Consultants_Num" >
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

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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
   </xsl:if>

	<fo:table-row height="5.4mm">

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif"
			 display-align="after" padding-start="2mm" number-columns-spanned="3">

		       	<fo:block  >

		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Consultants_Description" >
						<xsl:with-param name="n" select="$n + 1" />
					</xsl:call-template>

		       	</xsl:variable>

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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
			number-columns-spanned="2"
			  >


	<xsl:if test="$n = 1 ">

		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Consultants_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>
		       	 </xsl:variable>

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
	     	  	</fo:block>

  	</xsl:if>


	<xsl:if test="$Count &lt; 6 and $n = 3" >

		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Consultants_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>
		       	 </xsl:variable>

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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
	       	number-columns-spanned="3"
	       	padding-right="2mm"
	       	>

		<xsl:choose>
				<xsl:when test="$Count &gt; 5 and $n &gt; 2">

	<fo:block>

		<xsl:call-template name="ConsultantTotal_P1" >
			<xsl:with-param name="k" select="5 + $Zero_Count" />
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

<xsl:if test="$n = 3" >
	<fo:block>
<xsl:value-of select="$S1X_Consultants" />

	</fo:block >
	</xsl:if>

		</fo:table-cell>

       </fo:table-row>

<xsl:if test="$n = 1" >
		<xsl:call-template name="Consultants" >
  		<xsl:with-param name="n" select="$n + 2" />
  	</xsl:call-template>
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

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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
			text-align="left" padding-start="7mm"
			number-columns-spanned="2">

<xsl:if test="$Count &lt; 4" >

		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Travel_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>


		       	</xsl:variable>

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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
	       	number-columns-spanned="3"
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
		<xsl:when test="$Count &gt; 7 ">

		<xsl:call-template name="Filter_Zeros_Equipment_Num" >
			<xsl:with-param name="n" select="7" />
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

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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


	<xsl:if test="$n &lt; 5 ">

		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Equipment_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>
		       	 </xsl:variable>

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
	     	  	</fo:block>

  	</xsl:if>


	<xsl:if test="$Count &lt; 8 and $n = 5" >

		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Equipment_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>
		       	 </xsl:variable>

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
	     	  	</fo:block>
  	</xsl:if>


	<xsl:if test="$Count &gt; 7 and $n = 5">
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
				<xsl:when test="$Count &gt; 7 and $n &gt; 3">

	<fo:block>

		<xsl:call-template name="EquipmentTotal_P1" >
			<xsl:with-param name="k" select="7 + $Zero_Count" />
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

<xsl:if test="$n = 5" >
	<fo:block>
<xsl:value-of select="$S1X_Equipment" />

	</fo:block >
	</xsl:if>

		</fo:table-cell>

       </fo:table-row>

<xsl:if test="$n &lt; 5" >
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
<fo:table-row height="5.15mm" >

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

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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

<fo:table-row height="5.15mm">

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif"
			 display-align="after" padding-start="2mm" number-columns-spanned="3">

		       	<fo:block  >

		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Supplies_Description" >
						<xsl:with-param name="n" select="$n + 1" />
					</xsl:call-template>

		       	</xsl:variable>

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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
			number-columns-spanned="2"
			  >

		<xsl:if test="$Count &lt; 12 and $n&lt; 10 ">

		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Supplies_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>

		       	</xsl:variable>

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
	     	  	</fo:block>
		</xsl:if>


 		<xsl:if test="$Count &gt; 11 and $n&lt; 8 ">

		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Supplies_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>

		       	</xsl:variable>

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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
	       	number-columns-spanned="3"
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

<fo:table-row height="5.5mm"  line-height="12pt">

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

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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

	<fo:table-row height="5.5mm" line-height="12pt">

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif"
			 display-align="after" padding-start="2mm" number-columns-spanned="3">


		       	<fo:block  >
		       	<xsl:variable name="truncate" >

<xsl:call-template name="OtherTextControl" >
	<xsl:with-param name="n" select="$n + 1" />
</xsl:call-template>
<!--  <xsl:value-of select="$n" />  -->
		       	</xsl:variable>
		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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
			number-columns-spanned="2"
			  >


		       	<fo:block  >
		       	<xsl:variable name="truncate" >

<xsl:call-template name="OtherTextControl" >
	<xsl:with-param name="n" select="$n + 2" />
</xsl:call-template>

		       	 </xsl:variable>
		       	<xsl:value-of select="substring($truncate, 1, 24)" />
	     	  	</fo:block>


		</fo:table-cell>

    		<fo:table-cell   font-size="10pt"  font-family="Arial, sans-serif"
			border-left-width="0.3mm"
			border-top-width="0mm"
			border-right-width="0mm"
			border-bottom-width="0.3mm"
	       	display-align="after"
	       	text-align="right"
	       	number-columns-spanned="3"
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

	<xsl:if test="$n = 3" >
	<fo:block>

<xsl:value-of select="$S1X_OtherExpenses" />
	</fo:block >
	</xsl:if>

		</fo:table-cell>

       </fo:table-row>

	<xsl:if test="$n &lt; 3" >

	<xsl:call-template name="Other_B" >
		<xsl:with-param name="n" select="$n + 2" />
	</xsl:call-template>

  </xsl:if>

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
		       <xsl:value-of select="substring($truncate, 1, 24)" />
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

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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
			number-columns-spanned="2"
			  >

<xsl:if test="$Count &lt; 4" >

		       	<fo:block  >
		       	<xsl:variable name="truncate" >
					<xsl:call-template name="Alterations_Description" >
						<xsl:with-param name="n" select="$n + 2" />
					</xsl:call-template>
		       	</xsl:variable>

		       	<xsl:value-of select="substring($truncate, 1, 24)" />
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
	       	number-columns-spanned="3"
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
	<xsl:for-each select="$PERSON_OTHER " >

		<xsl:if test="position() = $Start + 1 ">
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 2 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 3 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 4 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 5 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 6 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 7 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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


<xsl:variable name="SALARY_SUM_H" >
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 8 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
 		</xsl:if>


	</xsl:for-each>
</xsl:variable>

<xsl:variable name="SALARY_SUM_HX"	>
	<xsl:choose>
		<xsl:when test="$SALARY_SUM_H = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$SALARY_SUM_H" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>



	<xsl:value-of select="  $SALARY_SUM_AX + $SALARY_SUM_BX + $SALARY_SUM_CX + $SALARY_SUM_DX + $SALARY_SUM_EX + $SALARY_SUM_FX + $SALARY_SUM_GX + $SALARY_SUM_HX  " />

</xsl:template>

<xsl:template name="SubtotalFringe" >
	<xsl:param name="Start" />
	<xsl:param name="total" />

	<xsl:variable name="FRINGE_SUM_A" >
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 1 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 2 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 3 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 4 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 5 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 6 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 7 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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




 <xsl:variable name="FRINGE_SUM_H" >
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 8 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
 		</xsl:if>


	</xsl:for-each>
</xsl:variable>

<xsl:variable name="FRINGE_SUM_HX"	>
	<xsl:choose>
		<xsl:when test="$FRINGE_SUM_H = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$FRINGE_SUM_H" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>





	<xsl:value-of select="  $FRINGE_SUM_AX + $FRINGE_SUM_BX + $FRINGE_SUM_CX + $FRINGE_SUM_DX + $FRINGE_SUM_EX + $FRINGE_SUM_FX + $FRINGE_SUM_GX + $FRINGE_SUM_HX   " />

</xsl:template>

<xsl:template name="SubtotalTotal" >
	<xsl:param name="Start" />
	<xsl:param name="total" />


	<xsl:variable name="FRINGE_SUM_A" >
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 1 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 2 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 3 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 4 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 5 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 6 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 7 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
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



 <xsl:variable name="FRINGE_SUM_H" >
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 8 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT)" />
 		</xsl:if>

	</xsl:for-each>
</xsl:variable>

<xsl:variable name="FRINGE_SUM_HX"	>
	<xsl:choose>
		<xsl:when test="$FRINGE_SUM_H = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$FRINGE_SUM_H" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>






	<xsl:variable name="SALARY_SUM_A" >
	<xsl:for-each select="$PERSON_OTHER " >

		<xsl:if test="position() = $Start + 1 ">
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 2 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 3 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 4 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 5 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 6 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 7 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
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




<xsl:variable name="SALARY_SUM_H" >
	<xsl:for-each select="$PERSON_OTHER " >
		<xsl:if test="position() = $Start + 8 " >
			<xsl:value-of select="sum( /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY)" />
 		</xsl:if>


	</xsl:for-each>
</xsl:variable>

<xsl:variable name="SALARY_SUM_HX"	>
	<xsl:choose>
		<xsl:when test="$SALARY_SUM_H = '' ">
			0
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$SALARY_SUM_H" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>





	<xsl:value-of select="  $FRINGE_SUM_AX + $FRINGE_SUM_BX + $FRINGE_SUM_CX + $FRINGE_SUM_DX + $FRINGE_SUM_EX + $FRINGE_SUM_FX + $FRINGE_SUM_GX + $FRINGE_SUM_HX +  $SALARY_SUM_AX + $SALARY_SUM_BX + $SALARY_SUM_CX + $SALARY_SUM_DX + $SALARY_SUM_EX + $SALARY_SUM_FX + $SALARY_SUM_GX  + $SALARY_SUM_HX  " />


</xsl:template>

<xsl:template name="InnerSalarySubtotal" >
	<xsl:param name="k" />
	<xsl:for-each select="$PERSON_OTHER" >
		<xsl:if test="position() = $k " >
			<xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY) " />
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
			<xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT) " />
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
			<xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_FRINGE_BENEFIT_AMOUNT) + sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD[@PERIOD_NUMBER = $Period_Number]/PERSONNEL/PERSON[ @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER and APPOINTMENT/@APPOINTMENT_CODE = current()/APPOINTMENT/@APPOINTMENT_CODE ] /@AGENCY_AMOUNT_SALARY) " />
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
		
	<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
	<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
		/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = $Period_Number]/MODULAR_ADJUSTMENT">
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
		
	<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
	<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
		/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = $Period_Number]/MODULAR_ADJUSTMENT">
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
		
	<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
	<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
		/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = $Period_Number]/MODULAR_ADJUSTMENT">
		4
	</xsl:when>	

	<xsl:otherwise>
		5
	</xsl:otherwise>

	</xsl:choose>
</xsl:template>

<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
<xsl:template name="Other_4">
	<xsl:choose>
		<xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS
			/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER = $Period_Number]/MODULAR_ADJUSTMENT">
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
	
	<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
	<xsl:variable name="Var_4">
		<xsl:call-template name="Other_4"/><fo:block padding="1mm"/>
	</xsl:variable>
	

<xsl:if test="$n = 1" >
	<xsl:choose>
		<xsl:when test="$Var_1 = 1  ">
			Fee Remissions
		</xsl:when>

		<xsl:when test="$Var_1 = 2 ">
			Subject Payments
		</xsl:when>

		<xsl:when test="$Var_1 = 3 ">
			Fellowships
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
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
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
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
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
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
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>

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
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4 ">
			Modular Adjustment
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4">
			Modular Adjustment
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<!-- 4/25/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4">
			Modular Adjustment
		</xsl:when>	
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="1" />
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
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4">
			Modular Adjustment
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3  ">
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

<!--
	$Var_1 = 1 means there are Fee Remissions.
	$Var_1 = 2 means there are no Fee Remissions, but there are Subject Payments.
	$Var_1 = 3 means there are no Fee Remissions or Subject Payments, but there are Fellowships.
	$Var_1 = 4 means there are no Fee Remissions, Subject Payments, or Fellowships.
	$Var_1 = 2 means there are Subject Payments, but says nothing about Fee Remissons.
	etc., etc.

 When the first three spaces contain aggregates, and the sum of filtered Other Expenses plus Participant expenses is equal to 6, then call Participant_Include -->

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and 
			$Other_Count + $Participant_Count = 2 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Other_Count + $Participant_Count &gt; 2 ">
			See Justification
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4 and $Other_Count + $Participant_Count = 3 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4 and 
			($Other_Count + $Participant_Count) &gt; 3 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5 and 
			($Other_Count + $Participant_Count) = 2 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5 and 
			($Other_Count + $Participant_Count) &gt; 2 ">
			See Justification
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4 and 
			($Other_Count + $Participant_Count) = 3 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5 and 
			($Other_Count + $Participant_Count) = 2 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4 and 
			($Other_Count + $Participant_Count) &gt; 3 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5 and 
			($Other_Count + $Participant_Count) &gt; 2 ">
			See Justification
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 and ($Other_Count + $Participant_Count) = 3 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 and ($Other_Count + $Participant_Count) &gt; 3 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_3 = 5 and ($Other_Count + $Participant_Count) = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 and ($Other_Count + $Participant_Count) &gt; 4 ">
			See Justification
		</xsl:when>

		<xsl:when test="$Var_1 = 2 and $Var_2 = 3 and $Other_Count + $Participant_Count = 3 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 2 and $Var_2 = 3 and $Other_Count + $Participant_Count &gt; 3 ">
			See Justification
		</xsl:when>

		<xsl:when test="$Var_1 = 2 and $Var_2 = 4 and $Other_Count + $Participant_Count = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 2 and $Var_2 = 4 and $Other_Count + $Participant_Count &gt; 4 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Other_Count + $Participant_Count = 3 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and  $Var_3 = 3 and $Other_Count + $Participant_Count &gt; 3 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and  $Var_3 = 4 and $Other_Count + $Participant_Count = 3 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 and $Other_Count + $Participant_Count &gt; 3 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and  $Var_3 = 5 and $Other_Count + $Participant_Count = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 and $Other_Count + $Participant_Count &gt; 4 ">
			See Justification
		</xsl:when>
		

		<xsl:when test="$Var_1 = 3 and $Other_Count + $Participant_Count = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 3  and $Other_Count + $Participant_Count &gt; 4 ">
			See Justification
		</xsl:when>

		<xsl:when test="$Var_1 = 4 and ($Other_Count + $Participant_Count) = 4 ">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 4  and ($Other_Count + $Participant_Count) &gt; 4 ">
			See Justification
		</xsl:when>
		
		<xsl:when test="$Var_1 = 5 and ($Other_Count + $Participant_Count) = 5">
			<xsl:call-template name="Participant_Include" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 5 and ($Other_Count + $Participant_Count) &gt; 5 ">
			See Justification
		</xsl:when>
		
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
	
	<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
	<xsl:variable name="Var_4" >
		<xsl:call-template name="Other_4" /><fo:block padding="1mm" />
	</xsl:variable>

<xsl:if test="$n = 1" >
	<xsl:choose>
			<xsl:when test="$Var_1 = 1  ">

<xsl:value-of select="format-number(sum( key('SUB_CATEGORY', 'Fee Remissions')/AGENCY_REQUEST_AMOUNT), '###,###')" />

		</xsl:when>

		<xsl:when test="$Var_1 = 2 ">

<xsl:value-of select="format-number(sum( key('SUB_CATEGORY', 'Subject Payments')/AGENCY_REQUEST_AMOUNT), '###,###')" />

		</xsl:when>

		<xsl:when test="$Var_1 = 3 ">
<xsl:value-of select="format-number(sum( key('CATEGORY', 'Fellowships')/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
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

<!--  [2]   ******************************************************************************* BBBBBBBBBB  -->


<xsl:if test="$n = 2" >
	<xsl:choose>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2  ">
<xsl:value-of select="format-number(sum( key('SUB_CATEGORY', 'Subject Payments')/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 3  ">
<xsl:value-of select="format-number(sum( key('CATEGORY', 'Fellowships')/AGENCY_REQUEST_AMOUNT), '###,###')" />
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
<xsl:value-of select="format-number(sum( key('CATEGORY', 'Fellowships')/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 ">
			<xsl:value-of select="$S1_ModularAdjustment_Display"/>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
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
		
		<xsl:when test="$Var_1 = 5 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>

	</xsl:choose>
</xsl:if>

<xsl:if test="$n = 3" >
	<xsl:choose>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3  ">
<xsl:value-of select="format-number(sum( key('CATEGORY', 'Fellowships')/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4 ">
			<xsl:value-of select="$S1_ModularAdjustment_Display"/>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4">
			<xsl:value-of select="$S1_ModularAdjustment_Display"/>
		</xsl:when>
		
		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
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
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_3 = 3 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:when>

		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 1 and $Var_3 = 4 ">
			<xsl:value-of select="$S1_ModularAdjustment_Display"/>
		</xsl:when>

		<!-- 4/29/2005 dterret: Modular adjustment addition to Other Expenses. -->
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4">
			<xsl:value-of select="$S1_ModularAdjustment_Display"/>
		</xsl:when>
		
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
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
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
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 ">
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
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and $Var_4 = 4 and
			($Other_Count + $Participant_Count) &gt; 1 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				/AGENCY_REQUEST_AMOUNT) , '###,###')" />	
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and 
			$Other_Count + $Participant_Count = 2 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>

<!-- 	This goes with "See Justification."  ^^^^^^^^^^^^^^^^^
   		When the first three spaces contain aggregates, and the sum of filtered Other Expenses plus 
			Participant expenses is equal to or greater than 7, and no Participant Expenses remain, 
			we sum the remaining Other Expenses  Otherwise, we add the remaining Participant Expenses 
			to whatever Other Expenses exist.
-->
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and 
			$Other_Count + $Participant_Count &gt; 2 and $Participant_Count = 0 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 1 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 3 and 
			($Other_Count + $Participant_Count) &gt; 2  ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 1]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>

		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4 and 
			($Other_Count + $Participant_Count) = 2 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>

		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4 and 
			($Other_Count + $Participant_Count) &gt; 2 and $Participant_Count &lt;= 1 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 1 - $Participant_Count]/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 4  and 
			($Other_Count + $Participant_Count) &gt; 2 and $Participant_Count &gt; 1 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 1]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5 and 
			($Other_Count + $Participant_Count) = 3 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5 and 
			($Other_Count + $Participant_Count) &gt; 3 and $Participant_Count &lt;= 2">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 2 - $Participant_Count]/AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 2 and $Var_3 = 5  and 
			($Other_Count + $Participant_Count) &gt; 3 and $Participant_Count &gt; 2 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 2]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->
		

		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4 and 
			($Other_Count + $Participant_Count) = 2 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>

		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4 and 
			($Other_Count + $Participant_Count) &gt; 2 and $Participant_Count &lt;= 1">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 1 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
	
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 4 and
			($Other_Count + $Participant_Count) &gt; 2 and $Participant_Count &gt; 1 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')[position() &gt; 1]
				/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
			
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->
			
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5 and 
			($Other_Count + $Participant_Count) = 3 ">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
			
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->
			
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5 and 
			($Other_Count + $Participant_Count) &gt; 3 and $Participant_Count &lt;= 2">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 2 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 3 and $Var_4 = 5 and 
			($Other_Count + $Participant_Count) &gt; 3 and $Participant_Count &gt; 2 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')[position() &gt; 2]
				/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
			
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 1 and $Var_2 = 4  and ($Other_Count + $Participant_Count) = 3">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>

		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 and ($Other_Count + $Participant_Count) &gt; 3 and 
			$Participant_Count &lt;= 2 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 2 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<xsl:when test="$Var_1 = 1 and $Var_2 = 4 and ($Other_Count + $Participant_Count) &gt; 3 and 
			$Participant_Count &gt; 2 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 2]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>

		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ alfred  -->
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5  and ($Other_Count + $Participant_Count) = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 and ($Other_Count + $Participant_Count) &gt; 4 and 
			$Participant_Count &lt;= 3 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 3 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 1 and $Var_2 = 5 and ($Other_Count + $Participant_Count) &gt; 4 and 
			$Participant_Count &gt; 3 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 3]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ alfred  -->

		<xsl:when test="$Var_1 = 2 and  $Var_3 = 3 and $Var_4 = 4 and 
			($Other_Count + $Participant_Count) = 2">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:when>

		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 2  and $Var_3 = 3 and $Var_4 = 4 and
			($Other_Count + $Participant_Count) &gt; 2 and $Participant_Count &lt;= 1">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 1 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 4 and
			($Other_Count + $Participant_Count) &gt; 2 and $Participant_Count &gt; 1">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 1]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->
		
		<xsl:when test="$Var_1 = 2 and  $Var_3 = 3 and $Var_4 = 5 and 
			($Other_Count + $Participant_Count) = 3">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->
		
		<xsl:when test="$Var_1 = 2  and $Var_3 = 3 and $Var_4 = 5 and 
			($Other_Count + $Participant_Count) &gt; 3 and $Participant_Count &lt;= 2">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 2 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 3 and $Var_4 = 5 and 
			($Other_Count + $Participant_Count) &gt; 3 and $Participant_Count &gt; 2 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 2]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 2 and  $Var_3 = 4  and $Other_Count + $Participant_Count = 3">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>

		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 2 and  $Var_3 = 4 and 
			($Other_Count + $Participant_Count) &gt; 3 and $Participant_Count &lt;= 2">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 2 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<xsl:when test="$Var_1 = 2 and $Var_3 = 4 and 
			($Other_Count + $Participant_Count) &gt; 3 and $Participant_Count &gt; 2 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 2]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>

		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->
		
		<xsl:when test="$Var_1 = 2 and  $Var_3 = 5  and $Other_Count + $Participant_Count = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->
		
		<xsl:when test="$Var_1 = 2 and  $Var_3 = 5 and $Other_Count + $Participant_Count &gt; 4 and 
			($Participant_Count &lt; 3 or $Participant_Count = 3) ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 3 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 2 and $Var_3 = 5 and $Other_Count + $Participant_Count &gt; 4 and 
			$Participant_Count &gt; 3 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 3]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->
		
		<xsl:when test="$Var_1 = 2 and $Var_2 = 3  and $Other_Count + $Participant_Count = 3">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>

		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 2 and $Var_2 = 3 and $Other_Count + $Participant_Count &gt; 3 and 
			($Participant_Count &lt; 2 or $Participant_Count = 2) ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 2 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<xsl:when test="$Var_1 = 2 and $Var_2 = 3 and $Other_Count + $Participant_Count &gt; 3 and 
			$Participant_Count &gt; 2 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 2]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 2 and $Var_2 = 4  and $Other_Count + $Participant_Count = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>

		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 2 and $Var_2 = 4 and $Other_Count + $Participant_Count &gt; 4 and 
			($Participant_Count &lt; 3 or $Participant_Count = 3) ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 3 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<xsl:when test="$Var_1 = 2 and $Var_2 = 4 and $Other_Count + $Participant_Count &gt; 4 and 
			$Participant_Count &gt; 3 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 3]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 3 and $Var_4 = 4 and ($Other_Count + $Participant_Count) = 3">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:when>

		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 3 and $Var_4 = 4 and 
			($Other_Count + $Participant_Count) &gt; 3 and $Participant_Count &lt;= 2 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 2 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<xsl:when test="$Var_1 = 3 and $Var_4 = 4 and 
			($Other_Count + $Participant_Count) &gt; 3 and $Participant_Count &gt; 2 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 2]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5 and ($Other_Count + $Participant_Count) = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>
		
		<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5 and 
			($Other_Count + $Participant_Count) &gt; 4 and $Participant_Count &lt;= 3">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 3 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 3 and $Var_4 = 5 and 
			($Other_Count + $Participant_Count) &gt; 4 and $Participant_Count &gt; 3 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 3]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
		<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 4 and ($Other_Count + $Participant_Count) = 4">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:when>

<!--  This goes with "See Justification."  ^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 4 and ($Other_Count + $Participant_Count) &gt; 4 and 
			$Participant_Count &lt;= 3 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 3 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>

		<xsl:when test="$Var_1 = 4 and ($Other_Count + $Participant_Count) &gt; 4 and 
			$Participant_Count &gt; 3 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 3]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
<!--  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  -->

		<xsl:when test="$Var_1 = 5 and ($Other_Count + $Participant_Count) = 5">
			<xsl:call-template name="Participant_Include_Amount" >
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="Var_1 = 5 and ($Other_Count + $Participant_Count) &gt; 5 and 
			$Participant_Count &lt;= 4">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				[position() &gt; 4 - $Participant_Count] /AGENCY_REQUEST_AMOUNT), '###,###')" />
		</xsl:when>
		
		<xsl:when test="$Var_1 = 5 and ($Other_Count + $Participant_Count) &gt; 5 and 
			$Participant_Count &gt; 4 ">
			<xsl:value-of select="format-number(sum(key('CATEGORY_Filtered', 'Other Expenses')
				/AGENCY_REQUEST_AMOUNT) + sum(key('CATEGORY', 'Participant Expenses')
				[position() &gt; 4]/AGENCY_REQUEST_AMOUNT) , '###,###')" />
		</xsl:when>
		
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
				<xsl:value-of select="format-number(key('CATEGORY_Participant_Filtered', 'Participant Expenses')[$index]/AGENCY_REQUEST_AMOUNT, '###,###')" />
			</xsl:when>

			<xsl:when test="key('CATEGORY_Filtered', 'Other Expenses')[$index - $Participant_Count]/AGENCY_REQUEST_AMOUNT">
			<xsl:value-of select="format-number(key('CATEGORY_Filtered', 'Other Expenses')[$index - $Participant_Count]/AGENCY_REQUEST_AMOUNT, '###,###')" />
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
