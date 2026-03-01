package org.example.task1

class Trigonometric {
    fun sec(x: Double, terms: Int = 10): Double {
        if (x <= -1.5707 || x >= 1.5707) {
            println("Внимание: Аргумент близок к границе сходимости (PI/2), результат может быть неточным.")
        }

        var result = 0.0

        eulerCache.clear()
        eulerCache[0] = 1L

        for (i in 0 until terms) {
            val n = 2 * i

            val eulerVal = getEulerNumber(n)

            val factVal = myFactorial(n)

            val xPow = myPow(x, n)

            val term = (eulerVal.toDouble() / factVal.toDouble()) * xPow

            result += term
        }

        return result
    }

    // Кэш для мемоизации чисел Эйлера
    val eulerCache = mutableMapOf<Int, Long>()

    /**
     * Рекурсивное вычисление n-го числа Эйлера (E_n).
     * Работает только для четных n.
     */
    fun getEulerNumber(n: Int): Long {
        if (n % 2 != 0) return 0
        if (n == 0) return 1L

        if (eulerCache.containsKey(n)) {
            return eulerCache[n]!!
        }

        var sum: Long = 0

        // Формула рекурсии выведена из cos(x)sec(x) = 1
        // E_n = - sum_{k=0, step 2}^{n-2} [ C(n, k) * E_k * (-1)^((n-k)/2) ]
        // Но мы переносим знак суммы, поэтому логика знаков инвертируется

        for (k in 0 until n step 2) {
            val combinations = myCombinations(n, k)
            val eulerK = getEulerNumber(k)

            // Разница степеней, деленная на 2
            val stepDiff = (n - k) / 2

            // Если stepDiff нечетный -> знак минус, иначе плюс
            val sign = if (stepDiff % 2 != 0) -1 else 1

            // Формируем слагаемое
            val term = sign * combinations * eulerK

            // Вычитаем из суммы (так как E_n переносится в другую часть уравнения)
            sum -= term
        }

        eulerCache[n] = sum
        return sum
    }

    /**
     * Вычисление биномиального коэффициента C(n, k)
     */
    fun myCombinations(n: Int, k: Int): Long {
        var kOptimized = k
        if (kOptimized < 0 || kOptimized > n) return 0
        if (kOptimized == 0 || kOptimized == n) return 1

        // Свойство симметрии C(n, k) == C(n, n-k)
        if (kOptimized > n / 2) {
            kOptimized = n - kOptimized
        }

        var res: Long = 1
        for (i in 0 until kOptimized) {
            // Умножаем, потом делим, чтобы оставаться в целых числах
            // Используем Long, чтобы избежать переполнения на промежуточных этапах
            res = res * (n - i) / (i + 1)
        }
        return res
    }

    /**
     * Вычисление факториала
     */
    fun myFactorial(n: Int): Long {
        if (n < 0) return 0 // Обработка ошибки
        if (n == 0) return 1
        var res: Long = 1
        for (i in 2..n) {
            res *= i
        }
        return res
    }

    /**
     * Возведение в степень
     */
    fun myPow(base: Double, exp: Int): Double {
        var res = 1.0
        for (i in 0 until exp) {
            res *= base
        }
        return res
    }
}