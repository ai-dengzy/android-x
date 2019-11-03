package com.example.eletronicengineer.ViewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eletronicengineer.R
import com.example.eletronicengineer.aninterface.Movie


class MovieViewHolder(inflater: LayoutInflater, vg: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_dispaly_demand_two, vg, false)) {

    private var reqirementMajor: TextView? = null
    private var projectSite: TextView? = null
    var requirementVariety=""
    var mId:String = ""

    init {
        reqirementMajor = itemView.findViewById(R.id.tv_display_demand_team_content1)
        projectSite = itemView.findViewById(R.id.tv_demand_demand_team_content2)
    }

    fun bind(movie: Movie) {
        reqirementMajor?.text = movie.requirementMajor
        projectSite?.text = movie.projectSite
        mId = movie.id
        requirementVariety=movie.reqiurementVariety
    }
}