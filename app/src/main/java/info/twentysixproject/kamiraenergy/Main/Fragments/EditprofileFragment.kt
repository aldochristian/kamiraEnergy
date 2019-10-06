package info.twentysixproject.kamiraenergy.Main.Fragments

import android.app.Dialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import info.twentysixproject.kamiraenergy.R
import info.twentysixproject.kamiraenergy.databinding.EditprofileFragmentBinding
import info.twentysixproject.kamiraenergy.viewmodel.EditprofileViewModel


class EditprofileFragment : Fragment() {

    val TAG: String = "EditprofileFragment"

    companion object {
        fun newInstance() = EditprofileFragment()
    }

    private lateinit var viewModel: EditprofileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: EditprofileFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.editprofile_fragment, container, false)

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.title = "Edit Profile"
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }


        viewModel = ViewModelProviders.of(this).get(EditprofileViewModel::class.java)
        binding.model = viewModel

        viewModel.hasLoaded.observe(this, Observer<Boolean> {
            Log.d(TAG, "Value of it "+it)
            if (!it) showProgressBar()
        })

        viewModel.hasComplete.observe(this, Observer<Boolean> {
            if (it) completeUpdate()
        })

        binding.lifecycleOwner = this

        return binding.root
    }

    fun showProgressBar(){
        Log.d(TAG, "Showing progress bar")
    }

    fun completeUpdate(){
        dialogInfo()
    }

    fun dialogInfo(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_order_created)

        val lp : WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog.findViewById<TextView>(R.id.title).text = "Yaay ..."
        dialog.findViewById<TextView>(R.id.content).text = "Your profile has been updated"

        dialog.show()
        dialog.window?.attributes = lp

        dialog.findViewById<Button>(R.id.bt_close).setOnClickListener {
            dialog.dismiss()
        }
    }
}
