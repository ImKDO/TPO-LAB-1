package org.example.task3

// Абсурдные инопланетные предметы (генерируют сильный стресс)
class AlienArtifact(override val description: String, override val cognitiveLoad: Int = 20) : Stimulus {
    override val providesMentalAnchor = false
}

// Абсурдные действия (генерируют максимальный стресс)
class AbsurdAction(override val description: String, override val cognitiveLoad: Int = 40) : Stimulus {
    override val providesMentalAnchor = false
}

// Знакомые земные предметы (снимают стресс, дают якорь)
class FamiliarEarthObject(override val description: String) : Stimulus {
    override val cognitiveLoad: Int = 0
    override val providesMentalAnchor = true // Главное бизнес-свойство этого объекта
}