package org.example.task3

class CognitiveProcessor(private val stressThreshold: Int = 50) {

    private var accumulatedStress: Int = 0
    private var isMentallyGrounded: Boolean = false

    /**
     * Бизнес-процесс: Обработка нового визуального или смыслового раздражителя.
     */
    fun processStimulus(stimulus: Stimulus) {
        accumulatedStress += stimulus.cognitiveLoad

        // Если объект дает ментальный якорь
        // система фиксирует заземление
        if (stimulus.providesMentalAnchor) {
            isMentallyGrounded = true
        }
    }

    /**
     * "Чувствовал бы себя увереннее, если бы... увидел хлопья".
     */
    fun evaluateCurrentState(): ConfidenceState {
        // хлопья -- чел УВЕРЕН,
        // независимо от того, насколько высок стресс от окружения.
        if (isMentallyGrounded) {
            return ConfidenceState.CONFIDENT
        }

        // Правило 2: Если якоря нет, и стресс превысил порог (матрацы + рыба в ухо) => ПАНИКА
        if (accumulatedStress >= stressThreshold) {
            return ConfidenceState.PANIC
        }

        // Правило 3: Стресс есть, но порог не пробит (просто смотрит на рыбу) => РАСТЕРЯННОСТЬ
        if (accumulatedStress > 0) {
            return ConfidenceState.CONFUSED
        }

        // Все чики пуки
        return ConfidenceState.CONFIDENT
    }

    // Метод для тестов
    fun getStressLevel() = accumulatedStress
}