!
! Financial Management Support
! Indiana University
! Bloomington, IN 47405
!
! Copyright  (C)  1999, Board of Trustees of Indiana University
!
!    check.h
!
!      Author:    Harry Walden Banek
!                 Date:    April 1999
!            Purpose:    generalize check format procedures
!                                set check format procedures for specific entities
!
!    symbols used for Check procedures
!        A    - alpha character
!        X    - alphanumeric character
!        #    -    numeric character
!        *    -    any character
! ****************************************************************************************************************
!        quick reference for procedures
!
!    Procedure name                Accepts                Passes
!    --------------                -------                ------
!    CheckAlpha                $string                    #invalid
!                            #length_minimum
!                            #length_maximum
!                            #allow_wildcard
!    CheckAlpha-Numeric        $string                    #dash_location
!                            #alpha_length            #invalid
!                            #numeric_length
!                            #allow_wildcard
!    CheckAlpha2-4            $string                    #dash_location
!                                                    #invalid
!    CheckAlpha2Numeric7        $string                    #dash_location
!                                                    #invalid
!    CheckAlphaMixed            $string                    #invalid
!                            #length_minimum
!                            #length_maximum
!    CheckAlphaSpace            $string                    #invalid
!                            #length_minimum
!                            #length_maximum
!                            #allow_wildcard
!    CheckChar                $string                    #invalid
!                            #length_minimum
!                            #length_maximum
!                            #allow_wildcard
!    CheckCharSpace            $string                    #invalid
!                            #length_minimum
!                            #length_maximum
!                            #allow_wildcard
!    CheckDate                $string                    #invalid
!                            #allow_short_year
!                            #allow_long_year
!    CheckDateType            $string                    #invalid
!    CheckDetailLevel        $string                    #invalid
!    CheckLength                $string                    #invalid
!                            #length_minimum
!                            #length_maximum
!    CheckNumber                #value                    #invalid
!                            #value_minimum
!                            #value_maximum
!    CheckNumeric            $string                    #invalid
!                            #length_minimum
!                            #length_maximum
!                            #allow_zero
!                            #allow_wildcard
!    CheckNumeric3-4            $string                    #invalid
! ****************************************************************************************************************

