package diamondcraftdevs.startups.speakingApp.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import diamondcraftdevs.startups.speakingApp.R
import diamondcraftdevs.startups.speakingApp.databinding.FragmentPracticeBinding


class PracticeFragment : Fragment() {
    private lateinit var binding: FragmentPracticeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPracticeBinding.inflate(inflater, container, false)
        val view: View = binding.root
        view.apply {
            val appContext = (requireContext() as AppCompatActivity)
            binding.practicePart1.setOnClickListener {
                appContext.apply {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.practice_fragment, FragmentHolder()).addToBackStack(null)
                        .commit()
                    findViewById<TextView>(R.id.holder_name).text = "Part-1"
                    findViewById<RelativeLayout>(R.id.holder_relative).setBackgroundColor(
                        Color.parseColor(
                            "#dda0dd"
                        )
                    )
                }
            }
            binding.practicePart2.setOnClickListener {
                appContext.apply {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.practice_fragment, FragmentHolder()).addToBackStack(null)
                        .commit()
                    findViewById<TextView>(R.id.holder_name).text = "Part-2"
                    findViewById<RelativeLayout>(R.id.holder_relative).setBackgroundColor(
                        Color.parseColor(
                            "#dda0dd"
                        )
                    )
                }
            }
            binding.practicePart3.setOnClickListener {
                appContext.apply {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.practice_fragment, FragmentHolder()).addToBackStack(null)
                        .commit()
                    findViewById<TextView>(R.id.holder_name).text = "Part-3"
                    findViewById<RelativeLayout>(R.id.holder_relative).setBackgroundColor(
                        Color.parseColor(
                            "#dda0dd"
                        )
                    )
                }
            }
            binding.practiceMaterial.setOnClickListener {
                appContext.apply {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.practice_fragment, FragmentHolder()).addToBackStack(null)
                        .commit()
                    findViewById<TextView>(R.id.holder_name).text = "Material"
                    findViewById<RelativeLayout>(R.id.holder_relative).setBackgroundColor(
                        Color.parseColor(
                            "#dda0dd"
                        )
                    )
                }
            }
        }
        return view
    }


}