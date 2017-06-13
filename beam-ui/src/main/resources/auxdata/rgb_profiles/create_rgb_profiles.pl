#!/usr/bin/perl -w

use strict;
use Math::Trig;

# A script to generate RGB profiles based on mission wavelengths
# Written by Daniel Knowles


my %MISSION_RGB_BAND_HASH = (
                 'MODIS' => {
                               'blue' => '469',
                               'green' => '555',
                               'red' => '645',
                               'nir' => '859',
                               'swir1' => '1240',
                               'swir2' => '2130'
                             },
                 'OLI' => {
                               'blue' => '482',
                               'green' => '561',
                               'red' => '655',
                               'nir' => '865',
                               'swir1' => '1609',
                               'swir2' => '2201'
                             },
                 'MERIS' => {
                               'blue' => '490',
                               'green' => '560',
                               'red' => '665',
                               'nir' => '865',
                               'swir1' => undef(),
                               'swir2' => undef()
                             },
                 'VIIRS' => {
                               'blue' => '486',
                               'green' => '551',
                               'red' => '671',
                               'nir' => '862',
                               'swir1' => '1238',
                               'swir2' => '2257'
                             },
                 'GOCI' => {
                               'blue' => '490',
                               'green' => '555',
                               'red' => '660',
                               'nir' => '865',
                               'swir1' => undef(),
                               'swir2' => undef()
                             },
                 'OLCI' => {
                               'blue' => '490',
                               'green' => '560',
                               'red' => '665',
                               'nir' => '865',
                               'swir1' => undef(),
                               'swir2' => undef()
                             },
                 'SEAWIFS' => {
                               'blue' => '490',
                               'green' => '555',
                               'red' => '670',
                               'nir' => '865',
                               'swir1' => undef(),
                               'swir2' => undef()
                             }
                 );



my @trueColorArray = ("red", "green", "blue");
my @falseColorSnowCloudArray = ("blue", "swir1", "swir2");
my @falseColorVegetationArray = ("nir", "red", "green");

my $gain = 10.0;
my $offset = 0.015;
my $product = "rhos";


foreach my $key (keys %MISSION_RGB_BAND_HASH)
{
    my %tmpHash = %{$MISSION_RGB_BAND_HASH{$key}};

#    &create_atan_file(\%tmpHash, \@trueColorArray, 1, "", $gain, $offset, $product);
    &create_atan_file(\%tmpHash, \@trueColorArray, 1, "", 10, 0.015, $product);
    &create_atan_file(\%tmpHash, \@trueColorArray, 1, "", 15, 0.015, $product);
    &create_atan_file(\%tmpHash, \@trueColorArray, 1, "", 20, 0.015, $product);
    &create_atan_file(\%tmpHash, \@trueColorArray, 1, "", 25, 0.015, $product);
    &create_atan_file(\%tmpHash, \@trueColorArray, 1, "", 10, 0.05, $product);
    &create_atan_file(\%tmpHash, \@trueColorArray, 1, "", 15, 0.05, $product);
    &create_atan_file(\%tmpHash, \@trueColorArray, 1, "", 20, 0.05, $product);
    &create_atan_file(\%tmpHash, \@trueColorArray, 1, "", 25, 0.05, $product);

    &create_hybrid_file(\%tmpHash, \@trueColorArray, 1, "");
    &create_log_file(\%tmpHash, \@trueColorArray, 1, "");
    &create_log_file(\%tmpHash, \@falseColorSnowCloudArray, 0, "SnowCloud_");
    &create_log_file(\%tmpHash, \@falseColorVegetationArray, 0, "Vegetation_");
}





