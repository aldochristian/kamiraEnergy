package info.twentysixproject.kamiraen


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

/**
 * A simple [Fragment] subclass.
 */
class QrScannerFragment : Fragment(), ZXingScannerView.ResultHandler {


    private var mScannerView: ZXingScannerView? = null
    val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mScannerView = ZXingScannerView(requireActivity())

        return mScannerView
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()

        println("Onstart")

        if (hasNoPermissions()) {
            requestPermission()
        }/*else{
            //fotoapparat?.start()
            //fotoapparatState = FotoapparatState.ON
        }*/
    }

    private fun hasNoPermissions(): Boolean{
        return ContextCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(){
        ActivityCompat.requestPermissions(requireActivity(), permissions,0)
    }

    override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera()          // Start camera on resume
    }

    override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()           // Stop camera on pause
    }

    override fun handleResult(rawResult: Result) {
        // Do something with the result here
        //Log.v("tag", "On Result") // Prints scan results
        //Log.v("tag", rawResult.getText()) // Prints scan results
        //Log.v("tag", rawResult.getBarcodeFormat().toString()) // Prints the scan format (qrcode, pdf417 etc.)

        //MainActivity.tvresult!!.setText(rawResult.text)
        //onBackPressed()

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }
}
