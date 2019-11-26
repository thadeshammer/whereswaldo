package thadeshammer.experimentalcode

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.openimaj.feature.DoubleFVComparison
import org.openimaj.image.MBFImage
import org.openimaj.math.geometry.shape.Rectangle
import org.openimaj.math.statistics.distribution.MultidimensionalHistogram
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

/**
 * thade created on 11/25/2019
 */
class WaldoHistogramFinder(
    private val modelHistogram: MultidimensionalHistogram,
    private val sampleWidth: Int,
    private val sampleHeight: Int,
    private val histogramDimensions: Triple<Int, Int, Int>
) {
    fun scanImage(image: MBFImage, scanStep: Int = 4, wheresWaldo: Rectangle? = null) {
        val sampleBuffer = MBFImage(sampleWidth, sampleHeight)

        var scoreList = listOf<ScoreKeeper>()
        var mutex = Mutex()

        val durationTotalTimeMS = measureTimeMillis {
            for (y in 0..image.height-sampleHeight step scanStep) runBlocking {
                for (x in 0..image.width-sampleWidth step scanStep) {
                    image.extractROI(x, y, sampleBuffer)

                    val sampleHistogram = WaldoUtil.buildHistogram(
                        sampleBuffer,
                        histogramDimensions
                    )

                    // TODO parameterize compare mode
                    val score = modelHistogram.compare(sampleHistogram, DoubleFVComparison.EUCLIDEAN)

                    mutex.withLock {
                        scoreList += ScoreKeeper(score, x, y, sampleWidth, sampleHeight)
                    }
                }
            }
        }

        val durationStr = String.format(
            "%d min, %d sec",
            TimeUnit.MILLISECONDS.toMinutes(durationTotalTimeMS),
            TimeUnit.MILLISECONDS.toSeconds(durationTotalTimeMS) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationTotalTimeMS))
        )

        println ("Took $durationStr")

        var trimmedScores =
            if (wheresWaldo != null) {
                scoreList.filter {
                    WaldoUtil.rectOverlap(
                        Rectangle(
                            it.x.toFloat(), it.y.toFloat(), it.width.toFloat(), it.height.toFloat()
                        ),
                        wheresWaldo
                    )
                }.sortedBy { it.score }
            } else {
                WaldoUtil.thoroughTrimScoreList(scoreList)
            }

        val limit = if (trimmedScores.size >= 20) {
            20
        } else {
            trimmedScores.size
        }

        for (i in 0 until limit) {
            println("$i: ${trimmedScores[i]}")
        }
    }
}