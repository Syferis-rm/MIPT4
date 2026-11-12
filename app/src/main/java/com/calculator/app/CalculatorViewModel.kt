package com.calculator.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for the Calculator, managing UI state and delegating
 * calculations to the CalculatorEngine.
 *
 * The calculator builds an expression string from user input and evaluates
 * it when '=' is pressed, using correct mathematical operator precedence.
 */
class CalculatorViewModel : ViewModel() {

    private val engine = CalculatorEngine()

    // --- Display state ---
    private val _displayText = MutableLiveData("0")
    val displayText: LiveData<String> = _displayText

    private val _expressionText = MutableLiveData("")
    val expressionText: LiveData<String> = _expressionText

    private val _memoryActive = MutableLiveData(false)
    val memoryActive: LiveData<Boolean> = _memoryActive

    private val _historyList = MutableLiveData<List<HistoryEntry>>(emptyList())
    val historyList: LiveData<List<HistoryEntry>> = _historyList

    // --- Internal state ---
    private var currentInput = "0"
    private var fullExpression = ""    // The evaluable expression being built
    private var displayExpression = "" // What's shown in the expression line
    private var justEvaluated = false
    private var lastResult = 0.0
    private var memoryValue = 0.0
    private var hasStartedInput = false // Whether user has typed digits since last operator/clear

    // =================== DIGIT & DECIMAL ===================

    fun onDigitClick(digit: String) {
        if (justEvaluated) {
            // Start fresh expression after evaluation
            clearAll()
        }
        if (!hasStartedInput && currentInput == "0") {
            if (digit == "0") return // Don't add leading zeros
            currentInput = digit
        } else {
            if (currentInput == "0" && !currentInput.contains(".")) {
                currentInput = digit
            } else {
                currentInput += digit
            }
        }
        hasStartedInput = true
        _displayText.value = currentInput
    }

    fun onDecimalClick() {
        if (justEvaluated) {
            clearAll()
        }
        if (!currentInput.contains(".")) {
            if (!hasStartedInput) {
                currentInput = "0"
            }
            currentInput += "."
            hasStartedInput = true
            _displayText.value = currentInput
        }
    }

    // =================== OPERATORS ===================

    fun onOperatorClick(operator: String) {
        val op = operator // Should be one of: +, −, ×, ÷

        if (justEvaluated) {
            // Continue with the result
            val resultStr = CalculatorEngine.formatResult(lastResult)
            fullExpression = "$resultStr $op "
            displayExpression = "$resultStr $op "
            justEvaluated = false
            currentInput = "0"
            hasStartedInput = false
            _expressionText.value = displayExpression
            return
        }

        if (hasStartedInput) {
            fullExpression += "$currentInput $op "
            displayExpression += "$currentInput $op "
        } else if (fullExpression.isNotEmpty()) {
            // User changes operator before typing new operand (replace previous operator)
            val operatorRegex = Regex("\\s+[+\\-×÷−*\\/]\\s+$")
            if (operatorRegex.containsMatchIn(fullExpression)) {
                fullExpression = fullExpression.replace(operatorRegex, " $op ")
                displayExpression = displayExpression.replace(operatorRegex, " $op ")
            } else {
                fullExpression += " $op "
                displayExpression += " $op "
            }
        } else {
            // Starting expression from initial zero or displayed value
            fullExpression = "$currentInput $op "
            displayExpression = "$currentInput $op "
        }

        _expressionText.value = displayExpression
        currentInput = "0"
        hasStartedInput = false
    }

    // =================== EQUALS ===================

    fun onEqualsClick() {
        if (justEvaluated) return // Don't re-evaluate

        var exprToEval: String
        var exprToShow: String

        if (hasStartedInput) {
            exprToEval = fullExpression + currentInput
            exprToShow = displayExpression + currentInput
        } else if (fullExpression.isNotEmpty()) {
            exprToEval = fullExpression + currentInput
            exprToShow = displayExpression + currentInput
        } else {
            exprToEval = currentInput
            exprToShow = currentInput
        }

        if (exprToEval.isBlank()) return

        try {
            lastResult = engine.evaluate(exprToEval)
            val resultStr = CalculatorEngine.formatResult(lastResult)

            _expressionText.value = "$exprToShow ="
            _displayText.value = resultStr

            // Add to history
            val entry = HistoryEntry(
                expression = "$exprToShow =",
                result = resultStr
            )
            HistoryRepository.addEntry(entry)
            _historyList.value = HistoryRepository.getHistory()

            justEvaluated = true
            hasStartedInput = false
            currentInput = resultStr
        } catch (e: ArithmeticException) {
            _displayText.value = "Cannot divide by zero"
            justEvaluated = true
            hasStartedInput = false
        } catch (e: CalculatorEngine.ParseException) {
            _displayText.value = "Error"
            justEvaluated = true
            hasStartedInput = false
        } catch (e: Exception) {
            _displayText.value = "Error"
            justEvaluated = true
            hasStartedInput = false
        }
    }

