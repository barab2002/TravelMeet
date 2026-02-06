package com.travelmeet.app.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.travelmeet.app.data.local.entity.SpotEntity

@Dao
interface SpotDao {

    @Query("SELECT * FROM spots ORDER BY timestamp DESC")
    fun getAllSpots(): LiveData<List<SpotEntity>>

    @Query("SELECT * FROM spots WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSpotsByUser(userId: String): LiveData<List<SpotEntity>>

    @Query("SELECT * FROM spots WHERE id = :spotId")
    fun getSpotById(spotId: String): LiveData<SpotEntity?>

    @Query("SELECT * FROM spots WHERE id = :spotId")
    suspend fun getSpotByIdSync(spotId: String): SpotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpot(spot: SpotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpots(spots: List<SpotEntity>)

    @Update
    suspend fun updateSpot(spot: SpotEntity)

    @Query("DELETE FROM spots WHERE id = :spotId")
    suspend fun deleteSpotById(spotId: String)

    @Query("DELETE FROM spots")
    suspend fun deleteAll()
}
