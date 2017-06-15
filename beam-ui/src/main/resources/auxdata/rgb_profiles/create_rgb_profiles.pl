#!/usr/bin/perl -w

use strict;
use Math::Trig;

# A script to generate RGB profiles based on mission wavelengths
# Written by Daniel Knowles


my @MISSION_RGB_BAND_ARRAY = (
                              {
                               'sensor' => 'MODIS',
                               'blue' => '469',
                               'green' => '555',
                               'red' => '645',
                               'nir' => '859',
                               'swir1' => '1240',
                               'swir2' => '1640',
                               'swir3' => '2130',
                               'blue-OC' => '488',
                               'green-OC' => '547',
                               'red-OC' => '667',
                               'blue-UV' => '412'
                             },
                             {
                               'sensor' => 'OLI',
                               'blue' => '482',
                               'green' => '561',
                               'red' => '655',
                               'nir' => '865',
                               'swir2' => '1609',
                               'swir3' => '2201'
                             },
                             {
                               'sensor' => 'MERIS',
                               'blue' => '490',
                               'green' => '560',
                               'red' => '665',
                               'nir' => '865',
                               'blue-UV' => '413'
                             },
                             {
                               'sensor' => 'VIIRS',
                               'blue' => '486',
                               'green' => '551',
                               'red' => '671',
                               'nir' => '862',
                               'swir1' => '1238',
                               'swir2' => '1601',
                               'swir3' => '2257',
                               'blue-UV' => '410'
                             },
                             {
                               'sensor' => 'GOCI',
                               'blue' => '490',
                               'green' => '555',
                               'red' => '660',
                               'nir' => '865',
                               'blue-UV' => '412'
                             },
                             {
                               'sensor' => 'OLCI-S3A',
                               'blue' => '490',
                               'green' => '560',
                               'red' => '665',
                               'nir' => '865',
                               'blue-UV' => '412'
                             },
                             {
                               'sensor' => 'SEAWIFS',
                               'blue' => '490',
                               'green' => '555',
                               'red' => '670',
                               'nir' => '865',
                               'blue-UV' => '412'
                             },
                             {
                               'sensor' => 'MSI-S2A',
                               'blue' => '496.6',
                               'green' => '560.0',
                               'red' => '664.5',
                               'nir' => '864.8',
                               'swir1' => '1373.5',
                               'swir2' => '1613.7',
                               'swir3' => '2202.4'
                             },
                             {
                               'sensor' => 'MSI-S3A',
                               'blue' => '492.1',
                               'green' => '559.0',
                               'red' => '665.0',
                               'nir' => '864.0',
                               'swir1' => '1376.9',
                               'swir2' => '1610.4',
                               'swir3' => '2185.7'
                             }
                            );




my @trueColorArrays = (
                           ["red", "green", "blue", ""],
                           ["red-OC", "green-OC", "blue-OC", "OC"],
                           ["red", "green", "blue-UV", "BlueUV"]
                       );

my @falseColorArrays = (
                           ["blue", "swir1", "swir3", "SnowCloud"],
                           ["blue", "swir2", "swir3", "SnowCloud"],
                           ["nir", "red", "green", "Vegetation"],
                           ["swir3", "swir1", "nir", "ShortwaveIR"],
                           ["swir3", "swir2", "nir", "ShortwaveIR"]
                       );

my @additionalFalseColorArrays = (
                           ["nir", "swir1", "blue", ""],
                           ["nir", "swir2", "blue", ""],
                           ["nir", "swir1", "red", ""],
                           ["nir", "swir2", "red", ""],
                           ["swir1", "nir", "blue", ""],
                           ["swir2", "nir", "blue", ""],
                           ["swir1", "nir", "red", ""],
                           ["swir2", "nir", "red", ""],
                           ["swir3", "nir", "green", ""],
                           ["swir3", "nir", "red", ""],
                           ["swir3", "swir1", "red", ""],
                           ["swir3", "swir2", "red", ""]
                       );


my $product = "rhos";
my $logMinDefault = 0.01;
my $logMaxDefault = 1.0;
my $atanOffsetDefault = 0.015;
my $atanGainDefault = 20;

