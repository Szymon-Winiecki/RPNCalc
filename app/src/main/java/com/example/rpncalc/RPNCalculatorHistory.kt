package com.example.rpncalc

class RPNCalculatorHistory {

    class RPNCalcualatorAction{
        var removed : ArrayList<Double>
        var added : ArrayList<Double>

        constructor(){
            removed = ArrayList(0)
            added = ArrayList(0)
        }

        constructor(removed : ArrayList<Double>, added : ArrayList<Double>){
            this.removed = removed
            this.added = added
        }

    }

    private var maxSize = 256

    private var pastActions : ArrayDeque<RPNCalcualatorAction> = ArrayDeque(0)
    private var nextActions : ArrayDeque<RPNCalcualatorAction> = ArrayDeque(0)

    public fun setMaxSize(size : Int){
        maxSize = size
    }

    public fun getSizePast() : Int{
        return pastActions.size
    }

    public fun getSizeNext() : Int{
        return nextActions.size
    }

    public fun undo() : RPNCalcualatorAction{
        val action = pastActions.removeLastOrNull()
        if(action == null)
            return RPNCalcualatorAction()

        nextActions.addFirst(action)
        return action;
    }

    public fun redo() : RPNCalcualatorAction{
        val action = nextActions.removeFirstOrNull()
        if(action == null)
            return RPNCalcualatorAction()

        pastActions.addLast(action)
        return action;
    }

    public fun push(action : RPNCalcualatorAction){
        nextActions.clear()

        if(pastActions.size >= maxSize){
            pastActions.removeFirstOrNull()
        }
        pastActions.addLast(action)
    }
}