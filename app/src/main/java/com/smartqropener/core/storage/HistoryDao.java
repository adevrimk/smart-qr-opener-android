package com.smartqropener.core.storage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
import kotlinx.coroutines.flow.Flow;

@Dao
public interface HistoryDao {
    @Insert
    long insert(HistoryEntity item);

    @Query("SELECT * FROM history_items ORDER BY createdAt DESC")
    Flow<List<HistoryEntity>> observeAll();

    @Query("UPDATE history_items SET isFavorite = :favorite WHERE id = :id")
    void updateFavorite(long id, boolean favorite);

    @Delete
    void delete(HistoryEntity item);

    @Query("DELETE FROM history_items WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM history_items")
    void clearAll();
}
