package edu.gvsu.cis.kit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import edu.gvsu.cis.kit.data.AppDB
import edu.gvsu.cis.kit.data.getDatabaseInstance

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val db: AppDB = getDatabaseInstance(getDatabaseBuilder(applicationContext))
        val dao = db.getDao()

        initKoin(dao, applicationContext)

        setContent {
            App()
        }
    }
}