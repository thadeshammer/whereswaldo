package thadeshammer.model

import org.openimaj.image.MBFImage
import org.openimaj.image.processor.ImageProcessor
import org.openimaj.image.processor.PixelProcessor

/**
 * thade created on 11/23/2019
 */
data class ModelComponent (
    val name: String,
    val originalImage: MBFImage,
    val pixelTransforms: List<PixelProcessor<Array<Float>>>,
    val imageTransforms: List<ImageProcessor<MBFImage>>
){
    var componentImage: MBFImage = originalImage.clone()

    init {
        pixelTransforms.forEach {
            componentImage.processInplace(it)
        }

        imageTransforms.forEach {
            componentImage.processInplace(it)
        }
    }
}