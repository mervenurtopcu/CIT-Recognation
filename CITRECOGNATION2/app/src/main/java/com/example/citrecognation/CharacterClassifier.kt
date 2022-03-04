package com.example.citrecognation

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class CharacterClassifier(private val context: Context) {
    // Add a TF Lite interpreter as a field.

    private var interpreter: Interpreter? = null
    var class_mapping = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabdefghnqrt"
    var isInitialized=false
        private set

    /**  Executor arka planda çıkarım görevini çalıştırır.*/
    private val executorService: ExecutorService = Executors.newCachedThreadPool()

    private var inputImageWidth: Int = 0 // TF Lite modelinden çıkarılacak
    private var inputImageHeight: Int = 0 // TF Lite modelinden çıkarılacak
    private var modelInputSize: Int = 0 // TF Lite modelinden çıkarılacak

    fun initialize(): Task<Void> {
        val task = TaskCompletionSource<Void>()
        executorService.execute {
            try {
                initializeInterpreter()
                task.setResult(null)
            } catch (e: IOException) {
                task.setException(e)
            }
        }
        return task.task
    }
    // TF Lite modeli dosyadan yüklenir ve yorumlayıcıyı(interpreter) başlatılır.
    private fun initializeInterpreter(){

        val assetManager= context.assets
        val model= loadModelFile(assetManager,"emnist.tflite")
        val options = Interpreter.Options()
        options.setUseNNAPI(true)
        val interpreter = Interpreter(model,options)


        // Model dosyasından input shape okunur.
        val inputShape= interpreter.getInputTensor(0).shape()
        inputImageWidth = inputShape[1]
        inputImageHeight = inputShape[2]

        //modelInputSize, TensorFlow Lite modelimiz için girdiyi saklamak üzere kaç bayt bellek ayırmamız gerektiğini gösterir.
        //FLOAT_TYPE_SIZE, girdi veri tipimizin kaç bayt gerektireceğini gösterir. Float32 kullanıyoruz, yani 4 bayt.
        //PIXEL_SIZE, her pikselde kaç renk kanalı olduğunu gösterir. Giriş resmimiz tek renkli bir resimdir, bu nedenle yalnızca 1 renk kanalımız var.
        modelInputSize = FLOAT_TYPE_SIZE * inputImageWidth * inputImageHeight * PIXEL_SIZE

        // Yorumlayıcı başlatma bitirilir.
        this.interpreter = interpreter
        isInitialized = true
        Log.d(TAG, "Initialized TFLite interpreter.")
    }

    // Model dosyasının yüklenmesi
    private fun loadModelFile(assetManager: AssetManager, filename: String): ByteBuffer {
        val fileDescriptor = assetManager.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

//    TensorFlow Lite yorumlayıcımız kuruldu, bu yüzden giriş görüntüsündeki karakteri tanımak için kod yazalım. Aşağıdakileri yapmamız gerekecek:
//    Girdinin önceden işlenmesi: Bir Bitmap örneğini, girdi görüntüsündeki tüm piksellerin piksel değerlerini içeren bir ByteBuffer örneğine dönüştürülür.
//      ByteBuffer kullanıyoruz çünkü Kotlin yerel float çok boyutlu dizisinden daha hızlı.
//    Çıkarım yapılması.
//    Çıktının sonradan işlenmesi: olasılık dizisi insan tarafından okunabilir bir dizeye dönüştürülür.
    private fun classify(bitmap: Bitmap):String {
        check(isInitialized){"TF Lite Interpreter is not initialized yet."}

        // Preprocessing işlemi: giriş görüntüsü model giriş şekline uyacak şekilde yeniden boyutlandırılır.
        val resizedImage = Bitmap.createScaledBitmap(
            bitmap,
            inputImageWidth,
            inputImageHeight,
            true
        )
        val byteBuffer = convertBitmapToByteBuffer(resizedImage)

        // Model çıktısını depolamak için bir dizi tanımlanır.
        val output = Array(1) { FloatArray(OUTPUT_CLASSES_COUNT) }

        // Giriş verileriyle çıkarım yapılır.
        interpreter?.run(byteBuffer, output)

        // Post-processing işlemi: En yüksek olasılığa sahip basamağı bulur ve ona insan tarafından okunabilir bir dize haline getirilir.
        val result = output[0]
        val maxIndex = result.indices.maxBy { result[it] } ?: -1
        val mapping= class_mapping[maxIndex]
        val a="%"
        val resultString = "Prediction Result: %s \n Confidence: %s %d".format(mapping, a,(result[maxIndex]*100).toInt())

        return resultString
    }
    // Gelen bitmap üzerinden classify fonksiyonu ile cevabı döndürür.
    fun classifyAsync(bitmap: Bitmap): Task<String> {
        val task = TaskCompletionSource<String>()
        executorService.execute {
            val result = classify(bitmap)
            task.setResult(result)
        }
        return task.task
    }
    // Yorumlayıcı kapalımı kontrol edilir.
    fun close() {
        executorService.execute {
            interpreter?.close()
            Log.d(TAG, "Closed TFLite interpreter.")
        }
    }

    // Bitmap ByteBuffer a dönüştürülür. Classify() -> Pre-processing de kullanıldı.
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(modelInputSize)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(inputImageWidth * inputImageHeight)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixelValue in pixels) {
            val r = (pixelValue shr 16 and 0xFF)
            val g = (pixelValue shr 8 and 0xFF)
            val b = (pixelValue and 0xFF)

            // RGB, grayscale'e dönüştürülür ve piksel değeri [0..1] olarak normalleştirilir.
            val normalizedPixelValue = (r + g + b) / 3.0f / 255.0f
            byteBuffer.putFloat(normalizedPixelValue)
        }

        return byteBuffer
    }
    companion object {
        private const val TAG = "CharacterClassifier"

        private const val FLOAT_TYPE_SIZE = 4 // Float32
        private const val PIXEL_SIZE = 1
        private const val OUTPUT_CLASSES_COUNT = 47 // (A-Z)&&(0-9)
    }
}