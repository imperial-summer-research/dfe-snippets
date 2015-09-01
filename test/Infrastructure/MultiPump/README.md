
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

Experiment on WindowSummation with different W and C value (Stream Frequency=100MHz)                                    
|N          |C  |W  |Logics         |DSPs   |BRAMs      |Time(us)   |Freq. tick(MHz)|Freq. elem(MHz)    |Bandwidth(MB/s)|
------------------------------------------------------------------------------------------------------------------------
|67108864   |1  |4  |9524(3.20%)    |0      |60(2.82%)  |0.002775   |90.10          |360.40             |1441.61        |
|           |   |8  |12361(4.15%)   |0      |102(4.79%) |0.002713   |46.08          |368.65             |1474.61        |
|           |   |16 |17467(5.87%)   |0      |176(8.27%) |0.002686   |23.27          |372.29             |1489.15        |
|           |   |32 |28821(9.68%)   |0      |324(15.23%)|0.002660   |11.75          |375.93             |1503.72        |
|           |   |64 |49614(16.67%)  |0      |620(29.14%)|0.002659   |2.90           |376.12             |1504.50        |
|           |2  |4  |10163(3.41%)   |0      |45(2.11%)  |0.005275   |47.39          |189.58             |758.32         |
|           |   |8  |12656(4.25%)   |0      |68(3.20%)  |0.002716   |46.03          |368.23             |1472.90        |
|           |   |16 |17082(5.74%)   |0      |117(5.50%) |0.002677   |23.35          |373.55             |1494.22        |
|           |   |32 |28260(9.50%)   |0      |205(9.63%) |0.002668   |11.71          |374.87             |1499.47        |
|           |   |64 |FAILED TO MEET TIMING!                      

