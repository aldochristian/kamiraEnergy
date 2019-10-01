package info.twentysixproject.kamiraenergy


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import info.twentysixproject.kamiraenergy.adapter.RewardsCatalogAdapter
import info.twentysixproject.kamiraenergy.adapter.RewardsCatalogListener
import info.twentysixproject.kamiraenergy.databinding.FragmentPointsBinding
import info.twentysixproject.kamiraenergy.viewmodel.PointsViewModel
import java.util.zip.Inflater

/**
 * A simple [Fragment] subclass.
 *
 */
class PointsFragment : Fragment() {

    private lateinit var viewModel: PointsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentPointsBinding = DataBindingUtil.inflate(
            inflater,  R.layout.fragment_points, container, false)

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.title = "Points Reward"
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        viewModel = ViewModelProviders.of(this).get(PointsViewModel::class.java)
        binding.model = viewModel

        binding.frpointsQrscan.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.pointstoqrscannerfr_action, null)
        )

        viewModel.fetchFirestore()

        val adapter = RewardsCatalogAdapter(RewardsCatalogListener{

        })
        binding.pointsfrCatalogRv.adapter = adapter

        binding.setLifecycleOwner(this)

        return binding.root
    }


}
