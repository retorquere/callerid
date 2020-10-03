package re.retorque.corpcallerid.models

import androidx.room.*
import android.telephony.PhoneNumberUtils
import java.util.*

@Entity(tableName = "user_table")
data class User(
    @ColumnInfo(name = "display_name") var displayName: String,
    @ColumnInfo(name = "phone_number") var phoneNumber: String,
    @ColumnInfo(name = "phone_type") var phoneType: String
) {
    constructor(): this("", "","")
    @PrimaryKey var id: String = UUID.randomUUID().toString()
    @Ignore var prettyPrint: String = "${phoneType}: $phoneNumber"
    @Ignore var phoneLabel: String = "Corporate Caller ID | ${phoneType}"

    fun hasPhone(phoneNumber: String): Boolean = PhoneNumberUtils.compare(this.phoneNumber, phoneNumber)

    override fun equals(other: Any?): Boolean {
        val that = other as? User ?: return false
        return this.displayName == that.displayName
                && this.phoneNumber == that.phoneNumber
                && this.phoneType == that.phoneType
    }

    override fun hashCode(): Int = super.hashCode()
}
