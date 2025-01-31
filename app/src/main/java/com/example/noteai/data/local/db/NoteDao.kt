import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.noteai.data.local.model.NoteDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    fun getAllNotes(): Flow<List<NoteDbModel>>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getNoteById(noteId: Int): NoteDbModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavourite(noteDbModel: NoteDbModel)

    @Update
    suspend fun updateNote(noteDbModel: NoteDbModel)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: Int)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun removeFromFavourite(noteId: Int)

    @Query("SELECT * FROM notes WHERE isFavourite = 1")
    fun getFavouriteNotes(): Flow<List<NoteDbModel>>

    @Query("SELECT EXISTS (SELECT * FROM notes WHERE id = :noteId AND isFavourite = 1)")
    fun observeIsFavourite(noteId: Int): Flow<Boolean>
}


