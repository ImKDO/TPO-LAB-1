package org.example.task3


/**
 * Состояние уверенности системы человека.
 */
enum class ConfidenceState {
    PANIC,      // Критический уровень
    CONFUSED,   // Высокий стресс
    CONFIDENT   // Стабильно все
}

/**
 * Характеристики любого объекта/события, попадающего в поле зрения.
 */
interface Stimulus {
    val description: String
    val cognitiveLoad: Int
    val providesMentalAnchor: Boolean   // Может ли служить "зацепкой" для психики
}