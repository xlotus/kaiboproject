package cn.cibn.kaibo.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SearchHistoryDao {
    @Query("SELECT * from 'search_history'")
    List<SearchHistory> getAll();

    @Insert
    void insert(SearchHistory history);

    @Delete
    void delete(SearchHistory history);

    @Query("delete from 'search_history'")
    void clear();

    @Query("SELECT count(*) from search_history where history=:key")
    int existCount(String key);
}
