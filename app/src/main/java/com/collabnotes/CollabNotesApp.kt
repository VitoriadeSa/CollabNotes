package com.collabnotes

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings

class CollabNotesApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Cache local (offline) explícita. Tem de ser definida ANTES de qualquer outro uso do Firestore.
        // Com a cache ativa, leituras funcionam sem rede e as escritas ficam em fila,
        // sendo enviadas automaticamente quando a ligação volta.
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(
                PersistentCacheSettings.newBuilder()
                    .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build()
            )
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }
}
