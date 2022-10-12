package com.serwylo.lexica.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import com.google.zxing.client.android.encode.QRCodeEncoder
import com.serwylo.lexica.activities.NewMultiplayerActivity
import com.serwylo.lexica.game.Game
import com.serwylo.lexica.share.SharedGameData
import kotlin.math.min

class QrCodeBinder(val context: Context, val resources: Resources, val game: Game) {
    val uri: Uri
    val webUri: Uri

    val appBitmap: Bitmap
    val webBitmap: Bitmap

    init {
        val board = mutableListOf<String>()
        for (i in 0 until game.board.size) {
            board.add(game.board.elementAt(i))
        }

        val sharedGameData = SharedGameData(board, game.language, game.gameMode, SharedGameData.Type.MULTIPLAYER)
        uri = sharedGameData.serialize(SharedGameData.Platform.ANDROID)
        val metrics = resources.displayMetrics
        val size = min(metrics.widthPixels, metrics.heightPixels)
        appBitmap = QRCodeEncoder.encodeAsBitmap(uri.toString(), size)

        webUri = sharedGameData.serialize(SharedGameData.Platform.WEB)
        webBitmap = QRCodeEncoder.encodeAsBitmap(webUri.toString(), size)

        Log.d(NewMultiplayerActivity.TAG, "Preparing multiplayer game: $uri")
    }

    fun bindUI(qr: ImageView, toggleQr: SwitchCompat) {
        qr.setImageBitmap(appBitmap)
        toggleQr.isEnabled = true
        toggleQr.isChecked = false

        toggleQr.setOnCheckedChangeListener { _, isChecked -> when (isChecked) {
            true -> qr.setImageBitmap(webBitmap)
            false -> qr.setImageBitmap(appBitmap)
        } }
    }
}