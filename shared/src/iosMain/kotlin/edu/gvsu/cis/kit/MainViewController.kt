package edu.gvsu.cis.kit

import androidx.compose.ui.window.ComposeUIViewController
import edu.gvsu.cis.kit.data.getDatabaseBuilder
import edu.gvsu.cis.kit.data.getDatabaseInstance

fun MainViewController() = ComposeUIViewController {
    val database = getDatabaseInstance(getDatabaseBuilder())
    initKoin(database.getDao())

    App()
}