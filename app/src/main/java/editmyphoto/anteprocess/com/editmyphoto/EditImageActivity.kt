package editmyphoto.anteprocess.com.editmyphoto

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import editmyphoto.anteprocess.com.editmyphoto.base.BaseActivity
import editmyphoto.anteprocess.com.editmyphoto.filters.FilterListener
import editmyphoto.anteprocess.com.editmyphoto.filters.FilterViewAdapter
import editmyphoto.anteprocess.com.editmyphoto.photoutils.*
import editmyphoto.anteprocess.com.editmyphoto.tools.EditingToolsAdapter
import editmyphoto.anteprocess.com.editmyphoto.tools.ToolType
import java.io.File
import java.io.IOException

class EditImageActivity : BaseActivity()
    , OnPhotoEditorListener
    , View.OnClickListener
    , PropertiesBSFragment.Properties
    , EmojiBSFragment.EmojiListener
    , StickerBSFragment.StickerListener
    , EditingToolsAdapter.OnItemSelected
    , FilterListener {
    private var mPhotoEditor: PhotoEditor? = null
    private var mPhotoEditorView: PhotoEditorView? = null
    private var mPropertiesBSFragment: PropertiesBSFragment? = null
    private var mEmojiBSFragment: EmojiBSFragment? = null
    private var mStickerBSFragment: StickerBSFragment? = null
    private var mTxtCurrentTool: TextView? = null
    private var mRvTools: RecyclerView? = null
    private var mRvFilters: RecyclerView? = null
    private val mEditingToolsAdapter = EditingToolsAdapter(this)
    private val mFilterViewAdapter = FilterViewAdapter(this, this)
    private var mRootView: ConstraintLayout? = null
    private val mConstraintSet = ConstraintSet()
    private var mIsFilterVisible: Boolean = false

    private val galleryPath: String
        get() {
            val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            val folder = File(root)
            if (!folder.exists()) folder.mkdir()
            return folder.absolutePath
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_edit_image)
        initViews()

        mPropertiesBSFragment = PropertiesBSFragment()
        mEmojiBSFragment = EmojiBSFragment()
        mStickerBSFragment = StickerBSFragment()
        mStickerBSFragment!!.setStickerListener(this)
        mEmojiBSFragment!!.setEmojiListener(this)
        mPropertiesBSFragment!!.setPropertiesChangeListener(this)

        val llmTools = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvTools!!.layoutManager = llmTools
        mRvTools!!.adapter = mEditingToolsAdapter

        val llmFilters = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvFilters!!.layoutManager = llmFilters
        mRvFilters!!.adapter = mFilterViewAdapter

        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView!!)
            .setPinchTextScalable(true)
            .build()

        mPhotoEditor!!.setOnPhotoEditorListener(this)

        //Set Image Dynamically
        // mPhotoEditorView.getSource().setImageResource(R.drawable.color_palette);
    }

    private fun initViews() {
        val imgUndo: ImageView
        val imgRedo: ImageView
        val imgCamera: ImageView
        val imgGallery: ImageView
        val imgSave: ImageView
        val imgClose: ImageView

        mPhotoEditorView = findViewById(R.id.photoEditorView)
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool)
        mRvTools = findViewById(R.id.rvConstraintTools)
        mRvFilters = findViewById(R.id.rvFilterView)
        mRootView = findViewById(R.id.rootView)

        imgUndo = findViewById(R.id.imgUndo)
        imgUndo.setOnClickListener(this)

        imgRedo = findViewById(R.id.imgRedo)
        imgRedo.setOnClickListener(this)

        imgCamera = findViewById(R.id.imgCamera)
        imgCamera.setOnClickListener(this)

        imgGallery = findViewById(R.id.imgGallery)
        imgGallery.setOnClickListener(this)

        imgSave = findViewById(R.id.imgSave)
        imgSave.setOnClickListener(this)

        imgClose = findViewById(R.id.imgClose)
        imgClose.setOnClickListener(this)

    }

    override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
        val textEditorDialogFragment = TextEditorDialogFragment.show(this, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode ->
            mPhotoEditor!!.editText(rootView, inputText, colorCode)
            mTxtCurrentTool!!.setText(R.string.label_text)
        }
    }

    override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {}
    override fun onRemoveViewListener(numberOfAddedViews: Int) {}
    override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {}
    override fun onStartViewChangeListener(viewType: ViewType) {}
    override fun onStopViewChangeListener(viewType: ViewType) {}

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imgUndo -> mPhotoEditor!!.undo()

            R.id.imgRedo -> mPhotoEditor!!.redo()

            R.id.imgSave -> saveImage()

            R.id.imgClose -> onBackPressed()

            R.id.imgCamera -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }

            R.id.imgGallery -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun saveImage() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showLoading("Saving...")
            val file = File(
                galleryPath
                        + File.separator + ""
                        + System.currentTimeMillis() + ".png"
            )
            try {
                file.createNewFile()

                val saveSettings = SaveSettings.Builder()
                    .setClearViewsEnabled(true)
                    .setTransparencyEnabled(true)
                    .build()

                mPhotoEditor!!.saveAsFile(file.absolutePath, saveSettings, object : PhotoEditor.OnSaveListener {
                    override fun onSuccess(imagePath: String) {
                        hideLoading()
                        showSnackbar("Image Saved Successfully")
                        mPhotoEditorView!!.source.setImageURI(Uri.fromFile(File(imagePath)))
                        //This is for updating the album
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                            val contentUri = Uri.fromFile(File(imagePath))
                            scanIntent.data = contentUri
                            sendBroadcast(scanIntent)
                        } else {
                            val intent = Intent(
                                Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://" + Environment.getExternalStorageDirectory())
                            )
                            sendBroadcast(intent)
                        }
                    }

                    override fun onFailure(exception: Exception) {
                        hideLoading()
                        showSnackbar("Failed to save Image")
                    }
                })
            } catch (e: IOException) {
                e.printStackTrace()
                hideLoading()
                showSnackbar(e.message!!)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    mPhotoEditor!!.clearAllViews()
                    val photo = data!!.extras!!.get("data") as Bitmap
                    mPhotoEditorView!!.source.setImageBitmap(photo)
                }
                PICK_REQUEST -> try {
                    mPhotoEditor!!.clearAllViews()
                    val uri = data!!.data
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    mPhotoEditorView!!.source.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    override fun onColorChanged(colorCode: Int) {
        mPhotoEditor!!.brushColor = colorCode
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor!!.setOpacity(opacity)
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        mPhotoEditor!!.brushSize = brushSize.toFloat()
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    override fun onEmojiClick(emojiUnicode: String) {
        mPhotoEditor!!.addEmoji(emojiUnicode)
        mTxtCurrentTool!!.setText(R.string.label_emoji)

    }

    override fun onStickerClick(bitmap: Bitmap) {
        mPhotoEditor!!.addImage(bitmap)
        mTxtCurrentTool!!.setText(R.string.label_sticker)
    }

    override fun isPermissionGranted(isGranted: Boolean, permission: String) {
        if (isGranted) saveImage()
    }

    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you want to exit without saving image ?")
        builder.setPositiveButton("Save") { dialog, which -> saveImage() }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }

        builder.setNeutralButton("Discard") { dialog, which -> finish() }
        builder.create().show()

    }

    override fun onFilterSelected(photoFilter: PhotoFilter) {
        mPhotoEditor!!.setFilterEffect(photoFilter)
    }

    override fun onToolSelected(toolType: ToolType) = when (toolType) {
        ToolType.BRUSH -> {
            mPhotoEditor!!.setBrushDrawingMode(true)
            mTxtCurrentTool!!.setText(R.string.label_brush)
            mPropertiesBSFragment!!.show(supportFragmentManager, mPropertiesBSFragment!!.tag)
        }
        ToolType.TEXT -> {
            val textEditorDialogFragment = TextEditorDialogFragment.show(this)
            textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode ->
                mPhotoEditor!!.addText(inputText, colorCode)
                mTxtCurrentTool!!.setText(R.string.label_text)
            }
        }
        ToolType.ERASER -> {
            mPhotoEditor!!.brushEraser()
            mTxtCurrentTool!!.setText(R.string.label_eraser)
        }
        ToolType.FILTER -> {
            mTxtCurrentTool!!.setText(R.string.label_filter)
            showFilter(true)
        }
        ToolType.EMOJI -> mEmojiBSFragment!!.show(supportFragmentManager, mEmojiBSFragment!!.tag)
        ToolType.STICKER -> mStickerBSFragment!!.show(supportFragmentManager, mStickerBSFragment!!.tag)
    }


    internal fun showFilter(isVisible: Boolean) {
        mIsFilterVisible = isVisible
        mConstraintSet.clone(mRootView!!)

        if (isVisible) {
            mConstraintSet.clear(mRvFilters!!.id, ConstraintSet.START)
            mConstraintSet.connect(
                mRvFilters!!.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )
            mConstraintSet.connect(
                mRvFilters!!.id, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            mConstraintSet.connect(
                mRvFilters!!.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
            mConstraintSet.clear(mRvFilters!!.id, ConstraintSet.END)
        }

        val changeBounds = ChangeBounds()
        changeBounds.duration = 350
        changeBounds.interpolator = AnticipateOvershootInterpolator(1.0f)
        TransitionManager.beginDelayedTransition(mRootView!!, changeBounds)

        mConstraintSet.applyTo(mRootView!!)
    }

    override fun onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false)
            mTxtCurrentTool!!.setText(R.string.app_name)
        } else if (!mPhotoEditor!!.isCacheEmpty) {
            showSaveDialog()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val CAMERA_REQUEST = 52
        private const val PICK_REQUEST = 53
    }
}
