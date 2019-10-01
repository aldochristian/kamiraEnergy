package info.twentysixproject.kamiraenergy


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.Navigation


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    internal var forProfile: ProfileFragment.OnFragmentInteractionForProfile? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<LinearLayout>(R.id.lyt_logout)?.setOnClickListener{
            val builder =  AlertDialog.Builder(context)
            builder.setTitle("Logout ?")
            builder.setMessage("You are going to logout and exit the apps")
            builder.setPositiveButton("Okay",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    forProfile?.logout()
                })
            builder.setNegativeButton("Discard", null)
            builder.show()
        }

        view.findViewById<LinearLayout>(R.id.prof_personal)?.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.profiletoeditprofile_action, null)
        )

        view.findViewById<LinearLayout>(R.id.prof_about)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.profiletoaboutfragment_action, null)
        )

        /*view.findViewById<LinearLayout>(R.id.prof_terms)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.profiletotermsfragment_action, null)
        )*/

        view.findViewById<LinearLayout>(R.id.prof_privacy)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.profiletoprivacyfragment_action, null)
        )
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        if (context is OnFragmentInteractionForProfile) {
            forProfile = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionForEditprofile")
        }
    }

    interface OnFragmentInteractionForProfile {
        fun logout()
    }

}
