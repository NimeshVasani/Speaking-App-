package diamondcraftdevs.startups.speakingApp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import diamondcraftdevs.startups.speakingApp.R
import diamondcraftdevs.startups.speakingApp.SpeakingTestActivity
import diamondcraftdevs.startups.speakingApp.databinding.HolderItemsBinding

class HolderAdapter(
    var context: Context,
    private var itemList: MutableList<String>,
    private var partName: String
) :
    RecyclerView.Adapter<HolderAdapter.MyOwnHolder>() {
    private lateinit var binding: HolderItemsBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOwnHolder {
        binding = HolderItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val view: View = binding.root
        return MyOwnHolder(view)
    }

    override fun onBindViewHolder(holder: MyOwnHolder, position: Int) {
        holder.tvItems.apply {
            text = (position + 1).toString() + ". " + itemList[position]
            if (partName != "Material") {
                setOnClickListener {
                    context.startActivity(
                        Intent(
                            context,
                            SpeakingTestActivity::class.java
                        ).putExtra("item_data", position).putExtra("item_name", itemList[position])
                            .putExtra("part_name", partName)
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class MyOwnHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvItems: TextView = itemView.findViewById(R.id.tv_items)
    }
}