package com.greencoach.service.ai

import com.greencoach.model.scan.Prediction
import ai.onnxruntime.*
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.nio.FloatBuffer
import jakarta.annotation.PreDestroy
import javax.imageio.ImageIO
import kotlin.math.exp

@Component
@Profile("onnx")
class OnnxAiEngine : AiEngine {

    override val modelName: String = "onnx-garbage-classifier"

    private val inputSize = 224
    private val channels = 3
    private val normalize = 1.0f / 255.0f

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession
    private val inputName: String
    private val labels: List<String> = LabelLoader.loadLabels("/models/classes.txt")

    init {
        val bytes = javaClass.getResourceAsStream("/models/garbage_classifier.onnx")
            ?.readAllBytes() ?: throw IllegalStateException("ONNX model not found in /models")

        session = env.createSession(bytes)          // ✅ 세션 생성 (한 번만)
        inputName = session.inputNames.first()      // ✅ 입력 이름 캐싱
    }

    override fun predict(file: MultipartFile): List<Prediction> {
        val tensor = preprocessToNCHWFloat(file.bytes, inputSize, inputSize) // [1,3,224,224]

        // ✅ 세션은 닫지 않는다!
        val results = session.run(mapOf(inputName to tensor))
        results.use {
            @Suppress("UNCHECKED_CAST")
            val raw = it[0].value as Array<FloatArray> // [1, numClasses]
            val probs = softmax(raw[0])
            return probs.mapIndexed { i, p ->
                Prediction(
                    label = labels.getOrElse(i) { "class_$i" },
                    confidence = p.toDouble(),
                    category = null
                )
            }.sortedByDescending { it.confidence }
        }
    }

    /** 이미지 → resize(224x224) → NCHW float32 [1,3,H,W] */
    private fun preprocessToNCHWFloat(imageBytes: ByteArray, w: Int, h: Int): OnnxTensor {
        val img = ImageIO.read(ByteArrayInputStream(imageBytes))
            ?: throw IllegalArgumentException("Invalid image file")
        val resized = resize(img, w, h)

        val buffer = FloatArray(1 * channels * h * w)
        var idx = 0
        // R
        for (y in 0 until h) for (x in 0 until w) {
            val c = Color(resized.getRGB(x, y))
            buffer[idx++] = c.red * normalize
        }
        // G
        for (y in 0 until h) for (x in 0 until w) {
            val c = Color(resized.getRGB(x, y))
            buffer[idx++] = c.green * normalize
        }
        // B
        for (y in 0 until h) for (x in 0 until w) {
            val c = Color(resized.getRGB(x, y))
            buffer[idx++] = c.blue * normalize
        }

        val shape = longArrayOf(1, channels.toLong(), h.toLong(), w.toLong())
        return OnnxTensor.createTensor(env, FloatBuffer.wrap(buffer), shape)
    }

    private fun resize(src: BufferedImage, w: Int, h: Int): BufferedImage {
        val scaled: Image = src.getScaledInstance(w, h, Image.SCALE_SMOOTH)
        val dst = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
        val g = dst.createGraphics()
        g.drawImage(scaled, 0, 0, null)
        g.dispose()
        return dst
    }

    private fun softmax(logits: FloatArray): FloatArray {
        val max = logits.maxOrNull() ?: 0f
        var sum = 0.0
        val exps = DoubleArray(logits.size) { i ->
            val e = exp((logits[i] - max).toDouble()); sum += e; e
        }
        return FloatArray(logits.size) { i -> (exps[i] / sum).toFloat() }
    }

    @PreDestroy
    fun close() {
        // ✅ 애플리케이션 종료 시에만 정리
        try { session.close() } catch (_: Exception) {}
        try { env.close() } catch (_: Exception) {}
    }
}