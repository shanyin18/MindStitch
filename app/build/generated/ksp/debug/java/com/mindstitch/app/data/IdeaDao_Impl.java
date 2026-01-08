package com.mindstitch.app.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IdeaDao_Impl implements IdeaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IdeaEntity> __insertionAdapterOfIdeaEntity;

  private final EntityDeletionOrUpdateAdapter<IdeaEntity> __deletionAdapterOfIdeaEntity;

  private final EntityDeletionOrUpdateAdapter<IdeaEntity> __updateAdapterOfIdeaEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public IdeaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIdeaEntity = new EntityInsertionAdapter<IdeaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ideas` (`id`,`title`,`contentBlocks`,`type`,`tags`,`folder`,`rating`,`upCount`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IdeaEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getContentBlocks());
        statement.bindString(4, entity.getType());
        statement.bindString(5, entity.getTags());
        statement.bindString(6, entity.getFolder());
        statement.bindLong(7, entity.getRating());
        statement.bindLong(8, entity.getUpCount());
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfIdeaEntity = new EntityDeletionOrUpdateAdapter<IdeaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `ideas` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IdeaEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfIdeaEntity = new EntityDeletionOrUpdateAdapter<IdeaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `ideas` SET `id` = ?,`title` = ?,`contentBlocks` = ?,`type` = ?,`tags` = ?,`folder` = ?,`rating` = ?,`upCount` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IdeaEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getContentBlocks());
        statement.bindString(4, entity.getType());
        statement.bindString(5, entity.getTags());
        statement.bindString(6, entity.getFolder());
        statement.bindLong(7, entity.getRating());
        statement.bindLong(8, entity.getUpCount());
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM ideas WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final IdeaEntity idea, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfIdeaEntity.insertAndReturnId(idea);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final IdeaEntity idea, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfIdeaEntity.handle(idea);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final IdeaEntity idea, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfIdeaEntity.handle(idea);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<IdeaEntity>> getAllIdeas() {
    final String _sql = "SELECT * FROM ideas ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ideas"}, new Callable<List<IdeaEntity>>() {
      @Override
      @NonNull
      public List<IdeaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContentBlocks = CursorUtil.getColumnIndexOrThrow(_cursor, "contentBlocks");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfFolder = CursorUtil.getColumnIndexOrThrow(_cursor, "folder");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfUpCount = CursorUtil.getColumnIndexOrThrow(_cursor, "upCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<IdeaEntity> _result = new ArrayList<IdeaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IdeaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContentBlocks;
            _tmpContentBlocks = _cursor.getString(_cursorIndexOfContentBlocks);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpFolder;
            _tmpFolder = _cursor.getString(_cursorIndexOfFolder);
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpUpCount;
            _tmpUpCount = _cursor.getInt(_cursorIndexOfUpCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new IdeaEntity(_tmpId,_tmpTitle,_tmpContentBlocks,_tmpType,_tmpTags,_tmpFolder,_tmpRating,_tmpUpCount,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<IdeaEntity>> searchIdeas(final String query) {
    final String _sql = "SELECT * FROM ideas WHERE title LIKE '%' || ? || '%' OR contentBlocks LIKE '%' || ? || '%' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ideas"}, new Callable<List<IdeaEntity>>() {
      @Override
      @NonNull
      public List<IdeaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContentBlocks = CursorUtil.getColumnIndexOrThrow(_cursor, "contentBlocks");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfFolder = CursorUtil.getColumnIndexOrThrow(_cursor, "folder");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfUpCount = CursorUtil.getColumnIndexOrThrow(_cursor, "upCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<IdeaEntity> _result = new ArrayList<IdeaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IdeaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContentBlocks;
            _tmpContentBlocks = _cursor.getString(_cursorIndexOfContentBlocks);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpFolder;
            _tmpFolder = _cursor.getString(_cursorIndexOfFolder);
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpUpCount;
            _tmpUpCount = _cursor.getInt(_cursorIndexOfUpCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new IdeaEntity(_tmpId,_tmpTitle,_tmpContentBlocks,_tmpType,_tmpTags,_tmpFolder,_tmpRating,_tmpUpCount,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getIdeaById(final long id, final Continuation<? super IdeaEntity> $completion) {
    final String _sql = "SELECT * FROM ideas WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IdeaEntity>() {
      @Override
      @Nullable
      public IdeaEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContentBlocks = CursorUtil.getColumnIndexOrThrow(_cursor, "contentBlocks");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfFolder = CursorUtil.getColumnIndexOrThrow(_cursor, "folder");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfUpCount = CursorUtil.getColumnIndexOrThrow(_cursor, "upCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final IdeaEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContentBlocks;
            _tmpContentBlocks = _cursor.getString(_cursorIndexOfContentBlocks);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpFolder;
            _tmpFolder = _cursor.getString(_cursorIndexOfFolder);
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpUpCount;
            _tmpUpCount = _cursor.getInt(_cursorIndexOfUpCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new IdeaEntity(_tmpId,_tmpTitle,_tmpContentBlocks,_tmpType,_tmpTags,_tmpFolder,_tmpRating,_tmpUpCount,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<String>> getFolders() {
    final String _sql = "SELECT DISTINCT folder FROM ideas ORDER BY folder ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ideas"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<IdeaEntity>> getIdeasByFolder(final String folder) {
    final String _sql = "SELECT * FROM ideas WHERE folder = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, folder);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ideas"}, new Callable<List<IdeaEntity>>() {
      @Override
      @NonNull
      public List<IdeaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContentBlocks = CursorUtil.getColumnIndexOrThrow(_cursor, "contentBlocks");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfFolder = CursorUtil.getColumnIndexOrThrow(_cursor, "folder");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfUpCount = CursorUtil.getColumnIndexOrThrow(_cursor, "upCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<IdeaEntity> _result = new ArrayList<IdeaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IdeaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContentBlocks;
            _tmpContentBlocks = _cursor.getString(_cursorIndexOfContentBlocks);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpFolder;
            _tmpFolder = _cursor.getString(_cursorIndexOfFolder);
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpUpCount;
            _tmpUpCount = _cursor.getInt(_cursorIndexOfUpCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new IdeaEntity(_tmpId,_tmpTitle,_tmpContentBlocks,_tmpType,_tmpTags,_tmpFolder,_tmpRating,_tmpUpCount,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getIdeasSince(final long startTime,
      final Continuation<? super List<IdeaEntity>> $completion) {
    final String _sql = "SELECT * FROM ideas WHERE createdAt >= ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IdeaEntity>>() {
      @Override
      @NonNull
      public List<IdeaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContentBlocks = CursorUtil.getColumnIndexOrThrow(_cursor, "contentBlocks");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfFolder = CursorUtil.getColumnIndexOrThrow(_cursor, "folder");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfUpCount = CursorUtil.getColumnIndexOrThrow(_cursor, "upCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<IdeaEntity> _result = new ArrayList<IdeaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IdeaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContentBlocks;
            _tmpContentBlocks = _cursor.getString(_cursorIndexOfContentBlocks);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpFolder;
            _tmpFolder = _cursor.getString(_cursorIndexOfFolder);
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpUpCount;
            _tmpUpCount = _cursor.getInt(_cursorIndexOfUpCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new IdeaEntity(_tmpId,_tmpTitle,_tmpContentBlocks,_tmpType,_tmpTags,_tmpFolder,_tmpRating,_tmpUpCount,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getIdeasByDateRange(final long startTime, final long endTime,
      final Continuation<? super List<IdeaEntity>> $completion) {
    final String _sql = "SELECT * FROM ideas WHERE createdAt >= ? AND createdAt < ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IdeaEntity>>() {
      @Override
      @NonNull
      public List<IdeaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContentBlocks = CursorUtil.getColumnIndexOrThrow(_cursor, "contentBlocks");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfFolder = CursorUtil.getColumnIndexOrThrow(_cursor, "folder");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfUpCount = CursorUtil.getColumnIndexOrThrow(_cursor, "upCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<IdeaEntity> _result = new ArrayList<IdeaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IdeaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContentBlocks;
            _tmpContentBlocks = _cursor.getString(_cursorIndexOfContentBlocks);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final String _tmpFolder;
            _tmpFolder = _cursor.getString(_cursorIndexOfFolder);
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final int _tmpUpCount;
            _tmpUpCount = _cursor.getInt(_cursorIndexOfUpCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new IdeaEntity(_tmpId,_tmpTitle,_tmpContentBlocks,_tmpType,_tmpTags,_tmpFolder,_tmpRating,_tmpUpCount,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
