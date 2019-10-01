package info.twentysixproject.kamiraenergy


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import info.twentysixproject.kamiraenergy.dataclass.Redeemcode
import info.twentysixproject.kamiraenergy.interfaces.ItemClickListener
import info.twentysixproject.kamiraenergy.utils.ImageSlider
import info.twentysixproject.kamiraenergy.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.dialog_redeem.*


class HomeFragment : Fragment(), Animation.AnimationListener {

    val TAG: String = "HomeFragment"
    internal var forHome: HomeFragment.OnFragmentInteractionForHome? = null
    lateinit var functions: FirebaseFunctions
    lateinit var viewModel: HomeViewModel
    lateinit var animFadein: Animation
    var currentStatus: String = ""

    var myPoints: String? = null
    var myContribution: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val imageList = viewModel.loadImageSlider()
        val imageSlider = view?.findViewById<ImageSlider>(R.id.image_slider)
        imageSlider?.setImageList(imageList)

        imageSlider?.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                // You can listen here.
                Log.d(TAG, "I'm pressing "+position)
            }
        })

        viewModel.getOrderFirestore()
        viewModel.getOrder().observe(this, Observer {
            status -> Log.d(TAG, "Here the status change "+status)
            currentStatus = status
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        functions = FirebaseFunctions.getInstance()

        //Set an OnClickListener, using Listener button
        animFadein = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        animFadein.setAnimationListener(this) //set animation listener

        view.findViewById<LinearLayout>(R.id.frhome_fieldgarbage)?.setOnClickListener {
            forHome?.openGmaps()
        }

        view.findViewById<LinearLayout>(R.id.frhome_fieldrewards)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.hometopointsfr_action, null)
        )

        view.findViewById<LinearLayout>(R.id.frhome_redeem)?.setOnClickListener {
            showRedeemDialog()
        }

        //view.findViewById<TextView>(R.id.frhome_pointdet).text = "We gave you "+viewModel.myPoint+"for thank"
        //view.findViewById<TextView>(R.id.frhome_contributiondet).text = "You had contributed "+viewModel.myContribution+"for recycle"
    }

    private fun addMessage(text: String): Task<String> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "text" to text,
            "push" to true
        )

        return functions
            .getHttpsCallable("addMessage")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as String
                result
            }
    }

    private fun onAddMessageClicked() {
        val inputMessage = "aku Sehat Selalu"

        if (TextUtils.isEmpty(inputMessage)) {
            showSnackbar("Please enter a message.")
            return
        }

        // [START call_add_message]
        addMessage(inputMessage)
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    if (e is FirebaseFunctionsException) {
                        val code = e.code
                        val details = e.details
                    }

                    // [START_EXCLUDE]
                    Log.w(TAG, "addMessage:onFailure", e)
                    showSnackbar("An error occurred.")
                    return@OnCompleteListener
                    // [END_EXCLUDE]
                }

                // [START_EXCLUDE]
                val result = task.result
                //fieldMessageOutput.setText(result)
                Log.d(TAG, result)
                // [END_EXCLUDE]
            })
        // [END call_add_message]
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        if (context is OnFragmentInteractionForHome) {
            forHome = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionForEditprofile")
        }
    }

    fun showRedeemDialog(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_redeem)
        dialog.setCancelable(true)

        val lp =  WindowManager.LayoutParams()
        lp.copyFrom(dialog.window.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog.findViewById<Button>(R.id.dialog_redeembtn).setOnClickListener {
            val code: String = dialog.findViewById<EditText>(R.id.dialog_redeeminput).text.toString()
            if(code.isEmpty()){
                dialog.findViewById<TextView>(R.id.dialog_errortx).setText("Empty Code, please enter the code")
            }else{
                var codeFounded = viewModel.activatedCode(code)
                if(codeFounded) {
                    dialog.dismiss()
                }else{
                    dialog.findViewById<TextView>(R.id.dialog_errortx).setText("Invalid/expired code, please enter new one or correction")
                }
            }
        }

        dialog.show()
        dialog.window?.attributes

    }

    interface OnFragmentInteractionForHome {
        fun openGmaps()
    }

    fun showSnackbar(message: String) {
        //Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onAnimationRepeat(p0: Animation?) {
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAnimationEnd(p0: Animation?) {
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAnimationStart(p0: Animation?) {
        //To change body of created functions use File | Settings | File Templates.
    }

}
