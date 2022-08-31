package com.aectann.ifan.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aectann.ifan.FactActivity
import com.aectann.ifan.R
import com.aectann.ifan.models.Fact

class FactsAdapter (private val facts: List<Fact>? = null)
    : RecyclerView.Adapter<FactsAdapter.FactsAdapterViewHolder>() {

    class FactsAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(fact: Fact) {
            val factName: String? = fact.fact
            val TVFact: TextView = itemView.findViewById(R.id.TVFact)
            TVFact.text = factName
            val context: Context = TVFact.context

            itemView.setOnClickListener {
                val i = Intent(context, FactActivity::class.java)
                i.putExtra("EXTRA_FACT", factName)
                ContextCompat.startActivity(context, i, null)
            }
        }

        companion object {
            fun from(parent: ViewGroup) : FactsAdapterViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val itemView = layoutInflater.inflate(R.layout.recycle_item_fact,
                    parent, false)
                return FactsAdapterViewHolder(itemView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactsAdapterViewHolder {
        return FactsAdapterViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FactsAdapterViewHolder, position: Int) {
        holder.bind(facts!![position])
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return facts!!.size
    }

}