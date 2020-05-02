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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_view.view.*
import kotlinx.android.synthetic.main.card_view.view.card
import kotlinx.android.synthetic.main.card_view.view.event_img
import kotlinx.android.synthetic.main.card_view.view.event_title
import kotlinx.android.synthetic.main.saved_card.view.*
import kotlinx.android.synthetic.main.wide_card.view.*
import kotlinx.android.synthetic.main.wide_card.view.backersNum
import kotlinx.android.synthetic.main.wide_card.view.currentDonated
import kotlinx.android.synthetic.main.wide_card.view.daysLeft
import kotlinx.android.synthetic.main.wide_card.view.eventGoal
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.roundToInt


class SavedAdapter (
    private val data: List<SavedObject>
) : RecyclerView.Adapter<SavedAdapter.ViewHolder>() {

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
            .inflate(R.layout.saved_card, parent, false)

        return ViewHolder(v)
    }

    //show information in card
    override fun onBindViewHolder(holder: SavedAdapter.ViewHolder, position: Int) {
        //show title
        holder.eventTitle.text = data[position].title

        //show img
        val img = data[position].img
        holder.card.setCardBackgroundColor(Color.parseColor(data[position].palette))
        Picasso.get().load(img).into(holder.eventImage)

        //ProgressBar
        val divide = data[position].goal?.let { data[position].current?.div(it) }
        val calProgress = divide?.times(100)
        if (calProgress != null) {
            holder.progressBar.setProgress(calProgress.roundToInt())
        }

        //show current, goal, backernum
        holder.currentDonated.text = data[position].current.toString()
        holder.eventGoal.text = data[position].goal.toString()
        holder.backersNum.text = data[position].backer.toString()

        //calculate days left && show
        val nowDate = LocalDateTime.now()
        val endDate = data[position].endDate?.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }
        val daysBetween: Long = ChronoUnit.DAYS.between(nowDate, endDate)
        holder.daysLeft.text = daysBetween.toString()

        //click to details page
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
        val progressBar: ProgressBar = itemView.savedProgressBar
        val currentDonated: TextView = itemView.currentDonated
        val eventGoal: TextView = itemView.eventGoal
        val backersNum: TextView = itemView.backersNum
        val daysLeft: TextView = itemView.daysLeft
    }
}