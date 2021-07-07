package com.rex.lifetracker.RoomDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.PersonalInfo_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SIM_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.deleteContactsCacheModel
import com.rex.lifetracker.utils.Converters

@Database(
    entities = [SOSContacts_Entity::class,
        PersonalInfo_Entity::class,
        SIM_Entity::class,
        deleteContactsCacheModel::class], version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LocalDataBase : RoomDatabase() {

    abstract fun userContactsDao(): LocalDataBaseDao

    companion object {

        @Volatile
        private var INSTANCE: LocalDataBase? = null

        fun getDatabase(context: Context): LocalDataBase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDataBase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }
}