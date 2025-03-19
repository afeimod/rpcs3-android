package net.rpcs3

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isInvisible
import net.rpcs3.databinding.ActivityRpcs3Binding
import net.rpcs3.dialogs.AlertDialogQueue
import kotlin.concurrent.thread

class RPCS3Activity : Activity() {
    private lateinit var binding: ActivityRpcs3Binding
    private lateinit var unregisterUsbEventListener: () -> Unit
  
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRpcs3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        unregisterUsbEventListener = listenUsbEvents(this)
        enableFullScreenImmersive()

        binding.oscToggle.setOnClickListener { 
            binding.padOverlay.isInvisible = !binding.padOverlay.isInvisible 
            binding.oscToggle.setImageResource(if (binding.padOverlay.isInvisible) R.drawable.ic_osc_off else R.drawable.ic_show_osc)
        }


        thread {
            if (RPCS3.getState() != EmulatorState.Stopped) {
                Log.w("RPCS3 State", RPCS3.getState().name)

                if (RPCS3.getState() != EmulatorState.Stopping) {
                    RPCS3.instance.kill()
                }

                while (RPCS3.getState() != EmulatorState.Stopped) {
                    Thread.sleep(300)
                }
            }

            Log.w("RPCS3 State", RPCS3.getState().name)

            val bootResult = RPCS3.boot(intent.getStringExtra("path")!!)

            if (bootResult != BootResult.NoErrors) {
                AlertDialogQueue.showDialog("Boot Failed", "Error: ${bootResult.name}")
                finish()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterUsbEventListener()
    }

    private fun enableFullScreenImmersive() {
        // TODO(Ishan09811, DHrpcs3): Implement edge to edge screen + set insets in overlay controlls to support edge to edge
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enableFullScreenImmersive()
    }
}