sub create_hybrid_file
{
    my $tmpHash_ref = shift;
    my $colorArray_ref = shift;
    my $true_color = shift;
    my $functionality = shift;

    my %tmpHash = %{$tmpHash_ref};
    my @colorArray = @{$colorArray_ref};
    my $red_source = $colorArray[0];
    my $green_source = $colorArray[1];
    my $blue_source = $colorArray[2];

    my $red_wave = $tmpHash{$red_source};
    my $green_wave = $tmpHash{$green_source};
    my $blue_wave = $tmpHash{$blue_source};

    if (defined $red_wave && defined $green_wave && defined $blue_wave) {
        my $gain = 10.0;
        my $offset = 0.015;
        my $product = "rhos";
        my $min = '0.01';
        my $max = '1.0';
        my $mask = 'LAND';
        my $mode = "Hybrid";

        my $color_type_camel = ($true_color == 1) ? "TrueColor" : "FalseColor";
        my $color_type = ($true_color == 1) ? "true color" : "false color";
       
        my $red_band = $product . "_" . $red_wave;
        my $green_band = $product . "_" . $green_wave;
        my $blue_band = $product . "_" . $blue_wave;

        my $basename = "${color_type_camel}_${functionality}" . $red_wave . "_" . $green_wave . "_" . $blue_wave . "_" . $mode;
        my $name = "${color_type_camel}_${functionality}(" . $red_wave . "," . $green_wave . "," . $blue_wave . ")_" . $mode;
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
        $contents .= "# ${color_type_camel}_(Red,Green,Blue)_${mode}\n";
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
        my $mode = "Arctan_${gain}_${offset}";

        my $color_type_camel = ($true_color == 1) ? "TrueColor" : "FalseColor";
        my $color_type = ($true_color == 1) ? "true color" : "false color";
       
        my $red_band = $product . "_" . $red_wave;
        my $green_band = $product . "_" . $green_wave;
        my $blue_band = $product . "_" . $blue_wave;

        my $basename = "${color_type_camel}_${functionality}" . $red_wave . "_" . $green_wave . "_" . $blue_wave . "_" . $mode;
        my $name = "${color_type_camel}_${functionality}(" . $red_wave . "," . $green_wave . "," . $blue_wave . ")_" . $mode;
        my $filename = $basename . ".rgb";

        my $red_expr = &create_atan_expression($red_band, $gain, $offset);
        my $green_expr = &create_atan_expression($green_band, $gain, $offset);
        my $blue_expr = &create_atan_expression($blue_band, $gain, $offset);
        
        my $red_full_expr = &create_expression($red_band, $green_band, $blue_band, $red_expr );
        my $green_full_expr = &create_expression($red_band, $green_band, $blue_band, $green_expr);
        my $blue_full_expr = &create_expression($red_band, $green_band, $blue_band, $blue_expr);
        
        my $contents = "# RGB-Image Configuration Profile\n";
        $contents .= "# ${color_type_camel}_(Red,Green,Blue)_${mode}\n";
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
        my $product = "rhos";
        my $mode = "Log";

        my $red_band = $product . "_" . $red_wave;
        my $green_band = $product . "_" . $green_wave;
        my $blue_band = $product . "_" . $blue_wave;

        my $basename = "${color_type_camel}_${functionality}" . $red_wave . "_" . $green_wave . "_" . $blue_wave . "_" . $mode;
        my $name = "${color_type_camel}_${functionality}(" . $red_wave . "," . $green_wave . "," . $blue_wave . ")_" . $mode;
        my $filename = $basename . ".rgb";

        my $red_expr = &create_log_expression($red_band, $min, $max);
        my $green_expr = &create_log_expression($green_band, $min, $max);
        my $blue_expr = &create_log_expression($blue_band, $min, $max);

        my $red_full_expr = &create_expression($red_band, $green_band, $blue_band, $red_expr );
        my $green_full_expr = &create_expression($red_band, $green_band, $blue_band, $green_expr);
        my $blue_full_expr = &create_expression($red_band, $green_band, $blue_band, $blue_expr);

        my $contents = "# RGB-Image Configuration Profile\n";
        $contents .= "# ${color_type_camel}_(Red,Green,Blue)_${mode}\n";
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

