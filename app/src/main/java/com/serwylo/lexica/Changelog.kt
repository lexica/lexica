package com.serwylo.lexica

import androidx.appcompat.app.AppCompatActivity
import io.github.tonnyl.whatsnew.WhatsNew
import io.github.tonnyl.whatsnew.item.WhatsNewItem
import io.github.tonnyl.whatsnew.util.PresentationOption

object Changelog {

    @JvmStatic
    fun show(activity: AppCompatActivity) {

        val whatsNew = WhatsNew.newInstance(
                WhatsNewItem(
                        activity.getString(R.string.whats_new_multiplayer),
                        activity.getString(R.string.whats_new_multiplayer_description),
                        R.drawable.ic_people,
                ),
                WhatsNewItem(
                        activity.getString(R.string.whats_new_support),
                        activity.getText(R.string.whats_new_support_description),
                        R.drawable.ic_support
                ),
        )

        with(whatsNew) {
            titleText = activity.getText(R.string.whats_new_title)
            buttonText = activity.getText(R.string.whats_new_continue).toString()
        }

        whatsNew.presentAutomatically(activity)

    }

}