package com.smartqropener.core.storage;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history_items")
public class HistoryEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String rawValue;
    public String normalizedValue;
    public String type;
    public String source;
    public long createdAt;
    public boolean isFavorite;
    public boolean isSuspicious;
    public String displayText;

    public HistoryEntity(long id,
                         String rawValue,
                         String normalizedValue,
                         String type,
                         String source,
                         long createdAt,
                         boolean isFavorite,
                         boolean isSuspicious,
                         String displayText) {
        this.id = id;
        this.rawValue = rawValue;
        this.normalizedValue = normalizedValue;
        this.type = type;
        this.source = source;
        this.createdAt = createdAt;
        this.isFavorite = isFavorite;
        this.isSuspicious = isSuspicious;
        this.displayText = displayText;
    }
}

