package diamondcraftdevs.startups.speakingApp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import diamondcraftdevs.startups.speakingApp.const.InternalStorageRecordFileList
import diamondcraftdevs.startups.speakingApp.databinding.ItemRecordPlayBinding

class RecordedListAdapter(
    var context: Context,
    private var files: List<InternalStorageRecordFileList>
) :
    RecyclerView.Adapter<RecordedListAdapter.MyOwnHolder>() {
    private var fileSize: Int = files.size
    private lateinit var binding: ItemRecordPlayBinding
    var onItemClickListeners: OnItemClickListener? = null
    var onItemDeleteClickListener: OnItemClickListener? = null
    private var onItemShareClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyOwnHolder {
        binding = ItemRecordPlayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val view = binding.root

        return MyOwnHolder(view)
    }

    override fun onBindViewHolder(holder: MyOwnHolder, position: Int) {
        holder.itemSongName.apply {
            text = files[position].fileName
        }
    }

    override fun getItemCount(): Int {
        return fileSize
    }

    inner class MyOwnHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var itemSongName: TextView = binding.itemSongName
        private var itemSongDelete: TextView = binding.musicDelete
        private var itemSongShare: TextView = binding.musicShare

        init {
            itemView.setOnClickListener(this)
            itemSongDelete.setOnClickListener {
                onItemDeleteClickListener?.onItemDeleteClick(adapterPosition, it)
                fileSize -= 1
            }
            itemSongShare.setOnClickListener {
                onItemDeleteClickListener?.onItemShareClick(adapterPosition, it)
            }
        }

        override fun onClick(v: View) {
            onItemClickListeners?.onItemClick(adapterPosition, v)
        }
    }


    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListeners = onItemClickListener
        this.onItemDeleteClickListener = onItemClickListener
        this.onItemShareClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View)
        fun onItemDeleteClick(position: Int, view: View)
        fun onItemShareClick(position: Int, view: View)
    }

}