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



my @trueColorArray = ("red", "green", "blue");
my @trueColorOCArray = ("red-OC", "green-OC", "blue-OC");
my @trueColorBlueUVArray = ("red", "green", "blue-UV");
my @falseColorSnowCloudArray = ("blue", "swir1", "swir3");
my @falseColorSnowCloud2Array = ("blue", "swir2", "swir3");
my @falseColorVegetationArray = ("nir", "red", "green");
my @falseColorShortwaveIRArray = ("swir3", "swir1", "nir");
my @falseColorShortwaveIR2Array = ("swir3", "swir2", "nir");

#my $gain = 10.0;
#my $offset = 0.015;
my $product = "rhos";

for (@MISSION_RGB_BAND_ARRAY) {
    my %tmpHash = %{$_};

    &create_atan_file(\%tmpHash, \@trueColorArray, 1, "", 10, 0.015, $product);
    &create_atan_file(\%tmpHash, \@trueColorArray, 1, "", 20, 0.015, $product);
    &create_atan_file(\%tmpHash, \@trueColorArray, 1, "", 10, 0.03, $product);
    &create_atan_file(\%tmpHash, \@trueColorArray, 1, "", 20, 0.03, $product);

    &create_hybrid_file(\%tmpHash, \@trueColorArray, 1, "", $product);

    &create_log_file(\%tmpHash, \@trueColorArray, 1, "", $product);
    &create_log_file(\%tmpHash, \@trueColorOCArray, 1, "OC_", $product);
    &create_log_file(\%tmpHash, \@trueColorBlueUVArray, 1, "BlueUV_", $product);
    &create_log_file(\%tmpHash, \@falseColorSnowCloudArray, 0, "SnowCloud_", $product);
    &create_log_file(\%tmpHash, \@falseColorSnowCloud2Array, 0, "SnowCloud_", $product);
    &create_log_file(\%tmpHash, \@falseColorVegetationArray, 0, "Vegetation_", $product);
    &create_log_file(\%tmpHash, \@falseColorShortwaveIRArray, 0, "ShortwaveIR_", $product);
    &create_log_file(\%tmpHash, \@falseColorShortwaveIR2Array, 0, "ShortwaveIR_", $product);
}




sub create_hybrid_file
{
    my $tmpHash_ref = shift;
    my $colorArray_ref = shift;
    my $true_color = shift;
    my $functionality = shift;
    my $product = shift;

    my %tmpHash = %{$tmpHash_ref};
    my @colorArray = @{$colorArray_ref};
    my $red_source = $colorArray[0];
    my $green_source = $colorArray[1];
    my $blue_source = $colorArray[2];

    my $red_wave = $tmpHash{$red_source};
    my $green_wave = $tmpHash{$green_source};
    my $blue_wave = $tmpHash{$blue_source};

    if (defined $red_wave && defined $green_wave && defined $blue_wave) {
        my $gain = 20.0;
        my $offset = 0.015;
        my $min = '0.01';
        my $max = '1.0';
        my $mask = 'LAND';
        my $mode = "Hybrid";

        my $color_type_camel = ($true_color == 1) ? "TrueColor" : "FalseColor";
        my $color_type = ($true_color == 1) ? "true color" : "false color";
       
        my $red_band = $product . "_" . $red_wave;
        my $green_band = $product . "_" . $green_wave;
        my $blue_band = $product . "_" . $blue_wave;

        my $basename = $tmpHash{'sensor'} . "_${color_type_camel}_${functionality}" . $red_wave . "_" . $green_wave . "_" . $blue_wave . "_" . $mode;
        my $name = $tmpHash{'sensor'} . "_${color_type_camel}_${functionality}(" . $red_wave . "," . $green_wave . "," . $blue_wave . ")_" . $mode;
        my $filename = $basename . ".rgb";

        my $red_mask_expr = &create_atan_expression($red_band, $gain, $offset);
        my $green_mask_expr = &create_atan_expression($green_band, $gain, $offset);
        my $blue_mask_expr = &create_atan_expression($blue_band, $gain, $offset);

        my $red_expr = &create_log_expression($red_band, $min, $max);
        my $green_expr = &create_log_expression($green_band, $min, $max);
        my $blue_expr = &create_log_expression($blue_band, $min, $max);

        my $red_full_expr = &create_expression($red_band, $green_band, $blue_band, $red_expr, $red_mask_expr, $mask );
        my $green_full_expr = &create_expression($red_band, $green_band, $blue_band, $green_expr, $green_mask_expr, $mask);
        my $blue_full_expr = &create_expression($red_band, $green_band, $blue_band, $blue_expr, $blue_mask_expr, $mask);

        my $contents = "# RGB-Image Configuration Profile\n";
        $contents .= "# $name\n";
        $contents .= "#\n";
        $contents .= "# A ${color_type} RGB configuration profile which uses a ${red_source}, ${green_source} and ${blue_source} band for the\n";
        $contents .= "# respective RGB color model channels\n";
        $contents .= "#\n";
        $contents .= "name=$name\n";
        $contents .= "red=$red_full_expr\n";
        $contents .= "green=$green_full_expr\n";
        $contents .= "blue=$blue_full_expr\n";

#        print "$contents\n";

        &write_file($filename, $contents);
    }
}



