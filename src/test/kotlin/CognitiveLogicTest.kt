package org.example.task1

import org.example.task3.AbsurdAction
import org.example.task3.AlienArtifact
import org.example.task3.CognitiveProcessor
import org.example.task3.ConfidenceState
import org.example.task3.FamiliarEarthObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CognitiveLogicTest {

    private lateinit var arthurBrain: CognitiveProcessor

    @BeforeEach
    fun setup() {
        arthurBrain = CognitiveProcessor(stressThreshold = 50)
    }

    @Test
    @DisplayName("Базовое состояние: без раздражителей система уверена")
    fun testInitialState() {
        assertEquals(ConfidenceState.CONFIDENT, arthurBrain.evaluateCurrentState())
    }

    @Test
    @DisplayName("Сценарий 1: Умеренный абсурд вызывает растерянность")
    fun testModerateAbsurdityCausesConfusion() {
        arthurBrain.processStimulus(AlienArtifact("человек с Бетельгейзе с флакончиком", 20))

        assertEquals(ConfidenceState.CONFUSED, arthurBrain.evaluateCurrentState())
    }

    @Test
    @DisplayName("Сценарий 2: Накопление абсурда приводит к панике (Суровая реальность текста)")
    fun testHighAbsurdityCausesPanic() {
        // Артур видит:
        arthurBrain.processStimulus(AlienArtifact("нижнее белье дентрасси", 20))
        arthurBrain.processStimulus(AlienArtifact("скворншельские матрацы", 20))
        // Форд переходит к активным абсурдным действиям
        arthurBrain.processStimulus(AbsurdAction("предлагает засунуть рыбку в ухо", 40))

        // Общий стресс = 80. Порог (50) пробит. Якоря нет.
        assertTrue(arthurBrain.getStressLevel() > 50)
        assertEquals(
            ConfidenceState.PANIC,
            arthurBrain.evaluateCurrentState(),
            "Без ментального якоря перегрузка приводит к панике"
        )
    }

    @Test
    @DisplayName("Сценарий 3: Эффект Кукурузных Хлопьев (Ментальный Якорь блокирует стресс)")
    fun testMentalAnchorOverridesPanic() {
        // Окружение экстремально абсурдное (как в тесте 2)
        arthurBrain.processStimulus(AlienArtifact("нижнее белье дентрасси", 20))
        arthurBrain.processStimulus(AlienArtifact("скворншельские матрацы", 20))
        arthurBrain.processStimulus(AbsurdAction("предлагает засунуть рыбку в ухо", 40))

        // НО! Артур замечает коробку хлопьев
        val anchor = FamiliarEarthObject("пакет кукурузных хлопьев")
        arthurBrain.processStimulus(anchor)

        // Проверяем: стресс все еще огромный (80)
        assertTrue(arthurBrain.getStressLevel() > 50)

        // БИЗНЕС-ПРАВИЛО: Наличие якоря форсирует состояние CONFIDENT
        assertEquals(
            ConfidenceState.CONFIDENT,
            arthurBrain.evaluateCurrentState(),
            "Наличие знакомого предмета должно возвращать уверенность даже при сильном стрессе"
        )
    }
}