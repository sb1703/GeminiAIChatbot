import com.darkrockstudios.libraries.mpfilepicker.MPFile

//actual class ByteArrayFactory {
//
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