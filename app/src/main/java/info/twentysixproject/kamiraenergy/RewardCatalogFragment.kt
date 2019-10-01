package info.twentysixproject.kamiraenergy

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import info.twentysixproject.kamiraenergy.viewmodel.RewardCatalogViewModel


class RewardCatalogFragment : Fragment() {

    companion object {
        fun newInstance() = RewardCatalogFragment()
    }

    private lateinit var viewModel: RewardCatalogViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.reward_catalog_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RewardCatalogViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
