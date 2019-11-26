package thadeshammer.experimentalcode

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.openimaj.image.MBFImage
import thadeshammer.model.Model
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

/**
 * thade created on 11/24/2019
 */
class WaldoFinderTerrible(
    val model: Model
) {
    fun scanImage(image: MBFImage) {
        /*
            Stupidest way possible, just one thread and throwing one single core at it full-time.

            Because the model is pixel-for-pixel (no bluring/rotation/transformations) this will
            only clock Waldo himself I think

            I imagine this will result in a pretty large data structure. let's go every other
         */

        // 1. take a sample from the target image (same dimensions as the model)
        // 2. get score and stick it in a sorted list (ascending)
        // 3. spit this list out (at least the top twenty or so)

        val sampleWidth = model.width()
        val sampleHeight = model.height()
        val sampleBuffer = MBFImage(sampleWidth, sampleHeight) // no concern for colorspace?

        var scoreList = listOf<ScoreKeeper>()

        var lastY = image.height

        val durationMS = measureTimeMillis {
            for (y in 0 until image.height-sampleHeight step 2) {
                for (x in 0 until image.width-sampleWidth step 2) {
                    image.extractROI(x, y, sampleBuffer)
                    val score = model.naiveCompare(sampleBuffer)
                    scoreList += ScoreKeeper(score, x, y, sampleWidth, sampleHeight)

                    if (y % 100 == 0 && lastY != y) {
                        println("$y")
                        lastY = y
                    }
                }
            }
        }

        val sortedScores = scoreList.sortedBy { it.score }

        val durationStr = String.format("%d min, %d sec",
            TimeUnit.MILLISECONDS.toMinutes(durationMS),
            TimeUnit.MILLISECONDS.toSeconds(durationMS) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMS))
        )

        println ("Took $durationStr")
        for (i in 0..20) {
            println("$i: ${sortedScores[i]}")
        }
    }

    fun scanImageConcurrent(image: MBFImage) {
        /*
            Stupidest way possible.

            Because the model is pixel-for-pixel (no bluring/rotation/transformations) this will
            only clock Waldo himself if it lands smack on him; classical over-fitting.

            I imagine this will result in a pretty large data structure. let's go every other
         */

        // 1. take a sample from the target image (same dimensions as the model)
        // 2. get score and stick it in a sorted list (ascending)
        // 3. spit this list out (at least the top twenty or so)

        val sampleWidth = model.width()
        val sampleHeight = model.height()
        val sampleBuffer = MBFImage(sampleWidth, sampleHeight) // no concern for colorspace?

        var scoreList = listOf<ScoreKeeper>()
        var mutex = Mutex()

        val durationTotalTimeMS = measureTimeMillis {
            for (y in 0 until image.height-sampleHeight step 4) runBlocking {
                for (x in 0 until image.width-sampleWidth step 4) {
                    image.extractROI(x, y, sampleBuffer)
                    val score = model.naiveCompare(sampleBuffer)

                    mutex.withLock {
                        scoreList += ScoreKeeper(score, x, y, sampleWidth, sampleHeight)
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