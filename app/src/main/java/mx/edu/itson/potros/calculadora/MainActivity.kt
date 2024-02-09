package mx.edu.itson.potros.calculadora

import android.os.Bundle
import java.util.Stack
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly

class MainActivity : AppCompatActivity() {
    private lateinit var operationTextView: TextView
    private lateinit var resultTextView: TextView

    private var currentInput = StringBuilder()
    private var currentOperation = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        operationTextView = findViewById(R.id.txtOperacion)
        resultTextView = findViewById(R.id.txtResultado)

        val numberButtons = listOf<Button>(
            findViewById(R.id.btn0), findViewById(R.id.btn1),
            findViewById(R.id.btn2), findViewById(R.id.btn3),
            findViewById(R.id.btn4), findViewById(R.id.btn5),
            findViewById(R.id.btn6), findViewById(R.id.btn7),
            findViewById(R.id.btn8), findViewById(R.id.btn9)
        )

        val operatorButtons = listOf<Button>(
            findViewById(R.id.btnMas), findViewById(R.id.btnMenos),
            findViewById(R.id.btnMultiplicacion), findViewById(R.id.btnDivision)
        )

        val equalsButton = findViewById<Button>(R.id.btnRESULT)
        val clearButton = findViewById<Button>(R.id.btnBORRAR)

        numberButtons.forEach { button ->
            button.setOnClickListener { onNumberButtonClick(button) }
        }

        operatorButtons.forEach { button ->
            button.setOnClickListener { onOperatorButtonClick(button) }
        }

        equalsButton.setOnClickListener { onEqualsButtonClick() }
        clearButton.setOnClickListener { onClearButtonClick() }
    }

    private fun onNumberButtonClick(button: Button) {
        currentInput.append(button.text)
        currentOperation.append(button.text)
        updateOperationText()
    }

    private fun onOperatorButtonClick(button: Button) {
        currentInput.setLength(0)
        currentOperation.append(" ${button.text} ")
        updateOperationText()
    }

    private fun onEqualsButtonClick() {
        try {
            val result = evaluateExpression()
            resultTextView.text = result.toString()
            currentInput.setLength(0)
            currentOperation.setLength(0)
        } catch (e: Exception) {
            resultTextView.text = "Error"
        }
    }

    private fun onClearButtonClick() {
        currentInput.setLength(0)
        currentOperation.setLength(0)
        updateOperationText()
        resultTextView.text = ""
    }

    private fun evaluateExpression(): Double {
        val expression = currentOperation.toString()
        return eval(expression)
    }

    private fun updateOperationText() {
        operationTextView.text = currentOperation.toString()
    }

    private fun eval(expression: String): Double {
        val postfixExpression = infixToPostfix(expression)
        return evaluatePostfix(postfixExpression)
    }

    private fun infixToPostfix(infix: String): String {
        val output = StringBuilder()
        val stack = Stack<Char>()

        for (token in infix) {
            when {
                token.isDigit() || token == '.' -> output.append(token)
                isOperator(token) -> {
                    output.append(' ')
                    while (stack.isNotEmpty() && getPrecedence(stack.peek()) >= getPrecedence(token)) {
                        output.append(stack.pop())
                        output.append(' ')
                    }
                    stack.push(token)
                }
            }
        }

        while (stack.isNotEmpty()) {
            output.append(' ')
            output.append(stack.pop())
        }

        return output.toString().trim()
    }

    private fun evaluatePostfix(postfix: String): Double {
        val stack = Stack<Double>()

        for (token in postfix.split(' ')) {
            when {
                token.isDigitsOnly() -> stack.push(token.toDouble())
                isOperator(token[0]) -> {
                    val operand2 = stack.pop()
                    val operand1 = stack.pop()
                    stack.push(performOperation(operand1, operand2, token[0]))
                }
            }
        }

        return if (stack.isNotEmpty()) stack.pop() else throw IllegalArgumentException("Invalid expression")
    }

    private fun isNumeric(str: String): Boolean {
        return str.matches("-?\\d+(\\.\\d+)?".toRegex())
    }

    private fun isOperator(char: Char): Boolean {
        return char == '+' || char == '-' || char == '*' || char == '/'
    }

    private fun getPrecedence(operator: Char): Int {
        return when (operator) {
            '+', '-' -> 1
            '*', '/' -> 2
            else -> 0
        }
    }

    private fun performOperation(operand1: Double, operand2: Double, operator: Char): Double {
        return when (operator) {
            '+' -> operand1 + operand2
            '-' -> operand1 - operand2
            '*' -> operand1 * operand2
            '/' -> operand1 / operand2
            else -> throw IllegalArgumentException("Invalid operator")
        }
    }
}