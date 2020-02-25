package info.twentysixproject.kamiraen.dataclass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inbox_table")
data class Inbox(
    @PrimaryKey @ColumnInfo(name="id") val id: String,
    @ColumnInfo (name="categorize") val catgorize: String,
    @ColumnInfo (name="dateCreated") val dateCreated: String,
    @ColumnInfo (name="dateValid") val dateValid: String?,
    @ColumnInfo (name="header") val header: String,
    @ColumnInfo (name="img") val img: String?
)