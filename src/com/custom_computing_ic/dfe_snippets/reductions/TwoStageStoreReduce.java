package com.custom_computing_ic.dfe_snippets.reductions;

/***
    This implements a container and summator of 2 floating point numbers.
      * These numbers are pushed to container sequentially (at most one value per cycle).
      * It is designed to wait as many cycles as necessary for data arrival.
      * Adding second value triggers summation of stored values and resetting the storage.
      * Flush signal outputs whatever data is available and resets the storage.

    We do not use Maxeler SM API since we need to do floating point addition
    (latency).

    Use case: implement accumulation-reducer of arbitrary number of values
    (not necessarily a power of 2) with logarithmic number of adders. Each adder
    serves its level of a binary add-reduction tree + one needs to save past outputs
    of previous level adders. It gets tricky when the number of values is not a power
    of two, where a state machine becomes necessary. A pipeline of such state
    machines solves the problem: for example, given an input problem (1+2)+(3),
    first level SM sequentially computes (1+2) and then (3), while second level SM
    computes (1+2) + (3). Trickery is in that first level SM needs to output (3)
    earlier, waiting for no 4th input.

    We have state machine with 3 binary variables determining the state
       => 8 combinations.

    Output valid, if:
      * one value stored and new value coming --- whether flush is on or off (2 combs)
       (if flush on, new value needs to sum with stored value and be pushed
        away, resetting the storage; result is the same as with flush off)
      * one value stored, no new value is coming and flush signal on
      * no value stored, new value is coming and flush signal on
    Output is invalid, if:
      * one value stored, no new value is coming, flush signal off
      * no value stored, new value is coming and flush signal off
      * no value stored, no new value coming, flush signal on/off (2 combs)

    When to save input value:
      * no saved value, new data comes, no flush
    When to keep current state:
      * one saved value, no new data comes, no flush
    When to initialise to 0:
      * no saved value, no data, flush on/off
      * no saved value, new data, flush
      * one saved value, no new data, flush
      * one saved value, new data, flush on/off

*/
import com.maxeler.maxcompiler.v2.kernelcompiler.KernelLib;
import com.maxeler.maxcompiler.v2.kernelcompiler.types.base.DFEType;
import com.maxeler.maxcompiler.v2.kernelcompiler.types.base.DFEVar;
import com.maxeler.maxcompiler.v2.kernelcompiler.stdlib.core.Count.*;



public class TwoStageStoreReduce extends KernelLib
{
    private static final int counterWidth = 32;

    private DFEVar output;
    private DFEVar outputValid;

    public  DFEVar getOutput()       {   return output;        }
    public  DFEVar isOutputValid()   {   return outputValid;   }


    /***
        @ param     input           new input term subject to be add-reduced
        @ param     newValueEnable  boolean: whether new input to be ignored
        @ param     flush           boolean: forcing to complete reduction and output sum of previously fed values (if any)
        @ param     dataType        underlying data type of add-reduced data
    */
    public TwoStageStoreReduce(KernelLib owner, DFEVar newValueEnable, DFEVar flush,
                                                DFEVar input, DFEType dataType)
    {
        super(owner);

        DFEVar cycleCounter = control.count.simpleCounter(counterWidth);

        DFEVar stored = dfeBool().newInstance(this);
        DFEVar storedValue = dataType.newInstance(this);

        // initially we have nothing stored
        DFEVar storedBefore = control.count.pulse(1, false) & stream.offset(stored,-1);
        DFEVar storedValueBefore = cycleCounter.eq(0)? 0 : stream.offset(storedValue,-1);

        DFEVar flushAtLeastOneValue =  flush & (storedBefore | newValueEnable);
        DFEVar addTwoValues         = ~flush & storedBefore & newValueEnable;
        DFEVar keepOneValue         = ~flush & storedBefore & ~newValueEnable;
        DFEVar saveInputValue       = ~flush & ~storedBefore & newValueEnable;

        // value stored before == 0 always when it makes no sense to store it
        output      = storedValueBefore + input;
        outputValid = flushAtLeastOneValue | addTwoValues;

        optimization.pushPipeliningFactor(0.0);
            stored      <== (flush)? 0 :
                                     (newValueEnable)? ~storedBefore : storedBefore;

            storedValue <== (saveInputValue)? input :
                                              (keepOneValue)? storedValueBefore: 0;
        optimization.popPipeliningFactor();
    }
}

