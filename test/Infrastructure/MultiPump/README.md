
#Multi-Pumping Applications

This is sort of a application gallery of multi-pumping in MaxJ.

Currently it has these designs:

1. [REMOVED]**DotProduct**: A simple application do x^2 * y^2 on two input streams `x` and `y`. Will multi-pump the DSP multipliers.
2. **WindowSummation**: Do a summation on a window of ROM read value from input stream.

Here're detailed description:

## DotProduct

Please take a look at the slides on 29-08-15.

## WindowSummation

This design has 2 parts: 

1. Read value from ROM by using index.
2. Do sum on the read value in a W width window.

The first part could be multi-pumped.

Here're some data for this application:

![](https://raw.githubusercontent.com/imperial-summer-research/ImperialSummerReports/master/assets/images/result%20for%20window%20summation.png)
