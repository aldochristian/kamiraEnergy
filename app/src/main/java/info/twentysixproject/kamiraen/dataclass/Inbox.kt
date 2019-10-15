package info.twentysixproject.kamiraen.dataclass

data class Inbox(
    val id: String,
    val catgorize: String,
    val dateCreated: String,
    val dateValid: String?,
    val header: String,
    val img: String?
)