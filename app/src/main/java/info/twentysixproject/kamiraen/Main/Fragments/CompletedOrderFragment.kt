package info.twentysixproject.kamiraen.Main.Fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import info.twentysixproject.kamiraen.R
import info.twentysixproject.kamiraen.adapter.CompleteOrderAdapter
import info.twentysixproject.kamiraen.databinding.CompletedOrderFragmentBinding
import info.twentysixproject.kamiraen.viewmodel.CompletedOrderViewModel


class CompletedOrderFragment : Fragment() {

    private lateinit var viewModel: CompletedOrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: CompletedOrderFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.completed_order_fragment, container, false)

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

        binding.lifecycleOwner = this

        return binding.root
    }

}
