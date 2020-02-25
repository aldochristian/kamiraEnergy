package info.twentysixproject.kamiraen.Main.Fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import info.twentysixproject.kamiraen.R
import info.twentysixproject.kamiraen.viewmodel.DetailOrderViewModel


class DetailOrderFragment : Fragment() {

    companion object {
        fun newInstance() = DetailOrderFragment()
    }

    private lateinit var viewModel: DetailOrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detail_order_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DetailOrderViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
