package com.serwylo.lexica

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import io.github.tonnyl.whatsnew.WhatsNew
import io.github.tonnyl.whatsnew.item.WhatsNewItem

object Changelog {

    @JvmStatic
    fun show(activity: AppCompatActivity) {

        val whatsNew = WhatsNew.newInstance(
                WhatsNewItem(
                        activity.getString(R.string.whats_new_game_modes),
                        activity.getString(R.string.whats_new_game_modes_description),
                        WhatsNewItem.NO_IMAGE_RES_ID
                ),
                WhatsNewItem(
                        activity.getString(R.string.whats_new_why),
                        activity.getString(R.string.whats_new_game_modes_why),
                        WhatsNewItem.NO_IMAGE_RES_ID
                ),
        )

        with(whatsNew) {
            titleText = activity.getText(R.string.whats_new_title)
            buttonText = activity.getText(R.string.whats_new_continue).toString()
        }

        // Don't show this for new installs. The best way to tell is to see if there has been any
        // high scores saved (if so, it is probably an upgrade, and if not, then they likely haven't
        // played enough to care about any changes).
        //
        // In the future, this check can change (e.g. handled by the WhatsNew library, see https://github.com/TonnyL/WhatsNew/issues/21).
        // For all future changelogs, we can replace the code above with a different changelog, and
        // change this check to look for "LAST_VERSION_CODE" in the default shared prefs (again,
        // see issue linked above).
        if (activity.getSharedPreferences("prefs_score_file", Context.MODE_PRIVATE).all.isNotEmpty()) {
            whatsNew.presentAutomatically(activity)
        }

    }

}