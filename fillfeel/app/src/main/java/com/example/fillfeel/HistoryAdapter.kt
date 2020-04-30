package com.example.fillfeel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_view.view.*
import kotlinx.android.synthetic.main.card_view.view.card
import kotlinx.android.synthetic.main.card_view.view.event_img
import kotlinx.android.synthetic.main.card_view.view.event_title
import kotlinx.android.synthetic.main.wide_card.view.*
import java.security.AccessController.getContext


class HistoryAdapter (
    private val data: List<ExploreObject>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    @Throws(IllegalArgumentException::class)
    fun convert(base64Str: String): Bitmap? {
        val decodedBytes: ByteArray = Base64.decode(
            base64Str.substring(base64Str.indexOf(",") + 1),
            Base64.DEFAULT
        )
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    private val items: MutableList<CardView>

    init {
        this.items = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.wide_card, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        holder.eventTitle.text = data[position].title
        val img = data[position].img
        holder.card.setCardBackgroundColor(Color.parseColor(data[position].paletteImage))
        Picasso.get().load(img).into(holder.eventImage)

        holder.itemView.setOnClickListener{view ->
            val activity = view.context as AppCompatActivity
            val fragment = DetailsFragment()

            val bundle = Bundle()
            bundle.putString("eventId", data[position].id.toString())
            fragment.setArguments(bundle)

            fragment.arguments = bundle

            activity.getSupportFragmentManager()
                .beginTransaction().replace(R.id.root_layout, fragment).addToBackStack(null).commit();
        }

        items.add(holder.card)
    }

    override fun getItemCount(): Int { return data.size }

    inner class ViewHolder
    internal constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val eventTitle: TextView = itemView.event_title
        val eventImage: ImageView = itemView.event_img
        val card: CardView = itemView.card
        val currentDonated: TextView = itemView.currentDonated
        val eventGoal: TextView = itemView.eventGoal
        val backersNum: TextView = itemView.backersNum
        val daysLeft: TextView = itemView.daysLeft
    }
}