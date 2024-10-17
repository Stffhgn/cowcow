package com.example.cow_cow.background

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class RollingHillsView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private val cloudPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
    }
    private val edgePaint = Paint()
    private val gradientPaint = Paint()
    private val foregroundTexturePaint = Paint()
    private val sunMoonPaint = Paint().apply { isAntiAlias = true }

    // Random instance for randomness
    private val random = Random(System.currentTimeMillis())

    // Variables for clouds
    private val clouds = mutableListOf<Cloud>()

    // Variables for hills
    private val hillPositions = mutableListOf<Hill>()

    // Cows positioned on the hills
    private val cowPositions = mutableListOf<Cow>()

    private val skyColors = listOf(
        SkyColor(0, Color.rgb(0, 0, 50)),    // Midnight
        SkyColor(6, Color.rgb(135, 206, 235)),  // Morning
        SkyColor(12, Color.rgb(135, 206, 250)), // Midday
        SkyColor(18, Color.rgb(255, 140, 0)),   // Evening
        SkyColor(24, Color.rgb(0, 0, 50))    // Midnight
    )

    init {
        // Foreground texture pattern: simple horizontal lines
        foregroundTexturePaint.apply {
            color = Color.argb(50, 255, 255, 255)
            style = Paint.Style.STROKE
            strokeWidth = 5f
            pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
            isAntiAlias = true
        }

        // Edge color for the hills
        edgePaint.apply {
            color = Color.rgb(0, 100, 0)  // Darker green for hill edges
            style = Paint.Style.STROKE
            strokeWidth = 4f  // Adjusted edge thickness
            isAntiAlias = true
        }

        // Initialize hills and clouds
        initializeHills()
        initializeClouds()
    }

    private fun initializeHills() {
        // Clear existing hills
        hillPositions.clear()

        // Create multiple layers of hills
        val hillLayers = 4
        val hillWidth = width.toFloat()

        for (i in 0 until hillLayers) {
            // Adjust height variation for more fluctuation
            val peakFactor = if (i == hillLayers - 1) {
                // Foreground hill (now with more fluctuation)
                0.3f + random.nextFloat() * 0.2f  // Peaks between 30% and 50% of screen height
            } else {
                // Background hill (higher peaks for background)
                0.3f + random.nextFloat() * 0.3f  // Peaks between 30% and 60% of screen height
            }

            // Calculate peakHeight and baseHeight using the new fluctuation factor
            val peakHeight = height * peakFactor
            val baseHeight = height - (height.toFloat() / (5 + i))  // Higher base to lower the hills

            // Generate enough hills to cover the entire screen width
            var initialX = -2 * hillWidth  // Start placing hills far to the left

            while (initialX < width + 2 * hillWidth) {
                hillPositions.add(
                    Hill(
                        x = initialX,  // Position the hills across the screen
                        speed = 0.5f + i * 0.3f,  // Faster in front
                        peakHeight = peakHeight,  // Use calculated peakHeight
                        baseHeight = baseHeight,  // Use calculated baseHeight
                        isForeground = i == hillLayers - 1,  // Foreground is the last layer
                        colorStart = adjustColorBrightness(Color.rgb(34, 139, 34), 1 - i * 0.15f),
                        colorEnd = adjustColorBrightness(Color.rgb(0, 100, 0), 1 - i * 0.15f),
                        variation = random.nextFloat() * 0.5f + 0.5f  // Add some variation to make hills unique
                    )
                )
                initialX += hillWidth  // Move the next hill to the right
            }
        }
    }


    private fun initializeClouds() {
        clouds.clear()
        val cloudCount = 5
        for (i in 0 until cloudCount) {
            clouds.add(
                Cloud(
                    x = random.nextFloat() * width,
                    y = random.nextFloat() * height / 2,
                    speed = 1f + random.nextFloat(),
                    shape = generateCloudShape(),
                    alpha = (random.nextFloat() * 0.5f + 0.5f)
                )
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Now that the size is set, we can initialize the hills
        initializeHills()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Get current time
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTime = currentHour + currentMinute / 60f

        // Draw dynamic sky
        drawDynamicSky(canvas, currentTime)

        // Draw sun or moon
        drawSunOrMoon(canvas, currentTime)

        // Move and draw clouds
        for (cloud in clouds) {
            cloud.x += cloud.speed
            if (cloud.x > width + 200) {
                cloud.x = -200f
                cloud.y = random.nextFloat() * height / 2
                cloud.shape = generateCloudShape()
                cloud.alpha = (random.nextFloat() * 0.5f + 0.5f)
            }
            drawCloud(canvas, cloud)
        }

        // Update and draw hills (without texture)
        for (hill in hillPositions) {
            hill.x += hill.speed

            // Ensure hills fully exit the screen before resetting their position
            if (hill.x > width + 2 * width.toFloat()) {
                hill.x = -2 * width.toFloat()  // Reset the hill position only when it's fully off-screen
                regenerateHill(hill)
            }

            // Set gradient paint for the hill
            gradientPaint.shader = LinearGradient(
                0f, hill.peakHeight, 0f, hill.baseHeight,
                hill.colorStart, hill.colorEnd,
                Shader.TileMode.CLAMP
            )

            // Draw the hills with edges but without texture
            drawHillWithEdge(canvas, hill)
        }

        // Move and draw cows
        for (cow in cowPositions) {
            cow.x += cow.speed
            if (cow.x > width + 50) {
                cow.x = -50f
            }
            drawCow(canvas, cow)
        }

        // Add new cows randomly
        if (cowPositions.size < 5 && random.nextFloat() < 0.01f) {
            cowPositions.add(
                Cow(
                    x = -50f,
                    y = height - height / 5f - random.nextFloat() * 100,
                    speed = hillPositions.last().speed,
                    scale = random.nextFloat() * 0.5f + 0.75f
                )
            )
        }

        // Redraw the view to simulate animation
        postInvalidateOnAnimation()
    }

    private fun drawDynamicSky(canvas: Canvas, time: Float) {
        val (startColor, endColor) = getSkyGradientColors(time)
        val skyGradient = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            startColor, endColor,
            Shader.TileMode.CLAMP
        )
        paint.shader = skyGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null
    }

    private fun getSkyGradientColors(time: Float): Pair<Int, Int> {
        val times = skyColors.map { it.hour.toFloat() }
        val colors = skyColors.map { it.color }

        var startIndex = 0
        for (i in 0 until times.size - 1) {
            if (time >= times[i] && time < times[i + 1]) {
                startIndex = i
                break
            }
        }

        val t = (time - times[startIndex]) / (times[startIndex + 1] - times[startIndex])
        val startColor = colors[startIndex]
        val endColor = colors[startIndex + 1]

        val blendedColor = blendColors(startColor, endColor, t)
        return blendedColor to endColor
    }

    private fun drawSunOrMoon(canvas: Canvas, time: Float) {
        val isDay = time >= 6f && time < 18f
        val sunMoonX = width * ((time - 6f) / 12f)  // Adjusted for sunrise at 6 AM and sunset at 6 PM
        val sunMoonY = height / 2f - (height / 3f) * sin(Math.toRadians(((time - 6f) / 12f) * 180.0)).toFloat()

        sunMoonPaint.color = if (isDay) Color.YELLOW else Color.WHITE
        sunMoonPaint.alpha = if (isDay) 255 else 180

        canvas.drawCircle(sunMoonX, sunMoonY, 50f, sunMoonPaint)
    }

    private fun updateAndDrawHills(canvas: Canvas) {
        // Move and draw hills
        for (hill in hillPositions) {
            hill.x += hill.speed

            // Ensure hills fully exit the screen before resetting their position
            if (hill.x > width + 2 * width.toFloat()) {
                hill.x = -2 * width.toFloat()  // Reset the hill position only when it's fully off-screen
                regenerateHill(hill)
            }

            // Set gradient paint for the hill
            gradientPaint.shader = LinearGradient(
                0f, hill.peakHeight, 0f, hill.baseHeight,
                hill.colorStart, hill.colorEnd,
                Shader.TileMode.CLAMP
            )

            // Draw the hills with textures and edges
            if (hill.isForeground) {
                drawTexturedHillWithEdge(canvas, hill)
            } else {
                drawHillWithEdge(canvas, hill)
            }
        }
    }

    private fun regenerateHill(hill: Hill) {
        // Variables for regeneration calculations
        val peakFactor = if (hill.isForeground) {
            // Foreground hill (max bottom half, now with more fluctuation)
            0.3f + random.nextFloat() * 0.2f  // Peaks between 30% and 50% of screen height
        } else {
            // Background hill (higher peaks for background)
            0.3f + random.nextFloat() * 0.3f  // Peaks between 30% and 60% of screen height
        }

        val baseFactor = if (hill.isForeground) 5 else 6  // Adjust factor to lower base heights

        // Calculate peakHeight and baseHeight using intermediate variables
        val peakHeight = height * peakFactor
        val baseHeight = height - (height.toFloat() / baseFactor)

        // Update hill properties
        hill.speed = 0.5f + random.nextFloat() * 0.5f
        hill.peakHeight = peakHeight
        hill.baseHeight = baseHeight
        hill.variation = random.nextFloat() * 0.5f + 0.5f
        hill.colorStart = adjustColorBrightness(hill.colorStart, hill.variation)
        hill.colorEnd = adjustColorBrightness(hill.colorEnd, hill.variation)
    }

    private fun drawHillWithEdge(canvas: Canvas, hill: Hill) {
        val path = generateHillPath(hill)
        canvas.drawPath(path, gradientPaint)
        canvas.drawPath(path, edgePaint)
    }

    private fun drawTexturedHillWithEdge(canvas: Canvas, hill: Hill) {
        val path = generateHillPath(hill)
        canvas.drawPath(path, gradientPaint)

        // Add texture (horizontal lines) with increased spacing to avoid dense gray dashes
        for (i in hill.peakHeight.toInt() until hill.baseHeight.toInt() step 80) {
            canvas.drawLine(-2 * width.toFloat() + hill.x, i.toFloat(), 2 * width + hill.x, i.toFloat(), foregroundTexturePaint)
        }

        canvas.drawPath(path, edgePaint)
    }

    private fun generateHillPath(hill: Hill): Path {
        val path = Path()
        val amplitude = 50f * hill.variation
        val frequency = 2 * Math.PI / width

        path.moveTo(-width.toFloat() + hill.x, hill.baseHeight)
        for (x in -width.toInt()..(2 * width).toInt() step 10) {
            val y = hill.baseHeight - amplitude * sin(frequency * (x + hill.x)).toFloat()
            path.lineTo(x + hill.x, y)
        }
        path.lineTo(2 * width.toFloat() + hill.x, height.toFloat())
        path.lineTo(-width.toFloat() + hill.x, height.toFloat())
        path.close()
        return path
    }

    private fun drawCloud(canvas: Canvas, cloud: Cloud) {
        cloudPaint.alpha = (cloud.alpha * 255).toInt()
        val x = cloud.x
        val y = cloud.y
        val shape = cloud.shape

        for ((radius, offsetX, offsetY) in shape) {
            canvas.drawCircle(x + offsetX, y + offsetY, radius, cloudPaint)
        }
    }

    private fun generateCloudShape(): List<Triple<Float, Float, Float>> {
        val numCircles = random.nextInt(3, 6)
        return List(numCircles) {
            val radius = random.nextFloat() * 50 + 30
            val offsetX = random.nextFloat() * 100 - 50
            val offsetY = random.nextFloat() * 20 - 10
            Triple(radius, offsetX, offsetY)
        }
    }

    private fun drawCow(canvas: Canvas, cow: Cow) {
        if (cow.x > -50 && cow.x < width + 50) {
            // Draw a simple cow using shapes or use a bitmap image for better visuals
            paint.color = Color.BLACK
            canvas.save()
            canvas.translate(cow.x, cow.y)
            canvas.scale(cow.scale, cow.scale)

            // Body
            val bodyRect = RectF(0f, 0f, 50f, 30f)
            canvas.drawRect(bodyRect, paint)

            // Legs
            for (i in 0 until 4) {
                val legX = i * 12f + 5f
                canvas.drawRect(legX, 30f, legX + 5f, 45f, paint)
            }

            // Head
            val headRect = RectF(-20f, 0f, 0f, 20f)
            canvas.drawRect(headRect, paint)

            // Eyes
            paint.color = Color.WHITE
            canvas.drawCircle(-10f, 5f, 3f, paint)
            canvas.drawCircle(-10f, 15f, 3f, paint)

            paint.color = Color.BLACK
            canvas.restore()
        }
    }

    private fun adjustColorBrightness(color: Int, factor: Float): Int {
        val r = ((Color.red(color) * factor).coerceIn(0f, 255f)).toInt()
        val g = ((Color.green(color) * factor).coerceIn(0f, 255f)).toInt()
        val b = ((Color.blue(color) * factor).coerceIn(0f, 255f)).toInt()
        return Color.rgb(r, g, b)
    }

    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val r = Color.red(color1) * ratio + Color.red(color2) * inverseRatio
        val g = Color.green(color1) * ratio + Color.green(color2) * inverseRatio
        val b = Color.blue(color1) * ratio + Color.blue(color2) * inverseRatio
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }

    // Data classes
    data class Hill(
        var x: Float,
        var speed: Float,
        var peakHeight: Float,
        var baseHeight: Float,
        val isForeground: Boolean,
        var colorStart: Int,
        var colorEnd: Int,
        var variation: Float
    )

    data class Cloud(
        var x: Float,
        var y: Float,
        var speed: Float,
        var shape: List<Triple<Float, Float, Float>>,
        var alpha: Float
    )

    data class Cow(
        var x: Float,
        var y: Float,
        var speed: Float,
        var scale: Float
    )

    data class SkyColor(
        val hour: Int,
        val color: Int
    )
}
