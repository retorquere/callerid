package re.retorque.corpcallerid.db

import androidx.lifecycle.LiveData
import androidx.room.*
import re.retorque.corpcallerid.models.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM user_table")
    suspend fun getUsersList(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("DELETE FROM user_table")
    suspend fun deleteAll()
}