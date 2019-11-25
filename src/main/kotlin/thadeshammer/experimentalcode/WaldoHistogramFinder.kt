package thadeshammer.experimentalcode

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.openimaj.feature.DoubleFVComparison
import org.openimaj.image.MBFImage
import org.openimaj.image.pixel.statistics.HistogramModel
import org.openimaj.math.statistics.distribution.Histogram
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
    fun scanImage(image: MBFImage) {
        val sampleBuffer = MBFImage(sampleWidth, sampleHeight) // no concern for colorspace?

        var scoreList = listOf<ScoreKeeper>()
        var mutex = Mutex()

        var lastY = image.height

        val durationTotalTimeMS = measureTimeMillis {
            for (y in 0 until image.height-sampleHeight step 4) runBlocking {
                for (x in 0 until image.width-sampleWidth step 4) {
                    image.extractROI(x, y, sampleBuffer)

                    val hm = HistogramModel(
                        histogramDimensions.first,
                        histogramDimensions.second,
                        histogramDimensions.third
                    )
                    hm.estimateModel(sampleBuffer)
                    val sampleHistogram = hm.histogram

                    // TODO parameterize compare mode
                    val score = modelHistogram.compare(sampleHistogram, DoubleFVComparison.EUCLIDEAN)

                    mutex.withLock {
                        scoreList += ScoreKeeper(score, x, y, sampleWidth, sampleHeight)
//                        if (y % 100 == 0 && lastY != y) {
//                            println("$y")
//                            lastY = y
//                        }
                    }
                }
            }
        }

        val trimmedScores = WaldoUtil.thoroughTrimScoreList(scoreList)

        val durationStr = String.format(
            "%d min, %d sec",
            TimeUnit.MILLISECONDS.toMinutes(durationTotalTimeMS),
            TimeUnit.MILLISECONDS.toSeconds(durationTotalTimeMS) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationTotalTimeMS))
        )

        println ("Took $durationStr")
        for (i in 0..20) {
            println("$i: ${trimmedScores[i]}")
        }
    }
}