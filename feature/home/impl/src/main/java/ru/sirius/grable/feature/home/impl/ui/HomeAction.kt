package ru.sirius.grable.feature.home.impl.ui

import ru.sirius.grable.core.navigation.api.Screens

data class HomeAction(
    val titleRes: Int,
    val descriptionRes: Int,
    val buttonTextRes: Int,
    val screen: Screens,
    val badge: String? = null
)

