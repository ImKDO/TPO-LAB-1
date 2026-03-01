package org.example.task1

import org.example.task2.BPlusTree
import org.example.task2.EventLogger
import org.example.task2.TraceEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BPlusTreeTest : EventLogger {

    private val actualEvents = mutableListOf<String>()
    private lateinit var tree: BPlusTree

    @BeforeEach
    fun setup() {
        actualEvents.clear()
        tree = BPlusTree(this)
    }

    override fun log(event: TraceEvent, msg: String) {
        actualEvents.add(event.name)
    }

    @Test
    fun `test sequential insert with root split`() {
        // --- Этап 1: Заполнение корня (6 элементов) ---
        // Ожидание: каждый раз поиск (в корне) и вставка
        val inputsPart1 = listOf(10, 20, 30, 40, 50, 60)

        inputsPart1.forEach { tree.insert(it) }

        // Проверим лог. Должно быть 6 пар (SEARCH_VISIT, INSERT_LEAF)
        val expectedPart1 = mutableListOf<String>()
        repeat(6) {
            expectedPart1.add("SEARCH_VISIT")
            expectedPart1.add("INSERT_LEAF")
        }

        assertEquals(expectedPart1, actualEvents, "Этап 1: Простое заполнение")

        // --- Этап 2: Переполнение корня (7-й элемент) ---
        actualEvents.clear()
        tree.insert(70)

        // Эталонная последовательность для 70:
        val expectedPart2 = listOf(
            "SEARCH_VISIT", // Зашли в корень
            "INSERT_LEAF",  // Вставили 70
            "SPLIT_LEAF",   // Обнаружили 7 ключей -> делим
            "PROMOTE_KEY",  // Поднимаем 40 наверх
            "NEW_ROOT"      // Создаем новый корень над листами
        )

        assertEquals(expectedPart2, actualEvents, "Этап 2: Расщепление корня")

        // --- Этап 3: Вставка в правое поддерево (80, 90, 100) ---
        // Корень теперь [40]. 70 > 40, идем направо.
        // Правый лист сейчас [40, 50, 60, 70].
        actualEvents.clear()
        listOf(80, 90, 100).forEach { tree.insert(it) }

        val expectedPart3 = mutableListOf<String>()
        repeat(3) {
            expectedPart3.add("SEARCH_VISIT") // Корень
            expectedPart3.add("SEARCH_VISIT") // Лист
            expectedPart3.add("INSERT_LEAF")  // Вставка
        }
        // После вставки 100 правый лист станет [40, 50, 60, 70, 80, 90, 100] (7 ключей) -> SPLIT
        // Это происходит внутри последнего insert(100)
        // Добавим события расщепления к эталону:
        expectedPart3.add("SPLIT_LEAF")   // Правый лист делится
        expectedPart3.add("PROMOTE_KEY")  // Поднимаем 70 (середину) к родителю
        // NEW_ROOT не будет, так как в корне [40] есть место для [40, 70]

        // Корректировка: insert(100) вызовет split сразу после insert
        // Моделируем точную последовательность:
        val preciseExpected3 = listOf(
            // 80
            "SEARCH_VISIT", "SEARCH_VISIT", "INSERT_LEAF",
            // 90
            "SEARCH_VISIT", "SEARCH_VISIT", "INSERT_LEAF",
            // 100 -> trigger split
            "SEARCH_VISIT", "SEARCH_VISIT", "INSERT_LEAF",
            "SPLIT_LEAF", "PROMOTE_KEY"
        )

        assertEquals(preciseExpected3, actualEvents, "Этап 3: Заполнение правого узла и его расщепление")
    }

    @Test
    fun `test internal node split`() {
        // Сценарий: Забить дерево так, чтобы расщепился внутренний узел (корень второго уровня)
        // Нам нужно, чтобы в корневом узле (который станет внутренним) стало 7 ключей.
        // Это значит, нам нужно порядка 7 * 4 = 28+ вставок.

        // Для теста просто проверим логику срабатывания SPLIT_INTERNAL
        // Мы можем искусственно создать дерево или просто заполнять данными.

        // Вставляем 1..50
        (1..50).forEach { tree.insert(it) }

        // В логах должно появиться SPLIT_INTERNAL хотя бы раз
        val hasInternalSplit = actualEvents.contains("SPLIT_INTERNAL")
        val hasNewRoot = actualEvents.filter { it == "NEW_ROOT" }.size

        // При большом кол-ве вставок высота дерева должна расти
        assert(hasInternalSplit) { "Должно было произойти расщепление внутреннего узла" }
        assert(hasNewRoot >= 2) { "Дерево должно было вырасти минимум дважды (Leaf->Root, Internal->Root)" }
    }
}