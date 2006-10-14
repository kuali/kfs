!
! Copyright 2005-2006 The Kuali Foundation.
! 
! $Source: /opt/cvs/kfs/work/fis-standard-reports/kul_enter.h,v $
! 
! Licensed under the Educational Community License, Version 1.0 (the "License");
! you may not use this file except in compliance with the License.
! You may obtain a copy of the License at
! 
! http://www.opensource.org/licenses/ecl1.php
! 
! Unless required by applicable law or agreed to in writing, software
! distributed under the License is distributed on an "AS IS" BASIS,
! WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
! See the License for the specific language governing permissions and
! limitations under the License.
! DO NOT add comments before the blank line below, or they will disappear.

!
! Financial Management Support
! Indiana University
! Bloomington, IN 47405
!
! Copyright  (C)  2000, Board of Trustees of Indiana University
!
!                                               enter.h 
!
!  Author:      Jerry K. Neal 
!
!  Adapted by:  Chris Shelton
!               For use with the FIS monthly standard labor reports.
!               November 2002.
!
! Purpose:  To group together all common input procedures.    


begin-procedure EnterAccount
    let $account                    = $BLANK
    let $account_clause        = $BLANK
    let $for_account_line    = $BLANK
    input $account 'Account Number (#######; blank to end)'
    do CleanResponse( $account )
    let #length_minimum    = 7
    let #length_maximum    = 7
    while ( $account != $BLANK AND $account != $EOL )
        if ( $account = '%' )
            if ( #level_upper = 0 )
                let #level_upper    = 2
            end-if
            let #level_lower    = 2
            let $account_clause        = $EXPRESSION_TRUE
            let $for_account_line    = 'Level Selected'
        else
            do CheckNumeric( $account, #length_minimum, #length_maximum, #NO_ZERO, #WILDCARD, #format_invalid )
            if ( #format_invalid )
            ! invalid account
                do ErrorAccount( $account )
            else
                if ( #level_upper = 0 )
                    let #level_upper    = 2
                end-if
                let #level_lower    = 2
                if ( $account_clause    = $BLANK )
                    let $for_account_line    = $account
                else
                    let $account_clause        = $account_clause || 'OR '
                    let $for_account_line    = $for_account_line || ', ' || $account
                end-if
                if ( instr( $account, '%', 1 ) > 0 OR instr( $account, '_', 1 ) > 0 )
                    let $account_clause    = $account_clause || 'a.account_nbr LIKE ''' || $account || ''' '
                else
                    let $account_clause    = $account_clause || 'a.account_nbr    = ''' || $account || ''' '
                end-if
            end-if
        end-if
        input $account 'Account Number (#######; blank to end)'
        do CleanResponse( $account )
    end-while
    if ( $account_clause    = $BLANK )
        let $account_clause        = $EXPRESSION_TRUE
        let $for_account_line    = $NONE
    end-if
    let $account_clause        = '( ' || $account_clause || ') '
    let $where_account        = $account_clause
    let $for_account_line    = 'For Account Number(s): ' || $for_account_line
end-procedure

begin-procedure EnterCenter
    let $center                        = $BLANK
    let $center_clause        = $BLANK
    let $for_center_line    = $BLANK
    input $center 'Responsibility Center Code (XX; blank to end)'
    do CleanResponse( $center )
    let #length_minimum    = 2
    let #length_maximum    = 2
    while ( $center != $BLANK AND $center != $EOL )
        if ( $center = '%' )
            if ( #level_upper = 0 )
                let #level_upper    = 5
            end-if
            let #level_lower    = 5
            let $center_clause        = $EXPRESSION_TRUE
            let $for_center_line    = 'Level Selected'
        else
            do CheckChar( $center, #length_minimum, #length_maximum, #NO_WILDCARD, #format_invalid )
            if ( #level_upper = 0 )
                let #level_upper    = 5
            end-if
            let #level_lower    = 5
            if ( #format_invalid )
            ! invalid responsibility center code
                do ErrorCenter( $center )
            else
                if ( $center_clause    = $BLANK )
                    let $for_center_line    = $center
                else
                    let $center_clause        = $center_clause || 'OR '
                    let $for_center_line    = $for_center_line || ', ' || $center
                end-if
                if ( instr( $center, '%', 1 ) > 0 OR instr( $center, '_', 1 ) > 0 )
                    let $center_clause    = $center_clause || 'a.rc_cd LIKE ''' || $center || ''' '
                else
                    let $center_clause    = $center_clause || 'a.rc_cd    = ''' || $center || ''' '
                end-if
            end-if
        end-if
        input $center 'Responsibility Center Code (XX; blank to end)'
        do CleanResponse( $center )
    end-while
    if ( $center_clause    = $BLANK )
        let $center_clause        = $EXPRESSION_TRUE
        let $for_center_line    = $NONE
    end-if
    let $center_clause        = '( ' || $center_clause || ') '
    let $where_center            = $center_clause
    let $for_center_line    = 'For Responsibility Center Code(s): ' || $for_center_line
end-procedure

begin-procedure EnterChart
    let $chart                            = $BLANK
    let $chart_clause                = $BLANK
    let $report_chart                = $BLANK
    let $for_chart_line            = $BLANK
    let $report_chart_line    = $BLANK
    input $chart 'Chart of Accounts Code (XX)'
    let #length_minimum    = 2
    let #length_maximum    = 2
    do CleanResponse( $chart )
    while ( $chart != $BLANK AND $chart != $EOL )
        do CheckAlpha( $chart, #length_minimum, #length_maximum, #NO_WILDCARD, #format_invalid )
        if ( #format_invalid )
        ! invalid chart of accounts code
            do ErrorChart( $chart )
        else
            if ( $chart_clause    = $BLANK )
                let $for_chart_line    = $chart
            else
                let $chart_clause        = $chart_clause || 'OR '
                let $for_chart_line    = $for_chart_line || ', ' || $chart
            end-if
            let $chart_clause    = $chart_clause || 'a.fin_coa_cd    = ''' || $chart || ''' '
            if ( $report_chart = $BLANK )
                let $report_chart                = 'h.rpts_to_fin_coa_cd    = ''' || $chart || ''' '
                let $report_chart_line    = $chart
            end-if
        end-if
        input $chart 'Chart of Accounts Code (XX; blank to end)'
        do CleanResponse( $chart )
    end-while
    if ( $chart_clause    = $BLANK )
    ! missing chart of accounts code
        do ErrorChartMissing( 'Blank entry' )
    else
        let $chart_clause                = '( ' || $chart_clause || ') '
        let $report_chart                = '( ' || $report_chart || ') '
        let $where_chart                = $chart_clause
    end-if
    let $where_report_chart    = $report_chart
    let $for_chart_line            = 'For Chart of Accounts Code(s): ' || $for_chart_line
    let $report_chart_line    = 'Reporting to Chart of Accounts Code: ' || $report_chart_line
end-procedure

begin-procedure EnterFiscalPeriod
    let $fiscal_period         = $BLANK 
    let $fiscal_period_clause    = $BLANK
    let $fiscal_period_line        = $BLANK
    input $fiscal_period 'Fiscal Period (# or ##; default = prior period)'
    ! if no period entered,
    ! set fiscal period to period prior to current date
    if ( $fiscal_period = $BLANK )
        let $fiscal_period    = $period_default
    end-if
    let #length_minimum    = 1
    let #length_maximum    = 2
    do CheckNumeric( $fiscal_period, #length_minimum, #length_maximum, #NO_ZERO, #NO_WILDCARD, #format_invalid )
    if ( #format_invalid )
        do ErrorPeriod( $fiscal_period )
    else
        let #fiscal_period            = to_number( $fiscal_period )
        if ( #fiscal_period = 14 )
            let $fiscal_period    = '12/13'
        end-if
        let $fiscal_period_line    = $fiscal_period
        let $fiscal_period_line    = 'For Fiscal Period: ' || $fiscal_period_line
    end-if
end-procedure

begin-procedure EnterFiscalYear
    let $year_fiscal_cur                = $BLANK
    let $year_fiscal_cur_clause    = $BLANK
    let $year_fiscal_cur_line        = $BLANK
    input $year_fiscal_cur 'Fiscal Year (####; default = fiscal year of prior period)'
    ! if no year entered,
    ! set fiscal year to fiscal year of prior period
    if ( $year_fiscal_cur = $BLANK )
        let $year_fiscal_cur    = $year_default
    end-if
    let #length_minimum    = 4
    let #length_maximum    = 4
    do CheckNumeric( $year_fiscal_cur, #length_minimum, #length_maximum, #NO_ZERO, #NO_WILDCARD, #format_invalid )
    if ( #format_invalid )
        do ErrorYear( $year_fiscal_cur, 'four' )
    else
        let #year_fiscal_cur                = to_number( $year_fiscal_cur )
        let #year_fiscal_cmp                = #year_fiscal_cur - 1
        let #year_fiscal_next               = #year_fiscal_cur + 1
        let $year_fiscal_cur        = to_char(#year_fiscal_cur)
        let $year_fiscal_cmp        = to_char(#year_fiscal_cmp)
        let $year_fiscal_next       = to_char(#year_fiscal_next)
        let $year_fiscal_cur_clause         = $year_fiscal_cur
        let $year_fiscal_cmp_clause    = to_char( #year_fiscal_cmp )
        let $year_fiscal_cur_clause                = 'b.univ_fiscal_yr    = ' || $year_fiscal_cur_clause || $SPACE
        let $year_fiscal_cmp_clause    = 'b.univ_fiscal_yr    = ' || $year_fiscal_cmp_clause || $SPACE
        let $year_fiscal_cur_clause                = '( ' || $year_fiscal_cur_clause || ') '
        let $year_fiscal_cmp_clause    = '( ' || $year_fiscal_cmp_clause || ') '
        let $where_year_cur                = $year_fiscal_cur_clause        
        let $where_year_cmp                    = $year_fiscal_cmp_clause        
        let $where_year_next               = '(b.univ_fiscal_yr ='||$year_fiscal_next||')'     
        let $year_fiscal_cur_line                    = $year_fiscal_cur
        let $year_fiscal_cur_line                    = 'For Fiscal Year: ' || $year_fiscal_cur_line
        let #year_fiscal_diff           = #year_fiscal_cur - #year_fiscal_cmp
        let $year_fiscal_diff           = to_char(#year_fiscal_diff)    
    end-if
end-procedure

begin-procedure EnterIncludeHierarchy
! check that organization is not blank 
    let $include_hierarchy                = $BLANK
    let $include_hierarchy_line        = $BLANK
    input $include_hierarchy 'Include Organization Hierarchy (N/Y; default N)'
    do CleanAnswer( $include_hierarchy )
    if ( $include_hierarchy = 'Y' )
        if ( $report_organization = $EXPRESSION_TRUE )
        ! missing organization for hierarchy
            do ErrorOrganizationMissing( 'Missing organization code' )
        else
            let $include_hierarchy_line    = 'Yes'
            let #include_hierarchy            = 1
        end-if
    else
        let $include_hierarchy_line    = 'No'
        let #include_hierarchy            = 0
    end-if
    let $include_hierarchy_line        = 'Include Organization Hierarchy: ' || $include_hierarchy_line
end-procedure

begin-procedure EnterOrganization
! required entry when using organization hierarchy
    let $organization                            = $BLANK
    let $organization_clause            = $BLANK
    let $for_organization_line        = $BLANK
    let $report_organization_line    = $BLANK
    input $organization 'Organization Code (XXXX; blank to end)'
    do CleanResponse( $organization )
    let #length_minimum    = 2
    let #length_maximum    = 4
    while ( $organization != $BLANK AND $organization != $EOL )
        if ( $organization = '%' )
            if ( #level_upper = 0 )
                let #level_upper    = 4
            end-if
            let #level_lower    = 4
            let $organization_clause        = $EXPRESSION_TRUE
            let $for_organization_line    = 'Level Selected'
        else
            do CheckAlpha( $organization, #length_minimum, #length_maximum, #NO_WILDCARD, #format_invalid )
            if ( #format_invalid )
            ! invalid organization code
                do ErrorOrganization( $organization )
            else
                if ( #level_upper = 0 )
                    let #level_upper    = 4
                end-if
                let #level_lower    = 4
                if ( $organization_clause    = $BLANK )
                    let $for_organization_line    = $organization
                else
                    let $organization_clause    = $organization_clause || 'OR '
                    let $for_organization_line    = $for_organization_line || ', ' || $organization
                end-if
                let $organization_clause    = $organization_clause || 'a.org_cd    = ''' || $organization || ''' '
                if ( $report_organization_line = $BLANK )
                    let $report_organization            = 'h.rpts_to_org_cd    = ''' || $organization || ''' '
                    let $report_organization_line    = $organization
                end-if
            end-if
        end-if
        input $organization 'Organization Code (XXXX; blank to end)'
        do CleanResponse( $organization )
    end-while
    if ( $organization_clause    = $BLANK )
        let $organization_clause            = $EXPRESSION_TRUE
        let $report_organization            = $EXPRESSION_TRUE
        let $for_organization_line        = $NONE
        let $report_organization_line    = $NONE
    end-if
    let $organization_clause                = '( ' || $organization_clause || ') '
    let $report_organization                = '( ' || $report_organization || ') '
    let $where_organization                    = $organization_clause
    let $where_report_organization    = $report_organization
    let $for_organization_line            = 'For Organization Code(s): ' || $for_organization_line
    let $report_organization_line        = 'Reporting to Organization Code: ' || $report_organization_line
end-procedure

begin-procedure EnterSubaccount
    let $subaccount                        = $BLANK
    let $subaccount_clause        = $BLANK
    let $for_subaccount_line    = $BLANK
    input $subaccount 'Subaccount Number (XXXXX; blank to end)'
    do CleanResponse( $subaccount )
    let #length_minimum    = 1
    let #length_maximum    = 5
    while ( $subaccount != $BLANK AND $subaccount != $EOL )
        if ( $subaccount = '%' )
            if ( #level_upper = 0 )
                let #level_upper    = 1
            end-if
            let #level_lower    = 1
            let $subaccount_clause        = $EXPRESSION_TRUE
            let $for_subaccount_line    = 'Level Selected'
        else
            do CheckChar( $subaccount, #length_minimum, #length_maximum, #WILDCARD, #format_invalid )
            if ( #format_invalid )
            ! invalid subaccount
                do ErrorSubaccount( $subaccount )
            else
            if ( #level_upper = 0 )
                let #level_upper    = 1
            end-if
            let #level_lower    = 1
                if ( $subaccount_clause = $BLANK )
                    let $for_subaccount_line    = $subaccount
                else
                    let $subaccount_clause        = $subaccount_clause || 'OR '
                    let $for_subaccount_line    = $for_subaccount_line || ', ' || $subaccount
                end-if
                if ( instr( $subaccount, '%', 1 ) > 0 OR instr( $subaccount, '_', 1 ) > 0 )
                    let $subaccount_clause    = $subaccount_clause || 's.sub_acct_nbr LIKE ''' || $subaccount || ''' '
                else
                    let $subaccount_clause    = $subaccount_clause || 's.sub_acct_nbr    = ''' || $subaccount || ''' '
                end-if
            end-if
        end-if
        input $subaccount 'Subaccount Number (XXXXX; blank to end)'
        do CleanResponse( $subaccount )
    end-while
    if ( $subaccount_clause    = $BLANK )
        let $subaccount_clause        = $EXPRESSION_TRUE
        let $for_subaccount_line    = $NONE
    end-if
    let $subaccount_clause        = '( ' || $subaccount_clause || ') '
    let $where_subaccount            = $subaccount_clause
    let $for_subaccount_line    = 'For Subaccount Number(s): ' || $for_subaccount_line
end-procedure




