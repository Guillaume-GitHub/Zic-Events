package com.lab.zicevents.ui.publication

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.utils.ImagePickerHelper
import com.lab.zicevents.utils.OnRequestPermissionsListener
import com.lab.zicevents.utils.PermissionHelper
import com.lab.zicevents.utils.base.BaseFragmentDialog
import kotlinx.android.synthetic.main.fragment_add_publication_dialog.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.text.Editable
import android.util.Log
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.lab.zicevents.utils.adapter.NetworkConnectivity
import kotlinx.android.synthetic.main.publication_recycler_item.*

class AddPublicationFragmentDialog(private val viewModel: PublicationViewModel) :
    BaseFragmentDialog(),
    androidx.appcompat.widget.Toolbar.OnMenuItemClickListener,
    View.OnClickListener {

    private val IMAGE_PICKER_RQ = 54
    private var currentUser: User? = null
    private var isPublicationValid = false
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.AppTheme_FullScreenDialog)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_publication_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Restore saved elements (image)
        savedInstanceState?.let {
            val bitmap = savedInstanceState.getParcelable<Bitmap?>("image")
            bitmap?.let {
                publication_dialog_image.setImageBitmap(bitmap)
                publication_dialog_image_container.visibility = View.VISIBLE
            }
        }

        setUpToolbar()
        observeProfileResult()
        observePublicationValidation()
        observePublicationCreation()
        publication_dialog_bottom_bar.replaceMenu(R.menu.add_publication_menu)
        publication_dialog_bottom_bar.setOnMenuItemClickListener(this)
        publication_dialog_fab.setOnClickListener(this)
        publication_dialog_image_close_btn.setOnClickListener(this)

        publication_dialog_message.addTextChangedListener { text: Editable? ->
            viewModel.publicationDataChange(text)
        }
    }

    override fun setUpToolbar() {
        publication_dialog_toolbar.setNavigationOnClickListener {
            dismiss()
        }
    }

    // Set Menu
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.publication_dialog_bottom_bar_photo ->
                pickImageFromGallery(IMAGE_PICKER_RQ)
        }
        return true
    }

    // On click Listener
    override fun onClick(view: View?) {
        when (view?.id){
            R.id.publication_dialog_fab -> {
                if (isPublicationValid)
                    postPublication()
            }
            R.id.publication_dialog_image_close_btn ->
                cleanPublicationImage()
        }
    }

    /**
     * remove drawable in imageView and hide section
     */
    private fun cleanPublicationImage() {
        publication_dialog_image.setImageDrawable(null)
        publication_dialog_image_container.visibility = View.GONE
    }

    /**
     * Observe user fetching operation
     * get and pass result to fetch publications or display error message
     */
    private fun observeProfileResult() {
        viewModel.profileResult.observe(viewLifecycleOwner, Observer {
            when {
                it.data is User -> {
                    currentUser = it.data
                    updateUI(it.data)
                }
                it.error != null -> {
                    Toast.makeText(context, getString(it.error), Toast.LENGTH_LONG).show()
                    dismiss()
                }
            }
        })
    }

    /**
     * Enable/Disable Fab valid button is publication is valid
     */
    private fun observePublicationValidation() {
        viewModel.publicationValidation.observe(viewLifecycleOwner, Observer {
            publication_dialog_fab.apply {
                this@AddPublicationFragmentDialog.isPublicationValid = it
                alpha = if (it) 1f else 0.6f
                rippleColor = resources.getColor(R.color.shadowColorText)
            }
        })
    }

    /**
     * Check permissions, if is granted start Image Gallery Picker
     * else Ask these permissions to user
     * @param requestCode identifier of request
     */
    private fun pickImageFromGallery(requestCode: Int) {
        val permsResult =
            PermissionHelper().checkPermissions(context!!, PermissionHelper.STORAGE_PERMISSIONS)
        // Ask permission to user if permission denied
        if (permsResult.isNullOrEmpty()) {
            ImagePickerHelper.pickImageFromGallery(this, requestCode) // Start image picker gallery

        } else { // ASk permissions to user
            val activity = activity
            PermissionHelper().askRequestPermissions(activity,
                PermissionHelper.STORAGE_PERMISSIONS,
                object : OnRequestPermissionsListener {
                    override fun onRequestPermissions(
                        isGranted: Boolean,
                        grantResult: Map<String, Int>) {
                        if (isGranted)  // Start image picker gallery
                            ImagePickerHelper.pickImageFromGallery(
                                this@AddPublicationFragmentDialog, requestCode)
                        else
                            Toast.makeText(context, getString(R.string.storage_permission_denied),
                                Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                IMAGE_PICKER_RQ ->
                    // add image to imageView  if not null
                    data?.data?.let {
                        publication_dialog_image.setImageURI(it)
                        publication_dialog_image_container.visibility = View.VISIBLE
                    }
            }
        else
            super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     *  Update UI with user information
     *  @param user user profile object
     */
    private fun updateUI(user: User) {
        // username
        publication_dialog_username.text = user.displayName
        // user image
        user.profileImage?.let {
            Glide.with(context!!)
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.color.colorPrimaryLight)
                .into(publication_dialog_user_image)
        }
    }

    /**
     * Show progress dialog and create publication async
     */
    private fun postPublication(){
        if (NetworkConnectivity.isOnline(context)) {
            showDialog(true)
            viewModel.postPublication(currentUser,
                publication_dialog_message.text, publication_dialog_image.drawable)
        } else
            Toast.makeText(context, getText(R.string.no_network_connectivity), Toast.LENGTH_SHORT).show()
    }

    /**
     * Observe publication creation, hide progress dialog and dismiss
     */
    private fun observePublicationCreation(){
        viewModel.publicationCreationSate.observe(viewLifecycleOwner, Observer {
            showDialog(false)
            dismiss()
        })
    }

    /**
     * Display custom dialog with progress bar
     */
    private fun showDialog(display: Boolean){
        if (display) {
            context?.let {
                val dialog = Dialog(it)
                progressDialog = dialog
                val rootView =
                    LayoutInflater.from(it).inflate(R.layout.custom_progress_dialog, null)
                val messageTextView: TextView =
                    rootView.findViewById(R.id.custom_progress_dialog_message)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // hide window
                messageTextView.text = getString(R.string.publication_creation) // set message
                dialog.setContentView(rootView) // add custom view
                dialog.setCancelable(false) // not cancelable
                dialog.show()
            }
        }
        else
            progressDialog?.dismiss()
    }

    // Save Image -> screen rotation
    override fun onSaveInstanceState(outState: Bundle) {
        val drawable = publication_dialog_image.drawable
        if (drawable != null) {
            val bitmap = (drawable as BitmapDrawable).bitmap
            outState.putParcelable("image", bitmap)
        }
        super.onSaveInstanceState(outState)
    }
}