package thadeshammer.experimentalcode

import org.openimaj.image.DisplayUtilities
import org.openimaj.image.MBFImage
import org.openimaj.image.processor.PixelProcessor
import thadeshammer.model.Model
import thadeshammer.model.ModelComponent
import kotlin.math.abs

/**
 * thade created on 11/23/2019
 */
class playWithExperimentalModel(
    val waldoSourceImage: MBFImage
) {
    companion object{
        val RED = 0
        val GREEN = 1
        val BLUE = 2

        val LOWER_BOUND = 0.2f
    }

    val allWhite = MBFImage(waldoSourceImage.width, waldoSourceImage.height)
        .fill(arrayOf(1.0f, 1.0f, 1.0f))



    fun bias(value: Float, limit: Float): Float {
        return if (value < limit) {
            0.0f
        } else {
            1.0f
        }
    }

    fun tooClose(value: Float, other: Float, limit: Float): Boolean {
        return (abs(value - other) <= limit)
    }

    fun doIt() {

        val maxOutIfAboveThreshold = PixelProcessor<Float> {
            if (it >= LOWER_BOUND) {
                1.0f
            } else {
                0.0f
            }
        }

        var waldoModel1 = waldoSourceImage.clone()
        waldoModel1.getBand(GREEN).fill(0f) // ignore green

//        waldoModel.getBand(RED).process(maxOutIfAboveThreshold)  // max red
//        waldoModel.getBand(BLUE).process(maxOutIfAboveThreshold) // max blue

        waldoModel1.processInplace(PixelProcessor {
            if (it[RED] == it[BLUE]) {
                it[RED] = 0.0f
                it[BLUE] = 0.0f
            } else if (it[RED] > it[BLUE]) {
                it[BLUE] = 0.0f
            } else {
                it[RED] = 0.0f
            }

            it
        })

        val waldoModel2 = waldoSourceImage.clone()
            .processInplace(PixelProcessor {
                if (it[RED] == it[BLUE]) {
                    it[RED] = 0.0f
                    it[BLUE] = 0.0f
                } else {
                    it[RED] = bias(it[RED], 0.2f)
                    it[BLUE] = bias(it[BLUE], 0.2f)
                }

                it[GREEN] = 0.0f

                it
            })
//            .processInplace(
//                ImageProcessor {
//                    for (y in 0..it.height) {
//                        for (x in 0..it.width) {
//
//                        }
//                    }
//            })

        val waldoRed = waldoSourceImage.clone()
            .processInplace(PixelProcessor {
                if (tooClose(it[RED], it[BLUE], 0.3f)) {
                    it[RED] = 0.0f
                } else {
                    it[RED] = bias(it[RED], 0.95f)
                }

                it[GREEN] = 0.0f
                it[BLUE] = 0.0f

                it
            })

        val waldoBlue = waldoSourceImage.clone()
            .processInplace(PixelProcessor {
                if (tooClose(it[BLUE], it[RED], 0.3f)) {
                    it[BLUE] = 0.0f
                } else {
                    it[BLUE] = bias(it[BLUE], 0.7f)
                }

                it[RED] = 0.0f
                it[GREEN] = 0.0f

                it
            })

        val waldoRB = waldoBlue.add(waldoRed)


        DisplayUtilities.display(waldoSourceImage)
//        DisplayUtilities.display(waldoModel1)
//        DisplayUtilities.display(waldoModel2)
        DisplayUtilities.display(waldoRed)
//        DisplayUtilities.display(waldoBlue)

        DisplayUtilities.display(waldoSourceImage.subtract(waldoRed))
//        DisplayUtilities.display(waldoSourceImage.subtract(waldoBlue))


        println("red from red score: ${waldoRed.subtract(waldoRed).doublePixelVector.sum()}")

        val scoreForRaw =
            waldoSourceImage
                .subtract(waldoRed)
                .subtract(waldoBlue)
                .doublePixelVector
                .sum()

        println("full model from raw image: $scoreForRaw")

        val scoreForRB = waldoRed
            .add(waldoBlue)
            .subtract(waldoRed)
            .subtract(waldoBlue)
            .doublePixelVector
            .sum()

        println("full model from full model: $scoreForRB")

        DisplayUtilities.display(allWhite)

        val scoreForWhite = allWhite
            .subtract(waldoRed)
            .subtract(waldoBlue)
            .doublePixelVector
            .sum()

        println("full model vs all white: $scoreForWhite")
    }

    fun doItBetter() {
        val waldoRedComponent = ModelComponent(
            name = "waldoRedComponent",
            originalImage = waldoSourceImage,
            pixelTransforms = listOf(
                PixelProcessor {
                    if (tooClose(it[RED], it[BLUE], 0.3f)) {
                        it[RED] = 0.0f
                    } else {
                        it[RED] = bias(it[RED], 0.95f)
                    }

                    it[GREEN] = 0.0f
                    it[BLUE] = 0.0f

                    it
                }
            ),
            imageTransforms = listOf()
        )

        val waldoBlueComponent = ModelComponent(
            name = "waldoRedComponent",
            originalImage = waldoSourceImage,
            pixelTransforms = listOf(
                PixelProcessor {
                    if (tooClose(it[BLUE], it[RED], 0.3f)) {
                        it[BLUE] = 0.0f
                    } else {
                        it[BLUE] = bias(it[BLUE], 0.7f)
                    }

                    it[RED] = 0.0f
                    it[GREEN] = 0.0f

                    it
                }
            ),
            imageTransforms = listOf()
        )

        val model = Model(
            name = "waldoModel1",
            components = listOf(
                waldoRedComponent,
                waldoBlueComponent
            )
        )

        println("model vs model: ${model.compare(model)}")
        println("model vs original: ${model.compare(waldoSourceImage)}")
        println("model vs white: ${model.compare(allWhite)}")

    }
}