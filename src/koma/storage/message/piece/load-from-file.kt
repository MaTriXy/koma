package koma.storage.message.piece

import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import koma.matrix.UserIdAdapter
import koma.matrix.event.parse
import koma.matrix.room.naming.RoomId
import koma.matrix.sync.RawMessage
import koma.storage.config.config_paths
import java.io.File

val discussion_dir = config_paths.getOrCreate("discussion", create = true)

fun disc_save_path(vararg paths: String): String? = config_paths.getOrCreate(
        *paths, base = discussion_dir
)

fun loadStoredDiscussion(roomId: RoomId): List<DiscussionPiece> {
    val dir = disc_save_path(
            roomId.servername,
            roomId.localstr)
    dir ?: return listOf()
    val f = File(dir)
    return f.walk().filter { !it.isDirectory }.mapNotNull { it.loadDiscussion() }.toList()
}

private fun File.loadDiscussion(): DiscussionPiece? {
    val time = this.name.toLongOrNull()
    time?: return null
    var following: String? = null
    val moshi = Moshi.Builder().add(UserIdAdapter()).build()
    val adapter = moshi.adapter(RawMessage::class.java)
    val messages = this.readLines().mapNotNull {
        if (it.startsWith("#")) {
            if (it.startsWith("# following_event ")) {
                following = it.substringAfter("# following_event ").trim()
            }
            null
        } else {
            try {
                adapter.fromJson(it)?.parse()
            } catch (e: JsonEncodingException) {
                println("failed to load line $it")
                return@mapNotNull null
            }
        }
    }.toMutableList()
    val res = DiscussionPiece(
            messages = messages, timekey = time)
    res.following_event = following
    res.filename = this.absolutePath
    res.savedHash = res.contentHash()
    return res
}
