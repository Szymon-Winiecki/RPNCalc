package com.example.rpncalc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.rpncalc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var calc : RPNCalculator = RPNCalculator()
    private var input : StringBuilder = StringBuilder("")

    private lateinit var settings : Settings

    private lateinit var binding: ActivityMainBinding

    private val errorMessages = mapOf(
        RPNCalculator.OperationStatus.DIVISION_BY_ZERO to "nie można podzielić przez zero",
        RPNCalculator.OperationStatus.SQRT_OF_NEGATIVE_NUMBER to "nie można wyznaczyć pierwiastka z liczby ujemnej",
        RPNCalculator.OperationStatus.NOT_ENOUGH_ARGUMENTS to "za mało argumentów",
        RPNCalculator.OperationStatus.UNDEFINED_ERROR to "nie można wykonać tej operacji",
    ).withDefault {
        "nie można wykonać tej operacji"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Kalkulator ONP"

        settings = Settings(this)

        if(settings.darkTheme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setOnClickListeners()
        updateDisplay()
    }

    override fun onResume() {
        super.onResume()
        updateDisplay()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings_button -> {
                showSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSettings(){
        val settingsActivity = Intent(this, SettingsActivity::class.java)
        startActivity(settingsActivity)
    }

    private fun updateDisplay(){
        val visibleElementsCount : Int = if(input.isEmpty()) 4 else 3
        val decimals = settings.decimalPlaces
        val stack = Array<String>(visibleElementsCount, init= {_ : Int -> ""})

        for(i in 0 until visibleElementsCount){
            try {
                stack[i] = String.format("%.${decimals}f", calc.getStackElem(i))
            }catch (e : NoSuchElementException){
                stack[i] = ""
            }
        }

        binding.stackDisplay.text = "${if(input.isEmpty()) "4: ${stack[3]}\n" else ""}3: ${stack[2]}\n2: ${stack[1]}\n1: ${stack[0]}${if(input.isNotEmpty()) "\n-> ${input.toString()}" else ""} "
        binding.stackSize.text = "stack: ${calc.getStackSize().toString()}"
        binding.history.text = "<- ${calc.getHistorySizePast()} | ${calc.getHistorySizeNext()} ->"
    }

    private fun showShortFeedback(text : String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun setOnClickListeners(){
        val numBTNs = mapOf(
            binding.zero to '0',
            binding.one to '1',
            binding.two to '2',
            binding.three to '3',
            binding.four to '4',
            binding.five to '5',
            binding.six to '6',
            binding.seven to '7',
            binding.eight to '8',
            binding.nine to '9',
            binding.decimalSeparator to '.'
        )
        numBTNs.forEach{ btn ->
            btn.key.setOnClickListener(){
                type(btn.value)
            }
        }
        binding.sign.setOnClickListener(){
            changeSign()
        }

        binding.enter.setOnClickListener(){
            enter();
        }
        binding.delete.setOnClickListener(){
            delete();
        }
        binding.allClear.setOnClickListener(){
            allClear();
        }
        binding.swap.setOnClickListener(){
            swap();
        }
        binding.drop.setOnClickListener(){
            drop();
        }
        binding.undo.setOnClickListener(){
            undo()
        }
        binding.redo.setOnClickListener(){
            redo()
        }

        binding.addition.setOnClickListener(){
            add()
        }
        binding.subtraction.setOnClickListener(){
            subtract()
        }
        binding.multiplication.setOnClickListener(){
            multiply()
        }
        binding.division.setOnClickListener(){
            divide()
        }
        binding.power.setOnClickListener(){
            power()
        }
        binding.sqrt.setOnClickListener(){
            sqrt()
        }
    }

    private fun type(symbol : Char){
        input.append(symbol)
        try{
            input.toString().toDouble()
        }
        catch(e : NumberFormatException){
            input.deleteCharAt(input.length-1)
        }
        updateDisplay()
    }

    private fun changeSign(){
        if(input.isEmpty()){
            val status = calc.changeSign()
            if(status != RPNCalculator.OperationStatus.SUCCESS){
                showShortFeedback(errorMessages.getValue(status));
            }
        }
        else{
            changeInputSign()
        }
        updateDisplay()
    }

    private fun changeInputSign(){
        if(input.isEmpty()){
            input.append('-')
        }
        else if(input[0] == '-'){
            input.deleteCharAt(0)
        }
        else{
            input.insert(0,'-')
        }
    }

    private fun enter(){
        if(input.isNotEmpty()){
            try {
                calc.insertValue(input.toString().toDouble())
            }catch (e : NumberFormatException){
                return;
            }
        }
        else{
            val status = calc.duplicateLast()
            if(status != RPNCalculator.OperationStatus.SUCCESS){
                showShortFeedback(errorMessages.getValue(status));
            }
        }

        input.clear();
        updateDisplay();
    }

    private fun delete(){
        if(input.isNotEmpty()){
            input.deleteCharAt(input.length-1)
        }
        updateDisplay()
    }

    private fun allClear(){
        calc.clearStack()
        updateDisplay()
    }

    private fun swap(){
        val status = calc.swapLastTwoElements()
        if(status != RPNCalculator.OperationStatus.SUCCESS){
            showShortFeedback(errorMessages.getValue(status));
        }
        updateDisplay()
    }

    private fun drop(){
        val status = calc.dropValue()
        if(status != RPNCalculator.OperationStatus.SUCCESS){
            showShortFeedback(errorMessages.getValue(status));
        }
        updateDisplay()
    }

    private fun undo(){
        calc.undo()
        updateDisplay()
    }

    private fun redo(){
        calc.redo()
        updateDisplay()
    }

    private fun add(){
        val status = calc.add()
        if(status != RPNCalculator.OperationStatus.SUCCESS){
            showShortFeedback(errorMessages.getValue(status));
        }
        updateDisplay()
    }
    private fun subtract(){
        val status = calc.subtract()
        if(status != RPNCalculator.OperationStatus.SUCCESS){
            showShortFeedback(errorMessages.getValue(status));
        }
        updateDisplay()
    }
    private fun multiply(){
        val status = calc.multiply()
        if(status != RPNCalculator.OperationStatus.SUCCESS){
            showShortFeedback(errorMessages.getValue(status));
        }
        updateDisplay()
    }
    private fun divide(){
        val status = calc.divide()
        if(status != RPNCalculator.OperationStatus.SUCCESS){
            showShortFeedback(errorMessages.getValue(status));
        }
        updateDisplay()
    }
    private fun power(){
        val status = calc.power()
        if(status != RPNCalculator.OperationStatus.SUCCESS){
            showShortFeedback(errorMessages.getValue(status));
        }
        updateDisplay()
    }
    private fun sqrt(){
        val status = calc.squareRoot()
        if(status != RPNCalculator.OperationStatus.SUCCESS){
            showShortFeedback(errorMessages.getValue(status));
        }
        updateDisplay()
    }
}