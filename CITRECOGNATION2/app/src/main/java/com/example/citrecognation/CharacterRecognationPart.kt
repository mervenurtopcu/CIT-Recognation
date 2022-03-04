package com.example.citrecognation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import com.divyanshu.draw.widget.DrawView
import kotlinx.android.synthetic.main.activity_character_recognation_part.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.toolbar

class CharacterRecognationPart : AppCompatActivity() {

    private var drawView: DrawView? = null
    private var clearButton: Button? = null
    private var predictedTextView: TextView? = null
    private var characterClassifier = CharacterClassifier(this)



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_recognation_part)

        // Toolbar işlemleri
        toolbar1.title="Character Recognation"
        setSupportActionBar(toolbar1)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        // Görsel nesnelerin kurulumu
        drawView = findViewById(R.id.draw_view)
        drawView?.setStrokeWidth(70.0f)
        drawView?.setColor(Color.WHITE)
        drawView?.setBackgroundColor(Color.BLACK)
        clearButton = findViewById(R.id.clear_button)
        predictedTextView = findViewById(R.id.predicted_text)

        // Clear drawing button kurulumu
        clearButton?.setOnClickListener {
            drawView?.clearCanvas()
            predictedTextView?.text = getString(R.string.prediction_text_placeholder)
        }

        // Her vuruştan sonra sınıflandırma yapmak için sınıflandırma tetikleyicisi ayarlanır.
        drawView?.setOnTouchListener { _, event ->
            // Draw View dokunma olayını kesintiye uğrattığımız için,
            // çizimin gösterilmesi için önce dokunma olaylarını örneğe geçirmemiz gerekiyor.
            drawView?.onTouchEvent(event)

            // Ardından, kullanıcı bir dokunma olayını tamamladıysa, sınıflandırma çalıştırılır.
            if (event.action == MotionEvent.ACTION_UP) {
                classifyDrawing()
            }

            true
        }

        // Character classifier kurulumu.
        characterClassifier
            .initialize()
            .addOnFailureListener { e -> Log.e(TAG, "Error to setting up character classifier.", e) }
    }

    //CharacterClassifier nesnesi ve CharacterRecognationPart yaşam dögüsü senkronize edilir
    // ve activity yok edildiğinde kaynaklar serbest bırakılır.(örneğin, TF Lite instance)
    override fun onDestroy() {

        characterClassifier.close()
        super.onDestroy()
    }


    private fun classifyDrawing() {
        val bitmap = drawView?.getBitmap()

        if ((bitmap != null) && (characterClassifier.isInitialized)) {
            characterClassifier
                .classifyAsync(bitmap)
                .addOnSuccessListener { resultText ->
                    predictedTextView?.text = resultText }
                .addOnFailureListener { e ->
                    predictedTextView?.text = getString(
                        R.string.classification_error_message,
                        e.localizedMessage
                    )
                    Log.e(TAG, "Error classifying drawing.", e)
                }
        }
    }

    companion object {
        const val TAG = "CharacterRecActivity"
    }
}

