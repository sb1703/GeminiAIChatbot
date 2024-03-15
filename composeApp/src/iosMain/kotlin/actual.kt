//import com.darkrockstudios.libraries.mpfilepicker.MPFile
//import kotlinx.cinterop.ExperimentalForeignApi
//import kotlinx.cinterop.addressOf
//import kotlinx.cinterop.usePinned
//
//
//actual class ByteArrayFactory {
//
//    @OptIn(ExperimentalForeignApi::class)
//    actual suspend fun getByteArray(mpFile: MPFile<Any>): ByteArray? {
//        val file = mpFile.platformFile as NSURL
//        val data: NSData? = NSData.dataWithContentOfURL(file)
//        return data?.let { nsData ->
//            ByteArray(nsData.length.toInt()).apply {
//                usePinned {
//                    memcpy(it.addressOf(0), nsData.bytes, nsData.length)
//                }
//            }
//        }
//    }
//}