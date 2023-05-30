package diamondcraftdevs.startups.speakingApp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import diamondcraftdevs.startups.speakingApp.R
import diamondcraftdevs.startups.speakingApp.adapter.HolderAdapter
import diamondcraftdevs.startups.speakingApp.const.StringExtractor.holderList
import diamondcraftdevs.startups.speakingApp.const.StringExtractor.holderList2
import diamondcraftdevs.startups.speakingApp.const.StringExtractor.holderList3
import diamondcraftdevs.startups.speakingApp.databinding.FragmentHolderBinding


class FragmentHolder : Fragment() {
private lateinit var binding: FragmentHolderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHolderBinding.inflate(inflater,container,false)
        val view: View =binding.root
        view.apply {
            val appContext = requireContext() as AppCompatActivity
            binding.holderRecycler.layoutManager = LinearLayoutManager(requireContext())
            when (appContext.findViewById<TextView>(R.id.holder_name).text) {
                "Part-1" -> {
                    binding.holderRecycler.adapter = HolderAdapter(
                        requireContext(),
                        holderList,
                        "part1"
                    )
                }
                "Part-2" -> binding.holderRecycler.adapter = HolderAdapter(requireContext(), holderList2,"part2")
                "Part-3" -> binding.holderRecycler.adapter = HolderAdapter(requireContext(), holderList2,"part3")
                "Material" -> binding.holderRecycler.adapter = HolderAdapter(requireContext(), holderList3,"Material")
            }
        }
        return view
    }


}