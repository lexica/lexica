package com.serwylo.lexica

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import io.github.tonnyl.whatsnew.WhatsNew
import io.github.tonnyl.whatsnew.item.WhatsNewItem
import io.github.tonnyl.whatsnew.util.PresentationOption

object Changelog {

    @JvmStatic
    fun show(activity: AppCompatActivity) {

        val whatsNew = buildDialog(activity)

        // Only show when upgrading Lexica for the first time, not when we first open Lexica from
        // a fresh install.
        whatsNew.presentationOption = if (isFirstRun(activity)) PresentationOption.NEVER else PresentationOption.IF_NEEDED
        whatsNew.presentAutomatically(activity)

        rememberLexicaHasRun(activity)

    }

    private fun buildDialog(context: Context): WhatsNew {

        return WhatsNew.newInstance(
            WhatsNewItem(
                context.getString(R.string.whats_new_web_lexica),
                context.getText(R.string.whats_new_web_lexica_description),
                R.drawable.ic_people,
            ),
            WhatsNewItem(
                context.getString(R.string.whats_new_sharing),
                context.getText(R.string.whats_new_sharing_description),
                R.drawable.ic_people,
            ),
            WhatsNewItem(
                context.getString(R.string.whats_new_support),
                context.getText(R.string.whats_new_support_description),
                R.drawable.ic_support
            ),
        ).apply {
            titleText = context.getText(R.string.whats_new_title)
            buttonText = context.getText(R.string.whats_new_continue).toString()
        }

    }

    private fun isFirstRun(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return !prefs.getBoolean(HAS_RUN, false)
    }

    private fun rememberLexicaHasRun(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(HAS_RUN, true)
            .apply()
    }

    private const val HAS_RUN = "lexica-has-run-before"

}