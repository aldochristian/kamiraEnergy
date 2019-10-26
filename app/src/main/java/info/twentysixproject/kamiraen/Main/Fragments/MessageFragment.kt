package info.twentysixproject.kamiraen.Main.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import info.twentysixproject.kamiraen.R
import info.twentysixproject.kamiraen.adapter.InboxAdapter
import info.twentysixproject.kamiraen.adapter.InboxListener
import info.twentysixproject.kamiraen.databinding.FragmentMessageBinding
import info.twentysixproject.kamiraen.viewmodel.MessageViewModel

/**
 * A simple [Fragment] subclass.
 */
class MessageFragment : Fragment() {

    private lateinit var viewModel: MessageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentMessageBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_message, container, false)

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.title = "Messages"
        }


        viewModel = ViewModelProviders.of(this).get(MessageViewModel::class.java)

        //binding.messageViewModel = viewModel

        val adapter = InboxAdapter(InboxListener {

        })
        binding.messageRv.adapter = adapter

        viewModel.fetchFirestore()
        viewModel.messageList.observe(viewLifecycleOwner, Observer {
            it.let {
                adapter.submitList(it)
            }
        })

        binding.lifecycleOwner = this

        return binding.root
    }


}
