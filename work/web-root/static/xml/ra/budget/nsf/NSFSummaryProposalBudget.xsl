<?xml version="1.0" encoding="UTF-8" ?>
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
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  xmlns:xalan="http://xml.apache.org/xalan">
  
    <!--                           VERSION HISTORY
    - 05/2006: pcberg@indiana.edu, refactoring for KRA.
               - Added fo:block to fo:flow in order to correct error "org.apache.fop.apps.FOPException: fo:flow must contain block-level children".
               - Indiana University = My University
               - @TASK = @TASK_NUMBER
               - @PERIOD = @PERIOD_NUMBER
               - Removed unused attribute-set whiteSpaceText and whiteCellBlock. Those seemed to have killed evaluation in KRA FOP jars.
    -->
  
  <!-- ******************* **************************** ******************* -->
  <!-- *******************            Keys              ******************* -->
  <!-- ******************* **************************** ******************* -->
  
  <xsl:key name="tpPeriodKey"
    match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD"
    use="@PERIOD_NUMBER"/>
  
  <xsl:key name="persNameKey"
    match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD/PERSONNEL/PERSON"
    use="NAME"/>
  
  <xsl:key name="persPeriodKey"
    match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD/PERSONNEL/PERSON"
    use="../../@PERIOD_NUMBER"/>
  
  <xsl:key name="apptPersKey"
    match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD/PERSONNEL/PERSON"
    use="APPOINTMENT/@APPOINTMENT_CODE"/>
  
  <xsl:key name="apptPersNameKey"
    match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD/PERSONNEL/PERSON"
    use="concat( APPOINTMENT/@APPOINTMENT_CODE,' ',NAME )"/>
	
	<xsl:key name="apptPersPeriodKey"
		match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD/PERSONNEL/PERSON"
		use="concat( APPOINTMENT/@APPOINTMENT_CODE,' ',../../@PERIOD_NUMBER )"/>
  
  <xsl:key name="persApptPeriodNameKey"
    match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD/PERSONNEL/PERSON"
    use="concat( APPOINTMENT/@APPOINTMENT_CODE,' ',../../@PERIOD_NUMBER,' ',NAME )"/>
	
	<xsl:key name="othProfsPeriodKey"
		match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
		/PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'PS'] |
		/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
		/PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'BI']"
		use="../../@PERIOD_NUMBER"/>
	
	<xsl:key name="othProfsNameKey"
		match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
		/PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'PS'] |
		/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
		/PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'BI']"
		use="NAME"/>
	
	<xsl:key name="othProfsPeriodNameKey"
		match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
		/PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'PS'] |
		/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
		/PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'BI']"
		use="concat(../../@PERIOD_NUMBER,' ',NAME)"/>

  <xsl:key name="senPersNameKey"
    match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'A1'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'A2'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'AS']"
    use="NAME"/>
  
  <xsl:key name="senPersPeriodKey" 
    match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'A1'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'A2'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'AS']"
    use="../../@PERIOD_NUMBER"/>
  
  <xsl:key name="senPersPNKey"
    match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'A1'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'A2'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'AS']"
    use="concat( ../../@PERIOD_NUMBER,' ',NAME )"/>
   
  <xsl:key name="othOthPersPeriodKey"
    match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'H2'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'H1'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'SH'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'WS']"
    use="../../@PERIOD_NUMBER"/>
  
  <xsl:key name="othOthPersNameKey"
    match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'H2'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'H1'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'SH'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'WS']"
    use="NAME"/>
    
  <xsl:key name="othOthPersPeriodNameKey"
    match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'H2'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'H1'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'SH'] |
    /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
    /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'WS']"
    use="concat( ../../@PERIOD_NUMBER,' ',NAME )"/>
    
  <xsl:key name="taskKey"
    match="/PROPOSAL/BUDGET/TASKS/TASK"
    use="TASK_NUMBER"/>
  
  <xsl:key name="equipPeriodKey"
    match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL/NON_PERSONNEL_ITEM
    [CATEGORY = 'Equipment']"
    use="../../@PERIOD_NUMBER"/>
  
  <xsl:key name="idcRateKey" 
    match="/PROPOSAL/BUDGET/INDIRECT_COST/INDIRECT_COST_TASK/INDIRECT_COST_TASK_PERIOD" 
    use="@RATE"/>
  
  <xsl:key name="idcRatePeriodKey"
    match="/PROPOSAL/BUDGET/INDIRECT_COST/INDIRECT_COST_TASK/INDIRECT_COST_TASK_PERIOD"
    use="concat(@PERIOD_NUMBER,' ',@RATE)"/>
  
  <!-- ******************* **************************** ******************* -->
  <!-- *******************          Variables           ******************* -->
  <!-- ******************* **************************** ******************* -->
  
  <!-- These should eventually be passed in as parameters. -->
  <xsl:variable name="organization" select="'My University'"/>
    
  <!-- ******************* **************************** ******************* -->
  <!-- *******************          Attributes          ******************* -->
  <!-- ******************* **************************** ******************* -->
  
  <xsl:attribute-set name="bCell">
    <xsl:attribute name="border">solid</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="gbCell" use-attribute-sets="bCell">
    <xsl:attribute name="background-color">#dddddd</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="ansBCell" use-attribute-sets="bCell">
    <xsl:attribute name="display-align">after</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="ansBCentCell" use-attribute-sets="ansBCell">
    <xsl:attribute name="text-align">center</xsl:attribute>
  </xsl:attribute-set>
    
  <xsl:attribute-set name="whiteSpaceBlock">
    <xsl:attribute name="white-space-collapse">false</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="normalText">
    <xsl:attribute name="font-size">8pt</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="labelText" use-attribute-sets="normalText">
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="smLabText" use-attribute-sets="labelText">
    <xsl:attribute name="font-size">7pt</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="wsSmLabText" use-attribute-sets="smLabText"/>
  
  <xsl:attribute-set name="smCLabText" use-attribute-sets="smLabText">
    <xsl:attribute name="text-align">center</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="reallySmCLabText" use-attribute-sets="labelText">
    <xsl:attribute name="font-size">5pt</xsl:attribute>
    <xsl:attribute name="text-align">center</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="answerText" use-attribute-sets="normalText">
    <xsl:attribute name="font-family">serif</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="smAnsText" use-attribute-sets="answerText">
    <xsl:attribute name="font-size">7pt</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="wsAnswerText" use-attribute-sets="answerText whiteSpaceBlock"/>
  
  <xsl:attribute-set name="wsSmAnsText" use-attribute-sets="wsAnswerText">
    <xsl:attribute name="font-size">7pt</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="stdHeight">
    <xsl:attribute name="line-height">1.0</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="innerTC">
    <xsl:attribute name="padding-before">0mm</xsl:attribute>
    <xsl:attribute name="padding-after">0mm</xsl:attribute>
    <xsl:attribute name="margin-left">0mm</xsl:attribute>
    <xsl:attribute name="margin-right">0mm</xsl:attribute>
    <xsl:attribute name="display-align">after</xsl:attribute>
  </xsl:attribute-set>
  
  <!-- ******************* **************************** ******************* -->
  <!-- *******************         Root Template        ******************* -->
  <!-- ******************* **************************** ******************* -->
  <!-- The root template sets the table layout (size, page numbers, etc.).  -->
  
  <xsl:template match="/">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">   
      <fo:layout-master-set>     
        <fo:simple-page-master master-name="all_pages"
          page-width="216mm" page-height="279mm"
          margin-top="10mm"  margin-bottom="10mm"
          margin-left="15.5mm" margin-right="15.5mm">
          <fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>
            
      <fo:page-sequence master-reference="all_pages"
        initial-page-number="1" language="en" country="us">
        <fo:flow flow-name="xsl-region-body">
          <!-- Gets pagination right. -->
          <fo:block break-before="page">
            <xsl:apply-templates select="/PROPOSAL/BUDGET"/>
          </fo:block>
        </fo:flow>
      </fo:page-sequence>
      
    </fo:root>
  </xsl:template>

  <!-- ******************* **************************** ******************* -->
  <!-- *******************   Print Periods Template     ******************* -->
  <!-- ******************* **************************** ******************* -->
  
  <xsl:template match="/PROPOSAL/BUDGET">
    <xsl:for-each select="PERIODS/PERIOD">
      <xsl:variable name="periodNum" select="PERIOD_NUMBER"/>
      <!-- Print the main table with values summed over the tasks in each
           period.  The context is the first task of the period. -->
      <xsl:apply-templates select="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
        [generate-id(.) = generate-id(key('tpPeriodKey',$periodNum))]"
        mode="allTasksSummaries">
        <xsl:with-param name="periodNum" select="$periodNum"/>
      </xsl:apply-templates>
      <fo:block break-before="page"/>
    </xsl:for-each>
    <xsl:apply-templates select="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
      [@TASK_NUMBER = '1' and @PERIOD_NUMBER = '1']"
      mode="allPeriodsSummary"/>
  </xsl:template>

  <!-- ******************* **************************** ******************* -->
  <!-- *******************  Period Definition Template  ******************* -->
  <!-- ******************* **************************** ******************* -->
  
  <xsl:template match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD" mode="allTasksSummaries">
    <xsl:param name="periodNum"/>
    
    <!-- Period variables -->
    <xsl:variable name="periodStart" select="/PROPOSAL/BUDGET
          /PERIODS/PERIOD[PERIOD_NUMBER = $periodNum]/START_DATE"/>
    <xsl:variable name="periodStop" select="/PROPOSAL/BUDGET
          /PERIODS/PERIOD[PERIOD_NUMBER = $periodNum]/STOP_DATE"/>
    
    <!-- Distinct Senior Personnel used in this Period -->
    <xsl:variable name="senPers" select="key('senPersPeriodKey',$periodNum )
          [ generate-id(.) = generate-id( key('senPersPNKey',
          concat( $periodNum,' ',NAME ) ) ) ]"/>

    <!-- Equipment ordered this Period expensive enough to be listed. -->
    <xsl:variable name="expensiveEquipment"
          select="key( 'equipPeriodKey',$periodNum )/AGENCY_REQUEST_AMOUNT"/>
    
    <!-- Node sets containing groups of non-senior personnel. -->
  	<xsl:variable name="othProfs"
  				select="key('othProfsPeriodKey',$periodNum)
  				[ generate-id(.) = generate-id( key( 'othProfsPeriodNameKey',
  				concat( $periodNum,' ',NAME ))[1])]"/>
    <xsl:variable name="gradStudents"
    			select="key('apptPersPeriodKey',concat( 'GR',' ',$periodNum ))
    			[ generate-id(.) = generate-id( key( 'persApptPeriodNameKey', 
    			concat( 'GR',' ',$periodNum, ' ',NAME ) )[1])]"/>
    <xsl:variable name="othOthPers" select="key( 'othOthPersPeriodKey', $periodNum)
          [ generate-id(.) = generate-id( key( 'othOthPersPeriodNameKey', 
          concat( $periodNum,' ',NAME ) ) )]"/> 
    
    <!-- Variables counting numbers of people -->
    <xsl:variable name="numSenPers" select="count($senPers)"/>
    <xsl:variable name="numPostDocs" select="'*'"/>
    <xsl:variable name="numOthProfs" select="count($othProfs)"/>
    <xsl:variable name="numGradStudents" select="count($gradStudents)"/>
    <xsl:variable name="numUgradStudents" select="'*'"/>
    <xsl:variable name="numSecretarial" select="'*'"/>
    <xsl:variable name="numOtherPers" select="count($othOthPers)"/>
    <xsl:variable name="numParticipants" select="'?'"/>
    
    <!-- These sums of salaries are over all Tasks of the Period. -->
    <xsl:variable name="senPersSalaries" select="sum(key('senPersPeriodKey',$periodNum)
          /@AGENCY_AMOUNT_SALARY)"/>
    <xsl:variable name="postDocSalaries" select="'*'"/>
    <xsl:variable name="othProfSalaries" select="sum(key('othProfsPeriodKey',$periodNum)
         	/@AGENCY_AMOUNT_SALARY)"/>
    <xsl:variable name="gradStudentSalaries" select="sum(key('apptPersKey','GR')
          [../../@PERIOD_NUMBER = $periodNum]/@AGENCY_AMOUNT_SALARY)"/>
    <xsl:variable name="ugradStudentSalaries" select="'-'"/>
    <xsl:variable name="secretarialSalaries" select="'-'"/>
    <xsl:variable name="otherPersSalaries" select="sum(key('othOthPersPeriodKey',
          $periodNum)/@AGENCY_AMOUNT_SALARY)"/>
    
    <!-- These are derived salary values for addition, which become zero when undefined. -->
    <xsl:variable name="senPersSalaries_Add">
      <xsl:choose><xsl:when test="number($senPersSalaries)">
        <xsl:value-of select="$senPersSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="postDocSalaries_Add">
      <xsl:choose><xsl:when test="number($postDocSalaries)">
        <xsl:value-of select="$postDocSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="othProfSalaries_Add">
      <xsl:choose><xsl:when test="number($othProfSalaries)">
        <xsl:value-of select="$othProfSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="gradStudentSalaries_Add">
      <xsl:choose><xsl:when test="number($gradStudentSalaries)">
        <xsl:value-of select="$gradStudentSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="ugradStudentSalaries_Add">
      <xsl:choose><xsl:when test="number($ugradStudentSalaries)">
        <xsl:value-of select="$ugradStudentSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="secretarialSalaries_Add">
      <xsl:choose><xsl:when test="number($secretarialSalaries)">
        <xsl:value-of select="$secretarialSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="otherPersSalaries_Add">
      <xsl:choose><xsl:when test="number($otherPersSalaries)">
        <xsl:value-of select="$otherPersSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    
    <!-- Totals -->
    <xsl:variable name="salaryTotal" select="$senPersSalaries_Add + $postDocSalaries_Add +
          $othProfSalaries_Add + $gradStudentSalaries_Add +
          $ugradStudentSalaries_Add + $secretarialSalaries_Add +
          $otherPersSalaries_Add"/>
    <xsl:variable name="fringeBenefitsTotal" select="sum( key('persPeriodKey',$periodNum)
          /@AGENCY_FRINGE_BENEFIT_AMOUNT )"/>
    <xsl:variable name="salariesBenefitsTotal" select="$salaryTotal + $fringeBenefitsTotal"/>
    <xsl:variable name="equipmentTotal" select="sum(key('tpPeriodKey',$periodNum)/NON_PERSONNEL
          /NON_PERSONNEL_ITEM[CATEGORY = 'Equipment']/AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="domesticTravelTotal"  
          select="sum(NON_PERSONNEL/NON_PERSONNEL_ITEM[(CATEGORY = 'Travel') and 
          ((SUB_CATEGORY = 'Out-of-State') or (SUB_CATEGORY = 'In-State') or
    			(SUB_CATEGORY = 'Non-Employee'))]/AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="foreignTravelTotal"
          select="sum(NON_PERSONNEL/NON_PERSONNEL_ITEM[(CATEGORY = 'Travel') and 
          (SUB_CATEGORY = 'Foreign')]/AGENCY_REQUEST_AMOUNT)"/>
    
    <!-- Participant Support -->
    <xsl:variable name="participantStipends" select="sum(key('tpPeriodKey',$periodNum)
          /NON_PERSONNEL/NON_PERSONNEL_ITEM[((CATEGORY = 'Participant Expenses') 
          and (SUB_CATEGORY = 'Participant Stipend')) or
    			((CATEGORY = 'Fellowships') and ((SUB_CATEGORY = 'Pre-Doctoral Stipends') or
    			(SUB_CATEGORY = 'Post-Doctoral Stipends')))]
          /AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="participantTravel" select="sum(key('tpPeriodKey',$periodNum)
          /NON_PERSONNEL/NON_PERSONNEL_ITEM[(CATEGORY = 'Participant Expenses') 
          and (SUB_CATEGORY = 'Participant Travel')]
          /AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="participantSubsistence" select="sum(key('tpPeriodKey',$periodNum)
          /NON_PERSONNEL/NON_PERSONNEL_ITEM[(CATEGORY = 'Participant Expenses') 
          and(SUB_CATEGORY = 'Participant Subsistence')]
          /AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="participantOther" select="sum(key('tpPeriodKey',$periodNum)
          /NON_PERSONNEL/NON_PERSONNEL_ITEM[((CATEGORY = 'Participant Expenses') 
          and ((SUB_CATEGORY = 'Participant Other') or 
          (SUB_CATEGORY = 'Participant Tuition'))) or
    			((CATEGORY = 'Fellowships') and ((SUB_CATEGORY = 'Student Health Insurance') or
    			(SUB_CATEGORY = 'Tuition') or (SUB_CATEGORY = 'Institutional Allowance')))]
          /AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="participantCosts" select="$participantStipends +
    			$participantTravel + $participantSubsistence + $participantOther"/>
    
    <!-- Other Direct Cost variables -->
    <xsl:variable name="materialsSupplies" select="sum(key('tpPeriodKey',$periodNum)/NON_PERSONNEL
          /NON_PERSONNEL_ITEM[CATEGORY = 'Supplies']
          /AGENCY_REQUEST_AMOUNT)"/> <!-- (applies to all subcategories) --> 
    <xsl:variable name="publication" select="sum(key('tpPeriodKey',$periodNum)/NON_PERSONNEL
          /NON_PERSONNEL_ITEM[(CATEGORY = 'Other Expenses') and
          (SUB_CATEGORY = 'Publications')]/AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="consultantServices" select="sum(key('tpPeriodKey',$periodNum)/NON_PERSONNEL
          /NON_PERSONNEL_ITEM[CATEGORY = 'Consultants']
          /AGENCY_REQUEST_AMOUNT)"/> <!-- (applies to all subcategories) -->   
    <xsl:variable name="compServices" select="'?'"/>
    <xsl:variable name="compServices_Add">
      <xsl:choose><xsl:when test="number(compServices)">
        <xsl:value-of select="compServices"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="subawards" select="sum(key('tpPeriodKey',$periodNum)/NON_PERSONNEL
          /NON_PERSONNEL_ITEM[CATEGORY = 'Subcontractors']
          /AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="othOthDirectCosts" select="sum(key('tpPeriodKey',$periodNum)/NON_PERSONNEL
          /NON_PERSONNEL_ITEM[(CATEGORY = 'Other Expenses') and
          (SUB_CATEGORY != 'Publications')]
          /AGENCY_REQUEST_AMOUNT)"/> <!-- (does not apply to all subcategories) -->
    
    <xsl:variable name="totalOthDirectCosts"
          select="$materialsSupplies + $publication + $consultantServices + $subawards 
          + $othOthDirectCosts + $compServices_Add"/>
    <xsl:variable name="totalDirectCosts" 
          select="$salariesBenefitsTotal + $equipmentTotal + $domesticTravelTotal +
          $foreignTravelTotal + $participantCosts + $totalOthDirectCosts"/>
    <xsl:variable name="totalIndirectCosts" 
          select="sum(/PROPOSAL/BUDGET/INDIRECT_COST
          /INDIRECT_COST_TASK
          /INDIRECT_COST_TASK_PERIOD[@PERIOD_NUMBER = $periodNum]
          /INDIRECT_COST_TASK_PERIOD_AGENCY_AMOUNT
          /AGENCY_CALCULATED_INDIRECT_COST)"/>

    <xsl:variable name="totalDirectIndirectCosts" 
          select="$totalDirectCosts + $totalIndirectCosts"/>
    <xsl:variable name="residualFunds" select="'?'"/>
    <xsl:variable name="requestAmt" select="$totalDirectIndirectCosts"/>
    
    <xsl:call-template name="main">
      <xsl:with-param name="periodNum" select="$periodNum"/>
      <xsl:with-param name="periodStart" select="$periodStart"/>
      <xsl:with-param name="periodStop" select="$periodStop"/>
      <xsl:with-param name="senPers" select="$senPers"/>  
      <xsl:with-param name="expensiveEquipment" select="$expensiveEquipment"/>
      <xsl:with-param name="othProfs" select="$othProfs"/>
      <xsl:with-param name="gradStudents" select="$gradStudents"/>
      <xsl:with-param name="othOthPers" select="$othOthPers"/>
      <xsl:with-param name="numSenPers" select="$numSenPers"/>
      <xsl:with-param name="numPostDocs" select="$numPostDocs"/>
      <xsl:with-param name="numOthProfs" select="$numOthProfs"/>
      <xsl:with-param name="numGradStudents" select="$numGradStudents"/>
      <xsl:with-param name="numUgradStudents" select="$numUgradStudents"/>
      <xsl:with-param name="numSecretarial" select="$numSecretarial"/>
      <xsl:with-param name="numOtherPers" select="$numOtherPers"/>
      <xsl:with-param name="numParticipants" select="$numParticipants"/>
      <xsl:with-param name="senPersSalaries" select="$senPersSalaries"/>
      <xsl:with-param name="postDocSalaries" select="$postDocSalaries"/>
      <xsl:with-param name="othProfSalaries" select="$othProfSalaries"/>
      <xsl:with-param name="gradStudentSalaries" select="$gradStudentSalaries"/>
      <xsl:with-param name="ugradStudentSalaries" select="$ugradStudentSalaries"/>
      <xsl:with-param name="secretarialSalaries" select="$secretarialSalaries"/>
      <xsl:with-param name="otherPersSalaries" select="$otherPersSalaries"/>
      <xsl:with-param name="salaryTotal" select="$salaryTotal"/>
      <xsl:with-param name="fringeBenefitsTotal" select="$fringeBenefitsTotal"/>
      <xsl:with-param name="salariesBenefitsTotal" select="$salariesBenefitsTotal"/>
      <xsl:with-param name="equipmentTotal" select="$equipmentTotal"/>
      <xsl:with-param name="domesticTravelTotal" select="$domesticTravelTotal"/>
      <xsl:with-param name="foreignTravelTotal" select="$foreignTravelTotal"/>
      <xsl:with-param name="participantStipends" select="$participantStipends"/>
      <xsl:with-param name="participantTravel" select="$participantTravel"/>
      <xsl:with-param name="participantSubsistence" select="$participantSubsistence"/>
      <xsl:with-param name="participantOther" select="$participantOther"/>
      <xsl:with-param name="participantCosts" select="$participantCosts"/>
      <xsl:with-param name="materialsSupplies" select="$materialsSupplies"/>
      <xsl:with-param name="publication" select="$publication"/>
      <xsl:with-param name="consultantServices" select="$consultantServices"/>
      <xsl:with-param name="compServices" select="$compServices"/>
      <xsl:with-param name="subawards" select="$subawards"/>
      <xsl:with-param name="othOthDirectCosts" select="$othOthDirectCosts"/>
      <xsl:with-param name="totalOthDirectCosts" select="$totalOthDirectCosts"/>
      <xsl:with-param name="totalDirectCosts" select="$totalDirectCosts"/>
      <xsl:with-param name="totalIndirectCosts" select="$totalIndirectCosts"/>
      <xsl:with-param name="totalDirectIndirectCosts" select="$totalDirectIndirectCosts"/>
      <xsl:with-param name="residualFunds" select="$residualFunds"/>
      <xsl:with-param name="requestAmt" select="$requestAmt"/>
    </xsl:call-template>
    
  </xsl:template>
  
  <!-- ******************* **************************** ******************* -->
  <!-- ******************  Summary Definition Template  ******************* -->
  <!-- ******************* **************************** ******************* -->
  
  <xsl:template match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD"
    mode="allPeriodsSummary">
    
    <!-- Distinct Senior Personnel used in this Period -->
    <xsl:variable name="senPers" select="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
      /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'A1'] |
      /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
      /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'A2'] |
      /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
      /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'AS']"/>
    
    <!-- Equipment which is expensive enough to be listed. -->
    <xsl:variable name="expensiveEquipment"
      select="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL/NON_PERSONNEL_ITEM
      [CATEGORY = 'Equipment']/AGENCY_REQUEST_AMOUNT"/>
    
    <!-- Node sets containing groups of non-senior personnel. -->
  	<xsl:variable name="othProfs"
  		select="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
  		/PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'PS' and
  		generate-id(.) = generate-id( key( 'othProfsNameKey',NAME ))] |
  		/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
  		/PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'BI' and 
  		generate-id(.) = generate-id( key( 'othProfsNameKey',NAME ))]"/>
    <xsl:variable name="gradStudents" select="key('apptPersKey','GR')
      [generate-id(.) = generate-id( key( 'apptPersNameKey', 
      concat( 'GR',' ',NAME ) ) )]"/>
    <xsl:variable name="othOthPers" select="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
      /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'H2'] |
      /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
      /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'H1'] |
      /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
      /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'SH'] |
      /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD
      /PERSONNEL/PERSON[APPOINTMENT/@APPOINTMENT_CODE = 'WS']"/>
    
    <!-- Variables counting numbers of people -->
    <xsl:variable name="numSenPers" select="count($senPers[ generate-id(.) = 
      generate-id( key('persNameKey',NAME) ) ])"/>
    <xsl:variable name="numPostDocs" select="'*'"/>
    <xsl:variable name="numOthProfs" select="count($othProfs)"/>
    <xsl:variable name="numGradStudents" select="count(key('apptPersKey','GR')
      [generate-id(.) = generate-id( key( 'apptPersNameKey', 
      concat( 'GR',' ',NAME ) ) ) ] )"/>
    <xsl:variable name="numUgradStudents" select="'*'"/>
    <xsl:variable name="numSecretarial" select="'*'"/>
    <xsl:variable name="numOtherPers" 
      select="count(key('apptPersKey','H2')[generate-id(.) = 
      generate-id( key( 'apptPersNameKey', concat( 'H2',' ',NAME ) ) ) ] ) 
      + count(key('apptPersKey','H1')[generate-id(.) = 
      generate-id( key( 'apptPersNameKey', concat( 'H1',' ',NAME ) ) ) ] ) 
      + count(key('apptPersKey','SH')[generate-id(.) = 
      generate-id( key( 'apptPersNameKey', concat( 'SH',' ',NAME ) ) ) ] )
      + count(key('apptPersKey','WS')[generate-id(.) = 
      generate-id( key( 'apptPersNameKey', concat( 'WS',' ',NAME ) ) ) ] )"/>
    <xsl:variable name="numParticipants" select="'?'"/>
    
    <!-- These sums of salaries are over all Tasks of the Period. -->
    <xsl:variable name="senPersSalaries" select="sum($senPers/@AGENCY_AMOUNT_SALARY) div 2"/>
    <xsl:variable name="postDocSalaries" select="'*'"/>
    <xsl:variable name="othProfSalaries" select="sum(key('apptPersKey','PS')
    	/@AGENCY_AMOUNT_SALARY) + sum(key('apptPersKey','BI')/@AGENCY_AMOUNT_SALARY)"/>
    <xsl:variable name="gradStudentSalaries" select="sum(key('apptPersKey','GR')
      /@AGENCY_AMOUNT_SALARY)"/>
    <xsl:variable name="ugradStudentSalaries" select="'-'"/>
    <xsl:variable name="secretarialSalaries" select="'-'"/>
    <xsl:variable name="otherPersSalaries" select="sum($othOthPers/@AGENCY_AMOUNT_SALARY)"/>
    
    <!-- These are derived salary values for addition, which become zero when undefined. -->
    <xsl:variable name="senPersSalaries_Add">
      <xsl:choose><xsl:when test="number($senPersSalaries)">
        <xsl:value-of select="$senPersSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="postDocSalaries_Add">
      <xsl:choose><xsl:when test="number($postDocSalaries)">
        <xsl:value-of select="$postDocSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="othProfSalaries_Add">
      <xsl:choose><xsl:when test="number($othProfSalaries)">
        <xsl:value-of select="$othProfSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="gradStudentSalaries_Add">
      <xsl:choose><xsl:when test="number($gradStudentSalaries)">
        <xsl:value-of select="$gradStudentSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="ugradStudentSalaries_Add">
      <xsl:choose><xsl:when test="number($ugradStudentSalaries)">
        <xsl:value-of select="$ugradStudentSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="secretarialSalaries_Add">
      <xsl:choose><xsl:when test="number($secretarialSalaries)">
        <xsl:value-of select="$secretarialSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="otherPersSalaries_Add">
      <xsl:choose><xsl:when test="number($otherPersSalaries)">
        <xsl:value-of select="$otherPersSalaries"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    
    <!-- Totals -->
    <xsl:variable name="salaryTotal" select="$senPersSalaries_Add + $postDocSalaries_Add +
      $othProfSalaries_Add + $gradStudentSalaries_Add +
      $ugradStudentSalaries_Add + $secretarialSalaries_Add +
      $otherPersSalaries_Add"/>
    <xsl:variable name="fringeBenefitsTotal" select="sum( /PROPOSAL/BUDGET
      /TASK_PERIODS/TASK_PERIOD/PERSONNEL/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT )"/>
    <xsl:variable name="salariesBenefitsTotal" select="$salaryTotal + $fringeBenefitsTotal"/>
    <xsl:variable name="equipmentTotal" select="sum(/PROPOSAL/BUDGET
      /TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL/NON_PERSONNEL_ITEM
      [CATEGORY = 'Equipment']/AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="domesticTravelTotal"  
      select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL
      /NON_PERSONNEL_ITEM[(CATEGORY = 'Travel') and 
      ((SUB_CATEGORY = 'Out-of-State') or (SUB_CATEGORY = 'In-State') or
    	(SUB_CATEGORY = 'Non_Employee'))]/AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="foreignTravelTotal"
      select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL
      /NON_PERSONNEL_ITEM[(CATEGORY = 'Travel') and 
      (SUB_CATEGORY = 'Foreign')]/AGENCY_REQUEST_AMOUNT)"/>
    
    <!-- Participant Support -->
    <xsl:variable name="participantStipends" select="sum(/PROPOSAL/BUDGET
      /TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL/NON_PERSONNEL_ITEM
    	[((CATEGORY = 'Participant Expenses') and (SUB_CATEGORY = 'Participant Stipend')) or
    	((CATEGORY = 'Fellowships') and ((SUB_CATEGORY = 'Pre-Doctoral Stipends') or
    	(SUB_CATEGORY = 'Post-Doctoral Stipends')))]
      /AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="participantTravel" select="sum(/PROPOSAL/BUDGET
      /TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL/NON_PERSONNEL_ITEM
      [(CATEGORY = 'Participant Expenses') and (SUB_CATEGORY = 'Participant Travel')]
      /AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="participantSubsistence" select="sum(/PROPOSAL/BUDGET
      /TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL/NON_PERSONNEL_ITEM
      [(CATEGORY = 'Participant Expenses') and (SUB_CATEGORY = 'Participant Subsistence')]
      /AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="participantOther" select="sum(/PROPOSAL/BUDGET
      /TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL/NON_PERSONNEL_ITEM
    	[((CATEGORY = 'Participant Expenses') and ((SUB_CATEGORY = 'Participant Other') or 
    	(SUB_CATEGORY = 'Participant Tuition'))) or ((CATEGORY = 'Fellowships') and ((SUB_CATEGORY = 'Student Health Insurance') or
    	(SUB_CATEGORY = 'Tuition') or (SUB_CATEGORY = 'Institutional Allowance')))]
      /AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="participantCosts" select="$participantStipends +
    	$participantTravel + $participantSubsistence + $participantOther"/>
    
    <!-- Other Direct Cost variables -->
    <xsl:variable name="materialsSupplies" select="sum(/PROPOSAL/BUDGET
      /TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = 'Supplies']
      /AGENCY_REQUEST_AMOUNT)"/> <!-- (applies to all subcategories) --> 
    <xsl:variable name="publication" select="sum(/PROPOSAL/BUDGET
      /TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL/NON_PERSONNEL_ITEM
      [(CATEGORY = 'Other Expenses') and (SUB_CATEGORY = 'Publications')]
      /AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="consultantServices" select="sum(/PROPOSAL/BUDGET
      /TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = 'Consultants']
      /AGENCY_REQUEST_AMOUNT)"/> <!-- (applies to all subcategories) -->   
    <xsl:variable name="compServices" select="'?'"/>
    <xsl:variable name="compServices_Add">
      <xsl:choose><xsl:when test="number(compServices)">
        <xsl:value-of select="compServices"/>
      </xsl:when><xsl:otherwise>0</xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:variable name="subawards" select="sum(/PROPOSAL/BUDGET
      /TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = 'Subcontractors']
      /AGENCY_REQUEST_AMOUNT)"/>
    <xsl:variable name="othOthDirectCosts" select="sum(/PROPOSAL/BUDGET
      /TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL/NON_PERSONNEL_ITEM
      [(CATEGORY = 'Other Expenses') and (SUB_CATEGORY != 'Publications')]
      /AGENCY_REQUEST_AMOUNT)"/> <!-- (does not apply to all subcategories) -->
    
    <xsl:variable name="totalOthDirectCosts"
      select="$materialsSupplies + $publication + $consultantServices + $subawards 
      + $othOthDirectCosts + $compServices_Add"/>
    <xsl:variable name="totalDirectCosts" 
      select="$salariesBenefitsTotal + $equipmentTotal + $domesticTravelTotal +
      $foreignTravelTotal + $participantCosts + $totalOthDirectCosts"/>
    <xsl:variable name="totalIndirectCosts" 
      select="sum(/PROPOSAL/BUDGET/INDIRECT_COST
      /INDIRECT_COST_TASK
      /INDIRECT_COST_TASK_PERIOD
      /INDIRECT_COST_TASK_PERIOD_AGENCY_AMOUNT
      /AGENCY_CALCULATED_INDIRECT_COST)"/>
    
    <xsl:variable name="totalDirectIndirectCosts" 
      select="$totalDirectCosts + $totalIndirectCosts"/>
    <xsl:variable name="residualFunds" select="'?'"/>
    <xsl:variable name="requestAmt" select="$totalDirectIndirectCosts"/>
    
    <xsl:call-template name="main">
      <xsl:with-param name="periodNum" select="0"/>
      <xsl:with-param name="periodStart" select="0"/>
      <xsl:with-param name="periodStop" select="0"/>
      <xsl:with-param name="senPers" select="$senPers"/>  
      <xsl:with-param name="expensiveEquipment" select="$expensiveEquipment"/>
      <xsl:with-param name="othProfs" select="$othProfs"/>
      <xsl:with-param name="gradStudents" select="$gradStudents"/>
      <xsl:with-param name="othOthPers" select="$othOthPers"/>
      <xsl:with-param name="numSenPers" select="$numSenPers"/>
      <xsl:with-param name="numPostDocs" select="$numPostDocs"/>
      <xsl:with-param name="numOthProfs" select="$numOthProfs"/>
      <xsl:with-param name="numGradStudents" select="$numGradStudents"/>
      <xsl:with-param name="numUgradStudents" select="$numUgradStudents"/>
      <xsl:with-param name="numSecretarial" select="$numSecretarial"/>
      <xsl:with-param name="numOtherPers" select="$numOtherPers"/>
      <xsl:with-param name="numParticipants" select="$numParticipants"/>
      <xsl:with-param name="senPersSalaries" select="$senPersSalaries"/>
      <xsl:with-param name="postDocSalaries" select="$postDocSalaries"/>
      <xsl:with-param name="othProfSalaries" select="$othProfSalaries"/>
      <xsl:with-param name="gradStudentSalaries" select="$gradStudentSalaries"/>
      <xsl:with-param name="ugradStudentSalaries" select="$ugradStudentSalaries"/>
      <xsl:with-param name="secretarialSalaries" select="$secretarialSalaries"/>
      <xsl:with-param name="otherPersSalaries" select="$otherPersSalaries"/>
      <xsl:with-param name="salaryTotal" select="$salaryTotal"/>
      <xsl:with-param name="fringeBenefitsTotal" select="$fringeBenefitsTotal"/>
      <xsl:with-param name="salariesBenefitsTotal" select="$salariesBenefitsTotal"/>
      <xsl:with-param name="equipmentTotal" select="$equipmentTotal"/>
      <xsl:with-param name="domesticTravelTotal" select="$domesticTravelTotal"/>
      <xsl:with-param name="foreignTravelTotal" select="$foreignTravelTotal"/>
      <xsl:with-param name="participantStipends" select="$participantStipends"/>
      <xsl:with-param name="participantTravel" select="$participantTravel"/>
      <xsl:with-param name="participantSubsistence" select="$participantSubsistence"/>
      <xsl:with-param name="participantOther" select="$participantOther"/>
      <xsl:with-param name="participantCosts" select="$participantCosts"/>
      <xsl:with-param name="materialsSupplies" select="$materialsSupplies"/>
      <xsl:with-param name="publication" select="$publication"/>
      <xsl:with-param name="consultantServices" select="$consultantServices"/>
      <xsl:with-param name="compServices" select="$compServices"/>
      <xsl:with-param name="subawards" select="$subawards"/>
      <xsl:with-param name="othOthDirectCosts" select="$othOthDirectCosts"/>
      <xsl:with-param name="totalOthDirectCosts" select="$totalOthDirectCosts"/>
      <xsl:with-param name="totalDirectCosts" select="$totalDirectCosts"/>
      <xsl:with-param name="totalIndirectCosts" select="$totalIndirectCosts"/>
      <xsl:with-param name="totalDirectIndirectCosts" select="$totalDirectIndirectCosts"/>
      <xsl:with-param name="residualFunds" select="$residualFunds"/>
      <xsl:with-param name="requestAmt" select="$requestAmt"/>
    </xsl:call-template>
  </xsl:template>
  
  <!-- ******************* **************************** ******************* -->
  <!-- *******************      Main Table Template     ******************* -->
  <!-- ******************* **************************** ******************* -->
  
  <xsl:template name="main">
    <xsl:param name="periodNum"/>
    <xsl:param name="periodStart"/>
    <xsl:param name="periodStop"/>
    <xsl:param name="senPers"/>  
    <xsl:param name="expensiveEquipment"/>
    <xsl:param name="othProfs"/>
    <xsl:param name="gradStudents"/>
    <xsl:param name="othOthPers"/>
    <xsl:param name="numSenPers"/>
    <xsl:param name="numPostDocs"/>
    <xsl:param name="numOthProfs"/>
    <xsl:param name="numGradStudents"/>
    <xsl:param name="numUgradStudents"/>
    <xsl:param name="numSecretarial"/>
    <xsl:param name="numOtherPers"/>
    <xsl:param name="numParticipants"/>
    <xsl:param name="senPersSalaries"/>
    <xsl:param name="postDocSalaries"/>
    <xsl:param name="othProfSalaries"/>
    <xsl:param name="gradStudentSalaries"/>
    <xsl:param name="ugradStudentSalaries"/>
    <xsl:param name="secretarialSalaries"/>
    <xsl:param name="otherPersSalaries"/>
    <xsl:param name="salaryTotal"/>
    <xsl:param name="fringeBenefitsTotal"/>
    <xsl:param name="salariesBenefitsTotal"/>
    <xsl:param name="equipmentTotal"/>
    <xsl:param name="domesticTravelTotal"/>
    <xsl:param name="foreignTravelTotal"/>
    <xsl:param name="participantStipends"/>
    <xsl:param name="participantTravel"/>
    <xsl:param name="participantOther"/>
    <xsl:param name="participantSubsistence"/>
    <xsl:param name="participantCosts"/>
    <xsl:param name="materialsSupplies"/>
    <xsl:param name="publication"/>
    <xsl:param name="consultantServices"/>
    <xsl:param name="compServices"/>
    <xsl:param name="subawards"/>
    <xsl:param name="othOthDirectCosts"/>
    <xsl:param name="totalOthDirectCosts"/>
    <xsl:param name="totalDirectCosts"/>
    <xsl:param name="totalIndirectCosts"/>
    <xsl:param name="totalDirectIndirectCosts"/>
    <xsl:param name="residualFunds"/>
    <xsl:param name="requestAmt"/>
    
    <!-- Main Table Start -->
    <fo:table table-layout="fixed">
      <fo:table-column column-width="30mm"/>
      <fo:table-column column-width="48mm"/>
      <fo:table-column column-width="16mm"/>
      <fo:table-column column-width="14mm"/>
      <fo:table-column column-width="10mm"/>
      <fo:table-column column-width="11mm"/>
      <fo:table-column column-width="9.5mm"/>
      <fo:table-column column-width="3mm"/>
      <fo:table-column column-width="5mm"/>
      <fo:table-column column-width="18.5mm"/>
      <fo:table-column column-width="3.5mm"/>
      <fo:table-column column-width="16mm"/>
      <fo:table-body>
        
        <!-- 1st Row -->
        <fo:table-row height="5mm">
          <fo:table-cell number-columns-spanned="5"
                         number-rows-spanned="2"
                         text-align="center"
                         display-align="after">
            <fo:block xsl:use-attribute-sets="labelText stdHeight" 
              font-weight="bold">
              INTERNAL NSF
            </fo:block>
            <fo:block xsl:use-attribute-sets="labelText stdHeight" 
              font-weight="bold">
              SUMMARY PROPOSAL BUDGET
            </fo:block>
          </fo:table-cell>
          <fo:table-cell number-columns-spanned="4">
            <fo:block xsl:use-attribute-sets="answerText stdHeight">Period:</fo:block> 
            <fo:block xsl:use-attribute-sets="answerText stdHeight">
              <xsl:choose>
                <xsl:when test="$periodNum = 0">
                  CUMULATIVE
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$periodStart"/>-<xsl:value-of select="$periodStop"/>
                </xsl:otherwise>
              </xsl:choose>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell number-columns-spanned="3" text-align="right">
            <fo:block xsl:use-attribute-sets="answerText stdHeight">Tracking #:</fo:block>
            <fo:block xsl:use-attribute-sets="answerText stdHeight">
              <xsl:value-of select="/PROPOSAL/BUDGET/@BUDGET_NUMBER"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 2nd Row -->
        <fo:table-row height="9mm">
          <fo:table-cell xsl:use-attribute-sets="gbCell"
                         number-columns-spanned="7">
            <fo:block xsl:use-attribute-sets="smLabText stdHeight"
                      text-align="center"
                      font-weight="bold">
              FOR NSF USE ONLY
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 3rd Row -->
        <fo:table-row height="6mm">
          <fo:table-cell xsl:use-attribute-sets="bCell"
                         number-rows-spanned="2"
                         number-columns-spanned="5">
            <fo:block xsl:use-attribute-sets="smLabText">
              <fo:inline xsl:use-attribute-sets="whiteSpaceBlock">&#160;</fo:inline>
              <fo:inline>ORGANIZATION</fo:inline>
            </fo:block>
            <fo:block xsl:use-attribute-sets="wsAnswerText">&#160;&#160;<xsl:value-of select="$organization"/></fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
                         number-rows-spanned="2"
                         number-columns-spanned="4">
            <fo:block xsl:use-attribute-sets="smCLabText">
              PROPOSAL NO.
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
                         number-columns-spanned="3">
            <fo:block xsl:use-attribute-sets="smCLabText">
              DURATION (MONTHS)
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 4th row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="gbCell"
                         number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smCLabText stdHeight">
              Proposed
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell">
            <fo:block xsl:use-attribute-sets="smCLabText stdHeight">
              Granted
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 5th row -->
        <fo:table-row height="6mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
                         number-columns-spanned="5">
            <fo:block xsl:use-attribute-sets="smLabText stdHeight">
              <fo:inline xsl:use-attribute-sets="whiteSpaceBlock">&#160;</fo:inline>
              <fo:inline>PRINCIPAL INVESTIGATOR/PROJECT DIRECTOR</fo:inline>
            </fo:block>
          	<xsl:choose>
          		<xsl:when test="contains(/PROPOSAL/BUDGET/PROJECT_DIRECTOR/@FIRST_NAME,'To Be Named')">
          			<fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">&#160;&#160;To Be Named</fo:block>
          		</xsl:when>
          		<xsl:otherwise>
            		<fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">&#160;&#160;<xsl:value-of 
            			select="/PROPOSAL/BUDGET/PROJECT_DIRECTOR/@LAST_NAME"/>,&#160;<xsl:value-of 
            			select="/PROPOSAL/BUDGET/PROJECT_DIRECTOR/@FIRST_NAME"/>&#160;</fo:block>
          		</xsl:otherwise>
          	</xsl:choose>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
                         number-columns-spanned="4">
            <fo:block xsl:use-attribute-sets="smCLabText stdHeight">
              AWARD NO.
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
                          number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="whiteSpaceBlock stdHeight">&#160;</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell">
            <fo:block xsl:use-attribute-sets="whiteSpaceBlock stdHeight">&#160;</fo:block>
          </fo:table-cell>                        
        </fo:table-row>        
      
        <!-- 6th row -->
        <fo:table-row height="7.5mm">
          <fo:table-cell xsl:use-attribute-sets="bCell"
                         number-columns-spanned="4"
                         number-rows-spanned="2">
            <fo:block xsl:use-attribute-sets="wsSmLabText">
              <fo:inline xsl:use-attribute-sets="whiteSpaceBlock">&#160;</fo:inline>
              <fo:inline>A. SENIOR PERSONNEL: PI/PD, Co-PIs, Faculty and Other Senior Associates</fo:inline>
            </fo:block>
            <fo:block xsl:use-attribute-sets="wsSmLabText">&#160;&#160;&#160;List each separately with name and title. (A.7. Show number in brackets)</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell"
                         number-columns-spanned="4">
            <fo:block xsl:use-attribute-sets="smCLabText">
              NSF Funded
            </fo:block>
            <fo:block xsl:use-attribute-sets="smCLabText">
              Person-months
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell"
                         number-columns-spanned="2"
                         number-rows-spanned="2">
            <fo:block xsl:use-attribute-sets="smCLabText stdHeight">
              Funds
            </fo:block>
            <fo:block xsl:use-attribute-sets="reallySmCLabText stdHeight">
              Requested By
            </fo:block>
            <fo:block xsl:use-attribute-sets="smCLabText stdHeight">
              Proposer
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell"
                         number-columns-spanned="2"
                         number-rows-spanned="2">
            <fo:block xsl:use-attribute-sets="smCLabText stdHeight">
              Funds
            </fo:block>
            <fo:block xsl:use-attribute-sets="reallySmCLabText stdHeight">
              Granted by NSF
            </fo:block>
            <fo:block xsl:use-attribute-sets="smCLabText stdHeight">
              (If Different)
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 7th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="bCell">
            <fo:block xsl:use-attribute-sets="smCLabText stdHeight">
              CAL
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell">
            <fo:block xsl:use-attribute-sets="smCLabText stdHeight">
              ACAD
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell"
                         number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smCLabText stdHeight">
              SUMR
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 8th Row through 12th(potentially higher) Row -->
        <xsl:choose>
          <xsl:when test="$periodNum = 0">
            <xsl:apply-templates select="//PERSON[generate-id(.) = generate-id( key('senPersNameKey',NAME) )]" mode="senPersLine">
              <xsl:sort select="NAME"/>
              <xsl:with-param name="periodNum" select="$periodNum"/>
              </xsl:apply-templates>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="$senPers" mode="senPersLine">
              <xsl:sort select="NAME"/>
              <xsl:with-param name="periodNum" select="$periodNum"/>
            </xsl:apply-templates>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:call-template name="blankSenPersLine">
          <xsl:with-param name="begin" select="$numSenPers"/>
        </xsl:call-template>
        
        <!-- 13th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="4">
            <fo:block xsl:use-attribute-sets="stdHeight">
              <fo:inline xsl:use-attribute-sets="wsSmLabText">
                &#160;&#160;&#160;TOTAL SENIOR PERSONNEL
              </fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCentCell">
            <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCentCell">
            <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCentCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($senPersSalaries,'###,###')"/>
              </fo:inline>
              <fo:inline xsl:use-attribute-sets="stdHeight">&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 14th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="4">
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;B. OTHER PERSONNEL (SHOW NUMBERS IN BRACKETS) </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 15th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="4">
            <fo:block xsl:use-attribute-sets="stdHeight">
              <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;&#160;1.(&#160;<xsl:if test="string-length($numPostDocs) &lt; 2">&#160;</xsl:if></fo:inline>
              <xsl:choose>
                <xsl:when test="number($numPostDocs)">
                  <fo:inline xsl:use-attribute-sets="smAnsText">
                    <xsl:value-of select="$numPostDocs"/>
                  </fo:inline>
                </xsl:when>
                <xsl:otherwise>
                  <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;&#160;</fo:inline>
                </xsl:otherwise>
              </xsl:choose>
              <fo:inline xsl:use-attribute-sets="smLabText">
                ) POST DOCTORAL ASSOCIATES
              </fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCentCell">
            <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCentCell">
            <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCentCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell stdHeight"
            number-columns-spanned="2">
            <xsl:choose>
              <xsl:when test="number($postDocSalaries)">
                <fo:block xsl:use-attribute-sets="smAnsText"
                  text-align="right">
                  <xsl:value-of select="format-number($postDocSalaries,'###,###')"/>
                </fo:block>
              </xsl:when>
              <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
              </xsl:otherwise>
            </xsl:choose>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 16th Row -->
        <fo:table-row>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="4">
            <fo:table table-layout="fixed">
              <fo:table-column column-width="107mm"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block xsl:use-attribute-sets="stdHeight">
                      <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;&#160;2.(&#160;<xsl:if test="string-length($numOthProfs) &lt; 2">&#160;</xsl:if></fo:inline>
                      <fo:inline xsl:use-attribute-sets="smAnsText">
                        <xsl:choose>
                          <xsl:when test="number($numOthProfs)">
                            <xsl:value-of select="$numOthProfs"/>
                          </xsl:when>
                          <xsl:otherwise>0</xsl:otherwise>
                        </xsl:choose>
                      </fo:inline>
                      <fo:inline xsl:use-attribute-sets="smLabText">
                        ) OTHER PROFESSIONALS (TECHNICIAN, PROGRAMMER, ETC.)
                      </fo:inline>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:list-block margin-left="5mm" margin-right="5mm">
                      <xsl:call-template name="genPersList">
                        <xsl:with-param name="periodNum" select="$periodNum"/>
                        <xsl:with-param name="contextName" select="'othProfs'"/>
                        <xsl:with-param name="nodeContext" select="$othProfs"/>
                      </xsl:call-template>
                    </fo:list-block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCentCell">
            <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCentCell">
            <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCentCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($othProfSalaries,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block>&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 17th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:table table-layout="fixed">
              <fo:table-column column-width="141mm"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block xsl:use-attribute-sets="stdHeight">
                      <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;&#160;3.(&#160;<xsl:if test="string-length($numGradStudents) &lt; 2">&#160;</xsl:if></fo:inline>
                      <fo:inline xsl:use-attribute-sets="smAnsText">
                        <xsl:choose>
                          <xsl:when test="number($numGradStudents)">
                            <xsl:value-of select="$numGradStudents"/>
                          </xsl:when>
                          <xsl:otherwise>0</xsl:otherwise>
                        </xsl:choose>
                      </fo:inline>  
                      <fo:inline xsl:use-attribute-sets="smLabText">
                        ) GRADUATE STUDENTS
                      </fo:inline>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:list-block margin-left="5mm" margin-right="5mm">
                      <xsl:call-template name="genPersList">
                        <xsl:with-param name="periodNum" select="$periodNum"/>
                        <xsl:with-param name="contextName" select="'gradStudents'"/>
                        <xsl:with-param name="nodeContext" select="$gradStudents"/>
                      </xsl:call-template>
                    </fo:list-block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($gradStudentSalaries,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>

        <!-- 18th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="stdHeight">
              <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;&#160;4.(&#160;<xsl:if test="string-length($numUgradStudents) &lt; 2">&#160;</xsl:if></fo:inline>
              <xsl:choose>
                <xsl:when test="number($numUgradStudents)">
                  <fo:inline xsl:use-attribute-sets="smAnsText">
                    <xsl:value-of select="$numUgradStudents"/>
                  </fo:inline>
                </xsl:when>
                <xsl:otherwise>
                  <fo:inline>&#160;</fo:inline>
                </xsl:otherwise>
              </xsl:choose>
              <fo:inline xsl:use-attribute-sets="smLabText">
                ) UNDERGRADUATE STUDENTS
              </fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <xsl:choose>
              <xsl:when test="number($ugradStudentSalaries)">
                <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
                  text-align="right">
                  <xsl:value-of select="format-number($ugradStudentSalaries,'###,###')"/>
                </fo:block>
              </xsl:when>
              <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
              </xsl:otherwise>
            </xsl:choose>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 19th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="stdHeight">
              <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;&#160;5.(&#160;<xsl:if test="string-length($numSecretarial) &lt; 2">&#160;</xsl:if></fo:inline>
              <xsl:choose>
                <xsl:when test="number($numSecretarial)">
                  <fo:inline xsl:use-attribute-sets="smAnsText">
                    <xsl:value-of select="format-number($numSecretarial,'###,###')"/>
                  </fo:inline>
                </xsl:when>
                <xsl:otherwise>
                  <fo:inline>&#160;</fo:inline>
                </xsl:otherwise>
              </xsl:choose>
              <fo:inline xsl:use-attribute-sets="smLabText">
                ) SECRETARIAL - CLERICAL (IF CHARGED DIRECTLY)
              </fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <xsl:choose>
              <xsl:when test="number($secretarialSalaries)">
                <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
                  text-align="right">
                  <xsl:value-of select="format-number($secretarialSalaries,'###,###')"/>
                </fo:block>
              </xsl:when>
              <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
              </xsl:otherwise>
            </xsl:choose>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 20th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:table table-layout="fixed">
              <fo:table-column column-width="141mm"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block xsl:use-attribute-sets="stdHeight">
                      <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;&#160;6.(&#160;<xsl:if test="string-length($numOtherPers) &lt; 2">&#160;</xsl:if></fo:inline>
                      <fo:inline xsl:use-attribute-sets="smAnsText">
                        <xsl:choose>
                          <xsl:when test="number($numOtherPers)">
                            <xsl:value-of select="$numOtherPers"/>
                          </xsl:when>
                          <xsl:otherwise>0</xsl:otherwise>
                        </xsl:choose>
                      </fo:inline>
                      <fo:inline xsl:use-attribute-sets="smLabText">
                        ) OTHER
                      </fo:inline>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:list-block margin-left="5mm" margin-right="5mm">
                      <xsl:call-template name="genPersList">
                        <xsl:with-param name="periodNum" select="$periodNum"/>
                        <xsl:with-param name="contextName" select="'othOthPers'"/>
                        <xsl:with-param name="nodeContext" select="$othOthPers"/>
                      </xsl:call-template>
                    </fo:list-block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <xsl:choose>
              <xsl:when test="number($otherPersSalaries)">
                <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
                  text-align="right">
                  <xsl:value-of select="format-number($otherPersSalaries,'###,###')"/>
                  <fo:inline>&#160;&#160;</fo:inline>
                </fo:block>
              </xsl:when>
              <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="wsSmAnsText stdHeight"
                  text-align="right">0&#160;&#160;</fo:block>
              </xsl:otherwise>
            </xsl:choose>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 21st Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="stdHeight">
              <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;&#160;&#160;</fo:inline>
              <fo:inline xsl:use-attribute-sets="smLabText">
                TOTAL SALARIES AND WAGES (A + B)
              </fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($salaryTotal, '###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 22nd Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="stdHeight">
              <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;</fo:inline>
              <fo:inline xsl:use-attribute-sets="smLabText">
                C. FRINGE BENEFITS (IF CHARGED AS DIRECT COSTS)
              </fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($fringeBenefitsTotal,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 23rd Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="stdHeight">
              <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;&#160;&#160;</fo:inline>
              <fo:inline xsl:use-attribute-sets="smLabText">
                TOTAL SALARIES, WAGES AND FRINGE BENEFITS (A + B + C)
              </fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($salariesBenefitsTotal, '###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 24th Row -->
        <fo:table-row>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8"
            number-rows-spanned="2"
            display-align="before">
            <fo:table table-layout="fixed">
              <fo:table-column column-width="141mm"/>
              <fo:table-body>
                <fo:table-row height="3mm">
                  <fo:table-cell>
                    <fo:block xsl:use-attribute-sets="stdHeight">
                      <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;</fo:inline>
                      <fo:inline xsl:use-attribute-sets="smLabText">
                        D. EQUIPMENT (LIST ITEM AND DOLLAR AMOUNT FOR EACH ITEM EXCEEDING $5000.)
                      </fo:inline>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block xsl:use-attribute-sets="stdHeight" 
                      margin-left="5mm" margin-right="6mm">
                      <xsl:apply-templates select="$expensiveEquipment">
                        <xsl:sort data-type="number" order="descending" select="."/>
                      </xsl:apply-templates>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row height="3mm">
                  <fo:table-cell>
                    <fo:block xsl:use-attribute-sets="stdHeight">
                      <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;&#160;&#160;</fo:inline>
                      <fo:inline xsl:use-attribute-sets="smLabText">
                        TOTAL EQUIPMENT
                      </fo:inline>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
                  number-columns-spanned="2">
            <xsl:for-each select="NON_PERSONNEL/NON_PERSONNEL_ITEM
              [CATEGORY = 'Equipment']/AGENCY_REQUEST_AMOUNT[node() &gt;= 5000]">
              <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
            </xsl:for-each>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
                  number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 25th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($equipmentTotal,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 26th Row -->
        <fo:table-row height="3mm" 
          display-align="after">
          <fo:table-cell xsl:use-attribute-sets="bCell"
            number-columns-spanned="8">
            <fo:list-block>
              <fo:list-item>
                <fo:list-item-label>
                  <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;E. TRAVEL</fo:block>
                </fo:list-item-label>
                <fo:list-item-body>
                  <fo:block xsl:use-attribute-sets="smLabText stdHeight" 
                    margin-left="25mm">
                    1. DOMESTIC (INCL. CANADA, MEXICO AND U.S. POSSESSIONS)
                  </fo:block>
                </fo:list-item-body>
              </fo:list-item>
            </fo:list-block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($domesticTravelTotal,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>          
        </fo:table-row>        

        <!-- 27th Row -->
        <fo:table-row height="3mm" 
          display-align="after">
          <fo:table-cell xsl:use-attribute-sets="bCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="smLabText stdHeight"
              margin-left="25mm">
              2. FOREIGN
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($foreignTravelTotal,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>          
        </fo:table-row>
        
        <!-- 28th Row -->
        <fo:table-row height="14mm">
          <fo:table-cell xsl:use-attribute-sets="bCell"
            number-columns-spanned="8">
            <fo:table table-layout="fixed">
              <fo:table-column column-width="141mm"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block xsl:use-attribute-sets="wsSmLabText">
                      <fo:inline xsl:use-attribute-sets="whiteSpaceBlock">&#160;</fo:inline>
                      <fo:inline>F. PARTICIPANT SUPPORT</fo:inline>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:list-block margin-left="5mm" margin-right="5mm">
                      <fo:list-item>
                        <fo:list-item-label>
                          <fo:block xsl:use-attribute-sets="smLabText stdHeight">1. STIPENDS</fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()"
                          text-align="right">
                          <fo:block xsl:use-attribute-sets="smLabText stdHeight"
                            text-align-last="justify">
                            <fo:leader leader-pattern="dots"/>
                            <fo:inline xsl:use-attribute-sets="smAnsText">
                              <xsl:value-of select="format-number($participantStipends, '###,###')"/>
                            </fo:inline>
                          </fo:block>
                        </fo:list-item-body>
                      </fo:list-item>
                      <fo:list-item>
                        <fo:list-item-label>
                          <fo:block xsl:use-attribute-sets="smLabText stdHeight">2. TRAVEL</fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()"
                          text-align="right">
                          <fo:block xsl:use-attribute-sets="smLabText stdHeight"
                            text-align-last="justify">
                            <fo:leader leader-pattern="dots"/>
                            <fo:inline xsl:use-attribute-sets="smAnsText">
                              <xsl:value-of select="format-number($participantTravel, '###,###')"/>
                            </fo:inline>
                          </fo:block>
                        </fo:list-item-body>
                      </fo:list-item>
                      <fo:list-item>
                        <fo:list-item-label>
                          <fo:block xsl:use-attribute-sets="smLabText stdHeight">3. SUBSISTENCE</fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()"
                          text-align="right">
                          <fo:block xsl:use-attribute-sets="smLabText stdHeight"
                            text-align-last="justify">
                            <fo:leader leader-pattern="dots"/>
                            <fo:inline xsl:use-attribute-sets="smAnsText">  
                              <xsl:value-of select="format-number( $participantSubsistence,'###,###' )"/>
                            </fo:inline>
                          </fo:block>
                        </fo:list-item-body>
                      </fo:list-item>
                      <fo:list-item>
                        <fo:list-item-label>
                          <fo:block xsl:use-attribute-sets="smLabText stdHeight">4. OTHER</fo:block>
                        </fo:list-item-label>
                        <fo:list-item-body start-indent="body-start()"
                          text-align="right">
                          <fo:block xsl:use-attribute-sets="smLabText stdHeight"
                            text-align-last="justify">
                            <fo:leader leader-pattern="dots"/>
                            <fo:inline xsl:use-attribute-sets="smAnsText">
                              <xsl:value-of select="format-number( $participantOther, '###,###' )"/>
                            </fo:inline>
                          </fo:block>
                        </fo:list-item-body>
                      </fo:list-item>
                    </fo:list-block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 29th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="bCell"
            number-columns-spanned="8">
            <fo:table table-layout="fixed">
              <fo:table-column column-width="6mm"/>
              <fo:table-column column-width="57mm"/>
              <fo:table-column column-width="17mm"/>
              <fo:table-column column-width="43mm"/>
              <fo:table-body>
                <fo:table-row display-align="after">
                  <fo:table-cell/>
                  <fo:table-cell>
                    <fo:block xsl:use-attribute-sets="stdHeight">
                      <fo:inline xsl:use-attribute-sets="smLabText">
                        TOTAL NUMBER OF PARTICIPANTS (
                      </fo:inline>
                      <xsl:choose>
                        <xsl:when test="number($numParticipants)">
                          <fo:inline xsl:use-attribute-sets="smAnsText">
                            <xsl:value-of select="$numParticipants"/>
                          </fo:inline>
                        </xsl:when>
                        <xsl:otherwise>
                          <fo:inline xsl:use-attribute-sets="wsAnswerText">*</fo:inline>
                        </xsl:otherwise>
                      </xsl:choose>
                      <fo:inline xsl:use-attribute-sets="smLabText">)</fo:inline>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell/>
                  <fo:table-cell>
                    <fo:block xsl:use-attribute-sets="stdHeight">
                      <fo:inline xsl:use-attribute-sets="smLabText">
                        TOTAL PARTICIPANT COSTS
                      </fo:inline>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <xsl:choose>
              <xsl:when test="number($participantCosts)">
                <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
                  text-align="right">
                  <fo:inline>
                    <xsl:value-of select="format-number($participantCosts, '###,###')"/>
                  </fo:inline>
                  <fo:inline>&#160;&#160;</fo:inline>
                </fo:block>
              </xsl:when>
              <xsl:otherwise>
              </xsl:otherwise>
            </xsl:choose>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 30th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;G. OTHER DIRECT COSTS</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 31st Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;&#160;1. MATERIALS AND SUPPLIES</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($materialsSupplies,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 32nd Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;&#160;2. PUBLICATION/DOCUMENTATION/DISSEMINATION</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($publication, '###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 33rd Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;&#160;3. CONSULTANT SERVICES</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($consultantServices,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 34th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;&#160;4. COMPUTER SERVICES</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <xsl:choose>
              <xsl:when test="number($compServices)">
                <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
                  text-align="right">
                  <xsl:value-of select="format-number($compServices,'###,###')"/>
                </fo:block>
              </xsl:when>
              <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
              </xsl:otherwise>
            </xsl:choose>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 35th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;&#160;5. SUBAWARDS</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($subawards,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 36th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;&#160;6. OTHER</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($othOthDirectCosts,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 37th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;&#160;&#160;TOTAL OTHER DIRECT COSTS</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($totalOthDirectCosts,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 38th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;H. TOTAL DIRECT COSTS (A THROUGH G)</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($totalDirectCosts,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="bCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!--39th Row -->
        <fo:table-row>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8"
            number-rows-spanned="2">
            <fo:block xsl:use-attribute-sets="smLabText" line-height="1.5">
              <fo:inline xsl:use-attribute-sets="whiteSpaceBlock">&#160;</fo:inline>
              <fo:inline>I. INDIRECT COSTS (F&amp;A) (SPECIFY RATE AND BASE)</fo:inline>
            </fo:block>
            <fo:block xsl:use-attribute-sets="stdHeight" 
              margin-left="5mm" 
              margin-right="5mm">
              <xsl:choose>
                <xsl:when test="$periodNum = 0">
                  <xsl:apply-templates select="/PROPOSAL/BUDGET/INDIRECT_COST/INDIRECT_COST_TASK
                    /INDIRECT_COST_TASK_PERIOD" mode="indirectCosts">
                    <xsl:with-param name="summary" select="'true'"/>
                  </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:apply-templates select="/PROPOSAL/BUDGET/INDIRECT_COST/INDIRECT_COST_TASK
                    /INDIRECT_COST_TASK_PERIOD[@PERIOD_NUMBER = $periodNum]" mode="indirectCosts">
                    <xsl:with-param name="summary" select="'false'"/>
                  </xsl:apply-templates>    
                </xsl:otherwise>
              </xsl:choose>
            </fo:block>
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;&#160;&#160;TOTAL INDIRECT COSTS (F&amp;A)</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 40th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($totalIndirectCosts,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 41st Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;J. TOTAL DIRECT AND INDIRECT COSTS (H + I)</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline>
                <xsl:value-of select="format-number($totalDirectIndirectCosts,'###,###')"/>
              </fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 42nd Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;K. RESIDUAL FUNDS (IF FOR FURTHER SUPPORT OF CURRENT PROJECT SEE GPG II.D.7.j.)</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <xsl:choose>
              <xsl:when test="number($residualFunds)">
                <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
                    text-align="right">
                  <fo:inline>
                    <xsl:value-of select="format-number($residualFunds,'###,###')"/>
                  </fo:inline>
                  <fo:inline>&#160;&#160;</fo:inline>
                </fo:block>
              </xsl:when>
              <xsl:otherwise>
                <fo:block>&#160;</fo:block>
              </xsl:otherwise>
            </xsl:choose>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 43rd Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="8">
            <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;L. AMOUNT OF THIS REQUEST (J) OR (J MINUS K)</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
              text-align="right">
              <fo:inline><xsl:value-of select="format-number($requestAmt,'###,###')"/></fo:inline>
              <fo:inline>&#160;&#160;</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell" number-columns-spanned="2"/>
        </fo:table-row>
        
        <!-- 44th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="3">
            <fo:list-block>
              <fo:list-item>
                <fo:list-item-label text-align="left">
                  <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;M. COST SHARING: PROPOSED LEVEL</fo:block>
                </fo:list-item-label>
                <fo:list-item-body text-align="right">
                  <fo:block>&#160;</fo:block>
                </fo:list-item-body>
              </fo:list-item>
            </fo:list-block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="9">
            <fo:list-block>
              <fo:list-item>
                <fo:list-item-label text-align="left">
                  <fo:block xsl:use-attribute-sets="wsSmLabText stdHeight">&#160;AGREED LEVEL IF DIFFERENT</fo:block>
                </fo:list-item-label>
                <fo:list-item-body text-align="right">
                  <fo:block>&#160;</fo:block>              
                </fo:list-item-body>
              </fo:list-item>
            </fo:list-block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 45th Row -->
        <fo:table-row height="4.5mm">
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="3"
            number-rows-spanned="2">
          	<!-- 5/13/2004 dterret: Change at Teresa Miller's request.-->
            <fo:block xsl:use-attribute-sets="wsSmLabText">
              <fo:inline xsl:use-attribute-sets="whiteSpaceBlock">&#160;</fo:inline>
              <fo:inline>PI/PD TYPED NAME AND SIGNATURE<!--**--></fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="2"
            number-rows-spanned="2">
            <fo:block xsl:use-attribute-sets="wsSmLabText">&#160;DATE</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="7">
            <fo:block xsl:use-attribute-sets="smLabText"
              text-align="center"
              font-weight="bold">
              FOR NSF USE ONLY
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 46th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="7">
            <fo:block xsl:use-attribute-sets="smLabText"
              text-align="center">
              INDIRECT COST RATE VERIFICATION
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 47th Row -->
        <fo:table-row height="7.5mm">
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="3">
          	<!-- 5/13/2004 dterret: Change at Teresa Miller's request.-->
            <fo:block xsl:use-attribute-sets="wsSmLabText">&#160;ORG. REP. TYPED NAME AND SIGNATURE<!--**--></fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="wsSmLabText">&#160;DATE</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smLabText"
              text-align="center">
              Date Checked
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="3">
            <fo:block xsl:use-attribute-sets="smLabText"
              text-align="center">
              Date of Rate Sheet
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="gbCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="smLabText"
              text-align="center">
              Initials-ORG
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        
        <!-- 48th Row -->
        <fo:table-row height="3mm">
          <fo:table-cell number-columns-spanned="3">
          	<!-- 5/13/2004 dterret: Change at Teresa Miller's request.
            <fo:block xsl:use-attribute-sets="smAnsText">
              NSF Form 1030 (10/99) <fo:inline font-style="italic">Supersedes All Previous Editions</fo:inline>
            </fo:block>
          	-->
          </fo:table-cell>
          <fo:table-cell number-columns-spanned="9">
            <fo:block xsl:use-attribute-sets="smLabText">
              *N/A -- MUST BE MANUALLY CALCULATED
            </fo:block>
          	<!-- 5/13/2004 dterret: Change at Teresa Miller's request.
            <fo:block xsl:use-attribute-sets="smLabText">
              **SIGNATURES REQUIRED ONLY FOR REVISED BUDGET (GPG III.C)
            </fo:block>
          	-->
          </fo:table-cell>
        </fo:table-row>
        
      </fo:table-body>
    </fo:table>
  </xsl:template>
  
  <!-- ******************* **************************** ******************* -->
  <!-- *******************    Indirect Costs Template   ******************* -->
  <!-- ******************* **************************** ******************* -->
  <!-- This template computes and prints Indirect Costs over the            -->
  <!-- individual tasks in the current Period.  It prints a list of names   -->
  <!-- of tasks followed by the rate and the agency base amount.            -->
  
  <xsl:template match="/PROPOSAL/BUDGET/INDIRECT_COST/INDIRECT_COST_TASK
    /INDIRECT_COST_TASK_PERIOD"
    mode="indirectCosts">
    <xsl:param name="summary"/>
    <xsl:variable name="periodNumber" select="@PERIOD_NUMBER"/>
    <xsl:variable name="taskNumber" select="../@TASK_NUMBER"/>
    <xsl:variable name="thisRate" select="@RATE"/>
  	<xsl:variable name="divisor" select="substring-before($thisRate,'%')"/>
    <xsl:if test="($summary != 'true' 
      and generate-id(.) = generate-id( key('idcRatePeriodKey',
      concat($periodNumber,' ',$thisRate))[1] )) or
      ($summary = 'true' 
      and generate-id(.) = generate-id( key( 'idcRateKey', $thisRate ) ) )">
      <fo:table table-layout="fixed">
        <fo:table-column column-width="97mm"/>
        <fo:table-column column-width="6mm"/>
        <fo:table-column column-width="10mm"/>
        <fo:table-column column-width="5mm"/>
        <fo:table-column column-width="7mm"/>
        <fo:table-column column-width="11mm"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell/>
            <fo:table-cell xsl:use-attribute-sets="innerTC">
              <fo:block xsl:use-attribute-sets="smLabText">
                Rate:
              </fo:block>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="innerTC">
              <xsl:choose>
                <xsl:when test="$divisor = 0">
                  <fo:block xsl:use-attribute-sets="smAnsText"
                  	text-align="right">0.0%</fo:block>
                </xsl:when>
                <xsl:otherwise>
                  <fo:block xsl:use-attribute-sets="smAnsText"
                    text-align="right"><xsl:value-of
                      select="concat(substring(substring-before(/PROPOSAL/BUDGET/INDIRECT_COST
                      /INDIRECT_COST_TASK[@TASK_NUMBER = $taskNumber]
                      /INDIRECT_COST_TASK_PERIOD
                    	[@PERIOD_NUMBER = $periodNumber]/@RATE,'%'),0,5),'%')"/></fo:block>
                </xsl:otherwise>
              </xsl:choose>
            </fo:table-cell>
            <fo:table-cell/>
            <fo:table-cell xsl:use-attribute-sets="innerTC">
              <fo:block xsl:use-attribute-sets="smLabText">
                Base:
              </fo:block>
            </fo:table-cell>  
            <fo:table-cell xsl:use-attribute-sets="innerTC">
              <fo:block xsl:use-attribute-sets="smAnsText"
                text-align="right">
                <xsl:choose>
                  <xsl:when test="$summary = 'true'">
                    <xsl:value-of
                      select="format-number(round(sum(key('idcRateKey',$thisRate)
                      /INDIRECT_COST_TASK_PERIOD_AGENCY_AMOUNT
                      /AGENCY_BASE)), '###,###')"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of
                      select="format-number(round(sum(key('idcRatePeriodKey',
                      concat($periodNumber,' ',$thisRate))
                      /INDIRECT_COST_TASK_PERIOD_AGENCY_AMOUNT
                      /AGENCY_BASE)), '###,###')"/>
                  </xsl:otherwise>
                </xsl:choose>
                
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </xsl:if>
  </xsl:template>
  
  <!-- ******************* **************************** ******************* -->
  <!-- *******************     SenPersLine Template     ******************* -->
  <!-- ******************* **************************** ******************* -->
  <!-- This templates prints the filled rows of the Senior Personnel        -->
  <!-- Section.                                                             -->
  
  <xsl:template match="PERSON" mode="senPersLine">
    <xsl:param name="periodNum"/>
    <xsl:variable name="appt_type" select="APPOINTMENT/@APPOINTMENT_TYPE"/>

    <fo:table-row height="3mm">
      <fo:table-cell xsl:use-attribute-sets="ansBCell"
        number-columns-spanned="4">
        <fo:block xsl:use-attribute-sets="stdHeight">
          <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;&#160;<xsl:value-of select="position()"/>.</fo:inline><fo:inline xsl:use-attribute-sets="wsSmAnsText">&#160;<xsl:value-of select="NAME"/>,&#160;<xsl:value-of select="ROLE"/>
          </fo:inline>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell xsl:use-attribute-sets="ansBCentCell">
        <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
      </fo:table-cell>
      <fo:table-cell xsl:use-attribute-sets="ansBCentCell">
        <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
      </fo:table-cell>
      <fo:table-cell xsl:use-attribute-sets="ansBCentCell"
        number-columns-spanned="2">
        <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
      </fo:table-cell>
      <fo:table-cell xsl:use-attribute-sets="ansBCell"
        number-columns-spanned="2">
        <fo:block xsl:use-attribute-sets="smAnsText stdHeight"
          text-align="right">
          <fo:inline>
            <xsl:choose>
              <xsl:when test="$periodNum = 0">
                <xsl:value-of select="format-number(sum( key('senPersNameKey', NAME )
                  /@AGENCY_AMOUNT_SALARY), '###,###')"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="format-number(sum( key('senPersPNKey', 
                  concat($periodNum,' ',NAME))/@AGENCY_AMOUNT_SALARY), '###,###')"/>
              </xsl:otherwise>
            </xsl:choose>
          </fo:inline>
          <fo:inline>&#160;&#160;</fo:inline>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell xsl:use-attribute-sets="ansBCell"
          number-columns-spanned="2">
        <fo:block xsl:use-attribute-sets="stdHeight">&#160;</fo:block>
      </fo:table-cell>
    </fo:table-row>
  </xsl:template>

  <!-- ******************* **************************** ******************* -->
  <!-- *******************  Blank SenPersLine Template  ******************* -->
  <!-- ******************* **************************** ******************* -->
  <!-- This template prints the unfilled rows of the Senior Personnel       -->
  <!-- Section.                                                             -->
    
  <xsl:template name="blankSenPersLine">
    <xsl:param name="begin"/>
      
    <xsl:choose>
      <xsl:when test="$begin &gt;= 5"/>
      <xsl:otherwise>
        <fo:table-row height="3mm">
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="4">
            <fo:block>
              <fo:inline xsl:use-attribute-sets="wsSmLabText">&#160;&#160;<xsl:value-of select="$begin + 1"/>.</fo:inline>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCentCell">
            <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCentCell">
            <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCentCell"
            number-columns-spanned="2">
            <fo:block xsl:use-attribute-sets="wsAnswerText stdHeight">*</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block>&#160;</fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="ansBCell"
            number-columns-spanned="2">
            <fo:block>&#160;</fo:block>
          </fo:table-cell>
        </fo:table-row>
        <xsl:call-template name="blankSenPersLine">
          <xsl:with-param name="begin" select="$begin + 1"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- ******************* **************************** ******************* -->
  <!-- *******************     GenPersList Template     ******************* -->
  <!-- ******************* **************************** ******************* -->
  
  <xsl:template name="genPersList">
    <xsl:param name="periodNum"/>
    <xsl:param name="contextName"/>
    <xsl:param name="nodeContext"/>
    <xsl:choose>
      <xsl:when test="$periodNum = 0">
        <xsl:for-each select="$nodeContext">
          <xsl:if test="($contextName = 'othProfs' and 
            generate-id(.) = generate-id( key( 'apptPersNameKey', concat( 'PS',' ',NAME ) ) ) ) 
            or ($contextName = 'othProfs' and
          	generate-id(.) = generate-id( key( 'apptPersNameKey', concat( 'BI',' ',NAME ) ) ) )
          	or ($contextName = 'gradStudents' and
            generate-id(.) = generate-id( key( 'apptPersNameKey', concat( 'GR',' ',NAME ) ) ) )
            or ($contextName = 'othOthPers' and
            generate-id(.) = generate-id( key( 'othOthPersNameKey', NAME ) ) )">
            <fo:list-item>
              <fo:list-item-label text-align="left" >
                <fo:block xsl:use-attribute-sets="smLabText"
                  text-align-last="justify">
                  <xsl:value-of select="NAME"/>
                  <fo:leader leader-pattern="dots"/>
                </fo:block>
              </fo:list-item-label>
              <fo:list-item-body start-indent="body-start()"
                text-align="right">
                <fo:block xsl:use-attribute-sets="smAnsText">
                  <xsl:choose>
                    <xsl:when test="$contextName = 'othProfs'">
                      <xsl:value-of select="format-number( sum(key( 'apptPersNameKey', 
                        concat( 'PS',' ',NAME ) )/@AGENCY_AMOUNT_SALARY) +
                      	sum(key( 'apptPersNameKey', concat( 'BI',' ',NAME ) )
                      	/@AGENCY_AMOUNT_SALARY),'###,###')"/>
                    </xsl:when>
                    <xsl:when test="$contextName = 'gradStudents'">
                      <xsl:value-of select="format-number( sum(key( 'apptPersNameKey',
                        concat( 'GR',' ',NAME ) )/@AGENCY_AMOUNT_SALARY),'###,###')"/>
                    </xsl:when>
                    <xsl:when test="$contextName = 'othOthPers'">
                      <xsl:value-of select="format-number( sum(key( 'othOthPersNameKey', NAME ) 
                        /@AGENCY_AMOUNT_SALARY),'###,###' )"/>
                    </xsl:when>
                  </xsl:choose>
                </fo:block>
              </fo:list-item-body>
            </fo:list-item>
          </xsl:if>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:for-each select="$nodeContext">
          <fo:list-item>
            <fo:list-item-label text-align="left" >
              <fo:block xsl:use-attribute-sets="smLabText"
                text-align-last="justify">
                <xsl:value-of select="NAME"/>
                <fo:leader leader-pattern="dots"/>
              </fo:block>
            </fo:list-item-label>
            <fo:list-item-body start-indent="body-start()"
              text-align="right">
              <fo:block xsl:use-attribute-sets="smAnsText">
                <xsl:choose>
                  <xsl:when test="$contextName = 'othProfs'">
                    <xsl:value-of select="format-number( sum(key( 'persApptPeriodNameKey', 
                      concat( 'PS',' ',$periodNum,' ',NAME ))
                    	/@AGENCY_AMOUNT_SALARY) + sum(key( 'persApptPeriodNameKey', 
                    	concat( 'BI',' ',$periodNum,' ',NAME ))
                    	/@AGENCY_AMOUNT_SALARY),'###,###')"/>
                  </xsl:when>
                  <xsl:when test="$contextName = 'gradStudents'">
                    <xsl:value-of select="format-number( sum(key( 'persApptPeriodNameKey',
                      concat( 'GR',' ',$periodNum,' ',NAME ) )
                      /@AGENCY_AMOUNT_SALARY),'###,###')"/>
                  </xsl:when>
                  <xsl:when test="$contextName = 'othOthPers'">
                    <xsl:value-of select="format-number( sum(key( 'othOthPersPeriodNameKey', 
                      concat( $periodNum,' ',NAME ) ) /@AGENCY_AMOUNT_SALARY),'###,###' )"/>
                  </xsl:when>
                </xsl:choose>
              </fo:block>
            </fo:list-item-body>
          </fo:list-item>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <!-- ******************* **************************** ******************* -->
  <!-- *****************   Agency Request Amt. Template  ****************** -->
  <!-- ******************* **************************** ******************* -->
  <!-- This template prints a list of Agency Request Amounts labeled by     -->
  <!-- their descriptions. It is meant to occupy the leftmost columns of    -->
  <!-- a row.                                                               -->
  <!--                                                                      -->
  <!-- Called by:   main (24th Row, Section D. Equipment)                   -->
  <!-- XML Context  /PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD/NON_PERSONNEL -->
  <!--              /NON_PERSONNEL_ITEM[CATEGORY = 'Equipment']             -->
  <!--              /AGENCY_REQUEST_AMOUNT                                  -->
  
  <xsl:template match="AGENCY_REQUEST_AMOUNT">
    <fo:list-block>
      <xsl:for-each select=".">
        <fo:list-item>
          <fo:list-item-label text-align="left">
            <fo:block xsl:use-attribute-sets="smLabText"
              text-align-last="justify">
              <xsl:value-of select="preceding-sibling::DESCRIPTION"/>
              <fo:leader leader-pattern="dots"/>
            </fo:block>
          </fo:list-item-label>
          <fo:list-item-body text-align="right">
            <fo:block xsl:use-attribute-sets="smAnsText">
              <xsl:value-of select="format-number( node(), '###,###' )"/></fo:block>
          </fo:list-item-body>
        </fo:list-item>
      </xsl:for-each>
    </fo:list-block>
  </xsl:template>
  
</xsl:stylesheet>