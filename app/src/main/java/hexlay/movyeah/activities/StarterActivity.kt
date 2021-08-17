package hexlay.movyeah.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hexlay.movyeah.helpers.initDarkMode
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.noAnimation

class StarterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDarkMode()
        startActivity(setupNormalIntent())
        finish()
    }

    private fun setupNormalIntent(): Intent {
        val intent = intentFor<MainActivity>().noAnimation()
        if (intent.extras != null && !intent.extras!!.isEmpty) {
            intent.putExtras(intent.extras!!)
        }
        return intent
    }

}
