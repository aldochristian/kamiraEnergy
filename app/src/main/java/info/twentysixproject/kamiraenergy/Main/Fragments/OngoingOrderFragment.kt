package info.twentysixproject.kamiraenergy.Main.Fragments

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import info.twentysixproject.kamiraenergy.R
import info.twentysixproject.kamiraenergy.adapter.OnGoingOrderListener
import info.twentysixproject.kamiraenergy.adapter.OrderAdapter
import info.twentysixproject.kamiraenergy.databinding.OngoingOrderFragmentBinding
import info.twentysixproject.kamiraenergy.viewmodel.OngoingOrderViewModel


class OngoingOrderFragment : Fragment() {

    val TAG: String = "OngoingOrderFragment"

    private lateinit var viewModel: OngoingOrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get a reference to the binding object and inflate the fragment views.
        val binding: OngoingOrderFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.ongoing_order_fragment, container, false)

        viewModel = ViewModelProviders.of(this).get(OngoingOrderViewModel::class.java)

        binding.ongoingOrderViewModel = viewModel

        val adapter = OrderAdapter(OnGoingOrderListener {
            viewModel.onCancelClicked(it)
        })
        binding.ongoingOrderRv.adapter = adapter

        viewModel.fetchFirestore()
        viewModel.getOnGoingOrder().observe(viewLifecycleOwner, Observer {
            it.let {
                adapter.submitList(it)
            }
        })

        var idToBeCancelled: String? = null
        viewModel.navigateToItem.observe(viewLifecycleOwner, Observer {
            if(idToBeCancelled != it){
                dialogForCancel(it)
                idToBeCancelled = it
            }
        })

        binding.lifecycleOwner = this

        return binding.root
    }

    fun dialogForCancel(id: String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirm for cancellation")
        builder.setMessage("Are you sure for cancellation ?")
        builder.setPositiveButton(
            R.string.OK,
            DialogInterface.OnClickListener { _, _ ->
                Log.d(TAG, "Here we are fo cancel "+id)
                viewModel.updateToCancel(id)
            })
        builder.setNegativeButton(R.string.CANCEL, null)
        builder.show()

    }


}
