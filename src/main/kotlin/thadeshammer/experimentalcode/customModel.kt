package thadeshammer.experimentalcode

import org.openimaj.image.FImage
import org.openimaj.image.MBFImage
import org.openimaj.image.processor.PixelProcessor

/**
 * thade created on 11/23/2019
 */
data class customModel(
    val originalImage: MBFImage,
    val transforms: List<PixelProcessor<Array<Float>>> // ??
) {
    val transformedImage: MBFImage = originalImage.clone()

    init {
        if (transforms.isNotEmpty()) {
            transforms.forEach {
                transformedImage.processInplace(it)
            }
        }
    }
}