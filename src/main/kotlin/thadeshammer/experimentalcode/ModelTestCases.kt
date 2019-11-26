package thadeshammer.experimentalcode

import org.openimaj.image.MBFImage
import org.openimaj.image.processing.convolution.FFastGaussianConvolve
import org.openimaj.math.geometry.shape.Rectangle
import thadeshammer.model.Model
import thadeshammer.model.ModelComponent

/**
 * thade created on 11/25/2019
 */
object ModelTestCases {
    fun trivialCasesModelVsModel(sourceImage: MBFImage, targetImage: MBFImage) {
        // Trivial case
        println("=== TRIVIAL CASE model vs model ===")
//        Took 0 min, 0 sec
//        0: ScoreKeeper(score=0.0, x=0, y=0, width=102, height=188)
        WaldoUtil.runHistogramScan(
            sourceImage,
            sourceImage,
            Triple(6, 6, 3)
        )

        println("=== TRIVIAL CASE blurred model vs model ===")
//        Took 0 min, 0 sec
//        0: ScoreKeeper(score=0.12571360738742135, x=0, y=0, width=102, height=188)
        WaldoUtil.runHistogramScan(
            sourceImage
                .clone()
                .processInplace(
                    FFastGaussianConvolve(1f, 3)
                ),
            sourceImage,
            Triple(6, 6, 3)
        )

        println("=== TRIVIAL CASE small target image ===")
        WaldoUtil.runHistogramScan(
            sourceImage
                .clone()
                .processInplace(
                    FFastGaussianConvolve(1f, 3)
                ),
            targetImage
                .extractROI(854, 560, 500, 500),
            Triple(6, 6, 1),
            1
        )
    }

    fun naiveRedBlueComponentCases(sourceImage: MBFImage, targetImage: MBFImage) {

        val waldoRedComponent = ModelComponent(
            name = "waldoRedComponent",
            originalImage = sourceImage,
            pixelTransforms = listOf(
                WaldoUtil.redComponentPPv1
            ),
            imageTransforms = listOf()
        )

        val waldoBlueComponent = ModelComponent(
            name = "waldoRedComponent",
            originalImage = sourceImage,
            pixelTransforms = listOf(
                WaldoUtil.blueComponentPPv1
            ),
            imageTransforms = listOf()
        )

        val modelRB = Model(
            name = "waldoModel1",
            components = listOf(
                waldoRedComponent,
                waldoBlueComponent
            )
        )
        println("=== VS RB COMPONENT TRANSFORMED IMAGE ===")
        /*
            20: ScoreKeeper(score=0.33025041543450273, x=832, y=564, width=102, height=188)
         */
        val redImage = targetImage
            .processInplace(WaldoUtil.redComponentPPv1)
        val blueImage = targetImage
            .processInplace(WaldoUtil.blueComponentPPv1)
        val componentTransformImage = redImage.subtract(blueImage).abs()

        WaldoUtil.runHistogramScan(
            modelRB.fullModel,
            componentTransformImage,
            Triple(8, 8, 4),
            4,
            Rectangle(855f, 563f, 102f, 188f)
        )
    }

    fun histogramVsRawImageTests(waldoSourceImage: MBFImage, waldoTargetImage: MBFImage) {
        println("=== VS RAW-GEN OVERFIT HISTOGRAM ===")
        /*
            16: ScoreKeeper(score=0.5593389518050591, x=832, y=564, width=102, height=188)
        */
        WaldoUtil.runHistogramScan(
            waldoSourceImage,
            waldoTargetImage,
            Triple(waldoSourceImage.width, waldoSourceImage.height, 3)
        )

        println("=== VS RAW-GEN CANONICAL-STEVE HISTOGRAM depth 3 ===")
        WaldoUtil.runHistogramScan(
            waldoSourceImage,
            waldoTargetImage,
            Triple(4, 4, 3)
        )

        println("=== VS RAW-GEN CANONICAL-STEVE HISTOGRAM depth 4 ===")
        WaldoUtil.runHistogramScan(
            waldoSourceImage,
            waldoTargetImage,
            Triple(4, 4, 4)
        )

        // Use Gaussian for a slightly "wider net"
        println("=== VS WITH GAUSSIAN RAW IMAGE HISTOGRAM ===")
        val waldoSourceBlurred = waldoSourceImage.clone()
            .processInplace(
                FFastGaussianConvolve(1f, 3)
            )
        val waldoTargetBlurred = waldoTargetImage.clone()
            .processInplace(
                FFastGaussianConvolve(1f, 3)
            )
        WaldoUtil.runHistogramScan(
            waldoSourceBlurred,
            waldoTargetBlurred,
            Triple(6, 6, 3)
        )
    }
}