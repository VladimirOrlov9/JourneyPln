package com.spbstu.journeypln.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.spbstu.journeypln.R
import com.spbstu.journeypln.data.room.entities.Trip
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class TripsRecyclerAdapter(
    context: Context,
    trips: List<Trip>,
    val onClickListener: (View, Trip) -> Unit,
    val onNoItems: (Int) -> Unit
): RecyclerView.Adapter<TripsRecyclerAdapter.MyViewHolder>() {
    private var mTrips: MutableList<Trip> = trips.toMutableList()
    private val mContext: Context = context


    private val outputDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tripImage: ImageView = view.findViewById(R.id.trip_image)
        val tripName: TextView = view.findViewById(R.id.trip_name)
        val tripDuration: TextView = view.findViewById(R.id.trip_duration)
        val tripInfo: TextView = view.findViewById(R.id.trip_info)
        val tripDestination: TextView = view.findViewById(R.id.trip_destination)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsRecyclerAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_trip, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TripsRecyclerAdapter.MyViewHolder, position: Int) {
        val currentTrip = mTrips[position]

        holder.tripName.text = currentTrip.name

        val duration = "${outputDateFormat.format(currentTrip.startDate)} - ${
            outputDateFormat.format(currentTrip.endDate)
        }"
        holder.tripDuration.text = duration

        holder.tripInfo.text = currentTrip.description
        holder.tripDestination.text = currentTrip.placeName

        Glide.with(mContext)
            .load(currentTrip.imageUri)
            .centerCrop()
            .into(holder.tripImage)
        holder.tripImage.clipToOutline = true

        holder.itemView.setOnClickListener { view ->
            onClickListener.invoke(view, currentTrip)
        }
    }

    override fun getItemCount(): Int {
        return mTrips.size
    }

    fun removeItem(position: Int) {
        mTrips.removeAt(position)
        notifyItemRemoved(position)

        if (mTrips.size == 0)
            onNoItems.invoke(View.VISIBLE)
    }

    fun restoreItem(trip: Trip, position: Int) {
        mTrips.add(position, trip)
        notifyItemInserted(position)

        if (mTrips.size > 0)
            onNoItems.invoke(View.GONE)
    }

    fun getData(): List<Trip?> = mTrips

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        if (mTrips.size == 0)
            onNoItems.invoke(View.VISIBLE)
        else
            onNoItems.invoke(View.GONE)
    }
}