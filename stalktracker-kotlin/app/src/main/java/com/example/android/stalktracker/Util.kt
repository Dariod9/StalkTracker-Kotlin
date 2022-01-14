package com.example.android.stalktracker

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Util {
    class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)
    class ButtonItemViewHolder(val button: Button): RecyclerView.ViewHolder(button)
}