import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import edu.gvsu.cis.kit.data.AppDB
import edu.gvsu.cis.kit.data.getDatabaseInstance

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val db: AppDB = getDatabaseInstance(getDatabaseBuilder(applicationContext))
        val dao = db.getDao()

        initKoin(dao)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
