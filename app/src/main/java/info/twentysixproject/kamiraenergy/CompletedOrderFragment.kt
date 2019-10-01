package info.twentysixproject.kamiraenergy

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import info.twentysixproject.kamiraenergy.adapter.CompleteOrderAdapter
import info.twentysixproject.kamiraenergy.adapter.CompleteOrderListener
import info.twentysixproject.kamiraenergy.databinding.CompletedOrderFragmentBinding
import info.twentysixproject.kamiraenergy.viewmodel.CompletedOrderViewModel


class CompletedOrderFragment : Fragment() {

    private lateinit var viewModel: CompletedOrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: CompletedOrderFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.completed_order_fragment, container, false)

        viewModel = ViewModelProviders.of(this).get(CompletedOrderViewModel::class.java)

        binding.completeOrderViewModel = viewModel

        val adapter = CompleteOrderAdapter()

        binding.completedOrderRv.adapter = adapter

        viewModel.fetchFirestore()
        viewModel.getCompleteOrder().observe(viewLifecycleOwner, Observer {
            it.let {
                adapter.submitList(it)
            }
        })

        binding.setLifecycleOwner(this)

        return binding.root
    }

}
