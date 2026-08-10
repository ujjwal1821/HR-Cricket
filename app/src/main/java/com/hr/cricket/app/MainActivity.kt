package com.hrcricket.app

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var root: LinearLayout
    private var score = 74

    private val perms = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { r ->
        val cam = r[Manifest.permission.CAMERA] == true
        val mic = r[Manifest.permission.RECORD_AUDIO] == true
        val loc = r[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                r[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        toast("Camera $cam • Mic $mic • Location $loc")
        if (cam) cameraScreen()
    }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        home()
    }

    private fun base(title: String): LinearLayout {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18,18,18,18)
            setBackgroundColor(Color.rgb(245,247,251))
        }
        val bar = TextView(this).apply {
            text = title
            textSize = 25f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(7,16,29))
            setPadding(18,20,18,20)
            gravity = Gravity.CENTER_VERTICAL
        }
        root.addView(bar, LinearLayout.LayoutParams(-1,-2))
        setContentView(root)
        return root
    }

    private fun button(text: String, action: () -> Unit) =
        Button(this).apply { this.text = text; setOnClickListener { action() } }

    private fun home() {
        val l = base("HR CRICKET")
        l.addView(TextView(this).apply {
            text = "Record. Detect. Score.\\n\\nAI Camera • AI Umpire • Hey HR • Teams • Challenges"
            textSize = 22f
            setPadding(18,28,18,28)
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(7,16,29))
        })
        l.addView(button("📷 START AI CAMERA") { cameraScreen() })
        l.addView(button("📍 FIND NEARBY TEAMS") { location() })
        l.addView(button("⚔️ CHALLENGE A TEAM") { challenge() })
        l.addView(button("🏏 LIVE SCORE") { score() })
        l.addView(button("🧠 ALL AI / LBW / PLAYER FEATURES") { features() })
        l.addView(button("👥 MY TEAMS") { teams() })
        l.addView(TextView(this).apply { text="TRIAL MODE • ALL FEATURES FREE"; setPadding(8,18,8,8) })
    }

    private fun cameraScreen() {
        val l = base("HR CRICKET • AI CAMERA")
        val preview = PreviewView(this)
        l.addView(preview, LinearLayout.LayoutParams(-1,0,1f))
        l.addView(TextView(this).apply {
            text="AI VISION READY\\nBall tracking • 4/6 • catch • run-out • stumping • LBW"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(7,16,29))
            setPadding(12,12,12,12)
        })
        l.addView(button("🎙️ HEY HR VOICE") { voice() })
        l.addView(button("● RECORD MATCH") {
            toast("Recording pipeline ready for ML/video module.")
        })
        l.addView(button("STOP / HOME") { home() })
        perms.launch(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ))
        val f = ProcessCameraProvider.getInstance(this)
        f.addListener({
            val p=f.get()
            val pr=Preview.Builder().build()
            pr.surfaceProvider=preview.surfaceProvider
            p.unbindAll()
            p.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                pr
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun voice() {
        val i=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        i.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Say: Hey HR, change six to wicket"
        )
        try {
            startActivityForResult(i,7)
        } catch(e:Exception) {
            toast("Speech recognition unavailable")
        }
    }

    override fun onActivityResult(
        r:Int,
        c:Int,
        d:Intent?
    ) {
        super.onActivityResult(r,c,d)
        if(r==7 && c==RESULT_OK) {
            val s=d?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )?.firstOrNull().orEmpty()
            toast("Hey HR heard: $s")
        }
    }

    private fun location() {
        perms.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
        toast("Nearby team location permission requested.")
    }

    private fun teams() {
        val l=base("MY TEAMS")
        l.addView(TextView(this).apply {
            text="🏏 Sanawad Strikers\\n12 players • 7 matches\\n\\n📍 Nearby\\nKhandwa Kings — 4.2 km\\nBarwaha Warriors — 18 km"
            textSize=18f
            setPadding(12,20,12,20)
        })
        l.addView(button("+ CREATE TEAM"){
            toast("Trial team flow ready.")
        })
        l.addView(button("HOME"){home()})
    }

    private fun challenge() {
        val l=base("⚔️ CHALLENGE")
        l.addView(TextView(this).apply {
            text="Khandwa Kings\\n📍 4.2 km\\n⭐ 4.7\\n\\nDate • Time • Overs • Ground\\nTRIAL: FREE"
            textSize=18f
            setPadding(12,20,12,20)
        })
        l.addView(button("SEND CHALLENGE"){
            toast("Trial challenge sent.")
        })
        l.addView(button("HOME"){home()})
    }

    private fun score() {
        val l=base("🏏 LIVE SCORE")
        val s=TextView(this)
        s.text="Sanawad Strikers\\n$score/3 • 12 overs\\n\\nLast ball: SIX • 96% confidence"
        s.textSize=20f
        s.setPadding(12,20,12,20)
        l.addView(s)

        l.addView(button("+1 RUN"){
            score++
            s.text="Sanawad Strikers\\n$score/3"
        })

        l.addView(button("+4 FOUR"){
            score+=4
            s.text="Sanawad Strikers\\n$score/3"
        })

        l.addView(button("+6 SIX"){
            score+=6
            s.text="Sanawad Strikers\\n$score/3"
        })

        l.addView(button("WICKET"){
            s.text="Sanawad Strikers\\n$score/4\\n\\nLast event: WICKET"
        })

        l.addView(button("🎥 AI REVIEW / CHANGE SIX TO WICKET"){
            s.text="Sanawad Strikers\\n$score/4\\n\\nDecision changed: SIX → WICKET"
        })

        l.addView(button("HOME"){home()})
    }

    private fun features() {
        val l=base("HR CRICKET FEATURES")
        l.addView(TextView(this).apply {
            text="🤖 AI Umpire\\n🎯 Ball Tracking\\n🦵 AI LBW\\n🧤 Catch / Run-out / Stumping\\n🎙️ Hey HR\\n🧠 AI Captain / Coach\\n🎬 Auto Highlights\\n🪪 Player Cricket Card\\n🏆 Tournament & Points Table\\n👕 Jersey Designer\\n\\nTRIAL: ALL FREE"
            textSize=18f
            setPadding(12,20,12,20)
        })
        l.addView(button("HOME"){home()})
    }

    private fun toast(s:String)=Toast.makeText(
        this,
        s,
        Toast.LENGTH_LONG
    ).show()
}