for (@MISSION_RGB_BAND_ARRAY) {
    my %tmpHash = %{$_};

    &create_atan_file(\%tmpHash, $trueColorArrays[0], 1, $atanGainDefault, $atanOffsetDefault, $product, 0);

    &create_atan_file(\%tmpHash, $trueColorArrays[0], 1, 10, 0.015, $product, 1);
    &create_atan_file(\%tmpHash, $trueColorArrays[0], 1, 20, 0.015, $product, 1);
    &create_atan_file(\%tmpHash, $trueColorArrays[0], 1, 30, 0.015, $product, 1);
    &create_atan_file(\%tmpHash, $trueColorArrays[0], 1, 60, 0.015, $product, 1);
    &create_atan_file(\%tmpHash, $trueColorArrays[0], 1, 10, 0.03, $product, 1);
    &create_atan_file(\%tmpHash, $trueColorArrays[0], 1, 20, 0.03, $product, 1);
    &create_atan_file(\%tmpHash, $trueColorArrays[0], 1, 30, 0.03, $product, 1);
    &create_atan_file(\%tmpHash, $trueColorArrays[0], 1, 60, 0.03, $product, 1);
    &create_atan_file(\%tmpHash, $trueColorArrays[0], 1, 60, 0.05, $product, 1);

    &create_atan_log_hybrid_file(\%tmpHash, $trueColorArrays[0], 1, $atanGainDefault, $atanOffsetDefault, $logMinDefault, $logMaxDefault, $product, 'LAND', 0);
    &create_atan_log_hybrid_file(\%tmpHash, $trueColorArrays[0], 1, $atanGainDefault, $atanOffsetDefault, $logMinDefault, $logMaxDefault, $product, 'LandMask', 0);

    for (@falseColorArrays) {
        my @falseColorArray = @{$_};
        &create_log_file(\%tmpHash, $_, 0, $logMinDefault, $logMaxDefault, $product, 0);
    }

    for (@additionalFalseColorArrays) {
        my @falseColorArray = @{$_};
        &create_log_file(\%tmpHash, $_, 0, $logMinDefault, $logMaxDefault, $product, 1);
    }
    for (@trueColorArrays) {
        my @trueColorArray = @{$_};
        &create_log_file(\%tmpHash, $_, 1, $logMinDefault, $logMaxDefault, $product, 0);
    }
}




sub create_atan_log_hybrid_file
{
    my $tmpHash_ref = shift;
    my $colorArray_ref = shift;
    my $true_color = shift;
    my $gain = shift;
    my $offset = shift;
    my $min = shift;
    my $max = shift;
    my $product = shift;
    my $mask = shift;
    my $putInSubDir = shift;

    my $mode = $mask . "_Hybrid";

    my $newHash_ref = &create_contents_hash($tmpHash_ref, $colorArray_ref, $true_color, $product, $mode);

    if (defined $newHash_ref) {
        my %tmpHash = %{$newHash_ref};

        my $red_inner_mask_expr = &create_atan_expression($tmpHash{'red_band'}, $gain, $offset);
        my $green_inner_mask_expr = &create_atan_expression($tmpHash{'green_band'}, $gain, $offset);
        my $blue_inner_mask_expr = &create_atan_expression($tmpHash{'blue_band'}, $gain, $offset);

        my $red_inner_expr = &create_log_expression($tmpHash{'red_band'}, $min, $max);
        my $green_inner_expr = &create_log_expression($tmpHash{'green_band'}, $min, $max);
        my $blue_inner_expr = &create_log_expression($tmpHash{'blue_band'}, $min, $max);

        $tmpHash{'red_expr'} = &create_expression($tmpHash{'red_band'}, $tmpHash{'green_band'}, $tmpHash{'blue_band'}, $red_inner_expr, $red_inner_mask_expr, $mask );
        $tmpHash{'green_expr'} = &create_expression($tmpHash{'red_band'}, $tmpHash{'green_band'}, $tmpHash{'blue_band'}, $green_inner_expr, $green_inner_mask_expr, $mask);
        $tmpHash{'blue_expr'} = &create_expression($tmpHash{'red_band'}, $tmpHash{'green_band'}, $tmpHash{'blue_band'}, $blue_inner_expr, $blue_inner_mask_expr, $mask);

        &create_file(\%tmpHash, $putInSubDir);
    }
}



