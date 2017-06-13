#
#
# TrueColor
# A true color RGB configuration profile which uses a red, green, and blue band.
# The transformation is log-based with min=0.01 and max=1.0
#
#
# TrueColor_Arctan
# A true color RGB configuration profile which uses a red, green, and blue band.
# The transformation is arctangent-based with the offset = 0.015 to approximate an average reflectance for the green
# band over clear oceanic water.  The gain coefficient = 10.0 was chosen to keep the land from saturating.  Note: these
# are not standardized values and the user may easily modify these coefficients to focus in on different ocean and/or
# land features.
#
#
# TrueColor_HighRange
# A true color RGB configuration profile which uses a red, green, and blue band.
# The transformation is log-based with min=0.01 and max=1.3.  The max of 1.3 is used instead of 1.0 to be able to include
# cloud tops.  The surface reflectance is designed to compute at Earth/Water surface and cloud tops can saturate.
#
#
# TrueColor_Hybrid
# A Land/Water hybrid which requires a land mask named LAND.
# For the LAND masked pixels: the transformation is log-based with min=0.01 and max=1.0 (same as TrueColor)
# For the not LAND pixels: the transformation is arctangent-based (same as TrueColor_Arctan)
#
#
# FalseColor_Vegetation
# A false color RGB configuration profile which uses a NIR, red, and green band.
# The transformation is log.
#
#
# MODIS_FalseColor_CloudIceSnow
# A false color RGB configuration profile which uses a blue, SWIR1, and SWIR2 band.
# The transformation is log.
#
# The following illustrates a selection of bands used by default in these configuration profile for various wavelength groupings
#
# MODIS:
# UV = 412 nm
# Blue = 469 nm
# Green = 555 nm
# Red = 645 nm
# NIR = 859 nm
# SWIR1 = 1240 nm   (# note: Aqua has problems with band 1640 nm so the 1240 nm will is used instead)
# SWIR2 = 2130 nm
# Note: These particular MODIS bands were chosen because they do not saturate over clouds.
#
#
# OLCI:
# UV = 412 nm
# Blue = 490 nm
# Green = 560 nm
# Red = 665 nm
# NIR = 865 nm
#
#
# VIIRS:
# UV = 410 nm
# Blue = 486 nm
# Green = 551 nm
# Red = 671 nm
# NIR = 862 nm
# SWIR1 = 1238 nm
# SWIR2 = 2257 nm
#
#
# GOCI:
# UV = 412 nm
# Blue = 490 nm
# Green = 555 nm
# Red = 660 nm
# NIR = 865 nm

