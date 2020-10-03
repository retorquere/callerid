package re.retorque.corpcallerid.callerid

import  android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.ContactsContract.Directory
import android.provider.ContactsContract.PhoneLookup
import re.retorque.corpcallerid.R
import re.retorque.corpcallerid.db.UserDatabase
import re.retorque.corpcallerid.db.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class CallerIDProvider : ContentProvider() {

    private var userRepository: UserRepository? = null

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    private lateinit var authorityUri: Uri

    override fun onCreate(): Boolean {
        context?.let {
            val userDao = UserDatabase.getDatabase(it).userDao()
            userRepository = UserRepository(userDao)
            val authority = it.getString(R.string.callerid_authority)
            authorityUri = Uri.parse("content://$authority")

            uriMatcher.apply {
                addURI(authority, "directories", DIRECTORIES)
                addURI(authority, "phone_lookup/*", PHONE_LOOKUP)
                addURI(authority, PRIMARY_PHOTO_URI, PRIMARY_PHOTO)
            }
        }
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        when (uriMatcher.match(uri)) {
            DIRECTORIES -> {
                val label = context?.getString(R.string.app_name) ?: return null
                val cursor = MatrixCursor(projection)
                projection?.map { column ->
                    when (column) {
                        Directory.ACCOUNT_NAME,
                        Directory.ACCOUNT_TYPE,
                        Directory.DISPLAY_NAME -> label
                        Directory.TYPE_RESOURCE_ID -> R.string.app_name
                        Directory.EXPORT_SUPPORT -> Directory.EXPORT_SUPPORT_SAME_ACCOUNT_ONLY
                        Directory.SHORTCUT_SUPPORT -> Directory.SHORTCUT_SUPPORT_NONE
                        else -> null
                    }
                }?.let { cursor.addRow(it) }
                return cursor
            }
            PHONE_LOOKUP -> {
                userRepository?.let { userRepo ->
                    val phoneNumber = uri.pathSegments[1]
                    val cursor = MatrixCursor(projection)
                    val user = runBlocking(Dispatchers.IO) { userRepo.getUser(phoneNumber) }
                    user?.let { u ->
                        projection?.map { column ->
                            when (column) {
                                PhoneLookup._ID -> -1
                                PhoneLookup.DISPLAY_NAME -> u.fullName
                                PhoneLookup.LABEL -> u.phoneLabel
                                PhoneLookup.PHOTO_THUMBNAIL_URI,
                                PhoneLookup.PHOTO_URI -> Uri.withAppendedPath(authorityUri, PRIMARY_PHOTO_URI)
                                else -> null
                            }
                        }?.let { cursor.addRow(it) }
                    }
                    return cursor
                }
            }
        }
        return null
    }

    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
        return when(uriMatcher.match(uri)) {
            PRIMARY_PHOTO -> context?.resources?.openRawResourceFd(R.raw.phineas)
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException()
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException()
    }

    companion object {
        private const val DIRECTORIES = 1
        private const val PHONE_LOOKUP = 2
        private const val PRIMARY_PHOTO = 3

        private const val PRIMARY_PHOTO_URI = "photo/primary_photo"
    }
}
