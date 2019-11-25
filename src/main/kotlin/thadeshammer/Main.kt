package thadeshammer

import org.openimaj.image.ImageUtilities
import org.openimaj.image.MBFImage
import org.openimaj.image.processor.PixelProcessor
import thadeshammer.experimentalcode.ModelBuildingUtil.bias
import thadeshammer.experimentalcode.ModelBuildingUtil.tooClose
import thadeshammer.experimentalcode.WaldoFinder
import thadeshammer.experimentalcode.ModelBuildingUtil
import thadeshammer.model.Model
import thadeshammer.model.ModelComponent
import java.io.File

fun main(args: Array<String>) {
    println("Hello, World")

    var waldoSourceImage: MBFImage = ImageUtilities.readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\wm2.bmp"))

//    var pixel = image.getPixel(0,0)
//
//    println("pixel size: ${pixel.size}")
//    println("pixel data: ${pixel.asList()}")

    //    DisplayUtilities.display(image)

//    val finder = playWithExperimentalModel(waldoModelSourceImage)
//    finder.doItBetter()

    val waldoRedComponent = ModelComponent(
        name = "waldoRedComponent",
        originalImage = waldoSourceImage,
        pixelTransforms = listOf(
            PixelProcessor {
                if (tooClose(it[ModelBuildingUtil.RED],
                    it[ModelBuildingUtil.BLUE],
                    0.3f)) {
                    it[ModelBuildingUtil.RED] = 0.0f
                } else {
                    it[ModelBuildingUtil.RED] = bias(it[ModelBuildingUtil.RED], 0.95f)
                }

                it[ModelBuildingUtil.GREEN] = 0.0f
                it[ModelBuildingUtil.BLUE] = 0.0f

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
                if (tooClose(it[ModelBuildingUtil.BLUE],
                    it[ModelBuildingUtil.RED],
                    0.3f)) {
                    it[ModelBuildingUtil.BLUE] = 0.0f
                } else {
                    it[ModelBuildingUtil.BLUE] = bias(it[ModelBuildingUtil.BLUE], 0.7f)
                }

                it[ModelBuildingUtil.RED] = 0.0f
                it[ModelBuildingUtil.GREEN] = 0.0f

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

    val finder = WaldoFinder(model)
    finder.scanImage(
        ImageUtilities.readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\waldo-7.bmp"))
    )

    /*
        TODO figure out proportions of waldo in images (i.e. find waldo and boundbox him
        what is ratio of bound box dimensions to full image dimensions...scale model to each
        image? build a scaled image and work from there
     */

    /*
        first target image is source of model
        "C:\Users\thade\Downloads\waldo\waldo-7.bmp"
     */

}

