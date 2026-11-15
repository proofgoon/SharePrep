package com.shareprep

import android.app.Application
import com.shareprep.data.ImageStorage
import com.shareprep.data.Repository
import com.shareprep.data.db.AppDatabase

class SharePrepApp : Application() {
    val repository: Repository by lazy {
        Repository(AppDatabase.build(this), ImageStorage(this))
    }
}
