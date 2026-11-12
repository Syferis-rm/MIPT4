package com.calculator.app

/**
 * Custom calculator engine implementing a recursive descent parser.
 * Evaluates mathematical expressions with correct operator precedence.
 * Built from scratch — no third-party expression evaluation libraries.
 *
 * Grammar:
 *   expression = term (('+' | '-') term)*
 *   term       = factor (('*' | '/') factor)*
 *   factor     = ['+' | '-'] (NUMBER | '(' expression ')')
 */
class CalculatorEngine {

    class ParseException(message: String) : Exception(message)

    /**
     * Evaluates a mathematical expression string and returns the result.
     * Supports: +, -, *, / (via display symbols ×, ÷, −) with correct precedence.
     * @throws ParseException if the expression is malformed
     * @throws ArithmeticException if division by zero occurs
     */
    fun evaluate(expression: String): Double {
        if (expression.isBlank()) throw ParseException("Empty expression")
        val tokens = tokenize(expression)
        if (tokens.isEmpty()) throw ParseException("No valid tokens")
        val parser = Parser(tokens)
        val result = parser.parseExpression()
        if (!parser.isAtEnd()) {
            throw ParseException("Unexpected token at position ${parser.position()}")
        }
        return result
    }

    private fun tokenize(expression: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        // Normalize display symbols to standard math operators and decimals
        val expr = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace(",", ".")

        while (i < expr.length) {
            when {
                expr[i].isWhitespace() -> i++
                expr[i].isDigit() || expr[i] == '.' -> {
                    val start = i
                    var hasDot = false
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        if (expr[i] == '.') {
                            if (hasDot) throw ParseException("Invalid number: multiple decimal points")
                            hasDot = true
                        }
                        i++
                    }
                    val numStr = expr.substring(start, i)
                    tokens.add(Token.Number(numStr.toDoubleOrNull()
                        ?: throw ParseException("Invalid number: $numStr")))
                }
                expr[i] == '+' -> { tokens.add(Token.Plus); i++ }
                expr[i] == '-' -> { tokens.add(Token.Minus); i++ }
                expr[i] == '*' -> { tokens.add(Token.Multiply); i++ }
                expr[i] == '/' -> { tokens.add(Token.Divide); i++ }
                expr[i] == '(' -> { tokens.add(Token.LParen); i++ }
                expr[i] == ')' -> { tokens.add(Token.RParen); i++ }
                else -> throw ParseException("Unknown character: '${expr[i]}'")
            }
        }
        return tokens
    }

    private sealed class Token {
        data class Number(val value: Double) : Token()
        data object Plus : Token()
        data object Minus : Token()
        data object Multiply : Token()
        data object Divide : Token()
        data object LParen : Token()
        data object RParen : Token()
    }

    private class Parser(private val tokens: List<Token>) {
        private var pos = 0

        fun isAtEnd(): Boolean = pos >= tokens.size
        fun position(): Int = pos

        private fun peek(): Token? = if (pos < tokens.size) tokens[pos] else null
        private fun consume(): Token = tokens[pos++]

        /**
         * expression = term (('+' | '-') term)*
         */
        fun parseExpression(): Double {
            var result = parseTerm()
            while (true) {
                when (peek()) {
                    is Token.Plus -> {
                        consume()
                        result += parseTerm()
                    }
                    is Token.Minus -> {
                        consume()
                        result -= parseTerm()
                    }
                    else -> break
                }
            }
            return result
        }

        /**
         * term = factor (('*' | '/') factor)*
         */
        private fun parseTerm(): Double {
            var result = parseFactor()
            while (true) {
                when (peek()) {
                    is Token.Multiply -> {
                        consume()
                        result *= parseFactor()
                    }
                    is Token.Divide -> {
                        consume()
                        val divisor = parseFactor()
                        if (divisor == 0.0) {
                            throw ArithmeticException("Division by zero")
                        }
                        result /= divisor
                    }
                    else -> break
                }
            }
            return result
        }

        /**
         * factor = ['+' | '-'] (NUMBER | '(' expression ')')
         */
        private fun parseFactor(): Double {
            return when (val token = peek()) {
                is Token.Number -> {
                    consume()
                    token.value
                }
                is Token.Minus -> {
                    consume()
                    -parseFactor()
                }
                is Token.Plus -> {
                    consume()
                    parseFactor()
                }
                is Token.LParen -> {
                    consume()
                    val result = parseExpression()
                    if (peek() !is Token.RParen) {
                        throw ParseException("Missing closing parenthesis")
                    }
                    consume()
                    result
                }
                null -> throw ParseException("Unexpected end of expression")
                else -> throw ParseException("Unexpected token: $token")
            }
        }
    }

    companion object {
        /**
         * Formats a Double result for display.
         * Removes unnecessary trailing zeros and handles special cases.
         */
        fun formatResult(value: Double): String {
            if (value.isNaN()) return "Error"
            if (value.isInfinite()) return "Error"

            // If the value is a whole number within safe range, show without decimal
            if (kotlin.math.abs(value) < 1e15 && value == value.toLong().toDouble()) {
                return value.toLong().toString()
            }

            // Remove trailing zeros but keep meaningful decimals using Locale.US to prevent comma bugs
            val formatted = String.format(java.util.Locale.US, "%.10f", value)
                .trimEnd('0')
                .trimEnd('.')
            return if (formatted == "-0" || formatted.isEmpty()) "0" else formatted
        }
    }
}
