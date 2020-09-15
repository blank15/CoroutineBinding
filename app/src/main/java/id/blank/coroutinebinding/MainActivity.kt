package id.blank.coroutinebinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.textChanges
import kotlin.coroutines.CoroutineContext

class MainActivity() : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main.immediate + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()

        combine(
            ed_email.textChanges()
                .map {
                    Patterns.EMAIL_ADDRESS.matcher(it).matches()
                }.onEach {
                    if(ed_email.length() > 1 && !it){
                        ed_email.error = "Format email salah!"
                    }
                },
            ed_password.textChanges()
                .map {
                    it.length > 5
                }.onEach {
                    if(ed_password.length() > 1 && !it){
                        ed_password.error = "Password kurang dari 6 karakter"
                    }
                },
            transform = {email,password -> email && password}
        ).onEach {
            btn_login.isEnabled = it
        }.launchIn(this)

        btn_login.clicks()
            .onEach {
                Toast.makeText(this,"Verifikasi berhasil",Toast.LENGTH_SHORT).show()
            }.launchIn(this)
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}