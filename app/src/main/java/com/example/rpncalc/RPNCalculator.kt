package com.example.rpncalc

import kotlin.collections.ArrayDeque
import kotlin.math.pow
import kotlin.math.sqrt

class RPNCalculator {
    enum class OperationStatus{
        SUCCESS,
        NOT_ENOUGH_ARGUMENTS,
        DIVISION_BY_ZERO,
        SQRT_OF_NEGATIVE_NUMBER,
        UNDEFINED_ERROR
    }

    private var stack : ArrayDeque<Double> = ArrayDeque(0)
    private var history : RPNCalculatorHistory = RPNCalculatorHistory()

    public fun getHistorySizePast() : Int{
        return history.getSizePast()
    }

    public fun getHistorySizeNext() : Int{
        return history.getSizeNext()
    }

    public fun clearStack(){
        history.push(RPNCalculatorHistory.RPNCalcualatorAction(stack.toCollection(ArrayList()), arrayListOf()))
        stack.clear()
    }

    public fun undo(){
        val action = history.undo()
        action.added.forEach{
            pop()
        }
        action.removed.forEach{
            push(it)
        }
    }

    public fun redo(){
        val action = history.redo()
        action.removed.forEach{
            pop()
        }
        action.added.forEach{
            push(it)
        }
    }

    public fun getStackElem(index : Int) : Double{
        if(index > stack.size - 1){
            throw NoSuchElementException("There is no item with index $index");
        }
        return stack[stack.size - index - 1]
    }

    public fun getStackSize() : Int{
        return stack.size;
    }

    public fun insertValue(value : Double){
        push(value)
        history.push(RPNCalculatorHistory.RPNCalcualatorAction(arrayListOf(), arrayListOf(value)))
    }

    private fun push(value : Double){
        stack.addLast(value)
    }

    public fun dropValue() : OperationStatus{
        if(stack.isEmpty())
            return OperationStatus.NOT_ENOUGH_ARGUMENTS
        val removed = pop()

        history.push(RPNCalculatorHistory.RPNCalcualatorAction(arrayListOf(removed), arrayListOf()))

        return OperationStatus.SUCCESS
    }

    private fun pop() : Double{
        return stack.removeLast()
    }

    public fun duplicateLast() : OperationStatus{
        try{
            val element = getStackElem(0)
            insertValue(element)
        }catch(e : NoSuchElementException){
            return OperationStatus.NOT_ENOUGH_ARGUMENTS
        }
        return OperationStatus.SUCCESS
    }

    public fun swapLastTwoElements() : OperationStatus{
        if(stack.size < 2){
            return OperationStatus.NOT_ENOUGH_ARGUMENTS
        }
        val elem1 = pop()
        val elem2 = pop()
        push(elem1)
        push(elem2)

        history.push(RPNCalculatorHistory.RPNCalcualatorAction(arrayListOf(elem2, elem1), arrayListOf(elem1, elem2)))

        return OperationStatus.SUCCESS
    }

    public fun changeSign() : OperationStatus{
        if(stack.isEmpty()){
            return OperationStatus.NOT_ENOUGH_ARGUMENTS
        }
        val oldValue = pop()
        val newValue = -oldValue
        push(newValue)

        history.push(RPNCalculatorHistory.RPNCalcualatorAction(arrayListOf(oldValue), arrayListOf(newValue)))

        return OperationStatus.SUCCESS
    }

    public fun add() : OperationStatus{
        if(stack.size < 2){
            return OperationStatus.NOT_ENOUGH_ARGUMENTS
        }
        val a = pop()
        val b = pop()
        val result = a + b
        push(result)

        history.push(RPNCalculatorHistory.RPNCalcualatorAction(arrayListOf(b, a), arrayListOf(result)))

        return OperationStatus.SUCCESS
    }

    public fun subtract() : OperationStatus{
        if(stack.size < 2){
            return OperationStatus.NOT_ENOUGH_ARGUMENTS
        }
        val subtrahend : Double = pop()
        val minuend : Double = pop()
        val result = minuend - subtrahend;
        push(result)

        history.push(RPNCalculatorHistory.RPNCalcualatorAction(arrayListOf(minuend, subtrahend), arrayListOf(result)))

        return OperationStatus.SUCCESS
    }

    public fun multiply() : OperationStatus{
        if(stack.size < 2){
            return OperationStatus.NOT_ENOUGH_ARGUMENTS
        }
        val a = pop()
        val b = pop()
        val result = a * b
        push(result)

        history.push(RPNCalculatorHistory.RPNCalcualatorAction(arrayListOf(b, a), arrayListOf(result)))

        return OperationStatus.SUCCESS
    }

    public fun divide() : OperationStatus{
        if(stack.size < 2){
            return OperationStatus.NOT_ENOUGH_ARGUMENTS
        }
        val divider = pop()
        if(divider == 0.0){
            push(divider)
            return OperationStatus.DIVISION_BY_ZERO
        }
        val dividend = pop()
        val result = dividend / divider
        push(result)

        history.push(RPNCalculatorHistory.RPNCalcualatorAction(arrayListOf(dividend, divider), arrayListOf(result)))

        return OperationStatus.SUCCESS
    }

    public fun power() : OperationStatus{
        if(stack.size < 2){
            return OperationStatus.NOT_ENOUGH_ARGUMENTS
        }
        val exponent = pop()
        val base = pop()
        val result = base.pow(exponent)
        push(result)

        history.push(RPNCalculatorHistory.RPNCalcualatorAction(arrayListOf(base, exponent), arrayListOf(result)))

        return OperationStatus.SUCCESS
    }

    public fun squareRoot() : OperationStatus{
        if(stack.size < 1){
            return OperationStatus.NOT_ENOUGH_ARGUMENTS
        }
        val base = pop()
        if(base < 0.0){
            push(base)
            return OperationStatus.SQRT_OF_NEGATIVE_NUMBER
        }
        val result = sqrt(base)
        push(result)

        history.push(RPNCalculatorHistory.RPNCalcualatorAction(arrayListOf(base), arrayListOf(result)))

        return OperationStatus.SUCCESS
    }
}