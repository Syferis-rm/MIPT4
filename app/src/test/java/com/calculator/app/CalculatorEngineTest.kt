package com.calculator.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.Locale

class CalculatorEngineTest {

    private val engine = CalculatorEngine()

    @Test
    fun testBasicArithmeticAndPrecedence() {
        assertEquals(14.0, engine.evaluate("2 + 3 * 4"), 1e-9)
        assertEquals(20.0, engine.evaluate("(2 + 3) * 4"), 1e-9)
        assertEquals(7.0, engine.evaluate("10 - 6 / 2"), 1e-9)
        assertEquals(2.0, engine.evaluate("(10 - 6) / 2"), 1e-9)
    }

    @Test
    fun testDisplaySymbolsNormalization() {
        // Test symbols used in the UI: ×, ÷, −
        val result = engine.evaluate("10 × 4 ÷ 2 − 5")
        assertEquals(15.0, result, 1e-9)
    }

    @Test
    fun testCommaDecimalSupport() {
        // Test that comma as decimal separator (e.g. Lithuanian locale) works
        val result = engine.evaluate("2,5 + 1,5")
        assertEquals(4.0, result, 1e-9)
    }

    @Test
    fun testUnaryMinus() {
        assertEquals(-5.0, engine.evaluate("-5"), 1e-9)
        assertEquals(5.0, engine.evaluate("--5"), 1e-9)
        assertEquals(-2.0, engine.evaluate("-5 + 3"), 1e-9)
    }

    @Test
    fun testDivisionByZero() {
        assertThrows(ArithmeticException::class.java) {
            engine.evaluate("5 / 0")
        }
    }

    @Test
    fun testMalformedExpression() {
        assertThrows(CalculatorEngine.ParseException::class.java) {
            engine.evaluate("2 + * 3")
        }
    }

    @Test
    fun testFormatResultLocaleIndependent() {
        // Even if default locale is Lithuanian with comma decimals,
        // formatResult should use standard dot or integers
        val defaultLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale("lt", "LT"))
            assertEquals("4", CalculatorEngine.formatResult(4.0))
            assertEquals("2.5", CalculatorEngine.formatResult(2.5))
            assertEquals("0", CalculatorEngine.formatResult(0.0))
            assertEquals("0", CalculatorEngine.formatResult(-0.0))
        } finally {
            Locale.setDefault(defaultLocale)
        }
    }
}
