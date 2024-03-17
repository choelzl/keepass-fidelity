package net.helcel.fidelity.activity.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import net.helcel.fidelity.databinding.ListItemFidelityBinding

class FidelityListAdapter(
    private val triples: ArrayList<Triple<String?, String?, String?>>,
    private val onItemClicked: (Triple<String?, String?, String?>) -> Unit
) :
    RecyclerView.Adapter<FidelityListAdapter.FidelityViewHolder>() {

    private lateinit var binding: ListItemFidelityBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FidelityViewHolder {
        binding = ListItemFidelityBinding.inflate(LayoutInflater.from(parent.context))
        binding.root.setLayoutParams(
            LinearLayout.LayoutParams(
                MATCH_PARENT, WRAP_CONTENT
            )
        )
        return FidelityViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: FidelityViewHolder, position: Int) {
        val triple = triples[position]
        holder.bind(triple)
    }

    override fun getItemCount(): Int = triples.size

    inner class FidelityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bind(triple: Triple<String?, String?, String?>) {
            val text = "${triple.first}"
            binding.textView.text = text
            binding.card.setOnClickListener { onItemClicked(triple) }
        }


    }

}