sub create_atan_file
{
    my $tmpHash_ref = shift;
    my $colorArray_ref = shift;
    my $true_color = shift;
    my $gain = shift;
    my $offset = shift;
    my $product = shift;
    my $putInSubDir = shift;


    my $mode = "Atan_${gain}_${offset}";

    my $newHash_ref = &create_contents_hash($tmpHash_ref, $colorArray_ref, $true_color, $product, $mode);

    if (defined $newHash_ref) {
        my %tmpHash = %{$newHash_ref};

        my $red_inner_expr = &create_atan_expression($tmpHash{'red_band'}, $gain, $offset);
        my $green_inner_expr = &create_atan_expression($tmpHash{'green_band'}, $gain, $offset);
        my $blue_inner_expr = &create_atan_expression($tmpHash{'blue_band'}, $gain, $offset);

        $tmpHash{'red_expr'} = &create_expression($tmpHash{'red_band'}, $tmpHash{'green_band'}, $tmpHash{'blue_band'}, $red_inner_expr );
        $tmpHash{'green_expr'} = &create_expression($tmpHash{'red_band'}, $tmpHash{'green_band'}, $tmpHash{'blue_band'}, $green_inner_expr);
        $tmpHash{'blue_expr'} = &create_expression($tmpHash{'red_band'}, $tmpHash{'green_band'}, $tmpHash{'blue_band'}, $blue_inner_expr);

        &create_file(\%tmpHash, $putInSubDir);
    }
}



sub create_log_file
{
    my $tmpHash_ref = shift;
    my $colorArray_ref = shift;
    my $true_color = shift;
    my $min = shift;
    my $max = shift;
    my $product = shift;
    my $putInSubDir = shift;

    my $mode = "Log";

    my $newHash_ref = &create_contents_hash($tmpHash_ref, $colorArray_ref, $true_color, $product, $mode);

    if (defined $newHash_ref) {
        my %tmpHash = %{$newHash_ref};

        my $red_inner_expr = &create_log_expression($tmpHash{'red_band'}, $min, $max);
        my $green_inner_expr = &create_log_expression($tmpHash{'green_band'}, $min, $max);
        my $blue_inner_expr = &create_log_expression($tmpHash{'blue_band'}, $min, $max);

        $tmpHash{'red_expr'} = &create_expression($tmpHash{'red_band'}, $tmpHash{'green_band'}, $tmpHash{'blue_band'}, $red_inner_expr );
        $tmpHash{'green_expr'} = &create_expression($tmpHash{'red_band'}, $tmpHash{'green_band'}, $tmpHash{'blue_band'}, $green_inner_expr);
        $tmpHash{'blue_expr'} = &create_expression($tmpHash{'red_band'}, $tmpHash{'green_band'}, $tmpHash{'blue_band'}, $blue_inner_expr);

        &create_file(\%tmpHash, $putInSubDir);
    }
}




sub create_atan_expression
{
    my $band = shift;
    my $gain = shift;
    my $offset = shift;

    my $A = 0;
    my $B = 0;

    $B = 1/(atan( $gain * (1 - $offset)) - atan( $gain * (0- $offset)));
    $A = - $B * atan( $gain * (0-$offset));

    my $expression = "$A + $B * atan( $gain * ($band - $offset))" ;

    return $expression;
}


sub create_log_expression
{
    my $band = shift;
    my $min = shift;
    my $max = shift;

    my $expression = "log(${band}/${min})/log(${max}/${min})";

    return $expression;
}




sub create_expression
{
    my $red_band = shift;
    my $green_band = shift;
    my $blue_band = shift;
    my $default_expression = shift;
    my $mask_expression = shift;
    my $mask_name = shift;

    my $expression = 'if (' . $red_band .' \!\= NaN and ' . $green_band . ' \!\= NaN and ' . $blue_band . ' \!\= NaN) then ';

    if (defined $mask_expression)
    {
        $expression .= "(if ($mask_name) then ($mask_expression) else ($default_expression))";
    }
    else
    {
        $expression .= "($default_expression)";
    }

    $expression .= " else NaN";

    return $expression;
}




