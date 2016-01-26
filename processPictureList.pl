#!/usr/bin/perl

use strict;
use warnings;
use autodie;

open my $file, "<", "picture_list.txt";
my @lines = <$file>;
close $file;

my $file_name;
for my $line (@lines) 
{
    if ($line =~ /Picture/)
    {
	$file_name = $&.$';#';
	if ($file_name =~ /png/)
	{
	    if ($file_name =~ /present/) 
	    {
		print "!";
	    }
	    print $file_name;
	}
    }
}
