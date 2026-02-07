package com.travelmeet.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.travelmeet.app.data.local.entity.SpotEntity;
import java.lang.Class;
import java.lang.Exception;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SpotDao_Impl implements SpotDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SpotEntity> __insertionAdapterOfSpotEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<SpotEntity> __updateAdapterOfSpotEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSpotById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public SpotDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSpotEntity = new EntityInsertionAdapter<SpotEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `spots` (`id`,`userId`,`username`,`userPhotoUrl`,`title`,`imageUrls`,`description`,`latitude`,`longitude`,`locationName`,`timestamp`,`likesCount`,`isLikedByCurrentUser`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, SpotEntity value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getUserId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getUserId());
        }
        if (value.getUsername() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getUsername());
        }
        if (value.getUserPhotoUrl() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getUserPhotoUrl());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getTitle());
        }
        final String _tmp = __converters.fromList(value.getImageUrls());
        if (_tmp == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, _tmp);
        }
        if (value.getDescription() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getDescription());
        }
        stmt.bindDouble(8, value.getLatitude());
        stmt.bindDouble(9, value.getLongitude());
        if (value.getLocationName() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getLocationName());
        }
        stmt.bindLong(11, value.getTimestamp());
        stmt.bindLong(12, value.getLikesCount());
        final int _tmp_1 = value.isLikedByCurrentUser() ? 1 : 0;
        stmt.bindLong(13, _tmp_1);
      }
    };
    this.__updateAdapterOfSpotEntity = new EntityDeletionOrUpdateAdapter<SpotEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `spots` SET `id` = ?,`userId` = ?,`username` = ?,`userPhotoUrl` = ?,`title` = ?,`imageUrls` = ?,`description` = ?,`latitude` = ?,`longitude` = ?,`locationName` = ?,`timestamp` = ?,`likesCount` = ?,`isLikedByCurrentUser` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, SpotEntity value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getUserId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getUserId());
        }
        if (value.getUsername() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getUsername());
        }
        if (value.getUserPhotoUrl() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getUserPhotoUrl());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getTitle());
        }
        final String _tmp = __converters.fromList(value.getImageUrls());
        if (_tmp == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, _tmp);
        }
        if (value.getDescription() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getDescription());
        }
        stmt.bindDouble(8, value.getLatitude());
        stmt.bindDouble(9, value.getLongitude());
        if (value.getLocationName() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getLocationName());
        }
        stmt.bindLong(11, value.getTimestamp());
        stmt.bindLong(12, value.getLikesCount());
        final int _tmp_1 = value.isLikedByCurrentUser() ? 1 : 0;
        stmt.bindLong(13, _tmp_1);
        if (value.getId() == null) {
          stmt.bindNull(14);
        } else {
          stmt.bindString(14, value.getId());
        }
      }
    };
    this.__preparedStmtOfDeleteSpotById = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM spots WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM spots";
        return _query;
      }
    };
  }

  @Override
  public Object insertSpot(final SpotEntity spot, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSpotEntity.insert(spot);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object insertSpots(final List<SpotEntity> spots,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSpotEntity.insert(spots);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object updateSpot(final SpotEntity spot, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSpotEntity.handle(spot);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteSpotById(final String spotId, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSpotById.acquire();
        int _argIndex = 1;
        if (spotId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, spotId);
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteSpotById.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public LiveData<List<SpotEntity>> getAllSpots() {
    final String _sql = "SELECT * FROM spots ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"spots"}, false, new Callable<List<SpotEntity>>() {
      @Override
      public List<SpotEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfUserPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "userPhotoUrl");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfImageUrls = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrls");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfLocationName = CursorUtil.getColumnIndexOrThrow(_cursor, "locationName");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfLikesCount = CursorUtil.getColumnIndexOrThrow(_cursor, "likesCount");
          final int _cursorIndexOfIsLikedByCurrentUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isLikedByCurrentUser");
          final List<SpotEntity> _result = new ArrayList<SpotEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final SpotEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final String _tmpUsername;
            if (_cursor.isNull(_cursorIndexOfUsername)) {
              _tmpUsername = null;
            } else {
              _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            }
            final String _tmpUserPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfUserPhotoUrl)) {
              _tmpUserPhotoUrl = null;
            } else {
              _tmpUserPhotoUrl = _cursor.getString(_cursorIndexOfUserPhotoUrl);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final List<String> _tmpImageUrls;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfImageUrls)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfImageUrls);
            }
            _tmpImageUrls = __converters.fromString(_tmp);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final String _tmpLocationName;
            if (_cursor.isNull(_cursorIndexOfLocationName)) {
              _tmpLocationName = null;
            } else {
              _tmpLocationName = _cursor.getString(_cursorIndexOfLocationName);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpLikesCount;
            _tmpLikesCount = _cursor.getInt(_cursorIndexOfLikesCount);
            final boolean _tmpIsLikedByCurrentUser;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsLikedByCurrentUser);
            _tmpIsLikedByCurrentUser = _tmp_1 != 0;
            _item = new SpotEntity(_tmpId,_tmpUserId,_tmpUsername,_tmpUserPhotoUrl,_tmpTitle,_tmpImageUrls,_tmpDescription,_tmpLatitude,_tmpLongitude,_tmpLocationName,_tmpTimestamp,_tmpLikesCount,_tmpIsLikedByCurrentUser);
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
  public LiveData<List<SpotEntity>> getSpotsByUser(final String userId) {
    final String _sql = "SELECT * FROM spots WHERE userId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"spots"}, false, new Callable<List<SpotEntity>>() {
      @Override
      public List<SpotEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfUserPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "userPhotoUrl");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfImageUrls = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrls");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfLocationName = CursorUtil.getColumnIndexOrThrow(_cursor, "locationName");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfLikesCount = CursorUtil.getColumnIndexOrThrow(_cursor, "likesCount");
          final int _cursorIndexOfIsLikedByCurrentUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isLikedByCurrentUser");
          final List<SpotEntity> _result = new ArrayList<SpotEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final SpotEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final String _tmpUsername;
            if (_cursor.isNull(_cursorIndexOfUsername)) {
              _tmpUsername = null;
            } else {
              _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            }
            final String _tmpUserPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfUserPhotoUrl)) {
              _tmpUserPhotoUrl = null;
            } else {
              _tmpUserPhotoUrl = _cursor.getString(_cursorIndexOfUserPhotoUrl);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final List<String> _tmpImageUrls;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfImageUrls)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfImageUrls);
            }
            _tmpImageUrls = __converters.fromString(_tmp);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final String _tmpLocationName;
            if (_cursor.isNull(_cursorIndexOfLocationName)) {
              _tmpLocationName = null;
            } else {
              _tmpLocationName = _cursor.getString(_cursorIndexOfLocationName);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpLikesCount;
            _tmpLikesCount = _cursor.getInt(_cursorIndexOfLikesCount);
            final boolean _tmpIsLikedByCurrentUser;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsLikedByCurrentUser);
            _tmpIsLikedByCurrentUser = _tmp_1 != 0;
            _item = new SpotEntity(_tmpId,_tmpUserId,_tmpUsername,_tmpUserPhotoUrl,_tmpTitle,_tmpImageUrls,_tmpDescription,_tmpLatitude,_tmpLongitude,_tmpLocationName,_tmpTimestamp,_tmpLikesCount,_tmpIsLikedByCurrentUser);
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
  public LiveData<SpotEntity> getSpotById(final String spotId) {
    final String _sql = "SELECT * FROM spots WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (spotId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, spotId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"spots"}, false, new Callable<SpotEntity>() {
      @Override
      public SpotEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfUserPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "userPhotoUrl");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfImageUrls = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrls");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfLocationName = CursorUtil.getColumnIndexOrThrow(_cursor, "locationName");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfLikesCount = CursorUtil.getColumnIndexOrThrow(_cursor, "likesCount");
          final int _cursorIndexOfIsLikedByCurrentUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isLikedByCurrentUser");
          final SpotEntity _result;
          if(_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final String _tmpUsername;
            if (_cursor.isNull(_cursorIndexOfUsername)) {
              _tmpUsername = null;
            } else {
              _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            }
            final String _tmpUserPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfUserPhotoUrl)) {
              _tmpUserPhotoUrl = null;
            } else {
              _tmpUserPhotoUrl = _cursor.getString(_cursorIndexOfUserPhotoUrl);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final List<String> _tmpImageUrls;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfImageUrls)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfImageUrls);
            }
            _tmpImageUrls = __converters.fromString(_tmp);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final String _tmpLocationName;
            if (_cursor.isNull(_cursorIndexOfLocationName)) {
              _tmpLocationName = null;
            } else {
              _tmpLocationName = _cursor.getString(_cursorIndexOfLocationName);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpLikesCount;
            _tmpLikesCount = _cursor.getInt(_cursorIndexOfLikesCount);
            final boolean _tmpIsLikedByCurrentUser;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsLikedByCurrentUser);
            _tmpIsLikedByCurrentUser = _tmp_1 != 0;
            _result = new SpotEntity(_tmpId,_tmpUserId,_tmpUsername,_tmpUserPhotoUrl,_tmpTitle,_tmpImageUrls,_tmpDescription,_tmpLatitude,_tmpLongitude,_tmpLocationName,_tmpTimestamp,_tmpLikesCount,_tmpIsLikedByCurrentUser);
          } else {
            _result = null;
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
  public Object getSpotByIdSync(final String spotId,
      final Continuation<? super SpotEntity> continuation) {
    final String _sql = "SELECT * FROM spots WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (spotId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, spotId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SpotEntity>() {
      @Override
      public SpotEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfUserPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "userPhotoUrl");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfImageUrls = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrls");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfLocationName = CursorUtil.getColumnIndexOrThrow(_cursor, "locationName");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfLikesCount = CursorUtil.getColumnIndexOrThrow(_cursor, "likesCount");
          final int _cursorIndexOfIsLikedByCurrentUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isLikedByCurrentUser");
          final SpotEntity _result;
          if(_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final String _tmpUsername;
            if (_cursor.isNull(_cursorIndexOfUsername)) {
              _tmpUsername = null;
            } else {
              _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            }
            final String _tmpUserPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfUserPhotoUrl)) {
              _tmpUserPhotoUrl = null;
            } else {
              _tmpUserPhotoUrl = _cursor.getString(_cursorIndexOfUserPhotoUrl);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final List<String> _tmpImageUrls;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfImageUrls)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfImageUrls);
            }
            _tmpImageUrls = __converters.fromString(_tmp);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final String _tmpLocationName;
            if (_cursor.isNull(_cursorIndexOfLocationName)) {
              _tmpLocationName = null;
            } else {
              _tmpLocationName = _cursor.getString(_cursorIndexOfLocationName);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpLikesCount;
            _tmpLikesCount = _cursor.getInt(_cursorIndexOfLikesCount);
            final boolean _tmpIsLikedByCurrentUser;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsLikedByCurrentUser);
            _tmpIsLikedByCurrentUser = _tmp_1 != 0;
            _result = new SpotEntity(_tmpId,_tmpUserId,_tmpUsername,_tmpUserPhotoUrl,_tmpTitle,_tmpImageUrls,_tmpDescription,_tmpLatitude,_tmpLongitude,_tmpLocationName,_tmpTimestamp,_tmpLikesCount,_tmpIsLikedByCurrentUser);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
