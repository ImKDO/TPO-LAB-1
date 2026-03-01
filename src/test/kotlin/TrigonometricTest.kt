package org.example.task1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.math.abs
import org.example.task1.TrigonometricTest

class TrigonometricTest {

    private val trig = Trigonometric()
    // Базовая точность сравнения
    private val delta = 0.0001

    /**
     * Параметризованный тест для проверки конкретных значений sec(x).
     * Используем CsvSource для передачи пар: x, expected
     */
    @ParameterizedTest(name = "sec({0}) должен быть равен {1}")
    @CsvSource(
        "0.0,               1.0",           // sec(0) = 1.0
        "0.5235987756,      1.154700538",            // sec(PI/6) ~ 1.154700538
        "0.7853981634,      1.414213562",            // sec(PI/4) ~ 1.414213562
        "-0.7853981634,     1.414213562"             // sec(-PI/4) ~ 1.414213562
    )
    fun testSecValues(x: Double, expected: Double) {
        val result = trig.sec(x, terms = 10)

        assertEquals(expected, result, 0.001, "Ошибка при x = $x")
    }

    /**
     * Параметризованный тест для проверки свойства четности функции:
     * sec(x) == sec(-x)
     */
    @ParameterizedTest(name = "Проверка симметрии для x = {0}")
    @ValueSource(doubles = [0.1, 0.5, 0.9, 1.2, 1.5])
    fun testSymmetry(x: Double) {
        val positive = trig.sec(x, terms = 20)
        val negative = trig.sec(-x, terms = 20)

        assertEquals(positive, negative, delta, "sec($x) должен быть равен sec(-$x)")
    }

    /**
     * Тестирование вспомогательной функции факториала
     * на наборе значений
     */
    @ParameterizedTest(name = "{0}! должен быть равен {1}")
    @CsvSource(
        "0, 1",
        "1, 1",
        "5, 120",
        "6, 720",
        "10, 3628800"
    )
    fun testFactorialParameterized(n: Int, expected: Long) {
        assertEquals(expected, trig.myFactorial(n))
    }

    /**
     * Тестирование чисел Эйлера
     * Только четные индексы, так как нечетные дают 0 в нашей реализации
     */
    @ParameterizedTest(name = "E_{0} должно быть {1}")
    @CsvSource(
        "0, 1",
        "2, 1",
        "4, 5",
        "6, 61",
        "8, 1385"
    )
    fun testEulerNumbersParameterized(n: Int, expected: Long) {
        // Очистим кэш перед тестом, чтобы проверить "чистое" вычисление
        val localTrig = Trigonometric()
        assertEquals(expected, localTrig.getEulerNumber(n))
    }
}