!begin-procedure CheckAlpha( $string, #length_minimum, #length_maximum, #allow_space, #allow_wildcard, :#invalid )
begin-procedure CheckAlpha( $string, #length_minimum, #length_maximum, #allow_wildcard, :#invalid )
! validate string of appropriate length as containing only alpha characters 
! allowing for occurrence of wildcard characters
    let #percent                    = instr( $string, '%', 1 )
    let #underscore                = instr( $string, '_', 1 )
    let #wildcard_present    = cond( ( #percent OR #underscore ), 1, 0 )
    if ( #allow_wildcard AND #wildcard_present )
        let #string_length        = #length_maximum
    else
        let #string_length        = length( $string )
    end-if
    let #too_short                    = cond( #string_length < #length_minimum, 1, 0 )
    let #too_long                        = cond( #string_length > #length_maximum, 1, 0 )
    let #string_length            = length( $string )
    let #index                            = 1
    let $character                    = substr( $string, #index, 1 )
    let #alpha_upper                = range( $character, 'A', 'Z' )
    let #percent                        = cond( $character = '%', 1, 0 )
    let #underscore                    = cond( $character = '_', 1, 0 )
    let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
!    let #character_valid        = cond( ( #alpha_upper OR ( #allow_space and #space ) OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
    let #character_valid        = cond( ( #alpha_upper OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
    while ( #index < #string_length AND #character_valid )
        let $character                    = substr( $string, #index, 1 )
        let #alpha_upper                = range( $character, 'A', 'Z' )
        let #percent                        = cond( $character = '%', 1, 0 )
        let #underscore                    = cond( $character = '_', 1, 0 )
        let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
!    let #character_valid        = cond( ( #alpha_upper OR ( #allow_space and #space ) OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
        let #character_valid        = cond( ( #alpha_upper OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
        let #index                            = #index + 1
    end-while
    let #character_invalid    = cond( #character_valid, 0, 1 )
    let #invalid    = cond( ( #too_short OR #too_long OR #character_invalid ), 1, 0 )
end-procedure

!begin-procedure CheckAlpha2-4( $string, #allow_wildcard, :#dash_location, :#invalid )
begin-procedure CheckAlpha2-4( $string, :#dash_location, :#invalid )
! validate string in format AA or hyphenated in format AA-AAAA
! allowing for occurrence of wildcard characters
! in the four alpha character extension
    let #allow_wildcard    = $_TRUE
    let #percent                        = instr( $string, '%', 1 )
    let #underscore                    = instr( $string, '_', 1 )
    let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
    if ( #allow_wildcard AND #wildcard_present )
        let #string_length            = 7
    else
        let #string_length            = length( $string )
    end-if
    let #dash_location            = instr( $string, '-', 1 )
    let #value                            = to_number( $string )
    let #index                            = 1
    let $character                    = substr( $string, #index, 1 )
    let #alpha_upper                = range( $character, 'A', 'Z' )
    let #character_valid    =     cond( #alpha_upper, 1, 0 )
    while ( #index < #dash_location AND #character_valid )
        let $character                    = substr( $string, #index, 1 )
        let #alpha_upper                = range( $character, 'A', 'Z' )
        let #character_valid        = cond( #alpha_upper, 1, 0 )
        let #index                            = #index + 1
    end-while
    if ( #character_valid )
        let #index                            = #dash_location + 1
        let $character                    = substr( $string, #index, 1 )
        let #alpha_upper                = range( $character, 'A', 'Z' )
        let #percent                        = cond( $character = '%', 1, 0 )
        let #underscore                    = cond( $character = '_', 1, 0 )
        let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
        let #character_valid    =     cond( ( #alpha_upper OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
        while ( #index < #string_length AND #character_valid )
            let $character                    = substr( $string, #index, 1 )
            let #alpha_upper                = range( $character, 'A', 'Z' )
            let #percent                        = cond( $character = '%', 1, 0 )
            let #underscore                    = cond( $character = '_', 1, 0 )
            let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
            let #character_valid        = cond( ( #alpha_upper OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
            let #index                            = #index + 1
        end-while
    end-if
    let #character_invalid        = cond( #character_valid, 0, 1 )
    let #invalid                            = cond( ( ( ( #string_length != 2 OR #dash_location != 0 ) AND ( #string_length < 4 OR #string_length > 7 OR #dash_location != 3 ) ) OR #character_invalid OR #value != 0 ), 1, 0 )
end-procedure

begin-procedure CheckAlpha-Numeric( $string, #alpha_length, #numeric_length, :#dash_location, #allow_wildcard, :#invalid )
! validate string in format AA or as hyphenated in format AA-#######
! allowing for occurrence of wildcard characters
! in the seven numeric character extension
    let #percent                        = instr( $string, '%', 1 )
    let #underscore                    = instr( $string, '_', 1 )
    let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
    if ( #allow_wildcard AND #wildcard_present )
        let #string_length            = (#alpha_length + #numeric_length + 1)
    else
        let #string_length            = length( $string )
    end-if
    let #dash_location            = instr( $string, '-', 1 )
    let #value                            = to_number( $string )
    let #index                            = 1
    let $character                    = substr( $string, #index, 1 )
    let #alpha_upper                = range( $character, 'A', 'Z' )
    let #character_valid    =     cond( #alpha_upper, 1, 0 )
    while ( #index < #dash_location AND #character_valid )
        let $character                    = substr( $string, #index, 1 )
        let #alpha_upper                = range( $character, 'A', 'Z' )
        let #character_valid        = cond( #alpha_upper, 1, 0 )
        let #index                            = #index + 1
    end-while
    Let #alpha_length_valid = cond(#index - 1 = #alpha_length, 1, 0)    
        Let #alpha_length_invalid = cond(#index - 1 != #alpha_length, 1, 0)
    if ( #character_valid and #alpha_length_valid)
        let #index                            = #dash_location + 1
        let $character                    = substr( $string, #index, 1 )
        let #numeric                        = range( $character, '0', '9' )
        let #percent                        = cond( $character = '%', 1, 0 )
        let #underscore                    = cond( $character = '_', 1, 0 )
        let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
        let #character_valid    =     cond( ( #numeric OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
        while ( #index < #string_length AND #character_valid )
            let $character                    = substr( $string, #index, 1 )
            let #numeric                        = range( $character, '0', '9' )
            let #percent                        = cond( $character = '%', 1, 0 )
            let #underscore                    = cond( $character = '_', 1, 0 )
            let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
            let #character_valid        = cond( ( #numeric OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
            let #index                            = #index + 1
        end-while
    end-if
    let #character_invalid        = cond( #character_valid, 0, 1 )
        Let #actual_numeric_length = #index - #dash_location - 1
    Let #numeric_length_valid = cond(0 < #actual_numeric_length and #actual_numeric_length <= #numeric_length, 1, 0)
        Let #numeric_length_invalid = cond (#numeric_length_valid, 0, 1)
    let #invalid = cond( ( ( #alpha_length_invalid OR #dash_location != 0 ) 
                              AND (#alpha_length_invalid or #numeric_length_invalid 
                                  OR #dash_location != #alpha_length + 1)) 
                            OR #character_invalid or #value != 0, 1, 0 )
end-procedure

begin-procedure CheckAlpha2Numeric7( $string, :#dash_location, :#invalid )
! validate string in format AA or as hyphenated in format AA-#######
! allowing for occurrence of wildcard characters
! in the seven numeric character extension
    let #allow_wildcard    = $_TRUE
    let #percent                        = instr( $string, '%', 1 )
    let #underscore                    = instr( $string, '_', 1 )
    let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
    if ( #allow_wildcard AND #wildcard_present )
        let #string_length            = 10
    else
        let #string_length            = length( $string )
    end-if
    let #dash_location            = instr( $string, '-', 1 )
    let #value                            = to_number( $string )
    let #index                            = 1
    let $character                    = substr( $string, #index, 1 )
    let #alpha_upper                = range( $character, 'A', 'Z' )
    let #character_valid    =     cond( #alpha_upper, 1, 0 )
    while ( #index < #dash_location AND #character_valid )
        let $character                    = substr( $string, #index, 1 )
        let #alpha_upper                = range( $character, 'A', 'Z' )
        let #character_valid        = cond( #alpha_upper, 1, 0 )
        let #index                            = #index + 1
    end-while
    if ( #character_valid )
        let #index                            = #dash_location + 1
        let $character                    = substr( $string, #index, 1 )
        let #numeric                        = range( $character, '0', '9' )
        let #percent                        = cond( $character = '%', 1, 0 )
        let #underscore                    = cond( $character = '_', 1, 0 )
        let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
        let #character_valid    =     cond( ( #numeric OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
        while ( #index < #string_length AND #character_valid )
            let $character                    = substr( $string, #index, 1 )
            let #numeric                        = range( $character, '0', '9' )
            let #percent                        = cond( $character = '%', 1, 0 )
            let #underscore                    = cond( $character = '_', 1, 0 )
            let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
            let #character_valid        = cond( ( #numeric OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
            let #index                            = #index + 1
        end-while
    end-if
    let #character_invalid        = cond( #character_valid, 0, 1 )
    let #invalid                            = cond( ( ( ( #string_length != 2 OR #dash_location != 0 ) AND ( #string_length != 10 OR #dash_location != 3 ) ) OR #character_invalid OR #value != 0 ), 1, 0 )
end-procedure

!begin-procedure CheckAlphaMixed( $string, #allow_space, #length_minimum, #length_maximum, :#invalid )
begin-procedure CheckAlphaMixed( $string, #length_minimum, #length_maximum, :#invalid )
! validate string of appropriate length as containing lower- and upper-case alpha characters and spaces
    let #string_length            = length( $string )
    let #too_short                    = cond( #string_length < #length_minimum, 1, 0 )
    let #too_long                        = cond( #string_length > #length_maximum, 1, 0 )
    let #index                            = 1
    let $character                    = substr( $string, #index, 1 )
    let #alpha_upper                = range( $character, 'A', 'Z' )
    let #alpha_lower                = range( $character, 'a', 'z' )
    let #space                            = cond( $character = $_SPACE, 1, 0 )
!    let #character_valid        = cond( ( #alpha_upper OR ( #allow_space and #space ) OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
    let #character_valid        = cond( ( #alpha_upper OR #alpha_lower OR #space ), 1, 0 )
    while ( #index < #string_length AND #character_valid )
        let $character                    = substr( $string, #index, 1 )
        let #alpha_upper                = range( $character, 'A', 'Z' )
        let #alpha_lower                = range( $character, 'a', 'z' )
        let #space                            = cond( $character = $_SPACE, 1, 0 )
!    let #character_valid        = cond( ( #alpha_upper OR ( #allow_space and #space ) OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
        let #character_valid        = cond( ( #alpha_upper OR #alpha_lower OR #space ), 1, 0 )
        let #index                            = #index + 1
    end-while
    let #character_invalid    = cond( #character_valid, 0, 1 )
    let #invalid                        = cond( ( #too_short OR #too_long OR #character_invalid ), 1, 0 )
end-procedure

!begin-procedure CheckAlphaSpace( $string, #length_minimum, #length_maximum, #allow_space, #allow_wildcard, :#invalid )
begin-procedure CheckAlphaSpace( $string, #length_minimum, #length_maximum, #allow_wildcard, :#invalid )
! validate string of appropriate length as containing only alpha characters and spaces
! allowing for occurrence of wildcard characters
    let #percent                    = instr( $string, '%', 1 )
    let #underscore                = instr( $string, '_', 1 )
    let #wildcard_present    = cond( ( #percent OR #underscore ), 1, 0 )
    if ( #allow_wildcard AND #wildcard_present )
        let #string_length        = #length_maximum
    else
        let #string_length        = length( $string )
    end-if
    let #too_short                    = cond( #string_length < #length_minimum, 1, 0 )
    let #too_long                        = cond( #string_length > #length_maximum, 1, 0 )
    let #string_length            = length( $string )
    let #index                            = 1
    let $character                    = substr( $string, #index, 1 )
    let #alpha_upper                = range( $character, 'A', 'Z' )
    let #space                            = cond( $character = $_SPACE, 1, 0 )
    let #percent                        = cond( $character = '%', 1, 0 )
    let #underscore                    = cond( $character = '_', 1, 0 )
    let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
!    let #character_valid        = cond( ( #alpha_upper OR ( #allow_space and #space ) OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
    let #character_valid        = cond( ( #alpha_upper OR #space OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
    while ( #index < #string_length AND #character_valid )
        let $character                    = substr( $string, #index, 1 )
        let #alpha_upper                = range( $character, 'A', 'Z' )
        let #space                            = cond( $character = $_SPACE, 1, 0 )
        let #percent                        = cond( $character = '%', 1, 0 )
        let #underscore                    = cond( $character = '_', 1, 0 )
        let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
!    let #character_valid        = cond( ( #alpha_upper OR ( #allow_space and #space ) OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
        let #character_valid        = cond( ( #alpha_upper OR #space OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
        let #index                            = #index + 1
    end-while
    let #character_invalid    = cond( #character_valid, 0, 1 )
    let #invalid                        = cond( ( #too_short OR #too_long OR #character_invalid ), 1, 0 )
end-procedure

!begin-procedure CheckChar( $string, #length_minimum, #length_maximum, #allow_space, #allow_wildcard, :#invalid )
begin-procedure CheckChar( $string, #length_minimum, #length_maximum, #allow_wildcard, :#invalid )
! validate string of appropriate length as containing only alphanumeric characters
! allowing for occurrence of wildcard characters
    let #percent                    = instr( $string, '%', 1 )
    let #underscore                = instr( $string, '_', 1 )
    let #wildcard_present    = cond( ( #percent OR #underscore ), 1, 0 )
    if ( #allow_wildcard AND #wildcard_present )
        let #string_length        = #length_maximum
    else
        let #string_length        = length( $string )
    end-if
    let #too_short                    = cond( #string_length < #length_minimum, 1, 0 )
    let #too_long                        = cond( #string_length > #length_maximum, 1, 0 )
    let #string_length            = length( $string )
    let #index                            = 1
    let $character                    = substr( $string, #index, 1 )
    let #alpha_upper                = range( $character, 'A', 'Z' )
    let #numeric                        = range( $character, '0', '9' )
    let #percent                        = cond( $character = '%', 1, 0 )
    let #underscore                    = cond( $character = '_', 1, 0 )
    let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
    let #character_valid        = cond( ( #alpha_upper OR #numeric OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
    while ( #index < #string_length AND #character_valid )
        let $character                    = substr( $string, #index, 1 )
        let #alpha_upper                = range( $character, 'A', 'Z' )
        let #numeric                        = range( $character, '0', '9' )
        let #percent                        = cond( $character = '%', 1, 0 )
        let #underscore                    = cond( $character = '_', 1, 0 )
        let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
        let #character_valid        = cond( ( #alpha_upper OR #numeric OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
        let #index                            = #index + 1
    end-while
    let #character_invalid    = cond( #character_valid, 0, 1 )
    let #invalid                        = cond( ( #too_short OR #too_long OR #character_invalid ), 1, 0 )
end-procedure

!begin-procedure CheckCharSpace( $string, #length_minimum, #length_maximum, #allow_space, #allow_wildcard, :#invalid )
begin-procedure CheckCharSpace( $string, #length_minimum, #length_maximum, #allow_wildcard, :#invalid )
! validate string of appropriate length as containing only alphanumeric characters
! allowing for occurrence of wildcard characters
    let #percent                    = instr( $string, '%', 1 )
    let #underscore                = instr( $string, '_', 1 )
    let #wildcard_present    = cond( ( #percent OR #underscore ), 1, 0 )
    if ( #allow_wildcard AND #wildcard_present )
        let #string_length        = #length_maximum
    else
        let #string_length        = length( $string )
    end-if
    let #too_short                = cond( #string_length < #length_minimum, 1, 0 )
    let #too_long                    = cond( #string_length > #length_maximum, 1, 0 )
    let #string_length        = length( $string )
    let #index                        = 1
    let $character                = substr( $string, #index, 1 )
    let #alpha_upper            = range( $character, 'A', 'Z' )
    let #numeric                    = range( $character, '0', '9' )
    let #space                        = cond( $character = $_SPACE, 1, 0 )
    let #percent                    = cond( $character = '%', 1, 0 )
    let #underscore                = cond( $character = '_', 1, 0 )
    let #wildcard_present    = cond( ( #percent OR #underscore ), 1, 0 )
!    let #character_valid        = cond( ( #alpha_upper OR #numeric OR ( #allow_space and #space ) OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
    let #character_valid    = cond( ( #alpha_upper OR #numeric OR #space OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
    while ( #index < #string_length AND #character_valid )
        let $character                = substr( $string, #index, 1 )
        let #alpha_upper            = range( $character, 'A', 'Z' )
        let #numeric                    = range( $character, '0', '9' )
        let #space                        = cond( $character = $_SPACE, 1, 0 )
        let #percent                    = cond( $character = '%', 1, 0 )
        let #underscore                = cond( $character = '_', 1, 0 )
        let #wildcard_present    = cond( ( #percent OR #underscore ), 1, 0 )
!    let #character_valid        = cond( ( #alpha_upper OR #numeric OR ( #allow_space and #space ) OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
        let #character_valid    = cond( ( #alpha_upper OR #numeric OR #space OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
        let #index                        = #index + 1
    end-while
    let #character_invalid    = cond( #character_valid, 0, 1 )
    let #invalid                        = cond( ( #too_short OR #too_long OR #character_invalid ), 1, 0 )
end-procedure

begin-procedure CheckDate( $string, #allow_short_year, #allow_long_year, :#invalid )
! validate string for date
! two possible formats: short date, MM/DD/YY or MM/DD/CCYY
    let #string_length        = length( $string )
    let #slash1_position    = instr( $string, '/', 1 )
    let #has_no_slash1        = cond( #slash1_position    = 0, 1, 0 )
    let #has_bad_slash1        = cond( #slash1_position < 2 OR #slash1_position > 3, 1, 0 )
    let #slash2_position    = instr( $string, '/', #slash1_position + 1 )
    let #has_no_slash2        = cond( #slash2_position    = 0, 1, 0 )
    let #has_bad_slash2        = cond( #slash2_position    = #slash1_position + 1 OR #slash2_position > #slash1_position + 3, 1, 0 )
    if ( #allow_short_year )
        if ( #allow_long_year )
            let #has_invalid_length    = cond( #string_length < 6 OR #string_length > 10, 1, 0 )
            let #has_bad_string            = cond( #string_length != #slash2_position + 2 AND #string_length != #slash2_position + 4, 1, 0 )
        else
            let #has_invalid_length    = cond( #string_length < 6 OR #string_length > 8, 1, 0 )
            let #has_bad_string            = cond( #string_length != #slash2_position + 2, 1, 0 )
        end-if
    else
        if ( #allow_long_year )
            let #has_invalid_length    = cond( #string_length < 8 OR #string_length > 10, 1, 0 )
            let #has_bad_string            = cond( #string_length != #slash2_position + 4, 1, 0 )
        else
            let #has_invalid_length    = cond( #string_length < 6 OR #string_length > 10, 1, 0 )
            let #has_bad_string            = cond( #string_length != #slash2_position + 2 AND #string_length != #slash2_position + 4, 1, 0 )
        end-if
    end-if
    let $month                            = substr( $string, 1, #slash1_position - 1 )
    let #month                            = to_number( $month )
    let #has_no_month                = cond( #month = 0, 1, 0 )
    let #has_invalid_month    = cond( ( #month < 1 OR #month > 31 ), 1, 0 )
    let $day                                = substr( $string, #slash1_position + 1, #slash2_position - #slash1_position )
    let #day                                = to_number( $day )
    let #has_no_day                    = cond( #day = 0, 1, 0 )
    let #has_invalid_day        = cond( ( #day < 1 OR #day > 31 ), 1, 0 )
    let $year                                = substr( $string, #slash2_position + 1, #string_length - #slash2_position )
    let #year                                = to_number( $year )
    let #has_no_year                = cond( #year    = 0, 1, 0 )
    let #invalid                        = cond( 
                                                                    #has_invalid_length
                                                            OR #has_no_slash1
                                                            OR #has_bad_slash1
                                                            OR #has_no_slash2
                                                            OR #has_bad_slash2
                                                            OR #has_bad_string
                                                            OR #has_no_month
                                                            OR #has_invalid_month
                                                            OR #has_no_day
                                                            OR #has_invalid_day
                                                            OR #has_no_year
                                                            OR #has_invalid_year, 1, 0 )
end-procedure

begin-procedure CheckLength( $string, #length_minimum, #length_maximum, :#invalid )
! validate string of appropriate length 
    let #string_length    = length( $string )
    let #too_short            = cond( #string_length < #length_minimum, 1, 0 )
    let #too_long                = cond( #string_length > #length_maximum, 1, 0 )
    let #invalid                = cond( ( #too_short OR #too_long ), 1, 0 )
end-procedure

begin-procedure CheckNumber( #value, #value_minimum, #value_maximum, :#invalid )
! validate value in correct range 
    
    let #number_valid        = range( #value, #value_minimum, #value_maximum )
    let #invalid                = cond( #number_valid, 0, 1 )
end-procedure

begin-procedure CheckNumeric( $string, #length_minimum, #length_maximum, #allow_zero, #allow_wildcard, :#invalid )
! validate string of appropriate length as containing only numeric characters
! allowing for occurrence of wildcard characters
    let #percent                    = instr( $string, '%', 1 )
    let #underscore                = instr( $string, '_', 1 )
    let #wildcard_present    = cond( ( #percent OR #underscore ), 1, 0 )
    if ( #allow_wildcard AND #wildcard_present )
        let #string_length        = #length_maximum
    else
        let #string_length        = length( $string )
    end-if
    let #too_short                    = cond( #string_length < #length_minimum, 1, 0 )
    let #too_long                        = cond( #string_length > #length_maximum, 1, 0 )
    let #value                            = to_number( $string )
    let #zero_present                = cond( #value, 0, 1 )
    if ( #allow_zero )
        if ( #zero_present )
            let #value_invalid             = $_FALSE
        else
            let #value_invalid             = $_FALSE
        end-if
    else
        if ( #zero_present )
            if ( #wildcard_present )
                let #value_invalid        = $_FALSE
            else
                let #value_invalid        = $_TRUE
            end-if
        else
            let #value_invalid            = $_FALSE
        end-if
    end-if
    let #string_length            = length( $string )
    let #index                            = 1
    let $character                    = substr( $string, #index, 1 )
    let #numeric                        = range( $character, '0', '9' )
    let #percent                        = cond( $character = '%', 1, 0 )
    let #underscore                    = cond( $character = '_', 1, 0 )
    let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
    let #character_valid        = cond( ( #numeric OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
    while ( #index < #string_length AND #character_valid )
        let $character                    = substr( $string, #index, 1 )
        let #numeric                        = range( $character, '0', '9' )
        let #percent                        = cond( $character = '%', 1, 0 )
        let #underscore                    = cond( $character = '_', 1, 0 )
        let #wildcard_present        = cond( ( #percent OR #underscore ), 1, 0 )
        let #character_valid        = cond( ( #numeric OR ( #allow_wildcard AND #wildcard_present ) ), 1, 0 )
        let #index                            = #index + 1
    end-while
    let #character_invalid    = cond( #character_valid, 0, 1 )
    let #invalid                        = cond( ( #too_short OR #too_long OR #character_invalid OR #value_invalid ), 1, 0 )
end-procedure

begin-procedure CheckNumeric3-4( $string, :#invalid )
! validate string in format ###-####
    let #string_length            = length( $string )
    let #dash_location            = instr( $string, '-', 1 )
    let #bad_dash                        = cond( #dash_location != 4, 1, 0 )
    let #index                            = 1
    let $character                    = substr( $string, #index, 1 )
    let #numeric                        = range( $character, '0', '9' )
    let #character_valid        =     cond( #numeric, 1, 0 )
    while ( #index < #dash_location AND #character_valid )
        let $character                    = substr( $string, #index, 1 )
        let #numeric                        = range( $character, '0', '9' )
        let #character_valid        =     cond( #numeric, 1, 0 )
        let #index                            = #index + 1
    end-while
    if ( #dash_location AND #character_valid )
        let #index                            = #dash_location + 1
        let $character                    = substr( $string, #index, 1 )
        let #numeric                        = range( $character, '0', '9' )
        let #character_valid        =     cond( #numeric, 1, 0 )
        while ( #index < #string_length AND #character_valid )
            let $character                    = substr( $string, #index, 1 )
            let #numeric                        = range( $character, '0', '9' )
            let #character_valid        =     cond( #numeric, 1, 0 )
            let #index                            = #index + 1
        end-while
    end-if
    let #character_invalid        = cond( #character_valid, 0, 1 )
    let #invalid                            = cond( ( #string_length != 8 OR #bad_dash OR #character_invalid ), 1, 0 )
end-procedure



