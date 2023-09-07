package cn.cibn.kaibo.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VideoHistoryDao {
    @Query("SELECT * from 'video_history' order by id DESC")
    List<VideoHistory> getAll();

    @Insert
    void insert(VideoHistory history);

    @Delete
    void delete(VideoHistory history);

    @Query("delete from 'video_history'")
    void clear();

    @Query("delete from 'video_history' where video_id=:videoId")
    void deleteByVideoId(String videoId);
}
