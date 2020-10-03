package re.retorque.corpcallerid.models

import androidx.room.*
import android.telephony.PhoneNumberUtils
import java.util.*

@Entity(tableName = "user_table")
data class User(
    @ColumnInfo(name = "first_name") var firstName: String,
    @ColumnInfo(name = "last_name") var lastName: String,
    @ColumnInfo(name = "phone_number") var phoneNumber: String,
    @ColumnInfo(name = "phone_type") var phoneType: String
) {
    constructor(): this("", "","","")
    @PrimaryKey var id: String = UUID.randomUUID().toString()
    @Ignore var fullName: String = "$firstName $lastName"
    @Ignore var prettyPrint: String = "${phoneType}: $phoneNumber"
    @Ignore var phoneLabel: String = "Corporate Caller ID App | ${phoneType}"

    fun hasPhone(phoneNumber: String): Boolean = PhoneNumberUtils.compare(this.phoneNumber, phoneNumber)

    override fun equals(other: Any?): Boolean {
        val that = other as? User ?: return false
        return this.firstName == that.firstName
                && this.lastName == that.lastName
                && this.phoneNumber == that.phoneNumber
                && this.phoneType == that.phoneType
    }

    override fun hashCode(): Int = super.hashCode()
}
