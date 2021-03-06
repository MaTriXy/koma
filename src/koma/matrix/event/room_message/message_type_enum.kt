package matrix.event.room_message

import com.serjltt.moshi.adapters.FallbackEnum
import com.squareup.moshi.Json

@FallbackEnum(name = "Unrecognized")
enum class RoomEventType{
    @Json(name = "m.room.aliases") Aliases,
    @Json(name = "m.room.canonical_alias") CanonAlias,
    @Json(name = "m.room.create") Create,
    @Json(name = "m.room.join_rules") JoinRule,
    @Json(name = "m.room.power_levels") PowerLevels,
    @Json(name = "m.room.member") Member,
    @Json(name = "m.room.message") Message,
    @Json(name = "m.room.redaction") Redaction,

    @Json(name = "m.room.name") Name,
    @Json(name = "m.room.topic") Topic,
    @Json(name = "m.room.avatar") Avatar,

    @Json(name = "m.room.history_visibility") HistoryVisibility,

    @Json(name = "m.room.guest_access") GuestAccess,
    Unrecognized,
}
