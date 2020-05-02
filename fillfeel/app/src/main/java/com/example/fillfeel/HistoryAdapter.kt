package com.example.fillfeel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.wide_card.view.*
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId


class HistoryAdapter (
    private val data: List<HistoryObject>
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

    //show information in card
    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        //show title
        holder.eventTitle.text = data[position].title

        //show img
        val img = data[position].img
//        holder.card.setCardBackgroundColor(Color.parseColor(data[position].palette))
        holder.card.setCardBackgroundColor(Color.parseColor("#333333"))
        Picasso.get().load(img).into(holder.eventImage)

        //show historyDonateAmount && historyDonateDate
        val getAmount = data[position].amount?.toInt().toString()
        holder.donateAmount.text = "US$ " + getAmount

        val donateDate = data[position].timestamp?.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
        val changeFormat = donateDate.toString().split("-")
        val newFormat = changeFormat[2] + changeFormat[1] + changeFormat[0]
        holder.donateDate.text = "donate on " + newFormat

        //click to details page
        holder.itemView.setOnClickListener{view ->
            val activity = view.context as AppCompatActivity
            val fragment = DetailsFragment()

            val bundle = Bundle()
            bundle.putString("eventId", data[position].eid.toString())
            bundle.putString("eventImg", data[position].img)
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
        val donateAmount: TextView = itemView.historyDonateAmount
        val donateDate: TextView = itemView.historyDonateDate
    }
}