sub create_contents_hash
{
    my $tmpHash_ref = shift;
    my $colorArray_ref = shift;
    my $true_color = shift;
    my $product = shift;
    my $mode = shift;

    my %tmpHash = %{$tmpHash_ref};
    my @colorArray = @{$colorArray_ref};
    my $red_source = $colorArray[0];
    my $green_source = $colorArray[1];
    my $blue_source = $colorArray[2];
    my $functionality = $colorArray[3];


    if (defined $tmpHash{$red_source} && defined $tmpHash{$green_source} && defined $tmpHash{$blue_source}) {
        $tmpHash{'red_source'} = $red_source;
        $tmpHash{'green_source'} = $green_source;
        $tmpHash{'blue_source'} = $blue_source;

        $tmpHash{'red_wave'} = $tmpHash{$red_source};
        $tmpHash{'green_wave'} = $tmpHash{$green_source};
        $tmpHash{'blue_wave'} = $tmpHash{$blue_source};

        $tmpHash{'red_band'} = $product . "_" . $tmpHash{'red_wave'};
        $tmpHash{'green_band'} = $product . "_" . $tmpHash{'green_wave'};
        $tmpHash{'blue_band'} = $product . "_" . $tmpHash{'blue_wave'};

        $tmpHash{'product'} = $product;
        $tmpHash{'true_color'} = $true_color;
        $tmpHash{'functionality'} = $functionality;
        $tmpHash{'mode'} = $mode;
    } else {
        return undef();
    }

    return \%tmpHash;
}






sub create_file {

    my $tmpHash_ref = shift;
    my $putInSubDir = shift;

    my %tmpHash = %{$tmpHash_ref};

        my $color_type_camel = ($tmpHash{'true_color'} == 1) ? "TrueColor" : "FalseColor";
        my $color_type = ($tmpHash{'true_color'} == 1) ? "true color" : "false color";

        if (length $tmpHash{'functionality'} > 0) {
            $tmpHash{'functionality'} = "_" . $tmpHash{'functionality'};
        }
        my $basename = $tmpHash{'sensor'} . "_${color_type_camel}_" . $tmpHash{'red_wave'} . "_" . $tmpHash{'green_wave'} . "_" . $tmpHash{'blue_wave'}  . "_" . $tmpHash{'mode'} . "$tmpHash{'functionality'}";
        my $name = $tmpHash{'sensor'} . "_${color_type_camel}_(" . $tmpHash{'red_wave'} . "," . $tmpHash{'green_wave'} . "," . $tmpHash{'blue_wave'} . ")_" . $tmpHash{'mode'} . "$tmpHash{'functionality'}";
        my $filename = $basename . ".rgb";

        my $contents = "# RGB-Image Configuration Profile\n";
        $contents .= "# $name\n";
        $contents .= "#\n";
        $contents .= "# A ${color_type} RGB configuration profile which uses a $tmpHash{'red_source'}, $tmpHash{'green_source'} and $tmpHash{'blue_source'} band for the\n";
        $contents .= "# respective RGB color model channels\n";
        $contents .= "#\n";
        $contents .= "name=$name\n";
        $contents .= "red=$tmpHash{'red_expr'}\n";
        $contents .= "green=$tmpHash{'green_expr'}\n";
        $contents .= "blue=$tmpHash{'blue_expr'}\n";

        if ($putInSubDir) {
            if ($tmpHash{'true_color'}) {
                my $subDir = "additional_true_color_profiles";
                unless (-d $subDir) {
                    mkdir $subDir;
                }
                $filename = $subDir . "/" . $filename;

            } else {
                my $subDir = "additional_false_color_profiles";
                unless (-d $subDir) {
                    mkdir $subDir;
                }
                $filename = $subDir . "/" . $filename;
            }
        }

        &write_file($filename, $contents);
}




sub write_file
{
    my $filename = shift;
    my $contents = shift;

    if (open(WDATA, ">$filename")) {
        print WDATA $contents;
        close (WDATA);
    } else {
        print "error writing file: '$filename'\n";
    }
}

