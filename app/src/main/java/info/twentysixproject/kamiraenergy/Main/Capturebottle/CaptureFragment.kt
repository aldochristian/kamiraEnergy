package info.twentysixproject.kamiraenergy.Main.Capturebottle

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

import info.twentysixproject.kamiraenergy.R
import info.twentysixproject.kamiraenergy.databinding.CaptureFragmentBinding
import info.twentysixproject.kamiraenergy.utils.FirebaseRepo.CAPTUREBOTTLE
import info.twentysixproject.kamiraenergy.utils.FirebaseRepo.storeAddress
import info.twentysixproject.kamiraenergy.utils.Utils.confirmDialog
import info.twentysixproject.kamiraenergy.utils.Utils.warningDialog
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

class CaptureFragment : Fragment() {

    private val GALLERY = 1
    private val CAMERA = 2
    private val PERMISSION_CODE = 1000
    var imageUri: Uri? = null
    var imagePickCheck = false

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: String? = FirebaseAuth.getInstance().uid
    val storage = FirebaseStorage.getInstance(storeAddress)

    var bitmap: Bitmap? = null

    companion object {
        fun newInstance() = CaptureFragment()
    }

    private lateinit var viewModel: CaptureViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: CaptureFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.capture_fragment, container, false)

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.title = "Capture Your plastics"
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        viewModel = ViewModelProviders.of(this).get(CaptureViewModel::class.java)

        binding.captBtnpick.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                    //permission was not enabled
                    val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    //show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                    showPictureDialog() // openCamera()
                }
            }
            else{
                //system os is < marshmallow
                showPictureDialog() //penCamera()
            }
        }

        binding.captBtnupload.setOnClickListener {
            if(imagePickCheck){
                binding.progressIndeterminate.visibility = View.VISIBLE
                val key: String = viewModel.detailFileToBeUploaded(user)
                storedToBucket(key)
            }else{
                warningDialog(requireContext(), "Upload error", "Please take picture first prior upload")
            }

        }

        viewModel.successUpload.observe(this, Observer<Int> {

            when(it){
                0 -> {
                    binding.progressIndeterminate.visibility = View.INVISIBLE
                    warningDialog(requireContext(), "Upload error", "There is some issue please try again, if issue keep persist kindly connect Kamira admin")
                    viewModel.successUploaded()
                }
                1 -> {
                    binding.progressIndeterminate.visibility = View.INVISIBLE
                    confirmDialog(requireContext(), "Uploaded", "Please wait 24 hours then your point will received")
                    viewModel.successUploaded()
                }
            }
        })

        viewModel.imagecapture.observe(this, Observer<Uri> {
           binding.captImg.setImageURI(it)
            bitmap = (binding.captImg.drawable as BitmapDrawable).bitmap
        })

        binding.lifecycleOwner = this
        return binding.root
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(requireContext())
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> opencamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, GALLERY)
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width: Int = image.width
        var height: Int = image.height

        val bitmapRatio: Float = width.toFloat() / height.toFloat()
        if(bitmapRatio > 1){
            width = maxSize
            height = (width/bitmapRatio).toInt()
        }else{
            height = maxSize
            width = (height/bitmapRatio).toInt()
        }
        return  Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun opencamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, CAMERA)
    }

    fun storedToBucket(fileName: String): Boolean{
        imagePickCheck = false
        val storageRef = storage.reference
        val myBucket = storageRef.child(CAPTUREBOTTLE+"/"+user+"/"+fileName) //e.g : capturebottle/{USERID}/{NamaFile}

        //imageview.isDrawingCacheEnabled = true //imageview.buildDrawingCache()
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = myBucket.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
           //
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    showPictureDialog()
                }
                else{
                    //permission from popup was denied
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val thumbnail = data.data
                viewModel.setImageUri(thumbnail!!)//imageview?.setImageURI(thumbnail)
                try {
                    val imageStream: InputStream? = requireContext().contentResolver.openInputStream(thumbnail)
                    val selectedImageAfter: Bitmap = BitmapFactory.decodeStream(imageStream)
                    getResizedBitmap(selectedImageAfter, 400)
                    imagePickCheck = true
                    //imageview?.setImageBitmap(selectedImageAfter)
                }catch (e: FileNotFoundException){
                    e.printStackTrace()
                }
            }

        }
        else if (requestCode == CAMERA)
        {
            if(resultCode == Activity.RESULT_OK){
                try {
                    viewModel.setImageUri(imageUri!!) //imageview?.setImageURI(imageUri)
                    imagePickCheck = true
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

}