    // =================== CLEAR FUNCTIONS ===================

    fun onClearClick() {
        clearAll()
    }

    fun onClearEntryClick() {
        currentInput = "0"
        hasStartedInput = false
        _displayText.value = "0"
    }

    fun onBackspaceClick() {
        if (justEvaluated) return
        if (currentInput.length > 1) {
            currentInput = currentInput.dropLast(1)
            if (currentInput == "-") {
                currentInput = "0"
                hasStartedInput = false
            }
        } else {
            currentInput = "0"
            hasStartedInput = false
        }
        _displayText.value = currentInput
    }

    // =================== SPECIAL OPERATIONS ===================

    fun onPercentClick() {
        try {
            val value = getCurrentValue()
            val result = if (fullExpression.isNotBlank()) {
                val firstOperand = extractFirstOperand()
                if (firstOperand != null) {
                    firstOperand * value / 100.0
                } else {
                    value / 100.0
                }
            } else {
                value / 100.0
            }
            val resultStr = CalculatorEngine.formatResult(result)
            currentInput = resultStr
            hasStartedInput = true
            _displayText.value = resultStr
        } catch (_: Exception) {
            _displayText.value = "Error"
        }
    }

    fun onSquareClick() {
        try {
            val value = getCurrentValue()
            val result = value * value
            applySpecialResult(result, "sqr($currentInput)")
        } catch (_: Exception) {
            _displayText.value = "Error"
        }
    }

    fun onSquareRootClick() {
        try {
            val value = getCurrentValue()
            if (value < 0) {
                _displayText.value = "Invalid input"
                return
            }
            val result = kotlin.math.sqrt(value)
            applySpecialResult(result, "√($currentInput)")
        } catch (_: Exception) {
            _displayText.value = "Error"
        }
    }

    fun onFractionClick() {
        try {
            val value = getCurrentValue()
            if (value == 0.0) {
                _displayText.value = "Cannot divide by zero"
                return
            }
            val result = 1.0 / value
            applySpecialResult(result, "1/($currentInput)")
        } catch (_: Exception) {
            _displayText.value = "Error"
        }
    }

    fun onNegateClick() {
        if (justEvaluated) {
            if (lastResult == 0.0) return
            lastResult = -lastResult
            val resultStr = CalculatorEngine.formatResult(lastResult)
            currentInput = resultStr
            displayExpression = "negate($resultStr)"
            _expressionText.value = displayExpression
            _displayText.value = resultStr
            return
        }
        if (currentInput == "0") return
        currentInput = if (currentInput.startsWith("-")) {
            currentInput.substring(1)
        } else {
            "-$currentInput"
        }
        hasStartedInput = true
        _displayText.value = currentInput
    }

    // =================== MEMORY OPERATIONS ===================

    fun onMemoryStore() {
        val value = getCurrentValue()
        memoryValue = value
        _memoryActive.value = true
    }

    fun onMemoryRecall() {
        if (_memoryActive.value != true) return
        currentInput = CalculatorEngine.formatResult(memoryValue)
        hasStartedInput = true
        _displayText.value = currentInput
        if (justEvaluated) {
            fullExpression = ""
            displayExpression = ""
            _expressionText.value = ""
            justEvaluated = false
        }
    }

    fun onMemoryAdd() {
        val value = getCurrentValue()
        memoryValue += value
        _memoryActive.value = true
    }

    fun onMemorySubtract() {
        val value = getCurrentValue()
        memoryValue -= value
        _memoryActive.value = true
    }

    fun onMemoryClear() {
        memoryValue = 0.0
        _memoryActive.value = false
    }

    // =================== HISTORY ===================

    fun clearHistory() {
        HistoryRepository.clear()
        _historyList.value = emptyList()
    }

    fun refreshHistory() {
        _historyList.value = HistoryRepository.getHistory()
    }

    // =================== PRIVATE HELPERS ===================

    private fun clearAll() {
        currentInput = "0"
        fullExpression = ""
        displayExpression = ""
        justEvaluated = false
        hasStartedInput = false
        lastResult = 0.0
        _displayText.value = "0"
        _expressionText.value = ""
    }

    private fun getCurrentValue(): Double {
        return if (justEvaluated) {
            lastResult
        } else {
            currentInput.toDoubleOrNull() ?: 0.0
        }
    }

    private fun applySpecialResult(result: Double, displayNotation: String) {
        val resultStr = CalculatorEngine.formatResult(result)
        if (justEvaluated) {
            displayExpression = displayNotation
            _expressionText.value = displayExpression
            justEvaluated = false
        }
        currentInput = resultStr
        hasStartedInput = true
        _displayText.value = resultStr
    }

    private fun extractFirstOperand(): Double? {
        val trimmed = fullExpression.trim()
        if (trimmed.isEmpty()) return null
        // Extract the first number from the expression
        val parts = trimmed.split(Regex("\\s+[+\\-×÷−]\\s+"))
        return parts.firstOrNull()?.toDoubleOrNull()
    }
}
