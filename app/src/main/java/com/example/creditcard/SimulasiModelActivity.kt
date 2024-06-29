package com.example.creditcard

import android.annotation.SuppressLint
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiModelActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "credit.tflite"

    private lateinit var resultText: TextView
    private lateinit var ID: EditText
    private lateinit var LIMIT_BAL: EditText
    private lateinit var SEX: EditText
    private lateinit var EDUCATION: EditText
    private lateinit var MARRIAGE: EditText
    private lateinit var AGE: EditText
    private lateinit var BILL_AMT1: EditText
    private lateinit var PAY_AMT1: EditText
    private lateinit var checkButton : Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi_model)

        resultText = findViewById(R.id.txtResult)
        ID = findViewById(R.id.ID)
        LIMIT_BAL = findViewById(R.id.LIMIT_BAL)
        SEX = findViewById(R.id.SEX)
        EDUCATION = findViewById(R.id.EDUCATION)
        MARRIAGE = findViewById(R.id.MARRIAGE)
        AGE = findViewById(R.id.AGE)
        BILL_AMT1 = findViewById(R.id.BILL_AMT1)
        PAY_AMT1 = findViewById(R.id.PAY_AMT1)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                ID.text.toString(),
                LIMIT_BAL.text.toString(),
                SEX.text.toString(),
                EDUCATION.text.toString(),
                MARRIAGE.text.toString(),
                AGE.text.toString(),
                BILL_AMT1.text.toString(),
                PAY_AMT1.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Ya"
                }else if (result == 1){
                    resultText.text = "Tidak"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(9)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String, input8: String): Int{
        val inputVal = FloatArray(8)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        inputVal[7] = input8.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}