sub create_atan_file
{
    my $tmpHash_ref = shift;
    my $colorArray_ref = shift;
    my $true_color = shift;
    my $functionality = shift;
    my $gain = shift;
    my $offset = shift;
    my $product = shift;

    my %tmpHash = %{$tmpHash_ref};
    my @colorArray = @{$colorArray_ref};
    my $red_source = $colorArray[0];
    my $green_source = $colorArray[1];
    my $blue_source = $colorArray[2];

    my $red_wave = $tmpHash{$red_source};
    my $green_wave = $tmpHash{$green_source};
    my $blue_wave = $tmpHash{$blue_source};

    if (defined $red_wave && defined $green_wave && defined $blue_wave) {
        my $mode = "Atan_${gain}_${offset}";

        my $color_type_camel = ($true_color == 1) ? "TrueColor" : "FalseColor";
        my $color_type = ($true_color == 1) ? "true color" : "false color";
       
        my $red_band = $product . "_" . $red_wave;
        my $green_band = $product . "_" . $green_wave;
        my $blue_band = $product . "_" . $blue_wave;

        my $basename = $tmpHash{'sensor'} . "_${color_type_camel}_${functionality}" . $red_wave . "_" . $green_wave . "_" . $blue_wave . "_" . $mode;
        my $name = $tmpHash{'sensor'} . "_${color_type_camel}_${functionality}(" . $red_wave . "," . $green_wave . "," . $blue_wave . ")_" . $mode;
        my $filename = $basename . ".rgb";

        my $red_expr = &create_atan_expression($red_band, $gain, $offset);
        my $green_expr = &create_atan_expression($green_band, $gain, $offset);
        my $blue_expr = &create_atan_expression($blue_band, $gain, $offset);
        
        my $red_full_expr = &create_expression($red_band, $green_band, $blue_band, $red_expr );
        my $green_full_expr = &create_expression($red_band, $green_band, $blue_band, $green_expr);
        my $blue_full_expr = &create_expression($red_band, $green_band, $blue_band, $blue_expr);
        
        my $contents = "# RGB-Image Configuration Profile\n";
        $contents .= "# $name\n";
        $contents .= "#\n";
        $contents .= "# A ${color_type} RGB configuration profile which uses a ${red_source}, ${green_source} and ${blue_source} band for the\n";
        $contents .= "# respective RGB color model channels\n";
        $contents .= "#\n";
        $contents .= "name=$name\n";
        $contents .= "red=$red_full_expr\n";
        $contents .= "green=$green_full_expr\n";
        $contents .= "blue=$blue_full_expr\n";
        
#        print "$contents\n";
        
        &write_file($filename, $contents);
    }
}



sub create_log_file
{
    my $tmpHash_ref = shift;
    my $colorArray_ref = shift;
    my $true_color = shift;
    my $functionality = shift;
    my $product = shift;


    my %tmpHash = %{$tmpHash_ref};
    my @colorArray = @{$colorArray_ref};
    my $red_source = $colorArray[0];
    my $green_source = $colorArray[1];
    my $blue_source = $colorArray[2];

    my $red_wave = $tmpHash{$red_source};
    my $green_wave = $tmpHash{$green_source};
    my $blue_wave = $tmpHash{$blue_source};

    if (defined $red_wave && defined $green_wave && defined $blue_wave) {
        my $color_type_camel = ($true_color == 1) ? "TrueColor" : "FalseColor";
        my $color_type = ($true_color == 1) ? "true color" : "false color";

        my $min = '0.01';
        my $max = '1.0';
        my $mask = 'LAND';
        my $mode = "Log";

        my $red_band = $product . "_" . $red_wave;
        my $green_band = $product . "_" . $green_wave;
        my $blue_band = $product . "_" . $blue_wave;

        my $basename = $tmpHash{'sensor'} . "_${color_type_camel}_${functionality}" . $red_wave . "_" . $green_wave . "_" . $blue_wave . "_" . $mode;
        my $name = $tmpHash{'sensor'} . "_${color_type_camel}_${functionality}(" . $red_wave . "," . $green_wave . "," . $blue_wave . ")_" . $mode;
        my $filename = $basename . ".rgb";

        my $red_expr = &create_log_expression($red_band, $min, $max);
        my $green_expr = &create_log_expression($green_band, $min, $max);
        my $blue_expr = &create_log_expression($blue_band, $min, $max);

        my $red_full_expr = &create_expression($red_band, $green_band, $blue_band, $red_expr );
        my $green_full_expr = &create_expression($red_band, $green_band, $blue_band, $green_expr);
        my $blue_full_expr = &create_expression($red_band, $green_band, $blue_band, $blue_expr);

        my $contents = "# RGB-Image Configuration Profile\n";
        $contents .= "# $name\n";
        $contents .= "#\n";
        $contents .= "# A ${color_type} RGB configuration profile which uses a ${red_source}, ${green_source} and ${blue_source} band for the\n";
        $contents .= "# respective RGB color model channels\n";
        $contents .= "#\n";
        $contents .= "name=$name\n";
        $contents .= "red=$red_full_expr\n";
        $contents .= "green=$green_full_expr\n";
        $contents .= "blue=$blue_full_expr\n";

#        print "$contents\n";

        &write_file($filename, $contents);
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

