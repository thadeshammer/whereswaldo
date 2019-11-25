package thadeshammer.experimentalcode

import org.openimaj.image.MBFImage
import thadeshammer.model.Model
import kotlin.system.measureTimeMillis

/**
 * thade created on 11/24/2019
 */
class WaldoFinder(
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
            for (y in image.height downTo sampleHeight step 2) { // brace for off-by-one error
                for (x in image.width downTo sampleWidth step 2) {
                    image.extractROI(x, y, sampleBuffer)
                    val score = model.compare(sampleBuffer)
                    scoreList += ScoreKeeper(score, x, y)

                    if (y % 100 == 0 && lastY != y) {
                        println("$y")
                        lastY = y
                    }
                }
            }
        }

        val sortedScores = scoreList.sortedBy { it.score }

        println ("Took $durationMS ms.")
        for (i in 0..20) {
            println("$i: ${sortedScores[i]}")
        }
    }
}